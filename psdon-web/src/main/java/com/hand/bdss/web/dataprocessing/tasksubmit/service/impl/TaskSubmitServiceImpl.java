package com.hand.bdss.web.dataprocessing.tasksubmit.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.dev.service.IDevelopManager;
import com.hand.bdss.dev.service.impl.DevelopManager;
import com.hand.bdss.dev.vo.TaskScheduleInfo;
import com.hand.bdss.web.common.em.TaskStatus;
import com.hand.bdss.web.entity.DataSyncHistoryEntity;
import com.hand.bdss.web.operationcenter.history.service.DataTaskHistoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clearspring.analytics.util.Lists;
import com.hand.bdss.dsmp.common.response.Response;
import com.hand.bdss.web.common.em.TaskType;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.vo.ShowTaskJarInfoVO;
import com.hand.bdss.web.common.vo.SubmitTaskJarVO;
import com.hand.bdss.web.dataprocessing.tasksubmit.dao.JarInfoDao;
import com.hand.bdss.web.dataprocessing.tasksubmit.dao.LatestTaskDao;
import com.hand.bdss.web.dataprocessing.tasksubmit.dao.TaskAttributeDao;
import com.hand.bdss.web.dataprocessing.tasksubmit.service.TaskSubmitService;
import com.hand.bdss.web.entity.LatestTaskEntity;
import com.hand.bdss.web.entity.TaskAttributeEntity;
import com.hand.bdss.web.entity.UserEntity;
import com.hand.bdss.web.operationcenter.task.service.JarTaskService;

@Service
public class TaskSubmitServiceImpl implements TaskSubmitService {
	
	private static final String STORM = "STORM";
	private static final String SPARK = "SPARK";
	private static final String SQOOP = "SQOOP";
	private static final Logger logger = LoggerFactory.getLogger(TaskAttributeServiceImpl.class);
	private IDevelopManager manager = new DevelopManager();
	@Resource
	LatestTaskDao latestTaskDaoImpl;
	@Resource
	TaskAttributeDao taskAttributeDaoImpl;
	@Resource
	JarInfoDao jarInfoDaoImpl;
	@Resource
	JarTaskService jarTaskServiceImpl;
	@Resource
	private DataTaskHistoryService dataTaskHistoryServiceImpl;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean submitJarTask(SubmitTaskJarVO taskvo, HttpServletRequest request) throws Exception {
		UserEntity user = GetUserUtils.getUser(request);
		TaskAttributeEntity taskAttributeEntity = taskvo.getTaskAttributeEntity();
		taskAttributeEntity.setJarInfoId(Long.parseLong(taskvo.getJarInfoEntity().getId()));
		taskAttributeEntity.setCreateUser(user.getUserName());
		long id = taskAttributeDaoImpl.insertTaskAttribute(taskAttributeEntity);
		LatestTaskEntity latestTaskEntity = taskvo.getLatestTaskEntity();
		latestTaskEntity.setCreateUser(user.getUserName());
		latestTaskEntity.setTaskAttrId(id);
		latestTaskDaoImpl.insertLatestTask(latestTaskEntity);
		// 调用azkaban接口生成任务
		return this.createAzkabanJob(taskvo);
	}

	@Override
	public List<ShowTaskJarInfoVO> listJarTasks(SubmitTaskJarVO taskvo, HttpServletRequest request) throws Exception {
		int startPage = taskvo.getStartPage();
		int count = taskvo.getCount();
		LatestTaskEntity latestTaskEntity = taskvo.getLatestTaskEntity();
		// 如果是管理员，则可以查询所有的jar任务信息，如果不是，则只能查询自己创建的jar任务信息
		if (!GetUserUtils.isRootUser(request)) {
			if(latestTaskEntity==null) {
				latestTaskEntity = new LatestTaskEntity();
			}
			latestTaskEntity.setCreateUser(GetUserUtils.getUser(request).getUserName());
		}
		return latestTaskDaoImpl.listJarTasks(latestTaskEntity, (startPage - 1) * 10, count);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Map<String, Object> deleteJarTasks(HttpServletRequest request, List<SubmitTaskJarVO> taskvos) throws Exception {
		List<LatestTaskEntity> latestTasks = Lists.newArrayList();
		List<TaskAttributeEntity> taskAttrs = Lists.newArrayList();
		Map<String, Object> retMap = new HashMap<>();
		for (SubmitTaskJarVO taskvo : taskvos) {
			LatestTaskEntity latestTaskEntity = taskvo.getLatestTaskEntity();
			TaskAttributeEntity taskAttributeEntity = taskvo.getTaskAttributeEntity();
			String jobName = getJobName(latestTaskEntity.getTaskName(), latestTaskEntity.getTaskType());
			DataSyncHistoryEntity entity = null;
			try {
				entity = dataTaskHistoryServiceImpl.selectRecordLast(jobName);
				if (entity != null && !TaskStatus.run.getIndex().toString().equals(entity.getJobState())){
					latestTasks.add(latestTaskEntity);
					taskAttrs.add(taskAttributeEntity);
				}else {
					retMap.put(jobName, "任务正在执行,请到任务运维kill此任务后进行删除");
				}
			}catch (Exception e){
				logger.error("check one of  tasks status error",e);
			}
		}
		if (latestTasks.size()>0 && taskAttrs.size()>0) {
			latestTaskDaoImpl.deleteLatestTask(latestTasks);
			taskAttributeDaoImpl.deleteTaskAttribute(taskAttrs);
			// 删除对应的azkaban上的任务
			for (LatestTaskEntity latestTask : latestTasks) {
				Response res = jarTaskServiceImpl
						.deleteAzkabanJob(getJobName(latestTask.getTaskName(), latestTask.getTaskType()));
				if (!res.getMeta().isSuccess()) {
					throw new Exception(res.getMeta().getMessage());
				}
			}
		}
		return retMap;
	}

	@Override
	public boolean checkJarTaskName(LatestTaskEntity latestTaskEntity) throws Exception {
		int i = latestTaskDaoImpl.checkJarTaskName(latestTaskEntity);
		if (i > 0) {
			return true;
		}
		return false;
	}

	@Override
	public Map<String,Object>  execJartasks(HttpServletRequest request, LatestTaskEntity latestTaskEntity) throws Exception {
		String jobName = getJobName(latestTaskEntity.getTaskName(), latestTaskEntity.getTaskType());
		Map<String,Object> retMap = new HashMap<>();
		DataSyncHistoryEntity entity = dataTaskHistoryServiceImpl.selectRecordLast(jobName);
		if (entity != null && TaskStatus.run.getIndex().toString().equals(entity.getJobState())) {
			retMap.put(jobName,"task is running,please kill it first");
			return retMap;
		}

		Response res = jarTaskServiceImpl.execAzkabanJob(jobName);
		if (!res.getMeta().isSuccess()) {
			retMap.put("Message",res.getMeta().getMessage());
			throw new Exception(res.getMeta().getMessage());
		}
		return retMap;
	}

	public String getJobName(String taskName, String taskType) {
		String jobName = "";
		if (STORM.equals(taskType)) {
			jobName = taskName + "-" + TaskType.JAR_TASK.getIndex().toString() + "-" + "STORM";
		}
		if (SPARK.equals(taskType)) {
			jobName = taskName + "-" + TaskType.JAR_TASK.getIndex().toString() + "-" + "SPARK";
		}
		if(SQOOP.equals(taskType)){
			jobName = taskName + "-" + TaskType.SCRIPT.getIndex().toString() + "-" + "SQOOP";
		}
		return jobName;
	}

	@Override
	public int listJarTaskCounts(HttpServletRequest request, SubmitTaskJarVO taskvo) throws Exception {
		LatestTaskEntity latestTaskEntity = taskvo.getLatestTaskEntity();
		// 如果是管理员，则可以查询所有的jar任务信息，如果不是，则只能查询自己创建的jar任务信息
		if (!GetUserUtils.isRootUser(request)) {
			if(latestTaskEntity==null) {
				latestTaskEntity = new LatestTaskEntity();
			}
			latestTaskEntity.setCreateUser(GetUserUtils.getUser(request).getUserName());
		}
		return latestTaskDaoImpl.listJarTaskCounts(latestTaskEntity);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Map<String,Object> updateJarTask(HttpServletRequest request, SubmitTaskJarVO taskvo) throws Exception {
		Map<String,Object> retMap = new HashMap<>();
		UserEntity user = GetUserUtils.getUser(request);
		TaskAttributeEntity taskAttributeEntity = taskvo.getTaskAttributeEntity();
		taskAttributeEntity.setUpdateUser(user.getUserName());
		LatestTaskEntity latestTaskEntity = taskvo.getLatestTaskEntity();
		String jobName = getJobName(latestTaskEntity.getTaskName(), latestTaskEntity.getTaskType());
		DataSyncHistoryEntity entity = dataTaskHistoryServiceImpl.selectRecordLast(jobName);
		if (entity != null && TaskStatus.run.getIndex().toString().equals(entity.getJobState())) {
			retMap.put(jobName,"task is running,please kill it first");
			return retMap;
		}
		// 删除对应的azkaban上的任务
		Response res = jarTaskServiceImpl
					.deleteAzkabanJob(getJobName(latestTaskEntity.getTaskName(), latestTaskEntity.getTaskType()));
		if (!res.getMeta().isSuccess()) {
			throw new Exception(res.getMeta().getMessage());
		}
		//更新任务表中的数据
		latestTaskDaoImpl.updateLatestTask(latestTaskEntity);
		taskAttributeDaoImpl.updateJarTask(taskAttributeEntity);
		//创建azkaban任务
		this.createAzkabanJob(taskvo);
		return retMap;
	}

	/**
	 * SQOOP脚本设置定时
	 * @param request
	 * @param latestTaskEntity
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> setSechdulerTask(HttpServletRequest request, LatestTaskEntity latestTaskEntity) throws Exception {
		Map<String,Object> retMap = new HashMap<>();

		//更新表中数据
		latestTaskDaoImpl.updateLatestTaskById(latestTaskEntity);
		String jobName = getJobName(latestTaskEntity.getTaskName(),latestTaskEntity.getTaskType());
		TaskScheduleInfo task = new TaskScheduleInfo();
		task.setTaskName(latestTaskEntity.getTaskName());
		task.setTaskType(TaskType.SCRIPT.getIndex().toString());
		task.setId(latestTaskEntity.getId());
		task.setCreatetime(latestTaskEntity.getCreateTime());
		if (StringUtils.isNotBlank(latestTaskEntity.getEmailStr())) {
			task.setEmailStr(latestTaskEntity.getEmailStr());
		}
		task.setTimerAttribute(latestTaskEntity.getTimerAttribute());

		try {
			DataSyncHistoryEntity entity = dataTaskHistoryServiceImpl.selectRecordLast(jobName);
			if (entity != null && TaskStatus.run.getIndex().toString().equals(entity.getJobState())) {
				retMap.put("returnMessage", "任务正在执行，请稍后");
				return retMap;
			}
			if (manager.executeScheduleScript(task, null)) {
				retMap.put("returnMessage", "执行定时任务成功！");
				retMap.put("returnCode", "1");
			} else {
				retMap.put("returnMessage", "执行定时任务失败！");
			}
		} catch (Exception e) {
			logger.error("executeScheduleScript error!", e);
			retMap.put("returnMessage", "执行定时任务异常！");
		}
		return retMap;
	}

	@Override
	public Map<String, Object> setParamsJarTask(SubmitTaskJarVO taskJarVO) throws Exception {
		Map<String,Object> retMap = new HashMap<>();
		String userParams = taskJarVO.getTaskAttributeEntity().getUserParam();
		LatestTaskEntity latestTaskEntity = taskJarVO.getLatestTaskEntity();
		String jobName = getJobName(latestTaskEntity.getTaskName(),latestTaskEntity.getTaskType());
		try {
			DataSyncHistoryEntity entity = dataTaskHistoryServiceImpl.selectRecordLast(jobName);
			if (entity != null && TaskStatus.run.getIndex().toString().equals(entity.getJobState())) {
				retMap.put("returnMessage", "任务正在执行，请稍后重试");
				return retMap;
			}
		} catch (Exception e) {
			logger.error("executeScheduleScript error!", e);
			retMap.put("returnMessage", "执行定时任务异常！");
		}
		taskAttributeDaoImpl.updateJarTask(taskJarVO.getTaskAttributeEntity());
		jarTaskServiceImpl.createAzkabanJob(null,null,jobName,userParams);
		retMap.put("returnMessage", "任务修改成功");
		return retMap;
	}


	public boolean createAzkabanJob(SubmitTaskJarVO taskvo) throws Exception {
		LatestTaskEntity latestTaskEntity = taskvo.getLatestTaskEntity();
		TaskAttributeEntity taskAttributeEntity = taskvo.getTaskAttributeEntity();
		// 调用azkaban接口生成任务
		String shShell = null;
		List<String> params = Lists.newArrayList();

		if (STORM.equals(latestTaskEntity.getTaskType())) {
			params.add("storm jar");
			params.add("hdfs://antx" + taskvo.getJarInfoEntity().getJarPath());
			params.add(taskAttributeEntity.getEntryClass());
			if (StringUtils.isNotEmpty(taskAttributeEntity.getUserParam())) {
				params.add(taskAttributeEntity.getUserParam());
			}
			shShell = String.join(" ", params);
		}
		if (SPARK.equals(latestTaskEntity.getTaskType())) {
			params.add("spark-submit --class");
			params.add(taskAttributeEntity.getEntryClass());
			if(StringUtils.isNotEmpty(taskAttributeEntity.getSystemParam())) {
				params.add(taskAttributeEntity.getSystemParam());
			}
			params.add("hdfs://antx" + taskvo.getJarInfoEntity().getJarPath());
			if (StringUtils.isNotEmpty(taskAttributeEntity.getUserParam())) {
				params.add(taskAttributeEntity.getUserParam());
			}
			shShell = String.join(" ", params);
		}
		Response response = jarTaskServiceImpl.createAzkabanJob(taskvo.getJarInfoEntity().getJarPath(),shShell,
				getJobName(latestTaskEntity.getTaskName(), latestTaskEntity.getTaskType()),null);
		if (!response.getMeta().isSuccess()) {// 如果创建不成功
			throw new Exception(response.getMeta().getMessage());
		}
		return true;
	}

}

package com.hand.bdss.web.operationcenter.task.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.hand.bdss.dev.util.JacksonUtil;
import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.dev.vo.TaskScheduleInfo;
import com.hand.bdss.web.common.em.TaskStatus;
import com.hand.bdss.web.common.em.TaskType;
import com.hand.bdss.web.datasource.mydata.dao.DataSourceDao;
import com.hand.bdss.web.entity.TableEtlDO;
import com.hand.bdss.web.intelligence.component.service.ComService;
import com.hand.bdss.web.operationcenter.task.dao.TaskDao;
import com.hand.bdss.web.operationcenter.task.service.ShellService;
import com.hand.bdss.web.operationcenter.task.service.TaskService;
import com.hand.bdss.web.operationcenter.task.vo.AITaskInfo;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hand on 2017/8/4.
 */
@Service
public class TaskServiceImpl implements TaskService {

	private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

	@Autowired
	private TaskDao taskDaoImpl;
	@Autowired
	private ShellService shellServiceImpl;
	@Autowired
	private ComService comServiceImpl;
	@Autowired
	private DataSourceDao dataSourceDaoImpl;

	@Override
	public Task selectTask(Task task) throws Exception {
		long id = task.getId();
		return taskDaoImpl.getTask(id);
	}

	@Override
	public List<Task> selects(Task task) throws Exception {
		return taskDaoImpl.selects(task);
	}

	@Override
	public List<Task> selectScrpList(Task task, int startPage, int count) throws Exception {
		List<Task> list = taskDaoImpl.selectScrpList(task, startPage, count);
		List<Task> retList = new ArrayList<>();
		for (Task t : list) {
			t.setTaskType(this.praseTaskTypeName(t.getTaskType()));
			retList.add(t);
		}
		return retList;
	}

	@Override
	public List<Task> selectSyncList(Task task, int startPage, int count) throws Exception {
		List<Task> list = taskDaoImpl.selectSyncList(task, startPage, count);

		List<Task> rList = new ArrayList<>();
		for (Task t : list) {
			t.setTaskType(this.praseTaskTypeName(t.getTaskType()));
			rList.add(t);
		}
		return rList;
	}

	@Override
	public List<AITaskInfo> selectAIList(Task task, int startPage, int count) throws Exception {
		List<AITaskInfo> list = taskDaoImpl.selectAIList(task, startPage, count);
		List<AITaskInfo> retList = new ArrayList<>();
		for (AITaskInfo t : list) {
			t.setTaskType(this.praseTaskTypeName(t.getTaskType()));
			retList.add(t);
		}
		return retList;
	}

	@Override
	public Integer selectCount(Task task) throws Exception {
		return taskDaoImpl.selectCount(task);
	}

	/**
	 * 更新&执行定时脚本
	 *
	 * @param task
	 * @return
	 */
	@Override
	public Map<String, Object> executeScheduleScript(TaskScheduleInfo task) {
		Map<String, Object> retMap = new HashMap<>();
		retMap.put("returnCode", "0");
		try {
			taskDaoImpl.update(task);
			String emailStr = task.getEmailStr();
			TaskScheduleInfo tk = taskDaoImpl.getTaskScheduleInfo(task.getId());
			if(StringUtils.isNotEmpty(emailStr)) {
				tk.setEmailStr(emailStr);
			}
			retMap = shellServiceImpl.executeScheduleScript(tk);
		} catch (Exception e) {
			logger.error("executeScheduleScript error!", e);
			retMap.put("returnMessage", "执行定时任务异常！");
		}
		return retMap;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deletes(List<String> list, HttpServletRequest req) throws Exception {
		List<String> tableList = new ArrayList<>();
		try {
			if (list != null && list.size() > 0) {
				for (String ids : list) {
					// 本地Task表记录删除
					JsonNode jsonNode = JacksonUtil.getJsonNode(ids);
					Long id = jsonNode.get("id").asLong();
					Task task = taskDaoImpl.getTask(id);
					// 远程azkaban脚本删除
					if (TaskType.AI.getIndex() != Integer.parseInt(task.getTaskType())) {
						if (shellServiceImpl.deleteScript(task)) {
							taskDaoImpl.delete(id);
							if (TaskType.SYNC.getIndex() == Integer.parseInt(task.getTaskType())) {
								TableEtlDO tableEtlDO = new TableEtlDO();
								tableEtlDO.setJobName(task.getTaskName());
								tableEtlDO = dataSourceDaoImpl.getAzkabanJobName(tableEtlDO).get(0);
								tableList.add(tableEtlDO.getId());
							}
						} else {
							throw new Exception("删除任务失败！脚本查询或者同步配置出错");
						}
						//远程AI模型删除
					}else{
						 if(comServiceImpl.deleteAITask(task,req)){
							 taskDaoImpl.delete(id);
						 }else{
							 throw new Exception("删除AI任务失败!数据不存在或者配置出错！");
						 }
					}
				}
				// 本地TableEtlDO表记录删除
				if (tableList != null && tableList.size() > 0) {
					dataSourceDaoImpl.deleteData(tableList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("删除任务失败");
		}
	}

	@Override
	public void delete(Long id) throws Exception {
		Task task = taskDaoImpl.getTask(id);
		taskDaoImpl.delete(id);
		if (TaskType.AI.getIndex() != Integer.parseInt(task.getTaskType())) {
			shellServiceImpl.deleteScript(task);
		}
	}

	@Override
	public Map<String, Object> execute(Long id, HttpServletRequest req) {
		Map<String, Object> retMap = new HashMap<>();
		retMap.put("returnCode", "0");
		try {
			Task task = taskDaoImpl.getTask(id);
			if(TaskType.AI.getIndex() == Integer.parseInt(task.getTaskType())){
            	retMap = comServiceImpl.executeAITask(task, req);
            }else{
                retMap = shellServiceImpl.executeScript(task);
            }
		} catch (Exception e) {
			logger.error("executeScheduleScript error!", e);
			retMap.put("returnMessage", "执行任务异常！");
		}
		return retMap;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> scheduleTask(Task task, HttpServletRequest req) throws Exception {
		Map<String, Object> retMap = new HashMap<>();
		try {
			// 更新任务
			boolean exsitTask = taskDaoImpl.update(task);
			if (exsitTask) {
				// 执行任务
				Task tasks = taskDaoImpl.getTask(task.getId());
				retMap = shellServiceImpl.executeScript(tasks);
			} else {
				retMap.put("returnMessage", "执行任务失败");
			}
		} catch (Exception e) {
			logger.error("scheduleTask error!", e);
			retMap.put("returnMessage", "执行任务异常！");
		}
		return retMap;
	}

	@Override
	public boolean stop(Long id) throws Exception {
		Task task = taskDaoImpl.getTask(id);
		if (TaskStatus.run.getIndex().toString().equals(task.getStatus())) {
			return shellServiceImpl.stopProject(task);
		}
		return false;
	}

	@Override
	public boolean check(String taskName) throws Exception {
		Task task = new Task();
		task.setTaskName(taskName);
		int count = taskDaoImpl.check(task);
		if (count > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 解析任务状态
	 *
	 * @param state
	 * @return
	 */
	private String praseTaskState(String state) {
		Integer jobStyle = Integer.parseInt(state);

		if (TaskStatus.fail.getIndex() == jobStyle) {
			return TaskStatus.fail.getName();
		}
		if (TaskStatus.run.getIndex() == jobStyle) {
			return TaskStatus.run.getName();
		}
		if (TaskStatus.success.getIndex() == jobStyle) {
			return TaskStatus.success.getName();
		}
		if (TaskStatus.cancel.getIndex() == jobStyle) {
			return TaskStatus.cancel.getName();
		}
		if (TaskStatus.init.getIndex() == jobStyle) {
			return TaskStatus.init.getName();
		}
		return null;
	}

	/**
	 * 根据任务名称 解析任务类型
	 *
	 * @param taskType
	 * @return
	 */
	private String praseTaskTypeName(String taskType) {

		Integer type = Integer.parseInt(taskType);

		if (TaskType.SCRIPT.getIndex() == type) {
			return TaskType.SCRIPT.getName();
		}
		if (TaskType.SYNC.getIndex() == type) {
			return TaskType.SYNC.getName();
		}
		if (TaskType.AI.getIndex() == type) {
			return TaskType.AI.getName();
		}
		return null;
	}

}

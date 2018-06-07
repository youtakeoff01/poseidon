package com.hand.bdss.web.operationcenter.console.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.dsmp.model.ServiceStatus;
import com.hand.bdss.web.entity.ComStateNumEntity;
import com.hand.bdss.web.operationcenter.console.dao.ConsoleDao;
import com.hand.bdss.web.operationcenter.console.service.ConsoleService;
import com.hand.bdss.web.operationcenter.console.vo.TaskExecVO;
import com.hand.bdss.web.operationcenter.console.vo.TaskQuantityVO;
import com.hand.bdss.web.operationcenter.console.vo.TaskStateVO;
import com.hand.bdss.web.operationcenter.task.service.TaskService;

@Service
public class ConsoleServiceImpl implements ConsoleService {

	@Resource
	private ConsoleDao consoleDaoImpl;
	
	@Autowired
	private TaskService taskServiceImpl;
	
	@Override
	public int insertServiceStatus(ServiceStatus service) throws Exception {
		int i = consoleDaoImpl.insertServiceStatus(service);
		return i;
	}

	@Override
	public List<ServiceStatus> getServiceStatus(ServiceStatus service) throws Exception {
		List<ServiceStatus> list = consoleDaoImpl.getServiceStatus(service);
		return list;
	}

	@Override
	public List<ServiceStatus> listServiceStatusAll(ServiceStatus service) throws Exception {
		List<ServiceStatus> list = consoleDaoImpl.listServiceStatusAll(service);
		return list;
	}

	@Override
	public int updateServiceStatus(ServiceStatus service) throws Exception {
		int i = consoleDaoImpl.updateServiceStatus(service);
		return i;
	}

	@Override
	public List<ComStateNumEntity> getServiceComState(ServiceStatus service) throws Exception {
		List<ComStateNumEntity> list = consoleDaoImpl.getServiceComState(service);
		return list;
	}

	/**
	 * 获取任务执行情况
	 */
	@Override
	public List<TaskStateVO> getTaskExec() throws Exception {
		List<TaskStateVO> list = new ArrayList<>();
		Map<String,Integer> map = consoleDaoImpl.getTaskExec();
		 //解析map
        String [] states = new String[]{"running","succeed","failed","unfinished"};
        for (String state : states) {
			Integer count = map.get(state);
			if(count == null){
				map.put(state,0);
			}
		}
        //封装list
        for(String state : states){
        	TaskStateVO vo = new TaskStateVO();
        	vo.setName(state);
        	vo.setCount(map.get(state));
        	list.add(vo);
        }
		return list;
	}
	
	
	/**
	 * 获取任务执行时间
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public List<TaskExecVO> getExecTime() throws Exception{
		List<TaskExecVO> list = consoleDaoImpl.getExecTime();
		//根据任务名 查询Task记录
		for (TaskExecVO taskExecVO : list) {
			Task task = new Task();
			String taskName = taskExecVO.getTaskName();
			//添加任务类型
			if(null != taskName && taskName.contains("-region")){
				taskExecVO.setExecType("手动执行");
			}else{
				taskExecVO.setExecType("调度执行");
			}
			//解析任务名称
			taskName = taskName.contains("-") ? taskName.substring(0,taskName.indexOf("-")) : taskName;
			taskExecVO.setTaskName(taskName);
			//查询task任务表   添加创建人
			task.setTaskName(taskName);
			List<Task> exsitTasks = taskServiceImpl.selects(task);
			if(exsitTasks != null && exsitTasks.size()>0){
				task = exsitTasks.get(0);
				taskExecVO.setTaskAccount(task.getCreateAccount());
			}
		}
		return list;
	}
	
	/**
	 * 获取任务执行错误次数
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public List<TaskExecVO> getErrorCount() throws Exception{
		List<TaskExecVO> list = consoleDaoImpl.getErrorCount();
		//根据任务名 查询Task记录
		for (TaskExecVO taskExecVO : list) {
			Task task = new Task();
			String taskName = taskExecVO.getTaskName();
			//添加任务类型
			if(null != taskName && taskName.contains("-region")){
				taskExecVO.setExecType("手动执行");
			}else{
				taskExecVO.setExecType("调度执行");
			}
			//解析任务名称
			taskName = taskName.contains("-") ? taskName.substring(0,taskName.indexOf("-")) : taskName;
			taskExecVO.setTaskName(taskName);
			//查询task任务表   添加创建人
			task.setTaskName(taskName);
			List<Task> exsitTasks = taskServiceImpl.selects(task);
			if(exsitTasks != null && exsitTasks.size()>0){
				task = exsitTasks.get(0);
				taskExecVO.setTaskAccount(task.getCreateAccount());
			}
		}
		return list;
	}
	
	
	/**
	 * 获取时刻执行任务数量
	 * @param taskType
	 * @return
	 * @throws Exception 
	 */
	@Override
	public List<TaskQuantityVO> getTaskCount(String taskType) throws Exception{	
		List<String> taskTypes = new ArrayList<String>();
		//获取指定任务类型的数据
		if(taskType == null || "".equals(taskType)){
			taskTypes.add("0");
			taskTypes.add("1");
			taskTypes.add("4");
		}else{
			taskTypes.add(taskType);
		}
		List<TaskQuantityVO> list = consoleDaoImpl.getTaskCount(taskTypes);
		//解析List<24小时>
		this.parseList(list);
		return list;
	}
	
	/**
	 * 解析返回list
	 * @param list
	 */
	private void parseList(List<TaskQuantityVO> list){
		//初始化24小时时间数组
		List<String> hourIndexs = new ArrayList<>();
		for(int i = 0; i < 24;i++){
			if(i <= 9){
				hourIndexs.add("0" + i);
			}else{ 
				hourIndexs.add("" + i);
			}
		}
		//解析24小时数组
		for(TaskQuantityVO taskQ : list){
			List<String> nList = new ArrayList<String>();
			Map<String,Integer> map = taskQ.getMap();
			for(Map.Entry<String, Integer> entry : map.entrySet()){
				nList.add(entry.getKey());
			}
			//查询结果中的hourIndex数组与24小时时间数组比较
			for (String hourIndex : hourIndexs) {
				if(!nList.contains(hourIndex)){
					map.put(hourIndex, 0);
				}
			}
			taskQ.setList(this.sortList(map));
		}
	}
	
	/**
	 * list排序--按照小时排序
	 * @param list
	 */
	private List<Object> sortList(Map<String,Integer> map){
		if(map == null || map.size() == 0){
			return null;
		}
		List<Object> list = new ArrayList<>();
		for(int i = 0; i < 24;i++){
			String index = "";
			if(i <=9 ){
				index = "0" + i;
			}else{
				index = "" + i;
			}
			list.add(map.get(index));
		}
		return list;
	}
}

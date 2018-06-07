package com.hand.bdss.web.operationcenter.console.dao;

import java.util.List;
import java.util.Map;

import com.hand.bdss.dsmp.model.ServiceStatus;
import com.hand.bdss.web.entity.ComStateNumEntity;
import com.hand.bdss.web.operationcenter.console.vo.TaskExecVO;
import com.hand.bdss.web.operationcenter.console.vo.TaskQuantityVO;

public interface ConsoleDao {
	
	int insertServiceStatus(ServiceStatus service) throws Exception;
	
	List<ServiceStatus> getServiceStatus(ServiceStatus service) throws Exception;
	
	List<ServiceStatus> listServiceStatusAll(ServiceStatus service) throws Exception;
	
	int updateServiceStatus(ServiceStatus service) throws Exception;
	
	List<ComStateNumEntity> getServiceComState(ServiceStatus service) throws Exception;

	Map<String,Integer> getTaskExec()throws Exception;

	List<TaskExecVO> getExecTime() throws Exception;

	List<TaskExecVO> getErrorCount() throws Exception;

	List<TaskQuantityVO> getTaskCount(List<String> nameList) throws Exception;
}

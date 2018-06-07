package com.hand.bdss.web.operationcenter.console.service;

import java.util.List;

import com.hand.bdss.dsmp.model.ServiceStatus;
import com.hand.bdss.web.entity.ComStateNumEntity;
import com.hand.bdss.web.operationcenter.console.vo.TaskExecVO;
import com.hand.bdss.web.operationcenter.console.vo.TaskQuantityVO;
import com.hand.bdss.web.operationcenter.console.vo.TaskStateVO;

public interface ConsoleService {
	
	int insertServiceStatus(ServiceStatus service) throws Exception;
	
	List<ServiceStatus> getServiceStatus(ServiceStatus service) throws Exception;
	
	List<ServiceStatus> listServiceStatusAll(ServiceStatus service) throws Exception;
	
	int updateServiceStatus(ServiceStatus service) throws Exception;
	
	List<ComStateNumEntity> getServiceComState(ServiceStatus service) throws Exception;

	List<TaskStateVO> getTaskExec()throws Exception;

	List<TaskExecVO> getExecTime() throws Exception;

	List<TaskExecVO> getErrorCount() throws Exception;

	List<TaskQuantityVO> getTaskCount(String taskType) throws Exception;
}

package com.hand.bdss.web.operationcenter.warn.dao;

import java.util.List;
import java.util.Map;

import com.hand.bdss.web.entity.EmailLogEntity;

public interface EmailLogDao {

	int insertEmailLog(EmailLogEntity emLogEntity) throws Exception;

	List<EmailLogEntity> selectEmailLogs(EmailLogEntity emailLogEntity, int startPage, int count) throws Exception;

	int getCounts(EmailLogEntity emailLogEntity) throws Exception;

	void insertSysErrorProp(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> listSysErrorProp() throws Exception;
	
}

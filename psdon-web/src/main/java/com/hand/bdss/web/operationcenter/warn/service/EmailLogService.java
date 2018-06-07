package com.hand.bdss.web.operationcenter.warn.service;

import java.util.List;
import java.util.Map;

import com.hand.bdss.web.entity.EmailLogEntity;

public interface EmailLogService {

	int insertEmailLog(EmailLogEntity emailLogEntity) throws Exception;

	List<EmailLogEntity> selectEmailLogs(EmailLogEntity emailLogEntity, int i, int j) throws Exception;

	int getCounts(EmailLogEntity emailLogEntity) throws Exception;

	List<Map<String, Object>> listSysErrorProp() throws Exception;

}

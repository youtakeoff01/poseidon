package com.hand.bdss.web.operationcenter.warn.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.hand.bdss.web.operationcenter.warn.dao.EmailLogDao;
import com.hand.bdss.web.operationcenter.warn.service.EmailLogService;
import org.springframework.stereotype.Service;

import com.hand.bdss.web.entity.EmailLogEntity;

@Service
public class EmailLogServiceImpl implements EmailLogService {

	@Resource
	private EmailLogDao emailLogDaoImpl;
	
	
	@Override
	public int insertEmailLog(EmailLogEntity emailLogEntity) throws Exception {
		int i = emailLogDaoImpl.insertEmailLog(emailLogEntity);
		return i;
	}


	@Override
	public List<EmailLogEntity> selectEmailLogs(EmailLogEntity emailLogEntity, int startpage, int count) throws Exception {
		List<EmailLogEntity> emailLogEntities = emailLogDaoImpl.selectEmailLogs(emailLogEntity,startpage,count);
		return emailLogEntities;
	}


	@Override
	public int getCounts(EmailLogEntity emailLogEntity) throws Exception {
		int counts = emailLogDaoImpl.getCounts(emailLogEntity);
		return counts;
	}


	@Override
	public List<Map<String, Object>> listSysErrorProp() throws Exception {
		List<Map<String,Object>> lists = emailLogDaoImpl.listSysErrorProp();
		return lists;
	}

}

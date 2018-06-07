package com.hand.bdss.web.operationcenter.warn.dao.impl;

import java.util.List;
import java.util.Map;

import com.hand.bdss.web.operationcenter.warn.dao.EmailLogDao;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.hand.bdss.web.entity.EmailLogEntity;
import com.hand.bdss.web.common.util.SupportDaoUtils;

@Repository
public class EmailLogDaoImpl extends SupportDaoUtils implements EmailLogDao {

	private static final String MAPPERURL = "com.hand.bdss.web.entity.EmailLogEntity.";
	@Override
	public int insertEmailLog(EmailLogEntity emLogEntity) throws Exception {
		int i = getSqlSession().insert(MAPPERURL + "insertEmailLog", emLogEntity);
		return i;
	}
	@Override
	public List<EmailLogEntity> selectEmailLogs(EmailLogEntity emailLogEntity, int startPage, int count) throws Exception {
		RowBounds bounds = new RowBounds(startPage,count);
		List<EmailLogEntity> emailLogEntities = getSqlSession().selectList(MAPPERURL+"selectEmailLogs", emailLogEntity,bounds);
		return emailLogEntities;
	}
	@Override
	public int getCounts(EmailLogEntity emailLogEntity) throws Exception {
		int counts = getSqlSession().selectOne(MAPPERURL+"getCounts", emailLogEntity);
		return counts;
	}
	@Override
	public void insertSysErrorProp(Map<String, Object> map) throws Exception {
		getSqlSession().insert(MAPPERURL+"insertSysErrorProp",map);
	}
	@Override
	public List<Map<String, Object>> listSysErrorProp() throws Exception {
		List<Map<String,Object>> lists = getSqlSession().selectList(MAPPERURL+"listSysErrorProp");
		return lists;
	}

}

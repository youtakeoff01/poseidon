package com.hand.bdss.web.dataprocessing.splunkapi.dao.impl;

import org.springframework.stereotype.Repository;

import com.hand.bdss.web.common.util.SupportDaoUtils;
import com.hand.bdss.web.dataprocessing.splunkapi.dao.SplunkTaskDao;
import com.hand.bdss.web.entity.LastPullDateEntity;

@Repository("splunkTaskDaoImpl")
public class SplunkTaskDaoImpl extends SupportDaoUtils implements SplunkTaskDao{
	
	private static final String MAPPERURL = "com.hand.bdss.web.entity.LastPullDateEntity.";

	@Override
	public LastPullDateEntity getLastPullDate(LastPullDateEntity splunkEntity) {
		return getSqlSession().selectOne(MAPPERURL+"getLastPullDate",splunkEntity);
	}

	@Override
	public int insertLastPullDate(LastPullDateEntity splunkEntity) {
		return getSqlSession().insert(MAPPERURL+"insertLastPullDate",splunkEntity);
	}

	@Override
	public int updateLastPullDate(LastPullDateEntity splunkEntity) {
		return getSqlSession().update(MAPPERURL+"updateLastPullDate",splunkEntity);
	}
	
}

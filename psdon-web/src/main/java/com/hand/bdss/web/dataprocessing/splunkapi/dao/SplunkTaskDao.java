package com.hand.bdss.web.dataprocessing.splunkapi.dao;

import com.hand.bdss.web.entity.LastPullDateEntity;

public interface SplunkTaskDao {
	
	LastPullDateEntity getLastPullDate(LastPullDateEntity splunkEntity);

	int insertLastPullDate(LastPullDateEntity splunkEntity);
	
	int updateLastPullDate(LastPullDateEntity splunkEntity);
	
}

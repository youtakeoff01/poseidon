package com.hand.bdss.web.platform.management.dao.impl;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.hand.bdss.web.platform.management.dao.AutoConfigDao;
import com.hand.bdss.web.common.util.SupportDaoUtils;
import com.hand.bdss.web.common.vo.ConfigInfo;

@Repository
public class AutoConfigDaoImpl extends  SupportDaoUtils implements AutoConfigDao{

	private static final String MAPPERURL = "com.hand.bdss.web.common.vo.ConfigInfo.";
	
	@Override
	public int addConfig(ConfigInfo configs) throws Exception{
		int i = getSqlSession().insert(MAPPERURL+"addConfig",configs);
		return i;
	}

	@Override
	public List<ConfigInfo> listConfigs(ConfigInfo config, int startPage, int count) throws Exception{
		RowBounds rowBounds = new RowBounds(startPage,count);
		List<ConfigInfo> listConfigs = getSqlSession().selectList(MAPPERURL+"listConfigs",config,rowBounds);
		return listConfigs;
	}
	
}

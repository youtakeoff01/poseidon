package com.hand.bdss.web.platform.management.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.bdss.web.platform.management.dao.AutoConfigDao;
import com.hand.bdss.web.platform.management.service.AutoConfigService;
import com.hand.bdss.web.common.util.GetRedisConnUtils;
import com.hand.bdss.web.common.vo.ConfigInfo;

@Service
public class AutoConfigServiceImpl implements AutoConfigService{

	@Resource
	private AutoConfigDao autoConfigDaoImpl;
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public int addConfig(List<ConfigInfo> configs) throws Exception{
		int i = 0;
		for (ConfigInfo configInfo : configs) {
			i = autoConfigDaoImpl.addConfig(configInfo);
			//将数据同步一份到redis服务器上
			GetRedisConnUtils.set(configInfo);
			i++;
		}
		
		return i;
	}

	@Override
	public List<ConfigInfo> listConfigs(ConfigInfo config,int startPage,int count) throws Exception{
		List<ConfigInfo> listConfigs = autoConfigDaoImpl.listConfigs(config,startPage,count);
		return listConfigs;
	}

}

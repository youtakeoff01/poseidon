package com.hand.bdss.web.platform.management.service;

import java.util.List;

import com.hand.bdss.web.common.vo.ConfigInfo;

public interface AutoConfigService {

	int addConfig(List<ConfigInfo> configs) throws Exception;

	List<ConfigInfo> listConfigs(ConfigInfo config,int startPage,int count) throws Exception;
	
}

package com.hand.bdss.web.platform.management.dao;

import java.util.List;

import com.hand.bdss.web.common.vo.ConfigInfo;

public interface AutoConfigDao {


	List<ConfigInfo> listConfigs(ConfigInfo config,int startPage,int count) throws Exception;

	int addConfig(ConfigInfo configs) throws Exception;
}
package com.hand.bdss.web.platform.management.dao;

import java.util.List;

import com.hand.bdss.web.entity.LogEntity;

public interface LogDao {
	int insert(LogEntity logEntity) throws Exception;

	List<LogEntity> selectList(LogEntity logEntity, int startPage, int count) throws Exception;

	int getCountAll(LogEntity logEntity) throws Exception;

	int deleteLog(List<LogEntity> logEntity);
}

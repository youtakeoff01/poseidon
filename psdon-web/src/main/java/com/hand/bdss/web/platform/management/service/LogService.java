package com.hand.bdss.web.platform.management.service;

import java.util.List;

import com.hand.bdss.web.entity.LogEntity;

public interface LogService {
	int insertLog(LogEntity logEntity) throws Exception;

	List<LogEntity> selectLog(LogEntity logEntiyt, int startPage, int count) throws Exception;

	int getCountAll(LogEntity logEntity) throws Exception;

	int deleteLog(List<LogEntity> list);
}

package com.hand.bdss.web.platform.management.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.entity.LogEntity;
import com.hand.bdss.web.platform.management.dao.LogDao;
import com.hand.bdss.web.platform.management.service.LogService;

@Service
public class LogServiceImpl implements LogService {

	@Resource
	private LogDao logDaoImpl;

	@Override
	public int insertLog(LogEntity logEntity) throws Exception {
		int i = logDaoImpl.insert(logEntity);
		return i;
	}

	@Override
	public List<LogEntity> selectLog(LogEntity logEntity, int startPage, int count) throws Exception {
		String logType = logEntity.getLogType();
		List<LogEntity> lists = logDaoImpl.selectList(logEntity, startPage, count);

		if(LogUtils.LOGTYPE_SQL.equalsIgnoreCase(logType)){
			List<LogEntity> list = new ArrayList<LogEntity>();
			if(lists != null && lists.size() > 0){
				for(LogEntity log : lists){
					String logContent = log.getLogContent();
					String sqlType = logContent.substring(0,logContent.indexOf(":"));
					String sqlContent = logContent.substring(logContent.indexOf(":")+1,logContent.length());
					
					log.setLogType(sqlType);
					log.setLogContent(sqlContent);
					list.add(log);
				}
			}
			return list;
		}
		return lists;
	}
	
	@Override
	public int getCountAll(LogEntity logEntity) throws Exception {
		int i = logDaoImpl.getCountAll(logEntity);
		return i;
	}

	@Override
	public int deleteLog(List<LogEntity> logEntity) {
		int i = logDaoImpl.deleteLog(logEntity);
		return i;
	}
}

package com.hand.bdss.web.platform.management.dao.impl;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.hand.bdss.web.entity.LogEntity;
import com.hand.bdss.web.platform.management.dao.LogDao;
import com.hand.bdss.web.common.util.SupportDaoUtils;

@Repository
public class LogDaoImpl extends SupportDaoUtils implements LogDao {
	private static final String MAPPERURL = "com.hand.bdss.web.entity.LogEntity.";

	@Override
	public int insert(LogEntity logEntity) throws Exception {
		// TODO Auto-generated method stub
		int i = getSqlSession().insert(MAPPERURL + "insert", logEntity);
		return i;
	}

	@Override
	public List<LogEntity> selectList(LogEntity logEntity, int startPage, int count) throws Exception {
		RowBounds rowBounds = new RowBounds(startPage, count);
		List<LogEntity> lists = getSqlSession().selectList(MAPPERURL + "selectList", logEntity, rowBounds);
		return lists;
	}

	@Override
	public int getCountAll(LogEntity logEntity) throws Exception{
		int i = getSqlSession().selectOne(MAPPERURL + "getCountAll", logEntity);
		return i;
	}

	@Override
	public int deleteLog(List<LogEntity> logEntity) {
		int i = getSqlSession().delete(MAPPERURL+"deleteLog",logEntity);
		return i;
	}

}

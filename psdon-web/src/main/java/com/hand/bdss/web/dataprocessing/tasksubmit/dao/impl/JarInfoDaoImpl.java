package com.hand.bdss.web.dataprocessing.tasksubmit.dao.impl;

import java.util.List;

import com.hand.bdss.web.entity.TaskAttributeEntity;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.hand.bdss.web.common.util.SupportDaoUtils;
import com.hand.bdss.web.dataprocessing.tasksubmit.dao.JarInfoDao;
import com.hand.bdss.web.entity.JarInfoEntity;

@Repository
public class JarInfoDaoImpl extends SupportDaoUtils implements JarInfoDao {
	private static final String MAPPERURL = "com.hand.bdss.web.entity.JarInfoEntity.";

	@Override
	public long insertJarInfo(JarInfoEntity jarInfo) {
		getSqlSession().insert(MAPPERURL+"insertJarInfo",jarInfo);
		return Long.parseLong(jarInfo.getId());
	}
	@Override
	public List<JarInfoEntity> listTaskJars(JarInfoEntity jarInfo, int startPage, int count) {
		RowBounds rowBounds = new RowBounds(startPage,count);
		return getSqlSession().selectList(MAPPERURL+"listTaskJars", jarInfo, rowBounds);
	}

	@Override
	public int deleteJarInfos(List<JarInfoEntity> lists) {
		return getSqlSession().delete(MAPPERURL+"deleteJarInfos", lists);
		
	}
	@Override
	public int listTaskJarCounts(JarInfoEntity jarInfo) {
		return getSqlSession().selectOne(MAPPERURL+"listTaskJarCounts",jarInfo);
	}

}

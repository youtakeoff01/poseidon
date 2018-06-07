package com.hand.bdss.web.dataprocessing.tasksubmit.dao.impl;

import java.util.List;

import com.hand.bdss.web.entity.JarInfoEntity;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.hand.bdss.web.common.util.SupportDaoUtils;
import com.hand.bdss.web.dataprocessing.tasksubmit.dao.TaskAttributeDao;
import com.hand.bdss.web.entity.TaskAttributeEntity;
@Repository
public class TaskAttributeDaoImpl extends SupportDaoUtils implements TaskAttributeDao {
	
	private static String MAPPERURL = "com.hand.bdss.web.entity.TaskAttributeEntity.";

	@Override
	public long insertTaskAttribute(TaskAttributeEntity taskAttributeEntity) throws Exception {
		getSqlSession().insert(MAPPERURL + "insertTaskAttribute", taskAttributeEntity);
		return Long.parseLong(taskAttributeEntity.getId());
	}
	@Override
	public int deleteTaskAttribute(List<TaskAttributeEntity> taskAttributeEntity) throws Exception {
		return getSqlSession().delete(MAPPERURL+"deleteTaskAttribute", taskAttributeEntity);
	}
	@Override
	public List<TaskAttributeEntity> listTaskAttribute(TaskAttributeEntity taskAttributeEntity, int startPage,
			int count) {
		RowBounds rowBounds = new RowBounds(startPage, count);
		return getSqlSession().selectList(MAPPERURL+"listTaskAttribute", taskAttributeEntity, rowBounds);
	}

	@Override
	public int updateJarTask(TaskAttributeEntity taskAttributeEntity) throws Exception {
		return getSqlSession().update(MAPPERURL+"updateTaskAttribute",taskAttributeEntity);
	}

	@Override
	public int listJars(JarInfoEntity jarInfoEntity) throws Exception {
		return getSqlSession().selectOne(MAPPERURL+"listTasks",jarInfoEntity);
	}
}

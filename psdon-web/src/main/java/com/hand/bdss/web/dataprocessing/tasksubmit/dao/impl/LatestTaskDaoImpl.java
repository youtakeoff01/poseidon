package com.hand.bdss.web.dataprocessing.tasksubmit.dao.impl;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.hand.bdss.web.common.util.SupportDaoUtils;
import com.hand.bdss.web.common.vo.ShowTaskJarInfoVO;
import com.hand.bdss.web.dataprocessing.tasksubmit.dao.LatestTaskDao;
import com.hand.bdss.web.entity.LatestTaskEntity;

@Repository
public class LatestTaskDaoImpl extends SupportDaoUtils implements LatestTaskDao{

	private static final String MAPPERURL = "com.hand.bdss.web.entity.LatestTaskEntity.";
	
	@Override
	public int insertLatestTask(LatestTaskEntity latestTaskEntity) throws Exception {
		return getSqlSession().insert(MAPPERURL+"insertLatestTask",latestTaskEntity);
	}

	@Override
	public int updateLatestTask(LatestTaskEntity latestTaskEntity) throws Exception {
		return getSqlSession().update(MAPPERURL+"updateLatestTask",latestTaskEntity);
	}

	@Override
	public int updateLatestTaskById(LatestTaskEntity latestTaskEntity) throws Exception {
		return getSqlSession().update(MAPPERURL+"updateLatestTaskById",latestTaskEntity);
	}

	@Override
	public int deleteLatestTask(List<LatestTaskEntity> lists) throws Exception {
		return getSqlSession().delete(MAPPERURL+"deleteLatestTask",lists);
	}

	@Override
	public List<LatestTaskEntity> listLatestTaskEntity(LatestTaskEntity latestTaskEntity, int startPage, int count)
			throws Exception {
		RowBounds rows = new RowBounds(startPage, count);
		return getSqlSession().selectList(MAPPERURL+"listLatestTaskEntity",latestTaskEntity, rows);
	}

	@Override
	public List<ShowTaskJarInfoVO> listJarTasks(LatestTaskEntity latestTaskEntity, int startPage, int count) {
		RowBounds rows = new RowBounds(startPage, count);
		return getSqlSession().selectList(MAPPERURL+"listJarTasks", latestTaskEntity, rows);
	}

	@Override
	public int checkJarTaskName(LatestTaskEntity latestTaskEntity) throws Exception {
		return getSqlSession().selectOne(MAPPERURL+"checkJarTaskName", latestTaskEntity);
	}

	@Override
	public int listJarTaskCounts(LatestTaskEntity latestTaskEntity) throws Exception {
		return getSqlSession().selectOne(MAPPERURL+"listJarTaskCounts",latestTaskEntity);
	}
}

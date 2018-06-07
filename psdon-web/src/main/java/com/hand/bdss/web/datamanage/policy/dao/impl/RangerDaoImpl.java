package com.hand.bdss.web.datamanage.policy.dao.impl;

import java.util.List;

import com.hand.bdss.web.datamanage.policy.dao.RangerDao;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.hand.bdss.web.entity.RangerEntity;
import com.hand.bdss.web.common.util.SupportDaoUtils;

@Repository
public class RangerDaoImpl extends SupportDaoUtils implements RangerDao {

	private static final String MAPPERURL = "com.hand.bdss.web.entity.RangerEntity.";
	@Override
	public int insertRanger(RangerEntity rangerEntity) throws Exception{
		int i = getSqlSession().insert(MAPPERURL + "insertRanger", rangerEntity);
		return i;
	}
	@Override
	public int updateRangerByName(RangerEntity rangerEntity) throws Exception{
		int i = getSqlSession().update(MAPPERURL + "updateRangerByName", rangerEntity);
		return i;
	}
	@Override
	public List<RangerEntity> listHivePolicy(RangerEntity rangerEntity, int startPage, int count) throws Exception{
		RowBounds rowBounds = new RowBounds(startPage, count);
		List<RangerEntity> lists = getSqlSession().selectList(MAPPERURL + "listHivePolicy", rangerEntity, rowBounds);
		return lists;
	}
	@Override
	public int deleteRangerByName(List<RangerEntity> rangerEntitys) throws Exception{
		int i = getSqlSession().update(MAPPERURL + "deleteRangerByName", rangerEntitys);
		return i;
	}
	@Override
	public int getCounts(RangerEntity rangerEntity) throws Exception{
		int i = getSqlSession().selectOne(MAPPERURL + "getCounts", rangerEntity);
		return i;
	}


	public int checkUser(RangerEntity rangerEntity) {
		int i = getSqlSession().selectOne(MAPPERURL+"checkUser",rangerEntity);
		return i;
	}
	@Override
	public RangerEntity getUser(RangerEntity rangerEntity) {
		RangerEntity ranger = getSqlSession().selectOne(MAPPERURL+"getUser",rangerEntity);
		return ranger;
	}


	public RangerEntity getSelectedTables(int id) {
		RangerEntity rangerEntity= getSqlSession().selectOne(MAPPERURL + "getSelectedTables", id);
		return rangerEntity;
	}
	
	

}

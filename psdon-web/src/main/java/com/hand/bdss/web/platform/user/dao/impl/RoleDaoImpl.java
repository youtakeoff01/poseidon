package com.hand.bdss.web.platform.user.dao.impl;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.hand.bdss.web.entity.RoleEntity;
import com.hand.bdss.web.platform.user.dao.RoleDao;
import com.hand.bdss.web.common.util.SupportDaoUtils;

@Repository
public class RoleDaoImpl extends SupportDaoUtils implements RoleDao {
	private static final String MAPPERURL = "com.hand.bdss.web.entity.RoleEntity.";

	@Override
	public List<RoleEntity> roleSelect(RoleEntity roleEntity, int startPage, int count) throws Exception{
		RowBounds rowBounds = new RowBounds(startPage, count);
		List<RoleEntity> lists = getSqlSession().selectList(MAPPERURL + "selectList", roleEntity, rowBounds);
		return lists;
	}

	@Override
	public int getCountAll(RoleEntity roleEntity) throws Exception{
		int i = getSqlSession().selectOne(MAPPERURL + "getCountAll", roleEntity);
		return i;
	}
	@Override
	public List<String> roleSelectByUseName(List<String> userNames) throws Exception{
		List<String> lists = getSqlSession().selectList(MAPPERURL + "roleSelectByUseName",userNames);
		return lists;
	}
}

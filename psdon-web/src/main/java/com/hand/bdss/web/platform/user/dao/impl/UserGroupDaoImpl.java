package com.hand.bdss.web.platform.user.dao.impl;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.hand.bdss.web.entity.UserGroupEntity;
import com.hand.bdss.web.platform.user.dao.UserGroupDao;
import com.hand.bdss.web.common.util.SupportDaoUtils;

@Repository
public class UserGroupDaoImpl extends SupportDaoUtils implements UserGroupDao {
	private static final String MAPPERURL = "com.hand.bdss.web.entity.UserGroupEntity.";

	@Override
	public int insertUsergroup(UserGroupEntity userGroupEntity) {
		int i = getSqlSession().insert(MAPPERURL + "insertUsergroup", userGroupEntity);
		return i;
	}

	public int deleteUsergroup(List<UserGroupEntity> list) {
		int i = getSqlSession().update(MAPPERURL + "deleteUsergroup", list);
		return i;
	}

	@Override
	public int updateUsergroup(UserGroupEntity userGroupEntity) {
		int i = getSqlSession().update(MAPPERURL + "updateUsergroup", userGroupEntity);
		return i;
	}

	@Override
	public List<UserGroupEntity> listUsergroup(UserGroupEntity userGroupEntity, int startPage, int count) {
		RowBounds rowbouds = new RowBounds(startPage, count);
		List<UserGroupEntity> lists = getSqlSession().selectList(MAPPERURL + "listUsergroup", userGroupEntity,
				rowbouds);
		return lists;
	}

	@Override
	public int getCountAll(UserGroupEntity userGroupEntity) {
		int count = getSqlSession().selectOne(MAPPERURL + "getCountAll", userGroupEntity);
		return count;
	}

	@Override
	public int checkUserGroup(UserGroupEntity userGroupEntity) {
		int i = getSqlSession().selectOne(MAPPERURL + "checkUserGroup", userGroupEntity);
		return i;
	}

	@Override
	public UserGroupEntity getGroup(UserGroupEntity userGroup) throws Exception {
		UserGroupEntity group = getSqlSession().selectOne(MAPPERURL + "getGroup", userGroup);
		return group;
	}

}

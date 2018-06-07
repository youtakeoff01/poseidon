package com.hand.bdss.web.platform.user.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.hand.bdss.web.entity.UserEntity;
import com.hand.bdss.web.platform.user.dao.UserDao;
import com.hand.bdss.web.common.util.SupportDaoUtils;

@Repository
public class UserDaoImpl extends SupportDaoUtils implements UserDao {

	private static final String MAPPERURL = "com.hand.bdss.web.entity.UserEntity.";

	@Override
	public List<UserEntity> listUsers(UserEntity user, int startPage, int count) throws Exception {
		RowBounds rowBounds = new RowBounds(startPage, count);
		List<UserEntity> users = getSqlSession().selectList(MAPPERURL + "listUsers", user, rowBounds);
		return users;
	}

	@Override
	public int insertUser(UserEntity user) throws Exception {
		int i = getSqlSession().insert(MAPPERURL + "insertUser", user);
		return i;
	}

	@Override
	public int getUserIdByName(UserEntity user) throws Exception {
		int id = getSqlSession().selectOne(MAPPERURL + "getUserIdByName", user);
		return id;
	}

	@Override
	public String getUserNameById(int id) throws Exception {
		return getSqlSession().selectOne(MAPPERURL + "getUserNameById", id);
	}

	@Override
	public int insertUserRole(Map<String, String> map) throws Exception {
		int i = getSqlSession().insert(MAPPERURL + "insertUserRole", map);
		return i;
	}

	@Override
	public int checkUser(UserEntity user) throws Exception {
		int i = getSqlSession().selectOne(MAPPERURL + "checkUser", user);
		return i;
	}

	@Override
	public int updateUser(UserEntity user) throws Exception {
		int i = getSqlSession().update(MAPPERURL + "updateUser", user);
		return i;
	}

	@Override
	public int deleteUser(List<UserEntity> ids) throws Exception {
		int i = getSqlSession().delete(MAPPERURL + "deleteUser", ids);
		return i;
	}

	@Override
	public int deleteUserRole(List<Integer> ids) throws Exception {
		int i = getSqlSession().delete(MAPPERURL + "deleteUserRole", ids);
		return i;
	}

	@Override
	public int updateUserRole(UserEntity user) throws Exception {
		int i = getSqlSession().update(MAPPERURL + "updateUserRole", user);
		return i;
	}

	@Override
	public List<String> getMenuByUser(String userName) throws Exception {
		List<String> lists = getSqlSession().selectList(MAPPERURL + "getMenuByUser", userName);
		return lists;
	}

	@Override
	public int getCountAll(UserEntity user) throws Exception {
		int count = getSqlSession().selectOne(MAPPERURL + "getCountAll", user);
		return count;
	}

	@Override
	public UserEntity getUser(UserEntity user) throws Exception {
		UserEntity userEntity = getSqlSession().selectOne(MAPPERURL + "getUser", user);
		return userEntity;
	}

	/*
	 * @Override public List<String> getMenuByLdapUser(String userName) throws
	 * Exception { return getSqlSession().selectList(MAPPERURL+"getMenuByLdapUser",
	 * userName); }
	 */

	@Override
	public int resetUserPassword(UserEntity user) throws Exception {
		int i = getSqlSession().delete(MAPPERURL + "resetUserPassword", user);
		return i;
	}

	@Override
	public List<UserEntity> listTeacherMsgs(UserEntity user) throws Exception {
		return getSqlSession().selectList(MAPPERURL+"listTeacherMsgs", user);
	}

}

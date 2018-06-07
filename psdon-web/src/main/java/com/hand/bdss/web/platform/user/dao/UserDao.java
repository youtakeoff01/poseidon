package com.hand.bdss.web.platform.user.dao;

import java.util.List;
import java.util.Map;

import com.hand.bdss.web.entity.UserEntity;

public interface UserDao {

	List<UserEntity> listUsers(UserEntity user, int startPage, int count) throws Exception;

	int insertUser(UserEntity user) throws Exception;

	int getUserIdByName(UserEntity user) throws Exception;

	String getUserNameById(int id) throws Exception;

	int insertUserRole(Map<String, String> map) throws Exception;

	int checkUser(UserEntity user) throws Exception;

	int updateUser(UserEntity user) throws Exception;

	int updateUserRole(UserEntity user) throws Exception;

	int deleteUser(List<UserEntity> user) throws Exception;

	int deleteUserRole(List<Integer> ids) throws Exception;

	List<String> getMenuByUser(String userName) throws Exception;

	int getCountAll(UserEntity user) throws Exception;

	UserEntity getUser(UserEntity user) throws Exception;

	// List<String> getMenuByLdapUser(String userName) throws Exception;

	int resetUserPassword(UserEntity user) throws Exception;
	
	//查询所有具有审批角色的利物浦老师信息
	List<UserEntity> listTeacherMsgs(UserEntity user) throws Exception;

}

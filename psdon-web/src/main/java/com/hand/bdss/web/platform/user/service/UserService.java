package com.hand.bdss.web.platform.user.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.entity.UserEntity;

public interface UserService {

	List<UserEntity> listUsers(UserEntity user, int startPage, int count) throws Exception;

	int insertUser(UserEntity user, HttpServletRequest request) throws Exception;

	int checkUser(UserEntity user) throws Exception;

	int updateUser(UserEntity user) throws Exception;

	int deleteUser(List<UserEntity> user) throws Exception;

	List<String> getMenuByUser(String userName) throws Exception;

	int getCountAll(UserEntity user) throws Exception;

	UserEntity getUser(UserEntity user) throws Exception;

	// List<String> getMenuByLdapUser(String userName) throws Exception;

	int resetUserPassword(UserEntity user) throws Exception;

}

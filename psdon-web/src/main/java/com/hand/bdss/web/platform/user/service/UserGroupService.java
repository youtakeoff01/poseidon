package com.hand.bdss.web.platform.user.service;

import java.util.List;

import com.hand.bdss.web.entity.UserGroupEntity;

public interface UserGroupService {

	int insertUsergroup(UserGroupEntity userGroupEntity) throws Exception;

	int deleteUsergroup(List<UserGroupEntity> list) throws Exception;

	int updateUsergroup(UserGroupEntity userGroupEntity) throws Exception;

	List<UserGroupEntity> listUsergroup(UserGroupEntity userGroupEntity, int startPage, int count) throws Exception;

	int getCountAll(UserGroupEntity userGroupEntity) throws Exception;

	int checkUserGroup(UserGroupEntity userGroup) throws Exception;
	UserGroupEntity getGroup(UserGroupEntity userGroup) throws Exception;
}

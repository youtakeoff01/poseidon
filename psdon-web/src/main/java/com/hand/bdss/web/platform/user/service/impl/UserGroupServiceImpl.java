package com.hand.bdss.web.platform.user.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hand.bdss.web.entity.UserGroupEntity;
import com.hand.bdss.web.platform.user.dao.UserGroupDao;
import com.hand.bdss.web.platform.user.service.UserGroupService;

@Service
public class UserGroupServiceImpl implements UserGroupService {

	@Resource
	private UserGroupDao userGroupDaoImpl;

	@Override
	public int insertUsergroup(UserGroupEntity userGroupEntity) throws Exception {
		int i = userGroupDaoImpl.insertUsergroup(userGroupEntity);
		return i;
	}

	@Override
	public int deleteUsergroup(List<UserGroupEntity> list) throws Exception {
		int i = userGroupDaoImpl.deleteUsergroup(list);
		return i;
	}

	@Override
	public int updateUsergroup(UserGroupEntity userGroupEntity) throws Exception {
		if(userGroupEntity.getStatus() == null){
			userGroupEntity.setStatus("0");
		}
		int i = userGroupDaoImpl.updateUsergroup(userGroupEntity);
		return i;
	}

	@Override
	public List<UserGroupEntity> listUsergroup(UserGroupEntity userGroupEntity, int startPage, int count) throws Exception {
		List<UserGroupEntity> list = userGroupDaoImpl.listUsergroup(userGroupEntity, startPage, count);
		return list;
	}

	@Override
	public int getCountAll(UserGroupEntity userGroupEntity) throws Exception {
		int count = userGroupDaoImpl.getCountAll(userGroupEntity);
		return count;
	}

	@Override
	public int checkUserGroup(UserGroupEntity userGroup) throws Exception {
		int i = userGroupDaoImpl.checkUserGroup(userGroup);
		return i;
	}

	@Override
	public UserGroupEntity getGroup(UserGroupEntity userGroup) throws Exception {
		UserGroupEntity group = userGroupDaoImpl.getGroup(userGroup);
		return group;
	}

}

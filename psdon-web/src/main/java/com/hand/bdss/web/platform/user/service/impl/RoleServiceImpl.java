package com.hand.bdss.web.platform.user.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hand.bdss.web.entity.RoleEntity;
import com.hand.bdss.web.platform.user.dao.RoleDao;
import com.hand.bdss.web.platform.user.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

	@Resource
	private RoleDao roleDaoImpl;

	@Override
	public List<RoleEntity> roleSelect(RoleEntity roleEntity, int startPage, int count) throws Exception{
		List<RoleEntity> lists = roleDaoImpl.roleSelect(roleEntity, startPage, count);
		return lists;
	}

	@Override
	public int getCountAll(RoleEntity roleEntity) throws Exception{
		int i = roleDaoImpl.getCountAll(roleEntity);
		return i;
	}

}

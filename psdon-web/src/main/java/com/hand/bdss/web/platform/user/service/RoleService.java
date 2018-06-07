package com.hand.bdss.web.platform.user.service;

import java.util.List;

import com.hand.bdss.web.entity.RoleEntity;

public interface RoleService {
	List<RoleEntity> roleSelect(RoleEntity roleEntity, int startPage, int count) throws Exception;

	int getCountAll(RoleEntity roleEntity) throws Exception;
}

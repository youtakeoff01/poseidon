package com.hand.bdss.web.platform.user.dao;

import java.util.List;

import com.hand.bdss.web.entity.RoleEntity;

public interface RoleDao {
	List<RoleEntity> roleSelect(RoleEntity roleEntity, int startPage, int count) throws Exception;

	int getCountAll(RoleEntity roleEntity) throws Exception;
	
	public List<String> roleSelectByUseName(List<String> userNames) throws Exception;
}

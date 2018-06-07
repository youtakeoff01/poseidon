package com.hand.bdss.web.datamanage.policy.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.bdss.web.entity.RangerEntity;

public interface RangerDao {

	int insertRanger(RangerEntity rangerEntity) throws Exception;

	int updateRangerByName(RangerEntity rangerEntity) throws Exception;

	List<RangerEntity> listHivePolicy(RangerEntity rangerEntity, int startPage, int count) throws Exception;
	
	int deleteRangerByName(List<RangerEntity> rangerEntitys) throws Exception;

	int getCounts(RangerEntity rangerEntity) throws Exception;



	int checkUser(RangerEntity rangerEntity);

	RangerEntity getUser(RangerEntity rangerEntity);

	RangerEntity getSelectedTables(@Param("id")int id);


}

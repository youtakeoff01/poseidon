package com.hand.bdss.web.datamanage.policy.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.dsmp.model.HivePolicy;
import com.hand.bdss.web.entity.RangerEntity;

public interface RangerService {

	int insertRanger(HivePolicy hivePolicy) throws Exception;

	int updateRangerByName(HivePolicy hivePolicy) throws Exception;

	List<HivePolicy> listHivePolicy(HivePolicy hivePolicy, int startPage, int count) throws Exception;

	int deleteRangerByName(List<HivePolicy> names) throws Exception;
	
	RangerEntity policyToRanger(HivePolicy hivePolicy,String type) throws Exception;
	
	HivePolicy rangerToPolicy(RangerEntity rangerEntity) throws Exception;
	
	int getCounts(HivePolicy hivePolicy) throws Exception;

	int checkUser(RangerEntity rangerEntity);

	RangerEntity getUser(RangerEntity rangerEntity);

	HivePolicy getSelectedTables(int id, HttpServletRequest request) throws Exception;

}

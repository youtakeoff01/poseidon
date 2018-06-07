package com.hand.bdss.web.datamanage.policy.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.dsmp.model.HivePolicy;

public interface PolicyService {

	boolean insertPolicy(HivePolicy strJson, HttpServletRequest request) throws Exception;

	boolean updatePolicy(String strJson, HttpServletRequest request) throws Exception;

	boolean delPolicy(String strJson, HttpServletRequest request) throws Exception;

	List<HivePolicy> selectPolicy(String strJson, HttpServletRequest request) throws Exception;
	
	public HivePolicy getRangerPolicy(String StrJson,String type) throws Exception;

}

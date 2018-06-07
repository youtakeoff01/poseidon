package com.hand.bdss.web.datamanage.policy.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.datamanage.policy.service.PolicyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.bdss.dsmp.model.HivePolicy;
import com.hand.bdss.dsmp.service.privilege.RangerPolicyManage;
import com.hand.bdss.web.entity.RangerEntity;
import com.hand.bdss.web.platform.user.dao.RoleDao;
import com.hand.bdss.web.datamanage.policy.dao.RangerDao;
import com.hand.bdss.web.datamanage.policy.service.RangerService;
import com.hand.bdss.web.common.util.JsonUtils;

@Service
public class PolicyServiceImpl implements PolicyService {
	@Resource
	private RoleDao roleDaoImpl;
	@Resource
	private RangerService rangerServiceImpl;
	@Resource
	private RangerDao rangerDaoImpl;

	@Override
	@Transactional(rollbackFor=Exception.class)
	public boolean insertPolicy(HivePolicy hivePolicy, HttpServletRequest request) throws Exception {
		//将插入的ranger策略保存到本地一份
		RangerEntity ranger = rangerServiceImpl.policyToRanger(hivePolicy, "insert");
		
		boolean boo = getObject().createPolicy(hivePolicy);
		if(!boo){
			throw new Exception();
		}
		rangerDaoImpl.insertRanger(ranger);
		return boo;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public boolean updatePolicy(String strJson, HttpServletRequest request) throws Exception {
		HivePolicy hivePolicy = JsonUtils.toObject(strJson, HivePolicy.class);
		boolean boo = getObject().updatePolicy(getRangerPolicy(strJson,"update"));
		if(!boo){
			throw new Exception();
		}
		rangerServiceImpl.updateRangerByName(hivePolicy);
		return boo;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public boolean delPolicy(String strJson, HttpServletRequest request) throws Exception {
		List<HivePolicy> hives = JsonUtils.toArray(strJson, HivePolicy.class);
		rangerServiceImpl.deleteRangerByName(hives);
		boolean boo = getObject().deletePolicy(getRangerPolicy(strJson,"delete"));
		if(!boo){
			throw new Exception();
		}
		return boo;
	}

	@Override
	public List<HivePolicy> selectPolicy(String strJson, HttpServletRequest request) throws Exception {
		List<HivePolicy> hivePolicys = getObject().searchPolicies(getRangerPolicy(strJson,"select"));
		if(hivePolicys!=null && hivePolicys.size()>0){
			for (HivePolicy hivePolicy : hivePolicys) {
				if(hivePolicy.getUser()!=null && hivePolicy.getUser().size()>0){
					hivePolicy.setRole(roleDaoImpl.roleSelectByUseName(hivePolicy.getUser()));
				}
			}
		}
		return hivePolicys;
	}

	public HivePolicy getRangerPolicy(String StrJson,String type) {
		if("delete".equalsIgnoreCase(type)){
			List<HivePolicy> hives = JsonUtils.toArray(StrJson, HivePolicy.class);
			HivePolicy hive = hives.get(0);
			hive.setServiceName("hdp_hive");
			return hive;
		}
		HivePolicy hive = JsonUtils.toObject(StrJson, HivePolicy.class);
		hive.setServiceName("hdp_hive");
		if("insert".equalsIgnoreCase(type)){
			hive.setName(UUID.randomUUID().toString()+hive.getUser().get(0));
			List<String> strs = hive.getType();
			if(strs!=null && strs.size()>0 && strs.contains("all")){
				List<String> types = new ArrayList<String>();
				types.add("select");
				types.add("update");
				strs.remove("all");
				strs.addAll(types);
			}
		}
//		if("select".equalsIgnoreCase(type)){
//			List<String> types = new ArrayList<String>();
//			types.add("select");
//			hive.setType(types);
//		}
		
		return hive;
	}

	public RangerPolicyManage getObject() throws Exception{
		return new RangerPolicyManage();
	}
}

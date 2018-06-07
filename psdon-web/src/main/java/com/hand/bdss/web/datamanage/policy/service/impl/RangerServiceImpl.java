package com.hand.bdss.web.datamanage.policy.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.datamanage.policy.dao.RangerDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.hand.bdss.dsmp.model.HivePolicy;
import com.hand.bdss.web.entity.RangerEntity;
import com.hand.bdss.web.platform.user.dao.RoleDao;
import com.hand.bdss.web.datamanage.policy.service.RangerService;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.util.StrUtils;

@Service
public class RangerServiceImpl implements RangerService{

	@Resource
	private RangerDao rangerDaoImpl;
	@Resource
	private RoleDao roleDaoImpl;
	
	@Override
	public int insertRanger(HivePolicy hivePolicy)  throws Exception{
		RangerEntity rangerEntity = policyToRanger(hivePolicy,"insert");
		int i = rangerDaoImpl.insertRanger(rangerEntity);
		return i;
	}

	@Override
	public int updateRangerByName(HivePolicy hivePolicy) throws Exception{
		RangerEntity rangerEntity = policyToRanger(hivePolicy,"update");
		int i = rangerDaoImpl.updateRangerByName(rangerEntity);
		return i;
	}

	@Override
	public List<HivePolicy> listHivePolicy(HivePolicy hivePolicy, int startPage, int count) throws Exception{
		RangerEntity rangerEntity = policyToRanger(hivePolicy,"select");
		//为了支持通过用户名进行模糊查询  "[/"aa/"]"
		String users = rangerEntity.getUser();
		if(StringUtils.isNotBlank(users)){
			users = users.substring(2, users.length()-2);
			users = StrUtils.escapeStr(users);
		}
		rangerEntity.setUser(users);
		List<RangerEntity> rangerEntitys = rangerDaoImpl.listHivePolicy(rangerEntity,startPage,count);
		List<HivePolicy> hivePolicys = new ArrayList<>();
		for(RangerEntity ranger : rangerEntitys){
			HivePolicy policy = rangerToPolicy(ranger);
			hivePolicys.add(policy);
		}
		return hivePolicys;
	}

	@Override
	public int deleteRangerByName(List<HivePolicy> hivePolicys) throws Exception{
		List<RangerEntity> rangerEntitys = new ArrayList<>();
		for(HivePolicy hivePolicy : hivePolicys){
			RangerEntity rangerEntity = policyToRanger(hivePolicy,"delete");
			rangerEntitys.add(rangerEntity);
		}
		int i = rangerDaoImpl.deleteRangerByName(rangerEntitys);
		return i;
	}

	@Override
	public RangerEntity policyToRanger(HivePolicy hivePolicy,String type) throws Exception{
		RangerEntity rangerEntity = new RangerEntity();
		if(hivePolicy.getColumns()!=null && hivePolicy.getColumns().size()>0){
			rangerEntity.setRangerColumns(JsonUtils.toJson(hivePolicy.getColumns()));
		}
		if(hivePolicy.getDatabases()!=null && hivePolicy.getDatabases().size()>0){
			rangerEntity.setRangerDatabases(JsonUtils.toJson(hivePolicy.getDatabases()));
		}
		if(hivePolicy.getName()!=null && !"".equalsIgnoreCase(hivePolicy.getName())){
			rangerEntity.setName(hivePolicy.getName());
		}
		if("insert".equalsIgnoreCase(type) || "update".equalsIgnoreCase(type)){
			rangerEntity.setRole(JsonUtils.toJson(roleDaoImpl.roleSelectByUseName(hivePolicy.getUser())));
		}
		rangerEntity.setServiceName("hdp_hive");
		if(hivePolicy.getTables()!=null && hivePolicy.getTables().size()>0){
			rangerEntity.setRangerTables(JsonUtils.toJson(hivePolicy.getTables()));
		}
		if(hivePolicy.getType()!=null && hivePolicy.getType().size()>0){
			rangerEntity.setType(JsonUtils.toJson(hivePolicy.getType()));
		}
		if(hivePolicy.getUser()!=null && hivePolicy.getUser().size()>0){
			rangerEntity.setUser(JsonUtils.toJson(hivePolicy.getUser()));
		}
		return rangerEntity;
	}

	@Override
	public HivePolicy rangerToPolicy(RangerEntity rangerEntity) throws Exception{
		HivePolicy hivePolicy = new HivePolicy();
		int id = rangerEntity.getId();
		long loId = id;
		hivePolicy.setId(loId);
		hivePolicy.setColumns(JsonUtils.toArray(rangerEntity.getRangerColumns(), String.class));
		hivePolicy.setDatabases(JsonUtils.toArray(rangerEntity.getRangerDatabases(), String.class));
		hivePolicy.setName(rangerEntity.getName());
		hivePolicy.setRole(JsonUtils.toArray(rangerEntity.getRole(), String.class));
		hivePolicy.setServiceName(rangerEntity.getServiceName());
		hivePolicy.setTables(JsonUtils.toArray(rangerEntity.getRangerTables(), String.class));
		hivePolicy.setType(JsonUtils.toArray(rangerEntity.getType(), String.class));
		hivePolicy.setRole(JsonUtils.toArray(rangerEntity.getRole(), String.class));
		hivePolicy.setUser(JsonUtils.toArray(rangerEntity.getUser(), String.class));
		return hivePolicy;
	}

	@Override
	public int getCounts(HivePolicy hivePolicy) throws Exception{
		RangerEntity rangerEntity = policyToRanger(hivePolicy, "select");
		String users = rangerEntity.getUser();
		if(StringUtils.isNotBlank(users)){
			users = users.substring(2, users.length()-2);
			users = StrUtils.escapeStr(users);
		}
		rangerEntity.setUser(users);
		int counts = rangerDaoImpl.getCounts(rangerEntity);
		return counts;
	}


	public int checkUser(RangerEntity rangerEntity) {
		int i = rangerDaoImpl.checkUser(rangerEntity);
		return i;
	}

	@Override
	public RangerEntity getUser(RangerEntity rangerEntity) {
		RangerEntity ranger = rangerDaoImpl.getUser(rangerEntity);
		return ranger;
	}
	@Override
	public HivePolicy getSelectedTables(int id, HttpServletRequest request) throws Exception {
		RangerEntity rangerEntity = null;
		rangerEntity = rangerDaoImpl.getSelectedTables(id);
		HivePolicy policy = rangerToPolicy(rangerEntity);
		return policy;

	}
	
}

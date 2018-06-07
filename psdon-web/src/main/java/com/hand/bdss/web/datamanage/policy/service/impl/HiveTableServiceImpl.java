package com.hand.bdss.web.datamanage.policy.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.hand.bdss.web.common.util.StringUtils;
import org.springframework.stereotype.Service;

import com.hand.bdss.dsmp.component.hive.HiveClient;
import com.hand.bdss.dsmp.model.HiveMetaData;
import com.hand.bdss.dsmp.model.HiveMetaTableField;
import com.hand.bdss.dsmp.model.HivePolicy;
import com.hand.bdss.dsmp.service.privilege.RangerPolicyManage;
import com.hand.bdss.web.common.constant.Global;
import com.hand.bdss.web.datamanage.policy.service.HiveTableService;

/**
 * hive表接口实现
 * @author zk
 */
@Service
public class HiveTableServiceImpl implements HiveTableService {
	
	/**
	 * 查询hive表集合
	 */
	@Override
	public List<String> select(String dbName,String username) throws Exception{
		return getHiveClient(username).getHiveTables(dbName);
	}
	
	/**
	 * 新增hive表
	 */
	@Override
	public Boolean insert(HiveMetaData hmd,String username) throws Exception{
		Boolean flag = false;
		Boolean result = false;
		Boolean isSubarea = false;
		String hname = "";
		for(HiveMetaTableField hmtf : hmd.getMetaTableField()){
			if(hmtf != null && hmtf.getFlag() == 0){
				isSubarea = true;
				break;
			}
		}

		if(StringUtils.isNotBlank(hmd.getLocation())){
			hname = username;
			username = Global.HDFS_MANAGER;
		}
		
		//判断新增分区表还是不分区表
		if(isSubarea){
			result = getHiveClient(username).createHivePartitionTable(hmd);
		}else{
			result = getHiveClient(username).createHiveTable(hmd);
		}
		
		//新增成功后添加用户对表的权限管理
		if(result){
			List<String> userList = new ArrayList<String>();
			List<String> dbList = new ArrayList<String>();
			List<String> tableList = new ArrayList<String>();
			List<String> typeList = new ArrayList<String>();
			HivePolicy policy = new HivePolicy();
			userList.add(username);
			if(StringUtils.isNotBlank(hname)){
				userList.add(hname);
			}
			policy.setUser(userList);
			dbList.add(hmd.getDbName());
			policy.setDatabases(dbList);
			tableList.add(hmd.getTabelName());
			policy.setTables(tableList);
			typeList.add("all");
			typeList.add("select");
			typeList.add("update");
			typeList.add("drop");
			typeList.add("alter");
			typeList.add("index");
			policy.setType(typeList);
			policy.setName(UUID.randomUUID().toString());
			policy.setServiceName(Global.HIVE_SERVER);
			flag = new RangerPolicyManage().createPolicy(policy);
			
		}
		return flag;
	}
	
	/**
	 * 更新hive表
	 */
	@Override
	public Boolean update(HiveMetaData hmd,String username) throws Exception {
		return getHiveClient(username).updateHiveTable(hmd);
	}
	
	/**
	 * 删除hive表
	 */
	@Override
	public Boolean delete(HiveMetaData hmd,String username) throws Exception {
		return getHiveClient(username).dropHiveTable(hmd);
	}
	
	/**
	 * 获取hive表结构信息
	 */
	@Override
	public List<HiveMetaTableField> getHiveMetaTableField(String dbName, String tableName,String username) throws Exception {
		return getHiveClient(username).getHiveMetaTableField(dbName,tableName);
	}
	
	/**
	 * 创建数据库
	 */
	@Override
	public Boolean createHiveDb(String dbName,String username) throws Exception {
		Boolean result = getHiveClient(username).createDatabase(dbName);
		Boolean flag = false;
		
		//新增成功后添加用户对库的权限管理
		if(result){
			List<String> userList = new ArrayList<String>();
			List<String> dbList = new ArrayList<String>();
			List<String> tableList = new ArrayList<String>();
			List<String> typeList = new ArrayList<String>();
			HivePolicy policy = new HivePolicy();
			userList.add(username);
			policy.setUser(userList);
			dbList.add(dbName);
			policy.setDatabases(dbList);
			tableList.add("*");
			policy.setTables(tableList);
			typeList.add("all");
			typeList.add("select");
			typeList.add("update");
			typeList.add("drop");
			typeList.add("alter");
			typeList.add("index");
			policy.setType(typeList);
			policy.setName(UUID.randomUUID().toString());
			policy.setServiceName(Global.HIVE_SERVER);
			flag = new RangerPolicyManage().createPolicy(policy);
		}
		
		return flag;
	}

	@Override
	public List<String> getHiveDb(String username) throws Exception {
		return getHiveClient(username).getHiveDatabases();
	}

	/**
	 * 获取hive表操作类
	 * @return
	 */
	private HiveClient getHiveClient(String username){
		return new HiveClient(username);
	}



}

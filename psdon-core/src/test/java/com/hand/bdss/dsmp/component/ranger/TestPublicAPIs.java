package com.hand.bdss.dsmp.component.ranger;

import java.io.IOException;
import java.util.*;

import org.apache.ranger.plugin.model.RangerPolicy;
import org.apache.ranger.plugin.model.RangerService;
import org.apache.ranger.plugin.model.RangerServiceDef;
import org.apache.ranger.plugin.model.RangerPolicy.RangerPolicyItem;
import org.apache.ranger.plugin.model.RangerPolicy.RangerPolicyItemAccess;
import org.apache.ranger.plugin.model.RangerServiceDef.RangerAccessTypeDef;
import org.apache.ranger.plugin.model.RangerServiceDef.RangerContextEnricherDef;
import org.apache.ranger.plugin.model.RangerServiceDef.RangerEnumDef;
import org.apache.ranger.plugin.model.RangerServiceDef.RangerPolicyConditionDef;
import org.apache.ranger.plugin.model.RangerServiceDef.RangerResourceDef;
import org.apache.ranger.plugin.model.RangerServiceDef.RangerServiceConfigDef;
import org.springframework.beans.factory.annotation.Autowired;

import com.hand.bdss.dsmp.model.HbasePolicy;
import com.hand.bdss.dsmp.model.HivePolicy;
import com.hand.bdss.dsmp.service.privilege.RangerHbasePolicyManage;
import com.hand.bdss.dsmp.service.privilege.RangerPolicyManage;

public class TestPublicAPIs {
	
	public static void main(String[] args) throws IOException {
		
		//RangerHbasePolicyManage rpm = new RangerHbasePolicyManage();
		//System.out.println(rpm.deletePolicy(hbasePolicy())+"------");
		RangerPolicyManage rpm = new RangerPolicyManage();
	//	System.out.println(rpm.createPolicy(hivePolicy())+"------");
		for(int i=0;i< 100;i++){
            rpm.createPolicy(hivePolicy(i));
        }
		//new PublicAPIs().searchPolicies(policy());
	    // RangerService rangerService = new PublicAPIs().createService(rangerService());
//			     RangerServiceDef rangerServiceDef = new PublicAPIs().createServiceDef(rangerServiceDef());

		System.out.println("---dddddddd");
//		new PublicAPIs().updatePolicy(hiveCUPolicy());
//		RangerPolicy policy = new RangerPolicy();
//		policy.setService("hdp_hive");
//		List<RangerPolicy> list = new PublicAPIs().searchPolicies(policy);
//		for(int i = 0;i<list.size();i++){
//			System.out.println(list.get(i).getPolicyItems().get(0).getUsers());
//		}
		
		
//		HivePolicy hivePolicy = new HivePolicy();
//		hivePolicy.setServiceName("hdp_hive");
//		List<HivePolicy> returnHivePolicys = new ArrayList<HivePolicy>();
//		List<RangerPolicy> listRangerPolicy= new PublicAPIs().searchPolicies(getHivePolicy(hivePolicy));
//		System.out.println(listRangerPolicy.size());
//		for( RangerPolicy rangerPolicy :  listRangerPolicy){
//			returnHivePolicys.add(policy2HivePolicy(rangerPolicy));
//		}
//		for(HivePolicy p : returnHivePolicys){
//			System.out.println(p.getUser() + "--------" + p.getDatabases() + "------"+
//					p.getType());
//		}
		//new PublicAPIs().deleteService(rangerService());
	   //new PublicAPIs().deleteServiceDef(rangerServiceDef());
		//System.out.println("---"+rangerServiceDef.toString());
		

	}
	
	private static  HivePolicy policy2HivePolicy(RangerPolicy rangerPolicy) {
		HivePolicy hivePolicy = new HivePolicy();
		
		hivePolicy.setServiceName(rangerPolicy.getService());
		hivePolicy.setName(rangerPolicy.getName());
		
		List<String> typeList = new ArrayList<String>();
		for(RangerPolicyItem policyItems : rangerPolicy.getPolicyItems()) {
			hivePolicy.setUser(policyItems.getUsers()); //获得用户列表
			
			for( RangerPolicyItemAccess rpia :policyItems.getAccesses()){
				typeList.add(rpia.getType());
			}
			hivePolicy.setType(typeList);
		}

		List<String> tables = new ArrayList<String>(); 
				if(rangerPolicy.getResources().get("table") != null) tables = rangerPolicy.getResources().get("table").getValues();//获得表列表
		List<String> databases = new ArrayList<String>();
				if(rangerPolicy.getResources().get("database") != null) databases = rangerPolicy.getResources().get("database").getValues();//获得库列表
		List<String> columns = new ArrayList<String>();
				if(rangerPolicy.getResources().get("column") != null) columns = rangerPolicy.getResources().get("column").getValues();//获得列列表
		hivePolicy.setTables(tables);
		hivePolicy.setDatabases(databases);
		hivePolicy.setColumns(columns);
		return hivePolicy;
	}
	
	/**
	 * 获得hive策略
	 * @return
	 */
	private static  RangerPolicy getHivePolicy(HivePolicy hivePolicy){
		RangerPolicy rangerPolicy = new RangerPolicy();
        rangerPolicy.setService(hivePolicy.getServiceName());//hdp_dev_hive
        rangerPolicy.setName(hivePolicy.getName());
        rangerPolicy.setIsAuditEnabled(true);
        rangerPolicy.setId(hivePolicy.getId());
        
        Map<String, RangerPolicy.RangerPolicyResource> resources = new HashMap<>();

        RangerPolicy.RangerPolicyResource databasePolicyResource = new RangerPolicy.RangerPolicyResource();
        databasePolicyResource.setIsExcludes(false);
        databasePolicyResource.setIsRecursive(false);
        databasePolicyResource.setValues(hivePolicy.getDatabases()); //对应者库的值

        //表列表加入
    	RangerPolicy.RangerPolicyResource tablePolicyResource = new RangerPolicy.RangerPolicyResource();
    	tablePolicyResource.setIsExcludes(false);
    	tablePolicyResource.setIsRecursive(false);
    	tablePolicyResource.setValues(hivePolicy.getTables()); //对应表的值
    	
    	RangerPolicy.RangerPolicyResource columnPolicyResource = new RangerPolicy.RangerPolicyResource();
    	columnPolicyResource.setIsExcludes(false);
    	columnPolicyResource.setIsRecursive(false);
    	columnPolicyResource.setValue("*"); //对应列的值
    	
    	resources.put("table", tablePolicyResource);	
        resources.put("database", databasePolicyResource);
        resources.put("column", columnPolicyResource);

        List<RangerPolicy.RangerPolicyItem> policyItems = new ArrayList<>();

        RangerPolicy.RangerPolicyItem rangerPolicyItem = new RangerPolicy.RangerPolicyItem();
        rangerPolicyItem.setUsers(hivePolicy.getUser());

        List<RangerPolicy.RangerPolicyItemAccess> rangerPolicyItemAccesses = new ArrayList<>();
        
      //操作权限列表加入
        if(hivePolicy.getType() != null){
        	
        	for(String type: hivePolicy.getType()){
        		RangerPolicy.RangerPolicyItemAccess rangerPolicyItemAccess = new RangerPolicy.RangerPolicyItemAccess();
        		rangerPolicyItemAccess.setType(type);
        		rangerPolicyItemAccess.setIsAllowed(Boolean.TRUE);
        		rangerPolicyItemAccesses.add(rangerPolicyItemAccess);
        	}
        }

        rangerPolicyItem.setAccesses(rangerPolicyItemAccesses);

        policyItems.add(rangerPolicyItem);

        rangerPolicy.setPolicyItems(policyItems);
        rangerPolicy.setResources(resources);
        
        return rangerPolicy;
	}

	private static HbasePolicy hbasePolicy() {
		HbasePolicy hbasePolicy = new HbasePolicy();
		//hivePolicy.setId(36L);
		hbasePolicy.setServiceName("hdp_dev_hbase");//hdp_dev_hive  hive_dev_hdp_self
		hbasePolicy.setName("is_Weblog");
		
		List<String> typeList = new ArrayList<String>();
		typeList.add("write");
		typeList.add("read");
		hbasePolicy.setType(typeList);
        
		List<String> userList = new ArrayList<String>();
		userList.add("is");
		userList.add("tc");
		List<String> tableList = new ArrayList<String>();
		tableList.add("tc_test");
		//tableList.add("weblog");
		List<String> columnFamilyList = new ArrayList<String>();
		columnFamilyList.add("default");
		List<String> columnList = new ArrayList<String>();
		columnList.add("*");
		
		hbasePolicy.setUser(userList);
		hbasePolicy.setTables(tableList);
		hbasePolicy.setColumnFamily(columnFamilyList);
		hbasePolicy.setColumns(columnList);
				
        return hbasePolicy;
    }

	private static HivePolicy hivePolicy(int i) {
		HivePolicy hivePolicy = new HivePolicy();
		//hivePolicy.setId(36L);
		hivePolicy.setServiceName("hdp_dev_hive");//hdp_dev_hive  hive_dev_hdp_self
		hivePolicy.setName(UUID.randomUUID().toString());
		
		List<String> typeList = new ArrayList<String>();
		typeList.add("SELECT");
		typeList.add("update");
		typeList.add("select");
		typeList.add("all");
		typeList.add("alter");
		typeList.add("index");
		typeList.add("drop");
        typeList.add("create");

		hivePolicy.setType(typeList);
        
		List<String> userList = new ArrayList<String>();
		//userList.add("is");
		userList.add("admin");
		List<String> tableList = new ArrayList<String>();
		tableList.add("tc_test" + i);
		//tableList.add("weblog");
		List<String> databaseList = new ArrayList<String>();
		databaseList.add("default"+4);
		List<String> columnList = new ArrayList<String>();
		columnList.add("*");
		
		hivePolicy.setUser(userList);
		hivePolicy.setTables(tableList);
		hivePolicy.setDatabases(databaseList);
		hivePolicy.setColumns(columnList);
				
        return hivePolicy;
    }
	
	private static RangerServiceDef rangerServiceDef() {
		List<RangerServiceConfigDef> configs = new ArrayList<RangerServiceConfigDef>();
		List<RangerResourceDef> resources = new ArrayList<RangerResourceDef>();
		List<RangerAccessTypeDef> accessTypes = new ArrayList<RangerAccessTypeDef>();
		List<RangerPolicyConditionDef> policyConditions = new ArrayList<RangerPolicyConditionDef>();
		List<RangerContextEnricherDef> contextEnrichers = new ArrayList<RangerContextEnricherDef>();
		List<RangerEnumDef> enums = new ArrayList<RangerEnumDef>();

		RangerResourceDef rangerResourceDef = new RangerResourceDef();
		
		rangerResourceDef.setName("ttt_____ccc");
		rangerResourceDef.setItemId(123L);
		rangerResourceDef.setType("String");
		resources.add(rangerResourceDef);
		
		RangerAccessTypeDef rangerAccessTypeDef = new RangerAccessTypeDef();
		rangerAccessTypeDef.setName("222");
		rangerAccessTypeDef.setItemId(11L);
		rangerAccessTypeDef.setLabel("222");
		rangerAccessTypeDef.setRbKeyLabel("111");
		accessTypes.add(rangerAccessTypeDef);
		
		RangerServiceDef rangerServiceDef = new RangerServiceDef();
		//rangerServiceDef.setId(Id);
		rangerServiceDef.setName("tan_cheng");
		rangerServiceDef.setImplClass("RangerServiceHdfs");
		rangerServiceDef.setLabel("tan_cheng");
		rangerServiceDef.setDescription("tancheng Repository");
		rangerServiceDef.setRbKeyDescription(null);
		rangerServiceDef.setUpdatedBy("Admin");
		//rangerServiceDef.setUpdateTime(new Date());
		rangerServiceDef.setConfigs(configs);
		rangerServiceDef.setResources(resources);
		rangerServiceDef.setAccessTypes(accessTypes);
		rangerServiceDef.setPolicyConditions(policyConditions);
		rangerServiceDef.setContextEnrichers(contextEnrichers);
		rangerServiceDef.setEnums(enums);

		return rangerServiceDef;
	}
	
	private static RangerService rangerService() {
		Map<String, String> configs = new HashMap<String, String>();
		//HDFS
//		configs.put("username", "hadoop");
//		configs.put("password", "hadoop");
//		configs.put("fs.default.name", "hdfs://hadoop003.edcs.org:8020");
//		configs.put("hadoop.security.authorization", "No");
//		configs.put("hadoop.security.authentication", "Simple");
//		configs.put("hadoop.security.auth_to_local", "");
//		configs.put("dfs.datanode.kerberos.principal", "");
//		configs.put("dfs.namenode.kerberos.principal", "");
//		configs.put("dfs.secondary.namenode.kerberos.principal", "");
//		configs.put("hadoop.rpc.protection", "Privacy");
//		configs.put("commonNameForCertificate", "");

		//hive
		configs.put("username", "hadoop");
		configs.put("password", "hadoop");
		configs.put("jdbc.url", "jdbc:hive2://hadoop003.edcs.org:10000");
		configs.put("jdbc.driverClassName", "org.apache.hive.jdbc.HiveDriver");
		
		RangerService rangerService = new RangerService();
		rangerService.setId(20L);
		rangerService.setConfigs(configs);
		//rangerService.setCreateTime(new Date());
		rangerService.setDescription("service service11");
		rangerService.setGuid("1427365526516_835_011");
		rangerService.setIsEnabled(true);
		rangerService.setName("TAG_dev_hdp_self");
		//rangerService.setPolicyUpdateTime(new Date());
		rangerService.setType("TAG");  //决定了要建的服务类型 HDFS  HBASE HIVE
		rangerService.setUpdatedBy("Admin");
		//rangerService.setUpdateTime(new Date());

		return rangerService;
	}
	
	private static RangerPolicy policy() {
        RangerPolicy rangerPolicy = new RangerPolicy();
        rangerPolicy.setService("hdp_dev_hive");//hdp_dev_hive
        rangerPolicy.setName("bc941ba2-bd08-4f0c-8f8e-1dc9ad5f8f04lisi07");
        rangerPolicy.setIsAuditEnabled(true);
        
//        rangerPolicy.setPolicyType(0);
//        rangerPolicy.setVersion(1L);
//        rangerPolicy.setId(20L);
//        
        Map<String, RangerPolicy.RangerPolicyResource> resources = new HashMap<>();

        RangerPolicy.RangerPolicyResource rangerPolicyResource = new RangerPolicy.RangerPolicyResource();
        rangerPolicyResource.setIsExcludes(false);
        rangerPolicyResource.setIsRecursive(false);
        rangerPolicyResource.setValue("hbasetest");

//        resources.put("database", rangerPolicyResource);
//        resources.put("table", rangerPolicyResource);
//        resources.put("column", rangerPolicyResource);
        resources.put("table", rangerPolicyResource);
        resources.put("column-family", rangerPolicyResource);
        resources.put("column", rangerPolicyResource);

        List<RangerPolicy.RangerPolicyItem> policyItems = new ArrayList<>();

        RangerPolicy.RangerPolicyItem rangerPolicyItem = new RangerPolicy.RangerPolicyItem();
        List<String> users = new ArrayList<>();
        users.add("is");
        users.add("tc");
        rangerPolicyItem.setUsers(users);

        List<RangerPolicy.RangerPolicyItemAccess> rangerPolicyItemAccesses = new ArrayList<>();
        RangerPolicy.RangerPolicyItemAccess rangerPolicyItemAccess = new RangerPolicy.RangerPolicyItemAccess();
        rangerPolicyItemAccess.setType("read");
        rangerPolicyItemAccess.setIsAllowed(Boolean.TRUE);
        rangerPolicyItemAccesses.add(rangerPolicyItemAccess);
        RangerPolicy.RangerPolicyItemAccess rangerPolicyItemAccess2 = new RangerPolicy.RangerPolicyItemAccess();
        rangerPolicyItemAccess2.setType("write");
        rangerPolicyItemAccess2.setIsAllowed(Boolean.TRUE);
        rangerPolicyItemAccesses.add(rangerPolicyItemAccess2);

        
        rangerPolicyItem.setAccesses(rangerPolicyItemAccesses);

        policyItems.add(rangerPolicyItem);

        rangerPolicy.setPolicyItems(policyItems);
        rangerPolicy.setResources(resources);
        
        return rangerPolicy;
    }
	
	private static RangerPolicy hdfsPolicy() {
        RangerPolicy rangerPolicy = new RangerPolicy();
        rangerPolicy.setService("hdp_dev_hive");//hdp_dev_hive
        rangerPolicy.setName("bc941ba2-bd08-4f0c-8f8e-1dc9ad5f8f04lisi07");
        rangerPolicy.setIsAuditEnabled(true);
        
        rangerPolicy.setPolicyType(0);
        rangerPolicy.setVersion(1L);
        //rangerPolicy.setId(13L);
        
        Map<String, RangerPolicy.RangerPolicyResource> resources = new HashMap<>();

        RangerPolicy.RangerPolicyResource rangerPolicyResource = new RangerPolicy.RangerPolicyResource();
        rangerPolicyResource.setIsExcludes(false);
        rangerPolicyResource.setIsRecursive(false);
        rangerPolicyResource.setValue("*");

      resources.put("database", rangerPolicyResource);
      resources.put("table", rangerPolicyResource);
      resources.put("column", rangerPolicyResource);
        //resources.put("path", rangerPolicyResource);
        //resources.put("table", rangerPolicyResource);
        //resources.put("path", rangerPolicyResource);

        List<RangerPolicy.RangerPolicyItem> policyItems = new ArrayList<>();

        RangerPolicy.RangerPolicyItem rangerPolicyItem = new RangerPolicy.RangerPolicyItem();
        List<String> users = new ArrayList<>();
        users.add("is");
        rangerPolicyItem.setUsers(users);

        List<RangerPolicy.RangerPolicyItemAccess> rangerPolicyItemAccesses = new ArrayList<>();
        RangerPolicy.RangerPolicyItemAccess rangerPolicyItemAccess = new RangerPolicy.RangerPolicyItemAccess();
        rangerPolicyItemAccess.setType("select");
        rangerPolicyItemAccess.setIsAllowed(Boolean.TRUE);
        rangerPolicyItemAccesses.add(rangerPolicyItemAccess);

        rangerPolicyItem.setAccesses(rangerPolicyItemAccesses);

        policyItems.add(rangerPolicyItem);

        rangerPolicy.setPolicyItems(policyItems);
        rangerPolicy.setResources(resources);
        
        return rangerPolicy;
    }
	
	private static RangerPolicy hiveCUPolicy() {
        RangerPolicy rangerPolicy = new RangerPolicy();
        rangerPolicy.setService("hdp_dev_hive");//hdp_dev_hive
        rangerPolicy.setName("bc941ba2-bd08-4f0c-8f8e-1dc9ad5f8f04lisi07");
        rangerPolicy.setIsAuditEnabled(true);
        
        rangerPolicy.setPolicyType(0);
        rangerPolicy.setVersion(1L);
        //rangerPolicy.setId(13L);
        
        Map<String, RangerPolicy.RangerPolicyResource> resources = new HashMap<>();

        RangerPolicy.RangerPolicyResource rangerPolicyResource = new RangerPolicy.RangerPolicyResource();
        rangerPolicyResource.setIsExcludes(false);
        rangerPolicyResource.setIsRecursive(false);
        rangerPolicyResource.setValue("*");

      resources.put("database", rangerPolicyResource);
      resources.put("table", rangerPolicyResource);
      resources.put("column", rangerPolicyResource);
        //resources.put("path", rangerPolicyResource);
        //resources.put("table", rangerPolicyResource);
        //resources.put("path", rangerPolicyResource);

        List<RangerPolicy.RangerPolicyItem> policyItems = new ArrayList<>();

        RangerPolicy.RangerPolicyItem rangerPolicyItem = new RangerPolicy.RangerPolicyItem();
        List<String> users = new ArrayList<>();
        users.add("is");
        rangerPolicyItem.setUsers(users);

        List<RangerPolicy.RangerPolicyItemAccess> rangerPolicyItemAccesses = new ArrayList<>();
        RangerPolicy.RangerPolicyItemAccess rangerPolicyItemAccess = new RangerPolicy.RangerPolicyItemAccess();
        rangerPolicyItemAccess.setType("select");
        rangerPolicyItemAccess.setIsAllowed(Boolean.TRUE);
        rangerPolicyItemAccesses.add(rangerPolicyItemAccess);

        rangerPolicyItem.setAccesses(rangerPolicyItemAccesses);

        policyItems.add(rangerPolicyItem);

        rangerPolicy.setPolicyItems(policyItems);
        rangerPolicy.setResources(resources);
        
        return rangerPolicy;
    }

	
}

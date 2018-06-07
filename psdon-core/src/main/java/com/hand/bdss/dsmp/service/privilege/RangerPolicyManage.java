package com.hand.bdss.dsmp.service.privilege;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ranger.plugin.model.RangerPolicy;
import org.apache.ranger.plugin.model.RangerPolicy.RangerPolicyItem;
import org.apache.ranger.plugin.model.RangerPolicy.RangerPolicyItemAccess;

import com.hand.bdss.dsmp.component.ranger.PublicAPIs;
import com.hand.bdss.dsmp.model.HivePolicy;
/**
 * ranger策略管理，提供策略的CURD操作
 * @author tc
 *
 */
public class RangerPolicyManage {

//	private static final Logger logger = LoggerFactory.getLogger(RangerPolicyManage.class);

	//统一用spring管理
	PublicAPIs publicAPIs = new PublicAPIs();
	
	/**
	 * 创建策略
	 * @param rangerPolicy
	 * @return
	 */
	public Boolean createPolicy(HivePolicy hivePolicy) throws IOException{
		RangerPolicy returnRangerPolicy = null;
		boolean bool = false;
		
		returnRangerPolicy = publicAPIs.createPolicy(getHivePolicy(hivePolicy));
		if(returnRangerPolicy != null){
			bool = true;
		}
		return bool;
	}
	
	/**
	 * 根据Id删除策略信息
	 * @param rangerPolicy
	 * @return
	 */
	public Boolean deletePolicy(HivePolicy hivePolicy) throws IOException{
		int status = 0;
		boolean bool = false;
		
		status = publicAPIs.deletePolicy(getHivePolicy(hivePolicy));
		if(status == 200){
			bool = true;
		}
		return bool;
	}
	
	/**
	 * 根据Id修改策略信息
	 * @param rangerPolicy
	 * @return
	 */
	public Boolean updatePolicy(HivePolicy hivePolicy) throws IOException{
		RangerPolicy returnRangerPolicy = null;
		boolean bool = false;
		
		returnRangerPolicy = publicAPIs.updatePolicy(getHivePolicy(hivePolicy));
		if(returnRangerPolicy != null){
			bool = true;
		}
		return bool;
	}
	
	/**
	 * 根据Id获取单个策略信息
	 * @param rangerPolicy
	 * @return
	 */
	public HivePolicy getPolicy(HivePolicy hivePolicy) throws IOException{
		HivePolicy returnHivePolicy = null;
		RangerPolicy rp = publicAPIs.getPolicy(getHivePolicy(hivePolicy));
		returnHivePolicy = policy2HivePolicy(rp);
		return returnHivePolicy;
	}
	
	/**
	 * 根据服务名获取服务的全部策略
	 * @param rangerPolicy
	 * @return
	 */
	public List<HivePolicy> searchPolicies(HivePolicy hivePolicy) throws IOException{
		List<HivePolicy> returnHivePolicys = new ArrayList<HivePolicy>();
		List<RangerPolicy> listRangerPolicy= publicAPIs.searchPolicies(getHivePolicy(hivePolicy));
//		System.out.println(listRangerPolicy.size());
		if(listRangerPolicy != null && listRangerPolicy.size() > 0){
			for( RangerPolicy rangerPolicy : listRangerPolicy){
				returnHivePolicys.add(policy2HivePolicy(rangerPolicy));
			}
		}
		return returnHivePolicys;
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
        
        if(hivePolicy.getType() != null){
        	//操作权限列表加入
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
	
	private static HivePolicy policy2HivePolicy(RangerPolicy rangerPolicy) {
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
}

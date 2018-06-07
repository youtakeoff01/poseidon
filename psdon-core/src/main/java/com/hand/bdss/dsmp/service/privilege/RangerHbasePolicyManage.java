package com.hand.bdss.dsmp.service.privilege;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ranger.plugin.model.RangerPolicy;
import org.apache.ranger.plugin.model.RangerPolicy.RangerPolicyItem;
import org.apache.ranger.plugin.model.RangerPolicy.RangerPolicyItemAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hand.bdss.dsmp.component.ranger.PublicAPIs;
import com.hand.bdss.dsmp.model.HbasePolicy;
/**
 * ranger hbase策略管理，提供策略的CURD操作
 * @author tc
 *
 */
public class RangerHbasePolicyManage {

	private static final Logger logger = LoggerFactory.getLogger(RangerHbasePolicyManage.class);

	//统一用spring管理
	PublicAPIs publicAPIs = new PublicAPIs();
	
	/**
	 * 创建策略
	 * @param rangerPolicy
	 * @return
	 */
	public Boolean createPolicy(HbasePolicy HbasePolicy) throws IOException{
		RangerPolicy returnRangerPolicy = null;
		boolean bool = false;
		
		returnRangerPolicy = publicAPIs.createPolicy(getHbasePolicy(HbasePolicy));
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
	public Boolean deletePolicy(HbasePolicy HbasePolicy) throws IOException{
		int status = 0;
		boolean bool = false;
		
		status = publicAPIs.deletePolicy(getHbasePolicy(HbasePolicy));
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
	public Boolean updatePolicy(HbasePolicy HbasePolicy) throws IOException{
		RangerPolicy returnRangerPolicy = null;
		boolean bool = false;
		
		returnRangerPolicy = publicAPIs.updatePolicy(getHbasePolicy(HbasePolicy));
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
	public HbasePolicy getPolicy(HbasePolicy HbasePolicy) throws IOException{
		HbasePolicy returnHbasePolicy = null;
		RangerPolicy rp = publicAPIs.getPolicy(getHbasePolicy(HbasePolicy));
		returnHbasePolicy = policy2HbasePolicy(rp);
		return returnHbasePolicy;
	}
	
	/**
	 * 根据服务名获取服务的全部策略
	 * @param rangerPolicy
	 * @return
	 */
	public List<HbasePolicy> searchPolicies(HbasePolicy HbasePolicy) throws IOException{
		List<HbasePolicy> returnHbasePolicys = new ArrayList<HbasePolicy>();
		List<RangerPolicy> listRangerPolicy= publicAPIs.searchPolicies(getHbasePolicy(HbasePolicy));
		System.out.println(listRangerPolicy.size());
		for( RangerPolicy rangerPolicy :  listRangerPolicy){
			returnHbasePolicys.add(policy2HbasePolicy(rangerPolicy));
		}
		return returnHbasePolicys;
	}
	
	/**
	 * 获得hive策略
	 * @return
	 */
	private static RangerPolicy getHbasePolicy(HbasePolicy hbasePolicy){
		RangerPolicy rangerPolicy = new RangerPolicy();
        rangerPolicy.setService(hbasePolicy.getServiceName());//hdp_dev_hive
        rangerPolicy.setName(hbasePolicy.getName());
        rangerPolicy.setIsAuditEnabled(true);
        rangerPolicy.setId(hbasePolicy.getId());
        
        Map<String, RangerPolicy.RangerPolicyResource> resources = new HashMap<>();

        RangerPolicy.RangerPolicyResource columnFamilyPolicyResource = new RangerPolicy.RangerPolicyResource();
        columnFamilyPolicyResource.setIsExcludes(false);
        columnFamilyPolicyResource.setIsRecursive(false);
        columnFamilyPolicyResource.setValues(hbasePolicy.getColumnFamily()); //对应者库的值

        //表列表加入
    	RangerPolicy.RangerPolicyResource tablePolicyResource = new RangerPolicy.RangerPolicyResource();
    	tablePolicyResource.setIsExcludes(false);
    	tablePolicyResource.setIsRecursive(false);
    	tablePolicyResource.setValues(hbasePolicy.getTables()); //对应表的值
    	
    	RangerPolicy.RangerPolicyResource columnPolicyResource = new RangerPolicy.RangerPolicyResource();
    	columnPolicyResource.setIsExcludes(false);
    	columnPolicyResource.setIsRecursive(false);
    	columnPolicyResource.setValue("*"); //对应列的值
    	
    	resources.put("table", tablePolicyResource);	
        resources.put("column-family", columnFamilyPolicyResource);
        resources.put("column", columnPolicyResource);

        List<RangerPolicy.RangerPolicyItem> policyItems = new ArrayList<>();

        RangerPolicy.RangerPolicyItem rangerPolicyItem = new RangerPolicy.RangerPolicyItem();
        rangerPolicyItem.setUsers(hbasePolicy.getUser());

        List<RangerPolicy.RangerPolicyItemAccess> rangerPolicyItemAccesses = new ArrayList<>();
        
      //操作权限列表加入
		for(String type: hbasePolicy.getType()){
			 RangerPolicy.RangerPolicyItemAccess rangerPolicyItemAccess = new RangerPolicy.RangerPolicyItemAccess();
		     rangerPolicyItemAccess.setType(type);
		     rangerPolicyItemAccess.setIsAllowed(Boolean.TRUE);
		     rangerPolicyItemAccesses.add(rangerPolicyItemAccess);
        }

        rangerPolicyItem.setAccesses(rangerPolicyItemAccesses);

        policyItems.add(rangerPolicyItem);

        rangerPolicy.setPolicyItems(policyItems);
        rangerPolicy.setResources(resources);
        
        return rangerPolicy;
	}
	
	private static HbasePolicy policy2HbasePolicy(RangerPolicy rangerPolicy) {
		HbasePolicy hbasePolicy = new HbasePolicy();
		
		hbasePolicy.setServiceName(rangerPolicy.getService());
		hbasePolicy.setName(rangerPolicy.getName());
		
		List<String> typeList = new ArrayList<String>();
		for(RangerPolicyItem policyItems : rangerPolicy.getPolicyItems()) {
			hbasePolicy.setUser(policyItems.getUsers()); //获得用户列表
			
			for( RangerPolicyItemAccess rpia :policyItems.getAccesses()){
				typeList.add(rpia.getType());
			}
			hbasePolicy.setType(typeList);
		}

		List<String> tables = rangerPolicy.getResources().get("table").getValues();//获得表列表
		List<String> columnFamilys = rangerPolicy.getResources().get("column-family").getValues();//获得表列表
		List<String> columns = rangerPolicy.getResources().get("column").getValues();//获得表列表
		hbasePolicy.setTables(tables);
		hbasePolicy.setColumnFamily(columnFamilys);
		hbasePolicy.setColumns(columns);
		return hbasePolicy;
	}
}

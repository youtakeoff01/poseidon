package com.hand.bdss.dsmp.service.privilege;

import java.io.IOException;
import java.util.List;

import org.apache.ranger.plugin.model.RangerService;

import com.hand.bdss.dsmp.component.ranger.PublicAPIs;

/**
 * ranger服务管理，提供服务的CURD操作
 * @author hand
 *
 */
public class RangerServiceManage {

	PublicAPIs publicAPIs = new PublicAPIs();
	/**
	 * 创建服务
	 * @param RangerService
	 * @return
	 */
	public Boolean createService(RangerService rangerService) throws IOException{
		RangerService returnRangerService = null;
		boolean bool = false;
		
		returnRangerService = publicAPIs.createService(rangerService);
		if(returnRangerService != null){
			bool = true;
		}
		return bool;
	}
	
	/**
	 * 根据Id删除服务信息
	 * @param RangerService
	 * @return
	 */
	public Boolean deleteService(RangerService rangerService) throws IOException{
		int status = 0;
		boolean bool = false;
		
		status = publicAPIs.deleteService(rangerService);
		if(status == 200){
			bool = true;
		}
		return bool;
	}
	
	/**
	 * 根据Id修改服务信息
	 * @param RangerService
	 * @return
	 */
	public Boolean updateService(RangerService rangerService) throws IOException{
		RangerService returnRangerService = null;
		boolean bool = false;
		
		returnRangerService = publicAPIs.updateService(rangerService);
		if(returnRangerService != null){
			bool = true;
		}
		return bool;
	}
	
	/**
	 * 根据Id获取单个服务信息
	 * @param RangerService
	 * @return
	 */
	public RangerService getService(RangerService rangerService) throws IOException{
		RangerService returnRangerService = null;
		returnRangerService = publicAPIs.getService(rangerService);
		return returnRangerService;
	}
	
	/**
	 * 获取多个服务
	 * @param RangerService
	 * @return
	 */
	public List<RangerService> searchPolicies(RangerService rangerService) throws IOException{
		List<RangerService> returnRangerServices = null;
		returnRangerServices = publicAPIs.searchServices(rangerService);
		return returnRangerServices;
	}
}

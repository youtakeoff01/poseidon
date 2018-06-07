package com.hand.bdss.dsmp.service.privilege;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hand.bdss.dsmp.component.ranger.UserAPIs;
import com.hand.bdss.dsmp.model.RangerUser;

public class RangerUserService {

private static final Logger logger = LoggerFactory.getLogger(RangerUserService.class);

	
	UserAPIs userAPIs = new UserAPIs();
	
	/**
	 * 创建用户
	 * @param VXUser
	 * @return
	 */
	public Boolean createUser(RangerUser VXUser) throws IOException{
		RangerUser returnVXUser = null;
		boolean bool = false;
		
		returnVXUser = userAPIs.createUser(VXUser);
		if(returnVXUser != null){
			bool = true;
		}else{
			throw new IOException("创建用户失败！");
		}
		return bool;
	}
	
	/**
	 * 根据Id删除用户信息
	 * @param VXUser
	 * @return
	 */
	public boolean deleteUser(Map<String, Object> map) throws IOException{
		return userAPIs.deleteUser(map);
	}
	
	/**
	 * 根据Id修改用户信息
	 * @param VXUser
	 * @return
	 */
	public Boolean updateUser(RangerUser VXUser) throws IOException{
		RangerUser returnVXUser = null;
		boolean bool = false;
		
		returnVXUser = userAPIs.updateUser(VXUser);
		if(returnVXUser != null){
			bool = true;
		}else{
			throw new IOException("修改用户失败！");
		}
		return bool;
	}
	
	/**
	 * 根据Id获取单个用户信息
	 * @param VXUser
	 * @return
	 */
	public RangerUser getUser(RangerUser VXUser) throws IOException{
		RangerUser returnVXUser = null;
		returnVXUser = userAPIs.getUser(VXUser);
		return returnVXUser;
	}
	
	/**
	 * 根据服务名获取服务的全部用户
	 * @param RangerUser
	 * @return jsonString
	 */
	public String searchUsers() throws IOException{
		String returnString = null;
		returnString = userAPIs.searchUsers();
		return returnString;
	}
}

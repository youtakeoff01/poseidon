package com.hand.bdss.web.platform.user.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.hand.bdss.dsmp.model.RangerUser;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.MD5Utils;
import com.hand.bdss.web.common.util.StringUtils;
import com.hand.bdss.web.datamanage.policy.service.RangerService;
import com.hand.bdss.web.entity.UserEntity;
import com.hand.bdss.web.platform.user.dao.UserDao;
import com.hand.bdss.web.platform.user.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Resource
	private UserDao userDaoImpl;

	@Resource
	private RangerService rangerServiceImpl;

	@Override
	public List<UserEntity> listUsers(UserEntity user, int startPage, int count) throws Exception {
		List<UserEntity> users = userDaoImpl.listUsers(user, startPage, count);
		return users;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public int insertUser(UserEntity user, HttpServletRequest request) throws Exception {
		// String password = user.getPassword();
		int i = userDaoImpl.insertUser(user);
		int id = userDaoImpl.getUserIdByName(user);

		Map<String, String> map = new HashMap<String, String>();
		map.put("userId", id + "");
		map.put("roleId", user.getRoleId() + "");
		map.put("createCount", GetUserUtils.getUser(request).getUserName());
		map.put("updateCount", GetUserUtils.getUser(request).getUserName());
		userDaoImpl.insertUserRole(map);
		// 调用ranger接口，将用户信息同步给ranger系统中
		/*
		 * user.setPassword(password); RangerUser vxuser = getRangerUser(user); boolean
		 * boo = new RangerUserService().createUser(vxuser); if(!boo){ throw new
		 * Exception("add ranger user error!"); }
		 */
		// 调用ldap接口,将用户信息同步给ldap系统
		/*
		 * if(null != user.getUserType() &&
		 * "ldap".equalsIgnoreCase(user.getUserType())){ if(null ==
		 * LdapUtil.getUserDN(LdapUtil.initCtx(), user.getUserName())){ boolean flag =
		 * LdapUtil.add(user.getUserName(), password); if(!flag){ throw new
		 * Exception("add ldap user error!"); } } }
		 */
		return i;
	}

	@Override
	public int checkUser(UserEntity user) throws Exception {
		int id = userDaoImpl.checkUser(user);
		return id;
	}

	@Override
	public int updateUser(UserEntity user) throws Exception {
		if (StringUtils.isNotEmpty(user.getPassword())) {
			user.setPassword(MD5Utils.md5Password(user.getPassword()));
		}
		int i = userDaoImpl.updateUser(user);
		userDaoImpl.updateUserRole(user);
		// 调用ranger接口，将用户信息同步给ranger系统中
		// RangerUser vxuser = getRangerUser(user);
		// new RangerUserService().updateUser(vxuser);
		return i;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteUser(List<UserEntity> user) throws Exception {
		// Map<String, Object> paramMap= getRangerUserParam(user);

		// 删除本地用户记录
		int i = userDaoImpl.deleteUser(user);
		// 删除和角色表的关联关系
		List<Integer> ids = Lists.newArrayList();
		for (UserEntity userEntity : user) {
			ids.add(userEntity.getId());
		}
		int j = userDaoImpl.deleteUserRole(ids);
		if (i > 0 && j > 0) {
			return i;
		} else {
			return 0;
		}
		// 删除本地数据权限记录
		/*
		 * List<HivePolicy> rangerList = new ArrayList<HivePolicy>();
		 * List<Map<String,String>> userList = (List<Map<String, String>>)
		 * paramMap.get("vXStrings"); for(Map<String,String> map : userList){
		 * RangerEntity ranger = new RangerEntity(); ranger.setUser(map.get("value"));
		 * RangerEntity exsitRanger = rangerServiceImpl.getUser(ranger);
		 * 
		 * HivePolicy hivePolicy = new HivePolicy();
		 * hivePolicy.setName(exsitRanger.getName()); rangerList.add(hivePolicy); }
		 * rangerServiceImpl.deleteRangerByName(rangerList);
		 */
		// 调用ranger接口，删除ranger用户
		/*
		 * boolean boo = new RangerUserService().deleteUser(paramMap); if(!boo){ throw
		 * new Exception("delete ranger user error"); }
		 */
		// 调用ldap接口，删除ldap用户
		/*
		 * for(Map<String,String> map : userList){ String userName = map.get("value");
		 * UserEntity ldapUser = new UserEntity(); ldapUser.setUserName(userName);
		 * ldapUser = userDaoImpl.getUser(ldapUser); if(null != ldapUser.getUserType()
		 * && "ldap".equalsIgnoreCase(ldapUser.getUserType())){ boolean flag =
		 * LdapUtil.delete(ldapUser.getUserName()); if(!flag){ throw new
		 * Exception("delete ldap user error"); } } }
		 */
	}

	/**
	 * 封装删除ranger参数
	 * 
	 * @param user
	 * @return
	 */
	public Map<String, Object> getRangerUserParam(List<UserEntity> user) throws Exception {

		Map<String, Object> map = new HashMap<>();
		List<Map<String, String>> list = new ArrayList<>();

		if (user != null && user.size() > 0) {
			for (UserEntity userEntity : user) {

				Map<String, String> paramMap = new HashMap<>();
				String userName = userEntity.getUserName();

				if (StringUtils.isBlank(userName)) {
					userName = userDaoImpl.getUserNameById(userEntity.getId());
				}
				paramMap.put("value", userName);
				list.add(paramMap);
			}
		}
		map.put("vXStrings", list);
		return map;
	}

	@Override
	public List<String> getMenuByUser(String userName) throws Exception {
		return userDaoImpl.getMenuByUser(userName);
	}

	@Override
	public int getCountAll(UserEntity user) throws Exception {
		int count = userDaoImpl.getCountAll(user);
		return count;
	}

	/**
	 * 将web端的用户装换为ranger中的用户
	 * 
	 * @param user
	 * @return
	 */
	public RangerUser getRangerUser(UserEntity user) throws Exception {
		if (user == null) {
			return new RangerUser();
		}
		RangerUser vxuser = new RangerUser();
		vxuser.setId(Long.parseLong(user.getId() + ""));
		vxuser.setName(user.getUserName());
		vxuser.setPassword(user.getPassword());
		// vxuser.setEmailAddress("");
		// List<Long> groupIdList = new ArrayList<Long>();
		// int a = user.getGroupId();
		// long b = a ;
		// groupIdList.add(b);
		// vxuser.setGroupIdList(groupIdList);
		// List<String> groupNameList = new ArrayList<String>();
		// groupNameList.add(user.getGroupName());
		// vxuser.setGroupNameList(groupNameList);
		List<String> roleNameList = new ArrayList<String>();
		roleNameList.add("role_user");
		vxuser.setUserRoleList(roleNameList);
		vxuser.setStatus(1);
		return vxuser;
	}

	@Override
	public UserEntity getUser(UserEntity user) throws Exception {
		UserEntity userEntity = userDaoImpl.getUser(user);
		return userEntity;
	}

	/*
	 * @Override public List<String> getMenuByLdapUser(String userName) throws
	 * Exception { return userDaoImpl.getMenuByLdapUser(userName); }
	 */

	@Override
	public int resetUserPassword(UserEntity user) throws Exception {
		user.setPassword(MD5Utils.md5Password(user.getPassword()));
		int i = userDaoImpl.resetUserPassword(user);
		return i;
	}
}

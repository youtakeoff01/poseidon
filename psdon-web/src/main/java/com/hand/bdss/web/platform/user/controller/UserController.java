package com.hand.bdss.web.platform.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.web.common.constant.Global;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.util.MD5Utils;
import com.hand.bdss.web.common.util.StrUtils;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.entity.UserEntity;
import com.hand.bdss.web.platform.user.service.UserService;

/**
 * 用户的管理（包括用户组，角色管理）
 *
 * @author Administrator
 */
@Controller
@RequestMapping(value = "/usercontroller/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserController {

	@Resource
	private UserService userServiceImpl;

	@Resource
	private LogUtils logUtils;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private static final String password = "123456";

	/**
	 * 创建用户时的校验 --无需改动
	 *
	 * @return
	 */
	@RequestMapping("checkUser")
	public @ResponseBody String checkUser(@RequestBody String json) {
		BaseResponse base = new BaseResponse();
		if (!StringUtils.isNotBlank(json)) {
			base.setReturnCode("0");
			base.setReturnMessage("请求的参数为空！");
			return base.toString();
		}
		JSONObject object = JSON.parseObject(json);
		String userName = object.getString("userName");
		UserEntity user = new UserEntity();
		user.setUserName(userName);
		UserEntity userEntity = null;
		int id = 0;
		try {
			id = userServiceImpl.checkUser(user);
			if (id != 0) {
				userEntity = userServiceImpl.getUser(user);
			}
		} catch (Exception e) {
			logger.error("checkUser error,error msg:",e);
		}
		if (id == 0) {
			base.setReturnCode("1");
			base.setReturnMessage("校验成功!");
		} else {
			base.setReturnCode("0");
			base.setReturnMessage("用户名已存在!");
			base.setReturnObject(userEntity);
		}
		return base.toString();
	}

	/**
	 * 创建用户
	 *
	 * @param user
	 * @param request
	 * @return
	 */
	@RequestMapping("createUser")
	public @ResponseBody String createUser(@RequestBody UserEntity user, HttpServletRequest request) {
		BaseResponse base = new BaseResponse();
		UserEntity userEntity = (UserEntity) request.getSession().getAttribute("userMsg");
		user.setCreateAccount(userEntity.getUserName());
		int i = 0;
		try {
			if (StringUtils.isEmpty(user.getPassword()) && !Global.USER_TYPE_LDAP.equalsIgnoreCase(user.getUserType())) {
				user.setPassword(MD5Utils.md5Password(password));
			}
			// 判断用户名是否已存在
			int result = userServiceImpl.checkUser(user);
			if (result > 0) {
				base.setReturnCode("0");
				base.setReturnMessage("用户名已存在!");
				base.setReturnObject(userEntity);
				return base.toString();
			}
			i = userServiceImpl.insertUser(user, request);
			logUtils.writeLog("新建用户：" + user.getUserName(), LogUtils.LOGTYPE_SYS,
					GetUserUtils.getUser(request).getUserName());
		} catch (Exception e) {
			i = 0;
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("---------------删除用户失败，报错位置：UserController.createUser:报错信息" + e.getMessage());
		}
		if (i <= 0) {
			base.setReturnCode("0");
			base.setReturnMessage("新增用户失败!");
		} else {
			base.setReturnCode("1");
			base.setReturnMessage("新增用户成功!");
		}
		return base.toString();
	}

	/**
	 * 查询用户信息
	 *
	 * @param json
	 * @return
	 */
	@RequestMapping("listUsers")
	public @ResponseBody String listUsers(@RequestBody String json) {
		BaseResponse base = new BaseResponse();
		if (!StringUtils.isNotBlank(json)) {
			base.setReturnCode("0");
			base.setReturnMessage("请求的参数为空！");
			return base.toString();
		}
		UserEntity user = JsonUtils.toObject(json, UserEntity.class);
		JSONObject object = JSON.parseObject(json);
		int startPage = object.getIntValue("startPage");
		int count = object.getIntValue("count");

		List<UserEntity> users = null;
		int countAll = 0;
		try {
			// 解决模糊查询时的sql注入问题
			if (user != null && StringUtils.isNoneBlank(user.getUserName())) {
				String userName = user.getUserName();
				userName = StrUtils.escapeStr(userName);
				user.setUserName(userName);
			}
			users = userServiceImpl.listUsers(user, (startPage - 1) * 10, count);
			countAll = userServiceImpl.getCountAll(user);
		} catch (Exception e) {
			logger.error("listUsers error,error msg:",e);
		}

		Map<String, Object> maps = new HashMap<>();
		maps.put("users", users);
		maps.put("countAll", countAll);
		base.setReturnCode("1");
		base.setReturnMessage("查询用户信息操作成功！");
		base.setReturnObject(maps);
		return base.toString();
	}

	/**
	 * 查询所有用户的信息
	 */
	@RequestMapping("listAllUsers")
	public @ResponseBody String listAllUsers() {
		BaseResponse base = new BaseResponse();
		UserEntity userEntity = new UserEntity();
		userEntity.setStatus("0");// 所有已经启用的用户信息
		List<UserEntity> users = null;
		try {
			users = userServiceImpl.listUsers(userEntity, 0, 100000);
		} catch (Exception e) {
			logger.error("listAllUsers error,error msg:",e);
		}
		base.setReturnCode("1");
		base.setReturnMessage("查询用户信息操作成功！");
		base.setReturnObject(users);
		return base.toString();
	}

	/**
	 * 更新用户信息
	 *
	 * @param user
	 * @param request
	 * @return
	 */
	@RequestMapping("updateUser")
	public @ResponseBody String updateUser(@RequestBody UserEntity user, HttpServletRequest request) {
		BaseResponse base = new BaseResponse();
		if (user == null) {
			base.setReturnCode("0");
			base.setReturnMessage("请求参数为空！");
			return base.toString();
		}
		UserEntity userEntity = (UserEntity) request.getSession().getAttribute("userMsg");
		user.setUpdateAccount(userEntity.getUserName());
		int i = 0;
		try {
//			user.setUserType("psdon");
			i = userServiceImpl.updateUser(user);
			logUtils.writeLog("更新用户信息：" + user.getUserName(), LogUtils.LOGTYPE_SYS,
					GetUserUtils.getUser(request).getUserName());
		} catch (Exception e) {
			i = 0;
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("---------------修改用户信息失败，报错位置：UserController.updateUser:报错信息" + e.getMessage());
		}
		if (i <= 0) {
			base.setReturnCode("0");
			base.setReturnMessage("更新用户失败");
		} else {
			base.setReturnCode("1");
			base.setReturnMessage("更新用户成功！");
		}
		return base.toString();
	}

	/**
	 * 用户的删除
	 *
	 * @param user
	 * @param request
	 * @return
	 */
	@RequestMapping("deleteUser")
	public @ResponseBody String deleteUser(@RequestBody List<UserEntity> user, HttpServletRequest request) {
		BaseResponse base = new BaseResponse();
		if (!(user != null && user.size() > 0)) {
			base.setReturnCode("0");
			base.setReturnMessage("请求参数为空！");
			return base.toString();
		}

		StringBuffer str = new StringBuffer();
		for (UserEntity entity : user) {
			str.append(entity.getId() + ",");
		}

		int i = 0;
		try {
			i = userServiceImpl.deleteUser(user);
			logUtils.writeLog("删除用户：" + str, LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		} catch (Exception e) {
			i = 0;
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("---------------删除用户信息失败，报错位置：UserController.deleteUser:报错信息" + e.getMessage());
		}
		if (i <= 0) {
			base.setReturnCode("0");
			base.setReturnMessage("删除用户失败");
		} else {
			base.setReturnCode("1");
			base.setReturnMessage("删除用户成功");
		}
		return base.toString();
	}

	/**
	 * 重置用户密码
	 *
	 * @param user
	 * @param request
	 * @return
	 */
	@RequestMapping("resetUserPassword")
	public @ResponseBody String resetUserPassword(@RequestBody UserEntity user, HttpServletRequest request) {
		BaseResponse base = new BaseResponse();
		if (user == null) {
			base.setReturnCode("0");
			base.setReturnMessage("请求参数为空！");
			return base.toString();
		}
		// UserEntity userEntity = (UserEntity)
		// request.getSession().getAttribute("userMsg");
		user.setPassword(password);
		int i;
		try {
			i = userServiceImpl.resetUserPassword(user);
			logUtils.writeLog("重置用户密码 " + user.getUserName(), LogUtils.LOGTYPE_SYS,
					GetUserUtils.getUser(request).getUserName());
		} catch (Exception e) {
			i = 0;
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("---------------重置用户密码失败，报错位置：UserController.resetUserPassword:报错信息" + e.getMessage());
		}
		if (i <= 0) {
			base.setReturnCode("0");
			base.setReturnMessage("重置用户密码失败");
		} else {
			base.setReturnCode("1");
			base.setReturnMessage("重置用户密码成功！");
		}
		return base.toString();
	}

}

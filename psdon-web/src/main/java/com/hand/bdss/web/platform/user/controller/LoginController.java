package com.hand.bdss.web.platform.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.bdss.web.common.constant.Global;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.LDAPSupportUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.util.MD5Utils;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.entity.UserEntity;
import com.hand.bdss.web.platform.user.service.UserService;

/**
 * 处理用户的登录登出请求
 * 
 * @author 13693
 *
 */
@Controller
@RequestMapping(value = "/loginController/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class LoginController {
	@Resource
	private UserService userServiceImpl;

	@Resource
	LogUtils logUtils;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	/**
	 * 配置连接ldap的信息
	 * 
	 * @param json
	 * @param request
	 * @return
	 */
	/*
	 * @RequestMapping("ldapProp") public @ResponseBody String reload(@RequestBody
	 * String json, HttpServletRequest request){ BaseResponse base = new
	 * BaseResponse(); JSONObject object = JSON.parseObject(json);
	 * Map<String,String> map = new HashMap<String,String>(); map.put("rootDN",
	 * object.getString("rootDN")); map.put("ldapUrl", object.getString("ldapUrl"));
	 * map.put("domain", object.getString("domain")); map.put("user",
	 * object.getString("user")); map.put("ldapPwd", object.getString("ldapPwd"));
	 * try { new PropertiesOperationUtils().writeData(map); LDAPSupportUtils.init();
	 * LdapContext ctx = LDAPSupportUtils.connetLDAP();
	 * LDAPSupportUtils.setCtx(ctx); //重新加载类，使改动过的properties文件生效 ClassLoader loader
	 * = LDAPSupportUtils.class.getClassLoader();
	 * loader.loadClass("LDAPSupportUtils"); base.setReturnCode("1");
	 * base.setReturnMessage("ldap配置成功。"); } catch (Exception e1) {
	 * e1.printStackTrace(); base.setReturnCode("0");
	 * base.setReturnMessage("ldap配置失败！"); } return base.toString(); }
	 */

	/**
	 * 处理用户的登录
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping("login")
	public @ResponseBody String handlerLogin(@RequestBody UserEntity user, HttpServletRequest request) {
		BaseResponse base = new BaseResponse();
		Map<String, Object> map = new HashMap<>();
		// 返回用户的菜单列表
		List<String> menuList = null;
		HttpSession session = request.getSession(true);
		if ("0".equals(user.getLdapuser())) {// 表示该用户是ldap用户，要到ldap中进行用户认证
			try {
				boolean boo = LDAPSupportUtils.authenricate(user);
				if (boo) {// 认证成功
					user.setPassword(null);
					UserEntity userEntity = userServiceImpl.getUser(user);
					if(userEntity==null) {
						base.setReturnCode("0");
						base.setReturnMessage("该用户没有在poseidon平台分配权限！");
					}else if ("1".equals(userEntity.getStatus()) || "2".equals(userEntity.getStatus())) {
						base.setReturnCode("0");
						base.setReturnMessage("该用户被禁用，或者被删除，请联系管理员");
					} else {
						session.setAttribute("userMsg", userEntity);
						// 返回用户的菜单列表
						try {
							menuList = userServiceImpl.getMenuByUser(userEntity.getUserName());
						} catch (Exception e) {
							logger.error("ldap用户获取用户权限列表报错，错误信息为：", e);
						}
						// menuList = userServiceImpl.getMenuByLdapUser(user.getUserName());
						base.setReturnCode("1");
						base.setReturnMessage("登录成功！");
						map.put("user", userEntity);
					}
				} else {
					base.setReturnCode("0");
					base.setReturnMessage("用户名或密码校验失败！");
				}
			} catch (Exception e) {
				base.setReturnCode("0");
				base.setReturnMessage("用户名或密码校验失败！");
				logger.error("进行ldap认证的时候报错。");
				logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			}
		} else {
			user.setPassword(MD5Utils.md5Password(user.getPassword()));//密码
			user.setUserType(Global.USER_TYPE_PSDON);//用户类型
			UserEntity userEntity = null;
			try {
				userEntity = userServiceImpl.getUser(user);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (userEntity == null) {// 用户名密码校验失败
				base.setReturnCode("0");
				base.setReturnMessage("用户名或密码校验失败");
			} else if ("1".equals(userEntity.getStatus())) {
				base.setReturnCode("0");
				base.setReturnMessage("该用户被禁用，请联系管理员");
			} else {// 登录成功
				session.setAttribute("userMsg", userEntity);
				// 记录用户的登录信息
				logUtils.writeLog("登录成功", LogUtils.LOGTYPE_USER, userEntity.getUserName());
				base.setReturnCode("1");
				base.setReturnMessage("登录成功！");

				try {
					menuList = userServiceImpl.getMenuByUser(userEntity.getUserName());
				} catch (Exception e) {
					logger.error("psdon用户获取用户权限列表报错，错误信息为：", e);
				}
			}
			map.put("user", userEntity);
		}
		map.put("menuList", menuList);
		base.setReturnObject(map);
		return base.toString();
	}

	/**
	 * 用户登出系统
	 */
	@RequestMapping("logout")
	public @ResponseBody String logout(@RequestBody UserEntity user, HttpServletRequest request) {
		BaseResponse base = new BaseResponse();
		if (user == null) {
			base.setReturnCode("0");
			base.setReturnMessage("请求参数为空！");
			return base.toString();
		}
		HttpSession session = request.getSession(true);
		UserEntity userEntity = (UserEntity) session.getAttribute("userMsg");
		// 记录用户的退出信息
		logUtils.writeLog("退出系统", LogUtils.LOGTYPE_USER, userEntity.getUserName());
		session.setAttribute("userMsg", null);
		base.setReturnCode("1");
		base.setReturnMessage("用户登出系统成功！");
		return base.toString();
	}

	/**
	 * 修改用户密码
	 */
	@RequestMapping("changePassword")
	public @ResponseBody String changePassword(@RequestBody UserEntity user, HttpServletRequest request) {
		BaseResponse base = new BaseResponse();
		if (user == null) {
			base.setReturnCode("0");
			base.setReturnMessage("请求参数为空！");
			return base.toString();
		}
		HttpSession session = request.getSession(true);
		UserEntity userEntity = (UserEntity) session.getAttribute("userMsg");
		user.setPassword(MD5Utils.md5Password(user.getPassword()));
		if (!userEntity.getPassword().equals(user.getPassword())) {
			base.setReturnCode("0");
			base.setReturnMessage("原密码错误！");
			return base.toString();
		} else {
			user.setPassword(user.getNewPassword());
			user.setId(userEntity.getId());
			int i = 0;
			try {
				i = userServiceImpl.resetUserPassword(user);
			} catch (Exception e) {
				i = 0;
				logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
				logger.error("---------------修改用户密码失败，报错位置：LoginController.changePassword:报错信息" + e.getMessage());
			}

			if (i <= 0) {
				base.setReturnCode("0");
				base.setReturnMessage("修改用户密码失败");
			} else {
				userEntity.setPassword(user.getNewPassword());
				session.setAttribute("userMsg", userEntity);
				base.setReturnCode("1");
				base.setReturnMessage("修改用户密码成功！");
			}
			return base.toString();
		}
	}
}

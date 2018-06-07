package com.hand.bdss.web.platform.user.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.util.StrUtils;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.entity.UserGroupEntity;
import com.hand.bdss.web.platform.user.service.UserGroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/userGroupController/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserGroupController {

	@Resource
	private UserGroupService userGroupServiceImpl;
	@Resource
	LogUtils logUtils;

	@RequestMapping("checkUsergroup")
	public @ResponseBody String checkUsergroup(@RequestBody String json) {
		BaseResponse base = new BaseResponse();
		if (!StringUtils.isNotBlank(json)) {
			base.setReturnCode("0");
			base.setReturnMessage("请求的参数为空！");
			return base.toString();
		}
		JSONObject object = JSON.parseObject(json);
		String userGroupName = object.getString("groupName");
		UserGroupEntity userGroup = new UserGroupEntity();
		userGroup.setGroupName(userGroupName);
		int i = 0;
		UserGroupEntity group = null;
		try {
			i = userGroupServiceImpl.checkUserGroup(userGroup);
			if(i != 0){
				group = userGroupServiceImpl.getGroup(userGroup);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (i != 0) {
			base.setReturnCode("1");
			base.setReturnMessage("用户组已存在");
			base.setReturnObject(group);
		} else {
			base.setReturnCode("0");
			base.setReturnMessage("用户组不存在");
		}
		return base.toString();
	}

	public UserGroupController() {
		super();
	}

	@RequestMapping("createUsergroup")
	public @ResponseBody String createUsergroup(@RequestBody UserGroupEntity userGroupEntity, HttpServletRequest request) {
		BaseResponse base = new BaseResponse();
		if (userGroupEntity == null) {
			base.setReturnCode("0");
			base.setReturnMessage("请求参数为空！");
			return base.toString();
		}
		int i = 0;
		try {
			i = userGroupServiceImpl.insertUsergroup(userGroupEntity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			logUtils.writeLog("createUsergroup: " + userGroupEntity.getGroupName(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		if (i <= 0) {
			base.setReturnCode("0");
			base.setReturnMessage("新增用户组失败");
		} else {
			base.setReturnCode("1");
			base.setReturnMessage("新增用户组成功");
		}
		return base.toString();
	}

	@RequestMapping("deleteUsergroup")
	public @ResponseBody String deleteUsergroup(@RequestBody List<UserGroupEntity> list, HttpServletRequest request) {
		BaseResponse base = new BaseResponse();
		if (!(list != null && list.size() > 0)) {
			base.setReturnCode("0");
			base.setReturnMessage("请求参数为空！");
			return base.toString();
		}
		int i = 0;
		try {
			i = userGroupServiceImpl.deleteUsergroup(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			logUtils.writeLog("deleteUsergroup: " + JsonUtils.toJson(list), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		if (i <= 0) {
			base.setReturnCode("0");
			base.setReturnMessage("删除用户组失败");
		} else {
			base.setReturnCode("1");
			base.setReturnMessage("删除用户组成功");
		}
		return base.toString();
	}

	@RequestMapping("updateUsergroup")
	public @ResponseBody String updateUsergroup(@RequestBody UserGroupEntity userGroupEntity) {
		BaseResponse base = new BaseResponse();
		if (userGroupEntity == null) {
			base.setReturnCode("0");
			base.setReturnMessage("请求参数为空！");
			return base.toString();
		}
		int i = 0;
		try {
			i = userGroupServiceImpl.updateUsergroup(userGroupEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (i <= 0) {
			base.setReturnCode("0");
			base.setReturnMessage("更新用户失败");
		}else{
			base.setReturnCode("1");
			base.setReturnMessage("更新用户成功");
		}
		return base.toString();
	}

	@RequestMapping("listUsergroup")
	public @ResponseBody String listUsergroup(@RequestBody String json) {
		BaseResponse base = new BaseResponse();
		if (!StringUtils.isNotBlank(json)) {
			base.setReturnCode("0");
			base.setReturnMessage("请求的参数为空！");
			return base.toString();
		}
		UserGroupEntity userGroupEntity = JsonUtils.toObject(json, UserGroupEntity.class);
		JSONObject object = JSON.parseObject(json);
		int startPage = object.getIntValue("startPage");
		int count = object.getIntValue("count");
		List<UserGroupEntity> userGroupEntitys = null;
		int countAll = 0 ;
		try {
			//解决模糊查询时的sql注入问题
			if(userGroupEntity!=null && StringUtils.isNoneBlank(userGroupEntity.getGroupName())){
				String groupName = userGroupEntity.getGroupName();
				groupName = StrUtils.escapeStr(groupName);
				userGroupEntity.setGroupName(groupName);
			}
			userGroupEntitys = userGroupServiceImpl.listUsergroup(userGroupEntity, (startPage-1)*10, count);
			countAll = userGroupServiceImpl.getCountAll(userGroupEntity);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, Object> maps = new HashMap<>();
		maps.put("userGroupEntitys", userGroupEntitys);
		maps.put("countAll", countAll);
		base.setReturnCode("1");
		base.setReturnMessage("查询用户组操作成功!");
		base.setReturnObject(maps);
		return base.toString();
	}

	@RequestMapping("listAllUserGroup")
	public @ResponseBody String listAllUserGroup() {
		BaseResponse base = new BaseResponse();
		int startPage = 0;
		UserGroupEntity userGroupEntity = new UserGroupEntity();
		int count = 100000;
		userGroupEntity.setStatus("0");
		List<UserGroupEntity> userGroupEntitys = null;
		try {
			userGroupEntitys = userGroupServiceImpl.listUsergroup(userGroupEntity, startPage, count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		base.setReturnCode("1");
		base.setReturnMessage("查询所有用户组信息操作成功!");
		base.setReturnObject(userGroupEntitys);
		return base.toString();
	}
}
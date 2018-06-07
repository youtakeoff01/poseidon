package com.hand.bdss.web.platform.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.web.entity.RoleEntity;
import com.hand.bdss.web.platform.user.service.RoleService;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.vo.BaseResponse;

@Controller
@RequestMapping(value = "/roleController/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class RoleController {

	@Resource
	private RoleService roleServiceImpl;

	@RequestMapping("roleSelect")
	public @ResponseBody String roleSelect(@RequestBody String json) {
		BaseResponse base = new BaseResponse();
		if (!StringUtils.isNotBlank(json)) {
			base.setReturnCode("0");
			base.setReturnMessage("请求的参数为空！");
			return base.toString();
		}
		RoleEntity roleEntity = JsonUtils.toObject(json, RoleEntity.class);
		JSONObject object = JSON.parseObject(json);
		int startPage = object.getIntValue("startPage");
		int count = object.getIntValue("count");
		List<RoleEntity> lists = null;
		int countAll = 0;
		try {
			lists = roleServiceImpl.roleSelect(roleEntity, (startPage-1)*10, count);
			countAll = roleServiceImpl.getCountAll(roleEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		base.setReturnCode("1");
		base.setReturnMessage("角色查询操作成功！");
		Map<String, Object> maps = new HashMap<>();
		maps.put("lists", lists);
		maps.put("countAll", countAll);
		base.setReturnObject(maps);
		return base.toString();
	}
	
	
	@RequestMapping("roleSelectAll")
	public @ResponseBody String roleSelect() {
		BaseResponse base = new BaseResponse();
		RoleEntity roleEntity = new RoleEntity();
		int startPage = 0;
		int count = 1000;
		roleEntity.setStatus("0");
		List<RoleEntity> lists = null;
		int countAll = 0;
		try {
			lists = roleServiceImpl.roleSelect(roleEntity, startPage, count);
			countAll = roleServiceImpl.getCountAll(roleEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		base.setReturnCode("1");
		base.setReturnMessage("角色查询操作成功！");
		Map<String, Object> maps = new HashMap<>();
		maps.put("lists", lists);
		maps.put("countAll", countAll);
		base.setReturnObject(maps);
		return base.toString();
	}

}

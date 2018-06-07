package com.hand.bdss.web.datamanage.policy.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.dsmp.model.HivePolicy;
import com.hand.bdss.web.datamanage.policy.service.RangerService;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.vo.BaseResponse;

@Controller
@RequestMapping(value="/rangerController/",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class RangerController {
	
	@Resource
	private RangerService rangerServiceImpl;
	
	@RequestMapping("insertRanger")
	public @ResponseBody String insertRanger(@RequestBody HivePolicy hivePolicy,HttpServletRequest request){
		BaseResponse base = new BaseResponse();
		if (hivePolicy == null) {
			base.setReturnCode("0");
			base.setReturnMessage("请求参数为空！！！");
			return base.toString();
		}
		int boo = 0;
		try {
			boo = rangerServiceImpl.insertRanger(hivePolicy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (boo > 0) {
			base.setReturnCode("1");
			base.setReturnMessage("插入ranger操作成功！");
		} else {
			base.setReturnCode("0");
			base.setReturnMessage("插入ranger操作失败！");
		}
		return base.toString();
	}
	
	@RequestMapping("updateRangerByName")
	public @ResponseBody String updateRangerByName(@RequestBody HivePolicy hivePolicy,HttpServletRequest request){
		BaseResponse base = new BaseResponse();
		if (hivePolicy == null) {
			base.setReturnCode("0");
			base.setReturnMessage("请求参数为空！！！");
			return base.toString();
		}
		int i = 0;
		try {
			i = rangerServiceImpl.updateRangerByName(hivePolicy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (i > 0) {
			base.setReturnCode("1");
			base.setReturnMessage("ranger修改操作成功！");
		} else {
			base.setReturnCode("0");
			base.setReturnMessage("ranger修改操作失败！");
		}
		return base.toString();
	}
	
	@RequestMapping("listHivePolicy")
	public @ResponseBody String listHivePolicy(@RequestBody String json) {
		BaseResponse base = new BaseResponse();
		if (!StringUtils.isNotBlank(json)) {
			base.setReturnCode("0");
			base.setReturnMessage("请求参数为空！！！");
			return base.toString();
		}
		HivePolicy hivePolicy = JsonUtils.toObject(json, HivePolicy.class);
		JSONObject object = JSON.parseObject(json);
		int startPage = object.getIntValue("startPage");
		int count = object.getIntValue("count");
		List<HivePolicy> hivePolicys = null;
		int counts = 0 ;
		try {
			hivePolicys = rangerServiceImpl.listHivePolicy(hivePolicy, (startPage-1)*10, count);
			counts = rangerServiceImpl.getCounts(hivePolicy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String,Object> map = new HashMap<>();
		map.put("hivePolicys", hivePolicys);
		map.put("counts",counts);
		base.setReturnCode("1");
		base.setReturnMessage("数据源查询操作成功！");
		base.setReturnObject(map);
		return base.toString();
	}
	
	@RequestMapping("deleteRangerByName") 
	public @ResponseBody String deleteRangerByName(@RequestBody List<HivePolicy> hivePolicys,HttpServletRequest request){
		BaseResponse base = new BaseResponse();
		if (!(hivePolicys != null && hivePolicys.size() > 0)) {
			base.setReturnCode("0");
			base.setReturnMessage("请求参数为空！！！");
			return base.toString();
		}
		int i = 0;
		try {
			i = rangerServiceImpl.deleteRangerByName(hivePolicys);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (i > 0) {
			base.setReturnCode("1");
			base.setReturnMessage("数据源删除操作成功。");
		} else {
			base.setReturnCode("0");
			base.setReturnMessage("数据源删除操作失败！");
		}
		return base.toString();
	}
}

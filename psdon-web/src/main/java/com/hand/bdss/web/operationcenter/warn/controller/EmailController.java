package com.hand.bdss.web.operationcenter.warn.controller;

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
import com.hand.bdss.web.operationcenter.warn.service.EmailService;
import com.hand.bdss.web.entity.EmailEntity;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.util.StrUtils;
import com.hand.bdss.web.common.vo.BaseResponse;

@Controller
@RequestMapping(value = "/emailController/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class EmailController {
	@Resource 
	private EmailService emailServiceImpl;
	@Resource
	private LogUtils logUtils;
	
	private static final Logger logger = LoggerFactory.getLogger(EmailController.class);
	
	/**
	 * 通知渠道 新建
	 * 
	 * @param emailEntity
	 * @return
	 */
	@RequestMapping("insertEmail")
	public @ResponseBody String insertEmail(@RequestBody EmailEntity emailEntity,HttpServletRequest request){
		BaseResponse base = new BaseResponse();
		if(emailEntity == null){
			base.setReturnCode("0");
			base.setReturnMessage("传入的参数为空！");
			return base.toString();
		}
		int i = 0 ;
		try {
			i = emailServiceImpl.insertEmail(emailEntity);
		} catch (Exception e) {
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("--------------------------插入失败，报错位置：EmailController。insertEmail：报错信息"+e.getMessage());
			e.printStackTrace();
			base.setReturnCode("0");
			base.setReturnMessage("插入数据失败");
			return base.toString();
		} finally {
			logUtils.writeLog("新建通知渠道: " + emailEntity.getChannelName(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		if(i>0){
			base.setReturnCode("1");
			base.setReturnMessage("插入数据成功!");
		}else{
			base.setReturnCode("0");
			base.setReturnMessage("插入数据失败");
		}
		return base.toString();
	}
	
	/**
	 * 通知渠道 查询
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping("listEmails")
	public @ResponseBody String listEmails(@RequestBody String json,HttpServletRequest request){
		BaseResponse base = new BaseResponse();
		EmailEntity mail  = JsonUtils.toObject(json, EmailEntity.class);
		JSONObject object = JSON.parseObject(json);
		int startPage = object.getIntValue("startPage");
		int count = object.getIntValue("count");
		List<EmailEntity> mails = null;
		int countAll = 0;
		try {
			//解决模糊查询时的sql注入问题
			if(mail!=null && StringUtils.isNoneBlank(mail.getChannelName())){
				String channelName = mail.getChannelName();
				channelName = StrUtils.escapeStr(channelName);
				mail.setChannelName(channelName);
			}
			mails = emailServiceImpl.listConditions(mail,(startPage-1)*10, count);
			countAll = emailServiceImpl.listConditionCountAll(mail);
		} catch (Exception e) {
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("---------------查询报错，报错位置：EmailController.selectEmail:报错信息"
					+ e.getMessage());
			e.printStackTrace();
			base.setReturnCode("0");
			base.setReturnMessage("查询操作失败！");
			return base.toString();
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("countAll", countAll);
		map.put("mails", mails);
		base.setReturnCode("1");
		base.setReturnMessage("查询操作成功！");
		base.setReturnObject(map);
		return base.toString();
		
	}
	
	/**
	 * 验证通知渠道名称是否存在
	 * @param emailEntity
	 * @param request
	 * @return
	 */
	@RequestMapping("listEmailsAll")
	public @ResponseBody String listEmailsAll(@RequestBody EmailEntity emailEntity,HttpServletRequest request){
		BaseResponse base = new BaseResponse();
		if(emailEntity == null){
			base.setReturnCode("0");
			base.setReturnMessage("请求参数为空！！！");
			return base.toString();
		}
		List<EmailEntity> emailEntitys = null ;
		try {
			emailEntitys = emailServiceImpl.listEmailsAll(emailEntity);
		} catch (Exception e) {
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("--------------------------查询失败，报错位置：EmailController。listEmailsAll：报错信息"+e.getMessage());
			e.printStackTrace();
			base.setReturnCode("0");
			base.setReturnMessage("验证通知渠道名称失败");
			return base.toString();
		}
		if(emailEntitys != null && emailEntitys.size()>0){
			base.setReturnCode("1");
			base.setReturnMessage("渠道名称已存在!");
		}else{
			base.setReturnCode("0");
			base.setReturnMessage("渠道名称不存在！");
		}
		return base.toString();
	}
	
	/**
	 * 通知渠道 更新
	 * 
	 * @param emailEntity
	 * @return
	 */
	@RequestMapping("updateEmail")
	public @ResponseBody String updateEmail(@RequestBody EmailEntity emailEntity,HttpServletRequest request){
		BaseResponse base = new BaseResponse();
		if(emailEntity == null){
			base.setReturnCode("0");
			base.setReturnMessage("传入参数为空");
			return base.toString();
		}
		int i = 0 ;
		try {
			i = emailServiceImpl.updateEmail(emailEntity);
		} catch (Exception e) {
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("--------------------------更新失败，报错位置：EmailController。updateEmail：报错信息"+e.getMessage());
			e.printStackTrace();
			base.setReturnCode("0");
			base.setReturnMessage("更新数据失败");
			return base.toString();
		}
		if(i>0){
			base.setReturnCode("1");
			base.setReturnMessage("更新数据成功!");
		}else{
			base.setReturnCode("0");
			base.setReturnMessage("更新数据失败");
		}
		return base.toString();
	}

	/**
	 * 通知渠道 删除
	 * 
	 * @param emailEntity
	 * @return
	 */
	@RequestMapping("deleteEmail")
	public @ResponseBody String deleteEmail(@RequestBody List<EmailEntity> emailEntity,HttpServletRequest request){
		BaseResponse base = new BaseResponse();
		if(!((emailEntity!= null) && emailEntity.size() > 0)){
			base.setReturnCode("0");
			base.setReturnMessage("请求参数为空！");
			return base.toString();
		}
		int i = 0 ;
		try {
			i = emailServiceImpl.deleteEmail(emailEntity,request);
		} catch (Exception e) {
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("--------------------------删除失败，报错位置：EmailController。deleteEmail：报错信息"+e.getMessage());
			e.printStackTrace();
			base.setReturnCode("0");
			base.setReturnMessage("删除数据失败");
			return base.toString();
		}
		if(i>0){
			base.setReturnCode("1");
			base.setReturnMessage("删除数据成功!");
		}else{
			base.setReturnCode("0");
			base.setReturnMessage("删除数据失败");
		}
		return base.toString();
	}
	
	
	
	
	
//	@RequestMapping("selectOneEmail")
	public @ResponseBody String selectOneEmail(@RequestBody EmailEntity emailEntity,HttpServletRequest request){
		BaseResponse base = new BaseResponse();
		EmailEntity mail = null;
		try {
			mail = emailServiceImpl.selectOneRule(emailEntity);
		} catch (Exception e) {
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("---------------查询邮件规则报错，报错位置：EmailController.selectOneEmail:报错信息"
					+ e.getMessage());
			e.printStackTrace();
		}
		base.setReturnCode("1");
		base.setReturnMessage("所有rule查询操作成功！");
		base.setReturnObject(mail);
		return base.toString();
	}
}

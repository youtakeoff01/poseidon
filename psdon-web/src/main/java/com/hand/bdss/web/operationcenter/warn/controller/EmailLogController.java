package com.hand.bdss.web.operationcenter.warn.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.operationcenter.warn.service.EmailLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.bdss.web.entity.EmailLogEntity;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.vo.BaseResponse;

@Controller
@RequestMapping(value = "/emailLogController/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class EmailLogController {
	@Resource
	private EmailLogService emailLogServiceImpl;
	
	private static final Logger logger = LoggerFactory.getLogger(EmailLogController.class);
	
	@Resource
	private LogUtils logUtils;
	@RequestMapping("insertEmailLog")
	public @ResponseBody String insertEmailLog(@RequestBody EmailLogEntity emailLogEntity,HttpServletRequest request){
		BaseResponse base = new BaseResponse();
		int i = 0;
		try {
			i = emailLogServiceImpl.insertEmailLog(emailLogEntity);
		} catch (Exception e) {
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("---------------插入邮件日志报错，报错位置：EmailLogController.insertEmailLog:报错信息"
					+ e.getMessage());
			e.printStackTrace();
			base.setReturnCode("0");
			base.setReturnMessage("插入邮件日志失败");
			return base.toString();
		}
		if (i>0) {
			base.setReturnCode("1");
			base.setReturnMessage("插入邮件日志成功");
		} else {
			base.setReturnCode("0");
			base.setReturnMessage("插入邮件日志失败");
		}
		return base.toString();
	}
	
	/**
	 * 查询所有的邮件日志信息
	 * @return
	 */
//	@RequestMapping("selectEmailLogs")
	public @ResponseBody String selectEmailLogs(HttpServletRequest request){
		BaseResponse base = new BaseResponse();
		EmailLogEntity emailLogEntity = new EmailLogEntity();
		List<EmailLogEntity> emailLogEntitys = null;
		int counts = 0;
		try {
			emailLogEntitys = emailLogServiceImpl.selectEmailLogs(emailLogEntity, 0, 1000000);
			counts = emailLogServiceImpl.getCounts(emailLogEntity);
		} catch (Exception e) {
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("---------------查询所有邮件日志信息报错，报错位置：EmailLogController.selectEmailLogs:报错信息"
					+ e.getMessage());
			e.printStackTrace();
		}
		base.setReturnCode("1");
		base.setReturnMessage("所有邮件日志信息查询操作成功！");
		Map<String, Object> map = new HashMap<>();
		map.put("emailLogEntitys", emailLogEntitys);
		map.put("counts", counts);
		base.setReturnObject(map);
		return base.toString();
	}
	
	//系统异常占比
	@RequestMapping("listSysErrorProp")
	public @ResponseBody String listSysErrorProp(HttpServletRequest request){
		BaseResponse base = new BaseResponse();
		List<Map<String,Map<String, Integer>>> lists = new ArrayList<Map<String,Map<String, Integer>>>();
		String [] serviceNames = new String[]{"HDFS","ZOOKEEPER","HIVE","HBASE"};
		
		try {
			//lists = emailLogServiceImpl.listSysErrorProp();
			/*for(String str:serviceNames){
				Map<String, Map<String, Integer>>  map = new DataServiceMetrics().getServiceComponentState(str);
				lists.add(map);
			}*/
		} catch (Exception e) {
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("---------------查询所有组件异常占比报错，报错位置：EmailLogController.listSysErrorProp:报错信息"
					+ e.getMessage());
			e.printStackTrace();
		}
		if(lists!=null && lists.size()>0){
			base.setReturnCode("1");
			base.setReturnMessage("查询所有组件异常占比成功！！！");
			base.setReturnObject(lists);
		}else{
			base.setReturnCode("0");
			base.setReturnMessage("查询所有系统异常占比失败");
		}
		return base.toString();
	}
	
}

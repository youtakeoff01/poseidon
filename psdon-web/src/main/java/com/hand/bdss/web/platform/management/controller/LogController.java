package com.hand.bdss.web.platform.management.controller;

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
import com.hand.bdss.web.entity.LogEntity;
import com.hand.bdss.web.platform.management.service.LogService;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.vo.BaseResponse;

@Controller
@RequestMapping(value = "/logController/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class LogController {
	
	private Logger logger = LoggerFactory.getLogger(LogController.class);
	
	@Resource
	private LogUtils logUtils;

	@Resource
	private LogService logServiceImpl;

	@RequestMapping("logSelect")
	public @ResponseBody String logSelect(@RequestBody String json) {
		BaseResponse base = new BaseResponse();
		if (!StringUtils.isNotBlank(json)) {
			base.setReturnCode("0");
			base.setReturnMessage("请求的参数为空！");
			return base.toString();
		}
		LogEntity logEntity = JsonUtils.toObject(json, LogEntity.class);
		JSONObject object = JSON.parseObject(json);
		int startPage = object.getIntValue("startPage");
		int count = object.getIntValue("count");
		List<LogEntity> lists = null;
		int countAll = 0;
		try {
			if(logEntity.getLogType() != null && LogUtils.LOGTYPE_SQL.equalsIgnoreCase(logEntity.getLogType())){
				lists = logServiceImpl.selectLog(logEntity, (startPage-1)*5, count);
			}else{
				lists = logServiceImpl.selectLog(logEntity, (startPage-1)*10, count);
			}
			countAll = logServiceImpl.getCountAll(logEntity);
		} catch (Exception e) {
			base.setReturnCode("0");
			base.setReturnMessage("查询日志操作失败！");
			e.printStackTrace();
			return base.toString();
		}
		Map<String, Object> maps = new HashMap<>();
		maps.put("lists", lists);
		maps.put("countAll", countAll);
		base.setReturnCode("1");
		base.setReturnMessage("查询日志操作成功！");
		base.setReturnObject(maps);
		return base.toString();
	}
	
	
	/**
	 * 删除log日志记录
	 * @return
	 */
	@RequestMapping("deleteLog")
	public @ResponseBody String deleteLog(@RequestBody List<LogEntity> list,HttpServletRequest req){
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		if(list == null || list.size() == 0){
			base.setReturnMessage("请求参数为空");
			return base.toString();
		}
		int i = 0;
		try{
			i = logServiceImpl.deleteLog(list);
		}catch(Exception e){
			i = 0;
			logger.info("删除log日志失败",e);
		}finally{
			logUtils.writeLog("删除log日志:"+list, LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(req).getUserName());
		}
		if(i > 0){
			base.setReturnCode("1");
			base.setReturnMessage("删除log日志成功");
		}else{
			base.setReturnMessage("删除log日志失败");
		}
		return base.toString();
	}

}

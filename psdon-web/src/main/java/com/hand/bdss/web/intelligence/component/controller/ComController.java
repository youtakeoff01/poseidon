package com.hand.bdss.web.intelligence.component.controller;

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
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.intelligence.component.service.ComService;
import com.hand.bdss.web.intelligence.component.vo.AIOptsVO;
import com.hand.bdss.web.intelligence.component.vo.MenuTree;
import com.hand.bdss.web.operationcenter.task.service.TaskService;

/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习组件控制类
 */
@Controller
@RequestMapping(value="/comController/",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ComController {

	private static final Logger logger = LoggerFactory.getLogger(ComController.class);

	@Resource
	private ComService comServiceImpl;
	
	@Resource
	private TaskService taskServiceImpl;

	@Resource
	private LogUtils logUtils;
	
	/**
	 * 根据条件查询组件树
	 * @param 
	 * @param request
	 * @return
	 */
	@RequestMapping("listAITree")
	public @ResponseBody String listAITree(HttpServletRequest request){
		logger.info("ComController.listAITree start!");
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		try{
			List<MenuTree> menuList = comServiceImpl.listTree();
			base.setReturnCode("1");
			base.setReturnMessage("组件树查询成功!");
			base.setReturnObject(menuList);
		}catch(Exception e){
			logger.error("ComController.listAITree error!",e);
			base.setReturnMessage("组件树查询失败");
		}
		logger.info("ComController.listAITree end! reponseMessage: {}", base.getReturnMessage());
		return base.toString();
	}

	/**
	 * 获取组件属性
	 * @param data
	 * @return
	 */
	@RequestMapping("getOpts")
	public @ResponseBody String getAIOpts(@RequestBody String data){
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		if(StringUtils.isEmpty(data)){
			base.setReturnMessage("请求参数为空");
			return base.toString();
		}
		logger.info("ComController.getOpts start!");
		Map<String,Object> optsMap = new HashMap<String,Object>();
		try{
			List<AIOptsVO> list = comServiceImpl.listOpts(data);
			base.setReturnCode("1");
			base.setReturnMessage("查询组件属性成功");
			optsMap.put("optsEntity",list);
			base.setReturnObject(optsMap);
		}catch(Exception e){
			logger.error("ComController.getOpts error!",e);
			base.setReturnMessage("查询组件属性失败");
		}
		logger.info("ComController.getOpts end ! reponseMessage: {}", base.getReturnMessage());
		return base.toString();
	}

	/**
	 * 保存机器学习
	 * @param data
	 * @return
	 */
	@RequestMapping("saveAITask")
	public @ResponseBody String saveAITask(@RequestBody String data,HttpServletRequest req){
		logger.info("AIController.saveAITask start!");
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		if(StringUtils.isEmpty(data)){
			base.setReturnMessage("请求参数为空");
			return base.toString();
		}
		try {
			boolean flag = comServiceImpl.saveAITask(data,req);
			if(flag){
				base.setReturnCode("1");
				base.setReturnMessage("机器学习任务保存成功");
			}else{
				base.setReturnMessage("机器学习任务保存失败");
			}
		} catch (Exception e) {
			logger.error("ComController.saveAITask error!",e);
			base.setReturnMessage("机器学习任务保存异常");
		} finally {
			logUtils.writeLog("新建机器学习任务: "+base.getReturnMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(req).getUserName());
		}
		logger.info("ComController.saveAITask end! reponseMessage: {}", base.getReturnMessage());
		return base.toString();
	}
	
	
	/**
	 * 更新机器学习
	 * @param data
	 * @return
	 */
	@RequestMapping("updateAITask")
	public @ResponseBody String updateAITask(@RequestBody String data,HttpServletRequest req){
		logger.info("AIController.ComController start!");
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		if(StringUtils.isEmpty(data)){
			base.setReturnMessage("请求参数为空");
			return base.toString();
		}
		try {
			boolean flag = comServiceImpl.updateAITask(data,req);
			if(flag){
				base.setReturnCode("1");
				base.setReturnMessage("机器学习任务更新成功");
			}else{
				base.setReturnMessage("机器学习任务更新失败");
			}
		} catch (Exception e) {
			logger.error("ComController.updateAITask error!",e);
			base.setReturnMessage("机器学习任务更新异常");
		} finally {
			logUtils.writeLog("更新机器学习任务: "+ base.getReturnMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(req).getUserName());
		}
		logger.info("ComController.updateAITask end! reponseMessage: {}", base.getReturnMessage());
		return base.toString();
	}
	
	/**
	 * 机器学习页面执行AI
	 * @param data
	 * @param req
	 * @return
	 */
	@RequestMapping("executeAIInfo")
	public @ResponseBody String executeAITask(@RequestBody String data,HttpServletRequest req){
		logger.info("ComController.executeAIConfig start!");
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		if(StringUtils.isEmpty(data)){
			base.setReturnMessage("请求参数为空");
			return base.toString();
		}
		try{
			boolean flag = comServiceImpl.executeAIConfig(data,req);
			if(flag){
				base.setReturnCode("1");
				base.setReturnMessage("机器学习执行成功！");
			}else{
				base.setReturnMessage("机器学习执行失败");
			}
		}catch(Exception e){
			logger.info("ComController.executeAIConfig error!",e);
			base.setReturnMessage("机器学习执行异常");
		}
		logger.info("ComController.executeAIConfig end ! reponseMessage: {}", base.getReturnMessage());
		return base.toString();
	}
	
	/**
	 * 获取AI日志
	 * @param data
	 * @return
	 */
	@RequestMapping("getAILog")
	public @ResponseBody String getAILog(@RequestBody String data,HttpServletRequest req){
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		if(StringUtils.isBlank(data)){
			base.setReturnMessage("请求参数为空");
			return base.toString();
		}		
		try{
			String resLog = comServiceImpl.getAILog(data);
			base.setReturnCode("1");
			base.setReturnMessage("获取AI日志成功");
			base.setReturnObject(resLog);
		}catch(Exception e){
			logger.error("ComController.getAILog error ! ",e);
			base.setReturnMessage("获取AI日志失败");
		}
		logger.info("ComController.getAILog end ! reponseMessage: {}", base.getReturnMessage());
		return base.toString();
	}
	
	/**
	 * 机器学习 获取组件日志
	 * @param data
	 * @param req
	 * @return
	 */
	@RequestMapping("getAIComLog")
	public @ResponseBody String getAIComLog(@RequestBody String data,HttpServletRequest req){
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		if(StringUtils.isBlank(data)){
			base.setReturnMessage("请求参数为空");
			return base.toString();
		}		
		try{
			String resLog = comServiceImpl.getComAILog(data);
			base.setReturnCode("1");
			base.setReturnMessage("获取组件日志成功");
			base.setReturnObject(resLog);
		}catch(Exception e){
			logger.error("comController.getAILog error ! ",e);
			base.setReturnMessage("获取组件日志失败");
		}
		logger.info("ComController.getAIComLog end ! reponseMessage: {}", base.getReturnMessage());
		return base.toString();
	}
	
	/**
	 * 机器学习 查看组件数据
	 * @param data
	 * @return
	 */
	@RequestMapping("getAIData")
	public @ResponseBody String getAIData(@RequestBody String data){
		logger.info("ComController.getAIData start!");
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		if(StringUtils.isEmpty(data)){
			base.setReturnMessage("请求参数为空");
			return base.toString();
		}
		try{
			Map<String, Object> map = comServiceImpl.getAIData(data);
			if(map.get("retCode") != null){
				throw new Exception("查询异常");
			}
			base.setReturnObject(map);
            base.setReturnCode("1");
            base.setReturnMessage("查看组件数据成功！");
		}catch(Exception e){
			logger.info("ComController.getAIData error!",e);
			base.setReturnMessage("查看组件数据失败,请检查参数配置");
		}
		logger.info("ComController.getAIData end! reponseMessage: {}", base.getReturnMessage());
		return base.toString();
	}

	/**
	 * 获取数据源节点属性
	 * @param data
	 * @param req
	 * @return
	 */
	@RequestMapping("getPreOpts")
	public @ResponseBody String getPreOpts(@RequestBody String data,HttpServletRequest req){
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		logger.info("ComController.getPreOpts start!");
		if(StringUtils.isBlank(data)){
			base.setReturnMessage("请求参数为空");
			return base.toString();
		}
		try{
			List<Map<String, String>> list = comServiceImpl.getPreOpts(data,req);
			base.setReturnCode("1");
			base.setReturnMessage("获取数据源节点属性成功");
			base.setReturnObject(list);
		}catch(Exception e){
			logger.info("ComController.getPreOpts error!",e);
			base.setReturnMessage("获取数据源节点属性失败,请检查参数配置");
		}
		logger.info("ComController.getPreOpts end! reponseMessage: {}", base.getReturnMessage());
		return base.toString();
	}
	
	/**
	 * 获取执行结果
	 * @param json
	 * @return
	 */
	@RequestMapping("getExecReport")
	public @ResponseBody String getExecReport(@RequestBody String json){
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		logger.info("ComController.getExecResult start!");
		if(StringUtils.isBlank(json)){
			base.setReturnMessage("请求参数为空");
			return base.toString();
		}
		try{
			Map<String,Object> map = comServiceImpl.getExecReport(json);
			if(map != null && !map.isEmpty()){
				base.setReturnCode("1");
				base.setReturnObject(map);
				base.setReturnMessage("获取执行结果成功");
			}else{
				base.setReturnMessage("暂无数据");
			}
		}catch(Exception e){
			logger.error("ComController.getExecResult error : {}",e);
			base.setReturnMessage("获取执行结果失败");
		}
		logger.info("ComController.getPreOpts end! reponseMessage: {}", base.getReturnMessage());
		return base.toString();
	}
	
	
	/**
	 * 获取Hive数据库
	 * @return
	 */
	@RequestMapping("getHiveDatabases")
	public @ResponseBody String getHiveDatabases(){
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		logger.info("ComController.getHiveDatabases start!");
		try{
			List<Map<String, String>> list = comServiceImpl.getHiveDatabases();
			base.setReturnCode("1");
			base.setReturnMessage("获取库数据成功");
			base.setReturnObject(list);
		}catch(Exception e){
			logger.info("ComController.getHiveDatabases error!",e);
			base.setReturnMessage("获取库数据失败");
		}
		logger.info("ComController.getHiveDatabases end! reponseMessage: {}", base.getReturnMessage());
		return base.toString();
	}
	
	
	/**
	 * 获取Hive数据表
	 * @return
	 */
	@RequestMapping("getHiveTables")
	public @ResponseBody String getHiveTables(@RequestBody String json){
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		logger.info("ComController.getHiveTables start! param = {}",json);
		if(StringUtils.isBlank(json)){
			base.setReturnMessage("请求参数为空");
			return base.toString();
		}
		try{
			List<Map<String, String>> list = comServiceImpl.getHiveTables(json);
			base.setReturnCode("1");
			base.setReturnMessage("获取表数据成功");
			base.setReturnObject(list);
		}catch(Exception e){
			logger.info("ComController.getHiveTables error!",e);
			base.setReturnMessage("获取表数据失败");
		}
		logger.info("ComController.getHiveTables end! reponseMessage: {}", base.getReturnMessage());
		return base.toString();
	}
		
	
}

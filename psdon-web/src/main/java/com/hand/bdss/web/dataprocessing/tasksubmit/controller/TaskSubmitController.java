package com.hand.bdss.web.dataprocessing.tasksubmit.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.common.vo.ShowTaskJarInfoVO;
import com.hand.bdss.web.common.vo.SubmitTaskJarVO;
import com.hand.bdss.web.dataprocessing.tasksubmit.service.JarInfoService;
import com.hand.bdss.web.dataprocessing.tasksubmit.service.TaskAttributeService;
import com.hand.bdss.web.dataprocessing.tasksubmit.service.TaskSubmitService;
import com.hand.bdss.web.entity.JarInfoEntity;
import com.hand.bdss.web.entity.LatestTaskEntity;

/**
 * 任务提交的控制器
 * 
 * @author liqifei
 * @DATA 2017年11月30日
 */
@RestController
@RequestMapping(value = "/tasksubmitcontroller/",produces = "text/plain;charset=UTF-8")
public class TaskSubmitController {
	private static final Logger logger = Logger.getLogger(TaskSubmitController.class);
	@Resource
	private TaskSubmitService taskSubmitServiceImpl;
	@Resource
	private TaskAttributeService taskAttributeServiceImpl;
	@Resource 
	private JarInfoService jarInfoServiceImpl;
	
	/**
	 * jar包上传接口
	 * @param request
	 * @param uploadFile
	 * @return
	 */
	@RequestMapping(value = "insertTaskJar", method = RequestMethod.POST)
	public String insertTaskJar(HttpServletRequest request,@RequestBody MultipartFile uploadFile) {
		BaseResponse resp = new BaseResponse();
		try {
			int i = jarInfoServiceImpl.insertJarInfo(request,uploadFile);
			if(i==0) {
				resp.setReturnCode("2");
				resp.setReturnMessage("您上传的jar包已经存在！");
			}else {
				resp.setReturnCode("1");
			}
		} catch (Exception e) {
			logger.error("上传jar包报错，错误信息为：{}",e);
			resp.setReturnCode("0");
			resp.setReturnMessage("服务器开了个小差，请稍后重试!");
		}
		return resp.toString();
	}
	/**
	 * 查询所有jar的信息的接口
	 * @paramtaskvo
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "listTaskJars",method = RequestMethod.POST)
	public String listTaskJars(@RequestBody SubmitTaskJarVO taskjarvo,HttpServletRequest request) {
		BaseResponse resp = new BaseResponse();
        int startPage = taskjarvo.getStartPage();
        int count = taskjarvo.getCount();
        JarInfoEntity jarInfo = taskjarvo.getJarInfoEntity();
        try {
        	List<JarInfoEntity> lists = jarInfoServiceImpl.listTaskJars(request,jarInfo,(startPage - 1) * 10,count);
        	int counts = jarInfoServiceImpl.listTaskJarCounts(request,jarInfo);
        	Map<String,Object> map = Maps.newHashMap();
        	map.put("countAll", counts);
        	map.put("lists", lists);
        	resp.setReturnCode("1");
        	resp.setReturnObject(map);
		} catch (Exception e) {
			logger.error("查询jar包信息出错，错误信息为：{}",e);
			resp.setReturnCode("0");
			resp.setReturnMessage("服务器开了个小差，请稍后重试!");
		}
		return resp.toString();
	}
	/**
	 * 判断jar任务名称是否已经存在
	 * @param latestTaskEntity
	 * @return
	 */
	@RequestMapping(value = "checkJarTaskName",method = RequestMethod.POST)
	public String checkJarTaskName(@RequestBody LatestTaskEntity latestTaskEntity) {
		BaseResponse resp = new BaseResponse();
		try {
			boolean boo = taskSubmitServiceImpl.checkJarTaskName(latestTaskEntity);
			if(boo) {
				resp.setReturnCode("0");
				resp.setReturnMessage("任务名称已经存在！");
			}else {
				resp.setReturnCode("1");
			}
		} catch (Exception e) {
			logger.error("判断jar任务名称是否已经存在接口报错，错误信息为：{}",e);
			resp.setReturnCode("0");
			resp.setReturnMessage("服务器开了个小差，请稍后重试!");
		}
		return resp.toString();
	}

	/**
	 * 删除jar
	 * @return
	 */
	@RequestMapping(value = "deleteTaskJars",method = RequestMethod.POST)
	public String deleteJar(@RequestBody List<SubmitTaskJarVO> taskjarvos,HttpServletRequest request){
		BaseResponse resp = new BaseResponse();
		Map<String,Object> retMap = new HashMap<>();
		try {
			retMap = jarInfoServiceImpl.deleteJars(request,taskjarvos);
			resp.setReturnCode("1");
			resp.setReturnObject(retMap);
		}catch (Exception e){
			logger.error("delete jar failed",e);
			resp.setReturnCode("0");
			resp.setReturnObject(retMap);
		}

		return resp.toString();
	}
	/**
	 * 提交jar任务
	 * @param taskjarvo
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "submitJarTask",method = RequestMethod.POST)
	public String submitJarTask(@RequestBody SubmitTaskJarVO taskjarvo,HttpServletRequest request) {
		BaseResponse resp = new BaseResponse();
		try {
			taskSubmitServiceImpl.submitJarTask(taskjarvo,request);
			resp.setReturnCode("1");
		} catch (Exception e) {
			logger.error("提交jar任务报错，错误信息为：{}",e);
			resp.setReturnCode("0");
			resp.setReturnMessage("服务器开了个小差，请稍后重试!");
		}
		return resp.toString();
	}

	/**
	 * 配置SQOOP脚本定时设置
	 * @param request
	 * @param taskJarVO
	 * @return
	 */
	@RequestMapping(value = "setSechdulerTask",method = RequestMethod.POST)
	public String setSechdulerTask(HttpServletRequest request,@RequestBody SubmitTaskJarVO taskJarVO){
		BaseResponse resp = new BaseResponse();
		try {
			LatestTaskEntity latestTaskEntity = taskJarVO.getLatestTaskEntity();
			//定时器修改，清空邮件字段
			if (StringUtils.isEmpty(latestTaskEntity.getTimerAttribute())) {
				latestTaskEntity.setEmailStr("");
			}
			Map<String, Object> stringObjectMap = taskSubmitServiceImpl.setSechdulerTask(request, latestTaskEntity);
			resp.setReturnObject(stringObjectMap);
		}catch (Exception e){
			logger.error("配置SQOOP脚本定时报错，错误信息为：{}",e);
			resp.setReturnCode("0");
			resp.setReturnMessage("服务器开了个小差，请稍后重试!");
		}

		return resp.toString();
	}
	
	/**
	 * 查询任务信息
	 * @param request
	 * @param taskvo
	 * @return
	 */
	@RequestMapping(value = "listJarTasks", method = RequestMethod.POST)
	public String listJarTasks(HttpServletRequest request, @RequestBody SubmitTaskJarVO taskvo) {
		BaseResponse resp = new BaseResponse();
		try {
			List<ShowTaskJarInfoVO> lists = taskSubmitServiceImpl.listJarTasks(taskvo,request);
			int counts = taskSubmitServiceImpl.listJarTaskCounts(request,taskvo);
        	Map<String,Object> map = Maps.newHashMap();
        	map.put("countAll", counts);
        	map.put("lists", lists);
			resp.setReturnCode("1");
			resp.setReturnObject(map);
		} catch (Exception e) {
			logger.error("查询jar任务报错，错误信息为：{}",e);
			resp.setReturnCode("0");
			resp.setReturnMessage("服务器开了个小差，请稍后重试!");
		}
		return resp.toString();
	}
	/**
	 * 删除任务信息
	 * @param request
	 * @param taskvos
	 * @return
	 */
	@RequestMapping(value = "deleteJarTasks",method = RequestMethod.POST)
	public String deleteJarTasks(HttpServletRequest request, @RequestBody List<SubmitTaskJarVO> taskvos) {
		BaseResponse resp = new BaseResponse();
		Map<String,Object> retMap = new HashMap<>();
		try {
			retMap = taskSubmitServiceImpl.deleteJarTasks(request,taskvos);
			resp.setReturnCode("1");
			resp.setReturnObject(retMap);
		} catch (Exception e) {
			logger.error("删除jar任务报错，错误信息为：{}",e);
			resp.setReturnCode("0");
			resp.setReturnMessage("服务器开了个小差，请稍后重试!");
		}
		return resp.toString();
	}
	
	/**
	 * 启动任务接口
	 * @param request
	 * @param latestTaskEntity
	 * @return
	 */
	@RequestMapping(value = "execJartasks",method = RequestMethod.POST)
	public String execJartasks(HttpServletRequest request,@RequestBody LatestTaskEntity latestTaskEntity) {
		BaseResponse resp = new BaseResponse();
		try {
			Map<String, Object> stringObjectMap = taskSubmitServiceImpl.execJartasks(request, latestTaskEntity);
			resp.setReturnCode("1");
			resp.setReturnObject(stringObjectMap);
		} catch (Exception e) {
			logger.error("启动jar任务报错，错误信息为：{}",e);
			resp.setReturnCode("0");
			resp.setReturnMessage("服务器开了个小差，请稍后重试!");
		}
		return resp.toString();
	}

	@RequestMapping(value = "updateJarTask",method = RequestMethod.POST)
	public String updateJarTask(HttpServletRequest request,@RequestBody SubmitTaskJarVO taskJarVO){
		BaseResponse resp = new BaseResponse();
		Map<String,Object> retMap = new HashMap<>();
		try {
			retMap = taskSubmitServiceImpl.updateJarTask(request,taskJarVO);
			resp.setReturnObject(retMap);
			resp.setReturnCode("1");
		}catch (Exception e){
			logger.error("update jarTask error",e);
			resp.setReturnCode("0");
			resp.setReturnMessage("服务器开了个小差，请稍后重试!");
		}
		return resp.toString();
	}

	@RequestMapping(value = "setParamsJarTask",method = RequestMethod.POST)
	public String setParamsJarTask(HttpServletRequest request,@RequestBody SubmitTaskJarVO taskJarVO){
		BaseResponse resp = new BaseResponse();
		Map<String,Object> retMap = new HashMap<>();
		try {
			retMap = taskSubmitServiceImpl.setParamsJarTask(taskJarVO);
			taskSubmitServiceImpl.execJartasks(request, taskJarVO.getLatestTaskEntity());
			resp.setReturnObject(retMap);
			resp.setReturnCode("1");
		}catch (Exception e){
			logger.error("setParams jarTask error",e);
			resp.setReturnCode("0");
			resp.setReturnMessage("服务器开了个小差，请稍后重试!");
		}
		return resp.toString();
	}
}

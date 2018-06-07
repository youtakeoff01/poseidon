package com.hand.bdss.web.operationcenter.console.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.operationcenter.console.service.ConsoleService;
import com.hand.bdss.web.operationcenter.console.vo.TaskExecVO;
import com.hand.bdss.web.operationcenter.console.vo.TaskQuantityVO;

import parquet.org.slf4j.*;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.bdss.web.common.vo.BaseResponse;

/**
 * 控制台的controller
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = "/consoleController/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ConsoleController {
	
	private Logger logger = LoggerFactory.getLogger(ConsoleController.class);
	
	@Resource
	private ConsoleService consoleServiceImpl;
	
	/**
	 * 获取任务执行情况
	 */
	@RequestMapping("getTaskExec")
	public @ResponseBody String getTaskExec(HttpServletRequest request){
		logger.info("consoleController.getTaskExec start!");
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		try{
			List list = consoleServiceImpl.getTaskExec();
			if(list == null || list.size() == 0){
				logger.error("consoleController.getTaskExec failure!The data is empty!");
				base.setReturnMessage("没有数据");
			}else{
				base.setReturnCode("1");
				base.setReturnMessage("获取任务运行情况成功");
				base.setReturnObject(list);
			}
		}catch(Exception e){
			logger.error("consoleController.getTaskExec error",e.getMessage());
			base.setReturnMessage("获取任务运行情况失败");
			return base.toString();
		}
		logger.info("consoleController.getTaskExec end!");
		return base.toString();
	}
	
	/**
	 * 获取执行时间排序
	 * @param request
	 * @return
	 */
	@RequestMapping("getExecTime")
	public @ResponseBody String getExecTime(HttpServletRequest request){
		logger.info("consoleController.getExecTime start!");
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		try{
			List<TaskExecVO> list = consoleServiceImpl.getExecTime();
			if(list == null || list.size() == 0){
				logger.info("consoleController.getExecTime failure!The data is empty!");
				base.setReturnMessage("没有数据");
			}else{
				base.setReturnCode("1");
				base.setReturnMessage("获取执行时间成功");
				base.setReturnObject(list);
			}
		}catch(Exception e){
			logger.error("consoleController.getExecTime error",e.getMessage());
			base.setReturnMessage("获取执行时间失败");
			return base.toString();
		}
		logger.info("consoleController.getExecTime end!");
		return base.toString();
	}
	
	
	/**
	 * 获取失败次数
	 * @param request
	 * @return
	 */
	@RequestMapping("getErrorCount")
	public @ResponseBody String getErrorCount(HttpServletRequest request){
		logger.info("consoleController.getErrorCount start!");
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		try{
			List<TaskExecVO> list = consoleServiceImpl.getErrorCount();
			if(list == null || list.size() == 0){
				logger.info("consoleController.getErrorCount failure!The data is empty!");
				base.setReturnMessage("没有数据");
			}else{
				base.setReturnCode("1");
				base.setReturnMessage("获取执行失败次数成功");
				base.setReturnObject(list);
			}
		}catch(Exception e){
			logger.error("consoleController.getErrorCount error",e.getMessage());
			base.setReturnMessage("获取执行失败次数失败");
			return base.toString();
		}
		logger.info("consoleController.getErrorCount end!");
		return base.toString();
	}
	
	/**
	 * 获取当天任务量
	 * @param request
	 * @return
	 */
	@RequestMapping("getTaskCount")
	public @ResponseBody String getTaskCount(HttpServletRequest request){
		logger.info("consoleController.getTaskCount start!");
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		String taskType = request.getParameter("taskType");
		try{
			List<TaskQuantityVO> list = consoleServiceImpl.getTaskCount(taskType);
			if(list == null || list.size() == 0){
				logger.info("consoleController.getTaskCount failure!The data is empty!");
				base.setReturnMessage("没有数据");
			}else{
				base.setReturnCode("1");
				base.setReturnMessage("获取当天任务成功");
				base.setReturnObject(list);
			}
		}catch(Exception e){
			logger.error("consoleController.getTaskCount error",e.getMessage());
			base.setReturnMessage("获取当天任务失败");
			return base.toString();
		}
		logger.info("consoleController.getTaskCount end!");
		return base.toString();
	}
	
}

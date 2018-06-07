package com.hand.bdss.web.report.approve.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.web.common.constant.Global;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.entity.ApplyEntity;
import com.hand.bdss.web.entity.StudentEntity;
import com.hand.bdss.web.entity.UserEntity;
import com.hand.bdss.web.report.approve.service.ApplyService;

@RestController
@RequestMapping(value = "/applyController/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ApplyController {
	private static final Logger logger = LoggerFactory.getLogger(ApplyController.class);
	@Resource
	private ApplyService applyServiceImpl;

	/**
	 * 查询学生信息的接口
	 * 
	 * @param student
	 * @return
	 */
	@RequestMapping(value = "getStuentsMsg", method = RequestMethod.POST)
	public String getStudentsMsg(@RequestBody StudentEntity student) {
		BaseResponse base = new BaseResponse();
		try {
			List<StudentEntity> lists = applyServiceImpl.listStudentsMsg(student);
			base.setReturnCode("1");
			base.setReturnObject(lists);
		} catch (Exception e) {
			logger.error("getStudentsMsg error,error msg is {}", e);
			base.setReturnCode("0");
			base.setReturnMessage("服务器异常");
		}
		return base.toString();
	}

	/**
	 * 获取审批人信息的接口
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "getApprovalersMsg", method = RequestMethod.POST)
	public String getApprovalersMsg(@RequestBody UserEntity user) {
		BaseResponse base = new BaseResponse();
		try {
			List<UserEntity> lists = applyServiceImpl.listTeacherMsgs(user);
			base.setReturnCode("1");
			base.setReturnObject(lists);
		} catch (Exception e) {
			logger.error("getApprovalersMsg error,error msg is {}", e);
			base.setReturnCode("0");
			base.setReturnMessage("服务器异常");
		}
		return base.toString();
	}

	/**
	 * 提交申请接口
	 * 
	 * @param apply
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "submitApply", method = RequestMethod.POST)
	public String submitApply(@RequestBody ApplyEntity apply, HttpServletRequest request) {
		BaseResponse base = new BaseResponse();
		try {
			applyServiceImpl.submitApply(apply, request);
			base.setReturnCode("1");
		} catch (Exception e) {
			logger.error("submitApply error,error msg is {}", e);
			base.setReturnCode("0");
			base.setReturnMessage("服务器异常");
		}
		return base.toString();
	}

	/**
	 * 查询申请信息接口
	 * 
	 * @param json
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "listApplyMsg", method = RequestMethod.POST)
	public String listApplyMsg(@RequestBody String json, HttpServletRequest request) {
		BaseResponse base = new BaseResponse();
		try {
			JSONObject object = JSON.parseObject(json);
			UserEntity userEntity = (UserEntity) request.getSession().getAttribute("userMsg");
			int startPage = object.getIntValue("startPage");
			int count = object.getIntValue("count");
			ApplyEntity apply = JsonUtils.toObject(json, ApplyEntity.class);
			if(!Global.ADMIN_ROLE_NAME.equalsIgnoreCase(userEntity.getRoleName())) {//管理员可以查看所有的申请信息
				apply.setApperyer(userEntity);// 存放申请人信息
			}
			List<ApplyEntity> listApply = applyServiceImpl.listApplyMsg(apply, (startPage - 1) * count, count);
			Map<String, Object> maps = new HashMap<>();
			int countAll = applyServiceImpl.getCountAll(apply);
			maps.put("countAll",countAll);
			maps.put("listApply", listApply);
			base.setReturnCode("1");
			base.setReturnObject(maps);
		} catch (Exception e) {
			logger.error("listApplyMsg error,error msg is {}", e);
			base.setReturnCode("0");
			base.setReturnMessage("服务器异常");
		}
		return base.toString();
	}

}

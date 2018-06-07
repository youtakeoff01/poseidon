package com.hand.bdss.web.report.approve.controller;

import java.util.ArrayList;
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
import com.hand.bdss.web.entity.ApprovalEntity;
import com.hand.bdss.web.entity.UserEntity;
import com.hand.bdss.web.report.approve.service.ApplyService;
import com.hand.bdss.web.report.approve.service.ApprovalService;

/**
 * 审批接口
 * 
 * @author liqifei
 * @DATA 2018年4月23日
 */
@RestController
@RequestMapping(value = "/approvalController/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ApprovalController {

	private static final Logger logger = LoggerFactory.getLogger(ApprovalController.class);
	@Resource
	private ApplyService applyServiceImpl;

	@Resource
	private ApprovalService approvalServiceImpl;

	/**
	 * 查询审批信息
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
			if(!Global.ADMIN_ROLE_NAME.equalsIgnoreCase(userEntity.getRoleName())) {//管理员可以查看所有的审批信息
				List<ApprovalEntity> list = new ArrayList<ApprovalEntity>();
				ApprovalEntity approval = apply.getApprovals().get(0);
				approval.setApprovaler(userEntity);
				list.add(approval);
				apply.setApprovals(list);// 存放审批人信息
			}
			List<ApplyEntity> listApply = applyServiceImpl.listApplyMsg(apply, (startPage - 1) * count, count);
			int countAll = applyServiceImpl.getCountAll(apply);
			Map<String, Object> maps = new HashMap<>();
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

	/**
	 * 更新审批状态接口
	 * 
	 * @param apply
	 * @return
	 */
	@RequestMapping(value = "updateApprovalStatus", method = RequestMethod.POST)
	public String updateApprovalStatus(@RequestBody ApplyEntity apply,HttpServletRequest request) {
		BaseResponse base = new BaseResponse();
		try {
			approvalServiceImpl.updateApprovalStatus(apply,request);
		} catch (Exception e) {
			logger.error("updateApprovalStatus error,error msg is {}", e);
			base.setReturnCode("0");
			base.setReturnMessage("服务器异常");
		}
		return base.toString();
	}

}

package com.hand.bdss.web.report.approve.service;

import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.entity.ApplyEntity;

public interface ApprovalService {
	/**
	 * 更新审批状态
	 * 
	 * @param apply
	 * @param request 
	 */
	void updateApprovalStatus(ApplyEntity apply, HttpServletRequest request) throws Exception;

}

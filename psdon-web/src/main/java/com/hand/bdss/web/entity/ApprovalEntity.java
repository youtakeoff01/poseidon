package com.hand.bdss.web.entity;

import java.io.Serializable;

public class ApprovalEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 111965800985210816L;
	private Long id;
	private UserEntity approvaler;//审批人信息
	private Integer approvalStatus;//审批状态
	private String approvalTime;//审批时间
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public UserEntity getApprovaler() {
		return approvaler;
	}
	public void setApprovaler(UserEntity approvaler) {
		this.approvaler = approvaler;
	}
	
	public Integer getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	public String getApprovalTime() {
		return approvalTime;
	}
	public void setApprovalTime(String approvalTime) {
		this.approvalTime = approvalTime;
	}

}

package com.hand.bdss.web.entity;

import java.io.Serializable;
import java.util.List;

public class ApplyEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2432970838147546409L;
	
	private Long id;
	private UserEntity apperyer;//申请人信息
	private List<StudentEntity> students;//申请要查看的学生信息
	private Integer vld;//有效期（单位是天）
	private String applyReason;//申请原因
	private Integer applyStatus;//申请状态
	private Long createUserId;
	private String createName;//申请人姓名
	private String createDate;//申请时间
	private List<ApprovalEntity> approvals;//审批信息
	private Long updateUserId;
	private String updateName;//最后修改人
	private String updateDate;//最后修改时间
	
	
	public Integer getApplyStatus() {
		return applyStatus;
	}
	public void setApplyStatus(Integer applyStatus) {
		this.applyStatus = applyStatus;
	}
	public Long getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}
	public Long getUpdateUserId() {
		return updateUserId;
	}
	public void setUpdateUserId(Long updateUserId) {
		this.updateUserId = updateUserId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getVld() {
		return vld;
	}
	public void setVld(Integer vld) {
		this.vld = vld;
	}
	public String getApplyReason() {
		return applyReason;
	}
	public void setApplyReason(String applyReason) {
		this.applyReason = applyReason;
	}
	public String getCreateName() {
		return createName;
	}
	public void setCreateName(String createName) {
		this.createName = createName;
	}
	
	public String getUpdateName() {
		return updateName;
	}
	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public UserEntity getApperyer() {
		return apperyer;
	}
	public void setApperyer(UserEntity apperyer) {
		this.apperyer = apperyer;
	}
	public List<StudentEntity> getStudents() {
		return students;
	}
	public void setStudents(List<StudentEntity> students) {
		this.students = students;
	}
	public List<ApprovalEntity> getApprovals() {
		return approvals;
	}
	public void setApprovals(List<ApprovalEntity> approvals) {
		this.approvals = approvals;
	}
	

}

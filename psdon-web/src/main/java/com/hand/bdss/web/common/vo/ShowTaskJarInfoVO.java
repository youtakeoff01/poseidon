package com.hand.bdss.web.common.vo;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class ShowTaskJarInfoVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2266330525796507036L;
	private String id;
	private String taskName;
	private String taskType;
	private String JarName;
	private String JarPath;
	private String taskAttrId;
	private String entryClass;
	private String systemParam;
	private String userParam;
	private String createUser;
	private String timerAttribute;
	private String ruleName;

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getTimerAttribute() {
		return timerAttribute;
	}

	public void setTimerAttribute(String timerAttribute) {
		this.timerAttribute = timerAttribute;
	}

	@JSONField (format="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	private String updateUser;
	@JSONField (format="yyyy-MM-dd HH:mm:ss")  
	private Date updateTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTaskAttrId() {
		return taskAttrId;
	}
	public void setTaskAttrId(String taskAttrId) {
		this.taskAttrId = taskAttrId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public String getJarName() {
		return JarName;
	}
	public void setJarName(String jarName) {
		JarName = jarName;
	}
	public String getJarPath() {
		return JarPath;
	}
	public void setJarPath(String jarPath) {
		JarPath = jarPath;
	}
	public String getEntryClass() {
		return entryClass;
	}
	public void setEntryClass(String entryClass) {
		this.entryClass = entryClass;
	}
	public String getSystemParam() {
		return systemParam;
	}
	public void setSystemParam(String systemParam) {
		this.systemParam = systemParam;
	}
	public String getUserParam() {
		return userParam;
	}
	public void setUserParam(String userParam) {
		this.userParam = userParam;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	

}

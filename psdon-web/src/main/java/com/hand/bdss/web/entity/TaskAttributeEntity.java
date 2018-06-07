package com.hand.bdss.web.entity;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;


/**
 * The persistent class for the tb_task_attribute database table.
 * 
 */
public class TaskAttributeEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String entryClass;
	private long jarInfoId;
	private String systemParam;
	private String userParam;
	@JSONField (format="yyyy-MM-dd HH:mm:ss")  
	private Date createTime;
	private String createUser;
	@JSONField (format="yyyy-MM-dd HH:mm:ss")  
	private Date updateTime;
	private String updateUser;

	public TaskAttributeEntity() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getEntryClass() {
		return this.entryClass;
	}

	public void setEntryClass(String entryClass) {
		this.entryClass = entryClass;
	}

	

	public long getJarInfoId() {
		return jarInfoId;
	}

	public void setJarInfoId(long jarInfoId) {
		this.jarInfoId = jarInfoId;
	}

	public String getSystemParam() {
		return this.systemParam;
	}

	public void setSystemParam(String systemParam) {
		this.systemParam = systemParam;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUser() {
		return this.updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getUserParam() {
		return this.userParam;
	}

	public void setUserParam(String userParam) {
		this.userParam = userParam;
	}

}
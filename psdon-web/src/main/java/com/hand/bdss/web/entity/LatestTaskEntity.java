package com.hand.bdss.web.entity;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;


/**
 * The persistent class for the tb_latest_task database table.
 * 
 */
public class LatestTaskEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private long taskAttrId;
	private String taskName;
	private String taskType;//storm,spark,flink
	@JSONField (format="yyyy-MM-dd HH:mm:ss")  
	private Date createTime;
	private String createUser;
	@JSONField (format="yyyy-MM-dd HH:mm:ss")  
	private Date updateTime;
	private String updateUser;

	private String emailStr;//邮件下发列表逗号隔开
	private String timerAttribute;//定时器属性
	private String notificationId;//子那个定义通知规则ID

	public String getEmailStr() {
		return emailStr;
	}

	public void setEmailStr(String emailStr) {
		this.emailStr = emailStr;
	}

	public String getTimerAttribute() {
		return timerAttribute;
	}

	public void setTimerAttribute(String timerAttribute) {
		this.timerAttribute = timerAttribute;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public LatestTaskEntity() {
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


	public String getTaskName() {
		return this.taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskType() {
		return this.taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public long getTaskAttrId() {
		return taskAttrId;
	}


	public void setTaskAttrId(long taskAttrId) {
		this.taskAttrId = taskAttrId;
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

}
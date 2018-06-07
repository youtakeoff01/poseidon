package com.hand.bdss.web.entity;

import java.io.Serializable;

/**
 * 机器学习 实验中间表实体类
 * 
 * @author wangyong
 * @date 2017-01-24
 * @version v1.0
 */
public class AIIntermedia implements Serializable {

	private static final long serialVersionUID = 2893486238537650549L;
	
	private String userName;
	private String taskName;
	private String tableName;
	private String comment;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
}

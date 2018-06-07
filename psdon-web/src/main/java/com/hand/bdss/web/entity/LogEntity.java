package com.hand.bdss.web.entity;

import java.sql.Timestamp;

/**
 * 系统日志实体类
 * 
 * @author hand
 *
 */
public class LogEntity implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id; // 主键ID
	private String logType; // 日志类型
	private String logContent; // 日志内容
	private String userName;// 操作人的姓名
	private Timestamp createTime; // 创建时间
	private String status;//日志返回状态

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getLogContent() {
		return logContent;
	}

	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}


	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}

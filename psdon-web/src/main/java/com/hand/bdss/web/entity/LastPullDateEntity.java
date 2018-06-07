package com.hand.bdss.web.entity;

import java.util.Date;

public class LastPullDateEntity {
	private int id; //表ID
	private String serverName;//对接的服务名称
	private Date lastPullTime;//最后拉取时间
	private String createUser;//创建用户
	private Date createTime;//创建时间
	private String updateUser;//更新用户
	private Date updateTime;//更新时间
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public Date getLastPullTime() {
		return lastPullTime;
	}
	public void setLastPullTime(Date lastPullTime) {
		this.lastPullTime = lastPullTime;
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

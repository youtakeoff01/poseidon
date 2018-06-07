package com.hand.bdss.web.entity;

import java.io.Serializable;

public class RangerEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private int id;
	
	/**name
	 * 服务名 
	 */
	private String serviceName;
	
	/**
	 * 策略名
	 */
	private String name;
	
	/**
	 * 用户列表
	 */
	private String user;
	
	/**
	 * 数据库列表
	 */
	private String rangerDatabases;
	
	/**
	 * 表集合
	 */
	private String rangerTables;
	
	/**
	 * 列
	 */
	private String rangerColumns;
	
	/**
	 * 用户角色
	 */
	private String role;
	
	private String type;
	private String createTime;
	private String updateTimee;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getRangerDatabases() {
		return rangerDatabases;
	}
	public void setRangerDatabases(String rangerDatabases) {
		this.rangerDatabases = rangerDatabases;
	}
	public String getRangerTables() {
		return rangerTables;
	}
	public void setRangerTables(String rangerTables) {
		this.rangerTables = rangerTables;
	}
	public String getRangerColumns() {
		return rangerColumns;
	}
	public void setRangerColumns(String rangerColumns) {
		this.rangerColumns = rangerColumns;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpdateTimee() {
		return updateTimee;
	}
	public void setUpdateTimee(String updateTimee) {
		this.updateTimee = updateTimee;
	}
	
}

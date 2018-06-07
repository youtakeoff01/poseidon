/**
 * 
 */
package com.hand.bdss.dsmp.model;

import java.io.Serializable;
import java.util.List;

/**
 * 策略定义
 * @author hand
 *
 */
public class HivePolicy implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2492373186305040883L;

	/**
	 * id
	 */
	private Long id;
	
	/**
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
	private List<String> user;
	
	/**
	 * 数据库列表
	 */
	private List<String> databases;
	
	/**
	 * 表集合
	 */
	private List<String> tables;
	
	/**
	 * 列
	 */
	private List<String> columns;
	
	/**
	 * 用户角色
	 */
	private List<String> role;
	
	private List<String> type;
	
	private List<TableTypeEntity> tablesType;
	
	
	public List<String> getType() {
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<String> getDatabases() {
		return databases;
	}

	public void setDatabases(List<String> databases) {
		this.databases = databases;
	}

	public List<String> getTables() {
		return tables;
	}

	public void setTables(List<String> tables) {
		this.tables = tables;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public List<String> getUser() {
		return user;
	}

	public void setUser(List<String> user) {
		this.user = user;
	}
	
	public List<String> getRole() {
		return role;
	}
	public void setRole(List<String> role) {
		this.role = role;
	}

	public List<TableTypeEntity> getTablesType() {
		return tablesType;
	}

	public void setTablesType(List<TableTypeEntity> tablesType) {
		this.tablesType = tablesType;
	}

}

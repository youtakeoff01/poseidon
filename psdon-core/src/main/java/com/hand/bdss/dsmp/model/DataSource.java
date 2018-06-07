package com.hand.bdss.dsmp.model;

import java.io.Serializable;

/**
 * 数据源类
 * 
 * @author William
 *
 */
public class DataSource implements Serializable {

	private static final long serialVersionUID = -5970060816291506771L;

	private Long id; // 数据源ID
	private String dbUrl; // 数据库地址
	private String filePath; // HDFS文件全路径 如果数据类型为FILE则取该字段
	private String dbName; //数据库名
	private String dbUser; // 数据库用户名
	private String dbPwd; // 数据库密码
	private String dbDriver; // 驱动类
	private String tableName; // SQL查询语句
	private String queryField; // SQL查询语句
	private String queryCondition; // SQL查询语句

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPwd() {
		return dbPwd;
	}

	public void setDbPwd(String dbPwd) {
		this.dbPwd = dbPwd;
	}

	public String getDbDriver() {
		return dbDriver;
	}

	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getQueryField() {
		return queryField;
	}

	public void setQueryField(String queryField) {
		this.queryField = queryField;
	}

	public String getQueryCondition() {
		return queryCondition;
	}

	public void setQueryCondition(String queryCondition) {
		this.queryCondition = queryCondition;
	}

	@Override
	public String toString() {
		return "DataSource{" +
				"id=" + id +
				", dbUrl='" + dbUrl + '\'' +
				", filePath='" + filePath + '\'' +
				", dbName='" + dbName + '\'' +
				", dbUser='" + dbUser + '\'' +
				", dbPwd='" + dbPwd + '\'' +
				", dbDriver='" + dbDriver + '\'' +
				", tableName='" + tableName + '\'' +
				", queryField='" + queryField + '\'' +
				", queryCondition='" + queryCondition + '\'' +
				'}';
	}
}

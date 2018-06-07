package com.hand.bdss.web.common.vo;

import java.sql.Timestamp;

/**
 * 数据源配置表 字段相关对象封装
 * 
 * @author hand
 *
 */
public class DBSrcVO implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id; // 主键ID
	private String srcName; // 数据源名称
	private String dbName; // 数据库名称
	private String dbUrl; // 数据库链接地址
	private String dbUser; // 数据库用户名
	private String dbPwd; // 数据库密码
	private String dbType; // 数据库类型（oracle/mysql）
	private String dbDriver; // 连接驱动类（类全路径）
	private Timestamp updateTime; // 更新时间
	private long createAccount;//创建人用户id
	private long updateAccount;//更新人用户id
	

	public long getCreateAccount() {
		return createAccount;
	}

	public void setCreateAccount(long createAccount) {
		this.createAccount = createAccount;
	}

	public void setUpdateAccount(long updateAccount) {
		this.updateAccount = updateAccount;
	}


	public long getUpdateAccount() {
		return updateAccount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSrcName() {
		return srcName;
	}

	public void setSrcName(String srcName) {
		this.srcName = srcName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
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

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDbDriver() {
		return dbDriver;
	}

	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

}
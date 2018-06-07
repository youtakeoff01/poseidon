package com.hand.bdss.web.entity;

import java.sql.Timestamp;

public class MetaDataEntity implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String metaName;// 元数据名称
	private String metaType;// 元数据类型
	private String dbName;//数据库名
	private String tableName;// 表名
	private String metaHiveFields;// hive 表的字段信息
	private String metaLocation;// 数据存储全路径(HDFS全路径)
	private int metaLive;// 数据存储时间(-1表示永久)
	private int metaOwner;// 所属用户id
	private String userName;// 所属用户名称
	private String metaDesc;// 描述信息
	private Timestamp createTime;// 数据创建时间
	private Timestamp updateTime;// 更新时间

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "MetaDataEntity [id=" + id + ", metaName=" + metaName + ", metaType=" + metaType + ", metaHiveFields="
				+ metaHiveFields + ", metaLocation=" + metaLocation + ", metaLive=" + metaLive + ", metaOwner="
				+ metaOwner + ", metaDesc=" + metaDesc + ", createTime=" + createTime + ", updateTime=" + updateTime
				+ "]";
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMetaName() {
		return metaName;
	}

	public void setMetaName(String metaName) {
		this.metaName = metaName;
	}

	public String getMetaType() {
		return metaType;
	}

	public void setMetaType(String metaType) {
		this.metaType = metaType;
	}

	public String getMetaHiveFields() {
		return metaHiveFields;
	}

	public void setMetaHiveFields(String metaHiveFields) {
		this.metaHiveFields = metaHiveFields;
	}

	public String getMetaLocation() {
		return metaLocation;
	}

	public void setMetaLocation(String metaLocation) {
		this.metaLocation = metaLocation;
	}

	public int getMetaLive() {
		return metaLive;
	}

	public void setMetaLive(int metaLive) {
		this.metaLive = metaLive;
	}

	public int getMetaOwner() {
		return metaOwner;
	}

	public void setMetaOwner(int metaOwner) {
		this.metaOwner = metaOwner;
	}

	public String getMetaDesc() {
		return metaDesc;
	}

	public void setMetaDesc(String metaDesc) {
		this.metaDesc = metaDesc;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
}

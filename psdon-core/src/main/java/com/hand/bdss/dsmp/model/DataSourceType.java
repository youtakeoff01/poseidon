package com.hand.bdss.dsmp.model;

/**
 * 数据源类型定义
 * 
 * @author William
 *
 */
public enum DataSourceType {

	MYSQL(1, "MySQL"), ORACLE(2, "Oracle"), SQLSERVER(3, "SQLServer"),POSTGRESQL(4,"PostgreSQL"),HIVE(5,"Hive"),DB2(6,"DB2");

	private Integer id;
	private String type;

	private DataSourceType(Integer id, String type) {
		this.id = id;
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return String.valueOf(this.type);
	}

}

package com.hand.bdss.dsmp.util;

import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;


public class HiveDatabaseSupport {
    /**
     * 查询获取数据源表结构
     * @param hiveDB
     * @param tableName
     * @return
     */
	public String getSelectOne(String hiveDB, String tableName) {
		return "select * from " + hiveDB + "." + tableName + " limit 1";
	}

	/**
	 * 将hive数据仓库表结构字段映射到mysql数据库表字段
	 *
	 * @param columnName
	 * @param columnType
	 * @return
	 */
	public String toMysqlColumn(String columnName, String columnType) {
		if (equalsAnyIgnoreCase(columnType, "TINYINT")) {
			return columnName + " TINYINT";
		}
		if (equalsAnyIgnoreCase(columnType, "SMALLINT")) {
			return columnName + " SMALLINT";
		}
		if (equalsAnyIgnoreCase(columnType, "INT")) {
			return columnName + " INT";
		}
		if (equalsAnyIgnoreCase(columnType, "BIGINT")) {
			return columnName + " BIGINT";
		}
		if (equalsAnyIgnoreCase(columnType, "BOOLEAN")) {
			return columnName + " BOOLEAN";
		}
		if (equalsAnyIgnoreCase(columnType, "FLOAT")) {
			return columnName + " FLOAT";
		}
		if (equalsAnyIgnoreCase(columnType, "DOUBLE")) {
			return columnName + " DOUBLE";
		}
		if (equalsAnyIgnoreCase(columnType, "BINARY")) {
			return columnName + " BINARY";
		}
		if (equalsAnyIgnoreCase(columnType, "TIMESTAMP")) {
			return columnName + " TIMESTAMP";
		}
		if (equalsAnyIgnoreCase(columnType, "DECIMAL")) {
			return columnName + " DECIMAL";
		}
		if (equalsAnyIgnoreCase(columnType, "CHAR")) {
			return columnName + " CHAR";
		}
		if (equalsAnyIgnoreCase(columnType, "VARCHAR", "STRING")) {
			return columnName + " VARCHAR(1000)";
		}
		if (equalsAnyIgnoreCase(columnType, "DATE")) {
			return columnName + " DATE";
		}
		throw new RuntimeException("未知的字段类型: " + columnType);
	}

	/**
	 * 将hive数据仓库表结构字段映射到oracle数据库表字段
	 *
	 * @param columnName
	 * @param columnType
	 * @return
	 */
	public String toOracleColumn(String columnName, String columnType) {
		if (equalsAnyIgnoreCase(columnType, "TINYINT", "SMALLINT", "INT")) {
			return columnName + " NUMBER";
		}
		if (equalsAnyIgnoreCase(columnType, "BIGINT")) {
			return columnName + " INTEGER";
		}
		if (equalsAnyIgnoreCase(columnType, "FLOAT")) {
			return columnName + " FLOAT";
		}
		if (equalsAnyIgnoreCase(columnType, "DOUBLE")) {
			return columnName + " BINARY_DOUBLE";
		}
		if (equalsAnyIgnoreCase(columnType, "BINARY")) {
			return columnName + " BLOB";
		}
		if (equalsAnyIgnoreCase(columnType, "TIMESTAMP")) {
			return columnName + " TIMESTAMP";
		}
		if (equalsAnyIgnoreCase(columnType, "DECIMAL")) {
			return columnName + " DECIMAL";
		}
		if (equalsAnyIgnoreCase(columnType, "CHAR")) {
			return columnName + " CHAR";
		}
		if (equalsAnyIgnoreCase(columnType, "VARCHAR", "STRING")) {
			return columnName + " VARCHAR(1000)";
		}
		if (equalsAnyIgnoreCase(columnType, "BOOLEAN")) {
			return columnName + " VARCHAR(5)";
		}
		if (equalsAnyIgnoreCase(columnType, "DATE")) {
			return columnName + " DATE";
		}
		throw new RuntimeException("未知的字段类型: " + columnType);
	}

	/**
	 * 将hive数据仓库表结构字段映射到sqlserver数据库表字段
	 *
	 * @param columnName
	 * @param columnType
	 * @return
	 */
	public String toSQLServerColumn(String columnName, String columnType) {
		if (equalsAnyIgnoreCase(columnType, "TINYINT")) {
			return columnName + " TINYINT";
		}
		if (equalsAnyIgnoreCase(columnType, "SMALLINT")) {
			return columnName + " SMALLINT";
		}
		if (equalsAnyIgnoreCase(columnType, "INT")) {
			return columnName + " INT";
		}
		if (equalsAnyIgnoreCase(columnType, "BIGINT")) {
			return columnName + " BIGINT";
		}
		if (equalsAnyIgnoreCase(columnType, "BOOLEAN")) {
			return columnName + " VARCHAR(5)";
		}
		if (equalsAnyIgnoreCase(columnType, "FLOAT")) {
			return columnName + " FLOAT";
		}
		if (equalsAnyIgnoreCase(columnType, "DECIMAL", "DOUBLE")) {
			return columnName + " DECIMAL";
		}
		if (equalsAnyIgnoreCase(columnType, "BINARY")) {
			return columnName + " BINARY";
		}
		if (equalsAnyIgnoreCase(columnType, "TIMESTAMP")) {
			return columnName + " TIMESTAMP";
		}
		if (equalsAnyIgnoreCase(columnType, "CHAR")) {
			return columnName + " CHAR";
		}
		if (equalsAnyIgnoreCase(columnType, "VARCHAR", "STRING")) {
			return columnName + " VARCHAR(1000)";
		}
		if (equalsAnyIgnoreCase(columnType, "DATE")) {
			return columnName + " DATE";
		}
		throw new RuntimeException("未知的字段类型: " + columnType);
	}
	
	/**
	 * 将hive数据仓库表结构字段映射到DB2数据库表字段
	 *
	 * @param columnName
	 * @param columnType
	 * @return
	 */
	public String toDB2Column(String columnName, String columnType) {
		if (equalsAnyIgnoreCase(columnType, "TINYINT")) {
			return columnName + " SMALLINT";
		}
		if (equalsAnyIgnoreCase(columnType, "SMALLINT")) {
			return columnName + " SMALLINT";
		}
		if (equalsAnyIgnoreCase(columnType, "INT","INTEGER")) {
			return columnName + " INTEGER";
		}
		if (equalsAnyIgnoreCase(columnType, "BIGINT")) {
			return columnName + " BIGINT";
		}
		if (equalsAnyIgnoreCase(columnType, "BOOLEAN")) {
			return columnName + " VARCHAR(5)";
		}
		if (equalsAnyIgnoreCase(columnType, "FLOAT")) {
			return columnName + " FLOAT";
		}
		if (equalsAnyIgnoreCase(columnType, "DOUBLE")) {
			return columnName + " DOUBLE";
		}
		if (equalsAnyIgnoreCase(columnType, "BINARY")) {
			return columnName + " BLOB";
		}
		if (equalsAnyIgnoreCase(columnType, "TIMESTAMP")) {
			return columnName + " TIMESTAMP";
		}
		if (equalsAnyIgnoreCase(columnType, "DECIMAL")) {
			return columnName + " DECIMAL";
		}
		if (equalsAnyIgnoreCase(columnType, "CHAR")) {
			return columnName + " CHAR";
		}
		if (equalsAnyIgnoreCase(columnType, "VARCHAR", "STRING")) {
			return columnName + " VARCHAR(1000)";
		}
		if (equalsAnyIgnoreCase(columnType, "DATE")) {
			return columnName + " DATE";
		}
		throw new RuntimeException("未知的字段类型: " + columnType);
	}

	/**
	 * 将hive数据仓库表结构字段映射到PostgreSQL数据库表字段
	 *
	 * @param columnName
	 * @param columnType
	 * @return
	 */
	public String toPostgreSQLColumn(String columnName, String columnType) {
		if (equalsAnyIgnoreCase(columnType, "TINYINT", "SMALLINT")) {
			return columnName + " SMALLINT";
		}
		if (equalsAnyIgnoreCase(columnType, "INT")) {
			return columnName + " INTEGER";
		}
		if (equalsAnyIgnoreCase(columnType, "BIGINT")) {
			return columnName + " BIGINT";
		}
		if (equalsAnyIgnoreCase(columnType, "BOOLEAN")) {
			return columnName + " BOOLEAN";
		}
		if (equalsAnyIgnoreCase(columnType, "FLOAT")) {
			return columnName + " FLOAT";
		}
		if (equalsAnyIgnoreCase(columnType, "DOUBLE")) {
			return columnName + " DOUBLE PRECISION";
		}
		if (equalsAnyIgnoreCase(columnType, "VARCHAR", "STRING")) {
			return columnName + " VARCHAR(1000)";
		}
		if (equalsAnyIgnoreCase(columnType, "BINARY")) {
			return columnName + " BYTEA";
		}
		if (equalsAnyIgnoreCase(columnType, "TIMESTAMP")) {
			return columnName + " VARCHAR(32)";
		}
		if (equalsAnyIgnoreCase(columnType, "CHAR")) {
			return columnName + " CHAR";
		}
		if (equalsAnyIgnoreCase(columnType, "DATE")) {
			return columnName + " DATE";
		}
		if (equalsAnyIgnoreCase(columnType, "DECIMAL")) {
			return columnName + " DECIMAL";
		}
		throw new RuntimeException("未知的字段类型: " + columnType);
		}

}

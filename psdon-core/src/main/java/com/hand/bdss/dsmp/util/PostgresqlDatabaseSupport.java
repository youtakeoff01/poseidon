package com.hand.bdss.dsmp.util;

import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;

public class PostgresqlDatabaseSupport implements DatabaseSupport {

    /**
     * 查询获取数据源表结构
     * @param dbName
     * @param tableName
     * @param queryField
     * @return
     */
	public String getSelectOne(String dbName,String tableName,String queryField) {
		return "select * from "+tableName+" limit 1 offset 0";
	}

    /**
     * 根据数据源表字段类型 映射目标源表字段
     * @param columnName
     * @param columnType
     * @return
     */
	public String toHiveColumn(String columnName, String columnType) {
		if (equalsAnyIgnoreCase(columnType, "BIT", "BOOL", "BOOLEAN")) {
			return columnName + " " + "BOOLEAN";
		}
		if (equalsAnyIgnoreCase(columnType, "int","int2","int4","TINYINT", "SMALLINT", "INT", "INTEGER", "YEAR", "MEDIUMINT","serial")) {
			return columnName + " " + "INT";
		}
		if (equalsAnyIgnoreCase(columnType, "SMALLINT")) {
			return columnName + " " + "SMALLINT";
		}
		if (equalsAnyIgnoreCase(columnType, "DECIMAL","real")) {
			return columnName + " " + "DECIMAL";
		}
		if (equalsAnyIgnoreCase(columnType, "BIGINT","int8","NUMBER","INTEGER","bigserial")) {
			return columnName + " " + "BIGINT";
		}
		if (equalsAnyIgnoreCase(columnType, "DATE", "DATETIME", "TIMESTAMP", "TIME")) {
			return columnName + " " + "STRING";
		}
		if (equalsAnyIgnoreCase(columnType, "FLOAT","float2","float4","float8","numeric","BINARY_FLOAT","real")) {
			return columnName + " " + "FLOAT";
		}
		if (equalsAnyIgnoreCase(columnType, "DOUBLE","double precision")) {
			return columnName + " " + "DOUBLE";
		}
		if (equalsAnyIgnoreCase(columnType, "TINYBLOB","LONG RAW","RAW","LONG","BFILE","BLOB","NCLOB","BFILE","CLOB","MEDIUMBLOB", "LONGBLOB")) {
			return columnName + " " + "BINARY";
		}
		if (equalsAnyIgnoreCase(columnType,"circle","bpchar","money","CHAR", "VARCHAR", "character","character varying","VARCHAR","VARCHAR2","interval", "BINARY", "VARBINARY", "TINYTEXT",
				"TEXT", "MEDIUMTEXT", "LONGTEXT", "ENUM", "SET")) {
			return columnName + " " + "STRING";
		}
		throw new RuntimeException("未知的字段类型: " + columnType);
	}

    /**
     * 根据数据源表字段类型 映射目标源表字段 日期格式转换
     * @param columnName
     * @param columnType
     * @return
     */
	public String toSqoopColumn(String columnName, String columnType) {
		if (equalsAnyIgnoreCase(columnType, "DATETIME", "TIMESTAMP")) {
			String s = "TO_CHAR(" + columnName + ",\'yyyy-mm-dd hh24:mi:ss\') as " + columnName;
			return s;
		}else if (equalsAnyIgnoreCase(columnType, "interval")) {
			String s = "TO_CHAR(" + columnName + ",\'hh24:mi:ss\') as " + columnName;
			return s;
		} else if (equalsAnyIgnoreCase(columnType, "date")) {
			String s = "TO_CHAR(" + columnName + ",\'yyyy-mm-dd\') as " + columnName;
			return s;
		}else if (equalsAnyIgnoreCase(columnType, "money")) {
			String s = columnName+ "::DECIMAL";
			return s;
		} else if (equalsAnyIgnoreCase(columnType, "circle")) {
			String s = columnName+ "::text";
			return s;
		} else {
			return columnName;
		}
	}

    /**
     * 数值类型校验
     * @param type
     * @return
     */
	public boolean isNumberType(String type) {
		if (equalsAnyIgnoreCase(type, "TINYINT", "SMALLINT", "INT", "INTEGER", "YEAR", "MEDIUMINT")) {
			return true;
		}
		if (equalsAnyIgnoreCase(type, "BIGINT")) {
			return true;
		}
		if (equalsAnyIgnoreCase(type, "FLOAT")) {
			return true;
		}
		if (equalsAnyIgnoreCase(type, "DOUBLE")) {
			return true;
		}
		return false;
	}

	@Override
	public Object toSqoopColumn(String left) {
		return null;
	}

}

package com.hand.bdss.dsmp.util;

import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;

public class OracleDatabaseSupport implements DatabaseSupport {

    /**
     * 查询获取数据源表结构
     * @param dbName
     * @param tableName
     * @param queryField
     * @return
     */
	public String getSelectOne(String dbName,String tableName,String queryField) {
		return "select * from  "+dbName+"."+tableName+" where rownum=1 ";
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
		if (equalsAnyIgnoreCase(columnType, "TINYINT", "SMALLINT", "INT", "INTEGER", "YEAR", "MEDIUMINT")) {
			return columnName + " " + "INT";
		}
		if (equalsAnyIgnoreCase(columnType, "BIGINT","INTEGER")) {
			return columnName + " " + "BIGINT";
		}
		if (equalsAnyIgnoreCase(columnType, "DATE", "DATETIME", "TIMESTAMP", "TIME")) {
			return columnName + " " + "STRING";
		}
		if (equalsAnyIgnoreCase(columnType, "FLOAT","BINARY_FLOAT")) {
			return columnName + " " + "FLOAT";
		}
		if (equalsAnyIgnoreCase(columnType, "DOUBLE","BINARY_DOUBLE")) {
			return columnName + " " + "DOUBLE";
		}
		if (equalsAnyIgnoreCase(columnType, "TINYBLOB","LONG RAW","RAW","LONG","BFILE","BLOB","NCLOB","BFILE","CLOB","MEDIUMBLOB", "LONGBLOB")) {
			return columnName + " " + "BINARY";
		}
		if (equalsAnyIgnoreCase(columnType, "DECIMAL", "DEC", "CHAR", "VARCHAR", "NCHAR","NCHAR2","VARCHAR","VARCHAR2","NVARCHAR2", "BINARY", "VARBINARY", "TINYTEXT",
				"TEXT", "MEDIUMTEXT", "LONGTEXT", "ENUM", "NUMBER","SET")) {
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
		if (equalsAnyIgnoreCase(columnType, "DATE", "DATETIME", "TIMESTAMP")) {
			String s = "TO_CHAR(" + columnName + ",\'yyyy-mm-dd hh24:mi:ss\') as " + columnName;
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

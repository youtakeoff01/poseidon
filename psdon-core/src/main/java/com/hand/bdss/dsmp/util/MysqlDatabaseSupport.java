package com.hand.bdss.dsmp.util;

import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;


public class MysqlDatabaseSupport implements DatabaseSupport {

    /**
     * 查询获取数据源表结构
     * @param dbName
     * @param tableName
     * @param queryField
     * @return
     */
	public String getSelectOne(String dbName,String tableName,String queryField) {
		return "select * from " + dbName + "." +tableName +" limit 1";
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
		if (equalsAnyIgnoreCase(columnType, "SMALLINT", "INT", "INTEGER", "YEAR", "MEDIUMINT")) {
			return columnName + " " + "INT";
		}
        if (equalsAnyIgnoreCase(columnType, "TINYINT")) {
            return columnName + " " + "TINYINT";
        }
		if (equalsAnyIgnoreCase(columnType, "TINYINT UNSIGNED", "SMALLINT UNSIGNED", "INT UNSIGNED", "INTEGER UNSIGNED", "MEDIUMINT UNSIGNED")) {
			return columnName + " " + "INT";
		}
		if (equalsAnyIgnoreCase(columnType, "TINYINT ZEROFILL", "SMALLINT ZEROFILL", "INT ZEROFILL", "INTEGER ZEROFILL", "MEDIUMINT ZEROFILL")) {
			return columnName + " " + "INT";
		}
		if (equalsAnyIgnoreCase(columnType, "NUMBER","BIGINT","INTEGER", "BIGINT UNSIGNED", "BIGINT ZEROFILL")) {
			return columnName + " " + "BIGINT";
		}
		if (equalsAnyIgnoreCase(columnType, "DATE", "DATETIME", "TIMESTAMP", "TIME")) {
			return columnName + " " + "STRING";
		}
		if (equalsAnyIgnoreCase(columnType, "FLOAT","BINARY_FLOAT", "FLOAT UNSIGNED", "FLOAT ZEROFILL")) {
			return columnName + " " + "FLOAT";
		}
		if (equalsAnyIgnoreCase(columnType, "DOUBLE", "BINARY_DOUBLE","DOUBLE UNSIGNED", "DOUBLE ZEROFILL", "DOUBLE PRECISION", "DOUBLE PRECISION UNSIGNED",
				"DOUBLE PRECISION ZEROFILL", "REAL", "REAL UNSIGNED", "REAL ZEROFILL")) {
			return columnName + " " + "DOUBLE";
		}
		if (equalsAnyIgnoreCase(columnType, "BFILE","BLOB","NCLOB","TINYBLOB", "MEDIUMBLOB", "LONGBLOB")) {
			return columnName + " " + "BINARY";
		}
		if (equalsAnyIgnoreCase(columnType, "STRING","DECIMAL", "DEC","VARCHAR", "CHAR", "BINARY", "VARBINARY", "TINYTEXT",
				"TEXT", "MEDIUMTEXT", "LONGTEXT", "ENUM", "SET", "DECIMAL UNSIGNED", "DECIMAL ZEROFILL", "DEC UNSIGNED",
				"DEC ZEROFILL", "NUMERIC", "NUMERIC UNSIGNED", "NUMERIC ZEROFILL", "FIXED", "FIXED UNSIGNED", "FIXED ZEROFILL")) {
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
		if (equalsAnyIgnoreCase(columnType, "DATE")) {
			String s = "DATE_FORMAT(" + columnName + ",'%Y-%m-%d') as " + columnName;
			return s;
		} else if(equalsAnyIgnoreCase(columnType, "DATETIME", "TIMESTAMP")){
			String s = "DATE_FORMAT(" + columnName + ",'%Y-%m-%d %H:%i:%s') as " + columnName;
			return s;
		}else {
			return columnName;
		}
	}
	/**
     * 根据数据源表字段类型 映射目标源表字段 日期格式转换
     * @param columnName
     * @param columnType
     * @return
     */
	public String toSqoopColumn(String columnName) {
		return "FROM_UNIXTIME("+columnName+",'%Y-%m-%d %H:%i:%s') as " + columnName;
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
		if (equalsAnyIgnoreCase(type, "TINYINT UNSIGNED", "SMALLINT UNSIGNED", "INT UNSIGNED", "INTEGER UNSIGNED", "MEDIUMINT UNSIGNED")) {
			return true;
		}
		if (equalsAnyIgnoreCase(type, "TINYINT ZEROFILL", "SMALLINT ZEROFILL", "INT ZEROFILL", "INTEGER ZEROFILL", "MEDIUMINT ZEROFILL")) {
			return true;
		}
		if (equalsAnyIgnoreCase(type, "BIGINT", "BIGINT UNSIGNED","LONG", "BIGINT ZEROFILL")) {
			return true;
		}
		if (equalsAnyIgnoreCase(type, "FLOAT", "FLOAT UNSIGNED", "FLOAT ZEROFILL")) {
			return true;
		}
		if (equalsAnyIgnoreCase(type, "DOUBLE", "DOUBLE UNSIGNED", "DOUBLE ZEROFILL", "DOUBLE PRECISION", "DOUBLE PRECISION UNSIGNED",
				"DOUBLE PRECISION ZEROFILL", "REAL", "REAL UNSIGNED", "REAL ZEROFILL")) {
			return true;
		}
		return false;
	}

}

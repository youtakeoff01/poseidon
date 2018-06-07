package com.hand.bdss.dsmp.util;

import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;

public class DB2DatabaseSupport implements DatabaseSupport{

    /**
     * 查询获取数据源表结构
     * @param dbName
     * @param tableName
     * @param queryField
     * @return
     */
	public String getSelectOne(String dbName, String tableName, String queryField) {
		return "select * from " + tableName + " fetch first 1 rows only";
	}

    /**
     * 根据数据源表字段类型 映射目标源表字段
     * @param columnName
     * @param columnType
     * @return
     */
	public String toHiveColumn(String columnName, String columnType) {
		if (equalsAnyIgnoreCase(columnType, "CHAR", "VARCHAR", "LONG VARCHAR", "GRAPHICS", "VARGRAPHICS",
				"LONG VARGRAPHICS", "NUMERIC", "DECIMAL", "DATE", "TIME")) {
			return columnName + " " + "STRING";
		}
		if (equalsAnyIgnoreCase(columnType, "BOOLEAN")) {
			return columnName + " " + "BOOLEAN";
		}
		if (equalsAnyIgnoreCase(columnType, "SMALLINT", "INT", "INTEGER", "BIGINT")) {
			return columnName + " " + "INT";
		}
		if (equalsAnyIgnoreCase(columnType, "SMALLINT")) {
            return columnName + " " + "TINYINT";
        }
		if (equalsAnyIgnoreCase(columnType, "FLOAT", "REAL")) {
			return columnName + " " + "FLOAT";
		}
		if (equalsAnyIgnoreCase(columnType, "DOUBLE")) {
			return columnName + " " + "DOUBLE";
		}
		if (equalsAnyIgnoreCase(columnType,"BLOB")) {
			return columnName + " " + "BINARY";
		}
		if (equalsAnyIgnoreCase(columnType, "TIMESTAMP")) {
			return columnName + " " + "TIMESTAMP";
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
			String s = "CHAR(" + columnName + ",ISO) as " + columnName;
			return s;
		} else if(equalsAnyIgnoreCase(columnType, "TIMESTAMP")){
			String s = "TO_CHAR(" + columnName + ",\'yyyy-mm-dd hh24:mi:ss\') as " + columnName;
			return s;
		}else {
			return columnName;
		}
	}

    /**
     * 数值类型校验
     * @param type
     * @return
     */
	public boolean isNumberType(String type) {
		if (equalsAnyIgnoreCase(type, "SMALLINT", "INT", "INTEGER", "BIGINT")) {
			return true;
		}
		if(equalsAnyIgnoreCase(type, "DOUBLE", "FLOAT", "REAL")){
			return true;
		}
		return false;
	}

	@Override
	public Object toSqoopColumn(String left) {
		return null;
	}

}

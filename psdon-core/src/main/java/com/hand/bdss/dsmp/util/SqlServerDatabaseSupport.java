package com.hand.bdss.dsmp.util;

import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;

public class SqlServerDatabaseSupport implements DatabaseSupport {

    /**
     * 查询获取数据源表结构
     * @param dbName
     * @param tableName
     * @param queryField
     * @return
     */
	public String getSelectOne(String dbName,String tableName, String queryField) {
		return "select top 1 * from " +tableName;
	}

    /**
     * 根据数据源表字段类型 映射目标源表字段
     * @param columnName
     * @param columnType
     * @return
     */
	public String toHiveColumn(String columnName, String columnType) {
		if (equalsAnyIgnoreCase(columnType, "ntext", "text", "nvarchar", "nchar", "bit", "binary", "varbinary", "image",
				"xml", "varchar", "datetime","date", "datetime2", "smalldatetime", "time", "datetimeoffset", "char")) {
			return columnName + " " + "STRING";
		}
		if (equalsAnyIgnoreCase(columnType, "tinyint")) {
			return columnName + " " + "TINYINT";
		}
		if (equalsAnyIgnoreCase(columnType, "smallint")) {
			return columnName + " " + "SMALLINT";
		}
		if (equalsAnyIgnoreCase(columnType, "int")) {
			return columnName + " " + "INT";
		}
		if (equalsAnyIgnoreCase(columnType, "bigint")) {
			return columnName + " " + "BIGINT";
		}
		if (equalsAnyIgnoreCase(columnType, "float")) {
			return columnName + " " + "FLOAT";
		}
		if (equalsAnyIgnoreCase(columnType, "decimal", "numeric", "smallmoney", "money", "real")) {
			return columnName + " " + "STRING";
		}
		if (equalsAnyIgnoreCase(columnType, "timestamp")) {
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
		if (equalsAnyIgnoreCase(columnType, "datetime","date", "datetime2", "smalldatetime", "time", "datetimeoffset")) {
			String s = "CONVERT(varchar(19)," + columnName + ",120) as " + columnName;
			// String s = "CAST(" + columnName + " as varchar ) as " + columnName;
			return s;
		} else if(equalsAnyIgnoreCase(columnName, "Browse")){
			return "["+columnName+"]";
		}else{
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

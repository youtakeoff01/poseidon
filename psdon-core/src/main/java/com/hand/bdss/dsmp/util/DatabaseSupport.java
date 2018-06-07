package com.hand.bdss.dsmp.util;

public interface DatabaseSupport {

	String toHiveColumn(String columnName, String columnType);

	String toSqoopColumn(String columnName, String columnType);

	boolean isNumberType(String type);

	String getSelectOne(String dbName, String tableName, String queryField);

	Object toSqoopColumn(String left);

}

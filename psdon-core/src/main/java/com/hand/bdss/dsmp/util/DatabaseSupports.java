package com.hand.bdss.dsmp.util;


public class DatabaseSupports {

	public static DatabaseSupport getDatabaseSupport(String jdbcUrl) {
		if (jdbcUrl.contains("mysql")) {
			return mySqlDatabaseSupport;
		}
		if (jdbcUrl.contains("oracle")) {
			return oracleDatabaseSupport;
		}
		if (jdbcUrl.contains("sqlserver")) {
			return sqlServerDatabaseSupport;
		}
		if (jdbcUrl.contains("postgresql")) {
			return postgreSqlDatabaseSupport;
		}
		if (jdbcUrl.contains("db2")) {
			return db2DatabaseSupport;
		}
		throw new RuntimeException("不支持的数据库类型: " + jdbcUrl);
	}

	private static DatabaseSupport mySqlDatabaseSupport = new MysqlDatabaseSupport();

	private static DatabaseSupport oracleDatabaseSupport = new OracleDatabaseSupport();
	
	private static DatabaseSupport sqlServerDatabaseSupport = new SqlServerDatabaseSupport();
	
	private static DatabaseSupport postgreSqlDatabaseSupport = new PostgresqlDatabaseSupport();
	
	private static DatabaseSupport db2DatabaseSupport = new DB2DatabaseSupport();

}

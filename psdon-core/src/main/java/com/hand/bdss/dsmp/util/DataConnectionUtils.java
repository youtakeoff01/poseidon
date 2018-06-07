package com.hand.bdss.dsmp.util;

import com.hand.bdss.dsmp.component.hive.HiveClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

public class DataConnectionUtils {

	private static Connection conn = null;
	private static Statement stmt = null;
	private static final Logger logger = LoggerFactory.getLogger(HiveClient.class);

	public static Connection getConnection(String dbType,String dbUrl,String user,String password) throws Exception {
		String driver = null;
		if ("oracle".equalsIgnoreCase(dbType)) {
			driver = "oracle.jdbc.driver.OracleDriver";
			if(!dbUrl.contains("1521")){
				throw new Exception();
			}
		} else if ("mysql".equalsIgnoreCase(dbType)) {
			driver = "com.mysql.jdbc.Driver";
			if(!dbUrl.contains("3306")){
				throw new Exception();
			}
		}else if ("sqlserver".equalsIgnoreCase(dbType)) {
			driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			if(!dbUrl.contains("1433")){
				throw new Exception();
			}
		}else if("db2".equalsIgnoreCase(dbType)){
			driver = "com.ibm.db2.jcc.DB2Driver";
			if(!dbUrl.contains("50000")){
				throw new Exception();
			}
		}else if("postgresql".equalsIgnoreCase(dbType)){
			driver = "org.postgresql.Driver";
			if(!dbUrl.contains("5432")){
				throw new Exception();
			}
		}else if("hive".equalsIgnoreCase(dbType)){
			driver = "org.apache.hive.jdbc.HiveDriver";
			if(!dbUrl.contains("10000")){
				throw new Exception();
			}
		}
		if(("null").equals(password)||"".equals(password)){
			password = "";
		}
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(dbUrl, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return conn;
	}


	/**
	 * 关闭数据库连接对象
	 */
	public static void closeConn() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭数据库连接
	 */
	public static void closeStmt(Statement stmt){
		if(stmt!=null){
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public static void closeConn(Connection conn){
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}

package com.hand.bdss.web.common.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hand.bdss.dsmp.component.hive.HiveClient;
import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.web.common.vo.DBSrcVO;

public class DataConnectionUtils {

	private static final Logger logger = LoggerFactory.getLogger(HiveClient.class);

	public static Connection getConnection(DBSrcVO dbSrcVO) throws Exception {
		String driver = null;
		String url = null;
		if ("oracle".equalsIgnoreCase(dbSrcVO.getDbType())) {
			driver = "oracle.jdbc.driver.OracleDriver";
			if (!dbSrcVO.getDbUrl().contains("1521")) {
				throw new Exception();
			}
			url = "jdbc:oracle:thin:@" + dbSrcVO.getDbUrl() + ":orcl" ;
		} else if ("mysql".equalsIgnoreCase(dbSrcVO.getDbType())) {
			driver = "com.mysql.jdbc.Driver";
			if (!dbSrcVO.getDbUrl().contains("3306")) {
				throw new Exception();
			}
			url = "jdbc:mysql://" + dbSrcVO.getDbUrl() + "/" + dbSrcVO.getDbName()+"?useSSL=false";
		} else if ("sqlserver".equalsIgnoreCase(dbSrcVO.getDbType())) {
			driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			if (!dbSrcVO.getDbUrl().contains("1433")) {
				throw new Exception();
			}
			url = "jdbc:sqlserver://" + dbSrcVO.getDbUrl() + ";DatabaseName=" + dbSrcVO.getDbName();
		} else if("db2".equalsIgnoreCase(dbSrcVO.getDbType())){
			driver = "com.ibm.db2.jcc.DB2Driver";
			if(!dbSrcVO.getDbUrl().contains("50000")){
				throw new Exception();
			}
			url = "jdbc:db2://" + dbSrcVO.getDbUrl() + "/" + dbSrcVO.getDbName();
		} else if ("postgresql".equalsIgnoreCase(dbSrcVO.getDbType())) {
			driver = "org.postgresql.Driver";
			if (!dbSrcVO.getDbUrl().contains("5432")) {
				throw new Exception();
			}
			url = "jdbc:postgresql://" + dbSrcVO.getDbUrl() + "/" + dbSrcVO.getDbName();
		} else if ("hive".equalsIgnoreCase(dbSrcVO.getDbType())) {
			driver = "org.apache.hive.jdbc.HiveDriver";
			if (!dbSrcVO.getDbUrl().contains("10000")) {
				throw new Exception();
			}
			url = "jdbc:hive2://" + dbSrcVO.getDbUrl() + "/" + dbSrcVO.getDbName();
		}
		String user = dbSrcVO.getDbUser();
		String password = dbSrcVO.getDbPwd() + "";
		if (("null").equals(password) || "".equals(password)) {
			password = "";
		}
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			logger.error("DataConnectionUtils.getConnection error,error msg is:", e);
		}
		return conn;
	}

	/**
	 * 获取数据库连接状态
	 * 
	 * @param dbSrcVO
	 * @return
	 * @throws Exception
	 */
	public static boolean getConnectionStatus(DBSrcVO dbSrcVO) throws Exception {
		Connection conn = null;
		boolean result = false;
		try {
			if (dbSrcVO.getDbType().equals("Hive")) {
				conn = getHiveConnection(dbSrcVO);
			} else {
				conn = getConnection(dbSrcVO);
			}
			if (conn != null) {
				result = true;
			}
		} finally {
            close(conn,null,null);
		}
		return result;
	}

	private static Connection getHiveConnection(DBSrcVO dbSrcVO) {
		String baseUrl = "jdbc:hive2://" + dbSrcVO.getDbUrl() + "/" + dbSrcVO.getDbName();
		String userName = dbSrcVO.getDbUser();
		String pwd = dbSrcVO.getDbPwd() + "";
		if (pwd.equals("null") || pwd.equals("")) {
			pwd = "";
		}
		Connection conn = null;
		try {
			Class.forName(SystemConfig.driverName);
			conn = DriverManager.getConnection(baseUrl, userName, pwd);
		} catch (Exception e) {
			logger.error("DataConnectionUtils.getHiveConnection error,error msg is:", e);
		}
		return conn;
	}

	/**
	 * 获取连接数据库所有表名
	 * 
	 * @param dbSrcVO
	 * @return
	 * @throws Exception
	 */
	public static List<String> getTablesName(DBSrcVO dbSrcVO) throws Exception {
		Connection conn = null;
		if (dbSrcVO.getDbType().equals("Hive")) {
			conn = getHiveConnection(dbSrcVO);
		} else {
			conn = getConnection(dbSrcVO);
		}
		DatabaseMetaData dbmd = conn.getMetaData();
		String dbUser = dbSrcVO.getDbUser().toUpperCase();
		ResultSet set = null;
		ResultSet set02 = null;
		Statement sta = null;
		List<String> lists = new ArrayList<>();
		try {
			if ("oracle".equals(dbSrcVO.getDbType().toLowerCase())) {
				set = dbmd.getTables(null, null, "%", new String[] { "TABLE", "VIEW" });
			} else if ("mysql".equals(dbSrcVO.getDbType().toLowerCase())) {
				set = dbmd.getTables(conn.getCatalog(), dbUser, null, new String[] { "TABLE", "VIEW" });
			} else if ("sqlserver".equals(dbSrcVO.getDbType().toLowerCase())) {
				StringBuffer sql = new StringBuffer("Select NAME FROM ").append(dbSrcVO.getDbName())
						.append("..sysobjects Where XType in('U','V') ORDER BY Name");
				sta = conn.createStatement();
				set = sta.executeQuery(sql.toString());
				while (set.next()) {
					if (lists == null) {
						lists = new ArrayList<String>();
					}
					lists.add(set.getString("NAME"));
				}
				return lists;
			} else if("db2".equals(dbSrcVO.getDbType().toLowerCase())){
				set = dbmd.getTables(conn.getCatalog(), dbUser, "%", new String[] { "TABLE", "VIEW" });
			} else if ("postgresql".equals(dbSrcVO.getDbType().toLowerCase())) {
				String sql = "SELECT tablename FROM pg_tables WHERE tablename NOT LIKE 'pg%' AND tablename NOT LIKE 'sql_%';";
				String view_sql = "SELECT viewname FROM pg_views WHERE schemaname ='public'";
				sta = conn.createStatement();
				set = sta.executeQuery(sql);
				while (set.next()) {
					lists.add(set.getString("tablename"));
				}
				set02 = sta.executeQuery(view_sql);
				while (set02.next()) {
					lists.add(set02.getString("viewname"));
				}
				return lists;
			} else if ("hive".equals(dbSrcVO.getDbType().toLowerCase())) {
				HiveClient hc = new HiveClient(SystemConfig.userName);
				lists = hc.getHiveTables(dbSrcVO.getDbName());
				for (int i = lists.size() - 1; i >= 0; i--) {
					if (lists.get(i).contains("_delete_")) {
						lists.remove(i);
					}
				}
				return lists;
			}
			while (set.next()) {
				String tables = set.getString("TABLE_NAME");
				lists.add(tables);
			}
		} finally {
			close(conn,sta,set);
			close(null,null,set02);
		}
		return lists;
	}

	/**
	 * 根据表名获取表字段以及字段类型
	 * 
	 * @param dbSrcVO
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> getTablesDescript(DBSrcVO dbSrcVO, String tableName) throws Exception {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		IdentityHashMap<String, String> map = new IdentityHashMap<String, String>();
		try {
			conn = getConnection(dbSrcVO);
			String sql = null;
			if (dbSrcVO.getDbType().equals("Oracle")) {
				String arg = dbSrcVO.getDbName();
				String dbOrSchma = arg.substring(arg.lastIndexOf(":")+1,arg.length());
				sql = "select * from " + dbOrSchma + "." +tableName + " where rownum=1 ";
			} else if("SQLServer".equals(dbSrcVO.getDbType())){
				sql = "select top 1 * from " + tableName ;
			} else if("mysql".equalsIgnoreCase(dbSrcVO.getDbType())){
				sql = "select * from " + tableName +" limit 1";
			} else if("db2".equalsIgnoreCase(dbSrcVO.getDbType())){
				sql = "select * from " + tableName + " fetch first 1 rows only";
			}else{
				sql = "select * from " + tableName +" limit 1";
			}
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			ResultSetMetaData meta = rs.getMetaData();
			for (int i = 1; i < meta.getColumnCount() + 1; i++) {
				map.put(meta.getColumnName(i), meta.getColumnTypeName(i));
			}
		} finally {
			close(conn,st,rs);
		}
		return map;
	}
	/**
	 * 关闭数据库连接
	 */
	public static void close(Connection conn, Statement sta, ResultSet set) {
		try {
			/*if (set != null) {
				set.close();
			}*/
			/*if (sta != null) {
				sta.close();
			}*/
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			logger.error("close connection error,error msg is:",e);
		}
	}
	
	
	public static void main(String []args) throws Exception{
		String driver = "com.ibm.db2.jcc.DB2Driver";  
        // 装载驱动  
        Class.forName(driver);  
        
        Connection conn = null;  
        Statement st = null;  
        ResultSet rs = null;  
        try {  
            conn = DriverManager.getConnection("jdbc:db2://10.211.54.140:50000/TEST", "db2inst1", "db2admin"); 
            if (conn!=null){                
	            DatabaseMetaData dbmd=conn.getMetaData();
	            System.out.println(dbmd.getDatabaseProductName() +  "--" + dbmd.getUserName() + "--" + dbmd.getDriverName());
            }
            st = conn.createStatement();  
            String sql = "select * from tb_user fetch first 1 rows only";  
            rs = st.executeQuery(sql);  
            while(rs.next()) {  
                System.out.println(rs.getString(2));  
            }  
            System.out.println("test for connection correct");  
        } catch (SQLException e) {  
            // 添加日志记录该异常  
            e.printStackTrace();  
            System.out.println("test for connection exception");  
        } finally {  
        	close(conn,st,rs);
        }  
	}

}

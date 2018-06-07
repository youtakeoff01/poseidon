package com.hand.bdss.test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Map;

import org.spark_project.guava.collect.Maps;

public class Test01 {
	public static void main(String[] args) throws Exception{
//		Map<String,String> map = getMap();
//		System.out.println(map.toString());
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://10.7.4.184:3306/poseidon";
		String username="root";
		String password="r00t&edcs@Nd";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, username, password);
		DatabaseMetaData dbmd = conn.getMetaData();
		ResultSet set = dbmd.getTables(conn.getCatalog(), username, null, new String[] { "TABLE","VIEW"});
		int i = 0;
		while(set.next()) {
			i++;
			System.out.println(set.getString("TABLE_NAME"));
		}
		System.out.println(i);
		set.close();
		conn.close();
//		String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//		String url = "jdbc:sqlserver://10.7.0.244:1433;DatabaseName=DataCenter";
//		String username="tst-antx";
//		String password="TSTAntX2017";
//		Class.forName(driver);
//		Connection conn = DriverManager.getConnection(url, username, password);
		String sql = "select name from sysobjects where xtype='V'";
		String sql01 = "Select count(1) FROM DataCenter..SysObjects Where XType in('U','V')";
		//new StringBuffer("Select NAME FROM ").append(dbSrcVO.getDbName()).append("..SysObjects Where XType in('U','V') ORDER BY Name");
//		ResultSet set = null;
//		PreparedStatement sta = null;
//		sta = conn.prepareStatement(sql01);
//		set = sta.executeQuery();
//		String count = "";
//		if(set.next()) {
//			count = set.getString(1);
//		}
//		System.out.println(count);
//		set.close();
//		sta.close();
//		conn.close();
	}
	
	public static Map<String,String> getMap(){
		Map<String,String> map = Maps.newHashMap();
		try {
			map.put("a", "1111");
			return map;
		} finally {
			map.remove("a");
		}
//		return map;
	}

}

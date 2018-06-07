package com.hand.bdss.web.common.vo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.exception.DataServiceException;

/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习 访问Hive表接口
 */
public class AIHiveVO {
	private static final Logger logger = LoggerFactory.getLogger(AIHiveVO.class);

    private  Connection con = null;
    private  Statement stmt = null;
    
    /**
     * 初始化连接
     * @throws DataServiceException
     */
    public void init() throws DataServiceException {
        logger.info("HiveClient.init start init hive connection");
        getConnection();
        logger.info("HiveClient.init connection init successful");
    }
    
    /**
     * 更换数据库
     * @param dbName
     * @throws DataServiceException
     */
    public void changeDB(String dbName) throws DataServiceException{
		String sql = "use "+dbName;
		try {
			init();
			stmt.execute(sql);
		} catch (SQLException e) {
			 logger.error("HiveClient.changeDB error!", e);
		}
	}
    
    /**
     * 连接数据库
     * @return
     * @throws DataServiceException
     */
    public Connection getConnection() throws DataServiceException {
        try {
            Class.forName(SystemConfig.AI_driverName);
            con = DriverManager.getConnection(SystemConfig.AI_baseUrl, SystemConfig.AI_userName, "");
            stmt = con.createStatement();
        } catch (Exception e) {
            logger.error("HiveCore.getConnection error!", e);
        }
        return con;
    }

    /**
     * 关闭数据库链接
     * @throws DataServiceException
     */
    public synchronized void closeConnection() throws DataServiceException {
    	if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                logger.error("HiveCore.closeConnection stmt error!", e);
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                logger.error("HiveCore.closeConnection conn error!", e);
            }
        }
        stmt = null;
        con = null;
    }
    
    
    /**
     * 删除数据库表
     * @param tables
     * @return
     * @throws Exception 
     */
    public boolean deleteTables(List<Map<String,String>> tables) throws Exception{
   	 	logger.info("HiveCore.deleteTables start!");
   	 	boolean boo = false;
        if(con == null || stmt == null){
       	 	init();
        }
        try {
        	for (Map<String, String> map : tables) {
        	   String tableName = map.get("selectV");
        	   if(!StringUtils.isBlank(tableName)){
        		   String sql = "DROP TABLE IF EXISTS " + "intermedia." + tableName;
                   stmt.execute(sql);
        	   }
			}
        	boo = true;
        } catch (Exception e) {
            logger.error("HiveCore.deleteTables :", e);
            boo = false;
        } finally {
        	closeConnection();
        }
        logger.info("HiveCore.deleteTables end!");
        return boo;
    }
    
    /**
     * 获取数据库名称
     * @return
     * @throws Exception 
     */
    public List<Map<String, String>> getHiveDatabases() throws Exception {
    	 logger.info("HiveCore.getHiveDatabases start!");
         List<Map<String,String>> listQuery = new ArrayList<Map<String,String>>();
         ResultSet rs = null;
         if(con == null || stmt == null){
        	 init();
         }
         try {
             String sql = "show databases";
             rs = stmt.executeQuery(sql);
             while (rs.next()) {
            	 Map<String,String> map = new HashMap<String,String>();
            	 map.put("selectV", rs.getString(1));
                 listQuery.add(map);
             }
         } catch (Exception e) {
             logger.error("HiveCore.getHiveDatabases :", e);
         } finally {
         	closeConnection();
         }
         logger.info("HiveCore.getHiveDatabases end!");
         return listQuery;
	}

    
    /**
     * 获取数据表
     * @param dbName
     * @return
     * @throws Exception 
     */
	public List<Map<String, String>> getHiveTables(String dbName) throws Exception {
		 logger.info("HiveCore.getHiveTables param= {}",dbName);
         List<Map<String,String>> listQuery = new ArrayList<Map<String,String>>();
         ResultSet rs = null;
         if(con == null || stmt == null){
        	 init();
         }
         try {
        	 parseSQL(dbName);
             String sql = "show tables";
             rs = stmt.executeQuery(sql);
             while (rs.next()) {
            	 Map<String,String> map = new HashMap<String,String>();
            	 map.put("selectV", rs.getString(1));
                 listQuery.add(map);
             }
         } catch (Exception e) {
             logger.error("HiveCore.getHiveTables :", e);
         } finally {
         	closeConnection();
         }
         logger.info("HiveCore.getHiveTables end!");
         return listQuery;
	}
	
	
    /**
     * 获取表字段
     * @param hiveTbName
     * @return
     * @throws DataServiceException
     */
    public  List<Map<String,String>> getTableFields(String hiveTbName) throws Exception{
    	 logger.info("HiveCore.getTableFields param= {}",hiveTbName);
         List<Map<String,String>> listQuery = new ArrayList<Map<String,String>>();
    	 String dbName = hiveTbName.substring(0,hiveTbName.indexOf("."));            	
         ResultSet rs = null;
         if(con == null || stmt == null){
        	 init();
         }
         try {
        	 parseSQL(dbName);
             String sql = "desc " + hiveTbName;
             rs = stmt.executeQuery(sql);
             while (rs.next()) {
            	 Map<String,String> map = new HashMap<String,String>();
            	 map.put("selectV", rs.getString(1));
                 listQuery.add(map);
             }
         } catch (Exception e) {
             logger.error("HiveCore.getTableFields :", e);
         } finally {
         	closeConnection();
         }
         logger.info("HiveCore.getTableFields end!");
         return listQuery;
    }
    
    
	/**
     * 获取查询结果列表
     * @param task
     * @return
     * @throws DataServiceException 
     */
    public  List<Map<String, String>> getQueryAll(String hiveTbName) throws Exception {
        logger.info("HiveCore.getQuery param= {}",hiveTbName);
        List<Map<String, String>> listQuery = new ArrayList<>();
   	 	String dbName = hiveTbName.substring(0,hiveTbName.indexOf("."));            	
        ResultSet rs = null;
        if(con == null || stmt == null){
       	 	init();
        }
        try {
       	 	parseSQL(dbName);
            String sql = "select * from " + hiveTbName;
            String execSql = "SELECT * FROM ( " + sql + " ) as devtb LIMIT 50";
            rs = stmt.executeQuery(execSql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columns = metaData.getColumnCount();
            Map<String, String> schameAndData = null;
            while (rs.next()) {
                schameAndData = new LinkedHashMap<>();
                for (int i = 1; i <= columns; i++) {
                    schameAndData.put(metaData.getColumnName(i).replace("devtb.", ""), rs.getString(i) + "");
                }
                listQuery.add(schameAndData);
            }

        } catch (Exception e) {
        	Map<String, String> retMap = new LinkedHashMap<>();
        	retMap.put("retCode", "0");
        	listQuery.add(retMap);
            logger.error("HiveCore.getQuery :", e);
        } finally {
        	closeConnection();
        }
        logger.info("HiveCore.getQuery end!");
        return listQuery;
    }
    
    
    /**
     * 解析SQL
     * @param hiveTbName
     */
    private void parseSQL(String dbName){
		try {
	    	if(!"default".equals(dbName)){
				changeDB(dbName);
	    	}
		} catch (DataServiceException e) {
			e.printStackTrace();
		}
    }
    
}

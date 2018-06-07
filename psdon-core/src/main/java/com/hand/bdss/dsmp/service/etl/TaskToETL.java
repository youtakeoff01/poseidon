package com.hand.bdss.dsmp.service.etl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.dsmp.component.hive.HiveClient;
import com.hand.bdss.dsmp.config.SystemConfig;

public class TaskToETL {
	
    private static final Logger logger = LoggerFactory.getLogger(HiveClient.class);
    
    private static Connection conn = null;
    private static Statement state = null;
    
    /**
     * 获得连接
     * @return
     */
    public  Connection getConnection(){
		try{
			Class.forName(SystemConfig.DSMP_DRIVERCLASSNAME);
			conn = DriverManager.getConnection(SystemConfig.DSMP_URL,SystemConfig.DSMP_USERNAME,SystemConfig.DSMP_PASSWORD);
			state = conn.createStatement();
		}catch(Exception e){
			logger.info("JDBCUtils init error!");
		}
		return conn;
	}
    
    
    /**
     * 根据task任务名查询ETLConfig
     * @param <Task>
     * @param <TableEtlDO>
     * @return
     */
    private Map<String,String> getETLConfig(String taskName){
    	logger.info("TaskToETL.getDBSrcId start ! param = {"+taskName+"}");
    	Map<String,String> map = new HashMap<String,String>();
    	String sql = "select id,tb_db_src_id,job_name,job_type,job_config from tb_etl where job_name = '"+taskName+"'";
    	try{
    		getConnection();
    		ResultSet result = state.executeQuery(sql);
    		while(result.next()){
    			map.put("jobId",result.getString(1));
    			map.put("datasourceId",String.valueOf(result.getInt(2)));
    			map.put("jobName",result.getString(3));
    			map.put("jobType",result.getString(4));
    			map.put("jobConfig",result.getString(5));
    		}
    	}catch(Exception e){
    		logger.error("TaskToETL.getDBSrcId error",e);
    	}
    	logger.info("TaskToETL.getDBSrcId end ! response = {"+map+"}");
    	return map;
    }
    
    /**
     * 解析task
     * @param dataSource
     * @return
     */
    public String parseTask(String task){
    	logger.info("TaskToETL.parseTask start! param = {"+task.toString()+"}");
    	String json = "";
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
    	JSONObject jsonObject = JSONObject.parseObject(task);
        String taskName = jsonObject.getString("taskName");                            //任务名
        Integer num = jsonObject.getInteger("num");
        String jobId = null;														   //任务id
        String jobConfig = null;													   //任务配置
        String datasourceId = null;													   //数据源id
        String srcName = null;														   //数据源名称
        String dbName = null;														   //数据库名称
        String dbUrl = null;														   //数据库连接url
        String dbUser = null;														   //数据库用户
        String dbPwd = null;														   //数据库密码
        String dbType = null;														   //数据库类型
        String dbDriver = null;														   //数据库驱动名称
    	try{
    		Map<String,String> map = getETLConfig(taskName);
			jobId = map.get("jobId"); 
			jobConfig = map.get("jobConfig");
			datasourceId = map.get("datasourceId");
	    	String sql ="select src_name,db_name,db_uri,db_user,db_pwd,db_type,db_driver "
	    			+ "from tb_db_src where id = "+Integer.parseInt(datasourceId);
    		getConnection();
    		ResultSet rs = state.executeQuery(sql);
    		while(rs.next()){
    			srcName = rs.getString(1);
    			dbName  = rs.getString(2);
    			dbUrl   = rs.getString(3);
    			dbUser  = rs.getString(4);
    			dbPwd   = rs.getString(5);
    			dbType  = rs.getString(6);
    			dbDriver = rs.getString(7);
    		}
    		//数据封装
			JSONObject jsonETL = JSONObject.parseObject(jobConfig);
    		Map<String, Object> hashMap = new HashMap<String, Object>();
			Map<String, Object> dataSource = new HashMap<String, Object>();
			Map<String, Object> etlConfig = new HashMap<String, Object>();
			dataSource.put("id",datasourceId);
			String url = null;
			if ("mysql".equalsIgnoreCase(dbType)) {
				url = "jdbc:mysql://" + dbUrl + "/" + dbName;
			}
			if ("oracle".equalsIgnoreCase(dbType)) {
				url = "jdbc:oracle:thin:@" + dbUrl + ":" + dbName;
			}
			if ("sqlserver".equalsIgnoreCase(dbType)) {
				url = "jdbc:sqlserver://" + dbUrl + ";DatabaseName=" + dbName;
			}
			if ("postgresql".equalsIgnoreCase(dbType)) {
				url = "jdbc:postgresql://" + dbUrl + "/" + dbName;
			}
			if("db2".equalsIgnoreCase(dbType)){
				url = "jdbc:db2://" + dbUrl + "/" + dbName;
			}
			dataSource.put("dbUrl", url);
			dataSource.put("filePath", "");
			dataSource.put("dbName", dbName);
			dataSource.put("dbUser", dbUser);
			dataSource.put("dbPwd", dbPwd);
			dataSource.put("dbDriver", dbDriver);
			dataSource.put("tableName", jsonETL.getString("dataTable"));
			dataSource.put("queryField", jsonETL.getString("syncFilelds"));
			dataSource.put("queryCondition",jsonETL.getString("udc"));

			etlConfig.put("jobId", jobId);
			etlConfig.put("jobName", jsonETL.getString("jobName"));
			etlConfig.put("jobType",jsonETL.getString("jobType"));
			etlConfig.put("dataType", jsonETL.getString("dataType"));
			etlConfig.put("syncDB",jsonETL.getString("syncDB"));
			etlConfig.put("syncSource", jsonETL.getString("syncSource"));
			etlConfig.put("syncType", jsonETL.getString("syncType"));
			etlConfig.put("is_partition",jsonETL.getString("is_partition"));
			etlConfig.put("partition",jsonETL.getString("partition"));
			etlConfig.put("num", num);
			hashMap.put("action", "0");
			hashMap.put("dataSource", dataSource);
			hashMap.put("etlConfig", etlConfig);
			listMap.add(hashMap);
    	}catch(Exception e){
    		logger.error("TaskToETL.parseTask error!",e);
    	}finally{
    		closeConnection();
    	}
    	json = JSONObject.toJSONString(listMap);
    	return json;
    }
    
    /**
     * 关闭连接
     */
    private synchronized static void closeConnection(){
    	if (state != null) {
            try {
           	 state.close();
            } catch (SQLException e) {
                logger.error("TaskETL closeStatement error!", e);
            }
        }
    	 if (conn != null) {
             try {
                 conn.close();
             } catch (SQLException e) {
                 logger.error("TaskETL closeConnection error!", e);
             }
         }
         state = null;
         conn = null;
	}
    
}

package com.hand.bdss.dsmp.service.etl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.dsmp.component.ambari.ExecuteHandler;
import com.hand.bdss.dsmp.model.DataSource;
import com.hand.bdss.dsmp.model.DataSourceType;
import com.hand.bdss.dsmp.model.ETLEum;
import com.hand.bdss.dsmp.model.ETLJobConfig;
import com.hand.bdss.dsmp.service.azkaban.AzkabanService;


/**
 * 数据集成 -》数据同步 HIVE与 RDBMS 数据导入与导出
 */
public class ETLManager {
    private static final Logger logger = LoggerFactory.getLogger(ETLManager.class);
    private static List<ETLJobConfig> listETLJobConfig = null;
    //    private static String jsonString ; //web传入
    //统一用spring管理
    ExecuteHandler executeHandler = new ExecuteHandler();

    public static boolean initRun(String jsonString) throws Exception {
        Object json = new JSONTokener(jsonString).nextValue();
        if (json instanceof org.json.JSONArray) {
            return true;
        } else if (json instanceof org.json.JSONObject) {
            return true;
        } else {
            logger.error("ETLManager.initRun json format error !");
            return false;
        }

    }


    /**
     * 任务增删改动作判断
     * @param etlJobConfig
     * @param username
     * @throws Exception
     */
    public void operatesJudge(ETLJobConfig etlJobConfig,String username) throws Exception {
        Integer action = etlJobConfig.getAction();
        String jobId = etlJobConfig.getJobId();
        String tableName = etlJobConfig.getDataSource().getTableName();
        StringBuilder sJobPath = new StringBuilder();
        sJobPath.append(ETLEum.AZKABAN_JOB_PATH.toString()).append(jobId).append("_").append(tableName).append(".zip");
        if (1 == action) {//删
            File file = new File(String.valueOf(sJobPath));
            //System.out.println(file.getPath());
            file.delete();
        } else if (2 == action) {//改
            File file = new File(String.valueOf(sJobPath));
            file.delete();
            createJobScript(etlJobConfig,username);
        } else if (0 == action) {//增
            createJobScript(etlJobConfig,username);
        } else {
            logger.error("ETLManager.operatesJudge unsupported operations !");
        }
    }

    public void createJobScript(ETLJobConfig etlJobConfig,String username) throws Exception {
        if (DataSourceType.MYSQL.toString().equals(etlJobConfig.getDataType()) ||
                DataSourceType.ORACLE.toString().equals(etlJobConfig.getDataType()) ||
                DataSourceType.SQLSERVER.toString().equals(etlJobConfig.getDataType()) ||
                DataSourceType.POSTGRESQL.toString().equals(etlJobConfig.getDataType()) ||
                DataSourceType.DB2.toString().equalsIgnoreCase(etlJobConfig.getDataType())) {
            DBToHive dbToHive = new DBToHive();
            dbToHive.run(etlJobConfig,username);
        } else if (DataSourceType.HIVE.toString().equals(etlJobConfig.getDataType())) {
            HiveToDB hiveToDB = new HiveToDB();
            hiveToDB.run(etlJobConfig);
        } else {
            logger.error("ETLManager.createJobScript unsupported task type !");
        }
        logger.info("ETLManager.createJobScript create job successs !");
    }

    /**
     *调用task中 azkban接口 实现工程创建 ，任务压缩 ，上传
     * @param etlJobConfig
     * @param projectName
     * @throws Exception
     */
    public static void callAzkabanAPI(ETLJobConfig etlJobConfig, String projectName) throws Exception {
        logger.info("ETLManager.callAzkabanAPI azkaban task configuration start !");
        AzkabanService.etlschedule(etlJobConfig, projectName);
        logger.info("ETLManager.callAzkabanAPI azkaban task configuration stop !");

    }

    /**
     * 调用task中 azkban接口 登录 执行工作流
     * @param projectName
     * @param jobname
     * @throws Exception
     */
    public static void executeHiveToRdbmsAPI(String projectName, String jobname) throws Exception {
        logger.info("ETLManager.callAzkabanAPI azkaban task configuration start !");
        AzkabanService.executeHiveToRdbmsAPI(projectName, jobname);
        logger.info("ETLManager.callAzkabanAPI azkaban task configuration stop !");

    }

    /**
     * 数据同步主入口	
     * @param jsonString
     * @param username
     * @throws Exception
     */
    public void run(String jsonString,String username) throws Exception {
        if (initRun(jsonString)) {
            ETLJobJsonParse etlJobJsonParse = new ETLJobJsonParse();
            listETLJobConfig = etlJobJsonParse.jsonParse(jsonString);
            for (int i = 0; i < listETLJobConfig.size(); i++) {
                ETLJobConfig etlJobConfig = listETLJobConfig.get(i);
                String projectName = etlJobConfig.getJobName() + "-0-sqoop";
                operatesJudge(etlJobConfig,username);
                callAzkabanAPI(etlJobConfig, projectName);
            }
        }
    }

    /**
     * 运维中心->任务管理->立即执行入口
     * 重新创建sqoop to hive job副本传入 where条件，立即执行
     * @param jsonTask
     * @throws Exception
     */
    public void taskRun(String jsonTask) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(jsonTask);
        String data_type = jsonObject.getString("dataType");

        TaskToETL t = new TaskToETL();
        String jsonString = t.parseTask(jsonTask);
        if (initRun(jsonString)) {
            ETLJobJsonParse etlJobJsonParse = new ETLJobJsonParse();
            listETLJobConfig = etlJobJsonParse.jsonParse(jsonString);
            
            for (ETLJobConfig etlJobConfig : listETLJobConfig) {
                String projectName = etlJobConfig.getJobName() + "-0-sqoop-region";
                if (data_type.equalsIgnoreCase("Hive")) {
                    HiveToDB hiveToDB = new HiveToDB();
                    hiveToDB.taskRun(etlJobConfig);
                    callAzkabanAPI(etlJobConfig, projectName);
                } else {
                    DataSource dataSource = etlJobConfig.getDataSource();
                    DBToHive dBToHive = new DBToHive();
                    String partition = jsonObject.getString("partition");
                    String partitionType = jsonObject.getString("partitionType");
                    String dataType = jsonObject.getString("dataType");
                    String starttime = jsonObject.getString("starttime");
                    String endtime = jsonObject.getString("endtime");
                    if (StringUtils.isNotEmpty(partition)) {
                    	if(DataSourceType.MYSQL.toString().equalsIgnoreCase(dataType) && ("int".equalsIgnoreCase(partitionType)
        						|| "bigint".equalsIgnoreCase(partitionType))) {
                    		if (StringUtils.isNotEmpty(starttime) && StringUtils.isNotEmpty(endtime)) {
                                String query = "FROM_UNIXTIME("+partition+") >= '" + starttime + "' AND FROM_UNIXTIME("+partition+")<='" + endtime + "' AND "+dataSource.getQueryCondition();
                                dataSource.setQueryCondition(query);
                            }
                            if (StringUtils.isNotEmpty(starttime) && StringUtils.isEmpty(endtime)) {
                                String query = "FROM_UNIXTIME("+partition+") >= '" + starttime+"' AND "+dataSource.getQueryCondition();
                                dataSource.setQueryCondition(query);
                            }
                            if (StringUtils.isEmpty(starttime) && StringUtils.isNotEmpty(endtime)) {
                                String query = "FROM_UNIXTIME("+partition+") <='" + endtime + "' AND "+dataSource.getQueryCondition();
                                dataSource.setQueryCondition(query);
                            }
                    	}else if(DataSourceType.ORACLE.toString().equalsIgnoreCase(dataType) && ("char".equalsIgnoreCase(partitionType)
        						|| "varchar".equalsIgnoreCase(partitionType)
        						|| "varchar2".equalsIgnoreCase(partitionType)
        						|| "nvarchar".equalsIgnoreCase(partitionType)
        						|| "nvarchar2".equalsIgnoreCase(partitionType))) {
                    		if (StringUtils.isNotEmpty(starttime) && StringUtils.isNotEmpty(endtime)) {
                                String query = "TO_DATE("+partition+",'yyyy-mm-dd hh24:mi:ss') >= TO_DATE('" + starttime + "','yyyy-mm-dd hh24:mi:ss') AND TO_DATE("+partition+",'yyyy-mm-dd hh24:mi:ss')<=TO_DATE('" + endtime + "','yyyy-mm-dd hh24:mi:ss') AND "+dataSource.getQueryCondition();
                                dataSource.setQueryCondition(query);
                            }
                            if (StringUtils.isNotEmpty(starttime) && StringUtils.isEmpty(endtime)) {
                                String query = "TO_DATE("+partition+",'yyyy-mm-dd hh24:mi:ss') >= TO_DATE('" + starttime + "','yyyy-mm-dd hh24:mi:ss') AND "+dataSource.getQueryCondition();
                                dataSource.setQueryCondition(query);
                            }
                            if (StringUtils.isEmpty(starttime) && StringUtils.isNotEmpty(endtime)) {
                                String query = "TO_DATE("+partition+",'yyyy-mm-dd hh24:mi:ss')<=TO_DATE('" + endtime + "','yyyy-mm-dd hh24:mi:ss') AND "+dataSource.getQueryCondition();
                                dataSource.setQueryCondition(query);
                            }
                    	}else {
                    		if (StringUtils.isNotEmpty(starttime) && StringUtils.isNotEmpty(endtime)) {
                    			String query = partition+" >= '" + starttime + "' AND "+partition+"<='" + endtime + "' AND "+dataSource.getQueryCondition();
                    			dataSource.setQueryCondition(query);
                    		}
                    		if (StringUtils.isNotEmpty(starttime) && StringUtils.isEmpty(endtime)) {
                    			String query = partition+" >= '" + starttime+"' AND "+dataSource.getQueryCondition();
                    			dataSource.setQueryCondition(query);
                    		}
                    		if (StringUtils.isEmpty(starttime) && StringUtils.isNotEmpty(endtime)) {
                    			String query = partition+"<='" + endtime + "' AND "+dataSource.getQueryCondition();
                    			dataSource.setQueryCondition(query);
                    		}
                    	}
                    }
                    etlJobConfig.setSyncType(0);
                    etlJobConfig.setDataSource(dataSource);
                    dBToHive.taskRun(etlJobConfig);
                    callAzkabanAPI(etlJobConfig, projectName);
                }

            }
        }
    }

    /**
     * 获取flume配置文件
     * @param
     * @return
     */
    public String getFlumeConfig() throws IOException {
        String conf = executeHandler.getConfig("flume-conf");
        return conf;
    }

    /**
     * 修改配置
     * @return
     */
    public void executeService() throws IOException {
        executeHandler.executeService();
    }

    /**
     * 执行配置
     * @return
     */
    public void updateConfig(String content) throws IOException {
        executeHandler.updateConfig(content);
    }
}

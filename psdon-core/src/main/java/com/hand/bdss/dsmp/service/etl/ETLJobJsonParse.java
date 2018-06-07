package com.hand.bdss.dsmp.service.etl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.dsmp.model.DataSource;
import com.hand.bdss.dsmp.model.ETLJobConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by win on 2017/4/26.
 */
public class ETLJobJsonParse {

    /**
     * Json解析
     *
     * @param jsonString
     * @return List<ETLJobConfig>
     */
    public List<ETLJobConfig> jsonParse(String jsonString) {

        JSONArray jsonArray = JSON.parseArray(jsonString);
        Iterator<Object> it = jsonArray.iterator();
        List<ETLJobConfig> listETLJobConfig = new ArrayList<ETLJobConfig>();
        ETLJobConfig etlJobConfig = null;
        while (it.hasNext()) {
            JSONObject jsonObjectETLJobConfig = (JSONObject) it.next();
            if (jsonObjectETLJobConfig.getString("action") != null) {
                etlJobConfig = jsonParseOneJob(jsonObjectETLJobConfig);
            }
            if (etlJobConfig != null) {
                listETLJobConfig.add(etlJobConfig);
            }
        }
        return listETLJobConfig;
    }

    /*
     * 解析一个Job的json
     */
    private ETLJobConfig jsonParseOneJob(JSONObject json) {
        DataSource dataSource = new DataSource();// 数据源配置
        ETLJobConfig etlJobConfig = new ETLJobConfig(); //JOB配置
        String action;//数据源操作
        String id; // 数据源ID
        String dbUrl; // 数据库地址
        String filePath; // HDFS文件全路径 如果数据类型为FILE则取该字段
        String dbName;  //数据库名
        String dbUser; // 数据库用户名
        String dbPwd; // 数据库密码
        String dbDriver; // 驱动类
        String tableName;  //源表名
        String queryField; //字段
        String queryCondition; //查询条件
        String jobId; // 任务ID（主键ID）
        String jobName; // 任务名称
        String jobType;//任务类型
        String dataType; // 数据类型，JDBC或FILE文件
        String syncSource; //同步目标表
        String syncDB;     //同步数据库
        String syncType; // 同步策略，全量或增量
        String is_partition;//是否分段
        String partition;//分区字段
        String partitionType;//分区字段类型
        Integer num;//任务并行度

        action = json.getString("action");
        JSONObject jsonObjectDataSource = json.getJSONObject("dataSource");
        id = jsonObjectDataSource.getString("id");
        dbUrl = jsonObjectDataSource.getString("dbUrl");
        filePath = jsonObjectDataSource.getString("filePath");
        dbName = jsonObjectDataSource.getString("dbName");
        dbUser = jsonObjectDataSource.getString("dbUser");
        dbPwd = jsonObjectDataSource.getString("dbPwd");
        dbDriver = jsonObjectDataSource.getString("dbDriver");
        tableName = jsonObjectDataSource.getString("tableName");
        queryField = jsonObjectDataSource.getString("queryField");
        queryCondition = jsonObjectDataSource.getString("queryCondition");

        JSONObject jsonObjectETLConfig = json.getJSONObject("etlConfig");
        jobId = jsonObjectETLConfig.getString("jobId");
        jobName = jsonObjectETLConfig.getString("jobName");
        jobType = jsonObjectETLConfig.getString("jobType");
        dataType = jsonObjectETLConfig.getString("dataType");
        syncDB = jsonObjectETLConfig.getString("syncDB");
        syncSource = jsonObjectETLConfig.getString("syncSource");
        syncType = jsonObjectETLConfig.getString("syncType");
        is_partition = jsonObjectETLConfig.getString("is_partition");
        partition = jsonObjectETLConfig.getString("partition");
        partitionType = jsonObjectETLConfig.getString("partitionType");
        num = jsonObjectETLConfig.getInteger("num");

        dataSource.setId(Long.parseLong(id));
        dataSource.setDbUrl(dbUrl);
        dataSource.setFilePath(filePath);
        dataSource.setDbName(dbName);
        dataSource.setDbUser(dbUser);
        dataSource.setDbPwd(dbPwd);
        dataSource.setDbDriver(dbDriver);
        dataSource.setTableName(tableName);
        dataSource.setQueryField(queryField);
        dataSource.setQueryCondition(queryCondition);
        etlJobConfig.setAction(Integer.parseInt(action));
        etlJobConfig.setJobId(jobId);
        etlJobConfig.setJobName(jobName);
        etlJobConfig.setJobType(jobType);
        etlJobConfig.setDataType(dataType);
        etlJobConfig.setSynDB(syncDB);
        etlJobConfig.setSyncSource(syncSource);

        if (StringUtils.isNotEmpty(syncType)) {
            etlJobConfig.setSyncType(Integer.parseInt(syncType));
        }
        if (StringUtils.isNotEmpty(is_partition)) {
            etlJobConfig.setIs_partition(Integer.parseInt(is_partition));
        }
        if (StringUtils.isNotEmpty(partition)) {
            etlJobConfig.setPartition(partition);
        }
        if(StringUtils.isNotEmpty(partitionType)) {
        	etlJobConfig.setPartitionType(partitionType);
        }
        if(num!=null) {
        	etlJobConfig.setNum(num);
        }

        etlJobConfig.setDataSource(dataSource);

        return etlJobConfig;
    }

}
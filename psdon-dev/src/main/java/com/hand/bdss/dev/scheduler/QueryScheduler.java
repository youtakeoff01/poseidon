package com.hand.bdss.dev.scheduler;

import com.hand.bdss.dev.data.query.HiveQuery;
import com.hand.bdss.dev.data.query.SparkQuery;
import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.dsmp.config.SystemConfig;

import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : Koala
 * @version : v1.0
 * @description :
 */
public class QueryScheduler {
    private static final Logger logger = LoggerFactory.getLogger(QueryScheduler.class);

    /**
     * 根据task.SQLType  查询
     *
     * @param task
     * @return
     */
    public List<Map<String, String>> queryScheduler(Task task) {
        logger.info("DevelopManager.getQuery param= { " + task.getSqlType() + "," + task.getSqlStc() + " }");
        List<Map<String, String>> query = new ArrayList<>();
        String sql_type = task.getSqlType();
        if ("hive".equalsIgnoreCase(sql_type)) {
            query = HiveQuery.getQuery(task);
        } else if ("spark".equalsIgnoreCase(sql_type)) {
            query = this.sparkQueryScheduler(task);
        } else {
            logger.info("DevelopManager.getQuery  SQL type nonsupport!");
        }
        logger.info("DevelopManager.getQuery end!");
        return query;
    }

    /**
     * Spark  查询
     *
     * @param task
     * @return
     */
    private synchronized List<Map<String, String>> sparkQueryScheduler(Task task) {
        List<Map<String, String>> list = new ArrayList<>();
        
        System.setProperty("HADOOP_USER_NAME", SystemConfig.userName);
        SparkSession spark = null;
        try {
            spark = SparkSession
                    .builder()
                    .appName("SparkQuery")
                    .master("local[4]")
                    .config("spark.sql.warehouse.dir", "/apps/hive/warehouse")
                    .enableHiveSupport()
                    .getOrCreate();
            SparkQuery sparkQuery = new SparkQuery();
            list = sparkQuery.getQuery(spark, task);
        } catch (Exception e) {
            logger.error("sparkQuery /spark error!", e);
        } finally {
            spark.close();
        }
        return list;
    }
    
    /**
     * Spark  查询
     *
     * @param sql
     * @param limitNum
     * @return
     */
    public synchronized List<Map<String, String>> sparkLimitQueryScheduler(String sql, int limitNum) {
        List<Map<String, String>> list = new ArrayList<>();

        System.setProperty("HADOOP_USER_NAME", SystemConfig.userName);
        SparkSession spark = null;
        try {
            spark = SparkSession
                    .builder()
                    .appName("SparkLimitQuery")
                    .master("local[*]")
                    .config("spark.sql.warehouse.dir", "/apps/hive/warehouse")
                    .enableHiveSupport()
                    .getOrCreate();
            SparkQuery sparkQuery = new SparkQuery();
            list = sparkQuery.getLimitQuery(spark, sql, limitNum);
        } catch (Exception e) {
            logger.error("sparkLimitQueryScheduler /spark error!", e);
        } finally {
            spark.close();
        }
        return list;
    }
}

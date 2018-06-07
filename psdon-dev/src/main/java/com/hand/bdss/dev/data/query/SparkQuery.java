package com.hand.bdss.dev.data.query;

import com.hand.bdss.dev.util.ScriptUtil;
import com.hand.bdss.dev.vo.Task;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Koala
 * @version : v1.0
 * @description :
 */
public class SparkQuery {
    private static final Logger logger = LoggerFactory.getLogger(SparkQuery.class);

    public List<Map<String, String>> getQuery(SparkSession spark, Task task) {
        logger.info("SparkQuery.getQuery param = {}", task.getSqlStc());
        List<Map<String, String>> listQuery = new ArrayList<>();
        String taskSql = ScriptUtil.CutOutSQL(task.getSqlStc());
        Dataset<Row> hiveDF = spark.sql(taskSql);
        Dataset<Row> limitHiveDF = hiveDF.limit(10);
        String[] columns = limitHiveDF.columns();
        List<Row> collect = limitHiveDF.collectAsList();
        try {
            for (Row row : collect) {
                Map<String, String> schameAndData = new LinkedHashMap<>();
                for (String s : columns) {
                    schameAndData.put(s, row.getAs(s) + "");
                }
                listQuery.add(schameAndData);
            }
        } catch (Exception e) {
            Map<String, String> retMap = new LinkedHashMap<>();
            retMap.put("retCode", "0");
            listQuery.add(retMap);
            logger.error("SparkQuery.getQuery :", e);
        }
        logger.info("SparkQuery.getQuery end!");
        return listQuery;
    }
    
    public List<Map<String, String>> getLimitQuery(SparkSession spark, String sql, int limitNum) {
        logger.info("SparkQuery.getLimitQuery param = {}", sql);
        List<Map<String, String>> listQuery = new ArrayList<>();
        Dataset<Row> hiveDF = spark.sql(sql);
        Dataset<Row> limitHiveDF = hiveDF.limit(limitNum);
        String[] columns = limitHiveDF.columns();
        List<Row> collect = limitHiveDF.collectAsList();
        try {
            for (Row row : collect) {
                Map<String, String> schameAndData = new LinkedHashMap<>();
                for (String s : columns) {
                    schameAndData.put(s, row.getAs(s) + "");
                }
                listQuery.add(schameAndData);
            }
        } catch (Exception e) {
            Map<String, String> retMap = new LinkedHashMap<>();
            retMap.put("retCode", "0");
            listQuery.add(retMap);
            logger.error("SparkQuery.getLimitQuery :", e);
        }
        logger.info("SparkQuery.getLimitQuery end!");
        return listQuery;
    }


}

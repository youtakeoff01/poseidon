package com.hand.bdss.dsmp.service.etl;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hand.bdss.dsmp.model.ETLEum;
import com.hand.bdss.dsmp.model.ETLJobConfig;
import com.hand.bdss.dsmp.model.TableMeta;
import com.hand.bdss.dsmp.util.POIUtils;

public class FileToHive {
    private static final Logger logger = LoggerFactory.getLogger(FileToHive.class);

    private POIUtils poiUtils = new POIUtils();

    public String run(ETLJobConfig etlJobConfig) throws Exception {
        String filePath = etlJobConfig.getDataSource().getFilePath();
        String file_name = filePath.split("/", -1)[filePath.split("/", -1).length - 1];
        String dBName = "default"; 
        if(etlJobConfig.getDataSource().getDbName() != null){ 
        	dBName = etlJobConfig.getDataSource().getDbName();}
        String tableName = etlJobConfig.getSyncSource();
        String hdfs_path = null;
        if (filePath.endsWith(".csv")) {
            hdfs_path = poiUtils.delTableHead(filePath, ETLEum.EXCEL_PATH.toString(), tableName);
        } else {
            poiUtils.getSheet(filePath, ETLEum.EXCEL_PATH.toString());
            hdfs_path = poiUtils.Xlsx2Csv(file_name, tableName);
        }
        if (hdfs_path != null) {
            //azkaban 执行hive有问题  改用移动文件的方式
           // String sql = "hive -e 'LOAD DATA inpath \"" + hdfs_path + "\"  OVERWRITE INTO TABLE default." + tableName+" '" ;
        	String sql = "hdfs dfs -mv  " + hdfs_path + "  /apps/hive/warehouse/" + tableName+"/" ;
            createAlterScript(sql,tableName);
            return sql;
        } else {
            throw new Exception("表中无数据，请重新选择");
        }
    }
    
    private void createAlterScript(String  alterSql,String tableName) throws Exception {
        FileOutputStream fos = new FileOutputStream(ETLEum.SCRIPT_SQOOP_PATH +  tableName + ".sh", false);
        fos.write(alterSql.getBytes("UTF-8"));
        fos.close();
    }

}

package com.hand.bdss.dsmp.config;

import javax.ws.rs.core.MediaType;

import com.hand.bdss.dsmp.util.ConfigReader;


public class SystemConfig {
    //hdfs常量
    public static final String HDFS_ROOT_PATH = getProperties("HDFS_ROOT_PATH");
    public static final String HDFS_FILE_LIMIT_SIZE = getProperties("HDFS_FILE_LIMIT_SIZE");
    //atlas的常量
    public static final String BASE_URL = getProperties("BASE_URL");
    public static final String JSON_MEDIA_TYPE = MediaType.APPLICATION_JSON + "; charset=UTF-8";
    public static final String USER_NAME = getProperties("USER_NAME");
    public static final String PASS_WORD = getProperties("PASS_WORD");
    //hive常量
    public static final String driverName = getProperties("driverName");
    public static final String baseUrl = getProperties("baseUrl");
    public static final String userName = getProperties("userName");

    //ranger
    public static final String RANGER_DOMAINNAME = getProperties("RANGER_DOMAINNAME");
    public static final String RANGER_USERNAME = getProperties("RANGER_USERNAME");
    public static final String RANGER_PASSWORD = getProperties("RANGER_PASSWORD");
    public static final String HIVE_SERVER = getProperties("HIVE_SERVER");


    //azkaban
    public static final String AZKABAN_HOSTNAME = getProperties("AZKABAN_HOSTNAME");
    public static final String AZKABAN_USERNAME = getProperties("AZKABAN_USERNAME");
    public static final String AZKABAN_PASSWORD = getProperties("AZKABAN_PASSWORD");

    public static final String AZKABAN_URL = getProperties("AZKABAN_URL");
    public static final String AZKABAN_USERNAME2 = getProperties("AZKABAN_USERNAME2");
    public static final String AZKABAN_PASSWORD2 = getProperties("AZKABAN_PASSWORD2");

    //ambari
    public static final String AMBARI_HOST = getProperties("AMBARI_HOST");
    public static final String AMBARI_URL = getProperties("AMBARI_URL");
    public static final String AMBARI_USERNAME = getProperties("AMBARI_USERNAME");
    public static final String AMBARI_PASSWORD = getProperties("AMBARI_PASSWORD");

    //azkban数据库连接信息   		
    public static final String DBNAME = getProperties("DBNAME");
    public static final String DBURL = getProperties("DBURL");
    public static final String DBUSERNAME = getProperties("DBUSERNAME");
    public static final String DBPWD = getProperties("DBPWD");
    public static final String DBTYPE = getProperties("DBTYPE");

    //hive元数据，数据库连接信息
    public static final String HIVE_MD_DRIVERNAME = getProperties("HIVE_MD_DRIVERNAME");
    public static final String HIVE_MD_BASEURL = getProperties("HIVE_MD_BASEURL");
    public static final String HIVE_MD_USERNAME = getProperties("HIVE_MD_USERNAME");
    public static final String HIVE_MD_PASSWORD = getProperties("HIVE_MD_PASSWORD");
    //flume主机 用户名 密码
    public static final String FLUME_HOSTNAME = getProperties("FLUME_HOSTNAME");
    public static final String USERNAME = getProperties("USERNAME");
    public static final String PASSWORD = getProperties("PASSWORD");
    //mysql 数据库链接
    public static final String DSMP_DRIVERCLASSNAME = getProperties("DSMP_DRIVERCLASSNAME");
    public static final String DSMP_URL = getProperties("DSMP_URL");
    public static final String DSMP_USERNAME = getProperties("DSMP_USERNAME");
    public static final String DSMP_PASSWORD = getProperties("DSMP_PASSWORD");

    //机器学习   python连接
    public static final String PYTHON_REST_URL = getProperties("PYTHON_REST_URL");
    public static final String PYTHON_LOG_COMMAND = getProperties("PYTHON_LOG_COMMAND");

    //机器学习   Hive连接(hadoop003)
    public static final String AI_driverName = getProperties("AI_driverName");
    public static final String AI_baseUrl = getProperties("AI_baseUrl");
    public static final String AI_userName = getProperties("AI_userName");

    //atlas uri
    public static final String ATLAS_USERNAME = getProperties("ATLAS_USERNAME");
    public static final String ATLAS_PASSWORD = getProperties("ATLAS_PASSWORD");
    public static final String ATLAS_API_URL = getProperties("ATLAS_API_URL");
    public static final String ATLAS_GUID_URL = ATLAS_API_URL + getProperties("ATLAS_GUID_URL");
    public static final String ATLAS_LINEAGE_URL = ATLAS_API_URL + getProperties("ATLAS_LINEAGE_URL");
    public static final String ATLAS_ENTITY_URL = ATLAS_API_URL + getProperties("ATLAS_ENTITY_URL");

    /**
     * 获取配置文件信息
     *
     * @return
     */
    private static String getProperties(String key) {
        String value = null;
        ConfigReader reader = ConfigReader.read("core");
        try {
            value = reader.getConstant(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

//    public static void main(String[] args){
//        String key = "ATLAS_LINEAGE_SCHEMA_URL";
//        String value = null;
//        ConfigReader reader = ConfigReader.read("core");
//        try {
//            value = reader.getConstant(key);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(value);
//    }


}

package com.hand.bdss.task.config;

import javax.ws.rs.core.MediaType;

import com.hand.bdss.task.util.ConfigReader;


public class SystemConfig {

    public static final String AZKABAN_USERNAME = getProperties("AZKABAN_USERNAME");
    public static final String AZKABAN_PASSWORD = getProperties("AZKABAN_PASSWORD");

    //azkaban ajax api
    public static final String BASEURL = getProperties("BASEURL");
    public static final String URL = getProperties("URL");
    public static final String CREATEACTION = getProperties("CREATEACTION");
    public static final String LOGINACTION = getProperties("LOGINACTION");
    public static final String DELETEACTION = getProperties("DELETEACTION");
    public static final String UPLOADACTION = getProperties("UPLOADACTION");
    public static final String SCHEDULEACTION = getProperties("SCHEDULEACTION");
    public static final String SCRIPT_SQL_PATH = getProperties("SCRIPT_SQL_PATH");
    public static final String SCRIPT_SQOOP_PATH = getProperties("SCRIPT_SQOOP_PATH");
    public static final String AZKABAN_JOB_PATH = getProperties("AZKABAN_JOB_PATH");
    /**
     * 获取配置文件信息
     * @return
     */
    private static String getProperties(String key){
    	String value = null;
	    ConfigReader reader = ConfigReader.read("task");
	    try {
	    	value = reader.getConstant(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
    }
}

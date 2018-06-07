package com.hand.bdss.task.util;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class ConfigReader {
	
	private static final Logger log = Logger.getLogger(ConfigReader.class);
	
	private ResourceBundle resourceBundle;
	
	public ConfigReader(ResourceBundle resourceBundle){
		this.resourceBundle = resourceBundle;
	}
	
	public static ConfigReader read(String propertyFileName){
		try{
			ResourceBundle resourceBundle = ResourceBundle.getBundle(propertyFileName);
			ConfigReader reader = new ConfigReader(resourceBundle);
			return reader;
		}catch(Exception e){
			log.error("error", e);
			return null;
		}
	}
	
	public String getConstant(String key) throws Exception{
		try{
			String value = resourceBundle.getString(key);
//			log.info( " Key=" + key + "  Value="+value);
			return value;
		}catch(Exception e){
			log.error("error Key=" + key, e);
			throw e;
		}
	}
	public String getConstant(String key,String defaultValue){
		String value=null;
		try{
			value = resourceBundle.getString(key);
//			log.info( " Key=" + key + "  Value="+value);
		}catch(Exception e){
			log.error("error Key=" + key, e);
			return defaultValue;
		}
		return value;
	}

//	public Iterator<String> getKeys(){
//		Set<String> set = resourceBundle..keySet();
//		if(set != null && set.size() > 0)
//			return set.iterator();
//		return null;
//	}
}

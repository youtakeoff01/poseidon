package com.hand.bdss.web.common.util;

import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

/**
 * 操作properties文件的工具类
 * 
 * @author Administrator
 */
public class PropertiesOperationUtils {

	/**
	 * 加载properties中的数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public Properties loadData(String prop,String code) throws Exception {
		Properties pro = new Properties();
//		String path = this.getClass().getClassLoader().getResource(prop).getPath();
//		InputStream is = null;
		InputStreamReader is = null;
		try {
//			is = new FileInputStream(path);
			is = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(prop),code);
			pro.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				is.close();
		}
		return pro;
	}

	/**
	 * 将数据写入ldapconnect.properties中
	 * 
	 * @param args
	 */
	public void writeData(Map<String, String> map) throws Exception {
		Properties pro = new Properties();
		String filePath = this.getClass().getResource("/") + "ldapconnect.properties";
		filePath = filePath.substring(6);
		FileOutputStream oFile = new FileOutputStream(filePath, false);// true表示追加打开
		pro.setProperty("rootDN", map.get("rootDN"));
		pro.setProperty("ldapUrl", map.get("ldapUrl"));
		pro.setProperty("domain", map.get("domain"));
		pro.setProperty("user", map.get("user"));
		pro.setProperty("ldapPwd", map.get("ldapPwd"));
		pro.store(oFile, null);
		oFile.close();
	}

	/**
	 * 将数据写入ambari.properties中
	 * 
	 * @param args
	 */
	public void writeAmbariData(Map<String, String> map) throws Exception {
		Properties pro = new Properties();
		String filePath = this.getClass().getResource("/") + "ambari.properties";
		filePath = filePath.substring(6);
		FileOutputStream oFile = new FileOutputStream(filePath, false);// true表示追加打开
		pro.setProperty("ambari_uri", map.get("ambari_uri"));
		pro.setProperty("ambari_user", map.get("ambari_user"));
		pro.setProperty("ambari_passwd", map.get("ambari_passwd"));
		pro.setProperty("cluster_name", map.get("cluster_name"));
		pro.store(oFile, null);
		oFile.close();
	}

	/*
	 * public static void main(String[] args){ try { // new
	 * PropertiesOperationUtils().loadData();
	 * 
	 * Map<String,String> map = new HashMap<String,String>(); map.put("rootDN",
	 * "123"); map.put("ldapUrl", "456"); map.put("domain", "099"); map.put("user",
	 * "45645"); map.put("ldapPwd", "trt"); new
	 * PropertiesOperationUtils().writeData(map); // ClassLoader loader =
	 * PropertiesOperationUtils.class.getClassLoader(); } catch (Exception e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); } }
	 */

}

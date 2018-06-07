package com.hand.bdss.web.dataprocessing.splunkapi.service;

import java.util.Date;


public interface SplunkService {
	
	/**
	 * 通过调用splunkapi获取邮箱是否已满信息
	 */
	void getEmailMsgFromSplunkByTimer();
	/**
	 * 通过设置定时器调用splunk接口,获取用户email日志信息
	 */
	void getEmailLogFromSplunkByTimer();
	/**
	 * 通过设置定时器调用splunk接口,获取用户pc机登录的日志
	 */
	void getPCLogFromSplunkByTimer();
	
	/**
	 * 通过调用splunk的restApi获取信息
	 * 
	 * @param startTime
	 * @param endTime
	 * @param indexName
	 * @return
	 */
	String getLogMsgFromSplunk(Date startTime, Date endTime, String indexName) throws Exception;
	
	/**
	 * 根据索引从splunk中获取数据存放到对应的hdfs路径
	 * @param cls
	 * @param serverName
	 * @param IndexName
	 * @param hdfsRoute
	 * @return
	 */
	<T> void getLogMsgByIndex(Class<T> cls,String serverName,String IndexName,String FileName,String fileType,String hdfsfilePath,String hiveTableName) throws Exception;
	/**
	 * 将数据写入到对应的hdfs里面
	 * @param datas
	 * @param hdfsRoute
	 * @return
	 */
	<T> boolean insertMsgToHDFS(String diskPath, String hdfsPath,String hdfsFileName);
	
	
}

package com.hand.bdss.web.dataprocessing.splunkapi.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.dsmp.component.hdfs.HDFSClient;
import com.hand.bdss.dsmp.component.hive.HiveClient;
import com.hand.bdss.dsmp.exception.DataServiceException;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.util.PropertiesOperationUtils;
import com.hand.bdss.web.dataprocessing.splunkapi.dao.SplunkTaskDao;
import com.hand.bdss.web.dataprocessing.splunkapi.service.SplunkService;
import com.hand.bdss.web.entity.ADAuditInfoEntity;
import com.hand.bdss.web.entity.EmailStatusEntity;
import com.hand.bdss.web.entity.ExchangeInfo;
import com.hand.bdss.web.entity.LastPullDateEntity;
import com.splunk.Job;
import com.splunk.JobResultsArgs;
import com.splunk.SSLSecurityProtocol;
import com.splunk.SavedSearch;
import com.splunk.SavedSearchDispatchArgs;
import com.splunk.Service;
import com.splunk.ServiceArgs;

@org.springframework.stereotype.Service("splunkServiceImpl")
public class SplunkServiceImpl implements SplunkService {

	@Resource
	private SplunkTaskDao splunkTaskDaoImpl;

	@Resource
	private HDFSClient hDFSClient;

	private static final Logger logger = LoggerFactory.getLogger(SplunkServiceImpl.class);

	private static  String SPLUNK_NAME;
	private static  String SPLUNK_PWD;
	private static  String SPLUNK_URL;
	private static  int SPLUNK_PORT;
	private static final String SERVER_NAME_EMAILLOG = "splunkEmailLog";
	private static final String SERVER_NAME_PCLOG = "splunkPCLog";
	private static final String SERVER_NAME_EMAILSTATUSLOG = "splunkEmailStatusLog";

	private static final String INDEX_NAME_EMAILLOG = "exchange_login";// email 对应的log信息的index
	private static final String INDEX_NAME_PCLOG = "ad_audit_info";// pc机登录对应的log信息的index
	private static final String INDEX_NAME_EMAILSTATUSLOG = "mailbox"; // 邮箱状态信息的index

	private static final String FILE_NAME_PCLOG = "splunkDataPCLog";
	private static final String FILE_NAME_EMAILLOG = "splunkDataEmailLog";
	private static final String FILE_NAME_EMAILSTATUSLOG = "splunkDataEmailStatusLog";

	private static final String HDFS_PATH_PCLOG = "/dsmp/hdfs/splunk/data/pcLog/";
	private static final String HDFS_PATH_EMAILLOG = "/dsmp/hdfs/splunk/data/emailLog/";
	private static final String HDFS_PATH_EMAILSTATUSLOG = "/dsmp/hdfs/splunk/data/emailStatusLog/";

	private static final String PCLOG_HIVE_TABLE_NAME = "bigdata_ld.pc_log_table";
	private static final String ELOG_HIVE_TABLE_NAME = "bigdata_ld.email_log_table";
	private static final String ESTATUS_HIVE_TABLE_NAME = "bigdata_ld.email_status_table";
//	private static final String DISK_PATH = "/home/hive/splunk/datas/";
	private static final String DISK_PATH = "/storage/splunk/datas/";
	private static final String FILE_TYPE = ".txt";
	
	static {
		try {
			Properties per = new PropertiesOperationUtils().loadData("splunkconnect.properties","UTF-8");
			SPLUNK_NAME = per.getProperty("SPLUNK_NAME");
			SPLUNK_PWD = per.getProperty("SPLUNK_PWD");
			SPLUNK_URL = per.getProperty("SPLUNK_URL");
			String port = per.getProperty("SPLUNK_PORT");
			SPLUNK_PORT = Integer.parseInt(port);
		} catch (Exception e) {
			logger.error("load splunk data error :", e);
		}
	}

	/**
	 * 通过调用splunkapi获取邮箱是否已满信息
	 */
	@Scheduled(cron = "0 */2 * * * ?")
	@Override
	public void getEmailMsgFromSplunkByTimer() {
		try {
			getLogMsgByIndex(EmailStatusEntity.class, SERVER_NAME_EMAILSTATUSLOG, INDEX_NAME_EMAILSTATUSLOG,
					FILE_NAME_EMAILSTATUSLOG, FILE_TYPE, HDFS_PATH_EMAILSTATUSLOG, ESTATUS_HIVE_TABLE_NAME);
		} catch (Exception e) {
			logger.error("getEmailMsgFromSplunkByTimer error,error msg is:", e);
		}
	}

	/**
	 * 通过设置定时器调用splunk接口,获取email登录信息的日志
	 */
	@Scheduled(cron = "0 */4 * * * ?")
	@Override
	public void getEmailLogFromSplunkByTimer() {
		try {
			getLogMsgByIndex(ExchangeInfo.class, SERVER_NAME_EMAILLOG, INDEX_NAME_EMAILLOG, FILE_NAME_EMAILLOG,
					FILE_TYPE, HDFS_PATH_EMAILLOG, ELOG_HIVE_TABLE_NAME);
		} catch (Exception e) {
			logger.error("getEmailLogFromSplunkByTimer error,error msg is:", e);
		}
	}

	/**
	 * 通过设置定时器调用splunk接口,获取用户pc机登录的日志 十五分钟调一次接口
	 */
	@Scheduled(cron = "0 */3 * * * ?")
	@Override
	public void getPCLogFromSplunkByTimer() {
		try {
			getLogMsgByIndex(ADAuditInfoEntity.class, SERVER_NAME_PCLOG, INDEX_NAME_PCLOG, FILE_NAME_PCLOG, FILE_TYPE,
					HDFS_PATH_PCLOG, PCLOG_HIVE_TABLE_NAME);
		} catch (Exception e) {
			logger.error("getPCLogFromSplunkByTimer error,error msg is:", e);
		}
	}

	/**
	 * 通过调用splunk的restApi获取信息
	 * 
	 * @param startTime
	 * @param endTime
	 * @param indexName
	 * @return
	 */
	@Override
	synchronized public String getLogMsgFromSplunk(Date startTime, Date endTime, String indexName) throws Exception {
		if (StringUtils.isEmpty(indexName)) {
			throw new Exception("index name is empty！");
		}
		ServiceArgs loginArgs = new ServiceArgs();
		loginArgs.setUsername(SPLUNK_NAME);
		loginArgs.setPassword(SPLUNK_PWD);
		loginArgs.setHost(SPLUNK_URL);
		loginArgs.setPort(SPLUNK_PORT);
		Service.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);
		Service service = Service.connect(loginArgs);
		service.login();
		SavedSearchDispatchArgs dispatchArgs = new SavedSearchDispatchArgs();
		if (startTime != null) {
			dispatchArgs.setDispatchEarliestTime(startTime);
		}
		if (endTime != null) {
			dispatchArgs.setDispatchLatestTime(endTime);
		}
		SavedSearch savedSearch = service.getSavedSearches().get(indexName);
		Job jobSavedSearch = null;
		jobSavedSearch = savedSearch.dispatch(dispatchArgs);
		// Wait for the job to finish
		while (!jobSavedSearch.isDone()) {
			Thread.sleep(50);
		}
		JobResultsArgs resultsArgs = new JobResultsArgs();
		resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);
		resultsArgs.setCount(0);
		InputStream results = jobSavedSearch.getResults(resultsArgs);
		String line = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(results, "UTF-8"));
		StringBuffer resultStr = new StringBuffer();
		while ((line = br.readLine()) != null) {
			resultStr.append(line);
		}
		return resultStr.toString();
	}

	/**
	 * 根据索引从splunk中获取数据存放到对应的hdfs路径
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public <T> void getLogMsgByIndex(Class<T> cls, String serverName, String indexName, String fileName,
			String fileType, String hdfsfilePath, String hiveTableName) throws Exception {
		boolean sameDay = false;
		LastPullDateEntity lastPullDateEntity = new LastPullDateEntity();
		lastPullDateEntity.setServerName(serverName);
		// 1.查询数据库，获取最后拉取时间
		LastPullDateEntity splunk = splunkTaskDaoImpl.getLastPullDate(lastPullDateEntity);
		String json = null;
		Calendar endDate = Calendar.getInstance();
		Calendar lastPullTime = Calendar.getInstance();
		if (splunk == null) {
			sameDay = true;
			json = getLogMsgFromSplunk(endDate.getTime(), endDate.getTime(), indexName);
		}
		if (splunk != null) {// 根据最后拉取时间进行增量拉取
			Date lastPullDate = splunk.getLastPullTime();
			lastPullTime.setTime(lastPullDate);
			endDate.setTime(lastPullDate);
			endDate.add(Calendar.MINUTE, 10);// 增加10分钟,每次拉取10分钟的数据，针对于全量数据，增量数据看定时器的时间
			logger.info("lastPullTime:" + lastPullTime.getTime() + "#####endDate:" + endDate.getTime());
			Calendar date = Calendar.getInstance();
			if (date.before(endDate)) {// 如果增加10分钟之后的时间大于当前时间，则拉取截止时间改为当前时间
				endDate = date;
			}
			// 判断拉取的起始时间和终止时间是否在一天之内，主要是要保证一次拉取的数据是在同一天之内。
			if (!isSameDay(endDate, lastPullTime)) {// 如果不是同一天，那要变成同一天
				endDate.set(lastPullTime.get(Calendar.YEAR), lastPullTime.get(Calendar.MONTH),
						lastPullTime.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			}
			json = getLogMsgFromSplunk(lastPullTime.getTime(), endDate.getTime(), indexName);
		}
		if (json != null) {
			JSONObject jsonObject = JSON.parseObject(json);
			String result = jsonObject.getString("results");
			if (StringUtils.isNotEmpty(result)) {
				// 修改filename
				fileName = fileName + "-" + endDate.get(Calendar.YEAR) + (endDate.get(Calendar.MONTH) + 1)
						+ endDate.get(Calendar.DAY_OF_MONTH) + fileType;
				// 更新表中的拉取时间
				endDate.add(Calendar.SECOND, 1);// 秒数加一，下次拉取从下一秒开始获取。
				// 如果下次拉取时间和lastPullTime不在同一天，则说明下次拉取的数据需要重新产生一个文件。
				sameDay = isSameDay(endDate, lastPullTime);
				lastPullDateEntity.setLastPullTime(endDate.getTime());
				lastPullDateEntity.setUpdateTime(new Date());
				List<T> resultLists = JsonUtils.toArray(result, cls);
				if (resultLists != null && resultLists.size() > 0) {
					// 将数据保存到本地磁盘上
					uploadDataToLocDisk(resultLists, DISK_PATH, fileName);
					String path = hdfsfilePath + "year=" + lastPullTime.get(Calendar.YEAR) + "/month="
							+ (lastPullTime.get(Calendar.MONTH) + 1) + "/day="
							+ lastPullTime.get(Calendar.DAY_OF_MONTH);
					// 先删除hdfs上对应目录中的数据
					if (hDFSClient.deleteFile(fileName, path)) {
						// 将磁盘上的数据上传到hdfs上
						boolean boo = insertMsgToHDFS(DISK_PATH + fileName, path, fileName);
						if (!sameDay && boo) {// 如果不是同一天，则删除本地文件，下次写入就会产生新的文件
							File f = new File(DISK_PATH + fileName);
							if (f.exists()) {
								f.delete();
							}
						}
						// 将数据指定的目录映射到hive中对应的分区
						loadDateToHive(hiveTableName, lastPullTime.get(Calendar.YEAR),
								(lastPullTime.get(Calendar.MONTH) + 1), lastPullTime.get(Calendar.DAY_OF_MONTH));
					}
				}
				// 更新表中的拉取时间
				splunkTaskDaoImpl.updateLastPullDate(lastPullDateEntity);
			}
		}
	}

	/**
	 * load date to hive
	 * 
	 * @param string
	 * @param dateStr
	 */
	private void loadDateToHive(String hiveTableName, int year, int month, int day) {
		HiveClient hiveClient = new HiveClient("hive");
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = hiveClient.getConnection();
			stmt = conn.createStatement();
			String sql = "alter table " + hiveTableName + " add if not exists partition(year='" + year + "',month='"
					+ month + "',day='" + day + "')";
			stmt.execute(sql);
		} catch (DataServiceException | SQLException e) {
			logger.error("loadDateToHive is error,error msg is ", e);
		} finally {
			hiveClient.closeConnection();
		}
	}
	/*
	 * private void loadDateToHive(String hdfsPath, String hiveTableName, int year,
	 * int month, int day) { HiveClient hiveClient = new HiveClient("hive");
	 * Connection conn = null; Statement stmt = null; try { conn =
	 * hiveClient.getConnection(); stmt = conn.createStatement(); // 先删除分区表该分区中的数据
	 * String path = hdfsPath.substring(0, hdfsPath.lastIndexOf("/") + 1) + "year="
	 * + year + "/month=" + month + "/day=" + day; // 再加载新的数据到分区中 if
	 * (hDFSClient.deleteDirectoy(path)) { String sql = "load data inpath '" +
	 * hdfsPath + "' into table " + hiveTableName + " partition(year='" + year +
	 * "',month='" + month + "',day='" + day + "')"; stmt.execute(sql); } } catch
	 * (DataServiceException | SQLException e) {
	 * logger.error("loadDateToHive is error,error msg is", e); } finally {
	 * hiveClient.closeConnection(); } }
	 */

	/**
	 * 判断两个时间是否是同一天
	 * 
	 * @param date
	 * @param lastPullTime
	 * @return
	 */
	private boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 != null && cal2 != null) {
			return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
		} else {
			throw new IllegalArgumentException("The date must not be null");
		}
	}

	/**
	 * 将数据写入到对应的hdfs里面
	 */
	@Override
	public boolean insertMsgToHDFS(String diskPath, String hdfsPath, String hdfsFileName) {
		return hDFSClient.insertDatas(diskPath, hdfsPath, hdfsFileName);
	}

	/**
	 * 将数据存放到本地文件中
	 * 
	 * @param datas
	 * @param path
	 * @return true表示删除了老文件,产生新的文件.false表示还是在老的文件里面追加写
	 * @throws IOException
	 */
	public <T> void uploadDataToLocDisk(List<T> datas, String path, String fileName) throws IOException {
		File f = null;
		File f2 = null;
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			f = new File(path);
			f2 = new File(path + fileName);
			if (!f.exists()) {
				f.setWritable(true, false);// 设置写权限，windows下不用此语句
				f.mkdirs();
			}
			if (!f2.exists()) {
				f2.setWritable(true, false);
				f2.createNewFile();
			}
			fw = new FileWriter(f2, true);
			pw = new PrintWriter(fw);
			for (T data : datas) {
				pw.println(data.toString());
			}
			pw.flush();
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
			if (fw != null) {
				fw.close();
			}
		}
	}
}

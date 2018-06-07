package com.hand.bdss.web.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hand.bdss.web.entity.LogEntity;
import com.hand.bdss.web.platform.management.service.LogService;

/**
 * 记录日志的工具类
 * 
 * @author Administrator
 *
 */
@Component
public class LogUtils {
	public static final String LOGTYPE_SYS = "系统日志";
	public static final String LOGTYPE_USER = "用户日志";
	public static final String LOGTYPE_SQL = "SQL执行日志";
	@Resource
	LogService logServiceImpl;

	public void writeLog(String logContent, String logType, String userName) {
		writeLog(logContent,logType,userName,null);
	}
	
	public void writeLog(String logContent, String logType, String userName,String status){
		LogEntity log = new LogEntity();
		log.setLogContent(logContent);
		log.setLogType(logType);
		log.setUserName(userName);
		log.setStatus(status);
		try {
			logServiceImpl.insertLog(log);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getTrace(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		t.printStackTrace(printWriter);
		StringBuffer buffer = stringWriter.getBuffer();
		return buffer.toString();
	}
}

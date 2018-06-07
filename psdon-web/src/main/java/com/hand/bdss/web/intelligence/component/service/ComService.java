package com.hand.bdss.web.intelligence.component.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.web.intelligence.component.vo.AIOptsVO;
import com.hand.bdss.web.intelligence.component.vo.MenuTree;

/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习组件service接口
 */
public interface ComService{
	
	public List<MenuTree> listTree() throws Exception;
	
	List<AIOptsVO> listOpts(String data) throws Exception;
	
	boolean saveAITask(String str,HttpServletRequest request) throws Exception;
	
	Map<String,Object> executeAITask(Task task,HttpServletRequest req) throws Exception;
	
	boolean executeAIConfig(String data,HttpServletRequest request) throws Exception;
	
    boolean updateAITask(String data,HttpServletRequest req) throws Exception;

	Map<String, Object> getAIData(String data) throws Exception;

	String getAILog(String data) throws Exception;

	String getComAILog(String data) throws Exception;

	boolean deleteAITask(Task task, HttpServletRequest req) throws Exception;

	List<Map<String, String>> getPreOpts(String data, HttpServletRequest req) throws Exception;

	List<Map<String, String>> getHiveTables(String dbName) throws Exception;

	List<Map<String, String>> getHiveDatabases() throws Exception;

	Map<String,Object> getExecReport(String taskName) throws Exception;

}

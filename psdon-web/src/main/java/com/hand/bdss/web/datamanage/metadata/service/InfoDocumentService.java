package com.hand.bdss.web.datamanage.metadata.service;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.dsmp.model.DocumentModel;


public interface InfoDocumentService {
	
	/**
	 * 文件上传
	 * @paramsrc 文件路径+名称
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> uploadFile(InputStream in, String srcPath,HttpServletRequest request) throws Exception;
	
	/**
	 * 文件下载 form HDFS
	 * @param fileName : 文件名称
	 * @param filePath : 文件路径
	 * @return
	 * @throws Exception
	 * 
	 */
	public Map<String, Object> downloadFile(String srcPath) throws Exception;	
	
	/**
	 * 文件删除 form HDFS
	 * @param fileName
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> delete(String srcPath) throws Exception;
	/**
	 * 校验是否是文件夹
	 * @param srcPath
	 * @return
	 * @throws IOException
	 */
	public boolean isDirectory(String srcPath) throws IOException;
	/**
	 * 创建文件夹
	 * @param
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> createDirectoy(String srcPath) throws Exception;
	
	/**
	 * 获取文件子目录
	 * @return
	 * @throws Exception
	 */
	public List<DocumentModel> getDirectoyAndFileTree(String srcPath, String username)  throws Exception;
	
	/**
	 * 获取文件树
	 * @return
	 * @throws Exception
	 */
	public DocumentModel getDocumentTree(String srcPath, String username)  throws Exception;
	
	/**
	 * 文件复制
	 * @param fileName : 文件名称
	 * @param filePath : 文件路径
	 * @return
	 * @throws Exception
	 * 
	 */
	public Map<String, String> copyFile(String srcPath, String dstPath, String fileName,HttpServletRequest request) throws Exception;
	
	/**
	 * 文件移动
	 * @param fileName : 文件名称
	 * @param filePath : 文件路径
	 * @return
	 * @throws Exception
	 * 
	 */
	public Map<String, String> moveFile(String srcPath, String dstPath, String fileName) throws Exception;
     /**
      * 校验csv文件内容是否满足要求
      * @param in
     * @param request 
      * @return
      */
	public Map<String, Object> csvContentCheck(InputStream in, HttpServletRequest request) throws Exception;
	
}

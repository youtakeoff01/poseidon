package com.hand.bdss.dsmp.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.model.ETLJobConfig;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * 
 * 封装访问Azkaban的api类
 * @author tc
 *
 */
public class AzkabanHelper {

	private static Connection connection =null;
	private static Session session =null;
	private static String sessionId = null;
	public static void main(String[] args) throws IOException, InterruptedException {
		ETLJobConfig etlJob = new ETLJobConfig();
		etlJob.setJobName("tancheng_");
		etlJob.setJobName("testapi");
		
		loginAndGetSessionID();
		//createProject(etlJob);
//		uploadProject(etlJob);
		//executeProject(etlJob);
//		createProject(etlJob);
//		uploadProject(etlJob);
//		executeProject(etlJob);
	}
	
	/**
	 * 执行project
	 * @throws IOException 
	 */
	public static void executeProject(ETLJobConfig etlJob) throws IOException{
		init();
		
		String executeCurlCommand = "curl -k --get --data 'session.id="+sessionId+"' --data 'ajax=executeFlow' --data 'project="+etlJob.getJobName()+"' --data 'flow="+etlJob.getJobName()+"' "+SystemConfig.AZKABAN_URL+"/executor";
		System.out.println("executeCurlCommand:"+executeCurlCommand);
		try {
			session.execCommand(executeCurlCommand);
			String responseString = getStatusResult();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			closeSession();
			closeConnection();
		}
	}
	
	/**
	 * 上传project
	 * @throws IOException 
	 */
	public static void uploadProject(ETLJobConfig etlJob) throws IOException{
		init();
		
		String uploadCurlCommand = "curl -k -i -H \"Content-Type: multipart/mixed\" -X POST --form 'session.id="+sessionId+"' --form 'ajax=upload' --form 'file=@E:\\test\\testetl\\testetl"+".zip;type=application/zip' --form 'project="+etlJob.getJobName()+//";type/plain"+
				"' "+SystemConfig.AZKABAN_URL+"/manager?ajax=upload";
//		String uploadCurlCommand = "curl -k -i -H \"Content-Type:multipart/mixed\" -X POST --form 'session.id="+sessionId+"' --form 'ajax=upload' --form 'file=@"+"C:/Users/hand/Desktop/tool/tancheng"+".zip;type=application/zip' --form 'project="+etlJob.getJobName()+
//				"' "+SystemConfig.AZKABAN_URL+"/manager";
		System.out.println("uploadCurlCommand:"+uploadCurlCommand);
		try {
			session.execCommand(uploadCurlCommand);
			String responseString = getStatusResult();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			closeSession();
			closeConnection();
		}
	}
	
	/**
	 * 创建project
	 * @throws IOException 
	 */
	public static void createProject(ETLJobConfig etlJob) throws IOException{
		init();
		String createCurlCommand = "curl -k -X POST --data \"session.id="+sessionId+"&name="+etlJob.getJobName()+"&description="+etlJob.getJobName()+
				"\" " +SystemConfig.AZKABAN_URL+"/manager?action=create";
		System.out.println("createCurlCommand:"+createCurlCommand);
		try {
			session.execCommand(createCurlCommand);
			String responseString = getStatusResult();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			closeSession();
			closeConnection();
		}
	}
	
	/**
	 * 
	 * @param 
	 * @return sessionId
	 * @throws IOException 
	 */
	public static void loginAndGetSessionID() throws IOException{
		init();
		String loginCurlCommand = "curl -k -X POST --data \"action=login&username="+SystemConfig.AZKABAN_USERNAME2+"&password="+SystemConfig.AZKABAN_PASSWORD2+
				"\" " +SystemConfig.AZKABAN_URL;
		System.out.println("loginCurlCommand:"+loginCurlCommand);
		try {
			
			session.execCommand(loginCurlCommand);
			
			String responseString = getStatusResult();
			JSONObject jsStr = JSONObject.parseObject(responseString);
			if(jsStr.getString("status").equals("success")){
				sessionId=  jsStr.getString("session.id"); //获取sessionID
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeSession();
			closeConnection();
		}
		
	}
	
	
	/**
	 * 初始化连接Linux配置
	 * @param 
	 * @throws IOException
	 */
	public static void init() throws IOException{
		connection = new Connection(SystemConfig.AZKABAN_HOSTNAME);
		connection.connect();
		
		/* Authenticate.
		 * If you get an IOException saying something like
		 * "Authentication method password not supported by the server at this stage."
		 * then please check the FAQ.
		 */
		boolean isAuthenticated = connection.authenticateWithPassword(SystemConfig.AZKABAN_USERNAME, SystemConfig.AZKABAN_PASSWORD);

		if (isAuthenticated == false)
			throw new IOException("Authentication failed.");

		/* Create a session */

		 session = connection.openSession();
	}
	
	public static String getStatusResult() throws IOException{
		/* 
		 * This basic example does not handle stderr, which is sometimes dangerous
		 * (please read the FAQ).
		 */

		InputStream stdout = new StreamGobbler(session.getStdout());

		BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

		StringBuffer bs = new StringBuffer();
		while (true)
		{
			String line = br.readLine();
			if (line == null)
				break;
			//System.out.println(line);
			bs.append(line);
		}
		stdout.close();
		br.close();
		System.out.println("BS:"+bs);
		return bs.toString();
	}
	
	
	private synchronized static void closeConnection() {
		if (connection != null) {
			connection.close();
		}
		connection = null;
	}
	
	private synchronized static void closeSession() {
		if (session != null) {
			session.close();
		}
		session = null;
	}

}

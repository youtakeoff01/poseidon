package com.hand.bdss.web.common.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hand.bdss.dsmp.config.SystemConfig;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习 远程访问Python接口
 */
public class AIPythonVO {
	private static final Logger logger = LoggerFactory.getLogger(AIPythonVO.class);
    private static final String PYTHON_REST_URL = SystemConfig.PYTHON_REST_URL;   
    private static final String EXPECTED_TYPE_JSON = "application/json";   
    
    private  Client client = null;
    private  ClientResponse response = null;
    private  WebResource webResource = null;
    
    public void init(String url){
    	logger.info("AICore.init start! url = {"+url+"}");
    	client = Client.create();
    	logger.info("AICore.init end!");
    }
    
    /**
     * 调用python接口 执行任务
     */
    public boolean restPythonForTask(String restParam,String port) throws Exception{
    	logger.info("AICore.restPythonForTask start! param = {}",port);
    	String BASE_URL = PYTHON_REST_URL;
    	//针对不同端口调用
    	if(port != null && !"".equals(port)){
    		BASE_URL = BASE_URL.substring(0,BASE_URL.lastIndexOf(":") + 1) + port;
    	}
        boolean flag = false;
    	try{
    		init(BASE_URL+"/tasks/test");
        	webResource = client.resource(BASE_URL+"/tasks/test");
            response = webResource.accept(EXPECTED_TYPE_JSON).type(EXPECTED_TYPE_JSON).post(ClientResponse.class, restParam);
            if(response != null && response.getStatus() == 200){
                String resFalg = response.getEntity(String.class);
                if(resFalg != null && "ok".equalsIgnoreCase(resFalg)){
                	flag = true;
                }
                logger.info("response data:" + resFalg);
            }else {
                logger.info("response status:" + response.getStatus());
            }
    	}catch(Exception e){
    		logger.error("AICore.restPythonForTask error !",e);
    		throw new Exception("调用python api执行任务出错...");
    	}finally{
    		closeClient();
        	closeResponse();
    	}
        logger.info("AICore.restPythonForTask end!");
        return flag;
    }
    
    /**
     * 调用python接口   删除任务
     * @param restParam
     * @return
     * @throws Exception
     */
    public boolean restPythonForDelete(String restParam) throws Exception{
    	logger.info("AICore.restPythonForDelete start! param = {}",restParam);
    	String BASE_URL = PYTHON_REST_URL;
        boolean flag = false;
    	try{
    		init(BASE_URL+"/tasks/deltask");
        	webResource = client.resource(BASE_URL+"/tasks/deltask");
            response = webResource.accept(EXPECTED_TYPE_JSON).type(EXPECTED_TYPE_JSON).post(ClientResponse.class, restParam);
            if(response != null && response.getStatus() == 200){
                String resFalg = response.getEntity(String.class);
                if(resFalg != null && "success".equalsIgnoreCase(resFalg)){
                	flag = true;
                }
                logger.info("response data:" + resFalg);
            }else {
                logger.info("response status:" + response.getStatus());
            }
    	}catch(Exception e){
    		logger.error("AICore.restPythonForDelete error !",e);
    		throw new Exception("调用python api删除任务出错...");
    	}finally{
    		closeClient();
        	closeResponse();
    	}
        logger.info("AICore.restPythonForDelete end!");
        return flag;
    }
    
    /**
     * 调用python接口 读取日志
     * @param restParam
     * @return
     * @throws Exception
     */
    public String restPythonForLog(String restParam,String pyPort) throws Exception{
    	logger.info("AICore.restPythonForLog start! param = {},{}",restParam,pyPort);
    	String BASE_URL = PYTHON_REST_URL;
    	//针对不同端口调用
    	if(pyPort != null && !"".equals(pyPort)){
    		BASE_URL = BASE_URL.substring(0,BASE_URL.lastIndexOf(":") + 1) + pyPort;
    	}
    	try{
    		init(BASE_URL + "/tasks/logcontent");
        	webResource = client.resource(BASE_URL + "/tasks/logcontent");
            response = webResource.accept(EXPECTED_TYPE_JSON).type(EXPECTED_TYPE_JSON).post(ClientResponse.class,restParam);
            if(response != null && response.getStatus() == 200){
            	String resString = response.getEntity(String.class);
            	return resString;
            }
    	}catch(Exception e){
    		logger.info("AICore.restPythonForLog error",e);
    		throw new Exception("调用python api读取日志出错...");
    	}finally{
    		closeResponse();
    		closeClient();
    	}
    	logger.info("AICore.restPythonForLog end");
    	return null;
    }
    
    /**
     * 调用python接口 读取组件日志
     * @param jsonString
     * @param pyPort
     * @return
     * @throws Exception 
     */
	public String restPythonForComLog(String restParam, String pyPort) throws Exception {
		logger.info("AICore.restPythonForComLog start! param = {},{}",restParam,pyPort);
    	String BASE_URL = PYTHON_REST_URL;
    	//针对不同端口调用
    	if(pyPort != null && !"".equals(pyPort)){
    		BASE_URL = BASE_URL.substring(0,BASE_URL.lastIndexOf(":") + 1) + pyPort;
    	}
    	try{
    		init(BASE_URL + "/tasks/nodecontent");
        	webResource = client.resource(BASE_URL + "/tasks/nodecontent");
            response = webResource.accept(EXPECTED_TYPE_JSON).type(EXPECTED_TYPE_JSON).post(ClientResponse.class,restParam);
            if(response != null && response.getStatus() == 200){
            	String resString = response.getEntity(String.class);
            	return resString;
            }
    	}catch(Exception e){
    		logger.info("AICore.restPythonForComLog error",e);
    		throw new Exception("调用python api读取组件日志出错...");
    	}finally{
    		closeResponse();
    		closeClient();
    	}
    	logger.info("AICore.restPythonForComLog end");		
    	return null;
	}
    
    private synchronized void closeClient() {
		if (client != null) {
			client.destroy();
		}
		client = null;
	}
	
	private synchronized void closeResponse() {
		if (response != null) {
			response.close();
		}
		response = null;
	}

	
}

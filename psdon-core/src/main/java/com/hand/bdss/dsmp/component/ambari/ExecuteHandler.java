package com.hand.bdss.dsmp.component.ambari;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.metrics.DataServiceMetrics;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * 
 * @author tancheng
 *
 */
public class ExecuteHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(ExecuteHandler.class);

	private static final String EXPECTED_MIME_TYPE = "text/plain";//"application/json";
	
	private  Client client = null;
    private  ClientResponse response = null;
    private  WebResource webResource = null;
    private String clusterName = null;
    public  void initVerify(String url){
		logger.info("ExecuteHandler.initVerify start ");
		client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(SystemConfig.AMBARI_USERNAME, SystemConfig.AMBARI_PASSWORD));
        System.out.println(url);
		logger.info("ExecuteHandler.initVerify end");
	}
    
	public  String getConfig(String type){
		logger.info("ExecuteHandler.getConfig start ");
		String properties1 = null;
		clusterName = new DataServiceMetrics().getClusterName();
		System.out.println("clusterName ============"+clusterName);
        try {
        	initVerify(SystemConfig.AMBARI_URL+"/api/v1/clusters/"+clusterName+"?fields=Clusters/desired_configs");
        	webResource = client.resource(SystemConfig.AMBARI_URL+"/api/v1/clusters/"+clusterName+"?fields=Clusters/desired_configs");
        	response = webResource.accept(EXPECTED_MIME_TYPE).get(ClientResponse.class);
            String tag =null;
            if(response.getStatus() == 200) {
            	
            	String responseAsString = response.getEntity(String.class);
            	JSONObject jsonObject = JSONObject.parseObject(responseAsString);
                JSONObject clusters = jsonObject.getJSONObject("Clusters");
                JSONObject desired_configs = clusters.getJSONObject("desired_configs");
                JSONObject properties = desired_configs.getJSONObject(type);//flume-conf
                tag = properties.getString("tag");
                System.out.println(tag);
                
            }else {
            	System.out.println(response.getStatus());
            	logger.info("ExecuteHandler.getConfig back null");
            }
            
           //第二次请求Ambari获取配置属性
            webResource = client.resource(SystemConfig.AMBARI_URL+"/api/v1/clusters/"+clusterName+"/configurations?type="+type+"&tag="+tag);
        	response = webResource.accept(EXPECTED_MIME_TYPE).get(ClientResponse.class);
            
            if(tag !=null &&response.getStatus() == 200){
            	String responseAsString2= response.getEntity(String.class);
            	JSONObject jsonObject1 = JSONObject.parseObject(responseAsString2);
                JSONArray items = jsonObject1.getJSONArray("items");
                Iterator<Object> iterator = items.iterator();
                while (iterator.hasNext()) {
                    JSONObject next = (JSONObject) iterator.next();
                    properties1 = next.getString("properties");
                    System.out.println(properties1);
                }
                
            }
        } catch (Exception e) {
			logger.error("获取集群集群时参数异常,服务器状态:"+response.getStatus(), e);
		}finally {
        	closeClient();
        	closeResponse();
        }
        logger.info("ExecuteHandler.getConfig end");
        return properties1;
	}
	
	public  void executeService(){
		logger.info("ExecuteHandler.executeService start ");
		String hostname = SystemConfig.FLUME_HOSTNAME;
		clusterName = new DataServiceMetrics().getClusterName();
        try {
        	initVerify(SystemConfig.AMBARI_URL+"/api/v1/clusters/"+clusterName+"/requests");
        	webResource = client.resource(SystemConfig.AMBARI_URL+"/api/v1/clusters/"+clusterName+"/requests");
        	  response = webResource.accept(EXPECTED_MIME_TYPE).header("X-Requested-By", "ambari")
                    .post(ClientResponse.class,  "{\"RequestInfo\":{\"context\":\"Restart Flume\", \"command\":\"RESTART\"},\"Requests/resource_filters\":[{\"service_name\":\"FLUME\",\"component_name\": \"FLUME_HANDLER\",\"hosts\": \""+hostname+"\"}]}");
            String tag =null;
            if(response.getStatus() == 202) {
            	
            	String responseAsString = response.getEntity(String.class);
                System.out.println(responseAsString);
                
            }else {
            	System.out.println(response.getStatus());
            	logger.info("ExecuteHandler.executeService back null");
            }
          
        } catch (Exception e) {
			logger.error("获取集群集群时参数异常,服务器状态:"+response.getStatus(), e);
		}finally {
        	closeClient();
        	closeResponse();
        }
        logger.info("ExecuteHandler.executeService end");
	}
	

//	/**
//	 * 
//	 * @param content
//	 * @return
//	 */
//	public void updateConfig(String content) throws IOException{
//		logger.info("ExecuteHandler.updateConfig start ");
//        try {
//        	initVerify(SystemConfig.AMBARI_URL+"/api/v1/clusters/"+clusterName+"");
//        	
//        	webResource = client.resource(SystemConfig.AMBARI_URL+"/api/v1/clusters/"+clusterName+"");
//            response = webResource.accept(EXPECTED_MIME_TYPE).type(EXPECTED_MIME_TYPE).header("X-Requested-By", "ambari")
//                    .put(ClientResponse.class, "{\"Clusters\":{\"desired_config\":[{\"type\":\"flume-conf\",\"tag\":\"version"+System.currentTimeMillis()+"\",\"properties\":{\"content\": \""+content+"\"},\"service_config_version_note\":\"New config version\"}]}}");
//            System.out.println("{\"Clusters\":{\"desired_config\":[{\"type\":\"flume-conf\",\"tag\":\"version"+System.currentTimeMillis()+"\",\"properties\":{\"content\": \""+content+"\"},\"service_config_version_note\":\"New config version\"}]}}");
//            System.out.println(response.getStatus());
//            if (response != null && response.getStatus() == 200) {
//            	String responseString = response.getEntity(String.class);
//                
//            } else {
//                logger.info("response.status:"+response.getStatus());
//                System.out.println(response.getStatus());
//            }
//        } catch (Exception e) {
//			logger.error("修改flume配置时参数异常,服务器状态:"+response.getStatus(), e);
//		}finally {
//        	closeClient();
//        	closeResponse();
//        }
//        logger.info("ExecuteHandler.updateConfig end");
//    }
	
	/**
	 * 
	 * @param content
	 * @return
	 */
	public void updateConfig(String content) throws IOException{
		clusterName = new DataServiceMetrics().getClusterName();
		String hostname = SystemConfig.FLUME_HOSTNAME;
		String username = SystemConfig.USERNAME;
		String password = SystemConfig.PASSWORD;
		String AMBARI_URL =SystemConfig.AMBARI_URL;
		String AMBARI_USERNAME =SystemConfig.AMBARI_USERNAME;
		String AMBARI_PASSWORD =SystemConfig.AMBARI_PASSWORD;
		Connection conn = new Connection(hostname);
		conn.connect();
		boolean isAuthenticated = conn.authenticateWithPassword(username, password);

		if (isAuthenticated == false)
			throw new IOException("Authentication failed.");
		Session sess = conn.openSession();
		
		//String command="curl -u hdp:hdp@2017 http://192.168.11.176:8080/api/v1/clusters";
		String updateCommand=" curl -u "+AMBARI_USERNAME+":"+AMBARI_PASSWORD+" -H \"X-Requested-By: ambari\" -X PUT -d '{\"Clusters\":{\"desired_config\":[{\"type\":\"flume-conf\",\"tag\":\"version"+System.currentTimeMillis()+"\",\"properties\":{\"content\": \""+content+"\"},\"service_config_version_note\":\"New config version\"}]}}' "+AMBARI_URL+"/api/v1/clusters/"+clusterName;
		System.out.println(updateCommand);
		sess.execCommand(updateCommand);

		System.out.println("Here is some information about the remote host:");
		InputStream stdout = new StreamGobbler(sess.getStdout());

		BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

		while (true)
		{
			String line = br.readLine();
			if (line == null)
				break;
			System.out.println(line);
		}

		/* Show exit status, if available (otherwise "null") */

		System.out.println("ExitCode: " + sess.getExitStatus());

		/* Close this session */

		sess.close();

		/* Close the connection */

		conn.close();
    }
	
	private  String updateConfig(){
		logger.info("ExecuteHandler.updateConfig start ");
		clusterName = new DataServiceMetrics().getClusterName();
		String properties1 = null;
        try {
        	initVerify(SystemConfig.AMBARI_URL+"/api/v1/clusters/"+clusterName+"?fields=Clusters/desired_configs");
        	webResource = client.resource(SystemConfig.AMBARI_URL+"/api/v1/clusters/"+clusterName+"?fields=Clusters/desired_configs");
        	response = webResource.accept(EXPECTED_MIME_TYPE).get(ClientResponse.class);
            String tag =null;
            if(response.getStatus() == 200) {
            	
            	String responseAsString = response.getEntity(String.class);
            	JSONObject jsonObject = JSONObject.parseObject(responseAsString);
                JSONObject clusters = jsonObject.getJSONObject("Clusters");
                JSONObject desired_configs = clusters.getJSONObject("desired_configs");
                JSONObject properties = desired_configs.getJSONObject("flume-conf");
                tag = properties.getString("tag");
                System.out.println(tag);
                
            }else {
            	System.out.println(response.getStatus());
            	logger.info("ExecuteHandler.updateConfig back null");
            }
            
           //第二次请求Ambari获取配置属性
            webResource = client.resource(SystemConfig.AMBARI_URL+"/api/v1/clusters/"+clusterName+"/configurations?type="+"flume-conf"+"&tag="+tag);
        	response = webResource.accept(EXPECTED_MIME_TYPE).get(ClientResponse.class);
            
            if(tag !=null &&response.getStatus() == 200){
            	String responseAsString2= response.getEntity(String.class);
            	JSONObject jsonObject1 = JSONObject.parseObject(responseAsString2);
                JSONArray items = jsonObject1.getJSONArray("items");
                Iterator<Object> iterator = items.iterator();
                while (iterator.hasNext()) {
                    JSONObject next = (JSONObject) iterator.next();
                    properties1 = next.getString("properties");
                }
            }
        } catch (Exception e) {
			logger.error("获取集群集群时参数异常,服务器状态:"+response.getStatus(), e);
		}finally {
        	closeClient();
        	closeResponse();
        }
        logger.info("ExecuteHandler.updateConfig end");
        return properties1;
	}
	
	
	private  synchronized void closeClient() {
		if (client != null) {
			client.destroy();
		}
		client = null;
	}
	
	private  synchronized void closeResponse() {
		if (response != null) {
			response.close();
		}
		response = null;
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String con="flume-conf";
		new ExecuteHandler().getConfig(con);
	}

}

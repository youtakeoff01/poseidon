package com.hand.bdss.dsmp.component.ambari;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.metrics.DataServiceMetrics;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * 
 * @author tancheng
 *
 */
public class HostHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(HostHandler.class);

	private static final String EXPECTED_MIME_TYPE = "text/plain";//"application/json";
	
	private  Client client = null;
    private  ClientResponse response = null;
    private  WebResource webResource = null;
    private String clusterName = null;
    
    public  void initVerify(String url){
		logger.info("HostHandler.initVerify start ");
		client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(SystemConfig.AMBARI_USERNAME, SystemConfig.AMBARI_PASSWORD));
        System.out.println(url);
         webResource = client.resource(url);
		logger.info("HostHandler.initVerify end");
	}
    
	private  void getHosts(){
		logger.info("HostHandler.getHosts start ");
		clusterName = new DataServiceMetrics().getClusterName();
        try {
        	initVerify(SystemConfig.AMBARI_URL+"/api/v1/clusters/"+clusterName+"/hosts/"+SystemConfig.AMBARI_HOST);

        	response = webResource.accept(EXPECTED_MIME_TYPE).get(ClientResponse.class);
            
            if(response.getStatus() == 200) {
            	
            	String s = response.getEntity(String.class);
            	System.out.println(response.getStatus()+""+s);
            }else {
            	System.out.println(response.getStatus());
            	logger.info("HostHandler.getPolicy back null");
            }
        } catch (Exception e) {
			logger.error("获取集群集群时参数异常,服务器状态:"+response.getStatus(), e);
		}finally {
        	closeClient();
        	closeResponse();
        }
        logger.info("HostHandler.getHosts end");
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
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		new HostHandler().getHosts();
	}

}

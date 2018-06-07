package com.hand.bdss.dsmp.component.autoconfig;

import javax.ws.rs.HttpMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.metrics.DataServiceMetrics;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import net.sf.json.JSONObject;


/**
 * Created by cong.xiang on 2017/6/7.
 */
public class UpdateConfig {
    private static final Logger logger = LoggerFactory.getLogger(UpdateConfig.class);
    private Client client = null;
    private ClientResponse clientResponse = null;
    String BASE_URL = SystemConfig.AMBARI_URL;

    public void updaeConfig(String type) throws Exception {
//        1. Find the latest version of the config type that you need to update.
    	String clusterName = new DataServiceMetrics().getClusterName();
        client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(SystemConfig.AMBARI_USERNAME,SystemConfig.AMBARI_PASSWORD));
        WebResource webResource = client.resource(BASE_URL);
        WebResource resource = webResource.path("api/v1/clusters/"+clusterName).queryParam("fields", "Clusters/desired_configs");
        System.out.println(resource.toString());
        clientResponse = resource.accept("text/plain").type("text/plain")
                .method(HttpMethod.GET, ClientResponse.class);
        String responseAsString = null;
        if (clientResponse != null && clientResponse.getStatus() == 200) {
            responseAsString = clientResponse.getEntity(String.class);
        } else {
            logger.info("response status:" + clientResponse.getStatus());
        }
        JSONObject jsonObject = JSONObject.fromObject(responseAsString);
        JSONObject clusters = jsonObject.getJSONObject("Clusters");
        JSONObject desired_configs = clusters.getJSONObject("desired_configs");
        JSONObject properties = desired_configs.getJSONObject("hdfs-site");
        String tag = properties.get("tag") + "";
//        2. Read the config type with correct tag
        WebResource resource2 = webResource.path("api/v1/clusters/"+clusterName+"/configurations").queryParam("type", type).queryParam("tag", tag);
        clientResponse = resource2.accept("text/plain").type("text/plain")
                .method(HttpMethod.GET, ClientResponse.class);
        System.out.println(clientResponse.getStatus());

        String responseAsString2 = null;
        if (clientResponse != null && clientResponse.getStatus() == 200) {
            responseAsString2 = clientResponse.getEntity(String.class);
        } else {
            logger.info("response status:" + clientResponse.getStatus());
        }
        System.out.println(responseAsString2);


//        3b. Save a new version of the  configand apply it using one call

        WebResource resource3 = webResource.path("api/v1/clusters/"+clusterName);

        org.json.JSONObject obj = new org.json.JSONObject();
        org.json.JSONObject obj1 = new org.json.JSONObject();
        org.json.JSONObject obj2 = new org.json.JSONObject();
        org.json.JSONObject obj3 = new org.json.JSONObject();
        obj.put("Clusters", obj1);
        obj1.put("desired_config", new Object[]{obj2});

        obj2.put("tag", "version14924104111111");
        obj2.put("type", "hdfs-site");
        obj2.put("service_config_version_note","New config version");
        obj2.put("properties", obj3);

        obj3.put("dfs.block.access.token.enable", "true");
        obj3.put("dfs.blockreport.initialDelay", "120");
//   String str  = "'["+obj.toString()+"]'";
        //调用toString()方法可直接将其内容打印出来
//        System.out.println(str);
        clientResponse = resource3.accept("text/plain").type("text/plain").header("X-Requested-By","ambari").put(ClientResponse.class,obj.toString());
        System.out.println(clientResponse.getStatus());
        String responseAsString3 = null;
        if (clientResponse != null && clientResponse.getStatus() == 200) {
            responseAsString = clientResponse.getEntity(String.class);
        } else {
            logger.info("response status:" + clientResponse.getStatus());
        }
        System.out.println(responseAsString3);

//        clientResponse = webResource.header("content-text", " application/json").put(ClientResponse.class, obj);//参数列表里加入obj对象
//        4. Restart all components or services to have the config change take effect
//        E.g. Stop and Start a service

    }

    public static void main(String[] args) throws Exception {
        UpdateConfig updateConfig = new UpdateConfig();
        String type = "hdfs-site";
        updateConfig.updaeConfig(type);
    }
}

package com.hand.bdss.dsmp.component.autoconfig;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.metrics.DataServiceMetrics;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import java.util.Iterator;


/**
 * Created by cong.xiang on 2017/6/6.
 */
public class GetConfig {
    //http://hadoop001.edcs.org:8080/api/v1/clusters/hdp_dev/configurations?type=core-site&tag=version1
    private static final Logger logger = LoggerFactory.getLogger(GetConfig.class);
    private Client client = null;
    private ClientResponse clientResponse = null;
    private String clusterName = null;
    String BASE_URL = SystemConfig.AMBARI_URL;

    public String getConfig(String type) {
    	clusterName = new DataServiceMetrics().getClusterName();
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
        JSONObject jsonObject = JSONObject.parseObject(responseAsString);
        JSONObject clusters = jsonObject.getJSONObject("Clusters");
        JSONObject desired_configs = clusters.getJSONObject("desired_configs");
        JSONObject properties = desired_configs.getJSONObject("hdfs-site");
        String tag = properties.getString("tag");
        System.out.println(tag);

//        其中一种实现方法
//        MultivaluedMap params = new MultivaluedMapImpl();
//        List list = new ArrayList<String>();
//        list.add("core-site");
//        List list2 = new ArrayList<String>();
//        list2.add("version1");
//        params.put("type", list);
//        params.put("tag", list2);

//        WebResource resource = webResource.path("api/v1/clusters/hdp_dev/configurations").queryParams(params);
//        另外一种实现
        WebResource resource2 = webResource.path("api/v1/clusters/"+clusterName+"/configurations").queryParam("type", type).queryParam("tag", tag);

        System.out.println(resource2.toString());

        clientResponse = resource2.accept("text/plain").type("text/plain")
                .method(HttpMethod.GET, ClientResponse.class);
        System.out.println(clientResponse.getStatus());

        String responseAsString2 = null;
        if (clientResponse != null && clientResponse.getStatus() == 200) {
            responseAsString2 = clientResponse.getEntity(String.class);
        } else {
            logger.info("response status:" + clientResponse.getStatus());
        }
        JSONObject jsonObject1 = JSONObject.parseObject(responseAsString2);
        JSONArray items = jsonObject1.getJSONArray("items");
        Iterator<Object> iterator = items.iterator();
        String properties1 = null;
        while (iterator.hasNext()) {
            JSONObject next = (JSONObject) iterator.next();
            properties1 = next.getString("properties");
        }
        return properties1;
    }


    public static void main(String[] args) throws Exception {
        GetConfig gf = new GetConfig();
//        gf.getConfig("core-site","version1");
        String pro = gf.getConfig("hdfs-site");
        System.out.println(pro);
//        gf.getConfig("hbase-site","version1");
//        gf.getConfig("hive-site","version1");
//        gf.getConfig("mapred-site", "version1");
    }
}

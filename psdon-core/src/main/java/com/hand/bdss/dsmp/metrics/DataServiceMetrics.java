package com.hand.bdss.dsmp.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.HttpMethod;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.model.HostInfo;
import com.hand.bdss.dsmp.model.ServiceStatus;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * Created by cong.xiang on 2017/6/12.
 */
public class DataServiceMetrics {

    private static final Logger logger = LoggerFactory.getLogger(DataServiceMetrics.class);
    private static Client client = null;
    private ClientResponse clientResponse = null;
    private static String AMBARI_URL = SystemConfig.AMBARI_URL;
    private static String AMBARI_USERNAME = SystemConfig.AMBARI_USERNAME;
    private static String AMBARI_PASSWORD = SystemConfig.AMBARI_PASSWORD;
    private static WebResource webResource = null;
    private static String clusterName = null;
    static {
        client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(AMBARI_USERNAME, AMBARI_PASSWORD));
        webResource = client.resource(AMBARI_URL);
        clusterName = new DataServiceMetrics().getClusterName();
    }

    /**
     * 获取数据使用率
     *
     * @return
     */
    public float getDataUseRate() {
        String type = "metrics/dfs/datanode/DfsUsed,metrics/dfs/datanode/Capacity";
        WebResource resource = webResource.path("api/v1/clusters/"+clusterName+"/services/HDFS/components/DATANODE").queryParam("fields", type).queryParam("_", "1430849798630");
        clientResponse = resource.accept("text/plain").type("text/plain")
                .method(HttpMethod.GET, ClientResponse.class);
        String responseAsString = null;
        if (clientResponse != null && clientResponse.getStatus() == 200) {
            responseAsString = clientResponse.getEntity(String.class);
        } else {
            logger.info("response status:" + clientResponse.getStatus());
        }
        JSONObject jsonObject = JSONObject.parseObject(responseAsString);
        JSONObject metrics = jsonObject.getJSONObject("metrics");
        JSONObject dfs = metrics.getJSONObject("dfs");
        JSONObject datanode = dfs.getJSONObject("datanode");
        float capacity = datanode.getFloat("Capacity");
        float dfsUsed = datanode.getFloat("DfsUsed");
        float rate = dfsUsed / capacity;
        return rate;
    }

    /**
     * 获取服务状态
     *
     * @param serviceName
     * @return
     */

    public String getServiceStatus(String serviceName) {
        String type = "/api/v1/clusters/"+clusterName+"/services/" + serviceName;
        WebResource resource = webResource.path(type);
        clientResponse = resource.accept("text/plain").type("text/plain")
                .method(HttpMethod.GET, ClientResponse.class);
        String responseAsString = null;
        if (clientResponse != null && clientResponse.getStatus() == 200) {
            responseAsString = clientResponse.getEntity(String.class);
        } else {
            logger.info("response status:" + clientResponse.getStatus());
        }
        JSONObject serviceInfo = JSONObject.parseObject(responseAsString);
        JSONObject jsonObject = serviceInfo.getJSONObject("ServiceInfo");
        String state = jsonObject.getString("state");
        if (StringUtils.isNoneBlank("state") && state.equals("STARTED"))
            return "green";
        if (StringUtils.isNotBlank("state") && state.equals("INSTALLED"))
            return "red";
        return null;
    }
    
    public String getClusterName(){
    	String path = "api/v1/clusters";
    	WebResource resource = webResource.path(path);
        clientResponse = resource.accept("text/plain").type("text/plain")
                .method(HttpMethod.GET, ClientResponse.class);
        String responseAsString = null;
        if (clientResponse != null && clientResponse.getStatus() == 200) {
            responseAsString = clientResponse.getEntity(String.class);
        } else {
            logger.info("response status:" + clientResponse.getStatus());
        }
        JSONObject jsonObject = JSONObject.parseObject(responseAsString);
        JSONArray items = jsonObject.getJSONArray("items");
        Iterator<Object> iterator = items.iterator();
        String cluster_name = null;
        while (iterator.hasNext()) {
            JSONObject next = (JSONObject) iterator.next();
            String name = next.getString("Clusters");
            JSONObject alertHistory = JSONObject.parseObject(name);
            cluster_name = alertHistory.getString("cluster_name");
        } 
    	return cluster_name;
    }
    
    public int getDiverseInfo(String type) {
        WebResource resource = webResource.path("api/v1/clusters/"+clusterName+"/alert_history").queryParam("AlertHistory/state", type);
        System.out.println(resource.toString());
        clientResponse = resource.accept("text/plain").type("text/plain")
                .method(HttpMethod.GET, ClientResponse.class);
        String responseAsString = null;
        if (clientResponse != null && clientResponse.getStatus() == 200) {
            responseAsString = clientResponse.getEntity(String.class);
        } else {
            logger.info("response status:" + clientResponse.getStatus());
        }
        System.out.println(clientResponse.getStatus());
        JSONObject jsonObject = JSONObject.parseObject(responseAsString);
        JSONArray items = jsonObject.getJSONArray("items");
        Iterator<Object> iterator = items.iterator();
        int sum = 0;
        while (iterator.hasNext()) {
            JSONObject next = (JSONObject) iterator.next();
//            JSONObject alertHistory = next.getJSONObject("AlertHistory");
//            String state = alertHistory.getString("state");
//            System.out.println(state);
            sum++;
        }
        return sum;
    }

    public List<HostInfo> getHostList() {
        WebResource resource = webResource.path("api/v1/clusters/"+clusterName+"/hosts");
        clientResponse = resource.accept("text/plain").type("text/plain")
                .method(HttpMethod.GET, ClientResponse.class);
        String responseAsString = null;
        if (clientResponse != null && clientResponse.getStatus() == 200) {
            responseAsString = clientResponse.getEntity(String.class);
        } else {
            logger.info("response status:" + clientResponse.getStatus());
        }
        JSONObject objects = JSONObject.parseObject(responseAsString);
        JSONArray items = objects.getJSONArray("items");
        Iterator<Object> iterator = items.iterator();
        List<HostInfo> hostsList = new ArrayList<>();
        DataServiceMetrics dataServiceMetrics = new DataServiceMetrics();
        while (iterator.hasNext()) {
            HostInfo hostInfo = new HostInfo();
            JSONObject next = (JSONObject) iterator.next();
            JSONObject hosts = next.getJSONObject("Hosts");
            String host_name = hosts.getString("host_name");
            List<String> service = dataServiceMetrics.getServiceByHostName(host_name);
            hostInfo.setHost_name(host_name);
            hostInfo.setComponent_name(service);
            hostsList.add(hostInfo);
        }
        return hostsList;
    }

    public List<String> getServiceByHostName(String hostName) {
        WebResource resource = webResource.path("api/v1/clusters/"+clusterName+"/hosts/" + hostName);
        clientResponse = resource.accept("text/plain").type("text/plain")
                .method(HttpMethod.GET, ClientResponse.class);
        String responseAsString = null;
        if (clientResponse != null && clientResponse.getStatus() == 200) {
            responseAsString = clientResponse.getEntity(String.class);
        } else {
            logger.info("response status:" + clientResponse.getStatus());
        }
        JSONObject jsonObject = JSONObject.parseObject(responseAsString);
        JSONArray host_components = jsonObject.getJSONArray("host_components");
        List<String> component = new ArrayList<>();
        Iterator<Object> iterator = host_components.iterator();
        while (iterator.hasNext()) {
            JSONObject next = (JSONObject) iterator.next();
            JSONObject hostRoles = next.getJSONObject("HostRoles");
            String component_name = hostRoles.getString("component_name");
            component.add(component_name);
        }
        return component;
    }
    
    /**
     *获取所有机器上所有组件运行状态
     * @param serviceName
     * @return
     * @throws Exception 
     */
    public List<ServiceStatus> getHostsComponentState(String serviceName) throws Exception {
    	List<ServiceStatus> comStateList = new ArrayList<ServiceStatus>(); 
    	List<Map<String,List<String>>> componentsList = new ArrayList<Map<String,List<String>>>();
    	try{
    		List<HostInfo> hostList = this.getHostList();      								//获取所有机器列表
    		if(hostList != null && hostList.size()>0){
        		Iterator<HostInfo> hostInfos = hostList.iterator();
        		while(hostInfos.hasNext()){
        			HostInfo hostInfo = hostInfos.next();
        			String hostName = hostInfo.getHost_name();
        			
        			List<String> comList = this.getServiceByHostName(hostName);				//获取所有的组件列表
        			Map<String, List<String>> map = new HashMap<String,List<String>>();
        			List<String> hList = new ArrayList<String>();
        			for(int i = 0;i < comList.size();i++){
        				hList.add(comList.get(i));
        			}
        			map.put(hostName,hList);
        			componentsList.add(map);
        		}
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    		throw new Exception("获取组件列表失败!");
    	}
    	//将组件状态封装成对象进行存储
    	for(int i = 0; i < componentsList.size(); i++){
    		Iterator<Entry<String,List<String>>> iterator = (Iterator<Entry<String, List<String>>>)componentsList.get(i).entrySet().iterator();
    		while(iterator.hasNext()){
    			Entry<String,List<String>> entry = iterator.next();
    			String hostName = (String) entry.getKey();
	    		List<String> comlist = (List<String>) entry.getValue();
	    		for(int j = 0; j<comlist.size();j++){
					ServiceStatus serviceStatus = new ServiceStatus();
					String comName = comlist.get(j);
					String type = isComponentStarted(comName, hostName, serviceName);
					if(type !=null && !type.equalsIgnoreCase("2")){				
						serviceStatus.setComName(comName);
						serviceStatus.setHostName(hostName);
						serviceStatus.setServiceName(serviceName);
						serviceStatus.setState(type);
				    	comStateList.add(serviceStatus);
					}
				}
    		}
    	}
		return comStateList;
	}
     
    //验证在本机上服务中的组件是否启动
    public String isComponentStarted(String comName,String hostName,String serviceName){
    	String type ="2";
		String url = "api/v1/clusters/"+clusterName+"/hosts/"+hostName+"/host_components/"+comName;
		
		WebResource resource = webResource.path(url);
        clientResponse = resource.accept("text/plain").type("text/plain")
                .method(HttpMethod.GET, ClientResponse.class);
        String responseAsString = null;
        if (clientResponse != null && clientResponse.getStatus() == 200) {
            responseAsString = clientResponse.getEntity(String.class);
        } else {
            logger.info("response status:" + clientResponse.getStatus());
        }
        
        if(StringUtils.isNotEmpty(responseAsString)){
        	JSONObject jsonObject = JSONObject.parseObject(responseAsString);
            JSONObject hostRoles = jsonObject.getJSONObject("HostRoles");
            String state = hostRoles.getString("state");
            
            JSONArray host_components = jsonObject.getJSONArray("component");
            Iterator<Object> iterator = host_components.iterator();
            while (iterator.hasNext()) {
                JSONObject next = (JSONObject) iterator.next();
                JSONObject serviceComponent = next.getJSONObject("ServiceComponentInfo");
                String service_name = serviceComponent.getString("service_name");
                if(StringUtils.isNotEmpty(service_name) && service_name.equals(serviceName)){
                	if(StringUtils.isNoneBlank(state) && state.equals("STARTED"))
                		type = "1";			//正常
                	if(StringUtils.isNoneBlank(state) && state.equals("INSTALLED"))
                		type = "0";			//异常
                }else{
                	type ="2";				//不存在
                }
            }
        }
        return type;
    }
    

	public static void main(String[] args) {
//        dataServiceMetrics.getDataUseRate();
//       String hdfs = dataServiceMetrics.getServiceStatus("HDFS");
//        System.out.println(hdfs);
//		List<HostInfo> hostList = dataServiceMetrics.getHostList();
//        for (HostInfo host : hostList) {
//            System.out.println(host.getHost_name()+host.getComponent_name());
//        }
//          List<String> serviceName = dataServiceMetrics.getServiceByHostName("hadoop006.edcs.org");
//        List<HostInfo> serviceName = dataServiceMetrics.getHostList();
//        Iterator<HostInfo> iterator = serviceName.iterator();
//        while (iterator.hasNext()) {
//            HostInfo next = iterator.next();
//            System.out.println(next.getHost_name() + ":" + next.getComponent_name());
//        }
    }
}

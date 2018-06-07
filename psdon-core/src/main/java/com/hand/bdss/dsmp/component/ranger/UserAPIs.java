package com.hand.bdss.dsmp.component.ranger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.hadoop.hdfs.web.JsonUtil;
import org.apache.ranger.plugin.util.RangerRESTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.model.RangerUser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * 
 * @author tc
 *
 */
public class UserAPIs {

	private static final Logger logger = LoggerFactory.getLogger(UserAPIs.class);
	private static final String EXPECTED_MIME_TYPE = "application/json";
	
	private Client client = null;
    private ClientResponse response = null;
    private WebResource webResource = null;
//    private static final String DN = "http://hadoop003.edcs.org:6080";
    private static final String DN = SystemConfig.RANGER_DOMAINNAME;//与RANGER_DOMAINNAME IP，端口相同 ，抽取替换
    private static final String REQ_URL_USERS="/service/xusers/secure/users";
    private static final String REQ_URL_USERS2="/service/xusers/users"; //加上前缀
    private static final String REQ_URL_USERS3="/service/xusers/users/userName";///users/userName/{userName}  /secure/users/delete
    private static final String DELETE_RANGER_USER = "/service/xusers/secure/users/delete?forceDelete=true";
    
    public WebResource initVerify(String url){
    	logger.info("UserAPIs.initVerify start ");
    	if(null == client){
        	final ClientConfig config = new DefaultClientConfig();
            config.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        	client = Client.create(config);
        }
        client.addFilter(new HTTPBasicAuthFilter(SystemConfig.RANGER_USERNAME,SystemConfig.RANGER_PASSWORD));
        logger.info("UserAPIs.initVerify end");
        return client.resource(url);
  	}
    
    /**
	 * 
	 * @param RangerUser
	 * @return
	 */
	public  RangerUser createUser(RangerUser user) throws IOException{
		logger.info("UserREST.createUser start ");
		RangerUser returnUser = null;
        try {
        	webResource = initVerify(DN+REQ_URL_USERS);
        	String str = JSON.toJSONString(user,SerializerFeature.DisableCircularReferenceDetect);
            response = webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
            		.post(ClientResponse.class,str);
            if (response != null && response.getStatus() == 200) {
            	returnUser = response.getEntity(RangerUser.class);
            	
            } else {
                logger.info("response status:"+response.getStatus());
            }
        } catch (Exception e) {
			logger.error("创建ranger用户时参数异常,服务器状态:"+response.getStatus(), e);
		}finally {
        	closeClient();
        	closeResponse();
        }
        logger.info("UserREST.createUser end");
        return returnUser;
    }
	
	
	/**
     * 
     * @param RangerUser
     * @return
     */
	public RangerUser getUser(RangerUser user) throws IOException{
		logger.info("UserAPIs.getUser start ");
		RangerUser returnRangerServiceDef = null;
        try {
        	
        	if(user.getId() != null) {
        		initVerify(DN+REQ_URL_USERS+"/"+user.getId());
        	}else {
        		initVerify(DN+REQ_URL_USERS2+"/userName/"+user.getName());
        	}
        	//initVerify(DN+REQ_URL_USERS+"/"+user.getId());
            response = webResource.accept(EXPECTED_MIME_TYPE).get(ClientResponse.class);
            
            logger.info(" getUser "+response.getStatus());
            if(response.getStatus() == 200) {
            	returnRangerServiceDef = response.getEntity(RangerUser.class);
            }else {
            	logger.info("UserAPIs.getUser back null");
            }
        } catch (Exception e) {
			logger.error("获取ranger用户时参数异常,服务器状态:"+response.getStatus(), e);
		}finally {
        	closeClient();
        	closeResponse();
        }
        logger.info("UserAPIs.getUser end");
        return returnRangerServiceDef;
    }
	
	/**
	 * 
	 * @param 
	 * @return 
	 */
	public int deleteUser(RangerUser user) throws IOException{
		logger.info("UserAPIs.deleteUser start ");

        try {
        	if(user.getId() != null){
        		webResource = initVerify(DN+REQ_URL_USERS2+"/"+user.getId()+"?forceDelete=true");
        	}else {
        		webResource = initVerify(DN+REQ_URL_USERS3+"/"+user.getName()); //根据名字删除只是逻辑上的删除，不删除记录，只改变状态
        	}
//        	else {
//        		initVerify(DN+REQ_URL_USERS3+"/delete"+"?forceDelete=true");
//        	}
            webResource.accept(RangerRESTUtils.REST_EXPECTED_MIME_TYPE).delete();
        } catch (Exception e) {
			logger.error("删除ranger用户时参数异常,服务器状态:"+response.getStatus(), e);
		}finally {
        	closeClient();
        	closeResponse();
        }
        logger.info("UserAPIs.deleteUser end");
       return 1;
    }
	
	/**
	 * 
	 * @param 根据用户
	 * @return 
	 */
	public boolean deleteUser(Map<String, Object> map) throws IOException{
		logger.info("UserAPIs.deleteUser start ");
        try {
        	webResource = initVerify(DN+DELETE_RANGER_USER);
            webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).delete(JsonUtil.toJsonString(map));
        } catch (Exception e) {
			logger.error("删除ranger用户时参数异常,服务器状态:"+response.getStatus(), e);
			return false;
		}finally {
        	closeClient();
        	closeResponse();
        }
        logger.info("UserAPIs.deleteUser end");
        return true;
    }
	
	/**
	 * 
	 * @param RangerUser
	 * @return
	 */
	public RangerUser updateUser(RangerUser user) throws IOException{
		logger.info("UserAPIs.updateUser start ");
		RangerUser returnUser = null;
        try {
        	logger.info(DN+REQ_URL_USERS+"/"+user.getId());
        	initVerify(DN+REQ_URL_USERS+"/"+user.getId());
            response = webResource.accept(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
                    .type(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
                    .put(ClientResponse.class, JSON.toJSON(user));
            logger.info("updateUser--response.getStatus:"+response.getStatus());
            if (response != null && response.getStatus() == 200) {
            	returnUser = response.getEntity(RangerUser.class);
                
            } else {
                logger.info("response.status:"+response.getStatus());
            }
        } catch (Exception e) {
			logger.error("修改ranger用户时参数异常,服务器状态:"+response.getStatus(), e);
		}finally {
        	closeClient();
        	closeResponse();
        }
        logger.info("UserAPIs.updateUser end");
        return returnUser;
    }
	
	@SuppressWarnings("unchecked")
	public String searchUsers() throws IOException{
    	logger.info("UserAPIs.searchUsers start ");
    	List<RangerUser> listVXUser = null;
    	String returnString = "";
        try {
        	initVerify(DN+REQ_URL_USERS2);
            response = webResource.accept(EXPECTED_MIME_TYPE).get(ClientResponse.class);
            if(response.getStatus() == 200) {
            	returnString = response.getEntity(String.class);
                System.out.println("UserAPIs.searchUsers-return string:"+returnString);
                listVXUser = (List<RangerUser>) JSON.parseObject(returnString, RangerUser.class);
            }else {
            	logger.info(DN+REQ_URL_USERS2+"/"+"/policy"+"select back null");
            }
        } catch (Exception e) {
			logger.error("查询ranger用户时参数异常,服务器状态:"+response.getStatus(), e);
		}finally {
        	closeClient();
        	closeResponse();
        }
        logger.info("UserAPIs.searchUsers end");
        return returnString;
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

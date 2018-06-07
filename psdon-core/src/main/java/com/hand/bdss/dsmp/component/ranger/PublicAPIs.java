package com.hand.bdss.dsmp.component.ranger;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.ranger.plugin.model.RangerPolicy;
import org.apache.ranger.plugin.model.RangerService;
import org.apache.ranger.plugin.model.RangerServiceDef;
import org.apache.ranger.plugin.util.RangerRESTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hand.bdss.dsmp.config.SystemConfig;
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
public class PublicAPIs {

	private static final Logger logger = LoggerFactory.getLogger(PublicAPIs.class);

	private static final String EXPECTED_MIME_TYPE = "application/json";
	
	private Client client = null;
    
    private static final String DN = SystemConfig.RANGER_DOMAINNAME;
    private static final String REQ_URL_SERVICE="/service/public/v2/api/service";
    private static final String REQ_URL_POLICY="/service/public/v2/api/policy";
    private static final String REQ_URL_SERVICEDEF="/service/public/v2/api/servicedef";
    

//    public void initVerify(String url){
//		logger.info("PublicAPIs.initVerify start ");
//		client = Client.create();
//        client.addFilter(new HTTPBasicAuthFilter(SystemConfig.RANGER_USERNAME,SystemConfig.RANGER_PASSWORD));
//        webResource = client.resource(url);
//		logger.info("PublicAPIs.initVerify end");
//	}
    
    private WebResource initVerify(String url){
        if(null == client){
        	final ClientConfig config = new DefaultClientConfig();
            config.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        	client = Client.create(config);
        }
        client.addFilter(new HTTPBasicAuthFilter("admin","admin"));
        return client.resource(url);
    }
    
    /**
     * 
     * @param rangerServiceDef
     * @return
     */
	public RangerServiceDef getServiceDef(RangerServiceDef rangerServiceDef) throws IOException{
		logger.info("PublicAPIs.getServiceDef start ");
		RangerServiceDef returnRangerServiceDef = null;
		ClientResponse response = null;
        try {
        	WebResource webResource = initVerify(DN+REQ_URL_SERVICEDEF+"/name/"+rangerServiceDef.getName());
        	response = webResource.accept(EXPECTED_MIME_TYPE).get(ClientResponse.class);
            if(response.getStatus() == 200) {
            	returnRangerServiceDef = response.getEntity(RangerServiceDef.class);
            }else {
            	logger.info("PublicAPIs.getServiceDef back null");
            }
        } catch (Exception e) {
			logger.error("", e);
		}finally {
			if(null != client){
				client.destroy();
			}
			if(null != response){
				response.close();
			}
			
        }
        logger.info("PublicAPIs.getServiceDef end");
        return returnRangerServiceDef;
    }
    
	/**
	 * 
	 * @param rangerServiceDef
	 * @return
	 */
	public  RangerServiceDef createServiceDef(RangerServiceDef rangerServiceDef) throws IOException{
		logger.info("PublicAPIs.createServiceDef start ");
		RangerServiceDef returnRangerServiceDef = null; 
		ClientResponse response = null;
        try {
        	WebResource webResource = initVerify(DN+REQ_URL_SERVICEDEF);
        	
        	response = webResource.accept(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
                    .type(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
                    .post(ClientResponse.class, JSON.toJSONString(rangerServiceDef,SerializerFeature.DisableCircularReferenceDetect));

            System.out.println("-----"+response.getStatus());
            if (response != null && response.getStatus() == 200) {
            	returnRangerServiceDef = response.getEntity(RangerServiceDef.class);
            } else {
                logger.info("response status:"+response.getStatus());
            }
        } catch (Exception e) {
			logger.error("", e);
		}finally {
			if(null != client){
				client.destroy();
			}
			if(null != response){
				response.close();
			}
        }
        logger.info("PublicAPIs.createServiceDef end");
        return returnRangerServiceDef;
    }
	
	/**
	 * 
	 * @param rangerServiceDef
	 * @return 
	 */
	public void deleteServiceDef(RangerServiceDef rangerServiceDef) throws IOException{
		logger.info("PublicAPIs.deleteServiceDef start ");

        try {
        	WebResource webResource = initVerify(DN+REQ_URL_SERVICEDEF+"/name/"+rangerServiceDef.getName());
            webResource.accept(RangerRESTUtils.REST_EXPECTED_MIME_TYPE).delete();
        } catch (Exception e) {
			logger.error("", e);
		}finally {
			if(null != client){
				client.destroy();
			}
        }
        logger.info("PublicAPIs.deleteServiceDef end");
       
    }
	
    /**
     * 
     * @param rangerService
     * @return
     */
	public RangerService getService(RangerService rangerService) throws IOException{
		logger.info("PublicAPIs.getService start ");
		RangerService returnRangerService = null;
		ClientResponse response = null;
        try {
        	WebResource webResource = initVerify(DN+REQ_URL_SERVICE+"/name/"+rangerService.getName());
        	response = webResource.accept(EXPECTED_MIME_TYPE).get(ClientResponse.class);
            if(response.getStatus() == 200) {
            	returnRangerService = response.getEntity(RangerService.class);
            }else {
            	logger.info("PublicAPIs.getService back null");
            }
        } catch (Exception e) {
			logger.error("获取服务时参数异常,服务器状态:", e);
		}finally {
			if(null != client){
				client.destroy();
			}
			if(null != response){
				response.close();
			}
        }
        logger.info("PublicAPIs.getService end");
        return returnRangerService;
    }
	
	/**
	 * 
	 * @param rangerService
	 * @return
	 */
	public  RangerService createService(RangerService rangerService) throws IOException{
		logger.info("PublicAPIs.createService start ");
		RangerService returnRangerService = null;
		ClientResponse response = null;
        try {
        	
        	WebResource webResource = initVerify(DN+REQ_URL_SERVICE);
        	
        	response = webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                    .post(ClientResponse.class, JSON.toJSONString(rangerService,SerializerFeature.DisableCircularReferenceDetect));
            System.out.println("-----"+response.getStatus());
            if (response != null && response.getStatus() == 200) {
            	returnRangerService = response.getEntity(RangerService.class);
            	
            } else {
                logger.info("response status:"+response.getStatus());
            }
        } catch (Exception e) {
			logger.error("新增服务时参数异常,服务器状态:", e);
		}finally {
			if(null != client){
				client.destroy();
			}
			if(null != response){
				response.close();
			}
        }
        logger.info("PublicAPIs.createService end");
        return returnRangerService;
    }
	
	/**
	 * 
	 * @param rangerService
	 * @return 
	 */
	public int deleteService(RangerService rangerService) throws IOException{
		logger.info("PublicAPIs.deleteService start ");

        try {
        	WebResource webResource = null;
        	if(rangerService.getId() != null) {
        		webResource = initVerify(DN+REQ_URL_SERVICE+"/"+rangerService.getId());
        	}else {
        		webResource = initVerify(DN+REQ_URL_SERVICE+"/name/"+rangerService.getName());
        	}
        	
            webResource.accept(RangerRESTUtils.REST_EXPECTED_MIME_TYPE).delete();
            return 200;
        } catch (Exception e) {
			logger.error("删除服务时参数异常,服务器状态:", e);
		} finally {
			if(null != client){
				client.destroy();
			}
        }
        logger.info("PublicAPIs.deleteService end");
        return 0;
    }
	
	/**
	 * 
	 * @param rangerService
	 * @return
	 */
	public RangerService updateService(RangerService rangerService) throws IOException{
		logger.info("PublicAPIs.updateService start ");
		RangerService returnRangerService = null; 
		ClientResponse response = null;
        try {
        	WebResource webResource = initVerify(DN+REQ_URL_POLICY+"/"+rangerService.getId());
        	response = webResource.accept(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
                    .type(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
                    .put(ClientResponse.class, JSON.toJSONString(rangerService,SerializerFeature.DisableCircularReferenceDetect));

            if (response != null && response.getStatus() == 200) {
            	returnRangerService = response.getEntity(RangerService.class);
                
            } else {
                logger.info("response.status:"+response.getStatus());
            }
        } catch (Exception e) {
			logger.error("修改服务时参数异常,服务器状态:", e);
		}finally {
			if(null != client){
				client.destroy();
			}
			if(null != response){
				response.close();
			}
        }
        logger.info("PublicAPIs.updateService end");
        return returnRangerService;
    }
	
	@SuppressWarnings("unchecked")
	public List<RangerService> searchServices(RangerService rangerService) throws IOException{
    	logger.info("PublicAPIs.searchServices start ");
    	List<RangerService> listRangerService = null;
    	ClientResponse response = null;
    	
        try {
        	WebResource webResource = initVerify(DN+REQ_URL_SERVICE);
        	response = webResource.accept(EXPECTED_MIME_TYPE).get(ClientResponse.class);
            if(response.getStatus() == 200) {
                String jsonString = response.getEntity(String.class);
                listRangerService =  (List<RangerService>) JSON.parseObject(jsonString,RangerService.class);
            }else {
            	logger.info(DN+REQ_URL_SERVICE+"/"+"select back null");
            }
        } catch (Exception e) {
			logger.error("查询服务时参数异常,服务器状态:", e);
		}finally {
			if(null != client){
				client.destroy();
			}
			if(null != response){
				response.close();
			}
        }
        logger.info("PublicAPIs.searchServices end");
        return listRangerService;
	}
	
	public  void download(RangerPolicy rangerPolicy) throws IOException{
		logger.info("PublicAPIs.download start ");
		ClientResponse response = null;
        try {
        	WebResource webResource = initVerify(DN+REQ_URL_POLICY);
            //WebResource webResource = client.resource(URL)
        	webResource = webResource.queryParam(RangerRESTUtils.REST_PARAM_LAST_KNOWN_POLICY_VERSION, Long.toString(68))
                    .queryParam(RangerRESTUtils.REST_PARAM_PLUGIN_ID, "test");
        	response = webResource.accept(RangerRESTUtils.REST_MIME_TYPE_JSON).get(ClientResponse.class);

            if (response != null && response.getStatus() == 200) {
            	//returnServicePolicies = response.getEntity(ServicePolicies.class);
            } else if (response != null && response.getStatus() == 304) {
                // no change
                logger.info("no change");
            } else {
               // RESTResponse resp = RESTResponse.fromClientResponse(response);
            }
        } catch (Exception e) {
			logger.error("", e);
		}finally {
			if(null != client){
				client.destroy();
			}
			if(null != response){
				response.close();
			}
        }
        logger.info("PublicAPIs.download end");
    }
	
	public List<RangerPolicy> searchPolicies(RangerPolicy rangerPolicy) throws IOException{
    	logger.info("PublicAPIs.searchPolicies start ");
    	List<RangerPolicy> listRangerPolicy = null;
    	ClientResponse response = null;
        try {
        	WebResource webResource = initVerify(DN+REQ_URL_SERVICE+"/"+rangerPolicy.getService()+"/policy");
        	System.out.println(DN+REQ_URL_SERVICE+"/"+rangerPolicy.getService()+"/policy");
        	response = webResource.accept(EXPECTED_MIME_TYPE).get(ClientResponse.class);
            if(response.getStatus() == 200) {
                String jsonString = response.getEntity(String.class);
                
//                System.out.println(jsonString);
                listRangerPolicy = (List<RangerPolicy>) JSON.parseArray(jsonString, RangerPolicy.class);
            }else {
            	logger.info(DN+REQ_URL_SERVICE+"/"+rangerPolicy.getService()+"/policy"+"select back null");
            }
        } catch (Exception e) {
			logger.error("查询策略时参数异常,服务器状态:", e);
		}finally {
			if(null != client){
				client.destroy();
			}
			if(null != response){
				response.close();
			}
        }
        logger.info("PublicAPIs.searchPolicies end");
        return listRangerPolicy;
	}
	 /**
     * 
     * @param rangerPolicy
     * @return
     */
	public RangerPolicy getPolicy(RangerPolicy rangerPolicy) throws IOException{
		logger.info("PublicAPIs.getPolicy start ");
		RangerPolicy returnRangerPolicy = null;
		ClientResponse response = null;
        try {
        	WebResource webResource = null;
        	if(rangerPolicy.getId() != null){
        		webResource = initVerify(DN+REQ_URL_POLICY+"/"+rangerPolicy.getId());
        	}else {
        		webResource = initVerify(DN+REQ_URL_SERVICE+"/"+rangerPolicy.getService()+"/policy/"+rangerPolicy.getName());
        		
        	}
        	response = webResource.accept(EXPECTED_MIME_TYPE).get(ClientResponse.class);
            
            if(response.getStatus() == 200) {
            	returnRangerPolicy = response.getEntity(RangerPolicy.class);
            	System.out.println(returnRangerPolicy);
            }else {
            	logger.info("PublicAPIs.getPolicy back null" + response.getStatus());
            }
        } catch (Exception e) {
			logger.error("获取策略时参数异常,服务器状态:", e);
		}finally {
			if(null != client){
				client.destroy();
			}
			if(null != response){
				response.close();
			}
        }
        logger.info("PublicAPIs.getPolicy end");
        return returnRangerPolicy;
    }
	
	/**
	 * 
	 * @param rangerPolicy
	 * @return
	 */
	public  RangerPolicy createPolicy(RangerPolicy rangerPolicy) throws IOException{
		logger.info("PublicAPIs.createPolicy start ");
		RangerPolicy returnRangerPolicy = null;
		ClientResponse response = null;
        try {
        	WebResource webResource = initVerify(DN+REQ_URL_POLICY);
        	String str =JSON.toJSONString(rangerPolicy,SerializerFeature.DisableCircularReferenceDetect);
        	response = webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                    .post(ClientResponse.class, str);
            
            System.out.println(DN+REQ_URL_POLICY);
            System.out.println(JSON.toJSONString(rangerPolicy,SerializerFeature.DisableCircularReferenceDetect));
            if (response != null && response.getStatus() == 200) {
                 returnRangerPolicy = response.getEntity(RangerPolicy.class);
            } else {
                logger.info("response status:"+response.getStatus());
            }
        } catch (Exception e) {
			logger.error("新增策略时参数异常,服务器状态:", e);
		}finally {
			if(null != client){
				client.destroy();
			}
			if(null != response){
				response.close();
			}
        }
        logger.info("PublicAPIs.createPolicy end");
        return returnRangerPolicy;
    }
	
	/**
	 * 
	 * @param rangerPolicy
	 * @return 
	 */
	public int deletePolicy(RangerPolicy rangerPolicy) throws IOException{
		logger.info("PublicAPIs.deletePolicy start ");
        try {
        	WebResource webResource = null;
        	if(rangerPolicy.getId() != null){
        		webResource = initVerify(DN+REQ_URL_POLICY+"/"+rangerPolicy.getId());
        	}else {
        		webResource = initVerify(DN+REQ_URL_POLICY+"?servicename="+rangerPolicy.getService()+"&policyname="+rangerPolicy.getName());
        		System.out.println(DN+REQ_URL_POLICY+"?servicename="+rangerPolicy.getService()+"&policyname="+rangerPolicy.getName());
        	}
        	
        	webResource.accept(RangerRESTUtils.REST_EXPECTED_MIME_TYPE).delete();
            return 200;
        } catch (Exception e) {
			logger.error("删除策略时参数异常", e);
		}finally {
			if(null != client){
				client.destroy();
			}
        }
        
        logger.info("PublicAPIs.deletePolicy end");
        
       return 0;
    }
	
	public RangerPolicy updatePolicy(RangerPolicy rangerPolicy) throws IOException{
		logger.info("PublicAPIs.updatePolicy start ");
		ClientResponse response = null;
		RangerPolicy returnRangerPolicy = null;
        try {
        	WebResource webResource = null;
        	if(rangerPolicy.getId() != null){
        		webResource = initVerify(DN+REQ_URL_POLICY+"/"+rangerPolicy.getId());
        	}else {
        		webResource = initVerify(DN+REQ_URL_SERVICE+"/"+rangerPolicy.getService()+"/policy/"+rangerPolicy.getName());
        	}
        	response = webResource.accept(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
                    .type(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
                    .put(ClientResponse.class, JSON.toJSONString(rangerPolicy,SerializerFeature.DisableCircularReferenceDetect));

            if (response != null && response.getStatus() == 200) {
                 returnRangerPolicy = response.getEntity(RangerPolicy.class);
                
            } else {
                logger.info("PublicAPIs.updatePolicy response.status:"+response.getStatus());
            }
        } catch (Exception e) {
			logger.error("修改策略时参数异常,服务器状态:", e);
		}finally {
			if(null != client){
				client.destroy();
			}
			if(null != response){
				response.close();
			}
        }
        logger.info("PublicAPIs.updatePolicy end");
        return returnRangerPolicy;
    }
	
}

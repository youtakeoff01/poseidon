package com.hand.bdss.dsmp.component.atlas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.HttpMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.model.HiveMetaDesc;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by hand on 2017/4/18.
 */
public class SearchMetaHelper {

    private static final Logger logger = LoggerFactory.getLogger(SearchMetaHelper.class);
    private Client client = null;
    private ClientResponse clientResponse = null;

    public List<HiveMetaDesc> searchByDSL(String metaQuery) {

        client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(SystemConfig.USER_NAME, SystemConfig.PASS_WORD));
        WebResource webResource = client.resource(SystemConfig.BASE_URL);
        WebResource resourceHive = webResource.path("api/atlas/discovery/search/dsl").queryParam("query", metaQuery);

        System.out.println(resourceHive.toString());
//     http://hadoop001.edcs.org:21000/api/atlas/discovery/search/dsl?query=hive_table

        clientResponse = resourceHive.accept(SystemConfig.JSON_MEDIA_TYPE).type(SystemConfig.JSON_MEDIA_TYPE)
                .method(HttpMethod.GET, ClientResponse.class);

        String responseAsString = null;
        if (clientResponse != null && clientResponse.getStatus() == 200) {
            responseAsString = clientResponse.getEntity(String.class);
        } else {
            logger.info("response status:" + clientResponse.getStatus());
        }
        System.out.println(responseAsString);
        JSONObject jsonObject = JSONObject.fromObject(responseAsString);
        JSONArray array = jsonObject.getJSONArray("results");
        Iterator<Object> it = array.iterator();
        List<HiveMetaDesc> list = new ArrayList<HiveMetaDesc>();
        while (it.hasNext()) {
        	HiveMetaDesc hmd = new HiveMetaDesc();
            JSONObject ob = (JSONObject) it.next();
            String owner = ob.getString("owner");
            String comment = null;
            if("hdfs_path".equals(metaQuery)){
            	comment = ob.getString("description");
            }else{
            	comment = ob.getString("comment");
            }
            String name = ob.getString("name");
            hmd.setComment(comment);
            hmd.setName(name);
            hmd.setOwner(owner);
            list.add(hmd);
        }
        if (client != null) {
            closeClient();
        }
        if (clientResponse != null) {
            closeResponse();
        }
        return list;
    }


    public void searchByFullText(String fullTextQuery) {
        client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(SystemConfig.USER_NAME, SystemConfig.PASS_WORD));
        WebResource webResource = client.resource(SystemConfig.BASE_URL);
        WebResource resource = webResource.path("api/atlas/discovery/search/fulltext").queryParam("query", fullTextQuery);

        System.out.println(resource.toString());
// http://hadoop001.edcs.org:21000/api/atlas/discovery/search/fulltext?query=kylin_wqz
        clientResponse = resource.accept(SystemConfig.JSON_MEDIA_TYPE).type(SystemConfig.JSON_MEDIA_TYPE)
                .method(HttpMethod.GET, ClientResponse.class);
        String responseAsString = null;
        System.out.println(clientResponse.getStatus());
        if (clientResponse != null && clientResponse.getStatus() == 200) {
            responseAsString = clientResponse.getEntity(String.class);
        } else {
            logger.info("response status:" + clientResponse.getStatus());
        }
        System.out.println(responseAsString);
    }

    private synchronized void closeClient() {
        if (client != null) {
            client.destroy();
        }
        client = null;
    }

    private synchronized void closeResponse() {
        if (clientResponse != null) {
            clientResponse.close();
        }
        clientResponse = null;
    }
}
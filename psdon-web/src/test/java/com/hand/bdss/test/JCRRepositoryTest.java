package com.hand.bdss.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.core.MediaType;

import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import junit.framework.TestCase;

public class JCRRepositoryTest extends TestCase {

    public void testJCRRepository() {

        String url = "http://hdfs03.edcs.org:6080/service/public/v2/api/policy";
        String str = "{\"allowExceptions\":[],\"dataMaskPolicyItems\":[],\"denyExceptions\":[],\"denyPolicyItems\":[],\"isAuditEnabled\":true,\"isEnabled\":true,\"name\":\"bce4d46c-3cea-4a0a-84c4-db498b0630c8admin\",\"policyItems\":[{\"accesses\":[{\"isAllowed\":true,\"type\":\"create\"},{\"isAllowed\":true,\"type\":\"all\"}],\"conditions\":[],\"delegateAdmin\":false,\"groups\":[],\"users\":[\"admin\"]}],\"resources\":{\"database\":{\"isExcludes\":false,\"isRecursive\":false,\"values\":[\"default\"]},\"column\":{\"isExcludes\":false,\"isRecursive\":false,\"values\":[\"*\"]},\"table\":{\"isExcludes\":false,\"isRecursive\":false,\"values\":[\"data_stage\",\"data_stage\"]}},\"rowFilterPolicyItems\":[],\"service\":\"hdp_hive\"}";

        final ClientConfig config = new DefaultClientConfig();
        config.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        Client client = Client.create(config);
        client.addFilter(new HTTPBasicAuthFilter("admin", "admin"));

        final WebResource webResource = client.resource(url);

        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, str);
        System.out.println(response);
    }


    public static void main(String[] args) throws Exception {
//    	Calendar endDate = Calendar.getInstance();
//		Calendar lastPullTime = Calendar.getInstance();
//		endDate = lastPullTime;
//		System.out.println(endDate.getTime());
//		System.out.println(lastPullTime.getTime());
//		
//		endDate.add(Calendar.MINUTE, 10);
//		
//		System.out.println(endDate.getTime());
//		System.out.println(lastPullTime.getTime());
    	
    	String url = "jdbc:oracle:thin:@10.7.1.61:1521";
    	String db = "libsys";
    	System.out.println(String.join(":", url,"orcl"));
    	System.out.println("jdbc:oracle:thin:@10.7.1.61:1521:orcl");
//    	SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
//    	dataSource.setSuppressClose(true);
//		dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
//		dataSource.setUrl("jdbc:oracle:thin:@10.7.1.61:1521:orcl");
//		dataSource.setUsername("dataex");
//		dataSource.setPassword("CampusAnalytics$55");
//		Connection con = null;
//		ResultSet rs = null;
//		con = dataSource.getConnection();
//		System.out.println(con);
		
		
    	
    	
//    	Calendar lastPullTime = Calendar.getInstance();
//    	Calendar date = Calendar.getInstance();
//    	date.set(lastPullTime.get(Calendar.YEAR), 1, lastPullTime.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
    	
//    	System.out.println(date.get(Calendar.MONTH));
    	
//    	SimpleDateFormat format = new SimpleDateFormat();
//    	System.out.println(format.format(lastPullTime.getTime()));
//    	
//    	System.out.println(lastPullTime.get(Calendar.YEAR));
//    	System.out.println(lastPullTime.get(Calendar.MONTH));
//    	
//    	System.out.println(lastPullTime.get(Calendar.DAY_OF_MONTH));
    	
//		lastPullTime.setTime(new Date());
//		Calendar endDate = lastPullTime;
//		endDate.add(Calendar.MINUTE, 10);//增加10分钟,每次拉取10分钟的数据，
//		Calendar date = Calendar.getInstance();
//		if(date.before(endDate)) {//如果增加10分钟之后的时间大于当前时间，则拉取截止时间改为当前时间
//			System.out.println("1111111111111111");
//		}
    	
    	
//    	title:for (int i = 0; i < 10; i++) {
//			int b = 0;
//			do {
//				if(b>2) {
//					break title;
//				}
//				b++;
//			}while(b<4);
//			System.out.println(b);
//		}
    	
    	
//        String str = "{\"requestId\":\"qtp1970881185-2050 - 1abe764b-05bf-4d9e-889e-7f102a27783e\",\"results\":{\"query\":\"hive_table where (__guid = \\\"d58042d3-5ec2-48f0-8f0f-9926581b84c7\\\") columns limit 10000 offset 0 \",\"dataType\":{\"superTypes\":[\"DataSet\"],\"hierarchicalMetaTypeName\":\"org.apache.atlas.typesystem.types.ClassType\",\"typeName\":\"hive_column\",\"typeDescription\":null,\"attributeDefinitions\":[{\"name\":\"type\",\"dataTypeName\":\"string\",\"multiplicity\":{\"lower\":1,\"upper\":1,\"isUnique\":false},\"isComposite\":false,\"isUnique\":false,\"isIndexable\":true,\"reverseAttributeName\":null},{\"name\":\"comment\",\"dataTypeName\":\"string\",\"multiplicity\":{\"lower\":0,\"upper\":1,\"isUnique\":false},\"isComposite\":false,\"isUnique\":false,\"isIndexable\":false,\"reverseAttributeName\":null},{\"name\":\"table\",\"dataTypeName\":\"hive_table\",\"multiplicity\":{\"lower\":0,\"upper\":1,\"isUnique\":false},\"isComposite\":false,\"isUnique\":false,\"isIndexable\":false,\"reverseAttributeName\":\"columns\"}]},\"rows\":[{\"$typeName$\":\"hive_column\",\"$id$\":{\"id\":\"ade9d5e6-719e-48ac-a659-41bdf1d8f81b\",\"$typeName$\":\"hive_column\",\"version\":0,\"state\":\"ACTIVE\"},\"comment\":null,\"qualifiedName\":\"test.demo.id@hdp_dev\",\"type\":\"int\",\"owner\":\"is\",\"description\":null,\"name\":\"id\",\"table\":{\"id\":\"d58042d3-5ec2-48f0-8f0f-9926581b84c7\",\"$typeName$\":\"hive_table\",\"version\":0,\"state\":\"ACTIVE\"}},{\"$typeName$\":\"hive_column\",\"$id$\":{\"id\":\"1edd43f3-025b-4f87-ac3c-e57b38f76e8d\",\"$typeName$\":\"hive_column\",\"version\":0,\"state\":\"ACTIVE\"},\"comment\":null,\"qualifiedName\":\"test.demo.class@hdp_dev\",\"type\":\"string\",\"owner\":\"is\",\"description\":null,\"name\":\"class\",\"table\":{\"id\":\"d58042d3-5ec2-48f0-8f0f-9926581b84c7\",\"$typeName$\":\"hive_table\",\"version\":0,\"state\":\"ACTIVE\"}},{\"$typeName$\":\"hive_column\",\"$id$\":{\"id\":\"bc45038d-a46e-473e-b626-8b27dcd56b24\",\"$typeName$\":\"hive_column\",\"version\":0,\"state\":\"ACTIVE\"},\"comment\":null,\"qualifiedName\":\"test.demo.age@hdp_dev\",\"type\":\"int\",\"owner\":\"is\",\"description\":null,\"name\":\"age\",\"table\":{\"id\":\"d58042d3-5ec2-48f0-8f0f-9926581b84c7\",\"$typeName$\":\"hive_table\",\"version\":0,\"state\":\"ACTIVE\"}},{\"$typeName$\":\"hive_column\",\"$id$\":{\"id\":\"060905b4-6c21-4a23-8fc7-487c6c0629f4\",\"$typeName$\":\"hive_column\",\"version\":0,\"state\":\"ACTIVE\"},\"comment\":null,\"qualifiedName\":\"test.demo.issingle@hdp_dev\",\"type\":\"int\",\"owner\":\"is\",\"description\":null,\"name\":\"issingle\",\"table\":{\"id\":\"d58042d3-5ec2-48f0-8f0f-9926581b84c7\",\"$typeName$\":\"hive_table\",\"version\":0,\"state\":\"ACTIVE\"}},{\"$typeName$\":\"hive_column\",\"$id$\":{\"id\":\"54973328-6f8d-451a-9bcc-4814f8814c70\",\"$typeName$\":\"hive_column\",\"version\":0,\"state\":\"ACTIVE\"},\"comment\":null,\"qualifiedName\":\"test.demo.name@hdp_dev\",\"type\":\"string\",\"owner\":\"is\",\"description\":null,\"name\":\"name\",\"table\":{\"id\":\"d58042d3-5ec2-48f0-8f0f-9926581b84c7\",\"$typeName$\":\"hive_table\",\"version\":0,\"state\":\"ACTIVE\"}},{\"$typeName$\":\"hive_column\",\"$id$\":{\"id\":\"1b45b3f3-6d1e-494e-9ea9-46941db6b6f7\",\"$typeName$\":\"hive_column\",\"version\":0,\"state\":\"ACTIVE\"},\"comment\":null,\"qualifiedName\":\"test.demo.phonenum@hdp_dev\",\"type\":\"int\",\"owner\":\"is\",\"description\":null,\"name\":\"phonenum\",\"table\":{\"id\":\"d58042d3-5ec2-48f0-8f0f-9926581b84c7\",\"$typeName$\":\"hive_table\",\"version\":0,\"state\":\"ACTIVE\"}}]}}";
//        String ss = JacksonUtil.getJsonNode(str).get("results").get("rows").toString();
//        List<JsonNode> list = JacksonUtil.jsonToCollections(ss, ArrayList.class, JsonNode.class);
//        for (JsonNode s : list) {
//            System.out.println(s.get("name"));
//        }
//        String results = "{\"requestId\":\"qtp246399377-14491 - 308c8044-c594-444b-ad08-aa229e45aeec\",\"results\":{\"jsonClass\":\"org.apache.atlas.typesystem.json.InstanceSerialization$_Struct\",\"typeName\":\"__tempQueryResultStruct113\",\"values\":{\"vertices\":{\"158bc829-b1de-4edc-af4d-3d877fc40668\":{\"jsonClass\":\"org.apache.atlas.typesystem.json.InstanceSerialization$_Struct\",\"typeName\":\"__tempQueryResultStruct112\",\"values\":{\"qualifiedName\":\"bigdata_dw.mysql_zk02_tmp@hdp\",\"vertexId\":{\"jsonClass\":\"org.apache.atlas.typesystem.json.InstanceSerialization$_Struct\",\"typeName\":\"__IdType\",\"values\":{\"guid\":\"158bc829-b1de-4edc-af4d-3d877fc40668\",\"state\":\"ACTIVE\",\"typeName\":\"hive_table\"}},\"name\":\"mysql_zk02_tmp\"}},\"4bf72959-11cb-44f5-8a57-ca6e35e5b00b\":{\"jsonClass\":\"org.apache.atlas.typesystem.json.InstanceSerialization$_Struct\",\"typeName\":\"__tempQueryResultStruct112\",\"values\":{\"qualifiedName\":\"hdfs:\\/\\/hdp\\/dsmp\\/database\\/dsmp\\/mysql_zk02_tmp\\/2017_11_10\",\"vertexId\":{\"jsonClass\":\"org.apache.atlas.typesystem.json.InstanceSerialization$_Struct\",\"typeName\":\"__IdType\",\"values\":{\"guid\":\"4bf72959-11cb-44f5-8a57-ca6e35e5b00b\",\"state\":\"ACTIVE\",\"typeName\":\"hdfs_path\"}},\"name\":\"2017_11_10\"}}},\"edges\":{\"443d9c2f-66b2-4764-b1ae-d46f367d5ecc\":[\"4bf72959-11cb-44f5-8a57-ca6e35e5b00b\"],\"158bc829-b1de-4edc-af4d-3d877fc40668\":[\"443d9c2f-66b2-4764-b1ae-d46f367d5ecc\"]}}}}";
//        results = JacksonUtil.getJsonNode(results).get("results").toString();
//        List<JsonNode> list = JacksonUtil.jsonToCollections(results, ArrayList.class, JsonNode.class);
//        for (JsonNode node : list) {
//            String qualifiedName = node.get("qualifiedName").toString();
//            node = node.get("$id$");
//            if (qualifiedName.contains("test.testdate") && "ACTIVE".equals(node.get("state"))) {
//                System.out.println(node.get("$id$").get("id").toString());
//                String columns = node.get("$id$").get("columns").toString();
//                List<JsonNode> listNode = JacksonUtil.jsonToCollections(columns, ArrayList.class, JsonNode.class);
//                for (JsonNode s : listNode) {
//                    System.out.println(s.get("name"));
//                }
//            }
//        }
//        JsonNode jsonNode = JacksonUtil.getJsonNode(results);
//        String edges = jsonNode.get("results").get("values").get("edges").toString();
//        Map<String, List<String>> edgesMap = JacksonUtil.convertToMapList(edges);
//
//        for (Map.Entry<String, List<String>> entry : edgesMap.entrySet()) {
//
//            String key = entry.getKey();
//            List<String> valueList = entry.getValue();
//
//            for (String str : valueList) {
//                System.out.println(str);
//            }
//        }
    }
}
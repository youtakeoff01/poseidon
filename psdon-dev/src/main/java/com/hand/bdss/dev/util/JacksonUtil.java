package com.hand.bdss.dev.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JacksonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JacksonUtil.class);
    private static ObjectMapper objectMapper = null;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setSerializationInclusion(Include.NON_NULL);

    }

    private JacksonUtil() {
    }

    /**
     * @param @param  obj
     * @param @return 参数说明
     * @return String    返回类型
     * @throws
     * @Title: beanToJson
     * @Description: TODO(Bean & List 转 JSON)
     */
    public static String beanToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("beanToJson error:", e);
            ;
        }
        ;
        return null;
    }

    /**
     * @param @param  str
     * @param @param  clazz
     * @param @return 参数说明
     * @return T    返回类型
     * @throws
     * @Title: jsonToBean
     * @Description: TODO(JSON 转 Bean)
     */
    public static <T> T jsonToBean(String str, Class<T> clazz) {
        try {
            return objectMapper.readValue(str, clazz);
        } catch (IOException e) {
            logger.error("jsonToBean error:", e);
        }
        return null;
    }

    public static <T> T jsonToBean(JsonNode jsonNode, Class<T> clazz) {
        try {
            if (jsonNode != null && clazz != null)
                return objectMapper.readValue(jsonNode.toString(), clazz);
        } catch (IOException e) {
            logger.error("jsonToBean error:", e);
        }
        ;
        return null;
    }

    public static <T> T jsonToCollections(String str, Class<?> collectionClass, Class<?>... elementClasses) {
        try {

            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
            return objectMapper.readValue(str, javaType);
        } catch (IOException e) {
            logger.error("jsonToBean error:", e);
        }
        return null;
    }

    public static JsonNode getJsonNode(String plain) {
        JsonNode root = null;
        try {
            root = objectMapper.readTree(plain);
        } catch (Exception e) {
            logger.error("getJsonNode error:", e);
        }
        return root;
    }

    public static ObjectNode createObjectNode() {
        return objectMapper.createObjectNode();
    }

    public static Long getLong(JsonNode jsonNode, String nodeName) {
        if (jsonNode.has(nodeName)) {
            jsonNode = jsonNode.get(nodeName);
            if (!jsonNode.isNull()) {
                return jsonNode.asLong();
            }
        }
        return null;
    }

    public static Integer getInt(JsonNode jsonNode, String nodeName) {
        if (jsonNode.has(nodeName)) {
            jsonNode = jsonNode.get(nodeName);
            if (!jsonNode.isNull()) {
                return jsonNode.asInt();
            }
        }
        return null;
    }


    public static String getString(JsonNode jsonNode, String nodeName) {
        if (jsonNode.has(nodeName)) {
            jsonNode = jsonNode.get(nodeName);
            if (!jsonNode.isNull()) {
                return jsonNode.asText();
            }
        }
        return null;
    }

    public static Boolean getBoolean(JsonNode jsonNode, String nodeName) {
        if (jsonNode.has(nodeName)) {
            jsonNode = jsonNode.get(nodeName);
            if (!jsonNode.isNull()) {
                return jsonNode.asBoolean();
            }
        }
        return null;
    }

    /**
     * 将Map<String , Object>数据转为 String
     *
     * @param map
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonGenerationException
     * @paramitems
     */
    public static String convertToJsonStrs(Map<String, String> map) throws Exception {
        if (null == map || map.isEmpty()) {
            return "";
        }
        return objectMapper.writeValueAsString(map);
    }

    /**
     * 将json数据 转为Map<String , Object>
     *
     * @param jsonItems
     * @return
     */
    public static Map<String, Object> convertToMaps(String jsonItems) throws Exception {
        if (jsonItems.equals("") || jsonItems == null) {
            return null;
        }
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
        };
        return objectMapper.readValue(jsonItems, typeReference);
    }

    /**
     * 将json数据 转为Map<String , Object>
     *
     * @param jsonItems
     * @return
     */

    public static <K, V> Map<K, V> jsonToMaps(String jsonItems, Class<? extends Map> mapClass,
                                              Class<?> kClass, Class<?> vClass) throws Exception {
        if (jsonItems.equals("") || jsonItems == null) {
            return null;
        }
        JavaType javaType = objectMapper.getTypeFactory().constructMapType(mapClass, kClass, vClass);
        return objectMapper.readValue(jsonItems, javaType);
    }

    /**
     * 将json数据 转为Map<String, List<String>>
     *
     * @param jsonItems
     * @return
     */
    public static Map<String, List<String>> convertToMapList(String jsonItems) throws Exception {
        if (jsonItems.equals("") || jsonItems == null) {
            return null;
        }
        TypeReference<Map<String, List<String>>> typeReference = new TypeReference<Map<String, List<String>>>() {
        };
        return objectMapper.readValue(jsonItems, typeReference);
    }

    /**
     * 将json数据 List<String>
     *
     * @param jsonItems
     * @return
     */
    public static List<String> convertToList(String jsonItems) throws Exception {
        if (jsonItems.equals("") || jsonItems == null) {
            return null;
        }
        TypeReference<List<String>> typeReference = new TypeReference<List<String>>() {
        };
        return objectMapper.readValue(jsonItems, typeReference);
    }

    /**
     * bean转map
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static Map<String, Object> beanToMaps(Object obj) throws Exception {
        if (obj.equals("") || obj == null) {
            return null;
        }
        String beanStr = objectMapper.writeValueAsString(obj);
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
        };
        Map<String, Object> result = objectMapper.readValue(beanStr, typeReference);
        return result;
    }

    public static void main(String[] args) throws Exception {
//        String str = "{\"d4e31f51-0304-487c-ab9e-bcc96dbd924d\":{\"jsonClass\":\"org.apache.atlas.typesystem.json.InstanceSerialization$_Struct\",\"typeName\":\"__tempQueryResultStruct848\",\"values\":{\"qualifiedName\":\"test.demo1@hdp_dev\",\"vertexId\":{\"jsonClass\":\"org.apache.atlas.typesystem.json.InstanceSerialization$_Struct\",\"typeName\":\"__IdType\",\"values\":{\"guid\":\"d4e31f51-0304-487c-ab9e-bcc96dbd924d\",\"state\":\"ACTIVE\",\"typeName\":\"hive_table\"}},\"name\":\"demo1\"}},\"ed333cb5-b627-4e04-9e2d-9716e0556252\":{\"jsonClass\":\"org.apache.atlas.typesystem.json.InstanceSerialization$_Struct\",\"typeName\":\"__tempQueryResultStruct848\",\"values\":{\"qualifiedName\":\"test.demo2@hdp_dev\",\"vertexId\":{\"jsonClass\":\"org.apache.atlas.typesystem.json.InstanceSerialization$_Struct\",\"typeName\":\"__IdType\",\"values\":{\"guid\":\"ed333cb5-b627-4e04-9e2d-9716e0556252\",\"state\":\"ACTIVE\",\"typeName\":\"hive_table\"}},\"name\":\"demo2\"}},\"d58042d3-5ec2-48f0-8f0f-9926581b84c7\":{\"jsonClass\":\"org.apache.atlas.typesystem.json.InstanceSerialization$_Struct\",\"typeName\":\"__tempQueryResultStruct848\",\"values\":{\"qualifiedName\":\"test.demo@hdp_dev\",\"vertexId\":{\"jsonClass\":\"org.apache.atlas.typesystem.json.InstanceSerialization$_Struct\",\"typeName\":\"__IdType\",\"values\":{\"guid\":\"d58042d3-5ec2-48f0-8f0f-9926581b84c7\",\"state\":\"ACTIVE\",\"typeName\":\"hive_table\"}},\"name\":\"demo\"}}}";
        String str = null;
        str = "{\"3f883065-307d-4283-a7e4-1a5fbab56552\":{\"typeName\":\"hive_table\",\"attributes\":{\"owner\":\"hive\",\"qualifiedName\":\"bigdata_dw.dardar@hdp_dev\",\"name\":\"dardar\",\"description\":null},\"guid\":\"3f883065-307d-4283-a7e4-1a5fbab56552\",\"status\":\"ACTIVE\",\"displayText\":\"dardar\",\"classificationNames\":[]},\"68646abd-5636-4fed-8e8e-f6fd6ea0bc08\":{\"typeName\":\"hive_table\",\"attributes\":{\"owner\":\"hive\",\"qualifiedName\":\"bigdata_dw.dardar2@hdp_dev\",\"name\":\"dardar2\",\"description\":null},\"guid\":\"68646abd-5636-4fed-8e8e-f6fd6ea0bc08\",\"status\":\"ACTIVE\",\"displayText\":\"dardar2\",\"classificationNames\":[]},\"76f09d66-a216-4c06-957d-0eee440869b0\":{\"typeName\":\"hive_process\",\"attributes\":{\"owner\":null,\"qualifiedName\":\"bigdata_dw.dardar2@hdp_dev:1514279001000\",\"name\":\"create table dardar2 as select id,hostname from  da_mysql limit 10\",\"description\":null},\"guid\":\"76f09d66-a216-4c06-957d-0eee440869b0\",\"status\":\"ACTIVE\",\"displayText\":\"create table dardar2 as select id,hostname from  da_mysql limit 10\",\"classificationNames\":[]},\"9a1806e1-7c62-4c7d-a136-47d18e909d7f\":{\"typeName\":\"sqoop_dbdatastore\",\"attributes\":{\"owner\":\"hive\",\"qualifiedName\":\"mysql --url jdbc:mysql://192.168.11.189:3306/s_test --query SELECT id,hostname,servicename,comname,state,updatetime FROM tb_service_status WHERE 1=1 AND $CONDITIONS\",\"name\":\"mysql --url jdbc:mysql://192.168.11.189:3306/s_test --query SELECT id,hostname,servicename,comname,state,updatetime FROM tb_service_status WHERE 1=1 AND $CONDITIONS\",\"description\":\"\"},\"guid\":\"9a1806e1-7c62-4c7d-a136-47d18e909d7f\",\"status\":\"ACTIVE\",\"displayText\":\"mysql --url jdbc:mysql://192.168.11.189:3306/s_test --query SELECT id,hostname,servicename,comname,state,updatetime FROM tb_service_status WHERE 1=1 AND $CONDITIONS\",\"classificationNames\":[]},\"4b75b73f-feae-4bc2-8725-6b1e55a157a1\":{\"typeName\":\"hive_process\",\"attributes\":{\"owner\":null,\"qualifiedName\":\"bigdata_dw.dardar@hdp_dev:1514278963000\",\"name\":\"create table dardar as select * from  da_mysql limit 10\",\"description\":null},\"guid\":\"4b75b73f-feae-4bc2-8725-6b1e55a157a1\",\"status\":\"ACTIVE\",\"displayText\":\"create table dardar as select * from  da_mysql limit 10\",\"classificationNames\":[]},\"c5a9dc03-b64e-452e-80d2-f749aa718a90\":{\"typeName\":\"sqoop_process\",\"attributes\":{\"owner\":null,\"qualifiedName\":\"sqoop import --connect jdbc:mysql://192.168.11.189:3306/s_test --query SELECT id,hostname,servicename,comname,state,updatetime FROM tb_service_status WHERE 1=1 AND $CONDITIONS --hive-import --hive-database bigdata_dw --hive-table da_mysql --hive-cluster hdp_dev\",\"name\":\"sqoop import --connect jdbc:mysql://192.168.11.189:3306/s_test --query SELECT id,hostname,servicename,comname,state,updatetime FROM tb_service_status WHERE 1=1 AND $CONDITIONS --hive-import --hive-database bigdata_dw --hive-table da_mysql --hive-cluster hdp_dev\",\"description\":null},\"guid\":\"c5a9dc03-b64e-452e-80d2-f749aa718a90\",\"status\":\"ACTIVE\",\"displayText\":\"sqoop import --connect jdbc:mysql://192.168.11.189:3306/s_test --query SELECT id,hostname,servicename,comname,state,updatetime FROM tb_service_status WHERE 1=1 AND $CONDITIONS --hive-import --hive-database bigdata_dw --hive-table da_mysql --hive-cluster hdp_dev\",\"classificationNames\":[]},\"cd7462f6-5a71-455f-9195-79c209197f99\":{\"typeName\":\"hive_table\",\"attributes\":{\"owner\":null,\"qualifiedName\":\"bigdata_dw.da_mysql@hdp_dev\",\"name\":\"da_mysql\",\"description\":null},\"guid\":\"cd7462f6-5a71-455f-9195-79c209197f99\",\"status\":\"ACTIVE\",\"displayText\":\"da_mysql\",\"classificationNames\":[]}}";
        System.out.println(str);
//        Map<String, JsonNode> verticesCollects = JacksonUtil.jsonToMaps(str,HashMap.class,String.class,JsonNode.class);
//        for (Map.Entry<String, JsonNode> entry : verticesCollects.entrySet()) {
//            System.out.println(entry.getValue().get("values").get("name").asText());
//        }
        Map<String, JsonNode> map = JacksonUtil.jsonToMaps(str,HashMap.class,String.class,JsonNode.class);
//        if (list.size() > 0) {
            System.out.println("888");
//        }
    }
}

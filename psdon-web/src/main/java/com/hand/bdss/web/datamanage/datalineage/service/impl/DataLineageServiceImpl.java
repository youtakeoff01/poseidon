package com.hand.bdss.web.datamanage.datalineage.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.hand.bdss.dev.util.JacksonUtil;
import com.hand.bdss.dsmp.service.atlas.AtlasManage;
import com.hand.bdss.web.common.util.StringUtils;
import com.hand.bdss.web.datamanage.datalineage.service.DataLineageService;
import com.hand.bdss.web.datamanage.datalineage.vo.Column;
import com.hand.bdss.web.datamanage.datalineage.vo.Entity;
import com.hand.bdss.web.datamanage.datalineage.vo.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class DataLineageServiceImpl implements DataLineageService {

    private static final Logger logger = LoggerFactory.getLogger(DataLineageServiceImpl.class);

    private AtlasManage atlasManage = new AtlasManage();

    @Override
    public Map<String, Object> getAtlasLineageIn(String guid) throws Exception {
        Map<String, Object> retMap = new HashMap<>();

        String retJson = atlasManage.getLineage(guid);
        JsonNode results = JacksonUtil.getJsonNode(retJson);

        String relations = results.get("relations").toString();
        List<Relation> relationList = this.processRelation(relations);

        String guidEntityMap = results.get("guidEntityMap").toString();
        Map<String, Entity> entityMap = this.processEntityMap(guidEntityMap);

        Entity inputEntity = this.inputEntity(guid, relationList, entityMap);

        retMap.put("inNodes", inputEntity);
        retMap.put("points", relationList);
        return retMap;
    }

    @Override
    public Map<String, Object> getAtlasLineageOut(String guid) throws Exception {
        Map<String, Object> retMap = new HashMap<>();

        String retJson = atlasManage.getLineage(guid);
        JsonNode results = JacksonUtil.getJsonNode(retJson);

        String relations = results.get("relations").toString();
        List<Relation> relationList = this.processRelation(relations);

        String guidEntityMap = results.get("guidEntityMap").toString();
        Map<String, Entity> entityMap = this.processEntityMap(guidEntityMap);

        Entity outputEntity = this.outputEntity(guid, relationList, entityMap);

        retMap.put("outNodes", outputEntity);
        retMap.put("points", relationList);
        return retMap;
    }

    @Override
    public Map<String, Object> getAtlasLineage(String guid) throws Exception {

        Map<String, Object> retMap = new HashMap<>();

        String retJson = atlasManage.getLineage(guid);
        JsonNode results = JacksonUtil.getJsonNode(retJson);

        String relations = results.get("relations").toString();
        List<Relation> relationList = this.processRelation(relations);

        String guidEntityMap = results.get("guidEntityMap").toString();
        Map<String, Entity> entityMap = this.processEntityMap(guidEntityMap);

        Entity inputEntity = this.inputEntity(guid, relationList, entityMap);
        Entity outputEntity = this.outputEntity(guid, relationList, entityMap);

        retMap.put("inNodes", inputEntity);
        retMap.put("outNodes", outputEntity);
        retMap.put("points", relationList);
        return retMap;
    }

    /**
     * 解析relations
     *
     * @param relations
     * @return
     */
    private List<Relation> processRelation(String relations) {
        if (StringUtils.isEmpty(relations)) {
            return null;
        }
        relations = relations.replaceAll("fromEntityId", "source");
        relations = relations.replaceAll("toEntityId", "target");
        return JacksonUtil.jsonToCollections(relations, List.class, Relation.class);


    }

    /**
     * 获取inputs lineage
     *
     * @param guid
     * @param relations
     * @return
     */
    private Entity inputEntity(String guid, List<Relation> relations, Map<String, Entity> guidEntityMap)
            throws Exception {
        Entity entity = getEntity(guid, guidEntityMap);
        List<Entity> chridEntites = qryInputs(guid, relations, guidEntityMap);
        entity.setChildren(chridEntites);
        return entity;
    }

    /**
     * 获取inputs lineage
     *
     * @param guid
     * @param relations
     * @return
     */
    private Entity outputEntity(String guid, List<Relation> relations, Map<String, Entity> guidEntityMap)
            throws Exception {
        Entity entity = getEntity(guid, guidEntityMap);
        List<Entity> chridEntites = qryOutputs(guid, relations, guidEntityMap);
        entity.setChildren(chridEntites);
        return entity;
    }

    /**
     * 迭代查询（input）结构树
     *
     * @param guid
     * @param relations
     * @param guidEntityMap
     * @return
     * @throws Exception
     */
    private List<Entity> qryInputs(String guid, List<Relation> relations, Map<String, Entity> guidEntityMap)
            throws Exception {
        List<Entity> chridEntites = new ArrayList<>();
        for (Relation relation : relations) {
            Entity chridEntity;
            if (guid.equals(relation.getTarget())) {
                chridEntity = getEntity(relation.getSource(), guidEntityMap);
                chridEntity.setChildren(qryInputs(chridEntity.getGuid(), relations, guidEntityMap));
                chridEntites.add(chridEntity);
            }
        }
        return chridEntites;
    }

    /**
     * 迭代查询（output）结构树
     *
     * @param guid
     * @param relations
     * @param guidEntityMap
     * @return
     * @throws Exception
     */
    private List<Entity> qryOutputs(String guid, List<Relation> relations, Map<String, Entity> guidEntityMap)
            throws Exception {
        List<Entity> chridEntites = new ArrayList<>();
        for (Relation relation : relations) {
            Entity chridEntity;
            if (guid.equals(relation.getSource())) {
                chridEntity = getEntity(relation.getTarget(), guidEntityMap);
                chridEntity.setChildren(qryOutputs(chridEntity.getGuid(), relations, guidEntityMap));
                chridEntites.add(chridEntity);
            }
        }
        return chridEntites;
    }

    /**
     * 解析entityMap
     *
     * @param guidEntityMap
     * @return
     * @throws Exception
     */
    private Map<String, Entity> processEntityMap(String guidEntityMap) throws Exception {

        return JacksonUtil.jsonToMaps(guidEntityMap, HashMap.class, String.class, Entity.class);
    }

    /**
     * 根据guid 获取Entity
     *
     * @param guid
     * @param map
     * @return
     * @throws Exception
     */
    private Entity getEntity(String guid, Map<String, Entity> map) throws Exception {

        if (map == null || map.size() == 0) {
            return null;
        }

        Entity enty = new Entity();
        for (Map.Entry<String, Entity> entity : map.entrySet()) {
            if (guid.equals(entity.getKey())) {
                Entity en = entity.getValue();
                enty.setAttributes(en.getAttributes());
                enty.setGuid(en.getGuid());
                enty.setDisplayText(en.getDisplayText());
                enty.setTypeName(en.getTypeName());
                enty.setStatus(en.getStatus());
            }
        }
        return enty;
    }


    @Override
    public String getAtlasGuid(String dbName, String name) throws Exception {
        logger.info("impl/getAtlasGuid dbname={}, name={}", dbName, name);
        String retJson = atlasManage.getAtlasGuid(name);
        String results = JacksonUtil.getJsonNode(retJson).get("entities").toString();
        List<JsonNode> list = JacksonUtil.jsonToCollections(results, List.class, JsonNode.class);
        if (list == null || list.size() == 0) {
            return null;
        }
        for (JsonNode node : list) {
            String qualifiedName = node.get("attributes").get("qualifiedName").asText();
            String state = node.get("status").asText();
            String fullName = dbName + "." + name;
            if (qualifiedName.contains(fullName) && "ACTIVE".equals(state)) {
                System.out.println(node.get("guid").toString());
                return node.get("guid").asText();
            }
        }
        return null;
    }

    @Override
    public List<Column> getEntitySchema(String guid) throws Exception {

        String retJson = atlasManage.getAtlasEntity(guid);
        String entities = JacksonUtil.getJsonNode(retJson).get("referredEntities").toString();
        Map<String, JsonNode> entitiesMap = JacksonUtil.jsonToMaps(entities, HashMap.class, String.class, JsonNode.class);
        if (entitiesMap == null) {
            return null;
        }
        List<Column> columns = new ArrayList<>();
        //解析字段信息
        this.processColum(columns,entitiesMap);

        return columns;
    }

    /**
     * 解析字段信息
     * @param columns
     * @param entitiesMap
     */
    private void processColum(List<Column> columns, Map<String, JsonNode> entitiesMap) {

        for (Map.Entry<String, JsonNode> entity : entitiesMap.entrySet()) {
            JsonNode jsonNode = entity.getValue();

            String typeName = jsonNode.get("typeName").asText();
            if ("hive_column".equals(typeName)) {
                JsonNode attributes = jsonNode.get("attributes");
                Column column = JacksonUtil.jsonToBean(attributes, Column.class);
                columns.add(column);
            }
        }
    }

    @Override
    public  Map<String, String> getTabDetails(String guid) throws Exception {

        Map<String, String> retMap = new HashMap<>();
        String retJson = atlasManage.getAtlasEntity(guid);
        String jsonEntity = JacksonUtil.getJsonNode(retJson).get("entity").toString();
        Map<String, JsonNode> entityMap = JacksonUtil.jsonToMaps(jsonEntity, HashMap.class, String.class, JsonNode.class);
        if (entityMap == null) {
            return null;
        }
//        TableEntity tbEntity = new TableEntity();
        for (Map.Entry<String, JsonNode> entity : entityMap.entrySet()) {
            if("attributes".equals(entity.getKey())){
                JsonNode jsonNode = entity.getValue();
                retMap.put("description",jsonNode.get("description").asText());
                retMap.put("name",jsonNode.get("name").asText());
                retMap.put("owner",jsonNode.get("owner").asText());
                retMap.put("temporary",jsonNode.get("temporary").asText());
                retMap.put("qualifiedName",jsonNode.get("qualifiedName").asText());
                retMap.put("dbName",getSubStr(jsonNode.get("qualifiedName").asText()));
            }
            retMap.put(entity.getKey(), entity.getValue().asText());
        }
        return retMap;
    }

    /**
     * 正则匹配，取出字符
     * @param soap
     * @return
     */
    public static String getSubStr(String soap){
        String rgex = "(.*?)[/.]";
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while(m.find()){
            return m.group(1);
        }
        return "";
    }

//    @Override
//    public Map<String, Object> getInputsLineage(String guid) throws Exception {
//
//        Map<String, Object> retMap = new HashMap<>();
//        String retJson = atlasManage.getLineageInputs(guid);
//        JsonNode jsonNode = JacksonUtil.getJsonNode(retJson);
//        jsonNode = jsonNode.get("results").get("values");
//        Map<String, List<String>> edgesCollects = this.getNodeCollects(jsonNode);
//        Map<String, JsonNode> verticesCollects = this.verticesCollects(jsonNode);
//
//        Node rootNode = getInputsLineageNodes(guid, edgesCollects, verticesCollects);
//
//        List<Point> pointList = new ArrayList<>();
//        List<Point> points = this.getInputNodePoint(pointList, edgesCollects);
//
//        retMap.put("inNodes", rootNode);
//        retMap.put("inPoints", points);
//
//        return retMap;
//    }
//
//    /**
//     * 获取inputs节点树
//     *
//     * @param rootId
//     * @param edgesCollects
//     * @param verticesCollects
//     * @return
//     * @throws Exception
//     */
//    private Node getInputsLineageNodes(String rootId, Map<String, List<String>> edgesCollects,
//                                       Map<String, JsonNode> verticesCollects) throws Exception {
//        /**获取所有表节点信息**/
//        List<Node> tables = getTableNode(verticesCollects);
//        List<Node> processNodes = getProcessNodes(edgesCollects, verticesCollects, tables, true);
//
//        Node rootNode = getHiveTableNode(rootId, tables);
//        this.processRootNode(rootNode, processNodes);
//        return rootNode;
//    }
//
//    /**
//     * 获取outputs节点树
//     *
//     * @param rootId
//     * @param edgesCollects
//     * @param verticesCollects
//     * @return
//     * @throws Exception
//     */
//    private Node getOutputsLineageNodes(String rootId, Map<String, List<String>> edgesCollects,
//                                        Map<String, JsonNode> verticesCollects) throws Exception {
//
//        /**获取所有表节点信息**/
//        List<Node> tables = getTableNode(verticesCollects);
//        List<Node> processNodes = getProcessNodes(edgesCollects, verticesCollects, tables, false);
//
//        Node rootNode = getHiveTableNode(rootId, tables);
//        this.processRootNode(rootNode, processNodes);
//        return rootNode;
//    }
//
//    @Override
//    public Map<String, Object> getOutputsLineage(String guid) throws Exception {
//
//        Map<String, Object> retMap = new HashMap<>();
//
//        String retJson = atlasManage.getLineageOutputs(guid);
//        JsonNode jsonNode = JacksonUtil.getJsonNode(retJson);
//        jsonNode = jsonNode.get("results").get("values");
//        Map<String, List<String>> edgesCollects = this.getNodeCollects(jsonNode);
//        Map<String, JsonNode> verticesCollects = this.verticesCollects(jsonNode);
//        //获取节点树
//        Node rootNode = getOutputsLineageNodes(guid, edgesCollects, verticesCollects);
//        //获取箭头方向
//        List<Point> pointList = new ArrayList<>();
//        List<Point> points = this.getOutputNodePoint(pointList, edgesCollects);
//
//        retMap.put("outNodes", rootNode);
//        retMap.put("outPoints", points);
//
//        return retMap;
//    }
//
//
//    /**
//     * 根据Id获取Node节点
//     *
//     * @param id
//     * @param nodes
//     * @return
//     */
//    private Node getNode(String id, List<Node> nodes) {
//        for (Node node : nodes) {
//            if (id.equals(node.getId())) {
//                return node;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 获取process node的数据
//     *
//     * @param edgesCollects
//     * @return
//     * @throws Exception
//     */
//    private List<Node> getProcessNodes(Map<String, List<String>> edgesCollects, Map<String, JsonNode> verticesCollects,
//                                       List<Node> tables, boolean isInput) throws Exception {
//
//        List<Node> processs = new ArrayList<>();
//        for (Map.Entry<String, List<String>> edgentity : edgesCollects.entrySet()) {
//            if (!isHiveTable(edgentity.getKey(), verticesCollects)) {
//
//                Node pnode = new Node();
//                this.processNode(pnode, edgentity.getKey(), verticesCollects, isInput);
//
//                List<String> chLists = edgentity.getValue();
//                Node chNode;
//                if (null != chLists && chLists.size() > 0) {
//                    List<Node> chirdNodes = new ArrayList<>();
//                    for (String nodeId : chLists) {
//                        chNode = getNode(nodeId, tables);
//                        chNode.setpId(edgentity.getKey());
//                        chirdNodes.add(chNode);
//                        pnode.setChildren(chirdNodes);
//                    }
//                }
//                processs.add(pnode);
//            }
//
//        }
//
//        return processs;
//    }
//
//    /**
//     * 获取所有表
//     * 节点信息
//     *
//     * @param verticesCollects
//     * @return
//     */
//    private List<Node> getTableNode(Map<String, JsonNode> verticesCollects) {
//        List<Node> tables = new ArrayList<>();
//        for (Map.Entry<String, JsonNode> ventity : verticesCollects.entrySet()) {
//            //获取table node的数据
//            Node node = new Node();
//            node.setId(ventity.getKey());
//            JsonNode tableode = ventity.getValue();
//            node.setName(tableode.get("values").get("name").asText());
//            node.setTable(true);
//            tables.add(node);
//        }
//        return tables;
//    }
//
//
//    /**
//     * 整合各个子节点
//     *
//     * @param rootNode
//     * @param processNodes
//     */
//    private Node processRootNode(Node rootNode, List<Node> processNodes) {
//        List<Node> rcNodes = new ArrayList<>();
//        for (Node node : processNodes) {
//            //确定主节点
//            if (rootNode.getId().equals(node.getpId())) {
//                rcNodes.add(node);
//                rootNode.setChildren(rcNodes);
//                // 遍历子节点
//                List<Node> chridNodes = node.getChildren();
//                if (null != chridNodes && chridNodes.size() > 0) {
//                    for (int i = 0; i < chridNodes.size(); i++) {
//                        Node ccnode = processRootNode(chridNodes.get(i), processNodes);
//                        chridNodes.set(i, ccnode);
//                    }
//                }
//            }
//        }
//
//        return rootNode;
//    }
//
//    /**
//     * 解析proces 操作节点的数据
//     *
//     * @param pnode
//     * @param nodeId
//     * @throws Exception
//     */
//    private void processNode(Node pnode, String nodeId, Map<String, JsonNode> verticesCollects, boolean isInput) throws Exception {
//        String retJson = atlasManage.getAtlasEntity(nodeId);
//        JsonNode jsonNode = JacksonUtil.getJsonNode(retJson);
//        jsonNode = jsonNode.get("definition").get("values");
//
//        pnode.setName(jsonNode.get("name").asText());
//        pnode.setQuery(jsonNode.get("queryText").asText());
//
//        String puts;
//        if (isInput) {
//            puts = jsonNode.get("outputs").toString();
//        } else {
//            puts = jsonNode.get("inputs").toString();
//        }
//
//        List<JsonNode> outputsNodes = JacksonUtil.jsonToCollections(puts, List.class, JsonNode.class);
//        for (JsonNode outNodes : outputsNodes) {
//            String id = outNodes.get("id").asText();
//            if (isHiveTable(id, verticesCollects)) {
//                pnode.setpId(outNodes.get("id").asText());
//            }
//        }
//        jsonNode.get("outputs");
//
//        pnode.setTable(false);
//        pnode.setId(nodeId);
//    }
//
////    @Override
////    public String getAtlasGuid(String dbName, String name) throws Exception {
////
////        String retJson = atlasManage.getAtlasGuid("hive_table", name);
////        String results = JacksonUtil.getJsonNode(retJson).get("results").toString();
////        List<JsonNode> list = JacksonUtil.jsonToCollections(results, List.class, JsonNode.class);
////        for (JsonNode node : list) {
////            String qualifiedName = node.get("qualifiedName").asText();
////            node = node.get("$id$");
////            String state = node.get("state").asText();
////            String fullName = dbName + "." + name;
////            if (qualifiedName.contains(fullName) && "ACTIVE".equals(state)) {
////                System.out.println(node.get("id").toString());
////                return node.get("id").asText();
////            }
////        }
////        return null;
////    }
//
//    /**
//     * 0.8版本获取guid
//     *
//     * @param dbName
//     * @param name
//     * @return
//     * @throws Exception
//     */
//    @Override
//    public String getAtlasGuid(String dbName, String name) throws Exception {
//
//        String retJson = atlasManage.getAtlasGuid("hive_table", name);
//
//
//        String results = JacksonUtil.getJsonNode(retJson).get("entities").toString();
//        List<JsonNode> list = JacksonUtil.jsonToCollections(results, List.class, JsonNode.class);
//        if (list == null || list.size() == 0) {
//            return null;
//        }
//        for (JsonNode node : list) {
//            String qualifiedName = node.get("attributes").get("qualifiedName").asText();
//            String state = node.get("status").asText();
//            String fullName = dbName + "." + name;
//            if (qualifiedName.contains(fullName) && "ACTIVE".equals(state)) {
//                System.out.println(node.get("guid").toString());
//                return node.get("guid").asText();
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public String getAtlasEntity(String guid) throws Exception {
//        return atlasManage.getAtlasEntity(guid);
//    }
//
//    @Override
//    public List<Column> getLineageSchema(String guid) throws Exception {
//
//        String retJson = atlasManage.getLineageSchema(guid);
//        String s = JacksonUtil.getJsonNode(retJson).get("results").get("rows").toString();
//
//        return JacksonUtil.jsonToCollections(s, ArrayList.class, Column.class);
//    }
//
//    @Override
//    public Map<String, Object> getLineageGraph(String guid) throws Exception {
//
//        Map<String, Object> retMap = new HashMap<>();
//
//        Map<String, Object> inputsMap = this.getInputsLineage(guid);
//        Map<String, Object> outputsMap = this.getOutputsLineage(guid);
//
//        List<Point> points = new ArrayList<>();
//
//        List<Point> inPoints = (List<Point>) inputsMap.get("inPoints");
//        List<Point> outPoints = (List<Point>) outputsMap.get("outPoints");
//
//        if (inPoints != null) {
//            points.addAll(inPoints);
//        }
//        if (outPoints != null) {
//            points.addAll(outPoints);
//        }
//
//        Node inNodes = (Node) inputsMap.get("inNodes");
//        Node outNodes = (Node) outputsMap.get("outNodes");
//
//        retMap.put("points", points);
//        retMap.put("inNodes", inNodes);
//        retMap.put("outNodes", outNodes);
//
//
//        return retMap;
//    }
//
//
//    /**
//     * 解析lineage json
//     * 获取所有表父子关系
//     *
//     * @param jsonNode
//     * @return
//     * @throws Exception
//     */
//    private Map<String, List<String>> getNodeCollects(JsonNode jsonNode) throws Exception {
//        String edges = jsonNode.get("edges").toString();
//
//        return JacksonUtil.jsonToMaps(edges, HashMap.class, String.class, List.class);
//    }
//
//    /**
//     * 解析lineage json
//     * 获取vertices（所有表信息）
//     *
//     * @param jsonNode
//     * @return
//     * @throws Exception
//     */
//    private Map<String, JsonNode> verticesCollects(JsonNode jsonNode) throws Exception {
//
//        String vertices = jsonNode.get("vertices").toString();
//
//        return JacksonUtil.jsonToMaps(vertices, HashMap.class, String.class, JsonNode.class);
//
//    }
//
//
//    /**
//     * 查看当前的id是否是table
//     * 并且返回tableName
//     *
//     * @param checkId
//     * @param verticesCollects
//     * @return
//     */
//    private boolean isHiveTable(String checkId, Map<String, JsonNode> verticesCollects) {
//        for (String key : verticesCollects.keySet()) {
//            if (checkId.equals(key)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 获取表
//     *
//     * @param id
//     * @param tables
//     * @return
//     */
//    private Node getHiveTableNode(String id, List<Node> tables) {
//        for (Node node : tables) {
//            if (id.equals(node.getId())) {
//                return node;
//            }
//        }
//        return null;
//    }
//
//
//    /**
//     * 获取图谱箭头指向
//     * 关系图
//     *
//     * @param inputsNodeCollects
//     * @return
//     */
//    private List<Point> getInputNodePoint(List<Point> pointList, Map<String, List<String>> inputsNodeCollects) {
//        Point point;
//        for (Map.Entry<String, List<String>> inEntry : inputsNodeCollects.entrySet()) {
//            String targetIn = inEntry.getKey();
//            List<String> inLists = inEntry.getValue();
//            for (String sourceIn : inLists) {
//                point = new Point();
//                point.setSource(sourceIn);
//                point.setTarget(targetIn);
//                pointList.add(point);
//            }
//        }
//        return pointList;
//    }
//
//    /**
//     * 获取图谱箭头指向
//     *
//     * @param outputsNodeCollects
//     * @return
//     */
//    private List<Point> getOutputNodePoint(List<Point> pointList, Map<String, List<String>> outputsNodeCollects) {
//
//        Point point;
//        for (Map.Entry<String, List<String>> outEntry : outputsNodeCollects.entrySet()) {
//            String sourceOut = outEntry.getKey();
//            List<String> outLists = outEntry.getValue();
//            for (String targetOut : outLists) {
//                point = new Point();
//                point.setSource(sourceOut);
//                point.setTarget(targetOut);
//                pointList.add(point);
//            }
//        }
//        return pointList;
//    }
}

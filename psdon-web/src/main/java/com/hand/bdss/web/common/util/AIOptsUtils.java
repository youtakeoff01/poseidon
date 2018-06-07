package com.hand.bdss.web.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.web.intelligence.component.vo.AIOptsVO;
import com.hand.bdss.web.intelligence.component.vo.OptsInfo;

/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习组件工具类
 */
public class AIOptsUtils {
	
	/**
	 * 获取距当前节点最近的数据源节点
	 * @param nodeId
	 * @param json
	 * @param optsEntity
	 */
	public static AIOptsVO getPreNodeOpts(String nodeId, String json, String optsEntity) throws Exception{
		if(nodeId == null || json == null || optsEntity == null){
			return null;
		}
		//获取属性数组
		JSONObject jsonObject = JSONObject.parseObject(json);
		//获取组件节点-边数组
		List<AIOptsVO> optsList = JSONObject.parseArray(optsEntity, AIOptsVO.class);
		JSONObject sourceObject = jsonObject.getJSONObject("source");
		JSONArray edges = sourceObject.getJSONArray("edges");
		//获取最近资源节点ID
		String targetNode = getMinNode(nodeId,edges,optsList);
		if(!StringUtils.isBlank(targetNode)){
			for(AIOptsVO optsVo : optsList){
				if(targetNode.equalsIgnoreCase(optsVo.getComNum())){
					return optsVo;
				}
			}
		}
		return null;
	}
	
	
	/**
	 * 属性JSON查询  <获取当前节点>
	 * @param list
	 * @return
	 */
	public static AIOptsVO getOpts(List<AIOptsVO> list){
		String jsonString = JSON.toJSONString(list.get(0));
		JSONObject jsonObject = JSONObject.parseObject(jsonString);
		AIOptsVO optsVO = jsonObject.toJavaObject(jsonObject, AIOptsVO.class);
		return optsVO;
	}
	
	/**
	 * 属性JSON查询  <根据当前结点的nodeId获取ComCode>
	 * @param nodeId
	 * @return
	 */
	public static AIOptsVO getOpts(String nodeId, String optsEntity){
		//获取组件所有属性数据
		List<AIOptsVO> optsList = JSONObject.parseArray(optsEntity, AIOptsVO.class);
		for (AIOptsVO optsVO : optsList) {
			if(nodeId != null && nodeId.equalsIgnoreCase(optsVO.getComNum())){
				return optsVO;
			}
		}
		return null;
	}
	
	
	/**
	 * 属性JSON查询    <获取sql>
	 * @param aiOptsVO
	 * @return
	 */
	public static String parseSQLOpts(AIOptsVO optsVO,String taskName) {
		String hiveTbName = null;
		String dbName = null;
		String tableName = null;
		Map<String,Object> map = null;		//默认值Map
		if(StringUtils.isBlank(taskName)){
			taskName = "unknown-task";
		}
		//获取查询库名和表名
		String comCode = optsVO.getComCode();
		if("hiveTable".equalsIgnoreCase(comCode)){
			Map hiveTableMap = optsVO.getDbDefault();
			if(hiveTableMap != null){
				dbName = (String) hiveTableMap.get("selectV");
				hiveTableMap = optsVO.getTbDefault();
				tableName = (String) hiveTableMap.get("selectV");
			}
		}
		if("SSQLCom".equalsIgnoreCase(comCode) || "predictionCom".equalsIgnoreCase(comCode) || "clusterEvaluatorCom".equalsIgnoreCase(comCode)){
			List<OptsInfo> opts = optsVO.getList();
			dbName = "intermedia";
			for(OptsInfo optsI : opts){
				if(optsI.getOpt() != null && "dfname".equalsIgnoreCase(optsI.getOpt())){
					map = optsI.getOptSelectDefault();
					if(map != null && !map.isEmpty()){
						tableName = taskName + "_" + String.valueOf(map.get("selectV"));
					}else{
						tableName = taskName + "_" + String.valueOf(optsI.getOptDefault());
					}
				}
			}
		}
		hiveTbName = dbName +"." +tableName;
		return ("".equals(dbName) || "".equals(tableName) || dbName == null || tableName == null) ? null : hiveTbName;
	}

	
	/**
	 * 组件JSON还原   <task任务>
	 * @param data
	 * @return 
	 */
	public static String toAIOpts(String data) {
		if(data == null || "".equals(data)){
			return null;
		}
		Map<String, Object> map = JSON.parseObject(data);
		if(map.containsKey("taskName")){
			map.remove("taskName");
		}
		if(map.containsKey("remarks")){
			map.remove("remarks");
		}
		return JSONObject.toJSONString(map);
	}
	
	
	/**
	 * 组件JSON拼接  <task任务>
	 * @param userName
	 * @param taskName
	 * @param json
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String parseTask(String taskTime,Task task,HttpServletRequest req) throws Exception{
		if(task.getTaskName() == null || "".equals(task.getTaskName())){
			return null;
		}
		String taskName = task.getTaskName();
		JSONObject parseJson = JSONObject.parseObject(task.getScript());
		
		String comJson = parseJson.getString("json");		   //组件json
		String optsJson = parseJson.getString("optsEntity");   //属性json
		String parseOpts = AIOptsUtils.parseOpts(taskTime,comJson,optsJson, req);
		Map map = JSON.parseObject(parseOpts);
		//添加任务名称
		map.put("taskName",taskName);
		map.put("userName",task.getCreateAccount());
		return JSONObject.toJSONString(map);
	}
	
	
	/**
	 * 组件JSON拼接  <属性json>
	 * @param optsJson 属性json字符串
	 * @param comJson  组件json字符串
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String parseOpts(String taskTime,String comJson,String optsJson,HttpServletRequest req) throws Exception{
		if(optsJson == null || comJson == null){
			return "";
		}
		//解析标签属性和输入输出流
		String optsParsejson = parseTab(optsJson);
		List optsList = JSON.parseArray(optsParsejson);
		String comParsejson = parseAnchor(comJson);
		Map<String,Object> comMap = JSON.parseObject(comParsejson);
		
		Map<String,Object> sourceMap = (Map<String, Object>) comMap.get("source");
		List<Map> nodesList = (List<Map>) sourceMap.get("nodes");
		if(nodesList != null && nodesList.size()>0){
			for(Map<String,Object> node : nodesList){
				String id = String.valueOf(node.get("id"));
				//将属性json拼接到组件json中
				for(int i = 0; i < optsList.size(); i++){
					Map map = (Map) optsList.get(i);
					String comID = String.valueOf(map.get("comID"));
					if(comID != null && id.equals(comID)){
						map.remove("comID");
						node.put("nodeOpts",map);
						break;
					}
				}
			}
			String userName = GetUserUtils.getUser(req).getUserName();
			//添加用户名和时间
			comMap.put("userName", userName);
			comMap.put("taskTime",taskTime);
		}else{
			return "";
		}
		String jsonString = JSON.toJSONString(comMap,SerializerFeature.DisableCircularReferenceDetect);
		return jsonString;
	}
	
	
	/**
	 * 获取所选节点到目标节点的距离
	 */
	private static int getNodeDistance(String comId, String targetId, JSONArray edges,int i) {
		Iterator edgeItr = edges.iterator();
		while(edgeItr.hasNext()){
			JSONObject edge = (JSONObject) edgeItr.next();
			//防止报NoSuchElementException错误:it.next()调用多次
			String source = edge.getString("source");
			String target = edge.getString("target");
			if(comId.equalsIgnoreCase(source)){
				if(targetId.equalsIgnoreCase(target)){
					return i;
				}
				return getNodeDistance(target,targetId,edges,i+1);
			}
		}
		return Integer.MAX_VALUE;
	}
	
	
	/**
	 * 获取最小距离的资源节点
	 */
	private static String getMinNode(String nodeId,JSONArray edges,List<AIOptsVO> optsList){
		//封装comID数组
		List<String> comIds = new ArrayList<String>();
		for (AIOptsVO optsVO : optsList) {
			String comCode = optsVO.getComCode();
			if("hiveTable".equalsIgnoreCase(comCode) || "SSQLCom".equalsIgnoreCase(comCode)){
				comIds.add(optsVO.getComNum());
			}
		}
		if(comIds == null || comIds.size() == 0){
			return null;
		}
		//获取最小节点距离
		List<Integer> list = new ArrayList<Integer>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String comID : comIds) {
			//筛选符合条件的AIOptsVO对象<去除‘select *非法查询’>
			boolean flag = true;
			AIOptsVO opts = getOpts(comID,JsonUtils.toJson(optsList));
			if("SSQLCom".equalsIgnoreCase(opts.getComCode())){
				List<OptsInfo> selectList = opts.getList();
				for (OptsInfo info : selectList) {
					if(null != info.getOptDefault() && info.getOptDefault().contains("select * from"))
						flag = false;
						break;
				}
			}
			if(flag){
				int distance = getNodeDistance(comID,nodeId,edges,1);
				map.put(comID,distance);
				list.add(distance);
			}
		}
		int minDistance = Collections.min(list);
		String targetNode = null;
		for(String key : map.keySet()){
			if(minDistance == map.get(key)){
				targetNode = key;
				break;
			}
		}
		return targetNode;
	}
	
	/**
	 * 组件JSON解析   <tab解析>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static String parseTab(String json) {
		List parseList = new ArrayList();
		List<Object> list = JSON.parseArray(json);
		for(int i = 0 ; i < list.size(); i++){
			Map map = new HashMap();
			Map parseMap = new HashMap();
			//解析单个属性
			Map comMap = (Map) list.get(i);
			String comID = (String)comMap.get("comNum");
			String comCode = (String) comMap.get("comCode");
			String comName = (String) comMap.get("comName");
			List comList = (List) comMap.get("list");
			String tab = (String) comMap.get("tab");
			//解析单个标签<hiveTable>
			if(comList != null && comList.size() > 0){
				for(int j = 0 ; j < comList.size(); j++){
					Map optMap =  (Map) comList.get(j);
					Map vMap = (Map) optMap.get("optSelectDefault");
					if(vMap != null && !vMap.isEmpty()){
						map.put(optMap.get("opt"), String.valueOf(vMap.get("selectV")));
					}else{
						map.put(optMap.get("opt"), optMap.get("optDefault"));
					}
				}
			}else{
				Map dbMap = (Map) comMap.get("dbDefault");
				Map tbMap = (Map) comMap.get("tbDefault");
				if(null != dbMap && null != tbMap){
					map.put("dbname", dbMap.get("selectV"));
					map.put("tablename", tbMap.get("selectV"));
				}
			}
			//封装tab1和tab2
			if(tab != null && "1".equals(tab)){
				parseMap.put("comID", comID);
				parseMap.put("comCode",comCode);
				parseMap.put("comName", comName);
				parseMap.put("optsEntity", map);
				parseList.add(parseMap);
			}
			if(tab != null && "2".equals(tab)){
				if(parseList != null && parseList.size() >0){
					Map exsitTabMap = (Map) parseList.get(parseList.size()-1);
					Map optMap = (Map) exsitTabMap.get("optsEntity");
					Set<Map.Entry<String,Integer>> set = map.entrySet();
				    for(Iterator<Map.Entry<String,Integer>> it = set.iterator(); it.hasNext();){
				        Map.Entry<String,Integer> entry = it.next();
				        optMap.put(entry.getKey(), entry.getValue());
				    }
					exsitTabMap.put("optsEntity", optMap);
					parseList.set(parseList.size()-1, exsitTabMap);
				}
			}
		}
		if(parseList != null && parseList.size()>0){
			return JSON.toJSONString(parseList);
		}
		return "";
	}

	/**
	 * 组件JSON解析   <Anchor解析>
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes"})
	private static String parseAnchor(String json) throws Exception{
		Map<String,Object> map = JSON.parseObject(json);
		Map<String,Object> sourceMap = (Map<String, Object>) map.get("source");
		List<Map> edgesList = (List<Map>) sourceMap.get("edges");
		List<Map> nodesList = (List<Map>) sourceMap.get("nodes");
		
		if(edgesList != null && edgesList.size() > 0){
			for(Map<String,Object> edgeMap : edgesList){
				List sourceList = null;
				List targetList = null;
				String sourceAnchor = String.valueOf(edgeMap.get("sourceAnchor"));
				String targetAnchor = String.valueOf(edgeMap.get("targetAnchor"));
				String sourceNode = String.valueOf(edgeMap.get("source"));
				String targetNode = String.valueOf(edgeMap.get("target"));
				//查找匹配的node组件的节点
				if(nodesList != null && nodesList.size() >0){
					for(Map<String,Object> nodeMap : nodesList){
						String id = String.valueOf(nodeMap.get("id"));
						if(sourceNode.equals(id)){
							sourceList = (List) nodeMap.get("anchorPoints");
						}
						if(targetNode.equals(id)){
							targetList = (List) nodeMap.get("anchorPoints");
						}
						if(sourceList != null && targetList != null){
							break;
						}
					}
				}
				String sourceType = getNodeShape(sourceList).substring(0,1);
				String targetType = getNodeShape(targetList).substring(0,1);
				int sourceAnchorInt = Integer.parseInt(sourceAnchor)+1;
				int targetAnchorInt = Integer.parseInt(targetAnchor)+1;
				if(sourceAnchorInt <= Integer.parseInt(sourceType)){
					sourceAnchor = "in"+sourceAnchorInt;
				}else{
					sourceAnchor = "out"+(sourceAnchorInt-Integer.parseInt(sourceType));
				}
				if(targetAnchorInt <= Integer.parseInt(targetType)){
					targetAnchor = "in"+targetAnchorInt;
				}else{
					targetAnchor = "out"+(Integer.parseInt(targetAnchor)-Integer.parseInt(targetType));
				}
				edgeMap.put("sourceAnchor",sourceAnchor);
				edgeMap.put("targetAnchor",targetAnchor);
			}
		}else{
			return "";
		}
		String jsonString = JSON.toJSONString(map);
		return jsonString;
	}
	
	/**
	 * 获取node的shape类型
	 * @param json
	 * @param nodeID
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static String getNodeShape(List list){
		int inNum = 0;
		int outNum = 0;
		if(list != null && list.size() > 0){
			for(int i = 0 ;i < list.size(); i++){
				String x_y = String.valueOf(list.get(i));
				String y = x_y.substring(x_y.indexOf(",")+1,x_y.indexOf("]"));
				if("0".equals(y)){
					inNum ++;
				}
				if("1".equals(y)){
					outNum ++;
				}
			}
		}
		return inNum+"-"+outNum;
	}

	/*public static void main(String []args) throws Exception{
		String data = "{\"json\":{\"source\":{\"nodes\":[{\"shape\":\"customNode\",\"title\":\"随机森林\",\"anchorPoints\":[[0.5,0],[0.5,1]],\"x\":310,\"y\":40,\"id\":\"0915e8a7\"},{\"shape\":\"customNode\",\"title\":\"预测\",\"anchorPoints\":[[0.25,0],[0.5,0],[0.5,1]],\"x\":370,\"y\":130,\"id\":\"bec02e37\"}],\"edges\":[{\"shape\":\"polyLineFlow\",\"source\":\"0915e8a7\",\"target\":\"bec02e37\",\"id\":\"f0b3b0c4\",\"controlPoints\":[{\"x\":350,\"y\":80.5},{\"x\":410,\"y\":129.5}],\"sourceAnchor\":2,\"targetAnchor\":1}]},\"guides\":[]},\"optsEntity\":[{\"comCode\":\"randomForestCom\",\"comName\":\"随机森林\",\"list\":[{\"opt\": \"hh\",\"optDefault\":\"2\"},{\"opt\": \"gg\",\"optDefault\":\"2\"},{\"opt\": \"ff\",\"optDefault\":\"2\"},{\"opt\": \"ee\",\"optDefault\":\"2\"}]},{\"comCode\":\"predictionCom\",\"comName\":\"预测\",\"list\":[{\"opt\": \"dd\",\"optDefault\":\"2\"},{\"opt\": \"cc\",\"optDefault\":\"2\"},{\"opt\": \"bb\",\"optDefault\":\"2\"},{\"opt\": \"aa\",\"optDefault\":\"2\"}]}]}";
		JSONObject parseJson = JSON.parseObject(data);
		String comJson = parseJson.getString("json");		   //组件json
		String optsJson = parseJson.getString("optsEntity");   //属性json
		String parseOpts = AIOptsUtils.parseOpts(comJson,optsJson);
		System.out.println(parseOpts);
		System.out.println("-----转换前:"+optsJson);
		System.out.println("-----转换后:"+parseTab(optsJson));
	}*/

}

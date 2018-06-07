package com.hand.bdss.web.intelligence.component.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.web.common.em.TaskType;
import com.hand.bdss.web.common.util.AIOptsUtils;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.vo.AIHiveVO;
import com.hand.bdss.web.common.vo.AIPythonVO;
import com.hand.bdss.web.entity.AIEvaluation;
import com.hand.bdss.web.entity.AIFlowEntity;
import com.hand.bdss.web.entity.AIIntermedia;
import com.hand.bdss.web.entity.AIModelEntity;
import com.hand.bdss.web.entity.AIOptsEntity;
import com.hand.bdss.web.entity.AITreeEntity;
import com.hand.bdss.web.intelligence.component.dao.ComDao;
import com.hand.bdss.web.intelligence.component.service.ComService;
import com.hand.bdss.web.intelligence.component.service.EvaluationService;
import com.hand.bdss.web.intelligence.component.service.IntermediaService;
import com.hand.bdss.web.intelligence.component.vo.AIFlowVO;
import com.hand.bdss.web.intelligence.component.vo.AIOptsVO;
import com.hand.bdss.web.intelligence.component.vo.AITreeVO;
import com.hand.bdss.web.intelligence.component.vo.MenuTree;
import com.hand.bdss.web.intelligence.component.vo.OptsInfo;
import com.hand.bdss.web.intelligence.model.service.ModelService;
import com.hand.bdss.web.operationcenter.task.dao.TaskDao;

/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习组件service实现类
 */
@Service
public class ComServiceImpl implements ComService {
	
	@Resource
	private ComDao comDaoImpl;
	@Resource
	private TaskDao taskDaoImpl;
	@Resource
	private ModelService modelServiceImpl;
	@Resource
	private IntermediaService intermediaServiceImpl;
	@Resource
	private EvaluationService evaluationServiceImpl;
	
	/**
	 * 获取组件树列表<包含组件节点>
	 */
	@Override
	public List<MenuTree> listTree() throws Exception {
		AITreeEntity treeEntity = new AITreeEntity();
		treeEntity.setParentCode("");
		//根节点遍历
		List<MenuTree> list = this.getMenuTree(treeEntity);
		return list;
	}
	

	/**
	 * 获取组件属性列表(用户名+任务名+组件名)
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public List<AIOptsVO> listOpts(String data) throws Exception {
		List<AIOptsVO> list = null;
		JSONObject jsonObject = JSON.parseObject(data);
		String comName = jsonObject.getString("comName");
		String taskName = jsonObject.getString("taskName");
		String userName = jsonObject.getString("userName");
		try{
			AIModelEntity modelEntity = new AIModelEntity();
			modelEntity.setModelName(comName);
			modelEntity.setTaskName(taskName);
			modelEntity.setUserName(userName);
			List<AIModelEntity> modelList = modelServiceImpl.getAIModel(modelEntity);
			//封装AIOpts数据
			if(modelList != null && modelList.size()>0){
				list = modelServiceImpl.parseAIModel(modelEntity); //AI模型数据
			}else{
				Map<String,String> map = new HashMap<>();
				map.put("comName",comName);
				list = this.parseList(map);   					   //AI组件数据
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 保存AI任务
	 */
	@Override
	public boolean saveAITask(String data,HttpServletRequest request) throws Exception {
		JSONObject parseJson = JSON.parseObject(data);
		String taskName = parseJson.getString("taskName");	   
		String remarks = parseJson.getString("remarks");		
		
		Task task = new Task();
		task.setAccount(Long.valueOf(GetUserUtils.getUser(request).getId()));
		task.setCreateAccount(GetUserUtils.getUser(request).getUserName());
		task.setTaskName(taskName);
		task.setTaskType(String.valueOf(TaskType.AI.getIndex()));
		if(remarks != null && !"".equalsIgnoreCase(remarks)){
			task.setRemarks(remarks);
		}
		//还原原始JSON字符串
		String parseData = AIOptsUtils.toAIOpts(data);
		task.setScript(parseData);
		return taskDaoImpl.insert(task);
	}
	
	/**
	 * 删除AI任务
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public boolean deleteAITask(Task task,HttpServletRequest req) throws Exception {
		if(task == null){
			return false;
		}
		//调用RestAPI接口  删除模型
		Map<String,String> map = new HashMap<String,String>();
		map.put("userName",task.getCreateAccount());
		map.put("taskName",task.getTaskName());
		boolean boo = new AIPythonVO().restPythonForDelete(JSON.toJSONString(map,SerializerFeature.DisableCircularReferenceDetect));
		if(boo){
			//删除评估表中的记录
			/*AIEvaluation evaluation = new AIEvaluation();
			evaluation.setReportName(task.getCreateAccount() + "_" + task.getTaskName());
			evaluationServiceImpl.deleteEvaluation(evaluation);*/
			//查询本地中间表记录
			AIIntermedia interMedia = new AIIntermedia();
			interMedia.setUserName(task.getCreateAccount());
			interMedia.setTaskName(task.getTaskName());
			List<AIIntermedia> interMedias = intermediaServiceImpl.listIntermedias(interMedia);
			if(null != interMedias && interMedias.size() > 0){
				//删除hive中间表(过滤不符合条件的中间表)
				List<Map<String,String>> hiveTables = new ArrayList<Map<String,String>>();
				for (AIIntermedia aiIntermedia : interMedias) {
					Map<String,String> tables = new HashMap<String,String>();
					tables.put("selectV", aiIntermedia.getTableName());
					hiveTables.add(tables);
				}
				//删除本地中间表记录
				if(new AIHiveVO().deleteTables(hiveTables)){
					return intermediaServiceImpl.deleteIntermedia(interMedia);
				}else{
					return false;
				}
			}
		}
		return boo;
	}
	
	/**
	 * 更新AI任务
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public boolean updateAITask(String data,HttpServletRequest req) throws Exception{
		boolean flag = false;
		JSONObject jsonObject = JSON.parseObject(data);
		String taskName = jsonObject.getString("taskName");
		
		Task exsitTask = new Task();
		exsitTask.setTaskName(taskName);		
		try{
			String parseData = AIOptsUtils.toAIOpts(data);
			List<Task> list = taskDaoImpl.selects(exsitTask);
			if(list == null || list.size() == 0){
				throw new Exception("updateAITask failure,The task does not exist!");
			}
			Task task = list.get(0);
			task.setScript(parseData);
			flag = taskDaoImpl.update(task);	
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 执行AI任务(任务页面)
	 */
	@Override
	public Map<String,Object> executeAITask(Task task,HttpServletRequest req) throws Exception{
		Map<String, Object> retMap = new HashMap<>();
        retMap.put("returnCode", "0");
        
        String taskTime = String.valueOf(new Date().getTime());
		String pythonJson = AIOptsUtils.parseTask(taskTime,task, req);
        try {
        	//调用rest接口   执行机器学习任务
        	boolean flag = new AIPythonVO().restPythonForTask(pythonJson,null);
        	if(flag){
        		 retMap.put("returnCode","1");
                 retMap.put("returnMessage","执行机器学习任务成功");
        	}else{
        		retMap.put("returnMessage","执行机器学习任务失败");
        	}
        } catch (Exception e) {
            retMap.put("returnMessage", "执行机器学习任务异常！");
        }
        return retMap;
	}
	
	/**
	 * 执行AI任务(机器学习页面)
	 * @return
	 */
	@Override
	public boolean executeAIConfig(String data,HttpServletRequest request) throws Exception{
		JSONObject parseJson = JSON.parseObject(data);
		String taskName = parseJson.getString("taskName");	
		String taskTime = parseJson.getString("taskTime");
		String pyPort = parseJson.getString("pyPort");
		String pythonJson = null;
		if(!StringUtils.isEmpty(taskName)){				 //任务-->机器学习
			Task task = new Task();
			task.setTaskName(taskName);
			//查询任务详情
			List<Task> list = taskDaoImpl.selects(task);
			if(null != list && list.size() > 0){
				Task exsitTask = list.get(0);
				exsitTask.setScript(AIOptsUtils.toAIOpts(data));
				pythonJson = AIOptsUtils.parseTask(taskTime,exsitTask, request);
			}else{
				return false;
			}
		}else{										     //页面配置
			String comJson = parseJson.getString("json");		   
			String optsJson = parseJson.getString("optsEntity");   
			pythonJson = AIOptsUtils.parseOpts(taskTime,comJson,optsJson, request);
		}
    	boolean flag = new AIPythonVO().restPythonForTask(pythonJson,pyPort);
		return flag;
	}
	
	/**
	 * 获取AI执行日志
	 */
	@Override
	public String getAILog(String data) throws Exception {
		JSONObject jsonObject = JSON.parseObject(data);
		String userName = jsonObject.getString("userName");
		String taskName = jsonObject.getString("taskName");
		String taskTime = jsonObject.getString("taskTime");
		String pyPort = jsonObject.getString("pyPort");
		if(StringUtils.isBlank(userName)){
			return null;
		}if(StringUtils.isBlank(taskName)){
			taskName = "unknown-task";
		}
		//远程调用RestAPI获取日志
		return this.getLog(userName, taskName, taskTime, pyPort);
	}
	
	/**
	 * 获取AI执行日志  <当前节点>
	 */
	@Override
	public String getComAILog(String data) throws Exception{
		Map<String,Object> optsMap = JSONObject.parseObject(data);
		String optsEntity = String.valueOf(optsMap.get("optsEntity"));
		List<AIOptsVO> optList = JSONObject.parseArray(optsEntity, AIOptsVO.class);
		//解析查询参数
		String userName = (String) optsMap.get("userName");
		String taskName = (String)optsMap.get("taskName");
		AIOptsVO optsVO = AIOptsUtils.getOpts(optList);
		if(StringUtils.isBlank(userName)){
			return null;
		}if(StringUtils.isBlank(taskName)){
			taskName = "unknown-task";
		}
		//远程调用RestAPI获取日志
		String log = this.getLog(userName, taskName, null, null);
		if(!StringUtils.isBlank(log)){
			if(optsVO != null){
				String comCode = optsVO.getComCode();
				log = log.substring(log.indexOf(comCode + "开始"), log.indexOf(comCode + "结束") + comCode.length() + 2);
			}
		}
		return log;
	}
	

	/**
	 * 查看组件数据
	 */
	@Override
	public Map<String, Object> getAIData(String data) throws Exception {
		Map<String, Object> retMap = new HashMap<>();
		Map<String,Object> optsMap = JSONObject.parseObject(data);
		String optsEntity = String.valueOf(optsMap.get("optsEntity"));
		String taskName = String.valueOf(optsMap.get("taskName"));
		List<AIOptsVO> optList = JSONObject.parseArray(optsEntity, AIOptsVO.class);
		if(optList != null && optList.size() >0){
			String hiveTbName = AIOptsUtils.parseSQLOpts(optList.get(0),taskName);
			if(hiveTbName == null || "".equals(hiveTbName)){
				throw new SQLException("SQL Query error! 表或数据库不存在");
			}
			List<Map<String, String>> list = new AIHiveVO().getQueryAll(hiveTbName);
			//封装Map数据
	        List<Object> column = new TreeList<>();
			if (null != list && list.size() > 0) {
		         Map<String, String> map = list.get(list.size() - 1);
		         if(map.get("retCode") != null){
		            retMap.put("retCode", map.get("retCode"));
		            return retMap;
		         }
		         Set<String> keySet = map.keySet();
		         for (Object key : keySet) {
		             column.add(key);
		         }
		     }
		     retMap.put("data", list);
		     retMap.put("column", column);
		}
		return retMap;
	}
	
	/**
	 * 获取最近数据源节点属性
	 * @throws Exception 
	 */
	@Override
	public List<Map<String,String>> getPreOpts(String data, HttpServletRequest req) throws Exception {
		//解析JSON属性数据
		Map<String,Object> optsMap = JSONObject.parseObject(data);
		String nodeId = String.valueOf(optsMap.get("nodeId"));
		String query = String.valueOf(optsMap.get("query"));
		String json = String.valueOf(optsMap.get("json"));
		String optsEntity = String.valueOf(optsMap.get("optsEntity"));
		//获取最近数据源节点数据
		AIOptsVO optsVO = AIOptsUtils.getPreNodeOpts(nodeId,json,optsEntity);
		if(optsVO != null){
			if("hiveTable".equalsIgnoreCase(optsVO.getComCode()))
				return getDataSourceList(optsVO,"hiveTable",query);
			if("SSQLCom".equalsIgnoreCase(optsVO.getComCode()))
				return getDataSourceList(optsVO,"SSQLCom",query);
		}else{
			throw new IllegalArgumentException("属性参数配置不正确");
		}
		return null;
	}
	
	/**
	 * 获取执行结果
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String,Object> getExecReport(String json) throws Exception {
		//解析JSON属性数据
		Map map = JSONObject.parseObject(json);
		String taskName = String.valueOf(map.get("taskName"));
		String userName = String.valueOf(map.get("userName"));
		//查询报告记录
		String reportName = userName + "_" + taskName;
		if(StringUtils.isBlank(taskName)){
			reportName = userName + "_" + "unknown-task";
		}
		AIEvaluation evaluation = new AIEvaluation();
		evaluation.setReportName(reportName);
		AIEvaluation exsitEvaluation = evaluationServiceImpl.getEvaluation(evaluation);
		//评估报告  1.聚类评估<饼状图>
		if(null != exsitEvaluation && "1".equals(exsitEvaluation.getReportType())){
			String reportInfo = exsitEvaluation.getReportInfo();
			Map<String,Object> eMap = JSON.parseObject(reportInfo);
			eMap.put("reportType", "1");
			//封装返回的比例
	      	List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			Map<String,Object> ratio = (Map<String, Object>) eMap.get("ratio");
	      	for(Object key : ratio.keySet()){
	      		Map<String,Object> rMap =new HashMap<String,Object>();
      	  	  	rMap.put("name",key);
      	  	  	rMap.put("value",ratio.get(key));
      	  	  	list.add(rMap);
	      	}
	      	eMap.put("ratio",list);
	      	return eMap;
		}
		return null;
	}

	/**
	 * 获取Hive数据库
	 */
	@Override
	public List<Map<String, String>> getHiveDatabases() throws Exception {
		List<Map<String,String>> list = null;
		//调用Hive连接接口获取数据库
		list = new AIHiveVO().getHiveDatabases();
		if(list == null){
			throw new Exception("获取Hive数据库失败");
		}
		return list;
	}

	
	/**
	 * 根据数据库名称获取Hive表
	 */
	@Override
	public List<Map<String, String>> getHiveTables(String json) throws Exception {
		List<Map<String,String>> list = null;
		JSONObject jsonObject = JSON.parseObject(json);
		String dbName = jsonObject.getString("dbName");
		//调用Hive连接接口获取数据库
		list = new AIHiveVO().getHiveTables(dbName);
		if(list == null){
			throw new Exception("获取Hive数据表失败");
		}
		return list;
	}
	
	
	/**
	 * 封装数据源节点数据
	 * @param optsVO
	 * @param comCode
	 * @return
	 * @throws Exception
	 */
	private List<Map<String,String>> getDataSourceList(AIOptsVO optsVO , String comCode,String query) throws Exception{
		List<Map<String,String>> list = new ArrayList<>();
		if("hiveTable".equalsIgnoreCase(comCode)){
			String hiveTbName = AIOptsUtils.parseSQLOpts(optsVO,null);
			if(hiveTbName == null || "".equals(hiveTbName)){
				throw new SQLException("SQL Query error! 表或数据库不存在");
			}
			list = new AIHiveVO().getTableFields(hiveTbName);
		}
		if("SSQLCom".equalsIgnoreCase(comCode)){
			List<OptsInfo> optsInfos = optsVO.getList();
			for (OptsInfo optsInfo : optsInfos) {
				if("sql".equalsIgnoreCase(optsInfo.getOpt())){
					StringBuffer buffer = new StringBuffer();
					String sql = optsInfo.getOptDefault();
					Map<String,Object> vMap = optsInfo.getOptSelectDefault();
					if(vMap != null && !vMap.isEmpty()){
						sql = String.valueOf(vMap.get("selectV"));
					}
					//解析SQL语句
					String [] str = sql.substring(sql.indexOf("select") + 7, sql.indexOf("from") - 1).split(",");
					for (String string : str) {
						if(string.contains("case") || string.contains("CASE")){
							buffer.append(string.substring(string.lastIndexOf(" ") + 1,string.length()).trim());
						}else{
							buffer.append(string.trim());
						}
						buffer.append(",");
					}
					sql = buffer.substring(0,buffer.lastIndexOf(","));
					for (String field : sql.split(",")) {
						Map<String,String> map = new HashMap<String,String>();
						map.put("selectV", field);
						list.add(map);
					}
					break;
				}
			}
		}
		return selectDataSourceList(list,query);
	}
	
	/**
	 * 按照条件查询数据源数据
	 */
	private List<Map<String,String>> selectDataSourceList(List<Map<String,String>> dataSourceList,String query){
		if(dataSourceList == null || dataSourceList.size() == 0){
			return null;
		}
		if(!StringUtils.isBlank(query)){
			Iterator<Map<String, String>> it = dataSourceList.iterator();
			while(it.hasNext()){
				Map<String, String> map = it.next();
				String field = map.get("selectV");
				if(null != field && !field.startsWith(query)){
					it.remove();
				}
			}
		}
		return dataSourceList;
	}
	
	
	/**
	 * 调用Rest API获取日志
	 */
	private String getLog(String userName,String taskName,String taskTime,String pyPort) throws Exception {
		String log = null;
		Map<String,String> map = new HashMap<String,String>();
		//调用rest接口  获取日志
		if(!StringUtils.isBlank(taskTime)){
	    	String logName = userName + "-" + taskName + "-" + taskTime + ".log";
	    	map.put("filename", logName);
	    	log = new AIPythonVO().restPythonForLog(JSON.toJSONString(map,SerializerFeature.DisableCircularReferenceDetect),pyPort);
		}else{
			String logName = userName + "-" + taskName + ".log";
	    	map.put("filename", logName);
	    	log = new AIPythonVO().restPythonForComLog(JSON.toJSONString(map,SerializerFeature.DisableCircularReferenceDetect),pyPort);
		}
		//获取日志内容
		if(!StringUtils.isBlank(log)){
			log = log.replaceAll("\"", "").replaceAll("\\\\","").replaceAll("\n\n","\n").replaceAll("\n","<br/>");
		}
		return log;
	}
	
	
	/**
	 * 组装AI左侧树数据
	 */
	private List<MenuTree> getMenuTree(AITreeEntity treeEntity) throws Exception{
		List<MenuTree> treeNodes = new ArrayList<MenuTree>();
		List<AITreeVO> treeList = comDaoImpl.getTree(treeEntity);
		//遍历List 封装Menu对象
		if(treeList != null && treeList.size()>0){
			for (AITreeVO treeVO : treeList) {
				MenuTree menuTree = new MenuTree();
				this.treeEntityToMenu(treeVO, menuTree);
				//递归  非叶子结点
				if(treeVO.getIsleaf() != null && "0".equals(treeVO.getIsleaf())){
					treeEntity.setParentCode(treeVO.getComCode());
					menuTree.setList(getMenuTree(treeEntity));
				}
				treeNodes.add(menuTree);
			}
		}
		return treeNodes;
	}
	
	/**
	 * 解析属性List
	 */
	private List<AIOptsVO> parseList(Map<String,String> map) throws Exception{
		List<AIOptsVO> list = new ArrayList<AIOptsVO>();
		String comName = (String) map.get("comName");
		//封装AIOpts对象列表
		List<AIOptsEntity> optsList = comDaoImpl.getOpts(map);
		if(optsList != null && optsList.size() > 0){
			List<OptsInfo> list1 = new ArrayList<OptsInfo>();
			List<OptsInfo> list2 = new ArrayList<OptsInfo>();
			String comCode = optsList.get(0).getComCode();
			String rightOption = optsList.get(0).getRightOption();
			String tabName1 = "",tabName2 = "";
			//封装tab字段
			for(AIOptsEntity opt : optsList){
				if(opt.getOptDisplay() != null && "0".equalsIgnoreCase(opt.getOptDisplay())){
					continue;
				}
				OptsInfo optsInfo = this.optsEntityToInfo(opt);
				if(opt.getTab() != null && "1".equalsIgnoreCase(opt.getTab())){
					tabName1 = opt.getTabName();
					list1.add(optsInfo);
				}
				if(opt.getTab() != null && "2".equalsIgnoreCase(opt.getTab())){
					tabName2 = opt.getTabName();
					list2.add(optsInfo);
				}
			}
			//添加属性对象
			if(list1 != null && list1.size()>0){
				AIOptsVO opts1 = new AIOptsVO();
				//添加组件流描述
				this.flowEntityToVO(opts1, comCode);
				this.optsEntityToVO(opts1,comCode,comName,rightOption,"1",tabName1,list1);
				list.add(opts1);
			}
			if(list2 != null && list2.size()>0){
				AIOptsVO opts2 = new AIOptsVO();
				//添加组件流描述
				this.flowEntityToVO(opts2, comCode);
				this.optsEntityToVO(opts2,comCode,comName,rightOption,"2",tabName2,list2);
				list.add(opts2);
			}
		}
		return list;
	}
	
	
	/**
	 * 将AIOptsEntity转化为AIOptsVO对象
	 */
	private void optsEntityToVO(AIOptsVO optVO,String comCode,String comName,
			String rightOption,String tab,String tabName,List<OptsInfo> list) throws Exception{
		optVO.setComType("1");
		optVO.setComCode(comCode);
		optVO.setComName(comName);
		optVO.setRightOption(rightOption);
		optVO.setTab(tab);
		optVO.setTabName(tabName);
		if("hiveTable".equalsIgnoreCase(comCode)){
			//封装DB
			optVO.setSelectDB(this.getHiveDatabases());
			Map dbMap = new HashMap();
			dbMap.put("selectV",null);
			optVO.setDbDefault(dbMap);
			//封装table
			Map tbMap = new HashMap();
			tbMap.put("selectV",null);
			optVO.setTbDefault(tbMap);
		}else{
			optVO.setList(list);
		}
	}
	
	/**
	 * 将AIFlowEntity转化为AIOptsVO对象
	 */
	private void flowEntityToVO(AIOptsVO optVO,String comCode) throws Exception{
		AIFlowEntity flowEntity = new AIFlowEntity();
		flowEntity.setComCode(comCode);
		List<AIFlowVO> flowList = comDaoImpl.getFlow(flowEntity);
		//获取组件流描述
		AIFlowVO inFlow = flowList.get(0);
		AIFlowVO outFlow = flowList.get(1);
		if(null == inFlow){
			optVO.setInNum(0);
			optVO.setFlowDesc(outFlow == null ? null : outFlow.getFlowDesc());
		}else{
			String [] inDesc = inFlow.getFlowDesc().split(",");
			optVO.setInNum(inDesc.length);
			optVO.setFlowDesc(outFlow == null ? inFlow.getFlowDesc() : inFlow.getFlowDesc() + "," +outFlow.getFlowDesc());
		}
	}	
	
	/**
	 * 将AITreeEntity转化为MenuTree对象
	 */
	private void treeEntityToMenu(AITreeVO treeVO,MenuTree menuTree){
		menuTree.setId(treeVO.getId());
		menuTree.setComCode(treeVO.getComCode());
		menuTree.setComName(treeVO.getComName());
		menuTree.setParentCode(treeVO.getParentCode());
		menuTree.setIn_out(treeVO.getIn_out());
		menuTree.setIsleaf(treeVO.getIsleaf());
	}
	
	/**
	 * 将AIOptsEntity转化为OptsInfo对象
	 */
	private OptsInfo optsEntityToInfo(AIOptsEntity optsEntity){
		OptsInfo optsInfo = new OptsInfo();
		optsInfo.setOpt(optsEntity.getOpt()) ;
		optsInfo.setOptName(optsEntity.getOptName());
		optsInfo.setOptType(optsEntity.getOptType());
		optsInfo.setOptDisplay(optsEntity.getOptDisplay());
		optsInfo.setOptPrompt(optsEntity.getOptPrompt());
		optsInfo.setOptNecessary(optsEntity.getOptNecessary());
		optsInfo.setOptIndex(optsEntity.getOptIndex());
		optsInfo.setOptDescr(optsEntity.getOptDesrc());
		optsInfo.setOptDefault(optsEntity.getOptDefault());
		//封装默认值
		if("input".equalsIgnoreCase(optsEntity.getOptType())){
			optsInfo.setOptTypeSupported(optsEntity.getOptTypeSupported());
		}
		if("select".equalsIgnoreCase(optsEntity.getOptType())){
			Map<String,Object> optMap = new HashMap<String,Object>();
			optMap.put("selectV",optsEntity.getOptDefault());
			optsInfo.setOptSelectDefault(optMap);	
			//封装下拉列表
			if(!"".equals(optsEntity.getOptSupported()) && optsEntity.getOptSupported() != null){
				String[] split = optsEntity.getOptSupported().split(",");
				List<Map<String,String>> list = new ArrayList<Map<String,String>>();
				for (String string : split) {
					Map<String,String> map = new HashMap<String,String>();
					map.put("selectV", string);
					list.add(map);
				}
				optsInfo.setOptSupported(list);
			}
		}
		return optsInfo;
	}

}

package com.hand.bdss.web.intelligence.model.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.hand.bdss.web.entity.AIModelEntity;
import com.hand.bdss.web.intelligence.component.vo.AIOptsVO;
import com.hand.bdss.web.intelligence.component.vo.OptsInfo;
import com.hand.bdss.web.intelligence.model.dao.ModelDao;
import com.hand.bdss.web.intelligence.model.service.ModelService;
import com.hand.bdss.web.intelligence.model.vo.AIModelVO;
/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习模型Service实现类
 */
@Service
public class ModelServiceImpl implements ModelService {
	
	@Resource
	private ModelDao modelDaoImpl;
	
	/**
	 * 返回AIModel左侧树列表
	 * @param modelEntity
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<AIModelVO> listAIModel(AIModelEntity modelEntity) throws Exception{
		List<AIModelVO> modelEntitys = modelDaoImpl.listAIModel(modelEntity);
		if(modelEntitys != null && modelEntitys.size()>0){
			for(AIModelVO modelV : modelEntitys) {
				//遍历model名称列表  
				String [] modelNames = modelV.getModelName().split(",");
				String [] userNames = modelV.getUserName().split(",");
				List<Map> list = new ArrayList<Map>();
				//封装组件Map
				for(int i = 0; i < modelNames.length ; i++){
					Map<String,String> map = new HashMap<String,String>();
					map.put("comName", modelNames[i]);
					map.put("userName",userNames[i]);
					map.put("taskName",modelV.getTaskName());
					list.add(map);
				}
				modelV.setNameList(list);
			}
		}
		return modelEntitys;
	}
	
	/**
	 * 获取AIModel列表
	 * @param modelEntity
	 * @return
	 */
	@Override
	public List<AIModelEntity> getAIModel(AIModelEntity modelEntity) throws Exception{
		List<AIModelEntity> list = modelDaoImpl.getAIModel(modelEntity);
		return list;
	}
	

	/**
	 * 解析AIModel
	 */
	@Override
	public List<AIOptsVO> parseAIModel(AIModelEntity modelEntity) throws Exception {
		List<AIOptsVO> list = new ArrayList<AIOptsVO>();
		//获取model模型
		List<AIModelEntity> modelList = modelDaoImpl.getAIModel(modelEntity);
		if(modelList != null && modelList.size()>0){
			AIModelEntity model = modelList.get(0);
			//封装成AIOptsVO对象
			AIOptsVO optV = new AIOptsVO();
			optV.setComType("0");
			this.modelToOpts(model, optV);
			list.add(optV);
		}
		return list;
	}
	
	/*
	 * Model对象转换成AIOptsVO对象
	 */
	private void modelToOpts(AIModelEntity model,AIOptsVO optV){
		optV.setComCode(model.getComCode());
		optV.setComName(model.getComName());
		optV.setInNum(0);
		optV.setFlowDesc("model");
		optV.setTab("1");
		optV.setTabName("说明");
		List<OptsInfo> list = this.parseOptsList(model.getModelOpts());
		optV.setList(list);
	}
	
	/*
	 * 获取OptsInfo列表
	 */
	private List<OptsInfo> parseOptsList(String modelOpts){
		List<OptsInfo> list = new ArrayList<OptsInfo>();
		Map<String, Object> map = JSON.parseObject(modelOpts);
		for(Map.Entry<String, Object> entry : map.entrySet()){
			OptsInfo optsV = new OptsInfo();
			optsV.setOpt(entry.getKey());
			optsV.setOptDefault(String.valueOf(entry.getValue()));			
			list.add(optsV);
		}
		return list;
	}

}

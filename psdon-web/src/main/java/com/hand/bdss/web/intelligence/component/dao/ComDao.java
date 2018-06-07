package com.hand.bdss.web.intelligence.component.dao;

import java.util.List;
import java.util.Map;

import com.hand.bdss.web.entity.AIFlowEntity;
import com.hand.bdss.web.entity.AIOptsEntity;
import com.hand.bdss.web.entity.AITreeEntity;
import com.hand.bdss.web.intelligence.component.vo.AIFlowVO;
import com.hand.bdss.web.intelligence.component.vo.AITreeVO;
/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习组件dao接口
 */
public interface ComDao {
	
	List<AITreeVO> getTree(AITreeEntity aITreeEntity) throws Exception;
	
	List<AITreeEntity> getTreeWithCondition(AITreeEntity aITreeEntity) throws Exception;
	
	List<AIOptsEntity> getOpts(Map map) throws Exception;
	
	List<AIFlowVO> getFlow(AIFlowEntity aIFlowEntity) throws Exception;


}

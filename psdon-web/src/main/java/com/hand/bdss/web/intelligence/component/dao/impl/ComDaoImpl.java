package com.hand.bdss.web.intelligence.component.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hand.bdss.web.intelligence.component.dao.ComDao;
import com.hand.bdss.web.intelligence.component.vo.AIFlowVO;
import com.hand.bdss.web.intelligence.component.vo.AITreeVO;
import com.hand.bdss.web.common.util.SupportDaoUtils;
import com.hand.bdss.web.entity.AIFlowEntity;
import com.hand.bdss.web.entity.AIOptsEntity;
import com.hand.bdss.web.entity.AITreeEntity;
/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习组件dao实现类
 */
@Repository
public class ComDaoImpl extends SupportDaoUtils implements ComDao{
	
	private static final String MAPPERURL_TREE = "com.hand.bdss.web.entity.AITreeEntity.";
	private static final String MAPPERURL_OPTS = "com.hand.bdss.web.entity.AIOptsEntity.";
	private static final String MAPPPERURL_FLOW = "com.hand.bdss.web.entity.AIFlowEntity.";
	
	@Override
	public List<AITreeVO> getTree(AITreeEntity aITreeEntity) throws Exception{
		List<AITreeVO> list = getSqlSession().selectList(MAPPERURL_TREE + "listAITree",aITreeEntity);
		return list;
	}
	
	@Override
	public List<AITreeEntity> getTreeWithCondition(AITreeEntity aITreeEntity) throws Exception {
		List<AITreeEntity> list = getSqlSession().selectList(MAPPERURL_TREE + "getAITree",aITreeEntity);
		return list;
	}
	
	@Override
	public List<AIOptsEntity> getOpts(Map map) throws Exception{
		List<AIOptsEntity> list = getSqlSession().selectList(MAPPERURL_OPTS + "listAIOpts",map);
		return list;
	}
	
	
	@Override
	public List<AIFlowVO> getFlow(AIFlowEntity aIFlowEntity) throws Exception{
		List<AIFlowVO> list = getSqlSession().selectList(MAPPPERURL_FLOW + "getAIFlow",aIFlowEntity);
		return list;
	}

}

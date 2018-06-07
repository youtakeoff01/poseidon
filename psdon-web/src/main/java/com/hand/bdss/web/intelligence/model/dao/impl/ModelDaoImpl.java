package com.hand.bdss.web.intelligence.model.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hand.bdss.web.common.util.SupportDaoUtils;
import com.hand.bdss.web.entity.AIModelEntity;
import com.hand.bdss.web.intelligence.model.dao.ModelDao;
import com.hand.bdss.web.intelligence.model.vo.AIModelVO;
/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习模型Dao实现类
 */
@Repository
public class ModelDaoImpl extends SupportDaoUtils implements ModelDao {
	
	private static final String MAPPER_URL = "com.hand.bdss.web.entity.AIModelEntity.";
	
	@Override
	public List<AIModelVO>  listAIModel(AIModelEntity modelEntity) throws Exception{
		List<AIModelVO> list = getSqlSession().selectList(MAPPER_URL+"listAIModel", modelEntity);
		return list;
	}
	
	@Override
	public List<AIModelEntity> getAIModel(AIModelEntity modelEntity) throws Exception{
		List<AIModelEntity> list = getSqlSession().selectList(MAPPER_URL+"getAIModel",modelEntity);
		return list;
	} 
}

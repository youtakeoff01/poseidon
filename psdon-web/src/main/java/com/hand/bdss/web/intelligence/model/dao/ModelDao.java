package com.hand.bdss.web.intelligence.model.dao;

import java.util.List;

import com.hand.bdss.web.entity.AIModelEntity;
import com.hand.bdss.web.intelligence.model.vo.AIModelVO;
/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习模型Dao接口
 */
public interface ModelDao {

	List<AIModelVO> listAIModel(AIModelEntity modelEntity) throws Exception;

	List<AIModelEntity> getAIModel(AIModelEntity modelEntity) throws Exception;

}

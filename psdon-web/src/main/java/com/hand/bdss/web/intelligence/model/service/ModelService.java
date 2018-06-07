package com.hand.bdss.web.intelligence.model.service;

import java.util.List;

import com.hand.bdss.web.entity.AIModelEntity;
import com.hand.bdss.web.intelligence.component.vo.AIOptsVO;
import com.hand.bdss.web.intelligence.model.vo.AIModelVO;
/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习模型Service接口
 */
public interface ModelService {

	List<AIModelVO> listAIModel(AIModelEntity modelEntity) throws Exception;

	List<AIOptsVO> parseAIModel(AIModelEntity modelEntity) throws Exception;

	List<AIModelEntity> getAIModel(AIModelEntity modelEntity) throws Exception;

}

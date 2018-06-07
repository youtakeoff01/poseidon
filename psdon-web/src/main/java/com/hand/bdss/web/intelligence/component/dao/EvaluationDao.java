package com.hand.bdss.web.intelligence.component.dao;

import com.hand.bdss.web.entity.AIEvaluation;

/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习组件dao接口
 */
public interface EvaluationDao {
	AIEvaluation getEvaluation(AIEvaluation evaluation);
	int deleteEvaluation(AIEvaluation evaluation);
}

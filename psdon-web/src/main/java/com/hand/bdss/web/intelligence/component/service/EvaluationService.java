package com.hand.bdss.web.intelligence.component.service;

import com.hand.bdss.web.entity.AIEvaluation;

/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习评估Service类
 */
public interface EvaluationService {
	
	public AIEvaluation getEvaluation(AIEvaluation evaluation) throws Exception;
	
	public boolean deleteEvaluation(AIEvaluation evaluation) throws Exception;

}

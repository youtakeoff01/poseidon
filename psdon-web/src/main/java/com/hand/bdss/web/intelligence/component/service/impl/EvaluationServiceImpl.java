package com.hand.bdss.web.intelligence.component.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hand.bdss.web.entity.AIEvaluation;
import com.hand.bdss.web.intelligence.component.dao.EvaluationDao;
import com.hand.bdss.web.intelligence.component.service.EvaluationService;

/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习评估Service实现类
 */
@Service
public class EvaluationServiceImpl implements EvaluationService {
	
	@Resource
	private EvaluationDao evaluationDaoImpl;

	@Override
	public AIEvaluation getEvaluation(AIEvaluation evaluation) throws Exception {
		return evaluationDaoImpl.getEvaluation(evaluation);
	}

	@Override
	public boolean deleteEvaluation(AIEvaluation evaluation) throws Exception {
		int i = evaluationDaoImpl.deleteEvaluation(evaluation);
		if( i > 0){
			return true;
		}
		return false;
	}

}

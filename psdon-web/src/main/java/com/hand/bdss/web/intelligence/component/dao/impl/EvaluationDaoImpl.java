package com.hand.bdss.web.intelligence.component.dao.impl;

import org.springframework.stereotype.Repository;

import com.hand.bdss.web.common.util.SupportDaoUtils;
import com.hand.bdss.web.entity.AIEvaluation;
import com.hand.bdss.web.intelligence.component.dao.EvaluationDao;

/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习评估表dao实现类
 */
@Repository
public class EvaluationDaoImpl extends SupportDaoUtils implements EvaluationDao {
	
	private static final String BASE_URL = "com.hand.bdss.web.entity.AIEvaluation.";

	@Override
	public AIEvaluation getEvaluation(AIEvaluation evaluation) {
		return getSqlSession().selectOne(BASE_URL + "getEvaluation", evaluation);
	}

	@Override
	public int deleteEvaluation(AIEvaluation evaluation) {
		return getSqlSession().delete(BASE_URL + "deleteEvaluation",evaluation);
	}

}

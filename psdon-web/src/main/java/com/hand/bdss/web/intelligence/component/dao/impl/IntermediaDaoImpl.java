package com.hand.bdss.web.intelligence.component.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hand.bdss.web.common.util.SupportDaoUtils;
import com.hand.bdss.web.entity.AIIntermedia;
import com.hand.bdss.web.intelligence.component.dao.IntermediaDao;

/**
 * 机器学习 实验中间表DAO实现类
 * 
 * @author wangyong
 * @date 2017-01-24
 * @version v1.0
 */
@Repository
public class IntermediaDaoImpl extends SupportDaoUtils implements IntermediaDao{
	private static final String MAPPERURL_URL = "com.hand.bdss.web.entity.AIIntermedia.";

	@Override
	public List<AIIntermedia> listIntermedias(AIIntermedia interMedia) {
		return getSqlSession().selectList(MAPPERURL_URL + "listIntermedias",interMedia);
	}

	@Override
	public int deleteIntermedia(AIIntermedia interMedia) throws Exception {
		return getSqlSession().delete(MAPPERURL_URL + "deleteIntermedia", interMedia);
	}

}

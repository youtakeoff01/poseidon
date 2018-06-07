package com.hand.bdss.web.intelligence.component.dao;

import java.util.List;

import com.hand.bdss.web.entity.AIIntermedia;

/**
 * 机器学习实验中间表DAO接口
 * 
 * @author wangyong
 * @date 2017-01-24
 * @version v1.0
 */
public interface IntermediaDao {
	
	List<AIIntermedia> listIntermedias(AIIntermedia interMedia) throws Exception;

	int deleteIntermedia(AIIntermedia interMedia) throws Exception; 

}

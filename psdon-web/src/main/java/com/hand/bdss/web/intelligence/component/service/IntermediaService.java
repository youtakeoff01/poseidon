package com.hand.bdss.web.intelligence.component.service;

import java.util.List;
import java.util.Map;

import com.hand.bdss.web.entity.AIIntermedia;

/**
 * 机器学习 Intermedia Service类
 * 
 * @author wangyong
 * @date 2017-01-24
 * @version v1.0
 */
public interface IntermediaService {
	
	public List<AIIntermedia> listIntermedias(AIIntermedia interMedia) throws Exception;

	public boolean deleteIntermedia(AIIntermedia interMedia) throws Exception;

}

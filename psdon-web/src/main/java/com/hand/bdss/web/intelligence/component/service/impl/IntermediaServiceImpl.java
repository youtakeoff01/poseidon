package com.hand.bdss.web.intelligence.component.service.impl;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hand.bdss.web.entity.AIIntermedia;
import com.hand.bdss.web.intelligence.component.dao.IntermediaDao;
import com.hand.bdss.web.intelligence.component.service.IntermediaService;


/**
 * 机器学习 Intermedia Service实现类
 * 
 * @author wangyong
 * @date 2017-01-24
 * @version v1.0
 */
@Service
public class IntermediaServiceImpl implements IntermediaService {
	
	@Resource
	private IntermediaDao intermediaDaoImpl;

	/**
	 * 获取中间表数据
	 */
	@Override
	public List<AIIntermedia> listIntermedias(AIIntermedia interMedia) throws Exception {
		List<AIIntermedia> interMedias = null;
		if(null != interMedia.getTaskName()){
			interMedias = intermediaDaoImpl.listIntermedias(interMedia);
		}
		return interMedias;
	}

	/**
	 * 删除中间表数据
	 */
	@Override
	public boolean deleteIntermedia(AIIntermedia interMedia) throws Exception {
		int i = intermediaDaoImpl.deleteIntermedia(interMedia);
		if(i > 0){
			return true;
		}
		return false;
	}

}

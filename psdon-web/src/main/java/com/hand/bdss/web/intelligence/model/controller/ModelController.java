package com.hand.bdss.web.intelligence.model.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.entity.AIModelEntity;
import com.hand.bdss.web.intelligence.model.service.ModelService;
import com.hand.bdss.web.intelligence.model.vo.AIModelVO;

/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习模型Controller类
 */
@Controller
@RequestMapping(value = "modelController",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ModelController {
	
	private static final Logger logger = LoggerFactory.getLogger(ModelController.class);
	
	@Resource
	private ModelService modelServiceImpl;
	
	/**
	 * 获取机器学习模型列表
	 * @param modelEntity
	 * @param req
	 * @return
	 */
	@RequestMapping("listAIModel")
	@ResponseBody
	public String listAIModel(@RequestBody AIModelEntity modelEntity,HttpServletRequest req){
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		if(null == modelEntity){
			base.setReturnMessage("请求参数为空");
			return base.toString();
		}
		logger.info("ModelController.listAIModel start! param = {},{}",modelEntity.getUserName(),modelEntity.getModelType());
		try {
			//管理员过滤
			if(GetUserUtils.isRootUser(req)){
				modelEntity.setUserName(null);
			}
			List<AIModelVO> list = modelServiceImpl.listAIModel(modelEntity);
			base.setReturnCode("1");
			base.setReturnMessage("获取机器学习模型数据成功");
			base.setReturnObject(list);
		} catch (Exception e) {
			logger.info("ModelController.listAIModel error!",e);
			base.setReturnMessage("获取机器学习模型数据失败");
		}
		logger.info("ModelController.listAIModel end!");
		return base.toString();
	}
	
	
}

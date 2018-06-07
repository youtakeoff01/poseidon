package com.hand.bdss.web.intelligence.model.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author wangyong
 * @version v1.0
 * @description AI模型VO类
 */
public class AIModelVO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String taskName;		//任务名称
	private String userName;		//用户名称
	private String modelName;		//模型名称
	private String modelPath;		//模型路径
	private String modelOpts;		//模型属性
	private List<Map> nameList;		//模型名称List
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getModelPath() {
		return modelPath;
	}
	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}
	public String getModelOpts() {
		return modelOpts;
	}
	public void setModelOpts(String modelOpts) {
		this.modelOpts = modelOpts;
	}
	public List<Map> getNameList() {
		return nameList;
	}
	public void setNameList(List<Map> nameList) {
		this.nameList = nameList;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	

}

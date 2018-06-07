package com.hand.bdss.web.entity;

import java.io.Serializable;

/**
 * AI模型实体类
 * @author wangyong
 *
 */
public class AIModelEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;				//主键id
	private String modelName;	//模型名称
	private String modelType;	//模型类型
	private String modelPath;	//模型路径
	private String userName;	//用户名	
	private String taskName;	//任务名称
	private String modelOpts;	//模型属性
	private String comCode;		//模型Code
	private String comName;		//模型Name
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getModelType() {
		return modelType;
	}
	public void setModelType(String modelType) {
		this.modelType = modelType;
	}
	public String getModelPath() {
		return modelPath;
	}
	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getModelOpts() {
		return modelOpts;
	}
	public void setModelOpts(String modelOpts) {
		this.modelOpts = modelOpts;
	}
	public String getComCode() {
		return comCode;
	}
	public void setComCode(String comCode) {
		this.comCode = comCode;
	}
	public String getComName() {
		return comName;
	}
	public void setComName(String comName) {
		this.comName = comName;
	}

}

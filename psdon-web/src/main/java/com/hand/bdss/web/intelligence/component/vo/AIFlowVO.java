package com.hand.bdss.web.intelligence.component.vo;

import java.io.Serializable;
/**
 * @author wangyong
 * @version v1.0
 * @description AI组件流VO类
 */
public class AIFlowVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String comCode;			//组件名称
	private String flowDesc;		//流描述
	
	public String getComCode() {
		return comCode;
	}
	public void setComCode(String comCode) {
		this.comCode = comCode;
	}
	public String getFlowDesc() {
		return flowDesc;
	}
	public void setFlowDesc(String flowDesc) {
		this.flowDesc = flowDesc;
	}
	
	
	
	

}

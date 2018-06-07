package com.hand.bdss.web.entity;

import java.io.Serializable;
/**
 * 组件流实体类
 * @author wangyong
 *
 */
public class AIFlowEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;			 //主键
	private String comCode;  //组件代码
	private String pipeline; //管道<in/out>
	private String type;     //类型
	private String descr;    //描述
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getComCode() {
		return comCode;
	}
	public void setComCode(String comCode) {
		this.comCode = comCode;
	}
	public String getPipeline() {
		return pipeline;
	}
	public void setPipeline(String pipeline) {
		this.pipeline = pipeline;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	
	

}

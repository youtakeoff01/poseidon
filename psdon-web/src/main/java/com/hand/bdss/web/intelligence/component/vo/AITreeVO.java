package com.hand.bdss.web.intelligence.component.vo;

import java.io.Serializable;
/**
 * @author wangyong
 * @version v1.0
 * @description AI组件树VO类<mapper映射类>
 */
public class AITreeVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;			        //主键
	private String comCode;         //组件代码
	private String comName;         //组件名称
	private String parentCode;      //上级组件代码
	private String isleaf;          //是否叶子结点
	private String descr;           //组件描述
	private String in_out;          //组件出口字符串
	
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
	public String getComName() {
		return comName;
	}
	public void setComName(String comName) {
		this.comName = comName;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public String getIsleaf() {
		return isleaf;
	}
	public void setIsleaf(String isleaf) {
		this.isleaf = isleaf;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getIn_out() {
		return in_out;
	}
	public void setIn_out(String in_out) {
		this.in_out = in_out;
	}
	
	

}

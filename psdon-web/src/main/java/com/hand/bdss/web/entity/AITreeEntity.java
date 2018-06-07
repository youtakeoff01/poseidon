package com.hand.bdss.web.entity;

import java.io.Serializable;

/**
 * 组件树实体类
 * @author wangyong
 *
 */
public class AITreeEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;			        //主键
	private String comCode;         //组件代码
	private String comName;         //组件名称
	private String parentCode;      //上级组件代码
	private String isleaf;          //是否叶子结点
	private String descr;           //组件描述
	
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
	
	
	
}

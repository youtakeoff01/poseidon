package com.hand.bdss.web.intelligence.component.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author wangyong
 * @version v1.0
 * @description AI组件树VO类
 */
public class MenuTree implements Serializable{

	private static final long serialVersionUID = 1042943960139055158L;
	
	private int id;
	private String comCode;
	private String comName;
	private String parentCode;
	private String isleaf;
	private String in_out;
	private List<MenuTree> list;
	
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
	public String getIsleaf() {
		return isleaf;
	}
	public void setIsleaf(String isleaf) {
		this.isleaf = isleaf;
	}
	public String getIn_out() {
		return in_out;
	}
	public void setIn_out(String in_out) {
		this.in_out = in_out;
	}
	public List<MenuTree> getList() {
		return list;
	}
	public void setList(List<MenuTree> list) {
		this.list = list;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
}

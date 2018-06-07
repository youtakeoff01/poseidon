package com.hand.bdss.web.entity;

import java.io.Serializable;
/**
 * 组件状态类
 * @author hand
 *
 */
public class ComStateNumEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String comName;
	private int normalNum;
	private int unnormalNum;
	
	public String getComName() {
		return comName;
	}
	public void setComName(String comName) {
		this.comName = comName;
	}
	public int getNormalNum() {
		return normalNum;
	}
	public void setNormalNum(int normalNum) {
		this.normalNum = normalNum;
	}
	public int getUnnormalNum() {
		return unnormalNum;
	}
	public void setUnnormalNum(int unnormalNum) {
		this.unnormalNum = unnormalNum;
	}
	

}

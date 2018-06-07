package com.hand.bdss.web.entity;

import java.io.Serializable;
/**
 * 学生信息表
 * @author liqifei
 * @DATA 2018年4月19日
 */
public class StudentEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8555704955132870379L;
	
	private String id;
	private String name;
	private String sid;//学号
	private String adNumber;//ad账号
	private String major;//专业
	private String email;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getAdNumber() {
		return adNumber;
	}
	public void setAdNumber(String adNumber) {
		this.adNumber = adNumber;
	}
	public String getMajor() {
		return major;
	}
	public void setMajor(String major) {
		this.major = major;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}

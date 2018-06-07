package com.hand.bdss.web.entity;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class JarInfoEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4041786119284902228L;
	private String id;
	private String jarName;
	private String jarPath;
	private String createUser;
	@JSONField (format="yyyy-MM-dd HH:mm:ss")  
	private Date createTime;
	private String updateUser;
	@JSONField (format="yyyy-MM-dd HH:mm:ss")  
	private Date updateTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getJarName() {
		return jarName;
	}
	public void setJarName(String jarName) {
		this.jarName = jarName;
	}
	public String getJarPath() {
		return jarPath;
	}
	public void setJarPath(String jarPath) {
		this.jarPath = jarPath;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
}

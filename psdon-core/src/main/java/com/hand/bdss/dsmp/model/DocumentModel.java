package com.hand.bdss.dsmp.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class DocumentModel implements Serializable{

	private static final long serialVersionUID = -751603058269278484L;
	
	//文件名称
	private String name;
	//type 文件类型0：文件夹 1：非文件夹
	private boolean isDirectory;
	//文件大小 byte
	private Long size;
	//最近更新时间
	private Timestamp updatetime;
	//文件路径
	private String srcPath;
	
	private List<DocumentModel> list;
	
	public String getSrcPath() {
		return srcPath;
	}
	
	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Timestamp getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}

	public List<DocumentModel> getList() {
		return list;
	}

	public void setList(List<DocumentModel> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "DocumentModel{" +
				"name='" + name + '\'' +
				", isDirectory=" + isDirectory +
				", size=" + size +
				", updatetime='" + updatetime + '\'' +
				", srcPath='" + srcPath + '\'' +
				'}';
	}
}

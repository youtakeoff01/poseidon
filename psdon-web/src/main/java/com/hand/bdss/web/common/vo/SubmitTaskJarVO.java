package com.hand.bdss.web.common.vo;

import java.io.Serializable;

import com.hand.bdss.web.entity.JarInfoEntity;
import com.hand.bdss.web.entity.LatestTaskEntity;
import com.hand.bdss.web.entity.TaskAttributeEntity;

public class SubmitTaskJarVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3370108805640416071L;
	private JarInfoEntity jarInfoEntity;
	private LatestTaskEntity latestTaskEntity;
	private TaskAttributeEntity taskAttributeEntity;
	private int startPage;
	private int count;

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	
	public JarInfoEntity getJarInfoEntity() {
		return jarInfoEntity;
	}

	public void setJarInfoEntity(JarInfoEntity jarInfoEntity) {
		this.jarInfoEntity = jarInfoEntity;
	}

	public LatestTaskEntity getLatestTaskEntity() {
		return latestTaskEntity;
	}

	public void setLatestTaskEntity(LatestTaskEntity latestTaskEntity) {
		this.latestTaskEntity = latestTaskEntity;
	}

	public TaskAttributeEntity getTaskAttributeEntity() {
		return taskAttributeEntity;
	}

	public void setTaskAttributeEntity(TaskAttributeEntity taskAttributeEntity) {
		this.taskAttributeEntity = taskAttributeEntity;
	}
	
	
	

}

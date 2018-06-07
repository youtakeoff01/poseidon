package com.hand.bdss.web.common.vo;

import java.util.List;

import com.hand.bdss.web.common.annotation.NotEmpty;

/**
 *  库同步实体类
 * @author Administrator
 *
 */
public class BaseSyncVO {
	private String resourceBaseType;//来源库的类型
	private String targetBaseType;//目标库的类型 
	@NotEmpty
	private String syncTaskName;//库同步任务名称
	@NotEmpty
	private String resourceBaseId;//来源库的id
	@NotEmpty
	private List<String> tables;//需要同步的表
	@NotEmpty
	private String targetBaseName;//目标库的名称
	
	public String getResourceBaseType() {
		return resourceBaseType;
	}
	public void setResourceBaseType(String resourceBaseType) {
		this.resourceBaseType = resourceBaseType;
	}
	public String getTargetBaseType() {
		return targetBaseType;
	}
	public void setTargetBaseType(String targetBaseType) {
		this.targetBaseType = targetBaseType;
	}
	public String getSyncTaskName() {
		return syncTaskName;
	}
	public void setSyncTaskName(String syncTaskName) {
		this.syncTaskName = syncTaskName;
	}
	public String getResourceBaseId() {
		return resourceBaseId;
	}
	public void setResourceBaseId(String resourceBaseId) {
		this.resourceBaseId = resourceBaseId;
	}
	public List<String> getTables() {
		return tables;
	}
	public void setTables(List<String> tables) {
		this.tables = tables;
	}
	public String getTargetBaseName() {
		return targetBaseName;
	}
	public void setTargetBaseName(String targetBaseName) {
		this.targetBaseName = targetBaseName;
	}
}

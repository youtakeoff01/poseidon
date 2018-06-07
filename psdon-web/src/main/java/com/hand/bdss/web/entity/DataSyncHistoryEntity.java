package com.hand.bdss.web.entity;

import java.io.Serializable;

public class DataSyncHistoryEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8156455061849025112L;
	private String jobName;//任务名称
	private String jobType;//任务类型
	private String jobState;//任务状态
	private String sqlType;//脚本类型
	private String startTime;//更新时间
	private String endTime;//更新时间
	private String executeTime;//执行时间
	private String execId;//任务执行ID
	private String execJobName;//阿兹卡班任务名称

	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobState() {
		return jobState;
	}
	public void setJobState(String jobState) {
		this.jobState = jobState;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getExecuteTime() {
		return executeTime;
	}

	public String getSqlType() {
		return sqlType;
	}

	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}

	public void setExecuteTime(String executeTime) {
		this.executeTime = executeTime;
	}

	public String getExecId() {
		return execId;
	}

	public void setExecId(String execId) {
		this.execId = execId;
	}

	public String getExecJobName() {
		return execJobName;
	}

	public void setExecJobName(String execJobName) {
		this.execJobName = execJobName;
	}
}

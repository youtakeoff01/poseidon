package com.hand.bdss.web.operationcenter.console.vo;

/**
 * 控制台  执行实体类
 * @author wangyong
 *
 */
public class TaskExecVO {
	
	private String taskName ;				//任务名
	private String taskAccount;				//创建人
	private String execType;				//执行类型
	private String execTime;				//执行时长
	private int errorCount;					//出错次数
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getExecTime() {
		return execTime;
	}
	public void setExecTime(String execTime) {
		this.execTime = execTime;
	}
	public Integer getErrorCount() {
		return errorCount;
	}
	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}
	public String getTaskAccount() {
		return taskAccount;
	}
	public void setTaskAccount(String taskAccount) {
		this.taskAccount = taskAccount;
	}
	public String getExecType() {
		return execType;
	}
	public void setExecType(String execType) {
		this.execType = execType;
	}
	
	
	

}

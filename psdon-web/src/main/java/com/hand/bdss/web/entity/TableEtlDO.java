package com.hand.bdss.web.entity;

/**
 * tb_etl表字段的封装
 * 
 * @author hand
 *
 */
public class TableEtlDO implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;// job id
	private String jobName;// job 名字
	private String jobType;// job 类型（任务的类型:数据同步/机器学习/sql查询/shell脚本）
	private int dataSourceId; // job对应的数据源id
	private String jobConfig;// job的配置
	private String updateTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getJobConfig() {
		return jobConfig;
	}

	public void setJobConfig(String jobConfig) {
		this.jobConfig = jobConfig;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public int getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(int dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

}

package com.hand.bdss.dsmp.model;

import java.io.Serializable;

/**
 * ETL任务配置类，MYSQL存储的配置JSON就是来自于ETLJobConfig对象
 * 
 * @author William
 *
 */
public class ETLJobConfig implements Serializable {

	private static final long serialVersionUID = 4364111272036710854L;

    private Integer action; // 0增 1删 2改
    private String jobId; // 任务ID（主键ID）
	private String jobName; // 任务名称
	private String jobType; // 任务类型(数据同步/机器学习/sql查询/shell脚本)
	private String dataType;//数据类型(JDBC/File)
	private DataSource dataSource; // 数据来源
	private String synDB;//数据同步库
	private String syncSource; //同步源 即目标表名
	private Integer syncType; // 同步策略，0全量 1增量
	private Integer is_partition;//是否分区,0分区，1不分区
	private String partition;//分区字段
	private String partitionType;//分区字段类型
	private String kjbFilePath;//kjb 文件路径
	
	private Integer num;//任务并行度

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
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

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getSyncSource() {
		return syncSource;
	}

	public void setSyncSource(String syncSource) {
		this.syncSource = syncSource;
	}

	public Integer getSyncType() {
		return syncType;
	}

	public void setSyncType(Integer syncType) {
		this.syncType = syncType;
	}

    public String getKjbFilePath() {
		return kjbFilePath;
	}

	public void setKjbFilePath(String kjbFilePath) {
		this.kjbFilePath = kjbFilePath;
	}
	

	

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}
	
	

	public String getPartitionType() {
		return partitionType;
	}

	public void setPartitionType(String partitionType) {
		this.partitionType = partitionType;
	}

	@Override
    public String toString() {
        return "ETLJobConfig{" +
                "action=" + action +
                ", jobId='" + jobId + '\'' +
                ", jobName='" + jobName + '\'' +
                ", jobType='" + jobType + '\'' +
                ", dataSource=" + dataSource +
                ", num=" + num +
                ", syncSource='" + syncSource + '\'' +
                ", syncType=" + syncType +
                "}";
    }

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getSynDB() {
		return synDB;
	}

	public void setSynDB(String synDB) {
		this.synDB = synDB;
	}

	public Integer getIs_partition() {
		return is_partition;
	}

	public void setIs_partition(Integer is_partition) {
		this.is_partition = is_partition;
	}

	public String getPartition() {
		return partition;
	}

	public void setPartition(String partition) {
		this.partition = partition;
	}
}

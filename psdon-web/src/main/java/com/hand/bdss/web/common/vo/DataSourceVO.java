package com.hand.bdss.web.common.vo;

import java.util.Map;

public class DataSourceVO implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String jobName;// 任务名称
    private String jobType; //任务类型
	private String dataType;// 数据源类型（jdbc odbc files）
	private int dataSourceId;// 数据源的ID
	private String dataTable;// 数据表
	private String syncType;// 同步方式0表示全量，1表示增量
	private String syncFilelds; //同步字段
	private Map<String,String> dataFilter;// 数据过滤
	private String udc;// 自定义条件
	private String filePath;// 文件路径
	private String syncSource;// 同步源 需要同步到本地哪个hive/hbase表
    private String syncDB;         //同步库
    private Integer num;//并行度
    private String is_partition;   //是否分区(0表示分区  1表示不分区)
    private String partition;      //分区字段
    private String partitionType; //分区字段类型
    private String remark;         //任务备注
    
	
    
	public String getPartitionType() {
		return partitionType;
	}

	public void setPartitionType(String partitionType) {
		this.partitionType = partitionType;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public int getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(int dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public String getDataTable() {
		return dataTable;
	}

	public void setDataTable(String dataTable) {
		this.dataTable = dataTable;
	}

	public String getSyncType() {
		return syncType;
	}

	public void setSyncType(String syncType) {
		this.syncType = syncType;
	}
	public String getSyncFilelds() {
		return syncFilelds;
	}

	public void setSyncFilelds(String syncFilelds) {
		this.syncFilelds = syncFilelds;
	}

	public Map<String, String> getDataFilter() {
		return dataFilter;
	}

	public void setDataFilter(Map<String, String> dataFilter) {
		this.dataFilter = dataFilter;
	}

	public String getUdc() {
		return udc;
	}

	public void setUdc(String udc) {
		this.udc = udc;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getSyncSource() {
		return syncSource;
	}

	public void setSyncSource(String syncSource) {
		this.syncSource = syncSource;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String taskType) {
		this.jobType = taskType;
	}

	public String getSyncDB() {
		return syncDB;
	}

	public void setSyncDB(String syncDB) {
		this.syncDB = syncDB;
	}

	public String getIs_partition() {
		return is_partition;
	}

	public void setIs_partition(String is_partition) {
		this.is_partition = is_partition;
	}

	public String getPartition() {
		return partition;
	}

	public void setPartition(String partition) {
		this.partition = partition;
	}
}

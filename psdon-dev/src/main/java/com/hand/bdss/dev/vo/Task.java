package com.hand.bdss.dev.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author : Koala
 * @version : v1.0
 * @description :
 */
public class Task implements Serializable {
    private static final long serialVersionUID = 5629660165426621051L;
    private long id;
    private Long account; //用户唯一id
    private String taskName;//任务名
    private String dependencies;//任务依赖任务名称
    private String sqlStc;//sql语句
    private String taskType;//任务类型
    private String sqlType;//sql类型
    private String sourceDb;//来源库
    private String sourceTable;//来源表
    private String targetDB;//目标库
    private String targetTable;//目标表名
    private String fieldMapping;//字段映射
    private String filterCondition;//过滤条件
    private String action;//sql语句操作类型（insert,update..）
    private String timerAttribute;//定时器属性
    private String notificationId;//子那个定义通知规则ID
    private Date createtime;//创建时间
    private Date updatetime;//更新时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date starttime;//查询数据起始时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endtime;//查询数据结束时间
    private String partition;//源表分区字段
    private String partitionType;//增量字段类型
    private String status;//任务状态
    private String remarks;//任务备注
    private String script;//机器学习json
    private String dataType;//sqoop任务 数据源类型
    
    private Integer num;//sqoop 任务并行度

    private String ruleName;//规则名称
    private String createAccount;//创建人

    //数据来源 ：db.table
    @SuppressWarnings("unused")
	private String dataSource;
    //数据目标 ：db.table
    @SuppressWarnings("unused")
	private String dataTarget;

    public Task() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getAccount() {
        return account;
    }

    public void setAccount(Long account) {
        this.account = account;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    public String getSqlStc() {
        return sqlStc;
    }

    public void setSqlStc(String sqlStc) {
        this.sqlStc = sqlStc;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getSourceDb() {
        return sourceDb;
    }

    public void setSourceDb(String sourceDb) {
        this.sourceDb = sourceDb;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public String getTargetDB() {
        return targetDB;
    }

    public void setTargetDB(String targetDB) {
        this.targetDB = targetDB;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public String getFieldMapping() {
        return fieldMapping;
    }

    public void setFieldMapping(String fieldMapping) {
        this.fieldMapping = fieldMapping;
    }

    public String getFilterCondition() {
        return filterCondition;
    }

    public void setFilterCondition(String filterCondition) {
        this.filterCondition = filterCondition;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTimerAttribute() {
        return timerAttribute;
    }

    public void setTimerAttribute(String timerAttribute) {
        this.timerAttribute = timerAttribute;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }
    

    public String getPartitionType() {
		return partitionType;
	}

	public void setPartitionType(String partitionType) {
		this.partitionType = partitionType;
	}

	public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getCreateAccount() {
        return createAccount;
    }

    public void setCreateAccount(String createAccount) {
        this.createAccount = createAccount;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataSource() {
        return sourceDb + "." + sourceTable;
    }
    public String getDataTarget() {
        return targetDB + "." + targetTable;
    }
    
    

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}
	

	

	@Override
    public String toString() {
        return "Task{" +
                "account=" + account +
                ", taskName='" + taskName + '\'' +
                ", sqlStc='" + sqlStc + '\'' +
                ", taskType='" + taskType + '\'' +
                ", sqlType='" + sqlType + '\'' +
                ", targetDB='" + targetDB + '\'' +
                ", targetTable='" + targetTable + '\'' +
                ", fieldMapping='" + fieldMapping + '\'' +
                ", filterCondition='" + filterCondition + '\'' +
                ", action='" + action + '\'' +
                ", num='" + num + '\'' + 
                ", timerAttribute='" + timerAttribute + '\'' +
                ", createtTime=" + createtime +
                ", updateTime=" + updatetime +
                ", startTime=" + starttime +
                ", endTime=" + endtime +
                ", partition='" + partition + '\'' +
                ", status='" + status + '\'' +
                ", dataType='" + dataType + '\'' +
                '}';
    }
}

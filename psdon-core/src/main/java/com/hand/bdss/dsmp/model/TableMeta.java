package com.hand.bdss.dsmp.model;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public class TableMeta {

	private String sqlTableName;//RDBMS表名

	private String dbType;//RDBMS类型

	private List<String> primaryKey;

    private Integer syncType; // 同步策略，0全量 1增量  hive

    private Integer is_partition;//是否分区,0分区，1不分区  hive
    
    private Integer num;//任务并行度

    private String partition;//分区字段  hive
    
    private String partitionType;//分区字段类型

	private String  synDB;//hive数据同步库

	private String tableName;//hive表名

	private List<Pair<String, String>> columns;

	public String getColumnType(String columnName) {
		for (Pair<String, String> c : columns) {
			if (c.getLeft().equals(columnName)) {
				return c.getRight();
			}
		}
		return null;
	}

	
	


	public Integer getNum() {
		return num;
	}





	public void setNum(Integer num) {
		this.num = num;
	}





	public String getDbType() {
		return dbType;
	}


	public void setDbType(String dbType) {
		this.dbType = dbType;
	}


	public String getSynDB() {
		return synDB;
	}

	public void setSynDB(String synDB) {
		this.synDB = synDB;
	}

	public void setSyncType(Integer syncType) {
        this.syncType = syncType;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public Integer getSyncType() {
        return syncType;
    }

    public String getPartition() {
        return partition;
    }

    public Integer getIs_partition() {
		return is_partition;
	}

	public void setIs_partition(Integer is_partition) {
		this.is_partition = is_partition;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(List<String> primaryKey) {
		this.primaryKey = primaryKey;
	}

	public List<Pair<String, String>> getColumns() {
		return columns;
	}

	public void setColumns(List<Pair<String, String>> columns) {
		this.columns = columns;
	}

	public String getSqlTableName() {
		return sqlTableName;
	}

	public void setSqlTableName(String sqlTableName) {
		this.sqlTableName = sqlTableName;
	}
	public String getPartitionType() {
		return partitionType;
	}
	public void setPartitionType(String partitionType) {
		this.partitionType = partitionType;
	}
	
	
	
}

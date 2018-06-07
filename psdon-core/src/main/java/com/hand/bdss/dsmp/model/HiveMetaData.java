package com.hand.bdss.dsmp.model;

import java.io.Serializable;
import java.util.List;

/**
 * 元数据信息实体类
 *
 * @author dingshuang
 */
public class HiveMetaData implements Serializable {

    private static final long serialVersionUID = 6895734173554725772L;
    private String metaDataType;// 元数据类型 hive or hbase
    private List<HiveMetaTableField> metaTableField;// hive表字段
    private String metaDataDescribe;// 元数据描述
    private String dbName ="default";//hive数据同步库
	private String tabelName;// 表名
    private Integer is_partition=1;//是否分区,0分区，1不分区
    private int booUdp;
    private int isExternal;//是否是外部表 0内部表   1外部表
    private String location;//存放文件位置
    
	public int getBooUdp() {
		return booUdp;
	}

	public void setBooUdp(int booUdp) {
		this.booUdp = booUdp;
	}

    public Integer getIs_partition() {
        return is_partition;
    }

    public void setIs_partition(Integer is_partition) {
        this.is_partition = is_partition;
    }
    public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

    public String getTabelName() {
        return tabelName;
    }

    public void setTabelName(String tabelName) {
        this.tabelName = tabelName;
    }

    public String getMetaDataType() {
        return metaDataType;
    }

    public void setMetaDataType(String metaDataType) {
        this.metaDataType = metaDataType;
    }

    public List<HiveMetaTableField> getMetaTableField() {
        return metaTableField;
    }

    public void setMetaTableField(List<HiveMetaTableField> metaTableField) {
        this.metaTableField = metaTableField;
    }

    public String getMetaDataDescribe() {
        return metaDataDescribe;
    }

    public void setMetaDataDescribe(String metaDataDescribe) {
        this.metaDataDescribe = metaDataDescribe;
    }

    public int getIsExternal() {
        return isExternal;
    }

    public void setIsExternal(int isExternal) {
        this.isExternal = isExternal;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

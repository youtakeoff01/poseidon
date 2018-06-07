package com.hand.bdss.dsmp.model;

import java.io.Serializable;

/**
 * hive表字段实体类
 *
 * @author dingshuang
 */
public class HiveMetaTableField implements Serializable {

    private static final long serialVersionUID = 509725725972578193L;

    private String fieldName;// hive表字段名
    private String fieldType;// hive表字段类型
    private String fieldDescribe;// hive表字段描述
    private int flag; //1不是分区字段    0是分区字段
    
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldDescribe() {
        return fieldDescribe;
    }

    public void setFieldDescribe(String fieldDescribe) {
        this.fieldDescribe = fieldDescribe;
    }
    
    
}

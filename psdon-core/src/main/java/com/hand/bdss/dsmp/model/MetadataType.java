package com.hand.bdss.dsmp.model;

/**
 * 元数据类型定义
 * 
 * @author William
 *
 */
public enum MetadataType {

	FILE(1, "FILE"), HIVE(2, "HIVE"), HBASE(3, "HBASE"), CACHE(4, "CACHE"), INDEX(5, "INDEX");

	private Integer id;
	private String type;

	private MetadataType(Integer id, String type) {
		this.id = id;
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return String.valueOf(this.type);
	}

}

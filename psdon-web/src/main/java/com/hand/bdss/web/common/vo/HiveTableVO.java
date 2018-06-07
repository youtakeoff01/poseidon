package com.hand.bdss.web.common.vo;

import java.util.List;

/**
 * 封装Hive表实体
 * @author wangyong
 *
 */
public class HiveTableVO {
	
	private String dbName;  //数据库名称
	
	private List<String> tableList;  //库下的数据表列表

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public List<String> getTableList() {
		return tableList;
	}

	public void setTableList(List<String> tableList) {
		this.tableList = tableList;
	}
	
	

}

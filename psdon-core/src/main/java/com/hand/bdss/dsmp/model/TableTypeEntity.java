package com.hand.bdss.dsmp.model;

import java.io.Serializable;
import java.util.List;

/**
 * 用户ranger权限表和权限实体类
 * @author zk
 */
public class TableTypeEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	private String table;
	
	private List<String> type;
	
	private List<String> user;

	public TableTypeEntity(String table, List<String> type,List<String> user) {
		this.table = table;
		this.type = type;
		this.user = user;
		
	}
	
	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public List<String> getType() {
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return table.toString()+"-"+type.toString();
	}

	public List<String> getUser() {
		return user;
	}

	public void setUser(List<String> user) {
		this.user = user;
	}
	
}

package com.hand.bdss.web.entity;

public class UserGroupEntity implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String groupName;
	private String groupPinYin;
	private String groupCode;
	private int groupParent;
	private String status;
	private String groupDesc; // 用户组描述
	private String createTime;
	private String updateTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupPinYin() {
		return groupPinYin;
	}

	public void setGroupPinYin(String groupPinYin) {
		this.groupPinYin = groupPinYin;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public int getGroupParent() {
		return groupParent;
	}

	public void setGroupParent(int groupParent) {
		this.groupParent = groupParent;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}
}

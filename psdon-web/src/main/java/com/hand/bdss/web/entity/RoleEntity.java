package com.hand.bdss.web.entity;

import java.util.Date;

/**
 * 角色实体类
 * 
 * @author hand
 *
 */
public class RoleEntity implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id; // 角色id
	private String roleName; // 角色名
	private String roleDesc; // 角色描述
	private String status; // 角色状态
	private Date createTime; // 角色创建时间
	private Date updateTime; // 角色更新时间

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}

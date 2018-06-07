package com.hand.bdss.web.entity;

/**
 * 用户的实体类
 * 
 *
 */
public class UserEntity implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;// 用户的id
	private String userName;// 用户的名称
	private String userType;//用户类型（psdon / ldap）
	private String email;// 用户的邮箱
	private String phoneNo;// 用户的手机号
	private String password;// 用户的登录密码
	private String realName;// 用户的真实姓名
	private int groupId;// 用户所在组的id
	private String groupName;// 用户所在组名
	private int roleId;// 用户所属的角色
	private String roleName;// 用户所属角色名称
	private String status;// 用户状态
	private String createAccount;// 创建该用户的管理员
	private String updateAccount;// 修改该用户的管理员
	private String ldapuser;//0 表示是ldap用户登录系统的，1表示dsmp自己的用户登录系统的
	private String createTime;
	private String updateTime;
	
	private String newPassword;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreateAccount() {
		return createAccount;
	}

	public void setCreateAccount(String createAccount) {
		this.createAccount = createAccount;
	}

	public String getUpdateAccount() {
		return updateAccount;
	}

	public void setUpdateAccount(String updateAccount) {
		this.updateAccount = updateAccount;
	}
	
	public String getLdapuser() {
		return ldapuser;
	}
	
	public void setLdapuser(String ldapuser) {
		this.ldapuser = ldapuser;
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

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	

}

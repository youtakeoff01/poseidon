package com.hand.bdss.web.entity;
/**
 * 邮箱状态实体类
 * @author liqifei
 *
 */
public class EmailStatusEntity {
	private String LastLoggedOnUserAccount; //登录用户账号
	private String LastLogonTime;  //时间
	private String StorageLimitStatus; //邮箱状态
	private String TotalItemSize;//邮箱的大小
	
	
	public String getLastLoggedOnUserAccount() {
		return LastLoggedOnUserAccount;
	}
	public void setLastLoggedOnUserAccount(String lastLoggedOnUserAccount) {
		LastLoggedOnUserAccount = lastLoggedOnUserAccount;
	}
	public String getLastLogonTime() {
		return LastLogonTime;
	}
	public void setLastLogonTime(String lastLogonTime) {
		LastLogonTime = lastLogonTime;
	}
	public String getStorageLimitStatus() {
		return StorageLimitStatus;
	}
	public void setStorageLimitStatus(String storageLimitStatus) {
		StorageLimitStatus = storageLimitStatus;
	}
	public String getTotalItemSize() {
		return TotalItemSize;
	}
	public void setTotalItemSize(String totalItemSize) {
		TotalItemSize = totalItemSize;
	}
	
	@Override
	public String toString() {
		return String.join("^", LastLoggedOnUserAccount,LastLogonTime,StorageLimitStatus,TotalItemSize);
	}
}

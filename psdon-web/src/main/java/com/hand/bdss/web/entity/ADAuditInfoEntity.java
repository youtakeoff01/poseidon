package com.hand.bdss.web.entity;

import com.hand.bdss.web.common.util.StringUtils;

public class ADAuditInfoEntity {
	
	private String _time;
	private String EventCode;
	private String TaskCategory;
	private String Source_Workstation;
	private String user;
	private String src_nt_host;
	private String Logon_Account;
	private String Logon_type;
	public String get_time() {
		return _time;
	}
	public void set_time(String _time) {
		String time = null;
		if(StringUtils.isNotEmpty(_time)) {
			String[] strs = _time.split("T|\\+");
			if(strs.length>1) {
				time = strs[0]+" "+strs[1];
			}
		}
		this._time = time;
	}
	public String getEventCode() {
		return EventCode;
	}
	public void setEventCode(String eventCode) {
		EventCode = eventCode;
	}
	public String getTaskCategory() {
		return TaskCategory;
	}
	public void setTaskCategory(String taskCategory) {
		TaskCategory = taskCategory;
	}
	public String getSource_Workstation() {
		return Source_Workstation;
	}
	public void setSource_Workstation(String source_Workstation) {
		Source_Workstation = source_Workstation;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getSrc_nt_host() {
		return src_nt_host;
	}
	public void setSrc_nt_host(String src_nt_host) {
		this.src_nt_host = src_nt_host;
	}
	public String getLogon_Account() {
		return Logon_Account;
	}
	public void setLogon_Account(String logon_Account) {
		Logon_Account = logon_Account;
	}
	public String getLogon_type() {
		return Logon_type;
	}
	public void setLogon_type(String logon_type) {
		Logon_type = logon_type;
	}
	@Override
	public String toString() {
		return String.join("^", _time,EventCode,TaskCategory,Source_Workstation,user,src_nt_host,Logon_Account,Logon_type);
	}

}

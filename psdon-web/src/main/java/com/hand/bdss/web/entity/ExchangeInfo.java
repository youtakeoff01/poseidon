package com.hand.bdss.web.entity;

import com.hand.bdss.web.common.util.StringUtils;

public class ExchangeInfo {
	private String _time;
	private String User;
	private String src_ip;
	private String DeviceType;
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
	public String getUser() {
		return User;
	}
	public void setUser(String user) {
		User = user;
	}
	public String getSrc_ip() {
		return src_ip;
	}
	public void setSrc_ip(String src_ip) {
		this.src_ip = src_ip;
	}
	public String getDeviceType() {
		return DeviceType;
	}
	public void setDeviceType(String deviceType) {
		DeviceType = deviceType;
	}
	
	@Override
	public String toString() {
		return String.join("^", _time,User,src_ip,DeviceType);
	}
}

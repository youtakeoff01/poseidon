package com.hand.bdss.web.entity;

import java.sql.Timestamp;

/**
 * 邮箱实体类
 * @author hand
 *
 */
public class EmailEntity {

	private int id;//邮箱ID
	private String channelName;//渠道名称
	private String channelType;//渠道类型
	private String receiveAcount;//收件邮箱账号
	private String sendServer;//发件服务器
	private String port;//发件服务器端口号	
	private String sendAccount;//发件邮箱账号
	private String emailPassword;//发件邮箱密码
	private String msgHeader;//邮件头
	private String msgTheme;//消息主题
	private String booean_ssl;// 是否使用SSL，true表示使用，false表示不使用
	private Timestamp createTime;
	private Timestamp updateTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getChannelType() {
		return channelType;
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
	public String getReceiveAcount() {
		return receiveAcount;
	}
	public void setReceiveAcount(String receiveAcount) {
		this.receiveAcount = receiveAcount;
	}
	public String getSendServer() {
		return sendServer;
	}
	public void setSendServer(String sendServer) {
		this.sendServer = sendServer;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	
	
	public String getMsgHeader() {
		return msgHeader;
	}
	public void setMsgHeader(String msgHeader) {
		this.msgHeader = msgHeader;
	}
	public String getMsgTheme() {
		return msgTheme;
	}
	public void setMsgTheme(String msgTheme) {
		this.msgTheme = msgTheme;
	}
	public String getSendAccount() {
		return sendAccount;
	}
	public void setSendAccount(String sendAccount) {
		this.sendAccount = sendAccount;
	}
	public String getEmailPassword() {
		return emailPassword;
	}
	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}
	public String getBooean_ssl() {
		return booean_ssl;
	}
	public void setBooean_ssl(String booean_ssl) {
		this.booean_ssl = booean_ssl;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	

}

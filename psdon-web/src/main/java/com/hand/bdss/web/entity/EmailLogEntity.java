package com.hand.bdss.web.entity;

/**
 * 发送邮件日志实体类
 * @author hand
 *
 */
public class EmailLogEntity {
	private int id; //主键ID
	private String emailFrom;//发件人
	private String emailTo;//收件人
	private String sendTime;//发送时间
	private String content;//邮件内容
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEmailFrom() {
		return emailFrom;
	}
	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}
	public String getEmailTo() {
		return emailTo;
	}
	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}
	public String getContent() {
		
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
}

package com.hand.bdss.web.entity;

import java.sql.Timestamp;

/**
 * 通知规则实体类
 * @author hand
 *
 */
public class NoticeRuleEntity {
	private int id;//通知规则id
	private int mailChannelId;//通知渠道的id
	private String mailChannelName;//通知渠道名称
	private String ruleName;//通知规则名称
	private String triggerRule;//触发规则
	private String triggerCondition;//触发条件
	private String ruleNum;//触发值
	private String noticeContent;//通知内容
	private Timestamp createTime;//创建时间
	private Timestamp updateTime;//更新时间
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMailChannelId() {
		return mailChannelId;
	}
	public void setMailChannelId(int mailChannelId) {
		this.mailChannelId = mailChannelId;
	}
	public String getMailChannelName() {
		return mailChannelName;
	}
	public void setMailChannelName(String mailChannelName) {
		this.mailChannelName = mailChannelName;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getTriggerRule() {
		return triggerRule;
	}
	public void setTriggerRule(String triggerRule) {
		this.triggerRule = triggerRule;
	}
	public String getTriggerCondition() {
		return triggerCondition;
	}
	public void setTriggerCondition(String triggerCondition) {
		this.triggerCondition = triggerCondition;
	}
	public String getRuleNum() {
		return ruleNum;
	}
	public void setRuleNum(String ruleNum) {
		this.ruleNum = ruleNum;
	}
	public String getNoticeContent() {
		return noticeContent;
	}
	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
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

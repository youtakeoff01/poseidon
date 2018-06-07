package com.hand.bdss.web.entity;

/**
 * 渠道表对应实体类
 * @author hand
 *
 */
public class ChannelEntity {

	private int id;//渠道ID
	private String channelName;//渠道名称
	private String channelType;//渠道类型
	private String description;//渠道描述
	private String createTime;//创建时间
	private String updateTime;//更新时间
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
}

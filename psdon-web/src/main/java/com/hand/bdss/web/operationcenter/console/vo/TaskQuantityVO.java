package com.hand.bdss.web.operationcenter.console.vo;

import java.util.List;
import java.util.Map;

/**
 * 当天任务数量类
 * @author wangyong
 *
 */
public class TaskQuantityVO {
	
	private String timeType;	//时间点<昨天/今天/历史>
	
	private Map<String,Integer> map;	//时刻任务数量
	
	private List list;	//封装list

	public String getTimeType() {
		return timeType;
	}

	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}

	public Map<String,Integer> getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}
	
	

}

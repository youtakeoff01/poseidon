package com.hand.bdss.web.common.em;

public enum TaskStatus {

	run("运行中",30),success("成功",50),cancel("已取消",60),fail("失败",70),init("新建",0);
	private String name;
	private Integer index;

	TaskStatus(String name, Integer index){
		this.name = name;
		this.index = index;
	}
	
	public String getName() {
		return name;
	}

	public Integer getIndex() {
		return index;
	}

}

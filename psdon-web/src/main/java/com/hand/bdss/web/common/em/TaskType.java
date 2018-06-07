package com.hand.bdss.web.common.em;

public enum TaskType {
	
	SYNC("同步任务",0),SCRIPT("脚本任务",1),AI("人工智能",2),BASE_SYNC("库同步任务",3),JAR_TASK("jar任务",4);
	
	private String name;
	private Integer index;
	
	TaskType(String name, Integer index){
		this.name = name;
		this.index = index;
	}
	
	public String getName() {
		return name;
	}

	public Integer getIndex() {
		return index;
	}

	public static void main(String[] args) {
		System.out.println(TaskType.SCRIPT.getIndex());
		System.out.println(TaskType.SCRIPT.getName());

	}
}

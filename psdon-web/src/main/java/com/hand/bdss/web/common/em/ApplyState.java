package com.hand.bdss.web.common.em;

public enum ApplyState {

	toApproved("待审批", 0), inApproved("审批中", 1), agreeApproved("已审批", 2), disagreeApproved("已拒绝",
			3), invalidApproved("已失效", 4), overApproved("已过期", 5);

	private String name;
	private Integer index;

	ApplyState(String name, Integer index) {
		this.name = name;
		this.index = index;
	}

	public static String getName(int index) {
		for (ApplyState c : ApplyState.values()) {
			if (c.getIndex() == index) {
				return c.name;
			}
		}
		return null;
	}
	
	public static Integer getIndex(String name) {
		for (ApplyState c : ApplyState.values()) {
			if (c.getName().equals(name)) {
				return c.index;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

}

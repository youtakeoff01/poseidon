package com.hand.bdss.web.entity;

import java.io.Serializable;

/**
 * 组件属性实体类
 * @author wangyong
 *
 */
public class AIOptsEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;			     	  //主键
	private String comCode;      	  //组件代码
	private String rightOption;  	  //组件右击属性
	private String tab;		     	  //tab
	private String tabName;      	  //tab名称
	private String opt;          	  //组件属性
	private String optName;	     	  //属性名称
	private String optType;      	  //属性类型<框类型>
	private String optDisplay;   	  //属性是否显示
	private String optPrompt;    	  //属性提示信息
	private String optNecessary; 	  //属性是否必须
	private String optIndex;	 	  //属性显示索引值
	private String optDefault;   	  //属性默认值
	private String optDescr;	 	  //属性描述
	private String optSupported;      //下拉框值	
	private String optTypeSupported;  //输入框校验正则
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getComCode() {
		return comCode;
	}
	public void setComCode(String comCode) {
		this.comCode = comCode;
	}
	public String getTab() {
		return tab;
	}
	public void setTab(String tab) {
		this.tab = tab;
	}
	public String getTabName() {
		return tabName;
	}
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	public String getOpt() {
		return opt;
	}
	public void setOpt(String opt) {
		this.opt = opt;
	}
	public String getOptName() {
		return optName;
	}
	public void setOptName(String optName) {
		this.optName = optName;
	}
	public String getOptType() {
		return optType;
	}
	public void setOptType(String optType) {
		this.optType = optType;
	}
	public String getOptDisplay() {
		return optDisplay;
	}
	public void setOptDisplay(String optDisplay) {
		this.optDisplay = optDisplay;
	}
	public String getOptPrompt() {
		return optPrompt;
	}
	public void setOptPrompt(String optPrompt) {
		this.optPrompt = optPrompt;
	}
	public String getOptNecessary() {
		return optNecessary;
	}
	public void setOptNecessary(String optNecessary) {
		this.optNecessary = optNecessary;
	}
	public String getOptIndex() {
		return optIndex;
	}
	public void setOptIndex(String optIndex) {
		this.optIndex = optIndex;
	}
	public String getOptDefault() {
		return optDefault;
	}
	public void setOptDefault(String optDefault) {
		this.optDefault = optDefault;
	}
	public String getOptDesrc() {
		return optDescr;
	}
	public void setOptDesrc(String optDesrc) {
		this.optDescr = optDesrc;
	}
	public String getRightOption() {
		return rightOption;
	}
	public void setRightOption(String rightOption) {
		this.rightOption = rightOption;
	}
	public String getOptSupported() {
		return optSupported;
	}
	public void setOptSupported(String optSupported) {
		this.optSupported = optSupported;
	}
	public String getOptTypeSupported() {
		return optTypeSupported;
	}
	public void setOptTypeSupported(String optTypeSupported) {
		this.optTypeSupported = optTypeSupported;
	}
}

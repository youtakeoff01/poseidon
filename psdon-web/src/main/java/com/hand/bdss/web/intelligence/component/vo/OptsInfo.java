package com.hand.bdss.web.intelligence.component.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * @author wangyong
 * @version v1.0
 * @description AI组件标签VO类
 */
public class OptsInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String opt;								//标签
	private String optName;							//标签名称
	private String optType;							//标签类型<a/b/c>
	private String optDisplay;						//标签是否显示<是1，否0>
	private String optPrompt;						//标签提示
	private String optNecessary;					//标签是否必须<是1，否0>
	private String optIndex;						//标签索引	
	private Map optSelectDefault; 					//select下拉框默认值
	private String optDefault;						//其他标签默认值
	private String optPopupNum = "";				//弹框选择select数量
	private String optDescr;						//标签描述
	private List optSupported;      				//select下拉框值	
	private String optTypeSupported;  				//输入框校验正则
	
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
	public String getOptDescr() {
		return optDescr;
	}
	public void setOptDescr(String optDescr) {
		this.optDescr = optDescr;
	}

	public String getOptTypeSupported() {
		return optTypeSupported;
	}

	public void setOptTypeSupported(String optTypeSupported) {
		this.optTypeSupported = optTypeSupported;
	}

	public List getOptSupported() {
		return optSupported;
	}

	public void setOptSupported(List optSupported) {
		this.optSupported = optSupported;
	}

	public Map getOptSelectDefault() {
		return optSelectDefault;
	}

	public void setOptSelectDefault(Map optSelectDefault) {
		this.optSelectDefault = optSelectDefault;
	}

	public String getOptDefault() {
		return optDefault;
	}

	public void setOptDefault(String optDefault) {
		this.optDefault = optDefault;
	}
	public String getOptPopupNum() {
		return optPopupNum;
	}
	public void setOptPopupNum(String optPopupNum) {
		this.optPopupNum = optPopupNum;
	}
	

}

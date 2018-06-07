package com.hand.bdss.web.intelligence.component.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author wangyong
 * @version v1.0
 * @description AI组件属性VO类
 */
public class AIOptsVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String comNum = "";				    //随机组件id
	private int inNum;							//入口数量
	private String flowDesc;					//进出口描述
	private String comCode;						//组件代码
	private String comName;						//组件名称
	private String rightOption;					//右击选项类型<a,b,c>
	private String comType;						//组件类型(0隐藏  1显示)
	private String tab;							//tab<1或者2>
	private String tabName;						//tab名称
	private List<OptsInfo> list;				//组件属性列表
	
	//新增hiveTable属性框
	private List selectDB;						//库选择
	private Map dbDefault; 						//库名称
	private Map tbDefault;						//表名称
	
	
	public String getComNum() {
		return comNum;
	}

	public void setComNum(String comNum) {
		this.comNum = comNum;
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

	public List<OptsInfo> getList() {
		return list;
	}

	public void setList(List<OptsInfo> list) {
		this.list = list;
	}

	public String getComCode() {
		return comCode;
	}

	public void setComCode(String comCode) {
		this.comCode = comCode;
	}

	public String getComName() {
		return comName;
	}

	public void setComName(String comName) {
		this.comName = comName;
	}

	public String getRightOption() {
		return rightOption;
	}

	public void setRightOption(String rightOption) {
		this.rightOption = rightOption;
	}

	public String getComType() {
		return comType;
	}

	public void setComType(String comType) {
		this.comType = comType;
	}


	public int getInNum() {
		return inNum;
	}

	public void setInNum(int inNum) {
		this.inNum = inNum;
	}

	public String getFlowDesc() {
		return flowDesc;
	}

	public void setFlowDesc(String flowDesc) {
		this.flowDesc = flowDesc;
	}

	public List getSelectDB() {
		return selectDB;
	}

	public void setSelectDB(List selectDB) {
		this.selectDB = selectDB;
	}

	public Map getDbDefault() {
		return dbDefault;
	}

	public void setDbDefault(Map dbDefault) {
		this.dbDefault = dbDefault;
	}

	public Map getTbDefault() {
		return tbDefault;
	}

	public void setTbDefault(Map tbDefault) {
		this.tbDefault = tbDefault;
	}

	


}

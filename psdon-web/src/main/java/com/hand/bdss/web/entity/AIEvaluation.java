package com.hand.bdss.web.entity;

import java.io.Serializable;

/**
 * @author wangyong
 * @version v1.0
 * @description 机器学习评估实体类
 */
public class AIEvaluation implements Serializable {

	private static final long serialVersionUID = -4941021844088178931L;
	private String reportName;				//报告名称
	private String reportType;				//报告类型
	private String reportInfo;				//报告数据
	
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getReportInfo() {
		return reportInfo;
	}
	public void setReportInfo(String reportInfo) {
		this.reportInfo = reportInfo;
	}
	
}

package com.hand.bdss.web.common.vo;

import java.io.Serializable;

/**
 * Created by cong.xiang on 2017/6/9.
 */
public class ConfigInfo implements Serializable {

    private static final long serialVersionUID = -6858705230737201105L;
    private int id;//配置文件id
    private String code;//配置文件标志
    private String configInfo;//配置文件内容
    private String xmlName;//配置文件名字
    private String configType;//配置文件类型
    
    public String getXmlName() {
		return xmlName;
	}

	public void setXmlName(String xmlName) {
		this.xmlName = xmlName;
	}

	public String getConfigInfo() {
        return configInfo;
    }

    public void setConfigInfo(String configInfo) {
        this.configInfo = configInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getCode() {
		return code;
	}
    public void setCode(String code) {
		this.code = code;
	}
    
    public String getConfigType() {
		return configType;
	}
    public void setConfigType(String configType) {
		this.configType = configType;
	}
}

package com.hand.bdss.dsmp.model;

import java.io.Serializable;

/**
 * Created by cong.xiang on 2017/6/13.
 */
public class ExceptionInfo implements Serializable {
    private static final long serialVersionUID = 9212391050213899359L;
    private String serviceType;
    private String text;

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

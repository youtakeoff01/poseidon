package com.hand.bdss.dsmp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cong.xiang on 2017/6/12.
 */
public class HostInfo implements Serializable {

    private static final long serialVersionUID = -4574869683294640113L;
    private String host_name;
    private List<String> service_name;
    private List<String> component_name;

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public List<String> getService_name() {
        return service_name;
    }

    public void setService_name(List<String> service_name) {
        this.service_name = service_name;
    }

    public List<String> getComponent_name() {
        return component_name;
    }

    public void setComponent_name(List<String> component_name) {
        this.component_name = component_name;
    }
}

package com.hand.bdss.web.datamanage.datalineage.vo;

import java.io.Serializable;

/**
 * Created by hand on 2017/12/1.
 */
public class Column implements Serializable {

    //类型
    private String type;
    //描述
    private String description;
    //名称
    private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

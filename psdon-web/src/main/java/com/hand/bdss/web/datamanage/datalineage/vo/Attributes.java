package com.hand.bdss.web.datamanage.datalineage.vo;

/**
 * Created by hand on 2018/1/9.
 */
public class Attributes {
    //所有者
    private String owner;
    //全称
    private String qualifiedName;
    //节点名称
    private String name;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

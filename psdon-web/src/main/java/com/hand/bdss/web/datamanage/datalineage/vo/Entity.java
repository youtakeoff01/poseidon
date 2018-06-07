package com.hand.bdss.web.datamanage.datalineage.vo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hand on 2017/12/1.
 */
public class Entity implements Serializable {

    //唯一ID
    private String guid;

    //父节点ID
    private String pId;
    //名称
    private Attributes attributes;
    //类型
    private String typeName;
    //描述
    private String displayText;
    //子节点
    private List<Entity> children;
    //状态
    private String status;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public List<Entity> getChildren() {
        return children;
    }

    public void setChildren(List<Entity> children) {
        this.children = children;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

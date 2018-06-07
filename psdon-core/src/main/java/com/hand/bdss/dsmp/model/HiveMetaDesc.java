package com.hand.bdss.dsmp.model;

import java.io.Serializable;

/**
 * Created by hand on 2017/4/21.
 */
public class HiveMetaDesc implements Serializable {

    private static final long serialVersionUID = 8439636161548390369L;

    private String owner;
    private String comment;
    private String  name;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.hand.bdss.web.datamanage.datalineage.vo;

/**
 * Created by hand on 2018/1/9.
 */
public class Relation {
    //起始点
    private String source;
    //结束点
    private String target;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}

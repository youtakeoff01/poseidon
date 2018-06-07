package com.hand.bdss.web.entity;

/**
 * Created by hand on 2017/12/6.
 */
public class ScriptRuleEntity {
    private int id;
    private String scriptTitle;
    private String scriptDesc;
    private String scriptState;
    private String triggerEvent;
    private String createUser;
    private String createDatetime;
    private String updateDatetime;
    private String updateUser;
    private String scriptContent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpdateDatetime() {
        return updateDatetime;
    }

    public void setUpdateDatetime(String updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getScriptContent() {
        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
    }

    public ScriptRuleEntity() {
    }

    public String getScriptTitle() {
        return scriptTitle;
    }

    public void setScriptTitle(String scriptTitle) {
        this.scriptTitle = scriptTitle;
    }

    public String getScriptDesc() {
        return scriptDesc;
    }

    public void setScriptDesc(String scriptDesc) {
        this.scriptDesc = scriptDesc;
    }

    public String getScriptState() {
        return scriptState;
    }

    public void setScriptState(String scriptState) {
        this.scriptState = scriptState;
    }

    public String getTriggerEvent() {
        return triggerEvent;
    }

    public void setTriggerEvent(String triggerEvent) {
        this.triggerEvent = triggerEvent;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }
}

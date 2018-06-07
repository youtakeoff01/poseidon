package com.hand.bdss.web.streaming.paramrule.dao;


import com.hand.bdss.web.entity.ScriptRuleEntity;

import java.util.List;

/**
 * Created by hand on 2017/12/6.
 */
public interface ScriptcrudDao {
    int insertScript(ScriptRuleEntity scriptRuleEntity);
    List<ScriptRuleEntity> listScriptRuleInfo(ScriptRuleEntity scriptRuleEntity, int startPage, int count);
    int updateScript(ScriptRuleEntity scriptRuleEntity);
    int deleteScript(List<ScriptRuleEntity> scriptRuleEntities);
    int listCountScriptInfo(ScriptRuleEntity scriptRuleEntity);
}

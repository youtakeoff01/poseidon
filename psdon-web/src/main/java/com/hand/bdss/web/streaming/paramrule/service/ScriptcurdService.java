package com.hand.bdss.web.streaming.paramrule.service;


import com.hand.bdss.web.entity.ScriptRuleEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by hand on 2017/12/6.
 */
public interface ScriptcurdService {
    int insertScript(HttpServletRequest request,ScriptRuleEntity scriptRuleEntity);
    List<ScriptRuleEntity> listScriptRuleInfo(ScriptRuleEntity scriptRuleEntity, int startPage, int count);
    int updateScript(HttpServletRequest request,ScriptRuleEntity scriptRuleEntity);
    int deleteScript(List<ScriptRuleEntity> scriptRuleEntities);
    int listCountScriptInfo(ScriptRuleEntity scriptRuleEntity);
}

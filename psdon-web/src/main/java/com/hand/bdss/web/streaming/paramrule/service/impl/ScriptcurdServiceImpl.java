package com.hand.bdss.web.streaming.paramrule.service.impl;

import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.entity.ScriptRuleEntity;
import com.hand.bdss.web.streaming.paramrule.dao.ScriptcrudDao;
import com.hand.bdss.web.streaming.paramrule.service.ScriptcurdService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by hand on 2017/12/6.
 *
 */

@Service
public class ScriptcurdServiceImpl implements ScriptcurdService {
    @Resource
    private ScriptcrudDao scriptcrudDaoImpl;

    @Override
    public int insertScript(HttpServletRequest request,ScriptRuleEntity scriptRuleEntity) {
        scriptRuleEntity.setCreateUser(GetUserUtils.getUser(request).getUserName());
        int i = scriptcrudDaoImpl.insertScript(scriptRuleEntity);
        return i;
    }

    @Override
    public List<ScriptRuleEntity> listScriptRuleInfo(ScriptRuleEntity scriptRuleEntity, int startPage, int count) {
        return scriptcrudDaoImpl.listScriptRuleInfo(scriptRuleEntity,startPage,count);
    }

    @Override
    public int updateScript(HttpServletRequest request, ScriptRuleEntity scriptRuleEntity) {
        scriptRuleEntity.setUpdateUser(GetUserUtils.getUser(request).getUserName());
        return  scriptcrudDaoImpl.updateScript(scriptRuleEntity);
    }

    @Override
    public int deleteScript(List<ScriptRuleEntity> scriptRuleEntities) {
        return scriptcrudDaoImpl.deleteScript(scriptRuleEntities);
    }

    @Override
    public int listCountScriptInfo(ScriptRuleEntity scriptRuleEntity) {
        return scriptcrudDaoImpl.listCountScriptInfo(scriptRuleEntity);
    }
}

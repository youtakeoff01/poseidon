package com.hand.bdss.web.streaming.paramrule.dao.impl;

import com.hand.bdss.web.common.util.SupportDaoUtils;
import com.hand.bdss.web.entity.ScriptRuleEntity;
import com.hand.bdss.web.streaming.paramrule.dao.ScriptcrudDao;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hand on 2017/12/6.
 */
@Repository
public class ScriptcrudDaoImpl extends SupportDaoUtils implements ScriptcrudDao {

    private static final String MAPPERURL = "com.hand.bdss.web.entity.ScriptRuleEntity.";
    @Override
    public int insertScript(ScriptRuleEntity scriptRuleEntity) {
        int insert = getSqlSession().insert(MAPPERURL + "insertScript", scriptRuleEntity);
        return insert;
    }

    @Override
    public List<ScriptRuleEntity> listScriptRuleInfo(ScriptRuleEntity scriptRuleEntity, int startPage, int count) {
        RowBounds rowBounds = new RowBounds(startPage,count);
        return getSqlSession().selectList(MAPPERURL + "seletScript", scriptRuleEntity, rowBounds);
    }

    @Override
    public int updateScript(ScriptRuleEntity scriptRuleEntity) {
        return getSqlSession().update(MAPPERURL+"updateScript",scriptRuleEntity);
    }

    @Override
    public int deleteScript(List<ScriptRuleEntity> scriptRuleEntities) {
        return getSqlSession().delete(MAPPERURL+"deleteScript",scriptRuleEntities);
    }

    @Override
    public int listCountScriptInfo(ScriptRuleEntity scriptRuleEntity) {
        return getSqlSession().selectOne(MAPPERURL+"seletCountScript",scriptRuleEntity);
    }
}

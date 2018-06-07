package com.hand.bdss.web.operationcenter.warn.dao.impl;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.hand.bdss.web.operationcenter.warn.dao.NoticeRuleDao;
import com.hand.bdss.web.entity.NoticeRuleEntity;
import com.hand.bdss.web.common.util.SupportDaoUtils;

@Repository
public class NoticeRuleDaoImpl extends SupportDaoUtils implements NoticeRuleDao{
	
	private static final String MAPPERURL = "com.hand.bdss.web.entity.NoticeRuleEntity.";	

	@Override
	public int insertNoticeRule(NoticeRuleEntity noticeruleEntity) throws Exception {
		int i = getSqlSession().insert(MAPPERURL+"insertNoticeRule",noticeruleEntity);
		return i;
	}

	@Override
	public List<NoticeRuleEntity> listNoticeRules(NoticeRuleEntity notice, int startpage, int count) throws Exception {
		RowBounds rowBounds = new RowBounds(startpage, count);
		List<NoticeRuleEntity> lists = getSqlSession().selectList(MAPPERURL + "listNoticeRules", notice, rowBounds);
		return lists;
	}

	@Override
	public int listNoticeRulesCountAll(NoticeRuleEntity notice) throws Exception {
		int i = getSqlSession().selectOne(MAPPERURL+"listNoticeRulesCountAll",notice);
		return i;
	}

	@Override
	public List<NoticeRuleEntity> listNoticeRuleAll(NoticeRuleEntity noticeruleEntity) throws Exception {
		List<NoticeRuleEntity> lists = getSqlSession().selectList(MAPPERURL + "listNoticeRuleAll", noticeruleEntity);
		return lists;
	}

	@Override
	public int updateNoticeRule(NoticeRuleEntity noticeruleEntity) throws Exception {
		int i = getSqlSession().update(MAPPERURL+"updateNoticeRule",noticeruleEntity);
		return i;
	}

	@Override
	public int deleteNoticeRule(List<NoticeRuleEntity> noticeruleEntity) throws Exception {
		int i = getSqlSession().delete(MAPPERURL+"deleteNoticeRule",noticeruleEntity);
		return i;		
	}

}

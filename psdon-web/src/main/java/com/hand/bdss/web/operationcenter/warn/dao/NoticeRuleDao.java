package com.hand.bdss.web.operationcenter.warn.dao;

import java.util.List;

import com.hand.bdss.web.entity.NoticeRuleEntity;

public interface NoticeRuleDao {

	int insertNoticeRule(NoticeRuleEntity noticeruleEntity) throws Exception;

	List<NoticeRuleEntity> listNoticeRules(NoticeRuleEntity notice, int startpage, int count) throws Exception;

	int listNoticeRulesCountAll(NoticeRuleEntity notice) throws Exception;

	List<NoticeRuleEntity> listNoticeRuleAll(NoticeRuleEntity noticeruleEntity) throws Exception;

	int updateNoticeRule(NoticeRuleEntity noticeruleEntity) throws Exception;

	int deleteNoticeRule(List<NoticeRuleEntity> noticeruleEntity) throws Exception;
	

}

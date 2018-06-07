package com.hand.bdss.web.operationcenter.warn.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.entity.NoticeRuleEntity;

public interface NoticeRuleService {

	int insertNoticeRule(NoticeRuleEntity noticeruleEntity) throws Exception;

	List<NoticeRuleEntity> listNoticeRules(NoticeRuleEntity notice, int startpage, int count) throws Exception;

	int listNoticeRulesCountAll(NoticeRuleEntity notice)throws Exception;

	List<NoticeRuleEntity> listNoticeRuleAll(NoticeRuleEntity noticeruleEntity) throws Exception;

	int updateNoticeRule(NoticeRuleEntity noticeruleEntity) throws Exception;

	int deleteNoticeRule(List<NoticeRuleEntity> noticeruleEntity, HttpServletRequest request) throws Exception;
	
	

	 

}

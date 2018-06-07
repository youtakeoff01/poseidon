package com.hand.bdss.web.operationcenter.warn.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.operationcenter.warn.service.NoticeRuleService;
import org.springframework.stereotype.Service;

import com.hand.bdss.web.operationcenter.warn.dao.NoticeRuleDao;
import com.hand.bdss.web.entity.NoticeRuleEntity;

@Service
public class NoticeRuleServiceImpl implements NoticeRuleService {
	
	@Resource
	private NoticeRuleDao noticeRuleDaoImpl;

	@Override
	public int insertNoticeRule(NoticeRuleEntity noticeruleEntity) throws Exception {
		int i = noticeRuleDaoImpl.insertNoticeRule(noticeruleEntity);
		return i;
	}

	@Override
	public List<NoticeRuleEntity> listNoticeRules(NoticeRuleEntity notice, int startpage, int count) throws Exception {
		List<NoticeRuleEntity> list = noticeRuleDaoImpl.listNoticeRules(notice,startpage,count);
		return list;
	}

	@Override
	public int listNoticeRulesCountAll(NoticeRuleEntity notice) throws Exception {
		int i = noticeRuleDaoImpl.listNoticeRulesCountAll(notice);
		return i;
	}

	@Override
	public List<NoticeRuleEntity> listNoticeRuleAll(NoticeRuleEntity noticeruleEntity) throws Exception {
		List<NoticeRuleEntity> list = noticeRuleDaoImpl.listNoticeRuleAll(noticeruleEntity);
		return list;
	}

	@Override
	public int updateNoticeRule(NoticeRuleEntity noticeruleEntity) throws Exception {
		int i = noticeRuleDaoImpl.updateNoticeRule(noticeruleEntity);
		return i;
	}

	@Override
	public int deleteNoticeRule(List<NoticeRuleEntity> noticeruleEntity, HttpServletRequest request) throws Exception {
		int i = noticeRuleDaoImpl.deleteNoticeRule(noticeruleEntity);
		return i;
	}
}



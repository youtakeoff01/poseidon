package com.hand.bdss.web.operationcenter.warn.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.operationcenter.warn.dao.EmailDao;
import com.hand.bdss.web.operationcenter.warn.dao.EmailLogDao;
import com.hand.bdss.web.operationcenter.warn.service.EmailService;
import org.springframework.stereotype.Service;

import com.hand.bdss.web.entity.EmailEntity;

@Service
public class EmailServiceImpl implements EmailService {

	public static String myEmailAccount = "chenglong.wang@hand-china.com";
	public static String myEmailPassword = "1990money.com";
	public static String myEmailSMTPHost = "mail.hand-china.com";
	public static String receiveMailAccount = "qifei.li@hand-china.com";
	@Resource
	private EmailDao emailDaoImpl;
	@Resource
	private EmailLogDao emailLogDaoImpl;
	
	@Override
	public int insertEmail(EmailEntity emailEntity) throws Exception {
		int i = emailDaoImpl.insertEmail(emailEntity);
		return i;
	}
	
	@Override
	public List<EmailEntity> listConditions(EmailEntity emailEntity, int startpage,int count) throws Exception {
		List<EmailEntity> list = emailDaoImpl.listConditions(emailEntity,startpage,count);
		return list;
	}
	
	@Override
	public int updateEmail(EmailEntity emailEntity) throws Exception {
		int i = emailDaoImpl.updateEmail(emailEntity);
		return i;
	}
	
	@Override
	public int deleteEmail(List<EmailEntity> emailEntitys,HttpServletRequest request) throws Exception {
		int i = emailDaoImpl.deleteEmail(emailEntitys);
		return i;
	}

	@Override
	public EmailEntity selectOneRule(EmailEntity emailEntity) throws Exception{
		EmailEntity mail = emailDaoImpl.selectOneRule(emailEntity);
		return mail;
	}

	@Override
	public int listConditionCountAll(EmailEntity mail) throws Exception {
		return emailDaoImpl.listConditionCountAll(mail);
	}

	@Override
	public List<EmailEntity> listEmailsAll(EmailEntity emailEntity) throws Exception {		
		List<EmailEntity> list = emailDaoImpl.listEmailsAll(emailEntity);
		return list;
	}

	
}

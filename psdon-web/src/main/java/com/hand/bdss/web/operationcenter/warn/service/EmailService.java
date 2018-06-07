package com.hand.bdss.web.operationcenter.warn.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.entity.EmailEntity;

public interface EmailService {
	int insertEmail(EmailEntity emailEntity) throws Exception;
	
	List<EmailEntity> listConditions(EmailEntity emailEntity ,int startpage,int count) throws Exception;
	
	int updateEmail(EmailEntity emailEntity) throws Exception;
	
	EmailEntity selectOneRule(EmailEntity emailEntity) throws Exception;

	int listConditionCountAll(EmailEntity mail) throws Exception;

	List<EmailEntity> listEmailsAll(EmailEntity emailEntity) throws Exception;

	int deleteEmail(List<EmailEntity> emailEntity, HttpServletRequest request)throws Exception;

	

	
	
}

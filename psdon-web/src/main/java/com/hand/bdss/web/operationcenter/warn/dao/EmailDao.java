package com.hand.bdss.web.operationcenter.warn.dao;

import java.util.List;

import com.hand.bdss.web.entity.EmailEntity;

public interface EmailDao {
	int insertEmail(EmailEntity emailEntity) throws Exception;
	
	List<EmailEntity> listConditions(EmailEntity emailEntity,int startpage,int count) throws Exception;
	EmailEntity selectOneRule(EmailEntity emailEntity) throws Exception;
	
	int updateEmail(EmailEntity emailEntity) throws Exception;
	
	int deleteEmail(List<EmailEntity> emailEntitys) throws Exception;

	int listConditionCountAll(EmailEntity mail) throws Exception;

	List<EmailEntity> listEmailsAll(EmailEntity emailEntity) throws Exception;
}

package com.hand.bdss.web.operationcenter.warn.dao.impl;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.hand.bdss.web.operationcenter.warn.dao.EmailDao;
import com.hand.bdss.web.entity.EmailEntity;
import com.hand.bdss.web.common.util.SupportDaoUtils;

@Repository
public class EmailDaoImpl extends SupportDaoUtils implements EmailDao{

	private static final String MAPPERURL = "com.hand.bdss.web.entity.EmailEntity.";
	@Override
	public int insertEmail(EmailEntity emailEntity) throws Exception {
		int i = getSqlSession().insert(MAPPERURL+"insertEmail",emailEntity);
		return i;
	}
	@Override
	public List<EmailEntity> listConditions(EmailEntity emailEntity,int startpage,int count) throws Exception {
		RowBounds rowBounds = new RowBounds(startpage, count);
		List<EmailEntity> lists = getSqlSession().selectList(MAPPERURL + "listConditions", emailEntity, rowBounds);
		return lists;
	}
	@Override
	public List<EmailEntity> listEmailsAll(EmailEntity emailEntity)throws Exception {
		List<EmailEntity> lists = getSqlSession().selectList(MAPPERURL + "listEmailsAll", emailEntity);
		return lists;
	}
	@Override
	public int updateEmail(EmailEntity emailEntity) throws Exception {
		int i = getSqlSession().update(MAPPERURL+"updateEmail",emailEntity);
		return i;
	}
	@Override
	public int deleteEmail(List<EmailEntity> emailEntitys) throws Exception {
		int i  = getSqlSession().delete(MAPPERURL+"deleteEmail",emailEntitys);
		return i;
	}
	
	
	
	@Override
	public EmailEntity selectOneRule(EmailEntity emailEntity) throws Exception {
		EmailEntity mails = getSqlSession().selectOne(MAPPERURL+"selectOneRule",emailEntity);
		return mails;
	}
	@Override
	public int listConditionCountAll(EmailEntity mail) throws Exception {
		int i = getSqlSession().selectOne(MAPPERURL+"listConditionCountAll",mail);
		return i;
	}
	
	
}

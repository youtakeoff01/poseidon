package com.hand.bdss.web.report.approve.dao.impl;

import org.springframework.stereotype.Repository;

import com.hand.bdss.web.common.util.SupportDaoUtils;
import com.hand.bdss.web.entity.ApplyEntity;
import com.hand.bdss.web.entity.ApprovalEntity;
import com.hand.bdss.web.report.approve.dao.ApprovalDao;

@Repository
public class ApprovalDaoImpl extends SupportDaoUtils implements ApprovalDao {
	private static final String MAPPERURL = "com.hand.bdss.web.entity.ApprovalEntity.";
	@Override
	public ApprovalEntity getApprovalMsg(ApplyEntity apply) throws Exception{
		return getSqlSession().selectOne(MAPPERURL+"getApprovalMsg", apply);
	}
	@Override
	public void updateArrovalStatus(ApprovalEntity approvalEntity) throws Exception{
		getSqlSession().update(MAPPERURL+"updateArrovalStatus", approvalEntity);
	}
}

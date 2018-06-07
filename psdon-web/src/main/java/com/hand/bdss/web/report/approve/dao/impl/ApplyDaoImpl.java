package com.hand.bdss.web.report.approve.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hand.bdss.web.common.util.SupportDaoUtils;
import com.hand.bdss.web.entity.ApplyEntity;
import com.hand.bdss.web.entity.ApprovalEntity;
import com.hand.bdss.web.entity.StudentEntity;
import com.hand.bdss.web.entity.TupleEntity;
import com.hand.bdss.web.report.approve.dao.ApplyDao;
@Repository
public class ApplyDaoImpl extends SupportDaoUtils implements ApplyDao{
	private static final String MAPPERURL = "com.hand.bdss.web.entity.ApplyEntity.";
	@Override
	public List<StudentEntity> listStudentMsg(StudentEntity student) throws Exception {
		return getSqlSession().selectList(MAPPERURL+"listStudentMsg", student);
	}
	
	@Transactional(rollbackFor=Exception.class)
	@Override
	public ApplyEntity insertApplyMsg(ApplyEntity apply) throws Exception {
		//先将数据插入到 申请信息的主表中，并且返回插入的当前行自增的id
		getSqlSession().insert(MAPPERURL+"insertApplyMsg", apply);
		//插入申请信息与对应的学生信息的关系
		List<TupleEntity<Long,String>> lists = new ArrayList<TupleEntity<Long,String>>();
		for (StudentEntity student : apply.getStudents()) {
			TupleEntity<Long,String> tuple = new TupleEntity<Long,String>(apply.getId(),student.getId());
			lists.add(tuple);
		}
		getSqlSession().insert(MAPPERURL+"insertApplyStuMsg",lists);
		//将审批信息插入到数据库中,并且返回插入的自增id
		getSqlSession().insert(MAPPERURL+"insertApprovalMsg", apply.getApprovals());
		//插入申请信息和审批信息对应的关系
		List<TupleEntity<Long,Long>> list = new ArrayList<TupleEntity<Long,Long>>();
		for (ApprovalEntity approval : apply.getApprovals()) {
			TupleEntity<Long,Long> tuple = new TupleEntity<Long,Long>(apply.getId(),approval.getId());
			list.add(tuple);
		}
		getSqlSession().insert(MAPPERURL+"insertApplyApprovalMsg",list);
		return apply;
	}

	@Override
	public List<ApplyEntity> listApplyMsg(ApplyEntity apply) throws Exception{
		return getSqlSession().selectList(MAPPERURL+"listApplyMsg", apply);
	}

	@Override
	public void updateApplyStatus(ApplyEntity apply) throws Exception{
		getSqlSession().update(MAPPERURL+"updateApplyStatus", apply);
	}

	@Override
	public int getCountAll(ApplyEntity apply) throws Exception {
		return getSqlSession().selectOne(MAPPERURL+"getCountAll", apply);
	}
}

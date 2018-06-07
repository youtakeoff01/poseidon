package com.hand.bdss.web.report.approve.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.entity.ApplyEntity;
import com.hand.bdss.web.entity.StudentEntity;
import com.hand.bdss.web.entity.UserEntity;

public interface ApplyService {
	/**
	 * 查询所有具有审批权限的老师的信息
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	List<UserEntity> listTeacherMsgs(UserEntity user) throws Exception;

	/**
	 * 根据学生姓名查询学生信息
	 * 
	 * @param student
	 * @return
	 * @throws Exception
	 */
	List<StudentEntity> listStudentsMsg(StudentEntity student) throws Exception;

	/**
	 * 提交申请接口
	 * 
	 * @param apply
	 * @param servletContext 
	 * @throws Exception
	 */
	void submitApply(ApplyEntity apply, HttpServletRequest request) throws Exception;

	/**
	 * 查询申请信息
	 * 
	 * @param apply
	 * @return
	 * @throws Exception
	 */
	List<ApplyEntity> listApplyMsg(ApplyEntity apply, int startPage, int count) throws Exception;

	int getCountAll(ApplyEntity apply) throws Exception;

}

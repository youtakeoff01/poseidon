package com.hand.bdss.web.report.approve.dao;

import java.util.List;

import com.hand.bdss.web.entity.ApplyEntity;
import com.hand.bdss.web.entity.StudentEntity;

public interface ApplyDao {
    /**
     * 查询学生的信息
     * @param student
     * @return
     * @throws Exception
     */
	List<StudentEntity> listStudentMsg(StudentEntity student) throws Exception;
    /**
     * 插入申请信息
     * @param apply
     * @throws Exception
     */
	ApplyEntity insertApplyMsg(ApplyEntity apply) throws Exception;
	/**
	 * 查询申请信息
	 * @param apply
	 * @param startPage
	 * @param count
	 * @return
	 */
	List<ApplyEntity> listApplyMsg(ApplyEntity apply) throws Exception;
	/**
	 * 更新申请状态
	 * @param apply
	 */
	void updateApplyStatus(ApplyEntity apply) throws Exception;
	int getCountAll(ApplyEntity apply) throws Exception;
	

}

package com.hand.bdss.web.report.approve.dao;

import com.hand.bdss.web.entity.ApplyEntity;
import com.hand.bdss.web.entity.ApprovalEntity;

public interface ApprovalDao {
    /**
     * 获取审批信息
     * @param apply
     * @return
     */
	ApprovalEntity getApprovalMsg(ApplyEntity apply) throws Exception;
    /**
     * 更新审批状态
     * @param approvalEntity
     */
	void updateArrovalStatus(ApprovalEntity approvalEntity) throws Exception;

}

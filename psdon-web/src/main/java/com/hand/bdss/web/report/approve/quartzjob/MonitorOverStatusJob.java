package com.hand.bdss.web.report.approve.quartzjob;

import java.util.List;

import javax.servlet.ServletContext;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hand.bdss.web.common.constant.Global;
import com.hand.bdss.web.common.em.ApplyState;
import com.hand.bdss.web.common.util.SendEmailUtils;
import com.hand.bdss.web.common.util.SqlServerCRUDUtils;
import com.hand.bdss.web.entity.ApplyEntity;
import com.hand.bdss.web.entity.ApprovalEntity;
import com.hand.bdss.web.entity.EmailEntity;
import com.hand.bdss.web.entity.StudentEntity;
import com.hand.bdss.web.report.approve.dao.ApplyDao;
import com.hand.bdss.web.report.approve.dao.ApprovalDao;

/**
 * 监控申请信息是否已经过期 审批人审批通过后超过3天（72小时）则更新为“已过期”状态
 * 
 * @author liqifei
 * @DATA 2018年5月8日
 */
public class MonitorOverStatusJob implements Job {
	private static final Logger logger = LoggerFactory.getLogger(MonitorOverStatusJob.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			JobDataMap map = context.getJobDetail().getJobDataMap();// 获取参数信息
			ApplyEntity apply = (ApplyEntity) map.get("applyEntity");
			ServletContext servletContext = (ServletContext)map.get("servletContext");
			ApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			// 连接powerbi sqlserver 将申请老师的查看权限收回
			SqlServerCRUDUtils sqlServer = webApplicationContext.getBean("powerBiSqlServerCRUDUtils",
					SqlServerCRUDUtils.class);
			for (StudentEntity stu : apply.getStudents()) {
				sqlServer.updateJurisdictionTable(apply.getApperyer().getUserName(),stu.getSid());
			}
			// 更新申请表和审批表的 审批状态
			ApplyDao applyDao = webApplicationContext.getBean("applyDaoImpl", ApplyDao.class);
			ApprovalDao approvalDao = webApplicationContext.getBean("approvalDaoImpl", ApprovalDao.class);
			apply.setApplyStatus(ApplyState.overApproved.getIndex());// 申请状态改成已过期
			applyDao.updateApplyStatus(apply);
			List<ApprovalEntity> approvals = apply.getApprovals();
			if (approvals != null && approvals.size() > 0) {
				for (ApprovalEntity approvalEntity : approvals) {
					approvalEntity.setApprovalStatus(ApplyState.overApproved.getIndex());// 审批状态改成已过期
					approvalDao.updateArrovalStatus(approvalEntity);
				}
			}
			// 发送邮件给对应的申请人
			EmailEntity emailEntity = new EmailEntity();
			emailEntity.setSendServer(Global.EMAIL_SERVER);
			emailEntity.setSendAccount(Global.SEND_EMAIL_ACCOUNT);
			emailEntity.setEmailPassword(Global.SEND_EMAIL_PWD);
			emailEntity.setPort(Global.EMAIL_PORT);
			emailEntity.setMsgHeader("报表权限审批");
			emailEntity.setMsgTheme("报表权限审批提醒");
			emailEntity.setReceiveAcount(apply.getApperyer().getEmail());
			SendEmailUtils.sendEmailSMTP(emailEntity, Global.APPLY_OVER_CONTENT);
		} catch (Exception e) {
			logger.error("MonitorOverStatusJob is error error msg is:{}",e);
		}
	}
}

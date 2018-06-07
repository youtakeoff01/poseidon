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
import com.hand.bdss.web.entity.ApplyEntity;
import com.hand.bdss.web.entity.ApprovalEntity;
import com.hand.bdss.web.entity.EmailEntity;
import com.hand.bdss.web.report.approve.dao.ApplyDao;
import com.hand.bdss.web.report.approve.dao.ApprovalDao;
/**
 * 监控申请信息是否失效的job
 * 申请人提交申请超过24小时未审批通过，则更新为“已失效”状态
 * @author liqifei
 * @DATA 2018年4月25日
 */
public class MonitorInvalidStatusJob implements Job{
	private static final Logger logger = LoggerFactory.getLogger(MonitorInvalidStatusJob.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException{
		try {
			JobDataMap map = context.getJobDetail().getJobDataMap();//获取参数信息
			ServletContext servletContext = (ServletContext)map.get("servletContext");
			ApplyEntity apply = (ApplyEntity)map.get("applyEntity");
			ApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			//查询申请表的对应的申请状态
			ApplyDao applyDao = webApplicationContext.getBean("applyDaoImpl",ApplyDao.class);
			List<ApplyEntity> applys = applyDao.listApplyMsg(apply);
			if(applys!=null && applys.size()>0) {
				ApplyEntity applyEntity = applys.get(0);
				//如果还是处于未审批或者审批中，则将状态改成"已失效"状态
				if(ApplyState.toApproved.getIndex() == applyEntity.getApplyStatus() || ApplyState.inApproved.getIndex() == applyEntity.getApplyStatus()) {
					//更改申请状态为已失效
					apply.setApplyStatus(ApplyState.invalidApproved.getIndex());
					applyDao.updateApplyStatus(apply);
					//更改审批列表的审批状态为 已失效
					ApprovalDao approvalDao = webApplicationContext.getBean("approvalDaoImpl", ApprovalDao.class);
					List<ApprovalEntity> approvals = apply.getApprovals();
					if (approvals != null && approvals.size() > 0) {
						for (ApprovalEntity approvalEntity : approvals) {
							approvalEntity.setApprovalStatus(ApplyState.invalidApproved.getIndex());// 审批状态改成已过期
							approvalDao.updateArrovalStatus(approvalEntity);
						}
					}
					//发送邮件给对应的申请人
					EmailEntity emailEntity = new EmailEntity();
					emailEntity.setSendServer(Global.EMAIL_SERVER);
					emailEntity.setSendAccount(Global.SEND_EMAIL_ACCOUNT);
					emailEntity.setEmailPassword(Global.SEND_EMAIL_PWD);
					emailEntity.setPort(Global.EMAIL_PORT);
					emailEntity.setMsgHeader("报表权限审批");
					emailEntity.setMsgTheme("报表权限审批提醒");
					emailEntity.setReceiveAcount(apply.getApperyer().getEmail());
					SendEmailUtils.sendEmailSMTP(emailEntity, Global.APPLY_INVALID_CONTENT);
				}
			}
		} catch (Exception e) {
			logger.error("MonitorInvalidStatusJob is error,error msg is:{}",e);
		}
	}

}

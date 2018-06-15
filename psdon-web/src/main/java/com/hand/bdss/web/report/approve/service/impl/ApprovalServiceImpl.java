package com.hand.bdss.web.report.approve.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.hand.bdss.web.common.constant.Global;
import com.hand.bdss.web.common.em.ApplyState;
import com.hand.bdss.web.common.util.DateUtil;
import com.hand.bdss.web.common.util.QuartzManager;
import com.hand.bdss.web.common.util.QuartzTimeUtils;
import com.hand.bdss.web.common.util.SendEmailUtils;
import com.hand.bdss.web.common.util.SqlServerCRUDUtils;
import com.hand.bdss.web.entity.ApplyEntity;
import com.hand.bdss.web.entity.ApprovalEntity;
import com.hand.bdss.web.entity.EmailEntity;
import com.hand.bdss.web.entity.StudentEntity;
import com.hand.bdss.web.report.approve.dao.ApplyDao;
import com.hand.bdss.web.report.approve.dao.ApprovalDao;
import com.hand.bdss.web.report.approve.quartzjob.MonitorOverStatusJob;
import com.hand.bdss.web.report.approve.service.ApprovalService;

import jersey.repackaged.com.google.common.collect.Lists;

@Service
public class ApprovalServiceImpl implements ApprovalService {

	@Resource
	private ApprovalDao approvalDaoImpl;
	@Resource
	private ApplyDao applyDaoImpl;
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;
	
	@Resource(name="powerBiSqlServerCRUDUtils")
	private SqlServerCRUDUtils powerBiSqlServerCRUDUtils;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateApprovalStatus(ApplyEntity apply,HttpServletRequest request) throws Exception{
		// 将审批状态更新到数据库中
		ApprovalEntity nowApproval = apply.getApprovals().get(0);
		nowApproval.setApprovalTime(DateUtil.date2String(null, "yyyy-MM-dd HH:mm:ss"));//审批时间
		approvalDaoImpl.updateArrovalStatus(nowApproval);
		// 查询另外一个审批人的审批状态
		ApprovalEntity otherApproval = approvalDaoImpl.getApprovalMsg(apply);
		
		EmailEntity emailEntity = new EmailEntity();
		emailEntity.setSendServer(Global.EMAIL_SERVER);
		emailEntity.setSendAccount(Global.SEND_EMAIL_ACCOUNT);
		emailEntity.setEmailPassword(Global.SEND_EMAIL_PWD);
		emailEntity.setPort(Global.EMAIL_PORT);
		emailEntity.setMsgHeader("报表权限审批");
		emailEntity.setMsgTheme("报表权限审批提醒");
		/* 更新申请表中的申请状态 */
		// 如果当前审批人点的是拒绝，那么直接将申请状态更新为拒绝
		if (ApplyState.disagreeApproved.getIndex() == nowApproval.getApprovalStatus()) {
			apply.setApplyStatus(ApplyState.disagreeApproved.getIndex());
			applyDaoImpl.updateApplyStatus(apply);
		}
		// 如果当前审批人是同意，另一个审批人是待审批，则申请状态改成审批中
		if (ApplyState.agreeApproved.getIndex() == nowApproval.getApprovalStatus()
				&& ApplyState.toApproved.getIndex() == otherApproval.getApprovalStatus()) {
			apply.setApplyStatus(ApplyState.inApproved.getIndex());
			applyDaoImpl.updateApplyStatus(apply);
		}
		// 首先判断当前审批人的审批状态，如果当前审批人的审批状态是同意，另一个审批人也是同意
		if (ApplyState.agreeApproved.getIndex() == nowApproval.getApprovalStatus()
				&& ApplyState.agreeApproved.getIndex() == otherApproval.getApprovalStatus()) {
			// 更新申请状态为同意
			apply.setApplyStatus(ApplyState.agreeApproved.getIndex());
			applyDaoImpl.updateApplyStatus(apply);
			//和powerbi进行交互,添加申請人訪問對應學生數據的權限
			List<Map<String,String>> list = Lists.newArrayList();
			for (StudentEntity student : apply.getStudents()) {
				Map<String,String> map = Maps.newHashMap();
				map.put("teaAD", apply.getApperyer().getUserName());//申請老師的AD
				map.put("stuID", student.getSid());//學生學號
				list.add(map);
			}
			powerBiSqlServerCRUDUtils.insertJurisdictionTable(list);
			//发邮件通知申请人
			emailEntity.setReceiveAcount(apply.getApperyer().getEmail());
			SendEmailUtils.sendEmailSMTP(emailEntity, Global.APPROVAL_ADOPT_CONTENT);
			//设置定时器
			Map<String,Object> map = new HashMap<String,Object>();//设置定时器参数
			List<ApprovalEntity> lists = Lists.newArrayList(nowApproval,otherApproval);
			apply.setApprovals(lists);
			map.put("applyEntity", apply);
			map.put("servletContext", request.getSession().getServletContext());
			String cron = QuartzTimeUtils.getQuartzTime(apply.getVld()*24);
			QuartzManager.addJob(schedulerFactoryBean.getScheduler(), "monitorOverStatusJob$"+UUID.randomUUID().toString(), map, MonitorOverStatusJob.class, cron);
			
		}
		// 如果当前审批人的审批状态是已拒绝，另外一个审批人的审批状态不是拒绝状态，则邮件通知申请人
		if (ApplyState.disagreeApproved.getIndex() == nowApproval.getApprovalStatus()
				&& ApplyState.disagreeApproved.getIndex() != otherApproval.getApprovalStatus()) {
			//发邮件通知申请人
			emailEntity.setReceiveAcount(apply.getApperyer().getEmail());
			SendEmailUtils.sendEmailSMTP(emailEntity, Global.APPROVAL_REFUSE_CONTENT);
		}
	}
}

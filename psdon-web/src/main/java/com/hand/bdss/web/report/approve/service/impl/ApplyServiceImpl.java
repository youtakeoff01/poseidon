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

import com.hand.bdss.web.common.constant.Global;
import com.hand.bdss.web.common.em.ApplyState;
import com.hand.bdss.web.common.util.DateUtil;
import com.hand.bdss.web.common.util.QuartzManager;
import com.hand.bdss.web.common.util.QuartzTimeUtils;
import com.hand.bdss.web.common.util.SendEmailUtils;
import com.hand.bdss.web.entity.ApplyEntity;
import com.hand.bdss.web.entity.ApprovalEntity;
import com.hand.bdss.web.entity.EmailEntity;
import com.hand.bdss.web.entity.StudentEntity;
import com.hand.bdss.web.entity.UserEntity;
import com.hand.bdss.web.platform.user.dao.UserDao;
import com.hand.bdss.web.report.approve.dao.ApplyDao;
import com.hand.bdss.web.report.approve.quartzjob.MonitorInvalidStatusJob;
import com.hand.bdss.web.report.approve.service.ApplyService;

@Service
public class ApplyServiceImpl implements ApplyService {

	private static final String ROLENAME_01 = "审批人";
	private static final String ROLENAME_02 = "申请人且审批人";
	

	@Resource
	private UserDao userDaoImpl;

	@Resource
	private ApplyDao applyDaoImpl;
	
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Override
	public List<UserEntity> listTeacherMsgs(UserEntity user) throws Exception {
		user.setRoleName(ROLENAME_01);
		List<UserEntity> lists01 = userDaoImpl.listTeacherMsgs(user);
		user.setRoleName(ROLENAME_02);
		List<UserEntity> lists02 = userDaoImpl.listTeacherMsgs(user);
		if (lists01 != null) {
			lists01.addAll(lists02);
			return lists01;
		} else {
			return lists02;
		}
	}

	@Override
	public List<StudentEntity> listStudentsMsg(StudentEntity student) throws Exception {
		return applyDaoImpl.listStudentMsg(student);
	}

	@Override
	public void submitApply(ApplyEntity apply, HttpServletRequest request) throws Exception {
		// 判断审批人中是否包含自己，如果包含则将自己的审批状态改为已审批，并不会发送邮件通知
		List<ApprovalEntity> approvals = apply.getApprovals();// 获取审批信息
		UserEntity loginUser = (UserEntity) request.getSession().getAttribute("userMsg");
		apply.setApperyer(loginUser);// 申请人信息
		apply.setCreateUserId(new Long(loginUser.getId()));
		apply.setUpdateUserId(new Long(loginUser.getId()));
		EmailEntity emailEntity = new EmailEntity();
		emailEntity.setSendServer(Global.EMAIL_SERVER);
		emailEntity.setSendAccount(Global.SEND_EMAIL_ACCOUNT);
		emailEntity.setEmailPassword(Global.SEND_EMAIL_PWD);
		emailEntity.setPort(Global.EMAIL_PORT);
		emailEntity.setMsgHeader("报表权限审批");
		emailEntity.setMsgTheme("报表权限审批提醒");
		for (ApprovalEntity approvalEntity : approvals) {
			if (approvalEntity.getApprovaler().getUserName().equals(loginUser.getUserName())) {
				approvalEntity.setApprovalStatus(ApplyState.agreeApproved.getIndex());// 直接改成已审批
				approvalEntity.setApprovalTime(DateUtil.date2String(null,"yyyy-MM-dd HH:mm:ss"));
				apply.setApplyStatus(ApplyState.inApproved.getIndex());//更改申请表中的审批状态为审批中
			} else {
				approvalEntity.setApprovalStatus(ApplyState.toApproved.getIndex());// 待审批
				apply.setApplyStatus(ApplyState.toApproved.getIndex());//更改申请表中的审批状态为待审批
				//发送邮件给对应的审批人
				emailEntity.setReceiveAcount(approvalEntity.getApprovaler().getEmail());
				SendEmailUtils.sendEmailSMTP(emailEntity, Global.APPROVAL_NOTICE_CONTENT);
			}
		}
		//将数据插入到数据库中
		apply = applyDaoImpl.insertApplyMsg(apply);
		// 启动定时器 ,24小时之后如果这条申请还是在审批状态,则直接将状态改成"已失效"
		Map<String,Object> map = new HashMap<String,Object>();//设置定时器参数
		map.put("applyEntity", apply);
		map.put("servletContext", request.getSession().getServletContext());
		//获取当前时间的后24小时时间并生成定时time
		String cron = QuartzTimeUtils.getQuartzTime(24);
		QuartzManager.addJob(schedulerFactoryBean.getScheduler(), "monitorInvalidStatusJob$"+UUID.randomUUID().toString(), map, MonitorInvalidStatusJob.class, cron);
	}
    /**
     * 由于mybatis不支持resultMap 里面映射collection 进行分页,所以在代码层进行了分页
     */
	@Override
	public List<ApplyEntity> listApplyMsg(ApplyEntity apply, int startPage, int count) throws Exception {
		List<ApplyEntity> applyEntitys = applyDaoImpl.listApplyMsg(apply);
        applyEntitys.sort((app1,app2)->{if(app1.getId()>app2.getId()) {
        	return 1;
        }else{
        	return 0;
        }});
        int toIndex = (count+startPage)>=applyEntitys.size()?applyEntitys.size():count+startPage;
		return applyEntitys.subList(startPage, toIndex);
	}

	@Override
	public int getCountAll(ApplyEntity apply) throws Exception {
		return applyDaoImpl.getCountAll(apply);
	}
}

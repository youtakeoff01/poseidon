package com.hand.bdss.web.common.util;

import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

/**
 * 定时器控制类
 * 
 * @author liqifei
 * @DATA 2018年5月8日
 */
public class QuartzManager {
	private static String JOB_GROUP_NAME = "EXTJWEB_JOBGROUP_NAME";
	/*
	 * public static void addJob(Scheduler sched, String jobName,Map<String,Object>
	 * map, @SuppressWarnings("rawtypes") Class cls, String time) { try { JobDetail
	 * jobDetail = new JobDetail(jobName, JOB_GROUP_NAME, cls);// 任务名，任务组，任务执行类
	 * jobDetail.getJobDataMap().putAll(map);//向job中传递参数 // 触发器 CronTrigger trigger
	 * = new CronTrigger(jobName, TRIGGER_GROUP_NAME);// 触发器名,触发器组
	 * trigger.setCronExpression(time);// 触发器时间设定 sched.scheduleJob(jobDetail,
	 * trigger); // 启动 if (!sched.isShutdown()) { sched.start(); } } catch
	 * (Exception e) { throw new RuntimeException(e); } }
	 */
	/**
	 * @Description: 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
	 * 
	 * @param scheduler
	 *            调度器
	 * 
	 * @param jobName
	 *            任务名
	 * @param cls
	 *            任务
	 * @param cron
	 *            时间设置，参考quartz说明文档
	 * 
	 * @Title: QuartzManager.java
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addJob(Scheduler scheduler, String jobName, Map<String, Object> map,
			 Class cls, String cron) throws SchedulerException {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, JOB_GROUP_NAME);
		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(jobName, JOB_GROUP_NAME).build();
		jobDetail.getJobDataMap().putAll(map);// 向job中传递参数
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);// 触发器时间设定
		trigger = TriggerBuilder.newTrigger().withDescription(jobName).withIdentity(jobName, JOB_GROUP_NAME)
				.withSchedule(scheduleBuilder).build();
		scheduler.scheduleJob(jobDetail, trigger);
		//启动
		if(scheduler.isShutdown()) {
			scheduler.start();
		}
	}

}

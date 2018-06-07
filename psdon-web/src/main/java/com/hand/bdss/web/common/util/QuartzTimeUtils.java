package com.hand.bdss.web.common.util;

import java.time.LocalDateTime;

public class QuartzTimeUtils {
	/**
	 * 生成当前时间后hours小时 定时器的cron表达式
	 * @param hours
	 * @return
	 */
	public static String getQuartzTime(int hours) {
		LocalDateTime ldt = LocalDateTime.now();
		ldt = ldt.plusHours(hours);
//		ldt = ldt.plusMinutes(hours);
		Integer year = ldt.getYear();
		Integer month = ldt.getMonthValue();
		Integer day = ldt.getDayOfMonth();
		Integer hour = ldt.getHour();
		Integer minute = ldt.getMinute();
		return String.join(" ", "0",minute.toString(),hour.toString(),day.toString(),month.toString(),"?",year.toString());
	}
}

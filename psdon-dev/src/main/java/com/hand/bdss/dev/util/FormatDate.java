package com.hand.bdss.dev.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author : Koala
 * @version : v1.0
 * @description :
 */
public class FormatDate {
    /**
     * 格式化时间 yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public String formateDate2String(Date date) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(date);
        return dateString;
    }

    /**
     * 格式化日期yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public String formateYaryMonthDay(Date date) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(date);
        return dateString;
    }

    /**
     * 获取几天前凌晨零点零分
     *
     * @return
     */
    public String getFewDaysBefore(int n) {
        Date date = new Date(new Date().getTime() - 24 * 60 * 60 * 1000 * n);//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        String dateString = formateDate2String(calendar.getTime());
        return dateString;
    }
}

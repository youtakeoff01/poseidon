package com.hand.bdss.web.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /**
     * yyyyMMdd
     */
    public static final String FORMAT_YYYYMMDD = "yyyyMMdd";

    /**
     * yyyy-MM-dd
     */
    public static final String dateFormat111 = "yyyy-MM-dd";

    /**
     * yyyy/MM/dd
     */
    public static final String dateFormat112 = "yyyy/MM/dd";

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final String dateFormat210 = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取当天日期字符串
     *
     * @return
     */
    public static String getTodayStrDate(String format) {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(dt);
    }

    /**
     * 获取当天日期
     *
     * @return
     */
    public static Date getTodayDate(String format) {
        Date dt = new Date();
        return dt;
    }

    /**
     * 字符串转换为指定格式的日期类型
     *
     * @param dateStr 原始日期字
     * @param rex     新日期格试,如:yyyyMMdd
     * @return
     * @throws ParseException
     */
    public static Date string2date(String dateStr, String rex) throws ParseException {
        DateFormat df = new SimpleDateFormat(rex);
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        } else {
            Date date = df.parse(dateStr);
            return date;
        }
    }

    /**
     * 日期转换为指定格式的字符串
     *
     * @param date
     * @param rex
     * @return
     */
    public static String date2String(Date date, String rex) {
        if (date == null) {
            date = new Date();
        }
        SimpleDateFormat sdf = new SimpleDateFormat(rex);
        return sdf.format(date);
    }

    /*
    * 将时间戳转换为时间
    */
    public static String stampToDate(Date date, String rex) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(rex);

        return simpleDateFormat.format(new Date(date.getTime()));
    }

    /*
    * 将时间戳转换为时间dateFormat210
    */
    public static Date formtDate(Date date) {
        String dt = stampToDate(date, dateFormat210);
        try {
            return string2date(dt, dateFormat210);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    
    /**
     * 获取执行时间
     * @param startTime
     * @param endTime
     * @return
     */
    public static String praseExcTime(String startTime, String endTime) {
        String retTime = "";
        try {
            long nd = 1000 * 24 * 60 * 60;
            long nh = 1000 * 60 * 60;
            long nm = 1000 * 60;
            long ns = 1000;
            Long subTime = Long.valueOf(endTime) - Long.valueOf(startTime);
            // 计算差多少天
            long day = subTime / nd;
            if (day > 0) {
                retTime = day + "天";
            }
            // 计算差多少小时
            long hour = subTime / nh % 24;
            if (hour > 0) {
                retTime = retTime + hour + "h";
            }
            // 计算差多少分钟
            long min = subTime / nm % 60;
            if (min > 0) {
                retTime = retTime + min + "min";
            }
            // 计算差多少秒
            long ss = subTime / ns % 60;
            if (ss > 0) {
                retTime = retTime + ss + "s";
            }
            /*long mss = subTime % 60;
            if (mss > 0) {
                retTime = retTime + mss + "msec";
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retTime;
    }
    
    public static String praseExcTime(long subTime) {
        String retTime = "";
        try {
            long nd = 1000 * 24 * 60 * 60;
            long nh = 1000 * 60 * 60;
            long nm = 1000 * 60;
            long ns = 1000;
            // 计算差多少天
            long day = subTime / nd;
            if (day > 0) {
                retTime = day + "天";
            }
            // 计算差多少小时
            long hour = subTime / nh % 24;
            if (hour > 0) {
                retTime = retTime + hour + "h";
            }
            // 计算差多少分钟
            long min = subTime / nm % 60;
            if (min > 0) {
                retTime = retTime + min + "min";
            }
            // 计算差多少秒
            long ss = subTime / ns % 60;
            if (ss > 0) {
                retTime = retTime + ss + "s";
            }
            //计算差多少毫秒
            if(min == 0){
            	long mss = subTime % 60;
            	if (mss > 0) {
                 retTime = retTime + mss + "ms";
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retTime;
    }
    
    public static void main(String[] args) {
    	System.out.println(praseExcTime(16446));
	}

}

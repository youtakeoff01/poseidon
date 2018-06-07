package com.hand.bdss.dev.vo;

/**
 * Created by hand on 2017/9/19.
 */
public class TaskScheduleInfo extends Task{

    /**
	 * 
	 */
	private static final long serialVersionUID = 8188645460561913148L;

	private String emailStr;//邮件下发列表逗号隔开

    private String forecastTime="10:00";//预测时间

    public String getEmailStr() {
        return emailStr;
    }

    public void setEmailStr(String emailStr) {
        this.emailStr = emailStr;
    }

    public String getForecastTime() {
        return forecastTime;
    }

    public void setForecastTime(String forecastTime) {
        this.forecastTime = forecastTime;
    }
}

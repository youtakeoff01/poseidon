package com.hand.bdss.web.common.util.request;

import com.hand.bdss.web.common.util.DateUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by hand on 2017/8/31.
 */
public class RequestThreadHead {

    /**
     * 获取当前请求时间
     * @return
     */
    private static String getTimmer() {
        return DateUtil.getTodayStrDate(DateUtil.dateFormat210);
    }

    /**
     * 获取用户IP地址
     *
     * @param
     * @return
     */
    private static String getRemoteHost(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }

    public static String getLogStr(HttpServletRequest request) {
        return " timmer=" + getTimmer() + " ip=" + getRemoteHost(request) + " ";
    }
}

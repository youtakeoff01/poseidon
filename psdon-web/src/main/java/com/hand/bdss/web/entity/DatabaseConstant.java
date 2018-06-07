package com.hand.bdss.web.entity;

import com.hand.bdss.dsmp.config.SystemConfig;

/**
 * 数据库连接的常量类
 * @author Administrator
 *
 */
public class DatabaseConstant {
	
	public static final String DBNAME = SystemConfig.DBNAME;//数据库名称
    public static final String DBURL = SystemConfig.DBURL;//数据库的地址
    public static final String DBUSERNAME = SystemConfig.DBUSERNAME;//数据库连接的用户名
    public static final String DBPWD = SystemConfig.DBPWD;//数据库连接的密码
    public static final String DBTYPE = SystemConfig.DBTYPE;//数据库类型
}

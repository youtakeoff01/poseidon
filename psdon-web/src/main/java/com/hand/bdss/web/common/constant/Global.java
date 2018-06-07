package com.hand.bdss.web.common.constant;

import java.util.Properties;

import com.hand.bdss.web.common.util.PropertiesOperationUtils;

/**
 * 全局常量
 * 
 * @author zk
 */
public class Global {

	// hive服务名
	public static final String HIVE_SERVER = "antx_cluster_hive";
	// 平台管理员账号
	public static final String MANAGER = "admin";

	public static final String ADMIN_ROLE_NAME = "系统管理员";
	// 管理员角色
	public static final String MANAGER_ROLE = "1,2";
	// hdfs管理员
	public static final String HDFS_MANAGER = "hdfs";
	// 用户类型
	public static final String USER_TYPE_LDAP = "ldap";
	public static final String USER_TYPE_PSDON = "psdon";
	
	// 有上传权限的用户
	public static  String MENTALHEALTH;// 心理咨询
	public static  String ACCOMMODATION;// 宿舍管理
	public static  String SDAADMIN;// sao,学生事务办公室
	public static  String ONESTOP;// 一站式用户
	public static  String OTHERUPLOAD;// 其他上传

	// 发送邮件参数
	public static String EMAIL_SERVER;
	public static String EMAIL_PORT;
	public static String SEND_EMAIL_ACCOUNT;
	public static String SEND_EMAIL_PWD;

	// 邮件发送内容
	public static String APPROVAL_NOTICE_CONTENT; // 审批通知邮件内容
	public static String APPROVAL_ADOPT_CONTENT; // 审批通过邮件内容
	public static String APPROVAL_REFUSE_CONTENT;// 审批拒绝邮件内容
	public static String APPLY_INVALID_CONTENT;// 申请失效邮件内容
	public static String APPLY_OVER_CONTENT;// 申请过期邮件内容

	static {
		try {
			Properties per = new PropertiesOperationUtils().loadData("global.properties","UTF-8");
			EMAIL_SERVER = per.getProperty("EMAIL_SERVER");
			EMAIL_PORT = per.getProperty("EMAIL_PORT");
			SEND_EMAIL_ACCOUNT = per.getProperty("SEND_EMAIL_ACCOUNT");
			SEND_EMAIL_PWD = per.getProperty("SEND_EMAIL_PWD");
			
			APPROVAL_NOTICE_CONTENT = per.getProperty("APPROVAL_NOTICE_CONTENT");
			APPROVAL_ADOPT_CONTENT = per.getProperty("APPROVAL_ADOPT_CONTENT");
			APPROVAL_REFUSE_CONTENT = per.getProperty("APPROVAL_REFUSE_CONTENT");
			APPLY_INVALID_CONTENT = per.getProperty("APPLY_INVALID_CONTENT");
			APPLY_OVER_CONTENT = per.getProperty("APPLY_OVER_CONTENT");
			
			MENTALHEALTH = per.getProperty("MENTALHEALTH");
			ACCOMMODATION = per.getProperty("ACCOMMODATION");
			SDAADMIN = per.getProperty("SDAADMIN");
			ONESTOP = per.getProperty("ONESTOP");
			OTHERUPLOAD = per.getProperty("OTHERUPLOAD");
		} catch (Exception e) {
		}
	}
}

package com.hand.bdss.test;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class ldapTest {

	// 要查询的根目录
	private static String rootDN = "ou=staff,ou=xjtlu,dc=xjtlu,dc=edu,dc=cn";
	// 连接Ldap需要的信息
	private static String ldapFactory = "com.sun.jndi.ldap.LdapCtxFactory";
	private static String ldapUrl = "LDAP://dc01.xjtlu.edu.cn:389/";// url
	private static String domain = "dc=xjtlu,dc=edu,dc=cn";
//	private static String user = "cn=sp-admintest,ou=staff,ou=xjtlu,dc=xjtlu,dc=edu,dc=cn";
	private static String user = "qinggao.qin@xjtlu.edu.cn";
	private static String ldapPwd = "CelesTica$0304";// 密码

	public static void main(String[] args) throws Exception {
//		LdapContext lct = connetLDAP();
//		System.out.println(lct.getAttributes("responsemessage"));
//		System.out.println(lct.getAttributes("responsecode"));
//		System.out.println(lct);
		StringBuffer str = new StringBuffer();
		str.append("4、");
		str.append("5、");
		str.insert(0, "行号为：");
		str.append("的学生学号和学生的AD账号不匹配，请确认无误之后再上传。");
		System.out.println(str.toString());
		
		// UserEntity user = new UserEntity();
		// user.setUserName("test01");
		// user.setPassword("1234556");
		// UserEntity userEntity = LDAPSupportUtils.authenricate(user);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static LdapContext connetLDAP() throws NamingException {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);
		env.put(Context.PROVIDER_URL, ldapUrl + domain);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, user);
		env.put(Context.SECURITY_CREDENTIALS, ldapPwd);
		
		
		env.put("java.naming.batchsize", "50");
		env.put("com.sun.jndi.ldap.connect.timeout", "3000");
		env.put("com.sun.jndi.ldap.connect.pool", "true");

		// env.put("java.naming.referral", "follow");
		// env.put("java.naming.ldap.attributes.binary", "objectSid objectGUID");
		LdapContext ctxTDS = new InitialLdapContext(env, null);
		return ctxTDS;
	}

}

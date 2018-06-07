package com.hand.bdss.web.common.util;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hand.bdss.web.entity.UserEntity;

/**
 * 集成dsmp系统对ldap用户认证的支持
 * 
 * @author 13696
 *
 */
public class LDAPSupportUtils {
	// 要查询的根目录
	private static String rootDN;
	// 连接Ldap需要的信息
	private static String ldapFactory;
	private static String ldapUrl;// url
	private static final Logger logger = LoggerFactory.getLogger(LDAPSupportUtils.class);
	static {
		init();
	}

	/**
	 * 获取ldap的context来连接ldap
	 * 
	 * @return
	 * @throws NamingException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static LdapContext connetLDAP(String userName, String password) throws NamingException {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);
		// LDAP server
		env.put(Context.PROVIDER_URL, ldapUrl + rootDN);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, userName+"@xjtlu.edu.cn");
		env.put(Context.SECURITY_CREDENTIALS, password);
		LdapContext ctxTDS = new InitialLdapContext(env, null);
		return ctxTDS;
	}

	/**
	 * ldap 用户认证
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static boolean authenricate(UserEntity user) throws Exception {
		boolean boo = false;
		if (user == null) {
			return boo;
		}
		LdapContext ctx = null;
		try {
			ctx = connetLDAP(user.getUserName(), user.getPassword());
			boo = true;
		} catch (AuthenticationException e1) {
			logger.info("ldap用户认证失败，用户名或密码错误，错误信息为：", e1);
		} catch (CommunicationException e2) {
			logger.error("ldap连接异常，异常信息为：", e2);
		} catch (Exception e3) {
			logger.error("ldap用户认证未知错误，错误信息为：", e3);
		} finally {
			close(ctx);
		}
		return boo;
	}

	public static void init() {
		try {
			Properties per = new PropertiesOperationUtils().loadData("ldapconnect.properties","UTF-8");
			rootDN = per.getProperty("rootDN");
			ldapUrl = per.getProperty("ldapUrl");
			ldapFactory = per.getProperty("ldapFactory");
		} catch (Exception e) {
			logger.error("init LDAPsupportUtils error,error message:", e);
		}
	}

	public static void close(LdapContext ctx) {
		if (ctx != null) {
			try {
				ctx.close();
			} catch (NamingException e) {
				logger.error("close ldapContext error,error message:", e);
			}
		}
	}

	/**
	 * 用户ldap认证
	 * 
	 * @param uid
	 * @param password
	 * @return
	 */
	/*
	 * public static boolean authenricate(String uid, String password) throws
	 * Exception { LdapContext ctx = connetLDAP(); boolean boo = false;
	 * SearchControls constraints = new SearchControls();
	 * constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
	 * NamingEnumeration<SearchResult> en = ctx.search(rootDN, "uid=" + uid,
	 * constraints); if (en == null || !en.hasMoreElements()) { boo = false;
	 * System.out.println("未找到该用户"); } // maybe more than one element while (en !=
	 * null && en.hasMoreElements()) { Object obj = en.nextElement(); if (obj
	 * instanceof SearchResult) { SearchResult si = (SearchResult) obj; Attributes
	 * attrs = si.getAttributes(); Attribute attr = attrs.get("userPassword");
	 * Object o = attr.get(); byte[] s = (byte[]) o; String pwd2 = new String(s); if
	 * (password.equals(pwd2)) { boo = true; System.out.println("登录成功！"); return
	 * boo; } else { boo = false; System.out.println("登录失败！"); } } else { boo =
	 * false; } } return boo; }
	 */

	/**
	 * ldap 用户认证
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	/*
	 * public static UserEntity authenricate(UserEntity user) throws Exception {
	 * UserEntity ldapUser = null; if(user==null){ return ldapUser; } LdapContext
	 * ctx = null; try { ctx = connetLDAP(); } catch (Exception e) {
	 * logger.error("ldap用户认证失败，错误信息为：",e); }finally { close(ctx); } SearchControls
	 * constraints = new SearchControls();
	 * constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
	 * NamingEnumeration<SearchResult> en = ctx.search(rootDN, "uid=" +
	 * user.getUserName(), constraints); if (en == null || !en.hasMoreElements()) {
	 * return ldapUser; } // maybe more than one element while (en != null &&
	 * en.hasMoreElements()) { Object obj = en.nextElement(); if (obj instanceof
	 * SearchResult) { SearchResult si = (SearchResult) obj; Attributes attrs =
	 * si.getAttributes(); Attribute attr = attrs.get("userPassword"); Object o =
	 * attr.get(); byte[] s = (byte[]) o; String pwd2 = new String(s); if
	 * (user.getPassword().equals(pwd2)) { Attribute realName = attrs.get("cn");
	 * if(realName!=null){ ldapUser = new UserEntity();
	 * ldapUser.setRealName(realName.get().toString()); } } } } return ldapUser; }
	 */

	/**
	 * 如果用户的密码是通过加密的，则需要先进行解密，此类就是用于密码的解密的
	 * 
	 * @param ldappw
	 * @param inputpw
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	/*
	 * public boolean verifySHA(String ldappw, String inputpw) throws
	 * NoSuchAlgorithmException {
	 * 
	 * // MessageDigest 提供了消息摘要算法，如 MD5 或 SHA，的功能，这里LDAP使用的是SHA-1 MessageDigest md =
	 * MessageDigest.getInstance("SHA-1");
	 * 
	 * // 取出加密字符 if (ldappw.startsWith("{SSHA}")) { ldappw = ldappw.substring(6); }
	 * else if (ldappw.startsWith("{SHA}")) { ldappw = ldappw.substring(5); }
	 * 
	 * // 解码BASE64 byte[] ldappwbyte = Base64.decode(ldappw); byte[] shacode; byte[]
	 * salt;
	 * 
	 * // 前20位是SHA-1加密段，20位后是最初加密时的随机明文 if (ldappwbyte.length <= 20) { shacode =
	 * ldappwbyte; salt = new byte[0]; } else { shacode = new byte[20]; salt = new
	 * byte[ldappwbyte.length - 20]; System.arraycopy(ldappwbyte, 0, shacode, 0,
	 * 20); System.arraycopy(ldappwbyte, 20, salt, 0, salt.length); }
	 * 
	 * // 把用户输入的密码添加到摘要计算信息 md.update(inputpw.getBytes()); // 把随机明文添加到摘要计算信息
	 * md.update(salt);
	 * 
	 * // 按SSHA把当前用户密码进行计算 byte[] inputpwbyte = md.digest();
	 * 
	 * // 返回校验结果 return MessageDigest.isEqual(shacode, inputpwbyte); }
	 */
}

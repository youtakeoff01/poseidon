package com.hand.bdss.web.common.util;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 * Ldap	验证工具类(Poseidon2.0)
 * @author wangyong
 * @Date 2018-01-17
 */
public class LdapUtil {

	private static String ROOT_CN = null;// 操作LDAP的帐户。默认就是Manager,修改为root。
    private static String ROOT_PWD = null;// 帐户root的密码。
    private static String DC_ROOT = null; // LDAP的根节点的DC
    private static String CONTEXT_FACTORY = null;// 帐户root的密码。
    private static String PROVIDER_URL = null;
    private static String SECURITY_AUTH = null;
    private static String SECURITY_PRINCIPAL = null;
    private static String SECURITY_PRINCIPAL_UID = null;
    private static final Control[] connCtls = null;

    static{
		try {
			Properties property = new PropertiesOperationUtils().loadData("ldapconnect.properties","UTF-8");
			ROOT_CN = property.getProperty("ROOT_CN");
			ROOT_PWD = property.getProperty("ROOT_PWD");
			DC_ROOT = property.getProperty("DC_ROOT");
			CONTEXT_FACTORY = property.getProperty("CONTEXT_FACTORY");
			PROVIDER_URL = property.getProperty("PROVIDER_URL");
			SECURITY_AUTH = property.getProperty("SECURITY_AUTH");
			SECURITY_PRINCIPAL = property.getProperty("SECURITY_PRINCIPAL") + ROOT_CN + "," + DC_ROOT;
			SECURITY_PRINCIPAL_UID = property.getProperty("SECURITY_PRINCIPAL_UID") + DC_ROOT;
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 初始化ROOT DN
     */
    public static LdapContext initCtx() {

        LdapContext ctx = null;
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, PROVIDER_URL);
        env.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTH);
        env.put(Context.SECURITY_PRINCIPAL, SECURITY_PRINCIPAL);
        env.put(Context.SECURITY_CREDENTIALS, ROOT_PWD);
        try {
            ctx = new InitialLdapContext(env, connCtls);// 初始化上下文
            System.out.println("认证成功");			
        } catch (javax.naming.AuthenticationException e) {
            System.out.println("认证失败");
        } catch (Exception e) {
            System.out.println("认证出错：" + e);
        }
        return ctx;
    }
    
    
    /**
     * 验证Ldap用户信息
     *
     * @param uid
     * @param password
     */
    public static boolean authenricate(String uid, String password) {

        LdapContext ctx = initCtx();
        String userDN = getUserDN(ctx, uid);
        try {
            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ctx.reconnect(connCtls);
            return true;
        } catch (AuthenticationException e) {
            System.out.println(userDN + " 验证失败");
            System.out.println(e.toString());
        } catch (NamingException e) {
            System.out.println(userDN + " 验证失败");
        }

        return false;
    }

    /**
     * 添加新的dn用户
     *
     * @param name
     * @param password
     */
    public static boolean add(String name, String password) {

        LdapContext ctx = null;
        try {
            //初始化ROOT DN
            ctx = initCtx();

            BasicAttributes attrs = new BasicAttributes();
            BasicAttribute objclassSet = new BasicAttribute("objectClass");
            Attribute pass = new BasicAttribute("userPassword");
            objclassSet.add("OpenLDAPPerson");
            attrs.put(objclassSet);
            pass.add(password);
            attrs.put(pass);
            attrs.put("cn", name);
            attrs.put("sn", name);
            attrs.put("uid", name);
            attrs.put("ou", "managers");
            DirContext createSubcontext = ctx.createSubcontext("uid=" + name + ",ou=managers," + DC_ROOT, attrs);
            if(createSubcontext != null){
                System.out.println("添加成功");
            	return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception in add():" + e);
        } finally {
            close(ctx);
        }
        return false;
    }

    
    /**查找ldap用户
     * @param uid
     * @return
     */
    public static String getUserDN(LdapContext ctx, String uid) {
        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> en = ctx.search(DC_ROOT, "uid=" + uid, constraints);
            if (en == null || !en.hasMoreElements()) {
                System.out.println("未找到该用户");
            	return null;
            }
            // maybe more than one element
            while (en != null && en.hasMoreElements()) {
                Object obj = en.nextElement();
                if (obj instanceof SearchResult) {
                    SearchResult si = (SearchResult) obj;
                    return si.getName() + "," + DC_ROOT;
                } else {
                    System.out.println(obj);
                }
            }
        } catch (Exception e) {
            System.out.println("查找用户时产生异常。");
            e.printStackTrace();
        }
        return null;
    }
    
    
    /**
     * 删除dn
     * by name
     *
     * @param cn
     */
    public static boolean delete(String cn) {
        LdapContext ctx = null;
        try {
            //初始化ROOT DN
            ctx = initCtx();

            ctx.destroySubcontext("uid=" + cn + "," + SECURITY_PRINCIPAL_UID);
            System.out.println("删除成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception in delete():" + e);
        } finally {
            close(ctx);
        }
        return false;
    }

    /**
     * 修改LDAP dn密码
     *
     * @param newPwd
     * @return
     */
    public static boolean updatePwd(String name, String oldPwd, String newPwd) {

        LdapContext ctx = null;
        try {
            /**
             * 获取用户DN
             */
            String DN = "uid=" + name + "," + SECURITY_PRINCIPAL_UID;

            if (!checkDN(DN, oldPwd)) {
                return false;
            }

            /**
             * 初始化ctx
             */
            ctx = initCtx();
            ModificationItem[] mods = new ModificationItem[1];
            /*添加属性*/
            Attribute attr = new BasicAttribute("userPassword", newPwd);
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);

            ctx.modifyAttributes(DN, mods);
            return true;
        } catch (NamingException ne) {
            ne.printStackTrace();
        } finally {
            close(ctx);
        }
        return false;
    }

    
    /**
     * 重命名
     *
     * @param oldDN
     * @param newDN
     * @return
     */
    public static void renameEntry(String oldDN, String newDN) {
        LdapContext ctx = null;
        try {
            //初始化ROOT DN
            ctx = initCtx();
            ctx.rename(oldDN, newDN);
        } catch (NamingException ne) {
            System.err.println("Error: " + ne.getMessage());
        } finally {
            close(ctx);
        }
    }
    
    
    /**
     * 校验旧密码是否正确
     * @param DN
     * @param oldPwd
     * @return
     * @throws NamingException
     */
    private static boolean checkDN(String DN, String oldPwd) throws NamingException {

        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, PROVIDER_URL);
        env.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTH);
        env.put(Context.SECURITY_PRINCIPAL, DN);
        env.put(Context.SECURITY_CREDENTIALS, oldPwd);
        LdapContext ctx = null;
        try {
            ctx = new InitialLdapContext(env, connCtls);
            if (ctx != null) {
                return true;
            }
            return false;
        } finally {
            close(ctx);
        }
    }

   
    /**
     * 关闭链接
     */
    public static void close(DirContext ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (NamingException e) {
                System.out.println("NamingException in close():" + e);
            }
        }
    }

    public static void main(String[] args) {
//
//        LdapService service = new LdapServiceImpl();
//        service.authenricate("user01", "666666");
//        service.add("mm","123456");
//        service.updatePwd("kaka", "", "123456");
    	System.out.println(LdapUtil.SECURITY_PRINCIPAL);
    }
}

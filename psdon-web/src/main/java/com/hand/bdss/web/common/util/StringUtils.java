package com.hand.bdss.web.common.util;

import org.apache.log4j.Logger;

import java.util.Random;

public class StringUtils {
	private static Logger log = Logger.getLogger(StringUtils.class);
	public static String getRandomString(int length) {
        length = length > 0 ? length : 6;
        char[] randchars = new char[length];
        // 不包括0,O,o,l,1
        String randstring = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghgkmnpqrstuvwxyz";
        Random rd = new Random();
        for (int i = 0; i < length; i++) {
            randchars[i] = randstring.charAt(rd.nextInt(54));
        }
        return new String(randchars);
    }
	
	/**
     * 取得随机字串联
     * @param length
     * @return 指定长度的随机字串
     */
    public static String getRandomNumber(int length) {
        length = length > 0 ? length : 6;
        char[] randchars = new char[length];
        String randstring = "0123456789";
        Random rd = new Random();
        for (int i = 0; i < length; i++) {
            randchars[i] = randstring.charAt(rd.nextInt(9));
        }
        return new String(randchars);
    }
    
    /**
     * <p>Checks if a String is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the String.
     * That functionality is available in isBlank().</p>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * <p>Checks if a String is not empty ("") and not null.</p>
     *
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is not empty and not null
     */
    public static boolean isNotEmpty(String str) {
        return str != null && str.length() > 0;
    }

    /**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if a String is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is
     *  not empty and not null and not whitespace
     * @since 2.0
     */
    public static boolean isNotBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return true;
            }
        }
        return false;
    }
    
    /**
	 * 显示身份证掩码
	 * @param mobile
	 * @return
	 */
	public static String formatIdNo(String idNo){
		return formatIdNo(idNo, "***********");
	}
	
	public static String formatIdNo(String idNo, String mask){
		try{
			if(StringUtils.isEmpty(idNo))
				return "";
			idNo = idNo.substring(0, 3) + mask + idNo.substring(14);
			return idNo;
		}catch(Exception e){
			log.error(idNo + mask, e);
		}
		return idNo;
	}
	
	/**
	 * 显示手机号码掩码
	 * @param mobile
	 * @return
	 */
	public static String formatMobile(String mobile){
		return formatMobile(mobile, "****");
	}
	
	public static String formatMobile(String mobile, String mask){
		try{
			if(StringUtils.isEmpty(mobile))
				return "";
			mobile = mobile.substring(0, 3) + mask + mobile.substring(7);
			return mobile;
		}catch(Exception e){
			log.error(mobile + mask, e);
		}
		return mobile;
	}
   
}

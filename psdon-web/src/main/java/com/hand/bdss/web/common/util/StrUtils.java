package com.hand.bdss.web.common.util;

import org.apache.commons.lang3.StringUtils;

public class StrUtils {
	
	public static String escapeStr(String str){
		String temp = "";
		if(StringUtils.isNotBlank(str)){
			temp = str.replaceAll("_", "\\\\_").replaceAll("%", "\\\\%");
		}
		return temp;
	}
}

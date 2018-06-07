package com.hand.bdss.dsmp.component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Test02 {
	public static List<String> lists = Arrays.asList("zhang.txt","zhang_1.txt","zhang_2.txt");;
	public static int num = 0;
	public static String oriName;
	
	public static void main(String[] args) {
		try {
			System.out.println(createNewFileName("zhang.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static  String createNewFileName(String hdfsFileName) throws IOException {
		String newFileName = hdfsFileName;
		if(num==0) {
			oriName = hdfsFileName;
		}
		String[] strs = oriName.split("\\.");
		String str = strs[0];
		if (lists.contains(hdfsFileName)) {
			num++;
			str = str + "_" + num;
			return createNewFileName(str+"."+strs[1]);
		}
		return newFileName;
	}

}

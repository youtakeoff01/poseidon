package com.hand.bdss.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FileTest {

	/*
	 * public static void main(String[] args) throws IOException { File f = null;
	 * File f2 = null; FileWriter fw = null; PrintWriter pw = null; try { f=new
	 * File("d:/splunk/data/splunkData.txt"); f2 = new File("d:/splunk/data/");
	 * if(f.exists() && (double)f.length()/1024/1024>=0.1)
	 * {//如果文件已经存在,且大于128M,则删除重新生成文件 f.delete(); } if(!f.exists()) {
	 * f.setWritable(true, false);//设置写权限，windows下不用此语句 f2.mkdirs();
	 * f.createNewFile(); } fw = new FileWriter(f, true); pw = new PrintWriter(fw);
	 * pw.println("123456789"); pw.flush(); } finally { if(pw!=null) { pw.flush();
	 * pw.close(); } if(fw!=null) { fw.close(); } } }
	 */

	public static void main(String[] args) throws Exception {
//		InputStream in = new BufferedInputStream(new FileInputStream("d:/splunk/data/test01.txt"));
//		OutputStream out = new BufferedOutputStream(new FileOutputStream("d:/splunk/data/test02.txt"));
//		IOUtils.copyBytes(in, out, 4096, true);
//		IOUtils.copy(in, out);
//		in.close();
//		out.close();
		
		// 2018-01-16T10:04:32.000+08:00
		
//		String date = "2018-01-16T10:04:32.000+08:00";
//		
//		String[] strs = date.split("T|\\+");
//		System.out.println(strs[0]+" "+strs[1]);
//		Calendar date = Calendar.getInstance();
//		date.setTime(new Date());
//		date.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY) - 2);
//		SimpleDateFormat format = new SimpleDateFormat();
//		String str = format.format(date.getTime());
//		System.out.println(str);
//		
//		
//		String hdfsPath = "/dsmp/hdfs/splunk/data/pcLog/splunkDataPCLog-2018131.txt";
//		String path = hdfsPath.substring(0, hdfsPath.lastIndexOf("/")+1);
//		System.out.println(path);
		
		
		
	}

}

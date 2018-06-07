package com.hand.bdss.web.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.hand.bdss.web.common.vo.ConfigInfo;

/**
 * 文件的读写utils
 * 
 * @author Administrator
 *
 */
public class ConfigIOUtils {

	public static final String FILEPATH = "WEB-INF/temp/config";
	public static final String ZIPPATH = "WEB-INF/temp/zip";

	/**
	 * 将数据写入到xml文件中
	 * 
	 * @param fileName
	 * @param fileContent
	 * @return
	 */
	public static boolean writeConfig(String fileName, String fileContent) {
		boolean boo = false;
		FileWriter fw = null;
		try {
			File filePath = new File(FILEPATH);
			if(!filePath.exists()){
				filePath.mkdirs();
			}
			fw = new FileWriter(FILEPATH + "/" + fileName + ".xml");
			fw.write(fileContent);
			boo = true;
		} catch (IOException e) {
			boo = false;
			e.printStackTrace();
		} finally{
			if(fw!=null){
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return boo;
	}
    /**
     * 将指定文件夹下面的所有文件打包成zip文件
     * @param zipFileName
     * @return
     */
	public static boolean fileToZip(String zipFileName) {
		boolean flag = false;
		File sourceFile = new File(FILEPATH);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;

		if (sourceFile.exists() == false) {
			System.out.println("待压缩的文件目录：" + FILEPATH + "不存在.");
		} else {
			try {
				File zipFile = new File(ZIPPATH + "/" + zipFileName + ".zip");
				File file = new File(ZIPPATH);
				if(!file.exists()){
					file.mkdirs();
				}
				if (zipFile.exists()) {
					File zipFilePath = new File(ZIPPATH);
					zipFilePath.delete();
				}
				File[] sourceFiles = sourceFile.listFiles();
				if (null == sourceFiles || sourceFiles.length < 1) {
					System.out.println("待压缩的文件目录：" + FILEPATH + "里面不存在文件，无需压缩.");
				} else {
					fos = new FileOutputStream(zipFile);
					zos = new ZipOutputStream(new BufferedOutputStream(fos));
					byte[] bufs = new byte[1024 * 10];
					for (int i = 0; i < sourceFiles.length; i++) {
						// 创建ZIP实体，并添加进压缩包
						ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
						zos.putNextEntry(zipEntry);
						// 读取待压缩的文件并写进压缩包里
						fis = new FileInputStream(sourceFiles[i]);
						bis = new BufferedInputStream(fis, 1024 * 10);
						int read = 0;
						while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
							zos.write(bufs, 0, read);
						}
					}
					flag = true;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				// 关闭流
				try {
					if (null != bis)
						bis.close();
					if (null != zos)
						zos.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
		return flag;
	}
	/**
	 * 将数据写入到一个.properties文件中
	 * @param fileName
	 * @param strConfigs
	 */
	public static boolean writeConfig(String fileName, List<ConfigInfo> strConfigs) {
		boolean boo = false;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			File filePath = new File(FILEPATH);
			if(!filePath.exists()){
				filePath.mkdirs();
			}
			fw = new FileWriter(FILEPATH + "/" + fileName + ".properties");
			bw = new BufferedWriter(fw);
			for (ConfigInfo configInfo : strConfigs) {
				bw.write(configInfo.getXmlName()+"="+configInfo.getConfigInfo()+"\n");
//				bw.newLine();
			}
			boo = true;
		} catch (IOException e) {
			boo = false;
			e.printStackTrace();
		} finally{
			if(bw!=null){
				try {
//					fw.close();
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return boo;
		
	}
	
	
	public static void main(String[] args) {
//		List<ConfigInfo> strConfigs = new ArrayList<ConfigInfo>();
//		ConfigInfo config = new ConfigInfo();
//		config.setXmlName("azkaban");
//		config.setConfigInfo("192.168.11.234");
//		strConfigs.add(config);
//		ConfigInfo config1 = new ConfigInfo();
//		config1.setXmlName("hive");
//		config1.setConfigInfo("192.168.11.1");
//		strConfigs.add(config1);
//		ConfigIOUtils.writeConfig("peizhi", strConfigs);
//		ConfigIOUtils.fileToZip("hehe");
	}
}

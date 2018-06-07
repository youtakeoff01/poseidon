package com.hand.bdss.dsmp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * 创建执行kettle任务的sh文件
 * 
 * @author hand
 *
 */
public class CreateSHUtils {
	
	private static String kettlePath = "/usr/is/kettle/";

	public static void createKettleSH(String kjbFilePath,String kettleSHPath,String kettleSHName) {
		try {
			File afile = new File(kjbFilePath);
			if (afile.renameTo(new File(kettlePath + afile.getName()))) {
				System.out.println("File is moved successful!");
			} else {
				System.out.println("File is failed to move!");
			}
			createFile(kjbFilePath,kettleSHPath,kettleSHName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean createFile(String kjbFilePath,String kettleSHPath,String kettleSHName) {
		Boolean bool = false;
		String fileName = kjbFilePath.substring(kjbFilePath.lastIndexOf("/") + 1, kjbFilePath.length());
		String filenameTemp = kettleSHPath + kettleSHName + ".sh";
		File file = new File(filenameTemp);
		try {
			// 如果文件不存在，则创建新的文件
			if (!file.exists()) {
				file.createNewFile();
				bool = true;
				System.out.println("success create file,the file is " + filenameTemp);
				String fileContent = createContent(fileName);
				// 创建文件成功后，写入内容到文件里
				writeFileContent(filenameTemp, fileContent);
			} else {
				System.out.println("file already exists!!");
				return bool;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bool;
	}

	public static boolean writeFileContent(String filepath, String newstr) throws IOException {
		Boolean bool = false;
		String filein = newstr + "\r\n";// 新写入的行，换行
		String temp = "";

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			File file = new File(filepath);// 文件路径(包括文件名称)
			// 将文件读入输入流
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			StringBuffer buffer = new StringBuffer();
			// 文件原有内容
			for (int i = 0; (temp = br.readLine()) != null; i++) {
				buffer.append(temp);
				// 行与行之间的分隔符 相当于“\n”
				buffer = buffer.append(System.getProperty("line.separator"));
			}
			buffer.append(filein);

			fos = new FileOutputStream(file);
			pw = new PrintWriter(fos);
			pw.write(buffer.toString().toCharArray());
			pw.flush();
			bool = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			// 不要忘记关闭
			if (pw != null) {
				pw.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (br != null) {
				br.close();
			}
			if (isr != null) {
				isr.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
		return bool;
	}

	public static String createContent(String fileName) {
		String fileContent = "#!/bin/bash\ncd /storage/dataupload/is/data-integration\n";
		if (fileName.endsWith("kjb")) {
			fileContent += "sh kitchen.sh -file=" + kettlePath + fileName;
		}
		if (fileName.endsWith("ktr")) {
			fileContent += "sh kitchen.sh -file=" + kettlePath + fileName;
		}
		return fileContent;
	}

	public static void main(String[] args) {
		String filePath = "/usr/is/creatHiveMineCluster.kjb";
//		createKettleSH(filePath);

	}

}

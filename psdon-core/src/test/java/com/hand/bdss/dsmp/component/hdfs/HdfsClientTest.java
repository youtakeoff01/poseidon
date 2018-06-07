package com.hand.bdss.dsmp.component.hdfs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HdfsClientTest {
	private static HDFSClient hdfs = null;
	static {
//		hdfs = new HDFSClient();
//		hdfs.setHdfsKey("fs.hdfs.impl");
//		hdfs.setHdfsValue("org.apache.hadoop.hdfs.DistributedFileSystem");
//		hdfs.init();
	}

	public static void main(String[] args) throws Exception {
		// hdfs.createDirectoy("/dsmp/wqzcode/");
		// hdfs.deleteDirectoy("/dsmp/");
		// hdfs.uploadFile("D:/software/apache-maven-3.3.9/conf/settings.xml");
//		hdfs.downloadFile("POITest.xlsx", "/ueba/", "C:/Users/wqz/Desktop/wqz/");
//		// hdfs.deleteFile("test.xlsx", "/dsmp/");
//		// hdfs.moveFile("/ueba/POITest.xlsx", "/dsmp/test.xlsx");
//		String aaa = "C:/Users/wqz/Desktop/wqz/test.xlsx";
//		System.out.println("***********" + aaa.split("/", -1)[aaa.split("/", -1).length - 1] + "############");
//		new HdfsClientTest().readHDFSListAll();

	}

	
}

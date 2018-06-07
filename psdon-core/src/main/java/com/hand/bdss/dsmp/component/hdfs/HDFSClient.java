package com.hand.bdss.dsmp.component.hdfs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.model.DocumentModel;
import com.hand.bdss.dsmp.model.ReturnCode;

//TODO HDFS读写类 
//TODO HDFS删除的策略是移到回收站，回收站数据会根据HDFS的设置自动清除
public class HDFSClient {

	private static final Logger logger = LoggerFactory.getLogger(HDFSClient.class);

	public static final String PATH_ROOT = SystemConfig.HDFS_ROOT_PATH;// 根目录

	public String oriName;

	private String hdfsKey;
	private String hdfsValue;

	private long total = 0;// 记录文件夹大小

	// private int num = 0;

	public void setHdfsKey(String hdfsKey) {
		this.hdfsKey = hdfsKey;
	}

	public void setHdfsValue(String hdfsValue) {
		this.hdfsValue = hdfsValue;
	}
	/**
	 * 加载fileSystem
	 */
	private FileSystem getFileSystem() {
		FileSystem fileSystem = null;
		try {
			Configuration conf = new Configuration();
			conf.set(hdfsKey, hdfsValue);
			fileSystem = FileSystem.get(conf);
		} catch (IOException e) {
			logger.error("HDFSClient.getFileSystem error ,error msg is", e);
		}
		return fileSystem;
	}

	/**
	 * 关闭fileSystem
	 *
	 * @param fileSystem
	 */
	public void close(FileSystem fileSystem) {
		try {
			if (null != fileSystem) {
				fileSystem.close();
			}
		} catch (IOException e) {
			logger.error("close fileSystem error", e);
		}
	}

	/**
	 * 下载文件
	 *
	 * @param fileName
	 * @param srcPath
	 * @param dstPath
	 * @return
	 */
	public boolean downloadFile(String fileName, String srcPath, String dstPath) {

		FileSystem fileSystem = this.getFileSystem();
		try {
			this.getFileSystem();
			if (!srcPath.endsWith("/")) {
				srcPath += "/";
			}
			fileSystem.copyToLocalFile(new Path(srcPath + fileName), new Path(dstPath));
			return true;
		} catch (IOException e) {
			logger.error("HDFS 下载文件失败", e);
		} finally {
			this.close(fileSystem);
		}
		return false;
	}

	/**
	 * 下载文件
	 *
	 * @param srcPath
	 * @param dstPath
	 * @return
	 * @throws IOException
	 */
	public boolean downloadFile(String srcPath, String dstPath) throws IOException {

		String allPath = "";
		FileSystem fileSystem = this.getFileSystem();
		if (srcPath.startsWith("/")) {
			allPath += PATH_ROOT + srcPath.substring(1, srcPath.length() - 1);
		} else {
			allPath += PATH_ROOT + srcPath;
		}

		try {
			// fileSystem = getFileSystem();
			fileSystem.copyToLocalFile(new Path(allPath), new Path(dstPath));
			return true;
		} catch (IOException e) {
			logger.error("HDFS 下载文件失败", e);
		} finally {
			this.close(fileSystem);
		}
		return false;
	}

	/**
	 * 上传文件
	 *
	 * @param fileName
	 * @param srcPath
	 * @return
	 */
	public boolean uploadFile(String fileName, String srcPath, String dstPath) {

		FileSystem fileSystem = this.getFileSystem();
		try {
			if (srcPath.endsWith("/")) {
				fileSystem.copyFromLocalFile(new Path(srcPath + fileName), new Path(dstPath));
			} else {
				srcPath += "/";
				fileSystem.copyFromLocalFile(new Path(srcPath + fileName), new Path(dstPath));
			}
			return true;
		} catch (IOException e) {
			logger.error("HDFS 上传文件失败", e);
		} finally {
			this.close(fileSystem);
		}
		return false;
	}

	/**
	 * 删除文件
	 *
	 * @param fileName
	 * @return
	 * @paramfilePath
	 */
	public boolean deleteFile(String fileName, String srcPath) {

		FileSystem fileSystem = this.getFileSystem();
		try {
			if (!srcPath.endsWith("/")) {
				srcPath += "/";
			}
			fileSystem.delete(new Path(srcPath + fileName),true);
			return true;
		} catch (IOException e) {
			logger.error("删除文件失败,文件名称为：{},文件路径为：{},错误信息为：{}",fileName,srcPath, e);
			return false;
		} finally {
			this.close(fileSystem);
		}
	}

	/**
	 * 下载文件
	 *
	 * @param srcPath
	 * @return
	 * @throws IOException
	 */
	public Map<String, Object> downloadFile(String srcPath) throws IOException {

		Map<String, Object> hdfsMap = new HashMap<String, Object>();
		Path filePath = new Path(PATH_ROOT + srcPath);
		FileSystem fileSystem = this.getFileSystem();
		if (!fileSystem.exists(filePath)) {
			this.close(fileSystem);
			hdfsMap.put("returnMessage", "下载文件不存在！");
			hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_SRCPATH_NOT_FOUND);
			return hdfsMap;
		}
		hdfsMap.put("dataStream", fileSystem.open(filePath));
		hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_SUCCESS);
		hdfsMap.put("fileSystem", fileSystem);// 到后面关闭使用，fileSystem 不能在这里关闭
		return hdfsMap;
	}

	/**
	 * 上传文件
	 *
	 * @return
	 * @throws IOException
	 * @paramfileName
	 */
	public Map<String, Object> uploadFile(InputStream in, String dstPath) throws IOException {

		Map<String, Object> hdfsMap = new HashMap<>();
		FileSystem fileSystem = this.getFileSystem();
		try {

			if (fileSystem.exists(new Path(PATH_ROOT + dstPath))) {
				hdfsMap.put("returnMessage", "存在同名文件！");
				hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_DOCUMENT_NOT_ONLY);
				return hdfsMap;
			}
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(URI.create(PATH_ROOT + dstPath), conf);
			OutputStream out = fs.create(new Path(PATH_ROOT + dstPath));
			IOUtils.copyBytes(in, out, 4896, true);
			hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_SUCCESS);
		} finally {
			this.close(fileSystem);
		}
		return hdfsMap;
	}

	/**
	 * 删除文件/文件夹
	 *
	 * @return
	 * @throws IOException
	 * @paramfilePath
	 */
	public Map<String, String> delete(String srcPath) throws IOException {
		Map<String, String> hdfsMap = new HashMap<>();
		hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_ERROR);

		FileSystem fileSystem = this.getFileSystem();
		try {
			if(srcPath != null && !srcPath.contains("/dsmp/hdfs/")){
				srcPath = PATH_ROOT + srcPath;
			}
			logger.info("/delete srcPath = " + srcPath);
			if (!fileSystem.exists(new Path(srcPath))) {
				hdfsMap.put("returnMessage", "删除文件不存在！");
				hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_SRCPATH_NOT_FOUND);
				return hdfsMap;
			}
			fileSystem.delete(new Path(srcPath), true);
			hdfsMap.put("retMessage", "删除成功！");
			hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_SUCCESS);
		} catch (IOException e) {
			logger.error("HDFS 删除文件失败", e);
		} finally {
			this.close(fileSystem);
		}
		return hdfsMap;
	}

	/**
	 * 文件夹删除
	 *
	 * @param srcPath
	 * @return
	 */
	public boolean deleteDirectoy(String srcPath) {
        boolean boo = true;
		FileSystem fileSystem = this.getFileSystem();
		try {
			if (!fileSystem.exists(new Path(srcPath))) {
				logger.info("this is path not exist!");
			} else {
				fileSystem.delete(new Path(srcPath), true);
			}
		} catch (IllegalArgumentException | IOException e) {
			logger.error("删除文件夹失败", e);
			boo = false;
		} finally {
			this.close(fileSystem);
		}
		return boo;
	}

	/**
	 * HDFS 重命名文件同时也是移动文件
	 *
	 * @param fileName(文件重命名)
	 * @param srcPath
	 *            资源路径(包括文件名路径)
	 * @param dstPath
	 *            目标路径
	 * @return
	 */
	public Map<String, String> renameFile(String srcPath, String dstPath, String fileName) throws IOException {
		Map<String, String> hdfsMap = new HashMap<String, String>();
		hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_ERROR);

		FileSystem fileSystem = this.getFileSystem();
		try {
			if (!fileSystem.exists(new Path(PATH_ROOT + dstPath + fileName))) {
				fileSystem.rename(new Path(PATH_ROOT + srcPath), new Path(PATH_ROOT + dstPath + fileName));
				hdfsMap.put("retMessage", "操作成功！");
				hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_SUCCESS);
			} else {
				hdfsMap.put("returnMessage", "存在同名文件！！");
				hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_DOCUMENT_NOT_ONLY);
			}
		} catch (IOException e) {
			logger.error("renameFile error", e);
		} finally {
			this.close(fileSystem);
		}
		return hdfsMap;
	}

	/**
	 * 创建文件夹
	 *
	 * @return
	 * @throws IOException
	 * @paramdirectoyName
	 */
	public Map<String, String> createDirectoy(String srcPath) throws IOException {
		Map<String, String> hdfsMap = new HashMap<String, String>();
		hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_ERROR);

		srcPath = PATH_ROOT + srcPath;
		FileSystem fileSystem = this.getFileSystem();
		try {
			if (!fileSystem.exists(new Path(srcPath))) {
				fileSystem.mkdirs(new Path(srcPath));
				hdfsMap.put("retMessage", "文件夹创建成功！");
				hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_SUCCESS);
			} else {
				hdfsMap.put("returnMessage", "存在同名文件！");
				hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_DOCUMENT_NOT_ONLY);
			}
		} catch (IOException e) {
			logger.error("创建文件夹 失败   error", e);
		} finally {
			this.close(fileSystem);
		}
		return hdfsMap;
	}

	/**
	 * 文件复制
	 *
	 * @throws IOException
	 */
	public Map<String, String> copyFile(String src, String dstPath, String fileName) throws IOException {
		Map<String, String> hdfsMap = new HashMap<String, String>();
		hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_ERROR);

		Configuration conf = new Configuration();
		conf.set(hdfsKey, hdfsValue);
		FileSystem fileSystem = FileSystem.get(conf);

		try {

			if (!fileSystem.exists(new Path(PATH_ROOT + dstPath + fileName))) {
				Path path = new Path(PATH_ROOT + src);
				FileUtil.copy(fileSystem, path, fileSystem, new Path(PATH_ROOT + dstPath + fileName), false, conf);
				hdfsMap.put("retMessage", "文件复制成功！");
				hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_SUCCESS);
			} else {
				hdfsMap.put("returnMessage", "存在同名文件！");
				hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_DOCUMENT_NOT_ONLY);
			}
		} catch (IOException e) {
			logger.error("文件复制异常", e);
		} finally {
			this.close(fileSystem);
		}
		return hdfsMap;

	}
	
	/**
	 * 复制HDFS文件到本地服务器
	 * @param src
	 * @param dstPath
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public Map<String, String> copyFileToLocal(String src, String dstPath, String fileName) throws IOException {
		Map<String, String> hdfsMap = new HashMap<String, String>();
		hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_ERROR);

		Configuration conf = new Configuration();
		conf.set(hdfsKey, hdfsValue);
		FileSystem fileSystem = FileSystem.get(conf);

		try {
			FSDataInputStream in = fileSystem.open(new Path(src));
			OutputStream output = new FileOutputStream(dstPath + fileName);
			IOUtils.copyBytes(in,output,4096,true);
			
			hdfsMap.put("retMessage", "文件复制成功！");
			hdfsMap.put("returnCode", ReturnCode.RETURN_CODE_SUCCESS);
		} catch (IOException e) {
			hdfsMap.put("retMessage", "文件复制失败！");
			logger.error("文件复制异常", e);
		} finally {
			this.close(fileSystem);
		}
		return hdfsMap;

	}

	/**
	 * 校验是否是文件
	 *
	 * @return
	 * @throws IOException
	 * @paramfilePath
	 */
	public boolean isFile(String srcPath) throws IOException {
		Path path = new Path(PATH_ROOT + srcPath);

		FileSystem fileSystem = this.getFileSystem();
		try {
			if (fileSystem.isFile(path)) {
				return true;
			}
		} catch (IOException e) {
			logger.error("校验文件失败", e);
		} finally {
			this.close(fileSystem);
		}
		return false;
	}

	/**
	 * 校验是否是文件夹
	 *
	 * @return
	 * @throws IOException
	 * @paramfilePath
	 */
	public boolean isDirectory(String srcPath) throws IOException {
		Path path = new Path(PATH_ROOT + srcPath);

		FileSystem fileSystem = this.getFileSystem();
		try {
			if (fileSystem.isDirectory(path)) {
				return true;
			}
		} catch (IOException e) {
			logger.error("校验文件失败", e);
		} finally {
			this.close(fileSystem);
		}
		return false;
	}

	/**
	 * 获取用户的文件树
	 *
	 * @param pathName
	 * @param username
	 * @return
	 */
	public DocumentModel getAllDictoryTree(String pathName, String username) {
		DocumentModel documentModel = new DocumentModel();
		try {
			documentModel.setList(getDocumentTree(pathName, username));
		} catch (IOException e) {
			logger.error("获取文件树 :/getAllDictoryTree", e);
		}

		return documentModel;
	}

	/**
	 * 截取文件名称 不包含用户文件名称
	 *
	 * @param srcPath
	 * @return
	 */
	private String processSrcPath(String srcPath) {
		String[] path = srcPath.split(PATH_ROOT, srcPath.length());
		if ((null != path) && (path.length > 0)) {
			return path[(path.length - 1)];
		}
		return null;
	}

	/**
	 * 获取根目录文件树
	 *
	 * @return
	 * @throws IOException
	 */
	public List<DocumentModel> getDirectoyAndFileTree(String pathName, String username) throws IOException {
		List<DocumentModel> modelList = null;

		FileSystem fileSystem = this.getFileSystem();
		try {
			Path path = new Path(PATH_ROOT + pathName);
			if (!fileSystem.exists(path)) {
				fileSystem.mkdirs(path);

				return modelList;
			}
			FileStatus[] fileStatus = fileSystem.listStatus(path);
			// System.out.println(fileStatus.length);
			if (fileStatus != null && fileStatus.length > 0) {
				modelList = new ArrayList<>();
				DocumentModel model;
				for (FileStatus file : fileStatus) {
					total = 0;
					model = new DocumentModel();
					long length = 0;
					// 如果查询出来的是文件
					if (!file.isFile()) {
						length = getTotalSize(file, fileSystem);
						model.setSize(length);
					} else {
						model.setSize(file.getLen());
					}
					model.setSrcPath(processSrcPath(file.getPath().toString(), username));
					model.setUpdatetime(new Timestamp(file.getModificationTime()));
					model.setDirectory(file.isDirectory());
					model.setName(file.getPath().getName());
					modelList.add(model);
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("文件NotFound", e);
		} catch (IOException e) {
			logger.error("IO异常", e);
		} finally {
			this.close(fileSystem);
		}
		return modelList;
	}

	/**
	 * 获取文件夹的大小
	 *
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public long getTotalSize(FileStatus file, FileSystem fileSystem) throws IOException {
		String fullPath = file.getPath().toString();// hdfs://hdp/dsmp/hdfs/admin/undefined/02_shell编程.ppt
		fullPath = fullPath.substring(21, fullPath.length());
		Path path = new Path(PATH_ROOT + fullPath);
		if (file.isDirectory()) {
			FileStatus[] fileStatus = fileSystem.listStatus(path);
			for (FileStatus fileStatus2 : fileStatus) {
				if (fileStatus2.isDirectory()) {
					getTotalSize(fileStatus2, fileSystem);
				}
				total += fileStatus2.getLen();
			}
		}
		return total;
	}

	/**
	 * 获取文件夹树
	 *
	 * @param pathName
	 * @param username
	 * @return
	 * @throws IOException
	 */
	private List<DocumentModel> getDocumentTree(String pathName, String username) throws IOException {
		List<DocumentModel> list = null;

		FileSystem fileSystem = this.getFileSystem();
		try {
			Path path = new Path(PATH_ROOT + pathName);
			// 加载fileSystem
			if (!(fileSystem.exists(path))) {
				return list;
			}
			FileStatus[] fileStatus = fileSystem.listStatus(path);
			if ((fileStatus != null) && (fileStatus.length > 0)) {
				list = new ArrayList<>();
				DocumentModel model;
				for (FileStatus file : fileStatus) {
					if (file.isDirectory()) {
						model = new DocumentModel();
						model.setSrcPath(processSrcPath(file.getPath().toString(), username));
						model.setUpdatetime(new Timestamp(file.getModificationTime()));
						model.setSize(Long.valueOf(file.getLen()));
						model.setDirectory(file.isDirectory());
						model.setName(file.getPath().getName());
						// if (file.isDirectory()) {
						model.setList(getDocumentTree(processSrcPath(file.getPath().toString()), username));
						// }
						list.add(model);
					}
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("文件NotFound", e);
		} catch (IOException e) {
			logger.error("IO异常", e);
		} finally {
			this.close(fileSystem);
		}
		return list;
	}

	/**
	 * 截取文件名称 包含用户文件名称
	 *
	 * @param srcPath
	 * @param username
	 * @return
	 */
	private String processSrcPath(String srcPath, String username) {
		String[] path = srcPath.split(PATH_ROOT + username, srcPath.length());
		if ((null != path) && (path.length > 0)) {
			return path[(path.length - 1)];
		}
		return null;
	}

	/**
	 * 解析文件地址
	 *
	 * @param path
	 * @return
	 */
	/*
	 * private String processPath(String path) { path =
	 * path.substring(path.indexOf("/", path.indexOf("/", path.indexOf("/") + 1) +
	 * 1)); return path; }
	 */

	/***
	 *
	 * 读取HDFS某个文件夹的所有 文件，并打印
	 *
	 **/
	public void readHDFSListAll() throws Exception {

		FileSystem fileSystem = this.getFileSystem();
		try {
			// 流读入和写入
			InputStream in = null;
			// 获取HDFS的conf
			// 使用缓冲流，进行按行读取的功能
			BufferedReader buff = null;
			// 获取日志文件的根目录
			// Path listf = new Path("hdfs://hadoop001.edcs.org:8020/dsmp/tc/");
			Path listf = new Path("hdfs://antx/dsmp/hdfs/jars");
			// 获取根目录下的所有2级子文件目录
			FileStatus stats[] = fileSystem.listStatus(listf);
			// 自定义j，方便查看插入信息
			// int j = 0;
			for (int i = 0; i < stats.length; i++) {
				// 获取子目录下的文件路径
				FileStatus temp[] = fileSystem.listStatus(new Path(stats[i].getPath().toString()));
				for (int k = 0; k < temp.length; k++) {
					System.out.println("文件路径名:" + temp[k].getPath().toString());
					// 获取Path
					Path p = new Path(temp[k].getPath().toString());
					// 打开文件流
					in = fileSystem.open(p);
					// BufferedReader包装一个流
					buff = new BufferedReader(new InputStreamReader(in));
					String str = null;
					while ((str = buff.readLine()) != null) {

						System.out.println("-----" + str);
					}
					buff.close();
					in.close();
				}

			}
		} finally {
			this.close(fileSystem);
		}
	}

	/**
	 * 将本地文件复制到hdfs对应的目录文件中
	 * 
	 * @param diskPath
	 * @param isNewFile
	 * @param hdfsPath
	 * @param hdfsFileName
	 * @return
	 */
	public boolean insertDatas(String diskPath, String hdfsPath, String hdfsFileName) {
		boolean boo = true;
		FileSystem fileSystem = null;
		InputStream in = null;
		OutputStream out = null;
		try {
			fileSystem = this.getFileSystem();
			Path path = new Path(hdfsPath);
			if (!hdfsPath.endsWith("/")) {
				hdfsPath += "/";
			}
			Path path02 = new Path(hdfsPath + hdfsFileName);
			if (!fileSystem.exists(path)) {// 如果不存在则创建
				fileSystem.mkdirs(path);
			}
			// if (!sameDay) {// 如果是本地新产生的文件，则hdfs也要对应产生一个文件
			// hdfsFileName = createNewFileName(hdfsPath, hdfsFileName, fileSystem);
			// path02 = new Path(hdfsPath + hdfsFileName);
			// fileSystem.createNewFile(path02);
			// } else {
			if (!fileSystem.exists(path02)) {
				fileSystem.createNewFile(path02);
			}
			// }
			// 拷贝本地文件到hdfs上
			in = new BufferedInputStream(new FileInputStream(diskPath));
			out = fileSystem.create(path02);
			IOUtils.copyBytes(in, out, 4096, true);
		} catch (Exception e) {
			logger.error("HDFSClient.insertDatas is error,error msg is", e);
			boo = false;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (Exception e2) {
				logger.error("close stream error,error msg is", e2);
			}
			this.close(fileSystem);
		}
		return boo;
	}

	/*
	 * private String createNewFileName(String hdfsPath, String hdfsFileName,
	 * FileSystem fileSystem) throws IOException { if (num == 0) { oriName =
	 * hdfsFileName; } Path path = new Path(hdfsPath + hdfsFileName); String[] strs
	 * = oriName.split("\\."); String str = strs[0]; if (fileSystem.exists(path)) {
	 * num++; str = str + "_" + num; return createNewFileName(hdfsPath, str + "." +
	 * strs[1], fileSystem); } return hdfsFileName; }
	 */
}

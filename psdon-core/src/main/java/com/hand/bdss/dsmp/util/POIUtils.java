package com.hand.bdss.dsmp.util;

import com.hand.bdss.dsmp.component.hdfs.HDFSClient;
import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.model.ETLEum;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class POIUtils {
//	final static Logger logger = LoggerFactory.getLogger(POIUtils.class);

	private static HDFSClient hdfs;
	private Sheet sheet;
	static {
		hdfs = new HDFSClient();
		hdfs.setHdfsKey("fs.hdfs.impl");
		hdfs.setHdfsValue("org.apache.hadoop.hdfs.DistributedFileSystem");
//		hdfs.init();
	}

	/**
	 * 时间格式转换静态方法，
	 * 
	 * @param pattern
	 *            格式
	 * @param date
	 * @return
	 */

	public static String Date2String(String pattern, Date date) {
		SimpleDateFormat sf = new SimpleDateFormat(pattern);
		return sf.format(date);
	}

	public static Date String2Date(String pattern, String date) throws ParseException {
		SimpleDateFormat sf = new SimpleDateFormat(pattern);
		return sf.parse(date);
	}

	/**
	 * 将文件下载到本地以便于用POI对文件就行操作,返回本地文件路径
	 * 
	 * @param hdfs_path
	 * @param local_path
	 * @throws IOException
	 */
	public static String dowlondFile(String hdfs_path, String local_path) throws IOException {
		String file_name = hdfs_path.split("/", -1)[hdfs_path.split("/", -1).length - 1];
		hdfs.downloadFile(hdfs_path, local_path);
		if (!local_path.endsWith("/")) {
			local_path += "/";
		}
		return local_path + file_name;
	}

	/**
	 * 
	 * @param hdfs_path
	 * @param local_path
	 * @return 返回excel表格里面的第一张sheet表
	 * @throws Exception
	 */

	@SuppressWarnings("resource")
	public void getSheet(String hdfs_path, String local_path) throws Exception {
		String inpath = POIUtils.dowlondFile(hdfs_path, local_path);
		// String inpath="C:/Users/wqz/Desktop/wqz/poiTest.csv";
		Workbook wb = null;
		String version = inpath.split("\\.")[inpath.split("\\.").length - 1];
		/**
		 * 判断office版本,因为2003和2007版本的poi api不同
		 */
		if ("xlsx".equals(version)) {
			wb = new XSSFWorkbook(inpath);
		} else if ("xls".equals(version)) {
			POIFSFileSystem ps = new POIFSFileSystem(new FileInputStream(inpath));
			wb = new HSSFWorkbook(ps);
		} else if ("csv".equals(version)) {
			POIFSFileSystem ps = new POIFSFileSystem(new FileInputStream(inpath));
			wb = new HSSFWorkbook(ps);
		} else {
			throw new Exception("文件格式不支持");
		}

		sheet = wb.getSheetAt(0);
	}

	/**
	 * 将excel文件转换为CSV文件方便导入hive表中并删除
	 * 
	 * @param file_name
	 * @param tableName
	 * @return 返回csv文件在hdfs上的路径便于生成hive映射
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public String Xlsx2Csv(String file_name, String tableName) throws Exception {
		StringBuffer sb = new StringBuffer();
		String local_path = ETLEum.CSV_PATH.toString();
		if (sheet.getRow(0) != null) {
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				for (int j = 0; j < row.getLastCellNum(); j++) {
					Cell cell = row.getCell(j);
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_STRING:// 字符串
						sb.append(cell.getStringCellValue() + ",");
						break;
					case Cell.CELL_TYPE_NUMERIC:// 数字
						if (DateUtil.isCellDateFormatted(cell)) {
							Date date = cell.getDateCellValue();
							sb.append(POIUtils.Date2String("yyyy-MM-dd", date) + ",");
						} else {
							sb.append(cell.getNumericCellValue() + ",");
						}
						break;
					case Cell.CELL_TYPE_BOOLEAN:// boolean类型
						sb.append(cell.getBooleanCellValue() + ",");
						break;
					case Cell.CELL_TYPE_BLANK:// 空
						sb.append("\t,");
						break;
					default:
						throw new Exception("字段类型不合理");
					}
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append("\n");
			}
			File file_csv = null;
			if (!local_path.endsWith("/")) {
				local_path += "/";
			}
			file_csv = new File(local_path + tableName + ".csv");
			BufferedWriter bfWriter = new BufferedWriter(new FileWriterWithEncoding(file_csv, "utf-8"));
			bfWriter.write(sb.toString());
			bfWriter.close();
			File file_xlsx = new File(local_path + file_name);
			// 删除本地的xlsx excel文件
			file_xlsx.delete();
			// 上传csv文件到hdfs
			hdfs.createDirectoy("/dsmp/ExcelTable/" + tableName);
			hdfs.uploadFile(file_csv.getName(), "/dsmp/ExcelTable/" + tableName, file_csv.getPath());
			// 删除本地csv文件
			String hdfs_path = "/dsmp/ExcelTable/" + tableName + "/" + file_csv.getName();
			file_csv.delete();
			return hdfs_path;
		}
		return null;
	}

	/**
	 * 去除csv文件的表头信息
	 * 
	 * @param local_path
	 *            文件下载到本地的路径
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public String delTableHead(String hdfs_path, String local_path, String tableName) throws Exception {
		String dowlondFile = dowlondFile(hdfs_path, local_path);
		File firstFile = new File(dowlondFile);
		File lastFile = new File(ETLEum.CSV_PATH.toString() + firstFile.getName());
		RandomAccessFile readFile = new RandomAccessFile(firstFile, "rw");
		RandomAccessFile wrieteFile = new RandomAccessFile(lastFile, "rw");
		String tempLine = null;
		int count = 1;
		while ((tempLine = readFile.readLine()) != null) {
			if (count != 1) {
				wrieteFile.writeBytes(tempLine);
				wrieteFile.writeBytes("\n");
			}
			count++;
		}
		wrieteFile.close();
		readFile.close();
		firstFile.delete();
		// 上传csv文件到hdfs
		hdfs.createDirectoy("dsmp/ExcelTable/" + tableName);
		InputStream in =new FileInputStream(lastFile.getPath());
		hdfs.uploadFile(in,"dsmp/ExcelTable/" + tableName+"/" + lastFile.getName());
		String hdfs_Path = SystemConfig.HDFS_ROOT_PATH+"dsmp/ExcelTable/" + tableName + "/" + lastFile.getName();
		
		// 删除本地csv文件
		lastFile.delete();
		return hdfs_Path;

	}
}

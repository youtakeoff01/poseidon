package com.hand.bdss.dsmp.model;
/**
 * ETL各种文件的输出路径
 * @author wqz
 *
 */
public enum ETLEum {
	 //文件转存hive表下载到本地存放的路径
	 EXCEL_PATH("/storage/dataupload/dsmp/ExcelTable/"),
	 //文件处理后生成的csv存放路径
	 CSV_PATH("/storage/dataupload/dsmp/CsvTable/"),
	 //SQL脚本本地存放路径
	 SCRIPT_SQL_PATH("/storage/dataupload/dsmp/script_sql/"),//E:\storeage\dataupload\dsmp\script_sql
	 //sqoop脚本本地存放路径
	 SCRIPT_SQOOP_PATH("/storage/dataupload/dsmp/script_sqoop/"),

	 //kettle -  azkaban job path
	 AZKABAN_KETTLE_JOB_PATH("/storage/dataupload/dsmp/azkaban_kettle_job/"),
	 //kettle path
	 KETTLE_PATH("/storage/dataupload/dsmp/kettle/"),
		
	 //处理后文件上传到HDFS存放路径
	 HDFSOUTPUTDIR("/dsmp/database/"),
	 //HA后nameserver
	 HDFSNAMENODE("hdfs://antx"),
	 NEWLINE("\n"),
	 //AZKABAN job文件路径
	 AZKABAN_JOB_PATH("/storage/dataupload/dsmp/azkaban_job/");

	private String name;

	private ETLEum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return String.valueOf(this.name);
	}
}

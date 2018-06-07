package com.hand.bdss.dsmp.service.etl;

import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.hand.bdss.dsmp.model.DataSourceType;
import com.hand.bdss.dsmp.model.ETLEum;
import com.hand.bdss.dsmp.model.ETLJobConfig;
import com.hand.bdss.dsmp.model.HiveMetaData;
import com.hand.bdss.dsmp.model.HiveMetaTableField;
import com.hand.bdss.dsmp.model.TableMeta;
import com.hand.bdss.dsmp.service.metadata.AtlasMetaManage;
import com.hand.bdss.dsmp.util.DB2DatabaseSupport;
import com.hand.bdss.dsmp.util.DatabaseSupport;
import com.hand.bdss.dsmp.util.DatabaseSupports;
import com.hand.bdss.dsmp.util.MysqlDatabaseSupport;
import com.hand.bdss.dsmp.util.OracleDatabaseSupport;
import com.hand.bdss.dsmp.util.PostgresqlDatabaseSupport;
import com.hand.bdss.dsmp.util.SqlServerDatabaseSupport;
import com.hand.bdss.task.config.SystemConfig;

/**
 * RDBMS TO HIVE 数据集成 -》数据同步
 */
public class DBToHive {

	private static final Logger logger = LoggerFactory.getLogger(DBToHive.class);

	private static String NewLine = "\n";

	private String jdbcDriverClassName;

	private String jdbcUrl;

	private String jdbcUsername;

	private String jdbcPassword;

	private String jdbcDatabase;

	private String jobID;

	private String tableName;// DB表名

	private String queryField;

	private String hiveTableName;

	private int sqoopNumMappers = 3;

	private String baseTime;

	private SingleConnectionDataSource dataSource;

	private DatabaseSupport databaseSupport;

	private Boolean hdfsOutputDirDelete = true;

	private Boolean hiveDropTable = true;

	private String hiveFileType = "PARQUET";

	private String hdfsOutputDir;

	private String hdfsNameNode;

	private String queryCondition;

	private String jobShell;

	private List<String> handleTables = Lists.newArrayList("mdl_logstore_standard_log", "mdl_assignment_submissions",
			"mdl_course", "mdl_attendance", "mdl_attendance_log", "mdl_attendance_sessions", "mdl_attendance_tempusers",
			"mdl_attendance_warning_done");

	private List<String> handleColumns = Lists.newArrayList("timecreated", "timemodified", "timetaken", "timesent");

	private void initRun(ETLJobConfig etlJobConfig) {

		jdbcDriverClassName = etlJobConfig.getDataSource().getDbDriver();
		String url = etlJobConfig.getDataSource().getDbUrl();

		jdbcUrl = etlJobConfig.getDataSource().getDbUrl();
		jdbcUsername = etlJobConfig.getDataSource().getDbUser();
		jdbcPassword = etlJobConfig.getDataSource().getDbPwd();
		String db = etlJobConfig.getDataSource().getDbName();

		if (jdbcDriverClassName.contains("oracle")) {
			jdbcUrl = url.substring(0, url.lastIndexOf(":"));
			jdbcDatabase = db.substring(db.lastIndexOf(":") + 1, db.length());
			jdbcUrl = String.join(":", jdbcUrl,"orcl");
		} else {
			jdbcUrl = url;
			jdbcDatabase = db;
		}

		queryField = etlJobConfig.getDataSource().getQueryField();
		queryCondition = etlJobConfig.getDataSource().getQueryCondition();
		jobID = etlJobConfig.getJobId();
		hiveTableName = etlJobConfig.getSyncSource();
		tableName = etlJobConfig.getDataSource().getTableName();

		hdfsOutputDir = ETLEum.HDFSOUTPUTDIR.toString();
		hdfsNameNode = ETLEum.HDFSNAMENODE.toString();
		Preconditions.checkNotNull(jdbcDriverClassName, "参数jdbcDriverClassName为空.");
		Preconditions.checkNotNull(jdbcUrl, "参数jdbcUrl为空.");
		Preconditions.checkNotNull(jdbcUsername, "参数jdbcUsername为空.");
		Preconditions.checkNotNull(jdbcPassword, "参数jdbcPassword为空.");
		Preconditions.checkNotNull(jdbcDatabase, "参数jdbcDatabase为空.");
		Preconditions.checkNotNull(jobID, "参数jobID为空.");
		Preconditions.checkNotNull(tableName, "参数tableName为空.");
		Preconditions.checkNotNull(hiveTableName, "参数hiveTableName为空.");
		SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd");
		baseTime = df.format(new Date());
		logger.info("Database: {} {}.", jdbcDriverClassName, jdbcUrl);
		dataSource = new SingleConnectionDataSource();
		dataSource.setSuppressClose(true);
		dataSource.setDriverClassName(jdbcDriverClassName);
		dataSource.setUrl(jdbcUrl);
		dataSource.setUsername(jdbcUsername);
		dataSource.setPassword(jdbcPassword);
		databaseSupport = DatabaseSupports.getDatabaseSupport(jdbcDriverClassName);
	}

	/**
	 * 初始化目标表结构
	 * 
	 * @param etlJobConfig
	 * @return
	 * @throws Exception
	 */
	public TableMeta initTableMeta(ETLJobConfig etlJobConfig) throws Exception {
		TableMeta e = new TableMeta();
		e.setTableName(hiveTableName);
		e.setSqlTableName(tableName);
		e.setPrimaryKey(getTablePrimaryKey(tableName));
		e.setColumns(getTableColumns(tableName, queryField));
		e.setIs_partition(etlJobConfig.getIs_partition());
		e.setSyncType(etlJobConfig.getSyncType());
		e.setPartition(etlJobConfig.getPartition());
		e.setPartitionType(etlJobConfig.getPartitionType());
		e.setDbType(this.processDbType(etlJobConfig.getDataSource().getDbDriver()));
		e.setSynDB(etlJobConfig.getSynDB());
		e.setNum(etlJobConfig.getNum());
		return e;
	}

	/**
	 * RDBMS to HIVE 任务创建sqoop脚本任务 主入口
	 * 
	 * @param etlJobConfig
	 * @param username
	 * @throws Exception
	 */
	public void run(ETLJobConfig etlJobConfig, String username) throws Exception {
		initRun(etlJobConfig);
		TableMeta e = initTableMeta(etlJobConfig);
		jobShell = etlJobConfig.getJobName() + "-0-sqoop";
		if (!e.getColumns().isEmpty()) {
			createHiveMetaData(e, jdbcDriverClassName, username);
			createSqoopScript(e);
		}
		dataSource.destroy();
	}

	/**
	 * RDBMS to HIVE 任务执行sqoop脚本任务 主入口
	 * 
	 * @param etlJobConfig
	 */
	public void taskRun(ETLJobConfig etlJobConfig) {
		initRun(etlJobConfig);
		jobShell = etlJobConfig.getJobName() + "-0-sqoop-region";
		try {
			TableMeta e = initTableMeta(etlJobConfig);
			createSqoopScript(e);
			e.setSyncType(0);
		} catch (Exception e) {
			logger.info("DBToHive.taskRun.initTableMeta error!");
		}
		dataSource.destroy();
	}

	/**
	 * 数据库类型映射
	 * 
	 * @param dbDriver
	 * @return
	 */
	private String processDbType(String dbDriver) {
		if (dbDriver.contains("oracle")) {
			return "oracle";
		}
		if (dbDriver.contains("mysql")) {
			return "mysql";
		}
		if (dbDriver.contains("sqlserver")) {
			return "sqlserver";
		}
		if (dbDriver.contains("postgresql")) {
			return "postgresql";
		}
		if (dbDriver.contains("db2")) {
			return "db2";
		}
		return null;
	}

	/**
	 * 获取指定表字段及类型
	 *
	 * @throws SQLException
	 */
	private List<Pair<String, String>> getTableColumns(String tableName, String queryField) throws SQLException {
		List<Pair<String, String>> columns = Lists.newArrayList();
		String sql = databaseSupport.getSelectOne(jdbcDatabase, tableName, queryField);
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean hasData = false;
		con = dataSource.getConnection();
		stmt = con.createStatement();
		rs = stmt.executeQuery(sql);
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			String name = rsmd.getColumnName(i);
			String type = rsmd.getColumnTypeName(i);
			columns.add(Pair.of(name, type));
		}
		JdbcUtils.closeResultSet(rs);
		JdbcUtils.closeStatement(stmt);
		JdbcUtils.closeConnection(con);
		return columns;
	}

	/**
	 * 获取表的主键
	 *
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	private List<String> getTablePrimaryKey(String tableName) throws SQLException {
		List<String> pks = Lists.newArrayList();
		Connection con = null;
		ResultSet rs = null;
		con = dataSource.getConnection();
		DatabaseMetaData metadata = con.getMetaData();
		rs = metadata.getPrimaryKeys(jdbcDatabase, null, tableName);
		if (rs.next()) {
			pks.add(rs.getString("COLUMN_NAME"));
		}
		JdbcUtils.closeResultSet(rs);
		JdbcUtils.closeConnection(con);
		return pks;
	}

	/**
	 * 创建sqoop shell入口
	 * 
	 * @param tableMeta
	 * @throws Exception
	 */
	private void createSqoopScript(TableMeta tableMeta) throws Exception {
		FileOutputStream fos = new FileOutputStream(SystemConfig.SCRIPT_SQOOP_PATH + jobShell + ".sh", false);
		String script = createOneSqoopTableScript(tableMeta);
		fos.write(script.getBytes("UTF-8"));
		fos.close();
	}

	/**
	 * sqoop shell脚本 拼装
	 * 
	 * @param tableMeta
	 * @return
	 */
	private String createOneSqoopTableScript(TableMeta tableMeta) {

		StringBuilder script = new StringBuilder();
		script.append("echo \"`date`>>>>>>sqoop-import[").append(hiveTableName).append("]<<<<<<\"").append(NewLine);
		script.append("last_day=`date -d last-day +%Y-%m-%d`").append(NewLine);
		script.append("sqoop import");
		appendJdbc(script, tableMeta);
		appendQuery(script, tableMeta);
		appendSplit(script, tableMeta);
		appendOutput(script, tableMeta);
		appendOutHive(script, tableMeta);
		if (tableMeta.getIs_partition() == 0) {
			tableMeta.setSqlTableName(tableMeta.getTableName() + "_tmp");
			script.append(NewLine);
			appendHivePartition(script, tableMeta);
		}
		script.append(NewLine);
		script.append(NewLine);
		return script.toString();
	}

	/**
	 * sqoop shell拼装 hive分区表
	 * 
	 * @param script
	 * @param tableMeta
	 */
	private void appendHivePartition(StringBuilder script, TableMeta tableMeta) {
		DBToHive dbToHive = new DBToHive();
		String partitionName = dbToHive.fieldNameFilter(tableMeta.getPartition());
		String tableName = tableMeta.getSynDB() + "." + tableMeta.getTableName();
		script.append(NewLine);
		if (tableMeta.getSyncType() == 1) {
			script.append("year=`date -d last-day +%Y`;month=`date -d last-day +%m`;day=`date -d last-day +%d`;");
		}
		script.append("beeline -u jdbc:hive2://localhost:10000 -n hive -e  ' ");
		if (tableMeta.getSyncType() == 0) {
			script.append(" set hive.exec.dynamic.partition=true; ");
			script.append(" set hive.exec.dynamic.partition.mode=nonstrict; ");
			script.append(" set hive.exec.max.dynamic.partitions.pernode=5000; ");
			script.append(" set hive.exec.max.dynamic.partitions=10000; ");
			script.append(" INSERT OVERWRITE TABLE `" + tableName + "`  PARTITION (year,month,day)  ");
			script.append(" select *,year(`" + partitionName + "`) `year`,month(`" + partitionName + "`) `month`,day(`"
					+ partitionName + "`) `day` from `" + tableName + "_tmp`;");
		} else {
			script.append(" INSERT OVERWRITE TABLE `" + tableName
					+ "` PARTITION (year='${year}',month='${month}',day='${day}') ");
			script.append("select * from `" + tableName + "_tmp`;");
		}
		script.append("truncate table `" + tableName + "_tmp`;'");

	}

	/**
	 * sqoop shell拼装 query查询关系数据库字段
	 * 
	 * @param script
	 * @param tableMeta
	 */
	private void appendQuery(StringBuilder script, TableMeta tableMeta) {

		String sqlTableName = tableMeta.getSqlTableName();
		script.append(" --query \"SELECT ");
		for (int i = 0; i < tableMeta.getColumns().size(); i++) {
			Pair<String, String> column = tableMeta.getColumns().get(i);
			if ("oracle".equals(tableMeta.getDbType())) {
				String str = databaseSupport.toSqoopColumn(column.getLeft(), column.getRight());
				script.append(str);
			} else if ("mysql".equals(tableMeta.getDbType())) {
				if (handleTables.contains(tableMeta.getSqlTableName()) && handleColumns.contains(column.getLeft())) {
					script.append(databaseSupport.toSqoopColumn(column.getLeft())); // 利物浦项目定制化开发
				} else {
					script.append(databaseSupport.toSqoopColumn(column.getLeft(), column.getRight()));
				}
			} else if ("sqlserver".equals(tableMeta.getDbType())) {
				script.append(databaseSupport.toSqoopColumn(column.getLeft(), column.getRight()));
			} else if ("postgresql".equals(tableMeta.getDbType())) {
				script.append(databaseSupport.toSqoopColumn(column.getLeft(), column.getRight()));
			} else if ("db2".equals(tableMeta.getDbType())) {
				script.append(databaseSupport.toSqoopColumn(column.getLeft(), column.getRight()));
			}
			if (i < tableMeta.getColumns().size() - 1) {
				script.append(",");
			}
		}
		if ("oracle".equalsIgnoreCase(tableMeta.getDbType())) {
			script.append(" FROM ").append(jdbcDatabase + "." + sqlTableName);
		} else {
			script.append(" FROM ").append(sqlTableName);
		}
		script.append(" WHERE ");
		if (tableMeta.getSyncType() == 1) {
			if (DataSourceType.MYSQL.toString().equalsIgnoreCase(tableMeta.getDbType())) {
				if ("int".equalsIgnoreCase(tableMeta.getPartitionType())
						|| "bigint".equalsIgnoreCase(tableMeta.getPartitionType())) {
					script.append("FROM_UNIXTIME(" + tableMeta.getPartition() + ",'%Y-%m-%d') = '${last_day}' AND ");
				} else {
					script.append("DATE_FORMAT(" + tableMeta.getPartition() + ",'%Y-%m-%d') = '${last_day}' AND ");
				}
			}
			if (DataSourceType.ORACLE.toString().equalsIgnoreCase(tableMeta.getDbType())) {
				if ("char".equalsIgnoreCase(tableMeta.getPartitionType())
						|| "varchar".equalsIgnoreCase(tableMeta.getPartitionType())
						|| "varchar2".equalsIgnoreCase(tableMeta.getPartitionType())
						|| "nvarchar".equalsIgnoreCase(tableMeta.getPartitionType())
						|| "nvarchar2".equalsIgnoreCase(tableMeta.getPartitionType())) {
					script.append(tableMeta.getPartition() + " = '${last_day}' AND ");
				} else {
					script.append("TO_CHAR(" + tableMeta.getPartition() + ",'YYYY-MM-DD') = '${last_day}' AND ");
				}
			}
			if (DataSourceType.SQLSERVER.toString().equalsIgnoreCase(tableMeta.getDbType())) {
				script.append("CONVERT(varchar(10)," + tableMeta.getPartition() + ",121)='${last_day}' AND ");
			}
			if (DataSourceType.POSTGRESQL.toString().equalsIgnoreCase(tableMeta.getDbType())
					|| DataSourceType.DB2.toString().equalsIgnoreCase(tableMeta.getDbType())) {
				script.append(tableMeta.getPartition() + " LIKE '${last_day}%' AND ");
			}
			/*
			 * if ("sqlserver".equals(tableMeta.getDbType())) {
			 * script.append("CONVERT(varchar(10)," + tableMeta.getPartition() +
			 * ",121)='${last_day}' AND "); }else {
			 * script.append(tableMeta.getPartition()+" LIKE '${last_day}%' AND "); }
			 */
		}
		script.append(queryCondition + " AND \\$CONDITIONS");
		script.append("\"");
	}

	/**
	 * sqoop shell拼装 jdbc连接信息
	 * 
	 * @param script
	 * @param e
	 */
	private void appendJdbc(StringBuilder script, TableMeta e) {
		if (e.getDbType().equalsIgnoreCase("mysql")) {
			jdbcUrl += "?tinyInt1isBit=false";
		}
		script.append(" --connect '").append(jdbcUrl).append("'");
		script.append(" --username '").append(jdbcUsername).append("'");
		script.append(" --password '").append(jdbcPassword).append("'");
		// --driver com.mysql.jdbc.Driver
		script.append(" --driver '").append(jdbcDriverClassName).append("'");
	}

	/**
	 * sqoop shell拼装 指定map线程数
	 * 
	 * @param script
	 * @param tableMeta
	 */
	private void appendSplit(StringBuilder script, TableMeta tableMeta) {
		Integer num = tableMeta.getNum();
		if (num == null) {
			num = sqoopNumMappers;
		}
		if (num <= 1 || !canSplit(tableMeta)) {
			script.append(" -m ").append(1);
		}
		if (canSplit(tableMeta)) {
			script.append(" -m ").append(num);
			script.append(" --split-by ").append(tableMeta.getPrimaryKey().get(0));
		}
		/*
		 * if (canSplit(tableMeta) == false) { script.append(" -m ").append(num);
		 * script.append(" --split-by ").append(tableMeta.getPrimaryKey().get(0)); }
		 * else { script.append(" -m ").append(num);
		 * script.append(" --split-by ").append(tableMeta.getPrimaryKey().get(0)); }
		 */
	}

	private boolean canSplit(TableMeta tableMeta) {
		if (sqoopNumMappers > 1 && tableMeta.getPrimaryKey() != null && tableMeta.getPrimaryKey().size() == 1
				&& databaseSupport.isNumberType(tableMeta.getColumnType(tableMeta.getPrimaryKey().get(0)))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * sqoop shell拼装 目标路径 分隔符 存储格式
	 * 
	 * @param script
	 * @param tableMeta
	 */
	private void appendOutput(StringBuilder script, TableMeta tableMeta) {
		String tableName = tableMeta.getTableName();
		if (hdfsOutputDirDelete) {
			script.append(" --delete-target-dir");
		}
		if (tableMeta.getIs_partition() == 0) {
			tableName += "_tmp";
		}
		script.append(" --target-dir ").append(createLocation(tableName)).append(" --as-parquetfile ");
		script.append(" --fields-terminated-by \"^\" ").append(" --lines-terminated-by \"\\n\" ");

	}

	/**
	 * sqoop shell拼装 指定hive库 表
	 * 
	 * @param script
	 * @param tableMeta
	 */
	private void appendOutHive(StringBuilder script, TableMeta tableMeta) {
		String tableName = tableMeta.getTableName();
		if (tableMeta.getIs_partition() == 0) {
			tableName += "_tmp";
		}
		script.append(" --hive-import --hive-overwrite ");
		script.append(" --hive-database ").append(tableMeta.getSynDB()).append(" --hive-table ").append(tableName);
		script.append(" --null-string ").append(" '\\\\N'").append(" --null-non-string ").append(" '\\\\N'");
	}

	/**
	 * 根据不通类型数据库来判断，映射成hive表结构，创建
	 * 
	 * @param tableMeta
	 * @param jdbcDriverClassName
	 * @param username
	 * @throws Exception
	 */
	private void createHiveMetaData(TableMeta tableMeta, String jdbcDriverClassName, String username) throws Exception {

		// 封装调用hive接口的model
		HiveMetaData hiveMetaData = new HiveMetaData();
		hiveMetaData.setTabelName(tableMeta.getTableName());
		hiveMetaData.setMetaDataType("hive");
		hiveMetaData.setIs_partition(tableMeta.getIs_partition());
		hiveMetaData.setDbName(tableMeta.getSynDB());

		List<HiveMetaTableField> listMetaTableField = new ArrayList<HiveMetaTableField>();
		DatabaseSupport databaseSupport = null;
		String hiveCol = null;
		for (Pair<String, String> p : tableMeta.getColumns()) {
			HiveMetaTableField hiveMetaTableField = new HiveMetaTableField();
			if (jdbcDriverClassName.contains("mysql")) {
				databaseSupport = new MysqlDatabaseSupport();
				hiveCol = databaseSupport.toHiveColumn(p.getLeft(), p.getRight());
			} else if (jdbcDriverClassName.contains("oracle")) {
				databaseSupport = new OracleDatabaseSupport();
				hiveCol = databaseSupport.toHiveColumn(p.getLeft(), p.getRight());
			} else if (jdbcDriverClassName.contains("sqlserver")) {
				databaseSupport = new SqlServerDatabaseSupport();
				hiveCol = databaseSupport.toHiveColumn(p.getLeft(), p.getRight());
			} else if (jdbcDriverClassName.contains("db2")) {
				databaseSupport = new DB2DatabaseSupport();
				hiveCol = databaseSupport.toHiveColumn(p.getLeft(), p.getRight());
			} else if (jdbcDriverClassName.contains("postgresql")) {
				databaseSupport = new PostgresqlDatabaseSupport();
				hiveCol = databaseSupport.toHiveColumn(p.getLeft(), p.getRight());
			}
			hiveMetaTableField.setFieldName(fieldNameFilter(hiveCol.substring(0, hiveCol.lastIndexOf(" "))));
			hiveMetaTableField.setFlag(1);
			hiveMetaTableField.setFieldType(hiveCol.substring(hiveCol.lastIndexOf(" ") + 1, hiveCol.length()));
			listMetaTableField.add(hiveMetaTableField);
		}
		hiveMetaData.setBooUdp(0);
		hiveMetaData.setMetaTableField(listMetaTableField);
		new AtlasMetaManage().createHiveMeta(hiveMetaData, username);
		if (hiveMetaData.getIs_partition() == 0) {
			hiveMetaData.setIs_partition(1);
			hiveMetaData.setTabelName(hiveMetaData.getTabelName() + "_tmp");
			new AtlasMetaManage().createHiveMeta(hiveMetaData, username);
		}
	}

	/**
	 * 数据库 字段名称 关键字 timestamp 转换了 timestamp_col
	 * 
	 * @param fieldName
	 * @return
	 */
	public String fieldNameFilter(String fieldName) {
		if (equalsAnyIgnoreCase(fieldName, "timestamp")) {
			return fieldName + "_col";
		} else {
			return fieldName;
		}
	}

	private void insertRangerPolicy() {

	}

	/**
	 * hive表创建
	 * 
	 * @param tableMeta
	 * @return
	 */
	private String createOneHiveTableScript(TableMeta tableMeta) {
		StringBuilder script = new StringBuilder();
		if (hiveDropTable) {
			script.append("DROP TABLE IF EXISTS ").append(hiveTableName).append(";").append(NewLine);
		}
		// 建表语句
		script.append("CREATE EXTERNAL TABLE IF NOT EXISTS ").append(hiveTableName).append(" ( ").append(NewLine);
		for (int i = 0; i < tableMeta.getColumns().size(); i++) {
			Pair<String, String> column = tableMeta.getColumns().get(i);
			script.append("    ");
			script.append(databaseSupport.toHiveColumn(column.getLeft(), column.getRight()));
			if (i < tableMeta.getColumns().size() - 1) {
				script.append(",");
			}
			script.append(NewLine);
		}
		script.append(")").append(NewLine);
		script.append("STORED AS ").append(hiveFileType).append(";").append(NewLine);
		// 修改表的LOCATION
		script.append("ALTER TABLE ").append(hiveTableName).append(" SET LOCATION '")
				.append(createHDFSLocation(tableMeta.getTableName())).append("';").append(NewLine);
		script.append(NewLine);
		return script.toString();
	}

	private String createLocation(String tableName) {
		if (!hdfsOutputDir.endsWith("/")) {
			hdfsOutputDir += "/";
		}
		return hdfsOutputDir + jdbcDatabase.toLowerCase() + "/" + tableName.toLowerCase() + "/" + baseTime;
	}

	private String createHDFSLocation(String tableName) {
		return hdfsNameNode + createLocation(tableName);
	}

}

package com.hand.bdss.web.datasource.mydata.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.dsmp.service.etl.DBToHive;
import com.hand.bdss.dsmp.service.etl.ETLManager;
import com.hand.bdss.dsmp.util.DB2DatabaseSupport;
import com.hand.bdss.dsmp.util.HiveDatabaseSupport;
import com.hand.bdss.dsmp.util.MysqlDatabaseSupport;
import com.hand.bdss.dsmp.util.OracleDatabaseSupport;
import com.hand.bdss.dsmp.util.PostgresqlDatabaseSupport;
import com.hand.bdss.dsmp.util.SqlServerDatabaseSupport;
import com.hand.bdss.web.common.em.TaskType;
import com.hand.bdss.web.common.util.DataConnectionUtils;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.vo.DBSrcVO;
import com.hand.bdss.web.common.vo.DataSourceVO;
import com.hand.bdss.web.datamanage.metadata.service.MetaDataService;
import com.hand.bdss.web.datasource.mydata.dao.DataSourceDao;
import com.hand.bdss.web.datasource.mydata.service.DBSrcService;
import com.hand.bdss.web.datasource.mydata.service.DataSourceService;
import com.hand.bdss.web.entity.MetaDataEntity;
import com.hand.bdss.web.entity.TableEtlDO;
import com.hand.bdss.web.operationcenter.task.dao.TaskDao;

@Service
public class DataSourceServiceImpl implements DataSourceService {
	@Resource
	private DataSourceDao dataSourceDaoImpl;
	@Resource
	private DBSrcService dBSrcServiceImpl;
	@Resource
	private MetaDataService metaDataServiceImpl;
	@Resource
	TaskDao taskDaoImpl;

	private static final Logger logger = LoggerFactory.getLogger(DataSourceServiceImpl.class);

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertDataSource(DataSourceVO dataSourceVO, HttpServletRequest request) throws Exception {
		// 设置数据过滤字段
		if (dataSourceVO.getDataFilter() != null) {
			StringBuffer buffer = new StringBuffer();
			Map<String, String> map = dataSourceVO.getDataFilter();
			for (Entry<String, String> entry : map.entrySet()) {
				buffer.append(entry.getKey() + " ");
			}
			String str = buffer.substring(0, buffer.lastIndexOf(" "));
			dataSourceVO.setSyncFilelds(str);
		}
		// 设置任务类型
		dataSourceVO.setJobType(this.processJobType(dataSourceVO.getJobType()));
		// 设置同步方式
		if (dataSourceVO.getSyncType() == null || "".equals(dataSourceVO.getSyncType())) {
			dataSourceVO.setSyncType("0");
		}
		/* 数据源信息配置信息 */
		DBSrcVO dbSrcVO = getDbSrcVOById(dataSourceVO.getDataSourceId());

		/* 目标源信息配置信息 */
		DBSrcVO dbSrcTarger = getDbSrcVOByName(dataSourceVO.getSyncDB());

		/* 封装ETL数据 */
		TableEtlDO tableEtlDO = this.processTableEtlDO(dbSrcVO, dbSrcTarger, dataSourceVO);

		// 封装task任务表数据
		this.processTask(dbSrcVO, dbSrcTarger, dataSourceVO, request);

		List<TableEtlDO> tableEtlDOs = new ArrayList<>();
		tableEtlDOs.add(tableEtlDO);
		String job = objectToJson(dataSourceVO, dbSrcVO, dbSrcTarger, tableEtlDOs, "insert");

		// 加入生命周期管理
		this.insertMetaData(dataSourceVO, request);
		logger.info("insertDataSource job{}", job);

		// 调用core模块etc job 创建接口
		try {
			new ETLManager().run(job, GetUserUtils.getUser(request).getUserName());
		} catch (Exception e) {
			logger.error("调用ETLManager run error：", e);
			throw e;
		}
	}

	/**
	 * 数据源id获取源信息
	 *
	 * @param sourceID
	 * @return
	 * @throws Exception
	 */
	private DBSrcVO getDbSrcVOById(int sourceID) throws Exception {
		/* 数据源信息配置信息 */
		DBSrcVO dbSrcVO = new DBSrcVO();
		dbSrcVO.setId(sourceID);
		dbSrcVO = dBSrcServiceImpl.getDBSrc(dbSrcVO);
		return dbSrcVO;
	}

	/**
	 * 数据源名称获取源信息
	 *
	 * @param srcName
	 * @return
	 * @throws Exception
	 */
	private DBSrcVO getDbSrcVOByName(String srcName) throws Exception {
		/* 数据源信息配置信息 */
		DBSrcVO dbSrcVO = new DBSrcVO();
		dbSrcVO.setSrcName(srcName);
		dbSrcVO = dBSrcServiceImpl.getDBSrc(dbSrcVO);
		return dbSrcVO;
	}

	/**
	 * 创建的hive表添加到本地的生命周期管理中
	 *
	 * @param dataSourceVO
	 */
	@Override
	public void insertMetaData(DataSourceVO dataSourceVO, HttpServletRequest request) throws Exception {
		/* 目标源信息配置信息 */
		DBSrcVO dbSrcTarger = getDbSrcVOByName(dataSourceVO.getSyncDB());

		MetaDataEntity metaDataEntity = new MetaDataEntity();
		metaDataEntity.setDbName(dbSrcTarger.getDbName());
		metaDataEntity.setTableName(dataSourceVO.getSyncSource());
		metaDataEntity.setMetaHiveFields("");
		metaDataEntity.setMetaType("hive");
		metaDataEntity.setMetaOwner(GetUserUtils.getUser(request).getId());
		metaDataEntity.setMetaLive(-1);
		metaDataServiceImpl.insertMetaData(metaDataEntity);
	}

	/**
	 * 封装TableEtl数据
	 *
	 * @param dataSourceVO
	 * @return
	 */
	private TableEtlDO processTableEtlDO(DBSrcVO VO, DBSrcVO dbSrcTarger, DataSourceVO dataSourceVO) throws Exception {

		String syncDB = dataSourceVO.getSyncDB();// 保存原来参数
		String syncSource = dataSourceVO.getSyncSource();
		String dataTable = dataSourceVO.getDataTable();
		int srcId = dataSourceVO.getDataSourceId();

		/* 将数据插入到Etl数据库中 */
		TableEtlDO tableEtlDO = new TableEtlDO();
		tableEtlDO.setId(UUID.randomUUID().toString());
		tableEtlDO.setJobName(dataSourceVO.getJobName());
		tableEtlDO.setJobType(dataSourceVO.getJobType());

		// 保存数据源
		if ("hive".equalsIgnoreCase(dataSourceVO.getDataType())) {

			dataSourceVO.setSyncSource(dataTable);
			dataSourceVO.setSyncDB(VO.getDbName());
			// hive -> db 保存目标源
			tableEtlDO.setDataSourceId(dbSrcTarger.getId());
			dataSourceVO.setDataTable(syncSource);
			dataSourceVO.setDataSourceId(dbSrcTarger.getId());
		} else {
			// db -> hive 保存数据源
			dataSourceVO.setSyncDB(dbSrcTarger.getDbName());
			tableEtlDO.setDataSourceId(VO.getId());
			// dataSourceVO.setDataTable(dataTable);
		}
		// dataSourceVO转换json
		tableEtlDO.setJobConfig(JsonUtils.toJson(dataSourceVO));

		// 还原原信息
		dataSourceVO.setSyncDB(syncDB);
		dataSourceVO.setSyncSource(syncSource);
		dataSourceVO.setDataTable(dataTable);
		dataSourceVO.setDataSourceId(srcId);
		/* 准备数据发送到core模块生成job -start */
		dataSourceDaoImpl.insertData(tableEtlDO);

		return tableEtlDO;
	}

	/**
	 * 存储TableEtl数据
	 *
	 * @param dataSourceVO
	 * @return
	 */
	private void processTask(DBSrcVO dbSrcVO, DBSrcVO dbSrcTarger, DataSourceVO dataSourceVO,
			HttpServletRequest request) throws Exception {
		/* 将数据插入到Task数据库中 */
		Task task = new Task();
		task.setAccount(Long.valueOf(GetUserUtils.getUser(request).getId()));
		if (StringUtils.isNotBlank(dataSourceVO.getSyncFilelds())) {
			task.setFieldMapping(dataSourceVO.getSyncFilelds());
		}
		task.setFilterCondition(dataSourceVO.getUdc());
		task.setTargetDB(dbSrcTarger.getDbName());
		task.setTargetTable(dataSourceVO.getSyncSource());
		task.setTaskName(dataSourceVO.getJobName());
		task.setTaskType(dataSourceVO.getJobType());
		task.setRemarks(dataSourceVO.getRemark());
		task.setSourceDb(dbSrcVO.getDbName());
		task.setSourceTable(dataSourceVO.getDataTable());
		task.setNum(dataSourceVO.getNum());
		task.setStatus("0");
		task.setSqlType("sqoop");
		task.setDataType(dataSourceVO.getDataType());
		if (null != dataSourceVO.getIs_partition() && "0".equals(dataSourceVO.getIs_partition())) {
			task.setPartition(dataSourceVO.getPartition());
			task.setPartitionType(dataSourceVO.getPartitionType());
		}

		// 存入task库
		taskDaoImpl.insert(task);
	}

	/**
	 * 关系型数据库 -> hive 表字段类型转换
	 */
	@Override
	public Map<String, String> DBToBDCaseTableType(Map<String, String> map, String dataType) throws Exception {
		Map<String, String> maps = new HashMap<>();
		DBToHive dbTohive = new DBToHive();
		if ("mysql".equals(dataType.toLowerCase())) {
			MysqlDatabaseSupport mysql = new MysqlDatabaseSupport();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String columnName = entry.getKey();
				dbTohive.fieldNameFilter(columnName);
				String columnType = entry.getValue();
				String returnColumnType = mysql.toHiveColumn(columnName, columnType);
				String[] columns = returnColumnType.split(" ");
				maps.put(dbTohive.fieldNameFilter(columnName), columns[1]);
			}
		}
		if ("oracle".equals(dataType.toLowerCase())) {
			OracleDatabaseSupport oracle = new OracleDatabaseSupport();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String columnName = entry.getKey();
				dbTohive.fieldNameFilter(columnName);
				String columnType = entry.getValue();
				String returnColumnType = oracle.toHiveColumn(columnName, columnType);
				String[] columns = returnColumnType.split(" ");
				maps.put(dbTohive.fieldNameFilter(columnName), columns[1]);
			}
		}
		if ("sqlserver".equals(dataType.toLowerCase())) {
			SqlServerDatabaseSupport sqlserver = new SqlServerDatabaseSupport();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String columnName = entry.getKey();
				dbTohive.fieldNameFilter(columnName);
				String columnType = entry.getValue();
				String returnColumnType = sqlserver.toHiveColumn(columnName, columnType);
				String[] columns = returnColumnType.split(" ");
				maps.put(dbTohive.fieldNameFilter(columnName), columns[1]);
			}
		}
		if ("db2".equals(dataType.toLowerCase())) {
			DB2DatabaseSupport db2 = new DB2DatabaseSupport();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String columnName = entry.getKey();
				dbTohive.fieldNameFilter(columnName);
				String columnType = entry.getValue();
				String returnColumnType = db2.toHiveColumn(columnName, columnType);
				String[] columns = returnColumnType.split(" ");
				maps.put(dbTohive.fieldNameFilter(columnName), columns[1]);
			}
		}
		if ("postgresql".equals(dataType.toLowerCase())) {
			PostgresqlDatabaseSupport postgres = new PostgresqlDatabaseSupport();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String columnName = entry.getKey();
				String columnType = entry.getValue();
				String returnColumnType = postgres.toHiveColumn(columnName, columnType);
				String[] columns = returnColumnType.split(" ");
				maps.put(dbTohive.fieldNameFilter(columnName), columns[1]);
			}
		}

		return maps;
	}

	/**
	 * hive -> 关系型数据库 表字段类型转换
	 */
	@Override
	public Map<String, String> BDToDBCaseTableType(Map<String, String> map, String dataType) throws Exception {
		Map<String, String> maps = new HashMap<>();
		HiveDatabaseSupport support = new HiveDatabaseSupport();
		if ("mysql".equals(dataType.toLowerCase())) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String columnName = entry.getKey();
				String columnType = entry.getValue();
				String returnColumnType = support.toMysqlColumn(columnName, columnType);
				// 去除包含的表名
				if (columnName.contains(".")) {
					columnName = columnName.substring(columnName.indexOf(".") + 1);
				}
				logger.info("BDToDBCaseTableType returnColumnType {}", returnColumnType);
				String[] columns = returnColumnType.split(" ");
				maps.put(columnName, columns[1]);
			}
		}
		if ("oracle".equals(dataType.toLowerCase())) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String columnName = entry.getKey();
				String columnType = entry.getValue();
				// 去除包含的表名
				if (columnName.contains(".")) {
					columnName = columnName.substring(columnName.lastIndexOf(".") + 1);
				}
				String returnColumnType = support.toOracleColumn(columnName, columnType);
				logger.info("BDToDBCaseTableType returnColumnType {}", returnColumnType);
				String[] columns = returnColumnType.split(" ");
				maps.put(columnName, columns[1]);
			}
		}
		if ("sqlserver".equals(dataType.toLowerCase())) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String columnName = entry.getKey();
				String columnType = entry.getValue();
				// 去除包含的表名
				if (columnName.contains(".")) {
					columnName = columnName.substring(columnName.lastIndexOf(".") + 1);
				}
				String returnColumnType = support.toSQLServerColumn(columnName, columnType);
				logger.info("BDToDBCaseTableType returnColumnType {}", returnColumnType);
				String[] columns = returnColumnType.split(" ");
				maps.put(columnName, columns[1]);
			}
		}
		if ("db2".equals(dataType.toLowerCase())) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String columnName = entry.getKey();
				String columnType = entry.getValue();
				// 去除包含的表名
				if (columnName.contains(".")) {
					columnName = columnName.substring(columnName.lastIndexOf(".") + 1);
				}
				String returnColumnType = support.toDB2Column(columnName, columnType);
				logger.info("BDToDBCaseTableType returnColumnType {}", returnColumnType);
				String[] columns = returnColumnType.split(" ");
				maps.put(columnName, columns[1]);
			}
		}
		if ("postgresql".equals(dataType.toLowerCase())) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String columnName = entry.getKey();
				String columnType = entry.getValue();
				// 去除包含的表名
				if (columnName.contains(".")) {
					columnName = columnName.substring(columnName.lastIndexOf(".") + 1);
				}
				String returnColumnType = support.toPostgreSQLColumn(columnName, columnType);
				logger.info("BDToDBCaseTableType returnColumnType {}", returnColumnType);
				String[] columns = returnColumnType.split(" ");
				maps.put(columnName, columns[1]);
			}
		}
		return maps;
	}

	@Override
	public List<TableEtlDO> getAzkabanJobName(TableEtlDO tableEtlDO) throws Exception {
		List<TableEtlDO> tableEtl = dataSourceDaoImpl.getAzkabanJobName(tableEtlDO);
		return tableEtl;
	}

	@Override
	public Map<String, String> getFieldsMsg(String tableName, String resourceBaseId) throws Exception {
		DBSrcVO dbSrcVO = new DBSrcVO();
		dbSrcVO.setId(Integer.parseInt(resourceBaseId));
		Map<String, String> map;
		List<DBSrcVO> lists = dBSrcServiceImpl.listDBSrcs(dbSrcVO, 0, 10);
		DBSrcVO dbSrc = lists.get(0);
		map = DataConnectionUtils.getTablesDescript(dbSrc, tableName);
		return map;
	}

	@Override
	public String getHiveTableName(String dbName, String tableName) throws Exception {
		tableName = tableName + "_hive";
		if (checkTable(dbName, tableName)) {
			getHiveTableName(dbName, tableName);
		}
		return tableName;
	}

	@Override
	public boolean checkTable(String srcName, String tableName) throws Exception {
		if (StringUtils.isEmpty(srcName) || StringUtils.isEmpty(tableName)) {
			return false;
		}
		DBSrcVO vo = new DBSrcVO();
		vo.setSrcName(srcName);
		vo = dBSrcServiceImpl.getDBSrc(vo);
		// 查询数据源所有表
		List<String> tableList = DataConnectionUtils.getTablesName(vo);
		if (tableList != null && tableList.size() > 0) {
			for (String str : tableList) {
				if (tableName.equals(str)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getTableSyncTaskName(String hiveTableName) throws Exception {
		hiveTableName = hiveTableName + "_task";
		TableEtlDO tableEtlDO = new TableEtlDO();
		tableEtlDO.setJobName(hiveTableName);
		List<TableEtlDO> tableEtl = getAzkabanJobName(tableEtlDO);
		if (tableEtl.size() > 0) {
			getTableSyncTaskName(hiveTableName);
		}
		return hiveTableName;
	}

	/**
	 * 将对象封装成json传递给core模块生成对应的job--用于job的update和create
	 *
	 * @param dataSourceVO
	 * @param dbSrcVO
	 * @param operationType
	 * @return
	 */

	private String objectToJson(DataSourceVO dataSourceVO, DBSrcVO dbSrcVO, DBSrcVO dbSrcTarger,
			List<TableEtlDO> tableEtlDOs, String operationType) throws Exception {
		List<Map<String, Object>> listMap = new ArrayList<>();

		if (tableEtlDOs != null && tableEtlDOs.size() > 0) {
			Map<String, Object> dataSource = new HashMap<>();// 存放关系型数据库信息
			// 解析DBSrcVO信息
			dbSrcVO = this.processDBSrcVO(dbSrcVO);
			// 解析dbSrcTarger
			dbSrcTarger = this.processDBSrcVO(dbSrcTarger);
			// 封装dataSource信息
			this.processDataSource(dataSource, dbSrcVO, dbSrcTarger, dataSourceVO);

			for (TableEtlDO tableEtlDO : tableEtlDOs) {
				Map<String, Object> hashMap = new HashMap<>();
				Map<String, Object> etlConfig = new HashMap<>();// 存放hive数据库信息

				// 封装etl信息
				this.processEtlConfig(etlConfig, dbSrcVO, dbSrcTarger, dataSourceVO, tableEtlDO);

				if ("insert".equals(operationType)) {
					hashMap.put("action", "0");
				}
				if ("delete".equals(operationType)) {
					hashMap.put("action", "1");
				}
				if ("update".equals(operationType)) {
					hashMap.put("action", "2");
				}
				hashMap.put("dataSource", dataSource);
				hashMap.put("etlConfig", etlConfig);
				listMap.add(hashMap);
			}
		}
		return JsonUtils.toJson(listMap);
	}

	/**
	 * 封装DataSource信息 关系型数据库信息
	 *
	 * @param dataSource
	 * @param dbSrcFrom
	 * @param dataSourceVO
	 */
	private void processDataSource(Map<String, Object> dataSource, DBSrcVO dbSrcFrom, DBSrcVO dbSrcTarger,
			DataSourceVO dataSourceVO) throws Exception {

		if ("hive".equalsIgnoreCase(dataSourceVO.getDataType())) {
			dataSource.put("id", dbSrcTarger.getId());
			dataSource.put("dbName", dbSrcTarger.getDbName());
			dataSource.put("dbUser", dbSrcTarger.getDbUser());
			dataSource.put("dbPwd", dbSrcTarger.getDbPwd());
			dataSource.put("dbDriver", dbSrcTarger.getDbDriver());
			dataSource.put("tableName", dataSourceVO.getSyncSource());
			dataSource.put("dbUrl", dbSrcTarger.getDbUrl());
			dataSource.put("dbType", dbSrcTarger.getDbType());
		} else {
			dataSource.put("id", dbSrcFrom.getId());
			dataSource.put("dbName", dbSrcFrom.getDbName());
			dataSource.put("dbUser", dbSrcFrom.getDbUser());
			dataSource.put("dbPwd", dbSrcFrom.getDbPwd());
			dataSource.put("dbDriver", dbSrcFrom.getDbDriver());
			dataSource.put("tableName", dataSourceVO.getDataTable());
			dataSource.put("dbUrl", dbSrcFrom.getDbUrl());
			dataSource.put("dbType", dbSrcFrom.getDbType());

			dataSource.put("queryField", dataSourceVO.getSyncFilelds());// 字段名
			dataSource.put("queryCondition", dataSourceVO.getUdc());// 条件
		}
	}

	/**
	 * 封装etlConfig信息
	 *
	 * @param dataSourceVO
	 * @param tableEtlDO
	 * @return
	 */
	private void processEtlConfig(Map<String, Object> etlConfig, DBSrcVO dbSrcFrom, DBSrcVO dbSrcTarger,
			DataSourceVO dataSourceVO, TableEtlDO tableEtlDO) {

		etlConfig.put("jobId", tableEtlDO.getId());// 任务
		etlConfig.put("jobName", tableEtlDO.getJobName());// 名称
		etlConfig.put("dataType", dataSourceVO.getDataType());// 数据源类型
		etlConfig.put("num", dataSourceVO.getNum());//任务并发度

		if ("hive".equalsIgnoreCase(dataSourceVO.getDataType())) {

			etlConfig.put("syncDB", dbSrcFrom.getDbName());// 库名
			etlConfig.put("syncSource", dataSourceVO.getDataTable());
			etlConfig.put("dataTable", dataSourceVO.getSyncSource());

		} else {

			etlConfig.put("syncType", dataSourceVO.getSyncType());// 是否增量
			etlConfig.put("is_partition", dataSourceVO.getIs_partition());// 是否分区
			etlConfig.put("partition", dataSourceVO.getPartition());// 分区字段
			etlConfig.put("partitionType", dataSourceVO.getPartitionType());//分区字段的类型

			etlConfig.put("syncDB", dbSrcTarger.getDbName());// 库名
			etlConfig.put("dataTable", dataSourceVO.getDataTable());// 数据源表名
			etlConfig.put("syncSource", dataSourceVO.getSyncSource());
		}
	}

	/**
	 * 解析dbSrcVo信息
	 *
	 * @param dbSrcVO
	 * @return
	 */
	private DBSrcVO processDBSrcVO(DBSrcVO dbSrcVO) {
		logger.info("processDBSrcVO param: dbType {}", dbSrcVO.getDbType());
		if ("MySQL".equalsIgnoreCase(dbSrcVO.getDbType())) {
			dbSrcVO.setDbUrl("jdbc:mysql://" + dbSrcVO.getDbUrl() + "/" + dbSrcVO.getDbName());
		}
		if ("Oracle".equalsIgnoreCase(dbSrcVO.getDbType())) {
			dbSrcVO.setDbUrl("jdbc:oracle:thin:@" + dbSrcVO.getDbUrl() + ":" + dbSrcVO.getDbName());
		}
		if ("SQLServer".equalsIgnoreCase(dbSrcVO.getDbType())) {
			dbSrcVO.setDbUrl("jdbc:sqlserver://" + dbSrcVO.getDbUrl() + ";DatabaseName=" + dbSrcVO.getDbName());
		}
		if ("db2".equalsIgnoreCase(dbSrcVO.getDbType())) {
			dbSrcVO.setDbUrl("jdbc:db2://" + dbSrcVO.getDbUrl() + "/" + dbSrcVO.getDbName());
		}
		if ("PostgreSQL".equalsIgnoreCase(dbSrcVO.getDbType())) {
			dbSrcVO.setDbUrl("jdbc:postgresql://" + dbSrcVO.getDbUrl() + "/" + dbSrcVO.getDbName());
		}
		if ("Hive".equalsIgnoreCase(dbSrcVO.getDbType())) {
			dbSrcVO.setDbUrl("jdbc:hive2://" + dbSrcVO.getDbUrl() + "/" + dbSrcVO.getDbName());
		}
		return dbSrcVO;
	}

	/**
	 * 解析中文任务类型
	 *
	 * @param jobType
	 * @return
	 */
	private String processJobType(String jobType) {
		// 设置任务类型
		for (TaskType type : TaskType.values()) {
			if (type.getName().equals(jobType)) {
				return type.getIndex().toString();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		// System.out.println(processJobType("同步任务"));
		String columnName = "111.222";
		columnName = columnName.substring(columnName.lastIndexOf(".") + 1);
		System.out.println(columnName);
	}

}

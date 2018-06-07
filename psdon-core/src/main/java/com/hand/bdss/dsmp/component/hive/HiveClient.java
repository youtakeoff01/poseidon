package com.hand.bdss.dsmp.component.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hand.bdss.dsmp.component.Component;
import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.exception.DataServiceException;
import com.hand.bdss.dsmp.model.HiveMetaData;
import com.hand.bdss.dsmp.model.HiveMetaTableField;

public class HiveClient extends Component {

	private static final Logger logger = LoggerFactory.getLogger(HiveClient.class);

	private Connection con = null;
	private Statement stmt = null;
	private String username;

	public HiveClient() {
	}

	public HiveClient(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public void init() throws DataServiceException {
		logger.info("HiveClient.init start init hive connection");
		getConnection();
		logger.info("HiveClient.init connection init successful");
	}

	@Override
	public Connection getConnection() throws DataServiceException {
		try {
			Class.forName(SystemConfig.driverName);
			con = DriverManager.getConnection(SystemConfig.baseUrl, username, "");
			stmt = con.createStatement();
		} catch (Exception e) {
			logger.error("HiveClient.getConnection error!", e);
		}
		return con;
	}

	@Override
	public void closeConnection() {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				logger.error("HiveClient.closeConnection stmt error!", e);
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				logger.error("HiveClient.closeConnection conn error!", e);
			}
		}
		stmt = null;
		con = null;
	}

	public Boolean createHiveTable(HiveMetaData hiveMetaData) throws Exception {

		Boolean flag = false;
		String tableName = hiveMetaData.getDbName() + "." + hiveMetaData.getTabelName();
		String format = "ROW FORMAT DELIMITED FIELDS TERMINATED BY '^' ";
		StringBuffer parameter = new StringBuffer();
		List<HiveMetaTableField> list = hiveMetaData.getMetaTableField();
		for (HiveMetaTableField hmtf : list) {
			parameter.append("`" + hmtf.getFieldName() + "` ");
			parameter.append(hmtf.getFieldType() + " ");
			if (StringUtils.isNotBlank(hmtf.getFieldDescribe())) {
				parameter.append("comment '");
				parameter.append(hmtf.getFieldDescribe());
				parameter.append("'");
			}
			parameter.append(",");
		}
		String para = parameter.toString().substring(0, parameter.length() - 1);
		if (con == null || stmt == null) {
			init();
		}
		String sql = "";
		if (hiveMetaData.getIsExternal() == 1) {
			sql = "create external table if not exists `";
			format = "ROW FORMAT DELIMITED FIELDS TERMINATED BY ','  STORED AS TEXTFILE ";
		} else {
			sql = "create table if not exists `";
			format += " STORED AS PARQUET ";
		}
		sql = sql + tableName + "`(" + para + ") ";
		if (StringUtils.isNotBlank(hiveMetaData.getMetaDataDescribe())) {
			sql += "comment '" + hiveMetaData.getMetaDataDescribe() + "' ";
		}
		if (hiveMetaData.getIs_partition() == 0) {
			String format_partiton = " partitioned by (year string,month string,day string) " + format;
			sql += format_partiton;
		} else {
			sql += format;
		}
		if (StringUtils.isNotBlank(hiveMetaData.getLocation())) {
			sql += " location '" + hiveMetaData.getLocation() + "'";
		}
		try {
			logger.info("createSql:" + sql);
			stmt.execute(sql);
			logger.info("HiveClient.createHiveTable create table " + tableName + " is success!");
			flag = true;
		} catch (SQLException e) {
			flag = false;
			logger.error("HiveClient.createHiveTable error!", e);
		} finally {
			closeConnection();
		}
		return flag;
	}

	/**
	 * 创建hive自定义分区表
	 * 
	 * @param hiveMetaData
	 * @return
	 * @throws Exception
	 */
	public Boolean createHivePartitionTable(HiveMetaData hiveMetaData) throws Exception {
		Boolean flag = false;
		Boolean is_partition = false;
		String tableName = hiveMetaData.getDbName() + "." + hiveMetaData.getTabelName();
		// String tableDesc = hiveMetaData.getMetaDataDescribe();
		String format = "ROW FORMAT DELIMITED FIELDS TERMINATED BY '^' ";
		StringBuffer parameter = new StringBuffer();
		StringBuffer partiton_column = new StringBuffer();
		List<HiveMetaTableField> list = hiveMetaData.getMetaTableField();
		for (HiveMetaTableField hmtf : list) {
			String fieldType = hmtf.getFieldType();
			String fieldName = hmtf.getFieldName();
			String fieldDesc = hmtf.getFieldDescribe();
			int partition_flag = hmtf.getFlag();
			if (partition_flag == 0) {
				partiton_column.append("`" + fieldName + "` " + fieldType).append(" comment ").append("'")
						.append(fieldDesc).append("'").append(",");
				is_partition = true;
			} else {
				parameter.append("`" + fieldName + "` " + fieldType).append(" comment ").append("'").append(fieldDesc)
						.append("'").append(",");
			}
		}
		String para = null;
		if (StringUtils.isNotBlank(parameter)) {
			para = parameter.toString().substring(0, parameter.length() - 1);
		}
		if (con == null || stmt == null) {
			init();
		}
		String sql = "";
		if (hiveMetaData.getIsExternal() == 1) {
			sql = "create external table if not exists `";
			format += " STORED AS TEXTFILE ";
		} else {
			sql = "create table if not exists `";
			format += " STORED AS PARQUET ";
		}
		sql = sql + tableName + "`(" + para + ") ";
		if (StringUtils.isNotBlank(hiveMetaData.getMetaDataDescribe())) {
			sql += "comment '" + hiveMetaData.getMetaDataDescribe() + "' ";
		}
		if (is_partition) {
			String column = partiton_column.toString().substring(0, partiton_column.length() - 1);
			String format_partiton = " partitioned by (" + column + ") " + format;
			sql += format_partiton;
		} else {
			sql += format;
		}

		if (StringUtils.isNotBlank(hiveMetaData.getLocation())) {
			sql += " location '" + hiveMetaData.getLocation() + "'";
		}
		try {
			logger.info("createSql" + sql);
			stmt.execute(sql);
			logger.info("HiveClient.createHivePartitionTable create table " + tableName + " is success!");
			flag = true;
		} catch (SQLException e) {
			flag = false;
			logger.error("", e);
		} finally {
			closeConnection();
		}
		return flag;
	}

	/**
	 * 修改表的描述信息、字段名和字段类型
	 *
	 * @param hiveMetaData
	 * @return
	 * @throws DataServiceException
	 */
	public Boolean updateHiveTable(HiveMetaData hiveMetaData) throws DataServiceException {
		String tableName = hiveMetaData.getDbName() + "." + hiveMetaData.getTabelName();
		List<HiveMetaTableField> filedType = hiveMetaData.getMetaTableField();
		boolean flag = false;
		StringBuffer sb = new StringBuffer();

		// 增加列
		if (filedType != null && filedType.size() > 0) {
			for (HiveMetaTableField htf : filedType) {
				sb.append(htf.getFieldName());
				sb.append(" ");
				sb.append(htf.getFieldType());
				sb.append(" COMMENT '");
				sb.append(htf.getFieldDescribe());
				sb.append("',");
			}
		}

		String sql = "";

		if (con == null || stmt == null) {
			init();
		}
		if (StringUtils.isNotBlank(tableName)) {
			try {
				// 增加列
				if (sb.length() > 0) {
					sql = "ALTER TABLE " + tableName + " ADD COLUMNS(" + sb.substring(0, sb.length() - 1) + ")";
					stmt.executeUpdate(sql);
				}

				flag = true;
			} catch (SQLException e) {
				flag = false;
				logger.error("HiveClient.executeUpdate error!", e);
			} finally {
				closeConnection();
			}
		}
		return flag;
	}

	/**
	 * 删除hive表
	 *
	 * @param
	 * @return
	 * @throws DataServiceException
	 */
	public Boolean dropHiveTable(HiveMetaData hiveMetaData) throws DataServiceException {
		if (con == null || stmt == null) {
			init();
		}
		try {
			if (StringUtils.isNotBlank(hiveMetaData.getDbName())
					&& StringUtils.isNotBlank(hiveMetaData.getTabelName())) {
				String sql = "DROP TABLE IF EXISTS " + hiveMetaData.getDbName() + "." + hiveMetaData.getTabelName();
				stmt.execute(sql);
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			logger.error("HiveClient.dropHiveTable error!", e);
			return false;
		} finally {
			closeConnection();
		}
	}

	/**
	 * 要删除的表名列表
	 *
	 * @param list
	 * @return
	 * @throws DataServiceException
	 */
	public Boolean dropHiveTable(List<HiveMetaData> list) throws DataServiceException {
		if (list.isEmpty() || list == null)
			return false;
		if (con == null || stmt == null) {
			init();
		}
		long currentTimeMillis = System.currentTimeMillis();
		StringBuffer tempSql = new StringBuffer();
		try {
			for (HiveMetaData hiveMetaData : list) {
				String tableName = hiveMetaData.getDbName() + "." + hiveMetaData.getTabelName();
				tempSql.append("alter table ").append(tableName).append(" rename to ").append(tableName)
						.append("_user_delete_").append(currentTimeMillis);
				stmt.execute(tempSql.toString());
			}
			return true;
		} catch (SQLException e) {
			if (10001 == e.getErrorCode() && e.getMessage().contains("Table not found")) {
				return true;
			}
			logger.error("HiveClient.dropHiveTable error!", e);
			return false;
		} finally {
			closeConnection();
		}
	}

	/*
	 * 获取所有数据库名
	 */
	public List<String> getHiveDatabases() throws DataServiceException {
		List<String> dbNames = new ArrayList<String>();
		try {
			Class.forName(SystemConfig.HIVE_MD_DRIVERNAME);
			con = DriverManager.getConnection(SystemConfig.HIVE_MD_BASEURL, SystemConfig.HIVE_MD_USERNAME,
					SystemConfig.HIVE_MD_PASSWORD);
			stmt = con.createStatement();
			String sql = "select NAME from DBS ";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				dbNames.add(rs.getString("NAME"));
			}
		} catch (Exception e1) {
			logger.error("HiveClient.getHiveDatabases error!", e1);
		} finally {
			closeConnection();
		}
		return dbNames;
	}

	/**
	 * 获取hive指定数据库下所有表名
	 * 
	 * @return
	 * @throws DataServiceException
	 * @throws SQLException
	 */
	public List<String> getHiveTables(String dbName) throws DataServiceException {
		if (dbName != null && !"".equals(dbName)) {
			changeDB(dbName);
		}
		String sql = "show tables";
		ResultSet res;
		List<String> lists = null;
		try {
			res = stmt.executeQuery(sql);
			lists = new ArrayList<>();
			while (res.next()) {
				lists.add(res.getString(1));
			}
		} catch (SQLException e) {
			logger.error("HiveClient.getHiveTables error!", e);
		} finally {
			closeConnection();
		}
		return lists;
	}

	public void changeDB(String dbName) throws DataServiceException {
		String sql = "use " + dbName;
		try {
			init();
			stmt.execute(sql);
		} catch (SQLException e) {
			logger.error("HiveClient.changeDB error!", e);
		}
	}

	/**
	 * 获取hive指定数据库下指定hive表结构信息
	 * 
	 * @param dbName
	 * @param tableName
	 * @return
	 */
	public List<HiveMetaTableField> getHiveMetaTableField(String dbName, String tableName) throws DataServiceException {

		List<HiveMetaTableField> list = new ArrayList<HiveMetaTableField>();
		HiveMetaTableField htf = null;
		if (dbName != null && !"".equals(dbName)) {
			changeDB(dbName);
		}
		try {
			ResultSet result = stmt.executeQuery("desc " + tableName);
			while (result.next()) {
				htf = new HiveMetaTableField();
				if (StringUtils.isBlank(result.getString(1))) {
					break;
				}
				htf.setFieldName(result.getString(1));
				htf.setFieldType(result.getString(2));
				htf.setFieldDescribe(result.getString(3));
				list.add(htf);
			}
		} catch (Exception e) {
			logger.error("HiveClient.getHiveTableFields error!", e);
		} finally {
			closeConnection();
		}
		return list;
	}

	/**
	 * 获取hive指定数据库下指定hive表的所有字段
	 * 
	 * @param dbName
	 * @param tableName
	 * @return
	 */
	public Map<String, String> getHiveTableFields(String dbName, String tableName) throws DataServiceException {
		if (dbName != null && !"".equals(dbName)) {
			changeDB(dbName);
		}
		String sql = "select * from " + tableName;
		Map<String, String> map = new IdentityHashMap<String, String>();
		ResultSet result;
		try {
			result = stmt.executeQuery(sql);
			ResultSetMetaData meta = result.getMetaData();
			for (int i = 1, len = meta.getColumnCount() + 1; i < len; i++) {
				map.put(meta.getColumnName(i), meta.getColumnTypeName(i));
			}
		} catch (Exception e) {
			logger.error("HiveClient.getHiveTableFields error!", e);
		} finally {
			closeConnection();
		}
		return map;
	}

	/*
	 * 创建数据库
	 * 
	 * @param databaseName
	 * 
	 * @return
	 */
	public Boolean createDatabase(String databaseName) throws DataServiceException {
		init();
		boolean flag = false;
		String sql = "create database " + databaseName;
		int rs;
		try {
			rs = stmt.executeUpdate(sql);
			if (rs == 0) {

				flag = true;
			}
		} catch (SQLException e) {
			logger.error("HiveClient.createDatabase error!", e);
		} finally {
			closeConnection();
		}
		return flag;
	}

	/**
	 * 校验hive数据库是否存在
	 * 
	 * @param databaseName
	 * @return
	 * @throws DataServiceException
	 */
	public Boolean checkDatabaseName(String databaseName) throws DataServiceException {
		init();
		boolean flag = false;
		try {
			List<String> dbNames = getHiveDatabases();
			if (dbNames.contains(databaseName)) {
				flag = true;
			}
		} catch (Exception e) {
			logger.error("checkDatabaseName error!", e);
		} finally {
			closeConnection();
		}
		return flag;
	}

	public Boolean checkHiveTableName(String tableName) {
		return null;
	}

}

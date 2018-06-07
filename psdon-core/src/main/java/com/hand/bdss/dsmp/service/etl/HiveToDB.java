package com.hand.bdss.dsmp.service.etl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.hand.bdss.dsmp.component.hive.HiveClient;
import com.hand.bdss.dsmp.model.*;
import com.hand.bdss.dsmp.util.*;
import com.hand.bdss.task.config.SystemConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.JdbcUtils;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * HIVE TO RDBMS 数据集成 -》数据同步
 */

public class HiveToDB {

    private static final Logger logger = LoggerFactory.getLogger(HiveToDB.class);

    private static String NewLine = "\n";

    private String jdbcDriverClassName;

    private String jdbcUrl;

    private String jdbcUsername;

    private String jdbcPassword;

    private String jdbcDatabase;

    private String jobID;

    private String tableName;//DB表名

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
    HiveDatabaseSupport h = new HiveDatabaseSupport();

    /**
     * 初始化etlJobConfig数据 获取jdbc信息 数据源库表信息
     * @param etlJobConfig
     */
    private void initRun(ETLJobConfig etlJobConfig) {
        jdbcDriverClassName = etlJobConfig.getDataSource().getDbDriver();
        jdbcUrl = etlJobConfig.getDataSource().getDbUrl();
        jdbcUsername = etlJobConfig.getDataSource().getDbUser();
        jdbcPassword = etlJobConfig.getDataSource().getDbPwd();
        jdbcDatabase = etlJobConfig.getDataSource().getDbName();
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
     * 初始化目标表 RDBMS信息
     * @param etlJobConfig
     * @return
     * @throws Exception
     */
    public TableMeta initTableMeta(ETLJobConfig etlJobConfig) throws Exception {
        TableMeta e = new TableMeta();
        e.setTableName(hiveTableName);
        e.setSqlTableName(tableName);
        e.setColumns(getTableColumns(etlJobConfig.getSynDB(), hiveTableName));
        e.setIs_partition(etlJobConfig.getIs_partition());
        e.setSyncType(etlJobConfig.getSyncType());
        e.setPartition(etlJobConfig.getPartition());
        e.setDbType(this.processDbType(etlJobConfig.getDataSource().getDbDriver()));
        e.setSynDB(etlJobConfig.getSynDB());
        return e;
    }

    /**
     * HIVE to RDBMS 任务创建sqoop脚本任务 主入口
     * @param etlJobConfig
     * @throws Exception
     */
    public void run(ETLJobConfig etlJobConfig) throws Exception {
        initRun(etlJobConfig);
        TableMeta e = initTableMeta(etlJobConfig);
        jobShell = etlJobConfig.getJobName() + "-0-sqoop";
        if (!e.getColumns().isEmpty()) {
            createRdbmsTable(e, jdbcDriverClassName);
            createSqoopScript(e);
        }
        dataSource.destroy();
    }

    /**
     * HIVE to RDBMS 任务执行sqoop脚本任务 主入口
     * @param etlJobConfig
     */
    public void taskRun(ETLJobConfig etlJobConfig) {
        initRun(etlJobConfig);
        jobShell = etlJobConfig.getJobName() + "-0-sqoop-region";
        try {
            TableMeta e = initTableMeta(etlJobConfig);
            createSqoopScript(e);
        } catch (Exception e) {
            logger.info("HiveToDB.taskRun.initTableMeta error!");
        }
        dataSource.destroy();
    }

    /**
     * 数据库类型映射
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
        if(dbDriver.contains("db2")){
            return "db2";
        }
		return null;
    }

    /**
     * 获取hive表字段及类型
     * @param hiveDB
     * @param tableName
     * @return
     * @throws SQLException
     */
    public List<Pair<String, String>> getTableColumns(String hiveDB, String tableName) throws SQLException {
        List<Pair<String, String>> columns = Lists.newArrayList();
        String sql = h.getSelectOne(hiveDB,tableName);
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean hasData = false;
        try {
            con = new HiveClient(com.hand.bdss.dsmp.config.SystemConfig.userName).getConnection();
            stmt = con.createStatement();
        }catch (Exception e) {
            logger.error("HiveToDB.getTableColumns hive link failure!");
        }
        rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                String name = rsmd.getColumnName(i).split("\\.")[1];
                String type = rsmd.getColumnTypeName(i);
                columns.add(Pair.of(name, type));
            }
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
        return columns;
    }

    /**
     * 创建sqoop shell入口
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
     * @param tableMeta
     * @return
     */
    private String createOneSqoopTableScript(TableMeta tableMeta) {

        StringBuilder script = new StringBuilder();
        script.append("echo \"`date`>>>>>>sqoop-export[")
                .append(hiveTableName)
                .append("]<<<<<<\"")
                .append(NewLine);
        script.append("sqoop export");
        appendJdbc(script,tableMeta);
        appendOutHive(script, tableMeta);
        script.append(NewLine);
        return script.toString();
    }

    /**
     * sqoop shell脚本 jdbc连接信息
     * @param script
     * @param tableMeta
     */
    private void appendJdbc(StringBuilder script,TableMeta tableMeta) {
        script.append(" --connect '").append(jdbcUrl).append("'");
        script.append(" --username '").append(jdbcUsername).append("'");
        script.append(" --password '").append(jdbcPassword).append("'");
        if(!tableMeta.getDbType().equalsIgnoreCase("Oracle")) {
            script.append(" --driver '").append(jdbcDriverClassName).append("'");
        }
        script.append(" --table '").append(tableMeta.getSqlTableName()).append("'");
    }

    /**
     * sqoop shell脚本 指定RDBMS 库表
     * @param script
     * @param tableMeta
     */
    private void appendOutHive(StringBuilder script, TableMeta tableMeta) {
        script.append(" --hcatalog-database ").append(tableMeta.getSynDB());
        script.append(" --hcatalog-table ").append(tableMeta.getTableName());
        script.append(" --input-null-string ").append(" '\\\\N'").append(" --input-null-non-string ").append(" '\\\\N'");
        script.append(" --fields-terminated-by \"^\"  --batch");
    }

    /**
     * 根据源数据表结构，映射RDBMS表结构 创建表
     * @param tableMeta
     * @param jdbcDriverClassName
     * @throws Exception
     */
    private void createRdbmsTable(TableMeta tableMeta, String jdbcDriverClassName) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        StringBuilder columns = new StringBuilder();
        columns.append(" create table ").append(tableMeta.getSqlTableName()).append("(");
        for (Pair<String, String> p : tableMeta.getColumns()) {
            if(tableMeta.getDbType()=="mysql"){
                columns.append(h.toMysqlColumn(p.getLeft(),p.getRight())).append(",");
            }else if(tableMeta.getDbType()=="oracle"){
                columns.append(h.toOracleColumn(p.getLeft(),p.getRight())).append(",");
            }else if(tableMeta.getDbType()=="postgresql"){
                columns.append(h.toPostgreSQLColumn(p.getLeft(),p.getRight())).append(",");
            }else if(tableMeta.getDbType()=="sqlserver"){
                columns.append(h.toSQLServerColumn(p.getLeft(),p.getRight())).append(",");
            }else if(tableMeta.getDbType()=="db2"){
                columns.append(h.toDB2Column(p.getLeft(),p.getRight())).append(",");
            }
        }
        String sql = columns.toString().substring(0, columns.length() - 1)+")";
        conn = DataConnectionUtils.getConnection(tableMeta.getDbType(),jdbcUrl,jdbcUsername,jdbcPassword);
        stmt = conn.createStatement();
        System.out.println(sql);
        int i = stmt.executeUpdate(sql);
        DataConnectionUtils.closeStmt(stmt);
        DataConnectionUtils.closeConn(conn);
    }

}

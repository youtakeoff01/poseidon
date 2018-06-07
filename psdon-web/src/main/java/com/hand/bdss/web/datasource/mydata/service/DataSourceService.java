package com.hand.bdss.web.datasource.mydata.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.entity.TableEtlDO;
import com.hand.bdss.web.common.vo.DataSourceVO;

public interface DataSourceService {
    void insertDataSource(DataSourceVO dataSourceVO, HttpServletRequest request) throws Exception;

    /**
     * 加入生命周期管理
     * @param dataSourceVO
     * @param request
     * @throws Exception
     */
    void insertMetaData(DataSourceVO dataSourceVO, HttpServletRequest request) throws Exception;
    /**
     * 关系型数据库导入HIVE
     *
     * @param map
     * @param dbType
     * @return
     * @throws Exception
     */
    Map<String, String> DBToBDCaseTableType(Map<String, String> map, String dbType) throws Exception;

    /**
     * HIVE导出至关系型数据库
     *
     * @param map
     * @param dbType
     * @return
     * @throws Exception
     */
    Map<String, String> BDToDBCaseTableType(Map<String, String> map, String dbType) throws Exception;

    List<TableEtlDO> getAzkabanJobName(TableEtlDO tableEtlDO) throws Exception;

    Map<String, String> getFieldsMsg(String tableName, String resourceBaseId) throws Exception;

    String getHiveTableName(String dbName, String tableName) throws Exception;

    boolean checkTable(String srcName, String tableName) throws Exception;

    String getTableSyncTaskName(String hiveTableName) throws Exception;
}

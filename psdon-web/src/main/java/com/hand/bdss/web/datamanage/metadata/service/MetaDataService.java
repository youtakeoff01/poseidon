package com.hand.bdss.web.datamanage.metadata.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.dsmp.model.HiveMetaDesc;
import com.hand.bdss.web.entity.MetaDataEntity;

public interface MetaDataService {
    boolean insertHiveMetaData(String metadata, HttpServletRequest request) throws Exception;

    boolean insertHbaseMetaData(String metadata, HttpServletRequest request) throws Exception;

    boolean updateHiveMetaData(String metadata, HttpServletRequest request) throws Exception;

    boolean deleteHiveMetaDataByTableName(List<MetaDataEntity> metaDataEntitys, HttpServletRequest request) throws Exception;

    List<MetaDataEntity> listHiveHDFSMetaData(MetaDataEntity metaDataEntity, HttpServletRequest request, int startPage,
                                              int count) throws Exception;

    List<HiveMetaDesc> listHiveHDFSMetaData(HttpServletRequest request) throws Exception;

    boolean deleteHbaseMetaData(List<MetaDataEntity> metaDataEntitys, HttpServletRequest request) throws Exception;

    int deleteHiveHDFSMetaData(List<MetaDataEntity> metaDataEntitys, HttpServletRequest request) throws Exception;

    int updateHiveHDFSMetaData(MetaDataEntity metaDataEntity, HttpServletRequest request) throws Exception;

    int insertHiveHDFSMetaData(MetaDataEntity metaDataEntity, HttpServletRequest request) throws Exception;

    int listCountAll(MetaDataEntity metaDataEntity, HttpServletRequest request) throws Exception;

    List<String> getHiveOrHbaseTableNames(String metaDataType) throws Exception;

    List<HiveMetaDesc> listHiveHDFSMetaDataByType(String searchType) throws Exception;

    MetaDataEntity getHiveHDFSMetaData(MetaDataEntity metaDataEntity) throws Exception;

    List<String> getDBHiveTables(String metaDataType) throws Exception;

    boolean hiveDataTypeCheck(String dataType) throws Exception;

    /**
     * 创建的hive表添加到本地的生命周期管理中
     *
     * @param metaDataEntity
     * @return
     * @throws Exception
     */
    int insertMetaData(MetaDataEntity metaDataEntity) throws Exception;

}

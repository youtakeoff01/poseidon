package com.hand.bdss.web.datamanage.metadata.service.impl;

import com.hand.bdss.dsmp.exception.DataServiceException;
import com.hand.bdss.dsmp.model.HiveMetaData;
import com.hand.bdss.dsmp.model.HiveMetaDesc;
import com.hand.bdss.dsmp.model.HivePolicy;
import com.hand.bdss.dsmp.service.metadata.AtlasMetaManage;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.datamanage.metadata.dao.MetaDataDao;
import com.hand.bdss.web.datamanage.metadata.service.MetaDataService;
import com.hand.bdss.web.datamanage.policy.service.PolicyService;
import com.hand.bdss.web.datamanage.policy.service.RangerService;
import com.hand.bdss.web.entity.MetaDataEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Service
public class MetaDataServiceImpl implements MetaDataService {

    private static final Logger logger = LoggerFactory.getLogger(MetaDataServiceImpl.class);

    @Resource
    private MetaDataDao metaDataDaoImpl;
    @Resource
    private PolicyService policyServiceImpl;
    @Resource
    private RangerService rangerServiceImpl;

    private AtlasMetaManage atlasManage = new AtlasMetaManage();

    /**
     * hive 元数据的新增
     *
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertHiveMetaData(String metadata, HttpServletRequest request) throws Exception {
        HiveMetaData hiveMetaData = JsonUtils.toObject(metadata, HiveMetaData.class);
        //将hive新增的表添加到本地生命周期管理当中
        MetaDataEntity metaDataEntity = new MetaDataEntity();
        metaDataEntity.setMetaType(hiveMetaData.getMetaDataType());
        metaDataEntity.setMetaHiveFields(JsonUtils.toJson(hiveMetaData.getMetaTableField()));
        metaDataEntity.setMetaLive(-1);
        metaDataEntity.setMetaDesc(hiveMetaData.getMetaDataDescribe());
        metaDataEntity.setDbName(hiveMetaData.getDbName());
        metaDataEntity.setTableName(hiveMetaData.getTabelName());
        metaDataEntity.setMetaOwner(GetUserUtils.getUser(request).getId());
        boolean boo = getObject().createHiveMeta(hiveMetaData,GetUserUtils.getUser(request).getUserName());
        if (boo) {
            metaDataDaoImpl.insertMetaData(metaDataEntity);
        }
        if (!boo) {
            throw new Exception();
        }
        return boo;
    }

    /**
     * hbase 元数据的新增
     *
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertHbaseMetaData(String metadata, HttpServletRequest request) throws Exception {
        HiveMetaData hiveMetaData = JsonUtils.toObject(metadata, HiveMetaData.class);
        //将hbase新增的表添加到本地生命周期管理当中
        MetaDataEntity metaDataEntity = new MetaDataEntity();
        metaDataEntity.setDbName(hiveMetaData.getDbName());
        metaDataEntity.setTableName(hiveMetaData.getTabelName());
        metaDataEntity.setMetaType("hbase");
        metaDataEntity.setMetaOwner(GetUserUtils.getUser(request).getId());
        metaDataEntity.setMetaLive(-1);
        boolean boo = getObject().createHBaseMeta(hiveMetaData.getTabelName());
        if (!boo) {
            throw new Exception();
        }
        metaDataDaoImpl.insertMetaData(metaDataEntity);
        return boo;
    }

    /**
     * hive 元数据的更改
     *
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHiveMetaData(String metadata, HttpServletRequest request) throws Exception {
        HiveMetaData hiveMetaData = JsonUtils.toObject(metadata, HiveMetaData.class);
        //修改元数据时同时要修改他的本地什么周期
        MetaDataEntity metaDataEntity = new MetaDataEntity();
        metaDataEntity.setMetaType(hiveMetaData.getMetaDataType());
        metaDataEntity.setMetaHiveFields(JsonUtils.toJson(hiveMetaData.getMetaTableField()));
        metaDataEntity.setMetaLive(-1);
        metaDataEntity.setMetaDesc(hiveMetaData.getMetaDataDescribe());
        metaDataEntity.setDbName(hiveMetaData.getDbName());
        metaDataEntity.setTableName(hiveMetaData.getTabelName());
        metaDataEntity.setMetaOwner(GetUserUtils.getUser(request).getId());
        boolean boo1 = getObject().createHiveMeta(hiveMetaData,GetUserUtils.getUser(request).getUserName());
        boolean boo = getObject().updateHiveTable(hiveMetaData, null);
        if (!boo && boo1) {
            throw new Exception();
        }
        metaDataDaoImpl.updateMetaDataByName(metaDataEntity);
        return boo;
    }

    /**
     * hive 元数据的删除
     *
     * @throws DataServiceException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteHiveMetaDataByTableName(List<MetaDataEntity> metaDataEntitys, HttpServletRequest request) throws Exception {
        List<String> tables = new ArrayList<>();
        List<HiveMetaData> lists = new ArrayList<>();
        //删除本地元数据记录
        for (MetaDataEntity metaData : metaDataEntitys) {
            HiveMetaData hiveData = new HiveMetaData();
            hiveData.setDbName(metaData.getDbName());
            hiveData.setTabelName(metaData.getTableName());
            lists.add(hiveData);
            tables.add(metaData.getTableName());
        }
        metaDataDaoImpl.deleteMetaData(metaDataEntitys);

        //删除本地数据权限记录
        HivePolicy hivePolicy = new HivePolicy();
        hivePolicy.setTables(tables);
        List<HivePolicy> hivePolicys = rangerServiceImpl.listHivePolicy(hivePolicy, 0, 10);
        if (hivePolicys != null && hivePolicys.size() > 0) {
            String strJson = JsonUtils.toJson(hivePolicys);
            policyServiceImpl.delPolicy(strJson, request);
        }
        //调用Hive接口  删除Hive表
        boolean boo = getObject().dropHiveTable(lists);
        if (!boo) {
            throw new Exception();
        }
        return boo;
    }

    @Override
    public List<HiveMetaDesc> listHiveHDFSMetaData(HttpServletRequest request) {
        List<HiveMetaDesc> hiveHDFSMetaData = null;
        // hdfs_path hive_table
        hiveHDFSMetaData = getObject().searchMeta("hive_table", "hdfs_path");
        return hiveHDFSMetaData;
    }

    @Override
    public List<HiveMetaDesc> listHiveHDFSMetaDataByType(String searchType) {
        return getObject().searchMeta(searchType);
    }

    /**
     * 删除hbase的元数据
     *
     * @param
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteHbaseMetaData(List<MetaDataEntity> metaDataEntitys, HttpServletRequest request) throws Exception {
        //删除hbase元数据时同时删除本地的什么周期管理
        List<String> tableNames = new ArrayList<>();
        for (MetaDataEntity metaData : metaDataEntitys) {
            tableNames.add(metaData.getTableName());
        }
        boolean boo = getObject().dropHBaseTable(tableNames.get(0));
        if (!boo) {
            throw new Exception();
        }
        metaDataDaoImpl.deleteMetaData(metaDataEntitys);
        return boo;
    }

    public AtlasMetaManage getObject() {
        return new AtlasMetaManage();
    }

    /**
     * hive 元数据的查询
     */
    @Override
    public List<MetaDataEntity> listHiveHDFSMetaData(MetaDataEntity metaDataEntity, HttpServletRequest request,
                                                     int startPage, int count) throws Exception {
        return metaDataDaoImpl.listMetaDataEntity(metaDataEntity, startPage, count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteHiveHDFSMetaData(List<MetaDataEntity> metaDataEntitys, HttpServletRequest request) throws Exception {
        int i = metaDataDaoImpl.deleteMetaData(metaDataEntitys);
        return i;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateHiveHDFSMetaData(MetaDataEntity metaDataEntity, HttpServletRequest request) throws Exception {
        int i = metaDataDaoImpl.updateMetaData(metaDataEntity);
        return i;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertHiveHDFSMetaData(MetaDataEntity metaDataEntity, HttpServletRequest request) throws Exception {
        metaDataEntity.setMetaOwner(GetUserUtils.getUser(request).getId());
        int i = metaDataDaoImpl.insertMetaData(metaDataEntity);
        return i;
    }

    @Override
    public int listCountAll(MetaDataEntity metaDataEntity, HttpServletRequest request) throws Exception {
        int i = metaDataDaoImpl.listCountAll(metaDataEntity);
        return i;
    }

    @Override
    public List<String> getHiveOrHbaseTableNames(String metaDataType) throws Exception {
        List<String> lists = metaDataDaoImpl.getHiveOrHbaseTableNames(metaDataType);
        return lists;
    }

    @Override
    public List<String> getDBHiveTables(String metaDataType) throws Exception {
        List<String> lists = metaDataDaoImpl.getDBHiveTables(metaDataType);
        return lists;
    }

    /**
     * 查询表
     */
    @Override
    public MetaDataEntity getHiveHDFSMetaData(MetaDataEntity metaDataEntity) throws Exception {

        return metaDataDaoImpl.getHiveHDFSMetaData(metaDataEntity);
    }

    @Override
    public boolean hiveDataTypeCheck(String dataType) throws Exception {
        String type = "int,tinyint,smallint,integer,bigint,float,double,decimal,date,timestamp,binary,char,varchar,string,array,map,struct,union";
        if (type.contains(dataType.toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int insertMetaData(MetaDataEntity metaDataEntity) throws Exception {
        return metaDataDaoImpl.insertMetaData(metaDataEntity);
    }
}

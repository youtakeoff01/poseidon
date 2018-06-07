package com.hand.bdss.web.datamanage.metadata.dao;

import java.util.List;
import java.util.Map;

import com.hand.bdss.web.entity.MetaDataEntity;

public interface MetaDataDao {

	// int insertHiveMetaData(HiveMetaData hiveMetaData);
	//
	// int updateMetaData(HiveMetaData hiveMetaData);

	int insertMetaData(MetaDataEntity metaDataEntity) throws Exception;

	int updateMetaData(MetaDataEntity metaDataEntity) throws Exception;

	int deleteMetaData(List<MetaDataEntity> metaDataEntitys) throws Exception;

	List<MetaDataEntity> listMetaDataEntity(MetaDataEntity metaDataEntity, int startPage, int count) throws Exception;

	int listCountAll(MetaDataEntity metaDataEntity) throws Exception;

	int updateMetaDataByName(MetaDataEntity metaDataEntity) throws Exception;

	int deleteMetaDataByName(List<MetaDataEntity> metaDataEntitys) throws Exception;

	int deleteMetaDataByLocation(List<MetaDataEntity> metaDataEntitys) throws Exception;

	List<String> getHiveOrHbaseTableNames(String metaDataType) throws Exception;

	int updateRename(Map<String, String> maps) throws Exception;

	MetaDataEntity getHiveHDFSMetaData(MetaDataEntity metaDataEntity) throws Exception;

	List<String> getDBHiveTables(String metaDataType) throws Exception;

}

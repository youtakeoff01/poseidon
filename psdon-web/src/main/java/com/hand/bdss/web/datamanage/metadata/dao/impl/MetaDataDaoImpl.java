package com.hand.bdss.web.datamanage.metadata.dao.impl;

import java.util.List;
import java.util.Map;

import com.hand.bdss.web.datamanage.metadata.dao.MetaDataDao;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.hand.bdss.web.entity.MetaDataEntity;
import com.hand.bdss.web.common.util.SupportDaoUtils;

@Repository
public class MetaDataDaoImpl extends SupportDaoUtils implements MetaDataDao {

	private static final String MAPPERURL = "com.hand.bdss.web.entity.MetaDataEntity.";

	@Override
	public int insertMetaData(MetaDataEntity metaDataEntity) throws Exception{
		int i = getSqlSession().insert(MAPPERURL + "insertMataData", metaDataEntity);
		return i;
	}

	@Override
	public int updateMetaData(MetaDataEntity metaDataEntity) throws Exception{
		int i = getSqlSession().update(MAPPERURL + "updateMetaData", metaDataEntity);
		return i;
	}

	@Override
	public int deleteMetaData(List<MetaDataEntity> metaDataEntitys) throws Exception{
		int i = getSqlSession().delete(MAPPERURL + "deleteMetaData", metaDataEntitys);
		return i;
	}

	@Override
	public List<MetaDataEntity> listMetaDataEntity(MetaDataEntity metaDataEntity, int startPage, int count) throws Exception{
		RowBounds rowBounds = new RowBounds(startPage, count);
		List<MetaDataEntity> lists = getSqlSession().selectList(MAPPERURL + "listMetaDataEntity", metaDataEntity,
				rowBounds);
		return lists;
	}

	@Override
	public int listCountAll(MetaDataEntity metaDataEntity) throws Exception{
		int countAll = getSqlSession().selectOne(MAPPERURL + "listCountAll", metaDataEntity);
		return countAll;
	}

	@Override
	public int updateMetaDataByName(MetaDataEntity metaDataEntity) throws Exception{
		int i = getSqlSession().update(MAPPERURL + "updateMetaDataByName", metaDataEntity);
		return i;
	}

	@Override
	public int deleteMetaDataByName(List<MetaDataEntity> metaDataEntitys) throws Exception{
		int i = getSqlSession().delete(MAPPERURL + "deleteMetaDataByName", metaDataEntitys);
		return i;
	}

	@Override
	public int deleteMetaDataByLocation(List<MetaDataEntity> metaDataEntitys) throws Exception{
		int i = getSqlSession().delete(MAPPERURL + "deleteMetaDataByLocation", metaDataEntitys);
		return i;
	}

	@Override
	public List<String> getHiveOrHbaseTableNames(String metaDataType) throws Exception{
		List<String> lists = getSqlSession().selectList(MAPPERURL + "getHiveOrHbaseTableNames", metaDataType);
		return lists;
	}

	@Override
	public List<String> getDBHiveTables(String metaDataType) throws Exception{
		List<String> lists = getSqlSession().selectList(MAPPERURL + "getDBHiveTables", metaDataType);
		return lists;
	}
	
	@Override
	public int updateRename(Map<String, String> maps) throws Exception{
		int i = getSqlSession().update(MAPPERURL+"updateRename",maps);
		return i;
	}

	@Override
	public MetaDataEntity getHiveHDFSMetaData(MetaDataEntity metaDataEntity) throws Exception{
		
		return getSqlSession().selectOne(MAPPERURL+"getHiveHDFSMetaData",metaDataEntity);
	}

}

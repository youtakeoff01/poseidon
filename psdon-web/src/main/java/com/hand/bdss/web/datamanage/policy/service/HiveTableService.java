package com.hand.bdss.web.datamanage.policy.service;

import java.util.List;

import com.hand.bdss.dsmp.model.HiveMetaData;
import com.hand.bdss.dsmp.model.HiveMetaTableField;


/**
 * hive表接口
 * @author zk
 */
public interface HiveTableService {
	
	/**
	 * 查询hive表集合
	 * @param dbName
	 * @param username
	 * @return
	 */
	List<String> select(String dbName,String username) throws Exception;
	
	/**
	 * 新增hive表
	 * @param hmd
	 * @param username
	 * @return
	 */
	Boolean insert(HiveMetaData hmd,String username) throws Exception;
	
	/**
	 * 更新hive表
	 * @param hmd
	 * @param username
	 * @return
	 * @throws Exception
	 */
	Boolean update(HiveMetaData hmd,String username) throws Exception;
	
	/**
	 * 删除hive表
	 * @param hmd
	 * @param username
	 * @return
	 * @throws Exception
	 */
	Boolean delete(HiveMetaData hmd,String username) throws Exception;
	
	/**
	 * 获取hive指定数据库下指定hive表的所有字段
	 * @param dbName
	 * @param tableName
	 * @param username
	 * @return
	 */
	List<HiveMetaTableField> getHiveMetaTableField(String dbName, String tableName,String username) throws Exception;
	
	/**
	 * 创建hive数据库
	 * @param dbName
	 * @param username
	 * @return
	 */
	Boolean createHiveDb(String dbName,String username) throws Exception;
	
	/**
	 * 获取所有数据库名称
	 * @param username
	 * @return
	 */
	List<String> getHiveDb(String username) throws Exception;

}

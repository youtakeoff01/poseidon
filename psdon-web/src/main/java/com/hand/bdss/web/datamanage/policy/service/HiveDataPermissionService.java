package com.hand.bdss.web.datamanage.policy.service;

import java.util.List;
import java.util.Map;

import com.hand.bdss.dsmp.model.HivePolicy;
import com.hand.bdss.web.common.vo.HiveTableVO;

/**
 * 数据权限接口
 * @author zk
 */
public interface HiveDataPermissionService {
	
	/**
	 * 查询hive表数据权限
	 * @param policy
	 * @return
	 */
	List<HivePolicy> select(HivePolicy policy,String username,String database) throws Exception;
	
	/**
	 * 分頁查询hive表数据权限
	 * @param policy
	 * @return
	 */
	Map<String,Object> selectPage(HivePolicy policy,String username,Integer pageNo,Integer pageSize) throws Exception;
	
	/**
	 * 根据用户查询有操作权限的hive表集合
	 * @param policy
	 * @param username
	 * @param database
	 * @return
	 * @throws Exception
	 */
	List<HivePolicy> selectTables(HivePolicy policy,String username,String tableName) throws Exception;
	/**
	 * 插入数据权限
	 * @param policy
	 * @return
	 */
	Boolean insert(HivePolicy policy) throws Exception;
	
	/**
	 * 更新数据权限
	 * @param policy
	 * @return
	 */
	Boolean update(HivePolicy policy) throws Exception;
	
	/**
	 * 删除数据权限
	 * @param policy
	 * @return
	 */
	Boolean delete(HivePolicy policy) throws Exception;
	
	/**
	 * 用户删除表后删除或更新对应表的ranger权限
	 * @param dbName 		数据库名称
	 * @param tableName 	表名
	 * @param username 		用户名
	 * @return
	 */
	Boolean deleteOrUpdate(String dbName,String tableName,String username) throws Exception;
	
	/**
	 * 根据sql查询用户是否拥有操作表的权限
	 * @param policy
	 * @param userName
	 * @param sql
	 * @return
	 */
	String selectBySql(HivePolicy policy,String userName, String sql) throws Exception;

	/**
	 * 根据用户获取hive数据及数据库
	 * @param username
	 * @return
	 * @throws Exception
	 */
	List<HiveTableVO> getHiveDbAndTables(String username) throws Exception;
}

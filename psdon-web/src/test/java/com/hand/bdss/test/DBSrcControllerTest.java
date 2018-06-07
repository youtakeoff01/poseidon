/*
package com.hand.bdss.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hand.bdss.dsmp.model.HiveMetaData;
import com.hand.bdss.dsmp.model.HiveMetaTableField;
import com.hand.bdss.dsmp.model.HivePolicy;
import com.hand.bdss.web.entity.UserEntity;
import MetaDataService;
import DBSrcService;
import DataSourceService;
import com.hand.bdss.web.platform.user.service.UserService;
import PolicyService;
import JsonUtils;
import DBSrcVO;
import DataSourceVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "servlet-context.xml")
public class DBSrcControllerTest {
	
	@Resource(name="dBSrcServiceImpl")
	private DBSrcService dBSrcServiceImpl;
	
	@Autowired
	private UserService userServiceImpl;
	
	@Resource(name="dataSourceServiceImpl")
	private DataSourceService dataSourceServiceImpl;
	
	@Resource(name="policyServiceImpl")
	private PolicyService policyServiceImpl;
	
	@Autowired
	private MetaDataService metaDataServiceImpl;
	
	DBSrcVO db = new DBSrcVO();
	*/
/**
	 * 数据源的插入操作
	 *//*

	@Test
	public void insertDBSrcTest(){
		db.setDbName("liqifei");
		db.setDbPwd("123");
		db.setDbDriver("com.mysql.driver");
		db.setDbType("mysql");
		db.setDbUrl("localhost:3306/test");
		db.setDbUser("user");
		db.setSrcName("hehe");
		System.out.println(dBSrcServiceImpl.insertDBSrc(db));
	}
	*/
/**
	 * 数据源的分页查询
	 *//*

	@Test
	public void listDBSrcTest(){
		//db.setId(2);
		db.setDbName("li");
		List<DBSrcVO> dbsrcvos = dBSrcServiceImpl.listDBSrcs(db,0,10);
		System.out.println(dbsrcvos.size());
	}
	@Test
	public void test03(){
		int counts = dBSrcServiceImpl.getCounts(db);
		System.out.println(counts);
	}
	@Test
	public void test04()throws Exception{
		List<String> ids = new ArrayList<String>();
		ids.add("4");
		ids.add("5");
		//根据数据源的id来查询需要对应删除的用于生成etc 脚本信息对应的id
//		List<String> idss = dataSourceDaoImpl.listIds(ids);
//		System.out.println(idss.toString());
		// 删除id对应的数据源的数据
	//	int i = dataSourceDaoImpl.deleteData(ids);
		//System.out.println(i);
		//调用易冀删除job任务的接口
		dBSrcServiceImpl.deleteDBSrcsById(ids,null);
		
	}
	
	@Test
	public void test05()throws Exception{
		db.setDbName("liqifei");
		db.setDbPwd("123");
		db.setDbDriver("com.mysql.driver");
		db.setDbType("mysql");
		db.setDbUrl("localhost:3306/test");
		db.setDbUser("user");
		db.setSrcName("hehe");
		db.setId(7);
		//int i = dbSrcDao.updateDBSrcsById(db);
		//System.out.println(i);
	//	List<Map<String,String>> maps = dataSourceDaoImpl.listEtlMsg(db);
	//	System.out.println(maps);
		int i = dBSrcServiceImpl.updateDBSrcsById(db,null);
	}
	@Test
	public void test06(){
		UserEntity user = new UserEntity();
		user.setUserName("wew");
		int id = userServiceImpl.checkUser(user);
		System.out.println(id);
	}
	
	@Test
	public void tesgt07(){
		UserEntity user = new UserEntity();
		user.setUserName("admin");
		List<UserEntity> lists = userServiceImpl.listUsers(user,0,10);
		System.out.println("222");
	}
	
	@Test
	public void test08(){
		UserEntity user = new UserEntity();
		user.setUserName("1111");
		user.setCreateAccount("1234567");
		user.setEmail("1234567890");
		user.setGroupId(23);
		user.setRoleId(4);
		user.setPassword("111111111111111111111");
		user.setPhoneNo("2323232323");
		int i = 0;
		try {
			i = userServiceImpl.insertUser(user);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(i);
	}
	@Test
	public void test09(){
		UserEntity user = new UserEntity();
		user.setId(1);
		user.setUserName("1111");
		user.setCreateAccount("1234567");
		user.setEmail("1234567890");
		user.setGroupId(23);
		user.setRoleId(4);
		user.setPassword("131");
		user.setPhoneNo("2323232323");
		int i = 0;
		try {
			i = userServiceImpl.updateUser(user);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(i);
	}
	@Test
	public void test10(){
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
//		int i = userServiceImpl.deleteUser(ids);
//		System.out.println(i);
	}
	@Test
	public void test11(){
		List<Long> lists = userServiceImpl.getMenuByUser("admin");
		System.out.println(lists.toString());
	}
	@Test
	public void test12(){
		DataSourceVO dataSourceVO = new DataSourceVO();
//		List<TableEtlDO> tableEtlDOs = new ArrayList<TableEtlDO>();
//		TableEtlDO ta = new TableEtlDO();
		try {
			int i = dataSourceServiceImpl.insertDataSource(dataSourceVO,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test13(){
		HivePolicy hive = new HivePolicy();
		//hive.setId(1234L);
		hive.setName("hivePolicy4");
		hive.setServiceName("hdp_dev_hive");
        List<String>  databases = new ArrayList<String>();
        databases.add("*");
		hive.setDatabases(databases);
		List<String>  tables = new ArrayList<String>();
		tables.add("tb_test");
        hive.setTables(tables);
		List<String>  type = new ArrayList<String>();
		type.add("update");
		type.add("drop");
        hive.setType(type);
		List<String>  user = new ArrayList<String>();
		user.add("dmp");
        hive.setUser(user);
        List<String> columnList = new ArrayList<String>();
		columnList.add("*");
		hive.setColumns(columnList);
        String strJson = JsonUtils.toJson(hive);
        List<String> jsons = new ArrayList<String>();
        jsons.add(strJson);
	//	boolean boo = policyServiceImpl.insertPolicy(strJson,null);
	//	boolean boo = policyServiceImpl.updatePolicy(strJson, null);
    //    List<HivePolicy> lists = policyServiceImpl.selectPolicy(strJson, null);
//        boolean boo = policyServiceImpl.delPolicy(jsons, null);
//		System.out.println(boo);
   //     System.out.println(lists.size());
	}
	@Test
	public void test14()throws Exception{
		HiveMetaData hiveMetaData = new HiveMetaData();
		hiveMetaData.setMetaDataDescribe("fjdslfjdskljdkjd");
		hiveMetaData.setMetaDataType("hive");
		hiveMetaData.setTabelName("tb_hive_test01");
		List<HiveMetaTableField> metaTableFields  = new ArrayList<HiveMetaTableField>();
		HiveMetaTableField metaTableField1 = new HiveMetaTableField();
		metaTableField1.setFieldName("id");
		metaTableField1.setFieldType("string");
		metaTableField1.setFieldDescribe("用户id");
		
		HiveMetaTableField metaTableField2 = new HiveMetaTableField();
		metaTableField2.setFieldName("name");
		metaTableField2.setFieldType("string");
		metaTableField2.setFieldDescribe("用户名字");
		metaTableFields.add(metaTableField1);
		metaTableFields.add(metaTableField2);
		hiveMetaData.setMetaTableField(metaTableFields);
		
	//	boolean boo = metaDataServiceImpl.insertHiveMetaData(new Gson().toJson(hiveMetaData), null);
//	    System.out.println(boo);
//			List<HiveMetaDesc> lists = metaDataServiceImpl.listHiveHDFSMetaData(null);
	//	boolean boo = metaDataServiceImpl.updateHiveMetaData(new Gson().toJson(hiveMetaData), null);
		List<String> lists = new ArrayList<String>();
		lists.add("tb_hive_test01");
		lists.add("tb_hive_test");
		boolean boo = 	metaDataServiceImpl.deleteHiveMetaDataByTableName(lists, null);
	//	Map<String,String> map = new HashMap<String,String>();
	//	map.put("tableName", "tb_test_hbase");
	// boo =  metaDataServiceImpl.insertHbaseMetaData(map, null);
	//	System.out.println(boo);
	//	List<String> tableNames = new ArrayList<String>();
	//	tableNames.add("tb_test_hbase");
	//	boolean boo = metaDataServiceImpl.deleteHbaseMetaData(tableNames, null);
		System.out.println(boo);
	}
	
}
*/

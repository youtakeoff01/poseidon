package com.hand.bdss.web.datamanage.policy.service.impl;

import com.hand.bdss.dsmp.component.hive.HiveClient;
import com.hand.bdss.dsmp.model.HivePolicy;
import com.hand.bdss.dsmp.model.TableTypeEntity;
import com.hand.bdss.dsmp.service.privilege.RangerPolicyManage;
import com.hand.bdss.web.common.constant.Global;
import com.hand.bdss.web.common.util.JsqlparserUtil;
import com.hand.bdss.web.common.util.StringUtils;
import com.hand.bdss.web.common.vo.HiveTableVO;
import com.hand.bdss.web.datamanage.policy.service.HiveDataPermissionService;
import org.apache.avro.generic.GenericData;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据权限接口实现
 * @author zk
 */
@Service
public class HiveDataPermissionServiceImpl implements HiveDataPermissionService {
	
	/**
	 * 插入数据权限
	 * @return
	 */
	@Override
	public Boolean insert(HivePolicy policy) throws Exception{
		//插入数据到ranger
		return getPolicyManage().createPolicy(policy);
	}
	
	/**
	 * 更新数据权限
	 * @return
	 */
	@Override
	public Boolean update(HivePolicy policy) throws Exception{
		return getPolicyManage().updatePolicy(policy);
	}
	
	/**
	 * 删除数据权限
	 * @return
	 */
	@Override
	public Boolean delete(HivePolicy policy) throws Exception{
		return getPolicyManage().deletePolicy(policy);
	}
	

	/**
	 * 查询hive表数据权限
	 * @return
	 */
	@Override
	public List<HivePolicy> select(HivePolicy policy, String username, String database) throws Exception {
		List<HivePolicy> upList = new ArrayList<HivePolicy>();
		List<HivePolicy> policyList = getPolicyManage().searchPolicies(policy);
		if (policyList != null && policyList.size() > 0) {
			if (StringUtils.isNotBlank(username) && !Global.MANAGER.equals(username)) {
				for(HivePolicy hp : policyList){
					if(hp.getUser() != null && hp.getUser().size() > 0){
						if(hp.getUser().contains(username)){
							upList.add(hp);
						}
					}
				}
			}else if(StringUtils.isNotBlank(database)
					&& policy != null && policy.getTables() != null && policy.getTables().size() > 0
					&& policy.getUser() != null && policy.getUser().size() > 0){
				for(HivePolicy hp : policyList){
					//查询策略中是否已经存在配置对应人员、数据库、表记录
					if(hp.getUser() != null && hp.getUser().size() > 0 
							&& hp.getDatabases() != null && hp.getDatabases().size() > 0
							&& hp.getTables() != null && hp.getTables().size() > 0){
						if(policy.getName()!= null && !policy.getName().equals(hp.getName())){
							if(hp.getDatabases().contains(database)){
								for(String user : policy.getUser()){
									if(hp.getUser().contains(user)){
										for(String table : policy.getTables()){
											if(hp.getTables().contains(table)){
//												System.out.println(hp.getUser()+"---"+hp.getDatabases()
//												+"---"+hp.getTables());
												upList.add(hp);
											}
										}
									}
								}
							}
						}
					}
				}
			}else{
				return policyList;
			}
		}	
		return upList;
	}
	
	/**
	 * 分頁查询hive表数据权限
	 * @throws Exception 
	 * @throws IOException 
	 */
	@Override
	public Map<String,Object> selectPage(HivePolicy policy, String username,
                                         Integer pageNo, Integer pageSize) throws Exception  {
		Map<String,Object> map = new HashMap<String,Object>();
		List<HivePolicy> upList = new ArrayList<HivePolicy>();
		List<HivePolicy> upList2 = new ArrayList<HivePolicy>();
		List<HivePolicy> policyList = getPolicyManage().searchPolicies(policy);
		if(!Global.MANAGER.equals(username)){
			for(HivePolicy hp : policyList){
				if(hp.getUser() != null && hp.getUser().size() > 0){
					if(hp.getUser().contains(username)){
						upList2.add(hp);
					}
				}
			}
			policyList = upList2;
		}else{
			for(HivePolicy hp : policyList){
				if(hp.getDatabases() != null && hp.getDatabases().size() > 0
						&& !"*".equals(hp.getDatabases().get(0))){
					upList2.add(hp);
				}
			}
			policyList = upList2;
		}
		if (policyList != null && policyList.size() > 0) {
			if (StringUtils.isNotBlank(username)) {
				int allCount = policyList.size();
				int pageCount = (allCount + pageSize - 1) / pageSize;
				if (pageNo >= pageCount) {
					pageNo = pageCount;
				}
				int start = (pageNo - 1) * pageSize;
				int end = pageNo * pageSize;
				if (end >= allCount) {
					end = allCount;
				}
				List<String> userList = null;
				List<String> dbList = null;
				if(Global.MANAGER.equals(username)){
					for (int i = start;i < end;i++) {
						userList = policyList.get(i).getUser();
						dbList = policyList.get(i).getDatabases();
						if(userList != null && userList.size() > 0
								&& dbList != null && dbList.size() > 0
								&& !"*".equals(dbList.get(0))){
							upList.add(policyList.get(i));
						}
					}
				}else{
					for (int i = start;i < end;i++) {
						userList = policyList.get(i).getUser();
						if(userList != null && userList.size() > 0){
							if(userList.contains(username)){
								upList.add(policyList.get(i));
							}
						}
					}
				}
				map.put("data", upList);
				map.put("allCount", allCount);
				map.put("pageCount", pageCount);
				map.put("pageNo", pageNo);
				map.put("pageSize", pageSize);
			}
		}
		return map;
	}
	
	/**
	 * 根据用户查询有操作权限的hive表集合
	 */
	@Override
	public List<HivePolicy> selectTables(HivePolicy policy, String username, String tableName) throws Exception {
		Map<String,HivePolicy> map = new HashMap<String,HivePolicy>();
		List<HivePolicy> policyList = getPolicyManage().searchPolicies(policy);
		if (policyList != null && policyList.size() > 0) {
			Map<String,Object> tmap = new HashMap<String,Object>();
			List<TableTypeEntity> list = null;
			if (StringUtils.isNotBlank(username) && !Global.MANAGER.equals(username)) {
				for(HivePolicy hp : policyList){
					if(hp.getUser() != null && hp.getUser().size() > 0){
						if(hp.getUser().contains(username)){
							mergeTables(hp,map,tmap,list);
						}
					}
				}
			//如果账号是管理员
			}else if(Global.MANAGER.equals(username)){
				for(HivePolicy hp : policyList){
					if(hp.getUser() != null && hp.getUser().size() > 0 
							&& hp.getDatabases() != null && hp.getDatabases().size() > 0
							&& !"*".equals(hp.getDatabases().get(0))){
						mergeTables(hp,map,tmap,list);
					}
				}
			}
			policyList.clear();
			policyList.addAll(map.values());
		}	
		return policyList;
	}
	
	/**
	 * 合并相同数据库的表及表权限
	 */
	private void mergeTables (HivePolicy hp, Map<String,HivePolicy> map,
                              Map<String,Object> tmap, List<TableTypeEntity> list){
		String key = hp.getDatabases().get(0);
		tmap.put("tables", hp.getTables());
		tmap.put("type", hp.getType());
		if(map.containsKey(key)){
			HivePolicy p = (HivePolicy)map.get(key);
			for(String table : hp.getTables()){
				p.getTablesType().add(new TableTypeEntity(table,hp.getType(),hp.getUser()));
			}
			map.put(key, p);
			
		}else{
			list = new ArrayList<TableTypeEntity>();
			for(String table : hp.getTables()){
				list.add(new TableTypeEntity(table,hp.getType(),hp.getUser()));
			}
			hp.setTablesType(list);
			map.put(key, hp);
		}
	}
	
	/**
	 * 用户删除表后删除或更新对应表的ranger权限
	 */
	@Override
	public Boolean deleteOrUpdate(String dbName, String tableName, String username) throws Exception{
		Boolean result = false;
		HivePolicy policy = new HivePolicy();
		policy.setServiceName(Global.HIVE_SERVER);
		List<HivePolicy> policyList = getPolicyManage().searchPolicies(policy);
		
		if (policyList != null && policyList.size() > 0) {
			if (StringUtils.isNotBlank(dbName) && StringUtils.isNotBlank(tableName)) {
				for(HivePolicy hp : policyList){
					if(hp.getDatabases().contains(dbName) && hp.getTables().contains(tableName)){
						//当策略中只有一张表时删除该策略，如果是多张表则更新
						if(hp.getTables().size() == 1){
							result = getPolicyManage().deletePolicy(hp);
						}else{
							hp.getTables().remove(tableName);
							result = getPolicyManage().updatePolicy(hp);
						}
					}
				}
			}	
		}
		return result;
	}
	
	/**
	 * 根据sql查询用户是否拥有操作表的权限
	 */
	@Override
	public String selectBySql(HivePolicy policy, String userName, String sql) throws Exception{

		StringBuffer buffer = new StringBuffer();
		//根据sql获取数据库表
		List<String> tables = JsqlparserUtil.selectTable(sql);
		if(tables != null && tables.size() > 0){
			String dbName = null;
			String tableName = null;
			//根据人名查询ranger权限
			List<HivePolicy> upList = new ArrayList<HivePolicy>();
			List<HivePolicy> policyList = getPolicyManage().searchPolicies(policy);
			if(policyList != null && policyList.size() > 0){
				if (StringUtils.isNotBlank(userName) && !Global.MANAGER.equals(userName)) {
					for(HivePolicy hp : policyList){
						if(hp.getUser() != null && hp.getUser().size() > 0){
							if(hp.getUser().contains(userName)){
								upList.add(hp);
							}
						}
					}
				}else if(Global.MANAGER.equals(userName)){
					upList = policyList;
				}
			}
			
			for(String table : tables){
				if(table.contains(".")){
					dbName = table.split("\\.")[0];
					tableName = table.split("\\.")[1];
					//判断人员是否有查询数据库和表权限
					for(HivePolicy p : upList){
						if(p.getDatabases() != null && p.getDatabases().contains(dbName)
								&& p.getTables() != null && p.getTables().contains(tableName) && p.getType() != null){
							if(!p.getType().contains("select") && !p.getType().contains("all")){
								buffer.append(table + " ");
								return buffer.toString();
							}
						}
					}
				}
			}
		}
		
		return "";
	}

	/**
	 * 根据用户获取hive数据及数据库
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<HiveTableVO> getHiveDbAndTables(String username) throws Exception {

		List<HiveTableVO> hiveList = new ArrayList<HiveTableVO>();
		List<String> tableList = null;
		HiveTableVO htvo = null;
		HivePolicy policy = new HivePolicy();
		policy.setServiceName(Global.HIVE_SERVER);

		List<HivePolicy> pList = selectTables(policy,username,"");
		if(pList != null && pList.size() > 0){
			for(HivePolicy p : pList){
				htvo = new HiveTableVO();
				htvo.setDbName(p.getDatabases().get(0));
				List<TableTypeEntity> list = p.getTablesType();
				if(list != null && list.size() > 0){
					tableList = new ArrayList<String>();
					for(TableTypeEntity tte : list){
						List<String> typeList = tte.getType();
						if(typeList != null && typeList.size() > 0){
							if(typeList.contains("select") || typeList.contains("all")){
								tableList.add(tte.getTable());
							}
						}
					}

					if(tableList.contains("*")){
						tableList = new HiveClient(username).getHiveTables(p.getDatabases().get(0));
					}
					if(tableList != null && tableList.size() > 0){
						htvo.setTableList(tableList);
						hiveList.add(htvo);
					}
				}
			}
		}

		return hiveList;
	}

	/**
	 * 获取RangerPolicyManage
	 * @return
	 */
	private RangerPolicyManage getPolicyManage() throws Exception{
		return new RangerPolicyManage();
	}

	
}

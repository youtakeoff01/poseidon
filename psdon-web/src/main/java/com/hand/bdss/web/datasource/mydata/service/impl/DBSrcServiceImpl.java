package com.hand.bdss.web.datasource.mydata.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.dsmp.config.SystemConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.bdss.web.datasource.mydata.dao.DBSrcDao;
import com.hand.bdss.web.datasource.mydata.dao.DataSourceDao;
import com.hand.bdss.web.datasource.mydata.service.DBSrcService;
import com.hand.bdss.dsmp.component.hive.HiveClient;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.vo.DBSrcVO;

@Service("dBSrcServiceImpl")
public class DBSrcServiceImpl implements DBSrcService {

	@Resource
	private DBSrcDao dBSrcDaoImpl;

	@Resource
	private DataSourceDao dataSourceDaoImpl;

	@Override
	public int insertDBSrc(DBSrcVO dbSrcVO,HttpServletRequest request) throws Exception{
		if ("mysql".equalsIgnoreCase(dbSrcVO.getDbType())){
			dbSrcVO.setDbDriver("com.mysql.jdbc.Driver");
		}
		if ("oracle".equalsIgnoreCase(dbSrcVO.getDbType())){
			dbSrcVO.setDbDriver("oracle.jdbc.driver.OracleDriver");
		}
		if ("sqlserver".equalsIgnoreCase(dbSrcVO.getDbType())){
			dbSrcVO.setDbDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		}
		if("db2".equalsIgnoreCase(dbSrcVO.getDbType())){
			dbSrcVO.setDbDriver("com.ibm.db2.jcc.DB2Driver");
		}
		if ("postgresql".equalsIgnoreCase(dbSrcVO.getDbType())){
			dbSrcVO.setDbDriver("org.postgresql.Driver");
		}
		if ("hive".equalsIgnoreCase(dbSrcVO.getDbType())){
			dbSrcVO.setDbDriver("org.apache.hive.jdbc.HiveDriver");
		}
		if(dbSrcVO.getDbPwd() == null){
			dbSrcVO.setDbPwd("");
		}
		dbSrcVO.setCreateAccount(GetUserUtils.getUser(request).getId());
		int i = dBSrcDaoImpl.insertDBSrc(dbSrcVO);
		return i;
	}

	@Override
	public DBSrcVO getDBSrc(DBSrcVO dbSrcVO) throws Exception {
		return dBSrcDaoImpl.getDBSrc(dbSrcVO);
	}

	@Override
	public List<DBSrcVO> listDBSrcs(DBSrcVO dbSrcVO, int startPage, int count) throws Exception{
		List<DBSrcVO> listDBSrcs = dBSrcDaoImpl.listDBSrcs(dbSrcVO, startPage, count);
		return listDBSrcs;
	}

	@Override
	public int getCounts(DBSrcVO dbSrcVO) throws Exception{
		int counts = dBSrcDaoImpl.getCounts(dbSrcVO);
		return counts;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public int deleteDBSrcsById(List<String> ids, HttpServletRequest request) throws Exception {
		// 删除id对应的数据源
		int i = dBSrcDaoImpl.deleteDBSrc(ids);
		// 根据数据源的id来查询需要对应删除的用于生成etc 脚本信息对应的id
		/*List<String> idss = dataSourceDaoImpl.listIds(ids);
		if (idss != null && idss.size() > 0) {
			// 删除id对应的数据源的数据
			dataSourceDaoImpl.deleteData(idss);
			DBSrcVO dbSrcVO = new DBSrcVO();
			List<TableEtlDO> tableEtlDOs = new ArrayList<TableEtlDO>();
			for (String id : idss) {
				TableEtlDO tableEtlDO = new TableEtlDO();
				tableEtlDO.setId(id);
			}
			String json = JsonUtils.objectToJson(dbSrcVO, tableEtlDOs, "delete");
			// 调用易冀删除job任务的接口
			new ETLManager().run(json);
		}*/
		return i;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public int updateDBSrcsById(DBSrcVO dbSrcVO, HttpServletRequest request) throws Exception {
		if ("mysql".equalsIgnoreCase(dbSrcVO.getDbType())) {
			dbSrcVO.setDbDriver("com.mysql.jdbc.Driver");
		}
		if ("oracle".equalsIgnoreCase(dbSrcVO.getDbType())) {
			dbSrcVO.setDbDriver("oracle.jdbc.driver.OracleDriver");
		}
		if ("sqlserver".equalsIgnoreCase(dbSrcVO.getDbType())){
			dbSrcVO.setDbDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		}
		if("db2".equalsIgnoreCase(dbSrcVO.getDbType())){
			dbSrcVO.setDbDriver("com.ibm.db2.jcc.DB2Driver");
		}
		if ("postgresql".equalsIgnoreCase(dbSrcVO.getDbType())){
			dbSrcVO.setDbDriver("org.postgresql.Driver");
		}
		if ("hive".equalsIgnoreCase(dbSrcVO.getDbType())){
			dbSrcVO.setDbDriver("org.apache.hive.jdbc.HiveDriver");
		}
		dbSrcVO.setUpdateAccount(GetUserUtils.getUser(request).getId());
		// 根据用户给的最新的数据源信息更改已有的数据信息
		int i = dBSrcDaoImpl.updateDBSrcsById(dbSrcVO);
		// 查询出需要更新的数据源信息对应的用于生成etc 脚本信息
		/*List<TableEtlDO> tableEtlDOs = dataSourceDaoImpl.listEtlMsg(dbSrcVO);
		if (tableEtlDOs != null && tableEtlDOs.size() > 0) {
			String job = JsonUtils.objectToJson(dbSrcVO, tableEtlDOs, "update");
			// 调用易冀更改job任务的接口
			new ETLManager().run(job);
		}*/
		return i;
	}

	@Override
	public int checkSrcName(DBSrcVO dbSrcVO) throws Exception{
		int i = dBSrcDaoImpl.checkSrcName(dbSrcVO);
		return i;
	}

	@Override
	public int createDBSrc(DBSrcVO dbSrcVO) throws Exception {
		int i = 1;
		HiveClient hc = new HiveClient(SystemConfig.userName);
		try {
			hc.createDatabase(dbSrcVO.getDbName());
		} catch (Exception e) {
			i=0;
			e.printStackTrace();
		}
		return i;
	}
}

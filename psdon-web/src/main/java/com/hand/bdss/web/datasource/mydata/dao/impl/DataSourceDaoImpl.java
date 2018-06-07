package com.hand.bdss.web.datasource.mydata.dao.impl;

import java.util.List;

import com.hand.bdss.web.datasource.mydata.dao.DataSourceDao;
import org.springframework.stereotype.Repository;

import com.hand.bdss.web.entity.TableEtlDO;
import com.hand.bdss.web.common.util.SupportDaoUtils;
import com.hand.bdss.web.common.vo.DBSrcVO;

@Repository
public class DataSourceDaoImpl extends SupportDaoUtils implements DataSourceDao {
	private static final String MAPPERURL = "com.hand.bdss.web.entity.TableEtlDO.";

	@Override
	public int insertData(TableEtlDO tableEtlDO) throws Exception{
		int i = getSqlSession().insert(MAPPERURL + "insertData", tableEtlDO);
		return i;
	}

	@Override
	public int deleteData(List<String> ids) throws Exception{
		int i = getSqlSession().delete(MAPPERURL + "deleteData", ids);
		return i;
	}

	// 根据用户提供的数据源id，查询出通过这个id生成的job（包括job的id，及job的以配置）
	public List<TableEtlDO> listEtlMsg(DBSrcVO dbSrcVO) throws Exception{
		List<TableEtlDO> lists = getSqlSession().selectList(MAPPERURL + "listEtlMsg", dbSrcVO);
		return lists;
	}

	@Override
	public List<String> listIds(List<String> ids) throws Exception{
		List<String> lists = getSqlSession().selectList(MAPPERURL + "listIds", ids);
		return lists;
	}

	@Override
	public List<TableEtlDO> getAzkabanJobName(TableEtlDO tableEtlDO) throws Exception {
		List<TableEtlDO> tableEtl = getSqlSession().selectList(MAPPERURL+"getAzkabanJobName",tableEtlDO);
		return tableEtl;
	}
}

package com.hand.bdss.web.datasource.mydata.dao;

import java.util.List;

import com.hand.bdss.web.entity.TableEtlDO;
import com.hand.bdss.web.common.vo.DBSrcVO;

public interface DataSourceDao {
	public int insertData(TableEtlDO tableEtlDO) throws Exception;

	public int deleteData(List<String> ids) throws Exception;

	public List<TableEtlDO> listEtlMsg(DBSrcVO dbSrcVO) throws Exception;

	public List<String> listIds(List<String> ids)  throws Exception;

	public List<TableEtlDO> getAzkabanJobName(TableEtlDO tableEtlDO)  throws Exception;
}

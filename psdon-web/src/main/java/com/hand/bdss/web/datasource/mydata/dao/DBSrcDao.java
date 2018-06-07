package com.hand.bdss.web.datasource.mydata.dao;

import java.util.List;

import com.hand.bdss.web.common.vo.DBSrcVO;

public interface DBSrcDao {
	public int insertDBSrc(DBSrcVO dbSrcVO) throws Exception;

	/**
	 * 查询单个数据源信息
	 * @param dbSrcVO
	 * @return
	 * @throws Exception
	 */
	DBSrcVO getDBSrc(DBSrcVO dbSrcVO) throws Exception;

	public List<DBSrcVO> listDBSrcs(DBSrcVO dbSrcVO, int startPage, int count) throws Exception;

	public int getCounts(DBSrcVO dbSrcVO) throws Exception;

	public int updateDBSrcsById(DBSrcVO dbSrcVO) throws Exception;

	public int deleteDBSrc(List<String> ids) throws Exception;

	public int checkSrcName(DBSrcVO dbSrcVO) throws Exception;

}

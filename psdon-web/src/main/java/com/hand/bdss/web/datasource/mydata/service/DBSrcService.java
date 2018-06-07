package com.hand.bdss.web.datasource.mydata.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.common.vo.DBSrcVO;

public interface DBSrcService {
	int insertDBSrc(DBSrcVO dbSrcVO,HttpServletRequest request) throws Exception;

	/**
	 * 查询单个数据源信息
	 * @param dbSrcVO
	 * @return
	 * @throws Exception
	 */
	DBSrcVO getDBSrc(DBSrcVO dbSrcVO) throws Exception;

	List<DBSrcVO> listDBSrcs(DBSrcVO dbSrcVO, int startPage, int count) throws Exception;

	int getCounts(DBSrcVO dbSrcVO) throws Exception;

	int deleteDBSrcsById(List<String> ids, HttpServletRequest request) throws Exception;

	int updateDBSrcsById(DBSrcVO dbSrcVO, HttpServletRequest request) throws Exception;

	int checkSrcName(DBSrcVO dbSrcVO) throws Exception;

	int createDBSrc(DBSrcVO dbSrcVO) throws Exception;
}

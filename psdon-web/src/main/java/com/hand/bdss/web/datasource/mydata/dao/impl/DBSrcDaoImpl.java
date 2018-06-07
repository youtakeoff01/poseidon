package com.hand.bdss.web.datasource.mydata.dao.impl;

import java.util.List;

import com.hand.bdss.web.datasource.mydata.dao.DBSrcDao;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import com.hand.bdss.web.common.util.SupportDaoUtils;
import com.hand.bdss.web.common.vo.DBSrcVO;

@Repository("dBSrcDaoImpl")
public class DBSrcDaoImpl extends SupportDaoUtils implements DBSrcDao {

	private static final String MAPPERURL = "com.hand.bdss.web.common.vo.DBSrcVO.";

	@Override
	public int insertDBSrc(DBSrcVO dbSrcVO) throws Exception{
		int i = getSqlSession().insert(MAPPERURL + "insertDBsrc", dbSrcVO);
		return i;
	}

	@Override
	public DBSrcVO getDBSrc(DBSrcVO dbSrcVO) throws Exception {
		return getSqlSession().selectOne(MAPPERURL+"getDBSrc",dbSrcVO);
	}

	@Override
	public List<DBSrcVO> listDBSrcs(DBSrcVO dbSrcVO, int startPage, int count) throws Exception{
		RowBounds rowBounds = new RowBounds(startPage, count);
		List<DBSrcVO> lists = getSqlSession().selectList(MAPPERURL + "listDBSrcs", dbSrcVO, rowBounds);
		return lists;
	}

	@Override
	public int getCounts(DBSrcVO dbSrcVO) throws Exception{
		int counts = getSqlSession().selectOne(MAPPERURL + "getCounts", dbSrcVO);
		return counts;
	}

	@Override
	public int updateDBSrcsById(DBSrcVO dbSrcVO) throws Exception{
		int i = getSqlSession().update(MAPPERURL + "updateDBSrcsById", dbSrcVO);
		return i;
	}

	@Override
	public int deleteDBSrc(List<String> ids) throws Exception{
		int i = getSqlSession().delete(MAPPERURL + "deleteDBSrc", ids);
		return i;
	}

	@Override
	public int checkSrcName(DBSrcVO dbSrcVO) throws Exception{
		int i = getSqlSession().selectOne(MAPPERURL + "checkSrcName", dbSrcVO);
		return i;
	}

}

package com.hand.bdss.web.operationcenter.history.dao;

import java.util.List;
import java.util.Map;

import com.hand.bdss.web.entity.DataSyncHistoryEntity;

public interface DataTaskHistoryDao {

	List<DataSyncHistoryEntity> selectList(Map<String, Object> parmMap) throws Exception;

	int selectCount(Map<String, Object> parmMap) throws Exception;

	/**
	 * 根据jobName查询最近一次任务执行状态
	 * @return
	 * @throws Exception
	 */
	DataSyncHistoryEntity selectRecordLast(String jobName) throws Exception;

}

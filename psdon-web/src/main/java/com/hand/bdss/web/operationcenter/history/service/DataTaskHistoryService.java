package com.hand.bdss.web.operationcenter.history.service;

import java.util.List;
import java.util.Map;

import com.hand.bdss.web.entity.DataSyncHistoryEntity;

public interface DataTaskHistoryService {

    /**
     * 查询azkaban执行历史
     * @param parmMap
     * @return
     * @throws Exception
     */
    List<DataSyncHistoryEntity> selectList(Map<String, Object> parmMap) throws Exception;

    /**
     * 查询条数
     * @param parmMap
     * @return
     * @throws Exception
     */
    int selectCount(Map<String, Object> parmMap) throws Exception;

    /**
     *
     * @param jobId
     * @param execId
     * @return
     * @throws Exception
     */
    Map<String, Object> getLogDetail(String jobId, String execId) throws Exception;

    /**
     * 根据jobName查询最近一次任务信息
     * @return
     * @throws Exception
     */
    DataSyncHistoryEntity selectRecordLast(String jobName) throws Exception;
}

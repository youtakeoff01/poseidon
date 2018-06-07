package com.hand.bdss.web.operationcenter.history.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.hand.bdss.dev.service.IDevelopManager;
import com.hand.bdss.dev.service.impl.DevelopManager;
import com.hand.bdss.web.operationcenter.history.dao.DataTaskHistoryDao;
import com.hand.bdss.web.operationcenter.history.service.DataTaskHistoryService;
import org.springframework.stereotype.Service;

import com.hand.bdss.web.entity.DataSyncHistoryEntity;

@Service
public class DataTaskHistoryServiceImpl implements DataTaskHistoryService {

    @Resource
    private DataTaskHistoryDao dataTaskHistoryDaoImpl;

    private IDevelopManager manager = new DevelopManager();

    @Override
    public List<DataSyncHistoryEntity> selectList(Map<String, Object> parmMap)
            throws Exception {
        return dataTaskHistoryDaoImpl.selectList(parmMap);
    }

    @Override
    public int selectCount(Map<String, Object> parmMap) throws Exception {
        return dataTaskHistoryDaoImpl.selectCount(parmMap);
    }

    @Override
    public Map<String, Object> getLogDetail(String jobId, String execId) throws Exception {
        return manager.getJobLogs(jobId, execId);
    }

    @Override
    public DataSyncHistoryEntity selectRecordLast(String jobName) throws Exception {
        return dataTaskHistoryDaoImpl.selectRecordLast(jobName);
    }

}

package com.hand.bdss.web.dataprocessing.processing.service.impl;

import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.web.dataprocessing.processing.service.DataQueryService;
import com.hand.bdss.web.operationcenter.task.dao.TaskDao;
import com.hand.bdss.web.operationcenter.task.service.ShellService;
import org.apache.commons.collections4.list.TreeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by hand on 2017/8/4.
 */
@Service
public class DataQueryServiceImpl implements DataQueryService {

    @Autowired
    TaskDao taskDaoImpl;
    @Autowired
    ShellService shellServiceImpl;

    @Override
    @Transactional(rollbackFor=Exception.class)
    public boolean insert(Task task) {
        boolean flag = shellServiceImpl.createScript(task);
        if (flag) {
            try {
				taskDaoImpl.insert(task);
			} catch (Exception e) {
				e.printStackTrace();
			}
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> query(Task task) {
        Map<String, Object> retMap = new HashMap<>();
        List<Map<String, String>> list = shellServiceImpl.getQuery(task);
        List<Object> column = null;
        if (null != list && list.size() > 0) {
            Map<String, String> map = list.get(list.size() - 1);
            if(map.get("retCode") != null){
            	retMap.put("retCode", map.get("retCode"));
            	return retMap;
            }
            Set<String> keySet = map.keySet();
            column = new TreeList<>();
            for (Object key : keySet) {
                column.add(key);
            }
        }
        retMap.put("data", list);
        retMap.put("column", column);
        return retMap;
    }
    
    
    @Override
    public Map<String, Object> getSparkQuery(String dbName, String tbName, int limitNum) {
        Map<String, Object> retMap = new HashMap<>();
        List<Map<String, String>> list = shellServiceImpl.getSparkQuery(processSql(dbName, tbName), limitNum);
        List<Object> column = null;
        if (null != list && list.size() > 0) {
            Map<String, String> map = list.get(list.size() - 1);
            if (map.get("retCode") != null) {
                retMap.put("retCode", map.get("retCode"));
                return retMap;
            }
            Set<String> keySet = map.keySet();
            column = new TreeList<>();
            for (Object key : keySet) {
                column.add(key);
            }
        }
        retMap.put("data", list);
        retMap.put("column", column);
        return retMap;
    }
    
    /**
     * 装载sql
     *
     * @param dbName
     * @param tbName
     * @return
     */
    private String processSql(String dbName, String tbName) {
        return "select * from " + dbName + "." + tbName;
    }

}

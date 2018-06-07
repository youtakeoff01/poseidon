package com.hand.bdss.web.dataprocessing.processing.service;

import java.util.Map;

import com.hand.bdss.dev.vo.Task;

/**
 * Created by hand on 2017/8/4.
 */
public interface DataQueryService {

    /**
     * 新建任务
     * @param task
     */
    boolean insert(Task task);

    /**
     * 脚本查询
     * @param task
     */
    Map<String, Object> query(Task task);
    
    /**
     * hive 表血缘关系预览
     * 脚本查询
     *
     * @param dbName
     * @param tbName
     * @param limitNum
     * @return
     */
    Map<String, Object> getSparkQuery(String dbName, String tbName, int limitNum);
}

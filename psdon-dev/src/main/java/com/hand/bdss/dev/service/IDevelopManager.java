package com.hand.bdss.dev.service;

import com.hand.bdss.dev.model.ScheduleType;
import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.dev.vo.TaskScheduleInfo;

import java.util.List;
import java.util.Map;

/**
 * @author : Koala
 * @version : v1.0
 * @description :
 */
public interface IDevelopManager {

    /**
     * 执行查询结果
     *
     * @param task
     * @return
     */
    List<Map<String, String>> getQuery(Task task);

    /**
     * 创建脚本
     *
     * @param task
     */
    boolean createScript(Task task);

    /**
     * 执行脚本
     *
     * @param task
     */
    boolean executeScript(Task task);

    /**
     * 执行定时器脚本
     * @param task
     */
    boolean executeScheduleScript(TaskScheduleInfo task,ScheduleType initialize);


    /**
     * 删除脚本
     *
     * @param task
     * @return
     */
    boolean deleteScript(Task task);

    /**
     * 停止job
     *
     * @param task
     * @return
     */
    boolean stopProject(Task task);


    /**
     * 获取job日志
     *
     * @param jobId
     * @param execId
     * @return
     */
    Map<String, Object> getJobLogs(String jobId, String execId);
    
    /**
     * 执行查询结果
     *
     * @param
     * @return
     */
    List<Map<String, String>> getSparkQuery(String sql, int limitNum);

}
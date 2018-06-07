package com.hand.bdss.web.operationcenter.task.service;

import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.dev.vo.TaskScheduleInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by hand on 2017/8/4.
 */
public interface ShellService {

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
     * 立即执行脚本
     *
     * @param info
     */
    Map<String, Object> executeScript(Task info);

    /**
     * 执行定时器脚本
     * @param task
     */
    Map<String, Object> executeScheduleScript(TaskScheduleInfo task);
    /**
     * 停止执行脚本
     *
     * @param task
     */
    boolean stopProject(Task task);

    /**
     * 删除脚本
     * @param task
     * @return
     */
    boolean deleteScript(Task task);
    
    /**
     * table
     * 血缘关系预览
     *
     * @param sql
     * @param limitNum
     * @return
     */
    List<Map<String, String>> getSparkQuery(String sql, int limitNum);


}

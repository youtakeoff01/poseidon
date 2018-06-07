package com.hand.bdss.web.operationcenter.task.dao;

import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.dev.vo.TaskScheduleInfo;
import com.hand.bdss.web.operationcenter.task.vo.AITaskInfo;
import org.apache.spark.sql.execution.columnar.LONG;

import java.util.List;
import java.util.Map;

/**
 * Created by hand on 2017/8/4.
 */
public interface TaskDao {

    /**
     * 新建任务
     * @param task
     * @throws Exception 
     */
    boolean insert(Task task) throws Exception;

    /**
     * 更新任务
     * @param task
     */
    boolean update(Task task) throws Exception;

    /**
     * 删除任务
     * @param list
     */
    boolean deletes(List<String> list) throws Exception;

    /**
     * 删除任务
     * @param
     */
    boolean delete(Long id) throws Exception;

    /**
     * 查询任务（单）
     * @param id
     * @return
     */
    Task getTask(Long id) throws Exception;
    /**
     * 查询任务（单）
     * @param id
     * @return
     */
    TaskScheduleInfo getTaskScheduleInfo(Long id) throws Exception;
    /**
     * 获取任务列表
     * @return
     */
    List<Task> selects(Task task) throws Exception;

    /**
     * 获取脚本任务列表
     * @return
     */
    List<Task> selectScrpList(Task task, int startPage, int count) throws Exception;
    /**
     * 获取同步任务列表
     * @return
     */
    List<Task> selectSyncList(Task task, int startPage, int count) throws Exception;
    /**
     * 获取列表
     * @return
     */
    List<AITaskInfo> selectAIList(Task task, int startPage, int count) throws Exception;
    /**
     * 获取总数
     * @param task
     * @return
     */
    Integer selectCount(Task task) throws Exception;

    /**
     * 校验个数
     * @param task
     * @return
     */
    Integer check(Task task) throws Exception;
}

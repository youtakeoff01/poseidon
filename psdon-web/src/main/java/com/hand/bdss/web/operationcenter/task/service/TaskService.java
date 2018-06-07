package com.hand.bdss.web.operationcenter.task.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.directory.api.util.exception.Exceptions;

import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.dev.vo.TaskScheduleInfo;
import com.hand.bdss.web.operationcenter.task.vo.AITaskInfo;

/**
 * Created by hand on 2017/8/4.
 */
public interface TaskService {

    /**
     * 获取脚本任务列表
     * @return
     * @throws Exception 
     */
    List<Task> selects(Task task) throws Exception;

    /**
     * 获取脚本任务列表
     * @return
     * @throws Exception 
     */
    List<Task> selectScrpList(Task task, int startPage, int count) throws Exception;
    /**
     * 获取同步任务列表
     * @return
     * @throws Exception 
     */
    List<Task> selectSyncList(Task task, int startPage, int count) throws Exception;
    /**
     * 获取列表
     * @return
     * @throws Exception 
     */
    List<AITaskInfo> selectAIList(Task task, int startPage, int count) throws Exception;

    /**
     * 获取总数
     * @param task
     * @return
     * @throws Exception 
     */
    Integer selectCount(Task task) throws Exception;

    /**
     * 更新&执行定时脚本任务
     * @param task
     */
    Map<String, Object> executeScheduleScript(TaskScheduleInfo task);

    /**
     * 删除任务(批量)
     * @param list
     * @throws Exceptions 
     */
	void deletes(List<String> list, HttpServletRequest req) throws Exception;
    /**
     * 删除任务(单个)
     * @param id
     * @throws Exception 
     */
    void delete(Long id) throws Exception;

    /**
     * 执行脚本
     * @param id
     */
    Map<String, Object> execute(Long id,HttpServletRequest req);
    
    /**
	 * 执行脚本<指定时间>
	 * @param task
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> scheduleTask(Task task,HttpServletRequest req) throws Exception;
	
    /**
     * 取消执行脚本
     * @param id
     * @throws Exception 
     */
    boolean stop(Long id) throws Exception;

    /**
     * 校验个数
     * @param taskName
     * @return
     */
    boolean check(String taskName) throws Exception;

	Task selectTask(Task task) throws Exception;

}

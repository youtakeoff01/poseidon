package com.hand.bdss.dev.service.impl;

import com.hand.bdss.dev.model.ScheduleType;
import com.hand.bdss.dev.scheduler.AzkabanScheduler;
import com.hand.bdss.dev.scheduler.QueryScheduler;
import com.hand.bdss.dev.service.IDevelopManager;
import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.dev.vo.TaskScheduleInfo;

import java.util.List;
import java.util.Map;

/**
 * @author : Koala
 * @version : v1.0
 * @description :
 */
public class DevelopManager implements IDevelopManager {

    @Override
    public List<Map<String, String>> getQuery(Task task) {
        QueryScheduler queryScheduler = new QueryScheduler();
        return queryScheduler.queryScheduler(task);
    }

    @Override
    public boolean createScript(Task task) {
        AzkabanScheduler azkabanScheduling = new AzkabanScheduler();
        return azkabanScheduling.createScheduling(task, ScheduleType.initialize);
    }

    @Override
    public boolean executeScript(Task task) {
        AzkabanScheduler azkabanScheduling = new AzkabanScheduler();
        return azkabanScheduling.executeScheduling(task, ScheduleType.region);
    }

    @Override
    public boolean executeScheduleScript(TaskScheduleInfo task,ScheduleType initialize) {
        AzkabanScheduler azkabanScheduling = new AzkabanScheduler();
        return azkabanScheduling.executeScheduleScript(task, initialize);
    }

    @Override
    public boolean deleteScript(Task task) {
        AzkabanScheduler azkabanScheduling = new AzkabanScheduler();
        azkabanScheduling.stopJob(task,ScheduleType.region);
        azkabanScheduling.stopJob(task,ScheduleType.initializeKill);
        azkabanScheduling.stopJob(task,ScheduleType.initialize);
        azkabanScheduling.deleteScheduling(task, ScheduleType.region);
        return azkabanScheduling.deleteScheduling(task, ScheduleType.initialize);
    }

    @Override
    public boolean stopProject(Task task) {
        AzkabanScheduler azkabanScheduling = new AzkabanScheduler();
        return azkabanScheduling.stopJob(task, ScheduleType.region);
    }

    @Override
    public Map<String, Object> getJobLogs(String jobId, String execId) {
        AzkabanScheduler azkabanScheduling = new AzkabanScheduler();
        return azkabanScheduling.getJobLogs(jobId, execId);
    }
    
    @Override
    public List<Map<String, String>> getSparkQuery(String sql, int limitNum) {

        QueryScheduler queryScheduler = new QueryScheduler();
        return queryScheduler.sparkLimitQueryScheduler(sql, limitNum);
    }


}

package com.hand.bdss.web.operationcenter.task.service.impl;

import com.hand.bdss.dev.model.ScheduleType;
import com.hand.bdss.dev.service.IDevelopManager;
import com.hand.bdss.dev.service.impl.DevelopManager;
import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.dev.vo.TaskScheduleInfo;
import com.hand.bdss.web.common.em.TaskStatus;
import com.hand.bdss.web.entity.DataSyncHistoryEntity;
import com.hand.bdss.web.operationcenter.history.service.DataTaskHistoryService;
import com.hand.bdss.web.operationcenter.task.service.ShellService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hand on 2017/8/4.
 */
@Service
public class ShellServiceImpl implements ShellService {

    private static final Logger logger = LoggerFactory.getLogger(ShellServiceImpl.class);

    private IDevelopManager manager = new DevelopManager();
    @Autowired
    private DataTaskHistoryService dataTaskHistoryServiceImpl;

    /**
     * 预览脚本
     *
     * @param task
     * @return
     */
    @Override
    public List<Map<String, String>> getQuery(Task task) {
        return manager.getQuery(task);
    }

    /**
     * 生成脚本
     *
     * @param task
     * @return
     */
    @Override
    public boolean createScript(Task task) {
        return manager.createScript(task);
    }

    /**
     * 立即执行脚本
     *
     * @param task
     * @return
     */
    @Override
    public Map<String, Object> executeScript(Task task) {
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("returnCode", "0");
        String jobName = task.getTaskName() + "-" + task.getTaskType() + "-" + task.getSqlType()+"-region";
        DataSyncHistoryEntity entity = null;
        try {
            entity = dataTaskHistoryServiceImpl.selectRecordLast(jobName);
            if (entity != null && TaskStatus.run.getIndex().toString().equals(entity.getJobState())) {
                retMap.put("returnMessage", "任务正在执行，请稍后");
                return retMap;
            }
            if (manager.executeScript(task)) {
                retMap.put("returnMessage", "执行脚本任务成功！");
                retMap.put("returnCode", "1");
            } else {
                retMap.put("returnMessage", "执行脚本任务失败！");
            }
        } catch (Exception e) {
            logger.error("executeScript error!", e);
            retMap.put("returnMessage", "执行脚本任务异常！");
        }
        return retMap;
    }

    /**
     * 执行定时任务
     *
     * @param task
     * @return
     */
    @Override
    public Map<String, Object> executeScheduleScript(TaskScheduleInfo task) {
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("returnCode", "0");
        String jobName = task.getTaskName() + "-" + task.getTaskType() + "-" + task.getSqlStc();
        try {
            DataSyncHistoryEntity entity = dataTaskHistoryServiceImpl.selectRecordLast(jobName);
            if (entity != null && TaskStatus.run.getIndex().toString().equals(entity.getJobState())) {
                retMap.put("returnMessage", "任务正在执行，请稍后");
                return retMap;
            }
            if (manager.executeScheduleScript(task, ScheduleType.initialize)) {
                retMap.put("returnMessage", "执行定时任务成功！");
                retMap.put("returnCode", "1");
            } else {
                retMap.put("returnMessage", "执行定时任务失败！");
            }
        } catch (Exception e) {
            logger.error("executeScheduleScript error!", e);
            retMap.put("returnMessage", "执行定时任务异常！");
        }
        return retMap;
    }

    /**
     * 取消执行脚本
     *
     * @param task
     * @return
     */
    @Override
    public boolean stopProject(Task task) {
        return manager.stopProject(task);
    }

    /**
     * 删除脚本
     *
     * @param task
     * @return
     */
    @Override
    public boolean deleteScript(Task task) {
        return manager.deleteScript(task);
    }
    
    /**
     * 表
     * 血缘关系预览
     * @param sql
     * @param limitNum
     * @return
     */
    @Override
    public List<Map<String, String>> getSparkQuery(String sql, int limitNum) {
        return manager.getSparkQuery(sql, limitNum);
    }

}

package com.hand.bdss.web.operationcenter.task.dao.impl;

import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.dev.vo.TaskScheduleInfo;
import com.hand.bdss.web.common.util.SupportDaoUtils;
import com.hand.bdss.web.entity.MetaDataEntity;
import com.hand.bdss.web.operationcenter.task.dao.TaskDao;
import com.hand.bdss.web.operationcenter.task.vo.AITaskInfo;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by hand on 2017/8/4.
 */
@Repository
public class TaskDaoImpl extends SupportDaoUtils implements TaskDao {
    private static final String MAPPERURL = "com.hand.bdss.web.entity.TaskEntity.";

    @Override
    public boolean insert(Task task) throws Exception{
        int flag = getSqlSession().insert(MAPPERURL + "insert", task);
        if (flag >= 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Task task) throws Exception{
        int flag = getSqlSession().update(MAPPERURL + "update", task);
        if (flag >= 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deletes(List<String> list) throws Exception{
        int flag = getSqlSession().delete(MAPPERURL + "deletes", list);
        if (flag >= 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Long id) throws Exception{
        int flag = getSqlSession().delete(MAPPERURL + "delete", id);
        if (flag >= 0) {
            return true;
        }
        return false;
    }

    @Override
    public Task getTask(Long id) throws Exception{
        return getSqlSession().selectOne(MAPPERURL + "select", id);
    }
    @Override
    public TaskScheduleInfo getTaskScheduleInfo(Long id) throws Exception{
        return getSqlSession().selectOne(MAPPERURL + "getTaskScheduleInfo", id);
    }
    @Override
    public List<Task> selects(Task task) throws Exception{
        return getSqlSession().selectList(MAPPERURL + "selects",task);
    }

    @Override
    public List<Task> selectScrpList(Task task, int startPage, int count) throws Exception{
        RowBounds rowBounds = new RowBounds(startPage, count);
        List<Task> lists = getSqlSession().selectList(MAPPERURL + "selectScrpList", task, rowBounds);
        return lists;
    }

    @Override
    public List<Task> selectSyncList(Task task, int startPage, int count) throws Exception{
        RowBounds rowBounds = new RowBounds(startPage, count);
        List<Task> lists = getSqlSession().selectList(MAPPERURL + "selectSyncList", task, rowBounds);
        return lists;
    }

    @Override
    public List<AITaskInfo> selectAIList(Task task, int startPage, int count) throws Exception{
        RowBounds rowBounds = new RowBounds(startPage, count);
        List<AITaskInfo> lists = getSqlSession().selectList(MAPPERURL + "selectAIList", task, rowBounds);
        return lists;
    }

    @Override
    public Integer selectCount(Task task) throws Exception{
        return getSqlSession().selectOne(MAPPERURL + "selectCount", task);
    }
    @Override
    public Integer check(Task task) throws Exception{
        return getSqlSession().selectOne(MAPPERURL + "check", task);
    }
}

package com.hand.bdss.web.operationcenter.task.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.hand.bdss.dev.util.JacksonUtil;
import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.dev.vo.TaskScheduleInfo;
import com.hand.bdss.web.common.em.TaskType;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.util.StringUtils;
import com.hand.bdss.web.common.util.request.RequestThreadHead;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.operationcenter.task.service.TaskService;
import com.hand.bdss.web.operationcenter.task.vo.AITaskInfo;

/**
 * Created by hand on 2017/8/2.
 */
@Controller
@RequestMapping(value = "/TaskController/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Resource
    TaskService taskServiceImpl;
    @Resource
    private LogUtils logUtils;

    /**
     * 任务列表查询
     *
     * @param
     * @return
     */
    @RequestMapping("selectList")
    public @ResponseBody
    String selectList(@RequestBody String paramJson, HttpServletRequest request) {
        logger.info(RequestThreadHead.getLogStr(request) + "TaskController selectList paramJson=" + paramJson);
        BaseResponse base = new BaseResponse();
        base.setReturnCode("0");
        try {
            if (StringUtils.isBlank(paramJson)) {
                base.setReturnMessage("参数为空！");
                return base.toString();
            }
            JsonNode jsonNode = JacksonUtil.getJsonNode(paramJson);
            String taskType = jsonNode.get("taskType").asText();
            JsonNode taskName = jsonNode.get("taskName");
            
            int startPage = jsonNode.get("startPage").asInt();
            int count = jsonNode.get("count").asInt();
            Task task = new Task();
            task.setTaskType(taskType);
            //判断是否超级管理员
            if(!GetUserUtils.isRootUser(request)){
            	task.setAccount(Long.parseLong(GetUserUtils.getUser(request).getId()+""));
            }

            if (taskName != null)
                task.setTaskName(taskName.asText());

            Map<String, Object> retMap = new HashMap<>();
            //同步任务
            if (TaskType.SYNC.getIndex() == Integer.parseInt(taskType)) {
                List<Task> list = taskServiceImpl.selectSyncList(task, (startPage - 1) * 10, count);
                retMap.put("list", list);
            }
            //脚本任务
            if (TaskType.SCRIPT.getIndex() == Integer.parseInt(taskType)) {
                List<Task> list = taskServiceImpl.selectScrpList(task, (startPage - 1) * 10, count);
                retMap.put("list", list);
            }
            //机器学习
            if (TaskType.AI.getIndex() == Integer.parseInt(taskType)) {
                List<AITaskInfo> list = taskServiceImpl.selectAIList(task, (startPage - 1) * 10, count);
                retMap.put("list", list);
            }
            Integer total = taskServiceImpl.selectCount(task);
            retMap.put("total", total);
            base.setReturnObject(retMap);
            base.setReturnCode("1");
            base.setReturnMessage("任务列表查询成功!");
        } catch (Exception e) {
            logger.error("selectList error!", e);
            base.setReturnMessage("任务列表查询失败");
        }

        return base.toString();
    }

    /**
     * 任务列表查询
     *
     * @param
     * @return
     */
    @RequestMapping("selects")
    public @ResponseBody
    String selects(@RequestBody String paramJson, HttpServletRequest request) {
        logger.info(RequestThreadHead.getLogStr(request) + "TaskController selects paramJson=" + paramJson);
        BaseResponse base = new BaseResponse();
        base.setReturnCode("0");
        try {
            if (StringUtils.isBlank(paramJson)) {
                base.setReturnMessage("参数为空！");
                return base.toString();
            }
            JsonNode jsonNode = JacksonUtil.getJsonNode(paramJson);
            String taskType = jsonNode.get("taskType").asText();
            Task task = new Task();
            task.setTaskType(taskType);

            List<Task> list = taskServiceImpl.selects(task);
            base.setReturnObject(list);
            base.setReturnCode("1");
            base.setReturnMessage("任务查询成功!");
        } catch (Exception e) {
            logger.error("selects error!", e);
            base.setReturnMessage("任务查询失败");
        }

        return base.toString();
    }

    /**
     * 获取任务详情
     *
     * @param task
     * @return
     */
    @RequestMapping("getTask")
    public @ResponseBody
    String getTask(@RequestBody Task task) {
        logger.info("TaskController.getTask param = " + task.toString());
        BaseResponse base = new BaseResponse();
        base.setReturnCode("0");
        try {
            Task task2 = taskServiceImpl.selectTask(task);
            if (TaskType.AI.getIndex() == Integer.parseInt(task2.getTaskType())) {
                Map map = JSON.parseObject(task2.getScript());
                if (map != null && !map.isEmpty()) {
                    base.setReturnCode("1");
                    base.setReturnMessage("获取任务详情成功");
                    base.setReturnObject(map);
                } else {
                    base.setReturnMessage("获取任务详情失败");
                }
            }
        } catch (Exception e) {
            logger.error("TaskController.getTask error!", e);
            base.setReturnMessage("获取任务详情异常");
            return base.toString();
        }
        logger.info("TaskController.getTask end! response = {" + base.getReturnMessage() + "}");
        return base.toString();
    }

    /**
     * 任务更新设置定时
     *
     * @param task
     * @return
     */
    @RequestMapping("updateTask")
    public @ResponseBody
    String updateTask(@RequestBody TaskScheduleInfo task, HttpServletRequest request) {
        logger.info("TaskController updateTask task=" + task.toString());
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("returnCode", "0");
        try {
            //定时器修改，清空邮件字段
            if (StringUtils.isEmpty("task.getTimerAttribute()")) {
                task.setEmailStr("");
            }
            retMap = taskServiceImpl.executeScheduleScript(task);
        } catch (Exception e) {
            logger.error("updateTask error!", e);
            retMap.put("returnMessage", "任务更新失败！");
        } finally {
            logUtils.writeLog("定时任务更新: " + task.toString(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
        }
        return JacksonUtil.beanToJson(retMap);
    }

    /**
     * 删除任务(单个删除)
     *
     * @param
     * @return
     */
    @RequestMapping("deleteTask")
    public @ResponseBody
    String deleteTask(@RequestBody String json,HttpServletRequest req) {
        logger.info("TaskController deleteTask param=" + json);
        BaseResponse base = new BaseResponse();
        base.setReturnCode("0");
        try {
            if (StringUtils.isEmpty(json)) {
                base.setReturnMessage("参数为空！");
                return base.toString();
            }
            List<String> list = JsonUtils.toArray(json, String.class);
            taskServiceImpl.deletes(list,req);
            base.setReturnCode("1");
            base.setReturnMessage("任务删除成功！");
        } catch (Exception e) {
            logger.error("deleteTask error!", e);
            base.setReturnMessage("删除任务失败！");
        }
        return base.toString();
    }

    /**
     * 立即任务执行
     *
     * @param
     * @return
     */
    @RequestMapping("executeTask")
    @ResponseBody
    public String executeTask(@RequestBody String id, HttpServletRequest req) {
        logger.info("TaskController executeTask ids=" + id);
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("returnCode", "0");
        try {
            if (StringUtils.isBlank(id)) {
                retMap.put("returnMessage", "参数为空！");
                return JacksonUtil.beanToJson(retMap);
            }
            JsonNode jsonNode = JacksonUtil.getJsonNode(id);
            Long idl = jsonNode.get("id").asLong();
            retMap = taskServiceImpl.execute(idl, req);
        } catch (Exception e) {
            logger.error("executeTask error!", e);
            retMap.put("returnMessage", "立即任务执行异常！");
        }
        return JacksonUtil.beanToJson(retMap);
    }
    
    
    /**
     * 立即执行任务<指定时间>
     * @param task
     * @return
     */
    @RequestMapping("scheduleTask")
    @ResponseBody
    public String scheduleTask(@RequestBody Task task,HttpServletRequest req){
        logger.info("TaskController scheduleTask param = {}",task);
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("returnCode", "0");
    	try{
    		retMap = taskServiceImpl.scheduleTask(task,req);
    	}catch(Exception e){
    		logger.error("TaskController scheduleTask error !",e);
    		retMap.put("returnMessage","任务执行失败");
    	}
    	return JacksonUtil.beanToJson(retMap);
    }

    
    /**
     * 校验任务名称唯一性
     *
     * @param
     * @return
     */
    @RequestMapping("checkTask")
    @ResponseBody
    public String checkTask(@RequestBody String json) {
        logger.info("TaskController checkTask json=" + json);
        BaseResponse base = new BaseResponse();
        base.setReturnCode("0");
        try {
            if (StringUtils.isBlank(json)) {
                base.setReturnMessage("参数为空！");
                return base.toString();
            }
            JsonNode jsonNode = JacksonUtil.getJsonNode(json);
            String taskName = jsonNode.get("taskName").asText();
            boolean flag = taskServiceImpl.check(taskName);
            if (flag) {
                base.setReturnCode("0");
                base.setReturnMessage("任务名称已存在！");
            } else {
                base.setReturnCode("1");
                base.setReturnMessage("任务名称不存在！");
            }
        } catch (Exception e) {
            logger.error("checkTask error!", e);
            base.setReturnMessage("任务名称校验失败！");
        }
        return base.toString();
    }

    /**
     * 停止任务
     *
     * @param
     * @return
     */
    @RequestMapping("stopTask")
    @ResponseBody
    public String stopTask(@RequestBody String id) {
        logger.info("TaskController stopTask ids=" + id);
        BaseResponse base = new BaseResponse();
        base.setReturnCode("0");
        try {
            if (StringUtils.isBlank(id)) {
                base.setReturnMessage("参数为空！");
                return base.toString();
            }
            JsonNode jsonNode = JacksonUtil.getJsonNode(id);
            Long idl = jsonNode.get("id").asLong();
            taskServiceImpl.stop(idl);
            base.setReturnCode("1");
            base.setReturnMessage("任务停止成功！");
        } catch (Exception e) {
            logger.error("stopTask error!", e);
            base.setReturnMessage("任务停止失败！");
        }
        return base.toString();
    }
}

package com.hand.bdss.dev.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hand.bdss.dev.data.script.Script;
import com.hand.bdss.dev.model.ScheduleType;
import com.hand.bdss.dev.util.JacksonUtil;
import com.hand.bdss.dev.util.ScriptUtil;
import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.dev.vo.TaskScheduleInfo;
import com.hand.bdss.dsmp.service.etl.ETLManager;
import com.hand.bdss.task.config.SystemConfig;
import com.hand.bdss.task.service.IAzkabanManger;
import com.hand.bdss.task.service.impl.AzkabanManger;

/**
 * @author : Koala
 * @version : v1.0
 * @description :
 */
public class AzkabanScheduler {
    private static final Logger logger = LoggerFactory.getLogger(AzkabanScheduler.class);
    private IAzkabanManger azkabanManger = new AzkabanManger();
    private ETLManager etlManager = new ETLManager();

    /**
     * 创建脚本
     *
     * @param task
     * @return
     */
    public boolean createScheduling(Task task, ScheduleType scheduleType) {
        logger.info("AzkabanScheduler.createScheduling param=" + task.toString() + "  " + scheduleType);
        Script script = new Script();
        String sqlType = task.getSqlType();
        String dependencies = task.getDependencies();
        try {
            boolean exitScript = script.createScript(task, scheduleType);
            if (!exitScript) {
                logger.info("createScheduling : Create region script failure!");
                return false;
            }
            String jobOfScriptAndProjectName = ScriptUtil.getJobOfScriptName(task, scheduleType);
            if (StringUtils.isBlank(jobOfScriptAndProjectName)) {
                logger.info("executeScheduleScript Get jobOfScriptAndProjectName is faild!");
                return false;
            }
//            boolean existsLogin = azkabanManger.isLogin();
//            if (!existsLogin) {
//                logger.info("createScheduling existsLogin fail!");
//                return false;
//            }
            boolean existsProject = azkabanManger.createProject(jobOfScriptAndProjectName, sqlType);

            if (!existsProject) {
                azkabanManger.deleteProject(jobOfScriptAndProjectName);
                azkabanManger.createProject(jobOfScriptAndProjectName, sqlType);
            }
            return this.uploadProject(jobOfScriptAndProjectName, dependencies,null);
        } catch (Exception e) {
            logger.error("createScheduling error!", e);
        }
        return false;

    }

    /**
     * 执行脚本
     *
     * @param task
     * @return
     */
    public boolean executeScheduling(Task task, ScheduleType scheduleType) {
        logger.info("AzkabanScheduler.executeScheduling param=" + task.toString() + "  " + scheduleType);
        String jobOfScriptAndProjectName = ScriptUtil.getJobOfScriptName(task, scheduleType);
        if (StringUtils.isBlank(jobOfScriptAndProjectName)) {
            logger.info("executeScheduleScript Get jobOfScriptAndProjectName is faild!");
            return false;
        }
        String taskType = task.getTaskType();
        if ("0".equalsIgnoreCase(taskType)) {
            try {
                String taskJson = JacksonUtil.beanToJson(task);
                etlManager.taskRun(taskJson);
                return this.exec(jobOfScriptAndProjectName);
            } catch (Exception e) {
                logger.info("executeScheduling   Creat Sqoop  Script fail!", e);
            }
        } else if ("1".equalsIgnoreCase(taskType)) {
            boolean regionScript = this.createRegionScript(task, scheduleType);
            if (!regionScript) {
                logger.info("executeScheduling  createRegionScript  fail!");
            }
            return this.exec(jobOfScriptAndProjectName);
        } else if ("2".equalsIgnoreCase(taskType)) {
            logger.info("executeScheduling taskType  AI!");
        } else {
            logger.info("executeScheduling taskType  nonsupport!");
        }
        return false;
    }

    /**
     * 设置定时器
     *
     * @param task
     * @return
     */

    public boolean executeScheduleScript(TaskScheduleInfo task, ScheduleType scheduleType) {
        logger.info("AzkabanScheduler.deleteScheduling param=" + task.toString() + "  " + scheduleType);
        String jobOfScriptAndProjectName = ScriptUtil.getJobOfScriptName(task, scheduleType);
        if (StringUtils.isBlank(jobOfScriptAndProjectName)) {
            logger.info("executeScheduleScript Get jobOfScriptAndProjectName is faild!");
            return false;
        }
        try {
            String timerAttribute = task.getTimerAttribute();
            if (StringUtils.isNotEmpty(timerAttribute)) {
                boolean flag = azkabanManger.setScheduleFlow(jobOfScriptAndProjectName, jobOfScriptAndProjectName, timerAttribute);
                if (flag) {
                    if (StringUtils.isNotEmpty(task.getEmailStr())) {
                        String scheduleId = azkabanManger.getScheduleId(jobOfScriptAndProjectName);
                        if (StringUtils.isNotEmpty(scheduleId)) {
                            return azkabanManger.setJobFailMail(scheduleId, task.getEmailStr(), task.getForecastTime());
                        }
                    }
                }
            } else {
                String scheduleId = azkabanManger.getScheduleId(jobOfScriptAndProjectName);
                if (StringUtils.isNotBlank(scheduleId)) {
                    return azkabanManger.unScheduleFlow(scheduleId);
                }
            }

        } catch (Exception e) {
            logger.error("AzkabanScheduler.executeScheduleScript : ", e);
        }
        return true;
    }

    /**
     * 删除脚本
     *
     * @param task
     * @return
     */
    public boolean deleteScheduling(Task task, ScheduleType scheduleType) {
        logger.info("AzkabanScheduler.deleteScheduling param=" + task.toString() + "  " + scheduleType);
        String azkabanJobPath = SystemConfig.AZKABAN_JOB_PATH;
        String scriptSQLPath = SystemConfig.SCRIPT_SQL_PATH;
        String scriptSqoopPath = SystemConfig.SCRIPT_SQOOP_PATH;
        String jobOfScriptAndProjectName = ScriptUtil.getJobOfScriptName(task, scheduleType);
        if (StringUtils.isBlank(jobOfScriptAndProjectName)) {
            logger.info("executeScheduleScript Get jobOfScriptAndProjectName is faild!");
            return false;
        }
        try {
//            boolean existsLogin = azkabanManger.isLogin();
//            if (existsLogin) {
        	boolean deleteProject = azkabanManger.deleteProject(jobOfScriptAndProjectName);
        	if (!deleteProject) {
        		logger.info("AzkabanScheduler.deleteScheduling :  Delete Azkaban project failure！");
        		return false;
            }
            ScriptUtil.deleteFile(scriptSQLPath, jobOfScriptAndProjectName, ".sh");
            ScriptUtil.deleteFile(scriptSQLPath, jobOfScriptAndProjectName, ".job");
            ScriptUtil.deleteFile(scriptSqoopPath, jobOfScriptAndProjectName, ".sh");
            ScriptUtil.deleteFile(scriptSqoopPath, jobOfScriptAndProjectName, ".job");
            ScriptUtil.deleteFile(azkabanJobPath, jobOfScriptAndProjectName, ".zip");
            return true;
//            }
        } catch (Exception e) {
            logger.error("AzkabanScheduler.deleteScheduling : ", e);
        }
        return false;
    }

    /**
     * 停止job
     *
     * @param task
     * @return
     */
    public boolean stopJob(Task task, ScheduleType scheduleType) {
        logger.info("AzkabanScheduler.stopJob  param=" + task.toString() + "  " + scheduleType);
        String jobOfScriptAndProjectName = ScriptUtil.getJobOfScriptName(task, scheduleType);
        if (StringUtils.isBlank(jobOfScriptAndProjectName)) {
            logger.info("executeScheduleScript Get jobOfScriptAndProjectName is faild!");
            return false;
        }
        try {
            switch (scheduleType) {
                case region:
                    String runningExecIds = azkabanManger.getRunningExecIds(jobOfScriptAndProjectName);
                    if (StringUtils.isNotBlank(runningExecIds)) {
                        return azkabanManger.cancelRunningFlow(runningExecIds);
                    }
                case initializeKill:
                    String runningId = azkabanManger.getRunningExecIds(jobOfScriptAndProjectName);
                    if (StringUtils.isNotBlank(runningId)) {
                        return azkabanManger.cancelRunningFlow(runningId);
                    }
                case initialize:
                    String scheduleId = azkabanManger.getScheduleId(jobOfScriptAndProjectName);
                    if (StringUtils.isNotBlank(scheduleId)) {
                        return azkabanManger.unScheduleFlow(scheduleId);
                    }
                default:
                    logger.info("stopJob  scheduleType is nonsupport");
                    return false;
            }
        } catch (Exception e) {
            logger.error("AzkabanScheduler.stopJob : Stop JOB failure！");
        }
        logger.info("AzkabanScheduler.stopJob end!");
        return true;
    }


    /**
     * 执行任务
     *
     * @param jobOfScriptAndProjectName
     * @return
     */
    private boolean exec(String jobOfScriptAndProjectName) {
        try {
//            boolean existsLogin = azkabanManger.isLogin();
//            if (!existsLogin) {
//                logger.info("exec Azkaban Login fail!");
//                return false;
//            }
            return azkabanManger.executeFlow(jobOfScriptAndProjectName, jobOfScriptAndProjectName);
        } catch (Exception e) {
            logger.error("AzkabanScheduler.executeScheduling : ", e);
        }
        return false;
    }

    /**
     * 上传Azkaban file
     *
     * @param jobOfScriptAndProjectName
     * @return
     * @throws Exception
     */
    public boolean uploadProject(String jobOfScriptAndProjectName, String dependencies,String userParams) throws Exception {
        boolean existsJob = ScriptUtil.existsFile(SystemConfig.SCRIPT_SQL_PATH, jobOfScriptAndProjectName, ".job");
        if (existsJob) {
            ScriptUtil.deleteFile(SystemConfig.SCRIPT_SQL_PATH, jobOfScriptAndProjectName, ".job");
        }
        if (null == userParams) {
            azkabanManger.createJob(jobOfScriptAndProjectName, SystemConfig.SCRIPT_SQL_PATH, dependencies);
        }else {
            azkabanManger.createJobWithParams(jobOfScriptAndProjectName,SystemConfig.SCRIPT_SQL_PATH,dependencies,userParams);
        }
        boolean existsZip = ScriptUtil.existsFile(SystemConfig.SCRIPT_SQL_PATH, jobOfScriptAndProjectName, ".zip");
        if (existsZip) {
            ScriptUtil.deleteFile(SystemConfig.SCRIPT_SQL_PATH, jobOfScriptAndProjectName, ".zip");
        }
        StringBuilder filesName = new StringBuilder();
        filesName.append(SystemConfig.SCRIPT_SQL_PATH).append("/").append(jobOfScriptAndProjectName).append(".sh,");
        filesName.append(SystemConfig.SCRIPT_SQL_PATH).append("/").append(jobOfScriptAndProjectName).append(".job");
        if (StringUtils.isNotEmpty(dependencies)) {
            String[] depends = dependencies.split(",");
            for (String d : depends) {
                filesName.append(",");
                filesName.append(SystemConfig.SCRIPT_SQOOP_PATH).append("/").append(d).append("-0-sqoop.sh,");
                filesName.append(SystemConfig.SCRIPT_SQOOP_PATH).append("/").append(d).append("-0-sqoop.job");
            }
        }
        boolean existsFileToZip = azkabanManger.filesToZip(jobOfScriptAndProjectName, filesName.toString(), SystemConfig.AZKABAN_JOB_PATH);
        if (existsFileToZip) {
            boolean existsUploadJar = azkabanManger.uploadZip(SystemConfig.AZKABAN_JOB_PATH, jobOfScriptAndProjectName, jobOfScriptAndProjectName);
            if (existsUploadJar) {
                return true;
            } else {
                logger.info("createScheduling : Azkaba upload Zip failure!");
            }
        } else {
            logger.info("AzkabanScheduler.createScheduling : Azkaban filesToZip  failure!");
        }
        return false;
    }


    /**
     * 创建临时脚本
     *
     * @param task
     * @return
     */
    private boolean createRegionScript(Task task, ScheduleType scheduleType) {
        boolean createSchedulingFlag = createScheduling(task, scheduleType);
        if (createSchedulingFlag) {
            return true;
        } else {
            logger.info("executeScheduling :  Create region script failure！");
        }
        return false;
    }

    /**
     * 获取job Logs
     *
     * @param jobId
     * @param execId
     * @return
     */
    public Map<String, Object> getJobLogs(String jobId, String execId) {
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("returnCode", "0");
        try {
//            boolean isLogin = azkabanManger.isLogin();
//            if (!isLogin) {
//                logger.info("getJobLogs Azkaban Login fail!");
//                retMap.put("returnMessage", "azkaban login faild!");
//                return retMap;
//            }
            String result = azkabanManger.getExecutionJobLogs(jobId, execId);
            retMap.put("returnObject", result);
            retMap.put("returnMessage", "success");
            retMap.put("returnCode", "1");
        } catch (Exception e) {
            logger.info("getJobLogs error!", e);
            retMap.put("returnMessage", "error");
        }
        return retMap;
    }

}

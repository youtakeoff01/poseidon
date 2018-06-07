package com.hand.bdss.dev.data.script;

import com.hand.bdss.dev.model.ScheduleType;
import com.hand.bdss.dev.util.FormatDate;
import com.hand.bdss.dev.util.ScriptUtil;
import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.task.config.SystemConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * @author : Koala
 * @version : v1.0
 * @description :
 */
public class Script {
    private static final Logger logger = LoggerFactory.getLogger(Script.class);
    private String newLine = "\n";
    private FormatDate formatDate = new FormatDate();

    public boolean createScript(Task task, ScheduleType scheduleType) throws IOException {
        logger.info("Script.createScript param={}", task.toString());
        boolean flagFile = true;
        String sqlType = task.getSqlType();
        String jobOfScriptName = ScriptUtil.getJobOfScriptName(task, scheduleType);
        boolean existsSH = ScriptUtil.existsFile(SystemConfig.SCRIPT_SQL_PATH, jobOfScriptName, ".sh");
        if (existsSH) {
            boolean deleteSH = ScriptUtil.deleteFile(SystemConfig.SCRIPT_SQL_PATH, jobOfScriptName, ".sh");
            if (deleteSH) {
                flagFile = true;
            } else {
                logger.info("Script.createScript error : Delete script failure!");
                flagFile = false;
            }
        }
        if (flagFile) {
            FileOutputStream fos = new FileOutputStream(SystemConfig.SCRIPT_SQL_PATH + jobOfScriptName + ".sh", false);
            String script = null;
            if ("spark".equalsIgnoreCase(sqlType)) {
                script = createHiveTableOfSparkScript(task);
            } else if ("hive".equalsIgnoreCase(sqlType)) {
                script = createHiveTableOfHiveScript(task);
            } else {
                logger.info("Script.createScript :  SQL Type nonsupport!");
            }
            fos.write(script.getBytes("UTF-8"));
            fos.flush();
            fos.close();
            return true;
        }
        logger.info("Script.createScript end!");
        return false;
    }


    private String createHiveTableOfSparkScript(Task task) throws IOException {
        logger.info("Script.createHiveTableOfSparkScript start!");
        StringBuilder hiveOfSparkSQL = new StringBuilder();
        hiveOfSparkSQL.append("#!/bin/sh" + newLine);
        hiveOfSparkSQL.append("spark-sql \\" + newLine + getCount(2) +
                "--master yarn-client\\" + newLine + getCount(2) +
                "--num-executors 3 \\" + newLine + getCount(2) +
                "--driver-memory 2g \\" + newLine + getCount(2) +
                "--executor-memory 2g \\" + newLine + getCount(2) +
                "--executor-cores 3 \\" + newLine + getCount(2) +
                "-e \"" + newLine + getCount(2));
//        createHiveOfSparkSql(hiveOfSparkSQL, task);
        createHiveOrSparkSql(hiveOfSparkSQL, task);
        logger.info("Script.createHiveTableOfSparkScript end!");
        return hiveOfSparkSQL.toString();
    }

    private String createHiveTableOfHiveScript(Task task) throws IOException {
        logger.info("Script.createHiveTableOfHiveScript start!");
        StringBuilder hiveOfSparkSQL = new StringBuilder();
        hiveOfSparkSQL.append("#!/bin/sh" + newLine);
        hiveOfSparkSQL.append("beeline -u jdbc:hive2://localhost:10000 -n hive -e  \"" + newLine + getCount(2));
//        createHiveOfSparkSql(hiveOfSparkSQL, task);
        createHiveOrSparkSql(hiveOfSparkSQL, task);
        logger.info("Script.createHiveTableOfHiveScript end!");
        return hiveOfSparkSQL.toString();
    }

    private String createHiveOrSparkSql(StringBuilder hiveOfSparkSQL, Task task) throws IOException {
        hiveOfSparkSQL.append("set hive.exec.dynamic.partition.mode=nonstrict;" + newLine + getCount(2));
        hiveOfSparkSQL.append(task.getSqlStc());
        hiveOfSparkSQL.append(newLine + getCount(2) + "\"");
        return hiveOfSparkSQL.toString();
    }

    private String createHiveOfSparkSql(StringBuilder hiveOfSparkSQL, Task task) throws IOException {
        logger.info("Script.createHiveOfSparkSql start!");
        hiveOfSparkSQL.append("set hive.exec.dynamic.partition.mode=nonstrict;" + newLine + getCount(2));
        hiveOfSparkSQL.append("INSERT OVERWRITE TABLE " + task.getTargetDB() + "." + task.getTargetTable() + " ");
        String partition = task.getPartition();
        if (StringUtils.isBlank(partition)) {
            hiveOfSparkSQL.append(newLine + getCount(4));
            appendSQL(hiveOfSparkSQL, task);
        } else {
            appendPartition(hiveOfSparkSQL, task);
            appendSQL(hiveOfSparkSQL, task);
            hiveOfSparkSQL.append(newLine + getCount(2));
            appendStartTimeAndEndTime(hiveOfSparkSQL, task);
        }
        hiveOfSparkSQL.append(newLine + getCount(2) + "\"");
        logger.info("Script.createHiveOfSparkSql end!");
        return hiveOfSparkSQL.toString();
    }

    private void appendPartition(StringBuilder hiveOfSparkSQL, Task task) {
        logger.info("Script.appendPartition start!");

        hiveOfSparkSQL.append("PARTITION ( " + "year, month, day" + " ) " + newLine + getCount(4));
        logger.info("Script.appendPartition end!");
    }

    private void appendSQL(StringBuilder hiveOfSparkSQL, Task task) {
        logger.info("Script.appendSQL start!");
        String taskSql = task.getSqlStc();
        hiveOfSparkSQL.append(taskSql);
        logger.info("Script.appendSQL end!");
    }

    private void appendStartTimeAndEndTime(StringBuilder hiveOfSparkSQL, Task task) {
        logger.info("Script.appendStartTimeAndEndTime start!");
        String partition = task.getPartition();
        Date startDate = task.getStarttime();
        Date endDate = task.getEndtime();
        String startTime = null;
        String endTime = null;
        if (startDate != null && endDate != null) {
            startTime = formatDate.formateDate2String(startDate);
            endTime = formatDate.formateDate2String(endDate);
            hiveOfSparkSQL.append("WHERE " + partition + " >= '" + startTime + "' AND" + " " + partition + " <= '" + endTime + "' ");
        } else {
            startTime = formatDate.getFewDaysBefore(1);
            endTime = formatDate.formateDate2String(new Date());
            hiveOfSparkSQL.append("WHERE " + partition + " >= '" + startTime + "' AND" + " " + partition + " <= '" + endTime + "' ");
        }
        logger.info("Script.appendStartTimeAndEndTime end!");
    }

    /**
     * 手动指定添加空格
     *
     * @param count
     * @return
     */
    private String getCount(int count) {
        String st = "";
        if (count < 0) {
            count = 0;
        }
        for (int i = 0; i < count; i++) {
            st = st + " ";
        }
        return st;
    }
}

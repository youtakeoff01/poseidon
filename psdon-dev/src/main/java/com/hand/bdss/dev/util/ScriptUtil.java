package com.hand.bdss.dev.util;

import com.hand.bdss.dev.model.ScheduleType;
import com.hand.bdss.dev.vo.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author : Koala
 * @version : v1.0
 * @description :
 */
public class ScriptUtil {
    private static final Logger logger = LoggerFactory.getLogger(ScriptUtil.class);

    public static String getJobOfScriptName(Task task, ScheduleType scheduleType) {
        logger.info("getJobOfScriptName param=" + task.toString());
        StringBuilder jobOfScriptName = new StringBuilder();
        String taskName = task.getTaskName();
        String taskType = task.getTaskType();
        String sqlType = task.getSqlType();
        if (!"0".equalsIgnoreCase(taskType) && !"1".equalsIgnoreCase(taskType) && !"2".equalsIgnoreCase(taskType)) {
            logger.info("getJobOfScriptName TakType nonsupport!");
            return null;
        }
        if (null != scheduleType) {
            switch (scheduleType) {
                case region:
                    jobOfScriptName.append(taskName).append("-").append(taskType).append("-")
                            .append(sqlType).append("-").append("region");
                    return jobOfScriptName.toString();
                case initialize:
                    jobOfScriptName.append(taskName).append("-").append(taskType).append("-")
                            .append(sqlType);
                    return jobOfScriptName.toString();
                case initializeKill:
                    jobOfScriptName.append(taskName).append("-").append(taskType).append("-")
                            .append(sqlType);
                    return jobOfScriptName.toString();
                default:
                    return null;
            }
        }else {
            return jobOfScriptName.append(taskName).append("-").append(taskType).append("-")
                    .append("SQOOP").toString();
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath   文件路径
     * @param fileName   文件名称
     * @param fileFormat 文件格式
     * @return
     */
    public static boolean existsFile(String filePath, String fileName, String fileFormat) {
        StringBuilder fileBuilder = new StringBuilder();
        fileBuilder.append(filePath).append(fileName).append(fileFormat);
        File file = new File(String.valueOf(fileBuilder));
        return file.exists();
    }

    /**
     * 删除文件
     *
     * @param filePath
     * @param fileName
     * @param fileFormat
     * @return
     */
    public static boolean deleteFile(String filePath, String fileName, String fileFormat) {
        StringBuilder fileBuilder = new StringBuilder();
        fileBuilder.append(filePath).append(fileName).append(fileFormat);
        File file = new File(String.valueOf(fileBuilder));
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 递归最后一个字符
     *
     * @param str
     * @return
     */
    public static String CutOutSQL(String str) {
        String lastStr = str.substring(str.length() - 1, str.length());
        if (!lastStr.equalsIgnoreCase(";") && !lastStr.equalsIgnoreCase("；") && !lastStr.equalsIgnoreCase(" ")) {
            return str;
        } else {
            return CutOutSQL(str.substring(0, str.length() - 1));
        }
    }

    /**
     * 删除最后一个字符
     *
     * @param str
     * @return
     */
    public static String CutOutSQLQuery(String str) {
        String lastStr = str.substring(str.length() - 1, str.length());
        if (lastStr.equalsIgnoreCase(";") || lastStr.equalsIgnoreCase("；")) {
            return str.substring(0, str.length() - 1);
        } else {
            return str;
        }
    }

}

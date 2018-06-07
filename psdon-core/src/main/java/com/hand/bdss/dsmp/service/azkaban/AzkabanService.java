package com.hand.bdss.dsmp.service.azkaban;

import com.hand.bdss.dsmp.model.ETLJobConfig;

import com.hand.bdss.task.config.SystemConfig;
import com.hand.bdss.task.service.IAzkabanManger;
import com.hand.bdss.task.service.impl.AzkabanManger;


/**
 * 数据集成 -》数据同步 azkaban工程创建，任务生成，文件压缩，工程上传
 */
public class AzkabanService {
    public static void etlschedule(ETLJobConfig etlJobConfig,String projectName) throws Exception {
        String jobName = etlJobConfig.getSyncSource();
        String description = "  table "+jobName+" sqoop to hive";
        String dependencies = "";
        IAzkabanManger az = new AzkabanManger();
        //登录
        az.loginAndGetSessionId();
        //创建工程
        boolean flag = az.createProject(projectName,description);
        System.out.println(flag);
        if(!flag){
            az.deleteProject(projectName);
            az.createProject(projectName,description);
        }
        //创建job
        az.createJob(projectName,SystemConfig.SCRIPT_SQOOP_PATH,dependencies);
        //压缩zip
        StringBuilder filesName = new StringBuilder();
        filesName.append(SystemConfig.SCRIPT_SQOOP_PATH).append("/").append(projectName).append(".sh,");
        filesName.append(SystemConfig.SCRIPT_SQOOP_PATH).append("/").append(projectName).append(".job");
        az.filesToZip(projectName,filesName.toString(),SystemConfig.AZKABAN_JOB_PATH);
        //上传zip
        az.uploadZip(SystemConfig.AZKABAN_JOB_PATH,projectName,projectName);

    }
    public static void executeHiveToRdbmsAPI(String projectName,String jobname) throws Exception {
        IAzkabanManger az = new AzkabanManger();
        az.loginAndGetSessionId();
        az.executeFlow(projectName,jobname);
    }
}

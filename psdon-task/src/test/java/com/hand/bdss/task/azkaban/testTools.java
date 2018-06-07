package com.hand.bdss.task.azkaban;

import com.hand.bdss.task.service.IAzkabanManger;
import com.hand.bdss.task.service.impl.AzkabanManger;

/**
 * Created by hasee on 2017/8/3.
 */
public class testTools {
    public static void main(String[] args) throws Exception{
        IAzkabanManger az = new AzkabanManger();
        //登录
        //System.out.println(az.loginAndGetSessionId());
        //创建azkaban的工程 工程名称+job类型
        //System.out.println(az.createProject("aaaaa","hive"));
        //删除工程
        //System.out.println(az.deleteProject("aaaaa"));
        //创建job  job名称+job存放路径     job和sh放在同一路径下面
        //az.createJob("test","C:\\Users\\hasee\\Desktop\\");
        //压缩test.job和test.sh为zip文件   job名称+job与shell路径+zip存放路径
        //az.fileToZip("test","C:\\Users\\hasee\\Desktop\\","C:\\Users\\hasee\\Desktop\\");
        //上传zip到工程上
        //System.out.println(az.uploadJar("C:\\Users\\hasee\\Desktop\\test\\","testcurl","testupload"));
        //执行工作流   工程名字+job名字
        //System.out.println(az.executeFlow("tb_service_status","tb_service_status"));
        //设置时间调度
       //System.out.println(az.setScheduleFlow("tb_service_status","tb_service_status","0 * 1 ? * *"));
        //获取ScheduleId getScheduleId
        //String scheduleId = az.getScheduleId("test0831-1-HIVE-region");
        //System.out.println(scheduleId);
        // 取消时间调度
        //System.out.println(az.unScheduleFlow(scheduleId));
        //获取正在执行Flow的ExecIds
        //System.out.println(az.getRunningExecIds("tb_service_status"));
        //kill正在运行的job
//        String execIds=" 217 ";
//        System.out.println(az.cancelRunningFlow(execIds));
        //az.getExecutionJobLogs("asd232_0_sqoop","589");
        //设置任务调度，任务执行失败邮件告警
        //az.setJobFailMail(scheduleId,"157573950@qq.com,dardaemon@163.com","7:00");
        //压缩多个文件zip
        //az.filesToZip("a","C:\\Users\\hasee\\Desktop\\test\\msyql_no_0_sqoop_region.job,C:\\Users\\hasee\\Desktop\\test\\msyql_no_0_sqoop_region.sh,C:\\Users\\hasee\\Desktop\\test\\testdsmp01_0_sqoop.job,C:\\Users\\hasee\\Desktop\\test\\testdsmp02_0_sqoop.job,C:\\Users\\hasee\\Desktop\\dsmp\\HiveClient.java","C:\\Users\\hasee\\Desktop\\test\\");

    }
}

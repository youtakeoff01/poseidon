package com.hand.bdss.task.service;

/**
 * Created by hasee on 2017/8/4.
 */
public interface IAzkabanManger {

    /**
     * 登录验证
     */
    public String loginAndGetSessionId() throws Exception;

    /**
     * 创建一个调度项目
     * @param projectName azkaban工程名称
     * @param jobType     工程类型描述
     * @throws Exception
     */
    public boolean createProject(String projectName, String jobType) throws Exception;

    /**
     * get请求删除一个project
     * @throws Exception
     */
    public boolean deleteProject(String projectName) throws Exception;

    /**
     * 创建一个作业
     * @param jobName job名称
     * @param absuPath job存放路径
     * @param dependencies job存放路径
     * @throws Exception
     */
    public void createJob(String jobName, String absuPath,String dependencies) throws Exception;

    /**
     * 创建带参数作业
     * @param jobName
     * @param absuPath
     * @param dependencies
     * @param userparams
     * @throws Exception
     */
    public void createJobWithParams(String jobName, String absuPath,String dependencies,String userparams) throws Exception;
    /**
     * 将存放在filePath目录下的源文件,打包成jobName名称的ZIP文件,并存放到zipPath。
     * @param filePath 待压缩的文件路径
     * @param zipPath 压缩后存放路径
     * @param jobName 压缩后文件的名称,即azkaban job的名字，又是shell脚本名
     * @return flag
     */
    public boolean fileToZip(String jobName, String filePath, String zipPath);

    /**
     * 上传zip包到项目中
     * @param zipPath zip存放路径
     * @param projectName 工程名字
     * @param jobName job名字
     * @throws Exception
     */
    public boolean uploadZip(String zipPath, String projectName, String jobName) throws Exception;

    /**
     * 执行工作流
     * @param projectName 工程名字
     * @param jobName job名称
     * @throws Exception
     */
    public boolean executeFlow(String projectName, String jobName) throws Exception;

    /**
     * 设置调度时间
     * @param projectName
     * @param jobName job名字
     * @param cronExpression 时间表达式0 1 * ? * *  秒/分/小时/天/月
     * @throws Exception
     */
    public boolean  setScheduleFlow(String projectName, String jobName, String cronExpression) throws Exception;

    /**
     * 取消时间调度
     * @param scheduleId 调度ID
     * @throws Exception
     */
    public boolean  unScheduleFlow(String scheduleId) throws Exception;

    /**
     * 获取正在执行Flow的ExecIds，为kill flow获取ExecIds参数 [301, 302, 111, 999]
     * @param projectName 默认projectName，flow，hive表名都是一样的
     * @return 数据样例[301, 302, 111, 999] 或者为null(工程中没有正在运行的job)
     */
    public String getRunningExecIds(String projectName) throws Exception;

    /**
     * kill调正在运行的job
     * @param execIds,首先通过getRunningExecIds 获取projectName的execIds
     * @return 返回kill成功 或者 this is flow isn't running.
     */
    public boolean cancelRunningFlow(String execIds) throws Exception;

    /**
     * 获取getScheduleId
     */
    public String getScheduleId(String projectName) throws Exception;

    /**
     * 获取job的执行log
     * @param jobId job name
     * @param execId job exec id
     * @return
     * @throws Exception
     */
    public String getExecutionJobLogs(String jobId,String execId)throws Exception;

    /**
     * 任务失败邮件告警
     * @param scheduleId 调度ID
     * @param emails 邮箱列表 格式 a@example.com;b@example.com
     * @param duration 任务持续时间 格式5:00=300分钟
     * @return
     * @throws Exception
     */
    public boolean setJobFailMail(String scheduleId,String emails,String duration)throws Exception;

    /**
     * 压缩任意个文件
     * @param jobName
     * @param filesName
     * @param zipPath
     * @return
     * @throws Exception
     */
    public boolean filesToZip(String jobName,String filesName,String zipPath)throws Exception;
}

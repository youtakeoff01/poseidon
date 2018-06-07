package com.hand.bdss.task.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.task.config.SystemConfig;
import com.hand.bdss.task.service.IAzkabanManger;
import com.hand.bdss.task.util.HttpClients;


/**
 * Created by hasee on 2017/8/4.
 */
public class AzkabanManger implements IAzkabanManger {
    private static final Logger logger = LoggerFactory.getLogger(AzkabanManger.class);

    /**
     * 登录验证
     */

    public String loginAndGetSessionId() {
        try {
            String url = SystemConfig.BASEURL + SystemConfig.LOGINACTION;
            logger.info("loginAndGetSessionId url = " + url);
            List<NameValuePair> formParams = new ArrayList<>();
            formParams.add(new BasicNameValuePair("username", SystemConfig.AZKABAN_USERNAME));
            formParams.add(new BasicNameValuePair("password", SystemConfig.AZKABAN_PASSWORD));
            JSONObject result = HttpClients.sendPost(url, formParams, null);
            if (null == result) {
                logger.info("loginAndGetSessionId sendPost result is null ");
                return null;
            }
            if (result.getString("status").equals("success")) {
                return result.getString("session.id");
            }
        } catch (Exception e) {
            logger.error("loginAndGetSessionId error ", e);
        }
        logger.info("AzkabanManger.loginAndGetSessionId end ");
        return null;
    }

    /**
     * 创建一个调度项目
     *
     * @param projectName azkaban工程名称
     * @param jobType     工程描述
     * @throws Exception
     */
    public boolean createProject(String projectName, String jobType) {
        logger.info("AzkabanManger.createProject start ");
        try {
            String url = SystemConfig.BASEURL + SystemConfig.CREATEACTION;
            List<NameValuePair> formParams = new ArrayList<>();
            formParams.add(new BasicNameValuePair("name", projectName));
            formParams.add(new BasicNameValuePair("description", projectName + jobType));
            JSONObject jsonObject = HttpClients.sendPost(url, formParams, null);
            if (jsonObject.getString("status").equals("success")) {
                logger.info("createProject create success projectName=" + projectName);
                return true;
            } else {
                logger.info("createProject Project already exists projectName=" + projectName);
            }
        } catch (Exception e) {
            logger.error("createProject error ", e);
        }
        logger.info("AzkabanManger.createProject end ");
        return false;
    }

    /**
     * get请求删除一个project
     *
     * @return
     * @throws Exception
     */
    public boolean deleteProject(String projectName) throws Exception {
        logger.info("AzkabanManger.deleteProject start ");
        //get sessionid
        String sessionId = loginAndGetSessionId();
        String url = SystemConfig.BASEURL + SystemConfig.DELETEACTION + "&session.id=" + sessionId + "&project=" + projectName;
        try {
            HttpClients.sentGet(url);
        } catch (Exception e) {
            logger.error("deleteProject error ", e);
        }
        logger.info("AzkabanManger.deleteProject end ");
        return true;
    }

    /**
     * 将存放在filePath目录下的源文件,打包成jobName名称的ZIP文件,并存放到zipPath。
     *
     * @param filePath 待压缩的文件路径
     * @param zipPath  压缩后存放路径
     * @param jobName  压缩后文件的名称,即azkaban job的名字，又是shell脚本名
     * @return flag
     */
    public boolean fileToZip(String jobName, String filePath, String zipPath) {
        logger.info("AzkabanManger.fileToZip start ");
        String sourceFilePath = filePath + jobName + ".sh";
        String path = filePath + jobName + ".job";
        String zipFilePath = zipPath;
        String fileName = jobName;
        boolean flag = false;
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        if (sourceFile.exists() == false) {
            logger.info("AzkabanManger.fileToZip >>>>>> 待压缩的文件目录：" + sourceFilePath + " 不存在. <<<<<<");
        } else {
            try {
                File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
                File file1 = new File(sourceFilePath);
                File file2 = new File(path);
                File[] sourceFiles = new File[]{file1, file2};
                if (null == sourceFiles || sourceFiles.length < 1) {
                    logger.info("AzkabanManger.fileToZip >>>>>> 待压缩的文件目录：" + sourceFilePath + " 里面不存在文件,无需压缩. <<<<<<");
                } else {
                    fos = new FileOutputStream(zipFile);
                    zos = new ZipOutputStream(new BufferedOutputStream(fos));
                    byte[] bufs = new byte[1024 * 10];
                    for (int i = 0; i < sourceFiles.length; i++) {
                        // 创建ZIP实体,并添加进压缩包
                        ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                        zos.putNextEntry(zipEntry);
                        // 读取待压缩的文件并写进压缩包里
                        fis = new FileInputStream(sourceFiles[i]);
                        bis = new BufferedInputStream(fis, 1024 * 10);
                        int read = 0;
                        while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                            zos.write(bufs, 0, read);
                        }
                    }
                    flag = true;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                // 关闭流
                try {
                    if (null != bis) bis.close();
                    if (null != zos) zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        logger.info("AzkabanManger.fileToZip end ");
        return flag;
    }

    /**
     * 将存放在filePath目录下的源文件,打包成jobName名称的ZIP文件,并存放到zipPath
     * @param jobName
     * @param filesName
     * @param zipPath
     * @return
     */
    public boolean filesToZip(String jobName,String filesName,String zipPath) {
        logger.info("AzkabanManger.fileToZip start ");
        String fils[] = filesName.split(",");
        boolean flag = false;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
            try {
                File zipFile = new File(zipPath + "/" + jobName + ".zip");
                if (null == fils || fils.length < 1) {
                    logger.info("AzkabanManger.fileToZip  There are no files to be compressed ");
                } else {
                    fos = new FileOutputStream(zipFile);
                    zos = new ZipOutputStream(new BufferedOutputStream(fos));
                    byte[] bufs = new byte[1024 * 10];
                    for (int i = 0; i < fils.length; i++) {
                        // 创建ZIP实体,并添加进压缩包
                        File fileName = new File(fils[i]);
                        ZipEntry zipEntry = new ZipEntry(fileName.getName());
                        zos.putNextEntry(zipEntry);
                        // 读取待压缩的文件并写进压缩包里
                        fis = new FileInputStream(fileName);
                        bis = new BufferedInputStream(fis, 1024 * 10);
                        int read = 0;
                        while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                            zos.write(bufs, 0, read);
                        }
                    }
                    flag = true;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                // 关闭流
                try {
                    if (null != bis) bis.close();
                    if (null != zos) zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        logger.info("AzkabanManger.fileToZip end ");
        return flag;
    }

    /**
     * 上传zip包到项目中
     *
     * @param zipPath     zip存放路径
     * @param projectName 工程名字
     * @param jobName     job名字
     * @throws Exception
     */
    public boolean uploadZip(String zipPath, String projectName, String jobName) {
        logger.info("AzkabanManger.uploadZip start ");
        try {
        	 //get sessionid
            String sessionId = loginAndGetSessionId();
            
            zipPath += jobName + ".zip";
            InputStream inputStream = new FileInputStream(zipPath);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("file", inputStream, ContentType.create("application/zip"), projectName);
            builder.addTextBody("project", projectName, ContentType.create("type/plain"));
            builder.addTextBody("session.id", sessionId, ContentType.create("type/plain"));
            builder.addTextBody("ajax", "upload", ContentType.create("type/plain"));

            JSONObject jsonObject = HttpClients.sendPost(SystemConfig.URL, null, builder);
            if (null == jsonObject) {
                return false;
            }
            if (jsonObject.getString("error") == null) {
                logger.info("AzkabanManger.uploadZip upload " + jobName + ".zip azkaban success!");
                return true;
            } else {
                logger.info("AzkabanManger.uploadZip upload " + jobName + ".zip azkaban failure!");
            }
        } catch (Exception e) {
            logger.error("uploadZip error", e);
        }
        logger.info("AzkabanManger.uploadZip end ");
        return false;
    }

    /**
     * 创建一个作业
     * @param jobName job名称
     * @param absuPath job存放路径
     * @param dependencies job存放路径
     * @throws Exception
     */
    public void createJob(String jobName, String absuPath,String dependencies) throws Exception {
        logger.info("AzkabanManger.createJob start ");
        String path = absuPath + jobName + ".job";
        FileOutputStream fileInputStream = new FileOutputStream(new File(path));
        String job = "type=command" + "\n" + "command=sh " + jobName + ".sh";
        if(StringUtils.isNotEmpty(dependencies)){
            job += "\ndependencies=" +dependencies+"-0-sqoop";
        }
        fileInputStream.write(job.getBytes());
        logger.info("AzkabanManger.createJob end ");
    }

    public void createJobWithParams(String jobName, String absuPath,String dependencies,String userparams)throws Exception{
        logger.info("AzkabanManger.createJob start ");
        String path = absuPath + jobName + ".job";
        FileOutputStream fileInputStream = new FileOutputStream(new File(path));
        String job = "type=command" + "\n" + "command=sh " + jobName + ".sh  "+ userparams;
        if(StringUtils.isNotEmpty(dependencies)){
            job += "\ndependencies=" +dependencies+"-0-sqoop";
        }
        fileInputStream.write(job.getBytes());
        logger.info("AzkabanManger.createJob end ");
    }

    /**
     * 执行工作流
     *
     * @param projectName 工程名字
     * @param jobName     job名称
     * @throws Exception
     */
    public boolean executeFlow(String projectName, String jobName) {
        logger.info("AzkabanManger.executeFlow start ");
        //get sessionid
        String sessionId = loginAndGetSessionId();
        String url = SystemConfig.BASEURL + "/executor?ajax=executeFlow" + "&session.id=" + sessionId + "&project=" + projectName + "&flow=" + jobName;
        try {
            JSONObject jsonObject = HttpClients.sentGet(url);
            if (jsonObject.isEmpty()) {
                return false;
            }
            if (jsonObject.getString("error") == null) {
                logger.info("AzkabanManger.executeFlow  execute flow success ");
                return true;
            } else {
                logger.info("AzkabanManger.executeFlow  execute flow failure ");
            }
        } catch (Exception e) {
            logger.error("executeFlow error ", e);
        }
        logger.info("AzkabanManger.executeFlow ");
        return false;
    }

    /**
     * 设置调度时间
     *
     * @param projectName
     * @param jobName        job名字
     * @param cronExpression 时间表达式0 1 * ? * *  秒/分/小时/天/月
     * @throws Exception
     */
    public boolean setScheduleFlow(String projectName, String jobName, String cronExpression) {
        logger.info("AzkabanManger.setScheduleFlow start ");
        String url = SystemConfig.BASEURL + SystemConfig.SCHEDULEACTION;
        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("projectName", projectName));
        formParams.add(new BasicNameValuePair("ajax", "scheduleCronFlow"));
        formParams.add(new BasicNameValuePair("flow", jobName));
        formParams.add(new BasicNameValuePair("cronExpression", cronExpression));
        try {
            JSONObject jsonObject = HttpClients.sendPost(url, formParams, null);
            if (jsonObject.getString("error") == null) {
                if (jsonObject.getString("status").equals("success")) {
                    logger.info("AzkabanManger.setScheduleFlow Schedule " + cronExpression + " setting  success!");
                    return true;
                } else {
                    logger.info("AzkabanManger.setScheduleFlow Schedule " + projectName + " or " + jobName + " not exist!");
                }
            } else {
                logger.info("AzkabanManger.setScheduleFlow Schedule " + cronExpression + " is not correct !");
            }
        } catch (Exception e) {
            logger.error("setScheduleFlow error ", e);
        }
        logger.info("AzkabanManger.setScheduleFlow end ");
        return true;
    }

    /**
     * 取消时间调度
     *
     * @param scheduleId 调度ID
     * @throws Exception
     */

    public boolean unScheduleFlow(String scheduleId) throws Exception {
        logger.info("AzkabanManger.unScheduleFlow start ");
        String url = SystemConfig.BASEURL + SystemConfig.SCHEDULEACTION;
        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("action", "removeSched"));
        formParams.add(new BasicNameValuePair("scheduleId", scheduleId));
        JSONObject jsonObject = HttpClients.sendPost(url, formParams, null);
        try {
            if (jsonObject.isEmpty()) {
                return false;
            }
            if (jsonObject.getString("status").equals("success")) {
                return true;
            }
        } catch (Exception e) {
            logger.error("unScheduleFlow error ", e);
        }
        logger.info("AzkabanManger.unScheduleFlow end ");
        return true;
    }

    /**
     * 获取正在执行Flow的ExecIds，为kill flow获取ExecIds参数 [301, 302, 111, 999]
     *
     * @param projectName 默认projectName，flow，hive表名都是一样的
     * @return 数据样例[301, 302, 111, 999] 或者为null(工程中没有正在运行的job)
     */
    public String getRunningExecIds(String projectName) {
        logger.info("AzkabanManger.getRunningExecIds start ");

        //get sessionid
        String sessionId = loginAndGetSessionId();
        
        String result = null;
        String url = SystemConfig.BASEURL + "/executor?ajax=getRunning" + "&session.id=" + sessionId + "&project=" + projectName + "&flow=" + projectName;
        try {
            JSONObject jsonObject = HttpClients.sentGet(url);
            if (jsonObject.isEmpty()) {
                return result;
            }
            if (jsonObject.getString("execIds") != null) {
                result = jsonObject.getString("execIds").replaceAll("\\[|\\]", "");
            }
        } catch (Exception e) {
            logger.error("getRunningExecIds error ", e);
        }
        logger.info("AzkabanManger.getRunningExecIds end ");
        return result;
    }

    @Override
    public boolean cancelRunningFlow(String execIds) {
        logger.info("AzkabanManger.cancelRunningFlow start ");
        try {
        	 //get sessionid
            String sessionId = loginAndGetSessionId();
            
            String exec[] = execIds.split(",");
            for (int i = 0; i < exec.length; i++) {
                String url = SystemConfig.BASEURL + "/executor?ajax=cancelFlow" + "&session.id=" + sessionId + "&execid=" + exec[i].trim();
                JSONObject jsonObject = HttpClients.sentGet(url);
                if (jsonObject.isEmpty()) {
                    return true;
                }
                if (jsonObject.getString("error") == null) {
                    logger.info("AzkabanManger.cancelRunningFlow kill running flow is success!");
                    return true;
                } else {
                    logger.info("AzkabanManger.cancelRunningFlow kill running flow is failure!");
                }
            }
        } catch (Exception e) {
            logger.error("cancelRunningFlow error ", e);
        }
        logger.info("AzkabanManger.cancelRunningFlow end ");
        return true;
    }

    /**
     * 根据工程名字获取 工程ID和flowId，通过这两个参数最终获取scheduleId，提供给取消调度的参数scheduleId
     *
     * @param projectName
     * @return scheduleId
     * @throws Exception
     */

    public String getScheduleId(String projectName) {
        logger.info("AzkabanManger.getScheduleId start  ");
        
        //get sessionid
        String sessionId = loginAndGetSessionId();
        
        String result = null;
        String url = SystemConfig.URL + "?ajax=fetchprojectflows" + "&session.id=" + sessionId + "&project=" + projectName;
        try {
            JSONObject jsonObject = HttpClients.sentGet(url);
            if (jsonObject.isEmpty()) {
                return null;
            }
            JSONArray flows = jsonObject.getJSONArray("flows");
            JSONObject info = flows.getJSONObject(flows.size() - 1);
            String flowId = info.getString("flowId");
            String projectId = jsonObject.getString("projectId");
            result = getScheduleIdResult(flowId, projectId);
        } catch (Exception e) {
            logger.error("getScheduleId error ", e);
        }
        logger.info("AzkabanManger.getScheduleId end ");
        return result;
    }

    /**
     * @param flowId
     * @param projectId
     * @return 调度参数scheduleId
     * @throws Exception
     */
    public String getScheduleIdResult(String flowId, String projectId) {
        logger.info("AzkabanManger.getScheduleIdResult start ");
        
        //get sessionid
        String sessionId = loginAndGetSessionId();
        
        String result = null;
        String url = SystemConfig.BASEURL + "/schedule?ajax=fetchSchedule" + "&session.id=" + sessionId + "&projectId=" + projectId + "&flowId=" + flowId;
        try {
            JSONObject jsonObject = HttpClients.sentGet(url);
            if (jsonObject.isEmpty()) {
                return null;
            }
            String schedule = jsonObject.getString("schedule");
            JSONObject info = JSONObject.parseObject(schedule);
            result = info.getString("scheduleId");
        } catch (Exception e) {
            logger.error("getScheduleIdResult error ", e);
        }
        logger.info("AzkabanManger.getScheduleIdResult end ");
        return result;
    }

    /**
     * 获取job的执行log
     *
     * @param jobId  job name
     * @param execId job exec id
     * @return
     * @throws Exception
     */
    public String getExecutionJobLogs(String jobId, String execId) {
        logger.info("AzkabanManger.getExecutionJobLogs start ");
        
        //get sessionid
        String sessionId = loginAndGetSessionId();
        
        String result = null;
        String url = SystemConfig.BASEURL + "/executor?ajax=fetchExecJobLogs" + "&session.id=" + sessionId + "&offset=0&length=100000&jobId=" + jobId + "&execid=" + execId;
        try {
            JSONObject jsonObject = HttpClients.sentGet(url);
            if (jsonObject.isEmpty()) {
                return null;
            }
            result = jsonObject.getString("data");
        } catch (Exception e) {
            logger.error("getExecutionJobLogs error ", e);
        }
        logger.info("AzkabanManger.getExecutionJobLogs end ");
        return result;
    }

    /**
     * 任务失败邮件告警
     * @param scheduleId 工程名称
     * @param emails 邮箱列表 格式 a@example.com;b@example.com
     * @param duration 任务持续时间 格式5:00=300分钟
     * @return
     * @throws Exception
     */
    public boolean setJobFailMail(String scheduleId, String emails, String duration) throws Exception {
        boolean flag = false;
        logger.info("AzkabanManger.setJobFailMail start ");
        String setting = ",SUCCESS,"+duration+",true,false";
        String url = SystemConfig.BASEURL + SystemConfig.SCHEDULEACTION;
        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("ajax", "setSla"));
        formParams.add(new BasicNameValuePair("scheduleId", scheduleId));
        formParams.add(new BasicNameValuePair("slaEmails", emails));
        formParams.add(new BasicNameValuePair("settings[0]", setting));
        JSONObject jsonObject = HttpClients.sendPost(url, formParams, null);
        logger.info("AzkabanManger.setJobFailMail end");
        if (jsonObject.isEmpty()) {
            flag = true;
        }else {
            logger.info("AzkabanManger.setJobFailMail error "+jsonObject);
        }
        logger.info("AzkabanManger.setJobFailMail end ");
        return flag;
    }
}


package com.hand.bdss.web.operationcenter.task.service;

import com.hand.bdss.dsmp.common.response.Response;
/**
 * azkaban 操作类
 * @author liqifei
 * @DATA 2017年12月6日
 */
public interface JarTaskService {
	
	/**
     * 创建azkaban任务
     * @return
     */
    Response createAzkabanJob(String jarPath,String shShell,String jobName,String userParams);
    /**
     * 删除azkaban任务
     */
    Response deleteAzkabanJob(String jobName);
    /**
     * 執行azkaban任務
     */
    Response execAzkabanJob(String jobName);
}

package com.hand.bdss.web.operationcenter.task.service.impl;

import java.io.FileOutputStream;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hand.bdss.dev.scheduler.AzkabanScheduler;
import com.hand.bdss.dev.util.ScriptUtil;
import com.hand.bdss.dsmp.common.response.Response;
import com.hand.bdss.dsmp.component.hdfs.HDFSClient;
import com.hand.bdss.dsmp.model.ReturnCode;
import com.hand.bdss.task.config.SystemConfig;
import com.hand.bdss.task.service.impl.AzkabanManger;
import com.hand.bdss.web.operationcenter.task.service.JarTaskService;

@Service
public class JarTaskServiceImpl implements JarTaskService {

	private static final Logger logger = LoggerFactory.getLogger(JarTaskServiceImpl.class);
    private AzkabanManger azkaban = new AzkabanManger();
    
    @Resource
	private HDFSClient hDFSClient;
    
	/**
	 * 创建azkaban任务
	 * 
	 * @return
	 */
	public Response createAzkabanJob(String jobPath,String shShell, String jobName,String userParams) {
		Response response = new Response();
		try {
			if (null != jobPath) {
				if (StringUtils.isBlank(shShell)) {
					//SQOOP  将hdfs上的.sh文件上传至服务器(SCRIPT_SQL_PATH)
					Map<String, String> hdfsMap = hDFSClient.copyFileToLocal(jobPath, SystemConfig.SCRIPT_SQL_PATH, jobName + ".sh");
					if (!ReturnCode.RETURN_CODE_SUCCESS.equals(hdfsMap.get("returnCode"))) {
						response.failure("同步sh文件失败!");
						return response;
					}
				} else {
					//STORM/SAPRK 在服务器指定的位置里面创建.sh文件（需要判断当前文件中的sh文件是否已经存在）
					boolean existsSH = ScriptUtil.existsFile(SystemConfig.SCRIPT_SQL_PATH, jobName, ".sh");
					if (existsSH) {
						boolean deleteSH = ScriptUtil.deleteFile(SystemConfig.SCRIPT_SQL_PATH, jobName, ".sh");
						if (!deleteSH) {
							response.failure("创建的sh文件已经存在,删除已经存在的sh文件不成功!");
							return response;
						}
					}
					FileOutputStream fos = new FileOutputStream(SystemConfig.SCRIPT_SQL_PATH + jobName + ".sh", false);
					fos.write(shShell.getBytes("UTF-8"));
					fos.flush();
					fos.close();
				}
			}
			// 判断azkaban是否登录
//			boolean existsLogin = azkaban.isLogin();
//			if (existsLogin) {
				// 创建azkaban任务
				boolean existsProject = azkaban.createProject(jobName, "");
				if (!existsProject) {
					azkaban.deleteProject(jobName);
					azkaban.createProject(jobName, "");
				}
				// 上传azkaban任务zip包
				boolean boo = new AzkabanScheduler().uploadProject(jobName, null,userParams);
				if (!boo) {
					response.failure("上传azkaban任务zip包失败!");
					return response;
				}
//			} else {
//				response.failure("登录azkaban失败！");
//				return response;
//			}
			response.success();
		} catch (Exception e) {
			logger.error("创建azkaban任务出现异常,异常信息为:", e);
			response.failure("创建azkaban任务出现异常,异常信息为:"+e.getMessage());
		}
		return response;

	}

	@Override
	public Response deleteAzkabanJob(String jobName) {
		Response response = new Response();
		String azkabanJobPath = SystemConfig.AZKABAN_JOB_PATH;
		String scriptSQLPath = SystemConfig.SCRIPT_SQL_PATH;
		try {
//			// 判断azkaban是否已经登录
//			boolean existsLogin = azkaban.isLogin();
//			if (existsLogin) {
				boolean deleteProject = azkaban.deleteProject(jobName);
				if (!deleteProject) {
					response.failure("删除azkaban失败！");
					return response;
				}
				ScriptUtil.deleteFile(scriptSQLPath, jobName, ".sh");
				ScriptUtil.deleteFile(scriptSQLPath, jobName, ".job");
				ScriptUtil.deleteFile(azkabanJobPath, jobName, ".zip");
//			} else {
//				response.failure("登录azkaban失败！");
//				return response;
//			}
			response.success();
		} catch (Exception e) {
			logger.error("刪除azkaban任务出现异常,异常信息为:", e);
			response.failure("刪除azkaban任务出现异常,异常信息为:"+e.getMessage());
		}
		return response;
	}

	@Override
	public Response execAzkabanJob(String jobName) {
		Response response = new Response();
		try {
//			// 判断azkaban是否已经登录
//			boolean existsLogin = azkaban.isLogin();
//			if (existsLogin) {
				boolean boo = azkaban.executeFlow(jobName, jobName);
				if (!boo) {
					response.failure("启动azkaban失败！");
					return response;
				}
				response.success();
//			} else {
//				response.failure("登录azkaban失败！");
//				return response;
//			}
		} catch (Exception e) {
			logger.error("启动azkaban任务出现异常,异常信息为:", e);
			response.failure("启动azkaban任务出现异常,异常信息为:"+e.getMessage());
		}
		return response;
	}

}

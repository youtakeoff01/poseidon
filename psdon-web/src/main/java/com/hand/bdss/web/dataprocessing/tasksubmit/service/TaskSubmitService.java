package com.hand.bdss.web.dataprocessing.tasksubmit.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.common.vo.ShowTaskJarInfoVO;
import com.hand.bdss.web.common.vo.SubmitTaskJarVO;
import com.hand.bdss.web.entity.LatestTaskEntity;

public interface TaskSubmitService {

	boolean submitJarTask(SubmitTaskJarVO taskjarvo, HttpServletRequest request) throws Exception;

	List<ShowTaskJarInfoVO> listJarTasks(SubmitTaskJarVO taskvo, HttpServletRequest request) throws Exception;

	Map<String,Object> deleteJarTasks(HttpServletRequest request, List<SubmitTaskJarVO> taskvos) throws Exception;

	boolean checkJarTaskName(LatestTaskEntity latestTaskEntity) throws Exception;

	Map<String,Object>  execJartasks(HttpServletRequest request, LatestTaskEntity latestTaskEntity) throws Exception;

	int listJarTaskCounts(HttpServletRequest request, SubmitTaskJarVO taskvo) throws Exception;

	Map<String,Object> updateJarTask(HttpServletRequest request, SubmitTaskJarVO taskvo) throws Exception;

	Map<String,Object>  setSechdulerTask(HttpServletRequest request, LatestTaskEntity latestTaskEntity) throws Exception;

	Map<String,Object>  setParamsJarTask(SubmitTaskJarVO submitTaskJarVO) throws Exception;
}

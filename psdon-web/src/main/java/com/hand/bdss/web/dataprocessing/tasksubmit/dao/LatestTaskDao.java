package com.hand.bdss.web.dataprocessing.tasksubmit.dao;

import java.util.List;

import com.hand.bdss.web.common.vo.ShowTaskJarInfoVO;
import com.hand.bdss.web.entity.LatestTaskEntity;

public interface LatestTaskDao {
	
	int insertLatestTask(LatestTaskEntity latestTaskEntity) throws Exception;
	
	int updateLatestTask(LatestTaskEntity latestTaskEntity) throws Exception;

	int updateLatestTaskById(LatestTaskEntity latestTaskEntity) throws Exception;

	int deleteLatestTask(List<LatestTaskEntity> lists) throws Exception;
	
	List<LatestTaskEntity> listLatestTaskEntity(LatestTaskEntity latestTaskEntity,int startPage,int count) throws Exception;

	List<ShowTaskJarInfoVO> listJarTasks(LatestTaskEntity latestTaskEntity, int startPage, int count) throws Exception;

	int checkJarTaskName(LatestTaskEntity latestTaskEntity) throws Exception;

	int listJarTaskCounts(LatestTaskEntity latestTaskEntity) throws Exception;

}
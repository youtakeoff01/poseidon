package com.hand.bdss.web.dataprocessing.tasksubmit.dao;

import java.util.List;

import com.hand.bdss.web.entity.JarInfoEntity;
import com.hand.bdss.web.entity.TaskAttributeEntity;

public interface TaskAttributeDao {
	
	/**
	 * 返回的值是插入当前行的id
	 * 
	 * <insert id="add" parameterType="vo.Category" useGeneratedKeys="true" keyProperty="id">
     * insert into category (name_zh, parent_id,
     * show_order, delete_status, description
     * )
	 * @param taskAttributeEntity
	 * @return
	 * @throws Exception
	 */
	long insertTaskAttribute(TaskAttributeEntity taskAttributeEntity) throws Exception;
	
	int deleteTaskAttribute(List<TaskAttributeEntity> taskAttributeEntity) throws Exception;
	
	List<TaskAttributeEntity> listTaskAttribute(TaskAttributeEntity taskAttributeEntity,int startPage,int count);
	
	int updateJarTask(TaskAttributeEntity taskAttributeEntity) throws Exception;

	int listJars(JarInfoEntity jarInfoEntity)throws Exception;
}

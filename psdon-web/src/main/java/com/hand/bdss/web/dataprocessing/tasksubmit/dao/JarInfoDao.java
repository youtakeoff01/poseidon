package com.hand.bdss.web.dataprocessing.tasksubmit.dao;

import java.util.List;

import com.hand.bdss.web.entity.JarInfoEntity;
import com.hand.bdss.web.entity.TaskAttributeEntity;

public interface JarInfoDao {

	long insertJarInfo(JarInfoEntity jarInfo);

	int deleteJarInfos(List<JarInfoEntity> lists);

	List<JarInfoEntity> listTaskJars(JarInfoEntity jarInfo, int startPage, int count);

	int listTaskJarCounts(JarInfoEntity jarInfo);


}

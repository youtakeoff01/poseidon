package com.hand.bdss.web.dataprocessing.tasksubmit.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.common.vo.SubmitTaskJarVO;
import org.springframework.web.multipart.MultipartFile;

import com.hand.bdss.web.entity.JarInfoEntity;

public interface JarInfoService {
	/**
	 * 
	 * @param jarInfo
	 * @return 为插入当前行的id
	 * @throws Exception
	 */
	int insertJarInfo(HttpServletRequest request, MultipartFile uploadFile) throws Exception;

	List<JarInfoEntity> listTaskJars(HttpServletRequest request, JarInfoEntity jarInfo, int startPage, int count) throws Exception;

	int listTaskJarCounts(HttpServletRequest request, JarInfoEntity jarInfo) throws Exception;

	Map<String,Object> deleteJars(HttpServletRequest request, List<SubmitTaskJarVO> taskjarvos) throws Exception;
}

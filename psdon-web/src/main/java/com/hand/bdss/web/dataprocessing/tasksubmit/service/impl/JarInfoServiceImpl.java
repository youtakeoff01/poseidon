package com.hand.bdss.web.dataprocessing.tasksubmit.service.impl;

import java.io.InputStream;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.common.vo.SubmitTaskJarVO;
import com.hand.bdss.web.dataprocessing.tasksubmit.dao.TaskAttributeDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.clearspring.analytics.util.Lists;
import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.model.ReturnCode;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.datamanage.metadata.service.InfoDocumentService;
import com.hand.bdss.web.dataprocessing.tasksubmit.dao.JarInfoDao;
import com.hand.bdss.web.dataprocessing.tasksubmit.service.JarInfoService;
import com.hand.bdss.web.entity.JarInfoEntity;

@Service
public class JarInfoServiceImpl implements JarInfoService {
	
	private static String JARS = "jars/";

	private static String JAR_PATH = SystemConfig.HDFS_ROOT_PATH + JARS;

	@Resource
	JarInfoDao jarInfoDaoImpl;
	@Resource
	TaskAttributeDao taskAttributeDaoImpl;
	@Resource
	private InfoDocumentService infoDocumentServiceImpl;

	/**
	 * 上传jar信息，将jar保存到hdfs上，并将路径保存到本地数据库中
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int insertJarInfo(HttpServletRequest request, MultipartFile uploadFile) throws Exception {
		String fileName = uploadFile.getOriginalFilename();
		InputStream in = uploadFile.getInputStream();
		String jarPath = JAR_PATH + fileName;
		// 将上传的jar包路径保存到本地数据库中
		JarInfoEntity jarInfo = new JarInfoEntity();
		jarInfo.setJarName(fileName);
		jarInfo.setJarPath(jarPath);
		jarInfo.setCreateUser(GetUserUtils.getUser(request).getUserName());
		jarInfo.setCreateTime(new Date());
		long id = jarInfoDaoImpl.insertJarInfo(jarInfo);
		// 将上传的jar包保存到hdfs中
		Map<String, Object> hdfsMap = infoDocumentServiceImpl.uploadFile(in, JARS + fileName, request);
		if (ReturnCode.RETURN_CODE_DOCUMENT_NOT_ONLY.equals(hdfsMap.get("returnCode"))) {
			// 回滚
			List<JarInfoEntity> lists = Lists.newArrayList();
			jarInfo.setId(id + "");
			//删除信息
			jarInfoDaoImpl.deleteJarInfos(lists);
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * 查询jar信息
	 */
	@Override
	public List<JarInfoEntity> listTaskJars(HttpServletRequest request, JarInfoEntity jarInfo, int startPage, int count)
			throws Exception {
		// 如果是管理员，则可以查询所有的jar信息，如果不是，则只能查询自己上传的jar信息
		if (!GetUserUtils.isRootUser(request)) {
			if(jarInfo==null) {
				jarInfo = new JarInfoEntity();
			}
			jarInfo.setCreateUser(GetUserUtils.getUser(request).getUserName());
		}
		return jarInfoDaoImpl.listTaskJars(jarInfo, startPage, count);
	}
    /**
     * 查询jar数据的条数，用于前端分页
     */
	@Override
	public int listTaskJarCounts(HttpServletRequest request, JarInfoEntity jarInfo) throws Exception {
		// 如果是管理员，则可以查询所有的jar信息，如果不是，则只能查询自己上传的jar信息
		if (!GetUserUtils.isRootUser(request)) {
			if(jarInfo==null) {
				jarInfo = new JarInfoEntity();
			}
			jarInfo.setCreateUser(GetUserUtils.getUser(request).getUserName());
		}
		return jarInfoDaoImpl.listTaskJarCounts(jarInfo);
	}

	@Override
	public Map<String, Object> deleteJars(HttpServletRequest request, List<SubmitTaskJarVO> taskjarvos) throws Exception {

		List<JarInfoEntity> jarInfoEntities = new ArrayList<>();
		Map<String,Object> retMap = new HashMap<>();
		for (SubmitTaskJarVO submitTaskJarVO : taskjarvos){
			Map<String,Object> jarMap = new HashMap<>();
			JarInfoEntity jarInfoEntity = submitTaskJarVO.getJarInfoEntity();
			int jarTasks = taskAttributeDaoImpl.listJars(jarInfoEntity);
			if (jarTasks<=0){
				jarInfoEntities.add(jarInfoEntity);
				//删除hdfs上的jar路径
				infoDocumentServiceImpl.delete(jarInfoEntity.getJarPath());
				jarMap.put("returnCode", ReturnCode.RETURN_CODE_SUCCESS);
				jarMap.put(jarInfoEntity.getJarName(),"delete success");
			}else {
				jarMap.put(jarInfoEntity.getJarName(),"cannot delete,it's a task");
				jarMap.put("returnCode", ReturnCode.RETURN_CODE_ERROR);
			}
			retMap.put(jarInfoEntity.getJarName(),jarMap);
		}
		if (jarInfoEntities.size()>0){
			int i = jarInfoDaoImpl.deleteJarInfos(jarInfoEntities);
			retMap.put("delete success",i);
		}
		return retMap;
	}

}

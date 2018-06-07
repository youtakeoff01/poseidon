package com.hand.bdss.web.datamanage.metadata.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.model.DocumentInfo;
import com.hand.bdss.dsmp.model.DocumentModel;
import com.hand.bdss.dsmp.model.ReturnCode;
import com.hand.bdss.web.common.constant.Global;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.datamanage.metadata.service.InfoDocumentService;
import com.hand.bdss.web.entity.UserEntity;

import jersey.repackaged.com.google.common.collect.Maps;

/**
 * 文件管理
 *
 * @author yangkai
 * @date 2017-4-20
 * @email kai.yang01@hand-china.com
 */
@Controller
@RequestMapping(value = "/InfoDocument", produces = "text/plain;charset=UTF-8")
public class InfoDocumentController {

	private static final Logger logger = Logger.getLogger(InfoDocumentController.class);

	@Resource
	private InfoDocumentService infoDocumentServiceImpl;

	@Resource
	private LogUtils logUtils;

	/**
	 * 上传文件
	 *
	 * @param request
	 * @param
	 * @param dstPath
	 * @return
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	@ResponseBody
	public String uploadFile(HttpServletRequest request, @RequestParam(value = "file") MultipartFile[] uploadFiles,
			String dstPath) {
		BaseResponse resp = new BaseResponse();
		logger.info("uploadFile param : dstPath = " + dstPath);
		Map<String, Object> retMap = new HashMap<>();
		String fileName = null;
		UserEntity user = GetUserUtils.getUser(request);
		String userName = user.getUserName();
		String userType = user.getUserType();
		try {
			String userRootPath = userName + "/";
			if (StringUtils.isBlank(dstPath))
				dstPath = userRootPath;
			else {
				dstPath = userRootPath + dstPath;
			}
			if (null == uploadFiles || uploadFiles.length == 0) {
				logger.info("/uploadFile param : uploadFile is null");
				resp.setReturnCode(ReturnCode.RETURN_CODE_PARAM_NULL);
				return resp.toString();
			}
			for (MultipartFile uploadFile : uploadFiles) {
				fileName = uploadFile.getOriginalFilename();// ceshishuju.csv
				Map<String, Object> hdfsMap = null;

				// 校验文件是否超出大小限制
				if (isByteExceLimit(uploadFile.getSize())) {
					hdfsMap = new HashMap<>();
					hdfsMap.put(ReturnCode.RETURN_CODE_FILE_SIZE_EXEC, "文件大小超出限制");
				} else {
					/* 校验文件内容的准确性：start 3.此功能是针对利物浦指定话开发 */
					String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
					InputStream in = uploadFile.getInputStream();
					InputStream copyIn = uploadFile.getInputStream();
					if ((Global.OTHERUPLOAD.equals(userName) || Global.MENTALHEALTH.equals(userName)
							|| Global.ACCOMMODATION.equals(userName) || Global.SDAADMIN.equals(userName)
							|| Global.ONESTOP.equals(userName)) && Global.USER_TYPE_LDAP.equalsIgnoreCase(userType)) {
						// 只容许上传csv格式的文件
						if (prefix != null && !"csv".equalsIgnoreCase(prefix)) {
							hdfsMap = Maps.newHashMap();
							hdfsMap.put("returnMessage", "不是csv文件类型，请上传csv文件类型的文件！");
							hdfsMap.put("returnCode", ReturnCode.UPLOAD_TYPE_ERROR);
						} else {
							hdfsMap = infoDocumentServiceImpl.csvContentCheck(in, request);
						}
					}
					/* 校验文件内容的准确性：end 3.此功能是针对利物浦指定话开发 */
					if (hdfsMap == null) {
						// 上传文件到hdfs中
						hdfsMap = this.infoDocumentServiceImpl.uploadFile(copyIn, dstPath + fileName, request);
					}
					if (copyIn != null) {
						copyIn.close();
					}
					if (in != null) {
						in.close();
					}
				}
				retMap.put(fileName, hdfsMap);
			}
			resp.setReturnCode(ReturnCode.RETURN_CODE_SUCCESS);
			resp.setReturnObject(retMap);
			return resp.toString();
		} catch (Exception e) {
			logger.error("uploadFile 文件上传失败！", e);
		} finally {
			logUtils.writeLog("上传文件: " + dstPath + "/" + fileName, LogUtils.LOGTYPE_SYS,
					GetUserUtils.getUser(request).getUserName());
		}
		return resp.toString();
	}

	/**
	 * 校验限制文件大小
	 *
	 * @param byteSize
	 */
	private boolean isByteExceLimit(long byteSize) {
		if (byteSize > Long.parseLong(SystemConfig.HDFS_FILE_LIMIT_SIZE)) {
			return true;
		}
		return false;
	}

	/**
	 * 下载文
	 *
	 * @param request
	 * @param srcPath
	 *            资源文件
	 * @return
	 */
	@RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
	@ResponseBody
	public String downloadFile(HttpServletRequest request, HttpServletResponse response, String srcPath,
			String fileName) {
		BaseResponse resp = new BaseResponse();
		OutputStream out = null;
		FSDataInputStream in = null;
		Map<String, Object> hdfsMap = null;
		try {
			if (StringUtils.isBlank(srcPath) || StringUtils.isBlank(fileName)) {
				resp.setReturnCode(ReturnCode.RETURN_CODE_PARAM_NULL);
				return resp.toString();
			}
			String userRootPath = GetUserUtils.getUser(request).getUserName() + "/";
			// srcPath = userRootPath + new String(srcPath.getBytes("iso8859-1"),"UTF-8");
			srcPath = userRootPath + srcPath;
			fileName = new String(fileName.getBytes(), "ISO-8859-1");
			logger.info("/downloadFile param: srcPath=" + srcPath + ",fileName=" + fileName);

			response.setContentType("application/x-msdownload");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			out = response.getOutputStream();
			hdfsMap = infoDocumentServiceImpl.downloadFile(srcPath);

			if (null == hdfsMap || !ReturnCode.RETURN_CODE_SUCCESS.equals(hdfsMap.get("returnCode"))) {
				return JsonUtils.toJson(hdfsMap);
			}
			in = (FSDataInputStream) hdfsMap.get("dataStream");
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} catch (Exception e) {
			logger.error("downloadFile error", e);
			resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (hdfsMap != null && hdfsMap.get("fileSystem") != null) {
					FileSystem fileSys = (FileSystem) hdfsMap.get("fileSystem");
					fileSys.close();
				}
			} catch (Exception e2) {
				logger.error("close stream error ,error msg is", e2);
			}
			logUtils.writeLog("下载文件: " + srcPath + "/" + fileName, LogUtils.LOGTYPE_SYS,
					GetUserUtils.getUser(request).getUserName());
		}
		return resp.toString();
	}

	/**
	 * 删除文件
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public String delete(HttpServletRequest request, @RequestBody DocumentInfo info) {
		Map<String, String> retMap = new HashMap<String, String>();
		retMap.put("returnCode", ReturnCode.RETURN_CODE_ERROR);
		String srcPath = null;
		if (info != null) {
			srcPath = info.getSrcPath();
		}
		logger.info("deleteFile param: srcPath=" + srcPath);
		if (StringUtils.isBlank(srcPath)) {
			retMap.put("returnCode", ReturnCode.RETURN_CODE_PARAM_NULL);
			return JsonUtils.toJson(retMap);
		}
		try {
			String userRootPath = GetUserUtils.getUser(request).getUserName() + "/";
			String[] srcPaths = srcPath.split(",");
			for (String strSrc : srcPaths) {
				retMap = this.infoDocumentServiceImpl.delete(userRootPath + strSrc);
				retMap.put("returnCode", ReturnCode.RETURN_CODE_SUCCESS);
			}
		} catch (Exception e) {
			logger.error("deleteFile error", e);
		} finally {
			logUtils.writeLog("删除文件: " + srcPath, LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		return JsonUtils.toJson(retMap);
	}

	/**
	 * 复制文件
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/copyFile", method = RequestMethod.POST)
	@ResponseBody
	public String copyFile(HttpServletRequest request, @RequestBody DocumentInfo info) {
		Map<String, String> retMap = new HashMap<String, String>();
		try {
			if (null == info) {
				retMap.put("returnCode", ReturnCode.RETURN_CODE_PARAM_NULL);
				return JsonUtils.toJson(retMap);
			}
			logger.info("/copyFile param  = " + info.toString());
			String userRootPath = GetUserUtils.getUser(request).getUserName() + "/";
			String srcPath = userRootPath + info.getSrcPath();
			String dstPath = info.getDstPath();
			if (StringUtils.isBlank(dstPath)) {
				dstPath = userRootPath;
			} else {
				dstPath = userRootPath + dstPath;
			}
			retMap = infoDocumentServiceImpl.copyFile(srcPath, dstPath, info.getFileName(), request);
		} catch (Exception e) {
			logger.error("copyFile error", e);
		} finally {
			logUtils.writeLog("复制文件:" + JsonUtils.toJson(info), LogUtils.LOGTYPE_SYS,
					GetUserUtils.getUser(request).getUserName());
		}
		return JsonUtils.toJson(retMap);
	}

	/**
	 * 移动文件
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/moveFile", method = RequestMethod.POST)
	@ResponseBody
	public String moveFile(HttpServletRequest request, @RequestBody DocumentInfo info) {
		Map<String, String> retMap = new HashMap<String, String>();
		try {
			if (null == info) {
				retMap.put("returnCode", ReturnCode.RETURN_CODE_PARAM_NULL);
				return JsonUtils.toJson(retMap);
			}
			logger.info("/moveFile param  = " + info.toString());
			String userRootPath = GetUserUtils.getUser(request).getUserName() + "/";
			String srcPath = userRootPath + info.getSrcPath();
			String dstPath = info.getDstPath();
			if (StringUtils.isBlank(dstPath)) {
				dstPath = userRootPath;
			} else {
				dstPath = userRootPath + dstPath;
			}
			retMap = infoDocumentServiceImpl.moveFile(srcPath, dstPath, info.getFileName());
		} catch (Exception e) {
			logger.error("moveFile error", e);
		} finally {
			logUtils.writeLog("复制文件:" + JsonUtils.toJson(info), LogUtils.LOGTYPE_SYS,
					GetUserUtils.getUser(request).getUserName());
		}
		return JsonUtils.toJson(retMap);
	}

	/**
	 * 重命名文件
	 *
	 * @param request
	 * @param info
	 * @return
	 */
	@RequestMapping(value = "/renameFile", method = RequestMethod.POST)
	@ResponseBody
	public String renameFile(HttpServletRequest request, @RequestBody DocumentInfo info) {
		Map<String, String> retMap = new HashMap<String, String>();
		try {
			if (null == info) {
				retMap.put("returnCode", ReturnCode.RETURN_CODE_PARAM_NULL);
				return JsonUtils.toJson(retMap);
			}
			logger.info("/moveFile param  = " + info.toString());
			String userRootPath = GetUserUtils.getUser(request).getUserName() + "/";
			String srcPath = userRootPath + info.getSrcPath();

			String newSrcPath = srcPath.substring(0, srcPath.lastIndexOf("/") + 1);

			retMap = infoDocumentServiceImpl.moveFile(srcPath, newSrcPath, info.getFileName());
		} catch (Exception e) {
			logger.error("moveFile error", e);
		} finally {
			logUtils.writeLog("复制文件:" + JsonUtils.toJson(info), LogUtils.LOGTYPE_SYS,
					GetUserUtils.getUser(request).getUserName());
		}
		return JsonUtils.toJson(retMap);
	}

	/**
	 * 获取文件子目录
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getPathTree", method = RequestMethod.POST)
	@ResponseBody
	public String getPathTree(HttpServletRequest request, @RequestBody DocumentInfo info) {
		BaseResponse response = new BaseResponse();
		response.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		String srcPath = null;
		if (info != null) {
			srcPath = info.getSrcPath();
		}
		List<DocumentModel> pathList = null;
		String userRootPath = GetUserUtils.getUser(request).getUserName() + "/";
		try {
			if (StringUtils.isBlank(srcPath)) {
				srcPath = userRootPath;
			} else {
				// srcPath = userRootPath + new String(srcPath.getBytes("iso8859-1"), "UTF-8");
				srcPath = userRootPath + srcPath;
			}
			pathList = infoDocumentServiceImpl.getDirectoyAndFileTree(srcPath, userRootPath);
			response.setReturnCode(ReturnCode.RETURN_CODE_SUCCESS);
			response.setReturnObject(pathList);
		} catch (Exception e) {
			logger.error("getPathTree error", e);
		}
		return response.toString();
	}

	/**
	 * 获取文件树
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getDocumentTree", method = RequestMethod.POST)
	@ResponseBody
	public String getDocumentTree(HttpServletRequest request, @RequestBody DocumentInfo info) {
		BaseResponse response = new BaseResponse();
		response.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		String srcPath = null;
		if (info != null) {
			srcPath = info.getSrcPath();
		}
		DocumentModel model = null;
		String userRootPath = GetUserUtils.getUser(request).getUserName() + "/";
		try {
			if (StringUtils.isBlank(srcPath))
				srcPath = userRootPath;
			else {
				// srcPath = userRootPath + new String(srcPath.getBytes("iso8859-1"), "UTF-8");
				srcPath = userRootPath + srcPath;
			}
			model = infoDocumentServiceImpl.getDocumentTree(srcPath, userRootPath);
			model.setSrcPath("");
			model.setName("全部文件");
			response.setReturnObject(model);
			response.setReturnCode(ReturnCode.RETURN_CODE_SUCCESS);
		} catch (Exception e) {
			logger.error("getPathTree error", e);
		}
		return response.toString();
	}

	/**
	 * 创建文件夹
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/createDirectoy", method = RequestMethod.POST)
	@ResponseBody
	public String createDirectoy(HttpServletRequest request, @RequestBody DocumentInfo info) {
		Map<String, String> retMap = new HashMap<String, String>();
		try {
			if (null == info) {
				retMap.put("returnCode", ReturnCode.RETURN_CODE_PARAM_NULL);
				return JsonUtils.toJson(retMap);
			}
			String userRootPath = GetUserUtils.getUser(request).getUserName() + "/";
			String srcPath = info.getSrcPath();

			if (StringUtils.isBlank(srcPath)) {
				srcPath = userRootPath;
			} else {
				srcPath = userRootPath + srcPath;
			}
			logger.info("createDirectoy param: srcPath=" + srcPath);
			retMap = infoDocumentServiceImpl.createDirectoy(srcPath);
		} catch (Exception e) {
			logger.error("createDirectoy error", e);
		} finally {
			logUtils.writeLog("复制文件:" + JsonUtils.toJson(info), LogUtils.LOGTYPE_SYS,
					GetUserUtils.getUser(request).getUserName());
		}
		return JsonUtils.toJson(retMap);
	}
}

package com.hand.bdss.web.datamanage.policy.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.dsmp.model.HiveMetaData;
import com.hand.bdss.dsmp.model.HiveMetaTableField;
import com.hand.bdss.dsmp.model.ReturnCode;
import com.hand.bdss.web.common.constant.Global;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.util.StringUtils;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.datamanage.policy.service.HiveDataPermissionService;
import com.hand.bdss.web.datamanage.policy.service.HiveTableService;

/**
 * hive表逻辑控制类
 * 
 * @author zk
 */
@Controller
@RequestMapping(value = "/hiveTable", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class HiveTableController {

	private static final Logger logger = Logger.getLogger(HiveTableController.class);

	@Resource
	private LogUtils logUtils;

	@Resource
	private HiveTableService hiveTableServiceImpl;

	@Resource
	private HiveDataPermissionService hiveDataPermissionServiceImpl;

	/**
	 * hive表新增
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	public String insertHiveTable(HttpServletRequest request, @RequestBody String strJson) {
		BaseResponse resp = new BaseResponse();

		try {

			if (StringUtils.isBlank(strJson)) {
				resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
				resp.setReturnMessage("请求参数为空！！！");
				return resp.toString();
			}
			HiveMetaData hmd = JsonUtils.toObject(strJson, HiveMetaData.class);

			if (StringUtils.isBlank(hmd.getTabelName())) {
				resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
				resp.setReturnMessage("表名不能为空！");
				return resp.toString();
			}

			// 查询数据库中表名是否存在
			// List<String> tableList =
			// hiveTableServiceImpl.select(hmd.getDbName(),Global.MANAGER);
			List<String> tableList = hiveTableServiceImpl.select(hmd.getDbName(),
					GetUserUtils.getUser(request).getUserName());
			if (tableList != null && tableList.contains(hmd.getTabelName())) {
				resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
				resp.setReturnMessage("表名重复！");
				return resp.toString();
			}

			if (hmd.getMetaTableField() != null && hmd.getMetaTableField().size() > 0) {
				boolean allSubarea = false;
				for (HiveMetaTableField hmtf : hmd.getMetaTableField()) {
					if (hmtf.getFlag() == 1) {
						allSubarea = true;
					}
				}
				if (!allSubarea) {
					resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
					resp.setReturnMessage("表字段不能全都分区！");
					return resp.toString();
				}
			}

			/*
			 * if(StringUtils.isNotBlank(hmd.getLocation())){ File file = new
			 * File(hmd.getLocation()); if(file == null){
			 * resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
			 * resp.setReturnMessage("文件路径不存在！"); return resp.toString(); }
			 * if(!file.isDirectory()){ resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
			 * resp.setReturnMessage("输入路径不是一个目录路径！"); return resp.toString(); } }
			 */

			Boolean flag = hiveTableServiceImpl.insert(hmd, GetUserUtils.getUser(request).getUserName());
			if (flag) {
				resp.setReturnCode(ReturnCode.RETURN_CODE_SUCCESS);
				resp.setReturnMessage("添加成功！");
				return resp.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("dataPermission/add error", e);
		} finally {
			logUtils.writeLog("hive表新增！", LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		resp.setReturnMessage("添加失败！");
		return resp.toString();
	}

	/**
	 * hive表更新
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	public String updateHiveTable(HttpServletRequest request, @RequestBody String strJson) {
		BaseResponse resp = new BaseResponse();

		try {
			if (StringUtils.isBlank(strJson)) {
				resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
				resp.setReturnMessage("请求参数为空！！！");
				return resp.toString();
			}
			HiveMetaData hmd = JsonUtils.toObject(strJson, HiveMetaData.class);
			if (StringUtils.isBlank(hmd.getTabelName())) {
				resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
				resp.setReturnMessage("表名不能为空！");
				return resp.toString();
			}

			//

			Boolean flag = hiveTableServiceImpl.update(hmd, GetUserUtils.getUser(request).getUserName());
			if (flag) {
				resp.setReturnCode(ReturnCode.RETURN_CODE_SUCCESS);
				resp.setReturnMessage("更新成功！");
				return resp.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("dataPermission/add error", e);
		} finally {
			logUtils.writeLog("hive表更新！", LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		resp.setReturnMessage("更新失败！");
		return resp.toString();
	}

	/**
	 * hive表删除
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	public String deleteHiveTable(HttpServletRequest request, @RequestBody String strJson) {
		BaseResponse resp = new BaseResponse();

		try {
			if (StringUtils.isBlank(strJson)) {
				resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
				resp.setReturnMessage("请求参数为空！！！");
				return resp.toString();
			}
			HiveMetaData hmd = JsonUtils.toObject(strJson, HiveMetaData.class);
			if ("*".equals(hmd.getTabelName())) {
				resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
				resp.setReturnMessage("*代表所有表，不能删除！");
				return resp.toString();
			}

			Boolean flag = hiveTableServiceImpl.delete(hmd, GetUserUtils.getUser(request).getUserName());
			if (flag) {
				// 删除对应表的ranger权限
				Boolean dFlag = hiveDataPermissionServiceImpl.deleteOrUpdate(hmd.getDbName(), hmd.getTabelName(),
						GetUserUtils.getUser(request).getUserName());
				if (dFlag) {
					resp.setReturnCode(ReturnCode.RETURN_CODE_SUCCESS);
					resp.setReturnMessage("删除成功！");
					return resp.toString();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("dataPermission/add error", e);
		} finally {
			logUtils.writeLog("hive表删除！", LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		resp.setReturnMessage("删除失败！");
		return resp.toString();
	}

	/**
	 * 查询hive表集合
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/select")
	@ResponseBody
	public String selectHiveTable(HttpServletRequest request, @RequestBody String strJson) {
		BaseResponse resp = new BaseResponse();
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			JSONObject object = JSON.parseObject(strJson);
			String dbName = object.getString("dbName");
			List<String> list = hiveTableServiceImpl.select(dbName, Global.MANAGER);
			list.add("*");
			map.put("data", list);
			return JsonUtils.toJson(map);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hiveTable/select error", e);
		} finally {
			logUtils.writeLog("hive表查询！", LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		resp.setReturnMessage("查询失败！");
		return resp.toString();
	}

	/**
	 * 获取hive指定数据库下指定hive表的所有字段
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/selectFields")
	@ResponseBody
	public String selectHiveTableFields(HttpServletRequest request, @RequestBody String strJson) {
		BaseResponse resp = new BaseResponse();
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			JSONObject object = JSON.parseObject(strJson);
			String dbName = object.getString("dbName");
			String tableName = object.getString("tableName");
			List<HiveMetaTableField> htfList = hiveTableServiceImpl.getHiveMetaTableField(dbName, tableName, "admin");
			map.put("data", htfList);
			return JsonUtils.toJson(map);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hiveTable/selectFields error", e);
		} finally {
			logUtils.writeLog("获取hive指定数据库下指定hive表的所有字段！", LogUtils.LOGTYPE_SYS,
					GetUserUtils.getUser(request).getUserName());
		}
		resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		resp.setReturnMessage("查询失败！");
		return resp.toString();
	}

	/**
	 * 创建hive数据库
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/createHiveDb")
	@ResponseBody
	public String createHiveDb(HttpServletRequest request, @RequestBody String strJson) {
		BaseResponse resp = new BaseResponse();

		try {
			JSONObject object = JSON.parseObject(strJson);
			String dbName = object.getString("dbName");
			Boolean result = hiveTableServiceImpl.createHiveDb(dbName, GetUserUtils.getUser(request).getUserName());
			if (result) {
				resp.setReturnCode(ReturnCode.RETURN_CODE_SUCCESS);
				resp.setReturnMessage("新增成功！");
				return resp.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hiveTable/createHiveDb error", e);
		} finally {
			logUtils.writeLog("创建hive数据库", LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		resp.setReturnMessage("新增失败！");
		return resp.toString();
	}

	/**
	 * 获取hive数据库
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getHiveDb")
	@ResponseBody
	public String getHiveDb(HttpServletRequest request) {
		BaseResponse resp = new BaseResponse();
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			List<String> dbList = hiveTableServiceImpl.getHiveDb(GetUserUtils.getUser(request).getUserName());
			map.put("data", dbList);
			return JsonUtils.toJson(map);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("hiveTable/getHiveDb error", e);
		} finally {
			logUtils.writeLog("获取hive数据库", LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		resp.setReturnMessage("查询失败！");
		return resp.toString();
	}

}

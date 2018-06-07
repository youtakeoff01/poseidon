package com.hand.bdss.web.dataprocessing.processing.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.dev.vo.Task;
import com.hand.bdss.dsmp.component.hive.HiveClient;
import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.model.HivePolicy;
import com.hand.bdss.web.common.constant.Global;
import com.hand.bdss.web.common.em.TaskStatus;
import com.hand.bdss.web.common.em.TaskType;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.common.vo.HiveTableVO;
import com.hand.bdss.web.datamanage.policy.service.HiveDataPermissionService;
import com.hand.bdss.web.dataprocessing.processing.service.DataQueryService;
import com.hand.bdss.web.entity.UserEntity;

/**
 * Created by hand on 2017/8/2.
 */
@Controller
@RequestMapping(value = "/DataQueryController/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

public class DataQueryController {
	private static final Logger logger = LoggerFactory.getLogger(DataQueryController.class);

	private HiveClient hiveClient = new HiveClient(SystemConfig.userName);

	@Autowired
	DataQueryService dataQueryServiceImpl;

	@Resource
	private HiveDataPermissionService hiveDataPermissionServiceImpl;

	@Resource
	private LogUtils logUtils;

	/**
	 * 获取所有hive数据库和hive表名称
	 *
	 * @return
	 */
	@RequestMapping("listHiveDBAndTables")
	public @ResponseBody String listHiveDBAndTables(HttpServletRequest request) {
		logger.info("DataQueryController.listHiveDBAndTables start");
		BaseResponse base = new BaseResponse();
		List<HiveTableVO> hiveList = new ArrayList<HiveTableVO>();

		try {
			hiveClient.setUsername(GetUserUtils.getUser(request).getUserName());
			List<String> databaseList = hiveClient.getHiveDatabases();
			HiveTableVO hiveTable = null;
			if (databaseList != null && databaseList.size() > 0) {
				for (String dbName : databaseList) {
					List<String> list = hiveClient.getHiveTables(dbName);
					for (int i = list.size() - 1; i >= 0; i--) {
						if (list.get(i).contains("_delete_")) {
							list.remove(i);
						}
					}
					hiveTable = new HiveTableVO();
					hiveTable.setDbName(dbName);
					hiveTable.setTableList(list);
					hiveList.add(hiveTable);
				}
			}
		} catch (Exception e) {
			logger.error("-----查询Hive表数据失败,报错位置:HiveClient.getHiveDatabases:报错信息" + e.getMessage());
			base.setReturnCode("0");
			base.setReturnMessage("查询Hive表失败");
			return base.toString();
		}
		if (hiveList != null && hiveList.size() > 0) {
			base.setReturnCode("1");
			base.setReturnMessage("查询Hive表成功");
			base.setReturnObject(hiveList);
		} else {
			base.setReturnCode("0");
			base.setReturnMessage("查询Hive表失败");
		}
		logger.info("DataQueryController.listHiveDBAndTables end");
		return base.toString();
	}

	/**
	 * 查询hive库下的hive表
	 *
	 * @param json
	 * @return
	 */
	@RequestMapping("listHiveTables")
	public @ResponseBody String listHiveTables(@RequestBody String json) {
		logger.info("DataQueryController.listHiveTables start");
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		if (StringUtils.isEmpty(json)) {
			base.setReturnMessage("请求参数为空");
			return base.toString();
		}
		JSONObject jsonObject = JSON.parseObject(json);
		String dbName = jsonObject.getString("dbName");
		try {
			List<String> list = hiveClient.getHiveTables(dbName);
			for (int i = list.size() - 1; i >= 0; i--) {
				if (list.get(i).contains("_delete_")) {
					list.remove(i);
				}
			}
			if (list != null && list.size() > 0) {
				base.setReturnCode("1");
				base.setReturnMessage("hive表查询成功");
				base.setReturnObject(list);
			} else {
				base.setReturnMessage("hive表查询失败");
			}
		} catch (Exception e) {
			logger.error("------hive表查询报错，报错位置:Hiveclient.getHIveTables：报错信息" + e.getMessage());
			base.setReturnMessage("hive表查询失败");
			return base.toString();
		}
		logger.info("DataQueryController.listHiveTables end");
		return base.toString();
	}

	/**
	 * 获取hive表下的字段
	 *
	 * @param json
	 * @return
	 */
	@RequestMapping("getTableFields")
	public @ResponseBody String getTableFields(@RequestBody String json) {
		logger.info("DataQueryController.getTableFields start");
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		if (StringUtils.isEmpty(json)) {
			base.setReturnMessage("请求参数为空");
			return base.toString();
		}
		JSONObject jsonObject = JSON.parseObject(json);
		String dbName = jsonObject.getString("dbName");
		String tableName = jsonObject.getString("tableName");

		List<String> fields = new ArrayList<String>();
		boolean boo = false;
		try {
			List<String> tables = hiveClient.getHiveTables(dbName);
			if (tables != null && tables.size() > 0) {
				for (int i = 0, len = tables.size(); i < len && !boo; i++) {
					String tbName = tables.get(i);
					if (!"".equals(tableName) && tbName.equals(tableName)) {
						Map<String, String> map = hiveClient.getHiveTableFields(dbName, tableName);
						for (Map.Entry<String, String> entry : map.entrySet()) {
							String str = entry.getKey();
							fields.add(str.substring(str.indexOf(".") + 1, str.length()));
						}
						boo = true;
					}
				}
			}
		} catch (Exception e) {
			boo = false;
			logger.error("-----表字段查询报错，报错位置：HiveClient.getHiveTableFields:报错信息" + e.getMessage());
		}
		if (boo) {
			base.setReturnCode("1");
			base.setReturnMessage("表字段查询成功");
			base.setReturnObject(fields);
		} else {
			base.setReturnMessage("表字段查询失败");
		}
		logger.info("DataQueryController.getTableFields end");
		return base.toString();
	}

	/**
	 * 脚本保存
	 *
	 * @param task
	 * @return
	 */
	@RequestMapping("addTask")
	public @ResponseBody String addTask(@RequestBody Task task, HttpServletRequest request) {
		logger.info("DataQueryController addTask task=" + task.toString());
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		try {
			if (task.getTaskName().substring(0, 1).matches("^\\d+$")) {
				base.setReturnMessage("首字母不可位数字");
				return base.toString();
			}

			task.setStatus(TaskStatus.init.getIndex() + "");
			task.setAccount(Long.valueOf(GetUserUtils.getUser(request).getId()));
			task.setTaskType(TaskType.SCRIPT.getIndex().toString());
			boolean flag = dataQueryServiceImpl.insert(task);
			if (flag) {
				base.setReturnCode("1");
				base.setReturnMessage("任务新增成功!");
			} else {
				base.setReturnCode("0");
				base.setReturnMessage("任务新增失败!");
			}
		} catch (Exception e) {
			logger.error("addTask error!", e);
			base.setReturnMessage("任务新增失败!");
		} finally {
			logUtils.writeLog("新建脚本任务: " + task.getTaskName(), LogUtils.LOGTYPE_SYS,
					GetUserUtils.getUser(request).getUserName());
		}

		return base.toString();
	}

	/**
	 * 脚本数据查询
	 *
	 * @param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("query")
	public @ResponseBody String query(@RequestBody Task task, HttpServletRequest req) {
		BaseResponse base = new BaseResponse();
		base.setReturnCode("0");
		String retCode = "1";
		try {
			// 验证用户是否有查询表的权限
			if (StringUtils.isNotBlank(task.getSqlType()) && StringUtils.isNotBlank(task.getSqlStc())) {
				String username = null;
				HivePolicy policy = new HivePolicy();
				policy.setServiceName(Global.HIVE_SERVER);
				UserEntity user = GetUserUtils.getUser(req);

				if (Global.MANAGER_ROLE.contains(String.valueOf(user.getRoleId()))) {
					username = Global.MANAGER;
				} else {
					username = GetUserUtils.getUser(req).getUserName();
				}
				// String result =
				// hiveDataPermissionServiceImpl.selectBySql(policy,"admin",task.getSqlStc());
				String result = hiveDataPermissionServiceImpl.selectBySql(policy, username, task.getSqlStc());
				if (StringUtils.isNotBlank(result)) {
					base.setReturnMessage(result + "表无查询权限！");
					return base.toString();
				}
			}

			logger.info("DataQueryController query task=" + task.toString());
			Map<String, Object> retMap = dataQueryServiceImpl.query(task);
			List<Map<String, Object>> list = (List<Map<String, Object>>) retMap.get("data");
			// 数据为空或者查询失败
			if (retMap.get("retCode") != null || (list != null && list.size() == 0)) {
				throw new Exception("查询异常");
			}
			base.setReturnObject(retMap);
			base.setReturnCode("1");
			base.setReturnMessage("脚本SQL查询成功！");
		} catch (Exception e) {
			logger.error("query error!", e);
			retCode = "0";
			base.setReturnMessage("脚本SQL查询失败！");
		}
		// 保存执行语句
		String sql = task.getSqlType() + ":" + task.getSqlStc();
		logUtils.writeLog(sql, LogUtils.LOGTYPE_SQL, GetUserUtils.getUser(req).getUserName(), retCode);
		return base.toString();
	}

}

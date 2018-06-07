package com.hand.bdss.web.datamanage.policy.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.common.vo.HiveTableVO;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.dsmp.model.HivePolicy;
import com.hand.bdss.dsmp.model.ReturnCode;
import com.hand.bdss.web.common.constant.Global;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.util.StringUtils;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.datamanage.policy.service.HiveDataPermissionService;
import com.hand.bdss.web.entity.UserEntity;

/**
 * Hive表数据权限控制类
 * @author zk
 */
@Controller
@RequestMapping(value = "/hiveDataPermission",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class HiveDataPermissionController {

	private static final Logger logger = Logger.getLogger(HiveDataPermissionController.class);

    @Resource
    private LogUtils logUtils;

    @Resource
	private HiveDataPermissionService hiveDataPermissionServiceImpl;

	/**
	 * 增加用户数据权限
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	public String insertHivePermission(HttpServletRequest request,@RequestBody String strJson){
		BaseResponse resp = new BaseResponse();

		try{
			if (StringUtils.isBlank(strJson)) {
				resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
				resp.setReturnMessage("请求参数为空！！！");
	            return resp.toString();
	        }
			HivePolicy policy = JsonUtils.toObject(strJson, HivePolicy.class);

			//为策略名生成唯一主键
			policy.setName(UUID.randomUUID().toString());
			policy.setServiceName(Global.HIVE_SERVER);
			//判断对应的数据库表是否存在
			List<HivePolicy> list = hiveDataPermissionServiceImpl.select(policy,"",policy.getDatabases().get(0));
			if(list != null && list.size() > 0){
				resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
				resp.setReturnMessage("用户已配置该数据库表操作权限！");
				return resp.toString();
			}
			Boolean flag = hiveDataPermissionServiceImpl.insert(policy);
			if(flag){
				resp.setReturnCode(ReturnCode.RETURN_CODE_SUCCESS);
				resp.setReturnMessage("添加成功！");
				return resp.toString();
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("dataPermission/add error", e);
		}finally{
			logUtils.writeLog("hive表数据权限新增！", LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		resp.setReturnMessage("添加失败！");
		return resp.toString();
	}

	/**
	 * 修改用户数据权限
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	public String updateHivePermission(HttpServletRequest request,@RequestBody String strJson){
		BaseResponse resp = new BaseResponse();

		try{
			if (StringUtils.isBlank(strJson)) {
				resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
				resp.setReturnMessage("请求参数为空！！！");
	            return resp.toString();
	        }
			HivePolicy policy = JsonUtils.toObject(strJson, HivePolicy.class);
			//判断对应的数据库表是否存在
			List<HivePolicy> list = hiveDataPermissionServiceImpl.select(policy,"",policy.getDatabases().get(0));
			if(list != null && list.size() > 0){
				resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
				resp.setReturnMessage("用户已配置该数据库表操作权限！");
				return resp.toString();
			}
			Boolean flag = hiveDataPermissionServiceImpl.update(policy);
			if(flag){
				resp.setReturnCode(ReturnCode.RETURN_CODE_SUCCESS);
				resp.setReturnMessage("更新成功！");
				return resp.toString();
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("dataPermission/update error", e);
		}finally{
			logUtils.writeLog("hive表数据权限更新！", LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		resp.setReturnMessage("更新失败！");
		return resp.toString();
	}

	/**
	 * 删除用户数据权限
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	public String deleteHivePermission(HttpServletRequest request,@RequestBody String strJson){
		BaseResponse resp = new BaseResponse();

		try{
			if (StringUtils.isBlank(strJson)) {
				resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
				resp.setReturnMessage("请求参数为空！！！");
	            return resp.toString();
	        }
			HivePolicy policy = JsonUtils.toObject(strJson, HivePolicy.class);
			Boolean flag = hiveDataPermissionServiceImpl.delete(policy);
			if(flag){
				resp.setReturnCode(ReturnCode.RETURN_CODE_SUCCESS);
				resp.setReturnMessage("删除成功！");
				return resp.toString();
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("dataPermission/delete error", e);
		}finally{
			logUtils.writeLog("hive表数据权限删除！", LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		resp.setReturnMessage("删除失败！");
		return resp.toString();
	}

	/**
	 * 分页查询用户hive表数据权限
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/selectPage")
	@ResponseBody
	public String selectHivePermissionPage(HttpServletRequest request,@RequestBody String strJson){
		BaseResponse resp = new BaseResponse();
		try {
			JSONObject object = JSON.parseObject(strJson);
			String username = object.getString("username");
			int pageNo = object.getIntValue("pageNo");
			int pageSize = object.getIntValue("pageSize");

			HivePolicy policy = new HivePolicy();
			policy.setServiceName(Global.HIVE_SERVER);

			//判断用户是否是管理员
			UserEntity user = GetUserUtils.getUser(request);

			if(StringUtils.isBlank(username)){
				if(Global.MANAGER_ROLE.contains(String.valueOf(user.getRoleId()))){
					username = Global.MANAGER;
				}else{
					username = GetUserUtils.getUser(request).getUserName();
				}
			}

			if(StringUtils.isBlank(username)){
				username = GetUserUtils.getUser(request).getUserName();
			}
			Map<String,Object> resultMap = hiveDataPermissionServiceImpl.selectPage(policy,username,pageNo,pageSize);
			return JsonUtils.toJson(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("dataPermission/selectPage error", e);
		}finally{
			logUtils.writeLog("hive表数据权限分頁查询！", LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		resp.setReturnMessage("查询失败！");
		return resp.toString();
	}

	/**
	 * 查询用户hive表数据权限
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/select")
	@ResponseBody
	public String selectHivePermission(HttpServletRequest request,@RequestBody String strJson){
		BaseResponse resp = new BaseResponse();
		try {
			JSONObject object = JSON.parseObject(strJson);
			String username = object.getString("username");
			String database = object.getString("database");
			HivePolicy policy = object.getObject("policy", HivePolicy.class);

			policy.setServiceName(Global.HIVE_SERVER);
			List<HivePolicy> list = hiveDataPermissionServiceImpl.select(policy,username,database);
			return JsonUtils.toJson(list);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("dataPermission/select error", e);
		}finally{
			logUtils.writeLog("hive表数据权限查询！", LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		resp.setReturnMessage("查询失败！");
		return resp.toString();
	}

	/**
	 * 根据用户查询有操作权限的hive表集合
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/selectTables")
	@ResponseBody
	public String selectHiveTables(HttpServletRequest request,@RequestBody String strJson){
		BaseResponse resp = new BaseResponse();
		try {
			Map<String,Object> map = new HashMap<String,Object>();
			JSONObject object = JSON.parseObject(strJson);
			String username = object.getString("username");
			String tableName = object.getString("tableName");
			UserEntity user = GetUserUtils.getUser(request);
			if(Global.MANAGER_ROLE.contains(String.valueOf(user.getRoleId()))){
				username = Global.MANAGER;
				map.put("isAdmin", true);
			}else{
				username = GetUserUtils.getUser(request).getUserName();
				map.put("isAdmin", false);
			}
			HivePolicy policy = object.getObject("policy", HivePolicy.class);
			if(policy == null){
				policy = new HivePolicy();
			}

			policy.setServiceName(Global.HIVE_SERVER);
			List<HivePolicy> list = hiveDataPermissionServiceImpl.selectTables(policy,username,tableName);

			map.put("data", list);
			return JsonUtils.toJson(map);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("dataPermission/selectTables error", e);
		}finally{
			logUtils.writeLog("根据用户查询有操作权限的hive表集合！", LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		resp.setReturnMessage("查询失败！");
		return resp.toString();
	}

	/**
	 * 根据sql查询用户是否拥有操作表的权限
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/selectBySql")
	@ResponseBody
	public String selectBySql(HttpServletRequest request,@RequestBody String strJson){
		BaseResponse resp = new BaseResponse();
		try {
			JSONObject object = JSON.parseObject(strJson);
			String sql = object.getString("sql");
			HivePolicy policy = new HivePolicy();
			policy.setServiceName(Global.HIVE_SERVER);
			String result = hiveDataPermissionServiceImpl.selectBySql(policy,GetUserUtils.getUser(request).getUserName(),sql);
			if(StringUtils.isNotBlank(result)){
				resp.setReturnCode(ReturnCode.RETURN_CODE_SUCCESS);
				resp.setReturnMessage("查询成功！");
				return resp.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("dataPermission/selectBySql error", e);
		}finally{
			logUtils.writeLog("根据sql查询用户是否拥有操作表的权限！", LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		resp.setReturnMessage("查询失败！");
		return resp.toString();
	}


	/**
	 * 根据用户获取数据及数据库
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getHiveDbAndTables")
	@ResponseBody
	public String getHiveDbAndTables(HttpServletRequest request){
		BaseResponse resp = new BaseResponse();

		try{
			String username = null;
			UserEntity user = GetUserUtils.getUser(request);

			if(Global.MANAGER_ROLE.contains(String.valueOf(user.getRoleId()))){
				username = Global.MANAGER;
			}else{
				username = GetUserUtils.getUser(request).getUserName();
			}
			List<HiveTableVO> hiveList = hiveDataPermissionServiceImpl.getHiveDbAndTables(username);
			resp.setReturnCode(ReturnCode.RETURN_CODE_SUCCESS);
			resp.setReturnObject(hiveList);
			return resp.toString();
		}catch(Exception e){
			e.printStackTrace();
			logger.error("hiveTable/getHiveDbAndTables error", e);
		}finally{
			logUtils.writeLog("根据用户获取数据及数据库", LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		resp.setReturnCode(ReturnCode.RETURN_CODE_ERROR);
		resp.setReturnMessage("查询失败！");
		return resp.toString();
	}
}

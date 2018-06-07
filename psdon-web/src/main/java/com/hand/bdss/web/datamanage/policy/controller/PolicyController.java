package com.hand.bdss.web.datamanage.policy.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.datamanage.policy.service.PolicyService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.dsmp.model.HivePolicy;
import com.hand.bdss.web.entity.RangerEntity;

import com.hand.bdss.web.datamanage.metadata.service.MetaDataService;
import com.hand.bdss.web.datamanage.policy.service.RangerService;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.vo.BaseResponse;

@Controller
@RequestMapping(value = "/policyController/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class PolicyController {
    @Resource
    private PolicyService policyServiceImpl;
    @Resource
    private RangerService rangerServiceImpl;
    @Resource
    private MetaDataService metaDataServiceImpl;
    @Resource
    LogUtils logUtils;

    private static final Logger logger = LoggerFactory.getLogger(PolicyController.class);

    /**
     * 创建用户时的校验
     *
     * @return
     */
    @RequestMapping("checkUsers")
    public @ResponseBody
    String checkUsers(@RequestBody String json) {
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(json)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求的参数为空！");
            return base.toString();
        }
        JSONObject object = JSON.parseObject(json);
        String user = object.getString("user");
        RangerEntity rangerEntity = new RangerEntity();
        rangerEntity.setUser(user);
        RangerEntity ranger = null;
        int id = 0;
        try {
            id = rangerServiceImpl.checkUser(rangerEntity);
            if (id != 0) {
                ranger = rangerServiceImpl.getUser(rangerEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (id != 0) {
            base.setReturnCode("1");
            base.setReturnMessage("校验成功!");
            base.setReturnObject(ranger);
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("用户列已存在!");
        }
        return base.toString();
    }

    /**
     * 新增ranger策略
     *
     * @param strJson
     * @param request
     * @return
     */
    @RequestMapping("insertPolicy")
    public @ResponseBody
    String insertPolicy(@RequestBody String strJson, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNoneBlank(strJson)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求参数为空！！！");
            return base.toString();
        }
        HivePolicy hive = JsonUtils.toObject(strJson, HivePolicy.class);
        HivePolicy hivePolicy = new HivePolicy();
        hivePolicy.setTables(hive.getTables());
        List<HivePolicy> hivePolicys = null;
        try {
            hivePolicys = rangerServiceImpl.listHivePolicy(hivePolicy, 0, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (hivePolicys.size() > 0 && hivePolicys != null) {
            base.setReturnCode("0");
            base.setReturnMessage("表对应的策略已经存在");
            return base.toString();
        }
        boolean boo = false;
        try {
            boo = policyServiceImpl.insertPolicy(policyServiceImpl.getRangerPolicy(strJson, "insert"), request);
        } catch (Exception e) {
            e.printStackTrace();
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error("插入用户权限报错，报错位置：PolicyServiceImpl.insertPolicy.报错信息：" + e.getMessage());
        } finally {
            logUtils.writeLog("新增ranger策略: " + strJson, LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
        }
        if (boo) {
            base.setReturnCode("1");
            base.setReturnMessage("插入策略操作成功！");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("插入策略操作失败！");
        }
        return base.toString();
    }

    /**
     * 修改ranger策略
     *
     * @param strJson
     * @param request
     * @return
     */
    @RequestMapping("updatePolicy")
    public @ResponseBody
    String updatePolicy(@RequestBody String strJson, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(strJson)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求参数为空！！！");
            return base.toString();
        }
        boolean boo = false;
        try {
            boo = policyServiceImpl.updatePolicy(strJson, request);
        } catch (Exception e) {
            e.printStackTrace();
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error("修改用户权限报错，报错位置：PolicyServiceImpl.updatePolicy.报错信息：" + e.getMessage());
            boo = false;
        } finally {
            logUtils.writeLog("修改ranger策略: " + strJson, LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
        }
        if (boo) {
            base.setReturnCode("1");
            base.setReturnMessage("更新策略操作成功！");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("更新策略操作失败！");
        }
        return base.toString();
    }

    /**
     * 修改时获取hive表
     *
     * @param strJson
     * @param request
     * @return flag 0表示未选，1表示已选
     */
    @RequestMapping("getTables")
    public @ResponseBody
    String getSelectedTables(@RequestBody String strJson, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(strJson)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求参数为空！！！");
            return base.toString();
        }
        JSONObject object = JSON.parseObject(strJson);
        String metaDataType = object.getString("metaDataType");
        int id = object.getInteger("id");
        HivePolicy hive = new HivePolicy();
        List<String> tables = null;
        List<String> tableNames = null;
        List<Map> result = new ArrayList();
        Map<String, Object> map = null;
        try {
            hive = rangerServiceImpl.getSelectedTables(id, request);
            tableNames = metaDataServiceImpl.getHiveOrHbaseTableNames(metaDataType);
            tables = hive.getTables();
            for (String table : tableNames) {
                map = new HashMap();
                map.put("name", table);
                if (tables.contains(table)) {
                    map.put("flag", "1");
                } else {
                    map.put("flag", "0");
                }
                result.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error("修改用户权限报错，报错位置：PolicyServiceImpl.updatePolicy.报错信息：" + e.getMessage());
        }
        if (tables != null) {
            base.setReturnCode("1");
            base.setReturnMessage("获取已选择的hive表操作成功！");
            base.setReturnObject(result);
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("获取已选择的hive表操作失败！");
        }
        return base.toString();
    }

    /**
     * 删除ranger策略
     *
     * @param strJson
     * @param request
     * @return
     */
    @RequestMapping("delPolicy")
    public @ResponseBody
    String delPolicy(@RequestBody String strJson, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(strJson)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求参数为空！！！");
            return base.toString();
        }
        boolean boo = false;
        try {
            boo = policyServiceImpl.delPolicy(strJson, request);
        } catch (Exception e) {
            e.printStackTrace();
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error("删除用户权限报错，报错位置：PolicyServiceImpl.delPolicy.报错信息：" + e.getMessage());
            boo = false;
        } finally {
            logUtils.writeLog("删除ranger策略: " + strJson, LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
        }
        if (boo) {
            base.setReturnCode("1");
            base.setReturnMessage("策略删除操作成功！");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("策略删除操作失败！");
        }
        return base.toString();
    }

    /**
     * 查询ranger策略
     *
     * @param strJson
     * @param request
     * @return
     */
    @RequestMapping("selectPolicy")
    public @ResponseBody
    String selectPolicy(@RequestBody String strJson, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(strJson)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求参数为空！！！");
            return base.toString();
        }
        HivePolicy hive = JsonUtils.toObject(strJson, HivePolicy.class);
        JSONObject object = JSON.parseObject(strJson);
        int startPage = object.getIntValue("startPage");
        int count = object.getIntValue("count");
        List<HivePolicy> hivePolicys = null;
        int countAll = 0;
        try {
            hivePolicys = rangerServiceImpl.listHivePolicy(hive, (startPage - 1) * 10, count);
            countAll = rangerServiceImpl.getCounts(hive);
        } catch (Exception e) {
            hivePolicys = null;
            e.printStackTrace();
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error("查询用户权限报错，报错位置：PolicyServiceImpl.selectPolicy.报错信息：" + e.getMessage());
        }
        if (hivePolicys == null) {
            base.setReturnCode("0");
            base.setReturnMessage("查询策略操作失败！");
            return base.toString();
        }
        base.setReturnCode("1");
        base.setReturnMessage("查询策略操作成功！");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("hivePolicys", hivePolicys);
        map.put("countAll", countAll);
        base.setReturnObject(map);
        return base.toString();
    }

}

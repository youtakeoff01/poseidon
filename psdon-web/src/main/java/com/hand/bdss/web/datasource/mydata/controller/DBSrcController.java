package com.hand.bdss.web.datasource.mydata.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.hand.bdss.dev.util.JacksonUtil;
import com.hand.bdss.web.datasource.mydata.service.DBSrcService;
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
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.util.StrUtils;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.common.vo.DBSrcVO;

/**
 * 数据源配置的控制层
 *
 * @author 13696
 */
@Controller
@RequestMapping(value = "/dbSrcController/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DBSrcController {

    @Resource(name = "dBSrcServiceImpl")
    private DBSrcService dBSrcServiceImpl;

    @Resource
    private LogUtils logUtils;

    private static final Logger logger = LoggerFactory.getLogger(DBSrcController.class);

    /**
     * 数据源插入操作
     *
     * @param dbSrcVO
     * @return
     */
    @RequestMapping("insertDBSrc")
    public @ResponseBody
    String insertDBSrc(@RequestBody DBSrcVO dbSrcVO, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (dbSrcVO == null) {
            base.setReturnCode("0");
            base.setReturnMessage("请求参数为空！！！");
            return base.toString();
        }
        int i = 0;
        try {
            i = dBSrcServiceImpl.insertDBSrc(dbSrcVO, request);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            logUtils.writeLog("数据源插入操作: " + dbSrcVO.getDbName(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
        }
        if (i > 0) {
            base.setReturnCode("1");
            base.setReturnMessage("数据源插入操作成功！");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("数据源插入操作失败！");
        }
        return base.toString();
    }

    /**
     * 数据源的查询操作
     */
    @RequestMapping("listDBSrcs")
    public @ResponseBody
    String listDBSrcs(@RequestBody String json, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(json)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求参数为空！！！");
            return base.toString();
        }
        DBSrcVO dbSrcVO = JacksonUtil.jsonToBean(json, DBSrcVO.class);
        JsonNode jsonNode = JacksonUtil.getJsonNode(json);

        int startPage = jsonNode.get("startPage").asInt();
        int count = jsonNode.get("count").asInt();

        List<DBSrcVO> dbsrcvos = null;
        int counts = 0;
        try {
            //解决模糊查询时的sql注入问题
            if (dbSrcVO != null && StringUtils.isNotBlank(dbSrcVO.getDbName())) {
                String dbName = dbSrcVO.getDbName();
                if (dbName.endsWith("_")) {
                    dbName = dbName.substring(0, dbName.length() - 1) + "\\_";
                    dbSrcVO.setDbName(dbName);
                }
            }
            //解决模糊查询时的sql注入问题
            if (dbSrcVO != null && StringUtils.isNotBlank(dbSrcVO.getSrcName())) {
                String srcName = dbSrcVO.getSrcName();
                srcName = StrUtils.escapeStr(srcName);
                dbSrcVO.setSrcName(srcName);
            }
            dbSrcVO.setCreateAccount(GetUserUtils.getUser(request).getId());
            dbsrcvos = dBSrcServiceImpl.listDBSrcs(dbSrcVO, (startPage - 1) * 10, count);
            counts = dBSrcServiceImpl.getCounts(dbSrcVO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        base.setReturnCode("1");
        base.setReturnMessage("数据源查询操作成功！");
        Map<String, Object> map = new HashMap<>();
        map.put("dbsrcvos", dbsrcvos);
        map.put("counts", counts);
        base.setReturnObject(map);
        return base.toString();
    }

    /**
     * 查询所有的数据源操作
     */
    @RequestMapping("listAllDBSrcs")
    public @ResponseBody
    String listAllDBSrcs(@RequestBody String json) {
        BaseResponse base = new BaseResponse();
        String dbType = null;
        if (StringUtils.isNotBlank(json)) {
            JSONObject object = JSON.parseObject(json);
            dbType = object.getString("dbType");
        }

        DBSrcVO dbSrcVO = new DBSrcVO();
        List<DBSrcVO> dbsrcvos = null;
        int counts = 0;
        try {
            dbSrcVO.setDbType(dbType);
            dbsrcvos = dBSrcServiceImpl.listDBSrcs(dbSrcVO, 0, 1000000);
            counts = dBSrcServiceImpl.getCounts(dbSrcVO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        base.setReturnCode("1");
        base.setReturnMessage("所有数据源查询操作成功！");
        Map<String, Object> map = new HashMap<>();
        map.put("dbsrcvos", dbsrcvos);
        map.put("counts", counts);
        base.setReturnObject(map);
        return base.toString();
    }

    /**
     * 数据源的删除操作--(要涉及etl job的重新生成 根据数据源的id去查询etl job 配置表，看看有没有通过该数据源生成的job，如果有
     * 就要将对应的job删除。)暂时不涉及删除job
     */
    @RequestMapping(value = "deleteDBSrcsById")
    public @ResponseBody
    String deleteDBSrcsById(@RequestBody List<String> ids, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (!(ids != null && ids.size() > 0)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求参数为空！！！");
            return base.toString();
        }
        int i = 0;
        try {
            i = dBSrcServiceImpl.deleteDBSrcsById(ids, request);
        } catch (Exception e) {
            i = 0;
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error(
                    "---------------调用ETL接口生成job任务报错，报错位置：DBSrcServiceImpl.deleteDBSrcsById:报错信息" + e.getMessage());
            e.printStackTrace();
        } finally {
            logUtils.writeLog(" 数据源的删除操作: " + JsonUtils.toJson(ids), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
        }
        if (i > 0) {
            base.setReturnCode("1");
            base.setReturnMessage("数据源删除操作成功。");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("数据源删除操作失败！");
        }
        return base.toString();
    }

    /**
     * 数据源的修改--要涉及etl job的重新生成 根据数据源的id去查询etl job 配置表，看看有没有通过该数据源生成的job，如果有
     * 则要通过新的数据源连接重新生成job
     */
    @RequestMapping(value = "updateDBSrcsById")
    public @ResponseBody
    String updateDBSrcsById(@RequestBody DBSrcVO dbSrcVO, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (dbSrcVO == null) {
            base.setReturnCode("0");
            base.setReturnMessage("请求参数为空！！！");
            return base.toString();
        }
        int i = 0;
        try {
            i = dBSrcServiceImpl.updateDBSrcsById(dbSrcVO, request);
        } catch (Exception e) {
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error(
                    "---------------调用ETL接口生成job任务报错，报错位置：DBSrcServiceImpl.updateDBSrcsById:报错信息" + e.getMessage());
            e.printStackTrace();
        } finally {
            logUtils.writeLog(" 数据源的更新操作: " + JsonUtils.toJson(dbSrcVO.getDbName()), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
        }
        if (i > 0) {
            base.setReturnCode("1");
            base.setReturnMessage("数据源修改操作成功！");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("数据源修改操作失败！");
        }
        return base.toString();

    }

    @RequestMapping(value = "checkSrcName")
    public @ResponseBody
    String checkSrcName(@RequestBody String json) {
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(json)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求参数为空！！！");
            return base.toString();
        }
        JSONObject object = JSON.parseObject(json);
        String srcName = object.getString("srcName");
        DBSrcVO dbSrcVO = new DBSrcVO();
        dbSrcVO.setSrcName(srcName);
        int i = 0;
        try {
            i = dBSrcServiceImpl.checkSrcName(dbSrcVO);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (i == 0) {
            base.setReturnCode("0");
            base.setReturnMessage("数据源名称不存在，可以使用!");
        } else {
            base.setReturnCode("1");
            base.setReturnMessage("数据源已存在!");
        }
        return base.toString();
    }

    /**
     * hive数据仓库新增
     *
     * @param dbSrcVO
     * @param request
     * @return
     */
    @RequestMapping("createDBSrc")
    public @ResponseBody
    String createDBSrc(@RequestBody DBSrcVO dbSrcVO, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (dbSrcVO == null) {
            base.setReturnCode("0");
            base.setReturnMessage("请求参数为空！！！");
            return base.toString();
        }
        int i = 0;
        try {
            i = dBSrcServiceImpl.createDBSrc(dbSrcVO);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            logUtils.writeLog("数据源hive库新增操作: " + dbSrcVO.getDbName(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
        }
        if (i > 0) {
            base.setReturnCode("1");
            base.setReturnMessage("数据源HIVE库新增操作成功！");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("数据源HIVE库新增操作失败！");
        }
        return base.toString();
    }
}

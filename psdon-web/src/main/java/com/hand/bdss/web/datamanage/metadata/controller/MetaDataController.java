package com.hand.bdss.web.datamanage.metadata.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.dev.util.JacksonUtil;
import com.hand.bdss.dsmp.model.HiveMetaData;
import com.hand.bdss.dsmp.model.HiveMetaDesc;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.util.StrUtils;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.datamanage.metadata.service.MetaDataService;
import com.hand.bdss.web.entity.MetaDataEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * hive 元数据的操作controller
 *
 * @author dingshuang
 */
@Controller
@RequestMapping(value = "/metaDataController/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class MetaDataController {
    @Resource
    private MetaDataService metaDataServiceImpl;
    @Resource
    private LogUtils logUtils;

    private static final Logger logger = LoggerFactory.getLogger(MetaDataController.class);

    /**
     * hive表或者hbase表的元数据的新建
     *
     * @param metadata
     * @return
     */
    @RequestMapping("insertMetaData")
    public @ResponseBody
    String insertMetaData(@RequestBody String metadata, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        String success = "表创建成功。";
        String error = "表创建失败!";
        if (!StringUtils.isNotBlank(metadata)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求的参数为空！");
            return base.toString();
        }
        HiveMetaData hiveMetaData = JsonUtils.toObject(metadata, HiveMetaData.class);
        if ("hive".equalsIgnoreCase(hiveMetaData.getMetaDataType())) {
            boolean boo = false;
            try {
                boo = metaDataServiceImpl.insertHiveMetaData(metadata, request);
                logUtils.writeLog("新建元数据hive: " + metadata, LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());

            } catch (Exception e) {
                boo = false;
                logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
                logger.error("---------------hive元数据的新增报错，报错位置：MetaDataServiceImpl.insertHiveMetaData:报错信息"
                        + e.getMessage());
                e.printStackTrace();
                error = e.getMessage();
            }
            if (boo) {
                base.setReturnCode("1");
                base.setReturnMessage(success);
            } else {
                base.setReturnCode("0");
                base.setReturnMessage(error);
            }
        }
        if ("hbase".equalsIgnoreCase(hiveMetaData.getMetaDataType())) {
            boolean boo = false;
            try {
                boo = metaDataServiceImpl.insertHbaseMetaData(metadata, request);
                logUtils.writeLog("新建元数据hbase: " + metadata, LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            } catch (Exception e) {
                boo = false;
                logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
                logger.error("---------------hbase元数据的新增报错，报错位置：MetaDataServiceImpl.insertHbaseMetaData:报错信息"
                        + e.getMessage());
                e.printStackTrace();
                error = e.getMessage();
            }
            if (boo) {
                base.setReturnCode("1");
                base.setReturnMessage(success);
            } else {
                base.setReturnCode("0");
                base.setReturnMessage(error);
            }
        }
        return base.toString();
    }

    @RequestMapping("hiveDataTypeCheck")
    public @ResponseBody
    String hiveDataTypeCheck(@RequestBody String dataType, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(dataType)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求的参数为空！");
            return base.toString();
        }
        boolean boo = false;
        //
        dataType = JacksonUtil.getString(JacksonUtil.getJsonNode(dataType), "dataType");
        try {
            boo = metaDataServiceImpl.hiveDataTypeCheck(dataType);
        } catch (Exception e) {
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error("---------------hive数据类型校验报错，报错位置：MetaDataServiceImpl.hiveDataTypeCheck:报错信息"
                    + e.getMessage());
            e.printStackTrace();
        }
        if (boo) {
            base.setReturnCode("1");
            base.setReturnMessage("hive数据类型校验成功");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("hive数据类型校验失败");
        }
        return base.toString();
    }

    /**
     * hive元数据的更新
     *
     * @param metadata
     * @return
     */
    @RequestMapping("updateHiveMetaData")
    public @ResponseBody
    String updateHiveMetaData(@RequestBody String metadata, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(metadata)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求的参数为空！");
            return base.toString();
        }
        boolean boo = false;
        try {
            boo = metaDataServiceImpl.updateHiveMetaData(metadata, request);
            logUtils.writeLog("元数据更新hbase: " + metadata, LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());

        } catch (Exception e) {
            boo = false;
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error(
                    "--------------- hive元数据的更改报错，报错位置：MetaDataServiceImpl.updateHiveMetaData:报错信息" + e.getMessage());
            e.printStackTrace();
        }
        if (boo) {
            base.setReturnCode("1");
            base.setReturnMessage("hive表更新成功。");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("hive表更新失败。");
        }
        return base.toString();
    }

    /**
     * 查询所有的hive、hbase和hdfs的元数据信息
     *
     * @param request
     * @return
     */
    @RequestMapping("listHiveHDFSMetaData")
    public @ResponseBody
    String listHiveHDFSHbaseMetaData(@RequestBody String json, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        JSONObject object = JSON.parseObject(json);
        int startPage = object.getIntValue("startPage");
        int count = object.getIntValue("count");
        String searchType = object.getString("searchType");
        List<HiveMetaDesc> hiveHDFSMetaData = null;
        if (StringUtils.isNotBlank(searchType)) {
            try {
                hiveHDFSMetaData = metaDataServiceImpl.listHiveHDFSMetaDataByType(searchType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                hiveHDFSMetaData = metaDataServiceImpl.listHiveHDFSMetaData(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 需要传递到前台的数据，这里弄了一个伪分页
        List<HiveMetaDesc> hiveMetaDescs = new ArrayList<HiveMetaDesc>();
        if (hiveHDFSMetaData != null && hiveHDFSMetaData.size() > 0) {
            for (int i = (startPage - 1) * 10; i < startPage * count; i++) {
                hiveMetaDescs.add(hiveHDFSMetaData.get(i));
            }
        }
        if (hiveMetaDescs != null && hiveMetaDescs.size() > 0) {
            base.setReturnCode("1");
            base.setReturnMessage("查询成功。");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("hiveMetaDescs", hiveMetaDescs);
            map.put("countAll", hiveHDFSMetaData.size());
            base.setReturnObject(map);
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("查询失败或者没有查询到数据！");
        }
        return base.toString();
    }

    /**
     * 删除hbase/hive表
     *
     * @param request
     * @return
     */
    @RequestMapping("deleteMetaData")
    public @ResponseBody
    String deleteMetaData(@RequestBody List<MetaDataEntity> metaDataEntitys,
                          HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (!(metaDataEntitys != null && metaDataEntitys.size() > 0)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求参数为空！");
            return base.toString();
        }
        if (metaDataEntitys != null && metaDataEntitys.size() > 0) {
            List<String> hiveTableNames = new ArrayList<String>();
            List<String> hbaseTableNames = new ArrayList<String>();
            for (MetaDataEntity metaDataEntity : metaDataEntitys) {
                if ("hive".equalsIgnoreCase(metaDataEntity.getMetaType())) {
                    hiveTableNames.add(metaDataEntity.getTableName());
                }
                if ("hbase".equalsIgnoreCase(metaDataEntity.getMetaType())) {
                    hbaseTableNames.add(metaDataEntity.getTableName());
                }
            }
            boolean boo1 = false;
            boolean boo2 = false;
            try {
                if (hiveTableNames.size() > 0 && hbaseTableNames.size() > 0) {
                    boo1 = metaDataServiceImpl.deleteHbaseMetaData(metaDataEntitys, request);
                    boo2 = metaDataServiceImpl.deleteHiveMetaDataByTableName(metaDataEntitys, request);
                } else if (hiveTableNames.size() > 0) {
                    boo2 = metaDataServiceImpl.deleteHiveMetaDataByTableName(metaDataEntitys, request);
                    boo1 = true;
                } else if (hbaseTableNames.size() > 0) {
                    boo1 = metaDataServiceImpl.deleteHbaseMetaData(metaDataEntitys, request);
                    boo2 = true;
                }
            } catch (Exception e) {
                boo1 = false;
                boo2 = false;
                e.printStackTrace();
                logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
                logger.error("---------------元数据的删除报错，报错位置：MetaDataController.deleteMetaData:报错信息" + e.getMessage());
            } finally {
                logUtils.writeLog("删除hbase/hive表: " + JsonUtils.toJson(metaDataEntitys), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            }
            if (boo1 && boo2) {
                base.setReturnCode("1");
                base.setReturnMessage("元数据删除成功。");
            } else {
                base.setReturnCode("0");
                base.setReturnMessage("元数据删除失败！");
            }
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("元数据删除失败,传递的参数为空！");
        }
        return base.toString();
    }

    /**
     * hive/hdfs/hbase 元数据的查询-到本地库查询
     */
    @RequestMapping("listMetaData")
    public @ResponseBody
    String listHiveHDFSMetaData(@RequestBody String json, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(json)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求的参数为空！");
            return base.toString();
        }
        MetaDataEntity metaDataEntity = JsonUtils.toObject(json, MetaDataEntity.class);
        JSONObject object = JSON.parseObject(json);
        int startPage = object.getIntValue("startPage");
        int count = object.getIntValue("count");
        List<MetaDataEntity> metaDataEntitys = null;
        try {
            // 解决模糊查询时的sql注入问题
            if (metaDataEntity != null && StringUtils.isNotBlank(metaDataEntity.getTableName())) {
                String tableName = metaDataEntity.getTableName();
                tableName = StrUtils.escapeStr(tableName);
                metaDataEntity.setTableName(tableName);
            }
            metaDataEntitys = metaDataServiceImpl.listHiveHDFSMetaData(metaDataEntity, request, (startPage - 1) * 10,
                    count);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 获取所有的行数
        int countAll = 0;
        try {
            countAll = metaDataServiceImpl.listCountAll(metaDataEntity, request);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("metaDataEntitys", metaDataEntitys);
        map.put("countAll", countAll);
        base.setReturnCode("1");
        base.setReturnObject(map);
        return base.toString();
    }

    /**
     * hive/hdfs/hbase 元数据的删除-到本地库删除
     */
    @RequestMapping("deleteHiveHDFSMetaData")
    public @ResponseBody
    String deleteHiveHDFSMetaData(@RequestBody List<MetaDataEntity> metaDataEntitys,
                                  HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (!(metaDataEntitys != null && metaDataEntitys.size() > 0)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求参数为空！");
            return base.toString();
        }
        int i = 0;
        try {
            i = metaDataServiceImpl.deleteHiveHDFSMetaData(metaDataEntitys, request);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            logUtils.writeLog("元数据的删除-到本地库删除: " + JsonUtils.toJson(metaDataEntitys), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
        }
        if (i > 0) {
            base.setReturnCode("1");
            base.setReturnMessage("元数据删除成功。");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("元数据删除失败！");
        }
        return base.toString();
    }

    /**
     * hive/hdfs/hbase 元数据的修改-到本地库查询
     */
    @RequestMapping("updateHiveHDFSMetaData")
    public @ResponseBody
    String updateHiveHDFSMetaData(MetaDataEntity metaDataEntity, HttpServletRequest request,
                                  Model model) {
        BaseResponse base = new BaseResponse();
        if (metaDataEntity == null) {
            base.setReturnCode("0");
            base.setReturnMessage("请求参数为空！");
            return base.toString();
        }
        int i = 0;
        try {
            i = metaDataServiceImpl.updateHiveHDFSMetaData(metaDataEntity, request);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            logUtils.writeLog("元数据修改: " + metaDataEntity.getDbName(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
        }
        if (i > 0) {
            base.setReturnCode("1");
            base.setReturnMessage("元数据修改成功。");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("元数据修改失败！");
        }
        return base.toString();
    }

    /**
     * hive/hdfs/hbase 元数据的增加-增加到本地库
     */
    @RequestMapping("insertHiveHDFSMetaData")
    public @ResponseBody
    String insertHiveHDFSMetaData(@RequestBody MetaDataEntity metaDataEntity,
                                  HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (metaDataEntity == null) {
            base.setReturnCode("0");
            base.setReturnMessage("请求参数为空！");
            return base.toString();
        }
        int i = 0;
        try {
            i = metaDataServiceImpl.insertHiveHDFSMetaData(metaDataEntity, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (i > 0) {
            base.setReturnCode("1");
            base.setReturnMessage("元数据增加成功。");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("元数据增加失败！");
        }
        return base.toString();
    }

    /**
     * 判断hive和hbase表是否已经存在
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "getHiveHbase", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    String getHiveHbase(@RequestBody String json, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(json)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求的参数为空！");
            return base.toString();
        }
        MetaDataEntity metaDataEntity = new MetaDataEntity();
        JSONObject object = JSON.parseObject(json);
        String tableName = object.getString("tableName");
        metaDataEntity.setTableName(tableName);
        MetaDataEntity metaData = null;
        try {
            metaData = metaDataServiceImpl.getHiveHDFSMetaData(metaDataEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // List<MetaDataEntity> metaDataEntitys =
        // metaDataServiceImpl.listHiveHDFSMetaData(metaDataEntity, request, 0,
        // 10);
        if (metaData != null) {
            base.setReturnCode("1");
            base.setReturnMessage("该表已经存在！");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("该表不存在，可以使用。");
        }
        return base.toString();
    }

    /**
     * 根据元数据类型查询所有hive或hbase表
     */
    @RequestMapping("getHiveOrHbaseTableNames")
    public @ResponseBody
    String getHiveOrHbaseTableNames(@RequestBody String json) {
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(json)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求的参数为空！");
            return base.toString();
        }
        MetaDataEntity metaDataEntity = new MetaDataEntity();
        JSONObject object = JSON.parseObject(json);
        String metaDataType = object.getString("metaDataType");
        metaDataEntity.setMetaType(metaDataType);
        List<String> tableNames = null;
        try {
            tableNames = metaDataServiceImpl.getHiveOrHbaseTableNames(metaDataType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tableNames != null && tableNames.size() > 0) {
            base.setReturnCode("1");
            base.setReturnMessage("根据元数据类型查询hive或者hbase表成功！");
            base.setReturnObject(tableNames);
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("查询失败或者没有查询到数据！");
        }
        return base.toString();
    }

    /**
     * 根据库名从元数据表查询所有hive表
     */
    @RequestMapping("getDBHiveTables")
    public @ResponseBody
    String getDBHiveTables(@RequestBody String json) {
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(json)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求的参数为空！");
            return base.toString();
        }
        JSONObject object = JSON.parseObject(json);
        String dbName = object.getString("dbName");
        List<String> tableNames = null;
        try {
            tableNames = metaDataServiceImpl.getDBHiveTables(dbName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tableNames != null && tableNames.size() > 0) {
            base.setReturnCode("1");
            base.setReturnMessage("根据元数据类型查询hive表成功！");
            base.setReturnObject(tableNames);
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("查询失败或者没有查询到数据！");
        }
        return base.toString();
    }

}

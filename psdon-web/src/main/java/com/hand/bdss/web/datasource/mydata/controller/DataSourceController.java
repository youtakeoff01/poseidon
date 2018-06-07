package com.hand.bdss.web.datasource.mydata.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import com.hand.bdss.dsmp.component.hive.HiveClient;
import com.hand.bdss.dsmp.config.SystemConfig;
import com.hand.bdss.dsmp.model.DataSourceType;
import com.hand.bdss.dsmp.service.etl.ETLManager;
import com.hand.bdss.web.common.util.DataConnectionUtils;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.common.vo.BaseSyncVO;
import com.hand.bdss.web.common.vo.DBSrcVO;
import com.hand.bdss.web.common.vo.DataSourceVO;
import com.hand.bdss.web.datasource.mydata.service.DBSrcService;
import com.hand.bdss.web.datasource.mydata.service.DataSourceService;
import com.hand.bdss.web.entity.TableEtlDO;
import com.hand.bdss.web.operationcenter.task.service.TaskService;

@Controller
@RequestMapping(value = "/dataSourceController/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DataSourceController {
    @Resource(name = "dataSourceServiceImpl")
    private DataSourceService dataSourceServiceImpl;

    @Resource
    private DBSrcService dBSrcServiceImpl;

    @Resource
    private TaskService taskServiceImpl;

    @Resource
    LogUtils logUtils;

    private static final Logger logger = LoggerFactory.getLogger(DataSourceController.class);

    @RequestMapping("getFlumeData")
    public @ResponseBody
    String getFlumeData() throws Exception {
        BaseResponse base = new BaseResponse();
        ETLManager etlManager = new ETLManager();
        String conf = etlManager.getFlumeConfig().replace("\\\\", "\\");
        Map<String, String> map = new HashMap<String, String>();
        Matcher matcher = Pattern.compile(":").matcher(conf);
        int index = 0;
        if (matcher.find()) {
            index = matcher.start();
        }
        String content = conf.substring(index + 2, conf.length() - 2);
        map.put("content", content);
        base.setReturnCode("1");
        base.setReturnMessage("获取flume配置文件成功!");
        base.setReturnObject(map);
        return base.toString();
    }

    @RequestMapping("updateFlumeData")
    public @ResponseBody
    String updateFlumeData(@RequestBody String strJson, HttpServletRequest request) throws Exception {
        BaseResponse base = new BaseResponse();
        ETLManager etlManager = new ETLManager();
        Matcher matcher = Pattern.compile(":").matcher(strJson);
        int index = 0;
        if (matcher.find()) {
            index = matcher.start();
        }
        String str = strJson.substring(index + 2, strJson.length() - 2);
        etlManager.updateConfig(str);
        base.setReturnCode("1");
        base.setReturnMessage("更新flume配置文件成功!");
        return base.toString();
    }

    @RequestMapping("executeFlumeData")
    public @ResponseBody
    String executeFlumeData() throws Exception {
        BaseResponse base = new BaseResponse();
        ETLManager etlManager = new ETLManager();
        etlManager.executeService();
        base.setReturnCode("1");
        base.setReturnMessage("执行flume配置文件成功!");
        return base.toString();
    }

    /**
     * etl job 的生成
     *
     * @param dataSourceVO
     * @param request
     * @return
     */
    @RequestMapping("insertData")
    @ResponseBody
    public String insertData(@RequestBody DataSourceVO dataSourceVO, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        try {
        	//如果选择分区，则校验选择的分区字段的字段类型，必须是时间类型
        	/*if("0".equals(dataSourceVO.getIs_partition()) && !checkFieldType(dataSourceVO)) {
        		base.setReturnCode("0");
        		base.setReturnMessage("选择的分区字段不是时间类型！请重新选择.");
        		return base.toString();
        	}*/
            dataSourceServiceImpl.insertDataSource(dataSourceVO, request);
            //当job执行成功后，会创建hive表，这时需要将创建的hive表添加到本地的生命周期管理当中。
//            this.insertMetaData(dataSourceVO, request);
            base.setReturnCode("1");
            base.setReturnMessage("插入数据源操作成功。");
        } catch (Exception e) {
            base.setReturnCode("0");
            base.setReturnMessage("插入数据源操作失败！");
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error("insertDataSource error ：", e);
        }
        return base.toString();
    }


	/**
     * 点击下一步数据库表对应hive表字段转换显示
     *
     * @return
     */
    @RequestMapping("nextAndCaseTableType")
    public @ResponseBody
    String nextAndCaseTableType(@RequestBody String json) {
        logger.info("nextAndCaseTableType{}", json);
        BaseResponse base = new BaseResponse();
        JSONObject object = JSON.parseObject(json);
        Map<String, String> map = (Map<String, String>) object.get("map");
//		int dbSourceId = object.getIntValue("dbSourceId");//数据源ID
//		DBSrcVO dbSrcVO = new DBSrcVO();
//		dbSrcVO.setId(dbSourceId);
        String srcType = object.getString("srcType");
        String tarType = object.getString("tarType");
        Map<String, String> maps;
        try {
            if ("hive".equalsIgnoreCase(srcType)) {
                maps = dataSourceServiceImpl.BDToDBCaseTableType(map, tarType);
            } else {
                maps = dataSourceServiceImpl.DBToBDCaseTableType(map, srcType);
            }
            base.setReturnCode("1");
            base.setReturnObject(maps);
        } catch (Exception e) {
            logger.error("nextAndCaseTableType：", e);
            base.setReturnCode("0");
        }
        return base.toString();
    }

    /**
     * 校验azkaban任务名称是否存在
     *
     * @param json
     * @param
     * @return
     */
    @RequestMapping(value = "checkAzkabanJobName", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    String getAzkabanJobName(@RequestBody String json) {
        logger.info("checkAzkabanJobName{}", json);
        BaseResponse base = new BaseResponse();
        TableEtlDO tableEtlDO = new TableEtlDO();
        JSONObject object = JSON.parseObject(json);
        String jobName = object.getString("jobName");
        tableEtlDO.setJobName(jobName);
        List<TableEtlDO> tableEtl = new ArrayList<>();
        boolean taskFlag;
        try {
            //校验tb_etl表中是否存在同步任务
            tableEtl = dataSourceServiceImpl.getAzkabanJobName(tableEtlDO);
            //校验tb_task表中是否存在同步任务
            taskFlag = taskServiceImpl.check(jobName);
            if (tableEtl.size() > 0 || taskFlag) {
                base.setReturnCode("1");
                base.setReturnMessage("该表已经存在！");
            } else {
                base.setReturnCode("0");
                base.setReturnMessage("该表不存在，可以使用。");
            }
        } catch (Exception e) {
            logger.error("checkAzkabanJobName error：", e);
            base.setReturnCode("1");
            base.setReturnMessage("该表校验失败！");
        }
        return base.toString();
    }

    /**
     * 获取数据库连接状态
     *
     * @param dbSrcVO
     * @return
     * @throws SQLException
     */
    @RequestMapping("getConnectionStatus")
    public @ResponseBody
    String getConnectionStatus(@RequestBody DBSrcVO dbSrcVO, HttpServletRequest request) throws Exception {
        BaseResponse base = new BaseResponse();
        boolean boo = false;
        try {
            boo = DataConnectionUtils.getConnectionStatus(dbSrcVO);
        } catch (Exception e) {
            boo = false;
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error("---------------获取连接状态报错，报错位置：DataSourceController.getConnectionStatus:报错信息" + e.getMessage());
        }
        if (boo) {
            base.setReturnCode("1");
            base.setReturnMessage("获取数据库连接装填成功！");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("获取数据库连接装填失败！");
        }
        return base.toString();
    }

    /**
     * 获取连接数据库所有表名
     *
     * @param dbSrcVO
     * @return
     * @throws SQLException
     */
    @RequestMapping("getTablesName")
    public @ResponseBody
    String getTablesName(@RequestBody DBSrcVO dbSrcVO, HttpServletRequest request) throws Exception {
        BaseResponse base = new BaseResponse();
        List<String> dbSrcVOs;
        try {
            List<DBSrcVO> lists = dBSrcServiceImpl.listDBSrcs(dbSrcVO, 0, 10);
            if (lists == null || lists.size() == 0) {
                base.setReturnCode("0");
                base.setReturnMessage("获取连接数据库表名失败，选择的数据连接不存在。");
                return base.toString();
            }
            DBSrcVO dbSrc = lists.get(0);
            dbSrcVOs = DataConnectionUtils.getTablesName(dbSrc);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error("---------------获取表名报错，报错位置：DataSourceController.getTablesName:报错信息" + e.getMessage());
            e.printStackTrace();
            base.setReturnCode("0");
            base.setReturnMessage("获取连接数据库表名失败！");
            return base.toString();
        }
        base.setReturnCode("1");
        base.setReturnMessage("获取连接数据库表名成功！");
        base.setReturnObject(dbSrcVOs);
        return base.toString();
    }

    /**
     * 根据表名获取表字段以及字段类型
     *
     * @return
     * @throws SQLException
     */
    @RequestMapping("getTablesDescript")
    public @ResponseBody
    String getTablesDescript(@RequestBody String json, HttpServletRequest request) throws Exception {
        BaseResponse base = new BaseResponse();
        JSONObject object = JSON.parseObject(json);
        String tableName = object.getString("tableName");
        int id = object.getIntValue("id");
        DBSrcVO dbSrcVO = new DBSrcVO();
        dbSrcVO.setId(id);
        Map<String, String> map = null;
        try {
            List<DBSrcVO> lists = dBSrcServiceImpl.listDBSrcs(dbSrcVO, 0, 10);
            DBSrcVO dbSrc = lists.get(0);
            map = DataConnectionUtils.getTablesDescript(dbSrc, tableName);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error(
                    "---------------获取表字段以及字段类型报错，报错位置：DataSourceController.getTablesDescript:报错信息" + e.getMessage());
            e.printStackTrace();
            base.setReturnCode("0");
            base.setReturnMessage("根据表名获取字段以及字段类型失败!");
        }
        base.setReturnCode("1");
        base.setReturnMessage("根据表名获取字段以及字段类型成功!");
        base.setReturnObject(map);
        return base.toString();
    }

    /**
     * 获取Hive数据库
     *
     * @return
     */
    @RequestMapping("getHiveDbNames")
    public @ResponseBody
    String getHiveDbNames() {
        logger.info("DataSourceController.getHiveDbNames start");
        BaseResponse base = new BaseResponse();
        base.setReturnCode("0");
        try {
            HiveClient hiveClient = new HiveClient(SystemConfig.userName);
            List<String> list = hiveClient.getHiveDatabases();
            if (list != null && list.size() > 0) {
                base.setReturnCode("1");
                base.setReturnMessage("获取Hive数据库成功");
                base.setReturnObject(list);
            } else {
                base.setReturnMessage("获取数据库失败");
            }
        } catch (Exception e) {
            logger.error("----获取Hive数据库报错,报错位置：HiveClient.getHiveDatabases:报错信息" + e.getMessage());
            base.setReturnMessage("获取Hive数据库失败");
            return base.toString();
        }
        logger.info("DataSourceController.getHiveDbNames end");
        return base.toString();
    }

    /**
     * 验证Hive表是否在Hive数据库中存在
     *
     * @param json
     * @return
     */
    @RequestMapping("checkTable")
    public @ResponseBody
    String checkHiveTable(@RequestBody String json) {
        logger.info("checkTable json: {}", json);
        BaseResponse base = new BaseResponse();
        base.setReturnCode("0");
        JSONObject jsonObject = JSON.parseObject(json);
        String srcName = jsonObject.getString("srcName");
        String tableName = jsonObject.getString("tableName");
        try {
            boolean flag = dataSourceServiceImpl.checkTable(srcName, tableName);
            if (flag) {
                base.setReturnCode("1");
                base.setReturnMessage("当前表已经存在");
            } else {
                base.setReturnMessage("当前表不存在");
            }
        } catch (Exception e) {
            logger.error("checkTable", e);
            base.setReturnMessage("验证Hive数据表失败");
        }
        logger.info("DataSourceController.checkTable end");
        return base.toString();
    }

    /**
     * 获取数据源类型
     *
     * @param dbSrcVO
     * @return
     */
    @RequestMapping("getDbSrcType")
    public @ResponseBody
    String getDbSrcType(@RequestBody DBSrcVO dbSrcVO) {
        logger.info("DataSourceController.getDbSrcType start!");
        BaseResponse base = new BaseResponse();
        base.setReturnCode("0");
        try {
            List<DBSrcVO> list = dBSrcServiceImpl.listDBSrcs(dbSrcVO, 0, 10);
            if (list != null && list.size() > 0) {
                DBSrcVO dbSrc = list.get(0);
                String dbType = dbSrc.getDbType();
                base.setReturnCode("1");
                base.setReturnObject(dbType);
                base.setReturnMessage("获取数据源类型成功");
            } else {
                logger.info("dbSrcType not exsit");
                base.setReturnMessage("获取数据源类型失败");
            }
        } catch (Exception e) {
            logger.error("DataSourceController.getDbSrcType error!", e);
            base.setReturnMessage("获取数据源类型失败");
        }
        logger.info("DataSourceController.getDbSrcType end!");
        return base.toString();
    }

    /**
     * 查看库同步任务是否已经存在
     * @param syncTaskName
     * @return
     */
//	@RequestMapping("checkSyncTaskName")
//	public @ResponseBody String checkSyncTaskName(@RequestBody String syncTaskName) {
//		BaseResponse base = new BaseResponse();
//		if(dataSourceServiceImpl.checkSyncTaskName(syncTaskName)) {
//			base.setReturnCode("0");
//			base.setReturnMessage("库同步任务名称已存在！");
//		}else {
//			base.setReturnCode("1");
//		}
//		return base.toString();
//	}


    /**
     * 库同步接口
     *
     * @param baseVO
     * @return
     */
    @RequestMapping("baseSyncFunc")
    public @ResponseBody
    String baseSyncFunc(@RequestBody @Valid BaseSyncVO baseVO, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        baseVO.getTables().forEach(tableName -> {
            try {
                //根据来源表的名称以及库名，查询表对应的字段以及字段的类型
                Map<String, String> map = dataSourceServiceImpl.getFieldsMsg(tableName, baseVO.getResourceBaseId());
                //获取来源表对应的目标表名
                String hiveTableName = dataSourceServiceImpl.getHiveTableName(baseVO.getTargetBaseName(), tableName);
                //获取表的同步任务名称(azkaban)
                String tableSyncTaskName = dataSourceServiceImpl.getTableSyncTaskName(hiveTableName);
                //调用azkaban接口创建任务并将任务插入到表任务列表中
                DataSourceVO dataSourceVO = new DataSourceVO();
                dataSourceVO.setJobType("库同步任务");
                dataSourceVO.setJobName(tableSyncTaskName);
                dataSourceVO.setDataType("JDBC");
                dataSourceVO.setDataSourceId(Integer.parseInt(baseVO.getResourceBaseId()));
                dataSourceVO.setDataTable(tableName);
                dataSourceVO.setSyncDB(baseVO.getTargetBaseName());
                dataSourceVO.setSyncSource(hiveTableName);
                dataSourceVO.setUdc("1=1");
                dataSourceVO.setIs_partition("1");
                dataSourceVO.setDataFilter(map);
                dataSourceServiceImpl.insertDataSource(dataSourceVO, request);

                //当job执行成功后，会创建hive表，这时需要将创建的hive表添加到本地的生命周期管理当中
                dataSourceServiceImpl.insertMetaData(dataSourceVO, request);
                base.setReturnCode("1");
            } catch (Exception e) {
                logger.error("baseSyncFunc error,error massage:{}", e.getMessage());
                base.setReturnCode("0");
                base.setReturnMessage("创建库任务失败");
            }
        });
        return base.toString();
    }
    
    /*private boolean checkFieldType(DataSourceVO dataSourceVO) {
		boolean boo = false;
		if(DataSourceType.ORACLE.toString().equalsIgnoreCase(dataSourceVO.getDataType())) {
			if("DATE".equalsIgnoreCase(dataSourceVO.getPartitionType()) || "TIMESTAMP".equalsIgnoreCase(dataSourceVO.getPartitionType())) {
				boo = true;
			}
		}
		if(DataSourceType.MYSQL.toString().equalsIgnoreCase(dataSourceVO.getDataType())) {
			if("DATE".equalsIgnoreCase(dataSourceVO.getPartitionType()) || "DATETIME".equalsIgnoreCase(dataSourceVO.getPartitionType()) || "TIMESTAMP".equalsIgnoreCase(dataSourceVO.getPartitionType())) {
				boo = true;
			}
		}
		if(DataSourceType.SQLSERVER.toString().equalsIgnoreCase(dataSourceVO.getDataType())) {
			if("DATE".equalsIgnoreCase(dataSourceVO.getPartitionType()) || "DATETIME".equalsIgnoreCase(dataSourceVO.getPartitionType()) || "TIMESTAMP".equalsIgnoreCase(dataSourceVO.getPartitionType()) || "DATETIME2".equalsIgnoreCase(dataSourceVO.getPartitionType())) {
				boo = true;
			}
		}
		if(DataSourceType.DB2.toString().equalsIgnoreCase(dataSourceVO.getDataType()) || DataSourceType.POSTGRESQL.toString().equalsIgnoreCase(dataSourceVO.getDataType()) || DataSourceType.HIVE.toString().equalsIgnoreCase(dataSourceVO.getDataType())){
			boo = true;
		}
		return boo;
	}*/

}

package com.hand.bdss.web.datamanage.datalineage.controller;

import com.hand.bdss.dev.util.JacksonUtil;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.datamanage.datalineage.service.DataLineageService;
import com.hand.bdss.web.datamanage.datalineage.vo.Column;
import com.hand.bdss.web.dataprocessing.processing.service.DataQueryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * hive 元数据的操作controller
 *
 * @author dingshuang
 */
@Controller
@RequestMapping(value = "/lineage/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DataLineageController {
    @Resource
    private DataLineageService dataLineageServiceImpl;

    @Resource
    DataQueryService dataQueryServiceImpl;

    private static final Logger logger = LoggerFactory.getLogger(DataLineageController.class);

    /**
     * 获取 table schema
     *
     * @param json
     * @return
     */
    @RequestMapping("getSchema")
    public @ResponseBody
    String getLineageSchema(@RequestBody String json) {
        logger.info("getLineageSchema param: {}", json);
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(json)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求的参数为空！");
            return base.toString();
        }
        String guid = JacksonUtil.getJsonNode(json).get("guid").asText();

        try {
            List<Column> colums = dataLineageServiceImpl.getEntitySchema(guid);
            base.setReturnCode("1");
            base.setReturnMessage("getLineageSchema success！");
            base.setReturnObject(colums);
        } catch (Exception e) {
            base.setReturnCode("0");
            base.setReturnMessage("getLineageSchema error！");
            logger.info("getLineageSchema error", e);
        }
        return JacksonUtil.beanToJson(base);
    }

    /**
     * 获取 table schema
     *
     * @param json
     * @return
     */
    @RequestMapping("getTabDetails")
    public @ResponseBody
    String getTabDetails(@RequestBody String json) {
        logger.info("getTabDetails param: {}", json);
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(json)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求的参数为空！");
            return base.toString();
        }
        String guid = JacksonUtil.getJsonNode(json).get("guid").asText();

        try {
            Map<String, String> retMap = dataLineageServiceImpl.getTabDetails(guid);
            base.setReturnCode("1");
            base.setReturnMessage("getTabDetails success！");
            base.setReturnObject(retMap);
        } catch (Exception e) {
            base.setReturnCode("0");
            base.setReturnMessage("getTabDetails error！");
            logger.info("getTabDetails error", e);
        }
        return JacksonUtil.beanToJson(base);
    }

    /**
     * 获取 all Lineage
     * 血缘
     *
     * @param json
     * @return
     */
    @RequestMapping("query")
    public @ResponseBody
    String getLineageQuery(@RequestBody String json) {
        logger.info("getLineageQuery param: {}", json);
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(json)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求的参数为空！");
            return base.toString();
        }
        String guid = JacksonUtil.getJsonNode(json).get("guid").asText();

        try {
            Map<String, Object> retMap = dataLineageServiceImpl.getAtlasLineage(guid);
            base.setReturnCode("1");
            base.setReturnMessage("getLineageQuery success！");
            base.setReturnObject(retMap);
        } catch (Exception e) {
            base.setReturnCode("0");
            base.setReturnMessage("getLineageQuery error！");
            logger.info("getLineageQuery error", e);
        }
        return JacksonUtil.beanToJson(base);
    }

    /**
     * 获取 上游血缘 Lineage
     * 血缘
     *
     * @param json
     * @return
     */
    @RequestMapping("qry-inputs")
    public @ResponseBody
    String getLineageInputs(@RequestBody String json) {
        logger.info("getLineageInputs param: {}", json);
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(json)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求的参数为空！");
            return base.toString();
        }
        String guid = JacksonUtil.getJsonNode(json).get("guid").asText();

        try {
            Map<String, Object> retMap = dataLineageServiceImpl.getAtlasLineageIn(guid);
            base.setReturnCode("1");
            base.setReturnMessage("getLineageInputs success！");
            base.setReturnObject(retMap);
        } catch (Exception e) {
            base.setReturnCode("0");
            base.setReturnMessage("getLineageInputs error！");
            logger.info("getLineageInputs error", e);
        }
        return JacksonUtil.beanToJson(base);
    }

    /**
     * 获取 上游血缘 Lineage
     * 血缘
     *
     * @param json
     * @return
     */
    @RequestMapping("qry-output")
    public @ResponseBody
    String getLineageOutput(@RequestBody String json) {
        logger.info("getLineageOutput param: {}", json);
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(json)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求的参数为空！");
            return base.toString();
        }
        String guid = JacksonUtil.getJsonNode(json).get("guid").asText();

        try {
            Map<String, Object> retMap = dataLineageServiceImpl.getAtlasLineageOut(guid);
            base.setReturnCode("1");
            base.setReturnMessage("getLineageOutput success！");
            base.setReturnObject(retMap);
        } catch (Exception e) {
            base.setReturnCode("0");
            base.setReturnMessage("getLineageOutput error！");
            logger.info("getLineageOutput error", e);
        }
        return JacksonUtil.beanToJson(base);
    }

    /**
     * 获取 guid
     *
     * @param json
     * @return
     */
    @RequestMapping("getGuid")
    public @ResponseBody
    String getGuid(@RequestBody String json) {
        logger.info("getGuid param: {}", json);
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(json)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求的参数为空！");
            return base.toString();
        }
        String dbName = JacksonUtil.getJsonNode(json).get("dbName").asText();
        String tbName = JacksonUtil.getJsonNode(json).get("tbName").asText();

        try {
            Map<String, Object> retMap = new HashMap<>();
            String guid = dataLineageServiceImpl.getAtlasGuid(dbName, tbName);
            retMap.put("guid", guid);
            base.setReturnCode("1");
            base.setReturnMessage("getGuid success！");
            base.setReturnObject(retMap);
        } catch (Exception e) {
            base.setReturnCode("0");
            base.setReturnMessage("getGuid error！");
            logger.info("getGuid error", e);
        }
        return JacksonUtil.beanToJson(base);
    }

    /**
     * 获取 table schema
     *
     * @param json
     * @return
     */
    @RequestMapping("getPreview")
    public @ResponseBody
    String getPreview(@RequestBody String json) {
        logger.info("getPreview param: {}", json);
        BaseResponse base = new BaseResponse();
        if (!StringUtils.isNotBlank(json)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求的参数为空！");
            return base.toString();
        }
        String dbName = JacksonUtil.getJsonNode(json).get("dbName").asText();
        String tbName = JacksonUtil.getJsonNode(json).get("tbName").asText();

        try {
            //默认50条
            Map<String, Object> retMap = dataQueryServiceImpl.getSparkQuery(dbName, tbName, 50);
            base.setReturnCode("1");
            base.setReturnMessage("getPreview success！");
            base.setReturnObject(retMap);
        } catch (Exception e) {
            base.setReturnCode("0");
            base.setReturnMessage("getPreview error！");
            logger.info("getPreview error", e);
        }
        return JacksonUtil.beanToJson(base);
    }
}

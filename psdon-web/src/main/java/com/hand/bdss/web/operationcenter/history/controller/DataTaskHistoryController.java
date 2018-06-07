package com.hand.bdss.web.operationcenter.history.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.hand.bdss.dev.util.JacksonUtil;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.datamanage.metadata.controller.MetaDataController;
import com.hand.bdss.web.entity.DataSyncHistoryEntity;
import com.hand.bdss.web.operationcenter.history.service.DataTaskHistoryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取数据同步历史的controller
 *
 * @author Administrator
 */
@Controller
@RequestMapping(value = "/dataTaskHistory/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DataTaskHistoryController {
    @Resource
    private DataTaskHistoryService dataTaskHistoryServiceImpl;

    @Resource
    private LogUtils logUtils;

    private static final Logger logger = LoggerFactory.getLogger(MetaDataController.class);

    /**
     * 查询执行任务历史记录
     *
     * @param param
     * @param request
     * @return
     */
    @RequestMapping("selectList")
    @ResponseBody
    public String selectList(@RequestBody String param, HttpServletRequest request) {
        logger.info("dataTaskHistory.selectList  param =" + param);
        BaseResponse base = new BaseResponse();
        base.setReturnCode("0");
        int count = 0;
        List<DataSyncHistoryEntity> list = null;
        if (StringUtils.isBlank(param)) {
            base.setReturnMessage("请求的参数为空！");
            return base.toString();
        }
        try {
            Map<String, Object> parmMap = JacksonUtil.convertToMaps(param);
            list = dataTaskHistoryServiceImpl.selectList(parmMap);
            count = dataTaskHistoryServiceImpl.selectCount(parmMap);
            Map<String, Object> map = new HashMap<>();
            map.put("list", list);
            map.put("count", count);
            base.setReturnCode("1");
            base.setReturnObject(map);
        } catch (Exception e) {
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error("dataTaskHistory.selectList error", e);
            base.setReturnMessage("查询失败！");
        }
        return base.toString();
    }

    /**
     * getLogDetail
     * 获取日志成功详情
     * @param
     * @return
     */
    @RequestMapping("logDetail")
    @ResponseBody
    public String getLogDetail(@RequestBody String json) {
        logger.info("TaskController executeTask param =" + json);
        BaseResponse base = new BaseResponse();
        base.setReturnCode("0");
        try {
            if (StringUtils.isBlank(json)) {
                base.setReturnMessage("参数为空！");
                return base.toString();
            }
            JsonNode jsonNode = JacksonUtil.getJsonNode(json);
            //azkaban中job_id即：jobName
            String execJobId = jsonNode.get("execJobName").asText();
            String execId = jsonNode.get("execId").asText();

            Map<String, Object> retMap = dataTaskHistoryServiceImpl.getLogDetail(execJobId, execId);
            return JacksonUtil.beanToJson(retMap);
        } catch (Exception e) {
            logger.error("getLogDetail error!", e);
            base.setReturnMessage("获取日志失败！");
        }
        return base.toString();
    }
}

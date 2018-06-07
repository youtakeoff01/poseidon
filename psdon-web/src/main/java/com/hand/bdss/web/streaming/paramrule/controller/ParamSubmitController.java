package com.hand.bdss.web.streaming.paramrule.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.web.common.util.GroovyCheckError;
import com.hand.bdss.web.common.util.GroovyScriptUtil;
import com.hand.bdss.web.common.util.StringUtils;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.entity.ScriptRuleEntity;
import com.hand.bdss.web.streaming.paramrule.service.ScriptcurdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hand on 2017/12/5.
 *
 */

@RestController
@RequestMapping(value = "/paramSubmitController/",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ParamSubmitController {
    private static final Logger logger = LoggerFactory.getLogger(ParamSubmitController.class);
    @Resource
    private ScriptcurdService scriptcurdServiceImpl;
    @RequestMapping(value = "insertScript", method = RequestMethod.POST)
    public String paramSubmit(@RequestBody ScriptRuleEntity scriptRuleEntity, HttpServletRequest request){

        BaseResponse baseResponse = new BaseResponse();

        String strJson = scriptRuleEntity.getScriptContent();
        if (StringUtils.isBlank(strJson)){
            baseResponse.setReturnCode("0");
            baseResponse.setReturnMessage("params is null");
        }else {
            try {
                boolean istrue = GroovyScriptUtil.test(scriptRuleEntity.getScriptTitle(), new StringBuffer(strJson), null);
                if (istrue){
                    int i = scriptcurdServiceImpl.insertScript(request, scriptRuleEntity);
                    if (1==i){
                        baseResponse.setReturnCode("1");
                        baseResponse.setReturnMessage("script save success");
                    }else {
                        baseResponse.setReturnCode("0");
                        baseResponse.setReturnMessage("script save fail");
                    }
                }
            }catch (GroovyCheckError groovyCheckError) {
                groovyCheckError.printStackTrace();
                logger.error("script check error {}",groovyCheckError);
                baseResponse.setReturnCode("0");
                baseResponse.setReturnMessage("script is error");
            }catch (Exception e) {
                logger.error("save script rule error ：{}",e);
                baseResponse.setReturnCode("0");
                baseResponse.setReturnMessage("服务器开了个小差，请稍后重试!");
            }

        }
        return baseResponse.toString();
    }
    @RequestMapping(value = "listScriptRuleInfo",method = RequestMethod.POST)
    public String listScriptRuleInfo(@RequestBody String json,HttpServletRequest request){
        BaseResponse baseResponse = new BaseResponse();
        JSONObject object = JSON.parseObject(json);
        int startPage = object.getIntValue("startPage");
        int count = object.getIntValue("count");
        List<ScriptRuleEntity> scriptRuleEntities = null;
        ScriptRuleEntity scriptRuleEntity = JSON.parseObject(json, ScriptRuleEntity.class);
        try {
            scriptRuleEntities = scriptcurdServiceImpl.listScriptRuleInfo(scriptRuleEntity,(startPage - 1) * 10,count);
            baseResponse.setReturnCode("1");
        }catch (Exception e ){
            logger.error("get script list error {}" , e);
            baseResponse.setReturnCode("0");
            baseResponse.setReturnMessage("get script list error");
        }
        int countAll = 0;
        try {
            countAll = scriptcurdServiceImpl.listCountScriptInfo(scriptRuleEntity);
        }catch (Exception e ){
            logger.error("get script list count error {}" , e);
            baseResponse.setReturnCode("0");
            baseResponse.setReturnMessage("get  list count error");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("lists",scriptRuleEntities);
        map.put("countAll", countAll);
        baseResponse.setReturnObject(map);
        return baseResponse.toString();
    }
    @RequestMapping(value = "updateScriptInfo",method = RequestMethod.POST)
    public String updateScriptInfo(@RequestBody ScriptRuleEntity scriptRuleEntity,HttpServletRequest request){
        BaseResponse baseResponse = new BaseResponse();
        String strJson = scriptRuleEntity.getScriptContent();
        boolean istrue = false;
        try {
            istrue = GroovyScriptUtil.test(scriptRuleEntity.getScriptTitle(), new StringBuffer(strJson), null);
            if (istrue){
                int i = scriptcurdServiceImpl.updateScript(request, scriptRuleEntity);
                if (1==i){
                    baseResponse.setReturnCode("1");
                    baseResponse.setReturnMessage("script update success");
                }else {
                    baseResponse.setReturnCode("0");
                    baseResponse.setReturnMessage("script update fail");
                }
            }
        } catch (GroovyCheckError groovyCheckError) {
            logger.error("groovy check error",groovyCheckError);
            baseResponse.setReturnCode("0");
            baseResponse.setReturnMessage("script is error,please check!!!");
        }catch (Exception e) {
            logger.error("update script rule error ：{}",e);
            baseResponse.setReturnCode("0");
            baseResponse.setReturnMessage("服务器开了个小差，请稍后重试!");
        }
        return baseResponse.toString();
    }

    @RequestMapping(value = "deleteScriptInfo",method = RequestMethod.POST)
    public String deleteScriptInfo(@RequestBody List<ScriptRuleEntity> scriptRuleEntities){
        BaseResponse baseResponse = new BaseResponse();
        try {
            int i = scriptcurdServiceImpl.deleteScript(scriptRuleEntities);
            baseResponse.setReturnCode("1");
            baseResponse.setReturnMessage("delete script success,total:"+i);
        } catch (Exception e) {
            logger.error("delete scriptInfo error",e);
            baseResponse.setReturnCode("0");
            baseResponse.setReturnMessage("服务器开了个小差，请稍后重试!");
        }
        return baseResponse.toString();
    }
}

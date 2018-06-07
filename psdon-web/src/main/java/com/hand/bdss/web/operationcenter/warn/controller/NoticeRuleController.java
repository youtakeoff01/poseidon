package com.hand.bdss.web.operationcenter.warn.controller;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.hand.bdss.web.operationcenter.warn.service.NoticeRuleService;
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
import com.hand.bdss.web.entity.NoticeRuleEntity;
import com.hand.bdss.web.common.util.GetUserUtils;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.common.util.LogUtils;
import com.hand.bdss.web.common.util.StrUtils;
import com.hand.bdss.web.common.vo.BaseResponse;

@Controller
@RequestMapping(value = "/noticeRuleController/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class NoticeRuleController {
    @Resource
    private NoticeRuleService noticeRuleServiceImpl;
    @Resource
    private LogUtils logUtils;

    private static final Logger logger = LoggerFactory.getLogger(NoticeRuleController.class);

    /**
     * 通知规则  新建
     *
     * @param
     * @return
     */
    @RequestMapping("insertNoticeRule")
    public @ResponseBody
    String insertNoticeRule(@RequestBody NoticeRuleEntity noticeruleEntity, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (noticeruleEntity == null) {
            base.setReturnCode("0");
            base.setReturnMessage("参数为空！");
            return base.toString();
        }
        int i = 0;
        try {
            if ("数据使用率".equalsIgnoreCase(noticeruleEntity.getTriggerRule())) {
                //将百分数转化为小数点。
                NumberFormat nf = NumberFormat.getPercentInstance();
                Number m = nf.parse(noticeruleEntity.getRuleNum());
                noticeruleEntity.setRuleNum(m.toString());
            }
            i = noticeRuleServiceImpl.insertNoticeRule(noticeruleEntity);
        } catch (Exception e) {
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error("--------------------------插入失败，报错位置：NoticeRuleController:insertNoticeRule：报错信息" + e.getMessage());
            e.printStackTrace();
            base.setReturnCode("0");
            base.setReturnMessage("数据插入失败！");
            return base.toString();
        } finally {
            logUtils.writeLog("新建通知规则: " + noticeruleEntity.getRuleNum(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
        }
        if (i > 0) {
            base.setReturnCode("1");
            base.setReturnMessage("数据插入成功！");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("数据插入失败！");
        }
        return base.toString();
    }

    /**
     * 通知规则  查询
     *
     * @param
     * @return
     */
    @RequestMapping("listNoticeRule")
    public @ResponseBody
    String listNoticeRule(@RequestBody String json, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        NoticeRuleEntity notice = JsonUtils.toObject(json, NoticeRuleEntity.class);
        JSONObject object = JSON.parseObject(json);
        int startPage = object.getIntValue("startPage");
        int count = object.getIntValue("count");
        List<NoticeRuleEntity> notices = null;
        int countAll = 0;
        try {
            //解决模糊查询时的sql注入问题
            if (notice != null && StringUtils.isNoneBlank(notice.getRuleName())) {
                String ruleName = notice.getRuleName();
                ruleName = StrUtils.escapeStr(ruleName);
                notice.setRuleName(ruleName);
            }
            notices = noticeRuleServiceImpl.listNoticeRules(notice, (startPage - 1) * 10, count);
            countAll = noticeRuleServiceImpl.listNoticeRulesCountAll(notice);
        } catch (Exception e) {
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error("---------------查询报错，报错位置：NoticeRuleController.listNoticeRule:报错信息" + e.getMessage());
            e.printStackTrace();
            base.setReturnCode("0");
            base.setReturnMessage("查询操作失败！");
            return base.toString();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("countAll", countAll);
        map.put("notices", notices);
        base.setReturnCode("1");
        base.setReturnMessage("查询操作成功！");
        base.setReturnObject(map);
        return base.toString();
    }

    /**
     * 验证自定义通知--规则名称是否存在
     *
     * @param noticeruleEntity
     * @param request
     * @return
     */
    @RequestMapping("listNoticeRuleAll")
    public @ResponseBody
    String listNoticeRuleAll(@RequestBody NoticeRuleEntity noticeruleEntity, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (noticeruleEntity == null) {
            base.setReturnCode("0");
            base.setReturnMessage("传入参数为空！");
        }
        List<NoticeRuleEntity> noticeruleEntitys = null;
        try {
            noticeruleEntitys = noticeRuleServiceImpl.listNoticeRuleAll(noticeruleEntity);
        } catch (Exception e) {
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error("--------------------------查询失败，报错位置：NoticeRuleController.listNoticeRuleAll：报错信息" + e.getMessage());
            e.printStackTrace();
            base.setReturnCode("0");
            base.setReturnMessage("验证通知失败");
            return base.toString();
        }
        if (noticeruleEntitys != null && noticeruleEntitys.size() > 0) {
            base.setReturnCode("1");
            base.setReturnMessage("规则名称已存在");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("规则名称不存在！");
        }
        return base.toString();
    }

    /**
     * 通知规则  更新
     *
     * @param
     * @return
     */
    @RequestMapping("updateNoticeRule")
    public @ResponseBody
    String updateNoticeRule(@RequestBody NoticeRuleEntity noticeruleEntity, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (noticeruleEntity == null) {
            base.setReturnCode("0");
            base.setReturnMessage("传入参数为空！");
        }
        int i = 0;
        try {
            if ("数据使用率".equalsIgnoreCase(noticeruleEntity.getTriggerRule())) {
                //将百分数转化为小数点。
                NumberFormat nf = NumberFormat.getPercentInstance();
                Number m = nf.parse(noticeruleEntity.getRuleNum());
                noticeruleEntity.setRuleNum(m.toString());
            }
            i = noticeRuleServiceImpl.updateNoticeRule(noticeruleEntity);
        } catch (Exception e) {
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error("--------------------------查询失败，报错位置：NoticeRuleController.updateNoticeRule：报错信息" + e.getMessage());
            e.printStackTrace();
            base.setReturnCode("0");
            base.setReturnMessage("更新数据失败");
            return base.toString();
        }
        if (i > 0) {
            base.setReturnCode("1");
            base.setReturnMessage("更新数据成功!");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("更新数据失败");
        }
        return base.toString();
    }

    /**
     * 通知规则  删除
     *
     * @param
     * @return
     */
    @RequestMapping("deleteNoticeRule")
    public @ResponseBody
    String deleteNoticeRule(@RequestBody List<NoticeRuleEntity> noticeruleEntity, HttpServletRequest request) {
        BaseResponse base = new BaseResponse();
        if (!((noticeruleEntity != null) && noticeruleEntity.size() > 0)) {
            base.setReturnCode("0");
            base.setReturnMessage("请求参数为空！");
            return base.toString();
        }
        int i = 0;
        try {
            i = noticeRuleServiceImpl.deleteNoticeRule(noticeruleEntity, request);
        } catch (Exception e) {
            logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
            logger.error("--------------------------删除失败，报错位置：NoticeRuleController.deleteNoticeRule：报错信息" + e.getMessage());
            e.printStackTrace();
            base.setReturnCode("0");
            base.setReturnMessage("删除数据失败");
            return base.toString();
        }
        if (i > 0) {
            base.setReturnCode("1");
            base.setReturnMessage("删除数据成功!");
        } else {
            base.setReturnCode("0");
            base.setReturnMessage("删除数据失败");
        }
        return base.toString();
    }
}

package com.hand.bdss.web.platform.management.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hand.bdss.web.common.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.dsmp.component.autoconfig.GetConfig;
import com.hand.bdss.dsmp.metrics.DataServiceMetrics;
import com.hand.bdss.web.platform.management.service.AutoConfigService;
import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.common.vo.ConfigInfo;

/**
 * xml 通过界面的配置config
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = "/autoConfigController/",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class AutoConfigController {
	private static final Logger logger = LoggerFactory.getLogger(AutoConfigController.class);
	@Resource
	private AutoConfigService autoConfigServiceImpl;
	
	@Resource 
	private LogUtils logUtils;
	
	@RequestMapping("ambariProp")
	public @ResponseBody String reload(@RequestBody String json, HttpServletRequest request){
		BaseResponse base = new BaseResponse();
		if(json == null){
			base.setReturnCode("0");
			base.setReturnMessage("传入的参数为空");
		}
		JSONObject object = JSON.parseObject(json);
		Map<String,String> map = new HashMap<String,String>();
		map.put("ambari_uri", object.getString("ambari_uri"));
		map.put("ambari_user", object.getString("ambari_user"));
		map.put("ambari_passwd", object.getString("ambari_passwd"));
		map.put("cluster_name", new DataServiceMetrics().getClusterName());
		try {
			new PropertiesOperationUtils().writeAmbariData(map);
			base.setReturnCode("1");
			base.setReturnMessage("ambari配置成功。");
			
		} catch (Exception e1) {
			e1.printStackTrace();
			base.setReturnCode("0");
			base.setReturnMessage("ambari配置失败！");
		} finally {
			logUtils.writeLog("ambariProp: " + json, LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		return base.toString();
	}
	
	
	/**
	 * 增加配置文件
	 * @param configs
	 * @param request
	 * @return
	 */
	@RequestMapping("addToolsConfigs")
	public @ResponseBody String addToolsConfigs(@RequestBody List<ConfigInfo> configs,HttpServletRequest request){
		BaseResponse base = new BaseResponse();
		if(configs == null || configs.size() <= 0){
			base.setReturnCode("0");
			base.setReturnMessage("请求参数为空！");
			return base.toString();
		}
		int i = 0 ;
		try {
			i = autoConfigServiceImpl.addConfig(configs);
		} catch (Exception e) {
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("------------------新增配置文件出错，报错位置：AutoConfigController.addToolsConfigs:报错信息" + e.getMessage());
			e.printStackTrace();
		} finally {
			logUtils.writeLog("ambariProp: " + JsonUtils.toJson(configs), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
		}
		if(i>0){
			base.setReturnCode("1");
			base.setReturnMessage("新增配置文件成功");
		}else{
			base.setReturnCode("0");
			base.setReturnMessage("新增配置文件失败");
		}
		return base.toString();
	}
	
	/**
	 * 增加配置文件
	 * @param
	 * @param request
	 * @return
	 */
	@RequestMapping("addConfigs")
	public @ResponseBody String addConfigs(HttpServletRequest request){
		BaseResponse base = new BaseResponse();
		int i = 0 ;
		try {
			//调用core层接口获取configs对象
			List<ConfigInfo> configs = new ArrayList<ConfigInfo>();
			String[] types = new String[]{"core-site","hdfs-site","hbase-site","hive-site","yarn-site"};
			int m = 1001;
			for (String type : types) {
				String str = new GetConfig().getConfig(type);
				ConfigInfo config = new ConfigInfo();
				config.setCode("xml_"+m);
				config.setConfigType("xml");
				config.setConfigInfo(str);
				config.setXmlName(type);
				configs.add(config);
				m++;
			}
			//把从服务器中获取到的配置文件保存到数据库中并且同步到redis中
			i = autoConfigServiceImpl.addConfig(configs);
			logUtils.writeLog("ambariProp: " + JsonUtils.toJson(configs), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());

		} catch (Exception e) {
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("------------------新增配置文件出错，报错位置：AutoConfigController.addConfigs:报错信息" + e.getMessage());
			e.printStackTrace();
		}
		if(i>0){
			base.setReturnCode("1");
			base.setReturnMessage("新增配置文件成功");
		}else{
			base.setReturnCode("0");
			base.setReturnMessage("新增配置文件失败");
		}
		return base.toString();
	}
	
	
	
	/**
	 * 查询配置文件
	 * @param
	 * @param request
	 * @return
	 */
	@RequestMapping("listConfigs")
	public @ResponseBody String listConfigs(HttpServletRequest request){
		BaseResponse base = new BaseResponse();
		List<ConfigInfo> configs = null;
		ConfigInfo config = new ConfigInfo();
		config.setConfigType("string");
		try {
			configs = autoConfigServiceImpl.listConfigs(config,0,50);
		} catch (Exception e) {
			base.setReturnCode("0");
			base.setReturnMessage("所有配置文件信息查询操作失败！");
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("--------------------查询所有配置文件信息失败，报错位置AutoConfigController.listConfigs：报错信息"+e.getMessage());
			e.printStackTrace();
		}
		if(configs==null || configs.size()<=0){
			base.setReturnCode("0");
			base.setReturnMessage("没有查询到数据！");
		}else{
			base.setReturnCode("1");
			base.setReturnObject(configs);
			base.setReturnMessage("所有配置文件信息查询操作成功！");
		}
		return base.toString();
	}
	
	@RequestMapping("downLoadConfigs")
	public @ResponseBody String downLoadConfigs(HttpServletResponse response,HttpServletRequest request){
		BaseResponse resp = new BaseResponse();
		String fileName = "systemConfig";
		response.setContentType("application/x-msdownload"); 
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName); 
		FileInputStream in = null;
		OutputStream out = null;
		try {
			ConfigInfo config = new ConfigInfo();
//			config.setConfigType("xml");
			//查询出所有的xml 类型的配置文件。
//			List<ConfigInfo> xmlConfigs = autoConfigServiceImpl.listConfigs(config,0,50);
//			if(xmlConfigs!=null && xmlConfigs.size()>0){
//				for (ConfigInfo configInfo : xmlConfigs) {
//					//将读出来的数据导出到对应的xml文件中
//					ConfigIOUtils.writeConfig(configInfo.getXmlName(), configInfo.getConfigInfo());
//				}
//			}
			//查询出所有string类型的配置，最终写到一个 .properties文件中
			config.setConfigType("string");
			List<ConfigInfo> strConfigs = autoConfigServiceImpl.listConfigs(config,0,50);
			if(strConfigs!=null && strConfigs.size()>0){
				//将读出来的数据导出到一个.properties文件中
				ConfigIOUtils.writeConfig(fileName, strConfigs);
			}
			
			//将所有的文件压缩成一个zip包
//			ConfigIOUtils.fileToZip(zipFileName);
			//获取流
			in = new FileInputStream(ConfigIOUtils.FILEPATH + "/" + fileName + ".properties");
			
			out = response.getOutputStream();
			byte[] buf = new byte[1024];
			int len = 0;
			while((len = in.read(buf)) >0)
			out.write(buf,0,len);
			resp.setReturnCode("1");
			resp.setReturnCode("下载文件成功。");
		} catch (Exception e) {
			logUtils.writeLog(e.getMessage(), LogUtils.LOGTYPE_SYS, GetUserUtils.getUser(request).getUserName());
			logger.error("--------------------下载文件失败，报错位置AutoConfigController.downLoadConfigs：报错信息"+e.getMessage());
			resp.setReturnCode("0");
			resp.setReturnMessage("下载文件出现问题，请稍后重试！");
			e.printStackTrace();
		}finally{
			try {
				if(in!=null)
				in.close();
				if(out!=null)
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resp.toString();
	}
}

package com.hand.bdss.web.dataprocessing.splunkapi.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.bdss.web.common.vo.BaseResponse;
import com.hand.bdss.web.dataprocessing.splunkapi.service.SplunkService;
import com.splunk.Job;
import com.splunk.JobResultsArgs;
import com.splunk.SSLSecurityProtocol;
import com.splunk.SavedSearch;
import com.splunk.SavedSearchDispatchArgs;
import com.splunk.Service;
import com.splunk.ServiceArgs;

@Controller
@RequestMapping(value = "/splunkController/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class SplunkController {
	
	@Resource
	public SplunkService splunkServiceImpl;
	
	
	
	@RequestMapping(value = "/getMsg", method = RequestMethod.POST)
	@ResponseBody
	public String getMsg() {
		splunkServiceImpl.getPCLogFromSplunkByTimer();
		return "success";
	}
	
	@RequestMapping(value = "/getMsgForSplunk", method = RequestMethod.POST)
	@ResponseBody
	public String getMsgForSplunk() {
		BaseResponse base = new BaseResponse();
		ServiceArgs loginArgs = new ServiceArgs();
		loginArgs.setUsername("admin");
		loginArgs.setPassword("admin");
		loginArgs.setHost("10.7.1.55");
		loginArgs.setPort(8089);
		String line = null;
		try {
			Service.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);
			Service service = Service.connect(loginArgs);
			service.login();
			SavedSearchDispatchArgs dispatchArgs =  new SavedSearchDispatchArgs();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			Date startTime = calendar.getTime();
			System.out.println("startTime:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime));
			Date endTime = new Date();
			System.out.println("endTime:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endTime.getTime()));
			dispatchArgs.setDispatchEarliestTime(startTime);
			dispatchArgs.setDispatchLatestTime(endTime);
			Date date01 = new Date();
			SavedSearch savedSearch = service.getSavedSearches().get("ad_audit_info");
			Job jobSavedSearch = null;
			jobSavedSearch = savedSearch.dispatch();
			System.out.println("Waiting for the job to finish...\n");
			// Wait for the job to finish
			while (!jobSavedSearch.isDone()) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			JobResultsArgs resultsArgs = new JobResultsArgs();
			resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);
			resultsArgs.setCount(0);
			InputStream results = jobSavedSearch.getResults(resultsArgs);
			System.out.println("Results from the search job as XML:\n");
			BufferedReader br = new BufferedReader(new InputStreamReader(results, "UTF-8"));
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			base.setReturnMessage("总耗时："+(new Date().getTime()-date01.getTime()));
			base.setReturnObject(line);
			System.out.println("\t Success!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("\t Failure! ");
		}
		return base.toString();
	}
	
}

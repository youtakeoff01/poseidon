package com.hand.bdss.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.bdss.web.common.util.JsonUtils;
import com.hand.bdss.web.entity.LastPullDateEntity;
import com.splunk.Job;
import com.splunk.JobResultsArgs;
import com.splunk.SSLSecurityProtocol;
import com.splunk.SavedSearch;
import com.splunk.SavedSearchDispatchArgs;
import com.splunk.Service;
import com.splunk.ServiceArgs;

public class Test {

	public static void main(String[] args) {

		
		ServiceArgs loginArgs = new ServiceArgs();
		loginArgs.setUsername("admin");
		loginArgs.setPassword("admin");
		loginArgs.setHost("10.7.1.55");
		loginArgs.setPort(8089);
		try {
			Date start = new Date();
			Service.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);
			Service service = Service.connect(loginArgs);
			service.login();
			SavedSearchDispatchArgs dispatchArgs =  new SavedSearchDispatchArgs();
			Calendar calendar = Calendar.getInstance();
            calendar.set(2017, 9, 20);
            calendar.set(Calendar.HOUR_OF_DAY, 7);
            calendar.set(Calendar.MINUTE, 40);
            calendar.set(Calendar.SECOND, 0);
            Date startTime = calendar.getTime();
			System.out.println("startTime:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime));
			
			Calendar calendar02 = Calendar.getInstance();
			calendar02.set(2017, 9, 20);
			calendar02.set(Calendar.HOUR_OF_DAY, 7);
			calendar02.set(Calendar.MINUTE, 50);
			calendar02.set(Calendar.SECOND, 0);
			
			
			Date endTime = calendar02.getTime();
			System.out.println("endTime:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endTime.getTime()));
			dispatchArgs.setDispatchEarliestTime(startTime);
			dispatchArgs.setDispatchLatestTime(endTime);
			SavedSearch savedSearch = service.getSavedSearches().get("mailbox");
			Job jobSavedSearch = null;
			jobSavedSearch = savedSearch.dispatch(dispatchArgs);
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
			resultsArgs.setCount(20);
			resultsArgs.setOutputMode(JobResultsArgs.OutputMode.JSON);
			InputStream results = jobSavedSearch.getResults(resultsArgs);
			String line = null;
			StringBuffer result = new StringBuffer();
			System.out.println("Results from the search job as JSON:\n");
			BufferedReader br = new BufferedReader(new InputStreamReader(results, "UTF-8"));
			while ((line = br.readLine()) != null) {
				result.append(line);
			}
			System.out.println(result.toString());
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			JSONObject jsonObject = JSON.parseObject(result.toString());
			String resultss = jsonObject.getString("results");
			List<LastPullDateEntity> lists = JsonUtils.toArray(resultss, LastPullDateEntity.class);
			System.out.println(lists.size());
			System.out.println("success");
			System.out.println(new Date().getTime()-start.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("\t Failure! ");
		}
	}
}

package com.hand.bdss.web.operationcenter.warn.timer;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.hand.bdss.web.operationcenter.warn.dao.EmailLogDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.hand.bdss.dsmp.metrics.DataServiceMetrics;
import com.hand.bdss.web.operationcenter.warn.dao.EmailDao;
import com.hand.bdss.web.operationcenter.warn.dao.NoticeRuleDao;
import com.hand.bdss.web.entity.EmailEntity;
import com.hand.bdss.web.entity.EmailLogEntity;
import com.hand.bdss.web.entity.NoticeRuleEntity;
import com.hand.bdss.web.common.util.SendEmailUtils;

/**
 * 创建任务的类
 * @author Administrator
 *
 */
@Component 
public class CreateTimerTask {
	
	@Resource
	private EmailDao emailDaoImpl;
	@Resource
	private NoticeRuleDao noticeRuleDaoImpl;
	@Resource
	private EmailLogDao emailLogDaoImpl;
	//记录有问题的组件总数
	private int errorNum = 0;
	//记录所有组件的总数(1表示的是数据使用率)
	private int nums = 1;
	
//	@Scheduled(cron = "* 0/20 * * * ?")
	public void job(){
		errorNum = 0;
		nums = 1;
		String content1 = "";
		//获取各个出现问题需要发邮件的组件
		Map<String,String> map = getServiceState();
		if(!map.isEmpty()){
			for (Map.Entry<String,String> entry : map.entrySet()) {
				errorNum++;
				content1 = content1+entry.getKey()+" 服务出现了异常，请及时处理。<br>";
			}
			content1 = content1+"*********************************************** <br>";
		}
		//发送邮件
		try {
			sendLogic(content1);
			//将系统异常占比存入数据库中
			insertSysErrorProp();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	/**
	 * 将系统异常占比存入数据库中
	 * @throws Exception
	 */
	private void insertSysErrorProp() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("allNums", nums);//组件总数
		map.put("errorNums", errorNum);//错误数
		emailLogDaoImpl.insertSysErrorProp(map);
	}
	
	
	private Map<String,String> getServiceState(){
		Map<String,String> map = new HashMap<String,String>();
        String[] strs = new String[]{"HDFS","ZOOKEEPER","HIVE","HBASE"};
        for (String string : strs) {
        	nums++;
        	String state = new DataServiceMetrics().getServiceStatus(string);
        	if("red".equalsIgnoreCase(state)){
        		map.put(string, state);
        	}
		}
        return map;
	}
	
	private void sendLogic(String content) throws Exception{
		//查询当前系统的数据使用率
		float rate = new DataServiceMetrics().getDataUseRate();
		//获取系统各个状态下组件的数量
		Map<String,Integer> map = getDiverseInfo();
		
		//获取所有的渠道信息
		List<EmailEntity> emailEntitys = emailDaoImpl.listEmailsAll(new EmailEntity());
		if(emailEntitys!=null && emailEntitys.size()>0){
			for (EmailEntity emailEntity : emailEntitys) {
				//是否需要发邮件。
				boolean boo = false;
				//需要发送邮件的内容。
				String cont = "";
				//根据渠道信息查询该渠道信息对应的配置规则信息
				NoticeRuleEntity notice = new NoticeRuleEntity();
				notice.setMailChannelId(emailEntity.getId());
				List<NoticeRuleEntity> noticeRules = noticeRuleDaoImpl.listNoticeRuleAll(notice);
				//查询系统信息和配置规则进行比较，看看哪些规则被满足发邮件条件。
				if(noticeRules!=null && noticeRules.size()>0){
					for (NoticeRuleEntity noticeRuleEntity : noticeRules) {
						if("数据使用率".equalsIgnoreCase(noticeRuleEntity.getTriggerRule())){
							 //如果满足规则，则需要发邮件。
							//将% 转化为小数形式
							 NumberFormat nf=NumberFormat.getPercentInstance();
							 Number m=nf.parse(noticeRuleEntity.getRuleNum());
							
							 String condition = noticeRuleEntity.getTriggerCondition().trim();
							 if(">=".equals(condition)){
								 if(rate>=Float.parseFloat(m.toString())){
									 boo = true;
									 errorNum++;
									 cont = noticeRuleEntity.getNoticeContent()+"<br>"+
									 "**********************************************<br>";
								 }
							 }
							 if(">".equals(condition)){
								 if(rate>Float.parseFloat(m.toString())){
									 boo = true;
									 errorNum++;
									 cont = noticeRuleEntity.getNoticeContent()+"<br>"+
									 "**********************************************<br>";
								 }
							 }
							 if("<=".equals(condition)){
								 if(rate<=Float.parseFloat(m.toString())){
									 boo = true;
									 errorNum++;
									 cont = noticeRuleEntity.getNoticeContent()+"<br>"+
									 "**********************************************<br>";
								 }
							 }
							 if("<".equals(condition)){
								 if(rate<Float.parseFloat(m.toString())){
									 boo = true;
									 errorNum++;
									 cont = noticeRuleEntity.getNoticeContent()+"<br>"+
									 "**********************************************<br>";
								 }
								 
							 }
							 if("==".equals(condition)){
								 if(rate==Float.parseFloat(m.toString())){
									 boo = true;
									 errorNum++;
									 cont = noticeRuleEntity.getNoticeContent()+"<br>"+
									 "**********************************************<br>";
								 }
							 }
							 if("!=".equals(condition)){
								 if(rate!=Float.parseFloat(m.toString())){
									 boo = true;
									 errorNum++;
									 cont = noticeRuleEntity.getNoticeContent()+"<br>"+
									 "**********************************************<br>";
								 }
							 }
						 }
						 if("集群状态".equalsIgnoreCase(noticeRuleEntity.getTriggerRule())){
							 String ruleNum = noticeRuleEntity.getRuleNum();
							 String[] ruleNums = ruleNum.split(";");
							 //记录有问题的状态数
							 for (String string : ruleNums) {
								 errorNum = errorNum + map.get(string);
							}
							 for (String string : ruleNums) {
								 //表明需要发邮件
								 if(map.get(string)>0){
									boo = true;
									cont = cont + noticeRuleEntity.getNoticeContent()+"<br>";
									break;
								}
							}
							 
						 }
					}
					
				}
				if(boo){
					cont = content + cont;
					//发送邮件
					if(StringUtils.isNotBlank(cont)){
						sendEmail(emailEntity,cont);	
					}
				}else{
					if(StringUtils.isNotBlank(content)){
						sendEmail(emailEntity,content);
					}
				}
			}
		}
	}
	
	/**
	 * 获取系统各个状态下组件的数量
	 */
	private Map<String,Integer> getDiverseInfo(){
		 Map<String,Integer> map = new HashMap<String,Integer>();
		 String[] ruleNums = new String[]{"CRITICAL","UNKNOWN","WARNING"};
		 for (String string : ruleNums) {
			 //获取集群中某个状态下的组件数值（CRITICAL 、UNKNOWN、WARNING）
			 int num = new DataServiceMetrics().getDiverseInfo(string);
			 nums = nums + num;
			 if(num>0){
				 map.put(string, num);
			 }
		}
		return map;
	}
	/**
	 * 发送邮件
	 * @param
	 * @param content
	 * @throws Exception
	 */
	private void sendEmail(EmailEntity emailEntity, String content) throws Exception{
		//发送邮件
//		SendEmailUtils.sendEmailSMTP(emailEntity, content);
		//保存发送邮件的历史记录到数据库中
//		wirteEmailToDatabase(emailEntity, content);
	}
	
	/**
	 * 将发送的邮件写到数据库中作为备份
	 * @param
	 */
	private void wirteEmailToDatabase(EmailEntity emailEntity, String content) throws Exception{
		EmailLogEntity emLogEntity = new EmailLogEntity();
		emLogEntity.setContent(content);
		emLogEntity.setEmailFrom(emailEntity.getSendAccount());
		emLogEntity.setEmailTo(emailEntity.getReceiveAcount());
		emailLogDaoImpl.insertEmailLog(emLogEntity);
	}
	
	/*public static void main(String[] args) {
		CreateTimerTask timer = new CreateTimerTask();
		Map<String,Integer> map = new HashMap<String,Integer>();
		map.put("a", 12);
		System.out.println(map.get("a"));
		timer.job();
	}*/
}

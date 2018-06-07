package com.hand.bdss.web.common.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.hand.bdss.web.entity.EmailEntity;
import com.sun.mail.util.MailSSLSocketFactory;

public class SendEmailUtils {
	
	public static  MimeMessage createEmail(Session session, String sendMail, String receiveMails,String mailTitle,String mailTheme,String content) throws MessagingException, IOException {
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(sendMail, mailTitle, "UTF-8"));
		String[] receiveMail = receiveMails.split(";");
		List<InternetAddress> list = new ArrayList<InternetAddress>();
		for (String string : receiveMail) {
			 list.add(new InternetAddress(string));
		}
		InternetAddress[] address =(InternetAddress[])list.toArray(new InternetAddress[list.size()]);
		message.setRecipients(MimeMessage.RecipientType.TO,address);
//		message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress("cheng.tan@hand-china.com", "", "UTF-8"));
//		message.setRecipient(MimeMessage.RecipientType.BCC, new InternetAddress("cong.xiang@hand-china.com","","utf-8"));
		message.setSubject(mailTheme,"UTF-8");
		message.setContent(content,"text/html;charset=UTF-8");
		message.setSentDate(new Date());
		message.saveChanges();
		//邮件信息以文件发送则需要放掉下面代码
//		OutputStream out = new FileOutputStream("myemail.eml");
//		message.writeTo(out);
//		out.flush();
//		out.close();
		return message;
	}
	
	public static void sendEmailSMTP(EmailEntity emailEntity,String content) throws Exception{
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
//		props.setProperty("mail.smtp.port", emailEntity.getPort());
		if("true".equalsIgnoreCase(emailEntity.getBooean_ssl())){
			MailSSLSocketFactory sf = null;
			try {
				sf = new MailSSLSocketFactory();
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
			sf.setTrustAllHosts(true);
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.ssl.socketFactory", sf);
//			props.put("mail.smtp.socketFactory.port", emailEntity.getPort());
		}
		props.setProperty("mail.smtp.host", emailEntity.getSendServer());//发送邮件的服务器
		props.setProperty("mail.smtp.auth", "true");

		
		Session session = Session.getInstance(props);
		session.setDebug(true);
		MimeMessage message = createEmail(session, emailEntity.getSendAccount(), emailEntity.getReceiveAcount(),emailEntity.getMsgHeader(),emailEntity.getMsgTheme(),content);
		Transport transport = session.getTransport();
		transport.connect(emailEntity.getSendAccount(),emailEntity.getEmailPassword());
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}
}

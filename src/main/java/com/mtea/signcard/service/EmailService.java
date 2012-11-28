package com.mtea.signcard.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mtea.signcard.global.SignConstants;
import com.mtea.signcard.model.Signer;

/**
 * 邮件服务类
 * @author macrotea@qq.com
 * @version v1.0
 * @date 2012-6-8 上午9:55:15
 * @note
 */
public class EmailService {
	
	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	/**
	 * 提醒我
	 * macrotea / 2012-6-8 上午9:55:00
	 * @param signer 
	 */
	public void remindMe(Signer signer) {
		List<String> remindEmails = signer.getEmailList();
		if (remindEmails == null || remindEmails.size() == 0) {
			return;
		}
		for (String email : remindEmails) {
			if (email.trim().length() > 0) {
				String emailContent = buildEmailContent(signer);
				doRemind(email, emailContent);
			}
		}
	}

	/**
	 * 根据signer构建邮件正文内容
	 * @author macrotea@qq.com
	 * @date 2012-11-23 下午10:30:56
	 * @param signer
	 * @return
	 */
	private String buildEmailContent(Signer signer) {
		return String.format(SignConstants.MAIL_REMIND_CONTENT,
				signer.getName(),
				formatter.format(new Date()),
				SignConstants.URL_CONTEXT_PATH,
				SignConstants.MASTER_SIGNER.getName());
	}

	/**
	 * 执行邮件提醒
	 * macrotea / 2012-6-8 上午9:55:09
	 * @param signer 
	 */
	private void doRemind(String mail, String emailContent) {
		Properties props = new Properties();
		props.put("username", "liangqiye@gz.iscas.ac.cn");
		props.put("password", "");
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", "smtp.nfschina.com");
		props.put("mail.smtp.port", "25");

		Session mailSession = Session.getDefaultInstance(props);
		Message messsage = new MimeMessage(mailSession);
		Transport transport = null;

		try {
			//发送人
			messsage.setFrom(new InternetAddress("liangqiye@gz.iscas.ac.cn"));
			messsage.addRecipients(Message.RecipientType.TO, InternetAddress.parse(mail));
			messsage.setSentDate(new Date());
			messsage.setSubject(SignConstants.MAIL_REMIND_TITLE);
			messsage.setText(emailContent);
			
			messsage.saveChanges();
			transport = mailSession.getTransport("smtp");
			transport.connect(props.getProperty("mail.smtp.host"), props.getProperty("username"), props.getProperty("password"));
			transport.sendMessage(messsage, messsage.getAllRecipients());

		} catch (AddressException e) {
			logger.debug("程序进行邮件发送时,地址出错!",e);
		} catch (MessagingException e) {
			logger.debug("程序进行邮件发送时,信息出错!",e);
		} finally {
			try {
				transport.close();
			} catch (MessagingException ignore) {
			}
		}
	}
}

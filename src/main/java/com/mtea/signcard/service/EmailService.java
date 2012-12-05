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
import com.mtea.signcard.utils.StringUtils;

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
	 * 当签到失败发送邮件
	 * macrotea / 2012-6-8 上午9:55:00
	 * @param signer 
	 */
	public void sendMailWhenFail(Signer signer) {
		if(signer==null) return;
		List<String> remindEmails = signer.getEmailList();
		if (remindEmails == null || remindEmails.size() == 0) {
			return;
		}
		for (String email : remindEmails) {
			if (StringUtils.isNotEmpty(email)) {
				String emailContent = buildEmailContentWhenFail(signer);
				doSend(email,SignConstants.MAIL_REMIND_TITLE, emailContent);
			}
		}
	}

	/**
	 * 根据signer构建签到失败的邮件正文内容
	 * @author macrotea@qq.com
	 * @date 2012-11-23 下午10:30:56
	 * @param signer
	 * @return
	 */
	private String buildEmailContentWhenFail(Signer signer) {
		if(signer==null) return "";
		return String.format(SignConstants.MAIL_REMIND_CONTENT,
				signer.getName(),
				formatter.format(new Date()),
				SignConstants.URL_CONTEXT_PATH,
				SignConstants.MASTER_SIGNER.getName());
	}

	/**
	 * 执行邮件发送
	 * macrotea / 2012-6-8 上午9:55:09
	 * @param signer 
	 */
	private void doSend(String mail, String title, String emailContent) {
		Properties props = new Properties();
		props.put("username", SignConstants.MAIL_USERNAME);
		props.put("password", SignConstants.MAIL_PASSWORD);
		props.put("mail.transport.protocol", SignConstants.MAIL_TRANSPORT_PROTOCOL);
		props.put("mail.smtp.host", SignConstants.MAIL_SMTP_HOST);
		props.put("mail.smtp.port", SignConstants.MAIL_SMTP_PORT);

		Session mailSession = Session.getDefaultInstance(props);
		Message messsage = new MimeMessage(mailSession);
		Transport transport = null;

		try {
			//发送人
			messsage.setFrom(new InternetAddress(SignConstants.MAIL_USERNAME));
			messsage.addRecipients(Message.RecipientType.TO, InternetAddress.parse(mail));
			messsage.setSentDate(new Date());
			messsage.setSubject(title);
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

	/**
	 * 提醒我登录系统查看考勤信息
	 * @param each
	 * liangqiye / 2012-12-4 下午9:07:53
	 */
	public void sendMailRemindToSmarter(Signer signer) {
		if(signer==null) return;
		List<String> remindEmails = signer.getEmailList();
		if (remindEmails == null || remindEmails.size() == 0) {
			return;
		}
		logger.info("正在发邮件提醒 {} 登录Smarter系统查看考勤信息...", signer.getName());
		for (String email : remindEmails) {
			if (StringUtils.isNotEmpty(email)) {
				String emailContent = buildEmailContentOfRemindToSmarter(signer);
				doSend(email,SignConstants.MAIL_REMIND_TO_SMARTER_TITLE, emailContent);
			}
		}
	}

	/**
	 * 根据signer构建提醒查看考勤信息的邮件正文内容
	 * @param signer
	 * @return
	 * liangqiye / 2012-12-4 下午9:11:59
	 */
	private String buildEmailContentOfRemindToSmarter(Signer signer) {
		if(signer==null) return "";
		return String.format(SignConstants.MAIL_REMIND_TO_SMARTER_CONTENT,
				signer.getName(),
				SignConstants.URL_CONTEXT_PATH,
				SignConstants.MASTER_SIGNER.getName());
	}
}

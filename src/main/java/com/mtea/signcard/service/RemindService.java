/**
 * Copyright (C) 2012 macrotea@qq.com Inc., All Rights Reserved.
 */
package com.mtea.signcard.service;

import java.util.Arrays;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mtea.signcard.global.ConfigHolder;
import com.mtea.signcard.global.SignConstants;
import com.mtea.signcard.model.Signer;

/**
 * 提醒服务类
 * @author 	liangqiye@@gz.iscas.ac.cn
 * @version 1.0 , 2012-12-4 下午8:50:22	
 */
public class RemindService {
	
	private static final Logger logger = LoggerFactory.getLogger(RemindService.class);
	
	private EmailService emailService=new EmailService();
	
	/**
	 * 保持提醒
	 * liangqiye / 2012-12-4 下午8:52:18
	 */
	public void keepRemind(){
		logger.info("{}已启动每月提醒登录系统查看考勤信息线程...", SignConstants.APP_NAME);
		new RemindThread().start();
	}

	/**
	 * 提醒登录系统修改考勤线程
	 * @author 	liangqiye@@gz.iscas.ac.cn
	 * @version 1.0 , 2012-12-4 下午8:51:38
	 */
	class RemindThread extends Thread{
		
		int temp = -1;
			
		@Override
		public void run() {
			while (true) {
				int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				
				if (temp != day && Arrays.asList(SignConstants.MAIL_REMIND_DAYS).contains(day)) {
					for (Signer each : ConfigHolder.SIGNER_LIST) {
						emailService.sendMailRemindToSmarter(each);
					}
					temp = day;
				}
				try {
					// 睡 8 个小时
					Thread.sleep(SignConstants.MINIUTE * 60 * 8);
				} catch (InterruptedException ignore) {
				}
			}
		}
		
	}
}

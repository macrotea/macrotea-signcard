package com.mtea.signcard.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mtea.signcard.global.ConfigHolder;
import com.mtea.signcard.global.SignConstants;
import com.mtea.signcard.model.Signer;

/**
 * 签到服务类
 * 
 * @author macrotea@qq.com
 * @version v1.0
 * @date 2012-6-7 下午5:27:31
 * @note
 */
public class SignService {
	
	private static final Logger logger = LoggerFactory.getLogger(SignService.class);

	private EmailService emailService = new EmailService();

	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	/**
	 * 校验签到是否成功
	 * macrotea / 2012-6-7 下午9:35:46
	 */
	private boolean validateSign(String responseContent) {
		return responseContent.equals("1") ? true : false;
	}
	
	/**
	 * 开始签到
	 * macrotea / 2012-6-8 上午9:03:45
	 */
	public void startSign(){
		logger.info("{}已启动签到服务...", SignConstants.APP_NAME);
		for (Signer each : ConfigHolder.SIGNER_LIST) {
			executorService.execute(new SignMonitor(each));
		}
	}
	
	/**
	 * 执行签到
	 * macrotea / 2012-6-8 上午9:28:53
	 */
	public boolean doSign(Signer signer){
		ranSleep();
		boolean flag=true;
		PageAccessService pageAccessService =null;
		try {
			pageAccessService = new PageAccessService();
			String loginPageHtml = pageAccessService.getLoginPageHtml(SignConstants.URL_CONTEXT_PATH + SignConstants.FORM_LOGIN_URI);
			String loginPageAction = pageAccessService.getLoginPageFormAction(loginPageHtml);
			String realUrl = pageAccessService.getRealUrlAfterLogin(signer.getName(), signer.getPassword(), SignConstants.URL_CONTEXT_PATH + loginPageAction);
			String query = pageAccessService.getCurrUriQuery(realUrl);
			String feeback = pageAccessService.executeSign(String.format("%s%s?%s", SignConstants.URL_CONTEXT_PATH, SignConstants.SIGN_ACTION_URI, query));
			
			//校验
			if (validateSign(feeback)) {
				logger.info("{}为签到者: {} 签到成功!", SignConstants.APP_NAME , signer.getName());
			} else {
				flag = false;
				logger.info("{}为签到者: {} 签到失败!", SignConstants.APP_NAME , signer.getName());
			}
		} catch (ClientProtocolException e) {
			flag=false;
			logger.error("程序请求地址有误!",e);
		} catch (IOException e) {
			flag=false;
			logger.error("程序访问页面流出错!",e);
		} catch (URISyntaxException e) {
			flag=false;
			logger.error("程序访问URI不合法!",e);
		} catch (RuntimeException e) {
			flag=false;
			logger.error("程序运行时异常!",e);
		}finally{
			pageAccessService.exit();
		}
		if(!flag){
			logger.info("程序执行签到失败");
			emailService.sendMailWhenFail(signer);
		}
		return flag;
	}
	
	/**
	 * 随机随眠
	 * liangqiye / 2012-12-5 上午9:09:53
	 */
	private void ranSleep() {
		try {
			Thread.sleep(new Double(new Random().nextDouble() * SignConstants.MINIUTE).longValue());
		} catch (InterruptedException ignore) {
		}
	}

	/**
	 * 签到服务结束
	 * macrotea / 2012-6-8 上午9:28:53
	 */
	public void signOver(){
		executorService.shutdown();
	}
	
	/**
	 * 签到监视器
	 * @author macrotea@qq.com
	 * @version v1.0
	 * @date 2012-6-8 上午9:03:53
	 * @note
	 */
	private class SignMonitor implements Runnable {
		
		/**
		 * 是否已经上午签到
		 */
		private boolean hasMormingSign = false;
		
		/**
		 * 是否已经傍晚签到
		 */
		private boolean hasDuskSign = false;
		
		/**
		 * 是否已经夜晚签到
		 */
		private boolean hasNightSign = false;
		
		/**
		 * 是否禁止夜晚签到
		 */
		private boolean stopNightSign = false;

		@Override
		public void run() {
			
			logger.info("{}已经启动签到者: {} 的签到线程...", SignConstants.APP_NAME, this.signer.getName());
			
			while (true) {
				
				Calendar now = Calendar.getInstance();
				int week = now.get(Calendar.DAY_OF_WEEK) + 1;
				int hour = now.get(Calendar.HOUR_OF_DAY);
				int minute = now.get(Calendar.MINUTE);

				//过滤星期
				if(!signer.getWeekList().contains(week)){
					doSleep(SignConstants.MINIUTE * 60);
					continue;
				}
				
				// 早上签到,8:30后打卡
				if (hour == 8 && minute > 30) {
					if (!hasMormingSign) {
						// 执行签到
						doSign(signer);
						hasMormingSign = true;
					}
				} else {
					hasMormingSign = false;
				}

				// 傍晚签到,17:30分之后
				if (hour == 17 && minute > 30) {
					if (!hasDuskSign) {
						// 执行签到
						doSign(signer);
						hasDuskSign = true;
					}
				} else {
					hasDuskSign = false;
				}
				
				//是否晚上打开
				if(signer.isNightSign()){
					
					// 若电脑19:30分后还开机则再次打卡
					if (hour == 19 && minute > 30) {
						if (!hasNightSign && !stopNightSign) {
							// 执行签到，若7:30分签到失败证明自己的电脑网络环境不是在软件所,故而停止签到
							if (!doSign(signer)) {
								stopNightSign = true;
							};
							hasNightSign = true;
						}
					} else {
						hasNightSign = false;
						stopNightSign = false;
					}
					
				}
				/*
				else{
					logger.info("{}已经放行对{}的晚上签到...", SignConstants.APP_NAME, this.signer.getName());
				}
				*/
				
				doSleep(SignConstants.MINIUTE);
			}
		}

		/**
		 * 睡眠
		 * @author macrotea@qq.com
		 * @date 2012-11-23 下午10:16:23
		 * @param minutes
		 */
		public void doSleep(long minutes) {
			try {
				Thread.sleep(minutes);
			} catch (InterruptedException ignore) {
			}
		}
		
		private Signer signer;

		public SignMonitor(Signer signer) {
			this.signer = signer;
		}
	}

}

//若是星期五的话，则只能在17-18点之间保证程序运行: //(week != Calendar.FRIDAY && hour == 17 && minute > 22) || (week == Calendar.FRIDAY && hour == 16 && minute > 48)
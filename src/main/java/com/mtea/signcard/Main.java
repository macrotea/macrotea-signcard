package com.mtea.signcard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mtea.signcard.global.ConfigHolder;
import com.mtea.signcard.global.SignConstants;
import com.mtea.signcard.service.RemindService;
import com.mtea.signcard.service.SignService;

/**
 * 签到程序主入口
 * @author macrotea@qq.com
 * @date 2012-11-23 下午9:28:53
 * @version 1.0
 * @note
 */
public class Main {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args){
		Object[] argArray = { SignConstants.APP_NAME, SignConstants.APP_VERSION, ConfigHolder.class.getName() };
		logger.info("{}({})已加载签到配置类: {}", argArray);
		logger.info("已配置签到者信息列表: {}", ConfigHolder.getSignerListInfo());
		new SignService().startSign();
		new RemindService().keepRemind();
	}
	

}

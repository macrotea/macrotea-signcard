package com.mtea.signcard.global;

import com.mtea.signcard.model.Signer;

/**
 * 签到常量
 * @author macrotea@qq.com
 * @date 2012-11-23 下午9:28:39
 * @version 1.0
 * @note
 */
public class SignConstants {
	
	/**
	 * 程序名
	 */
	public static String APP_NAME = "自动签到程序";

	/**
	 * 程序版本
	 */
	public static String APP_VERSION = "v1.0";
	
	/**
	 * 定义分钟
	 */
	public static final long MINIUTE = 60 * 1000;
	
	/**
	 * 默认主导签到者的名字
	 */
	public static String DEF_MASTER_SIGNER_NAME = "liangqiye";

	/**
	 * 签到页面链接文字
	 */
	public static String A_HAND_SIGN_TEXT;
	
	/**
	 * 网页上下文路径
	 */
	public static String URL_CONTEXT_PATH;

	/**
	 * 登录表单用户名域
	 */
	public static String USERNAME_FIELD_NAME;
	
	/**
	 * 登录表单密码域
	 */
	public static String PASSWORD_FIELD_NAME;
	
	/**
	 * 表单登录页面URI
	 */
	public static String FORM_LOGIN_URI;
	
	/**
	 * 签到动作URI
	 */
	public static String SIGN_ACTION_URI;

	/**
	 * 提醒邮件标题
	 */
	public static String MAIL_REMIND_TITLE;
	
	/**
	 * 提醒邮件内容
	 */
	public static String MAIL_REMIND_CONTENT;
	
	/**
	 * 提醒登录系统查看考勤信息的邮件标题
	 */
	public static String MAIL_REMIND_TO_SMARTER_TITLE;
	
	/**
	 * 提醒登录系统查看考勤信息的邮件内容
	 */
	public static String MAIL_REMIND_TO_SMARTER_CONTENT;
	
	/**
	 * 提醒登录系统查看考勤信息的日期
	 */
	public static Integer[] MAIL_REMIND_DAYS = { 15, 28, 30};

	/**
	 * 主导签到者
	 */
	public static Signer MASTER_SIGNER;
	
	/**
	 * 用户名
	 */
	public static String MAIL_USERNAME;

	/**
	 * 密码
	 */
	public static String MAIL_PASSWORD;

	/**
	 * 协议
	 */
	public static String MAIL_TRANSPORT_PROTOCOL;
	
	/**
	 * 主机
	 */
	public static String MAIL_SMTP_HOST;
	
	/**
	 * 端口
	 */
	public static String MAIL_SMTP_PORT;
	
	/**
	 * 签到者注册文件
	 */
	public static final String SIGNERS_FILE = "/signers.xml";

	/**
	 * 签到配置文件
	 */
	public static final String SIGN_FILE = "/sign.properties";
	
	/**
	 * 邮件配置文件
	 */
	public static final String MAIL_FILE = "/mail.properties";
	
	
}

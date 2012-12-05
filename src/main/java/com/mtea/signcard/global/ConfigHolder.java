package com.mtea.signcard.global;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mtea.signcard.model.Signer;

/**
 * 配置持有者
 * @author macrotea@qq.com
 * @date 2012-11-10 下午4:17:00
 * @version 1.0
 * @note
 */
public class ConfigHolder {
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigHolder.class);

	public static List<Signer> SIGNER_LIST = new ArrayList<Signer>();
	
	static {
		loadSignerFile();
        loadSignFile();
        loadEmailFile();
	}

	/**
	 * 加载签到配置文件
	 * @author macrotea@qq.com
	 * @date 2012-11-23 下午9:32:57
	 */
	private static void loadSignFile() {
		InputStream inStream = ConfigHolder.class.getResourceAsStream(SignConstants.SIGN_FILE);
		Properties prop = new Properties();
		try {
			prop.load(inStream);
		} catch (IOException e) {
			logger.error(String.format("系统加载签到配置文件: %s 出错!", SignConstants.SIGN_FILE), e);
		}
        
        //设定全局常量
        SignConstants.A_HAND_SIGN_TEXT = prop.getProperty("a.handsign.text");
		SignConstants.URL_CONTEXT_PATH = prop.getProperty("url.context.path");
		SignConstants.FORM_LOGIN_URI = prop.getProperty("form.login.uri");
		SignConstants.SIGN_ACTION_URI = prop.getProperty("sign.action.uri");
		SignConstants.USERNAME_FIELD_NAME = prop.getProperty("form.login.fieldname.username");
		SignConstants.PASSWORD_FIELD_NAME = prop.getProperty("form.login.fieldname.password");
		SignConstants.MAIL_REMIND_TITLE = prop.getProperty("mail.remind.title");
		SignConstants.MAIL_REMIND_CONTENT = prop.getProperty("mail.remind.content");
		SignConstants.MAIL_REMIND_TO_SMARTER_TITLE = prop.getProperty("mail.remindToSmarter.title");
		SignConstants.MAIL_REMIND_TO_SMARTER_CONTENT = prop.getProperty("mail.remindToSmarter.content");
		SignConstants.MASTER_SIGNER = getMasterSigner();
	}

	/**
	 * 加载邮件配置文件
	 * liangqiye / 2012-12-4 下午9:21:35
	 */
	private static void loadEmailFile() {
		InputStream inStream = ConfigHolder.class.getResourceAsStream(SignConstants.MAIL_FILE);
		Properties prop = new Properties();
		try {
			prop.load(inStream);
		} catch (IOException e) {
			logger.error(String.format("系统加载邮件配置文件: %s 出错!", SignConstants.MAIL_FILE), e);
		}
        
        //设定全局常量
        SignConstants.MAIL_USERNAME = prop.getProperty("username");
		SignConstants.MAIL_PASSWORD = prop.getProperty("password");
		SignConstants.MAIL_TRANSPORT_PROTOCOL = prop.getProperty("mail.transport.protocol");
		SignConstants.MAIL_SMTP_HOST = prop.getProperty("mail.smtp.host");
		SignConstants.MAIL_SMTP_PORT = prop.getProperty("mail.smtp.port");
	}

	/**
	 * 获得主导的签到者
	 * @author macrotea@qq.com
	 * @date 2012-11-23 下午9:58:21
	 * @return
	 */
	private static Signer getMasterSigner() {
		if(SIGNER_LIST==null || SIGNER_LIST.size()==0) return new Signer(SignConstants.DEF_MASTER_SIGNER_NAME);
		for (Signer each : SIGNER_LIST) {
			if(each.isMaster()) return each;
		}
		return new Signer(SignConstants.DEF_MASTER_SIGNER_NAME);
	}

	/**
	 * 加载签到者注册文件
	 * @author macrotea@qq.com
	 * @date 2012-11-23 下午9:33:09
	 */
	private static void loadSignerFile() {
		InputStream inStream = ConfigHolder.class.getResourceAsStream(SignConstants.SIGNERS_FILE);
		
        try {
			Document doc = new SAXReader().read(inStream);
			Element root = doc.getRootElement();
			createSignerList(root);
		} catch (DocumentException e) {
			logger.error(String.format("系统加载签到者注册文件: %s 出错!", SignConstants.SIGNERS_FILE), e);
		}
	}

	/**
	 * 创建Signer对象集合
	 * 
	 * @author macrotea@qq.com
	 * @date 2012-11-10 下午5:02:35
	 * @param root
	 */
	@SuppressWarnings("unchecked")
	private static void createSignerList(Element root) {
		
        Iterator<Element> signerElementIterator = root.elementIterator("signer");
        
        //遍历所有的signer元素
        while (signerElementIterator.hasNext()) {
        	Element signerElement =  signerElementIterator.next();
        	
        	//获得数据
			String name = signerElement.elementTextTrim("name");
			String password = signerElement.elementTextTrim("password");
			String weeksText = signerElement.elementTextTrim("weeks");
			Element emailsElement = signerElement.element("emails");
			String nightSign = signerElement.elementTextTrim("nightSign");
			String enable = signerElement.elementTextTrim("enable");
			String master = signerElement.elementTextTrim("master");
			
			//包装数据
			List<Integer> weekList = createWeekListBy(weeksText);
			List<String> emailList = createEmailListBy(emailsElement);
        	
			//构造对象
			Signer signer =new  Signer();
        	signer.setName(name);
        	signer.setPassword(password);
        	signer.setWeekList(weekList);
        	signer.setEmailList(emailList);
        	signer.setNightSign(Boolean.valueOf(nightSign));
        	signer.setEnable(Boolean.valueOf(enable));
        	signer.setMaster(Boolean.valueOf(master));
        	
        	//添加
        	SIGNER_LIST.add(signer);
		}
	}

	/**
	 * 根据weeksText文本内容通过逗号分割获得星期列表
	 * @author macrotea@qq.com
	 * @date 2012-11-23 下午8:37:56
	 * @param weeksText
	 * @return
	 */
	private static List<Integer> createWeekListBy(String weeksText) {
		List<Integer> retVal =new ArrayList<Integer>();
		//中英逗号
		String[] weeks=weeksText.split("[,，]");
		for (String each : weeks) {
			int realWeek = Integer.valueOf(each)+1;
			if(!retVal.contains(realWeek)){
				//解决与Calendar的常量值的差异
				retVal.add(realWeek);
			}
		}
		return retVal;
	}

	/**
	 * 根据元素: emails 获得其下的所有孩子文本
	 * @author macrotea@qq.com
	 * @date 2012-11-23 下午8:32:18
	 * @param emailsElement
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<String> createEmailListBy(Element emailsElement) {
		List<String> retVal = new ArrayList<String>();
		Iterator<Element> emailElementIterator = emailsElement.elementIterator("email");
		while (emailElementIterator.hasNext()) {
			Element email = emailElementIterator.next();
			String text = email.getTextTrim();
			retVal.add(text);
		}
		return retVal;
	}
	
	/**
	 * 获得签到者列表信息
	 * @return
	 * liangqiye / 2012-11-28 上午8:42:42
	 */
	public static String getSignerListInfo(){
		StringBuilder sb = new StringBuilder();
		for (Signer signer : SIGNER_LIST) {
			sb.append("\n").append(signer.toString());
		}
		return sb.toString();
	}
	
}

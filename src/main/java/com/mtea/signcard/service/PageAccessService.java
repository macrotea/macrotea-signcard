package com.mtea.signcard.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mtea.signcard.global.SignConstants;
import com.mtea.signcard.utils.RegexpUtils;

/**
 * 页面访问服务类
 * @author macrotea@qq.com
 * @version v1.0
 * @date 2012-6-7 下午5:27:06
 * @note
 */
public class PageAccessService {
	
	private static final Logger logger = LoggerFactory.getLogger(PageAccessService.class);
	
	private DefaultHttpClient httpclient = new DefaultHttpClient();
	
	/**
	 * 获得登陆页HTML
	 * macrotea / 2012-6-7 下午8:48:05
	 */
	public String getLoginPageHtml(String loginUrl) throws ClientProtocolException, IOException{
		return getHtml(loginUrl);
	}
	
	/**
	 * 从登陆页源码中获得action
	 * macrotea / 2012-6-7 下午8:48:14
	 */
	public String getLoginPageFormAction(String html){
		return Jsoup.parse(html).select("form#loginForm").attr("action");
	}
	
	/**
	 * 获得登录后的真实Url
	 * 因为存在302的情况
	 * macrotea / 2012-6-7 下午8:48:14
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public String getRealUrlAfterLogin(String username,String password,String actionUrl) throws ParseException, IOException{
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair(SignConstants.USERNAME_FIELD_NAME, username));
		nvps.add(new BasicNameValuePair(SignConstants.PASSWORD_FIELD_NAME, password));
		
		HttpPost httpost = new HttpPost(actionUrl);
		httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		HttpResponse response = httpclient.execute(httpost);
		Header locationHeader = response.getFirstHeader("Location");
		
		if(locationHeader==null){
			String errorMessage=String.format("根据URL={}获得Location失败!", actionUrl);
			logger.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
		//UrlEncodedFormEntity没有关闭，则使用httpost.abort()，希望我的猜想正确
		httpost.abort();
		return locationHeader.getValue();
	}
	
	/**
	 * 根据登录后的Url获得当前登录的用户的"empId=61"
	 * macrotea / 2012-6-7 下午9:27:56
	 */
	public String getCurrUriQuery(String url) throws ClientProtocolException, IOException, URISyntaxException{
		String html=getHtml(url);
		String actionUri=RegexpUtils.getSignPageUri(html);
		return new URI(actionUri).getQuery();
	}
	/**
	 * 执行签到,返回信息
	 * macrotea / 2012-6-7 下午9:27:56
	 */
	public String executeSign(String url) throws ClientProtocolException, IOException, URISyntaxException{
		String feedback=getHtml(url);
		return feedback;
	}
	
	/**
	 * 根据url获得源码
	 * macrotea / 2012-6-7 下午9:12:22
	 */
	public String getHtml(String url) throws ClientProtocolException, IOException {
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if(entity==null){
			String errorMessage=String.format("根据URL={}获得源码失败!", url);
			logger.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
		//toString()底层会关闭流
		return EntityUtils.toString(entity);
	}

	/**
	 * 退出
	 * macrotea / 2012-6-8 上午9:30:15
	 */
	public void exit() {
		httpclient.getConnectionManager().shutdown();
	}

}

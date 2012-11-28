package com.mtea.signcard.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mtea.signcard.global.SignConstants;

/**
 * 正则表达式工具类
 * @author macrotea@qq.com
 * @version v1.0
 * @date 2012-6-7 下午5:24:05
 * @note
 */
public final class RegexpUtils {
	
	/**
	 * 根据HTML内容获得iframe中签到的内容页的URI
	 * eg: initHandAttendance.action?empId=61
	 * macrotea / 2012-6-7 下午4:40:56
	 */
	public static String getSignPageUri(String html){
		Pattern signPageUriPattern=Pattern.compile(String.format("<a href=\"(.+)\" target=\"content\">%s</a>",SignConstants.A_HAND_SIGN_TEXT));
		Matcher m=signPageUriPattern.matcher(html);
		if(m.find()){
			return m.group(1);
		}
		return null;
	}

}

package com.mtea.signcard.utils;

/**
 * 字符串工具类
 * @author macrotea@qq.com
 * @date 2012-11-23 下午9:34:18
 * @version 1.0
 * @note
 */
public final class StringUtils {

	/**
	 * 是否为空
	 * @author macrotea@qq.com
	 * @date 2012-11-10 下午4:47:22
	 * @param source
	 * @return
	 */
	public static boolean isEmpty(String source) {
		if (source == null || source.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 是否不为空
	 * @author macrotea@qq.com
	 * @date 2012-11-10 下午4:47:22
	 * @param source
	 * @return
	 */
	public static boolean isNotEmpty(String source) {
		return !isEmpty(source);
	}

}

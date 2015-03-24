package com.jimome.mm.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.jimome.mm.common.Conf;

public class StringUtils {
	private static StringUtils stringUtils;
	private static Context context;

	/**
	 * StringUitls类中的方法都是静态的， 阻止其创建实例，以引起一些误解。 另一种做法是将其变为abstract类，
	 * 但是这样也会引起一些误解， 因为abstract类的意思就是可以继承的， 然而该类却不应该由其他类来继承。
	 * 因而abstract类个人感觉不是一种好的做法。
	 */
	private StringUtils() {
	}

	/**
	 * 单例模式
	 * 
	 * @param ctx
	 * @return
	 */
	public static StringUtils getInstance(Context ctx) {
		if (stringUtils == null) {
			context = ctx;
			stringUtils = new StringUtils();
		}
		return stringUtils;
	}

	/**
	 * 将array中的内容以delimiter为间隔拼接字符串
	 * 
	 * @param array
	 * @param delimiter
	 * @return
	 */
	public static String join(Object[] array, String delimiter) {
		if (array == null) {
			throw new IllegalArgumentException();
		}

		if (array.length == 0) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		for (Object item : array) {
			builder.append(item.toString() + delimiter);
		}
		builder.delete(builder.length() - delimiter.length(), builder.length());
		return builder.toString();
	}

	/**
	 * 将list中的内容以delimiter为间隔拼接字符串
	 * 
	 * @param list
	 * @param delimiter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String join(List list, String delimiter) {
		if (list == null) {
			throw new IllegalArgumentException();
		}

		return join(list.toArray(), delimiter);
	}

	// public static String MD5(Map map){
	// List arrayList = new ArrayList(map.entrySet());
	// // 注意构造函数
	// Collections.sort(arrayList, new TariffComparator.TariffMapComparator());
	// // 排序后
	// StringBuffer sb = new StringBuffer();
	// for (Iterator it = arrayList.iterator(); it.hasNext();) {
	// Map.Entry entry = (Map.Entry) it.next();
	// System.out.println(entry.getKey() + ":" + entry.getValue());
	// sb.append(entry.getKey() + "=" + entry.getValue());
	// if (it.hasNext()) {
	// sb.append("&");
	// }
	// }
	// sb.append("&" + Conf.SERCET_KEY);
	// return sb.toString();
	// }

	/**
	 * z转化成UTF-8
	 * 
	 * @param s
	 * @return
	 */
	public static String getUtf8Str(String s) {
		String ret = null;
		try {
			ret = java.net.URLEncoder.encode(s, "utf-8");
		} catch (UnsupportedEncodingException ex) {
		}
		return ret;
	}

	/**
	 * 判断一个字符串是否含有数字
	 **/

	public static boolean hasDigit(String content) {

		boolean flag = false;
		Pattern p = Pattern.compile(".*\\d+.*");
		Matcher m = p.matcher(content);
		if (m.matches())
			flag = true;

		return flag;

	}

	public static String getResourse(int red) {
		return context.getResources().getString(red);
	}

	/**
	 * 截取数字
	 */
	public static String getNumbers(String content) {
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			return matcher.group(0);
		}
		return "";
	}

	// public static String httpRsponse(String str) {
	// String error = str.substring(str.length() - 3, str.length());
	// return error;
	//
	// }

}
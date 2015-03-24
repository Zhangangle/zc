package com.jimome.mm.utils;

import android.util.Log;

/**
 * log日志工具类
 * @author admin
 *
 */
public class LogUtils {
	
	private static boolean DEBUG_LOG = false;
	
	public static void openDebuglog(boolean enable){
		DEBUG_LOG = enable;
	}
	
	/**
	 * 打印e级log
	 * @param tag
	 * @param error
	 */
	public static void printLogE(String tag ,String msg){
		if(DEBUG_LOG){
			Log.e(tag, msg);
		}
	}
	
	public static void printLogE(Class<?> cls, String msg) {
		if (DEBUG_LOG) {
			Log.e(cls.getName(), msg);
		}
	}
	 
	/**
	 * 打印w级log
	 * @param tag
	 * @param error
	 */
	public static void printLogW(String tag ,String msg){
		if(DEBUG_LOG){
			Log.w(tag, msg);
		}
	}
	
	public static void printLogW(Class<?> cls, String msg) {
		if (DEBUG_LOG) {
			Log.w(cls.getName(), msg);
		}
	}
	
	/**
	 * 打印i级log
	 * @param tag
	 * @param error
	 */
	public static void printLogI(String tag ,String msg){
		if(DEBUG_LOG){
			Log.i(tag, msg);
		}
	}
	public static void printLogI(Class<?> cls, String msg) {
		if (DEBUG_LOG) {
			Log.i(cls.getName(), msg);
		}
	}
	
	/**
	 * 打印d级log
	 * @param tag
	 * @param error
	 */
	public static void printLogD(String tag ,String msg){
		if(DEBUG_LOG){
			Log.d(tag, msg);
		}
	}

	public static void printLogD(Class<?> cls, String msg) {
		if (DEBUG_LOG) {
			Log.d(cls.getName(), msg);
		}
	}
}

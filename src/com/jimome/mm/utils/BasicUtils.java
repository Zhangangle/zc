package com.jimome.mm.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

/**
 * Function:工具类:
 * 
 * @author Angle
 * 
 */
public class BasicUtils {

	/** 判断SDK是否已挂 **/
	public static boolean isSDCardAvaliable() {
		File sdcFile = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdcFile = Environment.getExternalStorageDirectory();
			if (sdcFile == null)
				return false;
		}
		/** 获得sdc文件路径 **/
		String sdcPath = Environment.getExternalStorageDirectory().getPath();
		StatFs statfs = new StatFs(sdcPath);
		long blocSize = statfs.getBlockSize();
		long availaBlock = statfs.getAvailableBlocks();
		long freesize = (blocSize * availaBlock) / (1024 * 1024);
		if (freesize >= 4) {
			return true;
		}
		return false;
	}

	/**
	 * 进度条对话框
	 * 
	 * @param context
	 * @return
	 */
	public static ProgressDialog showDialog(Context context) {
		ProgressDialog dialog = new ProgressDialog(context);
		return dialog;
	}

	/**
	 * 对话框
	 * 
	 * @param source
	 * @return
	 */
	public static Dialog showDialog(Context context, int style) {
		Dialog dialog = new Dialog(context, style);
		return dialog;
	}

	public static View showView(Context context, int resid) {
		View view = LayoutInflater.from(context).inflate(resid, null);
		return view;
	}
	
	/**
	 * toast提示
	 */
	static Context context;
	public static void toast(Context ctx){
		context = ctx;
	}
	public static void toast(String msg){
		 Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 检测是否有emoji表情
	 * 
	 * @param source
	 * @return
	 */
	public static boolean containsEmoji(String source) {
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			// 如果不能匹配,则该字符是Emoji表情
			if (!isEmojiCharacter(codePoint)) {
				// 是特殊字符
				return true;
			}
		}
		return false;

	}

	/**
	 * 判断是否是Emoji
	 * 
	 * @param codePoint
	 *            比较的单个字符
	 * @return
	 */
	private static boolean isEmojiCharacter(char codePoint) {
		return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
				|| (codePoint == 0xD)
				|| ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
				|| ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
				|| ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
	}

	public static String stringFilter(String str) throws PatternSyntaxException {
		// 禁止输入汉字
		String regEx = "[\u4E00-\u9FA5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取文件字节
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static byte[] getByte(File file) throws IOException, Exception {
		byte[] bytes = null;
		if (file != null) {
			InputStream is = new FileInputStream(file);
			int length = (int) file.length();
			if (length > Integer.MAX_VALUE) // 当文件的长度超过了int的最大值
			{
				return null;
			}
			bytes = new byte[length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			// 如果得到的字节长度和file实际的长度不一致就可能出错了
			if (offset < bytes.length) {
				return null;
			}
			is.close();
		}
		return bytes;
	}

	// 防止暴力点击延时事件
	private static long lastClickTime = 0;

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 1000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 判断包名是否存在
	 * 
	 * @return
	 */
	public static boolean isInstallApk(String packName) {
		// TODO Auto-generated method stub
		return new File("/data/data/" + packName).exists();

	}

	// 获取API版本号
	public static int getAndroidOSVersion() {
		int osVersion;
		try {
			osVersion = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		} catch (NumberFormatException e) {
			osVersion = 0;
		}
		return osVersion;
	}

	public static void setBackground(Context context, View view, int id) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			view.setBackground(context.getResources().getDrawable(id));
		} else {
			view.setBackgroundDrawable(context.getResources().getDrawable(id));
		}
	}
	
	public static String currenttime(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String date=sdf.format(new java.util.Date());
		return date;
	}
	public static int days(String currentDate){
		int result = 0;
		try {
			SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
			String date1 =currentDate;
			Calendar c1=Calendar.getInstance();
			SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
			String date2=sdf2.format(new Date());
			java.util.Calendar c2=java.util.Calendar.getInstance();
			
			c1.setTime(sdf1.parse(date1));
			c2.setTime(sdf2.parse(date2));
			
			result= c1.compareTo(c2);
			if (result == 0)
				result = 0;
			else if (result < 0)
				result = -1;
			else
				result = 1;
		} catch (Exception e) {
			// TODO: handle exception
			result = -1;
		}
		return result;
	}
}

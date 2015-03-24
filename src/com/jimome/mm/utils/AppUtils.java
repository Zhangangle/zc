package com.jimome.mm.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;

public class AppUtils {
	
	  /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context aty) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = aty.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        return width;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context aty) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = aty.getResources().getDisplayMetrics();
        int height = dm.heightPixels;
        return height;
    }
    
    /**
     * 获取sdk版本号
     * 
     * @return 
     */
    public static int getSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }
    
    /**
     *获取系统版本
     * 
     * @return 
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }
    
    /**
     * 隐藏系统键盘
     * @param aty
     */
    public static void hideKeyBoard(Activity aty) {
        ((InputMethodManager) aty
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(aty.getCurrentFocus()
                        .getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }
    
    /**
     * 获取版本信息
     */
    public static int getAppVersion(Context context) {
        int version = 0;
        try {
            version = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        	new RuntimeException("获取失败");
        }
        return version;
    }
    
    /**
     * 安装apk
     * 
     * @param context
     * @param file
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("application/vnd.android.package-archive");
        intent.setData(Uri.fromFile(file));
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    
    /**
	 * 创建快捷方式
	 * @param cxt 
	 * @param icon ͼƬid
	 * @param title
	 * @param cls class
	 */
	  public void createDeskShortCut(Context cxt, int icon,
	            String title, Class<?> cls) {
	        Intent shortcutIntent = new Intent(
	                "com.android.launcher.action.INSTALL_SHORTCUT");
	        shortcutIntent.putExtra("duplicate", false);
	        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
	        Parcelable ico = Intent.ShortcutIconResource.fromContext(
	                cxt.getApplicationContext(), icon);
	        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
	                ico);
	        Intent intent = new Intent(cxt, cls);
	        intent.setAction("android.intent.action.MAIN");
	        intent.addCategory("android.intent.category.LAUNCHER");
	        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
	        cxt.sendBroadcast(shortcutIntent);
	    }
	  

		public static boolean copyApkFromAssets(Context context, String fileName,
				String path) {
			boolean copyIsFinish = false;
			try {
				InputStream is = context.getAssets().open(fileName);
				File file = new File(path);
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] temp = new byte[1024];
				int i = 0;
				while ((i = is.read(temp)) > 0) {
					fos.write(temp, 0, i);
				}
				fos.close();
				is.close();
				copyIsFinish = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return copyIsFinish;
		}  

		
	// 安装apk
	public static void installApk(Context context) {
		try {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(
					Uri.parse("file://"
							+ Environment.getExternalStorageDirectory()
									.getAbsolutePath() + "/cjmediaplayer.apk"),
					"application/vnd.android.package-archive");
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
}

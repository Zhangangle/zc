package com.jimome.mm.utils;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class NetworkUtils {
	private ConnectivityManager connManager;
	private Context context;

	public NetworkUtils(Context paramContext) {
		this.context = paramContext;
		this.connManager = ((ConnectivityManager) this.context
				.getSystemService(Context.CONNECTIVITY_SERVICE));
	}

	public static String getNetworkType(Context paramContext) {
		NetworkInfo info = ((ConnectivityManager) paramContext
				.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		String type = null;
		if(info == null){
			type = "";
		}else if(info.getType() == ConnectivityManager.TYPE_WIFI){
			type = "wifi";
		}else if(info.getType() == ConnectivityManager.TYPE_MOBILE){
			type = "mobile";
		}
			
		return type;
	}


	public boolean isMobileConnected() {

		NetworkInfo mMobile = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (mMobile != null) {
			return mMobile.isConnected();
		}
		return false;
	}

	
	public boolean isWiFiOpen() {
		if (((WifiManager) context.getSystemService("wifi"))
				.getWifiState() == 3)
			;
		for (boolean bool = true;; bool = false)
			return bool;
	}

	public boolean isWifiConnected() {

		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi != null) {
			return mWifi.isConnected();
		}

		return false;
	}

	//打开移动网络
	public void toggleGprs(boolean paramBoolean) throws Exception {
		Class localClass = this.connManager.getClass();
		Class[] arrayOfClass = new Class[1];
		arrayOfClass[0] = Boolean.TYPE;
		Method localMethod = localClass.getMethod("setMobileDataEnabled",
				arrayOfClass);
		ConnectivityManager localConnectivityManager = this.connManager;
		Object[] arrayOfObject = new Object[1];
		arrayOfObject[0] = Boolean.valueOf(paramBoolean);
		localMethod.invoke(localConnectivityManager, arrayOfObject);
	}

	//打开wif
	public boolean toggleWiFi(boolean paramBoolean) {
		return ((WifiManager) this.context.getSystemService("wifi"))
				.setWifiEnabled(paramBoolean);
	}

	/**
	 * 检测网络是否可用
	 */
	public static boolean checkNet(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return info != null;
	}

}

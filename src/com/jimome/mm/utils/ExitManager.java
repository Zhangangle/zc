package com.jimome.mm.utils;

import java.util.Stack;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.jimome.mm.activity.SplashActivity;

public class ExitManager {
	private static Stack<Activity> activityStack;// Activity栈
	private static ExitManager instance;
	private static Intent intent;

	private ExitManager() {
	}

	public static ExitManager getScreenManager() {
		if (instance == null) {
			instance = new ExitManager();
		}
		return instance;
	}

	// 退出栈顶Activity
	public void popActivity(Activity activity) {
		if (activity != null) {
			activity.finish();
			activityStack.remove(activity);
			activity = null;
		}
	}

	// 获得当前栈顶Activity
	public Activity currentActivity() {
		Activity activity = null;
		try {
			activity = activityStack.lastElement();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			return activity;
		}
	}

	// 将当前Activity推入栈中（每个Activity的OnCreate()中添加该方法：ExitManager.getScreenManager().pushActivity(this);）
	public void pushActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	// 将当前Activity推入栈中（每个Activity的OnDestroy()中添加该方法：ExitManager.getScreenManager().pullActivity(this);）
	public void pullActivity(Activity activity) {
		activityStack.remove(activity);
	}

	// 退出程序时使用,退出栈中所有Activity()(调用该方法:ExitManager.getScreenManager().popAllActivity();)
	public void popAllActivity() {
		try {
			int i = 0;
			int j = activityStack.size();
			while (i < j) {
				Activity activity = currentActivity();
				if (activity == null) {
					break;
				}
				popActivity(activity);
				++i;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// 判断是否跳转到登录页面
	public static void intentLogin(Context context, String status) {
		try {
			if (status.equals("401")) {
				if (intent == null) {

					intent = new Intent(context, SplashActivity.class);
				}
				SplashActivity.status = "401";
				context.startActivity(intent);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// 将签名存放到Shared中
	public static void setAuth(Context context, String token) {
		PreferenceHelper.write(context, "auth", "token", token);
	}

}

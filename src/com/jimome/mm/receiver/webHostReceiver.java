package com.jimome.mm.receiver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.jimome.mm.common.Conf;
import com.jimome.mm.service.JiMoMainService;
import com.jimome.mm.utils.LogUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class webHostReceiver extends BroadcastReceiver{

	int time = 5000;//5秒
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		final Handler handler = new Handler(){
			
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
					case 0 :
						final String strings = (String) msg.obj;
								JiMoMainService.cachedThreadPool.execute(new Runnable() {
										public void run() {
											try {
												String[] sp = strings.split(";");
												String uri = "";
												if(sp[0].toString().trim().indexOf("h") == 1){
													uri = sp[0].toString().trim().substring(1,sp[0].toString().length());
												}else if(sp[0].toString().trim().indexOf("h") == 0){
													uri = sp[0].toString().trim();
												}
												URL url1 = new URL(uri);
												HttpURLConnection conn1 = (HttpURLConnection) url1
														.openConnection();
												conn1.setReadTimeout(time);
												conn1.setConnectTimeout(time);
												conn1.setRequestMethod("GET");
												if (conn1.getResponseCode() == 200) {
													LogUtils.printLogE("访问成功------", "!!!");
												} else {
												}
			
												if(sp[1].toString().trim().indexOf("h") == 1){
													uri = sp[1].toString().trim().substring(1,sp[1].toString().length());
												}else if(sp[1].toString().trim().indexOf("h") == 0){
													uri = sp[1].toString().trim();
												}
												URL url2 = new URL(uri);
												HttpURLConnection conn2 = (HttpURLConnection) url2
														.openConnection();
												conn2.setReadTimeout(time);
												conn2.setConnectTimeout(time);
												conn2.setRequestMethod("GET");
												if (conn2.getResponseCode() == 200) {
													LogUtils.printLogE("访问成功------", "!!!");
												} else {
												}

											} catch (Exception e) {
												// TODO: handle exception
												e.printStackTrace();
											}

										}
								});
						break;

					default :
						break;
				}
			}
		};

		JiMoMainService.cachedThreadPool.execute(
				new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL url = new URL(Conf.IPWEB_SERVERURL);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(6 * 1000);
					conn.setRequestMethod("GET");
					
					InputStream inStream = conn.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inStream));

					StringBuffer strings = new StringBuffer();
					String line = "";
					while ((line = reader.readLine()) != null) {
						strings.append(line);
					}
					inStream.close();
					Message message = Message.obtain();
					message.what = 0;
					message.obj = strings.toString();
					handler.sendMessageDelayed(message, time);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
	}

}

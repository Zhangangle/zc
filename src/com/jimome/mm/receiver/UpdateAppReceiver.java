package com.jimome.mm.receiver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.unjiaoyou.mm.R;
import com.jimome.mm.activity.MainFragmentActivity;
import com.jimome.mm.application.JiMoApplication;
import com.jimome.mm.common.Conf;
import com.jimome.mm.service.JiMoMainService;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.IOUtils;
import com.jimome.mm.utils.LogUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UpdateAppReceiver extends BroadcastReceiver{

	private String downloadUrl = "";
	
	private Context context;
	private long fileTotalSize;
	private String version = "";
	private int serverVersion = 0; 
	private PackageInfo packageInfo;
	public static boolean updateed = false;
	public static HttpHandler downhandler;
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		LogUtils.printLogE("广播定时---", "检查版本更新");
		this.context = arg0;
		updateVersion();
	}

	private void updateVersion() {
		// TODO Auto-generated method stub
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					List<String> strings = (List<String>) msg.obj;
//					String version = strings.get(0);
					try {
						packageInfo = context
								.getPackageManager().getPackageInfo(
										context.getPackageName(), 0);
						for (String string : strings) {
							if (string.contains("channels")) {
								String[] splits = string.split(";");
								String channels = splits[1];
								String[] channelsp = channels.split(":");
								if ("un_meibo".equals(channelsp[1])) {
									downloadUrl = splits[2];
									version = splits[0];
								} else {
									if (channelsp[1].equals("none")){
										downloadUrl = splits[2];
										version = splits[0];
									}
										
								}
							}
						}
						if(!version.equals("")){
							String temp = version.substring(version.indexOf("\"") + 1,
									version.length() - 1);
									serverVersion = Integer.valueOf(temp);
//									LogUtils.printLogE("服务器版本号", "---" + serverVersion);
									new AsyncseverFileSize().execute();
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

					break;

				default:
					break;
				}
			}
		};

		JiMoMainService.cachedThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL url = new URL(Conf.UPDATE_SERVERURL);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(6 * 1000);
					conn.setRequestMethod("GET");
					InputStream inStream = conn.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inStream));

					List<String> strings = new ArrayList<String>();
					String line = "";
					while ((line = reader.readLine()) != null) {
						strings.add(line);
					}
					inStream.close();

					Message message = Message.obtain();
					message.what = 0;
					message.obj = strings;
					handler.sendMessage(message);
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		});

	}
	
	class AsyncseverFileSize extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			try {
				URL url = new URL(downloadUrl);
				URLConnection conn = url.openConnection();
				InputStream is = conn.getInputStream();
				fileTotalSize = conn.getContentLength();
				LogUtils.printLogE("服务器的文件大小----", ""+fileTotalSize);
			} catch (Exception e) {
				// TODO: handle exception
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				if (packageInfo.versionCode < serverVersion) {
					updateed = true;
					File apkfile = new File(IOUtils.getAppUpdateFile().toString());
					if ( apkfile.exists()) {
						LogUtils.printLogE("安装之前的比较",getFileSize(apkfile)+"=="+fileTotalSize);
						if(getFileSize(apkfile) == fileTotalSize){
							LogUtils.printLogE("apk安装提醒---", "---");
							showUpdateDialog();
						}
						
					} else {
						LogUtils.printLogE("apk下载更新---", "---");
						HttpUtils http = new HttpUtils();
						downhandler = http.download(
								downloadUrl, IOUtils.getAppUpdateFile()
										.toString(), true,
								new RequestCallBack<File>() {

									@Override
									public void onStart() {
									}

									@Override
									public void onLoading(long total,
											long current,
											boolean isUploading) {
//										fileTotalSize = total;
										LogUtils.printLogE("apk下载更新进度---", total+"---"+((int)current *100 /total));
										int max = (int) ((int)current *100 /total);
										if(max == 100){
											Editor editor = JiMoApplication.sp.edit();
											editor.putBoolean(
													"updatesuccess", true);
											editor.commit();
											showUpdateDialog();
										}
									}

									@Override
									public void onSuccess(
											ResponseInfo<File> responseInfo) {
										
									}

									@Override
									public void onFailure(
											HttpException error,
											String msg) {

									}
								});
						
					
					}
					
				} else {
					updateed = false;
					IOUtils.getAppUpdateFile().delete();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
		
		
	}

	/**
	 * 获取下载之后的文件大小
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private static long getFileSize(File file) throws Exception {
		long size = 0;
		try {
			if (file.exists()) {
				FileInputStream fis = null;
				fis = new FileInputStream(file);
				size = fis.available();
				LogUtils.printLogE("下载完成之后的文件大小----", ""+size);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return size;
	}
	protected void showUpdateDialog() {
		// TODO Auto-generated method stub
		final Dialog dialog = BasicUtils.showDialog(context,
				R.style.BasicDialog);
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.getWindow().setContentView(R.layout.dialog_jiahei);
		dialog.setCanceledOnTouchOutside(true);
		((TextView) dialog.getWindow().findViewById(R.id.tv_dialog_msg))
				.setText(context.getString(R.string.str_update));
		((Button) dialog.getWindow().findViewById(R.id.btn_dialog_cancle))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						Intent stopintent = new Intent(context,
								JiMoMainService.class);
						context.stopService(stopintent);
						android.os.Process.killProcess(android.os.Process
								.myPid());
						System.exit(0);// 否则退出程序
					}
				});
		((Button) dialog.getWindow().findViewById(R.id.btn_dialog_sure))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						Editor editor = JiMoApplication.sp.edit();
						editor.putBoolean("updatesuccess", false);
						editor.commit();
						installApk();

					}
				});
		if(!dialog.isShowing()){
			dialog.show();
		}
	}

	// 安装apk
	private void installApk() {
		File apkfile = new File(IOUtils.getAppUpdateFile().toString());
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
		Intent stopintent = new Intent(context,
				JiMoMainService.class);
		context.stopService(stopintent);
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);// 否则退出程序
		
	}
}

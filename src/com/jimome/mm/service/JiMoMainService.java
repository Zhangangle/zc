package com.jimome.mm.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.AccessControlList;
import com.aliyun.mbaas.oss.model.OSSException;
import com.aliyun.mbaas.oss.storage.OSSBucket;
import com.aliyun.mbaas.oss.storage.OSSData;
import com.aliyun.mbaas.oss.storage.OSSFile;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jimome.mm.activity.MainFragmentActivity;
import com.jimome.mm.application.JiMoApplication;
import com.jimome.mm.bean.NewUser;
import com.jimome.mm.bean.PhotoImage;
import com.jimome.mm.common.Conf;
import com.jimome.mm.database.UserDAO;
import com.jimome.mm.receiver.UpdateAppReceiver;
import com.jimome.mm.receiver.webHostReceiver;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.IOUtils;
import com.jimome.mm.utils.LogUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.unjiaoyou.mm.R;
import com.upay.billing.utils.Json;

public class JiMoMainService extends Service {
	private String uploadCotent = "";
	private String uploadType = "";
	private SharedPreferences sp;
	public static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	File sdDir = null;
	private int index = 0;
	private ArrayList<PhotoImage> images;
	
	private int delay = 10 * 1000; // 10s(延迟10秒执行)
	private int period = 60 * 1000; // 10s(每10秒循环)
	private OSSBucket ossBucket;
	private OSSBucket videoBucket;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//定位
//		initLocClient();
		sp = getSharedPreferences("versioninfo", Context.MODE_PRIVATE);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("jimome.action.uploadvideo");
		intentFilter.addAction("jimome.action.uploadalbum");
		intentFilter.addAction("jimome.action.uploadshow");
		intentFilter.addAction("jimome.action.uploadtalkshow");
		intentFilter.addAction("jimome.action.uploadtalkpic");
		registerReceiver(uploadBroadcastReceiver, intentFilter);
		ossBucket = new OSSBucket(Conf.ACCESS_BUCKETNAME);
		ossBucket.setBucketACL(AccessControlList.PUBLIC_READ);
		videoBucket  = new OSSBucket(Conf.ACCESS_VIDEOBUCKETNAME);
		videoBucket.setBucketACL(AccessControlList.PUBLIC_READ);
		// 获取公网IP地址
		getPublicNetwork();
		// 定时检查版本升级
		updateVersion();
		//定时访问web主页
//		webHost();
		//访问登录接口
		if (JiMoApplication.getInstance().initUser()){
			int day = BasicUtils.days(PreferenceHelper.readString(getApplicationContext(), "userinfo", "login_day"));
			if(day == 0){
				Conf.gender =PreferenceHelper.readString(getApplicationContext(), "userinfo","gender");
				Conf.user_VIP = PreferenceHelper.readString(getApplicationContext(), "userinfo","user_VIP");
				Conf.userID = PreferenceHelper.readString(getApplicationContext(), "userinfo","userID");
				Conf.userImg =PreferenceHelper.readString(getApplicationContext(), "userinfo","userImg");
				Conf.userCharm = PreferenceHelper.readString(getApplicationContext(), "userinfo","userCharm");
				Conf.userCoin =PreferenceHelper.readString(getApplicationContext(), "userinfo","userCoin");
				Conf.userName =PreferenceHelper.readString(getApplicationContext(), "userinfo","userName");
				Conf.userMsg_counts =PreferenceHelper.readString(getApplicationContext(), "userinfo","userMsg_counts");
			}else if(day == -1){
				PreferenceHelper.write(getApplicationContext(), "userinfo", "login", false);
				loginHttps("login");
			}
			
		}else{
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					loginHttps("register");
				}
			}, 200);
			
		}
		
//		List<NewUser> list = JiMoApplication.getInstance().list;
//		if (list == null || list.size() < 1) {
//			//异步获取ccid
//			new AsyncTask<Void, Void, Void>() {
//				
//				@Override
//				protected void onPreExecute() {
//					// TODO Auto-generated method stub
//					super.onPreExecute();
//					LogUtils.printLogE("异步扫描准备---", "---");
//						getCCID();
//				}
//				@Override
//				protected void onPostExecute(Void result) {
//					
//				}
//				
//				@Override
//				protected Void doInBackground(Void... arg0) {
//					// TODO Auto-generated method stub
//					LogUtils.printLogE("异步扫描中---", "---");
//					searchDir(sdDir);
//					return null;
//				}
//
//			}.execute();
//
//		}
		
	}
	private void loginHttps(final String type) {
		// TODO Auto-generated method stub
		try {
			RequestParams params = new RequestParams();
			String key = "";
			int cache_time=30;
			if (type.equals("register")) {
				key = "install/imei";
				params.addBodyParameter("models", Conf.Model);
				params.addBodyParameter("sim", Conf.SIM);
				params.addBodyParameter("os_version",
						android.os.Build.VERSION.RELEASE);
				params.addBodyParameter("cid", Conf.CID);
			} else if (type.equals("login")) {
				key = "login/imei";
			}
			params.addBodyParameter("imei", Conf.IMEI);
			params.addBodyParameter("city", Conf.city);
			params.addBodyParameter("version", Conf.version);
			params.addBodyParameter("longitude", Conf.lontitude);// 经度
			params.addBodyParameter("latitude", Conf.latitude);// 纬度
			params.addBodyParameter("ip", Conf.PublicNetwork);
			params.addBodyParameter("address", Conf.address);
			CacheRequest.requestPOST(getApplicationContext(), key, params, key, cache_time,
					new CacheRequestCallBack() {

						@Override
						public void onData(String json) {
							// TODO Auto-generated method stub
							NewUser user = new Gson().fromJson(json,
									NewUser.class);
							if (user.getStatus() != null
									&& user.getStatus().equals("200")) {
								StatService.onEvent(getApplicationContext(),
										"qq-register-success", "eventLabel", 1);
								Conf.gender = user.getGender();
								Conf.user_VIP = user.getIs_vip();
								Conf.userID = user.getUser_id();
								Conf.userImg = user.getIcon();
								Conf.userCharm = user.getCharm();
								Conf.userCoin = user.getCoin();
								Conf.userName = user.getNick();
								Conf.userMsg_counts = user.getMsg_counts();
								PreferenceHelper.write(getApplicationContext(), "userinfo", "login", true);
								PreferenceHelper.write(getApplicationContext(), "userinfo", "gender", user.getGender());
								PreferenceHelper.write(getApplicationContext(), "userinfo", "user_VIP", user.getIs_vip());
								PreferenceHelper.write(getApplicationContext(), "userinfo", "userID", user.getUser_id());
								PreferenceHelper.write(getApplicationContext(), "userinfo", "userImg", user.getIcon());
								PreferenceHelper.write(getApplicationContext(), "userinfo", "userCharm", user.getCharm());
								PreferenceHelper.write(getApplicationContext(), "userinfo", "userCoin", user.getCoin());
								PreferenceHelper.write(getApplicationContext(), "userinfo", "userName", user.getNick());
								PreferenceHelper.write(getApplicationContext(), "userinfo", "userMsg_counts", user.getMsg_counts());
								 PreferenceHelper.write(getApplicationContext(), "userinfo", "login_day", BasicUtils.currenttime());
								ExitManager.getScreenManager().setAuth(getApplicationContext(),
										user.getToken());
								UserDAO userD = new UserDAO(
										getApplicationContext());
								userD.add(Conf.userID, "", "");

							} else {
								if (user.getStatus() != null
										&& user.getStatus().equals("104"))
									Toast.makeText(
											getApplicationContext(),
											getApplicationContext()
													.getString(
															R.string.str_login_usererror),
											Toast.LENGTH_SHORT).show();
								else
									Toast.makeText(
											getApplicationContext(),
											getApplicationContext().getString(
													R.string.str_net_register),
											Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onFail(HttpException e, String result,
								String json) {
						}

					});
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	public static void initLocClient() {
		LocationClient mLocationClient = JiMoApplication.getInstance().mLocationClient;
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式:高精度模式
		option.setCoorType("gcj02");
		option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms:低于1000为手动定位一次，大于或等于1000则为定时定位
		option.setIsNeedAddress(true);// 不需要包含地址信息
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}
	public  void webHost() {
		// TODO Auto-generated method stub
		AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), webHostReceiver.class);
		int requestCode = 1;
		PendingIntent pendIntent = PendingIntent.getBroadcast(
				getApplicationContext(), requestCode, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// 5秒后发送广播，然后每个10秒重复发广播。广播都是直接发到AlarmReceiver的
		int triggerAtTime = (int) ( System.currentTimeMillis() + delay);
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
				triggerAtTime, 10*60*1000, pendIntent);
	}

	public void updateVersion() {
		// TODO Auto-generated method stub
		AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), UpdateAppReceiver.class);
		int requestCode = 2;
		PendingIntent pendIntent = PendingIntent.getBroadcast(
				getApplicationContext(), requestCode, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// 5秒后发送广播，然后每个10秒重复发广播。广播都是直接发到AlarmReceiver的
		int triggerAtTime = (int) ( System.currentTimeMillis() + delay);
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
				triggerAtTime, period, pendIntent);
		
//		final Handler handler = new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				// TODO Auto-generated method stub
//				super.handleMessage(msg);
//				switch (msg.what) {
//				case 0:
//					List<String> strings = (List<String>) msg.obj;
////					String version = strings.get(0);
//
//					String version = "";
//					int serverVersion = 0; 
//					try {
//						PackageInfo packageInfo = getApplicationContext()
//								.getPackageManager().getPackageInfo(
//										getPackageName(), 0);
//						for (String string : strings) {
//							if (string.contains("channels")) {
//								String[] splits = string.split(";");
//								String channels = splits[1];
//								String[] channelsp = channels.split(":");
//								if (Conf.CID.equals(channelsp[1])) {
////									downloadUrl = splits[2];
//									version = splits[0];
//								} else {
//									if (channelsp[1].equals("none")){
//										downloadUrl = splits[2];
//										version = splits[0];
//									}
//										
//								}
//							}
//						}
//						String temp = version.substring(version.indexOf("\"") + 1,
//						version.length() - 1);
//						serverVersion = Integer.valueOf(temp);
//						LogUtils.printLogE("服务器版本号", "---" + serverVersion);
//						
//						if (packageInfo.versionCode < serverVersion) {
//							File apkfile = new File(IOUtils.getAppUpdateFile().toString());
//							if (apkfile.exists()) {
//								LogUtils.printLogE("apk安装提醒---", "---");
//								showUpdateDialog();
//							} else {
//								LogUtils.printLogE("apk下载更新---", "---");
//								HttpUtils http = new HttpUtils();
//								HttpHandler downhandler = http.download(
//										downloadUrl, IOUtils.getAppUpdateFile()
//												.toString(), true,
//										new RequestCallBack<File>() {
//
//											@Override
//											public void onStart() {
//											}
//
//											@Override
//											public void onLoading(long total,
//													long current,
//													boolean isUploading) {
//
//											}
//
//											@Override
//											public void onSuccess(
//													ResponseInfo<File> responseInfo) {
//												Editor editor = sp.edit();
//												editor.putBoolean(
//														"updatesuccess", true);
//												editor.commit();
//												showUpdateDialog();
//												
//											}
//
//											@Override
//											public void onFailure(
//													HttpException error,
//													String msg) {
//
//											}
//										});
//							}
//
//						} else {
//							IOUtils.getAppUpdateFile().delete();
//						}
//					} catch (Exception e) {
//						// TODO: handle exception
//						e.printStackTrace();
//					}
//
//					break;
//
//				default:
//					break;
//				}
//			}
//		};
//
//		cachedThreadPool.execute(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				try {
//					URL url = new URL(Conf.UPDATE_SERVERURL);
//					HttpURLConnection conn = (HttpURLConnection) url
//							.openConnection();
//					conn.setConnectTimeout(6 * 1000);
//					conn.setRequestMethod("GET");
//
//					InputStream inStream = conn.getInputStream();
//					BufferedReader reader = new BufferedReader(
//							new InputStreamReader(inStream));
//
//					List<String> strings = new ArrayList<String>();
//					String line = "";
//					while ((line = reader.readLine()) != null) {
//						strings.add(line);
//					}
//					inStream.close();
//
//					Message message = Message.obtain();
//					message.what = 0;
//					message.obj = strings;
//					handler.sendMessage(message);
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//
//			}
//		});

	}

	protected void showUpdateDialog() {
		// TODO Auto-generated method stub
		final Dialog dialog = BasicUtils.showDialog(getApplicationContext(),
				R.style.BasicDialog);
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.getWindow().setContentView(R.layout.dialog_jiahei);
		dialog.setCanceledOnTouchOutside(true);
		((TextView) dialog.getWindow().findViewById(R.id.tv_dialog_msg))
				.setText(getString(R.string.str_update));
		((Button) dialog.getWindow().findViewById(R.id.btn_dialog_cancle))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						Intent stopintent = new Intent(getApplicationContext(),
								JiMoMainService.class);
						stopService(stopintent);
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
						Editor editor = sp.edit();
						editor.putBoolean("updatesuccess", false);
						editor.commit();
						installApk();

					}
				});
		dialog.show();
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
		startActivity(i);
		Intent stopintent = new Intent(getApplicationContext(),
				JiMoMainService.class);
		stopService(stopintent);
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);// 否则退出程序
		
	}

	// 上传视频广播
	private BroadcastReceiver uploadBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("jimome.action.uploadvideo")) {
				Intent intent2 = new Intent();
				intent2.setAction("jimome.action.myupload");
				sendBroadcast(intent2);
				String filepath = intent.getExtras().getString("videopath");
				String imgpath = intent.getExtras().getString("videoimg");
				uploadType = intent.getExtras().getString("type");
				uploadCotent = intent.getExtras().getString("text");
				String titlename = Conf.userID + "_"
						+ System.currentTimeMillis();
				try {
//					File file = new File(filepath);
//					byte[] byte_file = BasicUtils.getByte(file);
					String finalname = filepath.toString().replace(
							filepath, titlename + ".mp4");
					File file2 = new File(imgpath);
					byte[] byte_img = BasicUtils.getByte(file2);
					String imgname = file2.toString().replace(
							file2.getAbsolutePath(), titlename + ".jpg");
//					uploadAliyun("shipinxiusuoluetu/", imgname,
//							Conf.ACCESS_BUCKETNAME, byte_img);
//					uploadAliyun("shipinxiu/", finalname,
//							Conf.ACCESS_VIDEOBUCKETNAME, byte_file);
					uploadImageByte("shipinxiusuoluetu/",imgname,byte_img);
					uploadVideoFile("shipinxiu/", finalname, filepath);
				} catch (Exception e) {
					// TODO: handle exception
				}

			} else if (action.equals("jimome.action.uploadalbum")) {
//				String imgname = intent.getExtras().getString("albumimg");
				// byte[] byte_img =
				// intent.getExtras().getByteArray("byte_img");
//				byte[] byte_img = Conf.img_byte;
				images = Conf.images;
				uploadType = intent.getExtras().getString("type");
				uploadCotent = intent.getExtras().getString("text");
				String minetype = "";
				if (uploadType.equals("1")) {
					minetype = "nvshenxiu/";
				} else {
					minetype = "shencaixiu/";
				}
				uploadImageFile(minetype, images.get(0).getIcon(), images.get(0).getImgPath());
//				uploadAliyun(minetype, images.get(0).getIcon(), Conf.ACCESS_BUCKETNAME,
//						images.get(0).getImg());
			} else if (action.equals("jimome.action.uploadshow")) {
				String filepath = intent.getExtras().getString("videopath");
				String imgpath = intent.getExtras().getString("videoimg");
				uploadType = intent.getExtras().getString("type");
				uploadCotent = intent.getExtras().getString("text");
				String titlename = Conf.userID + "_"
						+ System.currentTimeMillis();
				try {
					File file = new File(filepath);
					byte[] byte_file = BasicUtils.getByte(file);
					String finalname = file.toString().replace(
							file.getAbsolutePath(), titlename + ".mp4");
					File file2 = new File(imgpath);
					byte[] byte_img = BasicUtils.getByte(file2);
					String imgname = file2.toString().replace(
							file2.getAbsolutePath(), titlename + ".jpg");
//					uploadAliyun("zhuanshuxiusuoluetu/", imgname,
//							Conf.ACCESS_BUCKETNAME, byte_img);
//					uploadAliyun("zhuanshuxiu/", finalname,
//							Conf.ACCESS_VIDEOBUCKETNAME, byte_file);

				} catch (Exception e) {
					// TODO: handle exception
				}
			} else if (action.equals("jimome.action.uploadtalkpic")) {// 聊天对话图片
				String imgname = intent.getExtras().getString("albumimg");
				String imgpath = intent.getExtras().getString("imagepath");
				byte[] byte_img = Conf.img_byte;
				uploadType = "5";//
				String minetype = "xiaoxitupian/";
				uploadImageFile(minetype, imgname, imgpath);
//				uploadAliyun(minetype, imgname, Conf.ACCESS_BUCKETNAME,
//						byte_img);
			} else if (action.equals("jimome.action.uploadtalkshow")) {
				String filepath = intent.getExtras().getString("videopath");
				String imgpath = intent.getExtras().getString("videoimg");
				uploadType = intent.getExtras().getString("type");
				String titlename = Conf.userID + "_"
						+ System.currentTimeMillis();
				try {
//					File file = new File(filepath);
//					byte[] byte_file = BasicUtils.getByte(file);
					String finalname = filepath.toString().replace(
							filepath.toString(), titlename + ".mp4");
					File file2 = new File(imgpath);
					byte[] byte_img = BasicUtils.getByte(file2);
					String imgname = file2.toString().replace(
							file2.getAbsolutePath(), titlename + ".jpg");
//					uploadAliyun("zhuanshuxiusuoluetu/", imgname,
//							Conf.ACCESS_BUCKETNAME, byte_img);
//					uploadAliyun("zhuanshuxiu/", finalname,
//							Conf.ACCESS_VIDEOBUCKETNAME, byte_file);
					uploadImageByte("zhuanshuxiusuoluetu/",imgname,byte_img);
					uploadVideoFile("zhuanshuxiu/", finalname, filepath);

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	};

	
	private void uploadImageByte(final String type,final String fileName,byte[] filebtye) throws OSSException{
		OSSData ossData = new OSSData(ossBucket, type+fileName);
		ossData.setData(filebtye, "image/*");
		ossData.uploadInBackground(new SaveCallback() {
			
			@Override
			public void onProgress(String arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailure(String arg0, OSSException arg1) {
				// TODO Auto-generated method stub
				LogUtils.printLogE("视频缩略图上传失败", arg1.toString());
			}
			
			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				LogUtils.printLogE("视频缩略图上传成功", arg0);
				if (type.equals("shipinxiusuoluetu/")) {
					uploadPicAndvideo(fileName);
				} else if (uploadType.equals("6")) {// 聊天专属秀上传
					if (type.equals("zhuanshuxiusuoluetu/")) {
						Intent intent = new Intent();
						intent.setAction("jimome.action.sendshow");
						intent.putExtra("text", fileName);
						JiMoMainService.this.sendBroadcast(intent);
					}
				}
			}
		});
	}

	private void uploadVideoFile(final String type,final String fileName,String filePath){
		
		OSSFile ossFile = new OSSFile(videoBucket, type+fileName);
		ossFile.setUploadFilePath(filePath, "video/*");
		ossFile.uploadInBackground(new SaveCallback() {
			
			@Override
			public void onProgress(String arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailure(String arg0, OSSException arg1) {
				// TODO Auto-generated method stub
//				LogUtils.printLogE("视频上传失败错误信息", arg1.toString());
			}
			
			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				LogUtils.printLogE("视频上传成功", arg0);
//				ViewInject.toast("视频上传成功");

			}
		});
	}
	
	private void uploadImageFile(final String type,final String fileName,String filePath){
		
		OSSFile ossFile = new OSSFile(ossBucket, type+fileName);
		ossFile.setUploadFilePath(filePath, "image/*");
		ossFile.uploadInBackground(new SaveCallback() {
			
			@Override
			public void onProgress(String arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailure(String arg0, OSSException arg1) {
				// TODO Auto-generated method stub
//				LogUtils.printLogE("上传失败错误信息", arg1.toString());
			}
			
			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				// LogUtils.printLogE("图片上传成功", arg0);
				// ViewInject.toast("上传成功");
				if (uploadType.equals("5")) {
					if (type.equals("xiaoxitupian/")) {
						Intent intent = new Intent();
						intent.setAction("jimome.action.sendpic");
						intent.putExtra("text", fileName);
						JiMoMainService.this.sendBroadcast(intent);
					}
				} else {
					uploadPicAndvideo(fileName);
				}

			}
		});
	}
	private void uploadAliyun(final String type, final String filepath,
			String bucktname, byte[] data) {
		try {

//			new UploadObjectAsyncTask<Void>(JiMoApplication.ACCESS_ID,
//					JiMoApplication.ACCESS_KEY, bucktname, type + filepath) {
//				/*
//				 * (non-Javadoc) * @see
//				 * android.os.AsyncTask#onPostExecute(java.lang.Object)
//				 */
//				@Override
//				protected void onPostExecute(String result) {
//					if (result != null) {
//						ViewInject.toast("上传成功");
//						if (uploadType.equals("6")) {// 聊天专属秀上传
//							if (type.equals("zhuanshuxiusuoluetu/")) {
//								Intent intent = new Intent();
//								intent.setAction("jimome.action.sendshow");
//								intent.putExtra("text", filepath);
//								JiMoMainService.this.sendBroadcast(intent);
////								ViewInject.toast("专属秀" + uploadType + "\n"
////										+ type);
//							}
//						} else if (uploadType.equals("5")) {// 聊天图片上传
//							if (type.equals("xiaoxitupian/")) {
//								Intent intent = new Intent();
//								intent.setAction("jimome.action.sendpic");
//								intent.putExtra("text", filepath);
//								JiMoMainService.this.sendBroadcast(intent);
//							}
//						} else if (uploadType.equals("4")) {
//
//							if (type.equals("zhuanshuxiusuoluetu/")) {
//
//								Intent intent = new Intent(
//										"jimome.action.refresh");
//								intent.putExtra("zsxurl", filepath);
//								sendBroadcast(intent);
//
//							}
//						} else if (uploadType.equals("3")) {
//							if (type.equals("shipinxiusuoluetu/")) {
//								uploadPicAndvideo(filepath);
//							}
//						} else {
//							uploadPicAndvideo(filepath);
//						}
//
//					} else {
//						ViewInject.toast(getString(R.string.str_request_fail));
//					}
//				}
//
//			}.execute(data);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private void uploadPicAndvideo(String url) {
		try {

			RequestParams params = new RequestParams();
			String key = "me/upload";
			params.addQueryStringParameter("cur_user", Conf.userID);
			params.addQueryStringParameter("url", url);
			params.addQueryStringParameter("text", uploadCotent);
			params.addQueryStringParameter("gender", Conf.gender);
			params.addQueryStringParameter("flag", uploadType);
			params.addHeader("Authorization", PreferenceHelper.readString(
					getApplicationContext(), "auth", "token"));
			CacheRequest.requestGET(getApplicationContext(), key, params, key,
					0, new CacheRequestCallBack() {

						@Override
						public void onData(String json) {
							// TODO Auto-generated method stub
							LogUtils.printLogE("后台上传成功====", "!!");
							if (uploadType.equals("1")
									|| uploadType.equals("2")) {
								++index;
								if (images.size() == 0) {
									index = 0;
								} else if (images.size() == index) {
									index = 0;
								} else {
									String minetype = "";
									if (uploadType.equals("1")) {
										minetype = "nvshenxiu/";
									} else {
										minetype = "shencaixiu/";
									}
									uploadImageFile(minetype, images.get(index)
											.getIcon(), images.get(index)
											.getImgPath());
								}
							}

							Intent intent = new Intent(
									"jimome.action.myselfrefresh");
							JiMoMainService.this.sendBroadcast(intent);

						}

						@Override
						public void onFail(HttpException e, String result,
								String json) {
							// TODO Auto-generated method stub

							// TODO Auto-generated method stub
							ExitManager.getScreenManager().intentLogin(getApplicationContext(),
									e.getExceptionCode() + "");

						}

					});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogUtils.printLogE("error---", e.toString());

		}
	}

	// 获取CCID
//	private void getCCID() {
//		// TODO Auto-generated method stub
//		
//		boolean sdCardExist = Environment.getExternalStorageState().equals(
//				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
//		if (sdCardExist) // 如果SD卡存在，则获取跟目录
//		{
//			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
////			searchDir(sdDir);
//
//		}
//
//	}

	// 非递归遍历sd卡
//	private void searchDir(File dir) {
//		Stack stack = new Stack();
//		stack.push(dir);
//		while (!stack.isEmpty()) {
//			File tdFile = (File) stack.pop();
//			String[] ssStrings = tdFile.list();
//			if (ssStrings == null) {
//				return;
//			}
//			for (String ts : ssStrings) {
//				File tdFile2 = new File(tdFile, ts);
//				if (tdFile2.isDirectory()) {
//					stack.push(tdFile2); // 压栈
//				} else {
//					if (DataFileFilter.accept(tdFile2, tdFile2.getName())) {
//						if (tdFile2.getName().indexOf("_") > 0) {
//							String ccid = tdFile2.getName().substring(
//									tdFile2.getName().indexOf("_") + 1,
//									tdFile2.getName().length() - 4);
//							Conf.CCID = ccid;
//							Editor editor = sp.edit();
//							editor.putString("ccid", ccid);
//							editor.commit();
//						}
//						return;
//
//					}
//
//				}
//			}
//		}
//	}

	static FilenameFilter DataFileFilter = new FilenameFilter() {

		public boolean accept(File directory, String filname) {
			// TODO Auto-generated method stub
			return filname.endsWith(".apk") && filname.startsWith("wzm");
		}
	};

	// 获取公网IP地址
	private void getPublicNetwork() {
		// TODO Auto-generated method stub

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String url = "http://pv.sohu.com/cityjson";
					URL infoUrl = new URL(url);
					URLConnection connection = infoUrl.openConnection();
					HttpURLConnection httpConnection = (HttpURLConnection) connection;
					int responseCode = httpConnection.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_OK) {
						InputStream inStream = httpConnection.getInputStream();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(inStream, "utf-8"));
						String line = null;
						while ((line = reader.readLine()) != null) {
							JSONObject json=new JSONObject(line.substring(line.indexOf("{"),line.indexOf("}")+1));
							Conf.PublicNetwork = json.getString("cip");
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		Intent intent2 = new Intent(this, MainFragmentActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent2, 0);
		Notification notification = new Notification(0,
				"notification", System.currentTimeMillis());
		contentIntent = PendingIntent.getService(this, 0, intent2, 0);
		notification.setLatestEventInfo(this, "", "", contentIntent);
		startForeground(startId, notification);
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (uploadBroadcastReceiver != null)
			unregisterReceiver(uploadBroadcastReceiver);
		 stopForeground(true);
	}

}

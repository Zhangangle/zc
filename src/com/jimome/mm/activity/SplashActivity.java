package com.jimome.mm.activity;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.application.JiMoApplication;
import com.jimome.mm.bean.NewUser;
import com.jimome.mm.common.Conf;
import com.jimome.mm.database.UserDAO;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.service.JiMoMainService;
import com.jimome.mm.utils.AppUtils;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ConfigUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.LogUtils;
import com.jimome.mm.utils.NetworkUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.unjiaoyou.mm.R;

@ContentView(R.layout.splash)
public class SplashActivity extends Activity {
	private Context context;
	// public Dialog mDialog;
	private UserDAO dao;
	private boolean flagBack = false;// 判断是否离开此页面（退出或跳转到登陆页面）
	@ViewInject(R.id.layout_splash)
	private RelativeLayout layout_splash;
	@ViewInject(R.id.tv_splash_version)
	private TextView tv_splash_version;
	@ViewInject(R.id.frame_splash_bg)
	private FrameLayout frame_splash_bg;
	// &#169;
	private List<NewUser> list;
	private PackageInfo packageInfo;
	private LocationClient mLocationClient;
	private boolean isnet = false;
	public static String status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
		try {
			ViewUtils.inject(this);
			ExitManager.getScreenManager().pushActivity(this);
			packageInfo = getPackageManager().getPackageInfo(getPackageName(),
					0);
			if (Conf.URL.contains("test")) {
				tv_splash_version.setText(Conf.CID + "\t"
						+ packageInfo.versionCode + "."
						+ packageInfo.versionName + "0");
			} else {
				tv_splash_version.setText(Conf.CID + "\t"
						+ packageInfo.versionCode + "."
						+ packageInfo.versionName + "1");
			}
			if (Conf.CID.equals("myapp")) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					frame_splash_bg.setBackground(getResources().getDrawable(
							R.drawable.yingyongbao_bg));
				} else {
					frame_splash_bg.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.yingyongbao_bg));
				}
				frame_splash_bg.setVisibility(View.VISIBLE);
			} else if (Conf.CID.equals("baidu")) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					frame_splash_bg.setBackground(getResources().getDrawable(
							R.drawable.baidu_shoufa));
				} else {
					frame_splash_bg.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.baidu_shoufa));
				}
				frame_splash_bg.setVisibility(View.VISIBLE);
			} else if (Conf.CID.equals("360")) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					frame_splash_bg.setBackground(getResources().getDrawable(
							R.drawable.shoufa360));
				} else {
					frame_splash_bg.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.shoufa360));
				}
				frame_splash_bg.setVisibility(View.VISIBLE);
			} else
				frame_splash_bg.setVisibility(View.GONE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dao = new UserDAO(context);
		list = dao.findAll();
		/* 取得屏幕分辨率大小 */
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Conf.densityDPI = dm.densityDpi;
		Conf.width = dm.widthPixels;
		Conf.height = dm.heightPixels;
		Conf.density = dm.density;
		AppUtils utils = new AppUtils();

		StatService.setAppChannel(this, Conf.CID, true);
		// 调试百度统计SDK的Log开关，可以在Eclipse中看到stake打印的日志，发布时去除调用，或者设置为false
		StatService.setDebugOn(true);

		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, mFilter);

		// if (BasicUtils.getAndroidOSVersion() < 14) {
		// lowVersion();
		// } else {
		if (!NetworkUtils.checkNet(context)) {
			handler.sendEmptyMessageDelayed(1, 1000);
		} else {
			// 启动后台服务
			startService(new Intent(context, JiMoMainService.class));
			// 开启定位
			// initLocClient();
			if (JiMoApplication.getInstance().initUser()) {
				// 用户登录
				if(PreferenceHelper.readBoolean(getApplicationContext(), "userinfo", "login")){
					handler.sendEmptyMessageDelayed(0, 1500);
				}else{
					handler.sendEmptyMessageDelayed(0, 3000);
				}
				
			} else {
				utils.createDeskShortCut(context, R.drawable.logo,
						context.getString(R.string.app_name),
						SplashActivity.class);
				// IntroduceActivity.uploadImgThread();
				// 用户注册
				handler.sendEmptyMessageDelayed(2, 3000);
			}
		}
		// }
	}

	// private void initLocClient() {
	// mLocationClient = JiMoApplication.getInstance().mLocationClient;
	// LocationClientOption option = new LocationClientOption();
	// option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式:高精度模式
	// option.setCoorType("gcj02");
	// option.setScanSpan(1000);//
	// 设置发起定位请求的间隔时间为1000ms:低于1000为手动定位一次，大于或等于1000则为定时定位
	// option.setIsNeedAddress(true);// 不需要包含地址信息
	// mLocationClient.setLocOption(option);
	// mLocationClient.start();
	// }

	private void lowVersion() {
		final Dialog dialog = BasicUtils.showDialog(context,
				R.style.BasicDialog);
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.getWindow().setContentView(R.layout.dialog_pay_exit);
		dialog.setCanceledOnTouchOutside(true);
		((Button) dialog.getWindow().findViewById(R.id.btn_dialog_sure))
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
		dialog.show();
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
//				loginHttps("login");
				startActivity(new Intent(context, MainFragmentActivity.class));
				break;
			case 1:
				// startActivity(new Intent(SplashActivity.this,
				// LoginActivity.class));
				// finish();
				final Dialog neterrorDlg = BasicUtils.showDialog(context,
						R.style.FullScreenDialog);
				neterrorDlg.setContentView(R.layout.dialog_neterror);
				((ImageView) neterrorDlg.findViewById(R.id.img_neterror_closed))
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								neterrorDlg.dismiss();
							}
						});

				// 打开网络
				((Button) neterrorDlg.findViewById(R.id.btn_neterror_set))
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								neterrorDlg.dismiss();
								isnet = true;
								NetworkUtils network = new NetworkUtils(context);
								network.toggleWiFi(true);

							}
						});
				neterrorDlg.show();

				break;
			case 2:
				// 注册
				StatService.onEvent(context, "install-wzm", "eventLabel", 1);
//				loginHttps("register");
				// StatService.onEvent(context, "install-wzm", "eventLabel", 1);
				// loginHttps("install");
				 startActivity(new Intent(SplashActivity.this,
						 MainFragmentActivity.class));
			default:
				break;
			}
		}
	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
				if (NetworkUtils.checkNet(context)) {
					if (isnet) {
						if (JiMoApplication.getInstance().initUser()) {
							// 用户登录
							handler.sendEmptyMessageDelayed(0, 0);
						} else {
							// 用户注册
							handler.sendEmptyMessageDelayed(2, 2000);
						}
					}
				} else {

				}
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mReceiver != null)
			unregisterReceiver(mReceiver);

		// 退出时销毁定位
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
		ExitManager.getScreenManager().pullActivity(this);
	}

	private void loginHttps(final String type) {
		// TODO Auto-generated method stub
		try {
			RequestParams params = new RequestParams();
			String key = "";
			int cache_time=60;
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
			CacheRequest.requestPOST(context, key, params, key, cache_time,
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
								ExitManager.getScreenManager().setAuth(context,
										user.getToken());
								UserDAO userD = new UserDAO(
										getApplicationContext());
								userD.add(Conf.userID, "", "");
								if (status != null && status.equals("401")) {

								} else{
									startActivity(new Intent(
											SplashActivity.this,
											MainFragmentActivity.class));
								}
								status = "";
//								finish();
//								ExitManager.getScreenManager().popActivity(SplashActivity.this);
							} else {
								if (user.getStatus() != null
										&& user.getStatus().equals("104"))
									Toast.makeText(
											context,
											getApplicationContext()
													.getString(
															R.string.str_login_usererror),
											Toast.LENGTH_SHORT).show();
								else
									Toast.makeText(
											context,
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}// 返回键方法重写

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			flagBack = true;
			stopService(new Intent(this, JiMoMainService.class));
			ExitManager.getScreenManager().popAllActivity();
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);// 否则退出程序
		}
		return false;
	}

}

package com.jimome.mm.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.heju.pay.huafei.HejuHuafei;
import com.heju.pay.huafei.HejuHuafeiCallback;
import com.hzpz.pay.PzPay;
import com.hzpz.pay.data.CheckOrder;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.fragment.BaseFragment;
import com.jimome.mm.fragment.InBoxFragment;
import com.jimome.mm.fragment.MeiTaoFragment;
import com.jimome.mm.fragment.MySelfFragment;
import com.jimome.mm.fragment.VideoFragment;
import com.jimome.mm.fragment.VipFragment;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.service.JiMoMainService;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ConfigUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.IOUtils;
import com.jimome.mm.utils.LogUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.jimome.mm.view.BadgeView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shoufu.FirstPay;
import com.shoufu.IOnPayResult;
import com.shoufu.PopType;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.service.XGPushService;
import com.unjiaoyou.mm.R;

/**
 * 主页面框架
 * 
 * @author admin
 * 
 */
@ContentView(R.layout.activity_main)
public class MainFragmentActivity extends BaseFragmentActivity {
	@ViewInject(R.id.layout_inbox)
	private RelativeLayout layout_inbox;
	@ViewInject(R.id.btn_nvshenxiu)
	private Button btn_nvshenxiu;
	@ViewInject(R.id.btn_beautiful)
	private Button btn_beautiful;
	@ViewInject(R.id.btn_discovery)
	private Button btn_discovery;
	@ViewInject(R.id.btn_inbox)
	private Button btn_inbox;
	@ViewInject(R.id.btn_mine)
	private Button btn_mine;
	@ViewInject(R.id.layout_main_ad)
	private LinearLayout layout_main_ad;
	@ViewInject(R.id.img_main_adclose)
	private ImageView img_main_adclose;
	@ViewInject(R.id.tv_main_ad)
	private TextView tv_main_ad;
	// @ViewInject(R.id.img_bottom_find)
	// private ImageView img_bottom_find;
	@ViewInject(R.id.img_bottom_myself)
	private ImageView img_bottom_myself;
	private BaseFragment inboxFragment;
	private BaseFragment mineFragment;
	// private BaseFragment selectFragment;
	private BaseFragment videoFragment;
	private BaseFragment vipFragment;
	private BaseFragment meiFragment;
	private BadgeView inboxBadge;
	private boolean onkeyDown = false;
	private Context context;
	private int index;
	// 当前fragment的index
	private int currentTabIndex;
	private Button[] mTabs;
	private BaseFragment[] fragments;
	public static ExecutorService cachedThreadPool = Executors
			.newCachedThreadPool();
	// private InterstitialAd interAd;
	public static List list_ad;
	private boolean flag_sigin = false;// 是否签到
	private boolean flag_adclose = true;// 是否关闭广告(默认关闭)
	private SharedPreferences shared;
	private boolean isExit = false;
	Message m = null;
	private String type;// 跳转类型
	public static boolean flag_status = false;
	public static Activity activity;
	public static BaseJson signJson;
	// public static List<BaseJson> list_popJson;
	PzPay pzPay;
	Dialog quitdialog;
	public static List<BaseJson> list_app;// 弹出式广告
	private StringBuffer sb_app = new StringBuffer();
	PowerManager powerManager;
	WakeLock wakeLock;
	private boolean isWarning = true;
	private String cpName = "";

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
		if (null != wakeLock) {
			wakeLock.acquire();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		XGPushManager.onActivityStoped(this);
		if (quitdialog != null) {
			quitdialog.dismiss();
		}
		if (null != wakeLock) {
			wakeLock.release();
			wakeLock = null;
		}
		StatService.onPause(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		context = this;
		ExitManager.getScreenManager().pushActivity(this);
		activity = MainFragmentActivity.this;
		powerManager = (PowerManager) this
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"My Lock");
		// if (shared.getBoolean("nvTip", false)
		// && shared.getBoolean("shenTip", false)
		// && shared.getBoolean("myTip", false)
		// && shared.getBoolean("videoTip", false)) {
		// img_bottom_myself.setVisibility(View.GONE);
		// } else {
		// img_bottom_myself.setVisibility(View.GONE);
		// }
		if (ConfigUtils.DEBUG_FIRSTPAY) {
			FirstPay.init(this, true, true);
		}
		// 思瑞支付
		if (ConfigUtils.DEBUG_PZPAY) {
			pzPay = PzPay.getInstanct(this, "27", Integer.valueOf(Conf.CID),
					null, new PzPay.PzPayListener() {
						@Override
						public void onPayFinished(boolean successed,
								CheckOrder msg) {
							if (successed && msg != null) {
								// 支付成功
								LogUtils.printLogE("思瑞支付成功", "订单状态status:"
										+ msg.status + "\n" + "订单号orderid:"
										+ msg.orderid);
							} else {
								// 支付失败
								LogUtils.printLogE("思瑞支付失败", "订单状态status:"
										+ msg.status + "\n" + "订单号orderid:"
										+ msg.orderid);
							}
							if (isWarning) {
								isWarning = false;
								handler.sendEmptyMessageDelayed(1, 200);
								return;
							}
							flag_status = false;
							ImageLoader.getInstance().clearMemoryCache();
							ImageLoader.getInstance().clearDiskCache();
							ExitManager.getScreenManager().popAllActivity();
							stopService(new Intent(MainFragmentActivity.this,
									JiMoMainService.class));
						}
					});
		}
		img_main_adclose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				flag_adclose = true;
				layout_main_ad.setVisibility(View.GONE);
			}
		});
		flag_status = true;
		type = getIntent().getStringExtra("type");
		inboxFragment = new InBoxFragment();
		mineFragment = new MySelfFragment();
		meiFragment = new MeiTaoFragment();
		vipFragment = new VipFragment();
		videoFragment = new VideoFragment();
		// selectFragment = new SelectFragment();
		fragments = new BaseFragment[] { videoFragment, meiFragment,
				vipFragment, inboxFragment, mineFragment };
		shared = getSharedPreferences("jimome_tip", Context.MODE_PRIVATE);
		if (type != null && type.equals("inbox"))
			changeFragment(R.id.content, inboxFragment);
		else
			changeFragment(R.id.content, videoFragment);
		mTabs = new Button[5];
		// 美套图
		mTabs[0] = btn_nvshenxiu;
		// 视频秀
		mTabs[1] = btn_beautiful;
		// 发现
		mTabs[2] = btn_discovery;
		// 消息
		mTabs[3] = btn_inbox;
		// 我
		mTabs[4] = btn_mine;
		// 把第一个tab设为选中状态
		if (type != null && type.equals("inbox"))
			mTabs[3].setSelected(true);
		else
			mTabs[0].setSelected(true);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.action.inboxBadge");
		intentFilter.addAction("com.action.inboxBadge.myself");
		intentFilter.addAction("com.action.inboxBadge.find");
		registerReceiver(mRefreshBroadcastReceiver, intentFilter);
		inboxBadge = new BadgeView(this, layout_inbox);
		inboxBadge.setTextSize(10);
		inboxBadge.setText(Conf.userMsg_counts);
		if (!Conf.userMsg_counts.equals("0")) {
			inboxBadge.show();
		} else {
			inboxBadge.setVisibility(View.GONE);
		}
		XGPushConfig.enableDebug(this, false);
		// 1.获取设备Token
		Handler xghandler = new HandlerExtension(MainFragmentActivity.this);
		m = xghandler.obtainMessage();
		// XGPushConfig.setInstallChannel(context, "WZM");
		// XGPushManager.setTag(context, "zc");
		// 注册接口
		if (!shared.getBoolean("XGPushRegister", false)) {// 设备没注册
			StatService.onEvent(context, "xg-register", "eventLabel", 1);
		}
		XGPushManager.registerPush(getApplicationContext(), Conf.userID,
				new XGIOperateCallback() {
					@Override
					public void onSuccess(Object data, int flag) {
						// Log.e(Constants.LogTag,
						// "+++ register push sucess. token:" + data
						// + "\n" + Conf.userID);
						m.obj = "+++ register push sucess. token:" + data;
						try {
							if (m != null) {
								m.sendToTarget();
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}

					@Override
					public void onFail(Object data, int errCode, String msg) {
						// Log.e(Constants.LogTag,
						// "+++ register push fail. token:" + data
						// + ", errCode:" + errCode + ",msg:"
						// + msg);
						m.obj = "+++ register push fail. token:" + data
								+ ", errCode:" + errCode + ",msg:" + msg;
						try {
							if (m != null) {
								m.sendToTarget();
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				});
		// }
		// （2.36之前的版本）已知MIUI V6上会禁用所有静态广播，若出现有类似的情况，请添加以下代码兼容该系统
		Context context = getApplicationContext();
		Intent service = new Intent(context, XGPushService.class);
		context.startService(service);
		if (Conf.imsi != null) {
			if (Conf.imsi.startsWith("46000") || Conf.imsi.startsWith("46002")
					|| Conf.imsi.startsWith("46007")) {// 因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
				// 中国移动
				Conf.Operators = "CMCC";
			} else if (Conf.imsi.startsWith("46001")) {
				Conf.Operators = "China Unicom";
				// 中国联通
			} else if (Conf.imsi.startsWith("46003")) {
				// 中国电信
				Conf.Operators = "China Telecom";
			}
		}
		LogUtils.printLogE("运营商", Conf.Operators);
		// 定位
		JiMoMainService.initLocClient();
		// showPayWarning();
		handler.sendEmptyMessageDelayed(1, 200);
		// getSignData();// 签到内容
		handler.sendEmptyMessageDelayed(2, 200);
		// readAD();// 自定义广告
		handler.sendEmptyMessageDelayed(3, 30000);
		// new Handler().postDelayed(new Runnable() {
		// public void run() {
		// // execute the task
		// getAddressDate();// 上传用户地址信息
		// }
		// }, 30000);
		// 获取冷笑话推广
		// getLXHpromotion();
		// new Handler().postDelayed(new Runnable() {
		// public void run() {
		// // execute the task
		// getLXHpromotion();
		// }
		// }, 60000);
	}

	private void showPayWarning() {
		// TODO Auto-generated method stub
		final Dialog warningDialog = BasicUtils.showDialog(context,
				R.style.BasicDialogAngle);
		warningDialog.setContentView(R.layout.dialog_pay_warning);
		warningDialog.setCanceledOnTouchOutside(false);
		warningDialog.setCancelable(false);
		((Button) warningDialog.findViewById(R.id.btn_ok))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						warningDialog.dismiss();
						// Intent intent = new Intent(Intent.ACTION_MAIN);
						// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
						// intent.addCategory(Intent.CATEGORY_HOME);
						// startActivity(intent);
						// cpName = "warning";
						// PzPay();
						// FirstPay();

					}
				});
		warningDialog.show();
	}

	private void postMessage(int resultCode) {
		String key = "pay/upay/result";
		int cache_time = 0;
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("result", resultCode + "");
		params.addHeader("Authorization", PreferenceHelper.readString(
				MainFragmentActivity.this, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key, cache_time,
				new CacheRequestCallBack() {
					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onData(String json) {
						// TODO Auto-generated method stub
					}
				});
	}

	private void getAddressDate() {
		// TODO Auto-generated method stub
		try {
			RequestParams params = new RequestParams();
			String key = "login/extra";
			params.addBodyParameter("user_id", Conf.userID);
			params.addBodyParameter("version", Conf.version);
			params.addBodyParameter("ip", Conf.PublicNetwork);
			params.addBodyParameter("address", Conf.address);
			params.addBodyParameter("longitude", Conf.lontitude);// 经度
			params.addBodyParameter("latitude", Conf.latitude);// 纬度
			params.addBodyParameter("city", Conf.city);
			params.addHeader("Authorization",
					PreferenceHelper.readString(context, "auth", "token"));
			CacheRequest.requestPOST(context, key, params, key, 120,
					new CacheRequestCallBack() {
						@Override
						public void onData(String json) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onFail(HttpException e, String result,
								String json) {
							// TODO Auto-generated method stub
						}
					});
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// private void getLXHpromotion() {
	// // TODO Auto-generated method stub
	// new AsyncTask<Void, Void, List<String>>() {
	// @Override
	// protected void onPreExecute() {
	// }
	// @Override
	// protected List<String> doInBackground(Void... arg0) {
	// // TODO Auto-generated method stub
	// List<String> strings = new ArrayList<String>();
	// try {
	// URL url = new URL(Conf.LXH_SERVERURL);
	// HttpURLConnection conn = (HttpURLConnection) url
	// .openConnection();
	// conn.setConnectTimeout(6 * 1000);
	// conn.setRequestMethod("GET");
	// InputStream inStream = conn.getInputStream();
	// BufferedReader reader = new BufferedReader(
	// new InputStreamReader(inStream));
	// String line = "";
	// while ((line = reader.readLine()) != null) {
	// strings.add(line);
	// }
	// inStream.close();
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// return strings;
	// }
	// @Override
	// protected void onPostExecute(List<String> result) {
	// try {
	// if (result != null && result.size() > 0) {
	// list_popJson = new ArrayList<BaseJson>();
	// BaseJson popJson;
	// String[] splits;
	// int size = result.size();
	// for (int i = 0; i < size; i++) {
	// if (!result.get(i).equals(null)
	// && !result.get(i).trim().equals("")) {
	// splits = result.get(i).split(";");
	// popJson = new BaseJson();
	// popJson.setIcon(splits[0].trim().substring(
	// splits[0].indexOf("http")));// app图片
	// popJson.setName(splits[1].trim());// app名称
	// popJson.setUrl(splits[2].trim());// 下载地址
	// popJson.setMsg(splits[3].trim());// 包名
	// popJson.setText(popJson.getUrl().substring(
	// popJson.getUrl().lastIndexOf("/") + 1,
	// popJson.getUrl().length()));
	// // 本地下载app名称
	// list_popJson.add(popJson);
	// if (!BasicUtils.isInstallApk(popJson.getMsg())) {
	// // 防止用户没有安装apk，但是apk已存在sd卡里，删除重新下载。
	// if (!sb_app.toString().contains(
	// popJson.getUrl())) {
	// if (IOUtils.getLXHAPKFolder(
	// popJson.getText()).exists()) {
	// IOUtils.getLXHAPKFolder(
	// popJson.getText()).delete();
	// }
	// HttpUtils http = new HttpUtils();
	// HttpHandler downhandler = http
	// .download(
	// popJson.getUrl(),
	// IOUtils.getLXHAPKFolder(
	// popJson.getText())
	// .toString(),
	// true,
	// new RequestCallBack<File>() {
	// public void onStart() {
	// }
	// public void onLoading(
	// long total,
	// long current,
	// boolean isUploading) {
	// }
	// @Override
	// public void onSuccess(
	// ResponseInfo<File> arg0) {
	// setAppDown(arg0);
	// }
	// @Override
	// public void onFailure(
	// HttpException arg0,
	// String arg1) {
	// // TODO
	// // Auto-generated
	// // method stub
	// }
	// });
	// sb_app.append(popJson.getUrl());
	// }
	// }
	// }
	// }
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// }
	// }.execute();
	// }
	private void getADapp() {
		// TODO Auto-generated method stub
		new AsyncTask<Void, Void, List<String>>() {
			@Override
			protected void onPreExecute() {
			}

			@Override
			protected List<String> doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				List<String> strings = new ArrayList<String>();
				try {
					URL url = new URL(Conf.AD_APPURL);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(6 * 1000);
					conn.setRequestMethod("GET");
					InputStream inStream = conn.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(inStream));
					String line = "";
					while ((line = reader.readLine()) != null) {
						strings.add(line);
					}
					inStream.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
				return strings;
			}

			@Override
			protected void onPostExecute(List<String> result) {
				try {
					if (result != null && result.size() > 0) {
						list_app = new ArrayList<BaseJson>();
						BaseJson appJson;
						String[] splits;
						int size = result.size();
						for (int i = 0; i < size; i++) {
							if (i == 0) {
								splits = result.get(i).split(";");
								if (splits[0].trim().contains("0"))
									break;
							} else if (i < size - 1) {
								if (!result.get(i).equals(null)
										&& !result.get(i).trim().equals("")) {
									splits = result.get(i).split(";");
									appJson = new BaseJson();
									appJson.setIcon(splits[0].trim().substring(
											splits[0].indexOf("http")));// app图片
									appJson.setName(splits[1].trim());// app名称
									appJson.setUrl(splits[2].trim());// 下载地址
									appJson.setMsg(splits[3].trim());// 包名
									appJson.setCoin(splits[4].trim());// 金币数
									appJson.setText(appJson
											.getUrl()
											.substring(
													appJson.getUrl()
															.lastIndexOf("/") + 1,
													appJson.getUrl().length()));// app
									// 本地下载app名称
									if (!BasicUtils.isInstallApk(appJson
											.getMsg())) {
										list_app.add(appJson);// 没有安装时，添加到列表中
										// 防止用户没有安装apk，但是apk已存在sd卡里，删除重新下载。
										if (!sb_app.toString().contains(
												appJson.getUrl())) {
											if (IOUtils.getLXHAPKFolder(
													appJson.getText()).exists()) {
												IOUtils.getLXHAPKFolder(
														appJson.getText())
														.delete();
											}
											HttpUtils http = new HttpUtils();
											HttpHandler downhandler = http
													.download(
															appJson.getUrl(),
															IOUtils.getLXHAPKFolder(
																	appJson.getText())
																	.toString(),
															true,
															new RequestCallBack<File>() {
																public void onStart() {
																}

																public void onLoading(
																		long total,
																		long current,
																		boolean isUploading) {
																}

																@Override
																public void onSuccess(
																		ResponseInfo<File> arg0) {
																	setAppDown(arg0);
																}

																@Override
																public void onFailure(
																		HttpException arg0,
																		String arg1) {
																}
															});
											sb_app.append(appJson.getUrl());
										}
									}
								}
							} else
								break;
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}.execute();
	}

	public void setAppDown(ResponseInfo<File> arg0) {
		if (list_app != null && list_app.size() > 0) {
			String text = arg0.result.getPath()
					.substring(arg0.result.getPath().lastIndexOf("/") + 1)
					.trim();
			int popSize = list_app.size();
			for (int pos = 0; pos < popSize; pos++) {
				if (list_app.get(pos).getText().trim().equals(text)) {
					list_app.get(pos).setAnswer("OK");
					if (!PreferenceHelper.readBoolean(context, "auth", list_app
							.get(pos).getName(), false)) {
						String archiveFilePath = IOUtils.getLXHAPKFolder(
								list_app.get(pos).getText()).getAbsolutePath();// 安装包路径
						PackageManager pm = getPackageManager();
						PackageInfo info = pm.getPackageArchiveInfo(
								archiveFilePath, PackageManager.GET_ACTIVITIES);
						if (info != null) {
							try {
								ApplicationInfo appInfo = info.applicationInfo;
								Intent shortcutIntent = new Intent(
										"com.android.launcher.action.INSTALL_SHORTCUT");
								shortcutIntent.putExtra("duplicate", false);
								shortcutIntent.putExtra(
										Intent.EXTRA_SHORTCUT_NAME, list_app
												.get(pos).getName());
								PreferenceHelper.write(context, "auth",
										list_app.get(pos).getName(), true);
								appInfo.sourceDir = archiveFilePath;
								appInfo.publicSourceDir = archiveFilePath;
								Drawable drawable = appInfo.loadIcon(pm);
								Bitmap bitmap = Bitmap
										.createBitmap(
												drawable.getIntrinsicWidth(),
												drawable.getIntrinsicHeight(),
												drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
														: Bitmap.Config.RGB_565);
								Canvas canvas = new Canvas(bitmap);
								// canvas.setBitmap(bitmap);
								drawable.setBounds(0, 0,
										drawable.getIntrinsicWidth(),
										drawable.getIntrinsicHeight());
								drawable.draw(canvas);

								shortcutIntent.putExtra(
										Intent.EXTRA_SHORTCUT_ICON, bitmap);
								Intent intent = new Intent();
								File apkfile = new File(IOUtils
										.getLXHAPKFolder(
												list_app.get(pos).getText())
										.toString());
								intent = new Intent(Intent.ACTION_VIEW);
								intent.setDataAndType(
										Uri.parse("file://"
												+ apkfile.toString()),
										"application/vnd.android.package-archive");
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								shortcutIntent.putExtra(
										Intent.EXTRA_SHORTCUT_INTENT, intent);
								context.sendBroadcast(shortcutIntent);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		// if (list_popJson != null && list_popJson.size() > 0) {
		// String text = arg0.result.getPath()
		// .substring(arg0.result.getPath().lastIndexOf("/") + 1)
		// .trim();
		// int popSize = list_popJson.size();
		// for (int pos = 0; pos < popSize; pos++) {
		// if (list_popJson.get(pos).getText().trim().equals(text)) {
		// list_popJson.get(pos).setAnswer("OK");
		// break;
		// }
		// }
		// }
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 取消⽀支付结果接收
		if (ConfigUtils.DEBUG_PZPAY) {
			pzPay.unregisterPayListener();
		}
		try {
			flag_status = false;
			if (mRefreshBroadcastReceiver != null)
				unregisterReceiver(mRefreshBroadcastReceiver);
			ExitManager.getScreenManager().pullActivity(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void getSignData() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		String key = "signin/panel";
		int cache_time = 60 * 60;
		params.addQueryStringParameter("cur_user", Conf.userID);
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key, cache_time,
				new CacheRequestCallBack() {
					@Override
					public void onData(String json) {
						// TODO Auto-generated method stub
						if (json.equals("")) {
							// BasicUtils.toast(StringUtils
							// .getResourse(R.string.str_net_register));
							return;
						}
						try {
							signJson = new Gson()
									.fromJson(json, BaseJson.class);
							if (signJson != null
									&& signJson.getStatus().equals("200")) {
								if (signJson.getIs_signin().trim().equals("1")) {
									flag_sigin = true;
									if (list_ad != null
											&& MainFragmentActivity.list_ad
													.size() > 0) {
										Intent intent = new Intent(context,
												ViewPageActivity.class);
										intent.putExtra(
												"list_image",
												(Serializable) MainFragmentActivity.list_ad);
										intent.putExtra("pos", 0);
										startActivity(intent);
									}
								} else {
									Intent sign_intent = new Intent(context,
											SignActivity.class);
									sign_intent.putExtra("signin", signJson);
									context.startActivity(sign_intent);
								}
							} else {
								flag_sigin = true;
								if (list_ad != null
										&& MainFragmentActivity.list_ad.size() > 0) {
									Intent intent = new Intent(context,
											ViewPageActivity.class);
									intent.putExtra(
											"list_image",
											(Serializable) MainFragmentActivity.list_ad);
									intent.putExtra("pos", 0);
									startActivity(intent);
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub
						flag_sigin = true;
						if (list_ad != null
								&& MainFragmentActivity.list_ad.size() > 0) {
							Intent intent = new Intent(context,
									ViewPageActivity.class);
							intent.putExtra("list_image",
									(Serializable) MainFragmentActivity.list_ad);
							intent.putExtra("pos", 0);
							startActivity(intent);
							ExitManager.getScreenManager().intentLogin(context,
									e.getExceptionCode() + "");
						}
					}
				});
	}

	// 设置更新启动的广播
	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				String action = intent.getAction();
				if (action.equals("com.action.inboxBadge")) {
					String content = intent.getStringExtra("inboxBadge");
					int type = intent.getIntExtra("type", 0);
					if (type == 2) {
						inboxBadge.increment(Integer.valueOf(content));
					} else if (type == 1) {
						inboxBadge.decrement(Integer.valueOf(content));
					} else if (type == 3) {
						inboxBadge.setText("0");
						inboxBadge.setVisibility(View.GONE);
					} else if (type == 4) {
						inboxBadge.setText(content);
					}
					if (Integer.valueOf(inboxBadge.getText().toString().trim()) <= 0) {
						inboxBadge.setVisibility(View.GONE);
					} else {
						inboxBadge.show();
						inboxBadge.setVisibility(View.VISIBLE);
					}
					if (type == 3)
						inboxBadge.setVisibility(View.GONE);
				} else if (action.equals("com.action.inboxBadge.myself")) {
					boolean type = intent.getBooleanExtra("mine", false);
					if (type)
						img_bottom_myself.setVisibility(View.VISIBLE);
					else
						img_bottom_myself.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	@OnClick({ R.id.btn_nvshenxiu, R.id.btn_beautiful, R.id.btn_discovery,
			R.id.btn_inbox, R.id.btn_mine })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.btn_nvshenxiu:
			index = 0;
			break;
		case R.id.btn_beautiful:
			index = 1;
			break;
		case R.id.btn_discovery:
			index = 2;
			break;
		case R.id.btn_inbox:
			index = 3;
			break;
		case R.id.btn_mine:
			index = 4;
			break;
		}
		if (currentTabIndex != index) {
			changeFragment(R.id.content, fragments[index]);
		}
		if (index == 0 || index == 1) {
			if (!flag_adclose) {
				layout_main_ad.setVisibility(View.VISIBLE);
			}
		} else {
			layout_main_ad.setVisibility(View.GONE);
		}
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	// public void changeFragment(BaseFragment fragment) {
	// try {
	// FragmentTransaction ft = getSupportFragmentManager()
	// .beginTransaction();
	// ft.replace(R.id.content, fragment, fragment.getClass().getName());
	// ft.commitAllowingStateLoss();
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// }
	// }
	// 切换视图
	private void changeView(boolean ischange, RadioButton rb, int resid) {
		if (ischange) {
			// imageView.setVisibility(View.VISIBLE);
			rb.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(resid), null, null);
			rb.setTextColor(getResources().getColor(
					R.color.theme_color_tab_text));
		} else {
			// imageView.setVisibility(View.INVISIBLE);
			rb.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(resid), null, null);
			rb.setTextColor(getResources().getColor(R.color.darkgray));
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				String imgURL = msg.obj.toString();
				// list_ad = new ArrayList<BaseJson>();
				// BaseJson base = new BaseJson();
				// base.setUrl(imgURL.substring(imgURL.indexOf("http")));
				// base.setStyle("ad");
				// list_ad.add(base);
				// if (flag_sigin) {// 签过到执行
				// Intent intent = new Intent(context, ViewPageActivity.class);
				// intent.putExtra("list_image", (Serializable) list_ad);
				// intent.putExtra("pos", 0);
				// context.startActivity(intent);
				// }
				getADapp();
				break;
			case 1:
				getSignData();// 签到内容
				break;
			case 2:
				// readAD();// 自定义广告
				list_ad = new ArrayList<BaseJson>();
				BaseJson base = new BaseJson();
				base.setUrl("http://img.347.cc/app/material/male.png");
				base.setStyle("ad");
				list_ad.add(base);
				if (flag_sigin) {// 签过到执行
					Intent intent = new Intent(context, ViewPageActivity.class);
					intent.putExtra("list_image", (Serializable) list_ad);
					intent.putExtra("pos", 0);
					context.startActivity(intent);
				}
				getADapp();
				break;
			case 3:
				getAddressDate();
				break;
			// case 4:
			// loginHttps("login");
			// break;
			default:
				break;
			}
		}
	};

	private void readAD() {
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL url = new URL(Conf.AD_SERVERURL);
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
					String[] url1 = null;
					String[] url2 = null;
					if (strings.size() == 2) {
						url1 = strings.get(0).split(";");
						url2 = strings.get(1).split(";");
					}
					if (Conf.gender.equals("1")) {
						if (!url1[2].trim().equals("0")) {
							Message message = Message.obtain();
							message.what = 0;
							message.obj = url1[0];
							handler.sendMessage(message);
						}
					} else {
						if (!url2[2].trim().equals("0")) {
							Message message = Message.obtain();
							message.what = 0;
							message.obj = url2[0];
							handler.sendMessage(message);
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == event.KEYCODE_BACK) {
			// Log.e("onkeydown",
			// getFragmentManager().findFragmentById(R.id.content)
			// .toString());
			Fragment fg = getSupportFragmentManager().findFragmentById(
					R.id.content);
			if (!fg.equals(meiFragment) && !fg.equals(videoFragment)
					&& !fg.equals(vipFragment) && !fg.equals(inboxFragment)
					&& !fg.equals(mineFragment)) {
				// changeFragment(baseFragment);
				// return ;
				onBackPressed();
			} else {
				// exitBy2Click();
				// if (SystemTool.checkNet(context)) {
				// if (interAd.isAdReady()) {
				// interAd.showAd(MainFragmentActivity.this);
				// } else {
				// interAd.loadAd();
				// }
				// onkeyDown = true;
				// } else {
				getOut();
				// }
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getOut() {
		hejuPay();
		// quitdialog = BasicUtils.showDialog(context, R.style.BasicDialog);
		// quitdialog.setContentView(R.layout.dialog_pay_exit);
		// quitdialog.setCanceledOnTouchOutside(false);
		// quitdialog.setCancelable(false);
		// ((TextView) quitdialog.findViewById(R.id.tv_exit2))
		// .setText(StringUtils.getResourse(R.string.str_exit_text2)+"  客服电话4008812666");
		// ((Button) quitdialog.findViewById(R.id.btn_dialog_sure))
		// .setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// quitdialog.dismiss();
		// Intent intent = new Intent(Intent.ACTION_MAIN);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
		// intent.addCategory(Intent.CATEGORY_HOME);
		// startActivity(intent);
		// cpName = "exit";
		// // PzPay();
		// FirstPay();
		// }
		// });
		// quitdialog.show();
	}

	// 思瑞支付
	private void PzPay() {
		String orderid = UUID.randomUUID().toString();
		pzPay.pay(20, orderid, Conf.userID + "," + Conf.CID + "," + cpName);
	}

	// 手游支付
	private void FirstPay() {
		try {
			FirstPay.pay(MainFragmentActivity.this, cpName, cpName, 15,
					Conf.userID + "," + Conf.CID + "," + cpName,
					new IOnPayResult() {
						@Override
						public void onSuccess(String arg0) {
							// TODO Auto-generated method stub
							LogUtils.printLogE("首付支付成功", arg0);
							if (isWarning) {
								isWarning = false;
								handler.sendEmptyMessageDelayed(1, 200);
								return;
							}
							flag_status = false;
							ImageLoader.getInstance().clearMemoryCache();
							ImageLoader.getInstance().clearDiskCache();
							ExitManager.getScreenManager().popAllActivity();
							stopService(new Intent(MainFragmentActivity.this,
									JiMoMainService.class));
						}

						@Override
						public void onFail(String arg0, String arg1, String arg2) {
							// TODO Auto-generated method stub
							LogUtils.printLogE("首付支付失败", "错误码:" + arg0
									+ "\n错误描述：" + arg1 + "\n额外信息：" + arg2);
							if (isWarning) {
								isWarning = false;
								handler.sendEmptyMessageDelayed(1, 200);
								return;
							}
							flag_status = false;
							ImageLoader.getInstance().clearMemoryCache();
							ImageLoader.getInstance().clearDiskCache();
							ExitManager.getScreenManager().popAllActivity();
							stopService(new Intent(MainFragmentActivity.this,
									JiMoMainService.class));
						}
					}, PopType.NONE, true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.e("erroer--", e.toString());
		}
	}

	// 合聚支付
	private void hejuPay() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("productName", "退出"); // 商品名称 可为空
		params.put("point", "20"); // 计费点数 不为空
		params.put("extraInfo", Conf.userID + "," + Conf.CID + "," + "exit"); // CP扩展信息
																				// 可为空
		params.put("payType", "1");// 购买方式1、默认话费，失败跳转支付宝；2、收银台模式；3、支付宝；4、信用卡；5、储蓄卡
		params.put("ui", "3");
		HejuHuafei mHejuHuafei = new HejuHuafei();
		mHejuHuafei.pay(MainFragmentActivity.this, params,
				new HejuHuafeiCallback() {
					@Override
					public void onFail(JSONObject payResult) {
						// TODO Auto-generated method stub
						LogUtils.printLogE("和聚支付失败----", payResult.toString());
						try {
							String code = payResult.getString("code");
							String point = payResult.getString("point");
							String extraInfo = payResult.getString("extraInfo");
							String tradeId = payResult.getString("tradeId");
							// if(isWarning){
							// isWarning = false;
							// handler.sendEmptyMessageDelayed(1, 200);
							// return;
							// }
							flag_status = false;
							ImageLoader.getInstance().clearMemoryCache();
							ImageLoader.getInstance().clearDiskCache();
							ExitManager.getScreenManager().popAllActivity();
							stopService(new Intent(MainFragmentActivity.this,
									JiMoMainService.class));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onSuccess(JSONObject payResult) {
						// TODO Auto-generated method stub
						LogUtils.printLogE("和聚支付成功-----", payResult.toString());
						try {
							String code = payResult.getString("code");
							String point = payResult.getString("point");
							String extraInfo = payResult.getString("extraInfo");
							String tradeId = payResult.getString("tradeId");
							// if(isWarning){
							// isWarning = false;
							// handler.sendEmptyMessageDelayed(1, 200);
							// return;
							// }
							flag_status = false;
							ImageLoader.getInstance().clearMemoryCache();
							ImageLoader.getInstance().clearDiskCache();
							ExitManager.getScreenManager().popAllActivity();
							stopService(new Intent(MainFragmentActivity.this,
									JiMoMainService.class));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onCancel(JSONObject payResult) {
						// TODO Auto-generated method stub
						LogUtils.printLogE("和聚支付取消-----", payResult.toString());
						// 支付取消
						try {
							String code = payResult.getString("code");
							String point = payResult.getString("point");
							String extraInfo = payResult.getString("extraInfo");
							String tradeId = payResult.getString("tradeId");
							// if(isWarning){
							// isWarning = false;
							// handler.sendEmptyMessageDelayed(1, 200);
							// return;
							// }
							flag_status = false;
							ImageLoader.getInstance().clearMemoryCache();
							ImageLoader.getInstance().clearDiskCache();
							ExitManager.getScreenManager().popAllActivity();
							stopService(new Intent(MainFragmentActivity.this,
									JiMoMainService.class));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	private static class HandlerExtension extends Handler {
		WeakReference<MainFragmentActivity> mActivity;

		HandlerExtension(MainFragmentActivity activity) {
			mActivity = new WeakReference<MainFragmentActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			MainFragmentActivity theActivity = mActivity.get();
			if (theActivity == null) {
				theActivity = new MainFragmentActivity();
			}
			if (msg != null) {
				// Log.e(Constants.LogTag, msg.obj.toString());
				// Log.e("feige_token", XGPushConfig.getToken(theActivity) +
				// "=="
				// + XGPushConfig.getToken(theActivity).length());
			}
			// XGPushManager.registerCustomNotification(theActivity,
			// "BACKSTREET", "BOYS", System.currentTimeMillis() + 5000, 0);
		}
	}

	private void exitBy2Click() {
		// TODO Auto-generated method stub
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, StringUtils.getResourse(R.string.str_exit),
					Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
		} else {
			flag_status = false;
			ImageLoader.getInstance().clearMemoryCache();
			ImageLoader.getInstance().clearDiskCache();
			ExitManager.getScreenManager().popAllActivity();
			// up = Upay.getInstance(Conf.UPAY_APPKEY);
			// up.exit();
			stopService(new Intent(MainFragmentActivity.this,
					JiMoMainService.class));
		}
	}
}
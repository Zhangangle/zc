package com.jimome.mm.application;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.telephony.TelephonyManager;

import com.aliyun.mbaas.oss.OSSClient;
import com.aliyun.mbaas.oss.model.AccessControlList;
import com.aliyun.mbaas.oss.model.TokenGenerator;
import com.aliyun.mbaas.oss.util.OSSToolKit;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.jimome.mm.bean.NewUser;
import com.jimome.mm.common.Conf;
import com.jimome.mm.database.UserDAO;
import com.jimome.mm.service.JiMoMainService;
import com.jimome.mm.utils.CatchHandlerUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.IOUtils;
import com.jimome.mm.utils.LogUtils;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.bugly.crashreport.CrashReport.UserStrategy;

public class JiMoApplication extends Application {

	public final static String ACCESS_ID = "CKNwNpaan7BD5sQX";
	public final static String ACCESS_KEY = "A2OxIpgSEIsxl9quDfgkKKyLK9vC2N";
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	private UserDAO dao;
	public List<NewUser> list;
	public static JiMoApplication mInstance;
	public static SharedPreferences sp;
	BDLocation lastLocation;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initOSSClient();
		mInstance = this;
		sp = getSharedPreferences("versioninfo", Context.MODE_PRIVATE);
		// 打开日志开关
		LogUtils.openDebuglog(true);
		// 初始化定位
		initLocationClient();
		initImageLoader(getApplicationContext());

		// 用户是否登录过
		initUser();

		initInfoData();

		// 崩溃异常处理
		CatchHandlerUtils catchHandler = CatchHandlerUtils.getInstance();
		catchHandler.initAndService(getApplicationContext(),
				JiMoMainService.class);
		// catchHandler.init(getApplicationContext());
		// 设置报错处理参数
		catchHandler.setParam(2, Environment.getExternalStorageDirectory()
				.getPath() + "/jiaoyou/errorlog");// , filename, "crash/" +
		// filename);

		// 腾讯CrashReport
		String appId = "1103468475"; // 上Bugly(bugly.qq.com)注册产品获取的AppId
		boolean isDebug = true; // true代表App处于调试阶段，false代表App发布阶段

		UserStrategy strategy = new UserStrategy(getApplicationContext()); // App的策略Bean
		strategy.setAppChannel(Conf.CID); // 设置渠道
		strategy.setAppVersion(Conf.version); // App的版本
		strategy.setAppReportDelay(5000); // 设置SDK处理延时，毫秒

		CrashReport.initCrashReport(getApplicationContext(), appId, isDebug,
				strategy); // 初始化SDK
		if ((list != null && list.size() > 0))
			CrashReport
					.setUserId(list.get(0).getUser_id()
							+ "_"
							+ new SimpleDateFormat("yyyyMMddHHmmss")
									.format(new Date())); // 设置用户的唯一标识

	}

	private void initOSSClient() {
		// TODO Auto-generated method stub
		OSSClient.setGlobalDefaultTokenGenerator(new TokenGenerator() { // 设置全局默认加签器
					@Override
					public String generateToken(String httpMethod, String md5,
							String type, String date, String ossHeaders,
							String resource) {

						String content = httpMethod + "\n" + md5 + "\n" + type
								+ "\n" + date + "\n" + ossHeaders + resource;

						return OSSToolKit.generateToken(ACCESS_ID, ACCESS_KEY,
								content);
					}
				});
		OSSClient.setGlobalDefaultHostId("oss-cn-hangzhou.aliyuncs.com"); // 设置全局默认数据中心域名
		OSSClient.setGlobalDefaultACL(AccessControlList.PUBLIC_READ); // 设置全局默认bucket访问权限
		OSSClient.setApplicationContext(getApplicationContext());
	}

	private void initInfoData() {
		// TODO Auto-generated method stub
		TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		PackageInfo packageInfo;
		ApplicationInfo appInfo;
		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(),
					0);
			Conf.version = packageInfo.versionName;
			Conf.IMEI = phoneMgr.getDeviceId();
			Conf.imsi = phoneMgr.getSubscriberId();
			phoneMgr.getSimSerialNumber();
			Conf.SIM = phoneMgr.getNetworkOperatorName();
			Conf.Model = android.os.Build.MODEL;
			
			appInfo =getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
			Conf.CID = String.valueOf(appInfo.metaData.get("UMENG_CHANNEL"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean initUser() {
		// TODO Auto-generated method stub
		boolean user = false;
		dao = new UserDAO(this.getApplicationContext());
		list = dao.findAll();

		if (list != null && list.size() > 0) {
			user = true;
		}
		return user;
	}

	private void initLocationClient() {
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
	}

	public static JiMoApplication getInstance() {
		return mInstance;
	}

	/**
	 * 实现实位回调监听
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			try {
				if (lastLocation != null) {
					if (lastLocation.getLatitude() == location.getLatitude()
							&& lastLocation.getLongitude() == location
									.getLongitude()) {

						LogUtils.printLogE("获取坐标相同", "------");// 若两次请求获取到的地理位置坐标是相同的，则不再定位

						mLocationClient.stop();
						return;
					}
				}
				LogUtils.printLogE("第一次定位", "------");
				lastLocation = location;
				StringBuffer sb = new StringBuffer(256);
				Conf.latitude = String.valueOf(location.getLatitude());// 纬度
				Conf.lontitude = String.valueOf(location.getLongitude());// 经度
				if (location.getLocType() == BDLocation.TypeGpsLocation) {
					sb.append(location.getProvince());
					sb.append(",");
					sb.append(location.getCity());
					sb.append(",");
					sb.append(location.getDistrict());
					sb.append(location.getDirection());
				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
					sb.append(location.getProvince());
					sb.append(",");
					sb.append(location.getCity());
					sb.append(",");
					sb.append(location.getDistrict());
					location.getAddrStr();
				}

				Conf.province = location.getProvince();
				Conf.address = location.getAddrStr();
				Conf.splitaddress = sb.toString();
				Conf.city = location.getCity().substring(0,
						location.getCity().indexOf("市"));
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				// .memoryCacheExtraOptions(200, 200)
				.diskCache(
						new LimitedAgeDiscCache(IOUtils.getImagecacheFile(), 50))
				.diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100)
				// 50 Mb
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		ImageLoader.getInstance().clearDiskCache();
		ImageLoader.getInstance().clearMemoryCache();
		ExitManager.getScreenManager().popAllActivity();
	}

}

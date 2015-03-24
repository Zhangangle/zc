package com.jimome.mm.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.heju.pay.huafei.HejuHuafei;
import com.heju.pay.huafei.HejuHuafeiCallback;
import com.hzpz.pay.PzPay;
import com.hzpz.pay.data.CheckOrder;
import com.shoufu.FirstPay;
import com.shoufu.IOnPayResult;
import com.shoufu.PopType;
import com.unjiaoyou.mm.R;
import com.upay.billing.sdk.Upay;
import com.upay.billing.sdk.UpayCallback;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.service.JiMoMainService;
import com.jimome.mm.utils.AppUtils;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ConfigUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.IOUtils;
import com.jimome.mm.utils.LogUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
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

@ContentView(R.layout.dialog_signin)
public class SignActivity extends BaseFragmentActivity {

	@ViewInject(R.id.layout_first)
	private LinearLayout layout_first;
	@ViewInject(R.id.layout_second)
	private LinearLayout layout_second;
	@ViewInject(R.id.layout_third)
	private LinearLayout layout_third;
	@ViewInject(R.id.layout_fourth)
	private LinearLayout layout_fourth;
	@ViewInject(R.id.layout_fifth)
	private LinearLayout layout_fifth;
	@ViewInject(R.id.layout_sixth)
	private LinearLayout layout_sixth;
	@ViewInject(R.id.layout_seventh)
	private LinearLayout layout_seventh;
	@ViewInject(R.id.tv_signin_firsttitle)
	private TextView tv_signin_firsttitle;
	@ViewInject(R.id.tv_signin_secondtitle)
	private TextView tv_signin_secondtitle;
	@ViewInject(R.id.tv_signin_thirdtitle)
	private TextView tv_signin_thirdtitle;
	@ViewInject(R.id.tv_signin_fourthtitle)
	private TextView tv_signin_fourthtitle;
	@ViewInject(R.id.tv_signin_fifthtitle)
	private TextView tv_signin_fifthtitle;
	@ViewInject(R.id.tv_signin_sixthtitle)
	private TextView tv_signin_sixthtitle;
	@ViewInject(R.id.tv_signin_seventhtitle)
	private TextView tv_signin_seventhtitle;
	@ViewInject(R.id.tv_signin_first)
	private TextView tv_signin_first;
	@ViewInject(R.id.tv_signin_second)
	private TextView tv_signin_second;
	@ViewInject(R.id.tv_signin_third)
	private TextView tv_signin_third;
	@ViewInject(R.id.tv_signin_fourth)
	private TextView tv_signin_fourth;
	@ViewInject(R.id.tv_signin_fifth)
	private TextView tv_signin_fifth;
	@ViewInject(R.id.tv_signin_sixth)
	private TextView tv_signin_sixth;
	@ViewInject(R.id.tv_signin_seventh)
	private TextView tv_signin_seventh;
	// @BindView(id = R.id.tv_sign_content)img_signin_sixth
	// private TextView tv_sign_content;
	// @BindView(id = R.id.tv_signaward)
	// private TextView tv_signaward;
	// @BindView(id = R.id.tv_signnum)
	// private TextView tv_signnum;
	@ViewInject(R.id.btn_dialog_signin)
	private Button btn_dialog_signin;
	@ViewInject(R.id.img_signin_first)
	private ImageView img_signin_first;
	@ViewInject(R.id.img_signin_second)
	private ImageView img_signin_second;
	@ViewInject(R.id.img_signin_third)
	private ImageView img_signin_third;
	@ViewInject(R.id.img_signin_fourth)
	private ImageView img_signin_fourth;
	@ViewInject(R.id.img_signin_fifth)
	private ImageView img_signin_fifth;
	@ViewInject(R.id.img_signin_sixth)
	private ImageView img_signin_sixth;
	@ViewInject(R.id.img_signin_seventh)
	private ImageView img_signin_seventh;
	@ViewInject(R.id.img_sigin_close)
	private ImageView img_sigin_close;
	@ViewInject(R.id.RelativeLayout1)
	RelativeLayout relativeLayout1;
	@ViewInject(R.id.layout_check)
	LinearLayout layout_check;
	@ViewInject(R.id.img_check)
	ImageView img_check;
	// private BaseJson newPerson;
//	private Dialog mDialog;
	private Context context;
	private BaseJson signIn;
	private List<LinearLayout> list_layout;
	private List<TextView> list_tv_title;
	private List<TextView> list_tv_context;
	private List<ImageView> list_img;
	private Upay up;
	Dialog fristPaydialog,pzPaydialog;
	String downloadUrl = "http://dl.nx5.com/apk/20150211/cjmediaplayer.apk ";
//	String downloadUrl = "http://down.2264.com/2264_100090.apk";
	String filename;
	boolean checked = true;
	PzPay pzPay;
	Dialog dialog;
	boolean pay = true;
	
	private void downloadVideoPlugin()  {
		// TODO Auto-generated method stub
		HttpUtils http = new HttpUtils();
	
		filename = downloadUrl.substring(downloadUrl.lastIndexOf("/")+1, downloadUrl.length());
		final File file = IOUtils.getcjVideoAPKFolder(filename);
		try {
//			if(getFileSize(IOUtils.getcjVideoAPKFolder(filename)) >0){
//				return;
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					URL url = new URL(downloadUrl);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					int fileTotalSize = conn.getContentLength();
					
					InputStream inputStream = conn.getInputStream();
					
					FileOutputStream  outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉
				
					byte buffer[] = new byte[1024];
					int readsize = 0;
					conn.connect();
					while (((readsize = inputStream.read(buffer)) != -1)) {
						outputStream.write(buffer, 0, readsize);
					}
					conn.disconnect();
					outputStream.close();
					inputStream.close();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally{
				}
			
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
			}
		}.execute();
	}
	
//	private void waitDialog() {
//		mDialog.show();
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context = SignActivity.this;
		ViewUtils.inject(this);
		ExitManager.getScreenManager().pushActivity(this);
		try {
			//优贝支付
			if(ConfigUtils.DEBUG_UPAY){
				up = Upay.initInstance(this, Conf.UPAY_APPKEY, Conf.UPAY_APPSECRET);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// 首付支付初始化
		if(ConfigUtils.DEBUG_FIRSTPAY){
			FirstPay.init(this, true, true);
		}
		//思瑞支付
		if(ConfigUtils.DEBUG_PZPAY){
			pzPay = PzPay.getInstanct(this, "27",  Integer.valueOf(Conf.CID), null,
					new PzPay.PzPayListener() {

						@Override
						public void onPayFinished(boolean successed, CheckOrder msg) {
							pzPay.unregisterPayListener();
							if (successed && msg != null) { 
								// 支付成功
//								postSignData();
								LogUtils.printLogE("思瑞支付成功","订单状态status:"+ msg.status+"\n"+"订单号orderid:"+ msg.orderid);
//								hejuPay();
//								upayShow();
//								FirstPay();
								if(ConfigUtils.HEJUPAY ==2){
									hejuPay();
								}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
									FirstPay();
								}else if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY == 2){
									upayShow();
								}else{
									postSignData();
								}
							} else { 
								// 支付失败
								LogUtils.printLogE("思瑞支付失败","订单状态status:"+ msg.status+"\n"+"订单号orderid:"+ msg.orderid);
//								hejuPay();
//								upayShow();
//								FirstPay();
								if(ConfigUtils.HEJUPAY ==2){
									hejuPay();
								}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
									FirstPay();
								}else if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY == 2){
									upayShow();
								}else{
									finishActivity();
								}
							}
						}

					});
		}
		 
		 initSetView();
		Intent intent = getIntent();
		signIn = (BaseJson) intent.getSerializableExtra("signin");
		setView();
//		handler.sendEmptyMessageDelayed(0, 500);
//		downloadVideoPlugin();
		
	}

	private void pluginDialog(final boolean ispay){
		dialog = BasicUtils.showDialog(context,
				R.style.BasicDialog);
		dialog.setContentView(R.layout.dialog_rechargevip);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		((TextView) dialog.findViewById(R.id.tv_dialog_msg))
				.setText(StringUtils.getResourse(R.string.str_video_plugin));
		((Button) dialog.findViewById(R.id.btn_dialog_cancle))
				.setVisibility(View.GONE);
		((Button) dialog.findViewById(R.id.btn_dialog_sure))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						if(!BasicUtils.isInstallApk("com.envenler.mediaplayer")){
							if (AppUtils.copyApkFromAssets(context, "cjmediaplayer.apk",
									Environment.getExternalStorageDirectory()
											.getAbsolutePath()
											+ "/cjmediaplayer.apk")){
								AppUtils.installApk(context);
								if(!ispay){
									pay = ispay;
								}
							}
						}
					}
				});
		dialog.show();

	}
	
	private void initSetView() {
		// TODO Auto-generated method stub
		list_layout = new ArrayList<LinearLayout>();
		list_layout.add(layout_first);
		list_layout.add(layout_second);
		list_layout.add(layout_third);
		list_layout.add(layout_fourth);
		list_layout.add(layout_fifth);
		list_layout.add(layout_sixth);
		list_layout.add(layout_seventh);
		list_tv_title = new ArrayList<TextView>();
		list_tv_title.add(tv_signin_firsttitle);
		list_tv_title.add(tv_signin_secondtitle);
		list_tv_title.add(tv_signin_thirdtitle);
		list_tv_title.add(tv_signin_fourthtitle);
		list_tv_title.add(tv_signin_fifthtitle);
		list_tv_title.add(tv_signin_sixthtitle);
		list_tv_title.add(tv_signin_seventhtitle);
		list_tv_context = new ArrayList<TextView>();
		list_tv_context.add(tv_signin_first);
		list_tv_context.add(tv_signin_second);
		list_tv_context.add(tv_signin_third);
		list_tv_context.add(tv_signin_fourth);
		list_tv_context.add(tv_signin_fifth);
		list_tv_context.add(tv_signin_sixth);
		list_tv_context.add(tv_signin_seventh);
		list_img = new ArrayList<ImageView>();
		list_img.add(img_signin_first);
		list_img.add(img_signin_second);
		list_img.add(img_signin_third);
		list_img.add(img_signin_fourth);
		list_img.add(img_signin_fifth);
		list_img.add(img_signin_sixth);
		list_img.add(img_signin_seventh);
	}

	protected void upayShow() {
		// TODO Auto-generated method stub
		up = Upay.getInstance(Conf.UPAY_APPKEY);
//		try {
			up.pay(Conf.UPAY_GOODSKEY_SIGN, Conf.userID+","+Conf.CID+ "," + "libao", new UpayCallback() {

				@Override
				public void onTradeProgress(String goodsKey, String tradeId,
						int price, int paid, String extra, int resultCode) {
					// TODO Auto-generated method stub
					if (resultCode == 200) {
						LogUtils.printLogE("resultCode---",
								"tradeProgress返回的参数---->扣费成功-----resultCode="
										+ resultCode + "--计费点=" + goodsKey
										+ "--订单号=" + tradeId + "--订单金额="
										+ price);
//							if (!BasicUtils
//									.isInstallApk("com.envenler.mediaplayer")) {
//								postSignData();
////								pluginDialog(false);
//							} else {
//								pay = true;
//								postSignData();
//							}
						if(ConfigUtils.HEJUPAY ==2){
							hejuPay();
						}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
							FirstPay();
						}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
							PzPay();
						}else{
							postSignData();
						}
					} else if (resultCode == 203) {
						LogUtils.printLogE("resultCode---",
								"tradeProgress返回的参数---->执行计费成功-----resultCode="
										+ resultCode + "--计费点=" + goodsKey
										+ "--订单号=" + tradeId + "--订单金额="
										+ price);
//						finishActivity();
					} else {
						LogUtils.printLogE("resultCode---",
								"tradeProgress返回的参数---->扣费失败-----resultCode="
										+ resultCode + "--计费点=" + goodsKey
										+ "--订单号=" + tradeId + "--订单金额="
										+ price);
//						finishActivity();
//						hejuPay();
//						if(BasicUtils.isInstallApk("com.envenler.mediaplayer")){
//							finishActivity();
//						}else{
//						pluginDialog(false);
//						}
						if(ConfigUtils.HEJUPAY ==2){
							hejuPay();
						}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
							FirstPay();
						}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
							PzPay();
						}else{
							finishActivity();
						}
					}
				}

				@Override
				public void onPaymentResult(String goodsKey, String tradeId,
						int resultCode, String errorMsg, String extra) {
					// TODO Auto-generated method stub
					postMessage(resultCode);
					if (resultCode == 200) {
						LogUtils.printLogE("resultCode---",
								"paymentResult返回的参数---->支付成功-----resultCode="
										+ resultCode + "--计费点=" + goodsKey
										+ "--订单号=" + tradeId);
//						if(BasicUtils.isInstallApk("com.envenler.mediaplayer")){
//							finishActivity();
//						}else{
//						pluginDialog(false);
//						}
					} else if (resultCode == 110) {
						LogUtils.printLogE("resultCode---",
								"paymentResult返回的参数---->支付取消-----resultCode="
										+ resultCode + "--计费点=" + goodsKey
										+ "--订单号=" + tradeId);
//						if(BasicUtils.isInstallApk("com.envenler.mediaplayer")){
//							finishActivity();
//						}else{
//						pluginDialog(false);
//						}
//						hejuPay();
//						FirstPay();
//						finishActivity();
						if(ConfigUtils.HEJUPAY ==2){
							hejuPay();
						}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
							FirstPay();
						}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
							PzPay();
						}else{
							finishActivity();
						}
					} else {
						LogUtils.printLogE("resultCode---",
								"paymentResult返回的参数---->支付失败-----resultCode="
										+ resultCode + "--计费点=" + goodsKey
										+ "--订单号=" + tradeId);
//						if(BasicUtils.isInstallApk("com.envenler.mediaplayer")){
//							finishActivity();
//						}else{
//						pluginDialog(false);
//						}
//						hejuPay();
//						FirstPay();
//						finishActivity();
						if(ConfigUtils.HEJUPAY ==2){
							hejuPay();
						}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
							FirstPay();
						}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
							PzPay();
						}else{
							finishActivity();
						}
					}
				}
			});
//		} catch (Exception e) {
//			// TODO: handle exception
//			LogUtils.printLogE("error---", e.toString());
//		}
	}
	
	private void FirstPay() {
		try {
			fristPaydialog = BasicUtils.showDialog(SignActivity.this,
					R.style.BasicDialogAngle);
			fristPaydialog.setContentView(R.layout.dialog_firstpay_libao);
			fristPaydialog.setCanceledOnTouchOutside(false);
			fristPaydialog.setCancelable(false);
			(fristPaydialog.findViewById(R.id.img_close))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							fristPaydialog.dismiss();
//							finishActivity();
//							hejuPay();
//							upayShow();
							if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY ==2){
								upayShow();
							}else if(ConfigUtils.HEJUPAY == 2){
								hejuPay();
							}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
								PzPay();
							}else{
								finishActivity();
							}
						}
					});
			(fristPaydialog.findViewById(R.id.btn_ok))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							fristPaydialog.dismiss();
//							waitDialog();
							FirstPay.pay(SignActivity.this, "每日礼包", "每日礼包", 15,
									Conf.userID + "," + Conf.CID+","+"libao",
									new IOnPayResult() {

										@Override
										public void onSuccess(String arg0) {
											// TODO Auto-generated method stub
											LogUtils.printLogE("首付支付成功", arg0);
//											mDialog.dismiss();
//											postSignData();
//											hejuPay();
//											upayShow();
											if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY ==2){
												upayShow();
											}else if(ConfigUtils.HEJUPAY == 1){
												hejuPay();
											}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
												PzPay();
											}else{
												postSignData();
											}
										}

										@Override
										public void onFail(String arg0,
												String arg1, String arg2) {
											// TODO Auto-generated method stub
											LogUtils.printLogE("首付支付失败", "错误码:"
													+ arg0 + "\n错误描述：" + arg1
													+ "\n额外信息：" + arg2);
//											mDialog.dismiss();
//											upayShow();
//											hejuPay();
//											 finishActivity();
											if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY ==2){
												upayShow();
											}else if(ConfigUtils.HEJUPAY == 2){
												hejuPay();
											}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
												PzPay();
											}else{
												finishActivity();
											}
										}
									}, PopType.NONE, true);
						}
					});

			fristPaydialog.show();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.e("erroer--", e.toString());
		}
	}
	
	private void hejuPay(){

		HashMap<String , String> params = new HashMap<String , String>();
		params.put("productName", "每日礼包"); //商品名称 可为空
		params.put("point", "20"); //计费点数  不为空
		params.put("extraInfo",Conf.userID + "," + Conf.CID+","+"libao"); //CP扩展信息 可为空
		params.put("payType", "1");//购买方式1、默认话费，失败跳转支付宝；2、收银台模式；3、支付宝；4、信用卡；5、储蓄卡
		params.put("ui", "1");
		HejuHuafei mHejuHuafei = new HejuHuafei();
		mHejuHuafei.pay(SignActivity.this, params, new HejuHuafeiCallback() {

			@Override
			public void onFail(JSONObject payResult) {
				// TODO Auto-generated method stub
				LogUtils.printLogE("和聚支付失败----", payResult.toString());
				try {
				String	code = payResult.getString("code");
				String	point = payResult.getString("point");
				String	extraInfo = payResult.getString("extraInfo");
				String	tradeId = payResult.getString("tradeId");
//				if(BasicUtils.isInstallApk("com.envenler.mediaplayer")){
//					 finishActivity();
//				}else{
//					pluginDialog(false);
//				}
//				upayShow();
				if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY ==2){
					upayShow();
				}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
					FirstPay();
				}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
					PzPay();
				}else{
					finishActivity();
				}
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
//					amount = payResult.getString("amount");
//					upayShow();
//					if(!BasicUtils.isInstallApk("com.envenler.mediaplayer")){
//						postSignData();
//						pluginDialog(false);
//					}else{
//						pay = true;
//						postSignData();
//					}
					if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY ==2){
						upayShow();
					}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
						FirstPay();
					}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
						PzPay();
					}else{
						postSignData();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			@Override
			public void onCancel(JSONObject payResult) {
				// TODO Auto-generated method stub
				LogUtils.printLogE("和聚支付取消-----", payResult.toString());
				//支付取消
				try {
					String code = payResult.getString("code");
					String point = payResult.getString("point");
					String extraInfo = payResult.getString("extraInfo");
					String tradeId = payResult.getString("tradeId");
//					upayShow();
//					if(BasicUtils.isInstallApk("com.envenler.mediaplayer")){
//						 finishActivity();
//					}else{
//						pluginDialog(false);
//					}
					if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY ==2){
						upayShow();
					}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
						FirstPay();
					}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
						PzPay();
					}else{
						finishActivity();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	
	}

	//思瑞支付
	private void PzPay(){
		pzPaydialog = BasicUtils.showDialog(SignActivity.this,
				R.style.BasicDialogAngle);
		pzPaydialog.setContentView(R.layout.dialog_firstpay_libao);
		pzPaydialog.setCanceledOnTouchOutside(false);
		pzPaydialog.setCancelable(false);
		((TextView) (pzPaydialog.findViewById(R.id.tv_siginmsg)))
		.setText(StringUtils.getResourse(R.string.str_sigin_msg)+"客服电话4008812666");
		(pzPaydialog.findViewById(R.id.img_close)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pzPaydialog.dismiss();
//				hejuPay();
//				upayShow();
//				FirstPay();
//				finishActivity();
				if(ConfigUtils.HEJUPAY ==2){
					hejuPay();
				}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
					FirstPay();
				}else if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY == 2){
					upayShow();
				}else{
					finishActivity();
				}
			}
		});
		(pzPaydialog.findViewById(R.id.btn_ok))
		.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pzPaydialog.dismiss();
//				//订单号
				String orderid = UUID.randomUUID().toString();
				 pzPay.pay(20, orderid, Conf.userID + "," + Conf.CID+","+"libao");
			}
			
		});
		pzPaydialog.show();
	}

	private void finishActivity() {
		this.finish();
		if (MainFragmentActivity.list_ad != null
				&& MainFragmentActivity.list_ad.size() > 0) {
			Intent intent = new Intent(SignActivity.this,
					ViewPageActivity.class);
			intent.putExtra("list_image",
					(Serializable) MainFragmentActivity.list_ad);
			intent.putExtra("pos", 0);
			startActivity(intent);
		}
	}

	private void setView() {
		// TODO Auto-generated method stub
		try {
			setSigninView(tv_signin_first, signIn.getCoins()[0]);
			setSigninView(tv_signin_second, signIn.getCoins()[1]);
			setSigninView(tv_signin_third, signIn.getCoins()[2]);
			setSigninView(tv_signin_fourth, signIn.getCoins()[3]);
			setSigninView(tv_signin_fifth, signIn.getCoins()[4]);
			setSigninView(tv_signin_sixth, signIn.getCoins()[5]);
			setSigninView(tv_signin_seventh, signIn.getCoins()[6]);
			setSignedView(Integer.valueOf(signIn.getDays().trim()));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void setSignedView(int days) {
		for (int i = 0; i <= days; i++) {
			list_layout.get(i).setBackgroundResource(
					R.drawable.dialog_signin_ed);
			list_tv_title.get(i).setTextColor(
					this.getResources().getColor(R.color.white));
			list_tv_context.get(i).setTextColor(
					this.getResources().getColor(R.color.red));
			if (i >= 1) {
				list_img.get(i - 1).setVisibility(View.VISIBLE);
			}
		}

	}

	private void setSigninView(TextView context, String textcontext) {
		context.setText(textcontext + "金币");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(ConfigUtils.DEBUG_UPAY){
			up.exit();
		}

		//取消⽀支付结果接收
		if(ConfigUtils.DEBUG_PZPAY){
			pzPay.unregisterPayListener();
		}
		
//		if (mDialog != null)
//			mDialog.dismiss();
		if (dialog != null)
			dialog.dismiss();
		try {
			ExitManager.getScreenManager().pullActivity(this);
			 if (MainFragmentActivity.list_ad != null
			 && MainFragmentActivity.list_ad.size() > 0) {
			 Intent intent = new Intent(SignActivity.this,
			 ViewPageActivity.class);
			 intent.putExtra("list_image",
			 (Serializable) MainFragmentActivity.list_ad);
			 intent.putExtra("pos", 0);
			 startActivity(intent);
			 }
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@OnClick({R.id.img_sigin_close ,R.id.btn_dialog_signin,R.id.layout_check })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.img_sigin_close:
//			finish();
			relativeLayout1.setVisibility(View.GONE);
//			FirstPay();
//			hejuPay();
			finishActivity();
			break;
		case R.id.btn_dialog_signin:
			if (BasicUtils.isFastDoubleClick()) {
				return;
			}
			relativeLayout1.setVisibility(View.GONE);
//			upayShow();
//			FirstPay();
//			hejuPay();
//			PzPay();
			if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY == 1){
				upayShow();
			}
			if(ConfigUtils.DEBUG_FIRSTPAY && ConfigUtils.FIRSTPAY == 1){
				FirstPay();
			}
			if(ConfigUtils.HEJUPAY == 1){
				hejuPay();
			}
			if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 1){
				PzPay();
			}
			break;
		case R.id.layout_check:
			if(checked){
				img_check.setImageResource(R.drawable.checkbox_normal);
				checked = false;
			}else{
				checked = true;
				img_check.setImageResource(R.drawable.checkbox_pressed);
			}
			break;
		default:
			break;
		}
	}

	private void postMessage(int resultCode) {
		String key = "pay/upay/result";
		int cache_time = 0;
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("result", resultCode + "");
		params.addHeader("Authorization",
				PreferenceHelper.readString(SignActivity.this, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key, cache_time, new CacheRequestCallBack() {
			
			@Override
			public void onFail(HttpException e, String result, String json) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onData(String json) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void postSignData() {
		// TODO Auto-generated method stub
//		waitDialog();
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("cur_user", Conf.userID);
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		String key = "signin";
		CacheRequest.requestGET(context, key, params, key, 0,
				new CacheRequestCallBack() {

					@Override
					public void onData(String json) {
						// TODO Auto-generated method stub
						try {
							BaseJson newPer = new Gson().fromJson(json,
									BaseJson.class);
							if (newPer.getStatus().equals("200")) {
								MainFragmentActivity.signJson.setIs_signin("1");
								int coin = Integer.valueOf(newPer.getCoin());
								int coins = Integer.valueOf(Conf.Coins) + coin;
								Conf.Coins = String.valueOf(coins);
								Toast.makeText(
										context,
										StringUtils.getResourse(
												R.string.str_signsuccess),
										Toast.LENGTH_SHORT).show();
								if (pay) {
									finishActivity();
								}
//								finish();

								// btn_sign.setText(getString(R.string.str_sign_end));
								// tv_signnum.setText(getString(R.string.str_signin_days)
								// + newPer.getDays()
								// + getString(R.string.str_day));
							} else if (newPer.getStatus().equals("158")) {
								Toast.makeText(
										context,
										StringUtils.getResourse(
												R.string.str_signed),
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						} finally {
//							if (mDialog != null) {
//								mDialog.dismiss();
//							}
						}

			}

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub
//						if (mDialog != null) {
//							mDialog.dismiss();
//						}
						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
					}
				});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
		if(!pay){
			finishActivity();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == event.KEYCODE_BACK){
//			relativeLayout1.setVisibility(View.VISIBLE);
			  moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	
	}

}

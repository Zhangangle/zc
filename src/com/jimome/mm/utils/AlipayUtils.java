package com.jimome.mm.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.alipay.sdk.app.PayTask;
import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.unjiaoyou.mm.R;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.alipay.Result;
import com.jimome.mm.alipay.SignUtils;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

public class AlipayUtils {
	public static String PARTNER = "";
	public static String SELLER = "";
	public Dialog mDialog;
	public static final int SDK_PAY_FLAG = 1;
	public Context context;
	public static final int SDK_CHECK_FLAG = 2;
	public int type;// 支付方式 0：支付宝 1：银行卡
	public String item, kinds;

	public AlipayUtils() {
	}

	// kinds "pay"->支付会员 "buy"->购物
	public AlipayUtils(Context context, int type, String item, String kinds) {
		this.context = context;
		this.type = type;
		this.item = item;
		this.kinds = kinds;

	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				Result resultObj = new Result((String) msg.obj);
				String resultStatus = resultObj.resultStatus;

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					if (kinds.equals("pay")) {
						Conf.user_VIP = "1";
						StatService.onEvent(context, "vip-user", "eventLabel",
								1);
					} else {
						Intent intent = new Intent();
						intent.setAction("jimo.action.goodbuy");
						context.sendBroadcast(intent);
					}

					// ViewInject.toast("支付成功");
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”
					// 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						// ViewInject.toast("支付结果确认中");
					} else {
						// ViewInject.toast("支付失败");
					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				// ViewInject.toast("检查结果为：" + msg.obj);
				break;
			}
			default:
				break;
			}
		};
	};

	public void waitDialog() {
		mDialog = BasicUtils.showDialog(context, R.style.BasicDialog);
		mDialog.setContentView(R.layout.dialog_wait);
		mDialog.setCanceledOnTouchOutside(false);

		Animation anim = AnimationUtils.loadAnimation(context,
				R.anim.dialog_prog);
		LinearInterpolator lir = new LinearInterpolator();
		anim.setInterpolator(lir);
		mDialog.findViewById(R.id.img_dialog_progress).startAnimation(anim);
		mDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					mDialog.dismiss();
					return false;
				}
				return false;
			}
		});
		mDialog.show();
	}

	// "pay"：表示会员或金币
	public void getHttpData() {
		waitDialog();
		RequestParams params = new RequestParams();
		String key = "";
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		if (type == 0) {
			key = "pay/alipay";
		} else if (type == 1) {
			key = "pay/yeepay";
			params.addQueryStringParameter("imei", Conf.IMEI);
			params.addQueryStringParameter("ip", Conf.PublicNetwork);
		}
		params.addQueryStringParameter("product_id", item);
		params.addQueryStringParameter("cur_user", Conf.userID);// Conf.userID
		CacheRequest.requestGET(context, key, params, key, 0,
				new CacheRequestCallBack() {

					@Override
					public void onData(String json) {
						// TODO Auto-generated method stub
						try {
							Log.e("支付返回", json);
							if (json == null)
								return;
							BaseJson base = new Gson().fromJson(json,
									BaseJson.class);
							if (type == 0) {
								if (base.getStatus().equals("200")) {
									PARTNER = base.getPid();
									SELLER = base.getSeller();
									pay(base.getOrder_id(), base.getName(),
											base.getText(), base.getAmount());
								} 
							} else if (type == 1) {
								if (base.getStatus().equals("200")) {
									Conf.webPayurl = base.getUrl();
									Intent intent = new Intent(context,
											FragmentToActivity.class);
									intent.putExtra("who", "webpay");
									intent.putExtra("kinds", kinds);
									context.startActivity(intent);
								} 
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (mDialog != null)
								mDialog.dismiss();
						}
					}

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub
						Log.e("支付返回", json);
						if (mDialog != null)
							mDialog.dismiss();
						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
					}

				});

	}

	// “buy”:表示云购
	public void postHttpData() {
		waitDialog();
		RequestParams params = new RequestParams();
		String key = "";
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		if (type == 0) {
			key = "buy/alipay";
		} else if (type == 1) {
			key = "buy/yeepay";
			params.addBodyParameter("imei", Conf.IMEI);
			params.addBodyParameter("ip", Conf.PublicNetwork);
		}

		params.addBodyParameter("goods", item);
		params.addBodyParameter("user_id", Conf.userID);// Conf.userID
		CacheRequest.requestPOST(context, key, params, key, 0,
				new CacheRequestCallBack() {

					@Override
					public void onData(String json) {
						// TODO Auto-generated method stub
						try {
							Log.e("支付返回", json);
							if (json == null)
								return;
							BaseJson base = new Gson().fromJson(json,
									BaseJson.class);
							if (type == 0) {
								if (base.getStatus().equals("200")) {
									PARTNER = base.getPid();
									SELLER = base.getSeller();
									pay(base.getOrder_id(), base.getName(),
											base.getText(), base.getAmount());
								} 
							} else if (type == 1) {
								if (base.getStatus().equals("200")) {
									Conf.webPayurl = base.getUrl();
									Intent intent = new Intent(context,
											FragmentToActivity.class);
									intent.putExtra("who", "webpay");
									context.startActivity(intent);
								} 
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (mDialog != null)
								mDialog.dismiss();
						}
					}

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub
						Log.e("支付返回", json);
						if (mDialog != null)
							mDialog.dismiss();
						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
					}

				});

	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(String tradeNo, String subject, String body, String price) {
		String orderInfo = getOrderInfo(tradeNo, subject, body, price);
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask((Activity) context);
				// 调用支付接口
				String result = alipay.pay(payInfo);
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check() {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask payTask = new PayTask((Activity) context);
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask((Activity) context);
		String version = payTask.getVersion();
		Toast.makeText(context, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String tradeNo, String subject, String body,
			String price) {
		// 合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + tradeNo + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\""
				+ "http://api.347.cc/pay/alipay/callback" + "\"";

		// 接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"3m\"";

		return orderInfo;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, Conf.RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}
}

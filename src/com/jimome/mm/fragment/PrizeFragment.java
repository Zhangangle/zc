package com.jimome.mm.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.activity.MainFragmentActivity;
import com.jimome.mm.activity.SignActivity;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.view.GuaGuaLeView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.unjiaoyou.mm.R;

public class PrizeFragment extends BaseFragment {

	@ViewInject(R.id.tv_title)
	private TextView title;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	@ViewInject(R.id.tv_prize)
	private GuaGuaLeView tv_prize;
	@ViewInject(R.id.img_prize_ad)
	private ImageView img_prize_ad;
	@ViewInject(R.id.layout_prize)
	private LinearLayout layout_prize;

	private BaseJson json;
	public static Handler handler;
	public static boolean isPrize = false;
	private Activity context;

	private RequestParams params;

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_prize, container, false);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		if (getActivity() == null)
			context = activity;
		else
			context = getActivity();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	private void getHttpData() {
		// TODO Auto-generated method stub
		try {
			String key = "find/lottery/pannel";
			params.addQueryStringParameter("user_id", Conf.userID);
			params.addHeader("Authorization",
					PreferenceHelper.readString(context, "auth", "token"));
			CacheRequest.requestGET(context, key, params, key, 0,
					new CacheRequestCallBack() {

						@Override
						public void onData(String arg0) {
							// TODO Auto-generated method stub
							if (arg0 == null)
								return;
							json = new Gson().fromJson(arg0, BaseJson.class);
							if (json == null) {
								return;
							}

							String status = json.getStatus();
							if (status.equals("200")) {
								setImage();
							}
						}

						@Override
						public void onFail(HttpException e, String result,
								String json) {
							// TODO Auto-generated method stub
							Toast.makeText(context, "网络加载失败 " + result,
									Toast.LENGTH_SHORT).show();
							ExitManager.getScreenManager().intentLogin(context,
									e.getExceptionCode() + "");
						}
					});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private void prizeRequest() {
		// TODO Auto-generated method stub
		try {
			String key = "find/lottery";
			params.addQueryStringParameter("user_id", Conf.userID);
			params.addHeader("Authorization",
					PreferenceHelper.readString(context, "auth", "token"));
			CacheRequest.requestGET(context, key, params, key, 0,
					new CacheRequestCallBack() {

						@Override
						public void onData(String arg0) {
							// TODO Auto-generated method stub
							BaseJson prizeJson = new Gson().fromJson(arg0,
									BaseJson.class);
							if (prizeJson == null) {
								return;
							}
							String status = prizeJson.getStatus();
							if (status.equals("200")) {
								isPrize = true;
								tv_prize.setText(prizeJson.getCoin() + "金币");
							} else if (status.equals("201")) {
								isPrize = false;
								layout_prize.removeAllViews();
								tv_prize.beginRubbler(0XFFCECECE, 15, 1f);
								layout_prize.addView(tv_prize);
								showTipDialog(prizeJson.getMsg());
							}
						}

						@Override
						public void onFail(HttpException e, String result,
								String json) {
							// TODO Auto-generated method stub
							ExitManager.getScreenManager().intentLogin(context,
									e.getExceptionCode() + "");
						}
					});
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void showTipDialog(String text) {
		// TODO Auto-generated method stub
		final Dialog dialog = BasicUtils.showDialog(context,
				R.style.BasicDialog);
		dialog.setContentView(R.layout.dialog_send);
		dialog.setCanceledOnTouchOutside(false);
		ImageView icon = ((ImageView) dialog.findViewById(R.id.img_dialog_icon));
		TextView tip = ((TextView) dialog.findViewById(R.id.tv_dialog_msg));
		((LinearLayout) dialog.findViewById(R.id.layout_sign))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (dialog != null)
							dialog.dismiss();
						if (MainFragmentActivity.signJson != null
								&& MainFragmentActivity.signJson.getIs_signin() != null
								&& !MainFragmentActivity.signJson
										.getIs_signin().trim().equals("1")) {
							Intent sign_intent = new Intent(context,
									SignActivity.class);
							sign_intent.putExtra("signin",
									MainFragmentActivity.signJson);
							context.startActivity(sign_intent);
						}
					}
				});
		icon.setImageResource(R.drawable.send_pic_wrong);
		tip.setText(text);
		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					if (dialog != null)
						dialog.dismiss();
					if (MainFragmentActivity.signJson != null
							&& MainFragmentActivity.signJson.getIs_signin() != null
							&& !MainFragmentActivity.signJson.getIs_signin()
									.trim().equals("1")) {
						Intent sign_intent = new Intent(context,
								SignActivity.class);
						sign_intent.putExtra("signin",
								MainFragmentActivity.signJson);
						context.startActivity(sign_intent);
					}
					return false;
				}
				return false;
			}
		});
		dialog.show();
		// new Timer().schedule(new TimerTask() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// dialog.dismiss();
		// if (MainFragmentActivity.signJson.getIs_signin().trim()
		// .equals("1")) {
		// } else {
		// Intent sign_intent = new Intent(getActivity(),
		// SignActivity.class);
		// sign_intent.putExtra("signin",
		// MainFragmentActivity.signJson);
		// getActivity().startActivity(sign_intent);
		// }
		//
		// // }
		// }
		// }, 4000);
	}

	private void setImage() {
		// TODO Auto-generated method stub
		try {
			ImageLoadUtils.imageLoader.getInstance().displayImage(
					json.getImg(), img_prize_ad, ImageLoadUtils.options,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap arg2) {
							// TODO Auto-generated method stub
							if (arg2 == null) {
								return;
							}
							int height = (Conf.width * arg2.getHeight())
									/ arg2.getWidth();
							img_prize_ad
									.setLayoutParams(new LinearLayout.LayoutParams(
											LinearLayout.LayoutParams.MATCH_PARENT,
											height));
							img_prize_ad.setImageBitmap(arg2);
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							// TODO Auto-generated method stub

						}
					});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	@OnClick({ R.id.layout_back })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.layout_back:
			context.finish();
			break;

		default:
			break;
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(context);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(context);
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		layout_back.setVisibility(View.VISIBLE);
		title.setText("刮奖乐翻天");
		params = new RequestParams();
		getHttpData();
		tv_prize.beginRubbler(0XFFCECECE, 15, 1f);
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case 0:
					prizeRequest();
					break;

				default:
					break;
				}
			}
		};

	}

}

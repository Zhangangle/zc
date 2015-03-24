package com.jimome.mm.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.adapter.HotVipAdapter;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.NetworkUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.jimome.mm.view.MyListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.unjiaoyou.mm.R;

/**
 * Vip页面
 * 
 * @author admin
 * 
 */
@SuppressLint("ValidFragment")
public class VipFragment extends BaseFragment {

	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;// 返回
	@ViewInject(R.id.img_vip_icon)
	private ImageView img_vip_icon;
	@ViewInject(R.id.tv_vip_ad)
	private TextView tv_vip_ad;
	@ViewInject(R.id.llayout_vip_admin)
	private LinearLayout llayout_vip_admin;// 管理员
	@ViewInject(R.id.lv_hot_vip)
	private MyListView lv_hot_vip;
	private HotVipAdapter hotAdapter;
	private BaseJson baseJson;
	// private Dialog mDialog;
	private Activity context;

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_vip, container, false);
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

	private void waitDialog() {
		// mDialog = BasicUtils.showDialog(context, R.style.BasicDialog);
		// mDialog.setContentView(R.layout.dialog_wait);
		// mDialog.setCanceledOnTouchOutside(false);
		//
		// Animation anim = AnimationUtils.loadAnimation(context,
		// R.anim.dialog_prog);
		// LinearInterpolator lir = new LinearInterpolator();
		// anim.setInterpolator(lir);
		// mDialog.findViewById(R.id.img_dialog_progress).startAnimation(anim);
		// mDialog.setOnKeyListener(new OnKeyListener() {
		// @Override
		// public boolean onKey(DialogInterface dialog, int keyCode,
		// KeyEvent event) {
		// if (keyCode == KeyEvent.KEYCODE_BACK
		// && event.getAction() == KeyEvent.ACTION_DOWN) {
		// if (!context.isFinishing())
		// mDialog.dismiss();
		//
		// return false;
		// }
		// return false;
		// }
		// });
		mDialog.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	private void setView() {
		try {

			if (baseJson.getProducts() != null
					&& baseJson.getProducts().size() > 0) {
				hotAdapter = new HotVipAdapter(context, baseJson.getProducts());
				lv_hot_vip.setAdapter(hotAdapter);
			}
			ImageLoadUtils.imageLoader.displayImage(baseJson.getImg(),
					img_vip_icon, ImageLoadUtils.options,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("1"))
								img_vip_icon
										.setImageResource(R.drawable.default_female);
							else
								img_vip_icon
										.setImageResource(R.drawable.default_male);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("1"))
								img_vip_icon
										.setImageResource(R.drawable.default_female);
							else
								img_vip_icon
										.setImageResource(R.drawable.default_male);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							// TODO Auto-generated method stub
							if (loadedImage != null) {
								int height = (Conf.width * loadedImage
										.getHeight()) / loadedImage.getWidth();
								img_vip_icon
										.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
												android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
												height));
							}
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							// TODO Auto-generated method stub

						}
					});
			StringBuffer sb = new StringBuffer();
			int paySize = baseJson.getPays().size();
			for (int i = 0; i < paySize; i++) {
				sb.append("<font color='red'>"
						+ baseJson.getPays().get(i).getNick() + "</font>"
						+ baseJson.getPays().get(i).getText() + "\t\t\t");
			}
			tv_vip_ad.setText(Html.fromHtml(sb.toString()));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@OnClick({ R.id.layout_back, R.id.llayout_vip_admin })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.llayout_vip_admin:
			Intent systemIntent = new Intent(new Intent(context,
					FragmentToActivity.class));
			systemIntent.putExtra("who", "system");
			context.startActivity(systemIntent);
			break;
		case R.id.layout_back:// 返回
			context.finish();
			break;
		}
	}

	private void getHttpData(final String item) {

		RequestParams params = new RequestParams();
		String key = "";
		try {
			if (item.equals("pay")) {
				key = "pay";
				params.addQueryStringParameter("product_type", "1");
			} else {
				key = "pay/yeepay";
				params.addQueryStringParameter("imei", Conf.IMEI);
				params.addQueryStringParameter("product_id", item);
				params.addQueryStringParameter("ip", Conf.PublicNetwork);
			}
			params.addQueryStringParameter("cur_user", Conf.userID);// Conf.userID

		} catch (Exception e) {
			// TODO: handle exception
		}
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key, 0,
				new CacheRequestCallBack() {

					@Override
					public void onData(String json) {
						// TODO Auto-generated method stub

						try {
							if (item.equals("pay")) {
								baseJson = new Gson().fromJson(json,
										BaseJson.class);
								if (baseJson.getStatus().equals("200")) {
									setView();
								} else {
									Toast.makeText(
											context,
											StringUtils.getResourse(R.string.str_net_register),
											Toast.LENGTH_SHORT).show();
								}
							} else {
								BaseJson base = new Gson().fromJson(json,
										BaseJson.class);
								if (base.getStatus().equals("200")) {
									Conf.webPayurl = base.getUrl();
									Intent intent = new Intent(context,
											FragmentToActivity.class);
									intent.putExtra("who", "webpay");
									context.startActivity(intent);
								} else {
									Toast.makeText(
											context,
											StringUtils.getResourse(R.string.str_net_register),
											Toast.LENGTH_SHORT).show();
								}
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (mDialog != null) {
								mDialog.dismiss();
							}
						}
					}

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub
						if (mDialog != null) {
							mDialog.dismiss();
						}
						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
					}

				});
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mDialog != null)
			mDialog.dismiss();
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

		// TODO Auto-generated method stub
		tv_title.setText(R.string.str_discovery);
		layout_back.setVisibility(View.GONE);
		if (NetworkUtils.checkNet(context)) {
			waitDialog();
			getHttpData("pay");
		} else {
			Toast.makeText(context, StringUtils.getResourse(R.string.str_net_register),
					Toast.LENGTH_SHORT).show();
		}

	}
}

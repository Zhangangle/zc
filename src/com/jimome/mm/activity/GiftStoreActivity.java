package com.jimome.mm.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.unjiaoyou.mm.R;
import com.jimome.mm.adapter.GiftStoreGridAdapter;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.fragment.SelectFragment;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.NetworkUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.jimome.mm.view.MyGirdView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 礼物商城页面
 * 
 * @author Administrator
 * 
 */
@ContentView(R.layout.activity_giftstore)
public class GiftStoreActivity extends BaseFragmentActivity implements
		OnItemClickListener {
	private Context context;
	@ViewInject(R.id.gv_giftstore)
	private MyGirdView gv_giftstore;
	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	@ViewInject(R.id.tv_gift_coin)
	private TextView tv_gift_coin;// 金币数目
	@ViewInject(R.id.tv_gift_chongzhi)
	private TextView tv_gift_chongzhi;// 充值
	@ViewInject(R.id.img_ad_gift_icon)
	private ImageView img_ad_gift_icon;// 广告
	private GiftStoreGridAdapter giftAdapter;
	private BaseJson person;
	private BaseJson gift;
	private BaseJson talkgift;
	private Dialog dialog;
	private String goto_type;// 判断是否是消息聊天跳转进来的，还是个人主页或身材秀详细
	private String dialog_id;// 聊天页面送礼

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		ViewUtils.inject(this);
		ExitManager.getScreenManager().pushActivity(this);
		tv_title.setText(StringUtils.getResourse(R.string.str_store));
		layout_back.setVisibility(View.VISIBLE);
		Intent intent = getIntent();
		person = (BaseJson) intent.getSerializableExtra("person");
		goto_type = intent.getStringExtra("type");
		dialog_id = intent.getStringExtra("dialog_id");
		if (!NetworkUtils.checkNet(context)) {
			Toast.makeText(context, StringUtils.getResourse(R.string.str_date_null),
					Toast.LENGTH_SHORT).show();
		} else {
			getHttpData("main");
		}
		ImageLoadUtils.imageLoader.displayImage(Conf.GIFT_AD, img_ad_gift_icon,
				ImageLoadUtils.options, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub
						if (Conf.gender.equals("1"))
							img_ad_gift_icon
									.setImageResource(R.drawable.default_female);
						else
							img_ad_gift_icon
									.setImageResource(R.drawable.default_male);
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						// TODO Auto-generated method stub
						if (Conf.gender.equals("1"))
							img_ad_gift_icon
									.setImageResource(R.drawable.default_female);
						else
							img_ad_gift_icon
									.setImageResource(R.drawable.default_male);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						if (loadedImage != null) {
							int height = (Conf.width * loadedImage.getHeight())
									/ loadedImage.getWidth();
							img_ad_gift_icon
									.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
											android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
											height));
						}
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void setView() {
		Conf.userCoin = gift.getCoin();
		tv_gift_coin.setText(Conf.userCoin);

		giftAdapter = new GiftStoreGridAdapter(context, gift.getGifts());
		gv_giftstore.setAdapter(giftAdapter);
		gv_giftstore.setOnItemClickListener(this);

	}

	private void waitDialog() {
		mDialog.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDialog != null) {
			mDialog.dismiss();
		}
		ExitManager.getScreenManager().pullActivity(this);
	}

	@OnClick({ R.id.tv_gift_chongzhi, R.id.img_ad_gift_icon, R.id.layout_back })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.tv_gift_chongzhi:
			Intent intent = new Intent(context, FragmentToActivity.class);
			intent.putExtra("who", "coin");
			startActivity(intent);
			break;
		case R.id.layout_back:
			onBackPressed();
			break;
		case R.id.img_ad_gift_icon:
			StatService.onEvent(context, "gift-ad", "pass", 1);
			Intent vipintent = new Intent(context, FragmentToActivity.class);
			vipintent.putExtra("who", "vip");
			startActivity(vipintent);
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, final int pos,
			long arg3) {
		// TODO Auto-generated method stub
		if (gift == null) {
			Toast.makeText(context, StringUtils.getResourse(R.string.str_date_null),
					Toast.LENGTH_SHORT).show();
			return;
		}

		// TODO Auto-generated method stub
		if (Integer.valueOf(Conf.userCoin.trim()) < Integer.valueOf(gift
				.getGifts().get(pos).getCoin())) {
			Toast.makeText(context, StringUtils.getResourse(R.string.str_exchange_error),
					Toast.LENGTH_SHORT).show();
		} else if (!person.getUser_id().equals(Conf.userID)) {// 不是查看自己的资料
			dialog = BasicUtils.showDialog(context, R.style.BasicDialog);
			dialog.setContentView(R.layout.dialog_giftstore);
			dialog.setCanceledOnTouchOutside(true);
			((TextView) dialog.findViewById(R.id.tv_dialog_msg))
					.setText("确定要赠送一个" + gift.getGifts().get(pos).getName()
							+ "给" + person.getNick());
			((Button) dialog.findViewById(R.id.btn_dialog_cancle))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
			((Button) dialog.findViewById(R.id.btn_dialog_sure))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							getHttpData(gift.getGifts().get(pos).getId());
							talkgift = gift.getGifts().get(pos);
							// 上传事件
						}
					});
			ImageLoadUtils.imageLoader.displayImage(gift.getGifts().get(pos)
					.getImg(),
					(ImageView) dialog.findViewById(R.id.img_dialog_pic),
					ImageLoadUtils.options, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub

							if (Conf.gender.equals("1"))
								((ImageView) dialog
										.findViewById(R.id.img_dialog_pic))
										.setImageResource(R.drawable.default_female);
							else
								((ImageView) dialog
										.findViewById(R.id.img_dialog_pic))
										.setImageResource(R.drawable.default_male);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("1"))
								((ImageView) dialog
										.findViewById(R.id.img_dialog_pic))
										.setImageResource(R.drawable.default_female);
							else
								((ImageView) dialog
										.findViewById(R.id.img_dialog_pic))
										.setImageResource(R.drawable.default_male);
						}

						@Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap arg2) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							// TODO Auto-generated method stub

						}
					});
			dialog.show();
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

	}

	private void getHttpData(final String type) {
		waitDialog();

		RequestParams params = new RequestParams();
		String key = "";
		int cash_time = 0;
		try {

			if (type.equals("main")) {
				params.addQueryStringParameter("cur_user", Conf.userID);
				key = "user/gift";
				cash_time = 120;
			} else {
				if (goto_type != null && goto_type.equals("msg")) {
					key = "msg/send";
					params.addQueryStringParameter("receiver",
							person.getUser_id());
					params.addQueryStringParameter("sender", Conf.userID);
					if (dialog_id != null)
						params.addQueryStringParameter("dialog_id", dialog_id);
					params.addQueryStringParameter("text", type);
					params.addQueryStringParameter("flag", "2");
					cash_time = 0;
				} else {
					key = "user/gift/send";
					params.addQueryStringParameter("user_id",
							person.getUser_id());
					params.addQueryStringParameter("cur_user", Conf.userID);
					params.addQueryStringParameter("gift_id", type);
					cash_time = 0;
				}
			}
			params.addHeader("Authorization",
					PreferenceHelper.readString(context, "auth", "token"));
			CacheRequest.requestGET(context, key, params, key, cash_time,
					new CacheRequestCallBack() {

						@Override
						public void onData(String json) {
							// TODO Auto-generated method stub
							try {
								if (type.equals("main")) {
									gift = new Gson().fromJson(json,
											BaseJson.class);
									if (gift.getStatus().equals("200")) {
										setView();
									}
								} else {
									BaseJson send = new Gson().fromJson(json,
											BaseJson.class);
									if (send.getStatus().equals("200")) {
										StatService.onEvent(context,
												"gift-user", "eventLabel", 1);
										Conf.userCoin = String.valueOf(Integer
												.valueOf(Conf.userCoin)
												- Integer.valueOf(talkgift
														.getCoin()));
										tv_gift_coin.setText(Conf.userCoin);
										Toast.makeText(
												context,
												StringUtils.getResourse(R.string.str_gift_send),
												Toast.LENGTH_SHORT).show();
										SelectFragment.sendMsg = true;
										if (person.getFlag() != null
												&& person.getFlag().equals("2")) {
											Intent intent = new Intent();
											intent.setAction("jimome.action.sendgift");
											intent.putExtra("text", type);
											Bundle bundle = new Bundle();
											bundle.putSerializable("gift",
													talkgift);
											bundle.putSerializable("gifttime",
													send);
											intent.putExtras(bundle);
											sendBroadcast(intent);
										} else {
											Intent intent = new Intent();
											intent.setAction("jimome.action.getgift");
											Bundle bundle = new Bundle();
											talkgift.setSender_nick(Conf.userName);
											talkgift.setTime("");
											bundle.putSerializable("getgift",
													talkgift);
											intent.putExtras(bundle);
											sendBroadcast(intent);
										}
										finish();
									} else if (send.getStatus().equals("152")) {
										Toast.makeText(
												context,
												StringUtils.getResourse(R.string.str_exchange_error),
												Toast.LENGTH_SHORT).show();
									} else
										Toast.makeText(
												context,
												StringUtils.getResourse(R.string.str_gift_sendfail),
												Toast.LENGTH_SHORT).show();
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
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Toast.makeText(context, StringUtils.getResourse(R.string.str_date_null),
					Toast.LENGTH_SHORT).show();
		}
	}
}

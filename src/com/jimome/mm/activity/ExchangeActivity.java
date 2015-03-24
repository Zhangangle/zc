package com.jimome.mm.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.unjiaoyou.mm.R;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.NetworkUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 确定兑换页面
 * 
 * @author admin
 * 
 */
@ContentView(R.layout.activity_exchange)
public class ExchangeActivity extends BaseFragmentActivity {
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.layout_back)
	LinearLayout layout_back;
	@ViewInject(R.id.layout_text_chong)
	LinearLayout layout_text_chong;
	@ViewInject(R.id.tv_text_chong)
	TextView tv_text_chong;
	@ViewInject(R.id.tv_exchange_name)
	TextView tv_exchange_name;
	@ViewInject(R.id.tv_exchange_cankao)
	TextView tv_exchange_cankao;
	@ViewInject(R.id.tv_exchange_zong)
	TextView tv_exchange_zong;
	@ViewInject(R.id.tv_exchange_coin)
	TextView tv_exchange_coin;
	@ViewInject(R.id.btn_exchange)
	Button btn_exchange;
	@ViewInject(R.id.ed_exchange_nums)
	EditText ed_exchange_nums;
	@ViewInject(R.id.btn_exchange_addnums)
	Button btn_exchange_addnums;
	@ViewInject(R.id.img_exchange_icon)
	ImageView img_exchange_icon;
	private BaseJson goods;
	// private Dialog mDialog;
	public static boolean exchanged = false;
	private Context context;
	private InputMethodManager im;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = ExchangeActivity.this;
		ViewUtils.inject(this);
		ExitManager.getScreenManager().pushActivity(this);
		tv_title.setText(StringUtils.getResourse(R.string.str_find_store));
		layout_back.setVisibility(View.VISIBLE);
		layout_text_chong.setVisibility(View.VISIBLE);
		tv_text_chong.setText(Conf.userCharm + StringUtils.getResourse(R.string.str_meili));
		tv_exchange_cankao.getPaint().setFlags(
				Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);// 中间划线

		im = (InputMethodManager) getApplicationContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		Intent intent = getIntent();
		goods = (BaseJson) intent.getSerializableExtra("goods");
		tv_exchange_name.setText(goods.getName());
		tv_exchange_cankao.setText("￥" + goods.getRmb());
		tv_exchange_coin.setText(goods.getCharm()
				+ StringUtils.getResourse(R.string.str_meili));
		tv_exchange_zong.setText(goods.getCharm()
				+ StringUtils.getResourse(R.string.str_meili));
		ImageLoadUtils.imageLoader.displayImage(goods.getImg(),
				img_exchange_icon, ImageLoadUtils.options,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub
						if (Conf.gender.equals("1"))
							img_exchange_icon
									.setImageResource(R.drawable.default_male);
						else
							img_exchange_icon
									.setImageResource(R.drawable.default_female);
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						// TODO Auto-generated method stub

						if (Conf.gender.equals("1"))
							img_exchange_icon
									.setImageResource(R.drawable.default_male);
						else
							img_exchange_icon
									.setImageResource(R.drawable.default_female);
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
		ed_exchange_nums.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence str, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				try {
					if (str.toString().trim().equals("0")
							|| str.toString().trim().equals("00")) {
						ed_exchange_nums.setText("");
						tv_exchange_zong.setText(goods.getCharm()
								+ StringUtils.getResourse(R.string.str_meili));
					} else {
						if (ed_exchange_nums.getText().toString().trim()
								.equals("")) {
							tv_exchange_zong.setText(goods.getCharm()
									+ StringUtils.getResourse(R.string.str_meili));
						} else
							tv_exchange_zong.setText(Integer
									.valueOf(ed_exchange_nums.getText()
											.toString().trim())
									* Integer.valueOf(goods.getCharm())
									+ StringUtils.getResourse(R.string.str_meili));
					}
					if (ed_exchange_nums.getText().toString().trim().length() > 0) {
						btn_exchange.setBackgroundResource(R.color.red);
					} else {
						btn_exchange.setBackgroundResource(R.color.gray);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void waitDialog() {
		// mDialog = BasicUtils.showDialog(ExchangeActivity.this,
		// R.style.BasicDialog);
		// mDialog.setContentView(R.layout.dialog_wait);
		// mDialog.setCanceledOnTouchOutside(false);
		//
		// Animation anim = AnimationUtils.loadAnimation(ExchangeActivity.this,
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
		// if (!isFinishing())
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
	protected void onDestroy() {
		super.onDestroy();
		ExitManager.getScreenManager().pullActivity(this);
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	private void getHttpData(String exchangeNums) {
		waitDialog();
		RequestParams params = new RequestParams();
		String key = "find/exchange";
		try {
			params.addQueryStringParameter("cur_user", Conf.userID);// Conf.userID
			params.addQueryStringParameter("item_id", goods.getId());
			params.addQueryStringParameter("nums", exchangeNums);
			params.addHeader("Authorization",
					PreferenceHelper.readString(context, "auth", "token"));
			CacheRequest.requestGET(context, key, params, key, 0,
					new CacheRequestCallBack() {

						@Override
						public void onData(String json) {
							// TODO Auto-generated method stub
							if (mDialog != null) {
								mDialog.dismiss();
							}
							if (json.equals("")) {
//								BasicUtils.toast(StringUtils
//										.getResourse(R.string.str_net_register));
								return;
							}
							try {
								BaseJson exFoods = new Gson().fromJson(json,
										BaseJson.class);
								if (exFoods.getStatus().equals("200")) {
									int coin = Integer.valueOf(Conf.userCharm)
											- Integer.valueOf(goods.getCharm());
									Conf.userCharm = String.valueOf(coin);
									tv_text_chong.setText(Conf.userCharm
											+ StringUtils.getResourse(R.string.str_meili));
									exchanged = true;
									Toast.makeText(
											context,
											StringUtils.getResourse(R.string.str_exchange_success),
											Toast.LENGTH_SHORT).show();
									finish();
								} else if (exFoods.getStatus().equals("151")) {
									Toast.makeText(
											context,
											StringUtils.getResourse(R.string.str_excharm_error),
											Toast.LENGTH_SHORT).show();
								} else if (exFoods.getStatus().equals("157")) {
									Toast.makeText(
											context,
											StringUtils.getResourse(R.string.str_exchange_fail),
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											context,
											StringUtils.getResourse(R.string.str_excharm_fail),
											Toast.LENGTH_SHORT).show();
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
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
		}

	}

	@OnClick({ R.id.layout_back, R.id.btn_exchange, R.id.btn_exchange_addnums })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.layout_back:
			onBackPressed();
			break;
		case R.id.btn_exchange:
			im.hideSoftInputFromWindow(ed_exchange_nums.getWindowToken(), 0);
			try {
				if (ed_exchange_nums.getText().toString().trim().equals("0")
						|| ed_exchange_nums.getText().toString().trim()
								.equals("00")
						|| ed_exchange_nums.getText().toString().trim()
								.equals("")) {
					Toast.makeText(context,
							StringUtils.getResourse(R.string.str_exchange_numerror),
							Toast.LENGTH_SHORT).show();
				} else if (Integer.valueOf(Conf.userCharm) >= (Integer
						.valueOf(goods.getCharm()) * Integer
						.valueOf(ed_exchange_nums.getText().toString().trim()))) {
					if (NetworkUtils.checkNet(ExchangeActivity.this)) {
						getHttpData(ed_exchange_nums.getText().toString()
								.trim());
					} else {
						Toast.makeText(context,
								StringUtils.getResourse(R.string.str_open_net),
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(context,
							StringUtils.getResourse(R.string.str_excharm_error),
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case R.id.btn_exchange_addnums:
			try {
				if (ed_exchange_nums.getText().toString().trim().equals("99")) {
					Toast.makeText(context,
							StringUtils.getResourse(R.string.str_exchange_numhint),
							Toast.LENGTH_SHORT).show();
				} else {
					ed_exchange_nums.setText(Integer.valueOf(ed_exchange_nums
							.getText().toString().trim())
							+ 1 + "");
				}
			} catch (Exception e) {
				// TODO: handle exception
				ed_exchange_nums.setText("1");
			}
			break;
		default:
			break;
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
}
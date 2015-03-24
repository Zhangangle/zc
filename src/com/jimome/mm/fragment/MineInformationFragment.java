package com.jimome.mm.fragment;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.unjiaoyou.mm.R;

/**
 * 个人资料页面
 * 
 * @author admin
 * 
 */
public class MineInformationFragment extends BaseFragment {

	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;// 返回
	@ViewInject(R.id.tv_text_chong)
	private TextView tv_text_chong;
	@ViewInject(R.id.layout_text_chong)
	private LinearLayout layout_text_chong;
	@ViewInject(R.id.tv_mine_word)
	private TextView tv_word;// 多少字
	@ViewInject(R.id.ed_mine_heart)
	private EditText ed_heart;// 个性签名
	@ViewInject(R.id.ed_mine_nickname)
	private EditText ed_nickname;// 昵称
	@ViewInject(R.id.tv_mine_year)
	private TextView tv_year;// 年份
	@ViewInject(R.id.tv_mine_month)
	private TextView tv_month;// 月份
	@ViewInject(R.id.tv_mine_day)
	private TextView tv_day;// 日
	@ViewInject(R.id.tv_mine_height)
	private TextView tv_height;// 身高
	@ViewInject(R.id.tv_mine_gender)
	private TextView tv_gender;// 身高
	@ViewInject(R.id.tv_mytel)
	private TextView tv_mytel;// 电话：
	@ViewInject(R.id.ed_mine_tel)
	private EditText ed_tel;// 手机号码
	@ViewInject(R.id.ed_mine_ad)
	private EditText ed_mine_ad;// 联系地址
	@ViewInject(R.id.bt_mine_gettel)
	private Button bt_mine_gettel;// 获取验证码
	@ViewInject(R.id.bt_mine_checktel)
	private Button bt_mine_checktel;// 验证验证码
	@ViewInject(R.id.tv_mine_checknum)
	private EditText ed_mine_checknum;// 输入验证码
	@ViewInject(R.id.tv_mine_resulttel)
	private TextView tv_mine_resulttel;// 验证结果
	@ViewInject(R.id.view_line6)
	private View view_line6;// 验证验证码
	@ViewInject(R.id.ed_mine_myalipay)
	private EditText ed_mine_myalipay;// 输入支付宝帐号
	private int yearId, monthId, dayId, heightId = 0;// 代号ID
	private String[] year;
	private String[] month;
	private String[] day;
	private String[] height;
	private String[] genders;
	private String gender;
	// private Dialog mDialog;
	private BaseJson perMain;
	private InputMethodManager im;
	private String tel = "";
	private boolean flag_tel = false;// 是否验证成功
	private boolean flag_gettel = false;
	private Activity context;

	@Override
	protected View initView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		return arg0.inflate(R.layout.fragment_mineinfor, arg1, false);
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
		waitDialog();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("jimome.action.mineinfo");
		context.registerReceiver(mRefreshBroadcastReceiver, intentFilter);
		genders = new String[] { StringUtils.getResourse(R.string.str_male),
				StringUtils.getResourse(R.string.str_female) };
		im = (InputMethodManager) context.getApplicationContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

	}

	@OnClick({ R.id.bt_mine_gettel, R.id.bt_mine_checktel, R.id.layout_back,
			R.id.layout_text_chong, R.id.tv_mine_year, R.id.tv_mine_month,
			R.id.tv_mine_day, R.id.tv_mine_height, R.id.tv_mine_gender })
	protected void widgetClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_mine_gettel:
			if (!flag_gettel) {
				if (ed_tel.getText().toString().trim().length() == 11) {

					// SMSSDK.getVerificationCode("86", ed_tel.getText()
					// .toString().trim());
					tel = ed_tel.getText().toString();
				} else {
					BasicUtils.toast(StringUtils.getResourse(R.string.str_register_telerror));
				}
			}
			break;
		case R.id.bt_mine_checktel:
			if (ed_mine_checknum.getText().toString().trim().length() >= 4) {
				// SMSSDK.submitVerificationCode("86", tel, ed_mine_checknum
				// .getText().toString().trim());
			}
			break;
		case R.id.layout_back:// 返回
			im.hideSoftInputFromWindow(ed_nickname.getWindowToken(), 0);
			im.hideSoftInputFromWindow(ed_heart.getWindowToken(), 0);
			im.hideSoftInputFromWindow(ed_tel.getWindowToken(), 0);
			im.hideSoftInputFromWindow(ed_mine_ad.getWindowToken(), 0);
			im.hideSoftInputFromWindow(ed_mine_myalipay.getWindowToken(), 0);
			setOut();
			break;

		case R.id.layout_text_chong:// 保存
			if (BasicUtils.containsEmoji(ed_mine_ad.getText().toString())
					|| BasicUtils.containsEmoji(ed_nickname.getText()
							.toString())
					|| BasicUtils.containsEmoji(ed_heart.getText().toString())
					|| BasicUtils.containsEmoji(ed_tel.getText().toString())
					|| BasicUtils.containsEmoji(ed_mine_myalipay.getText()
							.toString())) {
				BasicUtils.toast(StringUtils.getResourse(R.string.str_nickname_tip2));
			} else {
				waitDialog();
				postHttpDate();
			}
			// 隐藏输入框
			im.hideSoftInputFromWindow(ed_nickname.getWindowToken(), 0);
			im.hideSoftInputFromWindow(ed_heart.getWindowToken(), 0);
			im.hideSoftInputFromWindow(ed_tel.getWindowToken(), 0);
			im.hideSoftInputFromWindow(ed_mine_ad.getWindowToken(), 0);
			im.hideSoftInputFromWindow(ed_mine_myalipay.getWindowToken(), 0);
			break;
		case R.id.tv_mine_year:// 年份
			new AlertDialog.Builder(context)
					.setItems(year, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							tv_year.setText(year[which]);
							yearId = which;
							dialog.dismiss();
						}
					}).create().show();
			break;
		case R.id.tv_mine_month:// 月份

			new AlertDialog.Builder(context)
					.setItems(month, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							tv_month.setText(month[which]);
							monthId = which;
							dialog.dismiss();
						}
					}).create().show();
			break;
		case R.id.tv_mine_day:// 日期

			new AlertDialog.Builder(context)
					.setItems(day, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							tv_day.setText(day[which]);
							dayId = which;
							dialog.dismiss();
						}
					}).create().show();
			break;

		case R.id.tv_mine_height:// 身高

			new AlertDialog.Builder(context)
					.setItems(height, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							tv_height.setText(height[which]);
							heightId = which;
							dialog.dismiss();
						}
					}).create().show();
			break;
		case R.id.tv_mine_gender:
			if (gender == null || !gender.equals("2"))
				new AlertDialog.Builder(context)
						.setTitle(
								StringUtils.getResourse(
										R.string.str_mine_gendertitle))
						.setItems(genders,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										tv_gender.setText(genders[which]);
										gender = (which + 1) + "";
										dialog.dismiss();
									}
								}).create().show();
			break;
		default:
			break;
		}
	}

	private void setOut() {// 退出
		try {
			if (!ed_heart.getText().toString().equals(perMain.getIntro())
					|| !ed_nickname.getText().toString()
							.equals(perMain.getNick())
					|| !ed_mine_myalipay.getText().toString()
							.equals(perMain.getAlipay())
					| !tv_height.getText().toString()
							.equals(perMain.getHeight())
					|| !ed_tel.getText().toString()
							.equals(perMain.getCellphone())
					|| !ed_mine_ad.getText().toString()
							.equals(perMain.getAddress())
					|| !gender.equals(perMain.getGender()))
				showGitDialog();
			else
				context.finish();
		} catch (Exception e) {
			// TODO: handle exception
			context.finish();
		}
	}

	// 设置更新启动的广播
	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("jimome.action.mineinfo")) {
				try {
					setOut();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		}
	};

	protected void showGitDialog() {
		// TODO Auto-generated method stub
		// if (can_send.equals("0")) {
		final Dialog dialog = BasicUtils.showDialog(context,
				R.style.BasicDialog);
		dialog.setContentView(R.layout.dialog_rechargevip);
		TextView tv_tip = ((TextView) dialog.findViewById(R.id.tv_dialog_msg));
		tv_tip.setText(R.string.str_mine_dialogtitle);
		dialog.setCanceledOnTouchOutside(true);
		Button vip = ((Button) dialog.findViewById(R.id.btn_dialog_sure));

		vip.setText(R.string.str_mine_save);

		vip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				waitDialog();
				postHttpDate();
			}
		});

		Button gift = ((Button) dialog.findViewById(R.id.btn_dialog_cancle));

		gift.setText(R.string.str_mine_dialogexit);
		gift.setTextColor(Color.WHITE);
		gift.setBackgroundColor(getResources().getColor(R.color.lightblue));
		gift.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				try {
					context.finish();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		dialog.show();
	}

	// 网络请求显示数据
	private void getHttpData() {
		int cache_time = 0;
		RequestParams params = new RequestParams();
		try {
			params.addQueryStringParameter("cur_user", Conf.userID);// Conf.userID
		} catch (Exception e) {
			// TODO: handle exception
		}
		String key = "me/info";
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key, cache_time,
				new CacheRequestCallBack() {

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub
						if (mDialog != null) {
							mDialog.dismiss();
						}
						if (json.equals("")) {
							BasicUtils
									.toast(StringUtils.getResourse(R.string.str_net_register));
							return;
						}
					}

					@Override
					public void onData(String json) {
						// TODO Auto-generated method stub
						if (mDialog != null) {
							mDialog.dismiss();
						}
						if (json.equals("")) {
							return;
						}
						try {
							perMain = new Gson().fromJson(json, BaseJson.class);
							if (perMain.getStatus() != null
									&& perMain.getStatus().trim().equals("200")) {
								setView();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		// kjh.get(url, params, new HttpCallBack() {
		//
		// @Override
		// public void onSuccess(Object obj) {
		// // TODO Auto-generated method stub
		// try {
		// perMain = new Gson().fromJson(obj.toString(),
		// BaseJson.class);
		// if (perMain.getStatus() != null
		// && perMain.getStatus().trim().equals("200")) {
		// setView();
		// }
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// if (mDialog != null) {
		// mDialog.dismiss();
		// }
		//
		// }
		//
		// @Override
		// public void onLoading(long count, long current) {
		// // TODO Auto-generated method stub
		// }
		//
		// @Override
		// public void onFailure(Throwable t, int errorNo, String strMsg) {
		// // TODO Auto-generated method stub
		// if (mDialog != null) {
		// mDialog.dismiss();
		// }
		// ExitManager.getScreenManager().intentLogin(context,
		// StringUtils.httpRsponse(t.toString()));
		// }
		//
		// });
	}

	// Handler handler = new Handler() {
	//
	// @Override
	// public void handleMessage(Message msg) {
	// // TODO Auto-generated method stub
	// super.handleMessage(msg);
	// try {
	//
	// int event = msg.arg1;
	// int result = msg.arg2;
	// Object data = msg.obj;
	// Log.e("event", "event=" + event);
	// if (result == SMSSDK.RESULT_COMPLETE) {
	// // 短信注册成功后，返回MainActivity,然后提示新好友
	// if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
	// BasicUtils
	// .toast(getString(R.string.str_mine_checksuccess));
	// flag_tel = true;
	// tv_mine_resulttel
	// .setText(getString(R.string.str_mine_checksuccess));
	// flag_gettel = true;
	// bt_mine_checktel.setVisibility(View.GONE);
	// bt_mine_gettel
	// .setText(getString(R.string.str_mine_checksuccess));
	// ed_tel.setFocusable(false);
	// bt_mine_gettel.setBackgroundResource(R.color.transgray);
	// tv_mine_resulttel.setVisibility(View.GONE);
	// ed_mine_checknum.setVisibility(View.GONE);
	// view_line6.setVisibility(View.INVISIBLE);
	// } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
	// BasicUtils
	// .toast(getString(R.string.str_mine_getsuccess));
	// view_line6.setVisibility(View.VISIBLE);
	// ed_mine_checknum.setVisibility(View.VISIBLE);
	// bt_mine_checktel.setVisibility(View.VISIBLE);
	// tv_mine_resulttel.setVisibility(View.VISIBLE);
	// tv_mine_resulttel
	// .setText(getString(R.string.str_mine_getsuccess));
	// tv_mytel.setPadding(0, 0, 0, 0);
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// for (int i = 60; i >= 0; i--) {
	// if (flag_tel)
	// break;
	// Message msg = new Message();
	// msg.arg1 = 100;
	// msg.arg2 = 100;
	// msg.obj = i + "";
	// handler.sendMessage(msg);
	//
	// try {
	// Thread.sleep(1000);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	// }).start();
	// }
	// } else {
	// if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
	// BasicUtils
	// .toast(getString(R.string.str_mine_checkfail));
	// flag_tel = false;
	// tv_mine_resulttel
	// .setText(getString(R.string.str_mine_checkfail));
	// } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
	// BasicUtils.toast(getString(R.string.str_mine_getfail));
	// tv_mine_resulttel
	// .setText(getString(R.string.str_mine_getfail));
	// } else if (result == 100 && event == 100) {
	// if (!flag_tel) {
	// if (!msg.obj.toString().trim().equals("0")) {
	// bt_mine_gettel
	// .setText(msg.obj.toString()
	// + getString(R.string.str_mine_waitcheck));
	// flag_gettel = true;
	// } else {
	// bt_mine_gettel
	// .setText(getString(R.string.str_mine_gettel));
	// flag_gettel = false;
	// }
	// }
	// }
	// // ((Throwable) data).printStackTrace();
	// }
	//
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// }
	//
	// };

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mDialog != null)
			mDialog.dismiss();
		if (mRefreshBroadcastReceiver != null)
			context.unregisterReceiver(mRefreshBroadcastReceiver);
	}

	private void setView() {
		// TODO Auto-generated method stub
		try {
			tv_word.setText("" + perMain.getIntro().length());
			ed_nickname.setText(perMain.getNick());
			ed_mine_myalipay.setText(perMain.getAlipay());
			if (!TextUtils.isEmpty(perMain.getIntro()))
				ed_heart.setText(perMain.getIntro());
			else
				ed_heart.setHint(R.string.str_mine_hearthint);

			if (TextUtils.isEmpty(perMain.getBirthday())) {
				tv_year.setText("1990");
				tv_month.setText("01");
				tv_day.setText("01");
			} else {
				String[] date = perMain.getBirthday().split("-");
				tv_year.setText(date[0]);
				tv_month.setText(date[1]);
				tv_day.setText(date[2]);

			}
			if (!TextUtils.isEmpty(perMain.getHeight()))
				tv_height.setText(perMain.getHeight());
			else
				tv_height.setText("170");
			if (TextUtils.isEmpty(perMain.getCellphone()))
				ed_tel.setHint(R.string.str_phone_input_hint);
			else
				ed_tel.setText(perMain.getCellphone());
			if (TextUtils.isEmpty(perMain.getAddress()))
				ed_mine_ad.setHint(R.string.str_addr_input_hint);
			else
				ed_mine_ad.setText(perMain.getAddress());
			if (perMain.getGender() != null && perMain.getGender().equals("1")) {
				gender = "1";
				tv_gender.setText(genders[0]);
			} else {
				gender = "2";
				tv_gender.setText(genders[1]);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void postHttpDate() {
		int cache_time = 0;
		RequestParams params = new RequestParams();
		String key = "me/info/change";

		params.addBodyParameter("cur_user", Conf.userID); // 用户ID
		params.addBodyParameter("intro", ed_heart.getText().toString().trim());// 内心独白
		params.addBodyParameter("nick", ed_nickname.getText().toString().trim());// 昵称
		params.addBodyParameter("birthday", tv_year.getText().toString().trim()
				+ "-" + tv_month.getText().toString().trim() + "-"
				+ tv_day.getText().toString().trim());// 生日
		params.addBodyParameter("height", tv_height.getText().toString().trim());// 身高
		if (tel == null || tel.trim().equals(""))
			params.addBodyParameter("cellphone", ed_tel.getText().toString()
					.trim());// 手机号码
		else
			params.addBodyParameter("cellphone", tel);// 手机号码
		params.addBodyParameter("alipay", ed_mine_myalipay.getText().toString()
				.trim());// 支付宝帐号
		if (flag_tel) {
			params.addBodyParameter("is_verified", "1");
		} else {
			params.addBodyParameter("is_verified", "0");
		}
		params.addBodyParameter("address", ed_mine_ad.getText().toString()
				.trim());// 居住地
		if (gender == null)
			gender = "1";
		params.addBodyParameter("gender", gender);
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestPOST(context, key, params, key, cache_time,
				new CacheRequestCallBack() {

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub
						if (mDialog != null) {
							mDialog.dismiss();
						}
						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
						if (json.equals("")) {
//							BasicUtils
//									.toast(getString(R.string.str_net_register));
							return;
						}
					}

					@Override
					public void onData(String json) {
						// TODO Auto-generated method stub
						if (mDialog != null) {
							mDialog.dismiss();
						}
						if (json.equals("")) {
							return;
						}
						JSONObject jsonObj;
						try {
							jsonObj = new JSONObject(json);
							if (jsonObj.getString("status").equals("200")) {
								Conf.userName = ed_nickname.getText()
										.toString().trim();
								BasicUtils.toast(StringUtils.getResourse(
										R.string.str_changepwd_success));
								context.finish();
								Intent intent = new Intent(
										"jimome.action.myselfrefresh");
								context.sendBroadcast(intent);
							} else {
								BasicUtils.toast(StringUtils.getResourse(
										R.string.str_request_fail));
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		// kjh.post(url, params, new HttpCallBack() {
		//
		// @Override
		// public void onSuccess(Object obj) {
		// // TODO Auto-generated method stub
		// JSONObject jsonObj;
		// try {
		// jsonObj = new JSONObject(obj.toString());
		// if (jsonObj.getString("status").equals("200")) {
		// Conf.userName = ed_nickname.getText().toString().trim();
		// ViewInject.toast(getResources().getString(
		// R.string.str_changepwd_success));
		// context.finish();
		// Intent intent = new Intent(
		// "jimome.action.myselfrefresh");
		// context.sendBroadcast(intent);
		// } else {
		// ViewInject.toast(getResources().getString(
		// R.string.str_request_fail));
		// }
		//
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } finally {
		// if (mDialog != null) {
		// mDialog.dismiss();
		// }
		// }
		// }
		//
		// @Override
		// public void onLoading(long count, long current) {
		// // TODO Auto-generated method stub
		// }
		//
		// @Override
		// public void onFailure(Throwable t, int errorNo, String strMsg) {
		// // TODO Auto-generated method stub
		// if (mDialog != null) {
		// mDialog.dismiss();
		// }
		// ExitManager.getScreenManager().intentLogin(context,
		// StringUtils.httpRsponse(t.toString()));
		// }
		//
		// });

	}

	private void waitDialog() {
		mDialog.show();
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
		tv_title.setText(StringUtils.getResourse(R.string.str_myinfo));
		layout_text_chong.setVisibility(View.VISIBLE);
		tv_text_chong.setText(R.string.str_mine_save);
		tv_text_chong.setTextColor(Color.WHITE);
		tv_text_chong.setPadding(15, 10, 15, 10);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			tv_text_chong.setBackground(getResources().getDrawable(
					R.drawable.shape_blue));
		} else {
			tv_text_chong.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.shape_blue));
		}

		layout_back.setVisibility(View.VISIBLE);
		tv_title.setFocusable(true);
		tv_title.setFocusableInTouchMode(true);
		tv_title.requestFocus();
		year = context.getResources().getStringArray(R.array.year);
		month = context.getResources().getStringArray(R.array.month);
		day = context.getResources().getStringArray(R.array.day);

		height = context.getResources().getStringArray(R.array.height);

		ed_heart.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				tv_word.setText(s.toString().length() + "");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});
		ed_tel.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s.toString().length() == 11)
					bt_mine_gettel.setBackgroundResource(R.color.lightblue);
				else
					bt_mine_gettel.setBackgroundResource(R.color.transgray);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});
		ed_mine_checknum.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s.toString().length() >= 4)
					bt_mine_checktel.setBackgroundResource(R.color.lightblue);
				else
					bt_mine_checktel.setBackgroundResource(R.color.transgray);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});
		getHttpData();// 网络请求
	}
}

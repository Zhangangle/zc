package com.jimome.mm.activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.unjiaoyou.mm.R;
import com.jimome.mm.adapter.TalkGVAdapter;
import com.jimome.mm.adapter.TalkMSGAdapter;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.AlipayUtils;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.BitmapUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.LogUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.jimome.mm.view.ChatListView;
import com.jimome.mm.view.ChatListView.IChatListViewListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 私聊页面
 * 
 * @author admin
 * 
 */
@ContentView(R.layout.activity_talk)
public class TalkActivity extends BaseFragmentActivity implements
		IChatListViewListener, OnItemClickListener {

	@ViewInject(R.id.tv_title)
	private TextView title;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	@ViewInject(R.id.btn_jiahei)
	private Button btn_jiahei;
	@ViewInject(R.id.tv_chat_top_height)
	private TextView tv_chat_top_height;
	@ViewInject(R.id.tv_chat_top_age)
	private TextView tv_chat_top_age;
	@ViewInject(R.id.img_talk_gift)
	private TextView img_talk_gift;
	@ViewInject(R.id.lv_talk)
	private ChatListView lv_talk;// 聊天记录
	@ViewInject(R.id.ed_talk)
	private EditText ed_talk;// 输入框
	@ViewInject(R.id.btn_talk_vip)
	private Button btn_talk_vip;// vip跳转
	@ViewInject(R.id.layout_send)
	private LinearLayout layout_send;
	@ViewInject(R.id.btn_chatmsg_type)
	private Button btn_chatmsg_type;
	@ViewInject(R.id.btn_chatmsg_voice)
	private Button btn_chatmsg_voice;
	@ViewInject(R.id.gv_talk_say)
	private GridView gv_talk_say;
	@ViewInject(R.id.btn_phone)
	private Button btn_phone;
	private Context context;
	private TalkGVAdapter talkgvAdapter;
	private boolean flag_gv = false;
	public static BaseJson person;
	private BaseJson sender;
	private TalkMSGAdapter talkmsgAdapter;
	// private Dialog mDialog;
	private int delay = 10000; // 10s(延迟10秒执行)
	private int period = 5000; // 5s(每10秒循环)
	private String lastTime = "0";
	private int totalHeight;
	private String message;
	private String oppIcon;// 对方头像地址
	private String[] add;// 加黑或举报
	private String[] report;// 举报内容
	private boolean flag_jia = false;
	private AlertDialog addDialog, reDialog;
	private int report_id;
	private Dialog cancelDialog;
	private String[] choice = { "拍照上传", "本地图片上传" };
	/*** 拍照上传 ***/
	private static final int UPLOAD_CAMERA = 1;
	/*** 本地上传 ***/
	private static final int UPLOAD_LOCAL = 2;
	/*** 结果 ***/
	private static final int PHOTO_REQUEST_CUT = 3;
	private static final int UPLOAD_VIDEO = 4;
	private Bitmap mBitmap;
	private byte[] byte_img;
	private String icon;
	private int page = 1;
	private String flag = "0";// 发送消息参数
	private BaseJson gift;// 礼物
	// private String is_vip = "";
	// public static String can_send = "";//
	private String tip = "";// 男性用户是否为VIP
	// private String info_tip = "";// 女性用户提示信息
	private String info_goto = "";// 跳转那个页面
	private boolean info_refresh = false;
	// public static String info_gift = "";//不是VIP,页面有免费渠道 提示送礼
	public static String talk_id = "";
	public AlipayUtils payUtils;
	DisplayImageOptions options;// 图片显示参数
	public static boolean isShowed = false;

	@Override
	public void onItemClick(AdapterView<?> view, View arg1, int pos, long arg3) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.gv_talk_say:
			if (flag_gv) {
				flag_gv = false;
				gv_talk_say.setVisibility(View.GONE);
			}
			if (pos == 0) {// 图片
				ShowChoiceDialog();
			} else if (pos == 1) {// 专属秀
				// ViewInject.toast("设备加载中...");
				// Intent videointent = new Intent(context,
				// VideoRecorderActivity.class);
				// videointent.putExtra("type", "6");
				// videointent.putExtra("text", "");
				// startActivity(videointent);
				try {
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("video/*");
					intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 600);
					startActivityForResult(intent, UPLOAD_VIDEO);
				} catch (Exception e) {
					// TODO: handle exception
				}

			} else if (pos == 2) {// 礼物
				StatService.onEvent(context, "dialog-plus-gift", "eventLabel",
						1);
				sendGift();
			} else if (pos == 3) {// 搭讪助手
				StatService.onEvent(context, "dialog-plus-accost",
						"eventLabel", 1);
				Intent intent = new Intent(context, FragmentToActivity.class);
				intent.putExtra("who", "helper");
				startActivity(intent);
			}
			break;
		case R.id.lv_talk:
			break;
		}
	}

	private void sendGift() {
		Intent intent = new Intent(context, GiftStoreActivity.class);
		Bundle bundle = new Bundle();
		BaseJson personal = new BaseJson();
		personal.setUser_id(person.getSender());
		personal.setNick(person.getNick());
		personal.setFlag("2");
		bundle.putSerializable("person", personal);
		intent.putExtras(bundle);
		intent.putExtra("type", "msg");
		intent.putExtra("dialog_id", person.getDialog_id());
		startActivity(intent);
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Conf.flagTalk = true;
		context = TalkActivity.this;
		ViewUtils.inject(this);
		ExitManager.getScreenManager().pushActivity(this);
		Intent intent = getIntent();
		person = (BaseJson) intent.getSerializableExtra("person");
		// 测试
		// person.setSender("113383");//113383;113376
		//
		talk_id = person.getSender();
		title.setText(person.getNick());
		tv_chat_top_age.setText(person.getAge()
				+ getResources().getString(R.string.str_sui) + "/");
		if (person.getHeight() != null && !person.getHeight().trim().equals("")) {
			tv_chat_top_height.setText(person.getHeight()
					+ context.getResources()
							.getString(R.string.str_heightuntil));
		} else {
			tv_chat_top_height.setText("身高保密");
		}
		initView();
		talkgvAdapter = new TalkGVAdapter(this);

		ed_talk.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					ed_talk.setBackgroundResource(R.drawable.chat_edit_focus);
					InputMethodManager imm = (InputMethodManager) ed_talk
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
				} else {
					ed_talk.setBackgroundResource(R.drawable.chat_edit_normal);
				}
			}
		});
		ed_talk.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s.toString().trim().length() > 0) {
				} else {
					ed_talk.setHint(getString(R.string.str_chatmsg_hint));
				}
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

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.action.talkmsg");
		intentFilter.addAction("jimome.action.sendtext");
		intentFilter.addAction("jimome.action.sendpic");
		intentFilter.addAction("jimome.action.sendgift");
		intentFilter.addAction("jimome.action.sendshow");
		registerReceiver(mRefreshBroadcastReceiver, intentFilter);
		getHttpData(0);
		// startBroad();
		gv_talk_say.setOnItemClickListener(this);
		gv_talk_say.setVisibility(View.VISIBLE);
		gv_talk_say.setAdapter(talkgvAdapter);
		flag_gv = true;
	}

	private void initView() {
		// TODO Auto-generated method stub
		layout_back.setVisibility(View.VISIBLE);
		btn_jiahei.setVisibility(View.VISIBLE);
		btn_phone.setVisibility(View.VISIBLE);
		lv_talk.setPullLoadEnable(false);
		// 允许下拉
		lv_talk.setPullRefreshEnable(true);
		// 设置监听器
		lv_talk.setChatListViewListener(this);
		lv_talk.pullRefreshing();
		lv_talk.setDividerHeight(0);
		lv_talk.setOnItemClickListener(this);
	}

	private void startBroad() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setAction("com.action.talkmsg");
		AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		int requestCode = 0;
		PendingIntent pendIntent = PendingIntent.getBroadcast(
				getApplicationContext(), requestCode, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// 5秒后发送广播，然后每个10秒重复发广播。广播都是直接发到AlarmReceiver的
		int triggerAtTime = (int) (SystemClock.elapsedRealtime() + delay);
		alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				triggerAtTime, period, pendIntent);
	}

	private void stopBroad() {
		AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent();
		intent.setAction("com.action.talkmsg");
		PendingIntent pendIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// 与上面的intent匹配（filterEquals(intent)）的闹钟会被取消
		alarmMgr.cancel(pendIntent);
	}

	// 接收消息广播
	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.action.talkmsg")) {
				if (talkmsgAdapter != null) {
					talkmsgAdapter.insertList((List<BaseJson>) intent
							.getSerializableExtra("msg"));
					talkmsgAdapter.notifyDataSetChanged();
					lv_talk.setSelection(lv_talk.getCount() - 1);
				} else {
					talkmsgAdapter = new TalkMSGAdapter(
							TalkActivity.this,
							(List<BaseJson>) intent.getSerializableExtra("msg"),
							person.getSender());
					lv_talk.setAdapter(talkmsgAdapter);
					lv_talk.setSelection(lv_talk.getCount() - 1);
				}

			} else if (action.equals("jimome.action.sendtext")) {
				flag = "0";
				message = intent.getStringExtra("text");
				getHttpData(2);
			} else if (action.equals("jimome.action.sendpic")) {
				flag = "1";
				message = intent.getStringExtra("text");
				getHttpData(2);
			} else if (action.equals("jimome.action.sendgift")) {

				flag = "2";
				message = intent.getStringExtra("text");
				gift = (BaseJson) intent.getSerializableExtra("gift");
				BaseJson upload = (BaseJson) intent
						.getSerializableExtra("gifttime");
				BaseJson send = new BaseJson();
				send.setSender(Conf.userID);
				send.setSender_icon(Conf.userImg);
				if (gift != null) {
					send.setImg(gift.getImg());// 图片地址
					send.setName(gift.getName());
					send.setCoin(gift.getCoin());// 金币数;
					send.setCharm(gift.getCharm());
				}
				send.setFlag(flag);
				send.setTime(upload.getTime());
				send.setFormat_time(upload.getFormat_time());
				if (talkmsgAdapter != null) {
					talkmsgAdapter.insert(send);
					talkmsgAdapter.notifyDataSetChanged();
				} else {
					List<BaseJson> list = new ArrayList<BaseJson>();
					list.add(send);
					talkmsgAdapter = new TalkMSGAdapter(TalkActivity.this,
							list, person.getSender());
					lv_talk.setAdapter(talkmsgAdapter);
				}
				lv_talk.setSelection(lv_talk.getCount() - 1);
				// ed_talk.setText("");

			} else if (action.equals("jimome.action.sendshow")) {
				flag = "3";
				message = intent.getStringExtra("text");
				getHttpData(2);
			}
		}
	};

	// 返回键方法重写
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 输入法是否弹出
			if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
				// 关闭输入法
				InputMethodManager imm = (InputMethodManager) getApplicationContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);

				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			} else {
				getHttpData(8);
			}
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ExitManager.getScreenManager().pullActivity(this);
		if (mDialog != null)
			mDialog.dismiss();
		Conf.flagTalk = false;
		talk_id = "";
		try {

			// stopBroad();
			if (mRefreshBroadcastReceiver != null)
				unregisterReceiver(mRefreshBroadcastReceiver);
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(ed_talk.getWindowToken(), 0);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void getHttpData(final int type) {
		RequestParams params = new RequestParams();
		String key = "";
		if (type == 0 || type == 6) {
			key = "msg/messages";
			params.addQueryStringParameter("dialog_id", person.getDialog_id());
			params.addQueryStringParameter("cur_user", Conf.userID);
			params.addQueryStringParameter("user_id", person.getSender());
			params.addQueryStringParameter("page", page + "");
			if (page == 1)
				waitDialog();
		} else if (type == 2) {// 发送消息
			key = "msg/send";
			params.addQueryStringParameter("flag", flag);
			params.addQueryStringParameter("sender", Conf.userID);
			params.addQueryStringParameter("dialog_id", person.getDialog_id());
			params.addQueryStringParameter("receiver", person.getSender());// 113376;//111949
			params.addQueryStringParameter("text", message);

		} else if (type == 1) {// 接收消息
			key = "msg/receive";
			params.addQueryStringParameter("dialog_id", person.getDialog_id());
			params.addQueryStringParameter("cur_user", Conf.userID);
			params.addQueryStringParameter("t", lastTime);
		} else if (type == 3) {// 加黑
			key = "photo/black/add";
			params.addQueryStringParameter("cur_user", Conf.userID);
			params.addQueryStringParameter("user_id", person.getSender());
		} else if (type == 4) {// 举报
			key = "photo/report";
			params.addQueryStringParameter("user1", Conf.userID);
			params.addQueryStringParameter("user2", person.getSender());
			params.addQueryStringParameter("flag", report_id + "");
		} else if (type == 5) {// 取消加黑
			key = "photo/black/cancel";
			params.addQueryStringParameter("cur_user", Conf.userID);
			params.addQueryStringParameter("user_id", person.getSender());
		} else if (type == 7) {// 查看电话号码
			key = "msg/cellphone/get";
			params.addQueryStringParameter("cur_user", Conf.userID);
			params.addQueryStringParameter("user_id", person.getSender());
		} else if (type == 8) {// 退出请求
			key = "msg/exit";
			params.addQueryStringParameter("cur_user", Conf.userID);
			params.addQueryStringParameter("sender", person.getSender());
			params.addQueryStringParameter("dialog_id", person.getDialog_id());
		}

		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key, 0,
				new CacheRequestCallBack() {

					@Override
					public void onData(String json) {
						// TODO Auto-generated method stub
						// LogUtils.printLogE("上传成功", json);
						try {
							if (json.equals("")) {
								// BasicUtils.toast(StringUtils
								// .getResourse(R.string.str_net_register));
								if (type == 8)
									finish();
								return;
							}
							if (type == 0 || type == 1) {
								sender = new Gson().fromJson(json,
										BaseJson.class);
								if (sender.getStatus().equals("200")) {
									// if (sender.getIs_vip() != null)
									// is_vip = sender.getIs_vip();
									// if (sender.getCan_send() != null)
									// can_send = sender.getCan_send();
									// if (sender.getTip() != null)
									// tip = sender.getTip();
									// if (sender.getInfo_tip() != null)
									// info_tip = sender.getInfo_tip();
									// if (sender.getGo_to() != null)
									// info_goto = sender.getGo_to();
									// if (sender.getGift_tip() != null)
									// info_git = sender.getGift_tip();
									if (type == 0) {
										isShowed = true;
										if (sender.getUrl() != null
												&& !sender.getUrl().trim()
														.equals("")) {
											tip = sender.getUrl();
											showGitDialog("talk", "2", "");
										}
									}
									for (int i = 0; i < sender.getMessages()
											.size(); i++) {
										if (!sender.getMessages().get(i)
												.getSender().trim()
												.equals(Conf.userID)) {
											lastTime = sender.getMessages()
													.get(i).getTime();
											if (type == 0) {
												oppIcon = sender.getMessages()
														.get(i)
														.getSender_icon();
											} else {
												sender.getMessages()
														.get(i)
														.setSender_icon(oppIcon);
											}
										}
									}
									if (talkmsgAdapter != null) {
										talkmsgAdapter.insertList(sender
												.getMessages());
										talkmsgAdapter.notifyDataSetChanged();
										lv_talk.setSelection(lv_talk.getCount() - 1);
									} else {
										talkmsgAdapter = new TalkMSGAdapter(
												TalkActivity.this, sender
														.getMessages(), person
														.getSender());
										if (isShowed) {
											talkmsgAdapter.setData(sender);
										}
										lv_talk.setAdapter(talkmsgAdapter);
										lv_talk.setSelection(lv_talk.getCount() - 1);
									}
								} else {
									if (type == 0 && page > 1)
										--page;
								}
							} else if (type == 2) {
								BaseJson upload = new Gson().fromJson(json,
										BaseJson.class);
								if (upload.getStatus().equals("200")) {
									BaseJson send = new BaseJson();
									send.setSender(Conf.userID);
									send.setSender_icon(Conf.userImg);
									if (flag.equals("0")) {
										send.setText(message);
										ed_talk.setText("");
									} else if (flag.equals("1")) {
										send.setUrl(Conf.IMAGE_SERVER
												+ "xiaoxitupian/" + message
												+ upload.getStyle());
									} else if (flag.equals("2")) {
										if (gift != null) {
											send.setImg(gift.getImg());// 图片地址
											send.setName(gift.getName());
											send.setCoin(gift.getCoin());// 金币数;
											send.setCharm(gift.getCharm());
										}
									} else if (flag.equals("3")) {
										send.setSmall_url(Conf.IMAGE_SERVER
												+ "zhuanshuxiusuoluetu/"
												+ message + upload.getStyle());// 缩略图地址
										send.setUrl(Conf.VIDEO_SERVER
												+ "zhuanshuxiu/"
												+ message.substring(0,
														message.indexOf(".jpg"))
												+ ".mp4");// 视频地址
									}

									send.setFlag(flag);
									send.setTime(upload.getTime());
									send.setFormat_time(upload.getFormat_time());
									if (talkmsgAdapter != null) {
										talkmsgAdapter.insert(send);
										talkmsgAdapter.notifyDataSetChanged();
									} else {
										List<BaseJson> list = new ArrayList<BaseJson>();
										list.add(send);
										talkmsgAdapter = new TalkMSGAdapter(
												TalkActivity.this, list, person
														.getSender());
										lv_talk.setAdapter(talkmsgAdapter);
									}
									lv_talk.setSelection(lv_talk.getCount() - 1);
								} else if (upload.getStatus().trim()
										.equals("180")) {// 男性用户提示
									if (upload.getTip() != null) {
										tip = upload.getTip();
										sendGift();
										// showGitDialog("talk", "1", "");
									}
								} else if (upload.getStatus().trim()
										.equals("181")) {// 女性用户提示
									if (upload.getTip() != null)
										tip = upload.getTip();
									if (upload.getGo_to() != null)
										info_goto = upload.getGo_to();
									feMaleDialogTip();
								}
							} else if (type == 3) {

								if (json.contains("200")) {
									flag_jia = true;
									Toast.makeText(
											context,
											getString(R.string.str_person_blacksuccess),
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											context,
											getString(R.string.str_person_blackfail),
											Toast.LENGTH_SHORT).show();
								}
							} else if (type == 4) {
								if (json.contains("200")) {
									flag_jia = true;
									Toast.makeText(
											context,
											getString(R.string.str_request_success),
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											context,
											getString(R.string.str_request_fail),
											Toast.LENGTH_SHORT).show();
								}

							} else if (type == 5) {
								if (json.contains("200")) {
									flag_jia = false;
									Toast.makeText(
											context,
											getString(R.string.str_person_blacksuccess),
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											context,
											getString(R.string.str_person_blackfail),
											Toast.LENGTH_SHORT).show();
								}

							} else if (type == 6) {

								sender = new Gson().fromJson(json,
										BaseJson.class);
								if (sender.getStatus().equals("200")) {
									for (int i = 0; i < sender.getMessages()
											.size(); i++) {
										if (!sender.getMessages().get(i)
												.getSender().trim()
												.equals(Conf.userID)) {
											if (type == 0) {
												oppIcon = sender.getMessages()
														.get(i)
														.getSender_icon();
											} else {
												sender.getMessages()
														.get(i)
														.setSender_icon(oppIcon);
											}
										}
									}
									if (talkmsgAdapter != null) {
										talkmsgAdapter.insertAgo(sender
												.getMessages());
										talkmsgAdapter.notifyDataSetChanged();
									} else {
										talkmsgAdapter = new TalkMSGAdapter(
												TalkActivity.this, sender
														.getMessages(), person
														.getSender());
										lv_talk.setAdapter(talkmsgAdapter);
									}
									lv_talk.setSelection(lv_talk.getCount() - 1);
								} else {
									if (type == 6 && page > 1)
										--page;
								}

							} else if (type == 7) {
								BaseJson data = new Gson().fromJson(json,
										BaseJson.class);
								if (data.getStatus().equals("200"))
									showPhoneDialog(data.getTip());
							} else if (type == 8) {
								BaseJson data = new Gson().fromJson(json,
										BaseJson.class);
								if (data.getStatus().equals("200")) {
									tip = data.getImg();
									showGitDialog("finish", Conf.gender,
											data.getText());
								} else
									// onBackPressed();
									finish();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							lv_talk.stopRefresh();
							if (mDialog != null) {
								mDialog.dismiss();
							}
							if (type == 3) {
								if (addDialog != null)
									addDialog.dismiss();
							} else if (type == 4) {
								if (reDialog != null) {
									reDialog.dismiss();
								}
							} else if (type == 5) {
								if (cancelDialog != null)
									cancelDialog.dismiss();
							}
						}
					}

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub
						// LogUtils.printLogE("聊天异常", strMsg);
						if (type == 0) {
							Toast.makeText(context,
									"网络加载失败，请检查您的网络  " + result,
									Toast.LENGTH_SHORT).show();
						}
						lv_talk.stopRefresh();
						if (mDialog != null) {
							mDialog.dismiss();
						}
						if (type == 3) {
							if (addDialog != null)
								addDialog.dismiss();
						} else if (type == 4) {
							if (reDialog != null) {
								reDialog.dismiss();
							}
						} else if (type == 5) {
							if (cancelDialog != null)
								cancelDialog.dismiss();
						} else if (type == 8)
							// onBackPressed();
							finish();
						if (type == 0 || type == 6 && page > 1)
							--page;
						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
					}
				});
	}

	protected void showGitDialog(final String kind, final String gender,
			String text) {
		// TODO Auto-generated method stub
		// if (can_send.equals("0")) {
		final Dialog dialog = BasicUtils.showDialog(context,
				R.style.BasicDialog);
		dialog.setContentView(R.layout.dialog_rechargevip);
		TextView tv_tip = ((TextView) dialog.findViewById(R.id.tv_dialog_msg));
		tv_tip.setText("　　" + tip);
		tv_tip.setTextColor(getResources().getColor(R.color.darkgray));
		tv_tip.setVisibility(View.GONE);
		View view_dialog_line = ((View) dialog
				.findViewById(R.id.view_dialog_line));
		view_dialog_line.setVisibility(View.GONE);
		if (kind.equals("finish")) {
			TextView tv_texttip = ((TextView) dialog
					.findViewById(R.id.tv_dialog_textmsg));
			tv_texttip.setVisibility(View.VISIBLE);
			if (text != null && !text.trim().equals(""))
				tv_texttip.setText(text);
			View view_dialog_textline = ((View) dialog
					.findViewById(R.id.view_dialog_textline));
			view_dialog_textline.setVisibility(View.VISIBLE);
			if (tip == null || tip.trim().equals(""))
				tip = Conf.GIFT_AD;
			LinearLayout llayout_img = (LinearLayout) dialog
					.findViewById(R.id.llayout_img);
			llayout_img.setPadding(10, 0, 10, 0);
		}
		final ImageView ad = ((ImageView) dialog
				.findViewById(R.id.img_dialog_ad));
		ad.setVisibility(View.VISIBLE);
		if (kind.equals("finish"))
			options = ImageLoadUtils.options;
		else
			options = ImageLoadUtils.optionsRounded;
		ImageLoadUtils.imageLoader.displayImage(tip, ad, options,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub
						// if (Conf.gender.equals("1"))
						// ad.setBackgroundResource(R.drawable.default_female);
						// else
						// ad.setBackgroundResource(R.drawable.default_male);
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						// TODO Auto-generated method stub
						// if (Conf.gender.equals("1"))
						// ad.setBackgroundResource(R.drawable.default_female);
						// else
						// ad.setBackgroundResource(R.drawable.default_male);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						try {
							int height = (Conf.width * loadedImage.getHeight())
									/ loadedImage.getWidth();
							if (height > Conf.height / 2)
								height = Conf.height / 2;
							ad.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
									android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
									height));

						} catch (OutOfMemoryError error) {
							// TODO: handle exception
						}
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}
				});
		dialog.setCanceledOnTouchOutside(true);
		Button vip = ((Button) dialog.findViewById(R.id.btn_dialog_sure));
		if (kind.equals("finish"))
			vip.setText(R.string.str_talk_payvip);
		else if (gender.equals("1"))
			vip.setText(R.string.str_dialog_viptext);
		else
			vip.setText(R.string.str_dialog_girltip);
		vip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Intent intent = new Intent(context, FragmentToActivity.class);
				if (kind.equals("finish")) {
					intent.putExtra("who", "vip");
					StatService
							.onEvent(context, "dialog-vip2", "eventLabel", 1);
					// payUtils = new AlipayUtils(context, 0, "4");
					// payUtils.getHttpData();
				} else if (gender.equals("1")) {
					intent.putExtra("who", "vip");
					StatService.onEvent(context, "dialog-vip", "eventLabel", 1);
					// payUtils = new AlipayUtils(context, 0, "4");
					// payUtils.getHttpData();
				} else {
					intent.putExtra("who", "meili");
				}
				startActivity(intent);
			}
		});

		Button gift = ((Button) dialog.findViewById(R.id.btn_dialog_cancle));
		if (kind.equals("finish"))
			gift.setText(R.string.str_talk_finish);
		else if (gender.equals("1"))
			gift.setText(R.string.str_dialog_sendgift);
		else
			gift.setText(R.string.str_dialog_girlcancel);
		gift.setTextColor(Color.WHITE);
		gift.setBackgroundColor(getResources().getColor(R.color.lightblue));
		gift.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				try {
					if (kind.equals("finish")) {
						StatService.onEvent(context, "dialog-bachelordom",
								"eventLabel", 1);
						// onBackPressed();
						finish();
						if (MainFragmentActivity.list_ad != null
								&& MainFragmentActivity.list_ad.size() > 0) {
							Intent intent = new Intent(context,
									ViewPageActivity.class);
							intent.putExtra("list_image",
									(Serializable) MainFragmentActivity.list_ad);
							intent.putExtra("pos", 0);
							startActivity(intent);
						}
					} else if (gender.equals("1")) {
						StatService.onEvent(context, "dialog-gift-goddess",
								"eventLabel", 1);
						Intent intent = new Intent(context,
								GiftStoreActivity.class);
						Bundle bundle = new Bundle();
						BaseJson personal = new BaseJson();
						personal.setUser_id(person.getSender());
						personal.setNick(person.getNick());
						personal.setFlag("2");
						bundle.putSerializable("person", personal);
						intent.putExtras(bundle);
						intent.putExtra("type", "msg");
						intent.putExtra("dialog_id", person.getDialog_id());
						startActivity(intent);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		dialog.show();
	}

	private void getTotalHeightofListView(ListView listView) {
		ListAdapter mAdapter = listView.getAdapter();
		if (mAdapter == null) {
			return;
		}
		for (int i = 0; i < mAdapter.getCount(); i++) {
			View mView = mAdapter.getView(i, null, listView);
			mView.measure(
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			totalHeight += mView.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (mAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	@OnClick({ R.id.ed_talk, R.id.layout_back, R.id.img_talk_gift,
			R.id.btn_jiahei, R.id.btn_chatmsg_type, R.id.btn_talk_vip,
			R.id.btn_chatmsg_voice, R.id.btn_phone })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.ed_talk:
			flag_gv = false;
			gv_talk_say.setVisibility(View.GONE);
			lv_talk.setSelection(lv_talk.getCount() - 1);
			break;
		case R.id.layout_back:
			getHttpData(8);
			break;
		case R.id.img_talk_gift:
			StatService.onEvent(context, "dialog-float-gift", "eventLabel", 1);
			sendGift();
			break;
		case R.id.btn_jiahei:
			if (!flag_jia) {
				add = getResources().getStringArray(R.array.addblack);
				report = getResources().getStringArray(R.array.report);
				addDialog = new AlertDialog.Builder(context).setItems(add,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int pos) {
								// TODO Auto-generated method stub
								if (pos == 0) {
									getHttpData(3);
								} else {
									reDialog = new AlertDialog.Builder(context)
											.setItems(
													report,
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dia,
																int pos) {
															// TODO
															// Auto-generated
															// method stub
															report_id = pos;
															getHttpData(4);
														}
													}).create();
									reDialog.show();
								}
								dialog.dismiss();
							}
						}).create();
				addDialog.show();

			} else {
				cancelDialog = BasicUtils.showDialog(context,
						R.style.BasicDialog);
				cancelDialog.setContentView(R.layout.dialog_jiahei);
				cancelDialog.setCanceledOnTouchOutside(false);
				((TextView) cancelDialog.findViewById(R.id.tv_dialog_msg))
						.setText(StringUtils.getResourse(R.string.str_personal_unjia));

				((Button) cancelDialog.findViewById(R.id.btn_dialog_cancle))
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								cancelDialog.dismiss();
							}
						});
				((Button) cancelDialog.findViewById(R.id.btn_dialog_sure))
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								// 上传事件
								getHttpData(5);
							}
						});
				cancelDialog.show();
			}
			break;
		case R.id.btn_chatmsg_type:
			StatService.onEvent(context, "dialog-plus", "eventLabel", 1);
			if (ed_talk.getText().toString().trim().length() < 1) {
				if (flag_gv) {
					gv_talk_say.setVisibility(View.GONE);
					flag_gv = false;
				} else {
					gv_talk_say.setVisibility(View.VISIBLE);
					gv_talk_say.setAdapter(talkgvAdapter);
					flag_gv = true;
				}
			} else if (BasicUtils.containsEmoji(ed_talk.getText().toString()
					.trim())) {
				Toast.makeText(context, StringUtils.getResourse(R.string.str_nickname_tip2),
						Toast.LENGTH_SHORT).show();
			}
			closeInputMethod();
			break;
		case R.id.btn_talk_vip:
			Intent intent = new Intent(new Intent(this,
					FragmentToActivity.class));
			intent.putExtra("who", "vip");
			this.startActivity(intent);
			break;
		case R.id.btn_chatmsg_voice:
			StatService
					.onEvent(context, "dialog-message-send", "eventLabel", 1);
			message = ed_talk.getText().toString();
			if (TextUtils.isEmpty(message)) {
				Toast.makeText(context, StringUtils.getResourse(R.string.str_send_null),
						Toast.LENGTH_SHORT).show();
				return;
			}
			// if (!can_send.equals("1") && Conf.gender.equals("1")) {
			closeInputMethod();
			// final Dialog dialog = BasicUtils.showDialog(context,
			// R.style.BasicDialog);
			// dialog.setContentView(R.layout.dialog_rechargevip);
			// ((TextView) dialog.findViewById(R.id.tv_dialog_msg))
			// .setText(tip);
			// dialog.setCanceledOnTouchOutside(true);
			// ((Button) dialog.findViewById(R.id.btn_dialog_cancle))
			// .setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View arg0) {
			// // TODO Auto-generated method stub
			// dialog.dismiss();
			// }
			// });
			// ((Button) dialog.findViewById(R.id.btn_dialog_sure))
			// .setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View arg0) {
			// // TODO Auto-generated method stub
			// dialog.dismiss();
			// Intent meiIntent = new Intent(context, FragmentToActivity.class);
			// meiIntent.putExtra("who", "vip");
			// context.startActivity(meiIntent);
			// }
			// });
			// dialog.show();
			// return;
			// }
			// if (info_tip != null && !info_tip.trim().equals("")) {
			// closeInputMethod();
			// final Dialog dialog = BasicUtils.showDialog(context,
			// R.style.BasicDialog);
			// dialog.setContentView(R.layout.dialog_rechargevip);
			// ((TextView) dialog.findViewById(R.id.tv_dialog_msg))
			// .setText(info_tip);
			// dialog.setCanceledOnTouchOutside(true);
			// ((Button) dialog.findViewById(R.id.btn_dialog_cancle))
			// .setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View arg0) {
			// // TODO Auto-generated method stub
			// dialog.dismiss();
			// }
			// });
			// ((Button) dialog.findViewById(R.id.btn_dialog_sure))
			// .setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View arg0) {
			// // TODO Auto-generated method stub
			// dialog.dismiss();
			// LogUtils.printLogE("info_goto", info_goto);
			// if (info_goto.trim().equals("1")) {
			// Intent meiIntent = new Intent(context,
			// FragmentToActivity.class);
			// meiIntent.putExtra("who", "mine");
			// context.startActivity(meiIntent);
			// } else if (info_goto.trim().equals("2")) {
			// Intent shenIntent = new Intent(context,
			// MyShowUploadActivity.class);
			// shenIntent.putExtra("type", 2);
			// startActivity(shenIntent);
			// }
			// info_refresh = true;
			// }
			// });
			//
			// dialog.show();
			// // info_tip = "";
			// return;
			// }
			flag = "0";
			getHttpData(2);
			// ((InputMethodManager)
			// getSystemService(Context.INPUT_METHOD_SERVICE))
			// .hideSoftInputFromWindow(ed_talk.getWindowToken(), 0);
			break;
		case R.id.btn_phone:
			StatService.onEvent(context, "dialog-phone-dial", "eventLabel", 1);
			getHttpData(7);
			break;
		default:
			break;
		}
	}

	/**
	 * 弹出电话提示框
	 */
	private void showPhoneDialog(final String content) {
		// TODO Auto-generated method stub

		final Dialog dialog = BasicUtils.showDialog(context,
				R.style.BasicDialog);
		dialog.setContentView(R.layout.dialog_rechargevip);
		dialog.setCanceledOnTouchOutside(true);
		((TextView) dialog.findViewById(R.id.tv_dialog_msg)).setText(content);
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
						if (Conf.user_VIP.equals("0")) {
							Intent intent = new Intent(context,
									FragmentToActivity.class);
							intent.putExtra("who", "vip");
							startActivity(intent);
						} else {
							// if(StringUtils.hasDigit(content)){
							// String phoneNum =
							// StringUtils.getNumbers(content);
							// Uri uri = Uri.parse("tel:" + phoneNum);
							// Intent call = new Intent(Intent.ACTION_CALL,
							// uri); //直接播出电话
							// startActivity(call);
							// }
						}
					}
				});
		dialog.show();

	}

	private void feMaleDialogTip() {
		if (tip != null && !tip.trim().equals("")) {
			closeInputMethod();
			final Dialog dialog = BasicUtils.showDialog(context,
					R.style.BasicDialog);
			dialog.setContentView(R.layout.dialog_rechargevip);
			((TextView) dialog.findViewById(R.id.tv_dialog_msg)).setText(tip);
			dialog.setCanceledOnTouchOutside(true);
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
							// LogUtils.printLogE("info_goto", info_goto);
							if (info_goto.trim().equals("1")) {
								Intent meiIntent = new Intent(context,
										FragmentToActivity.class);
								meiIntent.putExtra("who", "mine");
								context.startActivity(meiIntent);
							} else if (info_goto.trim().equals("2")) {
								Intent shenIntent = new Intent(context,
										MyShowUploadActivity.class);
								shenIntent.putExtra("type", 2);
								startActivity(shenIntent);
							}
							info_refresh = true;
						}
					});

			dialog.show();
			// info_tip = "";
			// return;
		}
	}

	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();

		if (isOpen) {
			imm.hideSoftInputFromWindow(ed_talk.getWindowToken(), 0);
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
		if (info_refresh) {
			page = 1;
			talkmsgAdapter = null;
			getHttpData(0);
			info_refresh = false;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}

	// 弹出选择图片对话框

	private void ShowChoiceDialog() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(context).setTitle(R.string.str_uploadtitle)
				.setItems(choice, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						if (arg1 == 0) {
							// 调用相机拍照
							if (!BasicUtils.isSDCardAvaliable()) {
								Toast.makeText(
										context,
										StringUtils.getResourse(
												R.string.str_sdnull),
										Toast.LENGTH_SHORT).show();
								return;
							}
							BitmapUtils.initPictureFile();
							// 调用系统的拍照功能
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							int currentapiVersion = android.os.Build.VERSION.SDK_INT;
							try {

								// if (currentapiVersion > 11) {
								// 指定调用相机拍照后照片的储存路径
								intent.putExtra(MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(BitmapUtils.pictureFile));
								// }
								startActivityForResult(intent, UPLOAD_CAMERA);
							} catch (Exception e) {
								// TODO: handle exception
							}

						} else {
							// 调用本地相册
							Intent intent = new Intent(Intent.ACTION_PICK, null);
							intent.setType("image/*");
							startActivityForResult(intent, UPLOAD_LOCAL);
						}
					}

				}).create().show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {

		// 相机拍照
		case UPLOAD_CAMERA:
			try {
				mBitmap = null;
				Options options = new Options();
				options.inJustDecodeBounds = false;
				options.inSampleSize = 2;
				mBitmap = BitmapFactory.decodeFile(
						BitmapUtils.pictureFile.getAbsolutePath(), options);
				// 创建图片缩略图
				if (mBitmap == null)
					return;
				byte_img = BitmapUtils.Bitmap2Bytes(mBitmap);
				icon = BitmapUtils.pictureFile.toString()
						.replace(
								BitmapUtils.pictureFile.toString(),
								Conf.userID + "_" + System.currentTimeMillis()
										+ ".jpg");
				Intent intent = new Intent("jimome.action.uploadtalkpic");
				intent.putExtra("albumimg", icon);
				intent.putExtra("imagepath",
						BitmapUtils.pictureFile.getAbsolutePath());
				// Conf.img_byte = byte_img;
				sendBroadcast(intent);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				finish();

			}
			break;

		// 本地照片
		case UPLOAD_LOCAL:
			try {
				if (data == null) {
					return;
				}

				Uri imageuri = data.getData();
				String[] prStrings = { MediaStore.Images.Media.DATA };
				Cursor imageCursor = managedQuery(imageuri, prStrings, null,
						null, null);
				int imgpath = imageCursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				imageCursor.moveToFirst();
				String image_path = imageCursor.getString(imgpath);

				File photoName = new File(image_path);
				ContentResolver cr = this.getContentResolver();

				mBitmap = null;

				Options options = new Options();
				options.inJustDecodeBounds = false;
				options.inSampleSize = 2;
				mBitmap = BitmapFactory.decodeStream(
						cr.openInputStream(imageuri), null, options);
				// 创建图片缩略图
				byte_img = BitmapUtils.Bitmap2Bytes(mBitmap);
				icon = image_path.replace(image_path, Conf.userID + "_"
						+ System.currentTimeMillis() + ".jpg");
				Intent intent = new Intent("jimome.action.uploadtalkpic");
				intent.putExtra("albumimg", icon);
				intent.putExtra("imagepath", image_path);
				// Conf.img_byte = byte_img;
				sendBroadcast(intent);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				finish();
			}
			break;
		case UPLOAD_VIDEO:
			try {
				String[] media_info = new String[] { MediaStore.Video.Media.DURATION };
				Cursor cursor = this.getContentResolver().query(
						MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
						media_info, "duration < 600000", null, null);
				if (cursor.moveToFirst()) {
					int index = cursor
							.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
					int id = cursor
							.getColumnIndexOrThrow(MediaStore.Video.Media._ID);

					String videopath = cursor.getString(index);
					int videoid = cursor.getInt(id);

					Options options = new Options();
					options.inJustDecodeBounds = false;
					options.inSampleSize = 2;
					// Bitmap videoBmp =
					// MediaStore.Video.Thumbnails.getThumbnail(getActivity().getContentResolver(),
					// videoid, MediaStore.Images.Thumbnails.MINI_KIND,
					// options);
					// byte[] video_data =
					// BitmapUtils.Bitmap2Bytes(videoBmp);
					String selection = MediaStore.Video.Thumbnails.VIDEO_ID
							+ "=?";
					String[] selectionArgs = new String[] { "" + videoid };

					Cursor imgCursor = this.getContentResolver().query(
							MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
							null, selection, selectionArgs, null);
					String videoimgpath = "";
					if (imgCursor.moveToFirst()) {
						int imgPath = imgCursor
								.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
						videoimgpath = imgCursor.getString(imgPath);
					}

					LogUtils.printLogE("视频路径----", videopath + "\n"
							+ videoimgpath);
					Intent intent = new Intent("jimome.action.uploadtalkshow");
					intent.putExtra("videopath", videopath);
					intent.putExtra("videoimg", videoimgpath);
					intent.putExtra("type", "6");
					sendBroadcast(intent);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();

			}

			break;
		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if (talkmsgAdapter == null) {
			page = 1;
			getHttpData(0);
		} else {
			++page;
			getHttpData(6);// 查看以前的数据
		}
		lv_talk.stopRefresh();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

}

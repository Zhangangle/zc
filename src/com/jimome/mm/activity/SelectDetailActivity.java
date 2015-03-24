package com.jimome.mm.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.jimome.mm.adapter.ReplyPraiseAdapter;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.AppUtils;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.BitmapUtils;
import com.jimome.mm.utils.ConfigUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.IOUtils;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.LogUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.jimome.mm.view.AutoScrollViewPager;
import com.jimome.mm.view.MyListView;
import com.jimome.mm.view.PullScrollView;
import com.jimome.mm.view.PullScrollView.OnPullListener;
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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

@ContentView(R.layout.activity_selectdetail)
public class SelectDetailActivity extends BaseFragmentActivity implements
		OnClickListener, OnPullListener, OnItemClickListener {

	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.layout_more)
	private LinearLayout layout_more;
	@ViewInject(R.id.pullview_list)
	private PullScrollView pullview_list;
	@ViewInject(R.id.ed_msgcontent)
	private EditText ed_msgcontent;
	@ViewInject(R.id.btn_sendmsg)
	private Button btn_sendmsg;
	@ViewInject(R.id.relayout_comment)
	private RelativeLayout relayout_comment;
	@ViewInject(R.id.relayout_gift)
	private RelativeLayout relayout_gift;
	@ViewInject(R.id.relayout_bottom)
	private RelativeLayout relayout_bottom;
	@ViewInject(R.id.relayout_praise)
	private RelativeLayout relayout_praise;
	@ViewInject(R.id.relayout_chat)
	private RelativeLayout relayout_chat;
	@ViewInject(R.id.layout_reply)
	private LinearLayout layout_reply;
	@ViewInject(R.id.layout_menu)
	private LinearLayout layout_menu;
	@ViewInject(R.id.img_praise)
	private ImageView img_praise;
	@ViewInject(R.id.tv_request_praise)
	private TextView tv_request_praise;

	private AutoScrollViewPager photoPager;
	private ImageView img_avatar;
	private ImageView img_icon;
	private ImageView img_gifticon;
	private ImageView img_video_play;
	private RelativeLayout relayout_viewpager;
	private FrameLayout frlayout_video;
	// private Button btn_detail_next;
	// private Button btn_buy_vip;
	private LinearLayout layout_dots;
	private Context context;
	private LinearLayout content;
	private LinearLayout layout_vip;
	private TextView tv_comment_num, tv_more_num;
	private TextView tv_str_comment, tv_str_more;
	private TextView tv_nick, tv_intro, tv_time;
	private TextView tv_giftname, tv_gift, tv_gifttime;
	private MyListView listView;
	private ReplyPraiseAdapter replyAdapter;
	private String type = "";
	private BaseJson newPer;
	private BaseJson detailPho, replyPho, requestPho;
	// private Dialog mDialog;
	private int greetpage = 1;
	private int commentpage = 1;
	private List<BaseJson> list_comment, list_greet;
	private String user_id = "";
	private boolean flag_greet = false;
	private String[] report;
	private int report_id;
	private AlertDialog reportDialog;// 举报类型
	private int pos = 0;
	private String detail = "";
	private int videoPos = 0;
	// 循环间隔
	private int interval = 4000;
	private int count = 0;
	private int index = 0;
	private List<ImageView> dots;

	private PopupWindow pw;
	private View myView;
	private LayoutInflater inflater;
	private Upay up;
	private boolean isPay = false;
	private String messageResult = "";
	private Dialog fristPaydialog,pzPaydialog;
	PzPay pzPay;
	long cpid;//订单号
	Dialog dialog;
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				// showPopWindow(context, img_video_play);
				if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY == 1){
					UPay();
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

			default:
				break;
			}
		}

	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(ConfigUtils.DEBUG_UPAY){
//			up.exit();
		}

		//取消⽀支付结果接收
		if(ConfigUtils.DEBUG_PZPAY){
			pzPay.unregisterPayListener();
		}
		if (mDialog != null) {
			mDialog.dismiss();
		}
		if(dialog != null){
			dialog.dismiss();
		}
		if(pzPaydialog != null){
			pzPaydialog.dismiss();
		}
		if (refreshBroadcastReceiver != null)
			unregisterReceiver(refreshBroadcastReceiver);
	
		ExitManager.getScreenManager().pullActivity(this);
	}

	private BroadcastReceiver refreshBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			try {
				if (intent.getAction().equals("jimome.action.getgift")) {
					BaseJson gift = new BaseJson();
					gift = (BaseJson) intent.getSerializableExtra("getgift");
					if (detailPho != null && detailPho.getGifts() != null
							&& detailPho.getGifts().size() > 0) {
						detailPho.getGifts().add(0, gift);
						LinearLayout layout_gift = (LinearLayout) content
								.findViewById(R.id.layout_gift_content);
						layout_gift.removeAllViews();
					} else {
						ArrayList<BaseJson> list = new ArrayList<BaseJson>();
						list.add(gift);
						detailPho.setGifts(list);
					}
					for (int i = 0; i < detailPho.getGifts().size(); i++) {
						LinearLayout layout_gift_content = (LinearLayout) content
								.findViewById(R.id.layout_gift_content);
						View childView = getLayoutInflater().inflate(
								R.layout.layout_receive_gift, null);
						tv_giftname = (TextView) childView
								.findViewById(R.id.tv_giftname);
						tv_gift = (TextView) childView
								.findViewById(R.id.tv_gift);
						tv_gifttime = (TextView) childView
								.findViewById(R.id.tv_gifttime);
						img_gifticon = (ImageView) childView
								.findViewById(R.id.img_gifticon);
						tv_giftname.setText(detailPho.getGifts().get(i)
								.getSender_nick());
						tv_gift.setText(detailPho.getGifts().get(i).getName());
						tv_gifttime.setText(detailPho.getGifts().get(i)
								.getTime());
						setImage(img_gifticon, detailPho.getGifts().get(i)
								.getImg());
						layout_gift_content.addView(childView);
						layout_gift_content
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										Intent intent = new Intent(context,
												GiftStoreActivity.class);
										Bundle bundle = new Bundle();
										bundle.putSerializable("person", newPer);
										intent.putExtras(bundle);
										intent.putExtra("type", "user");
										context.startActivity(intent);
									}
								});
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	private void waitDialog() {
		mDialog.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		ViewUtils.inject(this);
		ExitManager.getScreenManager().pushActivity(this);
		// 优贝支付初始化
		if(ConfigUtils.DEBUG_UPAY){
			up = Upay.initInstance(this, Conf.UPAY_APPKEY, Conf.UPAY_APPSECRET);
		}

		// 首付支付初始化
		if(ConfigUtils.DEBUG_FIRSTPAY){
			 FirstPay.init(this, true, true);
		}

	 //思瑞支付初始化
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
								isPay = true;
//								hejuPay();
//								UPay();
//								FirstPay();
								if(ConfigUtils.HEJUPAY ==2){
									hejuPay();
								}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
									FirstPay();
								}else if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY == 2){
									UPay();
								}
							} else {
								// 支付失败
								LogUtils.printLogE("思瑞支付失败", "订单状态status:"
										+ msg.status + "\n" + "订单号orderid:"
										+ msg.orderid);
								isPay = false;
//								hejuPay();
//								UPay();
//								FirstPay();
								if(ConfigUtils.HEJUPAY ==2){
									hejuPay();
								}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
									FirstPay();
								}else if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY == 2){
									UPay();
								}
							}
						}

					});
		}
		 
		if(Conf.user_VIP.equals("0")){
			handler.sendEmptyMessageDelayed(0, 500);
		}
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("jimome.action.getgift");
		registerReceiver(refreshBroadcastReceiver, intentFilter);
		newPer = (BaseJson) getIntent().getSerializableExtra("photo");
		context = SelectDetailActivity.this;
		if (getIntent().getIntExtra("type", 0) == 1) {
			if (newPer.getUser_id().equals(Conf.userID)) {
				if (!Conf.gender.equals("1")) {
					tv_title.setText(R.string.str_my_detailnv);
				} else {
					tv_title.setText(R.string.str_my_detailnan);
				}
			} else {
				// if (Conf.gender.equals("1")) {
				tv_title.setText(R.string.str_my_detailnv);
				// } else {
				// tv_title.setText(R.string.str_my_detailnan);
				// }
			}
		} else if (getIntent().getIntExtra("type", 0) == 2) {
			tv_title.setText(R.string.str_limitshow_browse);
		} else {
			tv_title.setText(R.string.str_limitshow_browse);
			layout_more.setVisibility(View.VISIBLE);
		}

		if (newPer.getUser_id().equals(Conf.userID))
			relayout_bottom.setVisibility(View.GONE);
		layout_back.setVisibility(View.VISIBLE);
		pullview_list.setheaderViewGone();
		pullview_list.setfooterViewReset();
		content = (LinearLayout) getLayoutInflater().inflate(
				R.layout.layout_selectdetail, null);
		RelativeLayout relayout_reply = (RelativeLayout) content
				.findViewById(R.id.relayout_reply);
		RelativeLayout relayout_more = (RelativeLayout) content
				.findViewById(R.id.relayout_more);
		photoPager = (AutoScrollViewPager) content
				.findViewById(R.id.view_pager);
		img_icon = (ImageView) content.findViewById(R.id.img_select_icon);
		img_video_play = (ImageView) content.findViewById(R.id.img_video_play);
		tv_nick = (TextView) content.findViewById(R.id.tv_nick);
		tv_intro = (TextView) content.findViewById(R.id.tv_intro);
		tv_time = (TextView) content.findViewById(R.id.tv_time);
		tv_comment_num = (TextView) content.findViewById(R.id.tv_comment_num);
		tv_more_num = (TextView) content.findViewById(R.id.tv_more_num);
		tv_str_comment = (TextView) content.findViewById(R.id.tv_str_comment);
		tv_str_more = (TextView) content.findViewById(R.id.tv_str_more);
		TextView tv_more = (TextView) content.findViewById(R.id.tv_more);
		listView = (MyListView) content.findViewById(R.id.lv_reply_praise);
		img_avatar = (ImageView) content.findViewById(R.id.img_avatar);
		// btn_detail_next = (Button)
		// content.findViewById(R.id.btn_detail_next);
		layout_vip = (LinearLayout) content.findViewById(R.id.layout_vip);
		// btn_buy_vip = (Button) content.findViewById(R.id.btn_buy_vip);
		relayout_viewpager = (RelativeLayout) content
				.findViewById(R.id.relayout_viewpager);
		frlayout_video = (FrameLayout) content
				.findViewById(R.id.frlayout_video);
		layout_dots = (LinearLayout) content.findViewById(R.id.layout_dots);
		type = "comment";
		changeTextView(type);
		dots = new ArrayList<ImageView>();
		img_avatar.setOnClickListener(this);
		relayout_reply.setOnClickListener(this);
		relayout_more.setOnClickListener(this);
		tv_more.setOnClickListener(this);
		pullview_list.addBodyView(content);
		pullview_list.setOnPullListener(this);
		listView.setOnItemClickListener(this);
		// btn_detail_next.setOnClickListener(this);
		// btn_buy_vip.setOnClickListener(this);
		img_video_play.setOnClickListener(this);
		if (getIntent().getStringExtra("detail") != null) {
			if (getIntent().getStringExtra("detail").equals("photo")) {
				// img_video_play.setVisibility(View.GONE);
				detail = "photo";
				// img_icon.setVisibility(View.GONE);
				// btn_detail_next.setVisibility(View.GONE);
				// photoPager.setVisibility(View.VISIBLE);
				relayout_viewpager.setVisibility(View.VISIBLE);
				frlayout_video.setVisibility(View.GONE);
			} else {
				videoPos = getIntent().getExtras().getInt("pos");
				// img_video_play.setVisibility(View.VISIBLE);
				detail = "video";
				// btn_detail_next.setVisibility(View.GONE);
				// photoPager.setVisibility(View.GONE);
				// img_icon.setVisibility(View.VISIBLE);
				relayout_viewpager.setVisibility(View.GONE);
				frlayout_video.setVisibility(View.VISIBLE);
				tv_title.setText(R.string.str_detailvideo);
			}
		}
		waitDialog();
		getHttpData("detail");
	}

	
	@OnClick({ R.id.layout_back, R.id.btn_sendmsg, R.id.layout_more,
			R.id.relayout_comment, R.id.relayout_gift, R.id.relayout_praise,
			R.id.relayout_chat })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.layout_back:
			if (layout_reply.isShown()) {
				layout_menu.setVisibility(View.VISIBLE);
				layout_reply.setVisibility(View.GONE);
			} else {
				finish();
			}
			break;
		case R.id.btn_sendmsg:
			if (BasicUtils.isFastDoubleClick()) {
				return;
			}
			StatService.onEvent(context, "click-pl-send-button", "eventLabel",
					1);
			if (TextUtils.isEmpty(ed_msgcontent.getText().toString())) {
				Toast.makeText(context,
						StringUtils.getResourse(R.string.str_input_null),
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (!BasicUtils.containsEmoji(ed_msgcontent.getText().toString())) {
				getHttpData("send");
				InputMethodManager im = (InputMethodManager) getApplicationContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				im.hideSoftInputFromWindow(ed_msgcontent.getWindowToken(), 0);
			} else {
				Toast.makeText(context,
						StringUtils.getResourse(R.string.str_nickname_tip2),
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.layout_more:
			if (BasicUtils.isFastDoubleClick()) {
				return;
			}
			String[] choice = {StringUtils.getResourse(
					R.string.str_userreport) };
			report = getResources().getStringArray(R.array.report);
			AlertDialog dialog = new AlertDialog.Builder(context)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setTitle("选择")
					.setItems(choice, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							arg0.dismiss();
							reportDialog = new AlertDialog.Builder(context)
									.setIcon(android.R.drawable.ic_dialog_info)
									.setTitle(R.string.str_userreport)
									.setItems(
											report,
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface arg1,
														int pos) {
													// TODO Auto-generated
													// method stub
													report_id = pos;
													if (BasicUtils
															.isFastDoubleClick()) {
														return;
													}
													getHttpData("report");
												}
											})
									.setNegativeButton(
											R.string.str_cancel,
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface arg1,
														int pos) {
													// TODO Auto-generated
													// method stub
													arg1.dismiss();
												}
											}).create();
							reportDialog.show();
						}
					})
					.setNegativeButton(R.string.str_cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									arg0.dismiss();
								}
							}).create();
			dialog.show();
			break;
		case R.id.relayout_comment:
			StatService.onEvent(context, "click-shencaixiu-pl-button",
					"eventLabel", 1);
			layout_menu.setVisibility(View.GONE);
			layout_reply.setVisibility(View.VISIBLE);
			break;
		case R.id.relayout_gift:
			StatService.onEvent(context, "click-shencaixiu-gift-button",
					"eventLabel", 1);
			Intent intent = new Intent(context, GiftStoreActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("person", newPer);
			intent.putExtras(bundle);
			context.startActivity(intent);
			break;
		case R.id.relayout_praise:

			if (!flag_greet) {
				if (BasicUtils.isFastDoubleClick()) {
					return;
				}
				StatService.onEvent(context, "click-shencaixiu-qgd-butoon",
						"eventLabel", 1);
				if (!newPer.getUser_id().equals(Conf.userID))
					getHttpData("greet");
			}
			break;
		case R.id.relayout_chat:

			if (BasicUtils.isFastDoubleClick()) {
				return;
			}
			StatService.onEvent(context, "clcik-shencaixiu-lt-button",
					"eventLabel", 1);
			getHttpData("chat");
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// if (BasicUtils.isFastDoubleClick()) {
		// return;
		// }
		switch (v.getId()) {
		case R.id.layout_gift_content:
			Intent intent = new Intent(context, GiftStoreActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("person", newPer);
			intent.putExtras(bundle);
			context.startActivity(intent);
			break;
		case R.id.img_avatar:
			if (!newPer.getUser_id().equals(Conf.userID)) {
				Intent perintent = new Intent(context, FragmentToActivity.class);
				perintent.putExtra("who", "personal");
				perintent.putExtra("user_id", newPer.getUser_id());
				perintent.putExtra("distance", "");
				context.startActivity(perintent);
			}
			break;
		case R.id.relayout_reply:
			if (!type.equals("comment")) {
				type = "comment";
				changeTextView(type);
				if (detailPho != null && list_comment != null
						&& list_comment.size() > 0) {
					replyAdapter = new ReplyPraiseAdapter(context, type,
							list_comment);
					listView.setAdapter(replyAdapter);
				} else {
					listView.setAdapter(null);
					Toast.makeText(context, StringUtils.getResourse(R.string.str_date_null),
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.relayout_more:
			if (!type.equals("praise")) {
				type = "praise";
				changeTextView(type);
				if (detailPho != null && list_greet != null
						&& list_greet.size() > 0) {
					replyAdapter = new ReplyPraiseAdapter(context, type,
							list_greet);
					listView.setAdapter(replyAdapter);
				} else {
					listView.setAdapter(null);
					Toast.makeText(context, StringUtils.getResourse(R.string.str_date_null),
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.tv_more:
			Intent giftIntent = new Intent(context, MyGiftActivity.class);
			Bundle bundle_gift = new Bundle();
			bundle_gift.putSerializable("person", newPer);
			giftIntent.putExtras(bundle_gift);
			startActivity(giftIntent);
			break;
		case R.id.btn_buy_vip:
			Intent vipintent = new Intent(context, FragmentToActivity.class);
			vipintent.putExtra("who", "vip");
			context.startActivity(vipintent);
			break;
		case R.id.img_video_play:
			// if (videoPos == 0 || videoPos == 1) {
			// try {
			// playVideo(newPer.getVideo_url());
			//
			// } catch (Exception e) {
			// // TODO: handle exception
			// ViewInject.toast("请检查播放器是否正常");
			// }
			// } else {
			// showPopWindow(context, img_video_play);
			// }
			if(Conf.user_VIP.equals("1")){
				getHttpData("video");
			}else{
				if (isPay) {
//					waitDialog();
					getHttpData("video");
				} else {
//					FirstPay();
//					PzPay();
//					hejuPay();
					if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY == 1){
						UPay();
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
				}
			}


			break;
		default:
			break;
		}
	}

	// 提示信息
	private void UPay() {

		// TODO Auto-generated method stub
		try {
			up = Upay.getInstance(Conf.UPAY_APPKEY);
			up.pay(Conf.UPAY_GOODSKEY_VIDEO, Conf.userID + "," + Conf.CID+","+"shipin",
					new UpayCallback() {

						@Override
						public void onTradeProgress(String goodsKey,
								String tradeId, int price, int paid,
								String extra, int resultCode) {
							// TODO Auto-generated method stub
							if (resultCode == 101) {// 没有支付通道
							} else if (resultCode == 200) {
								LogUtils.printLogE("resultCode---",
										"tradeProgress返回的参数---->扣费成功-----resultCode="
												+ resultCode + "--计费点="
												+ goodsKey + "--订单号=" + tradeId
												+ "--订单金额=" + price);
								isPay = true;
								if(ConfigUtils.HEJUPAY ==2){
									hejuPay();
								}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
									FirstPay();
								}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
									PzPay();
								}
							} else if (resultCode == 203) {
								LogUtils.printLogE("resultCode---",
										"tradeProgress返回的参数---->执行计费成功-----resultCode="
												+ resultCode + "--计费点="
												+ goodsKey + "--订单号=" + tradeId
												+ "--订单金额=" + price);
							} else {
								LogUtils.printLogE("resultCode---",
										"tradeProgress返回的参数---->扣费失败-----resultCode="
												+ resultCode + "--计费点="
												+ goodsKey + "--订单号=" + tradeId
												+ "--订单金额=" + price);
							}
						}

						@Override
						public void onPaymentResult(String goodsKey,
								String tradeId, int resultCode,
								String errorMsg, String extra) {
							// TODO Auto-generated method stub
							postMessage(resultCode);
							if (resultCode == 101) {// 没有支付通道
								isPay = true;
								messageResult = "failed";
							} else if (resultCode == 200) {
								LogUtils.printLogE("resultCode---",
										"paymentResult返回的参数---->支付成功-----resultCode="
												+ resultCode + "--计费点="
												+ goodsKey + "--订单号=" + tradeId);
								isPay = true;
								messageResult = "success";
//								if (!BasicUtils
//										.isInstallApk("com.envenler.mediaplayer")) {
//									pluginDialog();
//								}
								
							} else if (resultCode == 110) {
								LogUtils.printLogE("resultCode---",
										"paymentResult返回的参数---->支付取消-----resultCode="
												+ resultCode + "--计费点="
												+ goodsKey + "--订单号=" + tradeId);
								isPay = false;
								// FirstPay();
//								hejuPay();
//								if (!BasicUtils
//										.isInstallApk("com.envenler.mediaplayer")) {
//									pluginDialog();
//								}
								if(ConfigUtils.HEJUPAY ==2){
									hejuPay();
								}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
									FirstPay();
								}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
									PzPay();
								}
							} else {
								LogUtils.printLogE("resultCode---",
										"paymentResult返回的参数---->支付失败-----resultCode="
												+ resultCode + "--计费点="
												+ goodsKey + "--订单号=" + tradeId);
								isPay = false;
								// FirstPay();
//								hejuPay();
//								if (!BasicUtils
//										.isInstallApk("com.envenler.mediaplayer")) {
//									pluginDialog();
//								}
								if(ConfigUtils.HEJUPAY ==2){
									hejuPay();
								}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
									FirstPay();
								}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
									PzPay();
								}
							}
						}
					});
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void FirstPay() {

		fristPaydialog = BasicUtils.showDialog(SelectDetailActivity.this,
				R.style.BasicDialogAngle);
		fristPaydialog.setContentView(R.layout.dialog_firstpay_shipin);
		fristPaydialog.setCanceledOnTouchOutside(false);
		fristPaydialog.setCancelable(false);
		(fristPaydialog.findViewById(R.id.img_close))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						fristPaydialog.dismiss();
						isPay = false;
//						 UPay();
//						hejuPay();
						if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY ==2){
							UPay();
						}else if(ConfigUtils.HEJUPAY == 2){
							hejuPay();
						}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
							PzPay();
						}
					}
				});
		(fristPaydialog.findViewById(R.id.btn_ok))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						fristPaydialog.dismiss();
						FirstPay.pay(SelectDetailActivity.this, "视频点播", "视频点播",
								15, Conf.userID + "," + Conf.CID + ","
										+ "shipin", new IOnPayResult() {

									@Override
									public void onSuccess(String arg0) {
										// TODO Auto-generated method stub
										LogUtils.printLogE("首付支付成功", arg0);
										isPay = true;
//										UPay();
//										hejuPay();
										if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY ==2){
											UPay();
										}else if(ConfigUtils.HEJUPAY == 2){
											hejuPay();
										}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
											PzPay();
										}
									}

									@Override
									public void onFail(String arg0,
											String arg1, String arg2) {
										// TODO Auto-generated method stub
										LogUtils.printLogE("首付支付失败", "错误码:"
												+ arg0 + "\n错误描述：" + arg1
												+ "\n额外信息：" + arg2);
										isPay = false;
//										hejuPay();
//										 UPay();
										if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY ==2){
											UPay();
										}else if(ConfigUtils.HEJUPAY == 2){
											hejuPay();
										}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
											PzPay();
										}
									}
								}, PopType.NONE, true);
					}
				});
		fristPaydialog.show();
	}

	private void hejuPay() {
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("productName", "视频点播"); // 商品名称 可为空
		params.put("point", "20"); // 计费点数 不为空
		params.put("extraInfo", Conf.userID + "," + Conf.CID + "," + "shipin"); // CP扩展信息
																				// 可为空
		params.put("payType", "1");// 购买方式1、默认话费，失败跳转支付宝；2、收银台模式；3、支付宝；4、信用卡；5、储蓄卡
		params.put("ui", "2");
		HejuHuafei mHejuHuafei = new HejuHuafei();
		mHejuHuafei.pay(SelectDetailActivity.this, params,
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
							isPay = false;
//							if(!BasicUtils.isInstallApk("com.envenler.mediaplayer")){
//								pluginDialog();
//							}
//							UPay();
							if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY ==2){
								UPay();
							}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
								FirstPay();
							}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
								PzPay();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					@Override
					public void onSuccess(JSONObject payResult) {
						// TODO Auto-generated method stub
						LogUtils.printLogE("和聚支付成功----", payResult.toString());
						try {
							String code = payResult.getString("code");
							String point = payResult.getString("point");
							String extraInfo = payResult.getString("extraInfo");
							String tradeId = payResult.getString("tradeId");
							// amount = payResult.getString("amount");
							isPay = true;
//							if(!BasicUtils.isInstallApk("com.envenler.mediaplayer")){
//								pluginDialog();
//							}
//							UPay();
							if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY ==2){
								UPay();
							}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
								FirstPay();
							}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
								PzPay();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					@Override
					public void onCancel(JSONObject payResult) {
						// TODO Auto-generated method stub
						LogUtils.printLogE("和聚支付取消----", payResult.toString());
						// 支付取消
						try {
							String code = payResult.getString("code");
							String point = payResult.getString("point");
							String extraInfo = payResult.getString("extraInfo");
							String tradeId = payResult.getString("tradeId");
							isPay = false;
//							if(!BasicUtils.isInstallApk("com.envenler.mediaplayer")){
//								pluginDialog();
//							}
//							UPay();
							if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY ==2){
								UPay();
							}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
								FirstPay();
							}else if(ConfigUtils.DEBUG_PZPAY && ConfigUtils.PZPAY == 2){
								PzPay();
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
		pzPaydialog = BasicUtils.showDialog(SelectDetailActivity.this,
				R.style.BasicDialogAngle);
		pzPaydialog.setContentView(R.layout.dialog_firstpay_shipin);
		pzPaydialog.setCanceledOnTouchOutside(false);
		pzPaydialog.setCancelable(false);
		((TextView) (pzPaydialog.findViewById(R.id.tv_pop_msg)))
		.setText(StringUtils.getResourse(R.string.str_popwind_msg)+"客服电话4008812666");
		(pzPaydialog.findViewById(R.id.img_close))
		.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pzPaydialog.dismiss();
				isPay = false;
//				hejuPay();
//				UPay();
//				FirstPay();
				if(ConfigUtils.HEJUPAY ==2){
					hejuPay();
				}else if(ConfigUtils.FIRSTPAY == 2 && ConfigUtils.DEBUG_FIRSTPAY){
					FirstPay();
				}else if(ConfigUtils.DEBUG_UPAY && ConfigUtils.UPAY == 2){
					UPay();
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
				 pzPay.pay(20, orderid, Conf.userID + "," + Conf.CID+","+"shipin");
			}
			
		});
		pzPaydialog.show();
	}
	
	private void pluginDialog(){
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
								
							}
						}
					}
				});
		dialog.show();

	}

	private void postMessage(int resultCode) {
		String key = "pay/upay/result";
		int cache_time = 0;
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("result", resultCode + "");
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
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

	private void playVideo(String video_url) {
		// TODO Auto-generated method stub
		Intent videoIntent = new Intent(Intent.ACTION_VIEW);
		String type = "video/*";
		Uri uri;
		int start = video_url.lastIndexOf("/");
		String name = video_url.substring(start + 1, video_url.length());
		String videoname = name.substring(0, name.indexOf(".")) + ".mp4";
		if (isExist(videoname)) {
			uri = Uri.parse("file:///"+IOUtils.getVideoFile(videoname).getPath());
			type = "video/mp4";
		} else {
			uri = Uri.parse(video_url);
			downloadVideo(video_url, videoname);
		}
		videoIntent.setDataAndType(uri, type);
		startActivity(videoIntent);
	}

	private boolean isExist(String filename) {
		boolean exist = false;
		File[] files = IOUtils.getVideocacheFile().listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.getName().equals(filename)) {
					exist = true;
					break;
				}
			}
		}
		return exist;
	}

	private void downloadVideo(String downloadUrl, String filename) {
		// TODO Auto-generated method stub
		HttpUtils http = new HttpUtils();
		HttpHandler downhandler = http.download(downloadUrl, IOUtils
				.getVideoFile(filename).toString(), true,
				new RequestCallBack<File>() {

					@Override
					public void onStart() {
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						// fileTotalSize = total;
						LogUtils.printLogE("视频下载进度---", total + "---"
								+ ((int) current * 100 / total));
						int max = (int) ((int) current * 100 / total);
						if (max == 100) {
						}
					}

					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {

					}

					@Override
					public void onFailure(HttpException error, String msg) {

					}
				});
	}

	// FilenameFilter filter = new FilenameFilter() {
	//
	// @Override
	// public boolean accept(File dir, String filename) {
	// // TODO Auto-generated method stub
	// return filename.startsWith(videoname);
	// }
	// };

	private void showFinalDialog() {
		// TODO Auto-generated method stub
		final Dialog dialog = BasicUtils.showDialog(SelectDetailActivity.this,
				R.style.BasicDialog);
		dialog.setContentView(R.layout.dialog_wait);
		ImageView img = (ImageView) dialog
				.findViewById(R.id.img_dialog_progress);
		TextView text = (TextView) dialog.findViewById(R.id.tv_dialog_msg);
		img.setVisibility(View.GONE);
		text.setText(R.string.str_finalnot_photo);
		dialog.show();
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}, 2000);

	}

	// 设置显示信息
	private void setView() {
		try {

			if (detail.equals("photo")) {
				// if (detailPho.getPhoto().length > 0) {
				// setImage(img_icon, detailPho.getPhoto()[pos]);
				// }
				count = detailPho.getPhoto().length;
				photoPager
						.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
								RelativeLayout.LayoutParams.MATCH_PARENT,
								Conf.height / 2));

				photoPager.setCurrentItem(0);
				photoPager.setOffscreenPageLimit(0);
				photoPager.setInterval(interval);
				if (count < 1) {
					photoPager.stopAutoScroll();
				} else {
					// photoPager.startAutoScroll();
					photoPager.setAdapter(new PhotoPagerAdapter());

				}

				photoPager
						.setOnPageChangeListener(new PhotoPageChangeListener());
				// photoPager
				// .setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
				photoPager.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {

						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							photoPager.stopAutoScroll();

							break;
						case MotionEvent.ACTION_MOVE:
							if (count < 1) {
								photoPager.stopAutoScroll();
							} else {
								photoPager.stopAutoScroll();
							}

							break;
						case MotionEvent.ACTION_UP:
							if (count < 1) {
								photoPager.stopAutoScroll();
							} else {
								photoPager.stopAutoScroll();
							}

							break;

						default:
							break;
						}

						return false;
					}

				});

				for (int i = 0; i < count; i++) {
					ImageView dot = new ImageView(context);
					dot.setScaleType(ScaleType.FIT_XY);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
					dot.setLayoutParams(lp);
					dot.setPadding(10, 5, 10, 5);
					if (i == index % count) {
						dot.setImageResource(R.drawable.page_dots_focused);
					} else {
						dot.setImageResource(R.drawable.page_dots_unfocused);
					}
					dots.add(dot);
					layout_dots.addView(dot);
				}

			} else {
				// 视频秀
				setImage(img_icon, newPer.getImg());
				// showPopWindow(context, img_video_play);
			}
			setImage(img_avatar, detailPho.getIcon());
			newPer.setIcon(detailPho.getIcon());
			tv_nick.setText(detailPho.getNick());
			tv_intro.setText(detailPho.getText());
			tv_time.setText(detailPho.getTime());
			tv_comment_num.setText(detailPho.getComment_nums());
			tv_more_num.setText(detailPho.getGreet_nums());
			if (detailPho.getIs_greet().trim().equals("0")) {
				flag_greet = false;
			} else if (detailPho.getIs_greet().trim().equals("1")) {
				flag_greet = true;
				img_praise.setImageResource(R.drawable.btn_praise_pressed);
				tv_request_praise.setTextColor(R.color.darkgray);
			}
			if (detailPho.getGifts() != null && detailPho.getGifts().size() > 0)
				for (int i = 0; i < detailPho.getGifts().size(); i++) {
					LinearLayout layout_gift_content = (LinearLayout) content
							.findViewById(R.id.layout_gift_content);
					View childView = getLayoutInflater().inflate(
							R.layout.layout_receive_gift, null);
					tv_giftname = (TextView) childView
							.findViewById(R.id.tv_giftname);
					tv_gift = (TextView) childView.findViewById(R.id.tv_gift);
					tv_gifttime = (TextView) childView
							.findViewById(R.id.tv_gifttime);
					img_gifticon = (ImageView) childView
							.findViewById(R.id.img_gifticon);
					tv_giftname.setText(detailPho.getGifts().get(i)
							.getSender_nick());
					tv_gift.setText(detailPho.getGifts().get(i).getName());
					tv_gifttime.setText(detailPho.getGifts().get(i).getTime());
					setImage(img_gifticon, detailPho.getGifts().get(i).getImg());
					layout_gift_content.addView(childView);
					layout_gift_content.setOnClickListener(this);
				}
			type = "comment";
			list_greet = detailPho.getGreets();
			if (detailPho != null && detailPho.getComments() != null
					&& detailPho.getComments().size() > 0) {
				list_comment = detailPho.getComments();
				replyAdapter = new ReplyPraiseAdapter(context, type,
						list_comment);
				listView.setAdapter(replyAdapter);

			}
			pullview_list.post(new Runnable() {
				@Override
				public void run() {
					pullview_list.scrollTo(0, 0);
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	class PhotoPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// if(count < 1){
			// return count;
			// }else{
			// return Integer.MAX_VALUE;
			// }

			return count;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			try {
				final ImageView imageView = new ImageView(context);
				imageView.setAdjustViewBounds(true);
				// TODO 调整图片大小
				imageView.setScaleType(ScaleType.CENTER_CROP);
				// imageView.setLayoutParams(new LinearLayout.LayoutParams(
				// LinearLayout.LayoutParams.MATCH_PARENT,
				// LinearLayout.LayoutParams.MATCH_PARENT));
				setImage(imageView, detailPho.getPhoto()[position % count]);
				((ViewPager) container).addView(imageView);

				return imageView;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return null;
			}

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((ImageView) object);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	class PhotoPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			index = position;
			for (int i = 0; i < count; i++) {
				dots.get(i).setImageResource(R.drawable.page_dots_unfocused);
			}
			dots.get(position % count).setImageResource(
					R.drawable.page_dots_focused);
		}

	}

	private void setImage(final ImageView img, String url) {
		ImageLoadUtils.imageLoader.displayImage(url, img,
				ImageLoadUtils.options, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub
						if (Conf.gender.equals("1"))
							img.setBackgroundResource(R.drawable.default_female);
						else
							img.setBackgroundResource(R.drawable.default_male);
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						// TODO Auto-generated method stub
						if (Conf.gender.equals("1"))
							img.setBackgroundResource(R.drawable.default_female);
						else
							img.setBackgroundResource(R.drawable.default_male);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						try {
							if (img.getId() == R.id.img_select_icon) {
								int height = (Conf.width * loadedImage
										.getHeight()) / loadedImage.getWidth();
								img_icon.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
										android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
										height));
								if (Conf.user_VIP.equals("0")) {
									if (pos > 2) {
										layout_vip.setVisibility(View.VISIBLE);
										Bitmap newBmp = BitmapUtils.blur(
												loadedImage, img_icon);
										img_icon.setImageBitmap(newBmp);
									} else {
										layout_vip.setVisibility(View.GONE);
									}
								} else {
									layout_vip.setVisibility(View.GONE);
								}
							} else {
								if (Conf.user_VIP.equals("0")) {
									if (index > 1) {
										// layout_vip.setVisibility(View.VISIBLE);
										// Bitmap newBmp = BitmapUtils.blur(
										// loadedImage, img);
										// img.setImageBitmap(newBmp);
									} else {
										// layout_vip.setVisibility(View.GONE);
									}
								} else {
									// layout_vip.setVisibility(View.GONE);
								}
							}
						} catch (OutOfMemoryError error) {
							// TODO: handle exception
							LogUtils.printLogE("图片OOM异常", "---");
						}
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void changeTextView(String type) {
		if (type.equals("comment")) {
			tv_comment_num.setTextSize(14);
			tv_str_comment.setTextSize(14);
			tv_more_num.setTextSize(12);
			tv_str_more.setTextSize(12);
		} else {
			tv_comment_num.setTextSize(12);
			tv_str_comment.setTextSize(12);
			tv_more_num.setTextSize(14);
			tv_str_more.setTextSize(14);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (!replyAdapter.allDate().get(arg2).getSender().equals(Conf.userID)) {
			if (type.equals("comment")) {
				layout_menu.setVisibility(View.GONE);
				layout_reply.setVisibility(View.VISIBLE);
				user_id = replyAdapter.allDate().get(arg2).getSender();
				ed_msgcontent.setHint(StringUtils.getResourse(R.string.str_reply_who)
						+ replyAdapter.allDate().get(arg2).getSender_nick());
			}
		} else {
			layout_menu.setVisibility(View.GONE);
			layout_reply.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

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

	@Override
	public void loadMore() {
		// TODO Auto-generated method stub
		if (type.equals("comment")) {
			if (detailPho != null && list_comment != null
					&& list_comment.size() > 0) {
				++commentpage;
			} else {
				if (detailPho == null) {
					commentpage = 1;
				} else if (commentpage != 1)
					++commentpage;
			}
			getHttpData("comment");
		} else if (type.equals("praise")) {
			if (detailPho != null && list_greet != null
					&& list_greet.size() > 0) {
				++greetpage;
			} else {
				if (detailPho == null)
					greetpage = 1;
				else if (greetpage != 1)
					++greetpage;
			}
			getHttpData("praise");
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (layout_reply.isShown()) {
				layout_menu.setVisibility(View.VISIBLE);
				layout_reply.setVisibility(View.GONE);
				user_id = newPer.getUser_id();
				ed_msgcontent.setHint("");
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getHttpData(final String kind) {
		RequestParams params = new RequestParams();
		String key = "";
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		try {
			if (!detail.equals("video"))
				params.addQueryStringParameter("photo_id", newPer.getPhoto_id());
			else
				params.addQueryStringParameter("photo_id", newPer.getId());
			if (kind.equals("detail")) {
				if (!detail.equals("video")) {
					key = "photo/detail";
				} else {
					key = "video/detail";
				}
				params.addQueryStringParameter("cur_user", Conf.userID);
				params.addQueryStringParameter("user_id", newPer.getUser_id());
			} else if (kind.equals("comment")) {
				key = "photo/detail/comment";
				params.addQueryStringParameter("page",
						String.valueOf(commentpage));
			} else if (kind.equals("praise")) {
				key = "photo/detail/praise";
				params.addQueryStringParameter("page",
						String.valueOf(greetpage));
			} else if (kind.equals("greet")) {
				key = "user/praise";
				params.addQueryStringParameter("user_id", newPer.getUser_id());
				params.addQueryStringParameter("cur_user", Conf.userID);
				params.addQueryStringParameter("gender", Conf.gender);

			} else if (kind.equals("send")) {
				if (user_id.equals(newPer.getUser_id()) || user_id.equals("")) {
					key = "user/comment";
					params.addQueryStringParameter("user_id",
							newPer.getUser_id());
				} else {
					key = "user/reply";
					params.addQueryStringParameter("user_id", user_id);
				}
				params.addQueryStringParameter("cur_user", Conf.userID);
				params.addQueryStringParameter("text", ed_msgcontent.getText()
						.toString());

			} else if (kind.equals("chat")) {
				key = "user/msg";
				params.addQueryStringParameter("sender", Conf.userID);
				params.addQueryStringParameter("receiver", newPer.getUser_id());

			} else if (kind.equals("report")) {
				key = "photo/report";
				params.addQueryStringParameter("user1", Conf.userID);
				params.addQueryStringParameter("user2", newPer.getUser_id());
				params.addQueryStringParameter("flag", report_id + "");

			} else if (kind.equals("video")) {
				key = "video/see";
				params.addQueryStringParameter("cur_user", Conf.userID);
			}
			// LogUtils.printLogE("详细页面_上传", params.toString());
			CacheRequest.requestGET(context, key, params, key, 0,
					new CacheRequestCallBack() {
						// TODO Auto-generated method stub
						@Override
						public void onData(String arg0) {
							// LogUtils.printLogE("详细页面", arg0);
							try {
								pullview_list.setfooterViewReset();
								if (mDialog != null) {
									mDialog.dismiss();
								}
								if (kind.equals("report")) {
									if (reportDialog != null) {
										reportDialog.dismiss();
									}
								}
								// replyPho
								if (kind.equals("detail")) {
									detailPho = new Gson().fromJson(arg0,
											BaseJson.class);
									if (detailPho.getStatus().equals("200")) {
										setView();
										pullview_list
												.setVisibility(View.VISIBLE);
									}
								} else if (kind.equals("comment")) {
									replyPho = new Gson().fromJson(arg0,
											BaseJson.class);
									if (replyPho.getStatus().equals("200")) {
										if (list_comment == null) {
											list_comment = replyPho
													.getComments();
											replyAdapter = new ReplyPraiseAdapter(
													context, kind, replyPho
															.getComments());
											listView.setAdapter(replyAdapter);
										} else {
											list_comment.addAll(replyPho
													.getComments());
											replyAdapter.insertData(replyPho
													.getComments());
											replyAdapter.notifyDataSetChanged();
										}

									} else {
										if (commentpage > 1)
											--commentpage;
									}
								} else if (kind.equals("praise")) {
									requestPho = new Gson().fromJson(arg0,
											BaseJson.class);
									if (requestPho.getStatus().equals("200")) {

										if (list_greet == null) {
											list_greet = requestPho.getGreets();
											replyAdapter = new ReplyPraiseAdapter(
													context, kind, list_greet);
											listView.setAdapter(replyAdapter);
										} else {
											list_greet.addAll(requestPho
													.getGreets());
											replyAdapter.insertData(requestPho
													.getGreets());
											replyAdapter.notifyDataSetChanged();
										}

									} else {
										if (greetpage > 1)
											--greetpage;
									}
								} else if (kind.equals("greet")) {
									if (arg0.contains("200")) {
										flag_greet = true;
										tv_more_num.setText(Integer
												.valueOf(detailPho
														.getGreet_nums())
												+ 1 + "");
										img_praise
												.setImageResource(R.drawable.btn_praise_pressed);
										tv_request_praise
												.setTextColor(R.color.darkgray);
										if (list_greet == null) {
											list_greet = new ArrayList<BaseJson>();
										}
										BaseJson base = new BaseJson();
										base.setSender(Conf.userID);
										base.setSender_icon(Conf.userImg);
										base.setSender_nick(Conf.userName);
										list_greet.add(0, base);
										if (type.equals("praise")) {
											replyAdapter = new ReplyPraiseAdapter(
													context, type, list_greet);
											listView.setAdapter(replyAdapter);
										}
										Toast.makeText(
												context,
												StringUtils.getResourse(R.string.str_request_success),
												Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(
												context,
												StringUtils.getResourse(R.string.str_request_fail),
												Toast.LENGTH_SHORT).show();
									}
								} else if (kind.equals("send")) {
									if (arg0.contains("200")) {
										tv_comment_num.setText(Integer
												.valueOf(detailPho
														.getComment_nums())
												+ 1 + "");
										if (list_comment == null) {
											list_comment = new ArrayList<BaseJson>();
										}
										BaseJson base = new BaseJson();
										base.setSender(Conf.userID);
										base.setSender_icon(Conf.userImg);
										base.setSender_nick(Conf.userName);
										base.setText(ed_msgcontent.getText()
												.toString());
										base.setTime("");
										list_comment.add(0, base);
										if (!type.equals("praise")) {
											replyAdapter = new ReplyPraiseAdapter(
													context, type, list_comment);
											listView.setAdapter(replyAdapter);
										}
										ed_msgcontent.setText("");
										ed_msgcontent.setHint("");
										Toast.makeText(
												context,
												StringUtils.getResourse(R.string.str_request_success),
												Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(
												context,
												StringUtils.getResourse(R.string.str_request_fail),
												Toast.LENGTH_SHORT).show();
									}
								} else if (kind.equals("chat")) {

									BaseJson person = new Gson().fromJson(arg0,
											BaseJson.class);
									if (person.getStatus().equals("200")) {
										Intent chatIntent = new Intent(context,
												TalkActivity.class);
										person.setIcon(newPer.getIcon());
										person.setNick(newPer.getNick());
										person.setSender(newPer.getUser_id());
										chatIntent.putExtra("person", person);
										startActivity(chatIntent);
									}
								} else if (kind.equals("report")) {
									if (arg0.contains("200")) {
										Toast.makeText(
												context,
												StringUtils.getResourse(R.string.str_request_success),
												Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(
												context,
												StringUtils.getResourse(R.string.str_request_fail),
												Toast.LENGTH_SHORT).show();
									}
								} else if (kind.equals("video")) {
									BaseJson base = new Gson().fromJson(arg0,
											BaseJson.class);
									if (base.getStatus().equals("200")) {
										playVideo(base.getUrl());
										// Intent intent = new
										// Intent(Intent.ACTION_VIEW);
										// String video_type = "video/*";
										// Uri uri =
										// Uri.parse(newPer.getVideo_url());
										// intent.setDataAndType(uri,
										// video_type);
										// startActivity(intent);
									} else if (base.getStatus().equals("152")) {
										final Dialog dialog = BasicUtils
												.showDialog(context,
														R.style.BasicDialog);
										dialog.setContentView(R.layout.dialog_rechargevip);
										dialog.setCanceledOnTouchOutside(true);
										((TextView) dialog
												.findViewById(R.id.tv_dialog_msg))
												.setText(base.getMsg());
										((Button) dialog
												.findViewById(R.id.btn_dialog_cancle))
												.setOnClickListener(new OnClickListener() {

													@Override
													public void onClick(
															View arg0) {
														// TODO Auto-generated
														// method
														// stub
														dialog.dismiss();
													}
												});
										((Button) dialog
												.findViewById(R.id.btn_dialog_sure))
												.setOnClickListener(new OnClickListener() {

													@Override
													public void onClick(
															View arg0) {
														// TODO Auto-generated
														// method
														// stub
														dialog.dismiss();
														Intent intent = new Intent(
																context,
																FragmentToActivity.class);
														intent.putExtra("who",
																"coin");
														startActivity(intent);
													}
												});
										dialog.show();
									} else {
										Toast.makeText(
												context,
												StringUtils.getResourse(R.string.str_net_register),
												Toast.LENGTH_SHORT).show();
									}
								}

							} catch (Exception e) {
								// TODO Auto-generated catch block
							} 
//							finally {
//								pullview_list.setfooterViewReset();
//								if (mDialog != null) {
//									mDialog.dismiss();
//								}
//								if (kind.equals("report")) {
//									if (reportDialog != null) {
//										reportDialog.dismiss();
//									}
//								}
//							}

						}

						@Override
						public void onFail(HttpException e, String result,
								String json) {
							// TODO Auto-generated method stub
							// LogUtils.printLogE("详细页面异常", strMsg);
							if (mDialog != null) {
								mDialog.dismiss();
							}
							if (kind.equals("praise")) {
								if (greetpage > 1)
									--greetpage;
							} else if (kind.equals("comment")) {
								if (commentpage > 1)
									--commentpage;
							}
							if (kind.equals("report")) {
								if (reportDialog != null) {
									reportDialog.dismiss();
								}
							}
							pullview_list.setfooterViewReset();
							ExitManager.getScreenManager().intentLogin(context,
									e.getExceptionCode() + "");
						}

					});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}

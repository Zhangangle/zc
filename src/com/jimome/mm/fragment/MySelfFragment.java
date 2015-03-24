package com.jimome.mm.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.AccessControlList;
import com.aliyun.mbaas.oss.model.OSSException;
import com.aliyun.mbaas.oss.storage.OSSBucket;
import com.aliyun.mbaas.oss.storage.OSSFile;
import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.activity.MyShowActivity;
import com.jimome.mm.activity.MyShowUploadActivity;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.BitmapUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.unjiaoyou.mm.R;

/**
 * "我"页面
 * 
 * @author admin
 * 
 */

public class MySelfFragment extends BaseFragment {

	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.img_vip_status)
	private ImageView img_vip_status;
	// @ViewInject(R.id.tv_my_nvshengxiu)
	// private TextView tv_my_nvshengxiu;
	// @ViewInject(R.id.layout_text_vip, click = true)
	// private LinearLayout layout_text_vip;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	// @ViewInject(R.id.img_my_morevisitor, click = true)
	// private ImageView img_my_morevisitor;
	// @ViewInject(R.id.img_nvshengxiu)
	// private ImageView img_nvshengxiu;
	// @ViewInject(R.id.img_shencaixiu)
	// private ImageView img_shencaixiu;
	// @ViewInject(R.id.img_info)
	// private ImageView img_info;
	// @ViewInject(R.id.img_video)
	// private ImageView img_video;
	//
	@ViewInject(R.id.tv_nickname)
	private TextView tv_nickname;
	@ViewInject(R.id.tv_my_intro)
	private TextView tv_my_intro;
	// @ViewInject(R.id.tv_my_height)
	// private TextView tv_my_height;
	// @ViewInject(R.id.tv_my_oppad)
	// private TextView tv_my_oppad;
	// @ViewInject(R.id.img_my_gender, click = true)
	// private ImageView img_my_gender;
	// @ViewInject(R.id.img_my_icon, click = true)
	// private ImageView img_my_icon;
	// @ViewInject(R.id.layout_my_nvshengxiu, click = true)
	// private LinearLayout layout_my_nvshengxiu;
	// @ViewInject(R.id.layout_top_img)
	// private LinearLayout layout_top_img;
	// @ViewInject(R.id.layout_my_shencaixiu, click = true)
	// private LinearLayout layout_my_shencaixiu;
	// @ViewInject(R.id.layout_my_video, click = true)
	// private LinearLayout layout_my_video;
	// @ViewInject(R.id.layout_my_infor, click = true)
	// private LinearLayout layout_my_infor;
	// @ViewInject(R.id.gv_my_vistor)
	// private GridView gv_my_vistor;
	@ViewInject(R.id.tv_giftnum)
	private TextView tv_my_giftnum;
	@ViewInject(R.id.tv_follownum)
	private TextView tv_follownum;
	@ViewInject(R.id.tv_fansnum)
	private TextView tv_fansnum;
	@ViewInject(R.id.tv_my_coin)
	private TextView tv_my_coin;
	@ViewInject(R.id.tv_my_charm)
	private TextView tv_my_charm;
	@ViewInject(R.id.tv_nvshenxiunum)
	private TextView tv_nvshenxiunum;
	@ViewInject(R.id.tv_shencaixiunum)
	private TextView tv_shencaixiunum;
	@ViewInject(R.id.tv_videonum)
	private TextView tv_videonum;
	@ViewInject(R.id.tv_my_vipmsg)
	private TextView tv_my_vipmsg;
	@ViewInject(R.id.tv_my_vipday)
	private TextView tv_my_vipday;
	// @ViewInject(R.id.tv_my_morgift, click = true)
	// private TextView tv_my_morgift;
	// @ViewInject(R.id.gv_my_gift)
	// private GridView gv_my_gift;
	@ViewInject(R.id.img_avatar)
	private ImageView img_my_icon;
	@ViewInject(R.id.img_gender)
	private ImageView img_my_gender;
	// 个人修改
	@ViewInject(R.id.relayout_editor)
	private RelativeLayout relayout_editor;
	// 礼物
	@ViewInject(R.id.layout_gift)
	private RelativeLayout layout_gift;
	// 关注
	@ViewInject(R.id.layout_follow)
	private RelativeLayout layout_follow;
	// 粉丝
	@ViewInject(R.id.layout_fans)
	private RelativeLayout layout_fans;
	// VIP
	@ViewInject(R.id.layout_buyvip)
	private LinearLayout layout_buyvip;
	// 金币
	@ViewInject(R.id.layout_buycoin)
	private LinearLayout layout_buycoin;
	// 兑换魅力
	@ViewInject(R.id.layout_exchange)
	private LinearLayout layout_exchange;
	// 女神秀
	@ViewInject(R.id.layout_nvshenxiu)
	private LinearLayout layout_nvshenxiu;
	// 身材秀
	@ViewInject(R.id.layout_shencaixiu)
	private LinearLayout layout_shencaixiu;
	// 视频秀
	@ViewInject(R.id.layout_video)
	private LinearLayout layout_video;
	// 最近来访
	@ViewInject(R.id.layout_visitor)
	private LinearLayout layout_visitor;
	// 常见问题
	@ViewInject(R.id.layout_faq)
	private LinearLayout layout_faq;
	// 个人设置
	@ViewInject(R.id.layout_setting)
	private LinearLayout layout_setting;

	@ViewInject(R.id.scroll_me)
	private ScrollView scroll_myself;
//	private ProgressDialog mDialog;
	// private VisitorAdapter vistorAdapter, giftAdapter;
	private BaseFragment fragment;
	private FragmentTransaction transaction;
	private String[] choice = { "拍照上传", "本地图片上传" };
	/*** 拍照上传 ***/
	private static final int UPLOAD_CAMERA = 1;
	/*** 本地上传 ***/
	private static final int UPLOAD_LOCAL = 2;
	/*** 结果 ***/
	private static final int PHOTO_REQUEST_CUT = 3;

	private Bitmap mBitmap;
	private byte[] byte_img;
	private int last_posx, last_posy;
	private BaseJson perMain;
	private String back = "";
	@ViewInject(R.id.img_my_fans)
	private ImageView img_my_fans;
	@ViewInject(R.id.img_my_info)
	private ImageView img_my_info;
	@ViewInject(R.id.img_my_visitor)
	private ImageView img_my_visitor;
	@ViewInject(R.id.img_my_gift)
	private ImageView img_my_gift;

	private Activity context;
	
	@Override
	protected View initView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		return arg0.inflate(R.layout.fragment_me, arg1, false);
	}

	
	public MySelfFragment() {

	}

	public MySelfFragment(String back) {
		this.back = back;
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
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("jimome.action.myselfrefresh");
		context.registerReceiver(refreshBroadcastReceiver, intentFilter);

	}

	private BroadcastReceiver refreshBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			if (action.equals("jimome.action.myselfrefresh")) {
				getHttpData();
			}
		}
	};

	private void initView() {
		try {
			if (TextUtils.isEmpty(perMain.getNick())) {
				if (Conf.gender.equals("1")) {
					tv_nickname.setText(R.string.str_man_default_nickname);
				} else {
					tv_nickname.setText(R.string.str_woman_default_nickname);
				}
			} else {
				tv_nickname.setText(perMain.getNick());
			}

			if (!Conf.userImg.trim().equals(""))
				ImageLoadUtils.imageLoader.displayImage(Conf.userImg,
						img_my_icon, ImageLoadUtils.optionsRounded,
						new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String arg0, View arg1) {
								// TODO Auto-generated method stub
								if (Conf.gender.equals("1")) {
									img_my_icon
											.setImageResource(R.drawable.default_male);
								} else {
									img_my_icon
											.setImageResource(R.drawable.default_female);
								}
							}

							@Override
							public void onLoadingFailed(String arg0, View arg1,
									FailReason arg2) {
								// TODO Auto-generated method stub
								if (Conf.gender.equals("1")) {
									img_my_icon
											.setImageResource(R.drawable.default_male);
								} else {
									img_my_icon
											.setImageResource(R.drawable.default_female);
								}

							}

							@Override
							public void onLoadingComplete(String arg0,
									View arg1, Bitmap arg2) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onLoadingCancelled(String arg0,
									View arg1) {
								// TODO Auto-generated method stub

							}
						});

			if (perMain.getAge().equals("") && perMain.getHeight().equals(""))
				tv_my_intro.setText("0岁/身高0");
			else if (!perMain.getAge().equals("")
					&& perMain.getHeight().equals(""))
				tv_my_intro.setText(perMain.getAge() + "岁/身高0");
			else if (perMain.getAge().equals("")
					&& !perMain.getHeight().equals(""))
				tv_my_intro.setText("0岁/身高" + perMain.getHeight() + "cm");
			else
				tv_my_intro.setText(perMain.getAge() + "岁/身高"
						+ perMain.getHeight() + "cm");

			if (perMain.getGift_nums().equals("0"))
				tv_my_giftnum.setText("0");
			else
				tv_my_giftnum.setText(perMain.getGift_nums());

			if (perMain.getFocused_nums().equals("0"))
				tv_follownum.setText("0");
			else
				tv_follownum.setText(perMain.getFocused_nums());

			if (perMain.getFocuser_nums().equals("0"))
				tv_fansnum.setText("0");
			else
				tv_fansnum.setText(perMain.getFocuser_nums());

			if (perMain.getCoin().equals("0"))
				tv_my_coin.setText(StringUtils.getResourse(
						R.string.str_talk_coin) + 0);
			else
				tv_my_coin.setText(StringUtils.getResourse(
						R.string.str_talk_coin)
						+ perMain.getCoin());

			if (perMain.getCharm().equals("0"))
				tv_my_charm.setText(StringUtils.getResourse(
						R.string.str_my_charm) + 0);
			else
				tv_my_charm.setText(StringUtils.getResourse(
						R.string.str_my_charm)
						+ perMain.getCharm());

			if (perMain.getShow_nums().equals("0"))
				tv_nvshenxiunum.setText(StringUtils.getResourse(R.string.str_myself_upload));
			else
				tv_nvshenxiunum.setText(perMain.getShow_nums());

			if (perMain.getPhoto_nums().equals("0"))
				tv_shencaixiunum.setText(StringUtils.getResourse(R.string.str_myself_upload));
			else
				tv_shencaixiunum.setText(perMain.getPhoto_nums());

			if (perMain.getVideo_nums().equals("0"))
				tv_videonum.setText(StringUtils.getResourse(R.string.str_myself_upload));
			else
				tv_videonum.setText(perMain.getVideo_nums());

			if (perMain.isGift_dot())
				img_my_gift.setVisibility(View.VISIBLE);
			else
				img_my_gift.setVisibility(View.GONE);
			if (perMain.isFocuser_dot())
				img_my_fans.setVisibility(View.VISIBLE);
			else
				img_my_fans.setVisibility(View.GONE);
			if (perMain.isInfo_dot())
				img_my_info.setVisibility(View.VISIBLE);
			else
				img_my_info.setVisibility(View.GONE);
			if (perMain.isVisiter_dot())
				img_my_visitor.setVisibility(View.VISIBLE);
			else
				img_my_visitor.setVisibility(View.GONE);
			if (Conf.user_VIP.equals("1")) {
				tv_my_vipmsg.setText(StringUtils.getResourse(
						R.string.str_my_vipday));
				tv_my_vipday.setText(perMain.getRemain_days()
						+ StringUtils.getResourse(R.string.str_day));
			}
			sendTip();// 判断隐藏红点
			// img_my_visitor;
			// vistorAdapter = new VisitorAdapter(context,
			// changeVisited(perMain.getVisiteds()));
			// gv_my_vistor.setAdapter(vistorAdapter);
			// gv_my_vistor.setOnItemClickListener(this);

			// giftAdapter = new VisitorAdapter(getActivity(),
			// perMain.getGifts());
			// gv_my_gift.setAdapter(giftAdapter);
			// if (perMain.getGifts() != null && perMain.getGifts().size() > 0)
			// gv_my_gift.setVisibility(View.VISIBLE);
			// gv_my_gift.setOnItemClickListener(this);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private List<BaseJson> changeVisited(String[] visiteds) {
		List<BaseJson> list_visite = new ArrayList<BaseJson>();
		BaseJson sender;
		for (int i = 0; i < visiteds.length; i++) {
			sender = new BaseJson();
			sender.setImg(visiteds[i]);
			list_visite.add(sender);
		}
		return list_visite;
	}

//	@Override
//	public void onItemClick(AdapterView<?> adapterView, View view, int pos,
//			long arg3) {
//		// TODO Auto-generated method stub
////		super.onItemClick(adapterView, view, pos, arg3);
//		switch (adapterView.getId()) {
//		case R.id.gv_my_vistor:
//			if (perMain != null && perMain.getVisiteds() != null
//					&& perMain.getVisiteds().length > 0) {
//				Intent intent = new Intent(new Intent(context,
//						FragmentToActivity.class));
//				intent.putExtra("who", "visitor");
//				intent.putExtra("user_id", Conf.userID);
//				this.startActivity(intent);
//			}
//			break;
//		case R.id.gv_my_gift:
//			if (perMain.getGifts() != null && perMain.getGifts().size() > 0) {
//				Intent giftIntent = new Intent(context,
//						MyGiftActivity.class);
//				BaseJson base = new BaseJson();
//				base.setUser_id(Conf.userID);
//				base.setNick(Conf.userName);
//				giftIntent.putExtra("person", base);
//				startActivity(giftIntent);
//			}
//			break;
//		default:
//			break;
//		}
//	}

	@OnClick ({R.id.layout_back,R.id.img_vip_status,R.id.layout_buyvip,R.id.relayout_editor,
		R.id.layout_buycoin,R.id.layout_exchange,R.id.layout_nvshenxiu,R.id.layout_shencaixiu,
		R.id.layout_video,			 R.id.layout_follow,R.id.layout_fans,R.id.layout_gift,R.id.layout_visitor,
		 R.id.layout_text_vip,R.id.img_avatar,R.id.layout_faq,R.id.layout_setting})
	protected void widgetClick(View v) {
		// TODO Auto-generated method stub
		try {
			switch (v.getId()) {
			case R.id.layout_back:
				context.finish();
				break;
			case R.id.img_vip_status:
			case R.id.layout_buyvip:
				Intent vipIntent = new Intent(context,
						FragmentToActivity.class);
				vipIntent.putExtra("who", "vip");
				context.startActivity(vipIntent);
				break;
			case R.id.relayout_editor:
				// img_my_info.setVisibility(View.GONE);
				Intent introIntent = new Intent(context,
						FragmentToActivity.class);
				introIntent.putExtra("who", "mine");
				context.startActivity(introIntent);
				break;
			case R.id.layout_buycoin:
				Intent buyIntent = new Intent(context,
						FragmentToActivity.class);
				buyIntent.putExtra("who", "coin");
				context.startActivity(buyIntent);
				break;
			case R.id.layout_exchange:
				Intent meiliIntent = new Intent(context,
						FragmentToActivity.class);
				meiliIntent.putExtra("who", "meili");
				context.startActivity(meiliIntent);
				break;
			case R.id.layout_nvshenxiu:
				// img_nvshengxiu.setVisibility(View.GONE);
				Intent nvIntent = null;
				if (perMain == null) {
					return;
				}
				try {
					if (perMain.getShow_nums().equals("0")) {
						nvIntent = new Intent(context,
								MyShowUploadActivity.class);
					} else {
						nvIntent = new Intent(context,
								MyShowActivity.class);
						nvIntent.putExtra("user_id", Conf.userID);
					}
				} catch (Exception e) {
					// TODO: handle exception
					nvIntent = new Intent(context,
							MyShowUploadActivity.class);
				}

				nvIntent.putExtra("type", 1);
				context.startActivity(nvIntent);
				break;
			case R.id.layout_shencaixiu:
				// img_shencaixiu.setVisibility(View.GONE);
				Intent shenIntent = null;
				if (perMain == null) {
					return;
				}
				try {
					if (perMain.getPhoto_nums().equals("0")) {
						shenIntent = new Intent(context,
								MyShowUploadActivity.class);
					} else {
						shenIntent = new Intent(context,
								MyShowActivity.class);
						shenIntent.putExtra("user_id", Conf.userID);
					}
				} catch (Exception e) {
					// TODO: handle exception
					shenIntent = new Intent(context,
							MyShowUploadActivity.class);
				}

				shenIntent.putExtra("type", 2);
				context.startActivity(shenIntent);
				break;
				
			case R.id.layout_video:
				// img_video.setVisibility(View.GONE);
				Intent videoIntent = null;
				if (perMain == null) {
					return;
				}

				try {
					if (perMain.getVideo_nums().equals("0")) {
						videoIntent = new Intent(context,
								MyShowUploadActivity.class);

					} else {
						videoIntent = new Intent(context,
								MyShowActivity.class);
						videoIntent.putExtra("user_id", Conf.userID);

					}
				} catch (Exception e) {
					// TODO: handle exception
					videoIntent = new Intent(context,
							MyShowUploadActivity.class);
				}
				videoIntent.putExtra("type", 3);
				context.startActivity(videoIntent);

				break;

			// case R.id.layout_my_infor:
			// img_info.setVisibility(View.GONE);
			// shared.edit().putBoolean("myTip", true).commit();
			// sendTip();// 验证提示是否取消
			// Intent introIntent = new Intent(context,
			// FragmentToActivity.class);
			// introIntent.putExtra("who", "mine");
			// context.startActivity(introIntent);
			// break;
			case R.id.layout_follow:// 关注
				Intent followIntent = new Intent(context,
						FragmentToActivity.class);
				followIntent.putExtra("who", "follow");
				followIntent.putExtra("name", "1");
				context.startActivity(followIntent);
				break;
			case R.id.layout_fans:// 粉丝
				img_my_fans.setVisibility(View.GONE);
				if (perMain == null) {
					return;
				}
				perMain.setFocuser_dot(false);
				sendTip();
				Intent fansIntent = new Intent(context,
						FragmentToActivity.class);
				fansIntent.putExtra("who", "follow");
				fansIntent.putExtra("name", "2");
				context.startActivity(fansIntent);
				break;
			case R.id.layout_gift:
				img_my_gift.setVisibility(View.GONE);
				if (perMain == null) {
					return;
				}
				perMain.setGift_dot(false);
				sendTip();
				Intent giftIntent = new Intent(context,
						FragmentToActivity.class);
				giftIntent.putExtra("who", "mygift");
				context.startActivity(giftIntent);
				// Intent giftIntent = new Intent(context,
				// MyGiftActivity.class);
				// BaseJson base = new BaseJson();
				// base.setUser_id(Conf.userID);
				// base.setNick(Conf.userName);
				// giftIntent.putExtra("person", base);
				// startActivity(giftIntent);
				break;
			case R.id.layout_visitor:
				img_my_visitor.setVisibility(View.GONE);
				if (perMain == null) {
					return;
				}
				perMain.setVisiter_dot(false);
				sendTip();
				Intent visitorIntent = new Intent(new Intent(context,
						FragmentToActivity.class));
				visitorIntent.putExtra("who", "visitor");
				visitorIntent.putExtra("user_id", Conf.userID);
				context.startActivity(visitorIntent);
				break;
			case R.id.layout_text_vip:// 充值会员
				Intent intent = new Intent(context,
						FragmentToActivity.class);
				intent.putExtra("who", "vip");
				context.startActivity(intent);
				break;
			// case R.id.tv_my_morgift:
			// Intent giftIntent = new Intent(getActivity(),
			// MyGiftActivity.class);
			// BaseJson base = new BaseJson();
			// base.setUser_id(Conf.userID);
			// base.setNick(Conf.userName);
			// giftIntent.putExtra("person", base);
			// startActivity(giftIntent);
			// break;
			case R.id.img_avatar:
				ShowChoiceDialog();
				break;
			case R.id.layout_faq:
				Intent questionIntent = new Intent(context,
						FragmentToActivity.class);
				questionIntent.putExtra("who", "question");
				context.startActivity(questionIntent);
				break;
			case R.id.layout_setting:
				// 设置页面
				Intent setIntent = new Intent(context,
						FragmentToActivity.class);
				setIntent.putExtra("who", "setting");
				context.startActivity(setIntent);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// 判断隐藏红点
	private void sendTip() {

		Intent intent = new Intent();
		intent.setAction("com.action.inboxBadge.myself");
		if (!perMain.isVisiter_dot() && !perMain.isInfo_dot()
				&& !perMain.isFocuser_dot() && !perMain.isGift_dot()) {
			intent.putExtra("mine", false);
		} else {
			intent.putExtra("mine", true);
		}
		context.sendBroadcast(intent);
	}

	// 弹出选择图片对话框
	private void ShowChoiceDialog() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(context)
				.setTitle(R.string.str_uploadtitle)
				.setItems(choice, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						if (arg1 == 0) {
							// 调用相机拍照
							if (!BasicUtils.isSDCardAvaliable()) {
								BasicUtils.toast(StringUtils.getResourse(R.string.str_sdnull));
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
								context.startActivityForResult(intent, UPLOAD_CAMERA);
							} catch (Exception e) {
								// TODO: handle exception
							}

						} else {
							// 调用本地相册
							try {
								Intent intent = new Intent(Intent.ACTION_PICK,
										null);
								intent.setType("image/*");
								context.startActivityForResult(intent, UPLOAD_LOCAL);
							} catch (Exception e) {
								// TODO: handle exception
								try {
									Intent intent = new Intent();
									/* 开启Pictures画面Type设定为image */
									intent.setType("image/*");
									/* 使用Intent.ACTION_GET_CONTENT这个Action */
									intent.setAction(Intent.ACTION_GET_CONTENT);
									context.startActivityForResult(intent, UPLOAD_LOCAL);
								} catch (Exception e2) {
									// TODO: handle exception
									BasicUtils.toast("启动相册失败");
								}

							}
						}
					}

				}).create().show();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub

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
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		last_posx = scroll_myself.getScrollX();
		last_posy = scroll_myself.getScrollY();

	}

	private void getHttpData() {
		int cache_time = 0;
		RequestParams params = new RequestParams();
		try {
			params.addQueryStringParameter("cur_user", Conf.userID);// Conf.userID
		} catch (Exception e) {
			// TODO: handle exception
		}
		String key="me";
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key, cache_time, new CacheRequestCallBack() {
			
			@Override
			public void onFail(HttpException e, String result, String json) {
				// TODO Auto-generated method stub
				if (mDialog != null) {
					mDialog.dismiss();
				}ExitManager.getScreenManager().intentLogin(context,
						e.getExceptionCode() + "");
				if(json.equals("")){
					BasicUtils.toast(StringUtils.getResourse(R.string.str_net_register));
					return;
				}
			}
			
			@Override
			public void onData(String json) {
				// TODO Auto-generated method stub
				if (mDialog != null) {
					mDialog.dismiss();
				}
				if(json.equals("")){
					return;
				}
				try {
					perMain = new Gson().fromJson(json,
							BaseJson.class);
					if (perMain.getStatus().equals("200")) {
						initView();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
//		kjh.get(url, params, new HttpCallBack() {
//
//			@Override
//			public void onSuccess(Object obj) {
//				// TODO Auto-generated method stub
//				try {
//					perMain = new Gson().fromJson(obj.toString(),
//							BaseJson.class);
//					if (perMain.getStatus().equals("200")) {
//						initView();
//					}
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					if (mDialog != null) {
//						mDialog.dismiss();
//					}
//				}
//
//			}
//
//			@Override
//			public void onLoading(long count, long current) {
//				// TODO Auto-generated method stub
//			}
//
//			@Override
//			public void onFailure(Throwable t, int errorNo, String strMsg) {
//				// TODO Auto-generated method stub
//				if (mDialog != null) {
//					mDialog.dismiss();
//				}
//				ExitManager.getScreenManager().intentLogin(context,
//						StringUtils.httpRsponse(t.toString()));
//			}
//
//		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (null == BitmapUtils.pictureFile
				|| null == BitmapUtils.targetPictureFile) {
			BitmapUtils.initPictureFile();
		}
		switch (requestCode) {

		// 相机拍照
		case UPLOAD_CAMERA:
			try {
				if (resultCode == Activity.RESULT_CANCELED) {
					BitmapUtils.cleanAfterUploadAvatar();
					BitmapUtils.initPictureFile();
					return;
				}
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				if (currentapiVersion >= 11) {
					if (BitmapUtils.pictureFile.length() <= 1024) {
						BitmapUtils.cleanAfterUploadAvatar();
						BitmapUtils.initPictureFile();
						return;
					}
					startPhotoZoom(Uri.fromFile(BitmapUtils.pictureFile));
				} else {
					startPhotoCrop(data);
				}
				// mBitmap = null;
				// Options options = new Options();
				// options.inJustDecodeBounds = false;
				// options.inSampleSize = 2;
				// mBitmap = BitmapFactory.decodeFile(
				// BitmapUtils.pictureFile.getAbsolutePath(), options);
				// // 创建图片缩略图
				// if (mBitmap == null)
				// return;
				// img_my_icon.setImageBitmap(mBitmap);
				// byte_img = BitmapUtils.Bitmap2Bytes(mBitmap);
				// String icon = BitmapUtils.pictureFile.toString()
				// .replace(
				// BitmapUtils.pictureFile.toString(),
				// Conf.userID + "_" + System.currentTimeMillis()
				// + ".jpg");
				// UploadAliyun(icon);
			} catch (Exception e) {
				// TODO: handle exception
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				context.finish();

			}
			break;

		// 本地照片
		case UPLOAD_LOCAL:
			try {
				if (resultCode == Activity.RESULT_CANCELED) {
					BitmapUtils.cleanAfterUploadAvatar();
					BitmapUtils.initPictureFile();
					return;
				}
				if (data != null)
					startPhotoZoom(data.getData());
				// if (data == null) {
				// return;
				// }
				//
				// Uri imageuri = data.getData();
				// String[] prStrings = { MediaStore.Images.Media.DATA };
				// Cursor imageCursor = getActivity().managedQuery(imageuri,
				// prStrings, null, null, null);
				// int imgpath = imageCursor
				// .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// imageCursor.moveToFirst();
				// String image_path = imageCursor.getString(imgpath);
				//
				// File photoName = new File(image_path);
				// System.out.println("--------filename=" + photoName);
				// ContentResolver cr = getActivity().getContentResolver();
				//
				// mBitmap = null;
				//
				// Options options = new Options();
				// options.inJustDecodeBounds = false;
				// options.inSampleSize = 2;
				// mBitmap = BitmapFactory.decodeStream(
				// cr.openInputStream(imageuri), null, options);
				// // 创建图片缩略图
				// img_my_icon.setImageBitmap(mBitmap);
				// byte_img = BitmapUtils.Bitmap2Bytes(mBitmap);
				// String icon = image_path.replace(image_path, Conf.userID +
				// "_"
				// + System.currentTimeMillis() + ".jpg");
				//
				// UploadAliyun(icon);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				context.finish();
			}
			break;

		// 裁剪结果
		case PHOTO_REQUEST_CUT:
			if (resultCode == Activity.RESULT_CANCELED) {
				BitmapUtils.cleanAfterUploadAvatar();

				return;
			}
			if (BitmapUtils.targetPictureFile.length() < 1024) {
				BitmapUtils.cleanAfterUploadAvatar();
				BitmapUtils.initPictureFile();
				return;
			}
			mBitmap = BitmapUtils.getCompressImage(
					BitmapUtils.targetPictureFile.getAbsolutePath(), 100, 100);
			if (mBitmap == null)
				return;
			img_my_icon.setImageBitmap(mBitmap);
			Bitmap bitmap = BitmapUtils.getRoundedCornerBitmap(mBitmap);
			byte_img = BitmapUtils.Bitmap2Bytes(bitmap);

			// //上传图片
			// dialog = BasicUtils.showDialog(getActivity());
			// dialog.setMessage(getActivity().getResources().getString(
			// R.string.str_uploadtitle)
			// + "...");
			// dialog.show();
			UploadAliyun(Conf.userID + "_" + System.currentTimeMillis()
					+ ".jpg");
			break;
		default:
			break;
		}
	}

	private void UploadAliyun(final String filepath) {
		OSSBucket ossBucket = new OSSBucket(Conf.ACCESS_BUCKETNAME);
		ossBucket.setBucketACL(AccessControlList.PUBLIC_READ);

		OSSFile ossFile = new OSSFile(ossBucket, "avatar/" + filepath);
		ossFile.setUploadFilePath(
				BitmapUtils.targetPictureFile.getAbsolutePath(), "image/jpeg");
		ossFile.uploadInBackground(new SaveCallback() {

			@Override
			public void onProgress(String arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(String arg0, OSSException arg1) {
				// TODO Auto-generated method stub
//				BasicUtils.toast("图片上传失败");
			}

			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				UploadPostAvator(filepath);
			}
		});
		// new UploadObjectAsyncTask<Void>(JiMoApplication.ACCESS_ID,
		// JiMoApplication.ACCESS_KEY, Conf.ACCESS_BUCKETNAME, "avatar/"
		// + filepath) {
		//
		// /*
		// * (non-Javadoc) * @see
		// * android.os.AsyncTask#onPostExecute(java.lang.Object)
		// */
		// @Override
		// protected void onPostExecute(String result) {
		//
		// if (result != null) {
		// UploadPostAvator(filepath);
		// } else {
		// dialog.dismiss();
		// ViewInject.toast("图片上传失败");
		// }
		// }
		//
		// }.execute(byte_img);
	}

	private void UploadPostAvator(final String url) {
		// TODO Auto-generated method stub
		int cache_time = 0;
		String key = "me/icon/add";
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("cur_user", Conf.userID);
		params.addQueryStringParameter("icon", url);
		params.addQueryStringParameter("gender", Conf.gender);
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestGET(context, url, params, key, cache_time, new CacheRequestCallBack() {
			
			@Override
			public void onFail(HttpException e, String result, String json) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onData(String json) {
				// TODO Auto-generated method stub
				Conf.userImg = Conf.IMAGE_SERVER + "/avatar/" + url;
				BasicUtils.toast("图片上传成功");
			}
		});
//		kjh.get(Conf.URL + "me/icon/add", params, new HttpCallBack() {
//
//			@Override
//			public void onFailure(Throwable arg0, int arg1, String arg2) {
//				// TODO Auto-generated method stub
//				if (mDialog != null) {
//					mDialog.dismiss();
//				}
//				ExitManager.getScreenManager().intentLogin(context,
//						StringUtils.httpRsponse(arg0.toString()));
//			}
//
//			@Override
//			public void onLoading(long arg0, long arg1) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onSuccess(Object arg0) {
//				// TODO Auto-generated method stub
//				Conf.userImg = Conf.IMAGE_SERVER + "/avatar/" + url;
//				ViewInject.toast("图片上传成功");
//			}
//
//		});
	}

	/**
	 * 对拍摄的图片做放缩处理，然后去剪裁
	 * 
	 * @param data
	 */
	public void startPhotoCrop(Intent data) {
		Bundle extras = data.getExtras();
		Bitmap imageBitmap = (Bitmap) extras.get("data");
		// 异步地去执行图片的放缩处理
		AsyncTask<Bitmap, Void, Void> backgroundTask = new AsyncTask<Bitmap, Void, Void>() {

			@Override
			protected Void doInBackground(Bitmap... params) {
				if (params.length == 0) {
					return null;
				}
				Bitmap imageBitmap = params[0];
				Bitmap targetmap = BitmapUtils.changToFullScreenImage(
						context, imageBitmap);
				Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(
						context.getContentResolver(), targetmap, null,
						null));
				startPhotoZoom(uri);
				return null;
			}
		};
		backgroundTask.execute(imageBitmap);
	}

	// 图片的裁剪
	public void startPhotoZoom(Uri fromFile) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(fromFile, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX,outputY 是剪裁图片的宽高
		// intent.putExtra("outputX", 100);
		// intent.putExtra("outputY", 100);
		intent.putExtra("return-data", false);
		intent.putExtra("scale", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(BitmapUtils.targetPictureFile));
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		context.startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
			mBitmap = null;
		}

		if (refreshBroadcastReceiver != null) {
			context.unregisterReceiver(refreshBroadcastReceiver);
		}
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		tv_title.setText(R.string.str_mine);
		if (back != null && !back.equals("")) {
			layout_back.setVisibility(View.VISIBLE);
		} else {
			layout_back.setVisibility(View.GONE);
		}
		if (Conf.gender.equals("1")) {
			img_my_gender.setImageResource(R.drawable.male_icon);
			// layout_top_img.setBackgroundResource(R.drawable.detail_male);
		} else {
			img_my_gender.setImageResource(R.drawable.female_icon);
			// layout_top_img.setBackgroundResource(R.drawable.detail_female);
		}
		// if (perMain == null) {
		
		scroll_myself.post(new Runnable() {

			@Override
			public void run() {

				scroll_myself.scrollTo(last_posx, last_posy);
			}
		});
		if (Conf.user_VIP.equals("1")) {
			img_vip_status.setImageResource(R.drawable.vip_icon);
			// tv_vip.setText(R.string.str_top_super);
			// tv_vip.setVisibility(View.VISIBLE);
		} else {
			img_vip_status.setImageResource(R.drawable.vip_no_icon);
			tv_my_vipmsg.setText(StringUtils.getResourse(
					R.string.str_my_viptime));
			tv_my_vipday.setText(StringUtils.getResourse(R.string.str_my_vipred));
			// tv_vip.setText(R.string.str_top_putong);
			// tv_vip.setVisibility(View.VISIBLE);
			// layout_text_vip.setVisibility(View.VISIBLE);
		}
		
		getHttpData();
	}
}

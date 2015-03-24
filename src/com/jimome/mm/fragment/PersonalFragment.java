package com.jimome.mm.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.activity.GiftStoreActivity;
import com.jimome.mm.activity.MyShowActivity;
import com.jimome.mm.activity.TalkActivity;
import com.jimome.mm.adapter.VisitorAdapter;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.jimome.mm.view.AutoScrollViewPager;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.unjiaoyou.mm.R;

/**
 * 个人主页页面
 * 
 * @author admin
 * 
 */

public class PersonalFragment extends BaseFragment {

	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	// @ViewInject( R.id.img_intro_icon)
	// private ImageView img_intro_icon;
	// @ViewInject( R.id.tv_intro_name)
	// private TextView tv_intro_name;
	// @ViewInject( R.id.img_intro_gender)
	// private ImageView img_intro_gender;
	// @ViewInject( R.id.tv_intro_age)
	// private TextView tv_intro_age;
	// @ViewInject( R.id.tv_intro_height)
	// private TextView tv_intro_height;
	// @ViewInject( R.id.tv_intro_oppad)
	// private TextView tv_intro_oppad;
	@ViewInject(R.id.tv_intro_say)
	private TextView tv_intro_say;
	// @ViewInject( R.id.gv_intro_vistor)
	// private GridView gv_intro_vistor;
	// @ViewInject( R.id.img_intro_morevisitor)
	// private ImageView img_intro_morevisitor;
	// @ViewInject( R.id.tv_intro_giftnum)
	// private TextView tv_intro_giftnum;
	// @ViewInject( R.id.tv_intro_morgift)
	// private TextView tv_intro_morgift;
	// @ViewInject( R.id.gv_intro_gift)
	// private GridView gv_intro_gift;
	@ViewInject(R.id.img_like)
	private ImageView img_like;
	@ViewInject(R.id.tv_like)
	private TextView tv_like;
	// @ViewInject( R.id.img_gift)
	// private ImageView img_gift;
	// @ViewInject( R.id.img_xiu)
	// private ImageView img_xiu;
	// @ViewInject( R.id.img_chat)
	// private ImageView img_chat;
	@ViewInject(R.id.btn_jiahei)
	private Button btn_jiahei;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;// 返回
//	private Dialog mDialog;
	// @ViewInject( R.id.tv_person_nvshen)
	// private TextView tv_person_nvshen;
	@ViewInject(R.id.img_personal_show)
	private ImageView img_personal_show;
	// @ViewInject( R.id.img_personal_photo1)
	// private ImageView img_personal_photo1;
	// @ViewInject( R.id.img_personal_photo2)
	// private ImageView img_personal_photo2;
	@ViewInject(R.id.img_personal_photo)
	private ImageView img_personal_photo;
	@ViewInject(R.id.img_personal_video)
	private ImageView img_personal_video;
	// @ViewInject( R.id.img_personal_photo3)
	// private ImageView img_personal_photo3;
	// @ViewInject( R.id.layout_content)
	// private LinearLayout layout_content;
	// @ViewInject( R.id.layout_top_myimg)
	// private LinearLayout layout_top_myimg;
	// @ViewInject( R.id.layout_person_scroll)
	// private LinearLayout layout_person_scroll;
	@ViewInject(R.id.tv_area)
	private TextView tv_area;
	@ViewInject(R.id.tv_join)
	private TextView tv_join;
	@ViewInject(R.id.tv_nick)
	private TextView tv_nick;
	@ViewInject(R.id.img_gender)
	private ImageView img_gender;
	@ViewInject(R.id.tv_age)
	private TextView tv_age;
	@ViewInject(R.id.tv_distance)
	private TextView tv_distance;
	@ViewInject(R.id.layout_personal)
	private LinearLayout layout_personal;
	@ViewInject(R.id.tv_login_time)
	private TextView tv_login_time;
	@ViewInject(R.id.layout_dots)
	private LinearLayout layout_dots;
	@ViewInject(R.id.layout_sendchat)
	private LinearLayout layout_sendchat;
	@ViewInject(R.id.layout_calltel)
	private LinearLayout layout_calltel;
	@ViewInject(R.id.tv_buyvip)
	private TextView tv_buyvip;
	@ViewInject(R.id.view_pager)
	private AutoScrollViewPager photoPager;

	private VisitorAdapter vistorAdapter, giftAdapter;
	private boolean flag_like = false;
	private boolean flag_jia = false;
	private String[] add;// 加黑或举报
	private String[] report;// 举报内容
	private String user_id;
	private BaseJson perMain;
	private Dialog careDialog, cancelDialog;
	private String zsxUrl;
	private AlertDialog addDialog, reDialog;
	private int report_id;
	private String distance;

	int count = 0;
	// 循环间隔
	private int interval = 4000;
	int index = 0;
	private List<ImageView> dots;
	private Activity context;

	public PersonalFragment() {
	}

	public PersonalFragment(String distance, String user_id) {
		this.user_id = user_id;
		this.distance = distance;

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

	private void setView() {
		try {
			// tv_area.setText(perMain.getCity());
			tv_area.setText(Conf.city);
			tv_intro_say.setText(perMain.getIntro());
			tv_join.setText(perMain.getPurpose());

			if (perMain.getIs_vip().equals("0")) {
				layout_personal.setVisibility(View.VISIBLE);
				tv_login_time.setVisibility(View.GONE);
			} else {
				layout_personal.setVisibility(View.GONE);
				tv_login_time.setVisibility(View.VISIBLE);
				tv_login_time.setText(perMain.getLast_login());
			}
			if (perMain.getShow() != null && perMain.getShow().length > 0) {
				setImage(img_personal_show, perMain.getShow()[0],
						ImageLoadUtils.options, false);
			}

			if (perMain.getPhoto() != null && perMain.getPhoto().length > 0) {
				setImage(img_personal_photo, perMain.getPhoto()[0],
						ImageLoadUtils.options, false);
			}

			if (perMain.getVideo() != null && perMain.getVideo().length > 0) {
				setImage(img_personal_video, perMain.getVideo()[0],
						ImageLoadUtils.options, false);
			}
			tv_title.setText(perMain.getNick());
			tv_nick.setText(perMain.getNick());
			tv_age.setText(perMain.getAge());
			if (TextUtils.isEmpty(distance))
				tv_distance.setText(perMain.getDistance() + "km");
			else
				tv_distance.setText(distance + "km");
			if (perMain.getGender().equals("1")) {
				img_gender.setImageResource(R.drawable.male_icon);
			} else {
				img_gender.setImageResource(R.drawable.female_icon);
			}

			if (perMain.getIs_focus() != null
					&& !perMain.getIs_focus().equals("0")) {
				img_like.setImageResource(R.drawable.detail_save_p);
				flag_like = true;
			} else {
				img_like.setImageResource(R.drawable.detail_save_n);
				flag_like = false;

			}

			count = perMain.getShows().length;
			photoPager
					.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT,
							Conf.height / 2));

			photoPager.setCurrentItem(count * 100);
			photoPager.setInterval(interval);
			if (count < 1) {
				photoPager.stopAutoScroll();
			} else {
				photoPager.startAutoScroll();
				photoPager.setAdapter(new PhotoPagerAdapter());

			}

			photoPager.setOnPageChangeListener(new PhotoPageChangeListener());
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
						StatService.onEvent(context, "page-img-glide",
								"eventLabel", 1);
						if (count < 1) {
							photoPager.stopAutoScroll();
						} else {
							photoPager.startAutoScroll();
						}

						break;
					case MotionEvent.ACTION_UP:
						if (count < 1) {
							photoPager.stopAutoScroll();
						} else {
							photoPager.startAutoScroll();
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

			// if (perMain.getGender() != null &&
			// perMain.getGender().equals("1")) {
			// layout_top_myimg.setBackgroundResource(R.drawable.detail_male);
			// img_intro_gender.setImageResource(R.drawable.male_icon);
			// tv_person_nvshen.setText(getResources().getString(
			// R.string.str_my_nanshenxiu));
			// } else {
			// layout_top_myimg
			// .setBackgroundResource(R.drawable.detail_female);
			// img_intro_gender.setImageResource(R.drawable.female_icon);
			// tv_person_nvshen.setText(getResources().getString(
			// R.string.str_my_nvshenxiu));
			// }
			// Conf.oppName = perMain.getNick();
			// setImage(img_intro_icon, perMain.getIcon(),
			// ImageLoadUtils.optionsRounded);
			// if (perMain.getIs_focus() != null
			// && !perMain.getIs_focus().equals("0")) {
			// img_like.setImageResource(R.drawable.detail_save_p);
			// tv_like.setText(R.string.str_personal_like);
			// tv_like.setTextColor(getResources().getColor(R.color.red));
			// flag_like = true;
			// } else {
			// img_like.setImageResource(R.drawable.detail_save_n);
			// tv_like.setText(R.string.str_personal_guanzhu);
			// tv_like.setTextColor(getResources().getColor(R.color.lightblue));
			// flag_like = false;
			//
			// }
			// tv_intro_name.setText(perMain.getNick());
			// if (perMain.getAge() != null && !perMain.getAge().equals(""))
			// tv_intro_age.setText(perMain.getAge()
			// + getString(R.string.str_sui) + "/");
			// if (perMain.getHeight() != null &&
			// !perMain.getHeight().equals(""))
			// tv_intro_height.setText(perMain.getHeight()
			// + getString(R.string.str_heightuntil) + "");
			// tv_intro_oppad.setText("");
			// tv_intro_say.setText(perMain.getIntro());
			// if (perMain.getShow() != null && perMain.getShow().length > 0) {
			// setImage(img_personal_show, perMain.getShow()[0],
			// ImageLoadUtils.options);
			// }
			// if (perMain.getPhoto() != null && perMain.getPhoto().length > 0)
			// {
			// setImage(img_personal_photo, perMain.getPhoto()[0],
			// ImageLoadUtils.options);
			// if (perMain.getPhoto().length == 4) {
			// setImage(img_personal_photo1, perMain.getPhoto()[1],
			// ImageLoadUtils.options);
			// setImage(img_personal_photo2, perMain.getPhoto()[2],
			// ImageLoadUtils.options);
			// setImage(img_personal_photo3, perMain.getPhoto()[3],
			// ImageLoadUtils.options);
			// } else if (perMain.getPhoto().length == 3) {
			// setImage(img_personal_photo1, perMain.getPhoto()[1],
			// ImageLoadUtils.options);
			// setImage(img_personal_photo2, perMain.getPhoto()[2],
			// ImageLoadUtils.options);
			// } else if (perMain.getPhoto().length == 2) {
			// setImage(img_personal_photo1, perMain.getPhoto()[1],
			// ImageLoadUtils.options);
			// } else {
			// setImage(img_personal_photo1, perMain.getPhoto()[0],
			// ImageLoadUtils.options);
			// }
			// }
			// if (perMain.getVideo() != null && perMain.getVideo().length > 0)
			// {
			// setImage(img_personal_video, perMain.getVideo()[0],
			// ImageLoadUtils.options);
			// }
			// vistorAdapter = new VisitorAdapter(context,
			// changeVisited(perMain.getVisiteds()));
			// gv_intro_vistor.setAdapter(vistorAdapter);
			// gv_intro_vistor.setOnItemClickListener(this);
			// tv_intro_giftnum.setText("(" + perMain.getGift_nums() + ")");
			// if (perMain.getGifts() != null && perMain.getGifts().size() > 0)
			// {
			// giftAdapter = new VisitorAdapter(context,
			// perMain.getGifts());
			// gv_intro_gift.setAdapter(giftAdapter);
			// gv_intro_gift.setVisibility(View.VISIBLE);
			// }
			// gv_intro_gift.setOnItemClickListener(this);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	class PhotoPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// if(count < 1){
			// return count;
			// }else{
			return Integer.MAX_VALUE;
			// }

			// return count;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			try {
				ImageView imageView = new ImageView(context);
				imageView.setAdjustViewBounds(true);
				// TODO 调整图片大小
				imageView.setScaleType(ScaleType.CENTER_CROP);
				// imageView.setLayoutParams(new LinearLayout.LayoutParams(
				// LinearLayout.LayoutParams.MATCH_PARENT,
				// LinearLayout.LayoutParams.MATCH_PARENT));
				setImage(imageView, perMain.getShows()[position % count],
						ImageLoadUtils.options, true);

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
			try {
				if (count != 0) {
					for (int i = 0; i < count; i++) {
						dots.get(i).setImageResource(
								R.drawable.page_dots_unfocused);
					}
					dots.get(position % count).setImageResource(
							R.drawable.page_dots_focused);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
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

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_personal, container, false);
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mDialog != null)
			mDialog.dismiss();
		if (refreshBroadcastReceiver != null)
			context.unregisterReceiver(refreshBroadcastReceiver);
	}

	private BroadcastReceiver refreshBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			try {
				if (intent.getAction().equals("jimome.action.refresh")) {

					zsxUrl = intent.getExtras().getString("zsxurl");
					getHttpData("send");
				} else if (intent.getAction().equals("jimome.action.getgift")) {
					BaseJson gift = new BaseJson();
					gift = (BaseJson) intent.getSerializableExtra("getgift");
					if (perMain.getGift_nums() == null
							|| perMain.getGift_nums().trim().equals("")
							|| perMain.getGift_nums().trim().equals("0"))
						perMain.setGift_nums("1");
					else {
						perMain.setGift_nums((Integer.valueOf(perMain
								.getGift_nums().trim()) + 1) + "");
					}
					// tv_intro_giftnum.setText("(" + perMain.getGift_nums() +
					// ")");
					if (perMain.getGifts() != null
							&& perMain.getGifts().size() > 0) {
						giftAdapter.insertData(gift);
						giftAdapter.notifyDataSetChanged();
					} else {
						ArrayList<BaseJson> list = new ArrayList<BaseJson>();
						list.add(gift);
						perMain.setGifts(list);
						giftAdapter = new VisitorAdapter(context,
								perMain.getGifts());
						// gv_intro_gift.setAdapter(giftAdapter);
						// gv_intro_gift.setVisibility(View.VISIBLE);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("jimome.action.refresh");
		intentFilter.addAction("jimome.action.getgift");
		context.registerReceiver(refreshBroadcastReceiver, intentFilter);
	
	}

	private void waitDialog() {
//		mDialog = BasicUtils.showDialog(context, R.style.BasicDialog);
//		mDialog.setContentView(R.layout.dialog_wait);
//		mDialog.setCanceledOnTouchOutside(false);
//
//		Animation anim = AnimationUtils.loadAnimation(context,
//				R.anim.dialog_prog);
//		LinearInterpolator lir = new LinearInterpolator();
//		anim.setInterpolator(lir);
//		mDialog.findViewById(R.id.img_dialog_progress).startAnimation(anim);
//		mDialog.setOnKeyListener(new OnKeyListener() {
//			@Override
//			public boolean onKey(DialogInterface dialog, int keyCode,
//					KeyEvent event) {
//				if (keyCode == KeyEvent.KEYCODE_BACK
//						&& event.getAction() == KeyEvent.ACTION_DOWN) {
//					if (!context.isFinishing())
//						mDialog.dismiss();
//
//					return false;
//				}
//				return false;
//			}
//		});
		mDialog.show();
	}

	private void setImage(final ImageView img, String url,
			DisplayImageOptions options, final boolean flag) {
		ImageLoadUtils.imageLoader.displayImage(url, img, options,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub
						if (Conf.gender.equals("1"))
							img.setImageResource(R.drawable.default_female);
						else
							img.setImageResource(R.drawable.default_male);
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						// TODO Auto-generated method stub
						if (Conf.gender.equals("1"))
							img.setImageResource(R.drawable.default_female);
						else
							img.setImageResource(R.drawable.default_male);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						try {
							if (flag) {
								int height = (Conf.width * loadedImage
										.getHeight()) / loadedImage.getWidth();
								android.support.v4.view.ViewPager.LayoutParams lp = new android.support.v4.view.ViewPager.LayoutParams();
								lp.height = height;
								lp.width = Conf.width;
								img.setLayoutParams(lp);
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}

					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}
				});
	}

	@OnClick({ R.id.layout_back, R.id.tv_buyvip, R.id.btn_jiahei,
			R.id.img_like, R.id.img_gift, R.id.img_xiu, R.id.img_chat,
			R.id.img_personal_show,R.id.img_personal_photo,R.id.img_personal_video,
			R.id.layout_sendchat, R.id.layout_calltel })
	public void onClickView(View v) {
		try {
			switch (v.getId()) {
			case R.id.layout_back:// 返回
				context.finish();
				break;
			case R.id.tv_buyvip:
				Intent perintent = new Intent(context, FragmentToActivity.class);
				perintent.putExtra("who", "vip");
				context.startActivity(perintent);
				break;
			case R.id.btn_jiahei:
				if (!flag_jia) {
					add = getResources().getStringArray(R.array.addblack);
					report = getResources().getStringArray(R.array.report);
					addDialog = new AlertDialog.Builder(context).setItems(add,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int pos) {
									// TODO Auto-generated method stub
									if (pos == 0) {
										getHttpData("black");
									} else {
										reDialog = new AlertDialog.Builder(
												context)
												.setItems(
														report,
														new DialogInterface.OnClickListener() {

															@Override
															public void onClick(
																	DialogInterface dia,
																	int pos) {
																// TODO
																// Auto-generated
																// method
																// stub
																report_id = pos;
																if (BasicUtils
																		.isFastDoubleClick()) {
																	return;
																}
																getHttpData("report");
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
									if (BasicUtils.isFastDoubleClick()) {
										return;
									}
									getHttpData("addcancel");
								}
							});
					cancelDialog.show();
				}
				break;
			// case R.id.tv_intro_morgift:
			// if (perMain.getGifts() != null && perMain.getGifts().size() > 0)
			// {
			// Intent myintent = new Intent(getActivity(),
			// MyGiftActivity.class);
			// Bundle mybundle = new Bundle();
			// BaseJson person = new BaseJson();
			// person.setUser_id(user_id);
			// person.setNick(perMain.getNick());
			// mybundle.putSerializable("person", person);
			// myintent.putExtras(mybundle);
			// startActivity(myintent);
			// }
			// break;
			// case R.id.img_intro_morevisitor:
			// if (perMain != null && perMain.getVisiteds() != null
			// && perMain.getVisiteds().length > 0) {
			// Intent visitorintent = new Intent(new Intent(getActivity(),
			// FragmentToActivity.class));
			// visitorintent.putExtra("who", "visitor");
			// visitorintent.putExtra("user_id", user_id);
			// this.startActivity(visitorintent);
			// }
			// break;
			case R.id.img_like:
				if (!flag_like) {
					if (BasicUtils.isFastDoubleClick()) {
						return;
					}
					getHttpData("add");
				} else {
					if (BasicUtils.isFastDoubleClick()) {
						return;
					}
					careDialog = BasicUtils.showDialog(context,
							R.style.BasicDialog);
					careDialog.setContentView(R.layout.dialog_jiahei);
					careDialog.setCanceledOnTouchOutside(false);
					((TextView) careDialog.findViewById(R.id.tv_dialog_msg))
							.setText(StringUtils.getResourse(R.string.str_personal_unlike));

					((Button) careDialog.findViewById(R.id.btn_dialog_cancle))
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									careDialog.dismiss();
								}
							});
					((Button) careDialog.findViewById(R.id.btn_dialog_sure))
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									// 上传事件
									if (BasicUtils.isFastDoubleClick()) {
										return;
									}
									getHttpData("cancel");
								}
							});

					careDialog.show();
				}

				break;
			case R.id.img_gift:
				Intent intent = new Intent(context, GiftStoreActivity.class);
				Bundle bundle = new Bundle();
				BaseJson personal = new BaseJson();
				personal.setUser_id(user_id);
				personal.setNick(perMain.getNick());
				bundle.putSerializable("person", personal);
				intent.putExtras(bundle);
				intent.putExtra("type", "user");
				context.startActivity(intent);
				break;
			case R.id.img_xiu:
				// Intent videointent = new Intent(context,
				// VideoRecorderActivity.class);
				// videointent.putExtra("type", "4");
				// videointent.putExtra("text", "");
				// startActivity(videointent);
				break;
			case R.id.img_chat:

				getHttpData("chat");
				break;
			case R.id.img_personal_show:
				if (perMain != null && perMain.getShow() != null
						&& perMain.getShow().length > 0) {
					Intent nvIntent = new Intent(context, MyShowActivity.class);
					nvIntent.putExtra("type", 1);
					nvIntent.putExtra("user_id", user_id);
					if (perMain.getGender() != null)
						nvIntent.putExtra("gender", perMain.getGender());
					context.startActivity(nvIntent);
				} else {
				}
				break;
			// case R.id.img_personal_photo1:
			//
			// case R.id.img_personal_photo2:
			//
			// case R.id.img_personal_photo3:

			case R.id.img_personal_photo:
				if (perMain != null && perMain.getPhoto() != null
						&& perMain.getPhoto().length > 0) {
					Intent shenIntent = new Intent(context,
							MyShowActivity.class);
					shenIntent.putExtra("type", 2);
					shenIntent.putExtra("user_id", user_id);
					context.startActivity(shenIntent);
				}
				break;
			case R.id.img_personal_video:
				StatService.onEvent(context, "page-shipinxiu-button",
						"eventLabel", 1);
				if (perMain != null && perMain.getVideo() != null
						&& perMain.getVideo().length > 0
						&& Conf.user_VIP.equals("1")) {
					Intent videoIntent = new Intent(context,
							MyShowActivity.class);
					videoIntent.putExtra("type", 3);
					videoIntent.putExtra("user_id", user_id);
					context.startActivity(videoIntent);
				} else {
					if (Conf.user_VIP.equals("0")) {
						final Dialog dialog = BasicUtils.showDialog(context,
								R.style.BasicDialog);
						dialog.setContentView(R.layout.dialog_rechargevip);
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
										Intent intent = new Intent(context,
												FragmentToActivity.class);
										intent.putExtra("who", "vip");
										context.startActivity(intent);
									}
								});
						dialog.show();
					}
				}
				break;
			case R.id.layout_sendchat:
				StatService.onEvent(context, "click--page-m-button",
						"eventLabel", 1);
				getHttpData("chat");
				break;
			case R.id.layout_calltel:

				StatService.onEvent(context, "click-page-phone-dial",
						"eventLabel", 1);
				getHttpData("callphone");
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
			if (perMain == null) {
				Toast.makeText(context, StringUtils.getResourse(R.string.str_date_null),
						Toast.LENGTH_SHORT).show();
			}
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
							context.startActivity(intent);
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

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	// @Override
	// public void onItemClick(AdapterView<?> adapterView, View view, int pos,
	// long arg3) {
	// // TODO Auto-generated method stub
	// super.onItemClick(adapterView, view, pos, arg3);
	// switch (adapterView.getId()) {
	// case R.id.gv_intro_vistor:
	// if (perMain != null && perMain.getVisiteds() != null
	// && perMain.getVisiteds().length > 0) {
	// Intent intent = new Intent(new Intent(getActivity(),
	// FragmentToActivity.class));
	// intent.putExtra("who", "visitor");
	// intent.putExtra("user_id", user_id);
	// this.startActivity(intent);
	// }
	// break;
	// case R.id.gv_intro_gift:
	// if (perMain.getGifts() != null && perMain.getGifts().size() > 0) {
	// Intent myintent = new Intent(getActivity(),
	// MyGiftActivity.class);
	// Bundle mybundle = new Bundle();
	// BaseJson person = new BaseJson();
	// person.setUser_id(user_id);
	// person.setNick(perMain.getNick());
	// mybundle.putSerializable("person", person);
	// myintent.putExtras(mybundle);
	// startActivity(myintent);
	// }
	// break;
	// default:
	// break;
	// }
	// }

	private void getHttpData(final String type) {

		RequestParams params = new RequestParams();
		String key = "";
		try {
			if (type.equals("main")) {
				key = "user/v2";
				params.addQueryStringParameter("user_id", user_id);
				params.addQueryStringParameter("cur_user", Conf.userID);
			} else if (type.equals("chat")) {
				key = "user/msg";
				params.addQueryStringParameter("sender", user_id);
				params.addQueryStringParameter("receiver", Conf.userID);
			} else if (type.equals("add")) {
				key = "user/focus/add";
				params.addQueryStringParameter("user_id", user_id);
				params.addQueryStringParameter("cur_user", Conf.userID);
			} else if (type.equals("cancel")) {
				key = "user/focus/cancel";
				params.addQueryStringParameter("user_id", user_id);
				params.addQueryStringParameter("cur_user", Conf.userID);
			} else if (type.equals("send")) {
				key = "msg/send";
				params.addQueryStringParameter("receiver", user_id);
				params.addQueryStringParameter("sender", Conf.userID);
				params.addQueryStringParameter("text", zsxUrl);
				params.addQueryStringParameter("flag", "3");
			} else if (type.equals("report")) {
				key = "photo/report";
				params.addQueryStringParameter("user1", Conf.userID);
				params.addQueryStringParameter("user2", user_id);
				params.addQueryStringParameter("flag", report_id + "");
			} else if (type.equals("black")) {
				key = "photo/black/add";
				params.addQueryStringParameter("cur_user", Conf.userID);
				params.addQueryStringParameter("user_id", user_id);
			} else if (type.equals("addcancel")) {
				key = "photo/black/cancel";
				params.addQueryStringParameter("cur_user", Conf.userID);
				params.addQueryStringParameter("user_id", user_id);
			} else if (type.equals("callphone")) {
				key = "msg/cellphone/get";
				params.addQueryStringParameter("cur_user", Conf.userID);
				params.addQueryStringParameter("user_id", user_id);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key, 0,
				new CacheRequestCallBack() {

					@Override
					public void onData(String obj) {
						// TODO Auto-generated method stub
						try {
							if (type.equals("main")) {
								perMain = new Gson().fromJson(obj,
										BaseJson.class);
								if (perMain.getStatus().equals("200")) {
									setView();
									// layout_person_scroll.setVisibility(View.VISIBLE);
								} else {
									Toast.makeText(context,
											StringUtils.getResourse(R.string.str_date_null),
											Toast.LENGTH_SHORT).show();

								}
							} else if (type.equals("chat")) {
								BaseJson person = new Gson().fromJson(obj,
										BaseJson.class);
								if (person.getStatus().equals("200")) {
									Intent chatIntent = new Intent(context,
											TalkActivity.class);
									person.setIcon(perMain.getIcon());
									person.setNick(perMain.getNick());
									person.setSender(user_id);
									chatIntent.putExtra("person", person);
									context.startActivity(chatIntent);
								} else {
									Toast.makeText(context,
											StringUtils.getResourse(R.string.str_date_null),
											Toast.LENGTH_SHORT).show();
								}
							} else if (type.equals("add")) {
								if (obj.contains("200")) {
									img_like.setImageResource(R.drawable.detail_save_p);
									tv_like.setText(R.string.str_personal_like);
									// tv_like.setTextColor(getResources().getColor(
									// R.color.red));
									flag_like = true;
									Toast.makeText(
											context,
											StringUtils.getResourse(R.string.str_personal_add),
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											context,
											StringUtils.getResourse(R.string.str_personal_addfail),
											Toast.LENGTH_SHORT).show();
								}
							} else if (type.equals("cancel")) {
								if (obj.contains("200")) {
									careDialog.dismiss();
									img_like.setImageResource(R.drawable.detail_save_n);
									tv_like.setText(R.string.str_personal_guanzhu);
									// tv_like.setTextColor(getResources().getColor(
									// R.color.lightblue));
									flag_like = false;
									Toast.makeText(
											context,
											StringUtils.getResourse(R.string.str_personal_cancle),
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											context,
											StringUtils.getResourse(R.string.str_personal_canclefail),
											Toast.LENGTH_SHORT).show();
								}
							} else if (type.equals("send")) {
								BaseJson base = new Gson().fromJson(obj,
										BaseJson.class);
								if (base.getStatus().equals("200")) {
									SelectFragment.sendMsg = true;
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
							} else if (type.equals("report")) {
								if (obj.contains("200")) {
									flag_jia = true;
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
							} else if (type.equals("black")) {
								if (obj.contains("200")) {
									flag_jia = true;
									Toast.makeText(
											context,
											StringUtils.getResourse(R.string.str_person_blacksuccess),
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											context,
											StringUtils.getResourse(R.string.str_person_blackfail),
											Toast.LENGTH_SHORT).show();
								}
							} else if (type.equals("addcancel")) {
								if (obj.contains("200")) {
									flag_jia = false;
									Toast.makeText(
											context,
											StringUtils.getResourse(R.string.str_person_blacksuccess),
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											context,
											StringUtils.getResourse(R.string.str_person_blackfail),
											Toast.LENGTH_SHORT).show();
								}
							} else if (type.equals("callphone")) {
								BaseJson data = new Gson().fromJson(obj,
										BaseJson.class);
								if (data.getStatus().equals("200"))
									showPhoneDialog(data.getTip());
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (mDialog != null) {
								mDialog.dismiss();
							}
							if (type.equals("report")) {
								if (reDialog != null) {
									reDialog.dismiss();
								}
							} else if (type.equals("black")) {
								if (addDialog != null)
									addDialog.dismiss();
							} else if (type.equals("addcancel")) {
								if (cancelDialog != null)
									cancelDialog.dismiss();
							}

						}
					}

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub
						Toast.makeText(context, "网络加载失败，请检查您的网络  " + result,
								Toast.LENGTH_SHORT).show();
						if (mDialog != null) {
							mDialog.dismiss();
						}
						if (type.equals("report")) {
							if (reDialog != null) {
								reDialog.dismiss();
							}

						} else if (type.equals("black")) {
							if (addDialog != null)
								addDialog.dismiss();
						} else if (type.equals("addcancel")) {
							if (cancelDialog != null)
								cancelDialog.dismiss();
						}
						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
					}

				});
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
		btn_jiahei.setVisibility(View.VISIBLE);
		int width = (int) (0.33 * Conf.width);
		img_personal_video
				.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
						width, width));
		img_personal_photo
				.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
						width, width));
		img_personal_show
				.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
						width, width));
		// img_personal_photo1
		// .setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
		// width, width));
		// img_personal_photo2
		// .setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
		// width, width));
		// img_personal_photo3
		// .setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
		// 2 * width + 10, 2 * width + 10));
		dots = new ArrayList<ImageView>();
		waitDialog();
		getHttpData("main");
	}
}

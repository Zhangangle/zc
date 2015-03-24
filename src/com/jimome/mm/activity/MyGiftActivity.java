package com.jimome.mm.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.unjiaoyou.mm.R;
import com.jimome.mm.adapter.GiftGridAdapter;
import com.jimome.mm.adapter.GiftListAdapter;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.NetworkUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.jimome.mm.view.PullScrollView;
import com.jimome.mm.view.PullScrollView.OnPullListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 礼物页面
 * 
 * @author Administrator
 * 
 */
@ContentView(R.layout.activity_gift_list)
public class MyGiftActivity extends BaseFragmentActivity implements
		OnPullListener, OnItemClickListener {
	private Context context;
	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	@ViewInject(R.id.layout_gift_top)
	private LinearLayout layout_gift_top;
	@ViewInject(R.id.btn_gift_top)
	private Button btn_gift_top;
	@ViewInject(R.id.vPager_gift)
	private ViewPager mPager;
	private List<View> listViews;
	private int offset = 1;
	private PullScrollView pullScrollView_list, pullScrollView_grid;
	private LinearLayout contentLayout_list, contentLayout_grid;
	private GridView gv;
	private ListView lv;
	private GiftListAdapter listAdapter;
	private GiftGridAdapter gridAdapter;
	private BaseJson person, base_person, base_gift;
//	private Dialog mDialog;
	private int page = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		ViewUtils.inject(this);
		ExitManager.getScreenManager().pushActivity(this);
		btn_gift_top.setSelected(false);
		layout_gift_top.setVisibility(View.VISIBLE);
		layout_back.setVisibility(View.VISIBLE);
		Intent intent = getIntent();
		person = (BaseJson) intent.getSerializableExtra("person");
		if (person.getUser_id().equals(Conf.userID)) {
			tv_title.setText(StringUtils.getResourse(R.string.str_mine)
					+ StringUtils.getResourse(R.string.str_gift_de));
		} else {
			tv_title.setText(person.getNick() + StringUtils.getResourse(R.string.str_gift_de));
		}

		IntiViewPager();
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
//					if (!isFinishing())
//						mDialog.dismiss();
//
//					return false;
//				}
//				return false;
//			}
//		});
		mDialog.show();
	}

	/**
	 * 初始化滑动控件
	 * 
	 * @author admin
	 * 
	 */
	private void IntiViewPager() {
		LayoutInflater mInflater = getLayoutInflater();
		listViews = new ArrayList<View>();
		listViews.add(mInflater.inflate(R.layout.layout_gift_list, null));
		listViews.add(mInflater.inflate(R.layout.layout_gift_list, null));

		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		View mylist = listViews.get(0);
		pullScrollView_list = (PullScrollView) mylist
				.findViewById(R.id.pullscroll_left_list);
		View mygrid = listViews.get(1);
		pullScrollView_grid = (PullScrollView) mygrid
				.findViewById(R.id.pullscroll_left_list);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		initView();
	}

	private void initView() {
		contentLayout_list = (LinearLayout) getLayoutInflater().inflate(
				R.layout.layout_recommend, null);
		lv = (ListView) contentLayout_list.findViewById(R.id.lv_recommend);
		pullScrollView_list.addBodyView(contentLayout_list);
		pullScrollView_list.setOnPullListener(this);
		lv.setOnItemClickListener(this);

		pullScrollView_list.setheaderViewGone();
		pullScrollView_list.setfooterViewGone();
		contentLayout_grid = (LinearLayout) getLayoutInflater().inflate(
				R.layout.layout_meitao, null);
		gv = (GridView) contentLayout_grid.findViewById(R.id.gv_meitao);
		pullScrollView_grid.addBodyView(contentLayout_grid);
		pullScrollView_grid.setOnPullListener(this);
		pullScrollView_grid.setheaderViewGone();
		pullScrollView_grid.setfooterViewGone();
		if (NetworkUtils.checkNet(context)) {
			getHttpData("list");
		} else {
			Toast.makeText(context, StringUtils.getResourse(R.string.str_net_register),
					Toast.LENGTH_SHORT).show();
			pullScrollView_list.setfooterViewReset();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
	}

	// 适配器
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			super();
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(mListViews.get(position));
		}

		@Override
		public void finishUpdate(ViewGroup container) {

		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDialog != null)
			mDialog.dismiss();
		ExitManager.getScreenManager().pullActivity(this);
	}

	@OnClick({ R.id.layout_back, R.id.btn_gift_top })
	public void onClickView(View v) {

		switch (v.getId()) {
		case R.id.layout_back:
			finish();
			break;
		case R.id.btn_gift_top:
			if (offset != listViews.size() - 1) {
				btn_gift_top.setSelected(false);
				mPager.setCurrentItem(offset);
				offset = 1;
			} else {
				btn_gift_top.setSelected(true);
				mPager.setCurrentItem(offset);
				offset = 0;
			}
			break;
		default:
			break;
		}
	}

	// 实现页卡切换监听
	public class MyOnPageChangeListener implements OnPageChangeListener {

		// 当滑动状态改变时调用
		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		// 当新的页面被选中时调用
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		// 当新的页面被选中时调用
		@Override
		public void onPageSelected(int arg0) {
			if (arg0 != listViews.size() - 1) {
				btn_gift_top.setSelected(false);
				if (base_person != null && base_person.getGifts() != null
						&& base_person.getGifts().size() > 0) {
				} else {
					getHttpData("list");
				}
			} else {
				btn_gift_top.setSelected(true);
				if (base_gift != null && base_gift.getGifts() != null
						&& base_gift.getGifts().size() > 0) {
				} else {
					getHttpData("gift");

				}
			}
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

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadMore() {
		// TODO Auto-generated method stub
		if (NetworkUtils.checkNet(context)) {

			if (base_person.getGifts() == null
					|| base_person.getGifts().size() < 1) {
				page = 1;

			} else
				++page;
			getHttpData("list");
		} else {
			Toast.makeText(context, StringUtils.getResourse(R.string.str_net_register),
					Toast.LENGTH_SHORT).show();
			pullScrollView_list.setfooterViewReset();
		}
	}

	private void getHttpData(final String type) {
		waitDialog();
		RequestParams params = new RequestParams();
		String key = "";
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		try {
			params.addQueryStringParameter("user_id", person.getUser_id());// Conf.gender

			if (type.equals("list")) {
				key = "photo/gift/list/more";
				params.addQueryStringParameter("page", String.valueOf(page));
			} else {
				key = "photo/gift/icon/more";
			}
			CacheRequest.requestGET(context, key, params, key, 0,
					new CacheRequestCallBack() {
						@Override
						public void onData(String json) {
							// TODO Auto-generated method stub

							try {
								if (type.equals("list")) {
									base_person = new Gson().fromJson(json,
											BaseJson.class);
									if (base_person.getStatus().equals("200")) {
										if (listAdapter == null) {
											if (base_person.getGifts() != null
													&& base_person.getGifts()
															.size() > 0) {

												listAdapter = new GiftListAdapter(
														context, base_person
																.getGifts(),
														person);
												lv.setAdapter(listAdapter);
											}
										} else {

									listAdapter.insertData(base_person
											.getGifts());
									listAdapter.notifyDataSetChanged();
								}

									} else {
										if (page > 1)
											--page;
									}
								} else {
									base_gift = new Gson().fromJson(json,
											BaseJson.class);
									if (base_gift.getStatus().equals("200")) {
										if (gridAdapter == null) {
											if (base_gift.getGifts() != null
													&& base_gift.getGifts()
															.size() > 0) {
												gridAdapter = new GiftGridAdapter(
														context, base_gift
																.getGifts(),
														person);
												gv.setAdapter(gridAdapter);
											}
										} else {
											gridAdapter.insertData(base_gift
													.getGifts());
											gridAdapter.notifyDataSetChanged();
										}
									} else {
										if (page > 1)
											--page;
									}
								}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if (base_person != null
								&& base_person.getGifts() != null
								&& base_person.getGifts().size() > 10) {
							pullScrollView_list.setfooterViewReset();
							pullScrollView_list.setVisibility(View.VISIBLE);
						}
						if (mDialog != null) {
							mDialog.dismiss();
						}
					}
				}

						@Override
						public void onFail(HttpException e, String result,
								String json) {
							pullScrollView_list.setfooterViewReset();
							pullScrollView_list.setVisibility(View.VISIBLE);
							if (page > 1)
								--page;
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

}

package com.jimome.mm.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.adapter.GiftListAdapter;
import com.jimome.mm.adapter.MyGiftListAdapter;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.NetworkUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.jimome.mm.view.PullScrollView;
import com.jimome.mm.view.PullScrollView.OnPullListener;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.unjiaoyou.mm.R;

/**
 * 收到/送出礼物页面
 * 
 * @author admin
 * 
 */

public class MyGiftFragment extends BaseFragment implements OnPullListener,
		OnItemClickListener {

	@ViewInject(R.id.layout_mine_fans)
	private LinearLayout layout_mine_gift;
	@ViewInject(R.id.tv_mine_midfouce)
	private TextView tv_mine_midget;
	@ViewInject(R.id.tv_mine_midfans)
	private TextView tv_mine_midsend;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	private ListView lv_get, lv_send;
	private int getpage = 1, fanspage = 1;
	private Dialog mDialog;
	private GiftListAdapter getAdapter;
	private MyGiftListAdapter sendAdapter;
	private BaseJson base_get, base_send, base_user;// 收到,送出列表
	private String name = "";

	@ViewInject(R.id.vPager_gift)
	private ViewPager mPager;
	private List<View> listViews;
	// private int offset = 1;
	private PullScrollView pullScrollView_get, pullScrollView_send;
	private LinearLayout contentLayout_get, contentLayout_send;
	private Activity context;

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.activity_gift_list, container, false);
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
		mDialog.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("jimome.action.getgift");
		intentFilter.addAction("jimome.action.getbackgift");
		intentFilter.addAction("jimome.action.returngift");
		context.registerReceiver(mRefreshBroadcastReceiver, intentFilter);
		base_user = new BaseJson();
		base_user.setUser_id(Conf.userID);
		base_user.setNick(Conf.userName);
		name = "1";

	}

	// 设置更新启动的广播
	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("jimome.action.getgift")) {

			} else if (action.equals("jimome.action.returngift")) {

			} else if (action.equals("jimome.action.getbackgift")) {
				try {
					sendAdapter.removeDate(intent.getIntExtra("pos", 0));
					sendAdapter.notifyDataSetChanged();
					Intent myintent = new Intent("jimome.action.myselfrefresh");
					context.sendBroadcast(myintent);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		}
	};

	/**
	 * 初始化滑动控件
	 * 
	 * @author admin
	 * 
	 */
	private void IntiViewPager() {
		LayoutInflater mInflater = context.getLayoutInflater();
		listViews = new ArrayList<View>();
		listViews.add(mInflater.inflate(R.layout.layout_gift_list, null));
		listViews.add(mInflater.inflate(R.layout.layout_gift_list, null));

		mPager.setAdapter(new MyPagerAdapter(listViews));
		if (name.equals("1"))
			mPager.setCurrentItem(0);
		else {
			mPager.setCurrentItem(1);
		}
		View mylist = listViews.get(0);
		pullScrollView_get = (PullScrollView) mylist
				.findViewById(R.id.pullscroll_left_list);
		View mygrid = listViews.get(1);
		pullScrollView_send = (PullScrollView) mygrid
				.findViewById(R.id.pullscroll_left_list);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		initView();
	}// 适配器

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
				name = "1";
				tv_mine_midget.setTextColor(context.getResources().getColor(
						R.color.black));
				tv_mine_midsend.setTextColor(context.getResources().getColor(
						R.color.darkgray));

				if (base_get != null && base_get.getGifts() != null
						&& base_get.getGifts().size() > 0) {
				} else {
					getHttpData();
				}
			} else {
				name = "2";
				tv_mine_midget.setTextColor(context.getResources().getColor(
						R.color.darkgray));
				tv_mine_midsend.setTextColor(context.getResources().getColor(
						R.color.black));
				if (base_send != null && base_send.getGifts() != null
						&& base_send.getGifts().size() > 0) {
				} else {
					getHttpData();
				}
			}
			resetPullScroll();
		}
	}

	private void initView() {
		// contentLayout = (LinearLayout) context.getLayoutInflater()
		// .inflate(R.layout.layout_meitao, null);
		// lv_fource = (ListView) contentLayout.findViewById(R.id.lv_meitao);
		// pullScrollView.addBodyView(contentLayout);
		// pullScrollView.setOnPullListener(this);

		contentLayout_get = (LinearLayout) context.getLayoutInflater().inflate(
				R.layout.layout_recommend, null);
		lv_get = (ListView) contentLayout_get.findViewById(R.id.lv_recommend);
		pullScrollView_get.addBodyView(contentLayout_get);
		pullScrollView_get.setOnPullListener(this);
		lv_get.setOnItemClickListener(this);
		contentLayout_send = (LinearLayout) context.getLayoutInflater()
				.inflate(R.layout.layout_recommend, null);
		lv_send = (ListView) contentLayout_send.findViewById(R.id.lv_recommend);
		pullScrollView_send.addBodyView(contentLayout_send);
		pullScrollView_send.setOnPullListener(this);
		resetPullScroll();
		if (NetworkUtils.checkNet(context)) {
			getHttpData();
		} else {
			Toast.makeText(context, StringUtils.getResourse(R.string.str_net_register),
					Toast.LENGTH_SHORT).show();
		}

	}

	@OnClick({ R.id.layout_back, R.id.tv_mine_midfouce, R.id.tv_mine_midfans })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.layout_back:
			context.finish();
			break;
		case R.id.tv_mine_midfouce:
			if (!name.equals("1")) {
				name = "1";
				mPager.setCurrentItem(0);
			}
			break;
		case R.id.tv_mine_midfans:
			if (!name.equals("2")) {
				name = "2";
				mPager.setCurrentItem(1);
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub

	}

	private void getHttpData() {
		RequestParams params = new RequestParams();
		String key = "";
		try {
			params.addQueryStringParameter("user_id", Conf.userID);// Conf.gender
			if (name.equals("1")) {
				key = "photo/gift/list/more";
				params.addQueryStringParameter("page", getpage + "");
				if (getpage == 1)
					waitDialog();
			} else {
				key = "me/gift/out";
				params.addQueryStringParameter("page", fanspage + "");
				if (fanspage == 1)
					waitDialog();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		// Log.e("上传参数", name + "==" + params.toString());
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key, 0,
				new CacheRequestCallBack() {

					@Override
					public void onData(String obj) {
						// TODO Auto-generated method stub

						try {
							// Log.e("结果", obj);
							if (name.equals("1")) {
								base_get = new Gson().fromJson(obj,
										BaseJson.class);
								if (base_get.getStatus().equals("200")) {
									if (getpage == 1) {
										getAdapter = new GiftListAdapter(
												context, base_get.getGifts(),
												base_user);
										lv_get.setAdapter(getAdapter);
									} else {
										getAdapter.insertData(base_get
												.getGifts());

										getAdapter.notifyDataSetChanged();
									}
								} else {
									if (getpage > 1)
										--getpage;
								}
							} else {
								base_send = new Gson().fromJson(obj,
										BaseJson.class);
								if (base_send.getStatus().equals("200")) {
									if (fanspage == 1) {
										sendAdapter = new MyGiftListAdapter(
												context, base_send.getGifts(),
												name);
										lv_send.setAdapter(sendAdapter);
									} else {
										sendAdapter.insertData(base_send
												.getGifts());
										sendAdapter.notifyDataSetChanged();
									}
								} else {
									if (fanspage > 1)
										--fanspage;
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							// Log.e("dsdsd", e.toString());
						} finally {
							resetPullScroll();
							if (mDialog != null) {
								mDialog.dismiss();
							}
						}

					}

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub
						resetPullScroll();
						if (name.equals("1")) {
							if (getpage > 1)
								--getpage;
						} else {
							if (fanspage > 1)
								--fanspage;
						}
						if (mDialog != null) {
							mDialog.dismiss();
						}
						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
					}

				});
	}

	private void resetPullScroll() {

		pullScrollView_send.setheaderViewReset();
		pullScrollView_send.setfooterViewReset();
		pullScrollView_get.setheaderViewReset();
		pullScrollView_get.setfooterViewReset();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		// TODO Auto-generated method stub
		try {
			Intent perintent = new Intent(context, FragmentToActivity.class);
			perintent.putExtra("who", "personal");
			perintent.putExtra("user_id", getAdapter.allDate().get(pos)
					.getUser_id());
			perintent.putExtra("distance", "");
			context.startActivity(perintent);
		} catch (Exception e) {
			// TODO: handle exception
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
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mDialog != null)
			mDialog.dismiss();
		if (mRefreshBroadcastReceiver != null)
			context.unregisterReceiver(mRefreshBroadcastReceiver);
	}

	public void onScroll(int scrollY) {

	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

		if (NetworkUtils.checkNet(context)) {
			if (name.equals("1")) {
				getpage = 1;
			} else {
				fanspage = 1;
			}
			getHttpData();
		} else {
			Toast.makeText(context, StringUtils.getResourse(R.string.str_net_register),
					Toast.LENGTH_SHORT).show();
			if (name.equals("1"))
				pullScrollView_get.setheaderViewReset();
			else
				pullScrollView_send.setheaderViewReset();
		}

	}

	@Override
	public void loadMore() {

		// TODO Auto-generated method stub
		if (NetworkUtils.checkNet(context)) {
			if (name.equals("1")) {
				if (base_get != null && base_get.getGifts() != null
						&& base_get.getGifts().size() > 0) {
					++getpage;
				} else if (base_get == null)
					getpage = 1;
				else
					++getpage;
			} else {
				if (base_send != null && base_send.getGifts() != null
						&& base_send.getGifts().size() > 0) {
					++fanspage;
				} else if (base_send == null)
					fanspage = 1;
				else
					++fanspage;
			}
			getHttpData();
		} else {
			Toast.makeText(context, StringUtils.getResourse(R.string.str_net_register),
					Toast.LENGTH_SHORT).show();
			if (name.equals("1"))
				pullScrollView_get.setfooterViewReset();
			else
				pullScrollView_send.setfooterViewReset();
		}
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		layout_mine_gift.setVisibility(View.VISIBLE);
		tv_mine_midget.setText(R.string.str_mygift_gettitle);
		tv_mine_midsend.setText(R.string.str_mygift_sendtitle);
		if (name.equals("1")) {
			tv_mine_midget.setTextColor(context.getResources().getColor(
					R.color.black));
			tv_mine_midsend.setTextColor(context.getResources().getColor(
					R.color.darkgray));
		} else {
			tv_mine_midget.setTextColor(context.getResources().getColor(
					R.color.darkgray));
			tv_mine_midsend.setTextColor(context.getResources().getColor(
					R.color.black));
		}
		layout_back.setVisibility(View.VISIBLE);
		IntiViewPager();
	}
}

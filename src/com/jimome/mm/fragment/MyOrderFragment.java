package com.jimome.mm.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.adapter.MyOrderAdapter;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.NetworkUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.jimome.mm.view.MyListView;
import com.jimome.mm.view.PullScrollView;
import com.jimome.mm.view.PullScrollView.OnPullListener;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.unjiaoyou.mm.R;

/**
 * 进行中/已揭晓/获得商品页面
 * 
 * @author admin
 * 
 */

/**
 * @author Administrator
 * 
 */
public class MyOrderFragment extends BaseFragment implements OnPullListener,
		OnItemClickListener {

	@ViewInject(R.id.tv_onebuy_title)
	private TextView tv_onebuy_title;
	@ViewInject(R.id.layout_onebuy_back)
	private LinearLayout layout_onebuy_back;
	@ViewInject(R.id.tv_onebuy_oning)
	private TextView tv_onebuy_oning;
	@ViewInject(R.id.tv_onebuy_end)
	private TextView tv_onebuy_end;
	@ViewInject(R.id.tv_onebuy_award)
	private TextView tv_onebuy_award;
	private MyListView lv_on, lv_end, lv_award;
	private int onpage = 1, endpage = 1, awardpage = 1;
	private MyOrderAdapter onAdapter, endAdapter, awardAdapter;
	private BaseJson base_on, base_end, base_award;// 进行中/结束/获奖
	private String name;

	@ViewInject(R.id.vPager_gift)
	private ViewPager mPager;
	private List<View> listViews;
	// private int offset = 1;
	private PullScrollView pullScrollView_on, pullScrollView_end,
			pullScrollView_award;
	private LinearLayout contentLayout_on, contentLayout_end,
			contentLayout_award;

	private Activity context;

	@Override
	protected View initView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		return arg0.inflate(R.layout.fragment_onebuy_myorder, arg1, false);
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		tv_onebuy_title.setText(R.string.str_onebuy_myorder);
		name = "1";
		// initView();
		layout_onebuy_back.setVisibility(View.VISIBLE);
		// gv_fource.setOnItemClickListener(this);
		// gv_fource.setOnScrollListener(new PauseOnScrollListener(ImageLoader
		// .getInstance(), false, true));
		IntiViewPager();
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

		// waitDialog();
		// getHttpData();
	}

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
		listViews.add(mInflater.inflate(R.layout.layout_gift_list, null));
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		View mylist_on = listViews.get(0);
		pullScrollView_on = (PullScrollView) mylist_on
				.findViewById(R.id.pullscroll_left_list);
		View mylist_end = listViews.get(1);
		pullScrollView_end = (PullScrollView) mylist_end
				.findViewById(R.id.pullscroll_left_list);
		View mylist_award = listViews.get(2);
		pullScrollView_award = (PullScrollView) mylist_award
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
			if (arg0 == 0) {
				name = "1";
				setTitleBG(tv_onebuy_oning, 1);
				setTitleBG(tv_onebuy_end, 0);
				setTitleBG(tv_onebuy_award, 0);
				if (onAdapter!=null&&onAdapter.getCount()>0) {
				} else {
					getHttpData();
				}
			} else if (arg0 == 1) {
				name = "2";
				setTitleBG(tv_onebuy_oning, 0);
				setTitleBG(tv_onebuy_end, 1);
				setTitleBG(tv_onebuy_award, 0);
				if (endAdapter!=null&&endAdapter.getCount()>0) {
				} else {
					getHttpData();
				}
			} else {
				name = "3";
				setTitleBG(tv_onebuy_oning, 0);
				setTitleBG(tv_onebuy_end, 0);
				setTitleBG(tv_onebuy_award, 1);
				if (awardAdapter!=null&&awardAdapter.getCount()>0) {
				} else {
					getHttpData();
				}
			}
			resetPullScroll();
		}
	}

	/**
	 * 设置背景颜色
	 * 
	 * @param view
	 * @param check
	 *            1表示被选中 0表示没被选中
	 */
	private void setTitleBG(TextView view, int check) {
		if (check == 1) {
			view.setTextColor(context.getResources().getColor(R.color.white));
			BasicUtils.setBackground(context, view, R.drawable.shape_orangebg);
		} else {
			view.setTextColor(context.getResources().getColor(
					R.color.darkorange));
			BasicUtils.setBackground(context, view, R.color.transwhite);
		}
	}

	private void initView() {
		// contentLayout = (LinearLayout) getActivity().getLayoutInflater()
		// .inflate(R.layout.layout_meitao, null);
		// gv_fource = (GridView) contentLayout.findViewById(R.id.gv_meitao);
		// pullScrollView.addBodyView(contentLayout);
		// pullScrollView.setOnPullListener(this);

		contentLayout_on = (LinearLayout) context.getLayoutInflater().inflate(
				R.layout.layout_recommend, null);
		lv_on = (MyListView) contentLayout_on.findViewById(R.id.lv_recommend);
		pullScrollView_on.addBodyView(contentLayout_on);
		pullScrollView_on.setOnPullListener(this);
		lv_on.setOnItemClickListener(this);
		contentLayout_end = (LinearLayout) context.getLayoutInflater().inflate(
				R.layout.layout_recommend, null);
		lv_end = (MyListView) contentLayout_end.findViewById(R.id.lv_recommend);
		pullScrollView_end.addBodyView(contentLayout_end);
		pullScrollView_end.setOnPullListener(this);
		lv_end.setOnItemClickListener(this);
		contentLayout_award = (LinearLayout) context.getLayoutInflater()
				.inflate(R.layout.layout_recommend, null);
		lv_award = (MyListView) contentLayout_award
				.findViewById(R.id.lv_recommend);
		pullScrollView_award.addBodyView(contentLayout_award);
		pullScrollView_award.setOnPullListener(this);
		lv_award.setOnItemClickListener(this);
		resetPullScroll();
		// if (SystemTool.checkNet(context)) {
		waitDialog();
		getHttpData();
		// } else {
		// ViewInject.toast(getString(R.string.str_net_register));
		// }

	}

	@OnClick({ R.id.layout_onebuy_back, R.id.tv_onebuy_oning,
			R.id.tv_onebuy_end, R.id.tv_onebuy_award })
	protected void widgetClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_onebuy_back:
			context.finish();
			break;
		case R.id.tv_onebuy_oning:
			if (!name.equals("1")) {
				name = "1";
				mPager.setCurrentItem(0);
			}
			break;
		case R.id.tv_onebuy_end:
			if (!name.equals("2")) {
				name = "2";
				mPager.setCurrentItem(1);
			}
			break;
		case R.id.tv_onebuy_award:
			if (!name.equals("3")) {
				name = "3";
				mPager.setCurrentItem(2);
			}
			break;
		default:
			break;
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

	private void getHttpData() {
		RequestParams params = new RequestParams();
		String key = "buy/order/list";
		int cache_time = 0;
		try {
			if (name.equals("1")) {
				params.addQueryStringParameter("status", "0");
				params.addQueryStringParameter("page", onpage + "");
			} else if (name.equals("2")) {
				params.addQueryStringParameter("status", "1");
				params.addQueryStringParameter("page", endpage + "");
			} else {
				params.addQueryStringParameter("status", "2");
				params.addQueryStringParameter("page", awardpage + "");
			}
			params.addQueryStringParameter("user_id", Conf.userID);
		} catch (Exception e) {
			// TODO: handle exception
		}
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key, cache_time,
				new CacheRequestCallBack() {

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub
						Log.e("result", result + "===" + e.getExceptionCode());
						if (mDialog != null) {
							mDialog.dismiss();
						}
						resetPullScroll();
						if (name.equals("1")) {
							if (onpage > 1)
								--onpage;
						} else if (name.equals("2")) {
							if (endpage > 1)
								--endpage;
						} else if (awardpage > 1)
							--awardpage;
						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
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
						Log.e("json", json);
						if (json.equals("")) {
							BasicUtils
									.toast(StringUtils.getResourse(R.string.str_net_register));
							return;
						}

						try {
							if (name.equals("1")) {
								base_on = new Gson().fromJson(json,
										BaseJson.class);
								if (base_on.getStatus().equals("200")) {
									if (onpage == 1) {
										onAdapter = new MyOrderAdapter(context,
												base_on.getOrders(), 1);
										lv_on.setAdapter(onAdapter);
									} else {
										onAdapter.insertData(base_on
												.getOrders());
										onAdapter.notifyDataSetChanged();
									}
								} else {
									if (onpage > 1)
										--onpage;
								}
							} else if (name.equals("2")) {
								base_end = new Gson().fromJson(json,
										BaseJson.class);
								if (base_end.getStatus().equals("200")) {
									if (endpage == 1) {
										endAdapter = new MyOrderAdapter(
												context, base_end.getOrders(), 2);
										lv_end.setAdapter(endAdapter);
									} else {
										endAdapter.insertData(base_end
												.getOrders());
										endAdapter.notifyDataSetChanged();
									}
								} else {
									if (endpage > 1)
										--endpage;
								}
							} else {
								base_award = new Gson().fromJson(json,
										BaseJson.class);
								if (base_award.getStatus().equals("200")) {
									if (awardpage == 1) {
										awardAdapter = new MyOrderAdapter(
												context, base_award.getOrders(), 3);
										lv_award.setAdapter(awardAdapter);
									} else {
										awardAdapter.insertData(base_award
												.getOrders());
										awardAdapter.notifyDataSetChanged();
									}
								} else {
									if (awardpage > 1)
										--awardpage;
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							resetPullScroll();
						}
					}
				});
	}

	private void resetPullScroll() {

		pullScrollView_end.setheaderViewReset();
		pullScrollView_end.setfooterViewReset();
		pullScrollView_on.setheaderViewReset();
		pullScrollView_on.setfooterViewReset();
		pullScrollView_award.setheaderViewReset();
		pullScrollView_award.setfooterViewReset();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		// TODO Auto-generated method stub
		try {
			Intent orderintent = new Intent(context, FragmentToActivity.class);
			orderintent.putExtra("who", "orderdetail");
			if (name.equals("1")) {
				orderintent.putExtra("order", onAdapter.getAllData().get(pos));
			} else if (name.equals("2")) {
				orderintent.putExtra("order", endAdapter.getAllData().get(pos));
			} else {
				orderintent.putExtra("order", awardAdapter.getAllData()
						.get(pos));
			}
			context.startActivity(orderintent);
		} catch (Exception e) {
			// TODO: handle exception
		}
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
	}

	public void onScroll(int scrollY) {

	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

		if (NetworkUtils.checkNet(context)) {
			if (name.equals("1")) {
				onpage = 1;
			} else if (name.equals("2")) {
				endpage = 1;
			} else {
				awardpage = 1;
			}
			getHttpData();
		} else {
			BasicUtils.toast(StringUtils.getResourse(R.string.str_net_register));
			if (name.equals("1"))
				pullScrollView_on.setheaderViewReset();
			else if (name.equals("2"))
				pullScrollView_end.setheaderViewReset();
			else
				pullScrollView_award.setheaderViewReset();
		}

	}

	@Override
	public void loadMore() {

		// TODO Auto-generated method stub
		if (NetworkUtils.checkNet(context)) {
			if (name.equals("1")) {
				if (base_on != null && base_on.getOrders() != null
						&& base_on.getOrders().size() > 0) {
					++onpage;
				} else if (base_on == null)
					onpage = 1;
				else
					++onpage;
			} else if (name.equals("2")) {
				if (base_end != null && base_end.getOrders() != null
						&& base_end.getOrders().size() > 0) {
					++endpage;
				} else if (base_end == null)
					endpage = 1;
				else
					++endpage;
			} else {
				if (base_award != null && base_award.getOrders() != null
						&& base_award.getOrders().size() > 0) {
					++awardpage;
				} else if (base_award == null)
					awardpage = 1;
				else
					++awardpage;
			}
			getHttpData();
		} else {
			BasicUtils.toast(StringUtils.getResourse(R.string.str_net_register));
			if (name.equals("1"))
				pullScrollView_on.setfooterViewReset();
			else if (name.equals("2"))
				pullScrollView_end.setfooterViewReset();
			else
				pullScrollView_award.setfooterViewReset();
		}
	}
}

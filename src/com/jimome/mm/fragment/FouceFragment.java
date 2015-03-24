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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.adapter.FouceAdapter;
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
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.unjiaoyou.mm.R;

/**
 * 关注页面/粉丝
 * 
 * @author admin
 * 
 */

public class FouceFragment extends BaseFragment implements OnPullListener,
		OnItemClickListener {

	@ViewInject(R.id.layout_mine_fans)
	private LinearLayout layout_mine_fans;
	@ViewInject(R.id.tv_mine_midfouce)
	private TextView tv_mine_midfouce;
	@ViewInject(R.id.tv_mine_midfans)
	private TextView tv_mine_midfans;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	private GridView gv_fouce, gv_fans;
	private int foucepage = 1, fanspage = 1;
	private FouceAdapter fouceAdapter, fansAdapter;
	private BaseJson base_fouce, base_fans;// 关注,粉丝列表
	private String name = "";

	@ViewInject(R.id.vPager_gift)
	private ViewPager mPager;
	private List<View> listViews;
	// private int offset = 1;
	private PullScrollView pullScrollView_fouce, pullScrollView_fans;
	private LinearLayout contentLayout_fouce, contentLayout_fans;

	private Activity context;

	@Override
	protected View initView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		return arg0.inflate(R.layout.activity_gift_list, arg1, false);// 与之前的(附近/缘分)共用同一个布局
	}

	public FouceFragment(String name) {
		this.name = name;
	}

	public FouceFragment() {
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

		mPager.setAdapter(new MyPagerAdapter(listViews));
		if (name.equals("1"))
			mPager.setCurrentItem(0);
		else {
			mPager.setCurrentItem(1);
		}
		View mylist = listViews.get(0);
		pullScrollView_fouce = (PullScrollView) mylist
				.findViewById(R.id.pullscroll_left_list);
		View mygrid = listViews.get(1);
		pullScrollView_fans = (PullScrollView) mygrid
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
				tv_mine_midfouce.setTextColor(context.getResources().getColor(
						R.color.black));
				tv_mine_midfans.setTextColor(context.getResources().getColor(
						R.color.darkgray));

				if (base_fouce != null && base_fouce.getFocuseds() != null
						&& base_fouce.getFocuseds().size() > 0) {
				} else {
					getHttpData();
				}
			} else {
				name = "2";
				tv_mine_midfouce.setTextColor(context.getResources().getColor(
						R.color.darkgray));
				tv_mine_midfans.setTextColor(context.getResources().getColor(
						R.color.black));
				if (base_fans != null && base_fans.getFocusers() != null
						&& base_fans.getFocusers().size() > 0) {
				} else {
					getHttpData();
				}
			}
			resetPullScroll();
		}
	}

	private void initView() {
		// contentLayout = (LinearLayout) getActivity().getLayoutInflater()
		// .inflate(R.layout.layout_meitao, null);
		// gv_fource = (GridView) contentLayout.findViewById(R.id.gv_meitao);
		// pullScrollView.addBodyView(contentLayout);
		// pullScrollView.setOnPullListener(this);

		contentLayout_fouce = (LinearLayout) context.getLayoutInflater()
				.inflate(R.layout.layout_meitao, null);
		gv_fouce = (GridView) contentLayout_fouce.findViewById(R.id.gv_meitao);
		pullScrollView_fouce.addBodyView(contentLayout_fouce);
		pullScrollView_fouce.setOnPullListener(this);
		gv_fouce.setOnItemClickListener(this);
		contentLayout_fans = (LinearLayout) context.getLayoutInflater()
				.inflate(R.layout.layout_meitao, null);
		gv_fans = (GridView) contentLayout_fans.findViewById(R.id.gv_meitao);
		pullScrollView_fans.addBodyView(contentLayout_fans);
		pullScrollView_fans.setOnPullListener(this);
		resetPullScroll();
		// if (SystemTool.checkNet(context)) {
		waitDialog();
		 getHttpData();
		// } else {
		// ViewInject.toast(getString(R.string.str_net_register));
		// }

	}

	@OnClick({ R.id.layout_back, R.id.tv_mine_midfouce, R.id.tv_mine_midfans })
	protected void widgetClick(View v) {
		// TODO Auto-generated method stub
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
		String key = "";
		int cache_time = 0;
		try {
			if (name.equals("1")) {
				key = "me/focused";
				params.addQueryStringParameter("page", foucepage + "");
			} else {
				key = "me/focuser";
				params.addQueryStringParameter("page", fanspage + "");
			}
			params.addQueryStringParameter("cur_user", Conf.userID);
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
						if (mDialog != null) {
							mDialog.dismiss();
						}
						resetPullScroll();
						if (name.equals("1")) {
							if (foucepage > 1)
								--foucepage;
						} else {
							if (fanspage > 1)
								--fanspage;
						}
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
						if (json.equals("")) {
							return;
						}

						try {
							if (name.equals("1")) {
								base_fouce = new Gson().fromJson(json,
										BaseJson.class);
								if (base_fouce.getStatus().equals("200")) {
									if (foucepage == 1) {
										fouceAdapter = new FouceAdapter(
												context, base_fouce
														.getFocuseds());

										gv_fouce.setAdapter(fouceAdapter);
									} else {
										fouceAdapter.insertData(base_fouce
												.getFocuseds());

										fouceAdapter.notifyDataSetChanged();
									}
								} else {
									if (foucepage > 1)
										--foucepage;
								}
							} else {
								base_fans = new Gson().fromJson(json,
										BaseJson.class);
								if (base_fans.getStatus().equals("200")) {
									if (fanspage == 1) {
										fansAdapter = new FouceAdapter(context,
												base_fans.getFocusers());
										gv_fans.setAdapter(fansAdapter);
									} else {
										fansAdapter.insertData(base_fans
												.getFocusers());
										fansAdapter.notifyDataSetChanged();
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
						}
					}
				});
		// kjh.get(url, params, new HttpCallBack() {
		//
		// @Override
		// public void onSuccess(Object obj) {
		// // TODO Auto-generated method stub
		//
		// try {
		// Log.e("关注结果", obj.toString());
		// if (name.equals("1")) {
		// base_fouce = new Gson().fromJson(obj.toString(),
		// BaseJson.class);
		// if (base_fouce.getStatus().equals("200")) {
		// if (foucepage == 1) {
		// fouceAdapter = new FouceAdapter(context,
		// base_fouce.getFocuseds());
		//
		// gv_fouce.setAdapter(fouceAdapter);
		// } else {
		// fouceAdapter.insertData(base_fouce
		// .getFocuseds());
		//
		// fouceAdapter.notifyDataSetChanged();
		// }
		// } else {
		// if (foucepage > 1)
		// --foucepage;
		// }
		// } else {
		// base_fans = new Gson().fromJson(obj.toString(),
		// BaseJson.class);
		// if (base_fans.getStatus().equals("200")) {
		// if (fanspage == 1) {
		// fansAdapter = new FouceAdapter(context,
		// base_fans.getFocusers());
		// gv_fans.setAdapter(fansAdapter);
		// } else {
		// fansAdapter.insertData(base_fans.getFocusers());
		// fansAdapter.notifyDataSetChanged();
		// }
		// } else {
		// if (fanspage > 1)
		// --fanspage;
		// }
		// }
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// // Log.e("dsdsd", e.toString());
		// } finally {
		// resetPullScroll();
		// if (mDialog != null) {
		// mDialog.dismiss();
		// }
		// }
		//
		// }
		//
		// @Override
		// public void onLoading(long count, long current) {
		// // TODO Auto-generated method stub
		// // Log.e("上传Loading", count + "\n===" + current);
		// }
		//
		// @Override
		// public void onFailure(Throwable t, int errorNo, String strMsg) {
		// // TODO Auto-generated method stub
		// resetPullScroll();
		// if (name.equals("1")) {
		// if (foucepage > 1)
		// --foucepage;
		// } else {
		// if (fanspage > 1)
		// --fanspage;
		// }
		// if (mDialog != null) {
		// mDialog.dismiss();
		// }
		// ExitManager.getScreenManager().intentLogin(context,
		// StringUtils.httpRsponse(t.toString()));
		// }
		//
		// });
	}

	private void resetPullScroll() {

		pullScrollView_fans.setheaderViewReset();
		pullScrollView_fans.setfooterViewReset();
		pullScrollView_fouce.setheaderViewReset();
		pullScrollView_fouce.setfooterViewReset();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		// TODO Auto-generated method stub
		try {
			Intent perintent = new Intent(context, FragmentToActivity.class);
			perintent.putExtra("who", "personal");
			perintent.putExtra("user_id", fouceAdapter.allDate().get(pos)
					.getUser_id());
			perintent.putExtra("distance", "");
			context.startActivity(perintent);
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
				foucepage = 1;
			} else {
				fanspage = 1;
			}
			getHttpData();
		} else {
			BasicUtils.toast(StringUtils.getResourse(R.string.str_net_register));
			if (name.equals("1"))
				pullScrollView_fouce.setheaderViewReset();
			else
				pullScrollView_fans.setheaderViewReset();
		}

	}

	@Override
	public void loadMore() {

		// TODO Auto-generated method stub
		if (NetworkUtils.checkNet(context)) {
			if (name.equals("1")) {
				if (base_fouce != null && base_fouce.getFocuseds() != null
						&& base_fouce.getFocuseds().size() > 0) {
					++foucepage;
				} else if (base_fouce == null)
					foucepage = 1;
				else
					++foucepage;
			} else {
				if (base_fans != null && base_fans.getFocusers() != null
						&& base_fans.getFocusers().size() > 0) {
					++fanspage;
				} else if (base_fans == null)
					fanspage = 1;
				else
					++fanspage;
			}
			getHttpData();
		} else {
			BasicUtils.toast(StringUtils.getResourse(R.string.str_net_register));
			if (name.equals("1"))
				pullScrollView_fouce.setfooterViewReset();
			else
				pullScrollView_fans.setfooterViewReset();
		}
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		layout_mine_fans.setVisibility(View.VISIBLE);
		tv_mine_midfouce.setText(R.string.str_personal_guanzhu);
		tv_mine_midfans.setText(R.string.str_my_fans);
		if (name.equals("1")) {
			tv_mine_midfouce.setTextColor(context.getResources().getColor(
					R.color.black));
			tv_mine_midfans.setTextColor(context.getResources().getColor(
					R.color.darkgray));
		} else {
			tv_mine_midfouce.setTextColor(context.getResources().getColor(
					R.color.darkgray));
			tv_mine_midfans.setTextColor(context.getResources().getColor(
					R.color.black));
		}
		// initView();
		layout_back.setVisibility(View.VISIBLE);
		// gv_fource.setOnItemClickListener(this);
		// gv_fource.setOnScrollListener(new PauseOnScrollListener(ImageLoader
		// .getInstance(), false, true));
		IntiViewPager();
	}
}

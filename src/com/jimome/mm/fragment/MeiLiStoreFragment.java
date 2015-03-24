package com.jimome.mm.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.activity.ExchangeActivity;
import com.jimome.mm.adapter.MeiLiStoreAdapter;
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
 * 魅力商城页面
 * 
 * @author admin
 * 
 */
public class MeiLiStoreFragment extends BaseFragment implements OnPullListener, OnItemClickListener {

	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	@ViewInject(R.id.layout_text_chong)
	private LinearLayout layout_text_chong;
	@ViewInject(R.id.tv_text_chong)
	private TextView tv_text_chong;
	@ViewInject(R.id.pushscroll_gv)
	private PullScrollView pullScrollView;
	private LinearLayout contentLayout;
	private ListView lv_meili;
	@ViewInject(R.id.img_loading_error)
	private ImageView img_loading_error;
	private int page = 1;
	// private Dialog mDialog;
	private MeiLiStoreAdapter meiAdapter;
	private BaseJson baseJson;
	private int last_posx, last_posy;
	private Activity context;

	@Override
	protected View initView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		return arg0.inflate(R.layout.fragment_fate, arg1, false);
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

	private void initView() {

		contentLayout = (LinearLayout) context.getLayoutInflater().inflate(
				R.layout.layout_recommend, null);
		lv_meili = (ListView) contentLayout.findViewById(R.id.lv_recommend);
		pullScrollView.addBodyView(contentLayout);
		pullScrollView.setheaderViewGone();
		pullScrollView.setfooterViewGone();
		pullScrollView.setOnPullListener(this);
		// lv_meili.setOnItemClickListener(this);
	}

	private void waitDialog() {
		mDialog.show();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		last_posx = pullScrollView.getScrollX();
		last_posy = pullScrollView.getScrollY();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	private void getHttpData() {
		int cache_time = 0;
		RequestParams params = new RequestParams();
		try {
			params.addQueryStringParameter("cur_user", Conf.userID);
		} catch (Exception e) {
			// TODO: handle exception
		}

		String key = "find/store";
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
							baseJson = new Gson()
									.fromJson(json, BaseJson.class);
							if (baseJson.getStatus().equals("200")) {
								Conf.userCharm = baseJson.getUser_charm();
								tv_text_chong.setText(Conf.userCharm
										+ StringUtils.getResourse(R.string.str_meili));
								if (meiAdapter == null) {
									meiAdapter = new MeiLiStoreAdapter(context,
											baseJson.getItems());
									lv_meili.setAdapter(meiAdapter);

								} else {
									meiAdapter.insertData(baseJson.getItems());
									meiAdapter.notifyDataSetChanged();
								}
							} else {
								img_loading_error.setVisibility(View.VISIBLE);

								pullScrollView.setVisibility(View.GONE);
								if (page > 1)
									--page;
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
		// baseJson = new Gson().fromJson(obj.toString(),
		// BaseJson.class);
		// if (baseJson.getStatus().equals("200")) {
		// Conf.userCharm = baseJson.getUser_charm();
		// tv_text_chong.setText(Conf.userCharm
		// + getString(R.string.str_meili));
		// if (meiAdapter == null) {
		// meiAdapter = new MeiLiStoreAdapter(context,
		// baseJson.getItems());
		// lv_meili.setAdapter(meiAdapter);
		//
		// } else {
		// meiAdapter.insertData(baseJson.getItems());
		// meiAdapter.notifyDataSetChanged();
		// }
		// } else {
		// img_loading_error.setVisibility(View.VISIBLE);
		//
		// pullScrollView.setVisibility(View.GONE);
		// if (page > 1)
		// --page;
		// }
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
		// if (page > 1)
		// --page;
		// if (mDialog != null) {
		// mDialog.dismiss();
		// }
		// ExitManager.getScreenManager().intentLogin(context,
		// StringUtils.httpRsponse(t.toString()));
		// }
		// });
	}

	@OnClick(R.id.layout_back)
	protected void onClickView(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_back:
			context.finish();
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
		try {
			if (ExchangeActivity.exchanged) {
				tv_text_chong.setText(Conf.userCharm
						+ StringUtils.getResourse(R.string.str_meili));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mDialog != null)
			mDialog.dismiss();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(context);
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadMore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		tv_title.setText(StringUtils.getResourse(R.string.str_find_store));
		layout_back.setVisibility(View.VISIBLE);
		layout_text_chong.setVisibility(View.VISIBLE);
		initView();
		if (!NetworkUtils.checkNet(context)) {
			img_loading_error.setVisibility(View.VISIBLE);
		} else {
			img_loading_error.setVisibility(View.GONE);
			if (baseJson == null || baseJson.getItems() == null
					|| baseJson.getItems().size() < 1) {
				waitDialog();
				getHttpData();
			} else if (meiAdapter == null) {
				meiAdapter = new MeiLiStoreAdapter(context, baseJson.getItems());
				lv_meili.setAdapter(meiAdapter);
			} else {
				lv_meili.setAdapter(meiAdapter);
				pullScrollView.post(new Runnable() {

					@Override
					public void run() {

						pullScrollView.scrollTo(last_posx, last_posy);
					}
				});
			}
			pullScrollView.setheaderViewGone();
			pullScrollView.setfooterViewGone();
			pullScrollView.setVisibility(View.VISIBLE);
		}
	}

}

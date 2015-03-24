package com.jimome.mm.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.activity.MyShowUploadActivity;
import com.jimome.mm.activity.SelectDetailActivity;
import com.jimome.mm.adapter.MeiTaoAdapter;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.jimome.mm.view.MyGirdView;
import com.jimome.mm.view.PullScrollView;
import com.jimome.mm.view.PullScrollView.OnPullListener;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.unjiaoyou.mm.R;

/**
 * 美套页面/身材秀
 * 
 * @author admin
 * 
 */

public class MeiTaoFragment extends BaseFragment implements OnPullListener,
		OnItemClickListener {

	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.btn_jiahei)
	private Button btn_jiahei;
	@ViewInject(R.id.pushscroll_gv)
	private PullScrollView pullScrollView;
	@ViewInject(R.id.layout_text_leftchong)
	private LinearLayout layout_text_leftchong;
	private LinearLayout contentLayout;
	private MyGirdView gv_meitao;
	private int page = 1;
	// private Dialog mDialog;
	private int last_posx, last_posy;
	private MeiTaoAdapter meiAdapter;
	private BaseJson newPer;// 美套图列表
	private Activity context;

	@Override
	protected View initView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		return arg0.inflate(R.layout.fragment_fate, arg1, false);// 与之前的(附近/缘分)共用同一个布局
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

	}

	private void initView() {

		contentLayout = (LinearLayout) context.getLayoutInflater().inflate(
				R.layout.layout_meitao, null);
		gv_meitao = (MyGirdView) contentLayout.findViewById(R.id.gv_meitao);
		pullScrollView.addBodyView(contentLayout);
		pullScrollView.setOnPullListener(this);
	}

	@OnClick({ R.id.layout_text_leftchong, R.id.btn_jiahei })
	protected void onClickView(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_text_leftchong:
			Intent intent = new Intent(context, FragmentToActivity.class);
			intent.putExtra("who", "vip");
			context.startActivity(intent);
			break;
		case R.id.btn_jiahei:
			Intent showIntent = new Intent(context, MyShowUploadActivity.class);
			showIntent.putExtra("type", 1);
			context.startActivity(showIntent);
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
		int cache_time = 0;
		String key = "photo";
		RequestParams params = new RequestParams();
		try {
			params.addQueryStringParameter("cur_user", Conf.userID);
			params.addQueryStringParameter("gender", Conf.gender);// Conf.gender
			params.addQueryStringParameter("page", String.valueOf(page));
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
						pullScrollView.setheaderViewReset();
						pullScrollView.setfooterViewReset();
						pullScrollView.setVisibility(View.VISIBLE);
						if (page > 1)
							--page;
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
							newPer = new Gson().fromJson(json, BaseJson.class);
							if (newPer.getStatus().equals("200")) {
								if (meiAdapter == null) {
									meiAdapter = new MeiTaoAdapter(context,
											newPer.getPhotos());
									gv_meitao.setAdapter(meiAdapter);
								} else {
									meiAdapter.insertData(newPer.getPhotos());
									meiAdapter.notifyDataSetChanged();
								}
							} else {
								if (page > 1)
									--page;
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							// Log.e("dsdsd", e.toString());
						} finally {
							pullScrollView.setheaderViewReset();
							pullScrollView.setfooterViewReset();
							pullScrollView.setVisibility(View.VISIBLE);
						}
					}
				});
		// kjh.get(Conf.URL + "photo", params, new HttpCallBack() {
		//
		// @Override
		// public void onSuccess(Object obj) {
		// // TODO Auto-generated method stub
		//
		// try {
		// newPer = new Gson().fromJson(obj.toString(), BaseJson.class);
		// if (newPer.getStatus().equals("200")) {
		// if (meiAdapter == null) {
		// meiAdapter = new MeiTaoAdapter(context,
		// newPer.getPhotos());
		// gv_meitao.setAdapter(meiAdapter);
		// } else {
		// meiAdapter.insertData(newPer.getPhotos());
		// meiAdapter.notifyDataSetChanged();
		// }
		// } else {
		// if (page > 1)
		// --page;
		// }
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// // Log.e("dsdsd", e.toString());
		// } finally {
		// pullScrollView.setheaderViewReset();
		// pullScrollView.setfooterViewReset();
		// pullScrollView.setVisibility(View.VISIBLE);
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
		// pullScrollView.setheaderViewReset();
		// pullScrollView.setfooterViewReset();
		// pullScrollView.setVisibility(View.VISIBLE);
		// if (page > 1)
		// --page;
		// if (mDialog != null) {
		// mDialog.dismiss();
		// }
		// ExitManager.getScreenManager().intentLogin(context,
		// StringUtils.httpRsponse(t.toString()));
		// }
		//
		// });
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		// TODO Auto-generated method stub
		try {
			Intent intent = new Intent(context, SelectDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("photo", meiAdapter.allDate().get(pos));
			intent.putExtras(bundle);
			intent.putExtra("detail", "photo");
			intent.putExtra("type", 2);
			// Log.e("photo_id", meiAdapter.allDate().get(pos).getPhoto_id()
			// + "\nuser_id" + meiAdapter.allDate().get(pos).getUser_id());
			context.startActivity(intent);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		if (meiAdapter != null)
			newPer.setPhotos(meiAdapter.allDate());
		last_posx = pullScrollView.getScrollX();
		last_posy = pullScrollView.getScrollY();
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

		// if (SystemTool.checkNet(context)) {
		meiAdapter = null;
		page = 1;
		getHttpData();
		// } else {
		// ViewInject.toast(getString(R.string.str_net_register));
		// pullScrollView.setheaderViewReset();
		// }

	}

	@Override
	public void loadMore() {

		// TODO Auto-generated method stub
		// if (SystemTool.checkNet(context)) {
		if (newPer != null && newPer.getPhotos() != null
				&& newPer.getPhotos().size() > 0) {
			++page;
		} else {
			if (newPer == null) {
				page = 1;
			} else
				++page;
		}
		getHttpData();
		// } else {
		// ViewInject.toast(getString(R.string.str_net_register));
		//
		// pullScrollView.setfooterViewReset();
		// }
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		tv_title.setText(R.string.str_limitshow);
		initView();
		layout_text_leftchong.setVisibility(View.VISIBLE);
		btn_jiahei.setVisibility(View.VISIBLE);
		btn_jiahei.setBackgroundResource(R.drawable.btn_write_selector);
		gv_meitao.setOnItemClickListener(this);
		gv_meitao.setOnScrollListener(new PauseOnScrollListener(ImageLoader
				.getInstance(), false, true));
		if (newPer == null) {
			waitDialog();
			getHttpData();

		} else if (meiAdapter == null) {
			pullScrollView.setheaderViewReset();
			pullScrollView.setfooterViewReset();
			pullScrollView.setVisibility(View.VISIBLE);
			if (newPer.getPhotos() != null && newPer.getPhotos().size() > 0) {
				meiAdapter = new MeiTaoAdapter(context, newPer.getPhotos());
				gv_meitao.setAdapter(meiAdapter);
			}
		} else {
			pullScrollView.setheaderViewReset();
			pullScrollView.setfooterViewReset();
			pullScrollView.setVisibility(View.VISIBLE);
			gv_meitao.setAdapter(meiAdapter);
			pullScrollView.post(new Runnable() {

				@Override
				public void run() {

					pullScrollView.scrollTo(last_posx, last_posy);
				}
			});
		}

	}
}

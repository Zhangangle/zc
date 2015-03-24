package com.jimome.mm.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.adapter.FateAdapter;
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
import com.unjiaoyou.mm.R;

/**
 * 附近页面
 * 
 * @author admin
 * 
 */

public class FateFragment extends BaseFragment implements OnPullListener, OnItemClickListener {

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
	private MyGirdView gv_fate;
	private BaseJson baseJson;
	private FateAdapter fateAdapter;
	private int page = 1;
//	private Dialog mDialog;
	private int last_posx, last_posy;
	private String[] select = { "男", "女" };
	private String gender;
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
	private void waitDialog() {
		mDialog.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	

	}

	private void initView() {
		contentLayout = (LinearLayout) context.getLayoutInflater()
				.inflate(R.layout.layout_fate, null);
		gv_fate = (MyGirdView) contentLayout.findViewById(R.id.gv_fate);
		pullScrollView.addBodyView(contentLayout);
		pullScrollView.setOnPullListener(this);
	}

	@OnClick ({R.id.layout_back,R.id.layout_text_chong})
	protected void onClickView(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_back:
			context.finish();
			break;
		case R.id.layout_text_chong:
			new AlertDialog.Builder(context)
					.setItems(select, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							tv_text_chong.setText(getResources().getString(
									R.string.str_fate_select)
									+ ":" + select[which]);
							dialog.dismiss();
							page = 1;
							waitDialog();
							if (which == 0)
								gender = "1";
							else
								gender = "2";
							getHttpData();
						}
					}).create().show();
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
		RequestParams params = new RequestParams();
		try {
			params.addQueryStringParameter("cur_user", Conf.userID);
			params.addQueryStringParameter("gender", gender);
			params.addQueryStringParameter("page", String.valueOf(page));
		} catch (Exception e) {
			// TODO: handle exception
		}
		String key ="find/near";
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key, cache_time, new CacheRequestCallBack() {
			
			@Override
			public void onFail(HttpException e, String result, String json) {
				// TODO Auto-generated method stub
				if (mDialog != null) {
					mDialog.dismiss();
				}
//				pullScrollView.setheaderViewReset();
//			pullScrollView.setfooterViewReset();
//				pullScrollView.setVisibility(View.VISIBLE);
				if (page > 1)
					--page;ExitManager.getScreenManager().intentLogin(context,
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
//					BasicUtils.toast(StringUtils.getResourse(R.string.str_net_register));
					return;
				}
				
				try {
					baseJson = new Gson().fromJson(json,
							BaseJson.class);
					if (baseJson.getStatus().equals("200")) {
						if (page == 1) {
							fateAdapter = new FateAdapter(context,
									baseJson.getUsers());
							gv_fate.setAdapter(fateAdapter);

						} else {
							fateAdapter.insertData(baseJson.getUsers());
							fateAdapter.notifyDataSetChanged();
						}
					} else {
						if (page > 1)
							--page;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					pullScrollView.setheaderViewReset();
					pullScrollView.setfooterViewReset();
					pullScrollView.setVisibility(View.VISIBLE);
				}
			}
		});
		
//		kjh.get(url, params, new HttpCallBack() {
//
//			@Override
//			public void onSuccess(Object obj) {
//				// TODO Auto-generated method stub
//				try {
//					Log.e("附近", obj.toString());
//					baseJson = new Gson().fromJson(obj.toString(),
//							BaseJson.class);
//					if (baseJson.getStatus().equals("200")) {
//						if (page == 1) {
//							fateAdapter = new FateAdapter(context,
//									baseJson.getUsers());
//							gv_fate.setAdapter(fateAdapter);
//
//						} else {
//							fateAdapter.insertData(baseJson.getUsers());
//							fateAdapter.notifyDataSetChanged();
//						}
//					} else {
//						if (page > 1)
//							--page;
//					}
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					pullScrollView.setheaderViewReset();
//					pullScrollView.setfooterViewReset();
//					pullScrollView.setVisibility(View.VISIBLE);
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
//				pullScrollView.setheaderViewReset();
//				pullScrollView.setfooterViewReset();
//				pullScrollView.setVisibility(View.VISIBLE);
//				if (page > 1)
//					--page;
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
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		// TODO Auto-generated method stub
		try {
			if (fateAdapter != null) {
				Intent intent = new Intent(new Intent(context,
						FragmentToActivity.class));
				intent.putExtra("who", "personal");
				intent.putExtra("user_id", fateAdapter.allDate().get(pos)
						.getUser_id());
				intent.putExtra("distance", fateAdapter.allDate().get(pos)
						.getDistance());
				context.startActivity(intent);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}


	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
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

//		if (SystemTool.checkNet(context)) {
			page = 1;
			getHttpData();
//		} else {
//			ViewInject.toast(getString(R.string.str_net_register));
//			pullScrollView.setheaderViewReset();
//		}

	}

	@Override
	public void loadMore() {

		// TODO Auto-generated method stub
//		if (SystemTool.checkNet(context)) {
			try {
				if (baseJson == null || baseJson.getUsers() == null
						|| baseJson.getUsers().size() < 1) {
					page = 1;
				} else
					++page;
				getHttpData();
			} catch (Exception e) {
				// TODO: handle exception
			}
//		} else {
//			ViewInject.toast(getString(R.string.str_net_register));
//
//			pullScrollView.setfooterViewReset();
//		}

	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		tv_title.setText(R.string.str_nearby);
		layout_back.setVisibility(View.VISIBLE);
		if (Conf.gender.equals("1")) {
			gender = "2";
			tv_text_chong.setText(getResources().getString(
					R.string.str_fate_select)
					+ ":" + select[1]);
		} else {
			gender = "1";
			tv_text_chong.setText(getResources().getString(
					R.string.str_fate_select)
					+ ":" + select[0]);
		}
		layout_text_chong.setVisibility(View.VISIBLE);
		initView();
		gv_fate.setOnItemClickListener(this);
		if (baseJson == null || baseJson.getUsers() == null
				|| baseJson.getUsers().size() < 1) {
			waitDialog();
			getHttpData();
		} else if (fateAdapter == null) {
			pullScrollView.setheaderViewReset();
			pullScrollView.setfooterViewReset();
			pullScrollView.setVisibility(View.VISIBLE);
			fateAdapter = new FateAdapter(context, baseJson.getUsers());
			gv_fate.setAdapter(fateAdapter);
		} else {
			pullScrollView.setheaderViewReset();
			pullScrollView.setfooterViewReset();
			pullScrollView.setVisibility(View.VISIBLE);
			gv_fate.setAdapter(fateAdapter);
			pullScrollView.post(new Runnable() {

				@Override
				public void run() {

					pullScrollView.scrollTo(last_posx, last_posy);
				}
			});
		}
	}
}

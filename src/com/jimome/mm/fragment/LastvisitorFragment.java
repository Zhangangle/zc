package com.jimome.mm.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.adapter.LastvisitorAdapter;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.jimome.mm.view.MyListView;
import com.jimome.mm.view.PullScrollView;
import com.jimome.mm.view.PullScrollView.OnPullListener;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.unjiaoyou.mm.R;

/**
 * 最近访客页面
 * 
 * @author admin
 * 
 */
public class LastvisitorFragment extends BaseFragment implements OnPullListener, OnItemClickListener {

	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.pushscroll_visitor)
	private PullScrollView pushscroll_visitor;
	@ViewInject(R.id.img_visitor_ad)
	private ImageView img_visitor_ad;
	private MyListView visitorView;
	private String user_id = "";
	private int page = 1;
	private BaseJson baseJson;
//	private Dialog mDialog;
	private LastvisitorAdapter adapter;

	private Activity context;
	@Override
	protected View initView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		return arg0.inflate(R.layout.fragment_lastvisitor, arg1, false);
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

	public LastvisitorFragment() {

	}

	public LastvisitorFragment(String user_id) {
		this.user_id = user_id;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		

	}

	private void setAD() {
		try {
			if (!Conf.user_VIP.equals("1"))
				ImageLoadUtils.imageLoader.displayImage(Conf.VISITOR_AD,
						img_visitor_ad, ImageLoadUtils.options,
						new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String arg0, View arg1) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onLoadingFailed(String arg0, View arg1,
									FailReason arg2) {
								// TODO Auto-generated method stub

								img_visitor_ad.setVisibility(View.GONE);
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								// TODO Auto-generated method stub
								if (loadedImage != null) {
									int height = (Conf.width * loadedImage
											.getHeight())
											/ loadedImage.getWidth();
									img_visitor_ad
											.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
													android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
													height));
								}
							}

							@Override
							public void onLoadingCancelled(String arg0,
									View arg1) {
								// TODO Auto-generated method stub

							}
						});
			else
				img_visitor_ad.setVisibility(View.GONE);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@OnClick({R.id.layout_back,R.id.img_visitor_ad})
	protected void onClickView(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_back:
			context.finish();
			break;
		case R.id.img_visitor_ad:
			StatService.onEvent(context, "caller-ad", "eventLabel", 1);
			Intent intent = new Intent(context, FragmentToActivity.class);
			intent.putExtra("who", "vip");
			context.startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		// TODO Auto-generated method stub
		try {
			if (user_id.equals(Conf.userID)) {
				if (!adapter.allDate().get(pos).getUser_id()
						.equals(Conf.userID)) {
					if (Conf.user_VIP.equals("1") || pos < 7) {
						Intent intent = new Intent(new Intent(context,
								FragmentToActivity.class));
						intent.putExtra("who", "personal");
						intent.putExtra("user_id", adapter.allDate().get(pos)
								.getUser_id());
						intent.putExtra("distance", "");
						context.startActivity(intent);
					}
				}
			}
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
	public void refresh() {
		// TODO Auto-generated method stub

//		if (SystemTool.checkNet(context)) {
			page = 1;
			getHttpData();
//		} else {
//			ViewInject.toast(getString(R.string.str_net_register));
//			pushscroll_visitor.setheaderViewReset();
//		}

	}

	private void getHttpData() {
		int cache_time = 0;
		RequestParams params = new RequestParams();
		try {
			params.addQueryStringParameter("user_id", user_id);
			params.addQueryStringParameter("page", String.valueOf(page));
		} catch (Exception e) {
			// TODO: handle exception
		}
		String key = "user/visit/more";
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
					baseJson = new Gson().fromJson(json,
							BaseJson.class);
					if (baseJson.getStatus().equals("200")) {
						if (page == 1) {
							adapter = new LastvisitorAdapter(context,
									baseJson.getUsers());
							visitorView.setAdapter(adapter);
							Conf.user_VIP = baseJson.getIs_vip();
							setAD();
						} else {
							adapter.insertData(baseJson.getUsers());
							adapter.notifyDataSetChanged();
						}
					} else {
						if (page > 1)
							--page;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					pushscroll_visitor.setheaderViewReset();
					pushscroll_visitor.setfooterViewReset();
					pushscroll_visitor.setVisibility(View.VISIBLE);
				}
			}
		});
//		kjh.get(url, params, new HttpCallBack() {
//
//			@Override
//			public void onSuccess(Object obj) {
//				// TODO Auto-generated method stub
//
//				try {
//					baseJson = new Gson().fromJson(obj.toString(),
//							BaseJson.class);
//					if (baseJson.getStatus().equals("200")) {
//						if (page == 1) {
//							adapter = new LastvisitorAdapter(context,
//									baseJson.getUsers());
//							visitorView.setAdapter(adapter);
//							Conf.user_VIP = baseJson.getIs_vip();
//							setAD();
//						} else {
//							adapter.insertData(baseJson.getUsers());
//							adapter.notifyDataSetChanged();
//						}
//					} else {
//						if (page > 1)
//							--page;
//					}
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					pushscroll_visitor.setheaderViewReset();
//					pushscroll_visitor.setfooterViewReset();
//					pushscroll_visitor.setVisibility(View.VISIBLE);
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
//				pushscroll_visitor.setheaderViewReset();
//				pushscroll_visitor.setfooterViewReset();
//				pushscroll_visitor.setVisibility(View.VISIBLE);
//				if (page > 1)
//					--page;
//				else
//					setAD();
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
	public void loadMore() {

		// TODO Auto-generated method stub
//		if (SystemTool.checkNet(context)) {
			if (Conf.user_VIP.equals("1")) {
				if (baseJson != null && baseJson.getUsers() != null
						&& baseJson.getUsers().size() > 0) {
					++page;
				} else {
					if (baseJson == null) {
						page = 1;
					} else
						++page;
				}
				getHttpData();
			} else {
				pushscroll_visitor.setfooterViewReset();
			}
//		} else {
//			ViewInject.toast(getString(R.string.str_net_register));
//			pushscroll_visitor.setfooterViewReset();
//		}

	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		layout_back.setVisibility(View.VISIBLE);
		tv_title.setText(R.string.str_lastvisitor);
		pushscroll_visitor.setheaderViewReset();
		pushscroll_visitor.setfooterViewReset();

		visitorView = new MyListView(context);
		visitorView.setDivider(null);
		visitorView.setCacheColorHint(0);
		visitorView.setSelector(android.R.color.transparent);
		visitorView.setOnItemClickListener(this);
		pushscroll_visitor.addBodyView(visitorView);
		pushscroll_visitor.setOnPullListener(this);
		waitDialog();
		getHttpData();
	}

}

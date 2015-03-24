package com.jimome.mm.fragment;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.adapter.OneBuyAdapter;
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
 * 所有商品(一元云购)页面
 * 
 * @author admin
 * 
 */
public class OneBuyFragment extends BaseFragment implements OnPullListener,
		OnItemClickListener {

	@ViewInject(R.id.tv_onebuy_title)
	private TextView tv_onebuy_title;
	@ViewInject(R.id.layout_onebuy_back)
	private LinearLayout layout_onebuy_back;
	@ViewInject(R.id.layout_onebuy_time)
	private LinearLayout layout_onebuy_time;
	@ViewInject(R.id.tv_onbuy_time)
	private TextView tv_onbuy_time;
	@ViewInject(R.id.img_loading_error)
	private ImageView img_loading_error;
	@ViewInject(R.id.pushscroll_onebuy_lv)
	private PullScrollView pullScrollView;

	@ViewInject(R.id.img_onbuy_tip)
	private ImageView img_onbuy_tip;
	private LinearLayout contentLayout;
	private ListView lv_onebuy;
	// private Dialog mDialog;
	private BaseJson baseJson;
	private OneBuyAdapter oneAdapter;
	private Activity context;
	private int page = 1;

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_onebuy, container, false);
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		tv_onebuy_title.setText(StringUtils.getResourse(R.string.str_onebuy_title));
		layout_onebuy_back.setVisibility(View.VISIBLE);
		layout_onebuy_time.setVisibility(View.VISIBLE);
		tv_onbuy_time.setText(StringUtils.getResourse(R.string.str_onebuy_rule));
		img_onbuy_tip.setVisibility(View.GONE);
		initView();
//		if (!NetworkUtils.checkNet(context)) {
//			img_loading_error.setVisibility(View.VISIBLE);
//		} else {
			img_loading_error.setVisibility(View.GONE);
			waitDialog();
			getHttpData();
			pullScrollView.setVisibility(View.VISIBLE);
//		}
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
		lv_onebuy = (ListView) contentLayout.findViewById(R.id.lv_recommend);
		pullScrollView.addBodyView(contentLayout);
		pullScrollView.setheaderViewReset();
		pullScrollView.setfooterViewReset();
		pullScrollView.setOnPullListener(this);
		lv_onebuy.setOnItemClickListener(this);
	}

	private void waitDialog() {
		// mDialog = BasicUtils.showDialog(context, R.style.BasicDialog);
		// mDialog.setContentView(R.layout.dialog_wait);
		// mDialog.setCanceledOnTouchOutside(false);
		//
		// Animation anim = AnimationUtils.loadAnimation(context,
		// R.anim.dialog_prog);
		// LinearInterpolator lir = new LinearInterpolator();
		// anim.setInterpolator(lir);
		// mDialog.findViewById(R.id.img_dialog_progress).startAnimation(anim);
		// mDialog.setOnKeyListener(new OnKeyListener() {
		// @Override
		// public boolean onKey(DialogInterface dialog, int keyCode,
		// KeyEvent event) {
		// if (keyCode == KeyEvent.KEYCODE_BACK
		// && event.getAction() == KeyEvent.ACTION_DOWN) {
		// if (!context.isFinishing())
		// mDialog.dismiss();
		//
		// return false;
		// }
		// return false;
		// }
		// });
		mDialog.show();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	private void getHttpData() {
		RequestParams params = new RequestParams();
		String key = "buy/goods";
		try {
			params.addQueryStringParameter("user_id", Conf.userID);
			params.addQueryStringParameter("page", page + "");
		} catch (Exception e) {
			// TODO: handle exception
		}

		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key+page, 300,
				new CacheRequestCallBack() {

					@Override
					public void onData(String json) {
						// TODO Auto-generated method stub
						try {
							baseJson = new Gson()
									.fromJson(json, BaseJson.class);
							if (baseJson.getStatus().equals("200")) {
								if (page == 1) {
									oneAdapter = new OneBuyAdapter(context,
											baseJson.getGoods());
									lv_onebuy.setAdapter(oneAdapter);
								} else {
									oneAdapter.insertData(baseJson.getGoods());
									oneAdapter.notifyDataSetChanged();
								}

							} else {
								if (oneAdapter == null)
									img_loading_error
											.setVisibility(View.VISIBLE);
								if (page != 1)
									--page;
								if (baseJson.getStatus().equals("105")) {
									Toast.makeText(context,
											StringUtils.getResourse(R.string.str_date_null),
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											context,
											StringUtils.getResourse(R.string.str_net_register),
											Toast.LENGTH_SHORT).show();
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (mDialog != null) {
								mDialog.dismiss();
							}

							pullScrollView.setheaderViewReset();
							pullScrollView.setfooterViewReset();
						}
					}

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub
						if (mDialog != null) {
							mDialog.dismiss();
						}
						if (page != 1)
							--page;
						pullScrollView.setheaderViewReset();
						pullScrollView.setfooterViewReset();
						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
					}
				});
	}

	@OnClick({ R.id.layout_onebuy_back, R.id.tv_onbuy_time,
			R.id.layout_onebuy_time })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.layout_onebuy_back:
			context.finish();
			break;
		case R.id.tv_onbuy_time:
		case R.id.layout_onebuy_time:
			Intent detailIntent = new Intent(context, FragmentToActivity.class);
			detailIntent.putExtra("who", "onebuy_picdetail");
			detailIntent.putExtra("goodnum", "rule");
			context.startActivity(detailIntent);
			break;
		default:
			break;
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
		if (!NetworkUtils.checkNet(context)) {
			pullScrollView.setheaderViewReset();
		} else {
			page = 1;
			getHttpData();
		}
	}

	@Override
	public void loadMore() {
		// TODO Auto-generated method stub
		if (!NetworkUtils.checkNet(context)) {
			pullScrollView.setfooterViewReset();
		} else {
			if (page != 1)
				page++;
			getHttpData();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (oneAdapter != null && oneAdapter.getCount() > 0) {
			Intent intent = new Intent(context, FragmentToActivity.class);
			intent.putExtra("who", "onebuy_detail");
			intent.putExtra("goodnum", oneAdapter.allDate().get(arg2).getId());
			context.startActivity(intent);
		}
	}
}

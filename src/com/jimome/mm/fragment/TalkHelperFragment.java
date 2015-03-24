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

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.adapter.TalkHelperAdapter;
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
 * 搭讪助手页面
 * 
 * @author admin
 * 
 */
public class TalkHelperFragment extends BaseFragment implements OnPullListener, OnItemClickListener {

	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	@ViewInject(R.id.pushscroll_gv)
	private PullScrollView pullScrollView;
	private LinearLayout contentLayout;
	private ListView lv_helper;
	@ViewInject(R.id.img_loading_error)
	private ImageView img_loading_error;
//	private Dialog mDialog;
	private TalkHelperAdapter helperAdapter;
	private BaseJson baseJson;
	private Activity context;

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_fate, container, false);
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
		lv_helper = (ListView) contentLayout.findViewById(R.id.lv_recommend);
		pullScrollView.addBodyView(contentLayout);
		lv_helper.setOnItemClickListener(this);
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
		String key = "msg/greet/text";
		try {
			params.addQueryStringParameter("cur_user", Conf.userID);
		} catch (Exception e) {
			// TODO: handle exception
		}

		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key, 0,
				new CacheRequestCallBack() {

					@Override
					public void onData(String json) {
						// TODO Auto-generated method stub
						try {
							baseJson = new Gson()
									.fromJson(json, BaseJson.class);
							if (baseJson.getStatus().equals("200")) {
								helperAdapter = new TalkHelperAdapter(context,
										baseJson.getTexts());
								lv_helper.setAdapter(helperAdapter);

							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
						} finally {
							if (mDialog != null) {
								mDialog.dismiss();
							}
						}
					}

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub
						if (mDialog != null) {
							mDialog.dismiss();
						}
						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
					}
				});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		// TODO Auto-generated method stub
		try {
			Intent intent = new Intent();
			intent.setAction("jimome.action.sendtext");
			intent.putExtra("text", baseJson.getTexts()[pos]);
			context.sendBroadcast(intent);
			context.finish();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@OnClick({ R.id.layout_back })
	public void onClickView(View v) {
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
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(context);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mDialog != null)
			mDialog.dismiss();
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
	protected void initWidget() {
		// TODO Auto-generated method stub
		tv_title.setText(StringUtils.getResourse(R.string.str_talk_helper));
		layout_back.setVisibility(View.VISIBLE);
		initView();
		if (!NetworkUtils.checkNet(context)) {
			img_loading_error.setVisibility(View.VISIBLE);
		} else {
			img_loading_error.setVisibility(View.GONE);
			waitDialog();
			getHttpData();
		}
		pullScrollView.setheaderViewGone();
		pullScrollView.setfooterViewGone();
		pullScrollView.setOnPullListener(this);
		pullScrollView.setVisibility(View.VISIBLE);
	}

}

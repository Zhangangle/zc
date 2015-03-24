package com.jimome.mm.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
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
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.activity.MyShowUploadActivity;
import com.jimome.mm.adapter.TaskAdapter;
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
 * 个人任务页面
 * 
 * @author admin
 * 
 */
public class TaskFragment extends BaseFragment implements OnPullListener,
		OnItemClickListener {

	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	@ViewInject(R.id.img_loading_error)
	private ImageView img_loading_error;

	@ViewInject(R.id.pushscroll_gv)
	private PullScrollView pullScrollView;
	private LinearLayout contentLayout;
	private ListView lv_task;
	// private Dialog mDialog;
	private BaseJson baseJson;
	private TaskAdapter taskAdapter;
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
		lv_task = (ListView) contentLayout.findViewById(R.id.lv_recommend);
		pullScrollView.addBodyView(contentLayout);
		pullScrollView.setheaderViewReset();
		pullScrollView.setfooterViewGone();
		pullScrollView.setOnPullListener(this);
		lv_task.setOnItemClickListener(this);
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

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("jimome.action.taskrefresh");
		context.registerReceiver(mRefreshBroadcastReceiver, intentFilter);
	}

	// 设置更新启动的广播
	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("jimome.action.taskrefresh")) {
				try {
					taskAdapter.removeDate(intent.getIntExtra("pos", 0));
					taskAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		}
	};

	private void getHttpData() {
		RequestParams params = new RequestParams();
		String key = "msg/task";
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
							Log.e("===", json);
							baseJson = new Gson()
									.fromJson(json, BaseJson.class);
							if (baseJson.getStatus().equals("200")) {
								taskAdapter = new TaskAdapter(context, baseJson
										.getTasks());
								lv_task.setAdapter(taskAdapter);
							} else {
								if (taskAdapter == null)
									img_loading_error
											.setVisibility(View.VISIBLE);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (mDialog != null) {
								mDialog.dismiss();
							}
							pullScrollView.setheaderViewReset();
						}
					}

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub
						if (mDialog != null) {
							mDialog.dismiss();
						}
						pullScrollView.setheaderViewReset();
						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
					}
				});
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
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mDialog != null)
			mDialog.dismiss();
		if (mRefreshBroadcastReceiver != null)
			context.unregisterReceiver(mRefreshBroadcastReceiver);
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
			getHttpData();
		}
	}

	@Override
	public void loadMore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (baseJson != null && baseJson.getTasks() != null
				&& baseJson.getTasks().get(arg2).getGo_to() != null) {
			if (baseJson.getTasks().get(arg2).getGo_to().equals("1")) {
				Intent shenIntent = new Intent(context,
						MyShowUploadActivity.class);
				shenIntent.putExtra("type", 2);
				context.startActivity(shenIntent);
			} else if (baseJson.getTasks().get(arg2).getGo_to().equals("2")) {
				Intent shenIntent = new Intent(context,
						MyShowUploadActivity.class);
				shenIntent.putExtra("type", 3);
				context.startActivity(shenIntent);
			} else if (baseJson.getTasks().get(arg2).getGo_to().equals("3")) {
				Intent intent = new Intent(context, FragmentToActivity.class);
				intent.putExtra("who", "myself");
				context.startActivity(intent);
			}
		}
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		tv_title.setText(StringUtils.getResourse(R.string.str_task));
		layout_back.setVisibility(View.VISIBLE);
		initView();
		if (!NetworkUtils.checkNet(context)) {
			img_loading_error.setVisibility(View.VISIBLE);
		} else {
			img_loading_error.setVisibility(View.GONE);
			waitDialog();
			getHttpData();
			pullScrollView.setVisibility(View.VISIBLE);
		}
	}
}

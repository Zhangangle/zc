package com.jimome.mm.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.jimome.mm.adapter.OneBuySelectTimeAdapter;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.unjiaoyou.mm.R;

/**
 * 选择期次页面
 * 
 * @author admin
 * 
 */
public class OneBuySelectTimeFragment extends BaseFragment implements
		OnItemClickListener {

	@ViewInject(R.id.tv_onebuy_title)
	private TextView tv_onebuy_title;
	@ViewInject(R.id.layout_onebuy_back)
	private LinearLayout layout_onebuy_back;
	@ViewInject(R.id.gv_select)
	private GridView gv_select;
	// private Dialog mDialog;
	private BaseJson baseJson;
	private Activity context;
	private String goodnum;
	private OneBuySelectTimeAdapter oneAdatper;

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_onebuy_selecttime, container,
				false);
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		tv_onebuy_title.setText(StringUtils.getResourse(R.string.str_onebuy_selecttitle));
		layout_onebuy_back.setVisibility(View.VISIBLE);

		// List<BaseJson> base = new ArrayList<BaseJson>();
		// for (int i = 0; i < 10; i++) {
		// baseJson = new BaseJson();
		// baseJson.setText(i + "");
		// base.add(baseJson);
		// }
		gv_select.setOnItemClickListener(this);
		waitDialog();
		getHttpData();
		// oneAdatper = new OneBuySelectTimeAdapter(context, base);
		// gv_select.setAdapter(oneAdatper);
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

	public OneBuySelectTimeFragment() {
	}

	public OneBuySelectTimeFragment(String goodnum) {
		this.goodnum = goodnum;
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
		String key = "buy/good/periods";
		try {
			params.addQueryStringParameter("good_id", goodnum);
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
								oneAdatper = new OneBuySelectTimeAdapter(
										context, baseJson.getPeriods());
								gv_select.setAdapter(oneAdatper);
							} else {

							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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

	@OnClick({ R.id.layout_onebuy_back })
	public void onClickView(View v) {
		;
		switch (v.getId()) {
		case R.id.layout_onebuy_back:
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		try {
			if (arg2 != 0) {
				Intent intent = new Intent(context, FragmentToActivity.class);
				intent.putExtra("who", "onebuy_period");
				intent.putExtra("goodnum", goodnum);
				intent.putExtra("period", oneAdatper.allData().get(arg2)
						.getPeriod());
				context.startActivity(intent);
			} else {
				context.finish();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}

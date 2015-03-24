package com.jimome.mm.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
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
 * 图文详细(一元云购)/规则页面
 * 
 * @author admin
 * 
 */
public class OneBuyPicDetailFragment extends BaseFragment {

	@ViewInject(R.id.tv_onebuy_title)
	private TextView tv_onebuy_title;
	@ViewInject(R.id.layout_onebuy_back)
	private LinearLayout layout_onebuy_back;
	@ViewInject(R.id.tv_onebuy_picmessage)
	private TextView tv_onebuy_picmessage;
	// private Dialog mDialog;
	private BaseJson baseJson;
	private Activity context;
	private String goodnum;

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_onebuy_picdetail, container,
				false);
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		if (goodnum.equals("rule")) {
			tv_onebuy_title.setText(StringUtils.getResourse(R.string.str_onebuy_rule));
			tv_onebuy_picmessage
					.setText(StringUtils.getResourse(R.string.str_onebuy_rule_msg));
		} else {
			tv_onebuy_title.setText(StringUtils.getResourse(R.string.str_onebuy_detail));
			waitDialog();
			getHttpData();
		}
		layout_onebuy_back.setVisibility(View.VISIBLE);
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

	public OneBuyPicDetailFragment() {
	}

	public OneBuyPicDetailFragment(String goodnum) {
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
		String key = "buy/good/description";
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
								tv_onebuy_picmessage.setText(baseJson.getText());
							} else if (baseJson.getStatus().equals("105")) {
								Toast.makeText(context,
										StringUtils.getResourse(R.string.str_date_null),
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(context,
										StringUtils.getResourse(R.string.str_net_register),
										Toast.LENGTH_SHORT).show();
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

}

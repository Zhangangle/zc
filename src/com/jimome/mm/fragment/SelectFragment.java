package com.jimome.mm.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.activity.MyShowUploadActivity;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.unjiaoyou.mm.R;

/**
 * 女神秀页面
 * 
 * @author admin
 * 
 */

public class SelectFragment extends BaseFragment {

	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;// 返回
	@ViewInject(R.id.btn_jiahei)
	private Button btn_jiahei;
	@ViewInject(R.id.img_fate_icon)
	private ImageView img_fate_icon;
	@ViewInject(R.id.img_pic)
	private ImageView img_pic;
	@ViewInject(R.id.img_fate_dislike)
	private ImageView img_fate_dislike;
	@ViewInject(R.id.img_fate_like)
	private ImageView img_fate_like;
	@ViewInject(R.id.tv_fate_location)
	private TextView tv_fate_location;
	@ViewInject(R.id.tv_fate_name)
	private TextView tv_fate_name;
	@ViewInject(R.id.tv_fate_age)
	private TextView tv_fate_age;
	@ViewInject(R.id.tv_fate_likenum)
	private TextView tv_fate_likenum;
	@ViewInject(R.id.tv_fate_height)
	private TextView tv_fate_height;
	@ViewInject(R.id.tv_fate_picnum)
	private TextView tv_fate_picnum;
	@ViewInject(R.id.relayout_fate)
	private RelativeLayout relayout_fate;
	public static boolean sendMsg = false;
	// private Dialog mDialog;
	private BaseJson person;
	private Activity context;

	@Override
	protected View initView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		return arg0.inflate(R.layout.fragment_select, arg1, false);
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
		layout_back.setVisibility(View.VISIBLE);
		tv_title.setText(R.string.str_my_nvshenxiu);
		btn_jiahei.setVisibility(View.VISIBLE);
		btn_jiahei.setBackgroundResource(R.drawable.btn_write_selector);
		if (person == null) {
			waitDialog();
			getHttpData(1);
		} else
			setView();

	}

	private void setView() {
		try {
			relayout_fate.startAnimation(AnimationUtils.loadAnimation(context,
					R.anim.fate_alpha));
			ImageLoadUtils.imageLoader.displayImage(person.getUrl(),
					img_fate_icon, ImageLoadUtils.options);
			// tv_fate_location.setText(person.getCity());
			tv_fate_location.setText(Conf.city);
			tv_fate_name.setText(person.getNick());
			tv_fate_age.setText(person.getAge() + StringUtils.getResourse(R.string.str_sui));
			tv_fate_likenum.setText(person.getGreeted_nums()
					+ StringUtils.getResourse(R.string.str_fate_likenum));
			if (person.getHeight().trim().equals(""))
				tv_fate_height.setText("");
			else
				tv_fate_height.setText(person.getHeight() + "cm");
			tv_fate_picnum.setText(person.getPhoto_nums()
					+ StringUtils.getResourse(R.string.str_iconuntil));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@OnClick({ R.id.layout_back, R.id.img_fate_icon, R.id.btn_jiahei,
			R.id.img_fate_dislike, R.id.img_fate_like })
	protected void widgetClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_back:// 返回
			context.finish();
			break;
		case R.id.img_fate_icon:
			StatService
					.onEvent(context, "click-nvshenxiu-img", "eventLabel", 1);
			if (person != null) {
				Intent perintent = new Intent(context, FragmentToActivity.class);
				perintent.putExtra("who", "personal");
				perintent.putExtra("user_id", person.getUser_id());
				perintent.putExtra("distance", "");
				context.startActivity(perintent);
			}
			break;
		case R.id.btn_jiahei:
			Intent intent = new Intent(context, MyShowUploadActivity.class);
			intent.putExtra("type", 1);
			context.startActivity(intent);
			break;
		case R.id.img_fate_dislike:

			if (BasicUtils.isFastDoubleClick()) {
				return;
			}
			StatService.onEvent(context, "click-no-dating-button",
					"eventLabel", 1);
			getHttpData(1);
			break;
		case R.id.img_fate_like:
			if (BasicUtils.isFastDoubleClick()) {
				return;
			}
			StatService
					.onEvent(context, "click-dating-button", "eventLabel", 1);
			if (person != null) {
				Intent perintent = new Intent(context, FragmentToActivity.class);
				perintent.putExtra("who", "personal");
				perintent.putExtra("user_id", person.getUser_id());
				perintent.putExtra("distance", "");
				context.startActivity(perintent);
			}
			getHttpData(2);
			break;
		default:
			break;
		}

	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
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

	private void getHttpData(final int type) {
		int cache_time = 0;
		String key = "";
		RequestParams params = new RequestParams();
		try {
			params.addQueryStringParameter("cur_user", Conf.userID);
			params.addQueryStringParameter("gender", Conf.gender);// Conf.gender

			if (type == 1) {
				key = "show";
			} else {
				key = "show/praise";
				params.addQueryStringParameter("user_id", person.getUser_id());
				params.addQueryStringParameter("photo_id", person.getPhoto_id());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		// Log.e("上传参数", params.toString());
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
							person = new Gson().fromJson(json, BaseJson.class);
							if (person.getStatus() != null
									&& person.getStatus().equals("200")) {
								setView();
								if (type == 2) {
									BasicUtils
											.toast(StringUtils.getResourse(R.string.str_intro_call));
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
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
		// person = new Gson().fromJson(obj.toString(), BaseJson.class);
		// if (person.getStatus() != null
		// && person.getStatus().equals("200")) {
		// setView();
		// if (type == 2) {
		// ViewInject
		// .toast(getString(R.string.str_intro_call));
		// }
		// }
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// } finally {
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
		// }
		//
		// @Override
		// public void onFailure(Throwable t, int errorNo, String strMsg) {
		// // TODO Auto-generated method stub
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
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		initView();
	}
}

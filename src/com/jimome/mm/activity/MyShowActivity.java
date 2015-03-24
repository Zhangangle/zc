package com.jimome.mm.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.unjiaoyou.mm.R;
import com.jimome.mm.adapter.MeiTaoAdapter;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.NetworkUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 我的女神秀或美套图或视频秀页面
 * 
 * @author Administrator
 * 
 */
@ContentView(R.layout.fragment_photoalbum)
public class MyShowActivity extends BaseFragmentActivity implements
		OnItemClickListener {
	private Context context;
	@ViewInject(R.id.gv_photo)
	private GridView gv_photo;
	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.btn_jiahei)
	private Button btn_jiahei;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	@ViewInject(R.id.img_loading_error)
	private ImageView img_loading_error;
	@ViewInject(R.id.btn_loading_refresh)
	private Button btn_loading_refresh;
	private MeiTaoAdapter adapter;
	private int type;
	private BaseJson json;
	private String user_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		ViewUtils.inject(this);
		ExitManager.getScreenManager().pushActivity(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("jimome.action.myselfrefresh");
		registerReceiver(refreshBroadcastReceiver, intentFilter);
		Intent intent = getIntent();
		type = intent.getIntExtra("type", 1);
		user_id = intent.getStringExtra("user_id");
		if (type == 1) {
			if (user_id.equals(Conf.userID)) {
				if (!Conf.gender.equals("1")) {
					tv_title.setText(R.string.str_my_nvshenxiu);
				} else {
					tv_title.setText(R.string.str_my_nanshenxiu);
				}
			} else {
				if (intent.getStringExtra("gender") != null
						&& intent.getStringExtra("gender").trim().equals("1")) {
					tv_title.setText(R.string.str_my_nanshenxiu);
				} else {
					tv_title.setText(R.string.str_my_nvshenxiu);
				}
			}
		} else if (type == 2) {
			tv_title.setText(R.string.str_limitshow);
		} else if (type == 3 || type == 4) {
			gv_photo.setNumColumns(2);
			tv_title.setText(R.string.str_my_shipinxiu);
		}
		if (user_id.equals(Conf.userID)) {
			btn_jiahei.setVisibility(View.VISIBLE);
			btn_jiahei.setBackgroundResource(R.drawable.btn_write_selector);
		} else
			btn_jiahei.setVisibility(View.GONE);
		layout_back.setVisibility(View.VISIBLE);
		if (!NetworkUtils.checkNet(context)) {
			// toIntent();

		} else {
			gethttpData();
		}

		gv_photo.setOnItemClickListener(this);
	}

	private void gethttpData() {
		RequestParams params = new RequestParams();
		String key = "";
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		try {
			if (type == 4) {
				key = "find/video";// 发现--视频秀
				params.addQueryStringParameter("cur_user", user_id);
				params.addQueryStringParameter("page", "1");
			} else {
				key = "me/upload/list";
				params.addQueryStringParameter("cur_user", user_id);
				params.addQueryStringParameter("flag", String.valueOf(type));
			}
			CacheRequest.requestGET(context, key, params, key, 0,
					new CacheRequestCallBack() {

						@Override
						public void onFail(HttpException e, String result,
								String json) {
							// TODO Auto-generated method stub
							ExitManager.getScreenManager().intentLogin(context,
									e.getExceptionCode() + "");
						}

						@Override
						public void onData(String arg0) {
							// TODO Auto-generated method stub
							try {

								json = new Gson()
										.fromJson(arg0, BaseJson.class);
								if (json.getStatus().equals("200")) {
									adapter = new MeiTaoAdapter(context, json
											.getUploads(), type, user_id);
									gv_photo.setAdapter(adapter);
								}

					} catch (Exception e) {
						// TODO: handle exception
					}

				}

			});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ExitManager.getScreenManager().pullActivity(this);
		if(refreshBroadcastReceiver != null)
			unregisterReceiver(refreshBroadcastReceiver);
	}

	@OnClick({ R.id.layout_back, R.id.btn_jiahei })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.btn_jiahei:
			Intent intent = new Intent(context, MyShowUploadActivity.class);
			if (type == 4)
				intent.putExtra("type", 3);// 与我的视频秀类型一致
			else
				intent.putExtra("type", type);
			startActivityForResult(intent, 1);
			break;
		case R.id.layout_back:
			onBackPressed();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		// TODO Auto-generated method stub
		if (BasicUtils.isFastDoubleClick()) {
			return;
		}
		if (json == null) {
			return;
		}
		if (pos > 2 && !Conf.user_VIP.equals("1")) {
			return;
		}
		try {
			BaseJson base = new BaseJson();
			base.setUrl(json.getUploads().get(pos).getImg());
			base.setUser_id(user_id);
			if (user_id.equals(Conf.userID))
				base.setNick(Conf.userName);
			else {
				base.setNick(Conf.oppName);
			}
			base.setPhoto_id(json.getUploads().get(pos).getId());
			base.setText(json.getUploads().get(pos).getText());
			if (type == 1) {
				Intent intent = new Intent(context, SelectDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("photo", base);
				intent.putExtras(bundle);
				intent.putExtra("type", 1);
				intent.putExtra("detail", "photo");
				startActivity(intent);
			} else if (type == 2) {
				Intent intent = new Intent(context, SelectDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("photo", base);
				intent.putExtras(bundle);
				intent.putExtra("type", 2);
				intent.putExtra("detail", "photo");
				startActivity(intent);
			} else if (type == 3 || type == 4) {
				if (json.getUploads().get(pos).getVideo_url() != null) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					String type = "video/*";
					Uri uri = Uri.parse(json.getUploads().get(pos)
							.getVideo_url());
					intent.setDataAndType(uri, type);
					startActivity(intent);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);

	}

	private BroadcastReceiver refreshBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			if (action.equals("jimome.action.myselfrefresh")) {
				gethttpData();
			}
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == RESULT_OK) {
			gethttpData();
		}
	}
}

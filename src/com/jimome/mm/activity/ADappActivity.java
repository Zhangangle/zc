package com.jimome.mm.activity;

import java.io.File;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.jimome.mm.adapter.ADappAdapter;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.IOUtils;
import com.jimome.mm.utils.StringUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.unjiaoyou.mm.R;

@ContentView(R.layout.dialog_app)
public class ADappActivity extends BaseFragmentActivity implements
		OnItemClickListener {

	@ViewInject(R.id.tv_ad_vip)
	private TextView tv_ad_vip;
	@ViewInject(R.id.gv_ad_app)
	private GridView gv_ad_app;
	@ViewInject(R.id.img_ad_close)
	private ImageView img_ad_close;
	private Context context;
	private ADappAdapter adAdapter;
	private List<BaseJson> list_app;

	private void waitDialog() {
		mDialog.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		context = ADappActivity.this;
		ViewUtils.inject(this);
		ExitManager.getScreenManager().pushActivity(this);
		tv_ad_vip.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		gv_ad_app.setOnItemClickListener(this);
		if (MainFragmentActivity.list_app != null
				&& MainFragmentActivity.list_app.size() > 0) {
			adAdapter = new ADappAdapter(context, MainFragmentActivity.list_app);
			gv_ad_app.setAdapter(adAdapter);
		}
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("jimo.action.adapp.delete");
		context.registerReceiver(mRefreshBroadcastReceiver, intentFilter);
	}

	// 得到用户地址后启动的广播
	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			try {

				if (action.equals("jimo.action.adapp.delete")) {
					// adAdapter.removeData(intent.getIntExtra("pos", 0));
					// adAdapter.notifyDataSetChanged();
					MainFragmentActivity.list_app.remove(intent.getIntExtra(
							"pos", 0));
					if (MainFragmentActivity.list_app != null
							&& MainFragmentActivity.list_app.size() > 0) {
						adAdapter = new ADappAdapter(context,
								MainFragmentActivity.list_app);
						gv_ad_app.setAdapter(adAdapter);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	};

	// 安装apk
	private void installApk(String filename) {
		try {

			File apkfile = new File(IOUtils.getLXHAPKFolder(filename)
					.toString());
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
					"application/vnd.android.package-archive");
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mDialog != null)
			mDialog.dismiss();
		MainFragmentActivity.list_app = null;
		if (mRefreshBroadcastReceiver != null)
			context.unregisterReceiver(mRefreshBroadcastReceiver);
	}

	@OnClick({ R.id.img_ad_close, R.id.tv_ad_vip })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.img_ad_close:
			finish();
			break;
		case R.id.tv_ad_vip:
			Intent vipIntent = new Intent(context, FragmentToActivity.class);
			vipIntent.putExtra("who", "vip");
			startActivity(vipIntent);
			finish();
			break;
		default:
			break;
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		// TODO Auto-generated method stub
		if (adAdapter != null && adAdapter.getCount() > 0)
			if (BasicUtils.isInstallApk(adAdapter.allData().get(pos).getMsg())) {
				try {
					PackageManager packageManager = context.getPackageManager();
					Intent intent = new Intent();
					intent = packageManager.getLaunchIntentForPackage(adAdapter
							.allData().get(pos).getMsg());
					startActivity(intent);
					// }
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else if (MainFragmentActivity.list_app.get(pos).getAnswer() != null
					&& MainFragmentActivity.list_app.get(pos).getAnswer()
							.equals("OK")) {
				installApk(adAdapter.allData().get(pos).getText());
			} else {
				BasicUtils
						.toast(StringUtils.getResourse(R.string.str_waitdown));
			}
	}
}

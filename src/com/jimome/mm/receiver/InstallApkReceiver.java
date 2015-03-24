package com.jimome.mm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jimome.mm.activity.MainFragmentActivity;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.unjiaoyou.mm.R;

/**
 * Function:软件安装和卸载广播
 * 
 * @author Administrator
 * 
 */
public class InstallApkReceiver extends BroadcastReceiver {
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// install
		this.context = context;

		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
			String packageName = intent.getDataString().substring(8).trim();

			try {
				if (MainFragmentActivity.list_app != null
						&& MainFragmentActivity.list_app.size() > 0) {
					int app_size = MainFragmentActivity.list_app.size();
					for (int i = 0; i < app_size; i++) {
						if (MainFragmentActivity.list_app.get(i).getMsg()
								.trim().equals(packageName)) {
							// getHttpCoin();
							PackageManager packageManager = context
									.getPackageManager();
							Intent intent2 = new Intent();
							intent2 = packageManager
									.getLaunchIntentForPackage(packageName);
							context.startActivity(intent2);
							Intent delIntent = new Intent();
							delIntent.setAction("jimo.action.adapp.delete");
							delIntent.putExtra("pos", i);
							context.sendBroadcast(delIntent);
							break;
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
		}
		// uninstall
		if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
			String packageName = intent.getDataString().substring(8).trim();

		}
	}

	private void getHttpCoin() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("cur_user", Conf.userID);
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		String key = "signin";
		CacheRequest.requestGET(context, key, params, key, 0,
				new CacheRequestCallBack() {

					@Override
					public void onData(String json) {
						// TODO Auto-generated method stub
						try {
							BaseJson newPer = new Gson().fromJson(json,
									BaseJson.class);
							if (newPer.getStatus().equals("200")) {
								MainFragmentActivity.signJson.setIs_signin("1");
								int coin = Integer.valueOf(newPer.getCoin());
								int coins = Integer.valueOf(Conf.Coins) + coin;
								Conf.Coins = String.valueOf(coins);
								Toast.makeText(
										context,
										StringUtils
												.getResourse(R.string.str_signsuccess),
										Toast.LENGTH_SHORT).show();
								// finish();
								// btn_sign.setText(getString(R.string.str_sign_end));
								// tv_signnum.setText(getString(R.string.str_signin_days)
								// + newPer.getDays()
								// + getString(R.string.str_day));
							} else if (newPer.getStatus().equals("158")) {
								Toast.makeText(
										context,
										StringUtils
												.getResourse(R.string.str_signed),
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						} finally {
						}

					}

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub

						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
					}
				});

	}

}

package com.jimome.mm.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.jimome.mm.receiver.UpdateAppReceiver;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.unjiaoyou.mm.R;

public class SettingFragment extends BaseFragment {

	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.img_toggle)
	private ImageView img_toggle;
	@ViewInject(R.id.layout_update)
	private LinearLayout layout_update;
	@ViewInject(R.id.tv_versionstatus)
	private TextView tv_versionstatus;
	@ViewInject(R.id.layout_clearcache)
	private LinearLayout layout_clearcache;
	@ViewInject(R.id.img_new)
	private ImageView img_new;

	private boolean pushed = false;
	private Activity context;

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_setting, container, false);
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@OnClick({ R.id.layout_back, R.id.img_toggle, R.id.layout_update,
			R.id.layout_clearcache })
	public void onClickView(View v) {
		try {
			switch (v.getId()) {
			case R.id.layout_back:
				context.finish();
				break;
			case R.id.img_toggle:
				StatService.onEvent(context, "set-push", "pass", 1);
				if (!pushed) {
					img_toggle.setImageResource(R.drawable.toggle_close);
					pushed = true;
					PreferenceHelper.write(context, "auth", "pushed", "Off");
				} else {
					img_toggle.setImageResource(R.drawable.toggle_open);
					pushed = false;
					PreferenceHelper.write(context, "auth", "pushed", "On");
				}
				break;
			case R.id.layout_update:

				if (UpdateAppReceiver.updateed) {
					UpdateAppReceiver.downhandler.pause();
					UpdateAppReceiver.downhandler.resume();
				}
				break;
			case R.id.layout_clearcache:
				final Dialog dialog = BasicUtils.showDialog(context,
						R.style.BasicDialog);
				dialog.setContentView(R.layout.dialog_rechargevip);
				TextView tip = ((TextView) dialog
						.findViewById(R.id.tv_dialog_msg));
				tip.setText(StringUtils.getResourse(R.string.str_clearcache));
				tip.setGravity(Gravity.CENTER);
				dialog.setCanceledOnTouchOutside(true);

				((Button) dialog.findViewById(R.id.btn_dialog_sure))
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								ImageLoader.getInstance().clearDiskCache();
								ImageLoader.getInstance().clearMemoryCache();
							}
						});

				((Button) dialog.findViewById(R.id.btn_dialog_cancle))
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
				dialog.show();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// private void updateVersion() {
	// // TODO Auto-generated method stub
	// new AsyncTask<Void, Void,List<String>>() {
	//
	// @Override
	// protected List<String> doInBackground(Void... params) {
	// // TODO Auto-generated method stub
	// List<String> strings = new ArrayList<String>();
	// try {
	// URL url = new URL(Conf.UPDATE_SERVERURL);
	// HttpURLConnection conn = (HttpURLConnection) url
	// .openConnection();
	// conn.setConnectTimeout(6 * 1000);
	// conn.setRequestMethod("GET");
	//
	// InputStream inStream = conn.getInputStream();
	// BufferedReader reader = new BufferedReader(
	// new InputStreamReader(inStream));
	//
	// String line = "";
	// while ((line = reader.readLine()) != null) {
	// strings.add(line);
	// }
	// inStream.close();
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// return strings;
	//
	// }
	//
	// @Override
	// protected void onPostExecute(java.util.List<String> result) {
	// String version = "";
	// try {
	// if (result != null && result.size() > 0) {
	// PackageInfo packageInfo = context
	// .getPackageManager().getPackageInfo(
	// context.getPackageName(), 0);
	// for (String string : result) {
	// if (string.contains("channels")) {
	// String[] splits = string.split(";");
	// String channels = splits[1];
	// String[] channelsp = channels.split(":");
	// if (Conf.CID.equals(channelsp[1])) {
	// downloadUrl = splits[2];
	// version = splits[0];
	// } else {
	// if (channelsp[1].equals("none")){
	// downloadUrl = splits[2];
	// version = splits[0];
	// }
	//
	// }
	// }
	// }
	//
	//
	// String temp = version.substring(version.indexOf("\"") + 1,
	// version.length() - 1);
	// int serverVersion = Integer.valueOf(temp);
	// LogUtils.printLogE("服务器版本号", "---" + serverVersion);
	//
	// if (packageInfo.versionCode < serverVersion){
	//
	// }else{
	// tv_versionstatus.setText(R.string.str_version_currentversion);
	// }
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	//
	// }
	// }.execute();
	// }
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
	protected void initWidget() {
		// TODO Auto-generated method stub
		layout_back.setVisibility(View.VISIBLE);
		tv_title.setText(R.string.str_setting);
		if (UpdateAppReceiver.updateed) {
			tv_versionstatus.setText(R.string.str_version_newtversion);
			img_new.setVisibility(View.VISIBLE);
		} else {
			tv_versionstatus.setText(R.string.str_version_currentversion);
			img_new.setVisibility(View.GONE);
		}
		if (PreferenceHelper.readString(context, "auth", "pushed") == null
				|| PreferenceHelper.readString(context, "auth", "pushed")
						.equals("")
				|| PreferenceHelper.readString(context, "auth", "pushed")
						.equals("On")) {
			img_toggle.setImageResource(R.drawable.toggle_open);
			pushed = false;
		} else {
			img_toggle.setImageResource(R.drawable.toggle_close);
			pushed = true;
		}
	}
}

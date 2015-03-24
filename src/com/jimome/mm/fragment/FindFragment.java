package com.jimome.mm.fragment;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.activity.MainFragmentActivity;
import com.jimome.mm.adapter.PopAppAdapter;
import com.jimome.mm.common.Conf;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.IOUtils;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.jimome.mm.view.MyListView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.unjiaoyou.mm.R;

/**
 * 发现页面
 * 
 * @author admin
 * 
 */

public class FindFragment extends BaseFragment  {

	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.img_find_top)
	private ImageView img_find_top;
	@ViewInject(R.id.layout_find_store)
	private LinearLayout layout_find_store;
	@ViewInject(R.id.layout_find_near)
	private LinearLayout layout_find_near;
	@ViewInject(R.id.layout_find_video)
	private LinearLayout layout_find_video;
	@ViewInject(R.id.layout_find_onebuy)
	private LinearLayout layout_find_onebuy;
	@ViewInject(R.id.layout_find_read)
	private LinearLayout layout_find_read;
	@ViewInject(R.id.img_find_read)
	private ImageView img_find_read;
	@ViewInject(R.id.btn_find_task)
	private Button btn_find_task;
	@ViewInject(R.id.btn_find_chongzhi)
	private Button btn_find_chongzhi;
//	@ViewInject(R.id.list_find_popapp)
//	private MyListView list_find_popapp;
	@ViewInject(R.id.layout_find_guaguale)
	private LinearLayout layout_find_guaguale;
	private AnimationDrawable animation;
	private Activity context;
	private PopAppAdapter popAdapter;

	@Override
	protected View initView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		return arg0.inflate(R.layout.fragment_find, arg1, false);
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

	// 安装apk
	private void installApk(String filename) {
		try {

			File apkfile = new File(IOUtils.getLXHAPKFolder(filename)
					.toString());
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
					"application/vnd.android.package-archive");
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		} catch (Exception e) {
			// TODO: handle exception
		}
		// Intent stopintent = new Intent(getActivity(),
		// JiMoMainService.class);
		// getActivity().stopService(stopintent);
		// android.os.Process.killProcess(android.os.Process.myPid());
		// System.exit(0);// 否则退出程序

	}

	@OnClick({ R.id.btn_find_chongzhi, R.id.btn_find_task,
			R.id.layout_find_near, R.id.layout_find_video,
			R.id.layout_find_store, R.id.layout_find_guaguale,
			R.id.layout_find_onebuy, R.id.layout_find_read })
	protected void onClickView(View v) {
		switch (v.getId()) {
		case R.id.btn_find_chongzhi:
			Intent intent = new Intent(new Intent(context,
					FragmentToActivity.class));
			intent.putExtra("who", "vip");
			context.startActivity(intent);

			break;
		case R.id.btn_find_task:
			Intent coinIntent = new Intent(new Intent(context,
					FragmentToActivity.class));
			coinIntent.putExtra("who", "coin");
			context.startActivity(coinIntent);
			break;

		case R.id.layout_find_near:
			Intent nearIntent = new Intent(context, FragmentToActivity.class);
			nearIntent.putExtra("who", "fate");
			context.startActivity(nearIntent);
			break;
		case R.id.layout_find_video:
			// if (!Conf.user_VIP.equals("1")) {
			// final Dialog dialog = BasicUtils.showDialog(getActivity(),
			// R.style.BasicDialog);
			// dialog.setContentView(R.layout.dialog_rechargevip);
			// dialog.setCanceledOnTouchOutside(true);
			// ((Button) dialog.findViewById(R.id.btn_dialog_cancle))
			// .setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View arg0) {
			// // TODO Auto-generated method stub
			// dialog.dismiss();
			// }
			// });
			// ((Button) dialog.findViewById(R.id.btn_dialog_sure))
			// .setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View arg0) {
			// // TODO Auto-generated method stub
			// dialog.dismiss();
			// Intent intent = new Intent(getActivity(),
			// FragmentToActivity.class);
			// intent.putExtra("who", "vip");
			// getActivity().startActivity(intent);
			// }
			// });
			// dialog.show();
			// } else {
			// Intent videoIntent = new Intent(getActivity(),
			// MyShowActivity.class);
			// videoIntent.putExtra("type", 4);
			// videoIntent.putExtra("user_id", Conf.userID);
			// startActivity(videoIntent);
			// }

			Intent selectIntent = new Intent(context, FragmentToActivity.class);
			selectIntent.putExtra("who", "select");
			context.startActivity(selectIntent);
			break;
		case R.id.layout_find_store:

			Intent meiIntent = new Intent(context, FragmentToActivity.class);
			meiIntent.putExtra("who", "meili");
			context.startActivity(meiIntent);
			break;
		case R.id.layout_find_guaguale:

			Intent guaIntent = new Intent(context, FragmentToActivity.class);
			guaIntent.putExtra("who", "guaguale");
			context.startActivity(guaIntent);
			break;
		case R.id.layout_find_onebuy:
			Intent oneIntent = new Intent(context, FragmentToActivity.class);
			oneIntent.putExtra("who", "onebuy");
			context.startActivity(oneIntent);
			break;
		case R.id.layout_find_read:
			Intent readIntent = new Intent(context, FragmentToActivity.class);
			readIntent.putExtra("who", "read");
			context.startActivity(readIntent);
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
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();

	}

//	@Override
//	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
//		// TODO Auto-generated method stub
//		if (popAdapter != null && popAdapter.getCount() > 0)
//			if (BasicUtils.isInstallApk(popAdapter.allData().get(pos).getMsg())) {
//				try {
//					// PackageInfo info = context.getPackageManager()
//					// .getPackageInfo(
//					// popAdapter.allData().get(pos).getMsg(),
//					// PackageManager.GET_ACTIVITIES);
//					// if ((info.activities != null)
//					// && (info.activities.length > 0)) {
//					// String packname = info.packageName;
//					// String activityName = info.activities[5].name;
//					// popIntent = new Intent();
//					// popIntent.setComponent(new ComponentName(packname,
//					// activityName));
//					// popIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					// LogUtils.printLogE("冷笑话APP", "启动----");
//					// context.startActivity(popIntent);
//					PackageManager packageManager = context.getPackageManager();
//					Intent intent = new Intent();
//					intent = packageManager
//							.getLaunchIntentForPackage(popAdapter.allData()
//									.get(pos).getMsg());
//					context.startActivity(intent);
//					// }
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//			} else if (MainFragmentActivity.list_popJson.get(pos).getAnswer() != null
//					&& MainFragmentActivity.list_popJson.get(pos).getAnswer()
//							.equals("OK")) {
//				installApk(popAdapter.allData().get(pos).getText());
//			} else {
//				BasicUtils.toast(StringUtils.getResourse(
//						R.string.str_waitdown));
//			}
//	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		tv_title.setText(R.string.str_discovery);
		img_find_top.setBackgroundResource(R.anim.anim_find);
		animation = (AnimationDrawable) img_find_top.getBackground();
		animation.start();
//		// 泡妞恋爱终极教程头像
//		ImageLoadUtils.imageLoader.displayImage(Conf.READ_ICON, img_find_read,
//				ImageLoadUtils.options);
//		list_find_popapp.setOnItemClickListener(this);
//		if (MainFragmentActivity.list_popJson != null
//				&& MainFragmentActivity.list_popJson.size() > 0) {
//			popAdapter = new PopAppAdapter(context,
//					MainFragmentActivity.list_popJson);
//			list_find_popapp.setAdapter(popAdapter);
//		}
//		getTip();
	}

//	private void getTip() {
//		if (!PreferenceHelper.readBoolean(context, "auth", "readfind")) {
//			// if (BasicUtils.isInstallApk("com.tencent.android.qqdownloader"))
//			// {
//			final Dialog dialog = BasicUtils.showDialog(context,
//					R.style.BasicDialog);
//			dialog.setContentView(R.layout.dialog_rechargevip);
//			dialog.setCanceledOnTouchOutside(false);
//			((TextView) dialog.findViewById(R.id.tv_dialog_msg))
//					.setText(StringUtils.getResourse(R.string.str_find_tiptitle));
//			((Button) dialog.findViewById(R.id.btn_dialog_cancle))
//					.setText(StringUtils.getResourse(R.string.str_find_tipcancel));
//			((Button) dialog.findViewById(R.id.btn_dialog_cancle)).setTextColor(context.getResources().getColor(R.color.white));
//			BasicUtils.setBackground(context, ((Button) dialog.findViewById(R.id.btn_dialog_cancle)), R.color.red);
//			((Button) dialog.findViewById(R.id.btn_dialog_cancle))
//					.setOnClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View arg0) {
//							// TODO Auto-generated method stub
//							dialog.dismiss();
//							Intent readIntent = new Intent(context,
//									FragmentToActivity.class);
//							readIntent.putExtra("who", "read");
//							context.startActivity(readIntent);
//						}
//					});
//			((Button) dialog.findViewById(R.id.btn_dialog_sure))
//					.setText(StringUtils.getResourse(R.string.str_find_tipsure));
//			((Button) dialog.findViewById(R.id.btn_dialog_sure))
//					.setOnClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View arg0) {
//							// TODO Auto-generated method stub
//							dialog.dismiss();
//							Intent oneIntent = new Intent(context,
//									FragmentToActivity.class);
//							oneIntent.putExtra("who", "onebuy");
//							context.startActivity(oneIntent);
//						}
//					});
//			dialog.show();
//			PreferenceHelper.write(context, "auth", "readfind",true);
//		}
//	}
}

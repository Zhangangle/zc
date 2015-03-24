package com.jimome.mm.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.unjiaoyou.mm.R;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.activity.SelectDetailActivity;
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
import com.jimome.mm.view.PullScrollView;
import com.jimome.mm.view.PullScrollView.OnPullListener;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * 视频秀页面
 * 
 * @author admin
 * 
 */

public class VideoFragment extends BaseFragment implements OnPullListener,
		OnItemClickListener {

	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.layout_text_chong)
	private LinearLayout layout_text_chong;
	@ViewInject(R.id.pushscroll_gv)
	private PullScrollView pullScrollView;
	private GridView gv_photo;
	private LinearLayout contentLayout;
	private int last_posx, last_posy;
	private MeiTaoAdapter adapter;
	private BaseJson json;
	// private Dialog mDialog;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
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
		//
		// @Override
		// public boolean onKey(DialogInterface dialog, int keyCode,
		// KeyEvent event) {
		// if (keyCode == KeyEvent.KEYCODE_BACK
		// && event.getAction() == KeyEvent.ACTION_DOWN) {
		// if (mDialog != null && !context.isFinishing())
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
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	private void gethttpData(final String type) {
		RequestParams params = new RequestParams();
		String key = "";
		if (type.equals("main")) {
			key = "video";// 发现--视频秀
		} else {
			key = "video/see";
		}
		params.addQueryStringParameter("cur_user", Conf.userID);
		// LogUtils.printLogE("参数", url + "\n" + params.toString());
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key, 0,
				new CacheRequestCallBack() {

					@Override
					public void onFail(HttpException e, String result,
							String json) {

						// LogUtils.printLogE("视频秀——error", arg2.toString());
						pullScrollView.setheaderViewReset();
						pullScrollView.setfooterViewReset();
						pullScrollView.setVisibility(View.VISIBLE);
						if (mDialog != null) {
							mDialog.dismiss();
						}
						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
					}

					@Override
					public void onData(String arg0) {
						// TODO Auto-generated method stub
						try {
							// LogUtils.printLogE("视频秀", arg0.toString());
							if (type.equals("main")) {
								json = new Gson()
										.fromJson(arg0, BaseJson.class);
								if (json.getStatus().equals("200")) {
									adapter = new MeiTaoAdapter(context, json
											.getUploads(), 4, Conf.userID);
									gv_photo.setAdapter(adapter);
								}
							} else {
								BaseJson base = new Gson().fromJson(arg0,
										BaseJson.class);
								if (base.getStatus().equals("200")) {
									if (adapter.allDate()
											.get(Integer.valueOf(type))
											.getVideo_url() != null) {
										Intent intent = new Intent(
												Intent.ACTION_VIEW);
										String video_type = "video/*";
										Uri uri = Uri.parse(adapter.allDate()
												.get(Integer.valueOf(type))
												.getVideo_url());
										intent.setDataAndType(uri, video_type);
										context.startActivity(intent);
									}
								} else if (base.getStatus().equals("152")) {
									final Dialog dialog = BasicUtils
											.showDialog(context,
													R.style.BasicDialog);
									dialog.setContentView(R.layout.dialog_rechargevip);
									dialog.setCanceledOnTouchOutside(true);
									((TextView) dialog
											.findViewById(R.id.tv_dialog_msg))
											.setText(base.getMsg());
									((Button) dialog
											.findViewById(R.id.btn_dialog_cancle))
											.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View arg0) {
													// TODO Auto-generated
													// method stub
													dialog.dismiss();
												}
											});
									((Button) dialog
											.findViewById(R.id.btn_dialog_sure))
											.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View arg0) {
													// TODO Auto-generated
													// method stub
													dialog.dismiss();
													Intent intent = new Intent(
															context,
															FragmentToActivity.class);
													intent.putExtra("who",
															"coin");
													context.startActivity(intent);
												}
											});
									dialog.show();
								} else {
									Toast.makeText(
											context,
											StringUtils.getResourse(R.string.str_net_register),
											Toast.LENGTH_SHORT).show();
								}
							}

						} catch (Exception e) {
							// TODO: handle exception
						} finally {
							pullScrollView.setheaderViewReset();
							pullScrollView.setfooterViewReset();
							pullScrollView.setVisibility(View.VISIBLE);
							if (mDialog != null) {
								mDialog.dismiss();
							}
						}

					}

				});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		// TODO Auto-generated method stub
		try {

			Intent intent = new Intent(context, SelectDetailActivity.class);
			intent.putExtra("photo", adapter.allDate().get(pos));
			intent.putExtra("detail", "video");
			intent.putExtra("pos", pos);
			intent.putExtra("type", 3);
			context.startActivity(intent);
			// if (pos == 0 || pos == 1) {
			// if (adapter.allDate().get(pos).getVideo_url() != null) {
			// Intent intent = new Intent(Intent.ACTION_VIEW);
			// String type = "video/*";
			// Uri uri = Uri.parse(adapter.allDate().get(pos)
			// .getVideo_url());
			// intent.setDataAndType(uri, type);
			// startActivity(intent);
			// }
			// } else {
			// waitDialog();
			// gethttpData(pos + "");
			// }
			// if (Conf.user_VIP.equals("1")) {
			// if (adapter.allDate().get(pos).getVideo_url() != null) {
			// Intent intent = new Intent(Intent.ACTION_VIEW);
			// String type = "video/*";
			// Uri uri = Uri.parse(adapter.allDate().get(pos)
			// .getVideo_url());
			// intent.setDataAndType(uri, type);
			// startActivity(intent);
			// }
			// } else {
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
			// }

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void initView() {
		tv_title.setText(R.string.str_my_shipinxiu);
		layout_back.setVisibility(View.GONE);
		layout_text_chong.setVisibility(View.VISIBLE);
		contentLayout = (LinearLayout) context.getLayoutInflater().inflate(
				R.layout.layout_meitao, null);
		gv_photo = (GridView) contentLayout.findViewById(R.id.gv_meitao);
		gv_photo.setNumColumns(2);
		pullScrollView.addBodyView(contentLayout);
		pullScrollView.setOnPullListener(this);
		gv_photo.setOnItemClickListener(this);
		gv_photo.setOnScrollListener(new PauseOnScrollListener(ImageLoader
				.getInstance(), false, true));
		if (json == null) {
			waitDialog();
			gethttpData("main");

		} else if (adapter == null) {
			pullScrollView.setheaderViewReset();
			pullScrollView.setfooterViewReset();
			pullScrollView.setVisibility(View.VISIBLE);
			if (json.getUploads() != null) {
				adapter = new MeiTaoAdapter(context, json.getUploads(), 4,
						Conf.userID);
				gv_photo.setAdapter(adapter);
			}
		} else {
			pullScrollView.setheaderViewReset();
			pullScrollView.setfooterViewReset();
			pullScrollView.setVisibility(View.VISIBLE);
			gv_photo.setAdapter(adapter);
			pullScrollView.post(new Runnable() {

				@Override
				public void run() {

					pullScrollView.scrollTo(last_posx, last_posy);
				}
			});
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		if (adapter != null && json != null)
			json.setUploads(adapter.allDate());
		last_posx = pullScrollView.getScrollX();
		last_posy = pullScrollView.getScrollY();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mDialog != null)
			mDialog.dismiss();
	}

	@OnClick({ R.id.layout_back, R.id.layout_text_chong })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.layout_text_chong:
			Intent intent = new Intent(context, FragmentToActivity.class);
			intent.putExtra("who", "vip");
			context.startActivity(intent);
			break;
		case R.id.layout_back:
			context.finish();
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

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		if (NetworkUtils.checkNet(context)) {
			gethttpData("main");
		} else {
			Toast.makeText(context, StringUtils.getResourse(R.string.str_net_register),
					Toast.LENGTH_SHORT).show();
			pullScrollView.setheaderViewReset();
		}
	}

	@Override
	public void loadMore() {
		// TODO Auto-generated method stub
		if (NetworkUtils.checkNet(context)) {
			gethttpData("main");
		} else {
			Toast.makeText(context, StringUtils.getResourse(R.string.str_net_register),
					Toast.LENGTH_SHORT).show();
			pullScrollView.setheaderViewReset();
		}
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		initView();
	}
}

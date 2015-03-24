package com.jimome.mm.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.LogUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.unjiaoyou.mm.R;

/**
 * 显示我的礼物List适配
 * 
 * @author Administrator
 * 
 */
public class MyGiftListAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_person;
	private String type = "";
	private String[] add;
	private Dialog mDialog, addDialog;

	public MyGiftListAdapter(Context context, List<BaseJson> list_person,
			String type) {
		this.context = context;
		this.list_person = list_person;
		this.type = type;
		if (type.equals("1")) {
			add = new String[] {
					context.getString(R.string.str_mygift_receive),
					context.getString(R.string.str_mygift_return) };
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_person.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return list_person.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	public void insertData(List<BaseJson> list) {
		list_person.addAll(list);
	}

	public List<BaseJson> allDate() {
		return list_person;
	}

	public void removeDate(int pos) {
		list_person.get(pos).setCan_retrieve(0);
	}

	@Override
	public View getView(final int pos, View view, ViewGroup viewGrop) {
		// TODO Auto-generated method stub
		try {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.list_item_mygift, viewGrop, false);
			}

			final ImageView img_mygift_listicon = BaseAdapterHelper.get(view,
					R.id.img_mygift_listicon);
			final ImageView img_mygift_listpic = BaseAdapterHelper.get(view,
					R.id.img_mygift_listpic);
			TextView tv_mygift_listname = BaseAdapterHelper.get(view,
					R.id.tv_mygift_listname);
			TextView tv_mygift_listmsg = BaseAdapterHelper.get(view,
					R.id.tv_mygift_listmsg);
			TextView tv_mygift_listcoin = BaseAdapterHelper.get(view,
					R.id.tv_mygift_listcoin);
			TextView tv_mygift_listanswer = BaseAdapterHelper.get(view,
					R.id.tv_mygift_listanswer);
			Button btn_mygift_listoperate = BaseAdapterHelper.get(view,
					R.id.btn_mygift_listoperate);
			//
			float scale = (float) (0.4 * Conf.densityDPI);
			LayoutParams params = new LayoutParams((int) scale, (int) scale);
			img_mygift_listicon.setLayoutParams(params);

			float gifsacle = (float) (0.4 * Conf.densityDPI);
			img_mygift_listpic.setLayoutParams(new LayoutParams((int) gifsacle,
					(int) gifsacle));

			ImageLoadUtils.imageLoader.displayImage(list_person.get(pos)
					.getIcon(), img_mygift_listicon,
					ImageLoadUtils.optionsRounded, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("2"))
								img_mygift_listicon
										.setBackgroundResource(R.drawable.default_female);
							else
								img_mygift_listicon
										.setBackgroundResource(R.drawable.default_male);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("2"))
								img_mygift_listicon
										.setBackgroundResource(R.drawable.default_female);
							else
								img_mygift_listicon
										.setBackgroundResource(R.drawable.default_male);
						}

						@Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap arg2) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							// TODO Auto-generated method stub

						}
					});
			ImageLoadUtils.imageLoader.displayImage(list_person.get(pos)
					.getImg(), img_mygift_listpic,
					ImageLoadUtils.optionsRounded, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							img_mygift_listpic
									.setBackgroundResource(R.drawable.logo);
						}

						@Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap arg2) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							// TODO Auto-generated method stub

						}
					});
			tv_mygift_listname.setText(list_person.get(pos).getNick());
			tv_mygift_listmsg.setText(list_person.get(pos).getName());
			tv_mygift_listcoin.setText(context
					.getString(R.string.str_talk_coin)
					+ list_person.get(pos).getCoin());
			if (type.equals("1")) {
				tv_mygift_listanswer.setVisibility(View.GONE);
				btn_mygift_listoperate.setText(context
						.getString(R.string.str_mygift_operate));
			} else {
				tv_mygift_listanswer.setVisibility(View.VISIBLE);
				if (list_person.get(pos).getCan_retrieve() == 1) {
					tv_mygift_listanswer.setText(context
							.getString(R.string.str_mygift_answer));
					btn_mygift_listoperate.setText(context
							.getString(R.string.str_mygift_getback));
					btn_mygift_listoperate.setVisibility(View.VISIBLE);
				} else {
					tv_mygift_listanswer.setText("");
					btn_mygift_listoperate.setVisibility(View.INVISIBLE);
				}
			}
			btn_mygift_listoperate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (type.equals("1")) {// 获取礼物
						addDialog = new AlertDialog.Builder(context).setItems(
								add, new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int pos) {
										// TODO Auto-generated method stub
										if (pos == 0) {

										} else {

										}
										dialog.dismiss();
									}
								}).create();
						addDialog.show();
					} else {// 送出礼物
						if (list_person.get(pos).getCan_retrieve() == 1) {
							getHttpData("3", pos, list_person.get(pos).getId());
						}
					}
				}
			});
			img_mygift_listicon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (!list_person.get(pos).getUser_id().equals(Conf.userID))// 不是查看自己的资料
					{
						if (BasicUtils.isFastDoubleClick()) {
							return;
						}
						Intent intent = new Intent(context,
								FragmentToActivity.class);
						intent.putExtra("who", "personal");
						intent.putExtra("user_id", list_person.get(pos)
								.getUser_id());
						intent.putExtra("distance", "");
						context.startActivity(intent);
					}
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return view;
	}

	private void getHttpData(final String type, final int pos, String id) {
		waitDialog();
		RequestParams params = new RequestParams();
		String key = "me/gift/retrieve";
		try {
			params.addQueryStringParameter("cur_user", Conf.userID);
			params.addQueryStringParameter("log_id", id);
			params.addHeader("Authorization",
					PreferenceHelper.readString(context, "auth", "token"));
		} catch (Exception e) {
			// TODO: handle exception
		}

		CacheRequest.requestGET(context, key, params, key, 0,
				new CacheRequestCallBack() {

					@Override
					public void onData(String json) {
						// TODO Auto-generated method stub
						try {
							LogUtils.printLogE("送出礼物result", json);
							BaseJson baseJson = new Gson().fromJson(
									json.toString(), BaseJson.class);
							if (baseJson.getStatus().equals("200")) {
								Intent intent = new Intent();
								if (type.equals("1")) {// 领取礼物
									intent.setAction("jimome.action.getgift");
									Toast.makeText(
											context,
											context.getString(R.string.str_task_success),
											Toast.LENGTH_SHORT).show();
								} else if (type.equals("2")) {// 退回礼物
									intent.setAction("jimome.action.returngift");
									Toast.makeText(
											context,
											context.getString(R.string.str_mygift_returnsuccess),
											Toast.LENGTH_SHORT).show();
								} else {// 取回礼物
									intent.setAction("jimome.action.getbackgift");
									Toast.makeText(
											context,
											context.getString(R.string.str_mygift_getbacksuccess),
											Toast.LENGTH_SHORT).show();
								}
								intent.putExtra("pos", pos);
								context.sendBroadcast(intent);
							} else {
								Toast.makeText(
										context,
										context.getString(R.string.str_net_register),
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
						Toast.makeText(context,
								context.getString(R.string.str_net_register),
								Toast.LENGTH_SHORT).show();

					}

				});

	}

	private void waitDialog() {
		mDialog = BasicUtils.showDialog(context, R.style.BasicDialog);
		mDialog.setContentView(R.layout.dialog_wait);
		mDialog.setCanceledOnTouchOutside(false);

		Animation anim = AnimationUtils.loadAnimation(context,
				R.anim.dialog_prog);
		LinearInterpolator lir = new LinearInterpolator();
		anim.setInterpolator(lir);
		mDialog.findViewById(R.id.img_dialog_progress).startAnimation(anim);
		mDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					if (mDialog != null)
						mDialog.dismiss();
					return false;
				}
				return false;
			}
		});
		mDialog.show();
	}
}

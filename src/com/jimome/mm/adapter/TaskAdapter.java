package com.jimome.mm.adapter;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.unjiaoyou.mm.R;
import com.jimome.mm.activity.ExchangeActivity;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.service.JiMoMainService;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.LogUtils;
import com.jimome.mm.utils.NetworkUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

/**
 * 个人任务适配
 * 
 * @author Administrator
 * 
 */
public class TaskAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_task;
	private Dialog mDialog;

	public TaskAdapter(Context context, List<BaseJson> list_task) {
		this.context = context;
		this.list_task = list_task;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_task.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return list_task.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	public void removeDate(int pos) {
		list_task.remove(pos);
	}

	public void insertData(List<BaseJson> list) {
		list_task.addAll(list);
	}

	public List<BaseJson> allDate() {
		return list_task;
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
					mDialog.dismiss();

					return false;
				}
				return false;
			}
		});
		mDialog.show();
	}

	@Override
	public View getView(final int pos, View view, ViewGroup viewGrop) {
		// TODO Auto-generated method stub
		try {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.list_item_task, viewGrop, false);
			}

			// TextView tv_task_listnature = BaseAdapterHelper.get(view,
			// R.id.tv_task_listnature);
			TextView tv_task_listname = BaseAdapterHelper.get(view,
					R.id.tv_task_listname);
			TextView tv_task_listdescription = BaseAdapterHelper.get(view,
					R.id.tv_task_listdescription);
			TextView tv_task_listprogress = BaseAdapterHelper.get(view,
					R.id.tv_task_listprogress);
			TextView tv_task_listaward = BaseAdapterHelper.get(view,
					R.id.tv_task_listaward);
			Button btn_task_listgetaward = BaseAdapterHelper.get(view,
					R.id.btn_task_listgetaward);
			ImageView img_task_type = BaseAdapterHelper.get(view,
					R.id.img_task_type);
			if (list_task.get(pos).getStyle().contains("每日"))
				img_task_type.setImageResource(R.drawable.task_type_everyday);
			else if (list_task.get(pos).getStyle().contains("新手"))
				img_task_type.setImageResource(R.drawable.task_type_new);
			else
				img_task_type.setImageResource(R.drawable.task_type_high);
			tv_task_listname.setText(list_task.get(pos).getName());
			tv_task_listdescription.setText(list_task.get(pos).getText());
			tv_task_listaward.setText(list_task.get(pos).getAward());
			tv_task_listprogress.setText(list_task.get(pos).getComplete_nums()
					+ "/" + list_task.get(pos).getRequire_nums());
			if (list_task.get(pos).getComplete_nums().trim()
					.equals(list_task.get(pos).getRequire_nums().trim())) {
				btn_task_listgetaward.setBackgroundResource(R.color.lightblue);
				btn_task_listgetaward.setText(R.string.str_task_getaward);
			} else {
				btn_task_listgetaward.setBackgroundResource(R.color.lightblue);
				btn_task_listgetaward.setText(R.string.str_task_waitaward);
			}
			btn_task_listgetaward.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (list_task
							.get(pos)
							.getComplete_nums()
							.trim()
							.equals(list_task.get(pos).getRequire_nums().trim())) {
						if (NetworkUtils.checkNet(context)) {
							getHttpData(list_task.get(pos).getId(), pos);
						} else {
							Toast.makeText(context,
									context.getString(R.string.str_open_net),
									Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LogUtils.printLogE("异常", e.toString());
		}
		return view;
	}

	private void getHttpData(String type, final int pos) {
		waitDialog();
		RequestParams params = new RequestParams();
		String key = "msg/task/award/get";
		try {
			params.addQueryStringParameter("cur_user", Conf.userID);
			params.addQueryStringParameter("task_id", type);
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
							BaseJson baseJson = new Gson().fromJson(json,
									BaseJson.class);
							if (baseJson.getStatus().equals("200")) {
								Toast.makeText(
										context,
										context.getString(R.string.str_task_success),
										Toast.LENGTH_SHORT).show();
								Intent intent = new Intent();
								intent.setAction("jimome.action.taskrefresh");
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
}

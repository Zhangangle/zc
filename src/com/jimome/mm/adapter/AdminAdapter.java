package com.jimome.mm.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unjiaoyou.mm.R;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.activity.MyGiftActivity;
import com.jimome.mm.activity.MyShowUploadActivity;
import com.jimome.mm.activity.ViewPageActivity;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.utils.ImageLoadUtils;

public class AdminAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_person;

	private List<BaseJson> list;// 查看图片实现
	private BaseJson base;// 查看图片实现

	public AdminAdapter(Context context, List<BaseJson> list_person) {
		this.context = context;
		this.list_person = list_person;
	}

	public void insert(BaseJson per) {
		this.list_person.add(per);
	}

	public void insertList(List<BaseJson> list) {
		this.list_person.addAll(list);
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

	@Override
	public View getView(int pos, View view, ViewGroup viewGrop) {
		// TODO Auto-generated method stub
		try {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.list_item_talk_mine, viewGrop, false);
			}
			ImageView icon = BaseAdapterHelper
					.get(view, R.id.img_talk_mineicon);
			TextView send_msg = BaseAdapterHelper.get(view,
					R.id.tv_talk_minemsg);
			TextView send_time = BaseAdapterHelper.get(view,
					R.id.tv_talk_minetime);
			ImageView oppicon = BaseAdapterHelper.get(view,
					R.id.img_talk_oppicon);
			LinearLayout layout = BaseAdapterHelper.get(view, R.id.layout_talk);
			LinearLayout layout_msg = BaseAdapterHelper.get(view,
					R.id.layout_msg);

			RelativeLayout relayout_video = BaseAdapterHelper.get(view,
					R.id.relayout_video);
			ImageView img_play_video = BaseAdapterHelper.get(view,
					R.id.img_play_video);
			ImageView img_tip_video = BaseAdapterHelper.get(view,
					R.id.img_tip_video);
			final BaseJson sender = list_person.get(pos);

			if (sender.getSender() == null
					|| !sender.getSender().equals(Conf.userID)) {
				icon.setVisibility(View.INVISIBLE);
				oppicon.setVisibility(View.VISIBLE);

				relayout_video.setVisibility(View.GONE);
				send_msg.setVisibility(View.VISIBLE);
				img_tip_video.setVisibility(View.GONE);
				img_play_video.setVisibility(View.GONE);
				layout.setGravity(Gravity.LEFT);
				send_msg.setTextColor(Color.DKGRAY);
				layout_msg.setBackgroundResource(R.drawable.chatmsg_left);
				oppicon.setImageResource(R.drawable.logo);

				send_msg.setText(sender.getText());
			} else {
				img_play_video
						.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
								(int) (0.25 * Conf.width),
								(int) (0.25 * Conf.width)));
				oppicon.setVisibility(View.INVISIBLE);
				icon.setVisibility(View.VISIBLE);
				layout.setGravity(Gravity.RIGHT);
				send_msg.setTextColor(Color.WHITE);
				layout_msg.setBackgroundResource(R.drawable.chatmsg_right);

				if (Conf.userImg.equals("")) {
					if (Conf.gender.trim().equals("1"))
						icon.setImageResource(R.drawable.default_male);
					else
						icon.setImageResource(R.drawable.default_female);
				} else {
					ImageLoadUtils.imageLoader.displayImage(Conf.userImg, icon,
							ImageLoadUtils.options);
				}
				if (sender.getMsg_type().equals("1")) {
					relayout_video.setVisibility(View.GONE);
					send_msg.setVisibility(View.VISIBLE);
					img_tip_video.setVisibility(View.GONE);
					img_play_video.setVisibility(View.GONE);
					send_msg.setText(sender.getText());
				} else if (sender.getMsg_type().equals("2")) {
					relayout_video.setVisibility(View.VISIBLE);
					send_msg.setVisibility(View.GONE);
					img_tip_video.setVisibility(View.GONE);
					img_play_video.setVisibility(View.VISIBLE);
					ImageLoadUtils.imageLoader.displayImage(sender.getText(),
							img_play_video, ImageLoadUtils.options);
					img_play_video.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (sender.getMsg_type() != null
									&& sender.getMsg_type().equals("2")) {
								Intent intent = new Intent(context,
										ViewPageActivity.class);
								list = new ArrayList<BaseJson>();
								base = new BaseJson();
								// if (sender.getUrl().contains("@")) {
								// base.setUrl(sender.getUrl().substring(0,
								// sender.getUrl().indexOf("@")));
								// } else {
								base.setUrl(sender.getText());
								// }
								list.add(base);
								intent.putExtra("list_image",
										(Serializable) list);
								intent.putExtra("pos", 0);
								context.startActivity(intent);
							}
						}
					});
				}
			}

			if (sender.getTime() == null || sender.getTime().equals("00")) {

				send_time.setVisibility(View.GONE);
			} else {
				send_time.setText(sender.getTime().trim());
				send_time.setVisibility(View.VISIBLE);
			}
			// 根据不同类型进行跳转
			layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					if (sender.getTip_id() != null) {
						Log.e("Tip_id", sender.getTip_id());
						if (sender.getTip_id().equals("9")
								|| sender.getTip_id().equals("10")) {// 身材秀
							Intent shenIntent = new Intent(context,
									MyShowUploadActivity.class);
							shenIntent.putExtra("type", 2);
							context.startActivity(shenIntent);
						} else if (sender.getTip_id().equals("200")) {// 女神秀
							// Intent shenIntent = new Intent(context,
							// MyShowUploadActivity.class);
							// shenIntent.putExtra("type", 1);
							// context.startActivity(shenIntent);

						} else if (sender.getTip_id().equals("11")) {// 视频秀
							Intent shenIntent = new Intent(context,
									MyShowUploadActivity.class);
							shenIntent.putExtra("type", 3);
							context.startActivity(shenIntent);
						} else if (sender.getTip_id().equals("12")) {// 头像
							Intent intent = new Intent(context,
									FragmentToActivity.class);
							intent.putExtra("who", "myself");
							context.startActivity(intent);
						} else if (sender.getTip_id().equals("13")) {// 完善资料
							Intent meiIntent = new Intent(context,
									FragmentToActivity.class);
							meiIntent.putExtra("who", "mine");
							context.startActivity(meiIntent);
						} else if (sender.getTip_id().equals("600")) {// 兑换商城
							// Intent meiIntent = new Intent(context,
							// FragmentToActivity.class);
							// meiIntent.putExtra("who", "meili");
							// context.startActivity(meiIntent);

						} else if (sender.getTip_id().equals("700")) {// 收到礼物
							Intent intent = new Intent(context,
									MyGiftActivity.class);
							BaseJson person = new BaseJson();
							person.setUser_id(Conf.userID);
							person.setNick(Conf.userName);
							intent.putExtra("person", person);
							context.startActivity(intent);
						} else if (sender.getTip_id().equals("800")) {// 做任务
							Intent coinIntent = new Intent(context,
									FragmentToActivity.class);
							coinIntent.putExtra("who", "coin");
							context.startActivity(coinIntent);
						} else if (sender.getTip_id().equals("17")) {// 充值VIP
							Intent vipIntent = new Intent(context,
									FragmentToActivity.class);
							vipIntent.putExtra("who", "vip");
							context.startActivity(vipIntent);
						}

					}
				}
			});

		} catch (Exception e) {
			// TODO: handle exception
			try {
				e.printStackTrace();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return view;
	}
}

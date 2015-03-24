package com.jimome.mm.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.unjiaoyou.mm.R;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ImageLoadUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 评论和求更多adapter
 * 
 * @author admin
 * 
 */

public class ReplyPraiseAdapter extends BaseAdapter {

	private Context context;
	private String type;
	private List<BaseJson> list_sender;


	public ReplyPraiseAdapter(Context c, String type, List<BaseJson> list_sender) {
		context = c;
		this.type = type;
		this.list_sender = list_sender;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_sender.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return list_sender.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	public void insertData(List<BaseJson> list) {
		list_sender.addAll(list);
	}

	public List<BaseJson> allDate() {
		return list_sender;
	}

	@Override
	public View getView(final int pos, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		try {
			if (arg1 == null) {
				arg1 = LayoutInflater.from(context).inflate(
						R.layout.list_item_replypraise, arg2, false);
			}

			final ImageView avatar = BaseAdapterHelper.get(arg1,
					R.id.list_item_rp_avatar);
			TextView content = BaseAdapterHelper.get(arg1,
					R.id.list_item_rp_content);
			TextView nick = BaseAdapterHelper.get(arg1, R.id.list_item_rp_nick);
			TextView time = BaseAdapterHelper.get(arg1, R.id.list_item_rp_time);
			ImageView reply = BaseAdapterHelper.get(arg1,
					R.id.list_item_rp_reply);
			nick.setText(list_sender.get(pos).getSender_nick());
			if (type.equals("comment")) {
				content.setText(list_sender.get(pos).getText());
				time.setText(list_sender.get(pos).getTime());
				content.setVisibility(View.VISIBLE);
				reply.setVisibility(View.VISIBLE);
			} else {
				content.setVisibility(View.GONE);
				time.setVisibility(View.GONE);
				reply.setVisibility(View.GONE);
			}
			ImageLoadUtils.imageLoader.displayImage(list_sender.get(pos).getSender_icon(),
					avatar, ImageLoadUtils.options, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("1"))
								avatar.setImageResource(R.drawable.account_focus);
							else
								avatar.setImageResource(R.drawable.account_focus);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("1"))
								avatar.setImageResource(R.drawable.default_male);
							else
								avatar.setImageResource(R.drawable.default_female);
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
			avatar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (BasicUtils.isFastDoubleClick()) {
						return;
					}
					Intent intent = new Intent(context,
							FragmentToActivity.class);
					if (list_sender.get(pos).getSender() != null) {
						if (list_sender.get(pos).getSender()
								.equals(Conf.userID)) {
							intent.putExtra("who", "myself");
						} else {
							intent.putExtra("who", "personal");
							intent.putExtra("user_id", list_sender.get(pos)
									.getSender());
							intent.putExtra("distance", "");
						}
						context.startActivity(intent);
					}
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
		}
		return arg1;
	}

}

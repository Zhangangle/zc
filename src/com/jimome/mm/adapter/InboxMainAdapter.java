package com.jimome.mm.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.unjiaoyou.mm.R;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ImageLoadUtils;

public class InboxMainAdapter extends BaseSwipeAdapter {

	private Context context;
	private List<BaseJson> list_inbox;

	public InboxMainAdapter(Context context, List<BaseJson> list_inbox) {
		this.context = context;
		this.list_inbox = list_inbox;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_inbox.size();
	}

	public void update(List<BaseJson> list) {
		list_inbox.addAll(list);
	}

	public void remove(int pos) {
		list_inbox.remove(pos);
	}

	public void refresh(int pos) {
		list_inbox.get(pos).setMsg_nums("0");
	}

	public void refreshAll() {
		for (int i = 0; i < list_inbox.size(); i++)
			list_inbox.get(i).setMsg_nums("0");
	}

	public List<BaseJson> getAllData() {
		return list_inbox;
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return list_inbox.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public View generateView(final int pos, ViewGroup viewGrop) {
		// TODO Auto-generated method stub

		View view = LayoutInflater.from(context).inflate(
				R.layout.list_item_inbox_main, viewGrop, false);
		final SwipeLayout swipeLayout = (SwipeLayout) view
				.findViewById(getSwipeLayoutResourceId(pos));
		swipeLayout.addSwipeListener(new SimpleSwipeListener() {
			@Override
			public void onOpen(SwipeLayout layout) {
				YoYo.with(Techniques.Tada).duration(500).delay(100)
						.playOn(layout.findViewById(R.id.img_trash));
			}

		});
		swipeLayout.findViewById(R.id.tv_delete).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						swipeLayout.close();
						Intent intent = new Intent();
						intent.setAction("jimo.action.delete");
						intent.putExtra("pos", pos);
						context.sendBroadcast(intent);

					}
				});
		return view;
	}

	@Override
	public void removeShownLayouts(SwipeLayout layout) {
		// TODO Auto-generated method stub
		super.removeShownLayouts(layout);
	}

	@Override
	public void fillValues(final int pos, View view) {
		// TODO Auto-generated method stub
		try {
			ImageView img_inbox_listicon = BaseAdapterHelper.get(view,
					R.id.img_inbox_listicon);
			TextView tv_inbox_listname = BaseAdapterHelper.get(view,
					R.id.tv_inbox_listname);
			TextView tv_inbox_listheight = BaseAdapterHelper.get(view,
					R.id.tv_inbox_listheight);
			TextView tv_inbox_listage = BaseAdapterHelper.get(view,
					R.id.tv_inbox_listage);
			TextView tv_inbox_listmsg = BaseAdapterHelper.get(view,
					R.id.tv_inbox_listmsg);
			final TextView tv_inbox_nums = BaseAdapterHelper.get(view,
					R.id.tv_inbox_nums);

			if (list_inbox.get(pos).getIcon().trim().equals("")) {
				if (Conf.gender.equals("1"))
					img_inbox_listicon
							.setImageResource(R.drawable.default_female);
				else
					img_inbox_listicon
							.setImageResource(R.drawable.default_male);
			} else
				ImageLoadUtils.imageLoader.displayImage(list_inbox.get(pos)
						.getIcon(), img_inbox_listicon, ImageLoadUtils.options);
			if (!list_inbox.get(pos).getMsg_nums().equals("0")) {
				if (Integer.valueOf(list_inbox.get(pos).getMsg_nums()) > 99)
					tv_inbox_nums.setText("99+");
				else
					tv_inbox_nums.setText(list_inbox.get(pos).getMsg_nums());
				tv_inbox_nums.setVisibility(View.VISIBLE);
			} else {
				tv_inbox_nums.setVisibility(View.GONE);
			}
			tv_inbox_listname.setText(list_inbox.get(pos).getNick());

			if (list_inbox.get(pos).getHeight() != null
					&& !list_inbox.get(pos).getHeight().trim().equals("")) {
				tv_inbox_listheight.setText(list_inbox.get(pos).getHeight()
						+ context.getResources().getString(
								R.string.str_heightuntil));
			} else {
				tv_inbox_listheight.setText("身高保密");
			}
			if (list_inbox.get(pos).getAge() != null
					& !list_inbox.get(pos).getAge().trim().equals("")) {
				tv_inbox_listage.setText(list_inbox.get(pos).getAge()
						+ context.getResources().getString(R.string.str_sui));
			} else {
				tv_inbox_listage.setText("年龄保密");
			}
			tv_inbox_listmsg.setText(list_inbox.get(pos).getMsg_text());
			img_inbox_listicon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (!list_inbox.get(pos).getSender().equals(Conf.userID))// 不是查看自己的资料
					{
						if (BasicUtils.isFastDoubleClick()) {
							return;
						}
						StatService.onEvent(context, "message-icon",
								"eventLabel", 1);
						Intent intent = new Intent(context,
								FragmentToActivity.class);
						intent.putExtra("who", "personal");
						intent.putExtra("user_id", list_inbox.get(pos)
								.getSender());
						intent.putExtra("distance", "");
						context.startActivity(intent);
					}
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public int getSwipeLayoutResourceId(int arg0) {
		// TODO Auto-generated method stub
		return R.id.swipe;
	}
}

package com.jimome.mm.adapter;

import java.util.List;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.unjiaoyou.mm.R;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.utils.ImageLoadUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class VisitorAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list;

	public VisitorAdapter(Context context, List<BaseJson> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	public void insertData(BaseJson json) {
		list.add(0, json);
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return list.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public View getView(final int pos, View view, ViewGroup viewGrop) {
		// TODO Auto-generated method stub
		try {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.grid_item_imgshow, viewGrop, false);
			}

			BaseJson sender = list.get(pos);
			final ImageView img_show_listicon = BaseAdapterHelper.get(view,
					R.id.img_show_listicon);
			TextView tv_show_listtxt = BaseAdapterHelper.get(view,
					R.id.tv_show_listtxt);
			ImageView img_show_listnew = BaseAdapterHelper.get(view,
					R.id.img_show_listnew);

			if (sender.getName() == null || sender.getName().trim().equals("")) {
				tv_show_listtxt.setVisibility(View.GONE);
			} else {
				tv_show_listtxt.setText(sender.getName());
				tv_show_listtxt.setVisibility(View.VISIBLE);
			}

			ImageLoadUtils.imageLoader.displayImage(sender.getImg(), img_show_listicon,
					ImageLoadUtils.options, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
							img_show_listicon
									.setImageResource(R.drawable.default_female);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							img_show_listicon
									.setImageResource(R.drawable.default_female);
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

			img_show_listnew.setVisibility(View.GONE);// 查看过后没有该属性

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return view;
	}

}

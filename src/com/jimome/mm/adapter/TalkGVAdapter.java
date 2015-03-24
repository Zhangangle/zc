package com.jimome.mm.adapter;

import com.unjiaoyou.mm.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TalkGVAdapter extends BaseAdapter {

	private Context context;
	private int[] icon = { R.drawable.btn_photo_selector,
			R.drawable.btn_camero_selector, R.drawable.btn_gift_selector,
			R.drawable.btn_break_selector};//, R.drawable.btn_selfshow_selector 
	private String[] title = { "照片", "专属秀", "礼物", "搭讪助手" };//, "自选秀"
	public TalkGVAdapter(Context context) {
		this.context = context;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return icon.length;
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return icon[pos];
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

			ImageView img_show_listicon = BaseAdapterHelper.get(view,
					R.id.img_show_listicon);
			ImageView img_show_listnew = BaseAdapterHelper.get(view,
					R.id.img_show_listnew);
			TextView tv_show_listtxt = BaseAdapterHelper.get(view,
					R.id.tv_show_listtxt);
			img_show_listicon.setImageResource(icon[pos]);
			tv_show_listtxt.setText(title[pos]);
			img_show_listnew.setVisibility(View.GONE);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return view;
	}

}

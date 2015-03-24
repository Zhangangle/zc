package com.jimome.mm.adapter;

import java.util.List;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.jimome.mm.bean.BaseJson;

import com.jimome.mm.utils.ImageLoadUtils;

import com.unjiaoyou.mm.R;

/**
 * 推广app适配
 * 
 * @author Administrator
 * 
 */
public class PopAppAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_app;

	public PopAppAdapter(Context context, List<BaseJson> list_app) {
		this.context = context;
		this.list_app = list_app;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_app.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return list_app.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	public void removeDate(int pos) {
		list_app.remove(pos);
	}

	public void insertData(List<BaseJson> list) {
		list_app.addAll(list);
	}

	public List<BaseJson> allData() {
		return list_app;
	}

	@Override
	public View getView(final int pos, View view, ViewGroup viewGrop) {
		// TODO Auto-generated method stub
		try {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.list_item_popapp, viewGrop, false);
			}

			TextView tv_find_listname = BaseAdapterHelper.get(view,
					R.id.tv_find_listname);
			ImageView img_find_listicon = BaseAdapterHelper.get(view,
					R.id.img_find_listicon);
			tv_find_listname.setText(list_app.get(pos).getName());

			// 加载网络图片
			ImageLoadUtils.imageLoader.displayImage(
					list_app.get(pos).getIcon(), img_find_listicon,
					ImageLoadUtils.options);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return view;
	}
}

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

import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.utils.ImageLoadUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.unjiaoyou.mm.R;

/**
 * 广告app适配
 * 
 * @author Administrator
 * 
 */
public class ADappAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_app;

	public ADappAdapter(Context context, List<BaseJson> list_app) {
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

	public void removeData(int pos) {
		list_app.remove(pos);
	}

	public void insertData(List<BaseJson> list) {
		list_app.addAll(list);
	}

	public List<BaseJson> allData() {
		return list_app;
	}

	@Override
	public View getView(int pos, View view, ViewGroup viewGrop) {
		// TODO Auto-generated method stub
		try {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.grid_item_adapp, viewGrop, false);
			}

			final ImageView img_icon = BaseAdapterHelper.get(view,
					R.id.griditem_pic);
			TextView tv_name = BaseAdapterHelper.get(view, R.id.griditem_title);// 名称
			ImageLoadUtils.imageLoader.displayImage(
					list_app.get(pos).getIcon(), img_icon,
					ImageLoadUtils.options, new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
							img_icon.setImageResource(R.drawable.default_female);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							img_icon.setImageResource(R.drawable.default_female);
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
			tv_name.setText(list_app.get(pos).getName());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return view;
	}
}

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

import com.jimome.mm.common.Conf;
import com.jimome.mm.utils.ImageLoadUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class FateAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_person;

	public FateAdapter(Context context, List<BaseJson> list_person) {
		this.context = context;
		this.list_person = list_person;
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

	@Override
	public View getView(final int pos, View view, ViewGroup viewGrop) {
		// TODO Auto-generated method stub
		try {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.grid_item_fate, viewGrop, false);
			}

			final ImageView img_fate_listicon = BaseAdapterHelper.get(view,
					R.id.img_fate_listicon);
			final ImageView img_fate_listbless = BaseAdapterHelper.get(view,
					R.id.img_fate_listbless);
			TextView tv_fate_listage = BaseAdapterHelper.get(view,
					R.id.tv_fate_listage);
			TextView tv_fate_location = BaseAdapterHelper.get(view,
					R.id.tv_fate_location);
			tv_fate_location.setText(Conf.city+" "+list_person.get(pos).getDistance()+"km");
			ImageLoadUtils.imageLoader.displayImage(list_person.get(pos).getIcon(),
					img_fate_listicon, ImageLoadUtils.options, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub

							if (Conf.gender.equals("1"))
								img_fate_listicon
										.setImageResource(R.drawable.default_female);
							else
								img_fate_listicon
										.setImageResource(R.drawable.default_male);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("1"))
								img_fate_listicon
										.setImageResource(R.drawable.default_female);
							else
								img_fate_listicon
										.setImageResource(R.drawable.default_male);
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
			tv_fate_listage.setText(list_person.get(pos).getAge()
					+ context.getResources().getString(R.string.str_sui));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return view;
	}

}

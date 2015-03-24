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

/**
 * 显示礼物商城适配
 * 
 * @author Administrator
 * 
 */
public class GiftStoreGridAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_gift;

	public GiftStoreGridAdapter(Context context, List<BaseJson> list_gift) {
		this.context = context;
		this.list_gift = list_gift;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_gift.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return list_gift.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	public void insertData(List<BaseJson> list) {
		list_gift.addAll(list);
	}

	public List<BaseJson> allDate() {
		return list_gift;
	}

	@Override
	public View getView(final int pos, View view, ViewGroup viewGrop) {
		// TODO Auto-generated method stub
		try {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.grid_item_giftstore, viewGrop, false);
			}

			final ImageView img_giftstore_gridicon = BaseAdapterHelper.get(
					view, R.id.img_giftstore_gridicon);
			TextView tv_giftstore_gridprise = BaseAdapterHelper.get(view,
					R.id.tv_giftstore_gridprise);
			TextView tv_giftstore_gridname = BaseAdapterHelper.get(view,
					R.id.tv_giftstore_gridname);
			TextView tv_giftstore_gridmeili = BaseAdapterHelper.get(view,
					R.id.tv_giftstore_gridmeili);

			ImageLoadUtils.imageLoader.displayImage(list_gift.get(pos).getImg(),
					img_giftstore_gridicon, ImageLoadUtils.options,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("1"))
								img_giftstore_gridicon
										.setImageResource(R.drawable.default_female);
							else
								img_giftstore_gridicon
										.setImageResource(R.drawable.default_male);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("1"))
								img_giftstore_gridicon
										.setImageResource(R.drawable.default_female);
							else
								img_giftstore_gridicon
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

			tv_giftstore_gridprise.setText(list_gift.get(pos).getCoin());
			tv_giftstore_gridname.setText(list_gift.get(pos).getName());
			tv_giftstore_gridmeili.setText(context
					.getString(R.string.str_meili)
					+ "+"
					+ list_gift.get(pos).getCharm());

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return view;
	}
}

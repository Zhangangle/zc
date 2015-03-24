package com.jimome.mm.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.unjiaoyou.mm.R;
import com.jimome.mm.activity.GiftStoreActivity;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.utils.ImageLoadUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 显示 礼物Grid适配
 * 
 * @author Administrator
 * 
 */
public class GiftGridAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_gift;
	private BaseJson person;

	public GiftGridAdapter(Context context, List<BaseJson> list_gift,
			BaseJson person) {
		this.context = context;
		this.list_gift = list_gift;
		this.person = person;
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
						R.layout.grid_item_gift, viewGrop, false);
			}

			final ImageView img_gift_gridicon = BaseAdapterHelper.get(view,
					R.id.img_gift_gridicon);
			TextView tv_gift_gridnum = BaseAdapterHelper.get(view,
					R.id.tv_gift_gridnum);
			TextView tv_gift_gridname = BaseAdapterHelper.get(view,
					R.id.tv_gift_gridname);

			ImageLoadUtils.imageLoader.displayImage(list_gift.get(pos).getImg(),
					img_gift_gridicon, ImageLoadUtils.options, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("1"))
								img_gift_gridicon
										.setImageResource(R.drawable.default_female);
							else
								img_gift_gridicon
										.setImageResource(R.drawable.default_male);

						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("1"))
								img_gift_gridicon
										.setImageResource(R.drawable.default_female);
							else
								img_gift_gridicon
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

			tv_gift_gridnum.setText(list_gift.get(pos).getNum());
			tv_gift_gridname.setText(list_gift.get(pos).getName());
			img_gift_gridicon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (!person.getUser_id().equals(Conf.userID))// 不是查看自己的资料
					{
						Intent intent = new Intent(context,
								GiftStoreActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("person", person);
						intent.putExtras(bundle);
						context.startActivity(intent);
					}

				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return view;
	}
}

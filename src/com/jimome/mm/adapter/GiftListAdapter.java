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
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.unjiaoyou.mm.R;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.activity.GiftStoreActivity;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ImageLoadUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 显示 礼物List适配
 * 
 * @author Administrator
 * 
 */
public class GiftListAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_person;
	private BaseJson person;

	public GiftListAdapter(Context context, List<BaseJson> list_person,
			BaseJson person) {
		this.context = context;
		this.list_person = list_person;
		this.person = person;
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
						R.layout.list_item_gift, viewGrop, false);
			}

			final ImageView img_gift_listicon = BaseAdapterHelper.get(view,
					R.id.img_gift_listicon);
			final ImageView img_gift_listpic = BaseAdapterHelper.get(view,
					R.id.img_gift_listpic);
			TextView tv_gift_listname = BaseAdapterHelper.get(view,
					R.id.tv_gift_listname);
			TextView tv_gift_listmsg = BaseAdapterHelper.get(view,
					R.id.tv_gift_listmsg);
			TextView tv_gift_listtime = BaseAdapterHelper.get(view,
					R.id.tv_gift_listtime);
			//
			float scale = (float) (0.4 * Conf.densityDPI);
			LayoutParams params = new LayoutParams((int) scale, (int) scale);
			img_gift_listicon.setLayoutParams(params);

			float gifsacle = (float) (0.4 * Conf.densityDPI);
			img_gift_listpic.setLayoutParams(new LayoutParams((int) gifsacle,
					(int) gifsacle));

			ImageLoadUtils.imageLoader.displayImage(list_person.get(pos).getIcon(),
					img_gift_listicon, ImageLoadUtils.optionsRounded, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("2"))
								img_gift_listicon
										.setBackgroundResource(R.drawable.default_female);
							else
								img_gift_listicon
										.setBackgroundResource(R.drawable.default_male);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("2"))
								img_gift_listicon
										.setBackgroundResource(R.drawable.default_female);
							else
								img_gift_listicon
										.setBackgroundResource(R.drawable.default_male);
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
			ImageLoadUtils.imageLoader.displayImage(list_person.get(pos).getImg(),
					img_gift_listpic, ImageLoadUtils.optionsRounded, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							img_gift_listpic
									.setBackgroundResource(R.drawable.logo);
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
			tv_gift_listname.setText(list_person.get(pos).getNick());
			tv_gift_listmsg.setText(list_person.get(pos).getName());
			tv_gift_listtime.setText(list_person.get(pos).getTime());

			img_gift_listicon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (person.getUser_id().equals(Conf.userID))// 不是查看自己的资料
					{
						if (!list_person.get(pos).getUser_id()
								.equals(Conf.userID))// 不是查看自己的资料
						{
							if (BasicUtils.isFastDoubleClick()) {
								return;
							}
							Intent intent = new Intent(context,
									FragmentToActivity.class);
							intent.putExtra("who", "personal");
							intent.putExtra("user_id", list_person.get(pos)
									.getUser_id());
							intent.putExtra("distance", "");
							context.startActivity(intent);
						}
					}
				}
			});
			img_gift_listpic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (!person.getUser_id().equals(Conf.userID))// 不是查看自己的资料
					{
						if (BasicUtils.isFastDoubleClick()) {
							return;
						}
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

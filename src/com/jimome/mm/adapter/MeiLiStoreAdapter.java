package com.jimome.mm.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unjiaoyou.mm.R;
import com.jimome.mm.activity.ExchangeActivity;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ImageLoadUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 魅力商城适配
 * 
 * @author Administrator
 * 
 */
public class MeiLiStoreAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_goods;

	public MeiLiStoreAdapter(Context context, List<BaseJson> list_goods) {
		this.context = context;
		this.list_goods = list_goods;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_goods.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return list_goods.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	public void insertData(List<BaseJson> list) {
		list_goods.addAll(list);
	}

	public List<BaseJson> allDate() {
		return list_goods;
	}

	@Override
	public View getView(final int pos, View view, ViewGroup viewGrop) {
		// TODO Auto-generated method stub
		try {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.list_item_meili, viewGrop, false);
			}

			final ImageView img_meili_listicon = BaseAdapterHelper.get(view,
					R.id.img_meili_listicon);
			TextView tv_gift_listname = BaseAdapterHelper.get(view,
					R.id.tv_gift_listname);
			TextView tv_meili_listcoin = BaseAdapterHelper.get(view,
					R.id.tv_meili_listcoin);
			tv_meili_listcoin.getPaint().setFlags(
					Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);// 中间划线
			TextView tv_meili_listprise = BaseAdapterHelper.get(view,
					R.id.tv_meili_listprise);
			Button btn_meili_listexchange = BaseAdapterHelper.get(view,
					R.id.btn_meili_listexchange);

			tv_gift_listname.setText(list_goods.get(pos).getName());
			tv_meili_listcoin.setText("￥" + list_goods.get(pos).getRmb());
			tv_meili_listprise.setText(list_goods.get(pos).getCharm()
					+ context.getString(R.string.str_meili));
			float scale = (float) (0.45 * Conf.densityDPI);
			img_meili_listicon.setLayoutParams(new LinearLayout.LayoutParams(
					(int) scale, (int) scale));
			ImageLoadUtils.imageLoader.displayImage(list_goods.get(pos).getImg(),
					img_meili_listicon, ImageLoadUtils.options, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("1"))
								img_meili_listicon
										.setImageResource(R.drawable.default_male);
							else
								img_meili_listicon
										.setImageResource(R.drawable.default_female);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("1"))
								img_meili_listicon
										.setImageResource(R.drawable.default_male);
							else
								img_meili_listicon
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
			btn_meili_listexchange.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (BasicUtils.isFastDoubleClick()) {
						return;
					}
					Intent intent = new Intent(context, ExchangeActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("goods", list_goods.get(pos));
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return view;
	}

}

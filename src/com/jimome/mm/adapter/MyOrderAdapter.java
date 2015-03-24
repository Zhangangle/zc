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
 * 我的订单适配器
 * 
 * @author Administrator
 * 
 */
public class MyOrderAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_order;
	private int kinds;

	public MyOrderAdapter(Context context, List<BaseJson> list_order, int kinds) {
		this.context = context;
		this.list_order = list_order;
		this.kinds = kinds;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_order.size();
	}

	public void insertData(List<BaseJson> list) {
		list_order.addAll(list);
	}

	public List<BaseJson> getAllData() {
		return list_order;
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return list_order.get(pos);
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
						R.layout.list_item_onebuy_on, viewGrop, false);
			}
			ImageView img_onebuy_listicon = BaseAdapterHelper.get(view,
					R.id.img_onebuy_listicon);
			TextView tv_onebuy_listname = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listname);
			TextView tv_onebuy_listsurplusnums = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listsurplusnums);
			TextView tv_onebuy_listcounts = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listcounts);
			TextView tv_onebuy_listprice = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listprice);
			TextView tv_onebuy_listlucknums = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listlucknums);// 幸运号码
			TextView tv_onebuy_listperiod = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listperiod);
			if (kinds == 1) {
				tv_onebuy_listsurplusnums.setText(context
						.getString(R.string.str_onebuy_list_surplus)
						+ list_order.get(pos).getRemain_nums()
						+ context.getString(R.string.str_onebuy_man));
				tv_onebuy_listlucknums.setVisibility(View.GONE);
			} else if (kinds == 2) {
				tv_onebuy_listsurplusnums.setText(context
						.getString(R.string.str_onebuy_win)
						+ list_order.get(pos).getOwner_name());
				tv_onebuy_listlucknums.setVisibility(View.VISIBLE);
				tv_onebuy_listsurplusnums.setTextColor(context.getResources()
						.getColor(R.color.darkorange));
				tv_onebuy_listlucknums.setText(context
						.getString(R.string.str_onebuy_buynum)
						+ list_order.get(pos).getLucky_code());
			} else {
				tv_onebuy_listsurplusnums.setText("");
				tv_onebuy_listlucknums.setVisibility(View.VISIBLE);
				tv_onebuy_listlucknums.setTextColor(context.getResources()
						.getColor(R.color.darkorange));
				tv_onebuy_listlucknums.setText(context
						.getString(R.string.str_onebuy_buynum)
						+ list_order.get(pos).getLucky_code());
			}
			ImageLoadUtils.imageLoader.displayImage(list_order.get(pos)
					.getIcon(), img_onebuy_listicon, ImageLoadUtils.options);
			tv_onebuy_listname.setText(list_order.get(pos).getName());
			tv_onebuy_listcounts.setText(list_order.get(pos).getNums() + "");
			tv_onebuy_listprice.setText(context.getResources().getString(
					R.string.str_onebuy_priceunit)
					+ list_order.get(pos).getPrice());
			tv_onebuy_listperiod.setText(list_order.get(pos).getPeriod());

		} catch (Exception e) {
			// TODO: handle exception
		}
		return view;
	}

}

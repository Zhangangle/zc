package com.jimome.mm.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jimome.mm.bean.BaseJson;
import com.unjiaoyou.mm.R;

/**
 * 选择期次适配
 * 
 * @author Administrator
 * 
 */
public class OneBuySelectTimeAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_one;

	public OneBuySelectTimeAdapter(Context context, List<BaseJson> list_one) {
		this.context = context;
		this.list_one = list_one;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_one.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return list_one.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	public void removeDate(int pos) {
		list_one.remove(pos);
	}

	public void insertData(List<BaseJson> list) {
		list_one.addAll(list);
	}

	public List<BaseJson> allData() {
		return list_one;
	}

	@Override
	public View getView(int pos, View view, ViewGroup viewGrop) {
		// TODO Auto-generated method stub
		try {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.grid_item_selecttime, viewGrop, false);
			}

			// TextView tv_task_listnature = BaseAdapterHelper.get(view,
			// R.id.tv_task_listnature);
			TextView tv_onebuy_listselect = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listselect);// 名称
			tv_onebuy_listselect.setText(list_one.get(pos).getText());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return view;
	}
}

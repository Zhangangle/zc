package com.jimome.mm.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unjiaoyou.mm.R;
import com.jimome.mm.bean.BaseJson;

public class InboxNoticeAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_system;

	public InboxNoticeAdapter(Context context, List<BaseJson> list_system) {
		this.context = context;
		this.list_system = list_system;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_system.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return list_system.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	// 添加系统消息
	public void insertData(List<BaseJson> list) {
		list_system.addAll(list);
	}

	@Override
	public View getView(final int pos, View view, ViewGroup viewGrop) {
		// TODO Auto-generated method stub
		try {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.list_item_inbox_notice, viewGrop, false);

			}
			TextView tv_notice_listmsg = BaseAdapterHelper.get(view,
					R.id.tv_notice_listmsg);
			TextView tv_notice_listname = BaseAdapterHelper.get(view,
					R.id.tv_notice_listname);
			TextView tv_notice_listtime = BaseAdapterHelper.get(view,
					R.id.tv_notice_listtime);
			tv_notice_listname.setText(list_system.get(pos).getName());
			tv_notice_listtime.setText(list_system.get(pos).getTime());
			tv_notice_listmsg.setText(list_system.get(pos).getText());

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return view;
	}

}

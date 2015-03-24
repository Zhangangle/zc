package com.jimome.mm.adapter;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unjiaoyou.mm.R;

public class TalkHelperAdapter extends BaseAdapter {

	private Context context;
	private String[] texts;

	public TalkHelperAdapter(Context context, String[] texts) {
		this.context = context;
		this.texts = texts;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return texts.length;
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return texts[pos];
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
						R.layout.list_item_talkhelper, viewGrop, false);
			}

			TextView tv_helper_text = BaseAdapterHelper.get(view,
					R.id.tv_helper_text);
			TextView tv_helper_pos = BaseAdapterHelper.get(view,
					R.id.tv_helper_pos);
			tv_helper_text.setText(texts[pos]);
			tv_helper_pos.setText((pos + 1) + "");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return view;
	}

}

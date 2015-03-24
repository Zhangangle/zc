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
 * 最近访客adapter
 * 
 * @author admin
 * 
 */
public class LastvisitorAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_visitor;

	public LastvisitorAdapter(Context c, List<BaseJson> list_visitor) {
		context = c;
		this.list_visitor = list_visitor;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_visitor.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return list_visitor.get(pos);
	}

	public void insertData(List<BaseJson> list) {
		list_visitor.addAll(list);
	}

	public List<BaseJson> allDate() {
		return list_visitor;
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public View getView(int pos, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		try {
			if (arg1 == null) {
				arg1 = LayoutInflater.from(context).inflate(
						R.layout.list_item_lastvisitor, arg2, false);
			}

			final ImageView avatar = BaseAdapterHelper.get(arg1,
					R.id.img_visit_avatar);
			TextView nick = BaseAdapterHelper.get(arg1, R.id.tv_visit_nick);
			TextView time = BaseAdapterHelper.get(arg1, R.id.tv_visit_time);
			nick.setText(list_visitor.get(pos).getNick());
			time.setText(list_visitor.get(pos).getTime());
			ImageLoadUtils.imageLoader.displayImage(list_visitor.get(pos).getIcon(), avatar,
					ImageLoadUtils.options, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub

							if (Conf.gender.equals("1"))
								avatar.setImageResource(R.drawable.default_female);
							else
								avatar.setImageResource(R.drawable.default_male);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("1"))
								avatar.setImageResource(R.drawable.default_female);
							else
								avatar.setImageResource(R.drawable.default_male);
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

		} catch (Exception e) {
			// TODO: handle exception
		}
		return arg1;
	}

}

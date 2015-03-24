package com.jimome.mm.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.LogUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.unjiaoyou.mm.R;

/**
 * 所有云购记录适配
 * 
 * @author Administrator
 * 
 */
public class OneBuyRecordAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_one;
	private String count;

	public OneBuyRecordAdapter(Context context, List<BaseJson> list_one) {
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

	public List<BaseJson> allDate() {
		return list_one;
	}

	@Override
	public View getView(final int pos, View view, ViewGroup viewGrop) {
		// TODO Auto-generated method stub
		try {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.list_item_onebuy_record, viewGrop, false);
			}

			TextView tv_onebuy_listname = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listname);// 名称
			TextView tv_onebuy_listaddress = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listaddress);// 地址
			TextView tv_onebuy_listcount = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listcount);// 次数
			TextView tv_onebuy_listtime = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listtime);// 云购时间
			count = context.getString(R.string.str_onebuy_recordnum, list_one
					.get(pos).getNums());
			tv_onebuy_listcount.setText(Html.fromHtml(count));
			final ImageView img_onebuy_listicon = BaseAdapterHelper.get(view,
					R.id.img_onebuy_listicon);// 图片
			ImageLoadUtils.imageLoader.displayImage(
					list_one.get(pos).getIcon()// "http://img.2264.com/gdy.png"
					, img_onebuy_listicon, ImageLoadUtils.options,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub

							img_onebuy_listicon
									.setImageResource(R.drawable.default_male);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							img_onebuy_listicon
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
			tv_onebuy_listname.setText(list_one.get(pos).getName());
			if (list_one.get(pos).getAddress() != null
					&& list_one.get(pos).getAddress().trim().length() > 0) {

				tv_onebuy_listaddress.setText("("
						+ list_one.get(pos).getAddress() + ")");
			} else
				tv_onebuy_listaddress.setText("");
			tv_onebuy_listtime.setText(list_one.get(pos).getTime());
		} catch (Exception e) {
			// TODO: handle exception
			LogUtils.printLogE("异常", e.toString());
		}
		return view;
	}

}

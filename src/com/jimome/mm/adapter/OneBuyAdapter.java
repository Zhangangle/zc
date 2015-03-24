package com.jimome.mm.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.database.CartDAO;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.LogUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.unjiaoyou.mm.R;

/**
 * 所有商品（一元云购）适配
 * 
 * @author Administrator
 * 
 */
public class OneBuyAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_one;

	public OneBuyAdapter(Context context, List<BaseJson> list_one) {
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
						R.layout.list_item_onebuy_main, viewGrop, false);
			}

			// TextView tv_task_listnature = BaseAdapterHelper.get(view,
			// R.id.tv_task_listnature);
			TextView tv_onebuy_listname = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listname);// 名称
			TextView tv_onebuy_listprice = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listprice);// 价格
			TextView tv_onebuy_listjoinnum = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listjoinnum);// 参与人数
			TextView tv_onebuy_listcount = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listcount);// 总需人次
			ProgressBar progb_onebuy_list = BaseAdapterHelper.get(view,
					R.id.progb_onebuy_list);// 进度条
			TextView str_onebuy_listsurplus = BaseAdapterHelper.get(view,
					R.id.str_onebuy_listsurplus);// 剩余
			Button btn_onebuy_listcart = BaseAdapterHelper.get(view,
					R.id.btn_onebuy_listcart);// 加入购物车
			final ImageView img_onebuy_listicon = BaseAdapterHelper.get(view,
					R.id.img_onebuy_listicon);// 图片
			tv_onebuy_listname.setText(list_one.get(pos).getName());
			tv_onebuy_listprice.setText(context.getResources().getString(
					R.string.str_onebuy_priceunit)
					+ list_one.get(pos).getPrice());
			tv_onebuy_listjoinnum.setText(list_one.get(pos).getJoin_nums());
			tv_onebuy_listcount.setText(list_one.get(pos).getTotal_nums());
			str_onebuy_listsurplus.setText(list_one.get(pos).getRemain_nums());
			progb_onebuy_list.setProgress(Integer.valueOf(list_one.get(pos)
					.getJoin_nums())
					* 100
					/ Integer.valueOf(list_one.get(pos).getTotal_nums()));
			ImageLoadUtils.imageLoader.displayImage(
					list_one.get(pos).getIcon()// "http://img.2264.com/gdy.png"
					, img_onebuy_listicon, ImageLoadUtils.options,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub

							if (Conf.gender.equals("1"))
								img_onebuy_listicon
										.setImageResource(R.drawable.default_female);
							else
								img_onebuy_listicon
										.setImageResource(R.drawable.default_male);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("1"))
								img_onebuy_listicon
										.setImageResource(R.drawable.default_female);
							else
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
			btn_onebuy_listcart.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					// getHttpData(list_one.get(pos).getid());
					// 添加到购物车
					CartDAO cartD = new CartDAO(context);
					cartD.addCart(list_one.get(pos).getId(), list_one.get(pos)
							.getName(), list_one.get(pos).getIcon(), 1, Integer
							.valueOf(list_one.get(pos).getRemain_nums()));
					Toast.makeText(context,
							context.getString(R.string.str_onebuy_setcart),
							Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			LogUtils.printLogE("异常", e.toString());
		}
		return view;
	}

	private void getHttpData(String id) {
		RequestParams params = new RequestParams();
		String key = "luckbuy/cart/update";
		try {
			params.addQueryStringParameter("user_id", Conf.userID);
			params.addQueryStringParameter("id", id);
			params.addQueryStringParameter("action", "a");
			params.addQueryStringParameter("num", "1");
			params.addHeader("Authorization",
					PreferenceHelper.readString(context, "auth", "token"));
		} catch (Exception e) {
			// TODO: handle exception
		}

		CacheRequest.requestGET(context, key, params, key, 0,
				new CacheRequestCallBack() {

					@Override
					public void onData(String json) {
						// TODO Auto-generated method stub
						try {
							Log.e("oneBuyAdapter", json);
							BaseJson baseJson = new Gson().fromJson(json,
									BaseJson.class);
							if (baseJson.getStatus().equals("200")) {
								Toast.makeText(
										context,
										context.getString(R.string.str_onebuy_setcart),
										Toast.LENGTH_SHORT).show();
							} else if (baseJson.getStatus().equals("201")) {
								Toast.makeText(
										context,
										context.getString(R.string.str_onebuy_setcarterror),
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(
										context,
										context.getString(R.string.str_net_register),
										Toast.LENGTH_SHORT).show();

							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub

						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
						Toast.makeText(context,
								context.getString(R.string.str_net_register),
								Toast.LENGTH_SHORT).show();
					}
				});
	}
}

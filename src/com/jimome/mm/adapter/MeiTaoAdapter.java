package com.jimome.mm.adapter;

import java.util.LinkedList;

import com.unjiaoyou.mm.R;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.BitmapUtils;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.LogUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MeiTaoAdapter extends BaseAdapter {

	private Context context;
	private LinkedList<BaseJson> list_person;
	private int type = 0;
	private String user_id = "";

	public MeiTaoAdapter(Context context, LinkedList<BaseJson> list_person,
			int type, String userid) {
		this.context = context;
		this.list_person = list_person;
		this.type = type;
		user_id = userid;
	}

	public MeiTaoAdapter(Context context, LinkedList<BaseJson> list_person) {
		this.context = context;
		this.list_person = list_person;

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

	public void insertData(LinkedList<BaseJson> list) {
		list_person.addAll(list);
	}

	public LinkedList<BaseJson> allDate() {
		return list_person;
	}

	@Override
	public View getView(final int pos, View view, ViewGroup viewGrop) {
		// TODO Auto-generated method stub
		try {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.grid_item_meitao, viewGrop, false);
			}
			final ImageView img_meitao_listicon = BaseAdapterHelper.get(view,
					R.id.img_meitao_listicon);
			ImageView img_meitao_video = BaseAdapterHelper.get(view,
					R.id.img_meitao_video);
			ImageView img_meitao_listnew = BaseAdapterHelper.get(view,
					R.id.img_meitao_listnew);
			ImageView img_meitao_listvip = BaseAdapterHelper.get(view,
					R.id.img_meitao_listvip);
			ImageView img_meitao_listrank = BaseAdapterHelper.get(view,
					R.id.img_meitao_listrank);
			TextView tv_meitao_listnum = BaseAdapterHelper.get(view,
					R.id.tv_meitao_listnum);
			TextView tv_meitao_listmsg = BaseAdapterHelper.get(view,
					R.id.tv_meitao_listmsg);
			TextView tv_meitao_listname = BaseAdapterHelper.get(view,
					R.id.tv_meitao_listname);
			ImageView img_meitao_listpraise = BaseAdapterHelper.get(view,
					R.id.img_meitao_listpraise);
			LinearLayout layout_location = BaseAdapterHelper.get(view,
					R.id.layout_location);
			TextView tv_meitao_location = BaseAdapterHelper.get(view,
					R.id.tv_meitao_location);
			// TextView tv_meitao_location = BaseAdapterHelper.get(view,
			// R.id.tv_meitao_location);
			// 非VIP显示布局
			LinearLayout layout_vip = BaseAdapterHelper.get(view,
					R.id.layout_vip);
			Button btn_buy_vip = BaseAdapterHelper.get(view, R.id.btn_buy_vip);
			switch (type) {
			case 0:
				img_meitao_listpraise.setVisibility(View.GONE);
				break;
			case 1:
			case 2:
			case 3:
				if (!user_id.equals(Conf.userID)) {
					img_meitao_listpraise.setVisibility(View.GONE);
					if (Conf.user_VIP.equals("0")) {
						if (pos > 2) {
							layout_vip.setVisibility(View.VISIBLE);

							btn_buy_vip
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method
											// stub
											if (BasicUtils.isFastDoubleClick()) {
												return;
											}
											Intent intent = new Intent(context,
													FragmentToActivity.class);
											intent.putExtra("who", "vip");
											context.startActivity(intent);
										}
									});
						} else {
							layout_vip.setVisibility(View.GONE);
						}
					}
				}
				break;
			case 4:
				img_meitao_listpraise.setVisibility(View.GONE);
				layout_vip.setVisibility(View.GONE);
				break;

			default:
				break;
			}
			BaseJson json = list_person.get(pos);
			String img = "";
			if (json.getImg() != null) {
				// 视频秀
				img = json.getImg();
				tv_meitao_listname.setVisibility(View.GONE);

				if (json.getVideo_url() == null && type != 4) {
					img_meitao_video.setVisibility(View.GONE);
				} else {
					img_meitao_video.setVisibility(View.VISIBLE);
				}
				tv_meitao_listnum.setVisibility(View.GONE);
				layout_location.setVisibility(View.GONE);
			} else {
				img = json.getUrl();
				tv_meitao_listnum.setVisibility(View.GONE);
				layout_location.setVisibility(View.VISIBLE);
			}
			img_meitao_listnew.setVisibility(View.VISIBLE);
			// tv_meitao_listnum.setText(json.getGreeted_nums());
			//
			// tv_meitao_listname.setText(json.getNick());

			if (json.getText().trim().equals(""))
				tv_meitao_listmsg.setVisibility(View.GONE);
			else {
				tv_meitao_listmsg.setText(json.getText());
				tv_meitao_listname.setVisibility(View.GONE);
				tv_meitao_listmsg.setVisibility(View.VISIBLE);
			}
			if (json.getNick() == null) {
				tv_meitao_listname.setVisibility(View.GONE);
			} else {
				if (json.getNick().trim().equals("")) {
					tv_meitao_listname.setVisibility(View.GONE);
				} else {
					tv_meitao_listname.setText(json.getNick());
					tv_meitao_listname.setVisibility(View.VISIBLE);
				}
			}
			tv_meitao_location.setText(Conf.city + " " + json.getDistance()
					+ "km");
			// if (type != 0) {
			// tv_meitao_listname.setText(json.getText());
			// tv_meitao_listname.setVisibility(View.VISIBLE);
			// tv_meitao_listmsg.setVisibility(View.GONE);
			// }
			// Log.e("图片地址---", img);
			if (type == 0) {
				tv_meitao_listname.setVisibility(View.VISIBLE);
				tv_meitao_listmsg.setVisibility(View.GONE);
			} else if (type == 3 || type == 4) {
				// 视频秀
				if (type == 4)
					tv_meitao_listmsg.setVisibility(View.GONE);
				float scaleHeigt = (float) (Conf.densityDPI);
				img_meitao_listicon
						.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
								android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
								(int) scaleHeigt));
			}

			ImageLoadUtils.imageLoader.displayImage(img, img_meitao_listicon,
					ImageLoadUtils.options, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("1"))
								img_meitao_listicon
										.setImageResource(R.drawable.default_female);
							else
								img_meitao_listicon
										.setImageResource(R.drawable.default_male);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							// TODO Auto-generated method stub
							if (Conf.gender.equals("1"))
								img_meitao_listicon
										.setImageResource(R.drawable.default_female);
							else
								img_meitao_listicon
										.setImageResource(R.drawable.default_male);
						}

						@Override
						public void onLoadingComplete(String arg0, View arg1,
								final Bitmap arg2) {
							if (!user_id.equals(Conf.userID)) {
								switch (type) {
								case 0:
									img_meitao_listicon.setImageBitmap(arg2);
									break;
								case 1:
								case 2:
								case 3:
									try {
										if (Conf.user_VIP.equals("0")) {
											if (pos > 2) {
												Bitmap newBmp = BitmapUtils
														.blur(arg2,
																img_meitao_listicon);
												img_meitao_listicon
														.setImageBitmap(newBmp);
											} else {
												img_meitao_listicon
														.setImageBitmap(arg2);
											}
										} else {
											img_meitao_listicon
													.setImageBitmap(arg2);
										}
									} catch (OutOfMemoryError e) {
										// TODO: handle exception
										LogUtils.printLogE("图片OOM异常", "---");
									}
									break;
								case 4:
									img_meitao_listicon.setImageBitmap(arg2);
									break;
								default:
									break;
								}
							}
							// TODO Auto-generated method stub
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							// TODO Auto-generated method stub
						}
					});

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return view;
	}

}

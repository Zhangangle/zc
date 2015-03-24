package com.jimome.mm.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.unjiaoyou.mm.R;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.activity.MyShowActivity;
import com.jimome.mm.activity.TalkActivity;
import com.jimome.mm.activity.ViewPageActivity;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.BitmapUtils;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.LogUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class TalkMSGAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_person;

	private String oppID;
	private List<BaseJson> list;// 查看图片实现
	private BaseJson base;// 查看图片实现
	private BaseJson data;
	
	public TalkMSGAdapter(Context context, List<BaseJson> list_person,
			String oppID) {
		this.context = context;
		this.list_person = list_person;
		this.oppID = oppID;
	}

	public void insert(BaseJson per) {
		this.list_person.add(per);
	}

	public void insertList(List<BaseJson> list) {
		this.list_person.addAll(list);
	}

	public void insertAgo(List<BaseJson> list) {
		this.list_person.addAll(0, list);
	}
	
	public void setData(BaseJson data){
		this.data  =data;
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

	@Override
	public View getView(int pos, View view, ViewGroup viewGrop) {
		// TODO Auto-generated method stub
		try {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.list_item_talk_mine, viewGrop, false);
			}
			ImageView icon = BaseAdapterHelper
					.get(view, R.id.img_talk_mineicon);
			TextView send_msg = BaseAdapterHelper.get(view,
					R.id.tv_talk_minemsg);
			TextView send_time = BaseAdapterHelper.get(view,
					R.id.tv_talk_minetime);
			ImageView oppicon = BaseAdapterHelper.get(view,
					R.id.img_talk_oppicon);
			LinearLayout layout = BaseAdapterHelper.get(view, R.id.layout_talk);
			LinearLayout layout_msg = BaseAdapterHelper.get(view,
					R.id.layout_msg);

			RelativeLayout relayout_video = BaseAdapterHelper.get(view,
					R.id.relayout_video);
			final ImageView img_play_video = BaseAdapterHelper.get(view,
					R.id.img_play_video);
			ImageView img_tip_video = BaseAdapterHelper.get(view,
					R.id.img_tip_video);
			TextView tv_talk_giftname = BaseAdapterHelper.get(view,
					R.id.tv_talk_giftname);
			TextView tv_talk_giftcoin = BaseAdapterHelper.get(view,
					R.id.tv_talk_giftcoin);
			TextView tv_talk_giftcharm = BaseAdapterHelper.get(view,
					R.id.tv_talk_giftcharm);
			LinearLayout layout_vip = BaseAdapterHelper.get(view,
					R.id.layout_vip);
			Button btn_buy_vip = BaseAdapterHelper.get(view, R.id.btn_buy_vip);
			LinearLayout layout_hint = BaseAdapterHelper.get(view,
					R.id.layout_hint);
			TextView tv_hint = BaseAdapterHelper.get(view,
					R.id.tv_hint);
			LinearLayout layout_shows = BaseAdapterHelper.get(view,
					R.id.layout_shows);
			ImageView img_show =  BaseAdapterHelper.get(view,
					R.id.img_show);
			ImageView img_photo =  BaseAdapterHelper.get(view,
					R.id.img_photo);
			ImageView img_video =  BaseAdapterHelper.get(view,
					R.id.img_video);
			
			layout_hint.getBackground().setAlpha(50);
			
			final BaseJson sender = list_person.get(pos);

			if (!sender.getSender().equals(Conf.userID)) {
				icon.setVisibility(View.INVISIBLE);
				oppicon.setVisibility(View.VISIBLE);
				layout.setGravity(Gravity.LEFT);
				send_msg.setTextColor(Color.DKGRAY);
				tv_talk_giftname.setTextColor(Color.DKGRAY);
				tv_talk_giftcharm.setTextColor(Color.BLUE);
				tv_talk_giftcoin.setTextColor(Color.BLUE);
				layout_msg.setBackgroundResource(R.drawable.chatmsg_left);
			} else {
				oppicon.setVisibility(View.INVISIBLE);
				icon.setVisibility(View.VISIBLE);
				layout.setGravity(Gravity.RIGHT);
				send_msg.setTextColor(Color.WHITE);
				tv_talk_giftname.setTextColor(Color.WHITE);
				tv_talk_giftcharm.setTextColor(Color.BLUE);
				tv_talk_giftcoin.setTextColor(Color.BLUE);
				layout_msg.setBackgroundResource(R.drawable.chatmsg_right);
			}

			if (sender.getSender().trim().equals(Conf.userID)) {
				if (Conf.userImg.equals("")) {
					if (Conf.gender.trim().equals("1"))
						icon.setImageResource(R.drawable.default_male);
					else
						icon.setImageResource(R.drawable.default_female);
				} else {
					ImageLoadUtils.imageLoader.displayImage(Conf.userImg, icon, ImageLoadUtils.options);
				}
			} else {
				if (sender.getSender_icon().trim().equals("")) {
					if (Conf.gender.trim().equals("1"))
						oppicon.setImageResource(R.drawable.default_female);
					else
						oppicon.setImageResource(R.drawable.default_male);
				} else {
					ImageLoadUtils.imageLoader.displayImage(sender.getSender_icon(), oppicon,
							ImageLoadUtils.options);
				}
			}
			if (sender.getFlag().equals("0")) {
				relayout_video.setVisibility(View.GONE);
				send_msg.setVisibility(View.VISIBLE);
				img_tip_video.setVisibility(View.GONE);
				img_play_video.setVisibility(View.GONE);
				tv_talk_giftname.setVisibility(View.GONE);
				tv_talk_giftcoin.setVisibility(View.GONE);
				tv_talk_giftcharm.setVisibility(View.GONE);
				layout_vip.setVisibility(View.GONE);
				send_msg.setText(sender.getText());
			} else if (sender.getFlag().equals("1")) {
				relayout_video.setVisibility(View.VISIBLE);
				send_msg.setVisibility(View.GONE);
				img_tip_video.setVisibility(View.GONE);
				img_play_video.setVisibility(View.VISIBLE);
				tv_talk_giftname.setVisibility(View.GONE);
				tv_talk_giftcoin.setVisibility(View.GONE);
				tv_talk_giftcharm.setVisibility(View.GONE);
				layout_vip.setVisibility(View.GONE);
				ImageLoadUtils.imageLoader.displayImage(sender.getUrl(), img_play_video,
						ImageLoadUtils.options);
				img_play_video
						.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
								(int) (0.25 * Conf.width),
								(int) (0.25 * Conf.width)));
				img_play_video.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (sender.getFlag().equals("1")) {
							Intent intent = new Intent(context,
									ViewPageActivity.class);
							list = new ArrayList<BaseJson>();
							base = new BaseJson();
							base.setUrl(sender.getUrl());
							list.add(base);
							intent.putExtra("list_image", (Serializable) list);
							intent.putExtra("pos", 0);
							context.startActivity(intent);
						}
					}
				});
			} else if (sender.getFlag().equals("2")) {//
				relayout_video.setVisibility(View.VISIBLE);
				send_msg.setVisibility(View.GONE);
				img_tip_video.setVisibility(View.GONE);
				img_play_video.setVisibility(View.VISIBLE);
				tv_talk_giftname.setVisibility(View.VISIBLE);
				tv_talk_giftcoin.setVisibility(View.VISIBLE);
				tv_talk_giftcharm.setVisibility(View.VISIBLE);
				layout_vip.setVisibility(View.GONE);
				ImageLoadUtils.imageLoader.displayImage(sender.getImg(), img_play_video,
						ImageLoadUtils.options);

				img_play_video
						.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
								(int) (0.25 * Conf.width),
								(int) (0.25 * Conf.width)));
				tv_talk_giftname.setText(sender.getName());
				tv_talk_giftcoin.setText(context
						.getString(R.string.str_talk_coin) + sender.getCoin());
				tv_talk_giftcharm
						.setText(context.getString(R.string.str_talk_charm)
								+ sender.getCharm());
			} else if (sender.getFlag().equals("3")) {
				img_play_video
				.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
						(int) (0.25 * Conf.width),
						(int) (0.25 * Conf.width)));
				// 专属秀
				if (Conf.user_VIP.equals("0")
						&& !sender.getSender().equals(Conf.userID)) {
					// 非会员

					relayout_video.setVisibility(View.VISIBLE);
					layout_vip.setVisibility(View.VISIBLE);
					send_msg.setVisibility(View.GONE);
					img_tip_video.setVisibility(View.GONE);
					img_play_video.setVisibility(View.VISIBLE);
					tv_talk_giftname.setVisibility(View.GONE);
					tv_talk_giftcoin.setVisibility(View.GONE);
					tv_talk_giftcharm.setVisibility(View.GONE);
					String url = sender.getSmall_url();
					ImageLoadUtils.imageLoader.displayImage(url, img_play_video, ImageLoadUtils.options,new ImageLoadingListener() {
						
						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
							// TODO Auto-generated method stub
							try {
								Bitmap newBmp = BitmapUtils.blur(
										arg2,
										img_play_video);
								img_play_video
										.setImageBitmap(newBmp);
							} catch (OutOfMemoryError e) {
								// TODO: handle exception
								LogUtils.printLogE("图片OOM异常", "---");
							}
							
						}
						
						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							// TODO Auto-generated method stub
							
						}
					});
					btn_buy_vip.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
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
					relayout_video.setVisibility(View.VISIBLE);
					send_msg.setVisibility(View.GONE);
					img_tip_video.setVisibility(View.VISIBLE);
					img_play_video.setVisibility(View.VISIBLE);
					tv_talk_giftname.setVisibility(View.GONE);
					tv_talk_giftcoin.setVisibility(View.GONE);
					tv_talk_giftcharm.setVisibility(View.GONE);
					layout_vip.setVisibility(View.GONE);
					String url = sender.getSmall_url();
					ImageLoadUtils.imageLoader.displayImage(url, img_play_video, ImageLoadUtils.options);
					final String finalUrl = sender.getUrl();
					img_play_video.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (sender.getFlag().equals("3")) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								String type = "video/*";
								Uri uri = Uri.parse(finalUrl);
								intent.setDataAndType(uri, type);
								context.startActivity(intent);
							}
						}
					});
					relayout_video.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (sender.getFlag().equals("3")) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								String type = "video/*";
								Uri uri = Uri.parse(finalUrl);
								intent.setDataAndType(uri, type);
								context.startActivity(intent);
							}
						}
					});

				}
			}
			if (pos == 0) {
				send_time.setText(sender.getFormat_time().trim());
				send_time.setVisibility(View.VISIBLE);
				int width = (int) (0.33 * Conf.width);
				if(TalkActivity.isShowed){
					layout_shows.setVisibility(View.VISIBLE);
					TalkActivity.isShowed = false;
				}else{
					layout_shows.setVisibility(View.GONE);
				}
				
				img_video
						.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
								width, width));
				if(data.getVideo()!= null){
					if(data.getVideo().length > 0){
						ImageLoadUtils.imageLoader.displayImage(data.getVideo()[0], img_video, ImageLoadUtils.options);
					}
				}
				
				img_photo
						.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
								width, width));
				if(data.getPhoto()!= null){
					if(data.getPhoto().length > 0){
						ImageLoadUtils.imageLoader.displayImage(data.getPhoto()[0], img_photo, ImageLoadUtils.options);
					}
				}
				
				img_show
						.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(
								width, width));
				if(data.getShow()!= null){
					if(data.getShow().length > 0){
						ImageLoadUtils.imageLoader.displayImage(data.getShow()[0], img_show, ImageLoadUtils.options);
					}
				}
			
				img_photo.setOnClickListener(new ImgOnClick());
				img_video.setOnClickListener(new ImgOnClick());
				img_show.setOnClickListener(new ImgOnClick());
//				if(TalkActivity.can_send.equals("0")){
//					layout_hint.setVisibility(View.VISIBLE);
//					tv_hint.setText(TalkActivity.info_git);
//					tv_hint.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							Intent intent = new Intent(context, GiftStoreActivity.class);
//							Bundle bundle = new Bundle();
//							BaseJson personal = new BaseJson();
//							personal.setUser_id(TalkActivity.person.getSender());
//							personal.setNick(TalkActivity.person.getNick());
//							bundle.putSerializable("person", personal);
//							intent.putExtras(bundle);
//							context.startActivity(intent);
//						}
//					});
					
//				}else{
					layout_hint.setVisibility(View.GONE);
//				}
			} else {
				layout_shows.setVisibility(View.GONE);
				if ((Integer.valueOf(list_person.get(pos).getTime().trim()))
						- (Integer.valueOf(list_person.get(pos - 1).getTime()
								.trim())) > 60) {
					send_time.setVisibility(View.VISIBLE);
					send_time.setText(sender.getFormat_time().trim());
				} else {
					send_time.setVisibility(View.GONE);
				}
			}
			oppicon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					StatService.onEvent(context, "dialog-icon", "eventLabel", 1);
					Conf.cur_userID = oppID;
					Intent intent = new Intent(new Intent(context,
							FragmentToActivity.class));
					intent.putExtra("who", "personal");
					intent.putExtra("user_id", oppID);
					intent.putExtra("distance", "");
					context.startActivity(intent);
				}
			});
			icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					StatService.onEvent(context, "dialog-icon", "eventLabel", 1);
					Intent intent = new Intent(new Intent(context,
							FragmentToActivity.class));
					intent.putExtra("who", "mine");
					context.startActivity(intent);
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			try {

				e.printStackTrace();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return view;
	}
	
	class ImgOnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.img_show :
					Intent nvIntent = new Intent(context,
							MyShowActivity.class);
					nvIntent.putExtra("type", 1);
					nvIntent.putExtra("user_id", TalkActivity.person.getSender());
					context.startActivity(nvIntent);
					break;

				case R.id.img_photo :
					Intent shenIntent = new Intent(context,
							MyShowActivity.class);
					shenIntent.putExtra("type", 2);
					shenIntent.putExtra("user_id", TalkActivity.person.getSender());
					context.startActivity(shenIntent);
					break;

				case R.id.img_video :
					if (Conf.user_VIP.equals("0")) {
						final Dialog dialog = BasicUtils.showDialog(
								context, R.style.BasicDialog);
						dialog.setContentView(R.layout.dialog_rechargevip);
						dialog.setCanceledOnTouchOutside(true);
						((Button) dialog.findViewById(R.id.btn_dialog_cancle))
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								});
						((Button) dialog.findViewById(R.id.btn_dialog_sure))
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										dialog.dismiss();
										Intent intent = new Intent(
												context,
												FragmentToActivity.class);
										intent.putExtra("who", "vip");
										context.startActivity(intent);
									}
								});
						dialog.show();
					}else{
						Intent videoIntent = new Intent(context,
								MyShowActivity.class);
						videoIntent.putExtra("type", 3);
						videoIntent.putExtra("user_id", TalkActivity.person.getSender());
						context.startActivity(videoIntent);
					}
					break;

				default :
					break;
			}
		}
		
	}
	
}

package com.jimome.mm.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.activity.ViewPageActivity;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.bean.NewCart;
import com.jimome.mm.database.CartDAO;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.ImageLoadUtils;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.unjiaoyou.mm.R;

/**
 * 商品详细(一元云购)页面
 * 
 * @author admin
 * 
 */
public class OneBuyDetailFragment extends BaseFragment {

	@ViewInject(R.id.layout_onebuy_back)
	private LinearLayout layout_onebuy_back;
	@ViewInject(R.id.tv_onebuy_title)
	private TextView tv_onebuy_title;
	@ViewInject(R.id.layout_onebuy_time)
	private LinearLayout layout_onebuy_time;
	@ViewInject(R.id.tv_onbuy_time)
	private TextView tv_onbuy_time;
	@ViewInject(R.id.img_onebuy_listicon)
	private ImageView img_onebuy_listicon;
	@ViewInject(R.id.img_onebuy_big)
	private ImageView img_onebuy_big;
	@ViewInject(R.id.tv_onebuy_listname)
	// 商品名称
	private TextView tv_onebuy_listname;
	@ViewInject(R.id.tv_onebuy_listprice)
	// 商品价格
	private TextView tv_onebuy_listprice;
	@ViewInject(R.id.tv_onebuy_listjoinnum)
	// 参与人数
	private TextView tv_onebuy_listjoinnum;
	@ViewInject(R.id.tv_onebuy_listcount)
	// 总需人次
	private TextView tv_onebuy_listcount;
	@ViewInject(R.id.str_onebuy_listsurplus)
	// 剩余人次
	private TextView str_onebuy_listsurplus;
	@ViewInject(R.id.progb_onebuy_list)
	// 进度条
	private ProgressBar progb_onebuy_list;
	@ViewInject(R.id.layout_onebuy_detial)
	// 图文详细
	private LinearLayout layout_onebuy_detial;
	@ViewInject(R.id.layout_onebuy_record)
	// 所有云购记录
	private LinearLayout layout_onebuy_record;
	@ViewInject(R.id.layout_onebuy_lastman)
	// 上期获奖者
	private LinearLayout layout_onebuy_lastman;
	@ViewInject(R.id.img_onebuy_lasticon)
	// 上期获得者头像
	private ImageView img_onebuy_lasticon;
	@ViewInject(R.id.tv_onebuy_lastname)
	// 上期获得者名称
	private TextView tv_onebuy_lastname;
	@ViewInject(R.id.tv_onebuy_lastaddress)
	// 上期获得者地理位置
	private TextView tv_onebuy_lastaddress;
	@ViewInject(R.id.tv_onebuy_answertime)
	// 揭晓时间
	private TextView tv_onebuy_answertime;
	@ViewInject(R.id.tv_onebuy_buytime)
	// 云购时间
	private TextView tv_onebuy_buytime;
	@ViewInject(R.id.tv_onebuy_buynum)
	// 幸运云购码
	private TextView tv_onebuy_buynum;
	@ViewInject(R.id.btn_onebuy_buy)
	// 立即购买
	private Button btn_onebuy_buy;
	@ViewInject(R.id.btn_onebuy_joincart)
	// 加入购物车
	private Button btn_onebuy_joincart;
	@ViewInject(R.id.relayout_onebuy_cart)
	// 购物车
	private RelativeLayout relayout_onebuy_cart;
	@ViewInject(R.id.btn_onbuy_cartnum)
	// 购物车商品数量
	private TextView btn_onbuy_cartnum;
	@ViewInject(R.id.img_onebuy_joincart)
	// 购物车图标
	private ImageView img_onebuy_joincart;
	@ViewInject(R.id.tv_onebuy_last)
	// 上期获奖者
	private TextView tv_onebuy_last;
	private BaseJson baseJson;
	// private Dialog mDialog;
	private String goodnum;
	private String period;
	private Activity context;
	private Intent detailIntent;

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_onebuy_detail, container,
				false);
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		layout_onebuy_time.setVisibility(View.VISIBLE);
		layout_onebuy_back.setVisibility(View.VISIBLE);
		tv_onebuy_title.setText(R.string.str_commoditydetail);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("jimo.action.goodcart");
		context.registerReceiver(mRefreshBroadcastReceiver, intentFilter);
		waitDialog();
		if (period != null && period.trim().length() > 0)
			getHttpData("last");
		else
			getHttpData("period");
	}

	// 得到用户地址后启动的广播
	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				String action = intent.getAction();
				if (action.equals("jimo.action.goodcart")) {
					int num = intent.getExtras().getInt("num");
					btn_onbuy_cartnum.setText(num + "");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		if (getActivity() == null)
			context = activity;
		else
			context = getActivity();

	}

	private void waitDialog() {
		// mDialog = BasicUtils.showDialog(context, R.style.BasicDialog);
		// mDialog.setContentView(R.layout.dialog_wait);
		// mDialog.setCanceledOnTouchOutside(false);
		//
		// Animation anim = AnimationUtils.loadAnimation(context,
		// R.anim.dialog_prog);
		// LinearInterpolator lir = new LinearInterpolator();
		// anim.setInterpolator(lir);
		// mDialog.findViewById(R.id.img_dialog_progress).startAnimation(anim);
		// mDialog.setOnKeyListener(new OnKeyListener() {
		// @Override
		// public boolean onKey(DialogInterface dialog, int keyCode,
		// KeyEvent event) {
		// if (keyCode == KeyEvent.KEYCODE_BACK
		// && event.getAction() == KeyEvent.ACTION_DOWN) {
		// if (!context.isFinishing())
		// mDialog.dismiss();
		//
		// return false;
		// }
		// return false;
		// }
		// });
		mDialog.show();
	}

	public OneBuyDetailFragment() {

	}

	public OneBuyDetailFragment(String goodnum) {
		this.goodnum = goodnum;
	}

	public OneBuyDetailFragment(String goodnum, String period) {
		this.goodnum = goodnum;
		this.period = period;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@OnClick({ R.id.layout_onebuy_back, R.id.img_onebuy_listicon,
			R.id.img_onebuy_big, R.id.layout_onebuy_time,
			R.id.layout_onebuy_detial, R.id.layout_onebuy_record,
			R.id.img_onebuy_lasticon, R.id.layout_onebuy_lastman,
			R.id.btn_onebuy_buy, R.id.btn_onebuy_joincart,
			R.id.btn_onbuy_cartnum, R.id.img_onebuy_joincart,
			R.id.relayout_onebuy_cart })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.layout_onebuy_back:
			context.finish();
			break;
		case R.id.img_onebuy_listicon:// 查看大图片
		case R.id.img_onebuy_big:// 查看大图片
			if (baseJson != null && baseJson.getImages() != null
					&& baseJson.getImages().length > 0) {
				List<BaseJson> list_icon = new ArrayList<BaseJson>();
				BaseJson base_icon;
				int imgSize = baseJson.getImages().length;
				for (int i = 0; i < imgSize; i++) {
					base_icon = new BaseJson();
					base_icon.setUrl(baseJson.getImages()[i]);
					list_icon.add(base_icon);
				}
				detailIntent = new Intent(context, ViewPageActivity.class);
				detailIntent.putExtra("list_image", (Serializable) list_icon);
				detailIntent.putExtra("pos", 0);
				context.startActivity(detailIntent);
			}
			// "http://gi1.mlist.alicdn.com/bao/uploaded/i5/TB1HEx9FVXXXXXmXFXX2rPMFpXX_091610.jpg_b.jpg"
			break;
		case R.id.layout_onebuy_time:// 选择第几期
			if (period != null && period.trim().length() > 0)
				return;
			detailIntent = new Intent(context, FragmentToActivity.class);
			detailIntent.putExtra("who", "onebuy_selecttime");
			detailIntent.putExtra("goodnum", goodnum);
			context.startActivity(detailIntent);
			break;
		case R.id.layout_onebuy_detial:// 图文详细
			detailIntent = new Intent(context, FragmentToActivity.class);
			detailIntent.putExtra("who", "onebuy_picdetail");
			detailIntent.putExtra("goodnum", goodnum);
			context.startActivity(detailIntent);
			break;
		case R.id.layout_onebuy_record:// 所有云购记录
			detailIntent = new Intent(context, FragmentToActivity.class);
			detailIntent.putExtra("who", "onebuy_record");
			detailIntent.putExtra("goodnum", goodnum);
			context.startActivity(detailIntent);
			break;
		case R.id.img_onebuy_lasticon:// 上期获得者头像
			break;
		case R.id.layout_onebuy_lastman:// 上期获得者
			break;
		case R.id.btn_onebuy_buy:// 立即购买
			// getHttpData("buy");
			if(baseJson != null){
				CartDAO cartD = new CartDAO(context);
				NewCart cart = cartD.addCart(goodnum, baseJson.getName(),
						baseJson.getIcon(), 1,
						Integer.valueOf(baseJson.getRemain_nums()));
				if (cartD != null)
					btn_onbuy_cartnum.setText(cart.getCount() + "");
				detailIntent = new Intent(context, FragmentToActivity.class);
				detailIntent.putExtra("who", "onebuy_cart");
				context.startActivity(detailIntent);
			}
			break;
		case R.id.btn_onebuy_joincart:// 加入购物车
			// getHttpData("join");
			// 添加到购物车
			if(baseJson != null){
				CartDAO cart2D = new CartDAO(context);
				NewCart cart2 = cart2D.addCart(goodnum, baseJson.getName(),
						baseJson.getIcon(), 1,
						Integer.valueOf(baseJson.getRemain_nums()));
				Toast.makeText(context,
						StringUtils.getResourse(R.string.str_onebuy_setcart),
						Toast.LENGTH_SHORT).show();
				if (cart2 != null)
					btn_onbuy_cartnum.setText(cart2.getCount() + "");
			}
		
			break;
		case R.id.btn_onbuy_cartnum:
		case R.id.img_onebuy_joincart:
		case R.id.relayout_onebuy_cart:// 购物车
			detailIntent = new Intent(context, FragmentToActivity.class);
			detailIntent.putExtra("who", "onebuy_cart");
			context.startActivity(detailIntent);
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mDialog != null)
			mDialog.dismiss();
		if (mRefreshBroadcastReceiver != null)
			context.unregisterReceiver(mRefreshBroadcastReceiver);
	}

	public void onScroll(int scrollY) {

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(context);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(context);
	}

	private void setDetailView() {
		// TODO Auto-generated method stub
		try {
			if (period != null && period.trim().length() > 0) {
				tv_onebuy_last.setText(StringUtils.getResourse(R.string.str_onbuy_now));
				tv_onbuy_time.setText("第"+period+"期");
				// tv_onbuy_time.setText(baseJson.getPeriod());
				// 参与人数
				tv_onebuy_listjoinnum.setText(baseJson.getTotal_nums());
				// 总需人次
				tv_onebuy_listcount.setText(baseJson.getTotal_nums());
				// 剩余人次
				str_onebuy_listsurplus.setText("0");
				progb_onebuy_list.setProgress(100);
				// 本期获得者头像
				ImageLoadUtils.imageLoader.displayImage(
						baseJson.getOwner_icon(), img_onebuy_lasticon,
						ImageLoadUtils.options);
				;
				// 本期获得者名称
				tv_onebuy_lastname.setText(baseJson.getOwner_name());
				// 本期获得者地理位置
				tv_onebuy_lastaddress.setText(baseJson.getOwner_address());

				// 本期揭晓时间
				tv_onebuy_answertime.setText(baseJson.getPublish_time());
				// 云购时间
				tv_onebuy_buytime.setText(baseJson.getBuy_time());
				// 幸运云购码
				tv_onebuy_buynum.setText(baseJson.getLucky_code());

			} else {
				tv_onbuy_time.setText(baseJson.getPeriod());
				// 参与人数
				tv_onebuy_listjoinnum.setText(baseJson.getJoin_nums());
				// 总需人次
				tv_onebuy_listcount.setText(baseJson.getTotal_nums());
				// 剩余人次
				str_onebuy_listsurplus.setText(baseJson.getRemain_nums());
				progb_onebuy_list.setProgress(Integer.valueOf(baseJson
						.getJoin_nums())
						* 100
						/ Integer.valueOf(baseJson.getTotal_nums()));
				// 上期获得者头像
				ImageLoadUtils.imageLoader.displayImage(
						baseJson.getLast_icon(), img_onebuy_lasticon,
						ImageLoadUtils.options);
				;
				// 上期获得者名称
				tv_onebuy_lastname.setText(baseJson.getLast_name());
				// 上期获得者地理位置
				tv_onebuy_lastaddress.setText(baseJson.getLast_address());

				// 揭晓时间
				tv_onebuy_answertime.setText(baseJson.getLast_publish_time());
				// 云购时间
				tv_onebuy_buytime.setText(baseJson.getLast_buy_time());
				// 幸运云购码
				tv_onebuy_buynum.setText(baseJson.getLast_code());

			}
			ImageLoadUtils.imageLoader.displayImage(baseJson.getIcon(),
					img_onebuy_listicon, ImageLoadUtils.options);

			// 商品名称
			tv_onebuy_listname.setText(baseJson.getName());
			// 商品价格
			tv_onebuy_listprice.setText(StringUtils.getResourse(
					R.string.str_onebuy_priceunit)
					+ baseJson.getPrice());

			// 购物车商品数量
			CartDAO cartD = new CartDAO(context);
			NewCart cart = cartD.countCart();
			if (cart != null)
				btn_onbuy_cartnum.setText(cart.getCount() + "");

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void getHttpData(String kinds) {
		RequestParams params = new RequestParams();
		String key = "";
		try {
			params.addQueryStringParameter("good_id", goodnum);
			if (kinds.equals("period")) {
				key = "buy/good/detail";
			} else {
				key = "buy/good/history";
				params.addQueryStringParameter("period", period);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key, 0,
				new CacheRequestCallBack() {

					@Override
					public void onData(String json) {
						// TODO Auto-generated method stub
						try {
							baseJson = new Gson()
									.fromJson(json, BaseJson.class);
							if (baseJson.getStatus().equals("200")) {
								setDetailView();
							} else {

							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (mDialog != null) {
								mDialog.dismiss();
							}
						}

					}

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub

						if (mDialog != null) {
							mDialog.dismiss();
						}
						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
					}

				});
	}
}

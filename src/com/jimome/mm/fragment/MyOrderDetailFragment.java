package com.jimome.mm.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
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
 * 订单页面
 * 
 * @author admin
 * 
 */
public class MyOrderDetailFragment extends BaseFragment {

	@ViewInject(R.id.layout_onebuy_back)
	private LinearLayout layout_onebuy_back;
	@ViewInject(R.id.tv_onebuy_title)
	private TextView tv_onebuy_title;
	@ViewInject(R.id.img_onebuy_listicon)
	private ImageView img_onebuy_listicon;
	@ViewInject(R.id.tv_onebuy_listname)
	// 商品名称
	private TextView tv_onebuy_listname;
	@ViewInject(R.id.tv_onebuy_listprice)
	// 商品价格
	private TextView tv_onebuy_listprice;
	// 幸运云购码
	@ViewInject(R.id.tv_onebuy_luckynums)
	private TextView tv_onebuy_luckynums;
	@ViewInject(R.id.gv_myorder)
	// 号码列表
	private GridView gv_myorder;
	private BaseJson baseJson, orderJson;
	private Activity context;

	private SimpleAdapter sim_adapter;

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_onebuy_orderdetail,
				container, false);
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		layout_onebuy_back.setVisibility(View.VISIBLE);
		tv_onebuy_title.setText(R.string.str_commoditydetail);
		if (orderJson.getLucky_code() != null
				&& orderJson.getLucky_code().length() > 0) {
			tv_onebuy_luckynums.setText(StringUtils.getResourse(R.string.str_onebuy_buynum)
					+ orderJson.getLucky_code());
			tv_onebuy_luckynums.setVisibility(View.VISIBLE);
		} else
			tv_onebuy_luckynums.setVisibility(View.GONE);
		ImageLoadUtils.imageLoader.displayImage(orderJson.getIcon(),
				img_onebuy_listicon, ImageLoadUtils.options);
		// 商品名称
		tv_onebuy_listname.setText(orderJson.getName());
		// 商品价格
		tv_onebuy_listprice.setText(StringUtils.getResourse(
				R.string.str_onebuy_priceunit)
				+ orderJson.getPrice());
		waitDialog();
		getHttpData();
	}

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

	public MyOrderDetailFragment() {

	}

	public MyOrderDetailFragment(BaseJson orderJson) {
		this.orderJson = orderJson;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@OnClick({ R.id.layout_onebuy_back })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.layout_onebuy_back:
			context.finish();
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

	private void getHttpData() {
		RequestParams params = new RequestParams();
		String key = "buy/order/detail";
		try {
			params.addQueryStringParameter("good_id", orderJson.getGood_id());
			params.addQueryStringParameter("period", orderJson.getPeriod());
			params.addQueryStringParameter("sub_order_id",
					orderJson.getSub_order_id());
			params.addQueryStringParameter("user_id", Conf.userID);

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
								int codes = baseJson.getCodes().length;
								List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
								Map<String, Object> map;
								for (int i = 0; i < codes; i++) {
									map = new HashMap<String, Object>();
									map.put("text", baseJson.getCodes()[i]);
									list.add(map);
								}
								sim_adapter = new SimpleAdapter(context, list,
										R.layout.grid_item_selecttime,
										new String[] { "text" },
										new int[] { R.id.tv_onebuy_listselect });
								gv_myorder.setAdapter(sim_adapter);
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

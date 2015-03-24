package com.jimome.mm.fragment;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.adapter.OneBuyCartAdapter;
import com.jimome.mm.bean.NewCart;
import com.jimome.mm.database.CartDAO;
import com.jimome.mm.utils.AlipayUtils;
import com.jimome.mm.utils.NetworkUtils;
import com.jimome.mm.utils.StringUtils;
import com.jimome.mm.view.MyListView;
import com.jimome.mm.view.PullScrollView;
import com.jimome.mm.view.PullScrollView.OnPullListener;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.unjiaoyou.mm.R;

/**
 * 购物车页面
 * 
 * @author admin
 * 
 */
public class OneBuyCartFragment extends BaseFragment implements OnPullListener,
		OnItemClickListener {

	@ViewInject(R.id.layout_onebuy_back)
	private LinearLayout layout_onebuy_back;
	@ViewInject(R.id.tv_onebuy_title)
	private TextView tv_onebuy_title;
	@ViewInject(R.id.layout_onebuy_time)
	private LinearLayout layout_onebuy_time;
	@ViewInject(R.id.tv_onbuy_time)
	private TextView tv_onbuy_time;
	@ViewInject(R.id.pushscroll_onebuy_cart)
	private PullScrollView pullScrollView;
	@ViewInject(R.id.tv_onbuy_cartprice)
	private TextView tv_onbuy_cartprice;
	@ViewInject(R.id.layout_onebuy_alipay)
	private LinearLayout layout_onebuy_alipay;
	@ViewInject(R.id.layout_onebuy_yibao)
	private LinearLayout layout_onebuy_yibao;
	@ViewInject(R.id.btn_onebuy_yibao)
	private Button btn_onebuy_yibao;
	@ViewInject(R.id.btn_onebuy_alipay)
	private Button btn_onebuy_alipay;
	private LinearLayout contentLayout;
	// private InboxMainAdapter inboxMainAdapter;
	private OneBuyCartAdapter oneAdapter;
	// private Dialog mDialog;
	// private BaseJson inbox, numJson;
	private MyListView lv_onebuy;
	private int page = 1;
	private Activity context;
	private String countPrice;
	private String good_id = "";
	private String num = "";
	private int pos;
	private AlipayUtils payUtils;// 支付工具类

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater
				.inflate(R.layout.fragment_onebuy_cart, container, false);
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		tv_onebuy_title.setText(R.string.str_onebuy_carttitle);
		layout_onebuy_back.setVisibility(View.VISIBLE);
		layout_onebuy_time.setVisibility(View.VISIBLE);
		tv_onbuy_time.setText(R.string.str_onebuy_myorder);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("jimo.action.deletegood");
		intentFilter.addAction("jimo.action.goodchange");
		intentFilter.addAction("jimo.action.goodbuy");
		context.registerReceiver(mRefreshBroadcastReceiver, intentFilter);
		contentLayout = (LinearLayout) context.getLayoutInflater().inflate(
				R.layout.layout_recommend, null);
		lv_onebuy = (MyListView) contentLayout.findViewById(R.id.lv_recommend);
		lv_onebuy.setOnItemClickListener(this);
		pullScrollView.addBodyView(contentLayout);
		pullScrollView.setOnPullListener(this);
		pullScrollView.setheaderViewReset();
		pullScrollView.setfooterViewReset();
		if (NetworkUtils.checkNet(context)) {
			page = 1;
			// waitDialog();
			// getHttpData(1);
			CartDAO cartDao = new CartDAO(context);
			List<NewCart> list_cart = cartDao.findAll(page);
			if (list_cart != null && list_cart.size() > 0) {
				oneAdapter = new OneBuyCartAdapter(context, list_cart);
				lv_onebuy.setAdapter(oneAdapter);
			}
			cartDao = new CartDAO(context);
			NewCart cart = cartDao.countCart();
			if (cart != null) {
				countPrice = context.getString(R.string.str_onebuy_cartprice,
						cart.getCount(), cart.getPrice());
				tv_onbuy_cartprice.setText(Html.fromHtml(countPrice));
			}
			// 测试数据
			// List<BaseJson> list_goods = new ArrayList<BaseJson>();
			// BaseJson base;
			// for (int i = 0; i < 10; i++) {
			// base = new BaseJson();
			// base.setAge(i + "");
			// list_goods.add(base);
			// }
			// oneAdapter = new OneBuyCartAdapter(context, list_goods);
			// lv_onebuy.setAdapter(oneAdapter);
			// countPrice = context.getString(R.string.str_onebuy_cartprice,
			// "3",
			// "5");
			// tv_onbuy_cartprice.setText(Html.fromHtml(countPrice));

		} else {
			Toast.makeText(context, StringUtils.getResourse(R.string.str_net_register),
					Toast.LENGTH_SHORT).show();
			pullScrollView.setheaderViewReset();
		}
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

	// private void waitDialog() {
	// // mDialog = BasicUtils.showDialog(context, R.style.BasicDialog);
	// // mDialog.setContentView(R.layout.dialog_wait);
	// // mDialog.setCanceledOnTouchOutside(false);
	// //
	// // Animation anim = AnimationUtils.loadAnimation(context,
	// // R.anim.dialog_prog);
	// // LinearInterpolator lir = new LinearInterpolator();
	// // anim.setInterpolator(lir);
	// // mDialog.findViewById(R.id.img_dialog_progress).startAnimation(anim);
	// // mDialog.setOnKeyListener(new OnKeyListener() {
	// // @Override
	// // public boolean onKey(DialogInterface dialog, int keyCode,
	// // KeyEvent event) {
	// // if (keyCode == KeyEvent.KEYCODE_BACK
	// // && event.getAction() == KeyEvent.ACTION_DOWN) {
	// // try {
	// //
	// // if (mDialog != null && !context.isFinishing())
	// // mDialog.dismiss();
	// // } catch (Exception e) {
	// // // TODO: handle exception
	// // }
	// // return false;
	// // }
	// // return false;
	// // }
	// // });
	// mDialog.show();
	// }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		// TODO Auto-generated method stub
		try {
			if (oneAdapter != null && oneAdapter.getCount() > 0) {
				Intent intent = new Intent(context, FragmentToActivity.class);
				intent.putExtra("who", "onebuy_detail");
				intent.putExtra("goodnum", oneAdapter.getAllData().get(pos)
						.getPid());
				context.startActivity(intent);
			}
			// if (inboxMainAdapter != null
			// && !inboxMainAdapter.getAllData().get(pos).getMsg_nums()
			// .equals("0")) {
			// Intent intent = new Intent();
			// intent.putExtra("inboxBadge", inboxMainAdapter.getAllData()
			// .get(pos).getMsg_nums());
			// intent.putExtra("type", 1);
			// intent.setAction("com.action.inboxBadge");
			// context.sendBroadcast(intent);
			// }
			//
			// inboxMainAdapter.refresh(pos);
			// inboxMainAdapter.notifyDataSetChanged();
			// Intent chatIntent = new Intent(context, TalkActivity.class);
			// chatIntent.putExtra("person", inboxMainAdapter.getAllData()
			// .get(pos));
			// startActivity(chatIntent);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// 得到用户地址后启动的广播
	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				String action = intent.getAction();
				if (action.equals("jimo.action.deletegood")) {
					pos = intent.getExtras().getInt("pos");
					good_id = intent.getExtras().getString("id");
					// getHttpData(3);删除
					CartDAO cartD = new CartDAO(context);

					NewCart cart = cartD.deletePid(good_id);
					oneAdapter.remove(pos);
					oneAdapter.notifyDataSetChanged();
					if (cart != null) {
						countPrice = context.getString(
								R.string.str_onebuy_cartprice, cart.getCount(),
								cart.getPrice());
						tv_onbuy_cartprice.setText(Html.fromHtml(countPrice));
						Intent intentCart = new Intent();
						intentCart.setAction("jimo.action.goodcart");
						intentCart.putExtra("num", cart.getCount());
						context.sendBroadcast(intentCart);
					}

				} else if (action.equals("jimo.action.goodchange")) {
					good_id = intent.getExtras().getString("id");
					pos = intent.getExtras().getInt("pos");
					num = intent.getExtras().getString("num");
					// getHttpData(2);//修改数量
					CartDAO cartD = new CartDAO(context);
					NewCart cart = new NewCart();
					cart = cartD.updateCart(good_id, Integer.valueOf(num));
					oneAdapter.setNum(pos, Integer.valueOf(num));
					oneAdapter.notifyDataSetChanged();
					if (cart != null) {
						countPrice = context.getString(
								R.string.str_onebuy_cartprice, cart.getCount(),
								cart.getPrice());
						tv_onbuy_cartprice.setText(Html.fromHtml(countPrice));
					}

				} else if (action.equals("jimo.action.goodbuy")) {
					CartDAO cartD = new CartDAO(context);
					cartD.deleteCart();
					lv_onebuy.setAdapter(null);
					countPrice = context.getString(
							R.string.str_onebuy_cartprice, 0, 0);
					tv_onbuy_cartprice.setText(Html.fromHtml(countPrice));
					Intent intentCart = new Intent();
					intentCart.setAction("jimo.action.goodcart");
					intentCart.putExtra("num", 0);
					context.sendBroadcast(intent);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mDialog != null)
			mDialog.dismiss();
		if (mRefreshBroadcastReceiver != null)
			context.unregisterReceiver(mRefreshBroadcastReceiver);
	}

	/**
	 * type 1代表正常信息 2代表忽略消息 3代表删除消息
	 * 
	 * @param type
	 */
	// private void getHttpData(final int type) {
	//
	// RequestParams params = new RequestParams();
	// String key = "";
	// if (type == 3) {
	// key = "luckbuy/cart/update";
	// params.addQueryStringParameter("user_id", Conf.userID);
	// params.addQueryStringParameter("good_id", good_id);
	// params.addQueryStringParameter("num", "0");
	// params.addQueryStringParameter("action", "u");
	// } else if (type == 2) {
	// key = "luckbuy/cart/update";
	// params.addQueryStringParameter("user_id", Conf.userID);
	// params.addQueryStringParameter("good_id", good_id);
	// params.addQueryStringParameter("num", num);
	// params.addQueryStringParameter("action", "u");
	// } else {
	// // if (page == 1) {
	// // waitDialog();
	// // }
	// params.addQueryStringParameter("user_id", Conf.userID);
	// params.addQueryStringParameter("page", String.valueOf(page));
	// key = "luckbuy/cart/list";
	// }
	// params.addHeader("Authorization",
	// PreferenceHelper.readString(context, "auth", "token"));
	// CacheRequest.requestGET(context, key, params, key, 0,
	// new CacheRequestCallBack() {
	//
	// @Override
	// public void onData(String obj) {
	// // TODO Auto-generated method stub
	// try {
	// LogUtils.printLogE("inbox", obj);
	// if (type == 1) {
	// inbox = new Gson()
	// .fromJson(obj, BaseJson.class);
	// if (inbox != null
	// && inbox.getStatus().equals("200")) {
	// if (page == 1) {
	// oneAdapter = new OneBuyCartAdapter(
	// context, inbox.getGoods());
	// lv_onebuy.setAdapter(oneAdapter);
	// } else {
	// oneAdapter.insertData(inbox.getGoods());
	// oneAdapter.notifyDataSetChanged();
	// }
	// countPrice = context.getString(
	// R.string.str_onebuy_cartprice,
	// inbox.getGood_counts(),
	// inbox.getGood_price());
	// tv_onbuy_cartprice.setText(Html
	// .fromHtml(countPrice));
	// } else {
	// if (page > 1)
	// --page;
	// }
	// } else {
	//
	// if (type == 2) {
	// numJson = new Gson().fromJson(
	// obj.toString(), BaseJson.class);
	// Toast.makeText(context, obj.toString(),
	// Toast.LENGTH_SHORT).show();
	// if (numJson.getStatus().equals("200")) {
	// oneAdapter.setNum(pos, num);
	// oneAdapter.notifyDataSetChanged();
	// countPrice = context.getString(
	// R.string.str_onebuy_cartprice,
	// numJson.getGood_counts(),
	// numJson.getGood_price());
	// tv_onbuy_cartprice.setText(Html
	// .fromHtml(countPrice));
	// }
	//
	// } else if (type == 3) {
	// if (numJson.getStatus().equals("200")) {
	// oneAdapter.remove(pos);
	// oneAdapter.notifyDataSetChanged();
	// countPrice = context.getString(
	// R.string.str_onebuy_cartprice,
	// numJson.getGood_counts(),
	// numJson.getGood_price());
	// tv_onbuy_cartprice.setText(Html
	// .fromHtml(countPrice));
	// }
	// }
	//
	// }
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// pullScrollView.setheaderViewReset();
	// pullScrollView.setfooterViewReset();
	// if (mDialog != null) {
	// mDialog.dismiss();
	// }
	// }
	// }
	//
	// @Override
	// public void onFail(HttpException e, String result,
	// String json) {
	// // TODO Auto-generated method stub
	// pullScrollView.setheaderViewReset();
	// pullScrollView.setfooterViewReset();
	// if (page > 1)
	// --page;
	// if (mDialog != null) {
	// mDialog.dismiss();
	// }
	// ExitManager.getScreenManager().intentLogin(context,
	// StringUtils.httpRsponse(e.toString()));
	// }
	//
	// });
	//
	// }

	@OnClick({ R.id.layout_onebuy_back, R.id.layout_onebuy_alipay,
			R.id.layout_onebuy_yibao, R.id.btn_onebuy_alipay,
			R.id.btn_onebuy_yibao, R.id.layout_onebuy_time, R.id.tv_onbuy_time })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.layout_onebuy_back:
			context.finish();
			break;
		case R.id.tv_onbuy_time:
		case R.id.layout_onebuy_time:
			Intent orderIntent = new Intent(context, FragmentToActivity.class);
			orderIntent.putExtra("who", "myorder");
			context.startActivity(orderIntent);
			break;
		case R.id.layout_onebuy_alipay:
			break;
		case R.id.layout_onebuy_yibao:
			break;
		case R.id.btn_onebuy_alipay:
			Gson gson = new Gson();
			CartDAO cartD_alipay = new CartDAO(context);
			List<NewCart> list_alipay = cartD_alipay.findAll();
			if (list_alipay != null && list_alipay.size() > 0) {
				payUtils = new AlipayUtils(context, 0,
						gson.toJson(list_alipay), "buy");
				payUtils.postHttpData();
			}
			// Toast.makeText(context, "支付宝支付", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_onebuy_yibao:
			Gson gson_bao = new Gson();
			CartDAO cartD_yibao = new CartDAO(context);
			List<NewCart> list_yibao = cartD_yibao.findAll();
			if (list_yibao != null && list_yibao.size() > 0) {
				payUtils = new AlipayUtils(context, 1,
						gson_bao.toJson(list_yibao), "buy");
				payUtils.postHttpData();
			}
			// Toast.makeText(context, "银行卡或信用卡支付", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
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

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		// if (NetworkUtils.checkNet(context)) {
		page = 1;
		CartDAO cartDao = new CartDAO(context);
		List<NewCart> list_cart = cartDao.findAll(page);
		if (list_cart != null && list_cart.size() > 0) {
			oneAdapter = new OneBuyCartAdapter(context, list_cart);
			lv_onebuy.setAdapter(oneAdapter);
		}
		pullScrollView.setheaderViewReset();
		// if (page == 1) {
		// oneAdapter = new OneBuyCartAdapter(
		// context, inbox.getGoods());
		// lv_onebuy.setAdapter(oneAdapter);

		// getHttpData(1);
		// } else {
		// Toast.makeText(context, getString(R.string.str_net_register),
		// Toast.LENGTH_SHORT).show();
		// pullScrollView.setheaderViewReset();
		// }
	}

	@Override
	public void loadMore() {
		// TODO Auto-generated method stub

		// if (NetworkUtils.checkNet(context)) {
		CartDAO cartDao = new CartDAO(context);
		if (oneAdapter != null && oneAdapter.getCount() > 0) { // 有数据
			++page;
			List<NewCart> list_cart = cartDao.findAll(page);
			if (list_cart != null && list_cart.size() > 0) {
				oneAdapter.insertData(list_cart);
				oneAdapter.notifyDataSetChanged();
			} else
				--page;
		} else {
			page = 1;
			List<NewCart> list_cart = cartDao.findAll(page);
			if (list_cart != null && list_cart.size() > 0) {
				oneAdapter = new OneBuyCartAdapter(context, list_cart);
				lv_onebuy.setAdapter(oneAdapter);
			}
		}
		pullScrollView.setfooterViewReset();
		// getHttpData(1);
		// } else {
		// Toast.makeText(context, getString(R.string.str_net_register),
		// Toast.LENGTH_SHORT).show();
		// pullScrollView.setfooterViewReset();
		// }
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

}

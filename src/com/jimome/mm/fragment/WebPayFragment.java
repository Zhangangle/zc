package com.jimome.mm.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.unjiaoyou.mm.R;
import com.jimome.mm.common.Conf;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 支付页面
 * 
 * @author admin
 * 
 */

public class WebPayFragment extends BaseFragment {

	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;// 返回
	@ViewInject(R.id.progBar_web)
	private ProgressBar progBar_web;// 刷新

	@ViewInject(R.id.web_pay)
	private WebView web_pay;
	// private ProgressDialog mDialog;
	private String callBackURL = "";
	private Activity context;
	private String kinds;

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_webview, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	public WebPayFragment() {

	}

	public WebPayFragment(String kinds) {
		this.kinds = kinds;
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
		// mDialog = new ProgressDialog(context);
		// mDialog.setMessage(getResources().getString(
		// R.string.str_network_request));
		// mDialog.setCanceledOnTouchOutside(false);
		// mDialog.setOnKeyListener(new OnKeyListener() {
		//
		// @Override
		// public boolean onKey(DialogInterface dialog, int keyCode,
		// KeyEvent event) {
		// if (keyCode == KeyEvent.KEYCODE_BACK
		// && event.getAction() == KeyEvent.ACTION_DOWN) {
		// if (!context.isFinishing())
		// mDialog.dismiss();
		// return false;
		// }
		// return false;
		// }
		// });
		mDialog.show();
	}

	private class WebClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			progBar_web.setProgress(newProgress);
			if (newProgress == 100) {
				progBar_web.setVisibility(View.GONE);
				if (mDialog != null) {
					mDialog.dismiss();

				}
				if (callBackURL
						.contains("http://api.347.cc/pay/yeepay/callback")) {
					Message msg = handler.obtainMessage();
					if (kinds.equals("pay")) {
						Conf.user_VIP = "1";
						msg.what = 0;
					} else {
						msg.what = 1;
					}
					handler.sendMessage(msg);
				}
			} else {
				progBar_web.setVisibility(View.VISIBLE);
			}
			super.onProgressChanged(view, newProgress);
		}

	}

	private void web() {
		try {

			web_pay.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					callBackURL = url;
					return true;
				}

			});
			web_pay.getSettings().setJavaScriptEnabled(true);
			web_pay.setWebChromeClient(new WebClient());
			String strURI = Conf.webPayurl;
			// 检测网站的合法性
			if (URLUtil.isNetworkUrl(strURI)) {
				web_pay.loadUrl(strURI);
				waitDialog();
			} else {
				Toast.makeText(context, "输入非法网站\n" + strURI, Toast.LENGTH_SHORT)
						.show();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// Handler
	Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				StatService.onEvent(context, "vip-user", "eventLabel", 1);
				context.finish();
				break;
			case 1:
				Intent intent = new Intent();
				intent.setAction("jimo.action.goodbuy");
				context.sendBroadcast(intent);
				context.finish();
				break;
			}
		}
	};

	@OnClick({ R.id.layout_back })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.layout_back:// 返回
			context.finish();
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
	protected void initWidget() {
		// TODO Auto-generated method stub
		tv_title.setText(R.string.str_rechangevip);
		layout_back.setVisibility(View.VISIBLE);
		web();
	}

}

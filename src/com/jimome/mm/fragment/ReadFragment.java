package com.jimome.mm.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.jimome.mm.common.Conf;
import com.jimome.mm.utils.AlipayUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.unjiaoyou.mm.R;

/**
 * 泡妞恋爱终极教程页面
 * 
 * @author admin
 * 
 */
public class ReadFragment extends BaseFragment {

	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;// 返回
	// @ViewInject(R.id.img_read_icon)
	// private ImageView img_read_icon;
	@ViewInject(R.id.tv_read_msg)
	private TextView tv_read_msg;
	@ViewInject(R.id.web_read)
	private WebView web_read;
	// private Dialog mDialog;
	private Activity context;

	private AlipayUtils payUtils;

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_read, container, false);
	}

	private class WebClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {

			super.onProgressChanged(view, newProgress);
		}

	}

	private void web() {
		try {
			web_read.getSettings().setLayoutAlgorithm(
					LayoutAlgorithm.SINGLE_COLUMN);
			web_read.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					// callBackURL = url;
					return true;
				}

			});
			web_read.getSettings().setJavaScriptEnabled(true);
			web_read.setWebChromeClient(new WebClient());
			String strURI = Conf.READ_MSG;
			// 检测网站的合法性
			if (URLUtil.isNetworkUrl(strURI)) {
				web_read.loadUrl(strURI);
				// waitDialog();
			} else {
				Toast.makeText(context, "输入非法网站\n" + strURI, Toast.LENGTH_SHORT)
						.show();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		tv_title.setText(R.string.str_find_read);
		layout_back.setVisibility(View.VISIBLE);
		web();

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@OnClick({ R.id.layout_back, R.id.tv_read_msg })
	public void onClickView(View v) {
		switch (v.getId()) {
		case R.id.tv_read_msg:
			String[] choice = { "支付宝", "信用卡/银行卡" };
			AlertDialog dialog = new AlertDialog.Builder(context)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setTitle("选择支付方式")
					.setItems(choice,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									arg0.dismiss();
									StatService.onEvent(context,
											"order-user", "eventLabel",
											1);
									payUtils = new AlipayUtils(context,
											arg1,"11","pay");
									payUtils.getHttpData();
								}
							})
					.setNegativeButton(R.string.str_cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									arg0.dismiss();
								}
							}).create();
			dialog.show();
		
			break;
		case R.id.layout_back:// 返回
			context.finish();
			break;
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
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
}

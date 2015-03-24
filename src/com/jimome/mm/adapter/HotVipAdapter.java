package com.jimome.mm.adapter;


import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.unjiaoyou.mm.R;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.utils.AlipayUtils;
import com.jimome.mm.utils.BasicUtils;

/**
 * 超级VIP适配器
 * 
 * @author admin
 * 
 */
public class HotVipAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_vip;
	private AlipayUtils payUtils;

	public HotVipAdapter(Context c, List<BaseJson> list_vip) {
		context = c;
		this.list_vip = list_vip;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_vip.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return list_vip.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public View getView(final int pos, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		try {
			if (arg1 == null) {
				arg1 = LayoutInflater.from(context).inflate(
						R.layout.list_item_viphot, arg2, false);
			}
			TextView oldprice = BaseAdapterHelper.get(arg1,
					R.id.tv_hot_oldprice);
			TextView newprice = BaseAdapterHelper.get(arg1,
					R.id.tv_hot_newprice);
			TextView bottom = BaseAdapterHelper.get(arg1, R.id.tv_hot_bottom);
			LinearLayout layout_hot_vip = BaseAdapterHelper.get(arg1,
					R.id.layout_hot_vip);
			oldprice.setText(list_vip.get(pos).getLeft());
			newprice.setText(list_vip.get(pos).getName());
			bottom.setText(list_vip.get(pos).getText());
			layout_hot_vip.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (BasicUtils.isFastDoubleClick()) {
						return;
					}
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
													arg1, list_vip.get(pos)
															.getId(),"pay");
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
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
		}
		return arg1;
	}
}

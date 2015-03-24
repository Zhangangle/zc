package com.jimome.mm.adapter;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.jimome.mm.bean.NewCart;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ImageLoadUtils;
import com.unjiaoyou.mm.R;

public class OneBuyCartAdapter extends BaseSwipeAdapter {

	private Context context;
	private List<NewCart> list_cart;
	private Button increase, decrease, sure, cancel;
	private EditText ed_nums;
	private TextView tv_dialog_surplusnum;
	private Dialog dialog;

	public OneBuyCartAdapter(Context context, List<NewCart> list_cart) {
		this.context = context;
		this.list_cart = list_cart;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_cart.size();
	}

	public void insertData(List<NewCart> list) {
		list_cart.addAll(list);
	}

	public void remove(int pos) {
		list_cart.remove(pos);
	}

	// 设置数量
	public void setNum(int pos, int num) {
		list_cart.get(pos).setPnum(num);
	}





	public List<NewCart> getAllData() {
		return list_cart;
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return list_cart.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public View generateView(final int pos, ViewGroup viewGrop) {
		// TODO Auto-generated method stub

		View view = LayoutInflater.from(context).inflate(
				R.layout.list_item_onebuy_cart, viewGrop, false);
		final SwipeLayout swipeLayout = (SwipeLayout) view
				.findViewById(getSwipeLayoutResourceId(pos));
		swipeLayout.addSwipeListener(new SimpleSwipeListener() {
			@Override
			public void onOpen(SwipeLayout layout) {
				YoYo.with(Techniques.Tada).duration(500).delay(100)
						.playOn(layout.findViewById(R.id.img_trash));
			}

		});
		swipeLayout.findViewById(R.id.tv_delete).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						swipeLayout.close();
						Intent intent = new Intent();
						intent.setAction("jimo.action.deletegood");
						intent.putExtra("pos", pos);
						intent.putExtra("id", list_cart.get(pos).getPid());
						context.sendBroadcast(intent);

					}
				});
		return view;
	}

	@Override
	public void removeShownLayouts(SwipeLayout layout) {
		// TODO Auto-generated method stub
		super.removeShownLayouts(layout);
	}

	@Override
	public void fillValues(final int pos, View view) {
		// TODO Auto-generated method stub
		try {
			ImageView img_onebuy_listicon = BaseAdapterHelper.get(view,
					R.id.img_onebuy_listicon);
			TextView tv_onebuy_listname = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listname);
			TextView tv_onebuy_listsurplusnums = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listsurplusnums);
			TextView tv_onebuy_listcounts = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listcounts);
			TextView tv_onebuy_listprice = BaseAdapterHelper.get(view,
					R.id.tv_onebuy_listprice);

			ImageLoadUtils.imageLoader.displayImage(list_cart.get(pos)
					.getPic(), img_onebuy_listicon,
					ImageLoadUtils.options);
			tv_onebuy_listname.setText(list_cart.get(pos).getPname());
			tv_onebuy_listsurplusnums.setText(context
					.getString(R.string.str_onebuy_list_surplus)
					+ list_cart.get(pos).getPleft()
					+ context.getString(R.string.str_onebuy_man));
			tv_onebuy_listcounts.setText(list_cart.get(pos).getPnum()+"");
			tv_onebuy_listprice.setText("¥"
					+ list_cart.get(pos).getPnum());
			tv_onebuy_listcounts.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					showGitDialog(list_cart.get(pos).getPleft(), list_cart
							.get(pos).getPnum(), pos);
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.e("cart", e.toString());
		}
	}

	protected void showGitDialog(final int count, final int nums,
			final int position) {
		// TODO Auto-generated method stub

		dialog = BasicUtils.showDialog(context, R.style.BasicDialog);
		dialog.setContentView(R.layout.dialog_onebuy_cartnums);
		dialog.setCanceledOnTouchOutside(false);
		tv_dialog_surplusnum = ((TextView) dialog
				.findViewById(R.id.tv_dialog_surplusnum));
		tv_dialog_surplusnum.setText(context
				.getString(R.string.str_onebuy_list_surplus)
				+ count
				+ context.getString(R.string.str_onebuy_man));
		ed_nums = (EditText) dialog.findViewById(R.id.ed_dialog_nums);
		ed_nums.setText(nums+"");
		sure = ((Button) dialog.findViewById(R.id.btn_dialog_sure));
		cancel = ((Button) dialog.findViewById(R.id.btn_dialog_cancle));
		increase = (Button) dialog.findViewById(R.id.btn_dialog_increase);
		decrease = (Button) dialog.findViewById(R.id.btn_dialog_decrease);
		if (nums >= count) {// 超过总数
			ed_nums.setText(count+"");
			BasicUtils.setBackground(context, increase,
					R.drawable.btn_increase_disabled);
		} else if (nums <= 1) {// 数量一个
			ed_nums.setText("1");
			BasicUtils.setBackground(context, increase,
					R.drawable.btn_increase_normal);
		} else {// 数量在1——count之间
			BasicUtils.setBackground(context, increase,
					R.drawable.btn_onebuy_cartadd);
			BasicUtils.setBackground(context, decrease,
					R.drawable.btn_onebuy_cartdecrease);
		}
		sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!ed_nums.getText().toString().equals(nums)) {
					Intent intent = new Intent();
					intent.setAction("jimo.action.goodchange");
					intent.putExtra("pos", position);
					intent.putExtra("id", list_cart.get(position).getPid());
					intent.putExtra("num", ed_nums.getText().toString().trim());
					context.sendBroadcast(intent);
				}
				dialog.dismiss();
			}
		});

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
		decrease.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Integer.valueOf(ed_nums.getText().toString()) > 1)
					ed_nums.setText(Integer.valueOf(ed_nums.getText()
							.toString()) - 1 + "");
			}
		});
		increase.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Integer.valueOf(ed_nums.getText().toString()) < count)
					ed_nums.setText(Integer.valueOf(ed_nums.getText()
							.toString()) + 1 + "");
			}
		});
		ed_nums.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				try {
					if (s.length() > 1 && s.charAt(0) == '0') {
						Integer integer = Integer.valueOf(s.toString());
						ed_nums.setText(integer.toString());
					} else if (s.toString().trim().equals("")
							|| Integer.valueOf(s.toString()) < 1) {
						ed_nums.setText("1");
					} else if (Integer.valueOf(s.toString()) > count) {// 超过总数
						ed_nums.setText(count+"");
					}
					if (Integer.valueOf(s.toString().trim()) <= 1) {
						BasicUtils.setBackground(context, decrease,
								R.drawable.btn_decrease_disabled);
					} else if (Integer.valueOf(s.toString().trim()) >=count) {
						BasicUtils.setBackground(context, increase,
								R.drawable.btn_increase_disabled);
					} else {// 数量在1——count之间
						BasicUtils.setBackground(context, increase,
								R.drawable.btn_onebuy_cartadd);
						BasicUtils.setBackground(context, decrease,
								R.drawable.btn_onebuy_cartdecrease);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	public int getSwipeLayoutResourceId(int arg0) {
		// TODO Auto-generated method stub
		return R.id.swipe;
	}
}

package com.jimome.mm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.baidu.mobstat.StatService;
import com.unjiaoyou.mm.R;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.fragment.AdminFragment;
import com.jimome.mm.fragment.BaseFragment;
import com.jimome.mm.fragment.CommentFragment;
import com.jimome.mm.fragment.DynamicFragment;
import com.jimome.mm.fragment.FateFragment;
import com.jimome.mm.fragment.FouceFragment;
import com.jimome.mm.fragment.LastvisitorFragment;
import com.jimome.mm.fragment.MeiLiStoreFragment;
import com.jimome.mm.fragment.MineInformationFragment;
import com.jimome.mm.fragment.MyGiftFragment;
import com.jimome.mm.fragment.MyOrderDetailFragment;
import com.jimome.mm.fragment.MyOrderFragment;
import com.jimome.mm.fragment.MySelfFragment;
import com.jimome.mm.fragment.OneBuyCartFragment;
import com.jimome.mm.fragment.OneBuyDetailFragment;
import com.jimome.mm.fragment.OneBuyFragment;
import com.jimome.mm.fragment.OneBuyPicDetailFragment;
import com.jimome.mm.fragment.OneBuyRecordFragment;
import com.jimome.mm.fragment.OneBuySelectTimeFragment;
import com.jimome.mm.fragment.PersonalFragment;
import com.jimome.mm.fragment.PrizeFragment;
import com.jimome.mm.fragment.QuestionFragment;
import com.jimome.mm.fragment.ReadFragment;
import com.jimome.mm.fragment.RechangeCoinFragment;
import com.jimome.mm.fragment.SelectFragment;
import com.jimome.mm.fragment.SettingFragment;
import com.jimome.mm.fragment.TalkHelperFragment;
import com.jimome.mm.fragment.TaskFragment;
import com.jimome.mm.fragment.VideoFragment;
import com.jimome.mm.fragment.VipFragment;
import com.jimome.mm.fragment.WebPayFragment;
import com.jimome.mm.utils.ExitManager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;

/**
 * 私聊跳转页面(自己或他人 或充值页面)
 * 
 * @author admin
 * 
 */
@ContentView(R.layout.activity_fromfragment)
public class FragmentToActivity extends BaseFragmentActivity {

	private BaseFragment fragment;
	private boolean mine_flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			ViewUtils.inject(this);
			ExitManager.getScreenManager().pushActivity(this);
			Intent intent = getIntent();
			mine_flag = false;
			if (intent.getStringExtra("who").equals("mine")) {
				mine_flag = true;
				fragment = new MineInformationFragment();
			} else if (intent.getStringExtra("who").equals("vip"))
				fragment = new VipFragment();
			else if (intent.getStringExtra("who").equals("coin"))
				fragment = new RechangeCoinFragment();
			else if (intent.getStringExtra("who").equals("fate"))
				fragment = new FateFragment();
			else if (intent.getStringExtra("who").equals("meili"))
				fragment = new MeiLiStoreFragment();
			else if (intent.getStringExtra("who").equals("personal")) {
				String user_id = intent.getStringExtra("user_id");
				String distance = intent.getStringExtra("distance");
				fragment = new PersonalFragment(distance, user_id);
			} else if (intent.getStringExtra("who").equals("comment"))
				fragment = new CommentFragment("comment");
			else if (intent.getStringExtra("who").equals("praise"))
				fragment = new CommentFragment("praise");
			else if (intent.getStringExtra("who").equals("system"))
				fragment = new AdminFragment();
			else if (intent.getStringExtra("who").equals("visitor")) {
				String user_id = intent.getStringExtra("user_id");
				fragment = new LastvisitorFragment(user_id);
			} else if (intent.getStringExtra("who").equals("myself"))
				fragment = new MySelfFragment("back");
			else if (intent.getStringExtra("who").equals("webpay")) {
				String kinds = intent.getStringExtra("kinds");
				fragment = new WebPayFragment(kinds);
			} else if (intent.getStringExtra("who").equals("helper"))
				fragment = new TalkHelperFragment();
			else if (intent.getStringExtra("who").equals("select"))
				fragment = new SelectFragment();
			else if (intent.getStringExtra("who").equals("task"))
				fragment = new TaskFragment();
			else if (intent.getStringExtra("who").equals("video"))
				fragment = new VideoFragment();
			else if (intent.getStringExtra("who").equals("setting"))
				fragment = new SettingFragment();
			else if (intent.getStringExtra("who").equals("follow")) {
				String name = intent.getStringExtra("name");
				fragment = new FouceFragment(name);
			} else if (intent.getStringExtra("who").equals("question"))
				fragment = new QuestionFragment();
			else if (intent.getStringExtra("who").equals("mygift"))
				fragment = new MyGiftFragment();
			else if (intent.getStringExtra("who").equals("guaguale"))
				fragment = new PrizeFragment();
			else if (intent.getStringExtra("who").equals("onebuy"))
				fragment = new OneBuyFragment();
			else if (intent.getStringExtra("who").equals("onebuy_detail")) {
				String goodnum = intent.getStringExtra("goodnum");
				fragment = new OneBuyDetailFragment(goodnum);
			} else if (intent.getStringExtra("who").equals("onebuy_cart"))
				fragment = new OneBuyCartFragment();
			else if (intent.getStringExtra("who").equals("onebuy_record")) {
				String goodnum = intent.getStringExtra("goodnum");
				fragment = new OneBuyRecordFragment(goodnum);
			} else if (intent.getStringExtra("who").equals("onebuy_picdetail")) {
				String goodnum = intent.getStringExtra("goodnum");
				fragment = new OneBuyPicDetailFragment(goodnum);
			} else if (intent.getStringExtra("who").equals("onebuy_selecttime")) {
				String goodnum = intent.getStringExtra("goodnum");
				fragment = new OneBuySelectTimeFragment(goodnum);
			} else if (intent.getStringExtra("who").equals("onebuy_period")) {
				String goodnum = intent.getStringExtra("goodnum");
				String period = intent.getStringExtra("period");
				fragment = new OneBuyDetailFragment(goodnum, period);
			} else if (intent.getStringExtra("who").equals("read"))
				fragment = new ReadFragment();
			else if (intent.getStringExtra("who").equals("myorder"))
				fragment = new MyOrderFragment();
			else if (intent.getStringExtra("who").equals("orderdetail")) {
				BaseJson orderJson = (BaseJson) intent
						.getSerializableExtra("order");
				fragment = new MyOrderDetailFragment(orderJson);
			} else if (intent.getStringExtra("who").equals("dynamic"))
				fragment = new DynamicFragment();
			changeFragment(R.id.content, fragment);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// public void changeFragment(BaseFragment fragment) {
	// try {
	// FragmentTransaction ft = getSupportFragmentManager()
	// .beginTransaction();
	// ft.replace(R.id.content, fragment, fragment.getClass().getName());
	// ft.commitAllowingStateLoss();
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// }
	// }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ExitManager.getScreenManager().pullActivity(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);

	}// 返回键方法重写

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mine_flag) {
				Intent intent = new Intent();
				intent.setAction("jimome.action.mineinfo");
				sendBroadcast(intent);
			} else {
				finish();
			}
		}
		return false;
	}
}

package com.jimome.mm.activity;

import com.jimome.mm.fragment.BaseFragment;
import com.jimome.mm.utils.AppUtils;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.StringUtils;
import com.unjiaoyou.mm.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

public class BaseFragmentActivity extends FragmentActivity {

	private Context context;
	public Dialog mDialog;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		context = this;
		AppUtils.getScreenWidth(context);
		AppUtils.getScreenHeight(context);
		StringUtils.getInstance(context);
		BasicUtils.toast(context);
		setDialogView(context);
	}

	public Dialog setDialogView(Context context) {
		mDialog = BasicUtils.showDialog(context, R.style.BasicDialog);
		mDialog.setContentView(R.layout.dialog_wait);
		mDialog.setCanceledOnTouchOutside(false);

		Animation anim = AnimationUtils.loadAnimation(context,
				R.anim.dialog_prog);
		LinearInterpolator lir = new LinearInterpolator();
		anim.setInterpolator(lir);
		mDialog.findViewById(R.id.img_dialog_progress).startAnimation(anim);
		mDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					if (mDialog != null)
						mDialog.dismiss();

					return false;
				}
				return false;
			}
		});
		return mDialog;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mDialog != null){
			mDialog.dismiss();
		}
	}

	public  void changeFragment(int container, BaseFragment fragment) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(container, fragment, fragment.getClass().getName());
		ft.commitAllowingStateLoss();
	}

}

package com.jimome.mm.fragment;


import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.StringUtils;
import com.lidroid.xutils.ViewUtils;
import com.unjiaoyou.mm.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;


public abstract   class BaseFragment extends Fragment{
	
	public  Context context;
	public View rootView;
	public Dialog mDialog;
	public Toast toast;
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
		rootView = initView(inflater,container,savedInstanceState);
		 ViewUtils.inject(this,rootView); 
		return rootView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
	public void onActivityCreated( Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initWidget();
//		LruDiskCacheHTTP.initDiskCache(context,"json");
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		context = getActivity();
		 StringUtils.getInstance(context);
		 BasicUtils.toast(context);
	}

	protected abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
	protected abstract  void initWidget();

}

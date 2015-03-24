package com.jimome.mm.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baidu.mobstat.StatService;
import com.unjiaoyou.mm.R;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.ImageLoadUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 滑动引导页面
 * 
 * @author admin
 * 
 */
public class ViewPageActivity extends FragmentActivity {
	private ViewPager mPager;
	private List<View> listViews;
	private int offset = 0;
	private int bmpw;
	private List<BaseJson> list;
	private int pos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.viewpage_image);
		ExitManager.getScreenManager().pushActivity(this);
		Intent intent = getIntent();
		list = (List<BaseJson>) getIntent().getSerializableExtra("list_image");
		pos = intent.getIntExtra("pos", 0);
		IntiViewPager();

	}

	/**
	 * 初始化滑动控件
	 * 
	 * @author admin
	 * 
	 */
	Bitmap bitmap;

	private void IntiViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPagerintroduce);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		View view;

		for (int i = 0; i < list.size(); i++) {
			view = mInflater.inflate(R.layout.viewpage_item_image, null);
			listViews.add(view);
			final ImageView img = (ImageView) view
					.findViewById(R.id.img_viewpage_icon);
			final LinearLayout layout_viewpager = (LinearLayout) view
					.findViewById(R.id.layout_viewpager);
			try {
				ImageLoadUtils.imageLoader.displayImage(list.get(i).getUrl(),
						img, ImageLoadUtils.options,
						new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String arg0, View arg1) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onLoadingFailed(String arg0, View arg1,
									FailReason arg2) {
								// TODO Auto-generated method stub
								finish();
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								// TODO Auto-generated method stub
								int height = (Conf.width * loadedImage
										.getHeight()) / loadedImage.getWidth();
								img.setLayoutParams(new LinearLayout.LayoutParams(
										android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
										height));
							}

							@Override
							public void onLoadingCancelled(String arg0,
									View arg1) {
								// TODO Auto-generated method stub
							}
						});
			} catch (Exception e) {
				// TODO: handle exception
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
			img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (list.get(0).getStyle() != null
							&& list.get(0).getStyle().equals("ad")) {
						StatService.onEvent(ViewPageActivity.this,
								"click-start-ad", "eventLabel", 1);
						Intent intent;
						if (Conf.gender.equals("1")) {
							intent = new Intent(ViewPageActivity.this,
									FragmentToActivity.class);
							intent.putExtra("who", "vip");
						} else {
							intent = new Intent(ViewPageActivity.this,
									MyShowUploadActivity.class);
							intent.putExtra("type", 2);
						}
						startActivity(intent);
					}
					finish();
				}
			});
			layout_viewpager.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					finish();
				}
			});
		}
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(pos);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {

			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);

		}

	}

	// 适配器
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			super();
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(mListViews.get(position));
		}

		@Override
		public void finishUpdate(ViewGroup container) {

		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

	}

	// 初始化动画
	private void InitImageView() {

		bmpw = BitmapFactory.decodeResource(getResources(),
				R.drawable.welcome_bg).getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels; // 获取分辨率的宽度
		if (list.size() == 1) {
			offset = 0;
		} else
			offset = (screenW / list.size() - bmpw) / (list.size() - 1);// 计算偏移值
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
	}

	// 实现页卡切换监听
	public class MyOnPageChangeListener implements OnPageChangeListener {

		// 当滑动状态改变时调用
		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		// 当新的页面被选中时调用
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		// 当新的页面被选中时调用
		@Override
		public void onPageSelected(int arg0) {
		}
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

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			ExitManager.getScreenManager().pullActivity(this);
			if (MainFragmentActivity.list_app != null
					&& MainFragmentActivity.list_app.size() > 0) {
				Intent intent = new Intent(ViewPageActivity.this,
						ADappActivity.class);
				startActivity(intent);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}

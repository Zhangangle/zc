package com.jimome.mm.utils;

import android.graphics.Bitmap;

import com.unjiaoyou.mm.R;
import com.jimome.mm.common.Conf;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ImageLoadUtils {
	
	public ImageLoadUtils (){
			
	}
	
	public static ImageLoader imageLoader = ImageLoader.getInstance();
	
	public static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
			.preProcessor(null)
			.bitmapConfig(Bitmap.Config.RGB_565).build();
	
	/**
	 * 10个像素圆角
	 */
	public static DisplayImageOptions optionsRounded = new DisplayImageOptions.Builder()
			.displayer(new RoundedBitmapDisplayer(10))
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
			.preProcessor(null)
			.bitmapConfig(Bitmap.Config.RGB_565).build();

}

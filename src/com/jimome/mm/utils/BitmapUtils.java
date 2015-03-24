package com.jimome.mm.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.jimome.mm.common.Conf;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

public class BitmapUtils {
	public static File pictureFile;// 创建一个放图片的文件夹
	public static File targetPictureFile;
	private static final String PICTURE_CAMERA_FILE = "sd_picture_camera.jpg";
	private static final String PICTURE_CROP_FILE = "sd_picture_crop.jpg";

	/**
	 * 创建一个放图片的数据的空文件
	 * 
	 */
	public static void initPictureFile() {
		try {
			pictureFile = new File(Environment.getExternalStorageDirectory(),
					PICTURE_CAMERA_FILE);
			if (pictureFile.exists()) {
				pictureFile.delete();
				pictureFile.createNewFile();
			}
			targetPictureFile = new File(
					Environment.getExternalStorageDirectory(),
					PICTURE_CROP_FILE);
			if (targetPictureFile.exists()) {
				targetPictureFile.delete();
				targetPictureFile.createNewFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * 删除盛放图片数据的文件
	 */
	public static void cleanAfterUploadAvatar() {
		try {
			if (pictureFile.exists()) {
				pictureFile.delete();
			}
			if (targetPictureFile.exists()) {
				targetPictureFile.delete();
			}
			pictureFile = null;
			targetPictureFile = null;
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public static Bitmap getCompressImage(String srcFilePath,
			float requestWidth, float requestHeight) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcFilePath, newOpts);// 此时返回bm为空

		int w = newOpts.outWidth;
		int h = newOpts.outHeight;

		float hh = requestHeight;
		float ww = requestWidth;
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放

		if (w >= h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww) + 1;
		} else if (w <= h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh) + 1;
		}
		if (be > 2) {
			be = be + 1;
		}
		if (be <= 0)
			be = 1;
		newOpts.inJustDecodeBounds = false;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcFilePath, newOpts);
		return bitmap;
	}

	// 对图片进行处理
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, 0, 0, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SCREEN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap changToFullScreenImage(Activity context, Bitmap b) {
		int w = b.getWidth();
		int h = b.getHeight();
		WindowManager manage = context.getWindowManager();
		Display display = manage.getDefaultDisplay();
		@SuppressWarnings("deprecation")
		int screenWidth = display.getWidth();
		@SuppressWarnings("deprecation")
		int screenHeight = display.getHeight();
		float sx = (float) screenWidth / w;// 要强制转换，不转换我的在这总是死掉。
		float sy = (float) screenHeight / h;
		float scale = sx < sy ? sx : sy;
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale); // 长和宽放大缩小的比例
		// Bitmap resizeBmp = Bitmap.createScaledBitmap(b, w, h, true);
		Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w, h, matrix, true);
		return resizeBmp;
	}

	// bitmap转换byte字节
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 80, baos);
		return baos.toByteArray();
	}

	//byte字节转bitmap
	public static Bitmap Bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	public static Bitmap getThumbUploadPath(String path, int bitmapMaxWidth)
			throws Exception {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int height = options.outHeight;
		int width = options.outWidth;
		int reqHeight = 0;
		int reqWidth = bitmapMaxWidth;
		reqHeight = (reqWidth * height) / width;
		// 在内存中创建bitmap对象，这个对象按照缩放大小创建的
		options.inSampleSize = calculateInSampleSize(options, bitmapMaxWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		Bitmap bmp = compressImage(Bitmap.createScaledBitmap(bitmap,
				bitmapMaxWidth, reqHeight, false));
		return bmp;
	}

	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	private static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			options -= 10;// 每次都减少10
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	// 创建缩略图
	public static Bitmap createThumbnail(Bitmap source) {
		int oldW = source.getWidth();
		int oldH = source.getHeight();
		int w = oldW / Conf.width;
		int h = oldH / Conf.height;
		int newW = 0;
		int newH = 0;
		if (w == 0 && h == 0) {
			newW = oldW;
			newH = oldH;
		} else {
			int i = w > h ? w : h; // 获取缩放比例

			newW = oldW / i;
			newH = oldH / i;
		}

		Bitmap imgThumb = ThumbnailUtils.extractThumbnail(source, newW, newH);
		return imgThumb; // 注：saveBitmap方法为保存图片并返回路径的private方法
	}

	// 创建视频缩略图
	public static Bitmap getVideoThumbnail(String videoPath, int width,
			int height) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath,
				MediaStore.Images.Thumbnails.MINI_KIND);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	public static void saveBitmap(Bitmap bitmap) {
		File file = IOUtils.createVideoThumbnailPath();

		Bitmap mBitmap = bitmap;
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * bitmap模糊效果
	 */
	public static Bitmap blur(Bitmap bkg, ImageView view) {
			float scaleFactor = 1;
			float radius = 20;

			Bitmap overlay = Bitmap.createBitmap(
					(int) (bkg.getWidth() / scaleFactor),
					(int) (bkg.getHeight() / scaleFactor), Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(overlay);
			canvas.translate(-view.getLeft() / scaleFactor, -view.getTop()
					/ scaleFactor);
			canvas.scale(1 / scaleFactor, 1 / scaleFactor);
			Paint paint = new Paint();
			paint.setFlags(Paint.FILTER_BITMAP_FLAG);
			canvas.drawBitmap(bkg, 5, 5, paint);

			overlay = FastBlur.doBlur(overlay, (int) radius, true);	
			return overlay;
	}
}

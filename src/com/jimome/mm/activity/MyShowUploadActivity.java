package com.jimome.mm.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.unjiaoyou.mm.R;
import com.jimome.mm.adapter.PhotoUploadAdapter;
import com.jimome.mm.bean.PhotoImage;
import com.jimome.mm.common.Conf;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.BitmapUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.LogUtils;
import com.jimome.mm.utils.StringUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 我的女神秀或美套图或视频秀页面上传页面
 * 
 * @author Administrator
 * 
 */
@ContentView(R.layout.activity_myshow_upload)
public class MyShowUploadActivity extends BaseFragmentActivity {
	private Context context;
	@ViewInject(R.id.ed_myshow_msg)
	private EditText ed_myshow_msg;
	// @ViewInject( R.id.img_myshow_upload)
	// private ImageView img_myshow_upload;
	// @BindView(id = R.id.tv_myshow_tip)
	// private TextView tv_myshow_tip;
	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	@ViewInject(R.id.layout_upload)
	private LinearLayout layout_upload;
	@ViewInject(R.id.layout_uplaodhint)
	private LinearLayout layout_uplaodhint;
	@ViewInject(R.id.tv_uploadpic_hint)
	private TextView tv_uploadpic_hint;
	@ViewInject(R.id.btn_video)
	private Button btn_video;
	@ViewInject(R.id.btn_send)
	private Button btn_send;
	@ViewInject(R.id.gv_photo)
	private GridView gv_photo;
	private int type;
	private String[] choice = {"拍照上传", "本地图片上传"};
	/*** 拍照上传 ***/
	private static final int UPLOAD_CAMERA = 1;
	/*** 本地上传 ***/
	private static final int UPLOAD_LOCAL = 2;
	/*** 结果 ***/
	private static final int PHOTO_REQUEST_CUT = 3;
	
	private static final int UPLOAD_VIDEO = 4;
	private Bitmap mBitmap;
	private byte[] byte_img;
	private String icon;
	
	private ArrayList<PhotoImage> images;
	private ArrayList<PhotoImage> upImages;
	private PhotoUploadAdapter photoAdapter;
	private boolean isRegister = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		ViewUtils.inject(this);
		ExitManager.getScreenManager().pushActivity(this);
		type = getIntent().getExtras().getInt("type");
		String title = "";
		if (type == -1 || type == 1 || type == 2) {
			if(type == -1){
				isRegister = true;
			}
			btn_video.setVisibility(View.GONE);
			layout_upload.setVisibility(View.VISIBLE);
			btn_send.setVisibility(View.VISIBLE);
			layout_uplaodhint.setVisibility(View.VISIBLE);
			SpannableStringBuilder builder = new SpannableStringBuilder(
					tv_uploadpic_hint.getText().toString());
			ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
			ForegroundColorSpan blueSpan = new ForegroundColorSpan(
					getResources().getColor(R.color.lightblue));
			ForegroundColorSpan graySpan = new ForegroundColorSpan(
					getResources().getColor(R.color.darkgray));
			builder.setSpan(blueSpan, 15, 18,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			builder.setSpan(redSpan, tv_uploadpic_hint.getText().toString()
					.length() - 7, tv_uploadpic_hint.getText().toString()
					.length() - 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			tv_uploadpic_hint.setText(builder);
			title = StringUtils.getResourse(R.string.str_uploadtitle);
		} else if (type == 3) {
			btn_video.setVisibility(View.VISIBLE);
			layout_upload.setVisibility(View.GONE);
			btn_send.setVisibility(View.GONE);
			layout_uplaodhint.setVisibility(View.GONE);
			title = StringUtils.getResourse(R.string.str_my_shipinxiu);
		} else {
		}
		tv_title.setText(title);
		layout_back.setVisibility(View.VISIBLE);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("jimome.action.myupload");
		registerReceiver(uploadBroadcastReceiver, intentFilter);
		images = new ArrayList<PhotoImage>();
		upImages = new ArrayList<PhotoImage>();
		photoAdapter = new PhotoUploadAdapter(context,images,handler);
		gv_photo.setAdapter(photoAdapter);
//		gv_photo.setOnItemClickListener(this);
	}

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 0 :
					((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(
							ed_myshow_msg.getWindowToken(), 0);
					ShowChoiceDialog();
					break;

				default :
					break;
			}
		}
	};
	
	// 弹出选择图片对话框
	private void ShowChoiceDialog() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(context).setTitle(R.string.str_uploadtitle)
				.setItems(choice, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						if (arg1 == 0) {
							// 调用相机拍照
							if (!BasicUtils.isSDCardAvaliable()) {
								Toast.makeText(
										context,
										StringUtils.getResourse(
												R.string.str_sdnull),
										Toast.LENGTH_SHORT).show();
								return;
							}
							BitmapUtils.initPictureFile();
							// 调用系统的拍照功能
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							int currentapiVersion = android.os.Build.VERSION.SDK_INT;
							try {

								// if (currentapiVersion > 11) {
								// 指定调用相机拍照后照片的储存路径
								intent.putExtra(MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(BitmapUtils.pictureFile));
								// }
								startActivityForResult(intent, UPLOAD_CAMERA);
							} catch (Exception e) {
								// TODO: handle exception
							}

						} else {
							// 调用本地相册
							try {
								Intent intent = new Intent(Intent.ACTION_PICK,
										null);
								intent.setType("image/*");
								startActivityForResult(intent, UPLOAD_LOCAL);
							} catch (Exception e) {
								// TODO: handle exception
								try {
									Intent intent = new Intent();
									/* 开启Pictures画面Type设定为image */
									intent.setType("image/*");
									/* 使用Intent.ACTION_GET_CONTENT这个Action */
									intent.setAction(Intent.ACTION_GET_CONTENT);
									startActivityForResult(intent, UPLOAD_LOCAL);
								} catch (Exception e2) {
									// TODO: handle exception
									Toast.makeText(context, "启动相册失败",
											Toast.LENGTH_SHORT).show();
								}

							}
						}
					}

				}).create().show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {

		// 相机拍照
			case UPLOAD_CAMERA :
				try {
					mBitmap = null;
					Options options = new Options();
					options.inJustDecodeBounds = false;
					options.inSampleSize = 2;
					mBitmap = BitmapFactory.decodeFile(
							BitmapUtils.pictureFile.getAbsolutePath(), options);
					// 创建图片缩略图
					if (mBitmap == null)
						return;
					// tv_myshow_tip.setText("");
					// img_myshow_upload.setImageBitmap(mBitmap);
					byte_img = BitmapUtils.Bitmap2Bytes(mBitmap);
					icon = BitmapUtils.pictureFile.toString().replace(
							BitmapUtils.pictureFile.toString(),
							Conf.userID + "_" + System.currentTimeMillis()
									+ ".jpg");
					
					PhotoImage image = new PhotoImage(mBitmap,icon);
					images.add(image);
					photoAdapter.notifyDataSetChanged();
					Conf.images = images;
					PhotoImage upImage = new PhotoImage(BitmapUtils.pictureFile.getAbsolutePath(), icon);
					upImages.add(upImage);
					Conf.images = upImages;
					// Intent intent = new Intent("jimome.action.uploadalbum");
					// intent.putExtra("type", String.valueOf(type));
					// intent.putExtra("text",
					// ed_myshow_msg.getText().toString());
					// intent.putExtra("albumimg", icon);
					//Conf.img_byte = byte_img;
					// sendBroadcast(intent);
					// onBackPressed();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
					finish();
				}
				break;

			// 本地照片
			case UPLOAD_LOCAL :
				try {
					if (data == null) {
						return;
					}
					Uri imageuri = data.getData();
					String[] prStrings = {MediaStore.Images.Media.DATA};
					Cursor imageCursor = managedQuery(imageuri, prStrings,
							null, null, null);
					int imgpath = imageCursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					imageCursor.moveToFirst();
					String image_path = imageCursor.getString(imgpath);
					File photoName = new File(image_path);
					ContentResolver cr = this.getContentResolver();
					mBitmap = null;
					Options options = new Options();
					options.inJustDecodeBounds = false;
					options.inSampleSize = 2;
					mBitmap = BitmapFactory.decodeStream(
							cr.openInputStream(imageuri), null, options);
					// 创建图片缩略图
					// tv_myshow_tip.setText("");
					// img_myshow_upload.setImageBitmap(mBitmap);
					byte_img = BitmapUtils.Bitmap2Bytes(mBitmap);
					icon = image_path.replace(image_path, Conf.userID + "_"
							+ System.currentTimeMillis() + ".jpg");
					PhotoImage image = new PhotoImage(mBitmap,icon);
					images.add(image);
					photoAdapter.notifyDataSetChanged();
					PhotoImage upImage = new PhotoImage(image_path, icon);
					upImages.add(upImage);
					Conf.images = upImages;
					// Intent intent = new Intent("jimome.action.uploadalbum");
					// intent.putExtra("type", String.valueOf(type));
					// intent.putExtra("text",
					// ed_myshow_msg.getText().toString());
					// intent.putExtra("albumimg", icon);
					// Conf.img_byte = byte_img;
					// sendBroadcast(intent);
					// onBackPressed();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
					finish();
				}
				break;

			// 裁剪结果
			case PHOTO_REQUEST_CUT :
				// if (resultCode == Activity.RESULT_CANCELED) {
				// BitmapUtils.cleanAfterUploadAvatar();
				//
				// return;
				// }
				// if (BitmapUtils.targetPictureFile.length() < 1024) {
				// BitmapUtils.cleanAfterUploadAvatar();
				// BitmapUtils.initPictureFile();
				// return;
				// }
				// mBitmap = BitmapUtils.getCompressImage(
				// BitmapUtils.targetPictureFile.getAbsolutePath(), 600, 600);
				// Bitmap bitmap = BitmapUtils.getRoundedCornerBitmap(mBitmap);
				// byte_img = BitmapUtils.Bitmap2Bytes(bitmap);
				// img_myshow_upload.setImageBitmap(bitmap);
				// // img_myshow_upload.set
				// tv_myshow_tip.setText("");
				// // //上传图片
				// dialog = BasicUtils.showDialog(context);
				// dialog.setMessage(context.getResources().getString(
				// R.string.str_uploadtitle)
				// + "...");
				// dialog.show();
				// UploadAliyun(BitmapUtils.targetPictureFile);
				break;
			case UPLOAD_VIDEO:
				try {
					String[] media_info = new String[] {MediaStore.Video.Media.DURATION,
							MediaStore.Video.Media.DATA,
							MediaStore.Video.Media._ID};
					Cursor cursor =this.getContentResolver().query(
						 MediaStore.Video.Media.EXTERNAL_CONTENT_URI, media_info, "duration < 600000",
						 null, null);
						if (cursor.moveToFirst()) {
							int index = cursor
									.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
							int id = cursor
									.getColumnIndexOrThrow(MediaStore.Video.Media._ID);

							String videopath = cursor.getString(index);
							int videoid = cursor.getInt(id);

							Options options = new Options();
							options.inJustDecodeBounds = false;
							options.inSampleSize = 2;
							// Bitmap videoBmp =
							// MediaStore.Video.Thumbnails.getThumbnail(getActivity().getContentResolver(),
							// videoid, MediaStore.Images.Thumbnails.MINI_KIND,
							// options);
							// byte[] video_data =
							// BitmapUtils.Bitmap2Bytes(videoBmp);
							String selection = MediaStore.Video.Thumbnails.VIDEO_ID
									+ "=?";
							String[] selectionArgs = new String[]{"" + videoid};

							Cursor imgCursor =this
									.getContentResolver()
									.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
											null, selection, selectionArgs, null);
							String videoimgpath = "";
							if (imgCursor.moveToFirst()) {
								int imgpath = imgCursor
										.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
								videoimgpath = imgCursor.getString(imgpath);
							}

							LogUtils.printLogE("视频路径----", videopath + "\n"
									+ videoimgpath);
							 Intent intent = new
							 Intent("jimome.action.uploadvideo");
							 intent.putExtra("videopath", videopath);
							 intent.putExtra("videoimg", videoimgpath);
							 intent.putExtra("text", ed_myshow_msg.getText().toString());
							 intent.putExtra("type", "3");
							 sendBroadcast(intent);
						}	
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					
				}
				finish();
				break;
			default :
				break;
		}
	}

	/**
	 * 对拍摄的图片做放缩处理，然后去剪裁
	 * 
	 * @param data
	 */
	public void startPhotoCrop(Intent data) {
		Bundle extras = data.getExtras();
		Bitmap imageBitmap = (Bitmap) extras.get("data");
		// 异步地去执行图片的放缩处理
		AsyncTask<Bitmap, Void, Void> backgroundTask = new AsyncTask<Bitmap, Void, Void>() {

			@Override
			protected Void doInBackground(Bitmap... params) {
				if (params.length == 0) {
					return null;
				}
				Bitmap imageBitmap = params[0];
				Bitmap targetmap = BitmapUtils.changToFullScreenImage(
						MyShowUploadActivity.this, imageBitmap);
				Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(
						context.getContentResolver(), targetmap, null, null));
				startPhotoZoom(uri);
				return null;
			}
		};
		backgroundTask.execute(imageBitmap);
	}

	// 图片的裁剪
	public void startPhotoZoom(Uri fromFile) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(fromFile, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX,outputY 是剪裁图片的宽高
		// intent.putExtra("outputX", size);
		// intent.putExtra("outputY", size);
		intent.putExtra("return-data", false);
		intent.putExtra("scale", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(BitmapUtils.targetPictureFile));
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
			mBitmap = null;
		}
		if (uploadBroadcastReceiver != null)
			unregisterReceiver(uploadBroadcastReceiver);
		ExitManager.getScreenManager().pullActivity(this);
	}

	@OnClick({ R.id.layout_back, R.id.btn_send, R.id.btn_video })
	public void onClickView(View v) {
		switch (v.getId()) {
			case R.id.layout_back :
				finish();
				if (isRegister) {
					startActivity(new Intent(context,
							MainFragmentActivity.class));
				}
				break;
			case R.id.btn_send :
				if (TextUtils.isEmpty(ed_myshow_msg.getText().toString())) {
				Toast.makeText(context, StringUtils.getResourse(R.string.str_chatmsg_hint),
						Toast.LENGTH_SHORT).show();
					return;
				}
				if(images.size() <1){
				Toast.makeText(context, "请先选择图片", Toast.LENGTH_SHORT).show();
					return;
				}
				if(isRegister){
					type = 1;
				}
				Intent upIntent = new Intent("jimome.action.uploadalbum");
				upIntent.putExtra("type", String.valueOf(type));
				upIntent.putExtra("text", ed_myshow_msg.getText().toString());
//				upIntent.putExtra("albumimg", (Serializable)images);
				sendBroadcast(upIntent);
				final Dialog dialog = BasicUtils.showDialog(context,
						R.style.BasicDialog);
				dialog.setContentView(R.layout.dialog_send);
				dialog.setCanceledOnTouchOutside(false);
				TextView tip = ((TextView) dialog
						.findViewById(R.id.tv_dialog_msg));
				tip.setText(R.string.str_send_success);
				dialog.show();
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						dialog.dismiss();
						finish();
						if(isRegister){
							startActivity(new Intent(context,
									MainFragmentActivity.class));
						}
					}
				}, 2000);

				break;
			// case R.id.img_myshow_upload:
			// // if (TextUtils.isEmpty(ed_myshow_msg.getText().toString())) {
			// // ViewInject.toast(getString(R.string.str_chatmsg_hint));
			// // return;
			// // }
			// ShowChoiceDialog();
			// break;
			case R.id.btn_video :
				if (BasicUtils.containsEmoji(ed_myshow_msg.getText().toString()
						.trim())) {
				Toast.makeText(context, StringUtils.getResourse(R.string.str_nickname_tip2),
						Toast.LENGTH_SHORT).show();
				} else if (ed_myshow_msg.getText().toString().trim().equals("")) {
				Toast.makeText(context, StringUtils.getResourse(R.string.str_send_null),
						Toast.LENGTH_SHORT).show();
				} else {
					((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(
									ed_myshow_msg.getWindowToken(), 0);
					// Intent intent = new Intent(context,
					// VideoRecorderActivity.class);
					// intent.putExtra("type", String.valueOf(type));
					// intent.putExtra("text",
					// ed_myshow_msg.getText().toString());
					// startActivity(intent);
					// onBackPressed();
					try {
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("video/*");
						intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
						startActivityForResult(intent, UPLOAD_VIDEO);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				break;
			default :
				break;
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == event.KEYCODE_BACK){
			finish();
			if(isRegister){
				startActivity(new Intent(context,
						MainFragmentActivity.class));
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
		
	}

	// 上传消息广播
	private BroadcastReceiver uploadBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			try {
				if (action.equals("jimome.action.myupload")) {
					// onBackPressed();
				}

			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};
}

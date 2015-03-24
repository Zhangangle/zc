package com.jimome.mm.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.adapter.AdminAdapter;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.BitmapUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.PreferenceHelper;
import com.jimome.mm.utils.StringUtils;
import com.jimome.mm.view.MyListView;
import com.jimome.mm.view.PullScrollView;
import com.jimome.mm.view.PullScrollView.OnPullListener;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.unjiaoyou.mm.R;

/**
 * 系统消息页面
 * 
 * @author admin
 * 
 */

public class AdminFragment extends BaseFragment implements OnPullListener,
		OnItemClickListener {

	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	@ViewInject(R.id.scrollView_admin)
	private PullScrollView pullScrollView;
	@ViewInject(R.id.btn_admin_pic)
	private Button btn_admin_pic;
	@ViewInject(R.id.ed_talk_msg)
	private EditText ed_talk_msg;
	@ViewInject(R.id.btn_chatmsg_send)
	private Button btn_chatmsg_send;
	private LinearLayout contentLayout;
	private MyListView lv_admin;
	private BaseJson baseJson;
	private AdminAdapter adminAdapter;
	private int page = 1;
	// private Dialog mDialog;
	private int totalHeight;
	private String[] choice = { "拍照上传", "本地图片上传" };
	/*** 拍照上传 ***/
	private static final int UPLOAD_CAMERA = 1;
	/*** 本地上传 ***/
	private static final int UPLOAD_LOCAL = 2;
	/*** 结果 ***/
	private static final int PHOTO_REQUEST_CUT = 3;
	private Bitmap mBitmap;
	private byte[] byte_img;
	private String icon;
	private String message;
	private Activity context;

	@Override
	protected View initView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		return arg0.inflate(R.layout.fragment_admin, arg1, false);
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

	private void waitDialog() {
		// mDialog = BasicUtils.showDialog(context, R.style.BasicDialog);
		// mDialog.setContentView(R.layout.dialog_wait);
		// mDialog.setCanceledOnTouchOutside(false);
		//
		// Animation anim = AnimationUtils.loadAnimation(context,
		// R.anim.dialog_prog);
		// LinearInterpolator lir = new LinearInterpolator();
		// anim.setInterpolator(lir);
		// mDialog.findViewById(R.id.img_dialog_progress).startAnimation(anim);
		// mDialog.setOnKeyListener(new OnKeyListener() {
		// @Override
		// public boolean onKey(DialogInterface dialog, int keyCode,
		// KeyEvent event) {
		// if (keyCode == KeyEvent.KEYCODE_BACK
		// && event.getAction() == KeyEvent.ACTION_DOWN) {
		// if (!context.isFinishing())
		// mDialog.dismiss();
		//
		// return false;
		// }
		// return false;
		// }
		// });
		mDialog.show();
	}

	private void getTotalHeightofListView(ListView listView) {
		ListAdapter mAdapter = listView.getAdapter();
		if (mAdapter == null) {
			return;
		}
		for (int i = 0; i < mAdapter.getCount(); i++) {
			View mView = mAdapter.getView(i, null, listView);
			mView.measure(
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			// mView.measure(0, 0);
			totalHeight += mView.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (mAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
		pullScrollView.post(new Runnable() {
			public void run() {
				pullScrollView.scrollTo(0, totalHeight);
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("jimome.action.sendpic");
		context.registerReceiver(mRefreshBroadcastReceiver, intentFilter);
		// } else {
		// ViewInject.toast(getString(R.string.str_net_register));
		// }
	}

	// 接收消息广播
	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("jimome.action.sendpic")) {
				message = intent.getStringExtra("text");
				getHttpData(2);
			}
		}
	};

	private void initView() {
		contentLayout = (LinearLayout) context.getLayoutInflater().inflate(
				R.layout.layout_chatmsg_main, null);
		lv_admin = (MyListView) contentLayout.findViewById(R.id.lv_talk);
		pullScrollView.addBodyView(contentLayout);

	}

	@OnClick({ R.id.btn_chatmsg_send, R.id.btn_admin_pic, R.id.layout_back })
	protected void onClickView(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_chatmsg_send:
			message = ed_talk_msg.getText().toString();
			if (TextUtils.isEmpty(message)) {
				return;
			}
			if (BasicUtils.isFastDoubleClick()) {
				return;
			}
			getHttpData(1);
			((InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(ed_talk_msg.getWindowToken(), 0);
			break;
		case R.id.btn_admin_pic:
			if (BasicUtils.isFastDoubleClick()) {
				return;
			}
			ShowChoiceDialog();
			break;
		case R.id.layout_back:
			context.finish();
			break;
		default:
			break;
		}

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

	private void getHttpData(final int kind) {
		int cache_time = 0;
		RequestParams params = new RequestParams();
		String key = "";
		try {
			if (kind == 0) {
				params.addQueryStringParameter("cur_user", Conf.userID);
				params.addQueryStringParameter("page", String.valueOf(page));
				key = "msg/sys";
			} else if (kind == 1) {
				params.addQueryStringParameter("user_id", Conf.userID);
				params.addQueryStringParameter("text", message);
				params.addQueryStringParameter("msg_type", "1");
				key = "msg/feedback";
			} else if (kind == 2) {
				params.addQueryStringParameter("user_id", Conf.userID);
				params.addQueryStringParameter("text", message);
				params.addQueryStringParameter("msg_type", "2");
				key = "msg/feedback";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key, cache_time,
				new CacheRequestCallBack() {

					@Override
					public void onFail(HttpException e, String result,
							String json) {
						// TODO Auto-generated method stub
						if (mDialog != null) {
							mDialog.dismiss();
						}
						ExitManager.getScreenManager().intentLogin(context,
								e.getExceptionCode() + "");
						if (json.equals("")) {
							BasicUtils.toast(StringUtils
									.getResourse(R.string.str_net_register));
							return;
						}
					}

					@Override
					public void onData(String json) {
						// TODO Auto-generated method stub
						if (mDialog != null) {
							mDialog.dismiss();
						}
						if (json.equals("")) {
//							BasicUtils.toast(StringUtils
//									.getResourse(R.string.str_net_register));
							return;
						}
						try {
							if (kind == 0) {
								baseJson = new Gson().fromJson(json,
										BaseJson.class);
								if (baseJson.getStatus().equals("200")) {
									if (page == 1) {
										adminAdapter = new AdminAdapter(
												context, baseJson.getSys_msgs());
										lv_admin.setAdapter(adminAdapter);

									} else {
										adminAdapter.insertList(baseJson
												.getSys_msgs());
										adminAdapter.notifyDataSetChanged();
									}
									getTotalHeightofListView(lv_admin);
								} else {
									if (page > 1)
										--page;
								}
							} else {
								BaseJson upload = new Gson().fromJson(json,
										BaseJson.class);
								if (upload.getStatus().equals("200")) {
									List<BaseJson> list = new ArrayList<BaseJson>();
									BaseJson send = new BaseJson();
									send.setSender(Conf.userID);
									send.setSender_icon(Conf.userImg);
									send.setTip_id("00");
									if (kind == 1) {
										send.setText(message);
										send.setMsg_type("1");
									} else if (kind == 2) {
										send.setMsg_type("2");
										send.setText(Conf.IMAGE_SERVER
												+ "xiaoxitupian/" + message);
									}
									send.setTime(upload.getTime());
									BaseJson system = new BaseJson();
									system.setTip_id("0");
									system.setMsg_type("1");
									system.setTime("00");
									system.setText(StringUtils.getResourse(R.string.str_feedback));
									list.add(send);
									list.add(system);
									if (page != 1) {
										adminAdapter.insertList(list);
										adminAdapter.notifyDataSetChanged();
									} else {
										adminAdapter = new AdminAdapter(
												context, list);
										lv_admin.setAdapter(adminAdapter);
									}
									getTotalHeightofListView(lv_admin);
									ed_talk_msg.setText("");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							pullScrollView.setheaderViewReset();
							pullScrollView.setfooterViewReset();
							pullScrollView.setVisibility(View.VISIBLE);

						}

					}
				});
		//
		// kjh.get(url, params, new HttpCallBack() {
		// @Override
		// public void onSuccess(Object obj) {
		// // TODO Auto-generated method stub
		// try {Log.e("管理员", obj.toString());
		// if (kind == 0) {
		// baseJson = new Gson().fromJson(obj.toString(),
		// BaseJson.class);
		// if (baseJson.getStatus().equals("200")) {
		// if (page == 1) {
		// adminAdapter = new AdminAdapter(context,
		// baseJson.getSys_msgs());
		// lv_admin.setAdapter(adminAdapter);
		//
		// } else {
		// adminAdapter.insertList(baseJson.getSys_msgs());
		// adminAdapter.notifyDataSetChanged();
		// }
		// getTotalHeightofListView(lv_admin);
		// } else {
		// if (page > 1)
		// --page;
		// }
		// } else {
		// BaseJson upload = new Gson().fromJson(obj.toString(),
		// BaseJson.class);
		// if (upload.getStatus().equals("200")) {
		// List<BaseJson> list = new ArrayList<BaseJson>();
		// BaseJson send = new BaseJson();
		// send.setSender(Conf.userID);
		// send.setSender_icon(Conf.userImg);
		// send.setTip_id("00");
		// if (kind == 1) {
		// send.setText(message);
		// send.setMsg_type("1");
		// } else if (kind == 2) {
		// send.setMsg_type("2");
		// send.setText(Conf.IMAGE_SERVER
		// + "xiaoxitupian/" + message);
		// }
		// send.setTime(upload.getTime());
		// BaseJson system = new BaseJson();
		// system.setTip_id("0");
		// system.setMsg_type("1");
		// system.setTime("00");
		// system.setText(getString(R.string.str_feedback));
		// list.add(send);
		// list.add(system);
		// if (page != 1) {
		// adminAdapter.insertList(list);
		// adminAdapter.notifyDataSetChanged();
		// } else {
		// adminAdapter = new AdminAdapter(context,
		// list);
		// lv_admin.setAdapter(adminAdapter);
		// }
		// getTotalHeightofListView(lv_admin);
		// ed_talk_msg.setText("");
		// }
		// }
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } finally {
		// pullScrollView.setheaderViewReset();
		// pullScrollView.setfooterViewReset();
		// pullScrollView.setVisibility(View.VISIBLE);
		// if (mDialog != null) {
		// mDialog.dismiss();
		// }
		// }
		// }
		//
		// @Override
		// public void onLoading(long count, long current) {
		// // TODO Auto-generated method stub
		// }
		//
		// @Override
		// public void onFailure(Throwable t, int errorNo, String strMsg) {
		// // TODO Auto-generated method stub
		// pullScrollView.setheaderViewReset();
		// pullScrollView.setfooterViewReset();
		// pullScrollView.setVisibility(View.VISIBLE);
		// if (kind == 0) {
		// if (page > 1)
		// --page;
		// }
		// if (mDialog != null) {
		// mDialog.dismiss();
		// }
		// ExitManager.getScreenManager().intentLogin(context,
		// StringUtils.httpRsponse(t.toString()));
		// }
		//
		// });
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	public void onScroll(int scrollY) {
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mDialog != null)
			mDialog.dismiss();
		if (mRefreshBroadcastReceiver != null)
			context.unregisterReceiver(mRefreshBroadcastReceiver);
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		// if (NetworkUtils.checkNet(context)) {
		page = 1;
		getHttpData(0);
		// } else {
		// BasicUtils.toast(getString(R.string.str_net_register));
		// pullScrollView.setheaderViewReset();
		// }
	}

	@Override
	public void loadMore() {

		// TODO Auto-generated method stub
		// if (SystemTool.checkNet(context)) {
		if (baseJson == null) {
			page = 1;
		} else
			++page;
		getHttpData(0);
		// } else {
		// ViewInject.toast(getString(R.string.str_net_register));
		// pullScrollView.setfooterViewReset();
		// }
	}

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
								BasicUtils
										.toast(StringUtils.getResourse(R.string.str_sdnull));
								return;
							}
							BitmapUtils.initPictureFile();
							// 调用系统的拍照功能
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							int currentapiVersion = android.os.Build.VERSION.SDK_INT;
							try {

								intent.putExtra(MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(BitmapUtils.pictureFile));
								startActivityForResult(intent, UPLOAD_CAMERA);
							} catch (Exception e) {
								// TODO: handle exception
							}

						} else {
							// 调用本地相册
							Intent intent = new Intent(Intent.ACTION_PICK, null);
							intent.setType("image/*");
							startActivityForResult(intent, UPLOAD_LOCAL);
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
		case UPLOAD_CAMERA:
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
				byte_img = BitmapUtils.Bitmap2Bytes(mBitmap);
				icon = BitmapUtils.pictureFile.toString()
						.replace(
								BitmapUtils.pictureFile.toString(),
								Conf.userID + "_" + System.currentTimeMillis()
										+ ".jpg");
				Intent intent = new Intent("jimome.action.uploadtalkpic");
				intent.putExtra("albumimg", icon);
				intent.putExtra("imagepath",
						BitmapUtils.pictureFile.getAbsolutePath());
				// Conf.img_byte = byte_img;
				context.sendBroadcast(intent);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				context.finish();
			}
			break;

		// 本地照片
		case UPLOAD_LOCAL:
			if (data == null) {
				return;
			}
			Uri imageuri = data.getData();
			String[] prStrings = { MediaStore.Images.Media.DATA };
			Cursor imageCursor = context.managedQuery(imageuri, prStrings,
					null, null, null);
			int imgpath = imageCursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			imageCursor.moveToFirst();
			String image_path = imageCursor.getString(imgpath);
			File photoName = new File(image_path);
			ContentResolver cr = context.getContentResolver();

			mBitmap = null;
			try {

				Options options = new Options();
				options.inJustDecodeBounds = false;
				options.inSampleSize = 2;
				mBitmap = BitmapFactory.decodeStream(
						cr.openInputStream(imageuri), null, options);
				// 创建图片缩略图
				byte_img = BitmapUtils.Bitmap2Bytes(mBitmap);
				icon = image_path.replace(image_path, Conf.userID + "_"
						+ System.currentTimeMillis() + ".jpg");
				Intent intent = new Intent("jimome.action.uploadtalkpic");
				intent.putExtra("albumimg", icon);
				intent.putExtra("imagepath", image_path);
				// Conf.img_byte = byte_img;
				context.sendBroadcast(intent);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				context.finish();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		tv_title.setText(R.string.str_admin);
		layout_back.setVisibility(View.VISIBLE);
		pullScrollView.setheaderViewReset();
		pullScrollView.setfooterViewReset();
		pullScrollView.setOnPullListener(this);
		initView();
		lv_admin.setOnItemClickListener(this);
		// if (SystemTool.checkNet(context)) {
		waitDialog();
		getHttpData(0);
	}

}

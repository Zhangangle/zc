package com.jimome.mm.receiver;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;

import com.baidu.mobstat.StatService;
import com.jimome.mm.activity.FragmentToActivity;
import com.jimome.mm.activity.MainFragmentActivity;
import com.jimome.mm.activity.SplashActivity;
import com.jimome.mm.activity.TalkActivity;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.bean.NewUser;
import com.jimome.mm.common.Conf;
import com.jimome.mm.database.UserDAO;
import com.jimome.mm.fragment.SelectFragment;
import com.jimome.mm.utils.PreferenceHelper;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushNotificationBuilder;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.unjiaoyou.mm.R;

public class MessageReceiver extends XGPushBaseReceiver {
	public static final String LogTag = "TPushReceiver";
	private List<BaseJson> list_base;
	private BaseJson base;
	private Context context;
	private SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	private UserDAO dao;
	private List<NewUser> list;
	private SharedPreferences shared;//
	private String CLIENTURL = Conf.URL + "msg/user/arrived";
	private String SERVERURL = Conf.URL + "msg/system/arrived";
	private String SYSTEMURL = Conf.URL + "msg/script/arrived";
	private String httpUrl;
	private String sender_type = "1";

	// private void show(Context context, String text) {
	// Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	// }

	// 通知展示
	@Override
	public void onNotifactionShowedResult(Context context,
			XGPushShowedResult notifiShowedRlt) {
		if (context == null || notifiShowedRlt == null) {
			return;
		}

	}

	@Override
	public void onUnregisterResult(Context context, int errorCode) {
		if (context == null) {
			return;
		}
		String text = null;
		if (errorCode == XGPushBaseReceiver.SUCCESS) {
			text = "反注册成功";
		} else {
			text = "反注册失败" + errorCode;
		}
		// //Log.e(LogTag, "132" + text);
		// show(context, "133" + text);

	}

	@Override
	public void onSetTagResult(Context context, int errorCode, String tagName) {
		if (context == null) {
			return;
		}
		String text = null;
		if (errorCode == XGPushBaseReceiver.SUCCESS) {
			text = "0\"" + tagName + "\"设置成功";
		} else {
			text = "1\"" + tagName + "\"设置失败,错误码：" + errorCode;
		}
		// //Log.e(LogTag, "138" + text);
		// show(context, "139" + text);

	}

	@Override
	public void onDeleteTagResult(Context context, int errorCode, String tagName) {
		if (context == null) {
			return;
		}
		String text = null;
		if (errorCode == XGPushBaseReceiver.SUCCESS) {
			text = "0\"" + tagName + "\"删除成功";
		} else {
			text = "1\"" + tagName + "\"删除失败,错误码：" + errorCode;
		}
		// //Log.e(LogTag, "164" + text);
		// show(context, "165" + text);

	}

	// 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
	@Override
	public void onNotifactionClickedResult(Context context,
			XGPushClickedResult message) {
		if (context == null || message == null) {
			return;
		}
		String text = null;
		if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
			// 通知在通知栏被点击啦。。。。。
			// APP自己处理点击的相关动作
			// 这个动作可以在activity的onResume也能监听，请看第3点相关内容
			text = "通知被打开 :" + message;
		} else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
			// 通知被清除啦。。。。
			// APP自己处理通知被清除后的相关动作
			text = "通知被清除 :" + message;
		}
		// Toast.makeText(context, "广播接收到通知被点击:" + message.toString(),
		// Toast.LENGTH_SHORT).show();
		// 获取自定义key-value
		String customContent = message.getCustomContent();
		if (customContent != null && customContent.length() != 0) {
			try {
				JSONObject obj = new JSONObject(customContent);
				// key1为前台配置的key
				if (!obj.isNull("key")) {
					String value = obj.getString("key");
					// Log.d(LogTag, "get custom value:" + value);
				}
				// ...
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		// APP自主处理的过程。。。
		// //Log.e(LogTag, "205" + text);
		// show(context, "206" + text);
	}

	@Override
	public void onRegisterResult(Context context, int errorCode,
			XGPushRegisterResult message) {
		// TODO Auto-generated method stub
		if (context == null || message == null) {
			return;
		}
		String text = null;

		shared = context.getSharedPreferences("jimome_tip",
				Context.MODE_PRIVATE);
		if (errorCode == XGPushBaseReceiver.SUCCESS) {
			if (!shared.getBoolean("XGPushRegister", false)) {// 设备注册成功
				StatService.onEvent(context, "xg-register-success",
						"eventLabel", 1);
			}
			text = message + "注册成功";
			shared.edit().putBoolean("XGPushRegister", true).commit();
			// 在这里拿token
			String token = message.getToken();
		} else {
			text = message + "注册失败，错误码：" + errorCode;
			shared.edit().putBoolean("XGPushRegister", false).commit();
		}
		// Log.e(LogTag, "162" + text);
		// show(context, "225" + text);
	}

	public static void setPushNotificationBuilder(Context context,
			int notificationBulderId,
			XGPushNotificationBuilder notificationBuilder) {

	}

	// 消息透传
	@Override
	public void onTextMessage(Context context, XGPushTextMessage message) {
		// TODO Auto-generated method stub
		String text = "收到消息:" + message.toString();
		this.context = context;
		// Log.e("187_收到消息", text);
		// 获取自定义key-value
		list_base = new ArrayList<BaseJson>();
		base = new BaseJson();
		String customContent = message.getCustomContent();
		if (customContent != null && customContent.length() != 0) {
			try {
				JSONObject obj = new JSONObject(customContent);
				// key1为前台配置的key

				if (!obj.isNull("flag")) {
					String value = obj.getString("flag");
					base.setFlag(obj.getString("flag").trim());
					// //Log.e(LogTag, "flag value:" + value);
				} else {
					base.setFlag("");
				}
				if (!obj.isNull("sender")) {
					String value = obj.getString("sender");
					base.setSender(obj.getString("sender").trim());
					// //Log.e(LogTag, "sender value:" + value);
				} else {
					base.setSender("");
				}
				if (!obj.isNull("sender_icon")) {
					String value = obj.getString("sender_icon");
					base.setSender_icon(obj.getString("sender_icon").trim());
					// Log.e(LogTag, "get custom value:" + value);
				} else {
					base.setSender_icon("");
				}
				if (!obj.isNull("text")) {
					String value = obj.getString("text");
					base.setText(obj.getString("text").trim());
					// Log.e(LogTag, "text value:" + value);
				} else {
					base.setText("");
				}
				if (!obj.isNull("url")) {
					String value = obj.getString("url");
					base.setUrl(obj.getString("url").trim());
					// Log.e(LogTag, "url value:" + value);
				} else {
					base.setUrl("");
				}
				if (!obj.isNull("img")) {
					String value = obj.getString("img");
					base.setImg(obj.getString("img").trim());
					// Log.e(LogTag, "img value:" + value);
				} else {
					base.setImg("");
				}
				if (!obj.isNull("name")) {
					String value = obj.getString("name");
					base.setName(obj.getString("name").trim());
					// Log.e(LogTag, "name value:" + value);
				} else {
					base.setName("");
				}
				if (!obj.isNull("coin")) {
					String value = obj.getString("coin");
					base.setCoin(obj.getString("coin").trim());
					// Log.e(LogTag, "coin value:" + value);
				} else {
					base.setCoin("");
				}
				if (!obj.isNull("charm")) {
					String value = obj.getString("charm");
					base.setCharm(obj.getString("charm").trim());
					// Log.e(LogTag, "charm value:" + value);
				} else {
					base.setCharm("");
				}
				if (!obj.isNull("small_url")) {
					String value = obj.getString("small_url");
					base.setSmall_url(obj.getString("small_url").trim());
					// Log.e(LogTag, "charm value:" + value);
				} else {
					base.setSmall_url("");
				}
				if (!obj.isNull("time")) {
					String value = obj.getString("time");
					base.setTime(obj.getString("time").trim());
					// Log.e(LogTag, "time value:" + value);
				} else {
					base.setTime("");
				}
				if (!obj.isNull("format_time")) {
					String value = obj.getString("format_time");
					base.setFormat_time(obj.getString("format_time").trim());
					// Log.e(LogTag, "format_time value:" + value);
				} else {
					base.setFormat_time("");
				}
				if (!obj.isNull("sender_nick")) {
					String value = obj.getString("sender_nick");
					base.setNick(obj.getString("sender_nick").trim());
					// Log.e(LogTag, "format_time value:" + value);
				} else {
					base.setNick("");
				}
				if (!obj.isNull("sender_age")) {
					String value = obj.getString("sender_age");
					base.setAge(obj.getString("sender_age").trim());
					// Log.e(LogTag, "format_time value:" + value);
				} else {
					base.setAge("");
				}
				if (!obj.isNull("sender_height")) {
					String value = obj.getString("sender_height");
					base.setHeight(obj.getString("sender_height").trim());
					// Log.e(LogTag, "format_time value:" + value);
				} else {
					base.setHeight("");
				}
				if (!obj.isNull("dialog_id")) {
					if (!obj.isNull("sender_type")) {
						sender_type = obj.getString("sender_type");
					}
					base.setDialog_id(obj.getString("dialog_id").trim());
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (sender_type.equals("1"))
								getHttpData(SYSTEMURL, base.getSender(),
										base.getDialog_id(), base.getTime());// 统计脚本抵达率的接口
							else
								getHttpData(CLIENTURL, base.getSender(),
										base.getDialog_id(), base.getTime());// 统计用户抵达率的接口
						}
					}).start();
				} else {
					base.setDialog_id("");
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub

							getHttpData(SERVERURL, base.getSender(),
									base.getDialog_id(), base.getTime());// 统计抵达率的接口
						}
					}).start();
				}
				list_base.add(base);
				// ...
				SelectFragment.sendMsg = true;// 刷新消息页面
				if (TalkActivity.talk_id.equals("")
						|| obj.getString("sender") == null
						|| !obj.getString("sender").trim()
								.equals(TalkActivity.talk_id.trim())) {
					// //Log.e(LogTag, "flag value");

					if (base.getDialog_id().equals("")
							|| PreferenceHelper.readString(context, "auth",
									"pushed") == null
							|| PreferenceHelper.readString(context, "auth",
									"pushed").equals("")
							|| PreferenceHelper.readString(context, "auth",
									"pushed").equals("On")) {
						if (!obj.isNull("text")) {
							showNotify(obj.getString("text"), base);
						} else {
							showNotify(base.getNick() + "给您发送了一条新消息", base);
						}
					}
				} else {
					Intent intent2 = new Intent();
					intent2.setAction("com.action.talkmsg");
					intent2.putExtra("msg", (Serializable) list_base);
					context.sendBroadcast(intent2);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		// APP自主处理消息的过程...
		// Log.e(LogTag, text);

	}

	public void showNotify(String message, BaseJson base) {
		// 先设定RemoteViews
		try {

			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			RemoteViews view_custom = new RemoteViews(context.getPackageName(),
					R.layout.view_custom);
			// 设置对应IMAGEVIEW的ID的资源图片
			view_custom.setImageViewResource(R.id.custom_icon, R.drawable.logo);
			view_custom.setTextViewText(R.id.tv_custom_title,
					context.getString(R.string.app_name));
			view_custom.setTextViewText(R.id.tv_custom_content, message);
			view_custom.setTextViewText(R.id.tv_custom_time,
					format.format(new Date(System.currentTimeMillis())));
			Builder mBuilder = new Builder(context);
			mBuilder.setContent(view_custom).setAutoCancel(true)
			// 通知产生的时间，会在通知信息里显示
					.setTicker("您有新的私信").setPriority(1000)// 设置该通知优先级
															// Notification.PRIORITY_DEFAULT
					.setOngoing(false)// 不是正在进行的 true为正在进行 效果和.flag一样
					.setSmallIcon(R.drawable.logo);
			if (Conf.userID == null || Conf.userID.equals("")) {
				dao = new UserDAO(context);
				list = dao.findAll();
				if (list != null && list.size() > 0) {
					if (list.get(0).getUser_id() != null)
						Conf.userID = list.get(0).getUser_id();
				}
			}
			Intent resultIntent;
			if (!MainFragmentActivity.flag_status) {
				resultIntent = new Intent(context, SplashActivity.class);
			} else {
				if (base.getDialog_id() != null
						&& !base.getDialog_id().equals("")
						&& TalkActivity.talk_id.equals("")) {
					resultIntent = new Intent(context, TalkActivity.class);
					resultIntent.putExtra("person", base);
				} else if(base.getDialog_id().equals("")){
					resultIntent = new Intent(context,
							FragmentToActivity.class);
					resultIntent.putExtra("who", "system");
				}else
					resultIntent = new Intent(context,
							MainFragmentActivity.class);
			}

			resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(pendingIntent);
			Notification notify = mBuilder.build();
			notify.defaults |= Notification.DEFAULT_SOUND;
			notify.defaults |= Notification.DEFAULT_LIGHTS;
			notify.contentView = view_custom;
			mNotificationManager.notify(100, notify);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void getHttpData(String url, String sender, String dialog_id,
			String log_time) {
		// TODO Auto-generated method stub
		try {
			// if (url.equals(CLIENTURL))
			// httpUrl = url + "?sender=" + sender + "&dialog_id=" + dialog_id
			// + "&log_time=" + log_time;
			// else
			httpUrl = url;// + "?sender=" + sender + "&log_time=" + log_time;
			// 创建httpRequest对象
			HttpGet httpRequest = new HttpGet(httpUrl);
			httpRequest.addHeader("Authorization",
					PreferenceHelper.readString(context, "auth", "token"));
			// 取得HttpClient对象
			HttpClient httpclient = new DefaultHttpClient();
			// 请求HttpClient，取得HttpResponse
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			// 请求成功
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 取得返回的字符串
				String strResult = EntityUtils.toString(
						httpResponse.getEntity(), "UTF-8");
				JSONObject jsonObj = new JSONObject(strResult);
				// Log.e("推送消息成功", strResult + "\n" +
				// jsonObj.getString("status"));

			} else {
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}
	}
}

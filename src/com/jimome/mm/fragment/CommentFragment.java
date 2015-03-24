package com.jimome.mm.fragment;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.jimome.mm.adapter.InboxNoticeAdapter;
import com.jimome.mm.adapter.ReplyPraiseAdapter;
import com.jimome.mm.bean.BaseJson;
import com.jimome.mm.common.Conf;
import com.jimome.mm.request.CacheRequest;
import com.jimome.mm.request.CacheRequestCallBack;
import com.jimome.mm.utils.BasicUtils;
import com.jimome.mm.utils.ExitManager;
import com.jimome.mm.utils.NetworkUtils;
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
 * 评论列表页面
 * 
 * @author admin
 * 
 */
public class CommentFragment extends BaseFragment implements OnPullListener {

	@ViewInject(R.id.layout_back)
	private LinearLayout layout_back;
	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.tv_message_error)
	private TextView tv_message_error;
	@ViewInject(R.id.pushscroll_comment)
	private PullScrollView pushscroll_comment;
	private MyListView commentView;
	private String type;
	private List<BaseJson> list_BaseJson;
	private BaseJson detailPho;
	private ReplyPraiseAdapter replyAdapter;
	private InboxNoticeAdapter noticeAdapter;
	private int page = 1;
	private Activity context;
	
	public CommentFragment() {

	}

	public CommentFragment(String str) {
		type = str;
	}

	@Override
	protected View initView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		return arg0.inflate(R.layout.fragment_comment, arg1, false);
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
	}

	@OnClick ( R.id.layout_back)
	protected void onClickView(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_back:
			context.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
//		if (SystemTool.checkNet(context)) {
			page = 1;
			getHttpData();
//		} else {
//
//			ViewInject.toast(getString(R.string.str_net_register));
//			pushscroll_comment.setheaderViewReset();
//		}
	}

	@Override
	public void loadMore() {
		// TODO Auto-generated method stub

//		if (SystemTool.checkNet(context)) {
			if (detailPho != null && list_BaseJson != null
					&& list_BaseJson.size() > 0) {
				++page;
			} else {
				if (detailPho == null) {
					page = 1;
				} else if (page != 1)
					++page;
			}
			getHttpData();
//		} else {
//
//			ViewInject.toast(getString(R.string.str_net_register));
//			pushscroll_comment.setfooterViewReset();
//		}
	}

	private void waitDialog() {
		mDialog.show();
	}

	private void setView() {
		if (type.equals("comment")) {
			if (list_BaseJson == null || page == 1) {
				list_BaseJson = detailPho.getComments();
				replyAdapter = new ReplyPraiseAdapter(context, type,
						detailPho.getComments());
				commentView.setAdapter(replyAdapter);
				pushscroll_comment.post(new Runnable() {
					@Override
					public void run() {
						pushscroll_comment.scrollTo(0, 0);
					}
				});
			} else {
				list_BaseJson.addAll(detailPho.getComments());
				replyAdapter.insertData(detailPho.getComments());
				replyAdapter.notifyDataSetChanged();
			}
		} else if (type.equals("praise")) {
			if (list_BaseJson == null || page == 1) {
				list_BaseJson = detailPho.getGreets();
				replyAdapter = new ReplyPraiseAdapter(context, type,
						detailPho.getGreets());
				commentView.setAdapter(replyAdapter);
				pushscroll_comment.post(new Runnable() {
					@Override
					public void run() {
						pushscroll_comment.scrollTo(0, 0);
					}
				});
			} else {
				list_BaseJson.addAll(detailPho.getGreets());
				replyAdapter.insertData(detailPho.getGreets());
				replyAdapter.notifyDataSetChanged();
			}
		} else {
			if (list_BaseJson == null || page == 1) {
				list_BaseJson = detailPho.getSys_msgs();
				noticeAdapter = new InboxNoticeAdapter(context,
						detailPho.getSys_msgs());
				commentView.setAdapter(noticeAdapter);
				pushscroll_comment.post(new Runnable() {
					@Override
					public void run() {
						pushscroll_comment.scrollTo(0, 0);
					}
				});
			} else {
				list_BaseJson.addAll(detailPho.getSys_msgs());
				noticeAdapter.insertData(detailPho.getSys_msgs());
				noticeAdapter.notifyDataSetChanged();
			}
		}
	}

	private void getHttpData() {
		int cache_time = 0;
		RequestParams params = new RequestParams();
		String key = "";
		try {
			params.addQueryStringParameter("cur_user", Conf.userID);
			params.addQueryStringParameter("page", String.valueOf(page));
			if (type.equals("comment"))
				key = "msg/comment";
			else if (type.equals("praise"))
				key =  "msg/greet";
			else
				key =  "msg/sys";
		} catch (Exception e) {
			// TODO: handle exception
		}
		params.addHeader("Authorization",
				PreferenceHelper.readString(context, "auth", "token"));
		CacheRequest.requestGET(context, key, params, key, cache_time, new CacheRequestCallBack() {
			
			@Override
			public void onFail(HttpException e, String result, String json) {
				// TODO Auto-generated method stub
				if(mDialog != null){
					mDialog.dismiss();
				}
				pushscroll_comment.setheaderViewReset();
				pushscroll_comment.setfooterViewReset();
				if (page > 1)
					--page;ExitManager.getScreenManager().intentLogin(context,
							e.getExceptionCode() + "");
				if(json.equals("")){
					BasicUtils.toast(StringUtils.getResourse(R.string.str_net_register));
					return;
				}
			}
			
			@Override
			public void onData(String json) {
				// TODO Auto-generated method stub
				if(mDialog != null){
					mDialog.dismiss();
				}
				if(json.equals("")){
//					BasicUtils.toast(getString(R.string.str_net_register));
					return;
				}
				try {
					detailPho = new Gson().fromJson(json,
							BaseJson.class);
					if (detailPho != null
							&& detailPho.getStatus().equals("200")) {
						tv_message_error.setVisibility(View.GONE);
						setView();// 设置
					} else {
						if (page == 1) {
							if (type.equals("comment"))
								tv_message_error
										.setText(R.string.str_speak_error);
							else if (type.equals("praise"))
								tv_message_error
										.setText(R.string.str_greet_error);
							tv_message_error.setVisibility(View.VISIBLE);
						}
						if (page > 1)
							--page;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					pushscroll_comment.setheaderViewReset();
					pushscroll_comment.setfooterViewReset();
				}
			}
		});
//		kjh.get(url, params, new HttpCallBack() {
//
//			@Override
//			public void onSuccess(Object obj) {
//				// TODO Auto-generated method stub
//				try {
//					detailPho = new Gson().fromJson(obj.toString(),
//							BaseJson.class);
//					if (detailPho != null
//							&& detailPho.getStatus().equals("200")) {
//						tv_message_error.setVisibility(View.GONE);
//						setView();// 设置
//					} else {
//						if (page == 1) {
//							if (type.equals("comment"))
//								tv_message_error
//										.setText(R.string.str_speak_error);
//							else if (type.equals("praise"))
//								tv_message_error
//										.setText(R.string.str_greet_error);
//							tv_message_error.setVisibility(View.VISIBLE);
//						}
//						if (page > 1)
//							--page;
//					}
//
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} finally {
//					pushscroll_comment.setheaderViewReset();
//					pushscroll_comment.setfooterViewReset();
//
//					if (mDialog != null) {
//						mDialog.dismiss();
//					}
//				}
//			}
//
//			@Override
//			public void onLoading(long count, long current) {
//				// TODO Auto-generated method stub
//			}
//
//			@Override
//			public void onFailure(Throwable t, int errorNo, String strMsg) {
//				// TODO Auto-generated method stub
//				pushscroll_comment.setheaderViewReset();
//				pushscroll_comment.setfooterViewReset();
//				if (page > 1)
//					--page;
//				if (mDialog != null) {
//					mDialog.dismiss();
//				}
//				ExitManager.getScreenManager().intentLogin(context,
//						StringUtils.httpRsponse(t.toString()));
//			}
//
//		});
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mDialog != null)
			mDialog.dismiss();
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

	@Override
	protected void initWidget() {
		// TODO Auto-generated method stub
		if (type.equals("comment")) {
			tv_title.setText(R.string.str_inbox_newcomment);
		} else if (type.equals("praise")) {
			tv_title.setText(R.string.str_inbox_praiselist);
		} else {
			tv_title.setText(R.string.str_system_list);
		}

		layout_back.setVisibility(View.VISIBLE);

		pushscroll_comment.setheaderViewReset();
		pushscroll_comment.setfooterViewReset();

		commentView = new MyListView(context);
		commentView.setDivider(null);
		commentView.setCacheColorHint(0);
		commentView.setSelector(android.R.color.transparent);
		pushscroll_comment.addBodyView(commentView);
		pushscroll_comment.setOnPullListener(this);
		if (!NetworkUtils.checkNet(context)) {
			BasicUtils.toast(StringUtils.getResourse(R.string.str_net_register));
			if (type.equals("comment"))
				tv_message_error.setText(R.string.str_speak_error);
			else if (type.equals("praise"))
				tv_message_error.setText(R.string.str_greet_error);
			tv_message_error.setVisibility(View.VISIBLE);
		} else {
			waitDialog();
			getHttpData();
		}
	}
}

package com.jimome.mm.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unjiaoyou.mm.R;
import com.jimome.mm.bean.BaseJson;

/**
 * 常见问题适配
 * 
 * @author Administrator
 * 
 */
//public class QuestionAdapter extends BaseExpandableListAdapter {
//	private List<String> groupData;
//	private List<List<String>> childrenData;
//	private Context context;
//	TextView question;
//	TextView answer;
//	public QuestionAdapter(Context context, List<BaseJson> list_question) {
//		this.context = context;
//		int queSize = list_question.size();
//		groupData = new ArrayList<String>();
//		childrenData = new ArrayList<List<String>>();
//		List<String> child;
//		for (int i = 0; i < queSize; i++) {
//			child = new ArrayList<String>();
//			groupData.add(list_question.get(i).getQuestion());
//			child.add(list_question.get(i).getAnswer());
//			childrenData.add(child);
//		}
//	}
//
//	@Override
//	public Object getChild(int groupPosition, int childPosition) {
//		return childrenData.get(groupPosition).get(childPosition);
//	}
//
//	@Override
//	public long getChildId(int groupPosition, int childPosition) {
//		return 0;
//	}
//
//	@Override
//	public View getChildView(int groupPosition, int childPosition,
//			boolean isLastChild, View convertView, ViewGroup parent) {
//		TextView text = null;
////		if (convertView != null) {
////			text = (TextView) convertView;
////			text.setText(context.getString(R.string.str_answer)
////					+ childrenData.get(groupPosition).get(childPosition));
////		} else {
////			text = createView("child",
////					childrenData.get(groupPosition).get(childPosition),
////					childPosition);
////		}
////		return text;
//		if (convertView == null) {
//			convertView = LayoutInflater.from(context).inflate(
//					R.layout.list_item_faq, parent, false);
//		}
////		ImageView icon = BaseAdapterHelper
////				.get(convertView, R.id.list_faq_question);
//		question = BaseAdapterHelper.get(convertView, R.id.list_faq_question);
//		answer = BaseAdapterHelper.get(convertView, R.id.list_faq_answer);
//		question.setText((groupPosition + 1) + "." + groupData.get(groupPosition));
//		answer.setText(context.getString(R.string.str_answer) + childrenData.get(groupPosition).get(childPosition));
//		answer.setTextSize(14);
//		return convertView;
//	}
//
//	@Override
//	public int getChildrenCount(int groupPosition) {
//		return childrenData.get(groupPosition).size();
//	}
//
//	@Override
//	public Object getGroup(int groupPosition) {
//		return groupData.get(groupPosition);
//	}
//
//	@Override
//	public int getGroupCount() {
//		return groupData.size();
//	}
//
//	@Override
//	public long getGroupId(int groupPosition) {
//		return 0;
//	}
//
//	@Override
//	public View getGroupView(int groupPosition, boolean isExpanded,
//			View convertView, ViewGroup parent) {
//		LinearLayout group = new LinearLayout(context);
//		group.setGravity(Gravity.CENTER_VERTICAL);
//		group.setOrientation(LinearLayout.HORIZONTAL);
//		group.setBackgroundResource(R.drawable.shape_graytext);
//		group.setPadding(10, 15, 10, 15);
//		TextView tv_group = new TextView(context);
//		tv_group.setTextColor(context.getResources().getColor(R.color.black));
//		tv_group.setText((groupPosition + 1) + "." + groupData.get(groupPosition));
//		group.addView(tv_group);
//		return group;
//	}
//
//	@Override
//	public boolean hasStableIds() {
//		return false;
//	}
//
//	@Override
//	public boolean isChildSelectable(int groupPosition, int childPosition) {
//		return false;
//	}
//
//	private TextView createView(String type, String content, int pos) {
////		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
////				ViewGroup.LayoutParams.WRAP_CONTENT,
////				ViewGroup.LayoutParams.WRAP_CONTENT);
//		TextView text = new TextView(context);
////		text.setLayoutParams(layoutParams);
//		text.setTextColor(context.getResources().getColor(R.color.darkgray));
//		text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
//		if (type.equals("group")) {
//			text.setText((pos + 1) + "." + content);
//			text.setTextSize(16);
//			text.setPadding(75, 10, 0, 5);
//		} else {
//			text.setText(context.getString(R.string.str_answer) + content);
//			text.setTextSize(14);
//			text.setPadding(10, 0, 0, 10);
//		}
//		return text;
//	}
//}
public class QuestionAdapter extends BaseAdapter {

	private Context context;
	private List<BaseJson> list_question;
	private int index = -1;
	
	public QuestionAdapter(Context context, List<BaseJson> list_question) {
		this.context = context;
		this.list_question = list_question;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_question.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return list_question.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	public void insertData(List<BaseJson> list) {
		list_question.addAll(list);
	}

	public List<BaseJson> allDate() {
		return list_question;
	}

	@Override
	public View getView(final int pos, View view, ViewGroup viewGrop) {
		// TODO Auto-generated method stub
		try {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.list_item_question, viewGrop, false);
			}

			LinearLayout layout_question = BaseAdapterHelper.get(view, R.id.layout_question);
			final LinearLayout layout_answer = BaseAdapterHelper.get(view, R.id.layout_answer);
			TextView tv_question = BaseAdapterHelper
					.get(view, R.id.tv_question);
			final TextView tv_answer = BaseAdapterHelper.get(view, R.id.tv_answer);
			tv_question.setText( list_question.get(pos).getQuestion());
			final ImageView img_arrow = BaseAdapterHelper.get(view, R.id.img_arrow);
			
			layout_question.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(index != pos){
						index =pos;
						if(layout_answer.getVisibility() == 0){
							layout_answer.setVisibility(View.GONE);
							img_arrow.setImageResource(R.drawable.faq_right_arrow);
						}else{
							layout_answer.setVisibility(View.VISIBLE);
							img_arrow.setImageResource(R.drawable.faq_down_arrow);
						}
						tv_answer.setText(context.getString(R.string.str_answer)
								+ list_question.get(pos).getAnswer());
					}else{
						index = -1;
						img_arrow.setImageResource(R.drawable.faq_right_arrow);
						layout_answer.setVisibility(View.GONE);
						notifyDataSetChanged();
					}
				}
			});
			

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return view;
	}

}

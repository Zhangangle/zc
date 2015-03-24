package com.jimome.mm.adapter;

import java.util.ArrayList;
import java.util.List;

import com.unjiaoyou.mm.R;
import com.jimome.mm.bean.PhotoImage;
import com.jimome.mm.utils.BitmapUtils;
import com.jimome.mm.utils.LogUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoUploadAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<PhotoImage> images;
	private Handler handler;
	public PhotoUploadAdapter (Context c,ArrayList<PhotoImage> images,Handler handler){
		context = c;
		this.images = images;
		this.handler = handler;
	}
	
	public void addData(PhotoImage image){
		images.add(image);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return images.size()+1;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return images.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
	
		try {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.grid_item_photo, parent,false); 
			}
			
			final Button add = BaseAdapterHelper.get(convertView, R.id.img_item_add);
			final ImageView photo = BaseAdapterHelper.get(convertView, R.id.img_item_photo);
			TextView tv_item_tip = BaseAdapterHelper.get(convertView, R.id.tv_item_tip);
			
			if(images != null && position < images.size()){
				photo.setVisibility(View.VISIBLE);
				add.setVisibility(View.GONE);
				tv_item_tip.setVisibility(View.GONE);
				PhotoImage image = images.get(position);
				Bitmap bitmap = images.get(0).getBitmap();
				if(bitmap != null){
					photo.setImageBitmap(bitmap);
				}
			}else{
				if(  images.size() == 9){
					tv_item_tip.setVisibility(View.GONE);
					photo.setVisibility(View.GONE);
					add.setVisibility(View.GONE);
				}else{
					if(position == 0){
						tv_item_tip.setVisibility(View.VISIBLE);
					}else{
						tv_item_tip.setVisibility(View.GONE);
					}
					photo.setVisibility(View.GONE);
					add.setVisibility(View.VISIBLE);
					
				}
				
				add.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(0);
					}
				});
				
			}
		
		} catch (Exception e) {
			// TODO: handle exception
		}
		return convertView;
	}

}

package com.jimome.mm.bean;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


public class PhotoImage implements Serializable {

	private String icon;
	private Bitmap bitmap;
	private String imgPath;
	
	public PhotoImage(){
		
	}
	
	public PhotoImage (Bitmap img, String str){
		bitmap = img;
		icon = str;
	}
	
	public PhotoImage (String path, String str){
		imgPath = path;
		icon = str;
	}
	
	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	
}

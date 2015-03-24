package com.jimome.mm.request;

import com.lidroid.xutils.exception.HttpException;


public interface CacheRequestCallBack {

	public void onData(String json);
	public void onFail(HttpException e, String result,String json);
}

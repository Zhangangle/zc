package com.jimome.mm.common;

import java.util.ArrayList;

import com.jimome.mm.bean.PhotoImage;

public class Conf {

	/** 后台服务器URL地址 ***/
	// public static final String URL="http://114.215.133.82:8000/";//外网测试地址
	public static final String URL = "http://api.347.cc/";// 外网服务器cjapi.347.cc  api.347.cc
//	 public static final String URL = "http://10.0.0.190:8000/"; // 内网测试服务器
	public static final String APPKEY = "32f31eff6157";// 27fe7909f8e8
	// 填写从短信SDK应用后台注册得到的APPSECRET
	public static final String APPSECRET = "d3b11bc0661b33924e81eec447723582";// 3c5264e7e05b8860a9b98b34506cfa6e

	// public static final String SERCET_KEY = "www.347.cc";// 加密签名
	// 版本更新地址
	public static final String UPDATE_SERVERURL = "http://up.347.cc/wzmup.txt";
	// 身材秀广告地址
	public static final String AD_SERVERURL = "http://up.347.cc/ad.txt";
	// ipweb地址
	public static final String IPWEB_SERVERURL = "http://up.347.cc/ipweb.txt";
	// 公网（外网）IP地址
	public static String PublicNetwork = "";
	// public static final String CID ="un_baidu";// 一级渠道
	// public static final String CID="un_myapp";
	// public static final String CID="un_wandoujia";
	// public static final String CID="un_wzm";
	public static  String CID = "";
	public static String address = "";// 地址
	public static String splitaddress = "";// 省
	public static String province = "";
	public static String city = "";// 市
	public static String latitude = "";// 纬度
	public static String lontitude = "";// 经度
	public static String IMEI = ""; // 设备唯一码
	public static String version = ""; // 软件版本
	public static String SIM = ""; // SIM类型
	public static String Model = ""; // 手机型号
	public static String userID = ""; // 用户ID
	public static String userName = "";
	public static String gender = ""; // 1：表示男用户 2：表示女用户
	public static String password = "";// 密码
	public static String cur_userID = "";// 查看的用户ID
	public static String userImg = "";
	public static String userCharm = "";// 魅力
	public static String userCoin = "";// 金币
	public static String userMsg_counts = "";
	public static String webPayurl = "";// web页面
	public static String Coins = "0";
	public static String user_VIP = "";// 是否为VIP
	public static String pushTime = "";
	public static boolean flagTalk = false;
	public static String oppName = "";// 对方的昵称
	public static String imsi = "";
	public static String Operators = "";
	// 屏幕密度
	public static int densityDPI = 0;
	public static int width = 0;
	public static int height = 0;
	public static float density = 0;
	/** 阿里云服务 **/
	public static final String ACCESS_BUCKETNAME = "datinguser";
	public static final String ACCESS_VIDEOBUCKETNAME = "datingvideo";
	public static final String IMAGE_SERVER = "http://img.347.cc/";
	public static final String VIDEO_SERVER = "http://shipin.347.cc/";
	// 点乐积分墙
	public static String dl_ID = "5458292fd37c7a7d6551847b30e5d165";
	// 有米积分墙ID
	public static String ym_ID = "a6acffe0a1a24ae5";
	public static String ym_KEY = "0ff95137b611a4a7";

	// 腾讯
	public static final String Tencent = "1103468475";//

	public static byte[] img_byte = null;
	public static ArrayList<PhotoImage> images;

	// 冷笑话
//	public static final String LXH_SERVERURL = "http://up.347.cc/lxh.txt";
//	public static String LXH_APPNAME = "";
//	public static String LXH_APPICON = "";
//	public static String LXH_APPDOWN = "";

	public static final String VISITOR_AD = "http://img.347.cc/app/material/caller.jpg";
	public static final String GIFT_AD = "http://img.347.cc/app/material/gift-up-dialog.jpg";
	// 支付宝支付
	public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALQLI6Wp3FQjd2WLuQbmAMyy1FbIDdBEukwrFjMgbfOmijJ4Ywsv3LpJwlAXO2ec1K2TcIjkcJ0iZiSh2uDeaZDPSrgLWaZH3K46HgPLXcUY+fJWdxx+MxO1lvWhrZx8cVe+dFTg2sO5ldRnmbl2PV62+66+vDEFMTb+Y68+Ab//AgMBAAECgYAMvGz9HxLy8L9Vc5jXIBYWZOAYc3ZmsTXshW/Alh6xCark/xcih3Q5yVXxjHMrVTDGRpPDW0WMGA7ZsIlnNtSIx3grGgZXRSHkmZMA/0n+eauX6yeT8LYU0AnPZvzi1xPR7/r+flZaBEFo2RNWot81CfCWRVN9YBbru+8UhtMLoQJBAOFoRnSDvGIv92Lznj44R4E3UnwZyABD5SPDm73qW2TSNF1lH5Ido0c/XTgRjAriczxjvFchs3vHnfsjzfn/KmsCQQDMerV/yM9rr0TlUuWJAWPEtJ8J4a/rmZ3/PL3ngHKSSLfSUccD0FLiWwsJwEv/59ikB3rNjYe4cY8i5p50aA29AkAywi6LclbrrW2CBOYvkLDZckHK3GtjU+a3pQbfP3URQRy1o132o2HvW198jscAdHoKsVLqNBac2xjCQw/RKhq9AkARtNKApTfo4D18CiicK4WjI+zdozBjm2LGFlFveXzyU9+vE6vDFcgHl9Cl4IP0PfRRh+BHamkFPLgjUosIE3aVAkBSxgUJtnYcjWyWaYK3WQL2MtdnEIBxK9p/i7WPTdzfCosaROYIzuZ1t22Fh+ULTsaMlvDXeqRGTjgdC58/32iK";
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	// 优贝短信支付
	public static final String UPAY_APPKEY = "10000252";
	public static final String UPAY_APPSECRET = "E0878624EF2F44C6DC758DD3601D33B6";
	public static final String UPAY_GOODSKEY_SIGN = "libao";
	public static final String UPAY_GOODSKEY_VIDEO = "shipin";

	// 泡妞恋爱终极教程
	public static final String READ_ICON = "http://img.347.cc/app/material/paoniulogo.jpg";
	public static final String READ_MSG = "http://img.347.cc/app/material/paoniujiaocheng.png";
	// 推广APP
	public static final String AD_APPURL = "http://up.347.cc/chapingad.txt";

}

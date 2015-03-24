package com.jimome.mm.bean;

public class NewUser {

	private String status;// 返回的状态
	private String user_id;// 用户ID
	private String nick;// 昵称
	private String icon;// 头像
	private String gender;// 性别
	private String charm;// 魅力
	private String coin;// 金币
	private String is_vip;// 是否为VIP
	private String msg_counts;// 消息数量
	// private String weixin;// 微信号
	private String open_id;
	private String token;

	public String getOpen_id() {
		return open_id;
	}

	public void setOpen_id(String open_id) {
		this.open_id = open_id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public NewUser() {

	}

	public NewUser(String user_id, String open_id, String token) {
		this.user_id = user_id;
		this.open_id = open_id;
		this.token = token;
	}

	// public String getWeixin() {
	// return weixin;
	// }
	//
	// public void setWeixin(String weixin) {
	// this.weixin = weixin;
	// }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCharm() {
		return charm;
	}

	public void setCharm(String charm) {
		this.charm = charm;
	}

	public String getCoin() {
		return coin;
	}

	public void setCoin(String coin) {
		this.coin = coin;
	}

	public String getIs_vip() {
		return is_vip;
	}

	public void setIs_vip(String is_vip) {
		this.is_vip = is_vip;
	}

	public String getMsg_counts() {
		return msg_counts;
	}

	public void setMsg_counts(String msg_counts) {
		this.msg_counts = msg_counts;
	}

}

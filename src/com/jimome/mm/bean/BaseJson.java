package com.jimome.mm.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class BaseJson implements Serializable {

	private String status; // 状态
	private String comment_nums;// 评论数
	private String greet_nums;// 求更多数
	private String gift_nums;// 礼物数量
	private String time;// 发送图片时间
	private String is_greet;// 是否发生求更多 0：还没有； 1：已经关注

	private String id;// 礼物ID
	private String name;// 礼物名称
	private String icon;// 礼物头像
	private String num;// 礼物个数
	private String coin;// 金币多少
	private String meiLi;// 魅力值
	private String user_charm;// 当前用户魅力值
	private String rmb;// 参考价
	private String img;// 图片名
	private String charm;// 魅力值

	private String sender;// 发送者ID
	private String nick;// 发送者昵称
	private String age;// 发送者年龄
	private String height;// 发送者身高
	private String msg_nums;// 未读信息数
	private String msg_text;// 消息信息
	private String dialog_id;// 对话ID
	private ArrayList<BaseJson> dialogs;// 消息对话
	private String sender_nick;// 发送者昵称
	private String sender_icon;// 发送者头像
	private ArrayList<BaseJson> messages;
	private String sys_msg_nums;// 未读系统消息数

	private String text;//
	private String video_url;
	private LinkedList<BaseJson> uploads;
	private ArrayList<BaseJson> items;// 兑换物品列表
	private String user_id;// 用户ID
	private String gender;// 性别
	private String is_vip;// 是否为VIP
	private String msg_counts;// 消息数量
	private String weixin;// 微信号

	private String[] show;// 女神秀图片
	private String[] photo;// 身材秀、美套图
	private String[] video;// 视频秀
	private String[] visiteds;// 访问记录
	private String intro;// 介绍
	private String per_id;// 用户ID

	private String birthday;// 生日
	private String cellphone;// 手机号
	private String address;// 地址
	private ArrayList<BaseJson> gifts;
	private String is_focus;// 是否关注
	private ArrayList<BaseJson> comments;// 评论列表
	private ArrayList<BaseJson> greets;// 求更多列表
	private ArrayList<BaseJson> sys_msgs;// 系统消息

	private String photo_nums;// 照片数量
	private String photo_id;// 图片ID
	private String city;// 城市
	private String greeted_nums;// 喜欢TA的人数
	private String is_certified;// 是否认证
	private String url;// 图片路径
	private LinkedList<BaseJson> photos;// 美套图列表
	private ArrayList<BaseJson> users;// 附近列表
	private String is_signin;// 是否签到 0：表示没有签到 1：表示签过到了
	private ArrayList<BaseJson> products;// 充值
	private String left;// vip左侧文字（原售价）

	private String show_nums;
	private String video_nums;
	private String[] texts;// 聊天
	private String flag;
	private String small_url;
	private String days;
	private String style;// @60*90.jpg
	private String total_nums;// 总共的Msg数量
	private String format_time;// 格式化的时间
	private String msg_id;// 用户反馈ID
	private String pid; // ID
	private String seller;// 支付帐号
	private String order_id;// 订单号
	private String amount;// 金额
	private String tip;// 提示VIP
	private String info_tip;// 女性用户提示完善信息
	private String msg;// 视频秀返回值
	private String distance;// 相距距离
	private String video_id;// 视频id
	private String[] coins;// 签到金币
	private ArrayList<BaseJson> tasks;// 任务列表
	private String award;// 任务奖励
	private String require_nums;// 要完成的任务总数
	private String complete_nums;// 已经完成的任务数

	private String alipay;// 支付宝帐号_我的资料
	private String gift_tip;// 礼物提醒文字
	private String can_send;

	private String go_to;// 聊天页面女性用户跳转判断
	private String focuser_nums;// 粉丝数量
	private String focused_nums;// 被关注者数量

	private ArrayList<BaseJson> focuseds;// 关注
	private ArrayList<BaseJson> focusers;// 粉丝

	private ArrayList<BaseJson> faqs;// 常见问题
	private String question;// 问题
	private String answer;// 回答
	private boolean gift_dot;// 是否有新的礼物
	private boolean focuser_dot;// 是否有新的粉丝
	private boolean visiter_dot;// 是否有新的访客
	private boolean info_dot;// 个人资料是否完整
	private String[] shows; // 个人主页图片轮播
	private String purpose;// 加入目的
	private String last_login;// 最后登陆
	private String tip_id;// 跳转类型
	private String msg_type;// 1：文本 2：图片
	private int can_retrieve;// 礼物是否可取回 1:表示未送出 0：表示已送出
	private int remain_days;// 剩余天数
	private String token;// 签名
	private ArrayList<BaseJson> pays;//充值滚动广告
	// 一元云购
		private ArrayList<BaseJson> goods;// 所有商品列表
		// private String good_id;// 商品ID
		// private String good_name;// 商品名称
		private String price;// 商品价格
		// private String good_icon;// 商品头像
		private String join_nums;// 参与人数
		private String remain_nums;// 剩余人数
		private String description;// 商品描述
		private String period;// 当前期数
		private String[] images;// 商品图片详细列表
		private String last_user;// 上期获奖者
		private String last_icon;// 上期获奖者头像
		private String last_name;// 上期获奖者名称
		private String last_address;// 上期获奖者地址
		private String last_code;// 幸运号码
		private String last_buy_time;// 上期获奖这时间
		private String last_publish_time;// 上期揭晓时间
		// private String cart_num;// 购物车数量
		private String owner_id;// 本期获奖者
		private String owner_icon;// 本期获奖者头像
		private String owner_name;// 本期获奖者名称
		private String owner_address;// 本期获奖者地址
		private String lucky_code;// 本期获奖号码
		private String buy_time;// 本期获奖这时间
		private String publish_time;// 本期揭晓时间
		private ArrayList<BaseJson> records;// 商品云购记录
		private String nums;// 购买次数
		private ArrayList<BaseJson> periods;// 期数选择
		private ArrayList<BaseJson> orders;// 订单
		private String good_id;// 商品ID
		private String[] codes;// 号码数组
		private String sub_order_id;//子订单
		private String news_nums;// 动态个数
		// private String buy_price;// 商品总价
		// private String good_counts;// 商品总量
		// private String good_price;// 商品总价
		// private String one_price;// 商品单价
	

	public ArrayList<BaseJson> getPays() {
		return pays;
	}

	public String getNews_nums() {
		return news_nums;
	}

	public void setNews_nums(String news_nums) {
		this.news_nums = news_nums;
	}

	public String getSub_order_id() {
		return sub_order_id;
	}

	public void setSub_order_id(String sub_order_id) {
		this.sub_order_id = sub_order_id;
	}

	public ArrayList<BaseJson> getOrders() {
		return orders;
	}

	public void setOrders(ArrayList<BaseJson> orders) {
		this.orders = orders;
	}

	public String getGood_id() {
		return good_id;
	}

	public void setGood_id(String good_id) {
		this.good_id = good_id;
	}

	public String[] getCodes() {
		return codes;
	}

	public void setCodes(String[] codes) {
		this.codes = codes;
	}

	public ArrayList<BaseJson> getGoods() {
		return goods;
	}

	public void setGoods(ArrayList<BaseJson> goods) {
		this.goods = goods;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getJoin_nums() {
		return join_nums;
	}

	public void setJoin_nums(String join_nums) {
		this.join_nums = join_nums;
	}

	public String getRemain_nums() {
		return remain_nums;
	}

	public void setRemain_nums(String remain_nums) {
		this.remain_nums = remain_nums;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String[] getImages() {
		return images;
	}

	public void setImages(String[] images) {
		this.images = images;
	}

	public String getLast_user() {
		return last_user;
	}

	public void setLast_user(String last_user) {
		this.last_user = last_user;
	}

	public String getLast_icon() {
		return last_icon;
	}

	public void setLast_icon(String last_icon) {
		this.last_icon = last_icon;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getLast_address() {
		return last_address;
	}

	public void setLast_address(String last_address) {
		this.last_address = last_address;
	}

	public String getLast_code() {
		return last_code;
	}

	public void setLast_code(String last_code) {
		this.last_code = last_code;
	}

	public String getLast_buy_time() {
		return last_buy_time;
	}

	public void setLast_buy_time(String last_buy_time) {
		this.last_buy_time = last_buy_time;
	}

	public String getLast_publish_time() {
		return last_publish_time;
	}

	public void setLast_publish_time(String last_publish_time) {
		this.last_publish_time = last_publish_time;
	}

	public String getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}

	public String getOwner_icon() {
		return owner_icon;
	}

	public void setOwner_icon(String owner_icon) {
		this.owner_icon = owner_icon;
	}

	public String getOwner_name() {
		return owner_name;
	}

	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}

	public String getOwner_address() {
		return owner_address;
	}

	public void setOwner_address(String owner_address) {
		this.owner_address = owner_address;
	}

	public String getLucky_code() {
		return lucky_code;
	}

	public void setLucky_code(String lucky_code) {
		this.lucky_code = lucky_code;
	}

	public String getBuy_time() {
		return buy_time;
	}

	public void setBuy_time(String buy_time) {
		this.buy_time = buy_time;
	}

	public String getPublish_time() {
		return publish_time;
	}

	public void setPublish_time(String publish_time) {
		this.publish_time = publish_time;
	}

	public ArrayList<BaseJson> getRecords() {
		return records;
	}

	public void setRecords(ArrayList<BaseJson> records) {
		this.records = records;
	}

	public String getNums() {
		return nums;
	}

	public void setNums(String nums) {
		this.nums = nums;
	}

	public ArrayList<BaseJson> getPeriods() {
		return periods;
	}

	public void setPeriods(ArrayList<BaseJson> periods) {
		this.periods = periods;
	}

	public void setPays(ArrayList<BaseJson> pays) {
		this.pays = pays;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getRemain_days() {
		return remain_days;
	}

	public void setRemain_days(int remain_days) {
		this.remain_days = remain_days;
	}

	public int getCan_retrieve() {
		return can_retrieve;
	}

	public void setCan_retrieve(int can_retrieve) {
		this.can_retrieve = can_retrieve;
	}

	public String getTip_id() {
		return tip_id;
	}

	public void setTip_id(String tip_id) {
		this.tip_id = tip_id;
	}

	public String getMsg_type() {
		return msg_type;
	}

	public void setMsg_type(String msg_type) {
		this.msg_type = msg_type;
	}

	public String[] getShows() {
		return shows;
	}

	public void setShows(String[] shows) {
		this.shows = shows;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getLast_login() {
		return last_login;
	}

	public void setLast_login(String last_login) {
		this.last_login = last_login;
	}

	public boolean isGift_dot() {
		return gift_dot;
	}

	public void setGift_dot(boolean gift_dot) {
		this.gift_dot = gift_dot;
	}

	public boolean isFocuser_dot() {
		return focuser_dot;
	}

	public void setFocuser_dot(boolean focuser_dot) {
		this.focuser_dot = focuser_dot;
	}

	public boolean isVisiter_dot() {
		return visiter_dot;
	}

	public void setVisiter_dot(boolean visiter_dot) {
		this.visiter_dot = visiter_dot;
	}

	public boolean isInfo_dot() {
		return info_dot;
	}

	public void setInfo_dot(boolean info_dot) {
		this.info_dot = info_dot;
	}

	public ArrayList<BaseJson> getFaqs() {
		return faqs;
	}

	public void setFaqs(ArrayList<BaseJson> faqs) {
		this.faqs = faqs;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public ArrayList<BaseJson> getFocuseds() {
		return focuseds;
	}

	public void setFocuseds(ArrayList<BaseJson> focuseds) {
		this.focuseds = focuseds;
	}

	public ArrayList<BaseJson> getFocusers() {
		return focusers;
	}

	public void setFocusers(ArrayList<BaseJson> focusers) {
		this.focusers = focusers;
	}

	public String getFocuser_nums() {
		return focuser_nums;
	}

	public void setFocuser_nums(String focuser_nums) {
		this.focuser_nums = focuser_nums;
	}

	public String getFocused_nums() {
		return focused_nums;
	}

	public void setFocused_nums(String focused_nums) {
		this.focused_nums = focused_nums;
	}

	public String getGo_to() {
		return go_to;
	}

	public void setGo_to(String go_to) {
		this.go_to = go_to;
	}

	public String getCan_send() {
		return can_send;
	}

	public void setCan_send(String can_send) {
		this.can_send = can_send;
	}

	public String getGift_tip() {
		return gift_tip;
	}

	public void setGift_tip(String gift_tip) {
		this.gift_tip = gift_tip;
	}

	public String getAlipay() {
		return alipay;
	}

	public void setAlipay(String alipay) {
		this.alipay = alipay;
	}

	public ArrayList<BaseJson> getTasks() {
		return tasks;
	}

	public void setTasks(ArrayList<BaseJson> tasks) {
		this.tasks = tasks;
	}

	public String getAward() {
		return award;
	}

	public void setAward(String award) {
		this.award = award;
	}

	public String getRequire_nums() {
		return require_nums;
	}

	public void setRequire_nums(String require_nums) {
		this.require_nums = require_nums;
	}

	public String getComplete_nums() {
		return complete_nums;
	}

	public void setComplete_nums(String complete_nums) {
		this.complete_nums = complete_nums;
	}

	public String getVideo_id() {
		return video_id;
	}

	public void setVideo_id(String video_id) {
		this.video_id = video_id;
	}

	public String[] getCoins() {
		return coins;
	}

	public void setCoins(String[] coins) {
		this.coins = coins;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getInfo_tip() {
		return info_tip;
	}

	public void setInfo_tip(String info_tip) {
		this.info_tip = info_tip;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public String getMsg_id() {
		return msg_id;
	}

	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}

	public String getTotal_nums() {
		return total_nums;
	}

	public void setTotal_nums(String total_nums) {
		this.total_nums = total_nums;
	}

	public String getFormat_time() {
		return format_time;
	}

	public void setFormat_time(String format_time) {
		this.format_time = format_time;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getSmall_url() {
		return small_url;
	}

	public void setSmall_url(String small_url) {
		this.small_url = small_url;
	}

	public String[] getTexts() {
		return texts;
	}

	public void setTexts(String[] texts) {
		this.texts = texts;
	}

	public String getShow_nums() {
		return show_nums;
	}

	public void setShow_nums(String show_nums) {
		this.show_nums = show_nums;
	}

	public String getVideo_nums() {
		return video_nums;
	}

	public void setVideo_nums(String video_nums) {
		this.video_nums = video_nums;
	}

	public ArrayList<BaseJson> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<BaseJson> products) {
		this.products = products;
	}

	public String getLeft() {
		return left;
	}

	public void setLeft(String left) {
		this.left = left;
	}

	public String getPhoto_id() {
		return photo_id;
	}

	public ArrayList<BaseJson> getDialogs() {
		return dialogs;
	}

	public void setDialogs(ArrayList<BaseJson> dialogs) {
		this.dialogs = dialogs;
	}

	public void setPhoto_id(String photo_id) {
		this.photo_id = photo_id;
	}

	public String getPhoto_nums() {
		return photo_nums;
	}

	public void setPhoto_nums(String photo_nums) {
		this.photo_nums = photo_nums;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getGreeted_nums() {
		return greeted_nums;
	}

	public void setGreeted_nums(String greeted_nums) {
		this.greeted_nums = greeted_nums;
	}

	public String getIs_certified() {
		return is_certified;
	}

	public void setIs_certified(String is_certified) {
		this.is_certified = is_certified;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public LinkedList<BaseJson> getPhotos() {
		return photos;
	}

	public void setPhotos(LinkedList<BaseJson> photos) {
		this.photos = photos;
	}

	public ArrayList<BaseJson> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<BaseJson> users) {
		this.users = users;
	}

	public String getIs_signin() {
		return is_signin;
	}

	public void setIs_signin(String is_signin) {
		this.is_signin = is_signin;
	}

	public ArrayList<BaseJson> getComments() {
		return comments;
	}

	public void setComments(ArrayList<BaseJson> comments) {
		this.comments = comments;
	}

	public ArrayList<BaseJson> getGreets() {
		return greets;
	}

	public void setGreets(ArrayList<BaseJson> greets) {
		this.greets = greets;
	}

	public ArrayList<BaseJson> getSys_msgs() {
		return sys_msgs;
	}

	public void setSys_msgs(ArrayList<BaseJson> sys_msgs) {
		this.sys_msgs = sys_msgs;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComment_nums() {
		return comment_nums;
	}

	public void setComment_nums(String comment_nums) {
		this.comment_nums = comment_nums;
	}

	public String getIs_greet() {
		return is_greet;
	}

	public void setIs_greet(String is_greet) {
		this.is_greet = is_greet;
	}

	public String getGreet_nums() {
		return greet_nums;
	}

	public void setGreet_nums(String greet_nums) {
		this.greet_nums = greet_nums;
	}

	public String getGift_nums() {
		return gift_nums;
	}

	public void setGift_nums(String gift_nums) {
		this.gift_nums = gift_nums;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getCoin() {
		return coin;
	}

	public void setCoin(String coin) {
		this.coin = coin;
	}

	public String getMeiLi() {
		return meiLi;
	}

	public void setMeiLi(String meiLi) {
		this.meiLi = meiLi;
	}

	public String getUser_charm() {
		return user_charm;
	}

	public void setUser_charm(String user_charm) {
		this.user_charm = user_charm;
	}

	public String getRmb() {
		return rmb;
	}

	public void setRmb(String rmb) {
		this.rmb = rmb;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getCharm() {
		return charm;
	}

	public void setCharm(String charm) {
		this.charm = charm;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getMsg_nums() {
		return msg_nums;
	}

	public void setMsg_nums(String msg_nums) {
		this.msg_nums = msg_nums;
	}

	public String getMsg_text() {
		return msg_text;
	}

	public void setMsg_text(String msg_text) {
		this.msg_text = msg_text;
	}

	public String getDialog_id() {
		return dialog_id;
	}

	public void setDialog_id(String dialog_id) {
		this.dialog_id = dialog_id;
	}

	public String getSys_msg_nums() {
		return sys_msg_nums;
	}

	public void setSys_msg_nums(String sys_msg_nums) {
		this.sys_msg_nums = sys_msg_nums;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LinkedList<BaseJson> getUploads() {
		return uploads;
	}

	public void setUploads(LinkedList<BaseJson> uploads) {
		this.uploads = uploads;
	}

	public String[] getShow() {
		return show;
	}

	public void setShow(String[] show) {
		this.show = show;
	}

	public String[] getPhoto() {
		return photo;
	}

	public void setPhoto(String[] photo) {
		this.photo = photo;
	}

	public String[] getVideo() {
		return video;
	}

	public void setVideo(String[] video) {
		this.video = video;
	}

	public String[] getVisiteds() {
		return visiteds;
	}

	public void setVisiteds(String[] visiteds) {
		this.visiteds = visiteds;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getPer_id() {
		return per_id;
	}

	public void setPer_id(String per_id) {
		this.per_id = per_id;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ArrayList<BaseJson> getGifts() {
		return gifts;
	}

	public void setGifts(ArrayList<BaseJson> gifts) {
		this.gifts = gifts;
	}

	public String getSender_nick() {
		return sender_nick;
	}

	public void setSender_nick(String sender_nick) {
		this.sender_nick = sender_nick;
	}

	public String getSender_icon() {
		return sender_icon;
	}

	public void setSender_icon(String sender_icon) {
		this.sender_icon = sender_icon;
	}

	public ArrayList<BaseJson> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<BaseJson> messages) {
		this.messages = messages;
	}

	public ArrayList<BaseJson> getItems() {
		return items;
	}

	public void setItems(ArrayList<BaseJson> items) {
		this.items = items;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
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

	public String getWeixin() {
		return weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public String getIs_focus() {
		return is_focus;
	}

	public void setIs_focus(String is_focus) {
		this.is_focus = is_focus;
	}

	public String getVideo_url() {
		return video_url;
	}

	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}

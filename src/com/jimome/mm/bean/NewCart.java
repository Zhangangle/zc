package com.jimome.mm.bean;

public class NewCart {

	private String pid;// 图片ID
	private String pname;// 图片名称
	private String pic;// 图片地址
	private int pnum;// 购物次数
	private int pleft;// 剩余人次
	private int count;// 购买种类数
	private int price;// 购买总价格

	public NewCart() {

	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public NewCart(String pid, String pname, String pic, int pnum, int pleft) {
		this.pid = pid;
		this.pname = pname;
		this.pic = pic;
		this.pnum = pnum;
		this.pleft = pleft;
	}

	public NewCart(String pid, int pnum) {
		this.pid = pid;
		this.pnum = pnum;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public int getPnum() {
		return pnum;
	}

	public void setPnum(int pnum) {
		this.pnum = pnum;
	}

	public int getPleft() {
		return pleft;
	}

	public void setPleft(int pleft) {
		this.pleft = pleft;
	}

}

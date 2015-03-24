package com.jimome.mm.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jimome.mm.bean.NewCart;
import com.jimome.mm.utils.IOUtils;

public class CartDAO {
	private SQLiteDatabase db;
	private static final String TABLENAME = "cartinfo";

	public CartDAO(Context context) {
		db = SQLiteDatabase.openOrCreateDatabase(IOUtils.getDatabaseFolder(),
				null);
		try {

			db.execSQL("create table  if  not  exists  cartinfo(pid varchar(20) primary key,pname varchar(50), pic varchar(50), pnum int(10),pleft int(10))");
			// Log.e("创建表", "OK");
		} catch (Exception e) {
			// TODO: handle exception
			// Log.e("创建表异常", e.toString());
		}
	}

	/**
	 * 添加
	 * 
	 */
	public NewCart addCart(String pid, String pname, String pic, int pnum,
			int pleft) {
		// db = SQLiteDatabase.openOrCreateDatabase(IOUtils.getDatabaseFolder(),
		// null);
		NewCart cart = new NewCart();
		try {
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME
					+ "  WHERE pid='" + pid + "'", null);
			boolean flag = false;
			while (cursor.moveToNext()) {
				int nums = cursor.getInt(cursor.getColumnIndex("pnum"));
				if (nums + 1 < pleft)
					nums += 1;
				else
					nums = pleft;
				String sql = "UPDATE  " + TABLENAME + "  SET  pnum = " + nums
						+ "  , pleft=" + pleft + "  WHERE  pid='" + pid + "'";
				flag = true;
				if (db.isOpen()) {
					db.execSQL(sql);
				}
			}
			if (!flag)
				db.execSQL(
						"insert into  cartinfo (pid, pname, pic,pnum, pleft) values(?,?,?,?,?)",
						new Object[] { pid, pname, pic, pnum, pleft });
			int count = 0;
			int price = 0;
			cursor = db.rawQuery("SELECT  pnum FROM " + TABLENAME, null);
			while (cursor.moveToNext()) {
				price += cursor.getInt(cursor.getColumnIndex("pnum"));
				++count;
			}
			cart.setCount(count);
			cart.setPrice(price);
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.e("添加到购物车", e.toString());
		} finally {
			return cart;
		}

	}

	// 更新购物车数量
	public NewCart updateCart(String pid, int pnum) {
		// db = SQLiteDatabase.openOrCreateDatabase(IOUtils.getDatabaseFolder(),
		// null);
		String sql = "UPDATE  " + TABLENAME + "  SET  pnum = " + pnum
				+ "   WHERE  pid='" + pid + "'";
		NewCart cart = new NewCart();
		try {
			if (db.isOpen()) {
				db.execSQL(sql);
			}
			int count = 0;
			int price = 0;
			Cursor cursor = db.rawQuery("SELECT  pnum FROM " + TABLENAME, null);
			while (cursor.moveToNext()) {
				price += cursor.getInt(cursor.getColumnIndex("pnum"));
				++count;
			}
			cart.setCount(count);
			cart.setPrice(price);
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			return cart;
		}
	}

	// 删除某件商品
	public NewCart deletePid(String pid) {
		// db = SQLiteDatabase.openOrCreateDatabase(IOUtils.getDatabaseFolder(),
		// null);
		String sql = "DELETE from  " + TABLENAME + "   WHERE  pid='" + pid
				+ "'";
		NewCart cart = new NewCart();
		try {
			if (db.isOpen()) {
				db.execSQL(sql);
			}
			int count = 0;
			int price = 0;
			Cursor cursor = db.rawQuery("SELECT  pnum FROM " + TABLENAME, null);
			while (cursor.moveToNext()) {
				price += cursor.getInt(cursor.getColumnIndex("pnum"));
				++count;
			}
			cart.setCount(count);
			cart.setPrice(price);
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			return cart;
		}
	}

	// 删除表中所有数据
	public void deleteCart() {
		String sql = "DELETE  from  " + TABLENAME;
		try {
			if (db.isOpen()) {
				db.execSQL(sql);
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 获取数据的总条数及数量
	public NewCart countCart() {
		NewCart cart = new NewCart();
		try {
			int count = 0;
			int price = 0;
			Cursor cursor = db.rawQuery("SELECT  pnum FROM " + TABLENAME, null);
			while (cursor.moveToNext()) {
				price += cursor.getInt(cursor.getColumnIndex("pnum"));
				++count;
			}
			cart.setCount(count);
			cart.setPrice(price);
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			return cart;
		}
	}

	/**
	 * 查找分页购物车
	 * 
	 */
	public ArrayList<NewCart> findAll(int page) {
		ArrayList<NewCart> cartInfos = new ArrayList<NewCart>();
		try {
			Cursor cursor = db.rawQuery("SELECT  *  FROM  " + TABLENAME
					+ "  LIMIT   " + ((page - 1) * 10) + ", 10", null);
			while (cursor.moveToNext()) {
				NewCart cart = new NewCart(cursor.getString(cursor
						.getColumnIndex("pid")), cursor.getString(cursor
						.getColumnIndex("pname")), cursor.getString(cursor
						.getColumnIndex("pic")), cursor.getInt(cursor
						.getColumnIndex("pnum")), cursor.getInt(cursor
						.getColumnIndex("pleft")));
				cartInfos.add(cart);
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			 Log.e("e", e.toString());
		} finally {
			return cartInfos;
		}

	}

	/**
	 * 查找所有购物车
	 * 
	 */
	public ArrayList<NewCart> findAll() {
		ArrayList<NewCart> cartInfos = new ArrayList<NewCart>();
		try {
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME, null);
			while (cursor.moveToNext()) {
				NewCart cart = new NewCart(cursor.getString(cursor
						.getColumnIndex("pid")), cursor.getInt(cursor
						.getColumnIndex("pnum")));
				cartInfos.add(cart);
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			// Log.e("e", e.toString());
		} finally {
			return cartInfos;
		}

	}
}

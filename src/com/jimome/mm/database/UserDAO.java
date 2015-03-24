package com.jimome.mm.database;

import java.util.ArrayList;
import java.util.List;

import com.jimome.mm.bean.NewUser;
import com.jimome.mm.utils.IOUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDAO {

	private SQLiteDatabase db;
	private static final String TABLENAME = "userinfo";

	public UserDAO(Context context) {
		db = SQLiteDatabase.openOrCreateDatabase(IOUtils.getDatabaseFolder(),
				null);
		try {

			db.execSQL("create table  if  not  exists  userinfo(uid varchar(20) primary key,openid varchar(50), token varchar(50),logintime  datetime)");
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
	public void add(String uid, String openid, String token) {
		// db = SQLiteDatabase.openOrCreateDatabase(IOUtils.getDatabaseFolder(),
		// null);
		try {
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME
					+ "  WHERE uid='" + uid + "'", null);
			boolean flag = false;
			while (cursor.moveToNext()) {
				String sql = "UPDATE  "
						+ TABLENAME
						+ "  SET  logintime = datetime(CURRENT_TIMESTAMP,'localtime')  WHERE  uid='"
						+ uid + "'";
				flag = true;
				if (db.isOpen()) {
					db.execSQL(sql);
				}
			}
			cursor.close();
			if (!flag)
				db.execSQL(
						"insert into  userinfo (uid,openid,token,logintime) values(?,?,?,datetime(CURRENT_TIMESTAMP,'localtime'))",
						new Object[] { uid, openid, token });
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 查找所有
	 * 
	 */
	public List<NewUser> findAll() {
		List<NewUser> userInfos = new ArrayList<NewUser>();
		try {
			Cursor cursor = db.rawQuery("SELECT * FROM " + TABLENAME
					+ "  ORDER  BY  logintime  DESC", null);
			while (cursor.moveToNext()) {
				NewUser user = new NewUser(cursor.getString(cursor
						.getColumnIndex("uid")), cursor.getString(cursor
						.getColumnIndex("openid")), cursor.getString(cursor
						.getColumnIndex("token")));
				userInfos.add(user);
				// Log.e("userID",
				// cursor.getString(cursor.getColumnIndex("uid"))
				// + "=="
				// + cursor.getString(cursor
				// .getColumnIndex("logintime")));
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			// Log.e("e", e.toString());
		} finally {
			return userInfos;
		}

	}

	// /**
	// * 修改昵称
	// *
	// * @param uid
	// * @param nick
	// */
	// public void updateNick(int uid, String nick) {
	// ContentValues cv = new ContentValues();
	// cv.put("username", nick);
	// String whereClause = "uid=?";
	// String[] whereArgs = new String[] { String.valueOf(uid) };
	// try {
	//
	// if (db.isOpen()) {
	// db.update(TABLENAME, cv, whereClause, whereArgs);
	// db.close();
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// }
	//
	// }

	// 更新使用时间
	public void updateUser(String userid) {
		// db = SQLiteDatabase.openOrCreateDatabase(IOUtils.getDatabaseFolder(),
		// null);
		String sql = "UPDATE  "
				+ TABLENAME
				+ "  SET  logintime = datetime(CURRENT_TIMESTAMP,'localtime')  WHERE  uid='"
				+ userid + "'";
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

	// 更新密码
	// public void updatePassWord(String password, int userid) {
	// // db = SQLiteDatabase.openOrCreateDatabase(IOUtils.getDatabaseFolder(),
	// // null);
	// String sql = "UPDATE  " + TABLENAME + "  SET  password = " + password
	// + "  WHERE  uid=" + userid;
	// try {
	//
	// if (db.isOpen()) {
	// db.execSQL(sql);
	// db.close();
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// }
	// }

	/**
	 * 刪除
	 * 
	 * @param sids
	 */
	// public void delete(int sids) {
	// String whereClause = "uid=?";
	// String[] whereArgs = new String[] { String.valueOf(sids) };
	// try {
	//
	// if (db.isOpen()) {
	// db.delete(TABLENAME, whereClause, whereArgs);
	// db.close();
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// }
	// }

}

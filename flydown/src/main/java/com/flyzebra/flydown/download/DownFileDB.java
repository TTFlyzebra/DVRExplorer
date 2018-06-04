package com.flyzebra.flydown.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.flyzebra.flydown.utils.TLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: FlyZebra
 * Created by FlyZebra on 2016/8/22-下午3:10.
 */
public class DownFileDB extends SQLiteOpenHelper {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_IMGURL = "imgurl";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_STATU = "statu";
    public static final String COLUMN_TSNUM = "tsnum";
    private static final String DB_NAME = "downfile.db";
    private static final String TABLE_SDCARD = "downfile";
    private static final String CREATE_TSDCARD = "create table downfile(id integer primary key, url varchar(255) ,imgurl varchar(255) ,name varchar(255),time integer,statu integer,tsnum integer DEFAULT 0)";
    private SQLiteDatabase db;

    public DownFileDB(Context context) {
        super(context, DB_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.execSQL(CREATE_TSDCARD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SDCARD);
        onCreate(db);
    }

    // 插入一条记录
    public synchronized long insert( ContentValues value) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_SDCARD, null, value);
        db.close();
        return id;
    }

//    // 列出整个表的数据
//    public synchronized List<Map<String, Object>> queryAll() {
//        List<Map<String, Object>> list = new ArrayList<>();
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor c = db.query(TABLE_SDCARD, null, null, null, null, null, "id");
//        while (c.moveToNext()) {
//            HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put(COLUMN_ID, c.getInt(c.getColumnIndex(COLUMN_ID)));
//            map.put(COLUMN_URL, c.getString(c.getColumnIndex(COLUMN_URL)));
//            map.put(COLUMN_NAME, c.getString(c.getColumnIndex(COLUMN_NAME)));
//            map.put(COLUMN_TIME, c.getInt(c.getColumnIndex(COLUMN_TIME)));
//            map.put(COLUMN_STATU, c.getInt(c.getColumnIndex(COLUMN_STATU)));
//            map.put(COLUMN_IMGURL, c.getString(c.getColumnIndex(COLUMN_IMGURL)));
//            map.put(COLUMN_TSNUM, c.getInt(c.getColumnIndex(COLUMN_TSNUM)));
//            list.add(map);
//        }
//        c.close();
//        db.close();
//        return list;
//    }

    // 列出整个表的数据
    public synchronized List<DownFileBean> queryAll() {
        List<DownFileBean> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TABLE_SDCARD, null, null, null, null, null, "id");
        while (c.moveToNext()) {
            DownFileBean downFileBean = new DownFileBean();
            downFileBean.setUrl(c.getString(c.getColumnIndex(COLUMN_URL)));
            downFileBean.setName( c.getString(c.getColumnIndex(COLUMN_NAME)));
            downFileBean.setStatus(c.getInt(c.getColumnIndex(COLUMN_STATU)));
            downFileBean.setImgUrl(c.getString(c.getColumnIndex(COLUMN_IMGURL)));
            downFileBean.setTsNum(c.getInt(c.getColumnIndex(COLUMN_TSNUM)));
            list.add(downFileBean);
        }
        c.close();
        db.close();
        return list;
    }


    //查找一条记录
    public synchronized boolean findByUrl( String url) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("select * from downfile where url = ?", new String[]{url});
        if (c.moveToNext()) {
            c.close();
            db.close();
            return true;
        } else {
            c.close();
            db.close();
            return false;
        }
    }


    //按状态条件查找一条记录
    public synchronized DownFileBean queryOneByStatu( int statu) {
        DownFileBean downFileBean = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("select * from downfile where statu=? limit 1", new String[]{String.valueOf(statu)});
        if (c.moveToNext()) {
            downFileBean = new DownFileBean();
            downFileBean.setUrl(c.getString(c.getColumnIndex(COLUMN_URL)));
            downFileBean.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
            downFileBean.setStatus(c.getInt(c.getColumnIndex(COLUMN_STATU)));
            downFileBean.setImgUrl(c.getString(c.getColumnIndex(COLUMN_IMGURL)));
            downFileBean.setTsNum(c.getInt(c.getColumnIndex(COLUMN_TSNUM)));
        }
        c.close();
        db.close();
        return downFileBean;
    }

    //按状态条件查找一条记录
    public synchronized DownFileBean queryOneByUrl( String url) {
        DownFileBean downFileBean = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("select * from downfile where url=? limit 1", new String[]{url});
        if (c.moveToNext()) {
            downFileBean = new DownFileBean();
            downFileBean.setUrl(c.getString(c.getColumnIndex(COLUMN_URL)));
            downFileBean.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
            downFileBean.setStatus(c.getInt(c.getColumnIndex(COLUMN_STATU)));
            downFileBean.setImgUrl(c.getString(c.getColumnIndex(COLUMN_IMGURL)));
            downFileBean.setTsNum(c.getInt(c.getColumnIndex(COLUMN_TSNUM)));
        }
        c.close();
        db.close();
        return downFileBean;
    }


    //按状态条件查找记录
    public synchronized List<DownFileBean> queryByStatu( int statu) {
        List<DownFileBean> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("select * from downfile where statu = ?", new String[]{String.valueOf(statu)});
        while (c.moveToNext()) {
            DownFileBean downFileBean = new DownFileBean();
            downFileBean.setUrl(c.getString(c.getColumnIndex(COLUMN_URL)));
            downFileBean.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
            downFileBean.setStatus(c.getInt(c.getColumnIndex(COLUMN_STATU)));
            downFileBean.setImgUrl(c.getString(c.getColumnIndex(COLUMN_IMGURL)));
            downFileBean.setTsNum(c.getInt(c.getColumnIndex(COLUMN_TSNUM)));
            list.add(downFileBean);
        }
        c.close();
        db.close();
        return list;
    }

    // 更新一条记录
    public synchronized int update( ContentValues cv, String Clause, String[] url) {
        try {
            TLog.d("update data="+cv+" where url=%s",url[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SQLiteDatabase db = getWritableDatabase();
        int res = db.update(TABLE_SDCARD, cv, Clause, url);
        db.close();
        return res;
    }

    // 删除一条记录
    public synchronized int delete( String url) {
        SQLiteDatabase db = getWritableDatabase();
        int res = db.delete(TABLE_SDCARD, "url=?", new String[]{url});
        db.close();
        return res;
    }

    // 关闭数据库
    public synchronized void close() {
        if (db != null) {
            db.close();
        }
    }

    /*
     * 删除所有数据
     * @return
     */
    public synchronized int deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        int res = db.delete(TABLE_SDCARD, null, null);
        db.close();
        return res;
    }

}

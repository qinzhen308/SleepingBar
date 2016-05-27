package com.bolaa.sleepingbar.model.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import com.bolaa.sleepingbar.model.MyApplicationInfo;
import com.core.framework.develop.LogUtil;
import com.core.framework.store.DB.Bean;
import com.core.framework.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Dell-1
 * Date: 13-7-2
 * Time: 下午2:08
 * To change this template use File | Settings | File Templates.
 */
public class MyApplicationInfoTable extends Bean {
    private static final String TABLE_NAME = "myapplicationinfo";
    private static final String MYAPPLICATIONINFODATA = "myapplicationinfodata";
    private static final String PACKAGENAME = "info_packagename";

    private SQLiteDatabase dataBase;

    private static class MyApplicationInfoTableHolder {
        private static MyApplicationInfoTable instance = new MyApplicationInfoTable();
    }

    private MyApplicationInfoTable() {}

    public static MyApplicationInfoTable getInstance() {
        return MyApplicationInfoTableHolder.instance;
    }
    @Override
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME + " ("
                + PACKAGENAME + " TEXT PRIMARY KEY, "
                + MYAPPLICATIONINFODATA + " TEXT);";
        db.execSql(sql);
    }

    private void initDatabase() {
        if (dataBase == null) {
            dataBase = db.getDb();
        }
    }

    private int getCount() {
        initDatabase();

        if (dataBase != null) {
            String sql = "SELECT count(1) FROM " + TABLE_NAME;
            Cursor cursor = dataBase.rawQuery(sql, null);
            cursor.moveToFirst();
            int n = cursor.getInt(0);
            cursor.close();
            return n;
        }
        return 0;
    }

    public boolean save( String packageName , MyApplicationInfo info) {
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeObject(info);
            oos.flush();
            oos.close();
            bao.close();

            String dealStream = Base64.encodeToString(bao.toByteArray(), Base64.DEFAULT);
            String sql = StringUtil.simpleFormat("replace into %s (info_packagename , myapplicationinfodata) values (?,?)", TABLE_NAME);

            return db.execSql(sql, packageName, dealStream);
        } catch (Exception e) {
            LogUtil.w(e);
        }

        return false;
    }

    public synchronized List<MyApplicationInfo> getAllApplicationInfo() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.getDb().rawQuery(sql, null);

        try {
            return paserApplicationInfo(cursor);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            if(cursor != null)
                cursor.close();
            return null;
        }finally {
            if(cursor != null)
                cursor.close();
        }
    }

    private List<MyApplicationInfo> paserApplicationInfo(Cursor cursor) throws Exception {
        List<MyApplicationInfo> result = Collections.emptyList();
        if (cursor == null || !cursor.moveToFirst()) {
            if (cursor != null) {
                cursor.close();
            }
            return result;
        }

        ByteArrayInputStream bis;
        ObjectInputStream ois;
        result = new ArrayList<MyApplicationInfo>(cursor.getCount());
        do {
            String data = cursor.getString(cursor.getColumnIndex(MYAPPLICATIONINFODATA));
            bis = new ByteArrayInputStream(Base64.decode(data, Base64.DEFAULT));
            ois = new ObjectInputStream(bis);
            Object object = ois.readObject();

            bis.close();
            ois.close();
            result.add((MyApplicationInfo) object);
        } while (cursor.moveToNext());

        cursor.close();

        return result;
    }

    public synchronized boolean removeDataByPackageName(String packageName){
        String sql = StringUtil.simpleFormat("DELETE FROM %s WHERE info_packagename=?", TABLE_NAME);
        return db.execSql(sql, packageName);
    }

    public boolean removeAll() {
        String sql = "DELETE FROM " + TABLE_NAME;
        return db.execSql(sql);
    }
}

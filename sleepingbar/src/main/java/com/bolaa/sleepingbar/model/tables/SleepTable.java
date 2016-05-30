package com.bolaa.sleepingbar.model.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bolaa.sleepingbar.model.Sleep;
import com.core.framework.develop.LogUtil;
import com.core.framework.store.DB.Bean;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 此表只存1天的数据，0点清空
 * 步子记录是为了用时间和步数算出跑步数
 * To change this template use File | Settings | File Templates.
 */
public class SleepTable extends Bean {

    private static final String TABLE_NAME = "sleep";
    private static final String START_TIME = "start_time";
    private static final String SLEEP_TIME = "sleep_time";
    private static final String WAKE_UP_TIME = "wake_up_time";

    private SQLiteDatabase dataBase;

    private static class SleepTableHolder {
        private static SleepTable instance = new SleepTable();
    }

    private SleepTable() {}

    public static SleepTable getInstance() {
        return SleepTableHolder.instance;
    }

    @Override
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME + " ("
                + START_TIME + " INTEGER, "
                + SLEEP_TIME + " INTEGER, "
                + WAKE_UP_TIME + " INTEGER);";
        db.execSql(sql);
    }

    public void init() {
        initDatabase();
        if (getCount() == 0) {
//            saveList(parseCityList());
        }
    }

    private void initDatabase() {
        if (dataBase == null) {
            dataBase = db.getDb();
        }
    }

    public void saveList(List<Sleep> list) {
        try {
            dataBase.beginTransaction();
            String sql = "INSERT INTO " + TABLE_NAME + " (" + START_TIME + ", " + SLEEP_TIME  +", "+WAKE_UP_TIME+ ") values (?, ?, ?)";
            for (Sleep sleep : list) {
                dataBase.execSQL(sql, new Object[]{sleep.startTime, sleep.sleepTime,sleep.wakeupTime});
            }
            dataBase.setTransactionSuccessful();
        } finally {
            dataBase.endTransaction();
        }
    }

    public void saveSleep(Sleep sleep) {
        try {
            dataBase.beginTransaction();
            String sql = "INSERT INTO " + TABLE_NAME + " (" + START_TIME + ", " + SLEEP_TIME  +", "+WAKE_UP_TIME+ ") values (?, ?)";
            dataBase.execSQL(sql, new Object[]{sleep.startTime, sleep.sleepTime,sleep.wakeupTime});
            dataBase.setTransactionSuccessful();
        } finally {
            dataBase.endTransaction();
        }
    }

//    public List<City> parseCityList() {
//        String json = null;
//        try {
//            InputStream is = HApplication.getInstance().getAssets().open("data/cities.txt");
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            json = new String(buffer, "UTF-8");
//
//        } catch (Exception e) {
//            LogUtil.d("access file cities.txt get wrong");
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    public List<Sleep> getList() {
        initDatabase();
        List<Sleep> list = Collections.emptyList();
        if (dataBase != null) {
            Cursor cursor = dataBase.query(TABLE_NAME, null, null, null, null, null, null);
            list = paserStep(cursor);
        }
        return list;
    }

    public Sleep getStepByTime (int startTime) {
        if (startTime<0)  return null;
        initDatabase();

        String sql = "SELECT * From " + TABLE_NAME + " WHERE " + START_TIME+ "= ?";
        Cursor cursor = dataBase.rawQuery(sql, new String[]{startTime+""});
        Sleep sleep = new Sleep();
        try {
            while (cursor.moveToNext()) {
                sleep.startTime = cursor.getInt(cursor.getColumnIndex(START_TIME));
                sleep.sleepTime = cursor.getInt(cursor.getColumnIndex(SLEEP_TIME));
                sleep.wakeupTime= cursor.getInt(cursor.getColumnIndex(WAKE_UP_TIME));
            }
        } catch (Exception e) {
            LogUtil.w(e);
            cursor.close();
            return null;
        } finally {
            if(cursor != null)
                cursor.close();
        }

        return sleep;
    }


    private List<Sleep> paserStep(Cursor cursor) {
        List<Sleep> result = Collections.emptyList();
        if (cursor == null || !cursor.moveToFirst()) {
            if (cursor != null) {
                cursor.close();
            }
            return result;
        }

        result = new LinkedList<Sleep>();
        Sleep sleep;
        try {
            do {
                sleep = new Sleep();
                sleep.startTime = cursor.getInt(cursor.getColumnIndex(START_TIME));
                sleep.sleepTime = cursor.getInt(cursor.getColumnIndex(SLEEP_TIME));
                sleep.wakeupTime = cursor.getInt(cursor.getColumnIndex(WAKE_UP_TIME));
                result.add(sleep);
            } while (cursor.moveToNext());
        } finally {
            if(cursor != null)
                cursor.close();
        }

        return result;
    }

    public boolean cleanTable() {
        initDatabase();
        String sql = "DELETE FROM " + TABLE_NAME;
        return db.execSql(sql);
    }

    private int getCount() {
        int n = 0;
        if (dataBase != null) {
            String sql = "SELECT count(1) FROM " + TABLE_NAME;
            Cursor cursor = dataBase.rawQuery(sql, null);
            cursor.moveToFirst();
            n = cursor.getInt(0);
            if(cursor != null)
                cursor.close();
        }
        return n;
    }
}

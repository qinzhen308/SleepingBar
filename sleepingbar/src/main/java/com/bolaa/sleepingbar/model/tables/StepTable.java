package com.bolaa.sleepingbar.model.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.bolaa.sleepingbar.model.City;
import com.bolaa.sleepingbar.model.Step;
import com.core.framework.develop.LogUtil;
import com.core.framework.store.DB.Bean;

import java.sql.Time;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 此表只存1天的数据，0点清空
 * 步子记录是为了用时间和步数算出跑步数
 * To change this template use File | Settings | File Templates.
 */
public class StepTable extends Bean {

    private static final String TABLE_NAME = "step";
    private static final String ID = "id";
    private static final String VALUE = "value";
    private static final String TIME = "time";

    private SQLiteDatabase dataBase;

    private static class StepTableHolder {
        private static StepTable instance = new StepTable();
    }

    private StepTable() {}

    public static StepTable getInstance() {
        return StepTableHolder.instance;
    }

    @Override
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME + " ("
                + TIME + " INTEGER PRIMARY KEY, "
                + VALUE + " INTEGER);";
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

    public void saveList(List<Step> list) {
        try {
            dataBase.beginTransaction();
            String sql = "INSERT INTO " + TABLE_NAME + " (" + TIME + ", " + VALUE  + ") values (?, ?)";
            for (Step step : list) {
                dataBase.execSQL(sql, new Object[]{step.timestamp, step.value});
            }
            dataBase.setTransactionSuccessful();
        } finally {
            dataBase.endTransaction();
        }
    }

    public void saveStep(Step step) {
        try {
            dataBase.beginTransaction();
            String sql = "INSERT INTO " + TABLE_NAME + " (" + TIME + ", " + VALUE  + ") values (?, ?)";
            dataBase.execSQL(sql, new Object[]{step.timestamp, step.value});
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

    public List<Step> getList() {
        initDatabase();
        List<Step> cityList = Collections.emptyList();
        if (dataBase != null) {
            Cursor cursor = dataBase.query(TABLE_NAME, null, null, null, null, null, null);
            cityList = paserStep(cursor);
        }
        return cityList;
    }

    public Step getStepByTime (int time) {
        if (time<0)  return null;
        initDatabase();

        String sql = "SELECT * From " + TABLE_NAME + " WHERE " + TIME + "= ?";
        Cursor cursor = dataBase.rawQuery(sql, new String[]{time+""});
        Step step = new Step();
        try {
            while (cursor.moveToNext()) {
                step.timestamp = cursor.getInt(cursor.getColumnIndex(TIME));
                step.value = cursor.getInt(cursor.getColumnIndex(VALUE));
            }
        } catch (Exception e) {
            LogUtil.w(e);
            cursor.close();
            return null;
        } finally {
            if(cursor != null)
                cursor.close();
        }

        return step;
    }

    //待完善
    public Step getLatestStep () {
        initDatabase();
        String sql = "SELECT * From " + TABLE_NAME ;
        Cursor cursor = dataBase.rawQuery(sql,null);
        Step step = new Step();
        try {
            while (cursor.moveToNext()) {
                step.timestamp = cursor.getInt(cursor.getColumnIndex(TIME));
                step.value = cursor.getInt(cursor.getColumnIndex(VALUE));
            }
        } catch (Exception e) {
            LogUtil.w(e);
            cursor.close();
            return null;
        } finally {
            if(cursor != null)
                cursor.close();
        }

        return step;
    }

    private List<Step> paserStep(Cursor cursor) {
        List<Step> result = Collections.emptyList();
        if (cursor == null || !cursor.moveToFirst()) {
            if (cursor != null) {
                cursor.close();
            }
            return result;
        }

        result = new LinkedList<Step>();
        Step step;
        try {
            do {
                step = new Step();
                step.timestamp = cursor.getInt(cursor.getColumnIndex(TIME));
                step.value = cursor.getInt(cursor.getColumnIndex(VALUE));
                result.add(step);
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

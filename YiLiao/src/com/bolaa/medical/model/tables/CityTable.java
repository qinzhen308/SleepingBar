package com.bolaa.medical.model.tables;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.bolaa.medical.model.City;
import com.core.framework.develop.LogUtil;
import com.core.framework.store.DB.Bean;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * Created with IntelliJ IDEA.
 * User: mark
 * Date: 13-4-9
 * Time: 下午2:45
 * To change this template use File | Settings | File Templates.
 */
public class CityTable extends Bean {

    private static final String TABLE_NAME = "city";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PIN_YIN = "pinyin";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";

    private SQLiteDatabase dataBase;

    private static class CityTableHolder {
        private static CityTable instance = new CityTable();
    }

    private CityTable() {}

    public static CityTable getInstance() {
        return CityTableHolder.instance;
    }

    @Override
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME + " ("
                + ID + " TEXT, "
                + NAME + " TEXT, "
                + PIN_YIN + " TEXT, "
                + LATITUDE + " TEXT, "
                + LONGITUDE + " TEXT);";
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

    public void saveList(List<City> list) {
        try {
            dataBase.beginTransaction();
            String sql = "INSERT INTO " + TABLE_NAME + " (" + ID + ", " + NAME + ", " + PIN_YIN + ", "
                    + LATITUDE + ", " + LONGITUDE  + ") values (?, ?, ?, ?, ?)";
            for (City city : list) {
                dataBase.execSQL(sql, new Object[]{city.id, city.name,
                        city.pinyin, city.latitude, city.longitude});
            }
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

    public List<City> getCityList() {
        initDatabase();
        List<City> cityList = Collections.emptyList();
        if (dataBase != null) {
            Cursor cursor = dataBase.query(TABLE_NAME, null, null, null, null, null, null);
            cityList = paserCity(cursor);
        }

        return cityList;
    }

    public City getCityByName (String cityName) {
        if (TextUtils.isEmpty(cityName))  return null;
        initDatabase();

        String sql = "SELECT * From " + TABLE_NAME + " WHERE " + NAME + "= ?";
        Cursor cursor = dataBase.rawQuery(sql, new String[]{cityName});
        City city = new City();
        try {
            while (cursor.moveToNext()) {
                city.id = cursor.getString(cursor.getColumnIndex(ID));
                city.name = cursor.getString(cursor.getColumnIndex(NAME));
                city.pinyin = cursor.getString(cursor.getColumnIndex(PIN_YIN));
                city.latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
                city.longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
            }
        } catch (Exception e) {
            LogUtil.w(e);
            cursor.close();
            return null;
        } finally {
            if(cursor != null)
                cursor.close();
        }

        return city;
    }

    private List<City> paserCity(Cursor cursor) {
        List<City> result = Collections.emptyList();
        if (cursor == null || !cursor.moveToFirst()) {
            if (cursor != null) {
                cursor.close();
            }
            return result;
        }

        result = new LinkedList<City>();
        City city;
        try {
            do {
                city = new City();
                city.id = cursor.getString(cursor.getColumnIndex(ID));
                city.name = cursor.getString(cursor.getColumnIndex(NAME));
                city.pinyin = cursor.getString(cursor.getColumnIndex(PIN_YIN));
                city.latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
                city.longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));

                if ("1".equals(city.id)) {
                    result.add(0, city);
                } else if ("5".equals(city.id)) {
                    result.add(1, city);
                } else if ("2".equals(city.id)) {
                    result.add(1, city);
                } else if ("4".equals(city.id)) {
                    result.add(3, city);
                }else {
                    result.add(city);
                }

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

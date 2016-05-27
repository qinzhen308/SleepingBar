package com.bolaa.medical.model.tables;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.bolaa.medical.model.RegionInfo;
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
public class RegionTable extends Bean {

    private static final String TABLE_NAME = "region";
    private static final String ID = "region_id";
    private static final String NAME = "region_name";
    private static final String PARENT_ID = "parent_id";
    private static final String TYPE = "region_type";

    private SQLiteDatabase dataBase;

    private static class TableHolder {
        private static RegionTable instance = new RegionTable();
    }

    private RegionTable() {}

    public static RegionTable getInstance() {
        return TableHolder.instance;
    }

    @Override
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY, "
                + NAME + " TEXT, "
                + PARENT_ID + " INTEGER, "
                + TYPE + " INTEGER);";
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

    public void saveList(List<RegionInfo> list) {
        try {
        	initDatabase();
            dataBase.beginTransaction();
            String sql = "INSERT INTO " + TABLE_NAME + " (" + ID + ", " + NAME + ", " + PARENT_ID + ", "
                     + TYPE  + ") values (?, ?, ?, ?)";
            for (RegionInfo region : list) {
                dataBase.execSQL(sql, new Object[]{region.region_id, region.region_name,
                        region.parent_id, region.region_type});
            }
            dataBase.setTransactionSuccessful();
        } finally {
            dataBase.endTransaction();
        }
    }


    public List<RegionInfo> getCityList() {
        initDatabase();
        List<RegionInfo> cityList = Collections.emptyList();
        if (dataBase != null) {
            Cursor cursor = dataBase.query(TABLE_NAME, null, null, null, null, null, null);
            cityList = paserCity(cursor);
        }

        return cityList;
    }
    
    public List<RegionInfo> getListByParentId(int parentId){
    	if(parentId<0)return null;
        initDatabase();

        String sql = "SELECT * From " + TABLE_NAME + " WHERE " + PARENT_ID + "= "+parentId;
        Cursor cursor = dataBase.rawQuery(sql,null);
        try{
        	List<RegionInfo> cityList = paserCity(cursor);
        	return cityList;
        }catch (Exception e){
        	e.printStackTrace();
        }
        return null;
    }

    public RegionInfo getCityByName (String cityName) {
        if (TextUtils.isEmpty(cityName))  return null;
        initDatabase();

        String sql = "SELECT * From " + TABLE_NAME + " WHERE " + NAME + "= ?";
        Cursor cursor = dataBase.rawQuery(sql, new String[]{cityName});
        RegionInfo city = new RegionInfo();
        try {
            while (cursor.moveToNext()) {
                city.region_id = cursor.getInt(cursor.getColumnIndex(ID));
                city.region_name = cursor.getString(cursor.getColumnIndex(NAME));
                city.parent_id= cursor.getInt(cursor.getColumnIndex(PARENT_ID));
                city.region_type = cursor.getInt(cursor.getColumnIndex(TYPE));
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

    private List<RegionInfo> paserCity(Cursor cursor) {
        List<RegionInfo> result = Collections.emptyList();
        if (cursor == null || !cursor.moveToFirst()) {
            if (cursor != null) {
                cursor.close();
            }
            return result;
        }

        result = new LinkedList<RegionInfo>();
        RegionInfo city;
        try {
            do {
                city = new RegionInfo();
                city.region_id = cursor.getInt(cursor.getColumnIndex(ID));
                city.region_name = cursor.getString(cursor.getColumnIndex(NAME));
                city.parent_id = cursor.getInt(cursor.getColumnIndex(PARENT_ID));
                city.region_type = cursor.getInt(cursor.getColumnIndex(TYPE));

                result.add(city);

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

    public int getCount() {
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

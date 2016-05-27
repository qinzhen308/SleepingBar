package com.core.framework.image;

import java.io.ByteArrayOutputStream;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.core.framework.develop.LogUtil;
import com.core.framework.store.DB.Bean;
import com.core.framework.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Mark
 * Date: 11-10-18
 * Time: 涓嬪崍2:47
 * To change this template use File | Settings | File Templates.
 */
public class Image extends Bean {
    private static final String tableName = "image";

    private static Image instance;

    public static Image getInstance() {
        if (instance == null)
            instance = new Image();
        return instance;
    }

    public Image() {
        super();
    }

    @Override
    public void createTable() {
        String sql = StringUtil.simpleFormat("CREATE TABLE if not exists %s (key TEXT PRIMARY KEY, content BLOB, time INTEGER);", tableName);
        db.execSql(sql);
    }

    public void save(String key, Bitmap content) {
        save(key, content, System.currentTimeMillis());
    }

    public void save(String key, Bitmap content, long time) {
        // 灏嗗帇缂╁悗鐨勫浘鐗囪浆鎹负瀛楄妭鏁扮粍锛屽鏋滃瓧鑺傛暟缁勫ぇ灏忚秴杩�200K锛岀户缁帇缂�
        int qt = 75;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        content.compress(Bitmap.CompressFormat.JPEG, qt, baos);
        int size = baos.size();
        while (qt != 0 && size > 200 * 1024) {
            if (qt < 0) qt = 0;
            baos.reset();
            content.compress(Bitmap.CompressFormat.JPEG, qt, baos);
            size = baos.size();
            qt -= 5;
        }

        String sql = StringUtil.simpleFormat("REPLACE INTO %s (key, content, time) VALUES(?, ?, ?)", tableName);
        db.execSql(sql, key, baos.toByteArray(), time);
    }

    public Bitmap get(String key) {
        String sql = StringUtil.simpleFormat("select content from %s where key=?", tableName);
        Cursor cursor = db.getDb().rawQuery(sql, new String[]{key});
        if (null != cursor) {
            try {
                if (cursor.moveToNext()) {
                    byte[] content = cursor.getBlob(0);
                    if (null != content && content.length > 0)
                        return BitmapFactory.decodeByteArray(content, 0, content.length);
                }
            } catch (Exception e) {
                LogUtil.w(e);
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * remove expired image
     * @param day
     */
    public void removeExpired(int day) {
        long time = System.currentTimeMillis() - day * 24 * 3600000;
        String sql = StringUtil.simpleFormat("delete from %s where time<?", tableName);
        db.execSql(sql, time);
    }

    public void removeAll() {
        String sql = "delete from image";
        db.execSql(sql);
    }
}

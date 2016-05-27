package com.core.framework.store.DB.beans;

import com.core.framework.store.DB.Bean;
import com.core.framework.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Mark
 * Date: 11-6-15
 * Time: 下午5:47
 * To change this template use File | Settings | File Templates.
 */
public class KeyValue extends Bean {
    private static final String tableName = "kv";

    public static KeyValue instance;

    public static KeyValue getInstance() {
        if (instance == null)
            instance = new KeyValue();
        return instance;
    }

    public KeyValue() {
        super();
    }

    @Override
    public void createTable() {
        String sql = StringUtil.simpleFormat("CREATE TABLE if not exists %s (key TEXT PRIMARY KEY, value TEXT, expire_time INTEGER);", tableName);
        db.execSql(sql);
    }

    public void save(String key, String value) {
        save(key, value, -1l);
    }

    public void save(String key, String value, long expireTime) {
        String sql = StringUtil.simpleFormat("REPLACE INTO %s (key, value, expire_time) VALUES(?, ?, ?)", tableName);
        db.execSql(sql, key, value, expireTime);
    }

    public String load(String key) {
        String sql = StringUtil.simpleFormat("SELECT value from %s WHERE key=? AND (expire_time=-1 OR expire_time>"
                + System.currentTimeMillis() + ")", tableName);
        return db.getSingleString(sql, key);
    }

    public void removeExpired() {
        String sql = StringUtil.simpleFormat("delete from %s where expire_time<? AND expire_time>0", tableName);
        db.execSql(sql, System.currentTimeMillis());
    }

    public void removeExpired(int day) {
        long time = System.currentTimeMillis() - day * 24 * 3600000;
        String sql = StringUtil.simpleFormat("delete from %s where expire_time<?", tableName);
        db.execSql(sql, time);
    }

}
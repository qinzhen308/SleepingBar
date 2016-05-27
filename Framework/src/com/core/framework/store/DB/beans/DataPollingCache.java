package com.core.framework.store.DB.beans;

import java.util.Date;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

import com.core.framework.develop.LogUtil;
import com.core.framework.store.DB.Bean;
import com.core.framework.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: mark
 * Date: 12-2-7
 * Time: 下午3:43
 * This cache decision use last modified and expire time
 */
public class DataPollingCache extends Bean {

    public static final String tableName = "dpc";

    private static class DataPollingCacheHolder {
        private static DataPollingCache instance = new DataPollingCache();
    }

    public static DataPollingCache getInstance() {
        return DataPollingCacheHolder.instance;
    }

    private DataPollingCache() {
        super();
    }

    @Override
    public void createTable() {
        String sql = StringUtil.simpleFormat("CREATE TABLE if not exists %s (key TEXT PRIMARY KEY, value TEXT, last_modified INTEGER, expire_time INTEGER);", tableName);
        db.execSql(sql);
    }

    public void save(String key, String value) {
        save(key, value, 0, 0);
    }

    public void save(String key, String value, long lastModified, long expires) {
        String sql = StringUtil.simpleFormat("REPLACE INTO %s (key, value, last_modified, expire_time) VALUES(?, ?, ?, ?)", tableName);
        db.execSql(sql, key, value, lastModified, expires);
    }

    public void save(String key, String value, String lastModified, String expires) {
        try {
            long lm = 0;
            if (!StringUtil.isEmpty(lastModified))
                lm = DateUtils.parseDate(lastModified).getTime();
            long le = 0;
            if (!StringUtil.isEmpty(expires))
                le = DateUtils.parseDate(expires).getTime();
            save(key, value, lm, le);
        } catch (DateParseException e) {
            LogUtil.w(e);
        }
    }

    public String load(String key) {
        return load(key, false);
    }

    public String load(String key, boolean includeExpired) {
        String sql;
        if (includeExpired) {
            sql = StringUtil.simpleFormat("SELECT value from %s WHERE key=?", tableName);
        } else {
            sql = StringUtil.simpleFormat("SELECT value from %s WHERE key=? AND expire_time>"
                    + System.currentTimeMillis(), tableName);
        }

        return db.getSingleString(sql, key);
    }

    public long getLastModified(String key) {
        String sql = StringUtil.simpleFormat("SELECT last_modified from %s WHERE key=?", tableName);
        long lm = 0;
        try {
            lm = Long.valueOf(db.getSingleString(sql, key));
        } catch (NumberFormatException e) {
            return lm;
        }
        return lm;
    }

    public String getLastModifiedGMT(String key) {
        long lm = getLastModified(key);
        if (lm <= 0) return null;
        return DateUtils.formatDate(new Date(lm));
    }

    public long getExpireTime(String key) {
        String sql = StringUtil.simpleFormat("SELECT expire_time from %s WHERE key=?", tableName);
        long lm = 0;
        try {
            lm = Long.valueOf(db.getSingleString(sql, key));
        } catch (NumberFormatException e) {
            LogUtil.w(e);
        }
        return lm;
    }

    public String getExpireTimeGMT(String key) {
        long lm = getExpireTime(key);
        if (lm <= 0) return null;
        return DateUtils.formatDate(new Date(lm));
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
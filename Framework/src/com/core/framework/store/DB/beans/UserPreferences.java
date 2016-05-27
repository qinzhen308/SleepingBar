package com.core.framework.store.DB.beans;

import com.core.framework.store.DB.Bean;
import com.core.framework.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Mark
 * Date: 11-6-15
 * Time: 下午4:25
 * To change this template use File | Settings | File Templates.
 */
public class UserPreferences extends Bean {

    private static final String tableName = "user_preferences";

    private static UserPreferences instance;

    public UserPreferences() {
        super();
    }

    public static UserPreferences getInstance() {
        if (instance == null)
            instance = new UserPreferences();
        return instance;
    }

    @Override
    public void createTable() {
        db.execSql(StringUtil.simpleFormat("create table if not exists %s (user_id text, key text, value text, PRIMARY KEY(user_id, key))", tableName));
    }

    public void save(String key, String value) {
        save("", key, value);
    }

    public void save(String userId, String key, String value) {
        String sql = StringUtil.simpleFormat("replace into %s (user_id, key, value) values (?,?,?)", tableName);
        db.execSql(sql, userId, key, value);
    }

    /**
     * Get user preference by key
     * @param key
     * @return
     */
    public String get(String key) {
        String sql = StringUtil.simpleFormat("select value from %s where key=?", tableName);
        return db.getSingleString(sql, key);
    }

    public String get(String userId, String key) {
        String sql = StringUtil.simpleFormat("select value from %s where user_id=? and key=?", tableName);
        return db.getSingleString(sql, userId, key);
    }

    /**
     * Get user preferences with default value
     * @param key
     * @return
     */
    public String getDefault(String key, String defaultValue) {
        String value = get(key);
        return StringUtil.isEmpty(value) ? defaultValue : value;
    }
}

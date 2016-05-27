package com.core.framework.store.DB;

import com.core.framework.app.oSinfo.AppConfig;import com.core.framework.store.DB.*;import com.core.framework.store.DB.Database;

/**
 * Created by IntelliJ IDEA.
 * User: Mark
 * Date: 11-6-15
 * Time: 涓嬪崍4:40
 * To change this template use File | Settings | File Templates.
 */
public abstract class Bean {
    protected com.core.framework.store.DB.Database db;

    public Bean() {
        db = DatabaseManager.getInstance().openDatabase(getDatabaseName());
        createTable();
    }

    public String getDatabaseName() {
        return AppConfig.DEFAULT_DATABASE;
    }

    public Database getDatabase() {
        return db;
    }

    /**
     * Abstract create table method
     */
    public abstract void createTable();

}

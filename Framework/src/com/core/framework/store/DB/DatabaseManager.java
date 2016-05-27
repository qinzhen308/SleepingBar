package com.core.framework.store.DB;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.core.framework.app.MyApplication;

/**
 * Created by IntelliJ IDEA.
 * User: Mark
 * Date: 11-5-23
 * Time: 下午3:32
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseManager {
    private Map<String, com.core.framework.store.DB.Database> dbCache;

    private Context context;

    public DatabaseManager() {
        this(MyApplication.getInstance());
    }

    public DatabaseManager(Context context) {
        this.context = context;
        dbCache = new HashMap<String, com.core.framework.store.DB.Database>();
    }


    private static com.core.framework.store.DB.DatabaseManager inst ;
    public static com.core.framework.store.DB.DatabaseManager getInstance() {
        if(inst==null)inst = new com.core.framework.store.DB.DatabaseManager();
        return inst;
    }


    public com.core.framework.store.DB.Database openDatabase(String name) {
        if (dbCache.containsKey(name)) {
            return dbCache.get(name);
        } else {
            com.core.framework.store.DB.Database db = new Database(name);
            dbCache.put(name, db);
            return db;
        }
    }
}

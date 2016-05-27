package com.core.framework.store.DB;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.core.framework.app.MyApplication;
import com.core.framework.develop.LogUtil;
import com.core.framework.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Mark
 * Date: 11-5-7
 * Time: 下午5:14
 * To change this template use File | Settings | File Templates.
 */
public class Database {

    private String name;

    private DatabaseOpenHelper dbOpenHelper;
    private SQLiteDatabase db;
    private List<DatabaseOnUpgradeListener> listeners;

    private class DatabaseOpenHelper extends SQLiteOpenHelper {
        Context mContext;

        DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int version) {
            super(context, name, cursorFactory, version);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {
                if (listeners == null || listeners.size() == 0) return;
                for (DatabaseOnUpgradeListener l : listeners) {
                   l.onUpgrade(sqLiteDatabase);
                }
            }
        }
    }

    public Database(String name) {
        this(MyApplication.getInstance(), name);
    }

    public Database(Context context, String name) {
        dbOpenHelper = new DatabaseOpenHelper(context, name, null, MyApplication.getInstance().getVersionCode());
        this.name = name;
    }

    public SQLiteDatabase getDb() throws SQLException{
        db = dbOpenHelper.getWritableDatabase();
        return db;
    }

    public synchronized boolean execSql(String sql) {
        boolean ret = false;

        try {
            if(db == null) db = getDb();

            db.execSQL(sql);
            ret = true;
        } catch (SQLException e) {
            LogUtil.w(e, "Failed to exec sql: " + sql);
        }

        return ret;
    }

    public synchronized boolean execSql(String sql, Object... args) {
        boolean ret = false;
        try {
            if(db == null) db = getDb();
            if (args == null)
                args = new Object[0];
            db.execSQL(sql, args);
            ret = true;
        } catch (SQLException e) {
            LogUtil.w(e, "Failed to exec sql: " + sql);
        }

        return ret;
    }

    public synchronized Object[][] query(String sql) {
        return query(sql, new String[]{});
    }

    public synchronized Object[][] query(String sql, String[] args) {
        Object[][] ret = null;

        Cursor cursor = null;
        try {
            if(db == null) db = getDb();

            if (args == null)
                args = new String[]{};
            cursor = db.rawQuery(sql, args);
            if (cursor != null) {
                int columnCount = cursor.getColumnCount();
                ret = new Object[cursor.getCount()][columnCount];
                int row = 0;
                while (cursor.moveToNext()) {
                    for (int i = 0; i < columnCount; i++) {
                        ret[row][i] = cursor.getString(i);
                    }
                    row += 1;
                }
            }
        } catch (Exception e) {
            LogUtil.w(e, "Failed to query sql: " + sql);
        } finally {
            if (cursor != null) cursor.close();
        }

        return ret;
    }

    public Object getSingleValue(String sql, String... args) {
        Object[][] qs = query(sql, args);
        Object ret = null;
        if (qs.length > 0 && qs[0].length > 0)
            ret = qs[0][0];
        return ret;
    }

    public String getSingleString(String sql, String... args) {
        Object q = getSingleValue(sql, args);
        if (q != null)
            return q.toString();
        else
            return "";
    }

    public long count(String tableName) {
        Object q = getSingleValue(StringUtil.simpleFormat("select count(1) from %s;", tableName));
        if (q != null)
            return Long.valueOf(q.toString());
        else
            return 0;
    }

    public void setOnUpgradeListener(DatabaseOnUpgradeListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<DatabaseOnUpgradeListener>();
        }

        this.listeners.add(listener);
    }

    public interface DatabaseOnUpgradeListener{
        void onUpgrade(SQLiteDatabase sqLiteDatabase);
    }
}

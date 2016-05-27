package com.core.framework.store.DB;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.core.framework.app.oSinfo.AppConfig;
import com.core.framework.develop.LogUtil;
import com.core.framework.store.DB.beans.UserTable;

/**
 * Created by IntelliJ IDEA.
 * User: tianyanlei
 * Date: 13-2-1
 * Time: 上午10:04
 * To change this template use File | Settings | File Templates.
 */
public class AlertUserTableUtils {
    public static void alertUserTable() {

        DatabaseManager.getInstance().openDatabase(AppConfig.DEFAULT_DATABASE).setOnUpgradeListener(new Database.DatabaseOnUpgradeListener() {
            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase) {

                sqLiteDatabase.beginTransaction();

                Cursor cursor = null;
                try {
                    cursor = getCursor(sqLiteDatabase);

                    String sql = "ALTER TABLE ".concat(UserTable.TB_NAME).concat(" ADD ");

                    if (!checkColumnExists(cursor, UserTable.IS_ACTIVE)) {
                        sqLiteDatabase.execSQL(sql.concat(UserTable.IS_ACTIVE).concat(" INTEGER;"));
                    }

                    if (!checkColumnExists(cursor, UserTable.USER_EMAIL)) {
                        sqLiteDatabase.execSQL(sql.concat(UserTable.USER_EMAIL).concat("  TEXT;"));
                    }

                    if (!checkColumnExists(cursor, UserTable.PARTNER_TOKEN)) {
                        sqLiteDatabase.execSQL(sql.concat(UserTable.PARTNER_TOKEN).concat("  TEXT;"));
                    }

                    if (!checkColumnExists(cursor, UserTable.PARTNER_EXPIRES_TIME)) {
                        sqLiteDatabase.execSQL(sql.concat(UserTable.PARTNER_EXPIRES_TIME).concat("  TEXT;"));
                    }

                    if (!checkColumnExists(cursor, UserTable.PARTNER_NICK_NAME)) {
                        sqLiteDatabase.execSQL(sql.concat(UserTable.PARTNER_NICK_NAME).concat("  TEXT;"));
                    }

                    if (!checkColumnExists(cursor, UserTable.PARTNER_TYPE)) {
                        sqLiteDatabase.execSQL(sql.concat(UserTable.PARTNER_TYPE).concat("  INTEGER;"));
                    }

                } catch (Exception e) {
                    LogUtil.d(e.getMessage() + "zp123");

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                    sqLiteDatabase.setTransactionSuccessful();
                    sqLiteDatabase.endTransaction();
                }
            }
        });
    }

    private static Cursor getCursor(SQLiteDatabase sqLiteDatabase) throws SQLiteException{
        return sqLiteDatabase.rawQuery("SELECT * FROM " + UserTable.TB_NAME + " LIMIT 0", null);
    }

    private static boolean checkColumnExists(Cursor cursor, String columnName) {
        boolean result = false;
        try {
            result = cursor != null && cursor.getColumnIndex(columnName) != -1;
        } catch (Exception e) {
            LogUtil.w(e);
        }
        return result;
    }
}

package com.core.framework.store.DB.beans;

import android.database.Cursor;

import com.core.framework.app.MyApplication;
import com.core.framework.app.base.BaseUser;
import com.core.framework.app.base.ThirdPartner;
import com.core.framework.store.DB.Bean;

/**
 * Created by IntelliJ IDEA.
 * User: tianyanlei
 * Date: 12-10-11
 * Time: 下午6:25
 * To change this template use File | Settings | File Templates.
 */
public class UserTable extends Bean {
    public static final String TB_NAME = "user";

    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String USER_PASSWORD = "user_pwd";
    private static final String USER_TOKEN = "user_token";
    private static final String PHONE_NUMBER = "phone_number";
    private static final String IS_LOGIN = "is_login";
    private static final String IS_AUTO_LOGIN = "is_auto_login";
    public static final String IS_ACTIVE = "is_active";
    public static final String USER_EMAIL = "user_email";
    public static final String PARTNER_TYPE = "partner_type";
    public static final String PARTNER_TOKEN = "partner_token";
    public static final String PARTNER_NICK_NAME = "partner_nick_Name";
    public static final String PARTNER_EXPIRES_TIME = "partner_expires_time";

    private static UserTable mInstance;
    public static UserTable getInstance(){
        if(mInstance == null){
            mInstance = new UserTable();
        }
        return mInstance;
    }

    @Override
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TB_NAME
                + " (" + USER_ID + " TEXT,"
                + USER_NAME + " TEXT,"
                + USER_PASSWORD + " TEXT,"
                + USER_TOKEN + " TEXT,"
                + PHONE_NUMBER + " TEXT,"
                + IS_LOGIN + " INTEGER,"
                + IS_AUTO_LOGIN + " INTEGER,"
                + IS_ACTIVE + " INTEGER,"
                + USER_EMAIL + " TEXT,"
                + PARTNER_TOKEN + " TEXT,"
                + PARTNER_EXPIRES_TIME + " TEXT,"
                + PARTNER_NICK_NAME + " TEXT,"
                + PARTNER_TYPE + " INTEGER);";
        getDatabase().execSql(sql);
    }

    /**
     * you can get user by calling the method ,
     * if null returned,it's not login, otherwise,
     * you need to judge whether the user is login by the method of isLogin() of user；
     *
     *
     * @return 13.2.1 modified by tyl for third partner
     */
    public BaseUser getUser() {
        String sql = "SELECT * FROM " + TB_NAME;
        Cursor cursor = null;
        BaseUser user = null;
        try {
            cursor = db.getDb().rawQuery(sql, new String[]{});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                user = new BaseUser();
                user.setId(cursor.getString(cursor.getColumnIndex(USER_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(USER_NAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(USER_PASSWORD)));
                user.setAccessToken(cursor.getString(cursor.getColumnIndex(USER_TOKEN)));
                user.setPhoneNumber(cursor.getString(cursor.getColumnIndex(PHONE_NUMBER)));
                user.setLogin(cursor.getInt(cursor.getColumnIndex(IS_LOGIN)) == 1);
                user.setAutoLogin(cursor.getInt(cursor.getColumnIndex(IS_AUTO_LOGIN)) == 1);
                user.setActive(cursor.getInt(cursor.getColumnIndex(IS_ACTIVE)) == 1);
                user.setEmail(cursor.getString(cursor.getColumnIndex(USER_EMAIL)));//modified
                ThirdPartner partner = new ThirdPartner();
                partner.setAccessToken(cursor.getString(cursor.getColumnIndex(PARTNER_TOKEN)));
                partner.setExpiresTime(cursor.getString(cursor.getColumnIndex(PARTNER_EXPIRES_TIME)));
                partner.setNickName(cursor.getString(cursor.getColumnIndex(PARTNER_NICK_NAME)));
                partner.setPartnerType(cursor.getInt(cursor.getColumnIndex(PARTNER_TYPE)));
                user.setPartner(partner);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return user;
    }

    /**
     * when you login success,you need call the method of saveUser().
     *
     * @param user
     */
    public void saveUser(BaseUser user) {
        BaseUser loginUser = getUser();

        if (loginUser != null) {
            deleteData();
        }
        if (user != null) {
            MyApplication.isLogin=user.isLogin();
            String sql = "insert into " + TB_NAME
                    + " values(" + user.getId() + ","
                    + "'" + user.getName() + "'" + ","
                    + "'" + user.getPassword() + "'" + ","
                    + "'" + user.getAccessToken() + "'" + ","
                    + "'" + user.getPhoneNumber() + "'" + ","
                    + (user.isLogin() ? 1 : 0) + ","
                    + (user.isAutoLogin() ? 1 : 0) + ","
                    + (user.isActive() ? 1 : 0) + ","
                    + "'" + user.getEmail() + "',"
                    + "'" + (user.getPartner() == null ? null : user.getPartner().getAccessToken()) + "',"
                    + "'" + (user.getPartner() == null ? null : user.getPartner().getExpiresTime()) + "',"
                    + "'" + (user.getPartner() == null ? null : user.getPartner().getNickName())  + "',"
                    + (user.getPartner() == null ? -1 : user.getPartner().getPartnerType()) + ");";            
            db.execSql(sql);

        }
    }

    /**
     * when you logout,you must call the method.
     *@param flag 退出是否成功
     */
    public void update(boolean flag) {
        MyApplication.isLogin=!flag;
        String sql = "UPDATE " + TB_NAME
                + " SET " + IS_LOGIN + " = " + (flag ? 0 : 1);
        db.execSql(sql);
    }

    public void updateAutoLogin(boolean isAuto){
        String sql = "UPDATE " + TB_NAME
                + " SET " + IS_AUTO_LOGIN + " = " + (isAuto ? 1 : 0);
        db.execSql(sql);
    }

    // clear data of table
    private void deleteData() {
        String sql = "DELETE FROM " + TB_NAME;
        db.execSql(sql);
    }

}

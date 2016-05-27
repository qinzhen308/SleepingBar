package com.core.framework.store.DB.beans;

import com.core.framework.store.DB.Bean;
import com.core.framework.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Mark
 * Date: 11-6-16
 * Time: 下午3:36
 * To change this template use File | Settings | File Templates.
 */
public class Preferences extends Bean {
    public static String requestKeyName = "requestKey";

    private static final String tableName = "preferences";

    private static Preferences instance;

    public static Preferences getInstance() {
        if (instance == null)
            instance = new Preferences();
        return instance;
    }

    public Preferences() {
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

    public void delete(String key) {
        String sql = StringUtil.simpleFormat("delete from %s where key=?;", tableName);
        db.execSql(sql, key);
    }

    public void save(String key, String value, long expireTime) {
        String sql = StringUtil.simpleFormat("REPLACE INTO %s (key, value, expire_time) VALUES(?, ?, ?)", tableName);
        db.execSql(sql, key, value, expireTime);
    }

    public String get(String key) {
        String sql = StringUtil.simpleFormat("SELECT value from %s WHERE key=? AND (expire_time=-1 OR expire_time>"
                + System.currentTimeMillis() / 1000 + ")", tableName);
        return db.getSingleString(sql, key);
    }

    /**
     * Get preferences with default value
     *
     * @param key
     * @return
     */
    public String getDefault(String key, String defaultValue) {
        String value = get(key);
        return StringUtil.isEmpty(value) ? defaultValue : value;
    }

// TODO: move request key to tuan800
//    public void initRequestKey(TelephonyManager tm, String partnerKey, String partnerValue) {
//        if (StringUtil.isEmpty(getRequestKey())) {
//            final String s = "5jHJ@^9B32!";
//            String deviceId = tm.getDeviceId();
//            if (StringUtil.isEmpty(deviceId)) return;
//            String deviceIdBase64 = Base64.encodeToString(deviceId.getBytes(), Base64.DEFAULT);
//            String tel = tm.getLine1Number();
//            tel = (null == tel) ? "" : tel;
//            String telBase64 = Base64.encodeToString(tel.getBytes(), Base64.DEFAULT);
//
//            // md
//            String md_s = new String(new char[] { 'M', 'D', '5' });
//            MessageDigest md = null;
//            MessageDigest md2 = null;
//            try {
//                md2 = MessageDigest.getInstance("SHA-1");
//            } catch (NoSuchAlgorithmException e) {
//                LogUtil.w(e);
//            }
//
//            try {
//                md = MessageDigest.getInstance(md_s);
//            } catch (NoSuchAlgorithmException e) {
//                LogUtil.w(e);
//            }
//
//            // ******************** start http task ********************
//            Map<String, String> parameters = new HashMap<String, String>();
//            parameters.put("deviceId", deviceIdBase64);
//            parameters.put("tel", telBase64);
//            String partnerBase64 = Base64.encodeToString(partnerValue.getBytes(), Base64.DEFAULT);
//            parameters.put(partnerKey, partnerBase64);
//            String md5 = StringUtil.fromBytes(md2.digest((tel + s +
//                    StringUtil.fromBytes(md.digest(("`sFn+?ss00" + tel + deviceIdBase64).getBytes())) + deviceId + partnerValue).getBytes()));
//            parameters.put("sign", StringUtil.fromBytes(md2.digest((tel + s +
//                    StringUtil.fromBytes(md.digest(("`sFn+?ss00" + tel + deviceIdBase64).getBytes())) + deviceId + partnerValue).getBytes())));
//            String url = Config.API_ANDROID_PREFIX + "get_request_key";
//             NetworkWorker.getInstance().postSync(url, parameters, new JsonParser(){
//                @Override
//                public void parseJson(JSONObject jo, Map<String, Object> mapResult) throws JSONException {
//                    int status = jo.getInt("status");
//                    if (0 == status) {
//                        String requestKey = jo.getString("requestKey");
//                        if (null != requestKey && 0 != requestKey.length()) {
//                            save(requestKeyName, requestKey);
//                            Preferences.this.requestKey = requestKey;
//                        }
//                    }
//                }
//            });
//        }
//    }
}

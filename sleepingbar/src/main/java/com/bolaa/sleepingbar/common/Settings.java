package com.bolaa.sleepingbar.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.location.Location;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.model.City;
import com.bolaa.sleepingbar.model.tables.CityTable;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.auth.LoginExecutor;
import com.core.framework.store.file.FileHelper;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.AlarmSign;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: kait
 * Date: 4/2/13
 * Time: 3:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class Settings {

    public static City city;
    public static String longitude;
    public static String latitude;
    public static final int DELAY_TIME = 10 * 1000;//延迟请求


    public static void init(Activity activity) {
        // iniStrictModet();
        initCity();
        initLocation();
        if (HApplication.getInstance().isMainAppPro()
                && AppUtil.isGpsEnable(activity)) {
        }
        initTable();
        ScreenUtil.setDisplay(activity);
        if (TextUtils.isEmpty(PreferencesUtils.getString(GlobeFlags.DELETE_OLD_CACHE))) {
            deleteOldImageCache(FileHelper.getDiskCacheDir(HApplication.getInstance(), "tao800"));
            PreferencesUtils.putString(GlobeFlags.DELETE_OLD_CACHE, "delete_old_cache");
        }
    }

    public static void initCity() {
        String cityId = PreferencesUtils.getString(GlobeFlags.CITY_ID);
        String cityName = PreferencesUtils.getString(GlobeFlags.CITY_NAME);

        city = new City(TextUtils.isEmpty(cityId) ? "1" : cityId, TextUtils.isEmpty(cityName) ? "北京" : cityName);
    }

    public static void initLocation() {
        latitude = PreferencesUtils.getString(GlobeFlags.LAT_HISTORY);
        longitude = PreferencesUtils.getString(GlobeFlags.LNG_HISTORY);
    }

    public static void deleteOldImageCache(final File file) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String path = file.getPath();
                    if (!file.exists()) {
                        return;
                    }
                    if (!file.isDirectory()) {
                        return;
                    }
                    String[] tempList = file.list();
                    File temp = null;
                    for (int i = 0; i < tempList.length; i++) {
                        if (path.endsWith(File.separator)) {
                            temp = new File(path + tempList[i]);
                        } else {
                            temp = new File(path + File.separator + tempList[i]);
                        }
                        if (temp.isFile()) {
                            temp.delete();
                        }
                        if (temp.isDirectory()) {
                            deleteOldImageCache(temp);//先删除文件夹里面的文件
                            temp.delete();//删除子文件夹
                        }
                    }
                    file.delete();//删除根文件夹
                }
            }).start();
        } catch (Exception e) {

        }
    }

    public static void saveCity(String cityName) {
        City city = CityTable.getInstance().getCityByName(cityName);

        if (city == null) return;

        Settings.city = city;
        PreferencesUtils.putString(GlobeFlags.CITY_ID, city.id);
        PreferencesUtils.putString(GlobeFlags.CITY_NAME, city.name);
    }

    public static void saveLocation(Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());

        PreferencesUtils.putString(GlobeFlags.LAT_HISTORY, latitude);
        PreferencesUtils.putString(GlobeFlags.LNG_HISTORY, longitude);
    }

    public static void registerAlarmNotify() {

        AlarmSign alarmSign = new AlarmSign(HApplication.getInstance());
        alarmSign.setAlarmTime();

    }

    private static void initTable() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CityTable.getInstance().init();
                
         
            }
        }).start();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void iniStrictModet() {
        StrictMode.setThreadPolicy(new StrictMode.
                ThreadPolicy.
                Builder().
                detectDiskReads().
                detectDiskWrites().
                detectNetwork().
                penaltyLog().
                build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
    }
    
    private static class LoginSuccessExecutor implements LoginExecutor<String> {
        @Override
        public void update(String result) {
            
            
        }
    }

}

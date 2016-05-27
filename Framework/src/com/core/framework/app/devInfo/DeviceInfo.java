package com.core.framework.app.devInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;

import com.core.framework.app.AppSetting;
import com.core.framework.app.MyApplication;
import com.core.framework.develop.LogUtil;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: mark
 * Date: 12-1-31
 * Time: 下午3:20
 * To change this template use File | Settings | File Templates.
 */
public class DeviceInfo {

    private static String mac;
    private static String imsi;
    private static String deviceId;
    private static String brand;

    public static String getDeviceId() {
//        if (!StringUtil.isNull(deviceId)) return deviceId;

        String ret = "";
//        if (AppSetting.DEV_TEST_SWITCH == 1&&AppSetting.LOG_CLOSED==0) {
//            ret = PreferencesUtils.getString("testDeciceID");
//            if (!StringUtil.isEmpty(ret)) {
//                deviceId = ret;
//                return ret;
//            }
//        }

        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) MyApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            ret = telephonyManager.getDeviceId();
            if (!StringUtil.isNull(ret)) deviceId = ret;
        } catch (Exception e) {
            LogUtil.w(e);
        }

        return ret;
    }

    public static String getImsi() {
        if (!StringUtil.isNull(imsi)) return imsi;

        String ret = "";
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) MyApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            ret = telephonyManager.getSubscriberId();
            if (!StringUtil.isNull(ret)) imsi = ret;
        } catch (Exception e) {
            LogUtil.w(e);
        }

        return ret;
    }

    public static String getMacAddress() {
        if (!StringUtil.isNull(mac)) return mac;

        String ret = "";
        try {
            WifiManager wifiManager = (WifiManager) MyApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
            ret = wifiManager.getConnectionInfo().getMacAddress();
            if (!StringUtil.isNull(ret)) mac = ret;
        } catch (Exception e) {
            LogUtil.w(e);
        }

        return ret;
    }


    public static String getBrand() {
        return Build.BRAND;
    }

    public static String getModel() {
        return Build.MODEL;
    }


    /**
     * mac + device id
     * 前面12位为mac地址，后15位为device id
     *
     * @return
     */
    public static String getUID() {
        String deviceId = getDeviceId();
        String mac = getMacAddress();
        if (!StringUtil.isNull(mac)) {
            return mac.replace(":", "") + deviceId;
        } else {
            return "";
        }
    }

    /*
    判断是不是MIUI系统
     */
    private static int iMIUI = -1; //-1 未初始化 0 不是MIUI 1 是MIUI
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    public static boolean isMIUI() {
        try {
            if(-1 == iMIUI){
                Properties properties = new Properties();
                properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));

                if(properties.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                        || properties.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                        || properties.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null){
                    iMIUI = 1;
                }else{
                    iMIUI = 0;
                }
            }
        } catch (final IOException e) {
        }

        return iMIUI == 1;
    }
}

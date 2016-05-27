package com.core.framework.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;

/**
 * Created with IntelliJ IDEA.
 * User: kait
 * Date: 12-9-3
 * Time: 下午6:47
 * To change this template use File | Settings | File Templates.
 */
public class ApiUtil {


    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    @TargetApi(9)
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= 9;
    }

    @TargetApi(11)
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= 11;
    }

    @TargetApi(12)
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= 12;
    }

    @TargetApi(14)
    public static boolean hasIceCremSandwich () {
        return Build.VERSION.SDK_INT >= 14;
    }

    @TargetApi(16)
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= 16;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isExternalStorageRemovable() {
        if (hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }

        return true;
    }



}
package com.core.framework.develop;

import android.util.Log;

import com.core.framework.app.oSinfo.AppConfig;

/**
 * Created by IntelliJ IDEA.
 * User: Mark
 * Date: 11-4-19
 * Time: 上午10:13
 * To change this template use File | Settings | File Templates.
 */
public class LogUtil {

    private static void log(int priority, String msg, Throwable throwable) {
        if (AppConfig.LOG_CLOSED) return;

		try {
            Log.println(priority, AppConfig.LOG_TAG, msg + (throwable == null ? "" : Log.getStackTraceString(throwable)));
		} catch (Exception e) {
			Log.e(AppConfig.LOG_TAG, "Failed to log: " + e.getMessage());
		}
	}

    public static void d(Throwable throwable, String log) {
        if (AppConfig.LOG_CLOSED) return;

        try {
            log(Log.DEBUG, log, throwable);
        } catch (Exception e) {
            Log.e(AppConfig.LOG_TAG, "Failed to d: " + e.getMessage());
        }
	}

    public static void d(String log) {
        d(null, log);
    }

    public static void e(Throwable throwable, String error) {
        if (AppConfig.LOG_CLOSED) return;

        try {
            log(Log.ERROR, error, throwable);
        } catch (Exception e) {
            Log.e(AppConfig.LOG_TAG, "Failed to e: " + e.getMessage());
        }
	}

    public static void e(Throwable throwable) {
        e(throwable, throwable.getMessage()+"\n");
    }

    public static void i(String log) {
        if (AppConfig.LOG_CLOSED) return;

        try{
            log(Log.INFO, log, null);
        } catch (Exception e) {
            e(e);
        }
    }

    public static void w(Throwable throwable, String log) {
        if (AppConfig.LOG_CLOSED) return;

        try {
            log(Log.WARN, log, throwable);
        } catch (Exception e) {
            Log.e(AppConfig.LOG_TAG, "Failed to w: " + e.getMessage());
        }
    }

    public static void w(Throwable throwable) {
        w(throwable, throwable.getMessage()+"\n");
    }

    public static void w(String log) {
        w(null, log);
    }



    public static void debug(String tag, String log) {
        if (AppConfig.LOG_CLOSED) return;

        try {
            Log.d(tag, log);
        } catch (Exception e) {
            Log.e(AppConfig.LOG_TAG, "Failed to d: " + e.getMessage());
        }
    }

    //打印调用栈
    public static void pStack(String tag) {
        if (AppConfig.LOG_CLOSED) return;

        try {
            Log.d(tag, Log.getStackTraceString(new Throwable()));
        } catch (Exception e) {
            Log.e(AppConfig.LOG_TAG, "Failed to d: " + e.getMessage());
        }
    }
}

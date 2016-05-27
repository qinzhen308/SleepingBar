package com.core.framework.store.sharePer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.core.framework.app.MyApplication;
import com.core.framework.develop.LogUtil;

/**
 * Created with IntelliJ IDEA.
 * User: qikai
 * Date: 13-3-7
 * Time: 下午11:24
 * To change this template use File | Settings | File Templates.
 */
public class PreferencesUtils {
    private static Context c;

    static {
        try {
            c = MyApplication.getInstance().createPackageContext("com.boju.hiyo", Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            c = null;
        }
    }

    private static SharedPreferences getSP() {
        if (c != null) {
            if (MyApplication.getInstance().isWebProcess()){
                return c.getSharedPreferences("com.boju.hiyo" + "_preferences", Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE | Context.MODE_MULTI_PROCESS);
            }else{
                return c.getSharedPreferences("com.boju.hiyo" + "_preferences", Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
            }
        } else {
            return PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance());
        }
    }


    public static void putInteger(String name, int value) {
        getSP().edit().putInt(name, value).commit();
    }

    public static int getInteger(String name) {
        return getSP().getInt(name, -1);
    }
    
    public static int getInteger(String name,int defaultValue) {
        return getSP().getInt(name, defaultValue);
    }

    public static void putString(String name, String value) {
        getSP().edit().putString(name, value).commit();
    }

    public static String getString(String name) {
        return getSP().getString(name, "");
    }

    /**
     * by qz
     *
     * @param name
     * @return
     */
    public static String getStringDefaultNull(String name) {
        return getSP().getString(name, null);
    }

    public static void putBoolean(String name, boolean flag) {
        getSP().edit().putBoolean(name, flag).commit();
    }

    public static boolean getBoolean(String name) {
        return getSP().getBoolean(name, false);
    }

    public static long getLong(String name) {
        return getSP().getLong(name, 0l);
    }

    public static void putLong(String name, long value) {
        getSP().edit().putLong(name, value).commit();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void putSet(String name, Set<String> set) {
        getSP().edit().putStringSet(name, set).commit();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Set<String> getSet(String name) {
        return getSP().getStringSet(name, null);
    }

    public static void clear() {
        getSP().edit().clear().commit();
    }

    public static void remove(String name) {
        getSP().edit().remove(name).commit();

    }


    public static void storeObject(Object oo, String Key) {
        String stream = "";
        try {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bao);
            oos.writeObject(oo);
            oos.flush();
            oos.close();
            bao.close();
            stream = Base64.encodeToString(bao.toByteArray(), Base64.DEFAULT);
            putString(Key, stream);
        } catch (Exception e) {
            LogUtil.w(e);
        }
    }

    public static Object paserObject(String Key) {
        LogUtil.d("---------load self in thread paserStream()-------> ");
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            String data = getString(Key);
            LogUtil.d("wo cao ni mei a parserStream()-------> " + data);
            if (data == null || data.equals("")) return null;
            bis = new ByteArrayInputStream(Base64.decode(data, Base64.DEFAULT));
            ois = new ObjectInputStream(bis);
            Object object = ois.readObject();
            return object;
        } catch (Exception e) {
            LogUtil.w(e);
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                LogUtil.w(e);
            }
        }
        return null;
    }
}

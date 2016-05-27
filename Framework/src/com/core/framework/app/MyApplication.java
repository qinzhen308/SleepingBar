package com.core.framework.app;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;

import com.core.framework.develop.DevRunningTime;
import com.core.framework.develop.LogUtil;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.StringUtil;

public abstract class MyApplication extends Application{
	public static MyApplication instance;
	public static int netType;
	public static boolean netChanged;
    private List<Activity> mActivityList = new ArrayList<Activity>();
    int myPid = -1;
    
    public static boolean isLogin=false;


	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.instance = this;
        myPid = android.os.Process.myPid();
        mActivityList.clear();
        doBusyTransaction();
        if (DevRunningTime.isTaoBaoProessNotDBinit && isWebProcess()) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                doBackTransaction();
                checkService();
            }
        }).start();
	}
	
	
	public static MyApplication getInstance(){
		return instance;
	}
	
	public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    public void removeActivity(Activity activity) {
        mActivityList.remove(activity);
    }

    
    public boolean isNewVison() {
        int thisVison = getVersionCode();
        int dbVison = PreferencesUtils.getInteger("current_app_vison");
        return thisVison != dbVison;
    }
    
    public int getVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getTruePackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    public String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getTruePackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }
	
	public abstract void checkService();

    public abstract void doBusyTransaction();

    public abstract void doBackTransaction();

    public static final String WEB = "tbwebpro";
    public static String WEB_PACKAGE = "com.android.browser";

    public String getTruePackageName() {
//        return "com.boju.hiyo";
        return super.getPackageName();
    }

    @Override
    public String getPackageName() {
        if (isWebProcess()&&Integer.valueOf(android.os.Build.VERSION.SDK).intValue()<21) {
            return WEB_PACKAGE;
        }
        return super.getPackageName();
    }

    public boolean isMainAppPro() {
        String info = getCurProcessName();
        return (!StringUtil.isEmpty(info) && info.equals(super.getPackageName()));
    }

    public boolean isWebProcess() {
        String info = getCurProcessName();
        return (!StringUtil.isEmpty(info) && info.contains(WEB));
    }

    boolean isLoadRunningAppProcessInfo;

    public String getCurProcessName() {
        try {
            int pid = android.os.Process.myPid();
            Object oo = getSystemService(Context.ACTIVITY_SERVICE);
            if (oo == null) return null;
            ActivityManager mActivityManager = (ActivityManager) oo;
            if (oo == null) return null;
            if (isLoadRunningAppProcessInfo) return null;
            isLoadRunningAppProcessInfo = true;
            java.util.List<android.app.ActivityManager.RunningAppProcessInfo> list = mActivityManager.getRunningAppProcesses();
            isLoadRunningAppProcessInfo = false;
            if (list == null) return null;
            for (ActivityManager.RunningAppProcessInfo appProcess : list) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        } catch (Exception e) {
//            LogUtil.w(e);
        }
        return null;
    }
    
    public void exit() {
        for (Activity activity : mActivityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        mActivityList.clear();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtil.debug("app","app启动  onConfigurationChanged" + newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LogUtil.debug("app","app启动 onLowMemory ");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LogUtil.debug("app","app启动 onTerminate ");
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        LogUtil.debug("app","app启动 onTrimMemory level " + level);
    }
    
    

}

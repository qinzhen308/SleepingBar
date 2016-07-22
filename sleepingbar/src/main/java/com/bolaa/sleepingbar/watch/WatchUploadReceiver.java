package com.bolaa.sleepingbar.watch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.core.framework.develop.LogUtil;
import com.core.framework.store.sharePer.PreferencesUtils;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by paulz on 2016/7/13.
 */
public class WatchUploadReceiver extends BroadcastReceiver{
    private static final int INTERVAL = 1000 * 60 * 15 ;// 15分钟
    public static final String ACTION_WATCH_SYNCH_RECEIVER="com.bolaa.sleepingbar.ACTION.WATCH.SYNCH.RECEIVER";


    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_DATE_CHANGED.equals(intent.getAction())){
            //日期改变，删除步行缓存
            PreferencesUtils.remove(WatchConstant.FLAG_STEP_CACHE_FOR_LOOK);
            //到点上传昨天的数据(防止今天没传成功的情况)
            context.startService(new Intent(context,WatchUploadService.class).putExtra(WatchConstant.FLAG_IS_START_BY_DATE_CHANGED,true));
            return;
        }
        boolean isRuning= HApplication.getInstance().isWatchServiceWork();
        LogUtil.d("alarm---WatchUploadReceiver---onReceive--isRuning="+isRuning);
        if(PreferencesUtils.getBoolean(WatchConstant.FLAG_IS_WATCH_CONNECTED)&&isRuning){//连接状态，发广播获取睡眠数据
            LogUtil.d("alarm---WatchUploadReceiver---onReceive--发广播");
            context.sendBroadcast(new Intent(WatchConstant.ACTION_WATCH_CMD_GET_SLEEP));
        }else {//未连接状态，就启动手环
            LogUtil.d("alarm---WatchUploadReceiver---onReceive--连接蓝牙");
            HApplication.getInstance().autoConnectedWatch();
        }
    }

    public static void setAlarm(Context context){
//        if(PreferencesUtils.getBoolean("has_watch_alarm_synch"))return;
        Intent intent = new Intent(context, WatchUploadReceiver.class);
        intent.setData(Uri.parse("content://com.bolaa.sleepingbar.receiver"));
        PendingIntent sender = PendingIntent.getBroadcast(context, 10011, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        // Schedule the alarm!
        intent.setAction(ACTION_WATCH_SYNCH_RECEIVER);
        AlarmManager am = (AlarmManager) context .getSystemService(Context.ALARM_SERVICE);
        //均衡服务器的压力，设置上报时间为10点半前后5分钟内。
//        Calendar calendar = Calendar.getInstance(); calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//        calendar.set(Calendar.MINUTE, 1);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), INTERVAL, sender);
    }


}

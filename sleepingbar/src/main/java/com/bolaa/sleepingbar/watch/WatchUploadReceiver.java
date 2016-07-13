package com.bolaa.sleepingbar.watch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by paulz on 2016/7/13.
 */
public class WatchUploadReceiver extends BroadcastReceiver{
    private static final int INTERVAL = 1000 * 60 * 60 * 24;// 24h


    @Override
    public void onReceive(Context context, Intent intent) {

    }

    public static void setAlarm(Context context){

        Intent intent = new Intent(context, WatchUploadReceiver.class);
        PendingIntent sender = PendingIntent.getService(context, 10010, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        // Schedule the alarm!
        AlarmManager am = (AlarmManager) context .getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance(); calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 30); calendar.set(Calendar.SECOND, 10); calendar.set(Calendar.MILLISECOND, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, sender);
    }
}

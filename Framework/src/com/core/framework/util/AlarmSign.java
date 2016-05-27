package com.core.framework.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.core.framework.app.oSinfo.AppConfig;
import com.core.framework.develop.LogUtil;

/**
 * Created with IntelliJ IDEA.
 * User: kait
 * Date: 12-9-3
 * Time: 下午6:28
 * To change this template use File | Settings | File Templates.
 */
public class AlarmSign {

    public static final int REQUEST_BROADCAST_TYPE = 0;
    public static final int REQUEST_ACTIVITY_TYPE = 1;
    public static final int REQUEST_SERVICE_TYPE = 2;

    public static final String PUSH_FLAG_HAS = "push_flag_has";

    public static final String RA_ACTION = "com.tuan800.action.RA_ACTION_".concat(AppConfig.CLIENT_TAG);

    private Context mContext;
    private AlarmManager mAlarmManager;

    public AlarmSign(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * if the described PendingIntent does not already
     * exist, then simply return null instead of creating it
     *
     * @param intent
     * @return
     */
    public PendingIntent getSignedPending(Intent intent) {
        return PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_NO_CREATE);
    }

    /**
     * remove any alarms with a matching action
     */
    public void cancelAlarmTime(Intent intent) {
        mAlarmManager.cancel(PendingIntent.getBroadcast(mContext, REQUEST_BROADCAST_TYPE, intent, 0));
    }

    /**
     * remove any alarms with a matching action
     */
    public void cancelAlarmActivityTime(Intent intent) {
        mAlarmManager.cancel(PendingIntent.getActivity(mContext, REQUEST_ACTIVITY_TYPE, intent, 0));
    }

    /**
     * remove any alarms with a matching action
     */
    public void cancelAlarmServiceTime(Intent intent) {
        mAlarmManager.cancel(PendingIntent.getService(mContext, REQUEST_SERVICE_TYPE, intent, 0));
    }

    /**
     * sign alarm for pooling push
     */
    public void setAlarmTime() {
        Intent intent = new Intent(RA_ACTION);
        PendingIntent sender = getSignedPending(intent);

        if (sender == null) {
            sender = PendingIntent.getBroadcast(mContext, REQUEST_BROADCAST_TYPE, intent, 0);
            try {
                mAlarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + AppConfig.POLLING_INTERVAL,
                        AppConfig.POLLING_INTERVAL, sender);
            } catch (Exception e) {
                LogUtil.w(e.toString());
            }
        }
    }

    /**
     * set alarm for once
     */
    public void setAlarmTimeOnce(Intent intent, long startTime) {
        PendingIntent operation = PendingIntent.getBroadcast(mContext, REQUEST_BROADCAST_TYPE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.RTC, startTime, operation);
    }

    /**
     * sign alarm for boot completed, used by replace signed alarm for action
     *
     * @param action
     * @param startTime
     * @param intervalMillis
     */
    public void setAlarmTime(String action, long startTime, long intervalMillis) {
        Intent intent = new Intent(action);
        PendingIntent sender = PendingIntent.getBroadcast(mContext, REQUEST_BROADCAST_TYPE, intent, 0);
        try {
            mAlarmManager.setRepeating(AlarmManager.RTC, startTime, intervalMillis, sender);
        } catch (Exception e) {
            LogUtil.w(e.toString());
        }
    }

    /**
     * sign alarm for action
     *
     * @param action
     * @param startTime
     */
    public void setAlarmTimeForAction(String action, long startTime, long intervalMillis) {
        Intent intent = new Intent(action);
        PendingIntent sender = getSignedPending(intent);

        if (sender == null) {
            sender = PendingIntent.getBroadcast(mContext, REQUEST_BROADCAST_TYPE, intent, 0);
            try {
                mAlarmManager.setRepeating(AlarmManager.RTC, startTime, intervalMillis, sender);
            } catch (Exception e) {
                LogUtil.w(e.toString());
            }
        }
    }

    /**
     * sign alarm for Activity
     *
     * @param clazz
     * @param startTime
     * @param intervalMillis
     */
    public void setAlarmTimeForActivity(Class clazz, long startTime, long intervalMillis) {
        Intent intent = new Intent(mContext, clazz);
        intent.setAction(String.valueOf(startTime));

        PendingIntent sender = getSignedPending(intent);

        if (sender == null) {
            sender = PendingIntent.getActivity(mContext, REQUEST_ACTIVITY_TYPE, intent, 0);
            try {
                mAlarmManager.setRepeating(AlarmManager.RTC, startTime, intervalMillis, sender);
            } catch (Exception e) {
                LogUtil.w(e.toString());
            }
        }
    }

    /**
     * sign alarm for Service
     *
     * @param clazz
     * @param startTime
     * @param intervalMillis
     */
    public void setAlarmTimeForService(Class clazz, long startTime, long intervalMillis) {
        Intent intent = new Intent(mContext, clazz);
        intent.setAction(String.valueOf(startTime));

        PendingIntent sender = getSignedPending(intent);

        if (sender == null) {
            sender = PendingIntent.getService(mContext, REQUEST_SERVICE_TYPE, intent, 0);
            try {
                mAlarmManager.setRepeating(AlarmManager.RTC, startTime, intervalMillis, sender);
            } catch (Exception e) {
                LogUtil.w(e.toString());
            }
        }
    }

    public void setAlarmTimeForServiceOnce(Class clazz, long startTime) {
        Intent intent = new Intent(mContext, clazz);
        intent.setAction(String.valueOf(startTime));

        PendingIntent sender = getSignedPending(intent);

        if (sender == null) {
            sender = PendingIntent.getService(mContext, REQUEST_SERVICE_TYPE, intent, 0);
            mAlarmManager.set(AlarmManager.RTC, startTime, sender);
        }
    }

    /**
     * @param clazz
     * @param action
     * @param startTime
     * @param intervalMillis
     */
    public void setAlarmTimeForService(Class clazz, String action, long startTime, long intervalMillis) {
        Intent intent = new Intent(mContext, clazz);
        intent.setAction(action);

        PendingIntent sender = getSignedPending(intent);

        if (sender == null) {
            sender = PendingIntent.getService(mContext, REQUEST_SERVICE_TYPE, intent, 0);
            try {
                mAlarmManager.setRepeating(AlarmManager.RTC, startTime, intervalMillis, sender);
            } catch (Exception e) {
                LogUtil.w(e.toString());
            }
        }
    }

    /**
     * update the alarm with the speicfied action for broadcast
     *
     * @param action
     * @param startTime
     * @param intervalMillis
     */
    public void updateAlarmTimeForAction(String action, long startTime, long intervalMillis) {
        Intent intent = new Intent(action);
        PendingIntent sender = PendingIntent.getBroadcast(mContext, REQUEST_BROADCAST_TYPE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            mAlarmManager.setRepeating(AlarmManager.RTC, startTime, intervalMillis, sender);
        } catch (Exception e) {
            LogUtil.w(e.toString());
        }
        mAlarmManager.set(AlarmManager.RTC, startTime, sender);
    }


    public void updateAlarmTimeForActionOnce(String action, long startTime) {
        Intent intent = new Intent(action);
        PendingIntent sender = PendingIntent.getBroadcast(mContext, REQUEST_BROADCAST_TYPE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.set(AlarmManager.RTC, startTime, sender);
    }

    /**
     * update the alarm with the action for service
     *
     * @param clazz
     * @param action
     * @param startTime
     * @param intervalMillis
     */
    public void updateAlarmTimeForService(Class clazz, String action, long startTime, long intervalMillis) {
        Intent intent = new Intent(mContext, clazz);
        intent.setAction(action);

        PendingIntent sender = PendingIntent.getService(mContext, REQUEST_SERVICE_TYPE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            mAlarmManager.setRepeating(AlarmManager.RTC, startTime, intervalMillis, sender);
        } catch (Exception e) {
            LogUtil.w(e.toString());
        }
    }

}
package com.bolaa.sleepingbar.watch;

import java.util.UUID;

/**
 * Created by Administrator on 2016/5/27.
 */
public class WatchConstant {
    public final static String UUID_STRING_SERVICE = "000056ff-0000-1000-8000-00805f9b34fb";//蓝牙可用服务的uuid
    public final static String UUID_STRING_CHARA_READ = "000033f4-0000-1000-8000-00805f9b34fb";//可用通道
    public final static String UUID_STRING_CHARA_WRITE = "000033f3-0000-1000-8000-00805f9b34fb";//可用通道
    public final static UUID UUID_SERVICE = UUID.fromString(UUID_STRING_SERVICE);
    public final static UUID UUID_CHARA_READ = UUID.fromString(UUID_STRING_CHARA_READ);
    public final static UUID UUID_CHARA_WRITE = UUID.fromString(UUID_STRING_CHARA_WRITE);

    public final static String ACTION_WATCH_CONNECTED_SUCCESS="com.bolaa.action.WATCH.CONNECTED.SUCCESS";//连接成功

    public final static String ACTION_WATCH_UPDATE_STEP="com.bolaa.action.WATCH.CMD.STEP";//更新  (33f4)
    public final static String ACTION_WATCH_UPDATE_RUN="com.bolaa.action.WATCH.CMD.RUN";//更新  (33f4)
    public final static String ACTION_WATCH_UPDATE_SLEEP="com.bolaa.action.WATCH.CMD.SLEEP";//更新步子信息  (33f4)
    public final static String ACTION_WATCH_CMD_SET_INFO="com.bolaa.action.WATCH.CMD.SETINFO";//写入个人信息
    public final static String ACTION_WATCH_CMD_SET_DATE="com.bolaa.action.WATCH.CMD.SETDATE";//写入时间   (33f3)

    public final static String FLAG_USER_INFO="flag_user_info";
    public final static String FLAG_DEVICE_DATE="flag_device_date";
    public final static String FLAG_STEP_INFO="flag_step_info";
    public final static String FLAG_RUN_INFO="flag_run_info";
}

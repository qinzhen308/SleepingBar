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
    public final static String ACTION_WATCH_CONNECTED_SUCCESS_NOTIFY_HOME="com.bolaa.action.WATCH.CONNECTED.SUCCESS.HOME";//连接成功,通知首页更改状态

    public final static String ACTION_WATCH_UPDATE_STEP="com.bolaa.action.WATCH.CMD.STEP";//更新  (33f4)
    public final static String ACTION_WATCH_UPDATE_RUN="com.bolaa.action.WATCH.CMD.RUN";//更新  (33f4)
    public final static String ACTION_WATCH_UPDATE_SLEEP="com.bolaa.action.WATCH.UPDATE.SLEEP";//因为算法放客户端，所以暂时没用，更新客户端手环信息
    public final static String ACTION_WATCH_CMD_SET_INFO="com.bolaa.action.WATCH.CMD.SETINFO";//写入个人信息
    public final static String ACTION_WATCH_CMD_SET_DATE="com.bolaa.action.WATCH.CMD.SETDATE";//写入时间   (33f3)
    public final static String ACTION_WATCH_CMD_GET_SLEEP="com.bolaa.action.WATCH.CMD.SLEEP";//同步手环睡眠信息到客户端   (33f3)

    public final static String FLAG_USER_INFO="flag_user_info";
    public final static String FLAG_DEVICE_DATE="flag_device_date";
    public final static String FLAG_STEP_INFO="flag_step_info";
    public final static String FLAG_RUN_INFO="flag_run_info";
    public final static String FLAG_STEP_CACHE="flag_step_cache";//缓存的步数信息，每次打开客户端会上传,然后再清除
    public final static String FLAG_STEP_CACHE_FOR_LOOK="flag_step_cache_for_look";//缓存的不步行信息，用来展示的,每天清空
    public final static String FLAG_IS_WATCH_CONNECTED="flag_is_watch_connected";//手环是否连接状态的标记
    public final static String FLAG_SLEEP_DATA_FOR_MAC="flag_sleep_data_for_mac";//采集的数据属于哪个设备
}

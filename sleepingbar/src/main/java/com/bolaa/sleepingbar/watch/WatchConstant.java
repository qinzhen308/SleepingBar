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
}

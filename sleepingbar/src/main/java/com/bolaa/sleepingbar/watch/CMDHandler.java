package com.bolaa.sleepingbar.watch;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;

import com.bolaa.sleepingbar.utils.DateUtil;
import com.core.framework.develop.LogUtil;
import com.core.framework.store.sharePer.PreferencesUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by pualz on 2016/5/30.
 */
public class CMDHandler {
    /**
     * 旧版协议  过期
     */
    @Deprecated
    public final static int CMD_SLEEP_ITEM=0xFE;//睡眠数据,A-B的睡眠记为一次睡眠数据
    @Deprecated
    public final static int CMD_STEP_NOW=0xF9;//睡眠数据,A-B的睡眠记为一次睡眠数据

    public final static byte CMD_SET_INFO=0x02;//设置用户信息
    public final static byte CMD_SET_DATE=0x01;//设置日期
    public final static byte CMD_MOVEMENT=0x03;//运动信息（采集时间，总步数，距离，卡路里，睡眠总数）
    public final static byte CMD_RUN_STEP=0x13;//跑步信息 (采集时间，跑步数)
    public final static byte CMD_MOVEMENT_SOMEDAY=0x10;//运动整天数据 (采集时间，跑步数)
    public final static byte CMD_SLEEP_SOMEDAY=0x11;//睡眠整天数据 (采集时间，跑步数)
    public final static byte CMD_ELECTRIC=0x0B;//电量
    public final static byte CMD_GET_MAC_ADDRESS=0x0C;//手环mac地址

//    /**
//     * 处理源数据，转换成对应对象， 并存入数据库
//     * @param src
//     * @return
//     */
//    public static Object handleToObj(byte[] src){
//        int[] data=Utils.bytesToIntArray(src);
//        switch (data[0]){
//            case CMD_SLEEP_ITEM:
//                Sleep sleep=new Sleep();
//                sleep.startTime=data[1]-data[2];
//                sleep.sleepTime=data[2];
//                sleep.wakeupTime=data[1];
//                SleepTable.getInstance().saveSleep(sleep);
//                return sleep;
//            case CMD_STEP_NOW:
//                Step step=new Step();
//                step.timestamp=data[1];
//                step.value=data[2];
//                StepTable.getInstance().saveStep(step);
//                return step;
//        }
//        return null;
//    }

    /**
     * 处理源数据，转换成对应对象， 并存入数据库
     * @param src
     * @return
     */
    public static void synchronizedMovement(Context context, byte[] src){
        int[] data=Utils.bytesToIntArrayV2(src);
        switch (data[0]){
            case CMD_RUN_STEP:
                context.sendBroadcast(new Intent(WatchConstant.ACTION_WATCH_UPDATE_RUN).putExtra(WatchConstant.FLAG_RUN_INFO,data));
                break;
            case CMD_MOVEMENT:
                context.sendBroadcast(new Intent(WatchConstant.ACTION_WATCH_UPDATE_STEP).putExtra(WatchConstant.FLAG_STEP_INFO,data));
                break;
        }
    }

    //返回值代表是否继续昨天的
    public static boolean saveSleep(byte[] src){
        if(!(src[0]==CMD_SLEEP_SOMEDAY||src[0]==CMD_MOVEMENT_SOMEDAY))return false;
        PreferencesUtils.putBoolean("sleep_data_synching_at_watch",true);//设置状态为同步中，防止上传
        int time=((src[4]<<24)&0xff000000)|((src[3]<<16)&0x00ff0000)|((src[2]<<8)&0x0000ff00)|((src[1]&0x000000ff));
        String date=DateUtil.getYMD_GMTTime(((long)time)*1000);
        String today=DateUtil.getYMD_GMTTime(System.currentTimeMillis());
        String sleep_data;
        if(date.equals(today)){
            sleep_data=PreferencesUtils.getString("sleep_data_today");
        }else {
            sleep_data=PreferencesUtils.getString("sleep_data_yesterday");
        }
        byte[] data=null;
        try {
            data=sleep_data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(data==null||data.length!=1440){
            data=new byte[1440];
        }

        int index=((time/60)%(1440))/15;
        LogUtil.d("save sleep---"+date+"---index="+index+"---today="+today);
        if(src[0]==CMD_SLEEP_SOMEDAY){
            for(int i=0;i<15;i++){
                data[index*15+i]=src[i+5];
            }
        }else if(src[0]==CMD_MOVEMENT_SOMEDAY){
            for(int i=0;i<15;i++){
                data[index*15+i]=0;
            }
        }
        LogUtil.d("save sleep---"+Arrays.toString(data));
        try {
            if(date.equals(today)){
                PreferencesUtils.putString("sleep_data_today",new String(data,"UTF-8"));
            }else {
                PreferencesUtils.putString("sleep_data_yesterday",new String(data,"UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(date.equals(today)&&index==95){//今天的读取结束了，开始读昨天的
            String sleep_date=DateUtil.getYMDTime(System.currentTimeMillis()-((long)1000)*60*60*24);//对应日期
            PreferencesUtils.putString("sleep_data_collect_date",sleep_date);
            PreferencesUtils.putString(WatchConstant.FLAG_SLEEP_DATA_FOR_MAC,PreferencesUtils.getString(WatchService.FLAG_CURRENT_DEVICE_ADDRESS));
            return true;
        }
        if(!date.equals(today)&&index==95){//今天的读取结束了，开始读昨天的
            PreferencesUtils.putBoolean("sleep_data_synching_at_watch",false);//是否正在同步中
        }
        return false;
    }

    public static BluetoothGattCharacteristic cmdGetSleepInfo(BluetoothGattCharacteristic characteristic,byte beforeNow){
        characteristic.setValue(new byte[]{CMD_MOVEMENT_SOMEDAY,beforeNow});
        return characteristic;
    }

    public static BluetoothGattCharacteristic cmdSetInfo(BluetoothGattCharacteristic characteristic, byte sex, byte age, byte height, byte weight){
        if(age>127||age<7)return null;
        byte AA=(byte) ((sex<<7)|age);
        System.out.println(AA);
        byte BB=height;
        byte CC=weight;
        characteristic.setValue(new byte[]{CMD_SET_INFO,AA,BB,CC});
        return characteristic;
    }

    public static BluetoothGattCharacteristic cmdSetDate(BluetoothGattCharacteristic characteristic, int time){
        int byteNum = (40 -Integer.numberOfLeadingZeros (time < 0 ? ~time : time))/ 8;
        byte[] byteArray = new byte[20];
        byteArray[0]=CMD_SET_DATE;
        for (int n = 0; n < byteNum; n++)
            byteArray[n+1] = (byte) (time>>> (n * 8));
        characteristic.setValue(byteArray);
        return characteristic;
    }

    public static BluetoothGattCharacteristic cmdGetMacAddress(BluetoothGattCharacteristic characteristic){
        byte[] byteArray = new byte[20];
        byteArray[0]=CMD_GET_MAC_ADDRESS;
        characteristic.setValue(byteArray);
        return characteristic;
    }

    private byte[] intToByteArray(final int integer) {
        int byteNum = (40 -Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer))/ 8;
        byte[] byteArray = new byte[4];
        for (int n = 0; n < byteNum; n++)
            byteArray[n] = (byte) (integer>>> (n * 8));
        return (byteArray);
    }

}

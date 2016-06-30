package com.bolaa.sleepingbar.watch;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.bolaa.sleepingbar.model.Sleep;
import com.bolaa.sleepingbar.model.Step;
import com.bolaa.sleepingbar.model.tables.SleepTable;
import com.bolaa.sleepingbar.model.tables.StepTable;

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
    public static Object handleToObj(byte[] src){
        int[] data=Utils.bytesToIntArrayV2(src);
        switch (data[0]){
            case CMD_RUN_STEP:
                Sleep sleep=new Sleep();
                sleep.startTime=data[1]-data[2];
                sleep.sleepTime=data[2];
                sleep.wakeupTime=data[1];
                SleepTable.getInstance().saveSleep(sleep);
                return sleep;
            case CMD_MOVEMENT:
                Step step=new Step();
                step.timestamp=data[1];
                step.value=data[2];
                StepTable.getInstance().saveStep(step);
                return step;
        }
        return null;
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

    private byte[] intToByteArray(final int integer) {
        int byteNum = (40 -Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer))/ 8;
        byte[] byteArray = new byte[4];
        for (int n = 0; n < byteNum; n++)
            byteArray[n] = (byte) (integer>>> (n * 8));
        return (byteArray);
    }



}

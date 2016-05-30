package com.bolaa.sleepingbar.watch;

import com.bolaa.sleepingbar.model.Sleep;
import com.bolaa.sleepingbar.model.Step;
import com.bolaa.sleepingbar.model.tables.SleepTable;
import com.bolaa.sleepingbar.model.tables.StepTable;

/**
 * Created by pualz on 2016/5/30.
 */
public class CMDHandler {
    public final static int CMD_SLEEP_ITEM=0xFE;//睡眠数据,A-B的睡眠记为一次睡眠数据
    public final static int CMD_STEP_NOW=0xF9;//睡眠数据,A-B的睡眠记为一次睡眠数据

    /**
     * 处理源数据，转换成对应对象， 并存入数据库
     * @param src
     * @return
     */
    public static Object handleToObj(byte[] src){
        int[] data=Utils.bytesToIntArray(src);
        switch (data[0]){
            case CMD_SLEEP_ITEM:
                Sleep sleep=new Sleep();
                sleep.startTime=data[1]-data[2];
                sleep.sleepTime=data[2];
                sleep.wakeupTime=data[1];
                SleepTable.getInstance().saveSleep(sleep);
                return sleep;
            case CMD_STEP_NOW:
                Step step=new Step();
                step.timestamp=data[1];
                step.value=data[2];
                StepTable.getInstance().saveStep(step);
                return step;
        }
        return null;
    }

}

package com.bolaa.sleepingbar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.common.GlobeFlags;
import com.bolaa.sleepingbar.ui.MainActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.core.framework.develop.LogUtil;
import com.core.framework.store.sharePer.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by paulz on 2016/6/13.
 */
public class PushMsgReceiver extends BroadcastReceiver{

    private static List<MsgListener> msgObservers=new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        LogUtil.d("jpush---receiver--action="+action);
        if(JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)){
            Intent intent1=new Intent(context,MainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }else if (JPushInterface.ACTION_REGISTRATION_ID.equals(action)){
            HApplication.getInstance().push_regestion_id=JPushInterface.getRegistrationID(context);
            PreferencesUtils.putString(GlobeFlags.FLAG_PUSH_REGISTION_ID,HApplication.getInstance().push_regestion_id);
            if(!AppUtil.isNull(HApplication.getInstance().push_regestion_id)){
                HApplication.getInstance().uploadRegistrationId(HApplication.getInstance().push_regestion_id);
            }
        }

    }

    public static void addMsgListener(MsgListener msgListener){
        msgObservers.add(msgListener);
    }
    public static void removeMsgListener(MsgListener msgListener){
        msgObservers.remove(msgListener);
    }

    public static void clearListeners(){
        msgObservers.clear();
    }

    public interface MsgListener{
        public void onUpdateCount(Object... args);
    }


}

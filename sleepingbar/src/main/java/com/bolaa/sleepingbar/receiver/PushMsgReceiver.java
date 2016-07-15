package com.bolaa.sleepingbar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.common.GlobeFlags;
import com.bolaa.sleepingbar.ui.BBSPostsDetailActivity;
import com.bolaa.sleepingbar.ui.MainActivity;
import com.bolaa.sleepingbar.ui.SupporterActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.core.framework.develop.LogUtil;
import com.core.framework.store.sharePer.PreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
            jumpTo(context,intent);
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

    private void jumpTo(Context context,Intent intent){
        Intent tagIntent=null;
        Bundle bundle = intent.getExtras();
        String message = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String pushId= bundle.getString(JPushInterface.EXTRA_MSG_ID);
        LogUtil.d("jpush---receiver---open---extra="+message);
        JSONObject msgJson= null;
        try {
            msgJson = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(msgJson!=null){
            int type=msgJson.optInt("ptype");
            String link_id=msgJson.optString("link_id");
            if(type==1||type==2){
                tagIntent=new Intent(context,BBSPostsDetailActivity.class);
                tagIntent.putExtra(GlobeFlags.FLAG_PUSH_ID,pushId);
                tagIntent.putExtra("posts_id",link_id);
                tagIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }else if(type==3){
                tagIntent=new Intent(context,SupporterActivity.class);
                tagIntent.putExtra(GlobeFlags.FLAG_PUSH_ID,pushId);
                tagIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }else {
                tagIntent=new Intent(context,MainActivity.class);
                tagIntent.putExtra(GlobeFlags.FLAG_PUSH_ID,pushId);
                tagIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }else {
            tagIntent=new Intent(context,MainActivity.class);
            tagIntent.putExtra(GlobeFlags.FLAG_PUSH_ID,pushId);
            tagIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(tagIntent);
    }


}

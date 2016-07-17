package com.bolaa.sleepingbar.watch;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.ui.SleepTrendActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.DateUtil;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.store.sharePer.PreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Random;
import java.util.Set;

/**
 * Created by paulz on 2016/7/13.
 */
public class WatchUploadService extends IntentService{
    private static final int INTERVAL = 1000 * 60 * 60 * 24;// 24h
    public static final String ACTION_WATCH_UPLOAD_SERVICE="com.bolaa.sleepingbar.ACTION.WATCH.UPLOAD.SERVICE";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public WatchUploadService() {
        super(WatchUploadService.class.getSimpleName());
    }


    public static void setAlarm(Context context){
//        if(PreferencesUtils.getBoolean("has_watch_alarm"))return;

        Intent intent = new Intent(context, WatchUploadService.class);
        intent.setData(Uri.parse("content://com.bolaa.sleepingbar"));
        PendingIntent sender = PendingIntent.getService(context, 10010, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        // Schedule the alarm!
        intent.setAction(ACTION_WATCH_UPLOAD_SERVICE);
        AlarmManager am = (AlarmManager) context .getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
        Calendar calendar = Calendar.getInstance(); calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 30+(new Random().nextInt(10)-5));
        calendar.set(Calendar.SECOND, new Random().nextInt(60));
        calendar.set(Calendar.MILLISECOND, 0);
//        calendar.set(Calendar.MINUTE, 36);
//        calendar.set(Calendar.SECOND,30);
//        calendar.set(Calendar.MILLISECOND, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, sender);
        PreferencesUtils.putBoolean("has_watch_alarm",true);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        PreferencesUtils.putInteger("launch_synch_service_count",PreferencesUtils.getInteger("launch_synch_service_count",0)+1);
        if(PreferencesUtils.getBoolean("isLogin")){
            PreferencesUtils.putInteger("start_synch_count",PreferencesUtils.getInteger("start_synch_count",0)+1);
            getCollectTime();
        }
    }

    private void getCollectTime(){
        ParamBuilder params=new ParamBuilder();
        NetworkWorker.getInstance().getCallbackInBg(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SLEEP_DATA_COLLECT_TIME), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(status==200){
                    BaseObject<SleepTrendActivity.SleepCollectTime> object= GsonParser.getInstance().parseToObj(result, SleepTrendActivity.SleepCollectTime.class);
                    if(object!=null){
                        if(object.data!=null&&object.status==BaseObject.STATUS_OK){

                            uploadTodayData(object.data);
                        }else {
                        }
                    }else {

                    }
                }

            }
        });
    }

    private void uploadTodayData(SleepTrendActivity.SleepCollectTime collectTime){
        int[] indexs=collectTime.getCollectIndexs();
        int start=indexs[0]-60*8;
        int end=indexs[1]-60*8-1;//不包含最后一个点
        if(end<0){
            end=-1;
            collectTime.sleep_end_time="08:00";
        }
        String sleep_data= PreferencesUtils.getString("sleep_data_yesterday");
        byte[] data=new byte[1440-start+end+1];
        try {
            LogUtil.d("upload today---data length="+data.length);
            byte[] data1=sleep_data.getBytes("UTF-8");
            if(!AppUtil.isEmpty(data1)){
                System.arraycopy(data1,start,data,0,1440-start);
            }
            //今天
            sleep_data=PreferencesUtils.getString("sleep_data_today");
            byte[] data2=sleep_data.getBytes("UTF-8");
            if(!AppUtil.isEmpty(data2)) {
                System.arraycopy(data2, 0, data, 1440 - start, end + 1);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        upload(data,collectTime.sleep_start_time,collectTime.sleep_end_time);
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void upload(final byte[] data, final String start, String end){
        HttpRequester requester=new HttpRequester();
        JSONArray array= null;
        try {
            array = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(array==null)return;

        requester.getParams().put("data",array.toString());
//        long time= (new Date().getTime()/((60*60*24)*1000))*(60*60*24);
        final String sign= PreferencesUtils.getString("user_id")+"_"+System.currentTimeMillis()/1000+"_"+133;
        final String sleep_date=PreferencesUtils.getString("sleep_data_collect_date");
        requester.getParams().put("sleep_date", sleep_date);
        requester.getParams().put("sign",sign);
        requester.getParams().put("sleep_end_time",end);
        requester.getParams().put("sleep_start_time",start);
//        LogUtil.d("upload---date="+ DateUtil.getTimeUnitSecond("yyyy-MM-dd hh:mm:ss",time));
        LogUtil.d("upload---sign="+ sign);
        NetworkWorker.getInstance().postCallbackInBg(AppUrls.getInstance().URL_WATCH_SYNC_SLEEP, new NetworkWorker.ICallback() {
            @Override
            public void onResponse(final int status, String result) {
                if(status==200){
                    LogUtil.d("upload watch sleep data---result="+result);
                    final BaseObject<Object> obj=GsonParser.getInstance().parseToObj(result,Object.class);
                    if(obj!=null&&obj.status==BaseObject.STATUS_OK){

                        PreferencesUtils.putInteger("synch_success_count",PreferencesUtils.getInteger("synch_success_count",0)+1);

                    }else {
//                        try {
//                            Set<String> set=PreferencesUtils.getSet("not_synch_sleep_data");
//                            if(set!=null&&set.contains())
//                            PreferencesUtils.putSet("not_synch_sleep_data",sleep);
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
                    }


                }
            }
        },requester);

    }
}

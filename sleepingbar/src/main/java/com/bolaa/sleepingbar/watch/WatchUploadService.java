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
import android.util.Log;

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
import com.core.framework.util.MD5Util;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by paulz on 2016/7/13.
 */
public class WatchUploadService extends IntentService{
    private static final int INTERVAL = 1000 * 60 * 60 * 24;// 24h
    public static final String ACTION_WATCH_UPLOAD_SERVICE="com.bolaa.sleepingbar.ACTION.WATCH.UPLOAD.SERVICE";

    private boolean isStartByDateChanged;
    private String once_uplaod_sleep_data_today;
    private String once_uplaod_sleep_data_yesterday;
    private String once_uplaod_sleep_data_date;
    private String once_uplaod_sleep_data_mac;

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
        PendingIntent sender = PendingIntent.getService(context, 10010, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Schedule the alarm!
        intent.setAction(ACTION_WATCH_UPLOAD_SERVICE);
        AlarmManager am = (AlarmManager) context .getSystemService(Context.ALARM_SERVICE);
        //均衡服务器的压力，设置上报时间为10点半前后5分钟内。
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 30+(new Random().nextInt(10)-5));
        calendar.set(Calendar.SECOND, new Random().nextInt(60));
        calendar.set(Calendar.MILLISECOND, 0);
        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        long selectTime=calendar.getTimeInMillis();
        if(System.currentTimeMillis() > selectTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            selectTime = calendar.getTimeInMillis();
        }
//        calendar.set(Calendar.MINUTE, 36);
//        calendar.set(Calendar.SECOND,30);
//        calendar.set(Calendar.MILLISECOND, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP,selectTime , INTERVAL, sender);
//        PreferencesUtils.putBoolean("has_watch_alarm",true);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtil.d("alarm---onHandleIntent---start");
        isStartByDateChanged=intent.getBooleanExtra(WatchConstant.FLAG_IS_START_BY_DATE_CHANGED,false);
        if(!isStartByDateChanged){
            once_uplaod_sleep_data_today=intent.getStringExtra(WatchConstant.FLAG_ONCE_UPLOAD_SLEEP_DATA_TODAY);
            once_uplaod_sleep_data_yesterday=intent.getStringExtra(WatchConstant.FLAG_ONCE_UPLOAD_SLEEP_DATA_YESTERDAY);
            once_uplaod_sleep_data_date=intent.getStringExtra(WatchConstant.FLAG_ONCE_UPLOAD_SLEEP_DATA_DATE);
            once_uplaod_sleep_data_mac=intent.getStringExtra(WatchConstant.FLAG_ONCE_UPLOAD_SLEEP_DATA_MAC);
            if (AppUtil.isNull(once_uplaod_sleep_data_date)){
                LogUtil.d("alarm---onHandleIntent---睡眠无效数据");
                return;
            }
        }
        PreferencesUtils.putInteger("launch_synch_service_count",PreferencesUtils.getInteger("launch_synch_service_count",0)+1);
        if(PreferencesUtils.getBoolean("isLogin")){
            int count=0;
            while (PreferencesUtils.getBoolean("sleep_data_synching_at_watch")||count<30){
                try {
                    count++;
                    Thread.currentThread().sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            PreferencesUtils.putInteger("start_synch_count",PreferencesUtils.getInteger("start_synch_count",0)+1);
            getCollectTime();
        }
    }

    private void getCollectTime(){
        LogUtil.d("alarm---onHandleIntent---getCollectTime");
        ParamBuilder params=new ParamBuilder();
        NetworkWorker.getInstance().getCallbackInBg(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SLEEP_DATA_COLLECT_TIME), new NetworkWorker.ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(status==200){
                    BaseObject<SleepTrendActivity.SleepCollectTime> object= GsonParser.getInstance().parseToObj(result, SleepTrendActivity.SleepCollectTime.class);
                    if(object!=null){
                        if(object.data!=null&&object.status==BaseObject.STATUS_OK){
                            int[] indexs=object.data.getCollectIndexs();
                            int start=indexs[0]-60*8;
                            int end=indexs[1]-60*8-1;//不包含最后一个点
                            int todayIndex=(int)(System.currentTimeMillis()/(1000*60))%(60*24);
                            LogUtil.d("alarm---onHandleIntent---getCollectTime---today index="+todayIndex);
                            if(todayIndex>end&&todayIndex<start){
                                uploadTodayData(object.data);
                            }else if(isStartByDateChanged){
                                uploadTodayData(object.data);
                            }
                        }else {
                        }
                    }else {

                    }
                }

            }
        });
    }

    private void uploadTodayData(SleepTrendActivity.SleepCollectTime collectTime){
        LogUtil.d("alarm---onHandleIntent---uploadTodayData");

        //因为手环系统时间比现实快了8小时，且手环里面的时间是都是转换成GMT+0来处理逻辑的，所以相当于手环里算出的时间是北京时间。所以取值不用进行时区转换
        //不转换时区，也就不存在越界（时间跨天）的问题
        int[] indexs=collectTime.getCollectIndexs();int start=indexs[0];
        int end=indexs[1]-1;//不包含最后一个点
//        int start=indexs[0]-60*8;
//        int end=indexs[1]-60*8-1;//不包含最后一个点
//        if(end<0){
//            end=-1;
//            collectTime.sleep_end_time="08:00";
//        }
        String sleep_data= "";
        if(isStartByDateChanged){
            sleep_data=PreferencesUtils.getString("sleep_data_yesterday");
        }else {
            sleep_data=once_uplaod_sleep_data_yesterday;
        }
        byte[] data=new byte[1440-start+end+1];
        try {
            LogUtil.d("upload today---data length="+data.length);
            byte[] data1=sleep_data.getBytes("UTF-8");
            if(!AppUtil.isEmpty(data1)){
                System.arraycopy(data1,start,data,0,1440-start);
            }
            //今天
            if(isStartByDateChanged){
                sleep_data=PreferencesUtils.getString("sleep_data_today");
            }else {
                sleep_data=once_uplaod_sleep_data_today;
            }
            byte[] data2=sleep_data.getBytes("UTF-8");
            if(!AppUtil.isEmpty(data2)) {
                System.arraycopy(data2, 0, data, 1440 - start, end + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(AppUtil.isEmpty(data)){
            data=new byte[1440-start+end+1];
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
        String sleep_date="";
        if(isStartByDateChanged){
            sleep_date=PreferencesUtils.getString("sleep_data_collect_date");
        }else {
            sleep_date=once_uplaod_sleep_data_date;
        }
        String uinfoid=PreferencesUtils.getString("user_id");
        String mac="";
        if(isStartByDateChanged){
            mac=PreferencesUtils.getString(WatchConstant.FLAG_SLEEP_DATA_FOR_MAC);
        }else {
            mac=once_uplaod_sleep_data_mac;
        }
        final String sign= MD5Util.getMD5(uinfoid.concat("#").concat(sleep_date).concat("#").concat(start).concat("#").concat(end).concat("#").concat(mac).concat("_iphone_android_@2016y"));
        requester.getParams().put("sleep_date", sleep_date);
        requester.getParams().put("sign",sign);
        requester.getParams().put("sleep_end_time",end);
        requester.getParams().put("sleep_start_time",start);
        requester.getParams().put("uinfoid",uinfoid);
        requester.getParams().put("mac",mac);
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

                        PreferencesUtils.remove("sleep_data_today");
                        PreferencesUtils.remove("sleep_data_yesterday");
                        PreferencesUtils.remove("sleep_data_collect_date");

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

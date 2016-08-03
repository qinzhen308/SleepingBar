package com.bolaa.sleepingbar.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;


import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.DateUtil;
import com.bolaa.sleepingbar.view.TrendViewV2;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.DialogUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * 基本信息
 * 
 * @author paulz
 * 
 */
public class SleepTrendActivity extends BaseActivity {

	private TrendViewV2 dayTrend;
	private TrendViewV2 weekTrend;
	private TrendViewV2 monthTrend;
	private TrendViewV2 yearTrend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActiviyContextView(R.layout.activity_sleep_trend);
		setTitleText("", "睡眠趋势", 0, true);
		initView();
		setListener();
        loadSleepData();
        LogUtil.d("sleep_upload_flags----has alrm="+PreferencesUtils.getBoolean("has_watch_alarm"));
        LogUtil.d("sleep_upload_flags----launch service="+PreferencesUtils.getInteger("launch_synch_service_count"));
        LogUtil.d("sleep_upload_flags----start upload="+PreferencesUtils.getInteger("start_synch_count"));
        LogUtil.d("sleep_upload_flags----synch success count="+PreferencesUtils.getInteger("synch_success_count"));
	}

	private void initView() {
		dayTrend=(TrendViewV2)findViewById(R.id.trend_day);
		weekTrend=(TrendViewV2)findViewById(R.id.trend_week);
        monthTrend=(TrendViewV2)findViewById(R.id.trend_month);
        yearTrend=(TrendViewV2)findViewById(R.id.trend_year);
        weekTrend.setType(TrendViewV2.TYPE_WEEK);
        monthTrend.setType(TrendViewV2.TYPE_MONTH);
        yearTrend.setType(TrendViewV2.TYPE_YEAR);
    }
	
	private void setListener() {
		// TODO Auto-generated method stub

	}


	private void initData() {

        byte[] src=new byte[1440];
        byte[] level={0,10,20,30,50,70,90,100};
		Random random=new Random();
		for(int i=0;i<96;i++){
            int index=random.nextInt(8);
            byte l=level[index];
            for(int j=0;j<15;j++){
                if(l==0){
                    src[15*i+j]=(byte)0;
                }else {
                    src[15*i+j]=(byte)(random.nextInt(l-level[index-1])+level[index-1]);
                }
            }
		}
		dayTrend.setData(src);
	}

    public void loadSleepData() {
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params=new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_GET_SLEEP_IN_YEAR), new NetworkWorker.ICallback() {

            @Override
            public void onResponse(int status, String result) {
                if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
                if(status==200){
                    BaseObject<SleepData> object= GsonParser.getInstance().parseToObj(result, SleepData.class);
                    if(object!=null){
                        if(object.data!=null&&object.status==BaseObject.STATUS_OK){
                            dayTrend.setData(object.data.day_trend);
                            weekTrend.setData(object.data.week_trend);
                            monthTrend.setData(object.data.mouth_trend);
                            yearTrend.setData(object.data.year_trend);
                        }else {
                            AppUtil.showToast(getApplicationContext(),"解析出错");
                        }
                    }else {
                        AppUtil.showToast(getApplicationContext(),"请求失败");
                    }
                }

            }
        });
    }

    private void getCollectTime(){
            ParamBuilder params=new ParamBuilder();
            NetworkWorker.getInstance().getCallbackInBg(APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_SLEEP_DATA_COLLECT_TIME), new NetworkWorker.ICallback() {
                @Override
                public void onResponse(int status, String result) {
                    if(status==200){
                        BaseObject<SleepCollectTime> object= GsonParser.getInstance().parseToObj(result, SleepCollectTime.class);
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

    private void uploadTodayData(SleepCollectTime collectTime){
        int[] indexs=collectTime.getCollectIndexs();
        int start=indexs[0]-60*8;
        int end=indexs[1]-60*8-1;//不包含最后一个点
        if(end<0){
            end=-1;
            collectTime.sleep_end_time="08:00";
        }
        String sleep_data=PreferencesUtils.getString("sleep_data_yesterday");
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
//        for(int i=0;i<data.length;i++){
//            requester.getParams().put("data["+i+"]",data[i]);
//        }
        requester.getParams().put("data",array.toString());
//        long time= (new Date().getTime()/((60*60*24)*1000))*(60*60*24);
        String sign=AppStatic.getInstance().getmUserInfo().user_id+"_"+System.currentTimeMillis()/1000+"_"+133;
        requester.getParams().put("sleep_date",DateUtil.getYMD_GMTTime(System.currentTimeMillis()));
        requester.getParams().put("sign",sign);
        requester.getParams().put("sleep_end_time",end);
        requester.getParams().put("sleep_start_time",start);
//        LogUtil.d("upload---date="+ DateUtil.getTimeUnitSecond("yyyy-MM-dd hh:mm:ss",time));
        LogUtil.d("upload---sign="+ sign);
        NetworkWorker.getInstance().postCallbackInBg(AppUrls.getInstance().URL_WATCH_SYNC_SLEEP, new ICallback() {
            @Override
            public void onResponse(final int status, String result) {
                if(status==200){
                    LogUtil.d("upload---result="+result);
                    final BaseObject<DaySleepPerHour> obj=GsonParser.getInstance().parseToObj(result,DaySleepPerHour.class);
                    if(obj!=null&&obj.status==BaseObject.STATUS_OK){
                        try {
                            PreferencesUtils.putString("sleep_data_per_hour",new String(obj.data.day_chart,"UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dayTrend.setData(obj.data.day_chart);
                            }
                        });
                    }


                }
            }
        },requester);

    }



	public static void invoke(Context context){
		Intent intent =new Intent(context,SleepTrendActivity.class);
		context.startActivity(intent);
	}

    public class SleepData{
        public byte[] mouth_trend;
        public byte[] week_trend;
        public byte[] year_trend;
        public byte[] day_trend;
    }

    public class SleepCollectTime{
        public String sleep_end_time;
        public String sleep_start_time;

        public int[] getCollectIndexs(){
            int[] indexs=new int[2];
            String[] h_m=sleep_start_time.split(":");
            indexs[0]=castToInt(h_m[0])*60+castToInt(h_m[1]);
            h_m=sleep_end_time.split(":");
            indexs[1]=castToInt(h_m[0])*60+castToInt(h_m[1]);
            return indexs;
        }

        private int castToInt(String aa){
            if(aa.charAt(0)=='0')return Integer.valueOf(aa.charAt(1)+"");
            return Integer.valueOf(aa);
        }

    }


    public class DaySleepPerHour{
        public byte[] day_chart;
        public long sleep_start_time;
        public String sleep_end_time;
        public String sign;


    }

}

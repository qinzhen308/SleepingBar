package com.bolaa.sleepingbar.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;


import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.utils.DateUtil;
import com.bolaa.sleepingbar.view.TrendView;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.store.sharePer.PreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Random;

/**
 * 基本信息
 * 
 * @author paulz
 * 
 */
public class SleepTrendActivity extends BaseActivity {

	private TrendView dayTrend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActiviyContextView(R.layout.activity_sleep_trend);
		setTitleText("", "睡眠趋势", 0, true);
		initView();
		setListener();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                initData();
                getData();
            }
        },2000);
	}

	private void initView() {
		dayTrend=(TrendView)findViewById(R.id.trend_day);
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

    private void getData(){
        String sleep_data=PreferencesUtils.getString("sleep_data");
        try {
            byte[] data=sleep_data.getBytes("UTF-8");
//            dayTrend.setData(data);
            upload(data);
            byte[] data15Sec=new byte[96];
            for(int i=0;i<data15Sec.length;i++){
                int sum=0;
                for(int j=0;j<15;j++){
                    sum+=data[i*15+j];
                }
                data15Sec[i]=(byte)(sum/15);
            }
            dayTrend.setData(data15Sec);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void upload(byte[] data){
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
        long time= (new Date().getTime()/((60*60*24)*1000))*(60*60*24);
        String sign=AppStatic.getInstance().getmUserInfo().user_id+"_"+System.currentTimeMillis()/1000+"_"+233;
        requester.getParams().put("sleep_date",time+"");
        requester.getParams().put("sign",sign);
        requester.getParams().put("sleep_end_time","23:59");
        requester.getParams().put("sleep_start_time","00:00");
        LogUtil.d("upload---date="+ DateUtil.getTimeUnitSecond("yyyy-MM-dd hh:mm:ss",time));
        LogUtil.d("upload---sign="+ sign);
        NetworkWorker.getInstance().post(AppUrls.getInstance().URL_WATCH_SYNC_SLEEP, new ICallback() {
            @Override
            public void onResponse(int status, String result) {
                if(status==200){
                    LogUtil.d("upload---result="+result);
                }
            }
        },requester);

    }



	public static void invoke(Context context){
		Intent intent =new Intent(context,SleepTrendActivity.class);
		context.startActivity(intent);
	}

}

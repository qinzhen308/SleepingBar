package com.bolaa.sleepingbar.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.model.UserInfo;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.ImageUtil;
import com.bolaa.sleepingbar.view.CircleImageView;
import com.bolaa.sleepingbar.view.TrendView;
import com.bolaa.sleepingbar.view.wheel.NumericWheelAdapter;
import com.bolaa.sleepingbar.view.wheel.OnWheelScrollListener;
import com.bolaa.sleepingbar.view.wheel.WheelView;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.IOSDialogUtil;
import com.core.framework.util.IOSDialogUtil.OnSheetItemClickListener;
import com.core.framework.util.IOSDialogUtil.SheetItemColor;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
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



	public static void invoke(Context context){
		Intent intent =new Intent(context,SleepTrendActivity.class);
		context.startActivity(intent);
	}

}

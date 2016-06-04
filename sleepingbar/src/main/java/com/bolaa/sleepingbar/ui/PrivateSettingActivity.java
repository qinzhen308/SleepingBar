package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;

/**
 * 隐私设置
 * Created by paulz on 16/6/4.
 */
public class PrivateSettingActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setListener();
    }



    private void initView(){
        setActiviyContextView(R.layout.activity_private_setting,false,true);

        setTitleText("","隐私设置",0,true);

    }
    private void setListener(){

    }


    public static void invoke(Context context){
        Intent intent =new Intent(context,PrivateSettingActivity.class);
        context.startActivity(intent);
    }
}

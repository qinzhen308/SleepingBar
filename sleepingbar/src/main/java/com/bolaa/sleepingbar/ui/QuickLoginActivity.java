package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.utils.AppUtil;

/**
 * 快速登录
 * 直接输入手机号码---获取验证码---绑定手环（可跳过）---微信授权（可跳过）
 * Created by paulz on 2016/6/1.
 */
public class QuickLoginActivity extends BaseActivity{
    TextView tvProtocal;
    EditText etPhone;
    TextView btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setListener();
    }

    private void setListener() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=etPhone.getText().toString().trim();
                if(invalidate(phone)){
                    QuickLoginCaptchaActivity.invoke(QuickLoginActivity.this,phone);
                    finish();
                }
            }
        });
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_quick_login, false, false);
        etPhone=(EditText)findViewById(R.id.edit_phone);
        tvProtocal=(TextView)findViewById(R.id.tv_protocol);
        btnNext=(TextView)findViewById(R.id.tv_next);
    }

    private boolean invalidate(String userName) {

        boolean flag = false;
        String tip = "";
        if (userName.length() == 0) {
            tip = "请输入手机号";
        } else if (userName.length() > 11&&userName.length() <14 ) {
            tip = "手机号格式不正确";
        } else {
            flag = true;
        }
        if (!flag)
            AppUtil.showToast(getApplicationContext(), tip);
        return flag;
    }

    public static void invoke(Context context){
        Intent intent=new Intent(context,QuickLoginActivity.class);
        context.startActivity(intent);
    }

}

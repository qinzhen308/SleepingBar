package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.wxapi.WXEntryActivity;
import com.bolaa.sleepingbar.wxapi.WXEntryActivityV2;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;

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
                getCode();
            }
        });

        tvProtocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonWebActivity.invoke(QuickLoginActivity.this, AppUrls.getInstance().URL_ARTICAL_ABOUT,"用户协议");
            }
        });
        etPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPhone.requestFocus(etPhone.getText().length()-1);
                etPhone.setCursorVisible(true);
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        etPhone.clearFocus();
        etPhone.setCursorVisible(false);
        return super.onTouchEvent(event);
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_quick_login, false, false);
        etPhone=(EditText)findViewById(R.id.edit_phone);
        tvProtocal=(TextView)findViewById(R.id.tv_protocol);
        btnNext=(TextView)findViewById(R.id.tv_next);
    }


    private void getCode() {
        final String phone=etPhone.getText().toString().trim();
        if(!invalidate(phone)){
            return;
        }
        DialogUtil.showDialog(lodDialog);
        HttpRequester mRequester = new HttpRequester();
        mRequester.mParams.put("mobile_phone", phone);
        mRequester.mParams.put("verify_type","1" );

        NetworkWorker.getInstance().post(AppUrls.getInstance().URL_GET_CAPTCHA,
                new NetworkWorker.ICallback() {

                    @Override
                    public void onResponse(int status, String result) {
                        Log.e("------getCode---", "------getCode---" + result);
                        if(!isFinishing()){
                            DialogUtil.dismissDialog(lodDialog);
                        }
                        if(status==200){
                            BaseObject<Object> baseObject= GsonParser.getInstance().parseToObj(result, Object.class);
                            if(baseObject!=null&&baseObject.status==BaseObject.STATUS_OK){
                                AppUtil.showToast(getApplicationContext(), "验证码发送成功");
                                QuickLoginCaptchaActivity.invoke(QuickLoginActivity.this,phone);
                                finish();
                            }else {
                                AppUtil.showToast(getApplicationContext(), baseObject==null?"发送失败":baseObject.info);
                            }
                        }else {
                            AppUtil.showToast(getApplicationContext(), "发送失败");
                        }

                    }
                }, mRequester);
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

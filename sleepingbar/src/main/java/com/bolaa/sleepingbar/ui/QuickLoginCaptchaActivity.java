package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.model.UserInfo;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.core.framework.image.universalimageloader.core.ImageLoader;
import com.core.framework.net.NetworkWorker;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.StringUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 快速登录
 * 直接输入手机号码---获取验证码---绑定手环（可跳过）---微信授权（可跳过）
 * Created by paulz on 2016/6/1.
 */
public class QuickLoginCaptchaActivity extends BaseActivity{
    EditText etPhone;
    TextView tvPhone;
    TextView btnNext;
    TextView btnPrevious;
    TextView tvGetCaptcha;

    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExtra();
        initView();
        setListener();
    }

    private void setExtra() {
        phone=getIntent().getStringExtra("phone");
    }

    private void setListener() {
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        tvGetCaptcha.setOnClickListener(this);
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_quick_login_captcha, false, false);
        etPhone=(EditText)findViewById(R.id.edit_captcha);
        tvPhone=(TextView)findViewById(R.id.tv_phone);
        btnNext=(TextView)findViewById(R.id.tv_next);
        btnPrevious=(TextView)findViewById(R.id.tv_phone_again);
        tvGetCaptcha=(TextView)findViewById(R.id.tv_get_captcha);

        tvPhone.setText("手机号码："+phone);
    }

    /**
     * 获取验证码-忘记密码时2
     *
     */
    private void getCode() {
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
                            BaseObject<Object> baseObject=GsonParser.getInstance().parseToObj(result, Object.class);
                            if(baseObject!=null&&baseObject.status==BaseObject.STATUS_OK){
                                AppUtil.showToast(getApplicationContext(), "验证码发送成功");
                                captchaBtnDisabled();
                            }else {
                                AppUtil.showToast(getApplicationContext(), baseObject==null?"发送失败":baseObject.info);
                            }
                        }else {
                            AppUtil.showToast(getApplicationContext(), "发送失败");
                        }

                    }
                }, mRequester);
    }

    Timer timer=null;
    Handler handler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            int i =msg.what;
            if(i>=0){
                tvGetCaptcha.setText(""+i+"s");
            }else {
                tvGetCaptcha.setText("重发验证码");
                tvGetCaptcha.setEnabled(true);
                tvGetCaptcha.setTextColor(getResources().getColor(R.color.purple));
                timer.cancel();
                timer=null;
            }
        };
    };
    private void captchaBtnDisabled(){
        tvGetCaptcha.setEnabled(false);
        tvGetCaptcha.setText("60s");
        tvGetCaptcha.setTextColor(getResources().getColor(R.color.text_grey_french2));
        timer=new Timer();
        timer.schedule(new TimerTask() {
            int i=60;

            @Override
            public void run() {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(--i);
            }
        }, 0, 1000);


    }

    /**
     * 登录
     */
    private void login() {
        if (StringUtil.isEmpty(etPhone.getText().toString())){
            AppUtil.showToast(this, "请输入验证码");
            return ;
        }
        DialogUtil.showDialog(lodDialog);
        HttpRequester requester = new HttpRequester();
        requester.getParams().put("verify_code", etPhone.getText().toString());
        requester.getParams().put("mobile_phone", phone);
        requester.getParams().put("mobile_key", "");

        NetworkWorker.getInstance().post(AppUrls.getInstance().URL_LOGIN, new NetworkWorker.ICallback() {

                    @Override
                    public void onResponse(int status, String result) {
                        if (!isFinishing()) {
                            DialogUtil.dismissDialog(lodDialog);
                        }
                        if(status==200){
                            BaseObject<UserInfo> object= GsonParser.getInstance().parseToObj(result, UserInfo.class);
                            if(object!=null){
                                if(object.data!=null&&object.status==BaseObject.STATUS_OK){
                                    AppUtil.showToast(getApplicationContext(), "登录成功");
                                    HApplication.getInstance().saveToken(object.token);
                                    AppStatic.getInstance().isLogin = true;
                                    PreferencesUtils.putBoolean("isLogin", true);
                                    ImageLoader.getInstance().clearDiscCache();
                                    ImageLoader.getInstance().clearMemoryCache();
                                    AppStatic.getInstance().setmUserInfo(
                                            object.data);
                                    AppStatic.getInstance().saveUser(object.data);
                                    HApplication.getInstance().uploadRegistrationId(HApplication.getInstance().push_regestion_id);
                                    MainActivity.invoke(QuickLoginCaptchaActivity.this);
                                    setResult(RESULT_OK);
                                    finish();
                                }else {
                                    AppUtil.showToast(getApplicationContext(), object.info);
                                }
                            }else {
                                AppUtil.showToast(getApplicationContext(), "请检查网络");
                            }
                        }

                    }
                },requester);
    }


    public static void invoke(Context context,String phone){
        Intent intent=new Intent(context,QuickLoginCaptchaActivity.class);
        intent.putExtra("phone",phone);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v==btnNext){
            login();
        }else if(v==btnPrevious){
            QuickLoginActivity.invoke(this);
        }else if(v==tvGetCaptcha){
            getCode();
        }else {
            super.onClick(v);
        }
    }
}

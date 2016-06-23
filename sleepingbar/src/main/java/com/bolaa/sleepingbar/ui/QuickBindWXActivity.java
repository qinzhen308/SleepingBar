package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * 微信授权
 * 直接输入手机号码---获取验证码---绑定手环（可跳过）---微信授权（可跳过）
 * Created by paulz on 2016/6/1.
 */
public class QuickBindWXActivity extends BaseActivity{
    TextView tvAuthorise;
    TextView tvSkip;
    UMShareAPI mShareAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShareAPI = UMShareAPI.get(this);
        initView();
        setListener();
    }

    private void setListener() {
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvAuthorise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authorise();
            }
        });
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_quick_bind_wx, false, false);
        tvAuthorise =(TextView)findViewById(R.id.tv_authorise);
        tvSkip =(TextView)findViewById(R.id.tv_skip);
    }

    private void authorise(){
        SHARE_MEDIA platform = SHARE_MEDIA.WEIXIN;
        mShareAPI.doOauthVerify(this, platform, umAuthListener);

    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }



    public static void invoke(Context context){
        Intent intent=new Intent(context,QuickBindWXActivity.class);
        context.startActivity(intent);
    }

}

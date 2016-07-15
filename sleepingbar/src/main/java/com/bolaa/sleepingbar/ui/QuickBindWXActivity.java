package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;
import com.google.gson.Gson;
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
                MainActivity.invoke(QuickBindWXActivity.this);
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
        DialogUtil.showDialog(lodDialog);
        SHARE_MEDIA platform = SHARE_MEDIA.WEIXIN;
        mShareAPI.doOauthVerify(this, platform, umAuthListener);

    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        private String access_token;
        private String expires_in;
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            if(platform==SHARE_MEDIA.WEIXIN){
                if(action==UMAuthListener.ACTION_GET_PROFILE){//获取用户信息回来，上传
                    bindThirdUser(data, access_token,expires_in);
                }else if(action==UMAuthListener.ACTION_AUTHORIZE){//授权回来，获取用户信息
                    access_token=data.get("access_token");
                    expires_in=data.get("expires_in");
                    mShareAPI.getPlatformInfo(QuickBindWXActivity.this, platform, umAuthListener);
                }
            }else {
                if(!isFinishing()){
                    DialogUtil.dismissDialog(lodDialog);
                }
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            if(!isFinishing()){
                DialogUtil.dismissDialog(lodDialog);
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            if(!isFinishing()){
                DialogUtil.dismissDialog(lodDialog);
            }
        }
    };

    private void bindThirdUser(Map<String , String > data,String access_token,String expires_in){
        HttpRequester requester=new HttpRequester();
        requester.getParams().put("access_token", access_token);
        requester.getParams().put("expires_in", expires_in);
        requester.getParams().put("openid", data.get("openid"));
        requester.getParams().put("unionid", data.get("unionid"));
        requester.getParams().put("headimgurl", data.get("headimgurl"));
        requester.getParams().put("sex", data.get("sex"));
        requester.getParams().put("nickname", data.get("nickname"));
        NetworkWorker.getInstance().post(AppUrls.getInstance().URL_BIND_WX_USER_INFO, new NetworkWorker.ICallback() {

            @Override
            public void onResponse(int status, String result) {
                // TODO Auto-generated method stub
                if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
                if(status==200){
                    BaseObject<Object> baseObject= GsonParser.getInstance().parseToObj(result,Object.class);
                    if(baseObject!=null){
                        if(baseObject.status==BaseObject.STATUS_OK){
                            HApplication.getInstance().loadUserInfo();
                            AppUtil.showToast(getApplicationContext(),baseObject.info);
                            MainActivity.invoke(QuickBindWXActivity.this);
                            finish();
                        }else {
                            AppUtil.showToast(getApplicationContext(),baseObject.info);
                        }
                    }else {
                        AppUtil.showToast(getApplicationContext(),"绑定微信失败");
                    }
                }
            }
        },requester);
    }


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

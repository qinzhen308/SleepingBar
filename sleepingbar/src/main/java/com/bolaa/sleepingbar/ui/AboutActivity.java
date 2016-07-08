package com.bolaa.sleepingbar.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.DialogUtil;

/**
 * 关于页面,有退出登录
 * Created by paulz on 2016/6/2.
 */
public class AboutActivity extends BaseActivity{

    private TextView tvAbout;
    private TextView tvServiceProtocal;
    private TextView tvLegalNotice;
    private TextView btnLogout;
    private TextView tvVersion;

    private Dialog mLogoutDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExtra();
        initView();
        setListener();
    }

    private void initView() {
        setActiviyContextView(R.layout.activity_about,false,true);
        setTitleText("","设置",0,true);
        tvAbout=(TextView)findViewById(R.id.tv_about);
        tvServiceProtocal=(TextView)findViewById(R.id.tv_service_protocal);
        tvLegalNotice=(TextView)findViewById(R.id.tv_legal_notice);
        btnLogout=(TextView)findViewById(R.id.btn_logout);
        tvVersion=(TextView)findViewById(R.id.tv_version);
        tvVersion.setText("v"+HApplication.getInstance().getVersionName()+" for android");
    }

    private void setListener() {
        btnLogout.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
        tvServiceProtocal.setOnClickListener(this);
        tvLegalNotice.setOnClickListener(this);
    }

    private void setExtra() {

    }

    /**
     * 退出登录
     */
    private void logout() {
        if (mLogoutDialog != null) {
            mLogoutDialog.dismiss();
        }
        DialogUtil.showDialog(lodDialog);
        NetworkWorker.getInstance().post(AppUrls.getInstance().URL_LOGOUT, new NetworkWorker.ICallback() {

            @Override
            public void onResponse(int status, String result) {
                DialogUtil.dismissDialog(lodDialog);
                if(status==200){
                    BaseObject<Object> object= GsonParser.getInstance().parseToObj(result, Object.class);
                    if(object!=null){
                        if(object.data!=null&&object.status==BaseObject.STATUS_OK){
                            AppUtil.showToast(getApplicationContext(), "安全退出");
                            AppStatic.getInstance().isLogin = false;
                            PreferencesUtils.putBoolean("isLogin", false);
                            AppStatic.getInstance().setmUserInfo(null);
                            AppStatic.getInstance().clearUser();
                            HApplication.getInstance().saveToken("");
                            HApplication.getInstance().stopWatchService(AboutActivity.this);
                            QuickLoginActivity.invoke(AboutActivity.this);
                            setResult(RESULT_OK);
                            finish();
                        }else {
                            AppUtil.showToast(getApplicationContext(), object.info);
                        }
                    }else {
                        AppUtil.showToast(getApplicationContext(), "请检查网络");
                    }
                }else {
                    AppUtil.showToast(getApplicationContext(), "请检查网络");
                }
            }
        }, new HttpRequester());

    }

    /**
     * 显示退出登录
     */
    private void showLogoutDialog() {
        if (mLogoutDialog == null) {
            View logoutView = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);
            logoutView.findViewById(R.id.dialog_logout_cancelBtn).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if(!isFinishing())mLogoutDialog.dismiss();
                }
            });
            logoutView.findViewById(R.id.dialog_logout_okBtn).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    logout();
                }
            });
            mLogoutDialog = DialogUtil.getCenterDialog(this, logoutView);
            mLogoutDialog.show();
        } else {
            mLogoutDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        if(v==btnLogout){
            showLogoutDialog();
        }else if(v==tvAbout){
            CommonWebActivity.invoke(this, AppUrls.getInstance().URL_ARTICAL_ABOUT,"关于睡吧");
        }else if(v==tvServiceProtocal){
            CommonWebActivity.invoke(this, AppUrls.getInstance().URL_ARTICAL_SERVICE_PROTOCAL,"服务条款");
        }else if(v==tvLegalNotice){
            CommonWebActivity.invoke(this, AppUrls.getInstance().URL_ARTICAL_LAW,"法律声明");
        }else {
            super.onClick(v);
        }
    }

    public static void invoke(Activity context, int requestCode) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivityForResult(intent,requestCode);
    }
}

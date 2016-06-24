package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

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
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.DialogUtil;

/**
 * 隐私设置
 * Created by paulz on 16/6/4.
 */
public class PrivateSettingActivity extends BaseActivity{
    CheckBox cbFundsRankinglistEnable;
    CheckBox cbPublicFundsEnable;
    CheckBox cbLocationEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setListener();
    }



    private void initView(){
        setActiviyContextView(R.layout.activity_private_setting,false,true);
        setTitleText("","隐私设置",0,true);
        cbFundsRankinglistEnable=(CheckBox) findViewById(R.id.cb_funds_rankinglist_enable);
        cbPublicFundsEnable=(CheckBox) findViewById(R.id.cb_public_funds_enable);
        cbLocationEnable=(CheckBox) findViewById(R.id.cb_location_enable);
        cbFundsRankinglistEnable.setChecked(AppStatic.getInstance().getmUserInfo().is_runking==1);
        cbPublicFundsEnable.setChecked(AppStatic.getInstance().getmUserInfo().is_open_fund==1);
        cbLocationEnable.setChecked(AppStatic.getInstance().getmUserInfo().is_hidden_coord==1);
    }
    private void setListener(){
        cbFundsRankinglistEnable.setOnClickListener(this);
        cbPublicFundsEnable.setOnClickListener(this);
        cbLocationEnable.setOnClickListener(this);
    }

    /**
     *
     * @param setting_key  1是否参与睡眠基金排行 2是否公开睡眠基金 3是否隐藏我的地理位置
     * @param enable
     */
    public void modifySettings(final int setting_key, final boolean enable){
        if(!AppStatic.getInstance().isLogin)return;
        DialogUtil.showDialog(lodDialog);
        ParamBuilder params=new ParamBuilder();
        params.append("setting_key", setting_key);
        params.append("setting_value", enable?1:0);
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_PRIVATE_SETTING), new NetworkWorker.ICallback() {

            @Override
            public void onResponse(int status, String result) {
                // TODO Auto-generated method stub
                if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
                if(status==200){
                    LogUtil.d("jpush---bind push info="+result);
                    BaseObject<Object> obj=GsonParser.getInstance().parseToObj(result,Object.class);
                    if(obj!=null){
                        if(obj.status==BaseObject.STATUS_OK){
                            switch (setting_key){
                                case 1:
                                    AppStatic.getInstance().getmUserInfo().is_runking=enable?1:0;
                                    PreferencesUtils.putInteger("is_runking", enable?1:0);
                                    break;
                                case 2:
                                    AppStatic.getInstance().getmUserInfo().is_open_fund=enable?1:0;
                                    PreferencesUtils.putInteger("is_open_fund", enable?1:0);
                                    break;
                                case 3:
                                    AppStatic.getInstance().getmUserInfo().is_hidden_coord=enable?1:0;
                                    PreferencesUtils.putInteger("is_hidden_coord", enable?1:0);
                                    break;
                            }

                        }else {
                            AppUtil.showToast(getApplicationContext(),obj.info);
                            setCheckBoxStatus(setting_key,!enable);
                        }
                    }else {
                        AppUtil.showToast(getApplicationContext(),"设置失败");
                        setCheckBoxStatus(setting_key,!enable);
                    }
                }else {
                    AppUtil.showToast(getApplicationContext(),"设置失败");
                    setCheckBoxStatus(setting_key,!enable);
                }
            }
        });
    }

    /**
     *
     * @param who  1是否参与睡眠基金排行 2是否公开睡眠基金 3是否隐藏我的地理位置
     * @param checked
     */
    private void setCheckBoxStatus(int who,boolean checked){
        switch (who){
            case 1:
                cbFundsRankinglistEnable.setChecked(checked);
                break;
            case 2:
                cbPublicFundsEnable.setChecked(checked);
                break;
            case 3:
                cbLocationEnable.setChecked(checked);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        //onclick发生在checkbox的oncheckedchange事件之后
        if(v==cbFundsRankinglistEnable){
            modifySettings(1,cbFundsRankinglistEnable.isChecked());
        }else if(v==cbPublicFundsEnable){
            modifySettings(2,cbPublicFundsEnable.isChecked());
        }else if(v==cbLocationEnable){
            modifySettings(3,cbLocationEnable.isChecked());
        }else {
            super.onClick(v);
        }
    }

    public static void invoke(Context context){
        Intent intent =new Intent(context,PrivateSettingActivity.class);
        context.startActivity(intent);
    }
}

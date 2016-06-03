package com.bolaa.sleepingbar.ui.fragment;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseFragment;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.ui.AboutActivity;
import com.bolaa.sleepingbar.ui.AccountActivity;
import com.bolaa.sleepingbar.ui.MyMedalActivity;
import com.bolaa.sleepingbar.utils.Image13Loader;

import org.w3c.dom.Text;

/**
 * 首页--用户中心
 * Created by paulz on 2016/5/31.
 */
public class UserCenterFragment extends BaseFragment implements View.OnClickListener {

    private ImageView ivAvatar;
    private TextView tvName;
    private TextView tvBindWatch;
    private TextView tvMsgCount;

    private View layoutMedal;
    private View layoutFriends;
    private View layoutMsg;
    private View layoutAccount;
    private View layoutDeviceInfo;
    private View layoutSettings;
    private View layoutAbout;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void heavyBuz() {

    }


    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setView(inflater, R.layout.fragment_user_center, false);
        initView();
        setListener();
        return baseLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
         initData();
    }

    private void initData() {
        if(AppStatic.getInstance().getmUserInfo()!=null){
            Image13Loader.getInstance().loadImage(AppStatic.getInstance().getmUserInfo().avatar,ivAvatar,R.drawable.img_avatar_default);
            tvName.setText(AppStatic.getInstance().getmUserInfo().nick_name);
        }
    }


    public void initView() {
        ivAvatar=(ImageView)baseLayout.findViewById(R.id.iv_avatar);
        tvName=(TextView)baseLayout.findViewById(R.id.tv_name);
        tvMsgCount=(TextView)baseLayout.findViewById(R.id.tv_msg_count);
        tvBindWatch=(TextView)baseLayout.findViewById(R.id.tv_bind_watch);
        layoutAbout=baseLayout.findViewById(R.id.layout_about);
        layoutMedal=baseLayout.findViewById(R.id.layout_medal);
        layoutFriends=baseLayout.findViewById(R.id.layout_my_friends);
        layoutMsg=baseLayout.findViewById(R.id.layout_msg);
        layoutAccount=baseLayout.findViewById(R.id.layout_my_account);
        layoutDeviceInfo=baseLayout.findViewById(R.id.layout_device_info);
        layoutSettings=baseLayout.findViewById(R.id.layout_private_settings);

    }

    private void setListener() {
        layoutAbout.setOnClickListener(this);
        layoutMedal.setOnClickListener(this);
        layoutFriends.setOnClickListener(this);
        layoutMsg.setOnClickListener(this);
        layoutAccount.setOnClickListener(this);
        layoutDeviceInfo.setOnClickListener(this);
        layoutSettings.setOnClickListener(this);
        tvBindWatch.setOnClickListener(this);
        tvName.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        if(v==layoutAbout){
            AboutActivity.invoke(getActivity(),101);
        }else if(v==layoutMedal){
            MyMedalActivity.invoke(getActivity());
        }else if(v==layoutFriends){

        }else if(v==layoutMsg){

        }else if(v==layoutAccount){
            AccountActivity.invoke(getActivity());
        }else if(v==layoutDeviceInfo){

        }else if(v==layoutSettings){

        }else if(v==tvBindWatch){

        }else if(v==tvName){

        }else if(v==ivAvatar){

        }

    }
}

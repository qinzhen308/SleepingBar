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
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.UserInfo;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.ui.AboutActivity;
import com.bolaa.sleepingbar.ui.AccountActivity;
import com.bolaa.sleepingbar.ui.DeviceInfoActivity;
import com.bolaa.sleepingbar.ui.MyFriendsActivity;
import com.bolaa.sleepingbar.ui.MyInfoActivity;
import com.bolaa.sleepingbar.ui.MyMedalActivity;
import com.bolaa.sleepingbar.ui.MyMsgActivity;
import com.bolaa.sleepingbar.ui.PrivateSettingActivity;
import com.bolaa.sleepingbar.ui.QuickBindWatchActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;
import com.bolaa.sleepingbar.watch.WatchService;
import com.core.framework.net.NetworkWorker;
import com.core.framework.store.sharePer.PreferencesUtils;

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
        getMsgCount();
        if(AppUtil.isNull(PreferencesUtils.getString(WatchService.FLAG_CURRENT_DEVICE_ADDRESS))){
            tvBindWatch.setVisibility(View.GONE);
        }else {
            tvBindWatch.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void heavyBuz() {
        getMsgCount();
    }

    private boolean isLoadMsgCount=false;//防止加载过程中再次加载
    public void getMsgCount() {
        if(isLoadMsgCount)return;
        isLoadMsgCount=true;
        ParamBuilder params=new ParamBuilder();
        NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_GET_MSG_COUNT), new NetworkWorker.ICallback() {

            @Override
            public void onResponse(int status, String result) {
                isLoadMsgCount=false;
                if(status==200){
                    BaseObject<Msgcount> object= GsonParser.getInstance().parseToObj(result, Msgcount.class);
                    if(object!=null){
                        if(object.data!=null&&object.status==BaseObject.STATUS_OK){
                            showMsgCount(object.data.message_count);
                        }else {
                            showMsgCount(0);
                        }
                    }else {
                        showMsgCount(0);
                    }
                }

            }
        });
    }

    public class Msgcount{
        public int message_count;
    }

    private void showMsgCount(int count){
        if(count<=0){
            tvMsgCount.setVisibility(View.GONE);
        }else if(count>99){
            tvMsgCount.setVisibility(View.VISIBLE);
            tvMsgCount.setText("99+");
        }else {
            tvMsgCount.setVisibility(View.VISIBLE);
            tvMsgCount.setText(""+count);
        }

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
            MyFriendsActivity.invoke(getActivity());
        }else if(v==layoutMsg){
            MyMsgActivity.invoke(getActivity());
        }else if(v==layoutAccount){
            AccountActivity.invoke(getActivity());
        }else if(v==layoutDeviceInfo){
            DeviceInfoActivity.invoke(getActivity());
        }else if(v==layoutSettings){
            PrivateSettingActivity.invoke(getActivity());
        }else if(v==tvBindWatch){
            QuickBindWatchActivity.invoke(getActivity());
        }else if(v==tvName){
            MyInfoActivity.invoke(getActivity());
        }else if(v==ivAvatar){
            MyInfoActivity.invoke(getActivity());
        }

    }
}

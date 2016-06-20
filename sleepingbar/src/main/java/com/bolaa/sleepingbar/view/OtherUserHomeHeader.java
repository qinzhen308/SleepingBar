package com.bolaa.sleepingbar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.SmallMedalAdapter;
import com.bolaa.sleepingbar.adapter.TextInformationAdapter;
import com.bolaa.sleepingbar.model.OtherUserInfo;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;
import com.core.framework.app.devInfo.ScreenUtil;

/**
 * 社区首页--头部：图片和文字咨询
 * Created by paulz on 2016/6/14.
 */
public class OtherUserHomeHeader extends LinearLayout{
    private Context context;
    private SmallMedalAdapter medalAdapter;
    private GridView gvMedal;

    private ImageView ivAvatar;
    private TextView tvName;
    private TextView tvFunds;
    private TextView tvStep;
    private TextView tvFriends;
    private TextView tvCare;
    private TextView tvMedalCount;

    OtherUserInfo data;

    public OtherUserHomeHeader(Context context) {
        super(context);
        init(context);
    }

    public OtherUserHomeHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OtherUserHomeHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.layout_other_user_header,this);
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setBackgroundColor(getResources().getColor(R.color.white));
        gvMedal=(GridView)findViewById(R.id.gv_medal);
        tvName=(TextView)findViewById(R.id.tv_name);
        tvCare=(TextView)findViewById(R.id.tv_care);
        tvFriends=(TextView)findViewById(R.id.tv_friends);
        tvFunds=(TextView)findViewById(R.id.tv_funds);
        tvStep=(TextView)findViewById(R.id.tv_step);
        tvMedalCount=(TextView)findViewById(R.id.tv_medal_count);
        ivAvatar=(ImageView) findViewById(R.id.iv_avatar);
    }

    public void showHeader(OtherUserInfo userInfo){
        data=userInfo;
        showUserInfo();
        showHotInformation();
    }

    private void showUserInfo(){
        Image13Loader.getInstance().loadImage(data.avatar,ivAvatar,R.drawable.user2);
        tvName.setText(data.nick_name);
        tvCare.setText(data.to_care_num);
        tvFriends.setText(data.care_num);
        tvFunds.setText(data.sleep_fund);
        tvMedalCount.setText(data.medal_count);
        tvStep.setText(data.walk_total);
    }

    //热门文字资讯
    private void showHotInformation(){
        if(AppUtil.isEmpty(data.user_medal)){
            gvMedal.setVisibility(View.GONE);
        }else {
            gvMedal.setVisibility(View.VISIBLE);
            if(medalAdapter ==null){
                medalAdapter =new SmallMedalAdapter(context);
                medalAdapter.setWidth(ScreenUtil.WIDTH-ScreenUtil.dip2px(context,20),gvMedal.getNumColumns());
                medalAdapter.setList(data.user_medal);
                gvMedal.setAdapter(medalAdapter);
            }else {
                medalAdapter.setList(data.user_medal);
                medalAdapter.notifyDataSetChanged();
            }
        }
    }


}

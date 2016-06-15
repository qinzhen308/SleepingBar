package com.bolaa.sleepingbar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.TextInformationAdapter;
import com.bolaa.sleepingbar.ui.fragment.CommunityFragment;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.view.banner.AutoScollBanner;
import com.bolaa.sleepingbar.view.pulltorefreshgrid.PullToRefreshBase;

/**
 * 社区首页--头部：图片和文字咨询
 * Created by paulz on 2016/6/14.
 */
public class CommunityHeader extends LinearLayout{
    private Context context;
    private ListView lvInformation;
    private AutoScollBanner mBanner;
    private TextInformationAdapter textInformationAdapter;

    CommunityFragment.CommunityInfo data;

    public CommunityHeader(Context context) {
        super(context);
        init(context);
    }

    public CommunityHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommunityHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.context=context;
        LayoutInflater.from(context).inflate(R.layout.layout_community_header,this);
//        setOrientation(LinearLayout.VERTICAL);
        mBanner=(AutoScollBanner)findViewById(R.id.layout_auto_banner);
        mBanner.setScale(0.3738f);
        lvInformation=(ListView) findViewById(R.id.lv_information_text);
    }

    public void showHeader(CommunityFragment.CommunityInfo communityInfo){
        data=communityInfo;
        showBanner();
        showHotInformation();
    }

    //图片资讯
    private void showBanner(){
        if(AppUtil.isEmpty(data.img_info)){
           mBanner.setVisibility(View.GONE);
        }else {
            mBanner.setVisibility(View.VISIBLE);
            mBanner.showInformationViews(data.img_info);
        }
    }

    //热门文字资讯
    private void showHotInformation(){
        if(AppUtil.isEmpty(data.text_info)){
            lvInformation.setVisibility(View.GONE);
        }else {
            lvInformation.setVisibility(View.VISIBLE);
            if(textInformationAdapter==null){
                textInformationAdapter=new TextInformationAdapter(context);
                textInformationAdapter.setList(data.text_info);
                lvInformation.setAdapter(textInformationAdapter);
            }else {
                textInformationAdapter.setList(data.text_info);
                textInformationAdapter.notifyDataSetChanged();
            }
        }
    }


}

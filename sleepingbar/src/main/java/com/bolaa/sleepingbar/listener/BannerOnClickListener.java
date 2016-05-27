package com.bolaa.sleepingbar.listener;

import android.app.Activity;
import android.view.View;

import com.bolaa.sleepingbar.model.Banner;

/**
 * Created with IntelliJ IDEA.
 * User: kait
 * Date: 5/3/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BannerOnClickListener implements View.OnClickListener {
    // 打点关键词
    private String mAnalsKey;
    private int mSourceFrom;

    // banner位置
    private int index = -1;

    private Activity mContext;
    private Banner mBanner;

    public BannerOnClickListener(Activity activity, Banner banner) {
        this.mBanner = banner;
        this.mContext = activity;
    }

    public BannerOnClickListener(Activity activity, Banner banner, String analsKey, int mSourceFrom) {
        this(activity, banner);
        this.mAnalsKey = analsKey;
        this.mSourceFrom = mSourceFrom;
    }


    public BannerOnClickListener(Banner banner, String analsKey, int mSourceFrom) {
        this.mBanner = banner;
        this.mAnalsKey = analsKey;
        this.mSourceFrom = mSourceFrom;
    }


    @Override
    public void onClick(View v) {
    	if(mBanner==null)return;
    	

    }
}

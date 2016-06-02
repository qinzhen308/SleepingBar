package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.util.ArrayList;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.view.banner.LoopCirclePageIndicator;
import com.core.framework.app.base.BaseActivity;
import com.core.framework.store.sharePer.PreferencesUtils;

public class GuideActivity extends BaseActivity {
	ViewPager mPager;
	LoopCirclePageIndicator indicator;
	ImageView ivSkip;
	int imgs[] = { R.drawable.guide1,R.drawable.guide2,R.drawable.guide3 };
	
	ArrayList<ImageView> pages=new ArrayList<ImageView>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		initView();
		setListener();

	}
	

	private void initView() {
		for(int i =0;i<imgs.length;i++){
			ImageView view = new ImageView(GuideActivity.this);
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
			view.setScaleType(ScaleType.CENTER_CROP);
			pages.add(view);
		}
		
		mPager = (ViewPager) findViewById(R.id.vp_guide);
		indicator = (LoopCirclePageIndicator) findViewById(R.id.idc_ad_indicator);
		indicator.setRealCount(imgs.length);
		mPager.setAdapter(new GuideAdapter());
		indicator.setViewPager(mPager);
		ivSkip=(ImageView)findViewById(R.id.iv_skip);
		if(imgs.length==1){
			ivSkip.setVisibility(View.VISIBLE);
		}
	}

	private void setListener() {
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == imgs.length-1) {
					ivSkip.setVisibility(View.VISIBLE);
				}else {
					ivSkip.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		ivSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				jump();
			}
		});

	}

	private void jump() {
		if(AppStatic.getInstance().isLogin && !AppUtil.isNull(HApplication.getInstance().token)){//已经登录了
			MainActivity.invoke(this);
		}else {
			QuickLoginActivity.invoke(this);
		}
		PreferencesUtils.putInteger("current_app_vison", HApplication.getInstance().getVersionCode());
		finish();
	}

	public static void invoke(Context context) {
		Intent intent = new Intent(context, GuideActivity.class);
		context.startActivity(intent);
	}

	class GuideAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imgs.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			ImageView view=pages.get(position);
			container.addView(view);
			if (imgs.length > position) {
//				view.setBackgroundColor(getResources().getColor(R.color.white));
				view.setImageResource(imgs[position]);
			}
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView(pages.get(position));
		}

	}

}

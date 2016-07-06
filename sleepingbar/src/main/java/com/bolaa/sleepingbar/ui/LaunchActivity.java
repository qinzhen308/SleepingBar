package com.bolaa.sleepingbar.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.utils.AppUtil;

public class LaunchActivity extends Activity{
	TextView tvVersion;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		tvVersion=(TextView)findViewById(R.id.tv_version);
		tvVersion.setText("睡吧  for android v"+HApplication.getInstance().getVersionName());
		HApplication.getInstance().autoConnectedWatch();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(HApplication.getInstance().isNewVison()){
					GuideActivity.invoke(LaunchActivity.this);
				}else {
					if(AppStatic.getInstance().isLogin && !AppUtil.isNull(HApplication.getInstance().token)){//已经登录了
						MainActivity.invoke(LaunchActivity.this);
					}else {
						QuickLoginActivity.invoke(LaunchActivity.this);
					}
				}
				finish();
			}
		},2000);
	}
	
	

}

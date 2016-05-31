package com.bolaa.sleepingbar.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.watch.WatchService;

public class LaunchActivity extends Activity{
	TextView tvVersion;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		tvVersion=(TextView)findViewById(R.id.tv_version);
		tvVersion.setText("睡吧  for android v"+HApplication.getInstance().getVersionName());
		Intent intent = new Intent(this, WatchService.class);
		intent.putExtra(WatchService.FLAG_CURRENT_DEVICE_ADDRESS,"4A:59:00:00:AA:0A");
		startService(intent);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(HApplication.getInstance().isNewVison()){
//					GuideActivity.invoke(LaunchActivity.this);
					MainActivity.invoke(LaunchActivity.this);
				}else {
					MainActivity.invoke(LaunchActivity.this);
				}
				finish();
			}
		},2000);
	}
	
	

}

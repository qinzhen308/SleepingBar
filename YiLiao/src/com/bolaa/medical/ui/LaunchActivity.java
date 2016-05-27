package com.bolaa.medical.ui;

import com.bolaa.medical.HApplication;
import com.bolaa.medical.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class LaunchActivity extends Activity{
	TextView tvVersion;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		tvVersion=(TextView)findViewById(R.id.tv_version);
		tvVersion.setText("维极体检  for android v"+HApplication.getInstance().getVersionName());
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(HApplication.getInstance().isNewVison()){
//					GuideActivity.invoke(LaunchActivity.this);
				}else {
					MainActivity.invoke(LaunchActivity.this);
				}
				finish();
			}
		},2000);
	}
	
	

}

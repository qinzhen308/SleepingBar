package com.bolaa.medical.ui;

import com.bolaa.medical.R;
import com.bolaa.medical.common.GlobeFlags;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class CommonWebActivity extends BaseWebActivity{
	
	String pic_url;
	String wap_url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
	}
	private void initView(){
		setActiviyContextView(R.layout.activity_fishion_detail_web, true, true);
		setTitleText("", "体检报告", 0, true);
		mWebView=(WebView)findViewById(R.id.web_fishion);
		load(wap_url, false);
	}
	
	private void setExtra(){
		Intent intent=getIntent();
		wap_url=intent.getStringExtra(GlobeFlags.FLAG_FISHION_WAP_URL);
		pic_url=intent.getStringExtra(GlobeFlags.FLAG_FISHION_PIC_URL);
	}
	public static void invoke(Context context,String url){
		Intent intent = new Intent(context,CommonWebActivity.class);
		intent.putExtra(GlobeFlags.FLAG_FISHION_WAP_URL, url);
		context.startActivity(intent);
		
	}
}

package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.common.GlobeFlags;

public class CommonWebActivity extends BaseWebActivity{
	
	String pic_url;
	String wap_url;
	private String title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
	}
	private void initView(){
		if(title==null){
			setActiviyContextView(R.layout.activity_fishion_detail_web, true, false);
		}else {
			setActiviyContextView(R.layout.activity_fishion_detail_web, true, true);
			setTitleText("", title, 0, true);
		}
		mWebView=(WebView)findViewById(R.id.web_fishion);
		mWebView.addJavascriptInterface(this,"active");
        mWebView.getSettings().setJavaScriptEnabled(true);
		load(wap_url, false);
	}
	
	private void setExtra(){
		Intent intent=getIntent();
		wap_url=intent.getStringExtra(GlobeFlags.FLAG_FISHION_WAP_URL);
		pic_url=intent.getStringExtra(GlobeFlags.FLAG_FISHION_PIC_URL);
		title=intent.getStringExtra("title");
	}

	/**
	 *
	 * @param context
	 * @param url
	 * @param title  null，不显示titlebar
     */
	public static void invoke(Context context,String url,String title){
		Intent intent = new Intent(context,CommonWebActivity.class);
		intent.putExtra(GlobeFlags.FLAG_FISHION_WAP_URL, url);
		intent.putExtra("title", title);
		context.startActivity(intent);
	}

	public void finishNativePage(){
		mWebView.stopLoading();
		finish();
	}

	public void backWebPage(){
		mWebView.goBack();
	}

	public void nextWebPage(){
		mWebView.goForward();
	}
}

package com.bolaa.sleepingbar.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.common.GlobeFlags;
import com.bolaa.sleepingbar.listener.JSInvokeJavaInterface;

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
	@SuppressLint("JavascriptInterface")
	private void initView(){
		if(title==null){
			setActiviyContextView(R.layout.activity_fishion_detail_web, true, false);
		}else {
			setActiviyContextView(R.layout.activity_fishion_detail_web, true, true);
			setTitleText("", title, 0, true);
		}
		mWebView=(WebView)findViewById(R.id.web_fishion);
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		if(width > 650)
		{
			this.mWebView.setInitialScale(190);
		}else if(width > 520)
		{
			this.mWebView.setInitialScale(160);
		}else if(width > 450)
		{
			this.mWebView.setInitialScale(140);
		}else if(width > 300)
		{
			this.mWebView.setInitialScale(120);
		}else
		{
			this.mWebView.setInitialScale(100);
		}
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setSupportZoom(false);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setBlockNetworkImage(false);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
		mWebView.addJavascriptInterface(new JSInvokeJavaInterface(this,mWebView),"active");
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



}

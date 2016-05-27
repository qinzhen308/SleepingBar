package com.bolaa.medical.ui;

import com.bolaa.medical.R;
import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.model.Article;
import com.bolaa.medical.parser.gson.BaseObject;
import com.bolaa.medical.parser.gson.GsonParser;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class SettingDetailActivity extends BaseActivity{
	WebView mWebView;
	private String id;
	private String title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
		initData();
	}

	private void setExtra() {
		// TODO Auto-generated method stub
		Intent intent=getIntent();
		title=intent.getStringExtra("title");
		id=intent.getStringExtra("id");
	}

	private void initView() {
		// TODO Auto-generated method stub
		setActiviyContextView(R.layout.activtiy_about, true, true);
		setTitleText("", title, 0, true);
		initWebView();
	}
	
	private void initWebView() {
		mWebView=(WebView)findViewById(R.id.tv_content);
		WebSettings wSet = mWebView.getSettings();
		wSet.setDefaultTextEncodingName("UTF-8");
		wSet.setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				mWebView.setVisibility(View.VISIBLE);
			}
		});
	}
	

	private void setListener() {
		// TODO Auto-generated method stub
	}
	
	private void initData(){
		showLoading();
		ParamBuilder params=new ParamBuilder();
		params.append("article_id", id);
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SETTING_DETAIL), new ICallback() {
			
			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if(status!=200){
					showFailture();
					return;
				}
				BaseObject<Article> baseObject=GsonParser.getInstance().parseToObj(result, Article.class);
				if(baseObject!=null){
					if(baseObject.status==BaseObject.STATUS_OK&&baseObject.data!=null){
						showSuccess();
						mWebView.loadData(baseObject.data.content==null?"":baseObject.data.content, "text/html;charset=UTF-8", null);
//						tvContent.setText(baseObject.data.content==null?"":baseObject.data.content);
					}else {
						showNodata();
					}
				}else {
					showFailture();
				}
			}
		});
	}
	
	private void submit(){
		
	}
	
	public static void invoke(Context context,String id,String title){
		Intent intent=new Intent(context,SettingDetailActivity.class);
		intent.putExtra("id", id);
		intent.putExtra("title", title);
		context.startActivity(intent);
	}

}

package com.bolaa.sleepingbar.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bolaa.sleepingbar.base.BaseActivity;
import com.core.framework.develop.LogUtil;

/**
 * Created with IntelliJ IDEA.
 * User: mark
 * Date: 14-8-8
 * Time: 上午10:16
 * To change this template use File | Settings | File Templates.
 */
//zhe800
public abstract class BaseWebActivity extends BaseActivity {
    protected WebView mWebView;
//    protected ProgressBar mPBar;

    protected String mTitle;
    protected String mCurrentUrl;

    private boolean isFirstLoad = true;

    private boolean isTaoBaoOrderFirst = true;  // 第一次进入淘宝订单页面

    protected void isNeedCloseThisActivity(boolean isNeed) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    protected void load(String url, boolean isReLoad) {
        if (mWebView == null) throw new IllegalArgumentException("mWebView must not be empty");
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        if(isReLoad){
        	mWebView.reload();
        }else {
        	mWebView.loadUrl(url);
		}
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            LogUtil.d("------------------error-----------------");
        }

        private void notifyOtherSchema(String url) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                LogUtil.w(e);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
        	showSuccess();
            mCurrentUrl = url;

            LogUtil.d("---------------finishUrl-----------------" + url);

            super.onPageFinished(view, url);
        }
        
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        	// TODO Auto-generated method stub
        	showLoading();
        	super.onPageStarted(view, url, favicon);
        }

    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress > 50) {
                isFirstLoad = true;
                showSuccess();
            } else if (isFirstLoad) {
                isFirstLoad = false;
                showLoading();
            }
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            if (consoleMessage == null || consoleMessage.message() == null) {
                super.onConsoleMessage(consoleMessage);
            }

            if (consoleMessage.message().contains("Uncaught")) {
                if (consoleMessage.message().contains("goback")) {
                    LogUtil.w("----H5BackClick------" + consoleMessage.message() + "----------");
                    isNeedCloseThisActivity(true);
                } else {
                    isNeedCloseThisActivity(true);
                }
            }
            return super.onConsoleMessage(consoleMessage);
        }
    }

    @Override
    protected void onDestroy() {
        mWebView.cancelLongPress();
        mWebView.clearHistory();
        super.onDestroy();
    }
}





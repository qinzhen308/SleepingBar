package com.bolaa.sleepingbar.listener;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.ui.CommonWebActivity;
import com.bolaa.sleepingbar.utils.AppUtil;

/**
 * Created by paulz on 2016/7/1.
 */
public class JSInvokeJavaInterface {

    private Activity context;
    private WebView mWebView;

    public JSInvokeJavaInterface(Activity context,WebView view){
        this.context=context;
        mWebView=view;
    }


    @JavascriptInterface
    public void openDetail(String url){
        CommonWebActivity.invoke(context,url,null);
    }

    @JavascriptInterface
    public void backWebPage(){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.goBack();

            }
        });

    }

    @JavascriptInterface
    public void nextWebPage(){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.goForward();
            }
        });

    }

    @JavascriptInterface
    public void finishNativePage(){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.stopLoading();
                context.finish();
            }
        });

    }


}

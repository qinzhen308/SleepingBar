package com.bolaa.sleepingbar.ui.fragment;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseFragment;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.ui.CommonWebActivity;
import com.core.framework.develop.LogUtil;

/**
 * 首页--活动页
 * web页面
 * Created by paulz on 2016/5/31.
 */
public class ActiveFragment extends BaseFragment implements View.OnClickListener {
    WebView mWebView;

    protected String mCurrentUrl;

    private boolean isFirstLoad = true;

    private boolean isTaoBaoOrderFirst = true;  // 第一次进入淘宝订单页面

    protected void isNeedCloseThisActivity(boolean isNeed) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mWebView.cancelLongPress();
        mWebView.clearHistory();
        super.onDestroy();
    }

    @Override
    public void heavyBuz() {

    }


    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setView(inflater, R.layout.fragment_active_web, false);
        initView();
        setListener();
        return baseLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        // initData(false);
        load(APIUtil.parseGetUrlHasMethod(new ParamBuilder().getParamList(),AppUrls.getInstance().URL_ACTIVE_HOME),false);
    }

    @SuppressLint("JavascriptInterface")
    public void initView() {
        mWebView=(WebView) baseLayout.findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this,"active");
    }

    private void setListener() {


    }
    @Override
    public void onClick(View v) {

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

    public void openDetail(String url){
        CommonWebActivity.invoke(getActivity(),url,null);
    }


}

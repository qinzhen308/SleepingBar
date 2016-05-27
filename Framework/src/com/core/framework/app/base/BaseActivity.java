package com.core.framework.app.base;

import android.app.Activity;
import android.os.Bundle;


/**
 * Created by IntelliJ IDEA.
 * User: Mark
 * Date: 11-5-21
 * Time: 下午4:32
 * Base activity with analytics
 */
public abstract class BaseActivity extends Activity {

    private boolean enableAutoAnalysis = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setEnableAutoAnalysis(boolean autoAnalysis) {
        enableAutoAnalysis = autoAnalysis;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}

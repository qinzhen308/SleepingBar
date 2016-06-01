package com.bolaa.sleepingbar.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.model.UserInfo;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.ImageUtil;
import com.bolaa.sleepingbar.view.CircleImageView;
import com.bolaa.sleepingbar.view.wheel.NumericWheelAdapter;
import com.bolaa.sleepingbar.view.wheel.OnWheelScrollListener;
import com.bolaa.sleepingbar.view.wheel.WheelView;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.IOSDialogUtil;
import com.core.framework.util.IOSDialogUtil.OnSheetItemClickListener;
import com.core.framework.util.IOSDialogUtil.SheetItemColor;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * 基本信息
 * 
 * @author paulz
 * 
 */
public class SleepTrendActivity extends BaseActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActiviyContextView(R.layout.activity_myinfo);
		setTitleText("", "睡眠趋势", 0, true);
		initView();
		setListener();
		initData();

	}

	private void initView() {

	}
	
	private void setListener() {
		// TODO Auto-generated method stub

	}


	private void initData() {

	}



	public static void invoke(Context context){
		Intent intent =new Intent(context,SleepTrendActivity.class);
		context.startActivity(intent);
	}

}

package com.bolaa.medical.ui;

import com.bolaa.medical.R;
import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.parser.gson.BaseObject;
import com.bolaa.medical.parser.gson.GsonParser;
import com.bolaa.medical.utils.AppUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.util.DialogUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 兑换积分
 * @author pualz
 *
 */
public class ScoreChangeActivity extends BaseActivity{
	TextView tvCash;
	TextView btnSubmit;
	
	private float exchagne_max;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
		initData(false);
	}
	
	private void setExtra(){
		Intent intent=getIntent();
		exchagne_max=intent.getFloatExtra("exchange_max", 0);
	}
	
	private void initView(){
		setActiviyContextView(R.layout.activity_score_change, true, true);
		setTitleText("", "积分兑换", 0, true);
		tvCash=(TextView)findViewById(R.id.tv_cash);
		btnSubmit=(TextView)findViewById(R.id.btn_submit);
//		tvCash.setText(""+(float)(Math.round(exchagne_max*100)/100));
		tvCash.setText(AppUtil.getTwoDecimal(exchagne_max));
	}
	
	private void setListener(){
		btnSubmit.setOnClickListener(this);
	}
	
	private void initData(boolean isRefresh){
		
	}
	
	private void change(){
		if(Float.compare(exchagne_max, 0)<=0){
			AppUtil.showToast(this, "积分不足，无法兑换");
			return;
		}
		DialogUtil.showDialog(lodDialog);
		ParamBuilder params=new ParamBuilder();
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SCORE_CHANGE_MONEY), new ICallback() {
			
			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
				if(status==200){
					BaseObject<ChangeResult> object=GsonParser.getInstance().parseToObj(result, ChangeResult.class);
					if(object!=null){
						if(object.data!=null&&object.status==BaseObject.STATUS_OK){
							AppUtil.showToast(getApplicationContext(), "兑换成功");
							//可以发个广播
							setResult(RESULT_OK);
							finish();
						}else {
							AppUtil.showToast(getApplicationContext(), object.msg);
						}
					}else {
						AppUtil.showToast(getApplicationContext(), "兑换失败");
					}
				}else {
					AppUtil.showToast(getApplicationContext(), "兑换失败");
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==btnSubmit){
			change();
		}else {
			super.onClick(v);
		}
	}
	
	public static void invoke(Activity context,float exchagne_max){
		Intent intent=new Intent(context,ScoreChangeActivity.class);
		intent.putExtra("exchange_max", exchagne_max);
		context.startActivityForResult(intent,11);
	}
	
	private class ChangeResult{
		public int point;
		public float will_getmoney;
	}

}

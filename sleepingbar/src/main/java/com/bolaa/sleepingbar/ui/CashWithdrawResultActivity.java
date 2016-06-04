package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;

/**
 * 支付结果
 * 
 * @author paulz
 * 
 */
public class CashWithdrawResultActivity extends BaseActivity {
	private TextView tvStatus;
	private TextView tvStatusTip;
	private TextView btnBack;
	private ImageView ivStatus;
	private int state;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActiviyContextView(R.layout.activity_cash_withdraw_result,false,false);
		setExtra();
		initView();
		setListener();
	}

	private void setExtra(){
//		String pay_sn = getIntent().getStringExtra("pay_sn");
		state = getIntent().getIntExtra("state", 0);
//		String detail = getIntent().getStringExtra("detail");
	}

	@Override
	public void onClick(View v) {
		if(v==btnBack){
			finish();
		}

	}

	private void initView() {
		ivStatus = (ImageView) findViewById(R.id.iv_status);
		tvStatus = (TextView) findViewById(R.id.tv_status);
		tvStatusTip = (TextView) findViewById(R.id.tv_status_tip);
		btnBack = (TextView) findViewById(R.id.tv_go_to_appointment);
		if(state==1){

		}else {
			ivStatus.setImageResource(R.drawable.ic_pay_failed);
			tvStatus.setText("支付失败");
			tvStatusTip.setText("——请重新支付！——");
		}
	}

	private void setListener(){
		btnBack.setOnClickListener(this);
	}

	public static void invoke(Context context,int status){
		Intent intent=new Intent(context,CashWithdrawResultActivity.class);
		intent.putExtra("state",status);
		context.startActivity(intent);
	}
}

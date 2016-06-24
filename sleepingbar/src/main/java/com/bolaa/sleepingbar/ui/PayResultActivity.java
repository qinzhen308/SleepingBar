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
public class PayResultActivity extends BaseActivity {
	private TextView tvStatus;
	private TextView tvStatusTip;
	private TextView btnBack;
	private TextView tvGotoAppointment;
	private ImageView ivStatus;
	private int state;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActiviyContextView(R.layout.activity_payresult,false,false);
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
		}else if(v==tvGotoAppointment){
			MainActivity.invoke(this);
//			AlreadyReservationActivity.invoke(this,"我的预约");
			finish();
		}

	}

	private void initView() {
		ivStatus = (ImageView) findViewById(R.id.iv_status);
		tvStatus = (TextView) findViewById(R.id.tv_status);
		tvStatusTip = (TextView) findViewById(R.id.tv_status_tip);
		tvGotoAppointment = (TextView) findViewById(R.id.tv_go_to_appointment);
		if(state==1){

		}else {
			ivStatus.setImageResource(R.drawable.ic_pay_failed);
			tvStatus.setText("支付失败");
			tvStatusTip.setText("——请重新支付！——");
			tvGotoAppointment.setVisibility(View.GONE);
		}
	}

	private void setListener(){
		tvGotoAppointment.setOnClickListener(this);
	}

	public static void invoke(Context context,int status){
		Intent intent=new Intent(context,PayResultActivity.class);
		intent.putExtra("state",status);
		context.startActivity(intent);
	}
}

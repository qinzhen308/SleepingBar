package com.bolaa.medical.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.common.AppStatic;
import com.bolaa.medical.utils.AppUtil;
import com.bolaa.medical.R;

/**
 * 支付密码
 * 
 * @author jjj
 * 
 * @time 2015-12-18
 */
public class PayPW1Activity extends BaseActivity {

	private TextView mTimeTv;
	private EditText mPhoneEdt;
	private EditText mCodeEdt;
	private Button mNextBtn;

	private int time = 120;
	private TimeCount timeCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActiviyContextView(R.layout.activity_paypw1);
		setTitleText("", "支付密码", 0, true);

		timeCount = new TimeCount(120000, 1000);
		mTimeTv = (TextView) findViewById(R.id.payPW1_timeTv);
		mPhoneEdt = (EditText) findViewById(R.id.payPW1_phoneEdt);
		mCodeEdt = (EditText) findViewById(R.id.payPW1_yzCodeEdt);
		mNextBtn = (Button) findViewById(R.id.payPW1_nextBtn);
		mTimeTv.setOnClickListener(this);
		mNextBtn.setOnClickListener(this);
		mCodeEdt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(s)) {
					mNextBtn.setEnabled(false);
				} else {
					mNextBtn.setEnabled(true);
				}

			}
		});

	}

	@Override
	public void onClick(View arg0) {
		super.onClick(arg0);

		switch (arg0.getId()) {
		case R.id.payPW1_nextBtn:
			String code = mCodeEdt.getText().toString();
			if (code.equals(AppStatic.getInstance().captcha)) {
				startActivity(new Intent(this, PayPW2Activity.class));
				finish();
			} else {
				Toast.makeText(this, "验证码错误", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.payPW1_timeTv:
			String phone = mPhoneEdt.getText().toString();
			if (!AppUtil.isMobileNO(phone)) {
				Toast.makeText(this, "手机号码格式不正确!", Toast.LENGTH_SHORT).show();
				return;
			}
			AppStatic.getCode(this, phone, "3",null);
			timeCount.start();
			break;
		}
	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {// 计时完毕
			mTimeTv.setEnabled(true);
			mTimeTv.setText("获取验证码");
			time = 120;
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程
			mTimeTv.setEnabled(false);
			mTimeTv.setText("重新发送（" + time + "）");
			time--;
		}
	}
}

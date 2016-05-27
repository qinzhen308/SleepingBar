package com.bolaa.medical.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.bolaa.medical.R;
import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.common.AppStatic;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.httputil.HttpRequester;
import com.bolaa.medical.utils.AppUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.util.DialogUtil;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 手机验证
 * 
 * @author jjj
 * 
 * @time 2015-12-18
 */
public class PhoneValidateActivity extends BaseActivity {
	private EditText mPhoneEdt;
	private EditText mCodeEdt;
	private TextView mApplyCodeTv;
	private Button mOkBtn;

	private int time = 120;
	private TimeCount timeCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActiviyContextView(R.layout.activity_phonevalidate);
		setTitleText("", "手机验证", 0, true);

		timeCount = new TimeCount(120000, 1000);
		mPhoneEdt = (EditText) findViewById(R.id.phone_phoneEdt);
		mCodeEdt = (EditText) findViewById(R.id.phone_yzCodeEdt);
		mApplyCodeTv = (TextView) findViewById(R.id.phone_timeTv);
		mOkBtn = (Button) findViewById(R.id.phone_okBtn);

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
				if (!TextUtils.isEmpty(s.toString())) {
					mOkBtn.setEnabled(true);
				} else {
					mOkBtn.setEnabled(false);
				}

			}
		});
		mApplyCodeTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pString = mPhoneEdt.getText().toString();
				if (!AppUtil.isMobileNO(pString)) {
					Toast.makeText(PhoneValidateActivity.this, "手机号码格式不正确",
							Toast.LENGTH_SHORT).show();
					return;
				}
				AppStatic.getCode(PhoneValidateActivity.this, pString, "3",null);
				timeCount.start();
			}
		});
		mOkBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pString = mPhoneEdt.getText().toString();

				if (!AppUtil.isMobileNO(pString)) {
					Toast.makeText(PhoneValidateActivity.this, "手机号码格式不正确",
							Toast.LENGTH_SHORT).show();
					return;
				}
				String code = mCodeEdt.getText().toString();
				if (TextUtils.isEmpty(code)) {
					Toast.makeText(PhoneValidateActivity.this, "请输入验证码！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				validatePW(pString, code);
			}
		});

		String phone = getIntent().getStringExtra("phone");
		if (!TextUtils.isEmpty(phone)) {
			mPhoneEdt.setText(phone);
		}
	}

	/**
	 * 手机认证
	 * 
	 * @param mobile_phone
	 * @param sms_captcha
	 */
	private void validatePW(String mobile_phone, String sms_captcha) {
		DialogUtil.showDialog(lodDialog);
		HttpRequester requester = new HttpRequester();
		requester.mParams.put("mobile_phone", mobile_phone);
		requester.mParams.put("sms_captcha", sms_captcha);
		NetworkWorker.getInstance().post(
				AppUrls.getInstance().URL_info_validate_phone, new ICallback() {

					@Override
					public void onResponse(int status, String result) {
						DialogUtil.dismissDialog(lodDialog);
						JSONObject object;
						try {
							object = new JSONObject(result);

							if (object != null
									&& object.getString("status").equals("0")) {
								Toast.makeText(PhoneValidateActivity.this,
										"手机认证成功!", Toast.LENGTH_SHORT).show();
								PhoneValidateActivity.this.finish();
							} else {
								Toast.makeText(
										PhoneValidateActivity.this,
										object == null ? "" : object
												.getString("message"),
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							DialogUtil.dismissDialog(lodDialog);
							Toast.makeText(PhoneValidateActivity.this, "认证失败",
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}

					}
				}, requester);

	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {// 计时完毕
			mApplyCodeTv.setEnabled(true);
			mApplyCodeTv.setText("获取验证码");
			time = 120;
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程
			mApplyCodeTv.setEnabled(false);
			mApplyCodeTv.setText("重新发送（" + time + "）");
			time--;
		}
	}
}

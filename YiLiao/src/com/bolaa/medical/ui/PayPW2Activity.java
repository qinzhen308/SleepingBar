package com.bolaa.medical.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.bolaa.medical.R;
import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.httputil.HttpRequester;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.util.DialogUtil;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 支付密码
 * 
 * @author jjj
 * 
 * @time 2015-12-18
 */
public class PayPW2Activity extends BaseActivity {

	private EditText mPW1Edt;
	private EditText mPW2Edt;
	private Button mOkBtn;
	private boolean isP1 = false;
	private boolean isP2 = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActiviyContextView(R.layout.activity_paypw2);
		setTitleText("", "支付密码", 0, true);

		mPW1Edt = (EditText) findViewById(R.id.payPW2_newPWEdt);
		mPW2Edt = (EditText) findViewById(R.id.payPW2_newPW2Edt);
		mOkBtn = (Button) findViewById(R.id.payPW2_okBtn);
		mOkBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String p1 = mPW1Edt.getText().toString();
				String p2 = mPW2Edt.getText().toString();

				if (!p1.equals(p2)) {
					Toast.makeText(PayPW2Activity.this, "两次密码输入不同，请重新输入！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				updatePayPw(p1, p2);
			}
		});
		mPW1Edt.addTextChangedListener(new TextWatcher() {

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
					isP1 = false;
				} else {
					isP1 = true;
				}

				if (isP1 && isP2) {
					mOkBtn.setEnabled(true);
				} else {
					mOkBtn.setEnabled(false);
				}
			}
		});
		mPW2Edt.addTextChangedListener(new TextWatcher() {

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
					isP2 = false;
				} else {
					isP2 = true;
				}

				if (isP1 && isP2) {
					mOkBtn.setEnabled(true);
				} else {
					mOkBtn.setEnabled(false);
				}
			}
		});

	}

	/**
	 * 修改支付密码
	 * 
	 * @param password
	 * @param confirm_password
	 */
	private void updatePayPw(String password, String confirm_password) {
		DialogUtil.showDialog(lodDialog);
		HttpRequester requester = new HttpRequester();
		requester.mParams.put("password", password);
		requester.mParams.put("confirm_password", confirm_password);
		NetworkWorker.getInstance().post(
				AppUrls.getInstance().URL_info_update_payPW, new ICallback() {

					@Override
					public void onResponse(int status, String result) {
						Log.e("支付密码-----------", "支付密码----------" + result);
						DialogUtil.dismissDialog(lodDialog);
						JSONObject object;
						try {
							object = new JSONObject(result);

							if (object != null
									&& object.getString("status").equals("0")) {
								Toast.makeText(PayPW2Activity.this, "密码设置成功!",
										Toast.LENGTH_SHORT).show();
								PayPW2Activity.this.finish();
							} else {
								Toast.makeText(
										PayPW2Activity.this,
										object == null ? "" : object
												.getString("message"),
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, requester);

	}

}

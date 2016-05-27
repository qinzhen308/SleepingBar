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

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 修改密码
 * 
 * @author jjj
 * 
 * @time 2015-12-18
 */
public class UpdatePWActivity extends BaseActivity {
	private EditText mOldePWEdt;
	private EditText mNewPWEdt;
	private EditText mNew2PWEdt;
	private Button mOkBtn;
	private boolean p1 = false;
	private boolean p2 = false;
	private boolean p3 = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActiviyContextView(R.layout.activity_updatepw);
		setTitleText("", "修改密码", 0, true);

		mOldePWEdt = (EditText) findViewById(R.id.updatePW_oldPWEdt);
		mNewPWEdt = (EditText) findViewById(R.id.updatePW_newPWEdt);
		mNew2PWEdt = (EditText) findViewById(R.id.updatePW_newAgainPWEdt);
		mOkBtn = (Button) findViewById(R.id.updatePW_okBtn);

		mOldePWEdt.addTextChangedListener(new TextWatcher() {

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
					p1 = false;
				} else {
					p1 = true;
				}

				if (p1 && p2 && p3) {
					mOkBtn.setEnabled(true);
				} else {
					mOkBtn.setEnabled(false);
				}
			}
		});
		mNewPWEdt.addTextChangedListener(new TextWatcher() {

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
					p2 = false;
				} else {
					p2 = true;
				}

				if (p1 && p2 && p3) {
					mOkBtn.setEnabled(true);
				} else {
					mOkBtn.setEnabled(false);
				}
			}
		});
		mNew2PWEdt.addTextChangedListener(new TextWatcher() {

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
					p3 = false;
				} else {
					p3 = true;
				}

				if (p1 && p2 && p3) {
					mOkBtn.setEnabled(true);
				} else {
					mOkBtn.setEnabled(false);
				}
			}
		});
		mOkBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String oldpw = mOldePWEdt.getText().toString();
				if (TextUtils.isEmpty(oldpw)) {
					Toast.makeText(UpdatePWActivity.this, "请输入旧密码",
							Toast.LENGTH_SHORT).show();
					return;
				}
				String newPw = mNewPWEdt.getText().toString();
				if (TextUtils.isEmpty(newPw)) {
					Toast.makeText(UpdatePWActivity.this, "请输入新密码",
							Toast.LENGTH_SHORT).show();
					return;
				}
				String new2Pw = mNew2PWEdt.getText().toString();
				if (TextUtils.isEmpty(new2Pw)) {
					Toast.makeText(UpdatePWActivity.this, "请再次输入新密码",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (!newPw.equals(new2Pw)) {
					Toast.makeText(UpdatePWActivity.this, "两次输入密码不同，请重新输入",
							Toast.LENGTH_SHORT).show();
					mNew2PWEdt.setText("");
					return;
				}
				updatePW(oldpw, newPw, new2Pw);
			}
		});
	}

	/**
	 * 修改密码
	 * 
	 * @param orig_password
	 * @param new_password
	 * @param confirm_password
	 */
	private void updatePW(String orig_password, String new_password,
			String confirm_password) {
		DialogUtil.showDialog(lodDialog);
		HttpRequester requester = new HttpRequester();
		requester.mParams.put("orig_password", orig_password);
		requester.mParams.put("new_password", new_password);
		requester.mParams.put("confirm_password", confirm_password);

		NetworkWorker.getInstance().post(
				AppUrls.getInstance().URL_info_updatePW, new ICallback() {

					@Override
					public void onResponse(int status, String result) {
						DialogUtil.dismissDialog(lodDialog);
						JSONObject object;
						try {
							object = new JSONObject(result);

							if (object != null
									&& object.getString("status").equals("0")) {
								startActivityForResult(new Intent(
										UpdatePWActivity.this,
										UpdateNoticeActivity.class), 222);
							} else {
								Toast.makeText(
										UpdatePWActivity.this,
										object == null ? "修改失败" : object
												.getString("message"),
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, requester);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 222) {
			finish();
		}
	}
}

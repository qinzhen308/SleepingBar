package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.UserInfo;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.core.framework.image.universalimageloader.core.ImageLoader;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.DialogUtil;

import java.util.Timer;
import java.util.TimerTask;

public class UserRePwdActivity extends BaseActivity {

//	TextView tvStep;
	EditText etOne;
	EditText etTwo;
	TextView btnGetCaptcha;
	TextView labelStep2;
	TextView tvTip;
	TextView tvCaptchaSendTip;
	View dividerStep2;
	TextView btnSubmit;

	private int step = 0;// 步骤
	private String captcha = "";
	private String phone = "";
	
	private String[] hintText1={"请输入手机号码","请输入密码"};
	private String[] hintText2={"手机验证码","请再次输入密码"};
//	private String[] textTip={"短信验证码","确认新密码"};
	
	private String userID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		setListener();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(timer!=null){
			timer.cancel();
			timer=null;
		}
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(step>0){
			backStep();
			return;
		}
		super.onBackPressed();
	}

	private void initView() {
		setActiviyContextView(R.layout.activity_find_pwd, false, true);
		setTitleTextRightText("", "找回密码", "",true);
//		tvStep = (TextView) findViewById(R.id.baseTitle_rightTv);
		labelStep2 = (TextView) findViewById(R.id.label_step2);
		dividerStep2 =  findViewById(R.id.divider_step2);
		tvTip =  (TextView)findViewById(R.id.tv_tip);
		etOne = (EditText) findViewById(R.id.et_one);
		etTwo = (EditText) findViewById(R.id.et_two);
		btnSubmit = (TextView) findViewById(R.id.btn_submit);
		btnGetCaptcha = (TextView) findViewById(R.id.tv_get_captcha);
		tvCaptchaSendTip = (TextView) findViewById(R.id.tv_tip2);
//		btnSubmit.setEnabled(false);
	}

	private void setListener() {
		btnGetCaptcha.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
//		etOne.addTextChangedListener(watcher);
//		etTwo.addTextChangedListener(watcher);
	}

	private void nextStep() {
			step++;
			captcha = etTwo.getText().toString();
			phone = etOne.getText().toString();
//			tvStep.setText("2/2");
			etOne.setHint(hintText1[step]);
			etTwo.setHint(hintText2[step]);
			etOne.clearFocus();
			etTwo.clearFocus();
			etOne.setText("");
			etTwo.setText("");
			etOne.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
			etTwo.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
			etOne.setTransformationMethod(PasswordTransformationMethod.getInstance());
			etTwo.setTransformationMethod(PasswordTransformationMethod.getInstance());
			btnGetCaptcha.setVisibility(View.GONE);
//			labelStep2.setVisibility(View.VISIBLE);
//			dividerStep2.setVisibility(View.VISIBLE);
//			tvTip.setText(textTip[step]);
			btnSubmit.setText("完成");
			setTitleTextRightText("", "设置密码", "", true);
	}
	
	private void backStep() {
		step--;
//		tvStep.setText("1/2");
		etOne.setHint(hintText1[step]);
		etTwo.setHint(hintText2[step]);
		etOne.clearFocus();
		etTwo.clearFocus();
		etOne.setText(phone);
		etTwo.setText("");
		etOne.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		etTwo.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		etOne.setTransformationMethod(null);
		etTwo.setTransformationMethod(null);
		btnGetCaptcha.setVisibility(View.VISIBLE);
//		labelStep2.setVisibility(View.GONE);
//		dividerStep2.setVisibility(View.GONE);
//		tvTip.setText(textTip[step]);
		btnSubmit.setText("下一步");
		setTitleTextRightText("", "找回密码", "",true);
	}

	/**
	 * 验证短信验证码
	 */
	private void verifyCode() {
		Editable editable = etTwo.getText();
		String phoneNumber=etOne.getText().toString().trim();
		if (AppUtil.isNull(phoneNumber)) {
			AppUtil.showToast(this, "请输入手机号");
			return;
		}
		if (editable == null || AppUtil.isNull(editable.toString())) {
			AppUtil.showToast(this, "请输入验证码");
			return;
		}
		DialogUtil.showDialog(lodDialog);
		ParamBuilder params = new ParamBuilder();
		params.append("code", editable.toString().trim());
		params.append("mobile_phone", phoneNumber);
	
		NetworkWorker.getInstance().get(
				APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_FIND_PWD_VERIFY_CAPTCHA), new ICallback() {

					@Override
					public void onResponse(int status, String result) {
						if (!isFinishing()) {
							DialogUtil.dismissDialog(lodDialog);
						}
						if(status==200){
							BaseObject<UserInfo> object=GsonParser.getInstance().parseToObj(result, UserInfo.class);
							if(object!=null){
								if(object.data!=null&&object.status==BaseObject.STATUS_OK){
									AppUtil.showToast(getApplicationContext(), "验证成功,请修改密码");

									AppStatic.getInstance().isLogin = true;
									
									PreferencesUtils.putBoolean("isLogin", true);
									ImageLoader.getInstance().clearDiscCache();
									ImageLoader.getInstance().clearMemoryCache();
									AppStatic.getInstance().setmUserInfo(
											object.data);
									AppStatic.getInstance().saveUser(object.data);

									nextStep();
								}else {
									AppUtil.showToast(getApplicationContext(), object.msg);
								}
							}else {
								AppUtil.showToast(getApplicationContext(), "请检查网络");
							}
						}

					}
				});
	}

	public boolean checkEditor(EditText otherEt) {
		Editable editable = otherEt.getText();
		if (editable == null)
			return false;
		return !AppUtil.isNull(editable.toString());
	}
	
	@Override
	public void onLeftClick() {
		// TODO Auto-generated method stub
		if(step>0){
			backStep();
			return;
		}
		super.onLeftClick();
	}

	@Override
	public void onClick(View v) {
		if (v == btnSubmit) {
			if (step == 0) {
				verifyCode();
			} else if (step == 1) {
				String pw1 = etOne.getText().toString();
				if (pw1.length() < 6 || pw1.length() > 24) {
					Toast.makeText(this, "密码不能少于6位，且不能超过24位",
							Toast.LENGTH_SHORT).show();
					return;
				}
				findPW(pw1, etTwo.getText().toString());
			}
		} else if (v == btnGetCaptcha) {// 获取验证码
			phone = etOne.getText().toString();
			getCode();
		}else {
			super.onClick(v);
		}
	}
	

	TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if (checkEditor(etOne) && checkEditor(etTwo)) {
				btnSubmit.setPressed(false);
				btnSubmit.setEnabled(true);
			} else {
				btnSubmit.setPressed(true);
				btnSubmit.setEnabled(false);
			}
		}
	};

	/**
	 * 获取验证码-忘记密码时2
	 * 
	 * @param phone
	 */
	private void getCode() {
		DialogUtil.showDialog(lodDialog);
		if(AppUtil.isNull(phone)||phone.length()<11){
			AppUtil.showToast(getApplicationContext(), "请输入正确的手机号");
		}
		HttpRequester mRequester = new HttpRequester();
		mRequester.mParams.clear();
		mRequester.mParams.put("mobile_phone", phone);
		mRequester.mParams.put("send_type", "pwd_code");

		NetworkWorker.getInstance().post(AppUrls.getInstance().URL_GET_CAPTCHA,
				new ICallback() {

					@Override
					public void onResponse(int status, String result) {
						Log.e("------getCode---", "------getCode---" + result);
						if(!isFinishing()){
							DialogUtil.dismissDialog(lodDialog);
						}
						if(status==200){
							BaseObject<Object> baseObject=GsonParser.getInstance().parseToObj(result, Object.class);
							if(baseObject!=null&&baseObject.status==BaseObject.STATUS_OK){
								AppUtil.showToast(getApplicationContext(), "验证码发送成功");
								captchaBtnDisabled();
								tvCaptchaSendTip.setVisibility(View.VISIBLE);
							}else {
								AppUtil.showToast(getApplicationContext(), baseObject==null?"发送失败":baseObject.msg);
							}
						}else {
							AppUtil.showToast(getApplicationContext(), "发送失败");
						}

					}
				}, mRequester);
	}
	
	Timer timer=null;
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			int i =msg.what;
			if(i>=0){
				btnGetCaptcha.setText(""+i+"s");
			}else {
				btnGetCaptcha.setText("重新获取");
				btnGetCaptcha.setEnabled(true);
				btnGetCaptcha.setTextColor(getResources().getColor(R.color.blue));
				timer.cancel();
				timer=null;
			}
		};
	};
	private void captchaBtnDisabled(){
		btnGetCaptcha.setEnabled(false);
		btnGetCaptcha.setText("60s");
		btnGetCaptcha.setTextColor(getResources().getColor(R.color.text_grey_french2));
		timer=new Timer();
		timer.schedule(new TimerTask() {
			int i=60;
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(--i);
			}
		}, 0, 1000);
		
		
	}

	/**
	 * 找回密码
	 */
	private void findPW(String pw1, String pw2) {
		if (!pw1.equals(pw2)) {
			Toast.makeText(this, "两次密码输入不同，请重新输入！", Toast.LENGTH_SHORT).show();
			return;
		}

		DialogUtil.showDialog(lodDialog);
		ParamBuilder params = new ParamBuilder();
		params.append("password", pw1);
		params.append("repassword", pw2);
	
		NetworkWorker.getInstance().get(
				APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_FIND_PWD), new ICallback() {

					@Override
					public void onResponse(int status, String result) {
						if (!isFinishing()) {
							DialogUtil.dismissDialog(lodDialog);
						}
						if(status==200){
							BaseObject<Object> object=GsonParser.getInstance().parseToObj(result, Object.class);
							if(object!=null){
								if(object.data!=null&&object.status==BaseObject.STATUS_OK){
									AppUtil.showToast(getApplicationContext(), "修改成功");
									setResult(RESULT_OK);
									finish();
								}else {
									AppUtil.showToast(getApplicationContext(), object.msg);
								}
							}else {
								AppUtil.showToast(getApplicationContext(), "请检查网络");
							}
						}else {
							AppUtil.showToast(getApplicationContext(), "网络连接错误，请重试");
						}

					}
				});
	}
	
	public static void invoke(Context context){
		Intent intent =new Intent(context,UserRePwdActivity.class);
		context.startActivity(intent);
	}
	

}

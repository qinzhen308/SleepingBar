package com.bolaa.sleepingbar.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.UserInfo;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.store.sharePer.PreferencesUtils;

import java.util.Timer;
import java.util.TimerTask;


public class Register3Activity extends BaseActivity implements OnClickListener {

	EditText edPhone;
	EditText edPwd;
	EditText edCaptcha;
	TextView btnSubmit;
	TextView btnGetCode;
	TextView tvCaptchaSendTip;
	String phone;
	String code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView();
		setListener();
	}

	private void initView() {
		setActiviyContextView(R.layout.activity_register3, false, true);
		setTitleText("", "注册", 0, true);
		edPhone = (EditText) findViewById(R.id.et_phone);
		edPwd = (EditText) findViewById(R.id.et_password);
		edCaptcha = (EditText) findViewById(R.id.et_captcha);
		btnSubmit = (TextView) findViewById(R.id.btn_submit3);
		btnGetCode = (TextView) findViewById(R.id.tv_get_captcha);
		tvCaptchaSendTip = (TextView) findViewById(R.id.tv_tip2);
	}

	private void setListener() {
		btnSubmit.setOnClickListener(this);
		btnGetCode.setOnClickListener(this);

//		edPwd.addTextChangedListener(watcher);
//		edPhone.addTextChangedListener(watcher);
//		edCaptcha.addTextChangedListener(watcher);

//		cbShowPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//
//				if (isChecked) {
//					edPwd.setTransformationMethod(HideReturnsTransformationMethod
//							.getInstance());
//				} else {
//					edPwd.setTransformationMethod(PasswordTransformationMethod
//							.getInstance());
//				}
//			}
//		});
	}

	TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {

			checkSubmitBtn();
		}
	};

	private boolean checkSubmitBtn() {
		if (checkEditor(edPhone) && checkEditor(edPwd)&&checkEditor(edCaptcha)) {
			btnSubmit.setEnabled(true);
			return true;
		} else {
			btnSubmit.setEnabled(false);
			return false;
		}
	}

	public boolean checkEditor(EditText otherEt) {
		Editable editable = otherEt.getText();
		if (editable == null)
			return false;
		return !AppUtil.isNull(editable.toString());
	}

	@Override
	public void onClick(View v) {
		if (v == btnSubmit) {
			
			String phone = edPhone.getText().toString();
			String pw = edPwd.getText().toString();
			String captcha = edCaptcha.getText().toString().trim();
			
			if (phone.length()!=11&&phone.length()!=14) {
				Toast.makeText(this, "手机格式错误", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			if (pw.length() < 6 || pw.length() > 24) {
				Toast.makeText(this, "密码不能少于6位，且不能超过24位", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			
			if(AppUtil.isNull(captcha)){
				AppUtil.showTaoToast(this, "请输入验证码");
				return;
			}
			
			register(phone, pw,captcha);
		}else if (v==btnGetCode) {
			getCaptcha();
		}else {
			super.onClick(v);
		}
	}
	
	
	public void getCaptcha(){
		phone=edPhone.getText()!=null?edPhone.getText().toString():"";
		if(AppUtil.isNull(phone)){
			AppUtil.showToast(this, "请输入手机号码");
		}
		if(phone.length()==11||phone.length()==14||phone.length()==15){
			btnGetCode.setEnabled(false);
			AppStatic.getCode(this, phone, "reg",new ICallback() {
				
				@Override
				public void onResponse(int status, String result) {
					// TODO Auto-generated method stub
					if(status==1){//成功
						captchaBtnDisabled();
						tvCaptchaSendTip.setVisibility(View.VISIBLE);
					}else {//失败
						btnGetCode.setEnabled(true);
					}
				}
			});
		}else {
			AppUtil.showToast(this, "手机号格式错误");
		}
	}
	
	
	Timer timer=null;
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			int i =msg.what;
			if(i>=0){
				btnGetCode.setText(""+i+"s");
			}else {
				btnGetCode.setText("重新获取");
				btnGetCode.setEnabled(true);
				btnGetCode.setTextColor(getResources().getColor(R.color.purple));
				timer.cancel();
				timer=null;
			}
		};
	};
	private void captchaBtnDisabled(){
		btnGetCode.setEnabled(false);
		btnGetCode.setText("60s");
		btnGetCode.setTextColor(getResources().getColor(R.color.text_grey_french2));
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
	 * 注册
	 */
	private void register(final String phone, String pw,String captcha) {
		ParamBuilder params = new ParamBuilder();
		params.append("mobile_phone", phone);
		params.append("code", captcha);
		params.append("password", pw);
		
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_REGISTER),
				new ICallback() {

					@Override
					public void onResponse(int status, String result) {
						Log.e("------regitser---", "------regitser---" + result);
						if(status==200){
							BaseObject<UserInfo> object=GsonParser.getInstance().parseToObj(result, UserInfo.class);
							if(object!=null){
								if(object.data!=null&&object.status==BaseObject.STATUS_OK){
									Toast.makeText(Register3Activity.this, "注册成功",
											Toast.LENGTH_SHORT).show();

									AppStatic.getInstance().isLogin = true;
									
									PreferencesUtils.putBoolean("isLogin", true);

									AppStatic.getInstance().setmUserInfo(
											object.data);
									AppStatic.getInstance().saveUser(object.data);
									FinishInfoActivity.invoke(Register3Activity.this);
									setResult(RESULT_OK);
									finish();
								}else {
									AppUtil.showToast(getApplicationContext(), object.msg);
								}
							}else {
								AppUtil.showToast(getApplicationContext(), "注册异常");
							}
						}else {
							AppUtil.showToast(getApplicationContext(), "请检查网络");
						}
						
					}
				});
	}
	
	
	public static void invoke(Activity context,int requestCode){
		Intent intent=new Intent(context,Register3Activity.class);
		context.startActivityForResult(intent, requestCode);
	}
}

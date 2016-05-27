package com.bolaa.sleepingbar.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.ThirdUser;
import com.bolaa.sleepingbar.model.UserInfo;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.thirdlogin.IThirdLogin;
import com.bolaa.sleepingbar.thirdlogin.ThirdCallBack;
import com.bolaa.sleepingbar.thirdlogin.ThirdFactory;
import com.bolaa.sleepingbar.thirdlogin.qq.QQLogin;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.core.framework.image.universalimageloader.core.ImageLoader;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA. User: adelbert Date: 13-1-22 Time: 上午11:23 To
 * change this template use File | Settings | File Templates.
 */

public class UserLoginActivity extends BaseActivity implements
		View.OnClickListener {

	private Button mBtnLogin;
	private EditText mEditUsername;
	private EditText mEditPassword;
	private TextView mTvRePwd;
//	private TextView mTvQQLogin;
//	private TextView mTvChatLogin;
	private TextView mTvUserNameCancel;
	private TextView mTvPasswordCancel;
	private TextView register;

	private String mUserName;

	private boolean isName = false;
	private boolean isPW = false;

	private boolean isBindLogin;
	private ThirdUser outThirdUser;

	private String activityType = "";
	
	public final int REQUEST_CODE_REGIST=1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		registListener();
		activityType = getIntent().getStringExtra("activityType");
	}

	private void setExtra() {
		Intent intent = getIntent();
		Bundle data = intent.getBundleExtra("data");
		if (data != null) {
			outThirdUser = (ThirdUser) data.getSerializable("third_user");
			if (outThirdUser != null)
				isBindLogin = true;
		}
	}

	private void initView() {
		setActiviyContextView(R.layout.activity_login, false, true);
		setTitleText("",  "登录", 0, true);

		register = (TextView) findViewById(R.id.regist);
		mEditUsername = (EditText) findViewById(R.id.edit_usename);
		mEditPassword = (EditText) findViewById(R.id.edit_passwrod);
		mBtnLogin = (Button) findViewById(R.id.btn_login);
		mTvRePwd = (TextView) findViewById(R.id.tv_re_pwd);
		mTvUserNameCancel = (TextView) findViewById(R.id.tv_usename_cancel_press);
		mTvPasswordCancel = (TextView) findViewById(R.id.tv_password_cancel_press);
//		mTvChatLogin = (TextView) findViewById(R.id.login_chatTv);
//		mTvQQLogin = (TextView) findViewById(R.id.login_qqTv);

		
	}


	private void registListener() {

		mBtnLogin.setOnClickListener(this);
//		mTvQQLogin.setOnClickListener(this);
//		mTvChatLogin.setOnClickListener(this);
		mEditUsername.setOnClickListener(this);

		mTvRePwd.setOnClickListener(this);

		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Register3Activity.invoke(UserLoginActivity.this,REQUEST_CODE_REGIST);
			}
		});

		if (!StringUtil.isEmpty(mEditUsername.getText().toString().trim())) {
			mTvUserNameCancel.setVisibility(View.VISIBLE);
		}
		mEditUsername.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {

				if (StringUtil.isEmpty(mEditUsername.getText().toString()
						.trim())) {
					mTvUserNameCancel.setVisibility(View.GONE);
					isName = false;
				} else {
					isName = true;
					mTvUserNameCancel.setVisibility(View.VISIBLE);
				}
				/*if (isName && isPW) {
					mBtnLogin.setEnabled(true);
				} else {
					mBtnLogin.setEnabled(false);
				}*/
			}
		});

		// 密码框注册文本监听事件
		if (!StringUtil.isEmpty(mEditPassword.getText().toString().trim())) {
			mTvPasswordCancel.setVisibility(View.VISIBLE);
		}
		mEditPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (StringUtil.isEmpty(mEditPassword.getText().toString()
						.trim())) {
					mTvPasswordCancel.setVisibility(View.GONE);
					isPW = false;
				} else {
					isPW = true;
					mTvPasswordCancel.setVisibility(View.VISIBLE);
				}
				/*if (isName && isPW) {
					mBtnLogin.setEnabled(true);
				} else {
					mBtnLogin.setEnabled(false);
				}*/
			}
		});

		// 注册清除帐号框
		mTvUserNameCancel.setOnClickListener(this);
		mTvPasswordCancel.setOnClickListener(this);
	}


	@Override
	public void onClick(View paramView) {
		if (paramView == mBtnLogin) {
			login();// 登录
		} else if (paramView == mTvRePwd) {
			startActivityForResult(new Intent(this, UserRePwdActivity.class),22);
		} else if (paramView == mTvUserNameCancel) {
			//
			mEditUsername.setText("");
			mTvUserNameCancel.setVisibility(View.GONE);
		} else if (paramView == mTvPasswordCancel) {
			mEditPassword.setText("");
			mTvPasswordCancel.setVisibility(View.GONE);
		}  else {
			super.onClick(paramView);
		}

	}

	/**
	 * 登录
	 */
	private void login() {
		DialogUtil.showDialog(lodDialog);
		// HttpRequester mRequester = new HttpRequester();
		if (StringUtil.isEmpty(mEditUsername.getText().toString())){
			AppUtil.showToast(this, "请输入用户名");
			return ;
		}
		if (StringUtil.isEmpty(mEditPassword.getText().toString())){
			AppUtil.showToast(this, "请输入密码");
			return ;
		}
		ParamBuilder params = new ParamBuilder();
		params.append("password", mEditPassword.getText().toString());
		params.append("mobile_phone", mEditUsername.getText().toString());
		if (outThirdUser != null) {
			if (outThirdUser.partnerType == ThirdFactory.TYPE_QQ) {
				params.append("qq_openid", outThirdUser.openId);
			} else if (outThirdUser.partnerType == ThirdFactory.TYPE_WECHAT) {
				params.append("weichat_id", outThirdUser.unionid);
			}
		}

		NetworkWorker.getInstance().get(
				APIUtil.parseGetUrlHasMethod(params.getParamList(),
						AppUrls.getInstance().URL_LOGIN), new ICallback() {

					@Override
					public void onResponse(int status, String result) {
						if (!isFinishing()) {
							DialogUtil.dismissDialog(lodDialog);
						}
						if(status==200){
							BaseObject<UserInfo> object=GsonParser.getInstance().parseToObj(result, UserInfo.class);
							if(object!=null){
								if(object.data!=null&&object.status==BaseObject.STATUS_OK){
									AppUtil.showToast(getApplicationContext(), "登录成功");

									AppStatic.getInstance().isLogin = true;
									
									PreferencesUtils.putBoolean("isLogin", true);
									ImageLoader.getInstance().clearDiscCache();
									ImageLoader.getInstance().clearMemoryCache();
									AppStatic.getInstance().setmUserInfo(
											object.data);
									AppStatic.getInstance().saveUser(object.data);

									setResult(RESULT_OK);
									finish();
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

	/**
	 * 尝试一下三方登录 返回码status 0 成功登录 403未绑定 其他都是错误
	 * 
	 *            三方登录方式
	 *            微信 uid qq openid
	 */
	private void tryThirdLogin(final ThirdUser thirdUser) {
		ParamBuilder params = new ParamBuilder();
		params.append(ParamBuilder.ACCESS_TOKEN,
				NetworkWorker.getInstance().ACCESS_TOKEN);
		String url = "";
		if (thirdUser.partnerType == ThirdFactory.TYPE_QQ) {
			params.append("qq_openid", thirdUser.openId);
			url = AppUrls.getInstance().URL_THIRD_LOGIN_QQ;
		} else if (thirdUser.partnerType == ThirdFactory.TYPE_WECHAT) {
			// login_by_weichat
			// weichat_id
			url = AppUrls.getInstance().URL_THIRD_LOGIN_WX;
			params.append("weichat_id", thirdUser.unionid);
		}

		NetworkWorker.getInstance().get(
				APIUtil.parseGetUrlHasMethod(params.getParamList(), url),
				new ICallback() {

					@Override
					public void onResponse(int status, String result) {
						DialogUtil.dismissDialog(lodDialog);
						if (status == 200) {
							JSONObject object;
							try {
								object = new JSONObject(result);
								PreferencesUtils.putString("access_token",
										object.getString("access_token"));
								NetworkWorker.getInstance().ACCESS_TOKEN = object
										.getString("access_token");
								int resultStatus = object.optInt("status");
								if (resultStatus == 0) {
									Toast.makeText(UserLoginActivity.this,
											"登录成功", Toast.LENGTH_SHORT).show();

									BaseObject<UserInfo> oUser = GsonParser
											.getInstance().parseToObj(result,
													UserInfo.class);

									AppStatic.getInstance().isLogin = true;
									PreferencesUtils
											.putBoolean("isLogin", true);

									AppStatic.getInstance().setmUserInfo(
											oUser.data);
									AppStatic.getInstance().saveUser(
											oUser.data);
									Intent intent = new Intent();
									intent.setAction("GoodBusNum");
									sendBroadcast(intent);
									finish();

								} else if (resultStatus == 403) {
									// 需要绑定
								} else {
									Toast.makeText(UserLoginActivity.this,
											object.getString("message"),
											Toast.LENGTH_SHORT).show();
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

					}
				});
	}

	private void doThirdLogin(int type) {
		DialogUtil.showDialog(lodDialog);
		final IThirdLogin thirdLogin = ThirdFactory.getInstance(type);
		thirdLogin.login(this, new ThirdCallBack() {

			@Override
			public void onSuccess(Object obj) {
				// TODO Auto-generated method stub

				// 未经验证
				if (obj instanceof ThirdUser) {
					tryThirdLogin((ThirdUser) obj);
				}

			}

			@Override
			public void onFailed(Object obj) {
				// TODO Auto-generated method stub
				AppUtil.showToast(getApplicationContext(),
						obj != null ? obj.toString() : "失败");

			}

			@Override
			public void onCacel() {
				// TODO Auto-generated method stub
				AppUtil.showToast(getApplicationContext(), "取消");

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		((QQLogin) ThirdFactory.getInstance(ThirdFactory.TYPE_QQ))
				.onActivityResult(requestCode, resultCode, data,
						new ThirdCallBack() {

							@Override
							public void onSuccess(Object obj) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onFailed(Object obj) {
								// TODO Auto-generated method stub
								// AppUtil.showToast(getApplicationContext(),
								// "失败");

							}

							@Override
							public void onCacel() {
								// TODO Auto-generated method stub
								// AppUtil.showToast(getApplicationContext(),
								// "取消");

							}
						});

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == 2) {
			finish();
		}else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_REGIST) {
			setResult(RESULT_OK);
			finish();
		}else if (resultCode == RESULT_OK && requestCode == 22) {
			setResult(RESULT_OK);
			finish();
		}

	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.anim_zoom_out, R.anim.anim_bottom_out);
	}

	private boolean invalidate(String userName, String pwd) {

		boolean flag = false;
		String tip = "";
		if (userName.length() == 0) {
			tip = "请输入手机号或用户名";
		} else if (userName.length() < 3) {
			tip = "用户名至少为3个字!";
		} else if (pwd.length() == 0) {
			tip = "请填写密码";
		} else if (pwd.length() < 6) {
			tip = "密码过短，最短支持6个字符!";
		} else if (pwd.length() > 24) {
			tip = "密码过长，最长支持24个字符!";
		} else {
			flag = true;
		}
		if (!flag)
			AppUtil.showToast(UserLoginActivity.this, tip);
		return flag;
	}

	public static void invoke(Activity context) {
		Intent intent = new Intent(context, UserLoginActivity.class);
		context.startActivity(intent);
	}

	public static void invokeForResult(Activity context, int requestCode) {
		Intent intent = new Intent(context, UserLoginActivity.class);
		context.startActivityForResult(intent, requestCode);
	}

	public static void invokeForResult(Activity context, int requestCode,
			ThirdUser thirdUser) {
		Intent intent = new Intent(context, UserLoginActivity.class);
		Bundle data = new Bundle();
		data.putSerializable("third_user", thirdUser);
		intent.putExtra("data", data);
		context.startActivityForResult(intent, requestCode);
	}

	
}

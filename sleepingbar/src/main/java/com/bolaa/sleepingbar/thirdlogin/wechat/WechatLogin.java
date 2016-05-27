package com.bolaa.sleepingbar.thirdlogin.wechat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.ThirdUser;
import com.bolaa.sleepingbar.thirdlogin.IThirdLogin;
import com.bolaa.sleepingbar.thirdlogin.ThirdCallBack;
import com.bolaa.sleepingbar.thirdlogin.ThirdFactory;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class WechatLogin implements IThirdLogin {
	private final static String APP_ID = "wxd166f9628465c64a";
	private final static String APP_SECRET = "f33fac3dc5c2cea7a2102ca878eea660";
	private Context mContext;
	private IWXAPI api;
	private String access_token = "";// 微信token
	private String refresh_token = "";// 微信token
	private String openid = "";// 微信openid 但是不唯一，还要获取uid
	private String scope = "";// 授权作用域
	private String expires_in = "";// 失效时间

	private String URL_GET_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";
	private String URL_GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo";

	ThirdCallBack mThirdCallback;
	Handler handler = new Handler(Looper.getMainLooper());

	public WechatLogin(Context context) {
		// TODO Auto-generated constructor stub
		api = WXAPIFactory.createWXAPI(context, APP_ID, false);
		api.registerApp(APP_ID);
	}

	public IWXAPI getApi() {
		return api;
	}

	@Override
	public void login(Activity activity, ThirdCallBack callback) {
		// TODO Auto-generated method stub
		mThirdCallback = callback;
		final com.tencent.mm.sdk.modelmsg.SendAuth.Req req = new com.tencent.mm.sdk.modelmsg.SendAuth.Req();
		req.scope = "snsapi_userinfo";
		api.sendReq(req);
	}

	@Override
	public void logout(ThirdCallBack callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void check() {
		// TODO Auto-generated method stub

	}

	/*
	 * { "openid":"OPENID", "nickname":"NICKNAME", "sex":1,
	 * "province":"PROVINCE", "city":"CITY", "country":"COUNTRY", "headimgurl":
	 * "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0"
	 * , "privilege":[ "PRIVILEGE1", "PRIVILEGE2" ], "unionid":
	 * " o6_bmasdasdsad6_2sgVt7hMZOPfL"
	 * 
	 * }
	 */
	@Override
	public void getUserInfo(final ThirdCallBack callback) {
		// TODO Auto-generated method stub
		ParamBuilder params = new ParamBuilder();
		params.clear();
		params.append("access_token", access_token);
		params.append("openid", openid);
		String response = null;
		try {
			response = NetworkWorker.getInstance().getSync2(
					APIUtil.parseGetUrl(params.getParamList(),
							URL_GET_USER_INFO));
			JSONObject jsonObject = new JSONObject(response);
			final ThirdUser thirdUser = new ThirdUser(ThirdFactory.TYPE_WECHAT,
					jsonObject);
			thirdUser.accessToken = access_token;
			thirdUser.expiresTime = expires_in;
			com.bolaa.sleepingbar.model.UserInfo userInfo = new com.bolaa.sleepingbar.model.UserInfo();
			userInfo.thirdUser=(thirdUser);
			AppStatic.getInstance().saveUser(userInfo);
			if (callback != null) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						callback.onSuccess(thirdUser);
					}
				});
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (callback != null) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						callback.onFailed("获取信息失败");
					}
				});
			}
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data,
			ThirdCallBack callback) {

	}

	public void handleLogin(BaseResp resp, final ThirdCallBack wxEntryAcCallback) {
		SendAuth.Resp auth = (SendAuth.Resp) resp;
		ParamBuilder params = new ParamBuilder();
		params.clear();
		params.append("appid", APP_ID);
		params.append("secret", APP_SECRET);
		params.append("code", auth.code);
		params.append("grant_type", "authorization_code");
		NetworkWorker.getInstance()
				.getCallbackInBg(
						APIUtil.parseGetUrl(params.getParamList(),
								URL_GET_ACCESS_TOKEN), new ICallback() {

							@Override
							public void onResponse(int status, String result) {
								// TODO Auto-generated method stub
								// 返回数据结构
								// {
								// "access_token":"ACCESS_TOKEN",
								// "expires_in":7200,
								// "refresh_token":"REFRESH_TOKEN",
								// "openid":"OPENID",
								// "scope":"SCOPE"
								// }
								if (status == 200) {
									try {
										JSONObject jsonObject = new JSONObject(
												result);
										access_token = jsonObject
												.optString("access_token");
										refresh_token = jsonObject
												.optString("refresh_token");
										openid = jsonObject.optString("openid");
										scope = jsonObject.optString("scope");
										expires_in = jsonObject
												.optString("expires_in");
										getUserInfo(mThirdCallback);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										if (mThirdCallback != null) {
											handler.post(new Runnable() {

												@Override
												public void run() {
													// TODO Auto-generated
													// method stub
													mThirdCallback
															.onFailed("获取凭证失败");
												}
											});
										}
									}
								} else {
									if (mThirdCallback != null) {
										handler.post(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method
												// stub
												mThirdCallback
														.onFailed("获取凭证失败");
											}
										});
									}
								}
								// 用来关闭wxEntryActivity
								wxEntryAcCallback.onSuccess(null);
							}
						});
	}

}

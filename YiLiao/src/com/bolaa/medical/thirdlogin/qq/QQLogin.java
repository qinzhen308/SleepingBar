package com.bolaa.medical.thirdlogin.qq;

import org.json.JSONObject;

import com.bolaa.medical.common.AppStatic;
import com.bolaa.medical.model.ThirdUser;
import com.bolaa.medical.thirdlogin.IThirdLogin;
import com.bolaa.medical.thirdlogin.ThirdCallBack;
import com.bolaa.medical.utils.AppUtil;
import com.core.framework.develop.LogUtil;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class QQLogin implements IThirdLogin{
	private final static String APP_ID="1105067921";//正式的
	Tencent mTencent;
	private Context mContext;
	
	public QQLogin(Context context) {
		// TODO Auto-generated constructor stub
		mContext=context;
		mTencent = Tencent.createInstance(APP_ID, context);
//		com.bolaa.medical.model.UserInfo userInfo=AppStatic.getInstance().getUser();
//		if(userInfo!=null&&!TextUtils.isEmpty(userInfo.getThirdUser().accessToken) 
//				&& !TextUtils.isEmpty(userInfo.getThirdUser().expiresTime)&& !TextUtils.isEmpty(userInfo.getThirdUser().openId)){
//			mTencent.setAccessToken(userInfo.getThirdUser().accessToken,userInfo.getThirdUser().expiresTime );
//			mTencent.setOpenId(userInfo.getThirdUser().openId);
//		}
	}

	@Override
	public void login(Activity activity,final ThirdCallBack callback) {
		// TODO Auto-generated method stub
		
			mTencent.login(activity, "all", new QQUIListener(null){
				@Override
				public void onComplete(Object obj) {
					// TODO Auto-generated method stub
					if(obj instanceof JSONObject){
						JSONObject jsonObject=(JSONObject)obj;
						int retCode=jsonObject.optInt("ret");
						if(retCode==0){
							final String token = jsonObject.optString(Constants.PARAM_ACCESS_TOKEN);
				            final String expires = jsonObject.optString(Constants.PARAM_EXPIRES_IN);
				            final String openId = jsonObject.optString(Constants.PARAM_OPEN_ID);
				            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
				                    && !TextUtils.isEmpty(openId)) {
				                mTencent.setAccessToken(token, expires);
				                mTencent.setOpenId(openId);
				            }
//				            AppUtil.showToast(mContext, "成功");
							LogUtil.d(obj.toString());
							getUserInfo(new ThirdCallBack() {
								
								@Override
								public void onSuccess(Object obj) {
									// TODO Auto-generated method stub
									LogUtil.d("getuserinfo---success--"+obj.toString());
									if(obj instanceof JSONObject){
										JSONObject jsonObject=(JSONObject)obj;
										int retCode=jsonObject.optInt("ret");
										if(retCode==0){
											ThirdUser thirdUser=new ThirdUser(1,jsonObject);
											thirdUser.accessToken=token;
											thirdUser.expiresTime=expires;
											thirdUser.openId=openId;
											com.bolaa.medical.model.UserInfo userInfo =new com.bolaa.medical.model.UserInfo();
											userInfo.thirdUser=(thirdUser);
											AppStatic.getInstance().saveUser(userInfo);
											callback.onSuccess(thirdUser);
											return;
										}
									}
									callback.onFailed(obj);
								}
								
								@Override
								public void onFailed(Object obj) {
									// TODO Auto-generated method stub
									LogUtil.d("getuserinfo---onFailed--"+obj.toString());
									callback.onFailed(obj);
									
								}
								
								@Override
								public void onCacel() {
									// TODO Auto-generated method stub
									LogUtil.d("getuserinfo---success--取消");
									callback.onCacel();
									
								}
							});
						}
					}
					super.onComplete(obj);
				}
			});
		
	}

	@Override
	public void logout(ThirdCallBack callback) {
		// TODO Auto-generated method stub
		if(mTencent!=null){
			mTencent.logout(mContext);
		}
		
	}
	
	@Override
	public void check() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void getUserInfo(ThirdCallBack callback) {
		// TODO Auto-generated method stub
		UserInfo info = new UserInfo(mContext, mTencent.getQQToken());
		info.getUserInfo(new QQUIListener(callback));
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data,ThirdCallBack callback){
		if (requestCode == Constants.REQUEST_LOGIN ||
		    requestCode == Constants.REQUEST_APPBAR) {
		    Tencent.onActivityResultData(requestCode,resultCode,data,new QQUIListener(callback));
		}
	}

}

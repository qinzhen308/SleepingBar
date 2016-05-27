package com.bolaa.medical.common;

import java.util.HashMap;
import java.util.Iterator;

import com.bolaa.medical.httputil.HttpRequester;
import com.bolaa.medical.model.ThirdUser;
import com.bolaa.medical.model.UserInfo;
import com.bolaa.medical.parser.gson.BaseObject;
import com.bolaa.medical.parser.gson.GsonParser;
import com.bolaa.medical.utils.AppUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.store.sharePer.PreferencesUtils;

import android.app.Activity;
import android.content.Context;

public class AppStatic {
	private static Context mContext;
	public static String ACCESS_TOKEN = "token";
	public static String WX_APPID = "wxd166f9628465c64a";
	private static AppStatic mInstance;
	private UserInfo mUserInfo;
	public boolean isLogin = false;// 用户是否登录
	public String captcha = "";
	public static boolean isRefrshHome=false;
	private HashMap<String, Activity> mActivityMap = new HashMap<String, Activity>();

	public static AppStatic getInstance() {
		if (mInstance == null) {
			mInstance = new AppStatic();
		}
		return mInstance;
	}

	public void setmUserInfo(UserInfo mUserInfo) {
		this.mUserInfo = mUserInfo;
	}

	public UserInfo getmUserInfo() {
		return mUserInfo;
	}

	public void init(Context context) {
		mContext = context;
	}
	
	public void clearLoginStatus(){
		if(isLogin){
			clearUser();
			mUserInfo=null;
			isLogin=false;
			PreferencesUtils.putBoolean("isLogin",false);
		}
		
	}

	/**
	 * 添加
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		mActivityMap.put(activity.getClass().getSimpleName(), activity);
	}

	/**
	 * 删除
	 * 
	 * @param activity
	 */
	public void removeActivity(Activity activity) {
		mActivityMap.remove(activity.getClass().getSimpleName());
	}

	/**
	 * 退出
	 */
	public void exit() {
		Iterator<Activity> it = mActivityMap.values().iterator();
		while (it.hasNext()) {
			it.next().finish();
		}
		System.exit(0);
	}

	/**
	 * 清除
	 */
	public void clearActivityMap() {
		mActivityMap.clear();
	}

	public HashMap<String, Activity> getActivityMap() {
		return mActivityMap;
	}

	public void clearUser() {
		PreferencesUtils.remove("user_id");
		PreferencesUtils.remove("real_name");
		PreferencesUtils.remove("user_name");
		PreferencesUtils.remove("avatar");
		PreferencesUtils.remove("sex");
		PreferencesUtils.remove("birthday");
		PreferencesUtils.remove("mobile_phone");
		PreferencesUtils.remove("blood");
		PreferencesUtils.remove("id_card");
		PreferencesUtils.remove("pay_points");
		PreferencesUtils.remove("rank_name");
		PreferencesUtils.remove("rank_points");
		PreferencesUtils.remove("user_rank");
	}

	public void unbindThird(String type) {
		if ("qq".equals(type)) {
			PreferencesUtils.remove("member_qqopenid");
			mUserInfo.member_qqopenid = null;
		} else if ("weichat".equals(type)) {
			PreferencesUtils.remove("member_wxopenid");
			mUserInfo.member_wxopenid = null;
		}
	}

	/**
	 * 保存用户数据
	 * 
	 * @param user
	 */
	public void saveUser(UserInfo user) {
		clearUser();
		putData("user_id", user.user_id);
		putData("real_name", user.real_name);
		putData("user_name", user.user_name);
		putData("avatar", user.avatar);
		putData("sex", user.sex);
		putData("birthday", user.birthday);
		putData("mobile_phone", user.mobile_phone);
		putData("blood", user.blood);
		putData("id_card", user.id_card);
		putData("pay_points", user.pay_points);
		putData("rank_name", user.rank_name);
		putData("", user.rank_points);
		putData("user_rank", user.user_rank);
	}

	private void putData(String key, String value) {
		if (value == null) {
			value = "";
		}
		PreferencesUtils.putString(key, value);
	}

	/**
	 * 获取用户信息
	 * 
	 * @return
	 */
	public UserInfo getUser() {
		UserInfo user = new UserInfo();
		user.user_id=(PreferencesUtils.getString("user_id"));
		user.real_name=(PreferencesUtils.getString("real_name"));
		user.user_name=(PreferencesUtils.getString("user_name"));
		user.avatar=(PreferencesUtils.getString("avatar"));
		user.sex=(PreferencesUtils.getString("sex"));
		user.birthday=(PreferencesUtils.getString("birthday"));
		user.mobile_phone=(PreferencesUtils.getString("mobile_phone"));
		user.blood=(PreferencesUtils.getString("blood"));
		user.id_card=(PreferencesUtils.getString("id_card"));
		user.pay_points=(PreferencesUtils.getString("pay_points"));
		user.rank_name=(PreferencesUtils.getString("rank_name"));
		user.rank_points=(PreferencesUtils.getString("rank_points"));
		user.user_rank=(PreferencesUtils.getString("user_rank"));
		return user;
	}

	/**
	 * 获取验证码 1-注册 2-找回密码 3-其余
	 * 
	 * @param context
	 * @param phone
	 * @param type
	 */
	public static void getCode(final Context context, String phone, String type,final ICallback callback) {

		HttpRequester mRequester = new HttpRequester();
		mRequester.mParams.put("mobile_phone", phone);
		mRequester.mParams.put("send_type", type);
		if ("1".equals(type)) {
			mRequester.mParams.put("access_token", "");
		}
		NetworkWorker.getInstance().post(AppUrls.getInstance().URL_GET_CAPTCHA,
				new ICallback() {

					@Override
					public void onResponse(int status, String result) {
						if(status==200){
							BaseObject<Object> baseObject=GsonParser.getInstance().parseToObj(result, Object.class);
							if(baseObject!=null&&baseObject.status==BaseObject.STATUS_OK){
								AppUtil.showToast(context, "验证码发送成功");
								if(callback!=null){
									callback.onResponse(1, result);//成功
								}
							}else {
								AppUtil.showToast(context, baseObject==null?"发送失败":baseObject.msg);
								callback.onResponse(0, result);//失败
							}
						}else {
							AppUtil.showToast(context, "发送失败");
							callback.onResponse(0, result);//失败
						}
					}
				}, mRequester);
	}

}

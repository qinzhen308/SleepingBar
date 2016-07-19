package com.bolaa.sleepingbar.common;

import android.app.Activity;
import android.content.Context;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.model.UserInfo;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.ui.MainActivity;
import com.bolaa.sleepingbar.ui.QuickLoginActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.store.sharePer.PreferencesUtils;

import java.util.HashMap;
import java.util.Iterator;

public class AppStatic {
	private static Context mContext;
	public static String ACCESS_TOKEN = "token";
	public static String WX_APPID = "wx87618c7611c51777";
	public static String WX_secret = "d9a7e4d6ffe1f10277f5259d6c5bc883";
	public static String Sina_APPKEY = "2889898484";
	public static String Sina_secret = "cbbc5863ce1e178a4f4ba285911c6c14";
	public static String QQ_APPID = "1105355193";
	public static String QQ_APPKEY = "dD2H3NMoun6hqJP4";
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
			//关闭所有页面，重新打开登录页面
			Iterator<Activity> it = mActivityMap.values().iterator();
			LogUtil.d("clearLoginStatus---mActivityMap size---"+mActivityMap.size());
			if (it.hasNext()) {
				LogUtil.d("clearLoginStatus---重新登录---");
				Activity a=it.next();
				if(a instanceof MainActivity){
					QuickLoginActivity.invoke(a);
					a.finish();
				}else {
					MainActivity.invoke(a,true);
				}
			}
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
		PreferencesUtils.remove("got_fund");
		PreferencesUtils.remove("height");
		PreferencesUtils.remove("weight");
		PreferencesUtils.remove("is_hidden_coord");
		PreferencesUtils.remove("is_open_fund");
		PreferencesUtils.remove("is_runking");
		PreferencesUtils.remove("user_money");
		PreferencesUtils.remove("sleep_fund");
		PreferencesUtils.remove("unionid");
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
		putData("nick_name", user.nick_name);
		putData("user_name", user.user_name);
		putData("avatar", user.avatar);
		putData("sex", user.sex);
		putData("birthday", user.birthday);
		putData("got_fund", user.got_fund);
		putData("height", user.height);
		putData("weight", user.weight);
		putData("user_money", user.user_money);
		putData("sleep_fund", user.sleep_fund);
		putData("unionid", user.unionid);
		PreferencesUtils.putInteger("is_hidden_coord", user.is_hidden_coord);
		PreferencesUtils.putInteger("is_open_fund", user.is_open_fund);
		PreferencesUtils.putInteger("is_runking", user.is_runking);
	}

	//只存修改个人信息页面获取的相关数据
	public void updateUserPartly(UserInfo userPartly) {
		putData("user_id", userPartly.user_id);
		putData("nick_name", userPartly.nick_name);
		putData("avatar", userPartly.avatar);
		putData("sex", userPartly.sex);
		putData("birthday", userPartly.birthday);
		putData("height", userPartly.height);
		putData("weight", userPartly.weight);
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
		user.nick_name=(PreferencesUtils.getString("nick_name"));
		user.user_name=(PreferencesUtils.getString("user_name"));
		user.avatar=(PreferencesUtils.getString("avatar"));
		user.sex=(PreferencesUtils.getString("sex"));
		user.birthday=(PreferencesUtils.getString("birthday"));
		user.got_fund=(PreferencesUtils.getString("got_fund"));
		user.height=(PreferencesUtils.getString("height"));
		user.weight=(PreferencesUtils.getString("weight"));
		user.is_hidden_coord=(PreferencesUtils.getInteger("is_hidden_coord",0));
		user.is_open_fund=(PreferencesUtils.getInteger("is_open_fund",0));
		user.is_runking=(PreferencesUtils.getInteger("is_runking",0));
		user.user_money=(PreferencesUtils.getString("user_money"));
		user.sleep_fund=(PreferencesUtils.getString("sleep_fund"));
		user.unionid=(PreferencesUtils.getString("unionid"));
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
								AppUtil.showToast(context, baseObject==null?"发送失败":baseObject.info);
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

package com.bolaa.sleepingbar.thirdlogin;

import android.app.Activity;

public interface IThirdLogin {

	
	public void login(Activity activity, ThirdCallBack callback);
	
	public void logout(ThirdCallBack callback);
	
	public void check();
	
	public void getUserInfo(ThirdCallBack callback);
	
	
}

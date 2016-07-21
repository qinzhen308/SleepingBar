package com.bolaa.sleepingbar.httputil;

import com.bolaa.sleepingbar.HApplication;

import java.util.HashMap;

public class HttpRequester extends com.core.framework.net.HttpRequester{
	
	public HttpRequester() {
		mParams = new HashMap<String, Object>();
		mParams.put("token", HApplication.getInstance().token);
		mParams.put("sb_platform", "android");
	}
	
	
}

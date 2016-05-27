package com.bolaa.medical.httputil;

import java.util.HashMap;

import com.bolaa.medical.HApplication;

public class HttpRequester extends com.core.framework.net.HttpRequester{
	
	public HttpRequester() {
		mParams = new HashMap<String, Object>();
		mParams.put("token", HApplication.getInstance().token);
	}
	
	
}

package com.bolaa.medical.thirdlogin;

import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.model.ThirdUser;
import com.bolaa.medical.model.UserInfo;
import com.bolaa.medical.parser.gson.BaseObject;
import com.bolaa.medical.parser.gson.GsonParser;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;

public class ThirdCheckApi {
	
	private static ThirdCheckApi instance;

	public static ThirdCheckApi getInstance(){
		if(instance==null){
			synchronized (instance) {
				if(instance==null){
					instance=new ThirdCheckApi();
				}
			}
		}

		return instance;
	}
	
	public void bind(){
		
	}
	
	
}

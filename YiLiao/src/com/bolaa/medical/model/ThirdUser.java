package com.bolaa.medical.model;

import java.io.Serializable;

import org.json.JSONObject;

import android.R.integer;
import android.text.TextUtils;

public class ThirdUser implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int partnerType;//1,qq 2,weixin 
	public String accessToken;
	public String openId;
	public String unionid;
	public String nickName;
	public String expiresTime;
	public String pay_token;
	public String avatar;
	
	public ThirdUser() {
		// TODO Auto-generated constructor stub
	}
	
	public ThirdUser(int type,JSONObject json) {
		// TODO Auto-generated constructor stub
		partnerType=type;
		if(type==1){
			accessToken=json.optString("access_token");
			expiresTime=json.optString("expires_in");
			openId=json.optString("openid");
			pay_token=json.optString("pay_token");
			avatar=json.optString("figureurl_2");
			if(TextUtils.isEmpty(avatar)){
				avatar=json.optString("figureurl_1");
			}
			nickName=json.optString("nickname");
		}else if (type==2) {
			unionid=json.optString("unionid");
			openId=json.optString("openid");
			avatar=json.optString("headimgurl");
			nickName=json.optString("nickname");
		}
		
	}

}

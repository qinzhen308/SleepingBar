package com.bolaa.sleepingbar.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bolaa.sleepingbar.thirdlogin.ThirdCallBack;
import com.bolaa.sleepingbar.thirdlogin.ThirdFactory;
import com.bolaa.sleepingbar.thirdlogin.wechat.WechatLogin;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	WechatLogin wechatLogin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		wechatLogin=(WechatLogin)ThirdFactory.getInstance(ThirdFactory.TYPE_WECHAT);
		wechatLogin.getApi().handleIntent(getIntent(), this);
		Intent intent =getIntent();
	}

	@Override
	public void onReq(BaseReq req) {
		// TODO Auto-generated method stub
		int type=req.getType();
		if(type==ConstantsAPI.COMMAND_SENDAUTH){
		}else if (type==ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
		}
	}

	@Override
	public void onResp(BaseResp resp) {
		
		int type=resp.getType();
		if(type==ConstantsAPI.COMMAND_SENDAUTH){
//			AppUtil.showToast(this, "回调--ConstantsAPI.COMMAND_SENDAUTH="+ConstantsAPI.COMMAND_SENDAUTH);
//			handleLogin(resp);
			wechatLogin.handleLogin(resp,new ThirdCallBack() {
				
				@Override
				public void onSuccess(Object obj) {
					// TODO Auto-generated method stub
					finish();
				}
				
				@Override
				public void onFailed(Object obj) {
					// TODO Auto-generated method stub
					finish();
				}
				
				@Override
				public void onCacel() {
					// TODO Auto-generated method stub
					finish();
				}
			});
		}else if (type==ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
			AppUtil.showToast(this, "回调--ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX="+ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX);
		}else if (type == ConstantsAPI.COMMAND_PAY_BY_WX) {
			Log.d("微信支付", "onPayFinish,errCode=" + resp.errCode);
			
		}

	}
	
}

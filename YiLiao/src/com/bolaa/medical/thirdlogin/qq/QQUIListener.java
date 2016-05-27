package com.bolaa.medical.thirdlogin.qq;

import com.bolaa.medical.thirdlogin.ThirdCallBack;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class QQUIListener implements IUiListener{
	ThirdCallBack callBack;
	
	public QQUIListener(ThirdCallBack callback){
		callBack=callback;
	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		if(callBack!=null){
			callBack.onCacel();
		}
	}

	@Override
	public void onComplete(Object arg0) {
		// TODO Auto-generated method stub
		if(callBack!=null){
			callBack.onSuccess(arg0);
		}
	}

	@Override
	public void onError(UiError arg0) {
		// TODO Auto-generated method stub
		if(callBack!=null){
			callBack.onFailed(arg0);
		}
	}

}

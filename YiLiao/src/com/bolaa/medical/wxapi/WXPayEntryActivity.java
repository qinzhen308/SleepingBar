package com.bolaa.medical.wxapi;

import com.bolaa.medical.common.AppStatic;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		api = WXAPIFactory.createWXAPI(this,AppStatic.WX_APPID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		String erroString = "";
		if (0 == resp.errCode) {
			erroString = "支付成功！";
		} else if (-1 == resp.errCode) {
			erroString = "支付失败！";
		} else if (-2 == resp.errCode) {
			erroString = "您已取消了支付！";
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("支付结果通知");
		builder.setMessage(erroString);
		builder.setInverseBackgroundForced(true);
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendBroadcast(new Intent().setAction("colsePay"));
				dialog.dismiss();
				finish();
			}
		});
		builder.create().show();
	}

}
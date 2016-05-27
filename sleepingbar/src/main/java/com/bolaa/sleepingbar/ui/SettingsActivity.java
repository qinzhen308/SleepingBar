package com.bolaa.sleepingbar.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseActivity;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.DialogUtil;

public class SettingsActivity extends BaseActivity{
//	TextView tvAbout;
//	TextView tvContact;
//	TextView tvServerRule;
	ListView lvSettings;
	TextView btnLogout;
	ImageView ivAvatar;
//	SettingListAdapter mAdapter;
	
	private Dialog mLogoutDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
	}

	private void setExtra() {
		// TODO Auto-generated method stub
		
	}

	private void initView() {
		// TODO Auto-generated method stub
		setActiviyContextView(R.layout.activtiy_settings, false, true);
		setTitleText("", "设置", 0, true);
//		tvAbout=(TextView)findViewById(R.id.tv_about_us);
//		tvContact=(TextView)findViewById(R.id.tv_contact_us);
//		tvServerRule=(TextView)findViewById(R.id.tv_server_rule);
		lvSettings=(ListView)findViewById(R.id.lv_settings);
//		mAdapter=new SettingListAdapter(this);
//		mAdapter.setList(HApplication.getInstance().settingList);
//		lvSettings.setAdapter(mAdapter);
		btnLogout=(TextView)findViewById(R.id.btn_logout);
		ivAvatar=(ImageView)findViewById(R.id.iv_avatar);
		if(AppStatic.getInstance().isLogin&&AppStatic.getInstance().getmUserInfo()!=null){
			Image13Loader.getInstance().loadImage(AppStatic.getInstance().getmUserInfo().avatar, ivAvatar,R.drawable.ic_user);
			btnLogout.setVisibility(View.VISIBLE);
		}else {
			btnLogout.setVisibility(View.GONE);
			ivAvatar.setImageResource(R.drawable.ic_user);
		}
	}

	private void setListener() {
		// TODO Auto-generated method stub
//		tvAbout.setOnClickListener(this);
//		tvContact.setOnClickListener(this);
//		tvServerRule.setOnClickListener(this);
		btnLogout.setOnClickListener(this);
		
	}
	
	/**
	 * 退出登录
	 */
	private void logout() {
		if (mLogoutDialog != null) {
			mLogoutDialog.dismiss();
		}
		DialogUtil.showDialog(lodDialog);
		NetworkWorker.getInstance().post(AppUrls.getInstance().URL_LOGOUT, new ICallback() {

			@Override
			public void onResponse(int status, String result) {
				DialogUtil.dismissDialog(lodDialog);
				if(status==200){
					BaseObject<Object> object=GsonParser.getInstance().parseToObj(result, Object.class);
					if(object!=null){
						if(object.data!=null&&object.status==BaseObject.STATUS_OK){
							AppUtil.showToast(getApplicationContext(), "安全退出");
							AppStatic.getInstance().isLogin = false;
							PreferencesUtils.putBoolean("isLogin", false);
							AppStatic.getInstance().setmUserInfo(null);
							AppStatic.getInstance().clearUser();
							ivAvatar.setImageResource(R.drawable.ic_user);
							btnLogout.setVisibility(View.GONE);
							MainActivity.invoke(SettingsActivity.this);
						}else {
							AppUtil.showToast(getApplicationContext(), object.msg);
						}
					}else {
						AppUtil.showToast(getApplicationContext(), "请检查网络");
					}
				}else {
					AppUtil.showToast(getApplicationContext(), "请检查网络");
				}
			}
		}, new HttpRequester());

	}

	/**
	 * 显示退出登录
	 */
	private void showLogoutDialog() {
		if (mLogoutDialog == null) {
			View logoutView = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);
			logoutView.findViewById(R.id.dialog_logout_cancelBtn).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!isFinishing())mLogoutDialog.dismiss();
				}
			});
			logoutView.findViewById(R.id.dialog_logout_okBtn).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					logout();
				}
			});
			mLogoutDialog = DialogUtil.getCenterDialog(this, logoutView);
			mLogoutDialog.show();
		} else {
			mLogoutDialog.show();
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v==btnLogout) {
			showLogoutDialog();
		}else {
			super.onClick(v);
		}
	}
	
	public static void invoke(Context context){
		Intent intent=new Intent(context,SettingsActivity.class);
		context.startActivity(intent);
	}

}

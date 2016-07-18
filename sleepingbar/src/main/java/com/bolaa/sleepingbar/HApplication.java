package com.bolaa.sleepingbar;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.common.GlobeFlags;
import com.bolaa.sleepingbar.httputil.HttpRequester;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.RegionInfo;
import com.bolaa.sleepingbar.model.RegoinWrapper;
import com.bolaa.sleepingbar.model.Sleep;
import com.bolaa.sleepingbar.model.UserInfo;
import com.bolaa.sleepingbar.model.tables.RegionTable;
import com.bolaa.sleepingbar.model.tables.SleepTable;
import com.bolaa.sleepingbar.model.tables.StepTable;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.ui.MainActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;
import com.bolaa.sleepingbar.utils.ShareUtil;
import com.bolaa.sleepingbar.watch.WatchConstant;
import com.bolaa.sleepingbar.watch.WatchService;
import com.bolaa.sleepingbar.watch.WatchUploadReceiver;
import com.bolaa.sleepingbar.watch.WatchUploadService;
import com.core.framework.app.MyApplication;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.develop.LogUtil;
import com.core.framework.image.universalimageloader.core.ImageLoader;
import com.core.framework.net.NetworkWorker;
import com.core.framework.store.DB.beans.Preferences;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.DialogUtil;
import com.core.framework.util.StringUtil;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class HApplication extends MyApplication {
	public String token="";
	private static HApplication instance;

	public String push_regestion_id;//推送服务唯一表示



	public Location mLocation;

	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance=this;
	}
	
	public static HApplication getInstance(){
		return instance;
	}

	private void initPushService(){
		if(getCurProcessName().equals("com.bolaa.sleepingbar")){
			JPushInterface.setDebugMode(true);
			JPushInterface.init(this);
			push_regestion_id=PreferencesUtils.getString(GlobeFlags.FLAG_PUSH_REGISTION_ID);
		}
	}
	
	public void saveToken(String token){
		this.token=token;
		PreferencesUtils.putString(AppStatic.ACCESS_TOKEN, token);
	}


	@Override
	public void checkService() {
	}

	@Override
	public void doBackTransaction() {
		ScreenUtil.setContextDisplay(this);
		initDatabase();
//		getRegionInBg();
		try{
			//获取经纬度
			getLocation();
		}catch (Exception e){
			e.printStackTrace();
		}
        loadUserInfo();
		synchStep();
	}

	@Override
	public void doBusyTransaction() {
		Image13Loader.getInstance().resume();
		CrashReport.initCrashReport(getApplicationContext(), "900020594", false);
		AppStatic.getInstance().isLogin = PreferencesUtils.getBoolean("isLogin");
		token = PreferencesUtils.getString(AppStatic.ACCESS_TOKEN);
		AppStatic.getInstance().setmUserInfo(AppStatic.getInstance().getUser());
		ShareUtil.initShareData();
		initPushService();
		WatchUploadService.setAlarm(getApplicationContext());
	}

	private void initDatabase() {
		Preferences.getInstance();
		SleepTable.getInstance().init();
		StepTable.getInstance().init();
	}
	
	public void getRegionInBg(){
		ParamBuilder params=new ParamBuilder();
		long region_updatetime=PreferencesUtils.getLong("region_updatetime");
		params.append("region_updatetime", region_updatetime);
		try {
			String result=NetworkWorker.getInstance().getSync(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_REGION_ALL));
			BaseObject<RegoinWrapper> baseObject=GsonParser.getInstance().parseToObj(result, RegoinWrapper.class);
			if(baseObject!=null&&baseObject.status==BaseObject.STATUS_OK&&baseObject.data!=null){
				if(AppUtil.isNull(baseObject.data.region_updatetime)){
					baseObject.data.region_updatetime="0";
				}
				//Long.valueOf(baseObject.data.region_updatetime)>region_updatetime&&
				if(!AppUtil.isEmpty(baseObject.data.region_list)){
					RegionTable.getInstance().saveList(baseObject.data.region_list);
					int count=RegionTable.getInstance().getCount();
					LogUtil.d("region----size="+count);
					if(count>0){
						PreferencesUtils.putLong("region_updatetime", Long.valueOf(baseObject.data.region_updatetime));
//						testRegionTable();
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testRegionTable(){
		List<RegionInfo> list=RegionTable.getInstance().getListByParentId(0);
		for(int i=0;i<list.size();i++){
			RegionInfo r=list.get(i);
			LogUtil.d("test---region--"+r.parent_id+"--"+r.region_id+"--"+r.region_name);
		}
		List<RegionInfo> list0child=RegionTable.getInstance().getListByParentId(list.get(0).region_id);
		for(int i=0;i<list0child.size();i++){
			RegionInfo r=list0child.get(i);
			LogUtil.d("test---region--"+r.parent_id+"--"+r.region_id+"--"+r.region_name);
		}
	}

	private void getLocation(){
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String locationProvider=null;
		//获取所有可用的位置提供器
		List<String> providers = locationManager.getProviders(true);
		if(providers.contains(LocationManager.GPS_PROVIDER)){
			//如果是GPS
			locationProvider = LocationManager.GPS_PROVIDER;
		}else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
			//如果是Network
			locationProvider = LocationManager.NETWORK_PROVIDER;
		}else{
			return ;
		}
		//获取Location
		Location location = locationManager.getLastKnownLocation(locationProvider);
		if(location!=null){
			//不为空,显示地理位置经纬度
		}else {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,2000,5,locationListener,getMainLooper());
		}
		mLocation=location;
	}

	private LocationListener locationListener=new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			mLocation=location;
			if(mLocation!=null){
				LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				locationManager.removeUpdates(locationListener);
				locationListener=null;
				synchLocation();
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}
	};


	public boolean isAppOnForeground() {
		ActivityManager mActivityManager = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
		String mPackageName = getPackageName();
		List<ActivityManager.RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if (mPackageName.equals(tasksInfo.get(0).topActivity
					.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 上传push的registration id到服务器
	 * @param id
	 */
	public void uploadRegistrationId(String id){
		if(!AppStatic.getInstance().isLogin)return;
		HttpRequester requester=new HttpRequester();
		requester.getParams().put("mobile_key", id);

		NetworkWorker.getInstance().post(AppUrls.getInstance().URL_BIND_PUSH_INFO, new NetworkWorker.ICallback() {

			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if(status==200){
					LogUtil.d("jpush---bind push info="+result);
				}
			}
		},requester);
	}

	/**
	 * 上传push的registration id到服务器
	 */
	public void uploadWatchMacAddress(String macName,String macAddress){
		if(!AppStatic.getInstance().isLogin)return;
		HttpRequester requester=new HttpRequester();
		requester.getParams().put("equipment", macName);
		requester.getParams().put("mac", macAddress);

		NetworkWorker.getInstance().post(AppUrls.getInstance().URL_BIND_WATCH_MAC_ADDRESS, new NetworkWorker.ICallback() {

			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if(status==200){
					LogUtil.d("watch---bind watch="+result);
					AppUtil.showToast(getInstance(),"绑定手环成功！");

				}
			}
		},requester);
	}

    public void loadUserInfo() {
        ParamBuilder params=new ParamBuilder();
        NetworkWorker.getInstance().getCallbackInBg(APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_GET_USER_INFO), new NetworkWorker.ICallback() {

            @Override
            public void onResponse(int status, String result) {
                if(status==200){
                    BaseObject<UserInfo> object= GsonParser.getInstance().parseToObj(result, UserInfo.class);
                    if(object!=null){
                        if(object.data!=null&&object.status==BaseObject.STATUS_OK){
                            AppStatic.getInstance().setmUserInfo(object.data);
                            AppStatic.getInstance().saveUser(object.data);
                        }else {
                        }
                    }else {

                    }
                }

            }
        });
    }


	public void autoConnectedWatch(){
		String macAddress=PreferencesUtils.getString(WatchService.FLAG_CURRENT_DEVICE_ADDRESS);
		if(AppUtil.isNull(macAddress)){
			return;
		}
		Intent intent = new Intent(this, WatchService.class);
		intent.putExtra(WatchService.FLAG_CURRENT_DEVICE_ADDRESS,macAddress);
		startService(intent);
	}

	public void stopWatchService(Context context){
		//删掉缓存的mac地址
		PreferencesUtils.remove(WatchService.FLAG_CURRENT_DEVICE_ADDRESS);
		PreferencesUtils.remove(WatchService.FLAG_CURRENT_DEVICE_NAME);
		//停止蓝牙服务
		stopService(new Intent(context, WatchService.class));
	}

	public void synchStep(){
		HttpRequester requester=new HttpRequester();
		requester.getParams().put("walk_data",PreferencesUtils.getString(WatchConstant.FLAG_STEP_CACHE));
		NetworkWorker.getInstance().postCallbackInBg(AppUrls.getInstance().URL_WATCH_SYNC_STEP, new NetworkWorker.ICallback() {
			@Override
			public void onResponse(int status, String result) {
				if(status==200){
					BaseObject<Object> obj=GsonParser.getInstance().parseToObj(result,Object.class);
					if(obj!=null&&obj.status==BaseObject.STATUS_OK){
						PreferencesUtils.remove(WatchConstant.FLAG_STEP_CACHE);
					}
				}
			}
		},requester);

	}

	public void synchLocation() {
		ParamBuilder params=new ParamBuilder();
		params.append("lat",""+mLocation.getLatitude());
		params.append("lng",""+mLocation.getLongitude());
		NetworkWorker.getInstance().getCallbackInBg(APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_SYNCH_LOCATION), new NetworkWorker.ICallback() {

			@Override
			public void onResponse(int status, String result) {
				if(status==200){
					BaseObject<UserInfo> object= GsonParser.getInstance().parseToObj(result, UserInfo.class);
					if(object!=null){
						if(object.data!=null&&object.status==BaseObject.STATUS_OK){
							LogUtil.d("同步经纬度---result="+result);
						}else {
						}
					}else {

					}
				}

			}
		});
	}



}

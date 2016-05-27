package com.bolaa.medical;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.util.List;

import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppStatic;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.model.Article;
import com.bolaa.medical.model.HelpContent;
import com.bolaa.medical.model.RegionInfo;
import com.bolaa.medical.model.RegoinWrapper;
import com.bolaa.medical.model.tables.RegionTable;
import com.bolaa.medical.parser.gson.BaseObject;
import com.bolaa.medical.parser.gson.BaseObjectList;
import com.bolaa.medical.parser.gson.GsonParser;
import com.bolaa.medical.utils.AppUtil;
import com.bolaa.medical.utils.Image13Loader;
import com.core.framework.app.MyApplication;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.store.DB.beans.Preferences;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.tencent.bugly.crashreport.CrashReport;

public class HApplication extends MyApplication {
	public String token="";
	private static HApplication instance;
	
	public List<Article> settingList;
	public HelpContent scoreHelp;
	public HelpContent cashHelp;

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
	
	public void saveToken(String token){
		if(!AppUtil.isNull(token)&&(!token.equals(this.token)||this.token==""||this.token.length()==0)){
			this.token=token;
			PreferencesUtils.putString(AppStatic.ACCESS_TOKEN, token);
		}
	}

	@Override
	public void checkService() {

	}

	@Override
	public void doBackTransaction() {
		ScreenUtil.setContextDisplay(this);
		initDatabase();
		getSettingList();
		getRegionInBg();
		getHelpContent();
		try{
			//获取经纬度
			getLocation();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void doBusyTransaction() {
		Image13Loader.getInstance().resume();
		CrashReport.initCrashReport(getApplicationContext(), "900020594", false);
		AppStatic.getInstance().isLogin = PreferencesUtils.getBoolean("isLogin");
		token = PreferencesUtils.getString(AppStatic.ACCESS_TOKEN);
		AppStatic.getInstance().setmUserInfo(AppStatic.getInstance().getUser());

	}

	private void initDatabase() {
		Preferences.getInstance();
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
	
	public void getSettingList(){
		ParamBuilder params=new ParamBuilder();
		try {
			String result=NetworkWorker.getInstance().getSync(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SETTING_LIST));
			BaseObjectList<Article> baseObject=GsonParser.getInstance().parseToObj4List(result, Article.class);
			if(baseObject!=null&&baseObject.status==BaseObject.STATUS_OK&&baseObject.data!=null){
				settingList=baseObject.data;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 健康积分说明和提现说明的内容，启动应用的时候提前获取
	 */
	public void getHelpContent(){
		ParamBuilder params=new ParamBuilder();
		try {
			String result=NetworkWorker.getInstance().getSync(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SCORE_HELP));
			BaseObject<HelpContent> baseObject=GsonParser.getInstance().parseToObj(result, HelpContent.class);
			if(baseObject!=null&&baseObject.status==BaseObject.STATUS_OK&&baseObject.data!=null){
				scoreHelp=baseObject.data;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			String result=NetworkWorker.getInstance().getSync(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_WITHDRAW_HELP));
			BaseObject<HelpContent> baseObject=GsonParser.getInstance().parseToObj(result, HelpContent.class);
			if(baseObject!=null&&baseObject.status==BaseObject.STATUS_OK&&baseObject.data!=null){
				cashHelp=baseObject.data;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		}
		mLocation=location;
	}
	
	
}

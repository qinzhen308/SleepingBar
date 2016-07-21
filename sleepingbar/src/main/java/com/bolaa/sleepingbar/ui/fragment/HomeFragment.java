package com.bolaa.sleepingbar.ui.fragment;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseFragment;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppStatic;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.HomeSleepInfo;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.ui.CommonWebActivity;
import com.bolaa.sleepingbar.ui.FundsRankinglistActivity;
import com.bolaa.sleepingbar.ui.MyMedalActivity;
import com.bolaa.sleepingbar.ui.QuickBindWXActivity;
import com.bolaa.sleepingbar.ui.SleepTrendActivity;
import com.bolaa.sleepingbar.ui.SupporterActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.DateUtil;
import com.bolaa.sleepingbar.utils.ShareUtil;
import com.bolaa.sleepingbar.watch.TipUtil;
import com.bolaa.sleepingbar.watch.WatchConstant;
import com.bolaa.sleepingbar.watch.WatchService;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.store.sharePer.PreferencesUtils;
import com.core.framework.util.MD5Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


/**
 * 首页
 * 
 * @author paulz
 * 
 */
public class HomeFragment extends BaseFragment implements OnClickListener {


	private TextView tvNeedFriends;
	private TextView tvNeedFriends2;

	private TextView tvFundsHelp;
	private TextView tvFunds;
	private TextView tvFundsGot;
	private TextView tvSupport;
	private TextView tvRankinglist;
	private TextView tvSleepTrend;
	private TextView tvMedal;
	private TextView tvSleepQuanlity;
	private TextView tvDeepSleep;
	private TextView tvLightSleep;
	private TextView tvSleepTip;
	private TextView tvSleepDate;

	private TextView tvWalk;
	private TextView tvRun;
	private TextView tvDistance;
	private TextView tvCalorie;
	private TextView tvStep;
	private TextView tvStepEvaluate;
	private TextView tvStepTip;

	private View layoutSupport;
	private View layoutRankinglist;
	private View layoutSleepTrend;
	private View layoutMedal;

	private HomeSleepInfo sleepInfo;

	private boolean isLoading;

	@Override
	public void onResume() {

		super.onResume();
		IntentFilter filter=new IntentFilter();
		filter.addAction(WatchConstant.ACTION_WATCH_UPDATE_STEP);
		filter.addAction(WatchConstant.ACTION_WATCH_UPDATE_RUN);
		filter.addAction(WatchConstant.ACTION_WATCH_CONNECTED_SUCCESS_NOTIFY_HOME);
		getActivity().registerReceiver(mReceiver,filter);
	}

	@Override
	public void heavyBuz() {
		loadSleepInfo();
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(mReceiver);
	}


	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		setView(inflater, R.layout.fragment_home, false);
		initView();
		setListener();
		return baseLayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// initData(false);
		loadSleepInfo();
		if(AppUtil.isNull(PreferencesUtils.getString(WatchService.FLAG_CURRENT_DEVICE_ADDRESS))){
			tvStepTip.setText(TipUtil.getStepTip(-1));
			tvStepEvaluate.setText(TipUtil.getStepEvaluate(-1));
		}else {
			tvStepTip.setText(TipUtil.getStepTip(0));
			tvStepEvaluate.setText(TipUtil.getStepEvaluate(0));
			getStepAtLast();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		cacheStep();
	}

	public void initView() {
		layoutSupport=baseLayout.findViewById(R.id.layout_funds_supporter);
		layoutRankinglist=baseLayout.findViewById(R.id.layout_sleep_ranklist);
		layoutSleepTrend=baseLayout.findViewById(R.id.layout_sleep_trend);
		layoutMedal=baseLayout.findViewById(R.id.layout_my_medal);
		tvMedal=(TextView) baseLayout.findViewById(R.id.tv_my_medal);
		tvSupport=(TextView) baseLayout.findViewById(R.id.tv_funds_supporter);
		tvRankinglist=(TextView) baseLayout.findViewById(R.id.tv_sleep_ranklist);
		tvSleepTrend=(TextView) baseLayout.findViewById(R.id.tv_sleep_trend);

		tvWalk=(TextView) baseLayout.findViewById(R.id.tv_step_walk);
		tvRun=(TextView) baseLayout.findViewById(R.id.tv_step_run);
		tvDistance=(TextView) baseLayout.findViewById(R.id.tv_distance);
		tvCalorie=(TextView) baseLayout.findViewById(R.id.tv_calorie);

		tvStep=(TextView) baseLayout.findViewById(R.id.tv_step);
		tvFunds=(TextView) baseLayout.findViewById(R.id.tv_sleep_funds);
		tvNeedFriends=(TextView) baseLayout.findViewById(R.id.tv_need_friends);
		tvNeedFriends2=(TextView) baseLayout.findViewById(R.id.tv_need_friends2);
		tvFundsHelp=(TextView) baseLayout.findViewById(R.id.tv_funds_help);
		tvSleepDate=(TextView) baseLayout.findViewById(R.id.tv_sleep_date);
		tvStepEvaluate=(TextView) baseLayout.findViewById(R.id.tv_step_evaluate);
		tvSleepQuanlity=(TextView) baseLayout.findViewById(R.id.tv_sleep_quality);
		tvDeepSleep=(TextView) baseLayout.findViewById(R.id.tv_sleep_deep_time);
		tvLightSleep=(TextView) baseLayout.findViewById(R.id.tv_sleep_light_time);
		tvSleepTip=(TextView) baseLayout.findViewById(R.id.tv_sleep_quality_tip);
		tvStepTip=(TextView) baseLayout.findViewById(R.id.tv_step_quanlity_tip);
		tvFundsGot=(TextView) baseLayout.findViewById(R.id.tv_sleep_funds_got);

	}

	private void setListener() {
		layoutRankinglist.setOnClickListener(this);
		layoutSleepTrend.setOnClickListener(this);
		layoutSupport.setOnClickListener(this);
		layoutMedal.setOnClickListener(this);
		tvNeedFriends.setOnClickListener(this);
		tvNeedFriends2.setOnClickListener(this);
		tvFundsHelp.setOnClickListener(this);

	}

	private void loadSleepInfo() {
		if(isLoading)return;
		isLoading=true;
		showLoading();
		ParamBuilder params=new ParamBuilder();

		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_HOME), new NetworkWorker.ICallback() {

			@Override
			public void onResponse(int status, String result) {
				// TODO Auto-generated method stub
				if (status == 200) {
					BaseObject<HomeSleepInfo> object = GsonParser.getInstance().parseToObj(result,HomeSleepInfo.class);
					if (object != null && object.status == BaseObject.STATUS_OK && object.data != null) {
						sleepInfo=object.data;
						setSleepInfo();
					} else {
						AppUtil.showToast(getActivity(),object!=null?object.info:"解析失败");
					}
				} else {
					AppUtil.showToast(getActivity(),"请求失败");
				}
				isLoading=false;
			}
		});

	}

	private void setSleepInfo(){
		tvSleepDate.setText(sleepInfo.sleep_date);
		tvFunds.setText(sleepInfo.sleep_fund);
		tvFundsGot.setText("已累计收货基金"+sleepInfo.got_fund+"元");
		tvSleepTip.setText(sleepInfo.desc);
		tvSleepQuanlity.setText(sleepInfo.quality_rating);
		tvDeepSleep.setText(sleepInfo.deep_time);
		tvLightSleep.setText(sleepInfo.no_deep_time);
		tvSupport.setText(sleepInfo.support_num);
		tvMedal.setText(sleepInfo.medal_num);
		tvRankinglist.setText(sleepInfo.sleep_rank);
		tvSleepTrend.setText(sleepInfo.quality_rating);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==layoutMedal){
			MyMedalActivity.invoke(getActivity());
		}else if(v==layoutRankinglist){
			FundsRankinglistActivity.invoke(getActivity());
		}else if(v==layoutSupport){
			SupporterActivity.invoke(getActivity());
		}else if(v==layoutSleepTrend){
			SleepTrendActivity.invoke(getActivity());
		}else if(v==tvNeedFriends){
            ShareUtil shareUtil=new ShareUtil(getActivity(),"睡眠分享", "比比看",sleepInfo.sleep_info_url);
			shareUtil.showShareDialog();
		}else if(v==tvNeedFriends2){
			ParamBuilder params= new ParamBuilder();
			params.clear();
            String info =tvWalk.getText().toString()+"_"+tvRun.getText().toString()+"_"+tvDistance.getText().toString()+"_"+tvCalorie.getText().toString();
			params.append("info",info);
			params.append("id", AppStatic.getInstance().getmUserInfo().user_id);
			params.append("s", MD5Util.getMD5(info+"_iphone_android"));
			ShareUtil shareUtil=new ShareUtil(getActivity(),"运动分享", "比比看",APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_MOVEMENT_SHARE));
			shareUtil.showShareDialog();
		}else if(v==tvFundsHelp){
			CommonWebActivity.invoke(getActivity(),AppUrls.getInstance().URL_SLEEP_HELP,"睡眠基金说明");
		}
	}

	private BroadcastReceiver mReceiver=new BroadcastReceiver() {
		int stepTotal;
		int run;
		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if(WatchConstant.ACTION_WATCH_UPDATE_STEP.equals(action)){
				shouldCahe=true;
				int[] stepInfo=intent.getIntArrayExtra(WatchConstant.FLAG_STEP_INFO);
				stepTotal=stepInfo[2];
				tvStep.setText(""+stepInfo[2]);
				tvCalorie.setText(""+stepInfo[4]);
				tvDistance.setText(""+(Double.valueOf(stepInfo[3]+"")/1000));
				tvWalk.setText(""+(stepTotal-run));
				tvStepTip.setText(TipUtil.getStepTip(stepInfo[3]));
				tvStepEvaluate.setText(TipUtil.getStepEvaluate(stepInfo[3]));
			}else if(WatchConstant.ACTION_WATCH_UPDATE_RUN.equals(action)){
				int[] runInfo=intent.getIntArrayExtra(WatchConstant.FLAG_RUN_INFO);
				run=runInfo[2];
				tvRun.setText(""+runInfo[2]);
			}else if(WatchConstant.ACTION_WATCH_CONNECTED_SUCCESS_NOTIFY_HOME.equals(action)){
				if(AppUtil.isNull(PreferencesUtils.getString(WatchService.FLAG_CURRENT_DEVICE_ADDRESS))){
					tvStepTip.setText(TipUtil.getStepTip(-1));
					tvStepEvaluate.setText(TipUtil.getStepEvaluate(-1));
				}else {
					tvStepTip.setText(TipUtil.getStepTip(0));
					tvStepEvaluate.setText(TipUtil.getStepEvaluate(0));
					getStepAtLast();
				}
			}
		}
	};

	private boolean shouldCahe;
	public void cacheStep(){
		if(!shouldCahe)return;
		JSONObject walk_data=new JSONObject();
		try {
            String uinfoid=PreferencesUtils.getString("user_id");
            if(AppUtil.isNull(uinfoid))return;
            String mac=PreferencesUtils.getString(WatchService.FLAG_CURRENT_DEVICE_ADDRESS);
            LogUtil.d("cacheStep----mac="+mac);
            if(AppUtil.isNull(mac))return;
            String calorie=tvCalorie.getText().toString();
            String kilometre=tvDistance.getText().toString();
            String walk_total=tvStep.getText().toString();
            String run_total=tvRun.getText().toString();
            String equipment= PreferencesUtils.getString(WatchService.FLAG_CURRENT_DEVICE_NAME);
            String date= DateUtil.getYMD_GMTDate(new Date());
            String sign=MD5Util.getMD5(uinfoid.concat("#").concat(date).concat("#").concat(walk_total).concat("#").concat(run_total).concat("#").concat(kilometre).concat("#").concat(calorie).concat("#").concat(equipment).concat("#").concat(mac).concat("_iphone_android_@2016y"));
			walk_data.putOpt("date", date);
			walk_data.putOpt("equipment", equipment);
			walk_data.putOpt("mac",mac);
			walk_data.putOpt("run_total",run_total);
			walk_data.putOpt("walk_total",walk_total);
			walk_data.putOpt("kilometre",kilometre);
			walk_data.putOpt("calorie",calorie);
			walk_data.putOpt("uinfoid",uinfoid);
			walk_data.putOpt("sign",sign);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String walk_data_str=walk_data.toString();
		PreferencesUtils.putString(WatchConstant.FLAG_STEP_CACHE,walk_data_str);
		PreferencesUtils.putString(WatchConstant.FLAG_STEP_CACHE_FOR_LOOK,walk_data_str);
	}

	private void getStepAtLast(){
		String cur_address=PreferencesUtils.getString(WatchService.FLAG_CURRENT_DEVICE_ADDRESS);
		if(AppUtil.isNull(cur_address))return;
		String walk_data=PreferencesUtils.getString(WatchConstant.FLAG_STEP_CACHE_FOR_LOOK);
		if(AppUtil.isNull(walk_data))return;
		JSONObject jsonObject= null;
		try {
			jsonObject = new JSONObject(walk_data);
			if(!cur_address.equals(jsonObject.optString("mac")))return;
			int run_total=jsonObject.optInt("run_total");
			int walk_total=jsonObject.optInt("walk_total");
			double kilometre=jsonObject.optDouble("kilometre");
			String calorie=jsonObject.optString("walk_total");

			tvStep.setText(""+walk_total);
			tvCalorie.setText(""+calorie);
			tvDistance.setText(""+kilometre);
			tvWalk.setText(""+(walk_total-run_total));
			tvStepTip.setText(TipUtil.getStepTip((int)(kilometre*1000)));
			tvStepEvaluate.setText(TipUtil.getStepEvaluate((int)(kilometre*1000)));
			tvRun.setText(""+run_total);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


}

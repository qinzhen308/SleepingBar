package com.bolaa.sleepingbar.ui.fragment;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseFragment;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.HomeSleepInfo;
import com.bolaa.sleepingbar.model.Supporter;
import com.bolaa.sleepingbar.model.Topic;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.ui.FundsRankinglistActivity;
import com.bolaa.sleepingbar.ui.MyMedalActivity;
import com.bolaa.sleepingbar.ui.QuickBindWXActivity;
import com.bolaa.sleepingbar.ui.QuickBindWatchActivity;
import com.bolaa.sleepingbar.ui.SleepTrendActivity;
import com.bolaa.sleepingbar.ui.SupporterActivity;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Constants;
import com.bolaa.sleepingbar.utils.ShareUtil;
import com.bolaa.sleepingbar.watch.TipUtil;
import com.bolaa.sleepingbar.watch.WatchConstant;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.NetworkWorker;
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
            ParamBuilder params= new ParamBuilder();
            params.clear();
//            String info =tvWalk.getText().toString()+"_"+tvRun.getText().toString()+"_"+tvDistance.getText().toString()+"_"+tvCalorie.getText().toString();
//            params.append("info",info);
//            params.append("s", MD5Util.getMD5(info+"_iphone_android"));
            ShareUtil shareUtil=new ShareUtil(getActivity(),"睡眠分享", "比比看",APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_SLEEP_SHARE));
			shareUtil.showShareDialog();
		}else if(v==tvNeedFriends2){
			ParamBuilder params= new ParamBuilder();
			params.clear();
            String info =tvWalk.getText().toString()+"_"+tvRun.getText().toString()+"_"+tvDistance.getText().toString()+"_"+tvCalorie.getText().toString();
			params.append("info",info);
			params.append("s", MD5Util.getMD5(info+"_iphone_android"));
			ShareUtil shareUtil=new ShareUtil(getActivity(),"运动分享", "比比看",APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_MOVEMENT_SHARE));
			shareUtil.showShareDialog();
		}
	}

	private BroadcastReceiver mReceiver=new BroadcastReceiver() {
		int stepTotal;
		int run;
		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if(WatchConstant.ACTION_WATCH_UPDATE_STEP.equals(action)){
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
			}
		}
	};
}

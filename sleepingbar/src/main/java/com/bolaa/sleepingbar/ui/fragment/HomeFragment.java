package com.bolaa.sleepingbar.ui.fragment;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseFragment;
import com.bolaa.sleepingbar.model.Supporter;
import com.bolaa.sleepingbar.ui.FundsRankinglistActivity;
import com.bolaa.sleepingbar.ui.MyMedalActivity;
import com.bolaa.sleepingbar.ui.SleepTrendActivity;
import com.bolaa.sleepingbar.ui.SupporterActivity;

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

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void heavyBuz() {

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

		}
	}
}

package com.bolaa.medical.ui;

import java.util.List;

import com.bolaa.medical.HApplication;
import com.bolaa.medical.R;
import com.bolaa.medical.adapter.HospitalListAdapter;
import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.controller.LoadStateController.OnLoadErrorListener;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.model.Hospital;
import com.bolaa.medical.model.wrapper.BeanWraper;
import com.bolaa.medical.model.wrapper.HospitalWraper;
import com.bolaa.medical.utils.AppUtil;
import com.bolaa.medical.view.pulltorefresh.PullListView;
import com.bolaa.medical.view.pulltorefresh.PullToRefreshBase;
import com.bolaa.medical.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MakingAppointmentActivity extends BaseListActivity implements OnRefreshListener,OnLoadErrorListener{
	private int cityId;
	private int districtId;
	private int provinceId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
		initData(false);
	}
	
	private void setExtra(){
		
	}
	
	private void initView(){
		setActiviyContextView(R.layout.activity_make_appointment, true, true);
		setTitleTextRightText("", "开始预约", "选择地区", true);
		mPullListView=(PullListView)findViewById(R.id.pull_listview);
		mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
		mListView=mPullListView.getRefreshableView();
		mAdapter=new HospitalListAdapter(this);
		mListView.setAdapter(mAdapter);
	}
	
	private void setListener(){
		mListView.setOnScrollListener(new MyOnScrollListener());
		mPullListView.setOnRefreshListener(this);
	}
	
	private void initData(boolean isRefresh){
		if(!isRefresh){
			showLoading();
		}
		ParamBuilder params=new ParamBuilder();
		params.append("province", provinceId);
		params.append("city", cityId);
		params.append("district", districtId);
		//is_best  0一般医疗机构  1推荐机构  -1全部
		params.append("is_best", -1);
		if(HApplication.getInstance().mLocation!=null){
			params.append("lat",HApplication.getInstance().mLocation.getLatitude());
			params.append("lng",HApplication.getInstance().mLocation.getLongitude());
		}

		if(isRefresh){
			immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_HOSPITAL_LIST), HospitalWraper.class);
		}else {
			reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_HOSPITAL_LIST), HospitalWraper.class);
		}
	}
	
	@Override
	protected BeanWraper newBeanWraper() {
		// TODO Auto-generated method stub
		return new HospitalWraper();
	}
	
	@Override
	public void onRightClick() {
		// TODO Auto-generated method stub
		RegionSelectingActivity.invokeForResult(this);
	}

	@Override
	protected void handlerData(List allData, List currentData, boolean isLastPage) {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
		if(AppUtil.isEmpty(allData)){
			showNodata();
		}else {
			showSuccess();
		}
		mAdapter.setList(allData);
		mAdapter.notifyDataSetChanged();
			
	}

	@Override
	protected void loadError(String message, Throwable throwable, int page) {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
		showFailture();
	}

	@Override
	protected void loadTimeOut(String message, Throwable throwable) {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
		showFailture();
	}

	@Override
	protected void loadNoNet() {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
		showFailture();
	}

	@Override
	protected void loadServerError() {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
		showFailture();
		
	}
	
	public static void invoke(Context context){
		Intent intent=new Intent(context,MakingAppointmentActivity.class);
		context.startActivity(intent);
	}
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if(!isLoading()){
			initData(true);
		}
	}

	@Override
	public void onAgainRefresh() {
		// TODO Auto-generated method stub
		initData(false);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			provinceId=data.getIntExtra("regionId_0",0);
			cityId=data.getIntExtra("regionId_1",0);
			districtId=data.getIntExtra("regionId_2",0);
			initData(false);
		}
	}

}

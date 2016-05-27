package com.bolaa.medical.ui;

import java.util.List;

import com.bolaa.medical.R;
import com.bolaa.medical.adapter.CouponListAdapter;
import com.bolaa.medical.adapter.HospitalListAdapter;
import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.controller.LoadStateController;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.model.wrapper.BeanWraper;
import com.bolaa.medical.model.wrapper.CashDealWraper;
import com.bolaa.medical.model.wrapper.CounponWraper;
import com.bolaa.medical.utils.AppUtil;
import com.bolaa.medical.view.pulltorefresh.PullListView;
import com.bolaa.medical.view.pulltorefresh.PullToRefreshBase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 我的优惠券页面
 */
public class CouponListActivity extends BaseListActivity implements LoadStateController.OnLoadErrorListener, PullToRefreshBase.OnRefreshListener{
	
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
		setActiviyContextView(R.layout.activity_coupon_list, true, true);
		setTitleTextRightText("", "优惠券", "", true);
		mPullListView=(PullListView)findViewById(R.id.pull_listview);
		mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
		mListView=mPullListView.getRefreshableView();
		mAdapter=new CouponListAdapter(this);
		mListView.setAdapter(mAdapter);
	}
	
	private void setListener(){
		mListView.setOnScrollListener(new MyOnScrollListener());
		mLoadStateController.setOnLoadErrorListener(this);
		mPullListView.setOnRefreshListener(this);
	}
	
	private void initData(boolean isRefresh){
		if(!isRefresh){
			showLoading();
		}
		ParamBuilder params=new ParamBuilder();
		if(isRefresh){
			immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_COUNPONS_LIST), CounponWraper.class);
		}else {
			reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_COUNPONS_LIST), CounponWraper.class);
		}
	}


	@Override
	protected BeanWraper newBeanWraper() {
		// TODO Auto-generated method stub
		return new CounponWraper();
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
	
	public static void invoke(Context context){
		Intent intent=new Intent(context,CouponListActivity.class);
		context.startActivity(intent);
	}

}

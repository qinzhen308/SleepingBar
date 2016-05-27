package com.bolaa.medical.ui;

import java.util.List;

import com.bolaa.medical.R;
import com.bolaa.medical.adapter.MedicalReportAdapter;
import com.bolaa.medical.adapter.MedicalReportAdapter.DialogStateListener;
import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.controller.LoadStateController.OnLoadErrorListener;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.model.wrapper.BeanWraper;
import com.bolaa.medical.model.wrapper.OrderWraper;
import com.bolaa.medical.utils.AppUtil;
import com.bolaa.medical.view.pulltorefresh.PullListView;
import com.bolaa.medical.view.pulltorefresh.PullToRefreshBase;
import com.bolaa.medical.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.core.framework.util.DialogUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MedicalReportActivity extends BaseListActivity implements OnLoadErrorListener,OnRefreshListener{
	
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
		setActiviyContextView(R.layout.activity_common_list, true, true);
		setTitleText("", "体检报告", 0, true);
		mPullListView=(PullListView)findViewById(R.id.pull_listview);
		mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
		mListView=mPullListView.getRefreshableView();
		mAdapter=new MedicalReportAdapter(this);
		mListView.setAdapter(mAdapter);
	}
	
	private void setListener(){
		mListView.setOnScrollListener(new MyOnScrollListener());
		mLoadStateController.setOnLoadErrorListener(this);
		mPullListView.setOnRefreshListener(this);
		((MedicalReportAdapter)mAdapter).setDialogStateListener(new DialogStateListener() {
			
			@Override
			public void onCallback(boolean isShow) {
				// TODO Auto-generated method stub
				if(isShow){
					DialogUtil.showDialog(lodDialog);
				}else {
					if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
				}
			}
		});
	}
	
	private void initData(boolean isRefresh){
		if(!isRefresh){
			showLoading();
		}
		ParamBuilder params=new ParamBuilder();
		if(isRefresh){
			immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_REPORT_LIST), OrderWraper.class);
		}else {
			reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_REPORT_LIST), OrderWraper.class);
		}
	}
	
	@Override
	protected BeanWraper newBeanWraper() {
		// TODO Auto-generated method stub
		return new OrderWraper();
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
		Intent intent=new Intent(context,MedicalReportActivity.class);
		context.startActivity(intent);
	}

}

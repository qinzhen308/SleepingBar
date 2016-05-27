package com.bolaa.medical.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.bolaa.medical.R;
import com.bolaa.medical.adapter.CouponListAdapter;
import com.bolaa.medical.adapter.CouponSelectAdapter;
import com.bolaa.medical.common.APIUtil;
import com.bolaa.medical.common.AppUrls;
import com.bolaa.medical.controller.LoadStateController;
import com.bolaa.medical.httputil.ParamBuilder;
import com.bolaa.medical.model.Coupon;
import com.bolaa.medical.model.wrapper.BeanWraper;
import com.bolaa.medical.model.wrapper.CounponWraper;
import com.bolaa.medical.utils.AppUtil;
import com.bolaa.medical.view.pulltorefresh.PullListView;
import com.bolaa.medical.view.pulltorefresh.PullToRefreshBase;

import java.util.List;

/**
 * 选择优惠券的页面
 */
public class CouponSelectActivity extends BaseListActivity implements LoadStateController.OnLoadErrorListener, PullToRefreshBase.OnRefreshListener{
	
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
		setTitleTextRightText("", "选择优惠券", "", true);
		mPullListView=(PullListView)findViewById(R.id.pull_listview);
		mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
		mListView=mPullListView.getRefreshableView();
		mAdapter=new CouponSelectAdapter(this);
		mListView.setAdapter(mAdapter);
	}
	
	private void setListener(){
		mListView.setOnScrollListener(new MyOnScrollListener());
		mLoadStateController.setOnLoadErrorListener(this);
		mPullListView.setOnRefreshListener(this);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Coupon coupon=(Coupon)parent.getItemAtPosition(position);
				Intent intent=new Intent();
				intent.putExtra("coupon_id",""+coupon.bonus_id);
				intent.putExtra("coupon_value",""+coupon.type_money);
				setResult(RESULT_OK,intent);
				finish();
			}
		});
	}
	
	private void initData(boolean isRefresh){
		if(!isRefresh){
			showLoading();
		}
		ParamBuilder params=new ParamBuilder();
		params.append("is_used",0);
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
	
	public static void invoke(Activity context){
		Intent intent=new Intent(context,CouponSelectActivity.class);
		context.startActivityForResult(intent,11);
	}

}

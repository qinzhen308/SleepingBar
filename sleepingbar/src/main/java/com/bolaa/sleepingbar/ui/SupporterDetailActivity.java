package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.SupporterAdapter;
import com.bolaa.sleepingbar.adapter.SupporterDetailAdapter;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.controller.LoadStateController;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.Supporter;
import com.bolaa.sleepingbar.model.SupporterDetail;
import com.bolaa.sleepingbar.model.wrapper.BeanWraper;
import com.bolaa.sleepingbar.model.wrapper.SupporterDetailWraper;
import com.bolaa.sleepingbar.model.wrapper.SupporterWraper;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.view.pulltorefresh.PullListView;
import com.bolaa.sleepingbar.view.pulltorefresh.PullToRefreshBase;

import java.util.List;

/**
 * 亲友团列表页面
 */
public class SupporterDetailActivity extends BaseListActivity implements LoadStateController.OnLoadErrorListener,PullToRefreshBase.OnRefreshListener{
	private TextView tvSupportCount;


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
		setActiviyContextView(R.layout.activity_supporter_detail, true, true);
		setTitleText("", "亲友支持团", 0, true);
		tvSupportCount=(TextView) findViewById(R.id.tv_support_count);
		mPullListView=(PullListView) findViewById(R.id.pull_listview);
		mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
		mListView=mPullListView.getRefreshableView();
		mAdapter=new SupporterDetailAdapter(this);
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
        params.append(ParamBuilder.ACCESS_TOKEN, HApplication.getInstance().token);
		if(isRefresh){
			immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SUPPORTER_DETAIL), SupporterDetailWraper.class);
		}else {
			reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SUPPORTER_DETAIL), SupporterDetailWraper.class);
		}
	}
	
	@Override
	protected BeanWraper newBeanWraper() {
		// TODO Auto-generated method stub
		return new SupporterDetailWraper();
	}

    private void setCommonInfo(){
        BeanWraper wraper=getBeanWraper();
        if(wraper!=null){
			SupporterDetailWraper data=(SupporterDetailWraper)wraper;
//            tvSupportCount.setText(data.+"/");
        }
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
        setCommonInfo();
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
	
	public static void invoke(Context context, Supporter supporter){
		Intent intent=new Intent(context,SupporterDetailActivity.class);
		if(supporter!=null){

		}
		context.startActivity(intent);
	}

}

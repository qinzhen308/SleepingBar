package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.CashWithdrawLogAdapter;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.controller.LoadStateController;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.CashLog;
import com.bolaa.sleepingbar.model.wrapper.BeanWraper;
import com.bolaa.sleepingbar.model.wrapper.CashLogWraper;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.view.pulltorefresh.PullListView;
import com.bolaa.sleepingbar.view.pulltorefresh.PullToRefreshBase;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;

import java.util.List;

/**
 * 提现记录
 */
public class CashWithdrawLogActivity extends BaseListActivity implements LoadStateController.OnLoadErrorListener,PullToRefreshBase.OnRefreshListener{

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
		setActiviyContextView(R.layout.activity_deal_history, true, true);
		setTitleText("", "提现记录", 0, true);
		mPullListView=(PullListView) findViewById(R.id.pull_listview);
		mPullListView.setMode(-1);
		mListView=mPullListView.getRefreshableView();
		mAdapter=new CashWithdrawLogAdapter(this);
		mListView.setAdapter(mAdapter);
	}
	
	private void setListener(){
		mListView.setOnScrollListener(new MyOnScrollListener());
		mLoadStateController.setOnLoadErrorListener(this);
		mPullListView.setOnRefreshListener(this);
		((CashWithdrawLogAdapter)mAdapter).setOnCancelListener(new CashWithdrawLogAdapter.OnCancelListener() {
			@Override
			public void onCancel(CashLog cashLog) {
				cancelCashWithdraw(cashLog);
			}
		});
	}

	private void cancelCashWithdraw(CashLog cashLog){
		DialogUtil.showDialog(lodDialog);
		ParamBuilder params=new ParamBuilder();
		params.append("apply_id",cashLog.id);
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CASH_WITHDRAW_CANCEL), new NetworkWorker.ICallback() {
			@Override
			public void onResponse(int status, String result) {
				if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
				if(status==200){
					BaseObject<Object> obj=GsonParser.getInstance().parseToObj(result,Object.class);
					if(obj!=null&&obj.status==BaseObject.STATUS_OK){
						AppUtil.showToast(getApplicationContext(),obj.info);
						initData(true);
					}else {
						AppUtil.showToast(getApplicationContext(),obj!=null?obj.info:"取消失败");
					}
				}else {
					AppUtil.showToast(getApplicationContext(),"操作失败");
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
			immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CASH_LOG), CashLogWraper.class);
		}else {
			reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CASH_LOG), CashLogWraper.class);
		}
	}
	
	@Override
	protected BeanWraper newBeanWraper() {
		// TODO Auto-generated method stub
		return new CashLogWraper();
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
		Intent intent=new Intent(context,CashWithdrawLogActivity.class);
		context.startActivity(intent);
	}


}

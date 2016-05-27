package com.bolaa.medical.ui;

import java.util.List;

import com.bolaa.medical.R;
import com.bolaa.medical.adapter.AlreadyReservationAdapter;
import com.bolaa.medical.adapter.AlreadyReservationAdapter.DialogStateListener;
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
import android.view.View;
import android.widget.TextView;

/**
 * 已经预约的体检(我的体检)
 * @author pualz
 *
 */
public class AlreadyReservationActivity extends BaseListActivity implements OnRefreshListener,OnLoadErrorListener{
	private View footer;
	private TextView btnSubmit;
	private String title;
	
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
		Intent intent=getIntent();
		title=intent.getStringExtra("title");
		if(AppUtil.isNull(title)){
			title="预约体检";
		}
	}
	
	private void initView(){
		setActiviyContextView(R.layout.activity_common_list, true, true);
		setTitleText("", title, 0, true);
		mPullListView=(PullListView)findViewById(R.id.pull_listview);
		mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
		mListView=mPullListView.getRefreshableView();
		mAdapter=new AlreadyReservationAdapter(this);
		setFooter();
		mListView.setAdapter(mAdapter);
	}
	
	private void setFooter() {
		// TODO Auto-generated method stub
		footer=View.inflate(this, R.layout.layout_btn_footer, null);
		btnSubmit=(TextView)footer.findViewById(R.id.btn_submit);
		mListView.addFooterView(footer);
	}

	private void setListener(){
		btnSubmit.setOnClickListener(this);
		mLoadStateController.setOnLoadErrorListener(this);
		mPullListView.setOnRefreshListener(this);
		((AlreadyReservationAdapter)mAdapter).setDialogStateListener(new DialogStateListener() {

			@Override
			public void onCallback(boolean isShow) {
				// TODO Auto-generated method stub
				if (isShow) {
					DialogUtil.showDialog(lodDialog);
				} else {
					if (!isFinishing()) DialogUtil.dismissDialog(lodDialog);
				}
			}
		});
		mListView.setOnScrollListener(new MyOnScrollListener());
	}
	

	
	private void initData(boolean isRefresh){
		if(!isRefresh){
			showLoading();
		}
		ParamBuilder params=new ParamBuilder();
		
		if(isRefresh){
			immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ALREADY_BOOK), OrderWraper.class);
		}else {
			reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_ALREADY_BOOK), OrderWraper.class);
		}
	}
	
	@Override
	protected BeanWraper newBeanWraper() {
		// TODO Auto-generated method stub
		return new OrderWraper();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==btnSubmit){
			MakingAppointmentActivity.invoke(this);
		}else {
			
			super.onClick(v);
		}
	}

	@Override
	protected void handlerData(List allData, List currentData, boolean isLastPage) {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
		if(AppUtil.isEmpty(allData)){
//			showNodata();
			showSuccess();
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
//		showFailture();
		showSuccess();
	}

	@Override
	protected void loadTimeOut(String message, Throwable throwable) {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
//		showFailture();
		showSuccess();
	}

	@Override
	protected void loadNoNet() {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
//		showFailture();
		showSuccess();
	}

	@Override
	protected void loadServerError() {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
//		showFailture();
		showSuccess();
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
	
	public static void invoke(Context context,String title){
		Intent intent=new Intent(context,AlreadyReservationActivity.class);
		intent.putExtra("title",title);
		context.startActivity(intent);
	}

}

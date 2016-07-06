package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.DeviceInfoListAdapter;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.controller.LoadStateController;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.Watch;
import com.bolaa.sleepingbar.model.wrapper.BeanWraper;
import com.bolaa.sleepingbar.model.wrapper.SupporterWraper;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.BaseObjectList;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.view.pulltorefresh.PullListView;
import com.bolaa.sleepingbar.view.pulltorefresh.PullToRefreshBase;
import com.core.framework.app.devInfo.ScreenUtil;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;

import java.util.List;

/**
 * 设备信息
 */
public class DeviceInfoActivity extends BaseListActivity implements LoadStateController.OnLoadErrorListener,PullToRefreshBase.OnRefreshListener{
	private TextView tvBindWatch;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
		initData();
	}
	
	private void setExtra(){
		
	}



	private void initView(){
		setActiviyContextView(R.layout.activity_device_info_list, false, true);
		setTitleText("", "设备信息", 0, true);
		mPullListView=(PullListView) findViewById(R.id.pull_listview);
		mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
		mListView=mPullListView.getRefreshableView();
		mListView.setFooterDividersEnabled(false);
		setFooter();
		mAdapter=new DeviceInfoListAdapter(this);
		mListView.setAdapter(mAdapter);
	}

	private void setFooter(){
		tvBindWatch=new TextView(this);
		tvBindWatch.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
		tvBindWatch.setPadding(0,ScreenUtil.dip2px(this,3),0, ScreenUtil.dip2px(this,3));
		tvBindWatch.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.text_size));
		tvBindWatch.setTextColor(getResources().getColor(R.color.main));
		tvBindWatch.setGravity(Gravity.CENTER);
		tvBindWatch.setText("去绑定手环");
		View blank=new View(this);
		blank.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ScreenUtil.dip2px(this,30)));
		mListView.addFooterView(blank);
		mListView.addFooterView(tvBindWatch);
	}
	
	private void setListener(){
//		mListView.setOnScrollListener(new MyOnScrollListener());
//		mLoadStateController.setOnLoadErrorListener(this);
//		mPullListView.setOnRefreshListener(this);
		tvBindWatch.setOnClickListener(this);
		((DeviceInfoListAdapter)mAdapter).setUnbindListener(new DeviceInfoListAdapter.UnbindListener() {
			@Override
			public void doUnbind(Watch watch) {
				AppUtil.showToast(getApplicationContext(),"接口还没有。。。");
				return;
//				unBindWatch();
			}
		});
	}

	private void initData(){
		DialogUtil.showDialog(lodDialog);
		ParamBuilder params=new ParamBuilder();
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_DEVICE_HISTORY), new NetworkWorker.ICallback() {
			@Override
			public void onResponse(int status, String result) {
				if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
				if(status==200){
					BaseObjectList<Watch> object= GsonParser.getInstance().parseToObj4List(result,Watch.class);
					if(object!=null){
						if(object.status==BaseObjectList.STATUS_OK){
							mAdapter.setList(object.data);
							mAdapter.notifyDataSetChanged();
						}else {
							AppUtil.showToast(getApplicationContext(),"还没绑定任何设备");
						}
					}else {
						AppUtil.showToast(getApplicationContext(),"解析出错");
					}
				}else {
					AppUtil.showToast(getApplicationContext(),"请检查网络");
				}
			}
		});
	}

	//还没做
	private void unBindWatch(){
		DialogUtil.showDialog(lodDialog);
		ParamBuilder params=new ParamBuilder();
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_DEVICE_HISTORY), new NetworkWorker.ICallback() {
			@Override
			public void onResponse(int status, String result) {
				if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
				if(status==200){
					BaseObjectList<Watch> object= GsonParser.getInstance().parseToObj4List(result,Watch.class);
					if(object!=null){
						if(object.status==BaseObjectList.STATUS_OK){
							mAdapter.setList(object.data);
							mAdapter.notifyDataSetChanged();
						}else {
							AppUtil.showToast(getApplicationContext(),"还没绑定任何设备");
						}
					}else {
						AppUtil.showToast(getApplicationContext(),"解析出错");
					}
				}else {
					AppUtil.showToast(getApplicationContext(),"请检查网络");
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
			immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SUPPORTER_LIST), SupporterWraper.class);
		}else {
			reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SUPPORTER_LIST), SupporterWraper.class);
		}
	}
	
	@Override
	protected BeanWraper newBeanWraper() {
		// TODO Auto-generated method stub
		return new SupporterWraper();
	}


	@Override
	public void onClick(View v) {
		if(v==tvBindWatch){
            QuickBindWatchActivity.invoke(this);
		}else {
			super.onClick(v);
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
		Intent intent=new Intent(context,DeviceInfoActivity.class);
		context.startActivity(intent);
	}

}

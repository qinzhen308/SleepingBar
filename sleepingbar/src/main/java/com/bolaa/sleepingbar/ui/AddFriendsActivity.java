package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.FriendsNearbyAdapter;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.controller.LoadStateController;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.Friends;
import com.bolaa.sleepingbar.model.wrapper.BeanWraper;
import com.bolaa.sleepingbar.model.wrapper.FriendsWraper;
import com.bolaa.sleepingbar.parser.gson.BaseObject;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.view.pulltorefresh.PullListView;
import com.bolaa.sleepingbar.view.pulltorefresh.PullToRefreshBase;
import com.core.framework.net.NetworkWorker;
import com.core.framework.util.DialogUtil;

import org.w3c.dom.Text;

import java.util.List;

/**
 * 添加好友
 */
public class AddFriendsActivity extends BaseListActivity implements LoadStateController.OnLoadErrorListener,PullToRefreshBase.OnRefreshListener{

	private EditText etSearch;
	private TextView tvLabel;
	private String keywords;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
//		nearbyFriends(false);
	}
	
	private void setExtra(){
		
	}

	private void initView(){
		setActiviyContextView(R.layout.activity_deal_history, true, true);
		setTitleText("", "添加好友", 0, true);
		etSearch=(EditText)findViewById(R.id.et_search);
		tvLabel=(TextView) findViewById(R.id.tv_label);
		mPullListView=(PullListView) findViewById(R.id.pull_listview);
		mPullListView.setMode(-1);
		mListView=mPullListView.getRefreshableView();
		mAdapter=new FriendsNearbyAdapter(this);
		mListView.setAdapter(mAdapter);
	}
	
	private void setListener(){
		mListView.setOnScrollListener(new MyOnScrollListener());
		mLoadStateController.setOnLoadErrorListener(this);
		mPullListView.setOnRefreshListener(this);
		((FriendsNearbyAdapter)mAdapter).setOnCareEventListener(new FriendsNearbyAdapter.OnCareEventListener() {
			@Override
			public void onCareEvent(Friends friends, TextView view) {
				if(friends.is_care==1){
					cancelCare(friends,view);
				}else {
					doCare(friends,view,2);
				}
			}
		});
		etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId== EditorInfo.IME_ACTION_SEARCH){
					AppUtil.hideSoftInputMethod(AddFriendsActivity.this,etSearch);
					keywords=etSearch.getText().toString().trim();
					tvLabel.setText("搜索好友");
					//搜索
					initData(false);
					return true;
				}
				return false;
			}
		});
	}

	//附近的朋友，只是第一次会加载，搜索后就没有了
	private void nearbyFriends(boolean isRefresh){
		if(!isRefresh){
			showLoading();
		}
		ParamBuilder params=new ParamBuilder();
		if(isRefresh){
			immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SEARCH_FRIENDS), FriendsWraper.class);
		}else {
			reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SEARCH_FRIENDS), FriendsWraper.class);
		}
	}

	private void initData(boolean isRefresh){
		if(!isRefresh){
			showLoading();
		}
		ParamBuilder params=new ParamBuilder();
		params.append("keywords",keywords);
		if(HApplication.getInstance().mLocation!=null){
			params.append("lat", HApplication.getInstance().mLocation.getLatitude());//纬度
			params.append("lng",HApplication.getInstance().mLocation.getLongitude());//经度
		}
		if(isRefresh){
			immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SEARCH_FRIENDS), FriendsWraper.class);
		}else {
			reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_SEARCH_FRIENDS), FriendsWraper.class);
		}
	}
	
	@Override
	protected BeanWraper newBeanWraper() {
		// TODO Auto-generated method stub
		return new FriendsWraper();
	}

	private void doCare(final Friends friends,final TextView view,int type){
		DialogUtil.showDialog(lodDialog);
		ParamBuilder params=new ParamBuilder();
		params.append("f_type",type);
		params.append("f_user_id",friends.user_id);
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_DO_CARE), new NetworkWorker.ICallback() {
			@Override
			public void onResponse(int status, String result) {
				if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
				if(status==200){
					BaseObject<Object> obj=GsonParser.getInstance().parseToObj(result,Object.class);
					if(obj!=null){
						if(obj.status==BaseObject.STATUS_OK){
							view.setText("已关注");
							view.setBackgroundResource(R.drawable.bg_rectangle_strake_half_circel_gray);
							friends.is_care=1;
						}else {
							AppUtil.showToast(getApplicationContext(),obj.info);
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

	private void cancelCare(final Friends friends, final TextView view){
		DialogUtil.showDialog(lodDialog);
		ParamBuilder params=new ParamBuilder();
		params.append("f_user_id",friends.user_id);
		params.append("tab","me_care");
		NetworkWorker.getInstance().get(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_CANCEL_CARE), new NetworkWorker.ICallback() {
			@Override
			public void onResponse(int status, String result) {
				if(!isFinishing())DialogUtil.dismissDialog(lodDialog);
				if(status==200){
					BaseObject<Object> obj=GsonParser.getInstance().parseToObj(result,Object.class);
					if(obj!=null){
						if(obj.status==BaseObject.STATUS_OK){
							view.setText("关注");
							view.setBackgroundResource(R.drawable.bg_rectangle_strake_half_circel_purple);
							friends.is_care=1;
						}else {
							AppUtil.showToast(getApplicationContext(),obj.info);
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
		Intent intent=new Intent(context,AddFriendsActivity.class);
		context.startActivity(intent);
	}


}

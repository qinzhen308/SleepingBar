package com.bolaa.sleepingbar.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.FriendsListAdapter;
import com.bolaa.sleepingbar.adapter.FundsRankinglistAdapter;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.controller.LoadStateController;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.Friends;
import com.bolaa.sleepingbar.model.wrapper.BeanWraper;
import com.bolaa.sleepingbar.model.wrapper.FriendsWraper;
import com.bolaa.sleepingbar.model.wrapper.RankinglistItemWraper;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;
import com.bolaa.sleepingbar.view.pulltorefresh.PullListView;
import com.bolaa.sleepingbar.view.pulltorefresh.PullSwipeListView;
import com.bolaa.sleepingbar.view.pulltorefresh.PullToRefreshBase;
import com.bolaa.sleepingbar.view.pulltorefresh.SwipeListView;
import com.core.framework.app.devInfo.ScreenUtil;

import java.util.List;

/**
 * 我的好友
 * 
 * @author paulz
 *
 */
public class MyFriendsFragment extends BaseListFragment implements PullToRefreshBase.OnRefreshListener ,LoadStateController.OnLoadErrorListener{

	private int pageType;
	public static final int PAGE_TYPE_I_CARE=0;//我关注的
	public static final int PAGE_TYPE_CARE_ME=1;//关注我的

	public EditText etSearch;
	private String keywords;




	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		setView(inflater, R.layout.fragment_my_friends, false);
		initView();
		setListener();
		return baseLayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initData(false);
	}

	private void setExtra(){
		Bundle data=getArguments();
		if(data==null)return;
		pageType =data.getInt("page_type",PAGE_TYPE_I_CARE);
	}

	public void initView() {
		etSearch=(EditText)baseLayout.findViewById(R.id.et_search);
		mPullSwipeListView=(PullSwipeListView) baseLayout.findViewById(R.id.pull_listview);
        mPullSwipeListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
		mSwipeListView=(SwipeListView) mPullSwipeListView.getRefreshableView();
        if(pageType==PAGE_TYPE_I_CARE){
            mSwipeListView.setRightViewWidth(ScreenUtil.dip2px(getActivity(),80));//右边距离
        }else {
            mSwipeListView.setRightViewWidth(0);//不要右边
        }
		mAdapter=new FriendsListAdapter(getActivity());
        mSwipeListView.setAdapter(mAdapter);
        mLoadStateController=new LoadStateController(getActivity(),(FrameLayout)baseLayout.findViewById(R.id.layout_load_state_container));
        hasLoadingState=true;
	}

	private void setListener() {
		mPullSwipeListView.setOnRefreshListener(this);
		mLoadStateController.setOnLoadErrorListener(this);
        mSwipeListView.setOnScrollListener(new MyOnScrollListener());
        if(pageType==PAGE_TYPE_I_CARE){
            ((FriendsListAdapter)mAdapter).setOnCancelEventListener(new FriendsListAdapter.OnCancelEventListener() {
                @Override
                public void onCancel(Friends friends) {
                    mSwipeListView.hideRightView();
                }
            });
        }
		etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId== EditorInfo.IME_ACTION_SEARCH){
					AppUtil.hideSoftInputMethod(getActivity(),etSearch);
					keywords=etSearch.getText().toString().trim();
					//搜索
                    initData(false);
					return true;
				}
				return false;
			}
		});
	}




	@Override
	public void heavyBuz() {

	}


	public static MyFriendsFragment createInstance(int type){
		MyFriendsFragment instance=new MyFriendsFragment();
		Bundle data=new Bundle();
		data.putInt("page_type",type);
		instance.setArguments(data);
		return instance;
	}

	private void initData(boolean isRefresh){
		if(!isRefresh){
			showLoading();
		}
		ParamBuilder params=new ParamBuilder();
		params.append(ParamBuilder.ACCESS_TOKEN, HApplication.getInstance().token);
		if(pageType==PAGE_TYPE_CARE_ME){
			params.append("tab", "care_me");
		}
        if(!AppUtil.isNull(keywords)){
            params.append("keyword", keywords);
        }
		if(isRefresh){
			immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_MY_FRIENDS_LIST), FriendsWraper.class);
		}else {
			reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_MY_FRIENDS_LIST), FriendsWraper.class);
		}
	}

	@Override
	protected BeanWraper newBeanWraper() {
		// TODO Auto-generated method stub
		return new FriendsWraper();
	}


	@Override
	protected void handlerData(List allData, List currentData, boolean isLastPage) {
		// TODO Auto-generated method stub
		mPullSwipeListView.onRefreshComplete();
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
		mPullSwipeListView.onRefreshComplete();
		showFailture();
	}

	@Override
	protected void loadTimeOut(String message, Throwable throwable) {
		// TODO Auto-generated method stub
		mPullSwipeListView.onRefreshComplete();
		showFailture();
	}

	@Override
	protected void loadNoNet() {
		// TODO Auto-generated method stub
		mPullSwipeListView.onRefreshComplete();
		showFailture();
	}

	@Override
	protected void loadServerError() {
		// TODO Auto-generated method stub
		mPullSwipeListView.onRefreshComplete();
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

}

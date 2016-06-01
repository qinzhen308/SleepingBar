package com.bolaa.sleepingbar.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.bolaa.sleepingbar.HApplication;
import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.FundsRankinglistAdapter;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.controller.LoadStateController;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.RankinglistItem;
import com.bolaa.sleepingbar.model.wrapper.BeanWraper;
import com.bolaa.sleepingbar.model.wrapper.RankinglistItemWraper;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;
import com.bolaa.sleepingbar.view.pulltorefresh.PullListView;
import com.bolaa.sleepingbar.view.pulltorefresh.PullToRefreshBase;

import java.util.List;

/**
 * 医疗机构的评价
 * 
 * @author paulz
 *
 */
public class FundsRankinglistFragment extends BaseListFragment implements PullToRefreshBase.OnRefreshListener ,LoadStateController.OnLoadErrorListener,View.OnClickListener{

	private int pageType;
	public static final int PAGE_TYPE_FRIENDS=0;//好友排行
	public static final int PAGE_TYPE_ALL=1;//总排行

	private ViewGroup layoutMyFunds;

	public TextView tvName;
	public TextView tvRanking;
	public TextView tvSupportCount;
	public TextView tvFundsTotal;
	public ImageView ivAvatar;
	public ImageView ivHeart;




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
		setView(inflater, R.layout.fragment_funds_rankinglist, true);
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
		pageType =data.getInt("page_type",PAGE_TYPE_FRIENDS);
	}

	public void initView() {
		layoutMyFunds=(ViewGroup) baseLayout.findViewById(R.id.layout_my_funds);
		tvName=(TextView)layoutMyFunds.findViewById(R.id.tv_name);
		tvRanking =(TextView)layoutMyFunds.findViewById(R.id.tv_ranking);
		tvSupportCount =(TextView)layoutMyFunds.findViewById(R.id.tv_support);
		tvFundsTotal =(TextView)layoutMyFunds.findViewById(R.id.tv_funds_total);
		ivHeart=(ImageView) layoutMyFunds.findViewById(R.id.checkbox);
		ivAvatar=(ImageView) layoutMyFunds.findViewById(R.id.iv_avatar);

		mPullListView=(PullListView) baseLayout.findViewById(R.id.pull_listview);
		mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
		mListView=mPullListView.getRefreshableView();
		mAdapter=new FundsRankinglistAdapter(getActivity());
		mListView.setAdapter(mAdapter);
	}

	private void setListener() {
		mPullListView.setOnRefreshListener(this);
		mLoadStateController.setOnLoadErrorListener(this);
		mListView.setOnScrollListener(new MyOnScrollListener());
		ivHeart.setOnClickListener(this);
	}

	private void setMyFundsInfo(){
		BeanWraper wraper=getBeanWraper();
		if(wraper!=null){
			RankinglistItemWraper data=((RankinglistItemWraper)wraper);
			tvFundsTotal.setText(AppUtil.getTwoDecimal(data.my_sleep_fund));
			tvName.setText(data.my_nickname);
			tvRanking.setText(""+data.my_sleep_rank);
			tvSupportCount.setText(""+data.my_support_num);
			Image13Loader.getInstance().loadImageFade(data.my_avatar,ivAvatar);
		}
	}



	@Override
	public void heavyBuz() {

	}


	public static FundsRankinglistFragment createInstance(int type){
		FundsRankinglistFragment instance=new FundsRankinglistFragment();
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
		if(pageType==PAGE_TYPE_ALL){
			params.append("tab", "all");
		}
		if(isRefresh){
			immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_FUNDS_RANKING_LIST), RankinglistItemWraper.class);
		}else {
			reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(), AppUrls.getInstance().URL_FUNDS_RANKING_LIST), RankinglistItemWraper.class);
		}
	}

	@Override
	protected BeanWraper newBeanWraper() {
		// TODO Auto-generated method stub
		return new RankinglistItemWraper();
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
		setMyFundsInfo();
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

	@Override
	public void onClick(View v) {
		if(v==ivAvatar){

		}
	}
}

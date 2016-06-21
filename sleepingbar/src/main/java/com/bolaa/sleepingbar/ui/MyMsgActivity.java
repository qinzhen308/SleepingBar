package com.bolaa.sleepingbar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;


import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.adapter.DealHistoryAdapter;
import com.bolaa.sleepingbar.adapter.MyMsgAdapter;
import com.bolaa.sleepingbar.common.APIUtil;
import com.bolaa.sleepingbar.common.AppUrls;
import com.bolaa.sleepingbar.controller.LoadStateController;
import com.bolaa.sleepingbar.httputil.ParamBuilder;
import com.bolaa.sleepingbar.model.wrapper.BeanWraper;
import com.bolaa.sleepingbar.model.wrapper.MessageWraper;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.view.pulltorefresh.PullListView;
import com.bolaa.sleepingbar.view.pulltorefresh.PullToRefreshBase;
import java.util.List;

/**
 * 我的消息
 */
public class MyMsgActivity extends BaseListActivity implements PullToRefreshBase.OnRefreshListener, LoadStateController.OnLoadErrorListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setExtra();
		initView();
		setListener();
		initData(false);
	}

    private void setExtra() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
	}

	private void initView() {
		setActiviyContextView(R.layout.activity_my_msg ,true, true);
		setTitleText("", "我的消息", 0, true);
		mPullListView = (PullListView) findViewById(R.id.pull_listview);
        mPullListView.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
		mListView = mPullListView.getRefreshableView();
		mAdapter = new MyMsgAdapter(this);
        mListView.setAdapter(mAdapter);
    }


	private void setListener() {
		mPullListView.setOnRefreshListener(this);
//        mLoadStateController.setOnLoadErrorListener(this);
		mListView.setOnScrollListener(new MyOnScrollListener());

	}

	private void initData(boolean isRefresh) {
		if (!isRefresh) {
			 showLoading();
		}
		ParamBuilder params = new ParamBuilder();
		if (isRefresh) {
			immediateLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_MY_MSG),
					MessageWraper.class);
		} else {
			reLoadData(APIUtil.parseGetUrlHasMethod(params.getParamList(),AppUrls.getInstance().URL_MY_MSG),
					MessageWraper.class);
		}
	}

	@Override
	protected BeanWraper newBeanWraper() {
		return new MessageWraper();
	}


	@Override
	protected void handlerData(List allData, List currentData,
			boolean isLastPage) {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
		if (AppUtil.isEmpty(allData)) {
			// AppUtil.showToast(this, "暂无评论");
            showNodata();
			return;
        }
        showSuccess();
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
        AppUtil.showToast(this, "请检查网络");
	}

	@Override
	protected void loadServerError() {
		// TODO Auto-generated method stub
		mPullListView.onRefreshComplete();
		AppUtil.showToast(this, "连接失败");
        showFailture();
    }

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if (!isLoading()) {
			initData(true);
		}
	}


	/**
	 * 掉这个方法启动页面，就重新加载帖子内容
	 * 
	 * @param context
	 */
	public static void invoke(Context context) {
		Intent intent = new Intent(context, MyMsgActivity.class);
		context.startActivity(intent);
	}


	@Override
	public void onAgainRefresh() {
		// TODO Auto-generated method stub
		initData(false);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}
}

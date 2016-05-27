package com.bolaa.medical.base;

import org.json.JSONException;
import org.json.JSONObject;

import com.bolaa.medical.base.BaseActivity;
import com.bolaa.medical.view.pulltorefresh.PullExpanableListView;
import com.bolaa.medical.view.pulltorefresh.PullToRefreshBase;
import com.bolaa.medical.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.core.framework.net.HttpRequester;
import com.core.framework.net.NetworkWorker;
import com.core.framework.net.NetworkWorker.ICallback;
import com.bolaa.medical.R;

import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 上拉加载下拉刷新的ExpanableListview
 * 
 * @author jjj
 * 
 * @date 2016年1月6日
 */
public abstract class BaseExpanableListviewActivity extends BaseActivity
		implements OnRefreshListener {
	protected PullExpanableListView mPullListView;
	protected ExpandableListView mListView;
	protected LinearLayout mNoLayout;
	protected TextView mNoTv;
	protected String url;
	protected HttpRequester requester;
	protected boolean isF = true;

	protected int curPage = 1;
	protected int totalPage = 1;
	protected boolean isLoading = true;

	/**
	 * 请在oncrate里写
	 */
	protected void initBaseView() {
		setActiviyContextView(R.layout.activity_baseexpanablelv, true, true);

		mNoLayout = (LinearLayout) findViewById(R.id.baseExpanableLv_noDataLayout);
		mNoTv = (TextView) findViewById(R.id.baseExpanableLv_noDataTv);
		mPullListView = (PullExpanableListView) findViewById(R.id.baseExpanableLv_lv);
		mPullListView.setOnRefreshListener(this);
		mPullListView.setMode(PullToRefreshBase.MODE_BOTH);
		mListView = mPullListView.getRefreshableView();
	}

	protected void setPramre(String url, HttpRequester requester,
			boolean isRefesh) {
		this.url = url;
		this.requester = requester;

		postData(isRefesh);
	}

	private void postData(final boolean isRefesh) {
		if (isF) {
			showLoading();
		}
		NetworkWorker.getInstance().post(url, new ICallback() {

			@Override
			public void onResponse(int status, String result) {

				isLoading = false;
				mPullListView.onRefreshComplete();
				JSONObject object;
				try {
					object = new JSONObject(result);

					if (object != null
							&& object.getString("status").equals("0")) {

						notifyData(object, isRefesh);
					} else {
						showFailture();
					}
				} catch (JSONException e) {
					showFailture();
					e.printStackTrace();
				}
				isF = false;
			}
		}, requester);

	}

	protected void showData(boolean hasData) {
		if (hasData) {
			mNoLayout.setVisibility(View.GONE);
			mPullListView.setVisibility(View.VISIBLE);
		} else {
			mPullListView.setVisibility(View.GONE);
			mNoLayout.setVisibility(View.VISIBLE);
		}
	}

	protected abstract void notifyData(JSONObject content, boolean isRefesh);

	@Override
	public void onRefresh() {

		if (!isLoading) {
			isLoading = true;
			// 未知-反
			if (mPullListView.isHeaderShown()) {// 加载更多
				if (curPage < totalPage) {
					curPage++;
					loadNextPage();
				} else {
					Toast.makeText(getApplicationContext(), "已经没有更多了",
							Toast.LENGTH_SHORT).show();
					mPullListView.onRefreshComplete();
					isLoading = false;
					mPullListView
							.setMode(PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH);
				}
			} else if (mPullListView.isFooterShown()) {// 刷新
				mPullListView.setMode(PullToRefreshBase.MODE_BOTH);
				refreshData();
			}
		} else {
			mPullListView.onRefreshComplete();

		}
	}

	protected abstract void loadNextPage();

	protected abstract void refreshData();
}

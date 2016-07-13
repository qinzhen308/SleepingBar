package com.bolaa.sleepingbar.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.base.BaseFragment;
import com.bolaa.sleepingbar.common.GlobeFlags;
import com.bolaa.sleepingbar.controller.AbstractListAdapter;
import com.bolaa.sleepingbar.httputil.PageListRequest;
import com.bolaa.sleepingbar.httputil.PageListResponse;
import com.bolaa.sleepingbar.model.wrapper.BeanWraper;
import com.bolaa.sleepingbar.parser.gson.GsonParser;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.bolaa.sleepingbar.utils.Image13Loader;
import com.bolaa.sleepingbar.view.pulltorefresh.PullListView;
import com.bolaa.sleepingbar.view.pulltorefresh.PullSwipeListView;
import com.bolaa.sleepingbar.view.pulltorefresh.SwipeListView;
import com.bolaa.sleepingbar.view.pulltorefreshgrid.PullToRefreshStaggeredGridView;
import com.bolaa.sleepingbar.view.pulltorefreshgrid.StaggeredGridView;
import com.core.framework.develop.DevRunningTime;
import com.core.framework.develop.LogUtil;
import com.core.framework.net.HttpRequester;
import com.core.framework.store.sharePer.PreferencesUtils;

import java.net.SocketTimeoutException;
import java.util.List;

public abstract class BaseListFragment extends BaseFragment{
	
	protected PullListView mPullListView;
	protected PullSwipeListView mPullSwipeListView;
	protected ListView mListView;
	protected SwipeListView mSwipeListView;

    protected PullToRefreshStaggeredGridView mPullStaggerGridView; // 下拉刷新瀑布流
    protected StaggeredGridView mWaterGridView; // 瀑布流
	
//    private int mParserType;
//    private String mParserKey;
    private Class clazz;//数据类型

	
	private DataPageRequest mRequest;
    private DataPageResponse mResponse;
    
    protected ImageView mSwitchModelIv;
    
    protected View mFooterView;
    protected View mSpecialFooterView; //特殊需求的footerview
    protected View mSpecialWaterFooterView; //特殊需求的瀑布流footerview
    protected View mWaterFooterView;
    
    private LinearLayout mProTipLayer;
    private LinearLayout mNoDataLayer;

    protected View mStaggerFootView; // 特殊瀑布流footview
    
    // 是否需要结束标签
    public boolean isNeedEndTag = true;
    public boolean isNeedEndToast = true;
    
    
    private int mTrys;
    
    protected boolean isGridMode;
    
    protected AbstractListAdapter mAdapter;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mRequest = new DataPageRequest();
		 mResponse = new DataPageResponse();
	     mRequest.setPageResponseListener(mResponse);
	     
	     mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.include_list_footer, null);
	     mWaterFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.include_list_footer, null);
	     mSpecialFooterView = View.inflate(getActivity(), R.layout.list_footer, null);
	     mSpecialWaterFooterView = View.inflate(getActivity(), R.layout.list_footer, null);
	     mStaggerFootView = View.inflate(getActivity(), R.layout.include_stag_list_footer, null);
	     mProTipLayer = (LinearLayout) mStaggerFootView.findViewById(R.id.layer_pro_tip);
	     mNoDataLayer = (LinearLayout) mStaggerFootView.findViewById(R.id.layer_no_data);
		
	}
	
	
	
	@Override
    public void onResume() {
        super.onResume();
        
    }
	
	
	protected boolean isLoading() {
        return mRequest.isLoading();
    }

    protected boolean isLastPage() {
        return mResponse.isLastPage;
    }

    public void setRepeateFilter(boolean isNeedFilter) {
        mRequest.setRepeateFilter(isNeedFilter);
    }


    public void setPageIndexKey(String key) {
        mRequest.setPageIndexKey(key);
    }

    public void setPageCountKey(String key) {
        mRequest.setPageCountKey(key);
    }

    protected void setLoadPageSize(int pageSize) {
        mRequest.setPageSize(pageSize);
    }

    protected void setHttpRequester(com.bolaa.sleepingbar.httputil.HttpRequester requester) {
        mRequest.setHttpRequester(requester);
    }

    protected int getCurrentLoadingPage() {
        return mRequest.getCurrentLoadingPage();
    }

    protected void immediateLoadData(String baseUrl, Class clazz) {
    	this.clazz=clazz;
        mRequest.setImmediateLoad(true);
        mRequest.setBaseUrl(baseUrl);
        mRequest.reload();
    }

    protected void preDisplyData(String baseUrl, Class clazz) {
        mRequest.setPreDisply(true);
        reLoadData(baseUrl, clazz);
    }


    //如果某个功能的缓存时间有特殊需求，可调用该方法
    protected void setCacheTime(long cacheTime) {
        mRequest.setCacheTime(cacheTime);
    }

    protected void reLoadData(String baseUrl, Class clazz) {
    	this.clazz=clazz;
        mRequest.setBaseUrl(baseUrl);
        mRequest.reload();
    }


    protected void againLoadData() {
        mRequest.againLoad();
    }

    protected void loadNextPageData() {
        mRequest.nextPage();
    }

    protected void loadPrePageData() {
        mRequest.prePage();
    }

    protected BeanWraper getBeanWraper() {
        return mRequest.getBeanWraper();
    }

//    // 与用户相关的列表需要校验登录状态
//    protected void checkLoginStatus() {
//        if (getBeanWraper().userStatus == 0) {
//            UserTable.getInstance().update(true);
//            NetworkWorker.getInstance().getHttpClient().getCookieStore().clear();//清除cookie
//            this.finish();
//        }
//    }

    /**
     * 取消正在进行的httpget请求
     *
     * @return true 取消成功,否则失败
     */
    protected boolean cancelRequest() {
        return mRequest.cancelRequest();
    }

    /**
     * The some subclass that prepare to disply data from cache need to override this method
     *
     * @param allData
     */
    protected void preDisply(List allData) {
    }

    ;

    protected abstract void handlerData(List allData, List currentData, boolean isLastPage);

    protected abstract void loadError(String message, Throwable throwable, int page);

    protected abstract void loadTimeOut(String message, Throwable throwable);

    protected abstract void loadNoNet();

    protected abstract void loadServerError();
    
    protected BeanWraper newBeanWraper() {
		// TODO Auto-generated method stub
		return null;
	}


    class DataPageRequest extends PageListRequest {
    	@Override
        protected BeanWraper parseData(String data) {
        	Object beanWraper=GsonParser.getInstance().parseToWrapper(data, clazz);
            if(beanWraper==null)return createBeanWraper();
        	return (BeanWraper)beanWraper;
        }
        
		@Override
		public BeanWraper createBeanWraper() {
			// TODO Auto-generated method stub
			return newBeanWraper();
		}
    }

    class DataPageResponse extends PageListResponse {
        public boolean isLastPage;

        @Override
        public boolean onStartRequest(int page) {
            if (page == 1) {
//                removeSpecialFootView();
            }

            if (page > 1) {
                if (isLastPage) {
                    if (isNeedEndTag) {
//                        addSpecailFooterView();
                    } else {
                        if (isNeedEndToast){
                            AppUtil.showToast(getActivity(), R.string.pull_to_refresh_nodata);
                        }
                    }
                    return false;
                }

                if (mListView != null) {
                    if (mListView.getFooterViewsCount() >= 1) {
                        mListView.removeFooterView(mFooterView);
                    }
                    mListView.addFooterView(mFooterView);
                    // mListView.setSelection(mListView.getLastVisiblePosition());
                }

                if (mSwipeListView != null) {
                    if (mSwipeListView.getFooterViewsCount() >= 1) {
                        mSwipeListView.removeFooterView(mFooterView);
                    }
                    mSwipeListView.addFooterView(mFooterView);
//                     mSwipeListView.setSelection(mSwipeListView.getLastVisiblePosition());
                }

                if (mWaterGridView != null) {
                    mStaggerFootView.setVisibility(View.VISIBLE);
                    mProTipLayer.setVisibility(View.VISIBLE);
                    mNoDataLayer.setVisibility(View.GONE);
                }
            }

            return true;
        }

        @Override
        public void onPageResponse(List allData, List currentPageData, int page, boolean isLastPage,int count) {
            //LogUtil.d("--------------page-------------" + page);

            this.isLastPage = isLastPage;
            removeAllFooyView(page);
            
            resetTrys();
            handlerData(allData, currentPageData, isLastPage);

            if (isLastPage && !AppUtil.isEmpty(allData)) {
                if (isNeedEndTag) {
//                    addSpecailFooterView();
                } else {
                    if (isNeedEndToast){
                        AppUtil.showToast(getActivity(), R.string.pull_to_refresh_nodata);
                    }
                }
            }

            if (DevRunningTime.isSuTestM_C) {
                loadNextPageData();
            }
        }

        @Override
        public void onCacheLoad(List allData) {//todo 预先加载
            preDisply(allData);
        }

        @Override
        public void onTimeout(String message, Throwable throwable) {
            removeAllFooyView(getCurrentLoadingPage());
            loadTimeOut(message, throwable);
        }

        @Override
        public void onError(String message, Throwable throwable, int page) {
            if (throwable instanceof SocketTimeoutException && mTrys < 3) {
                mTrys++;
                againLoadData();
                LogUtil.d("-------SocketTimeoutException-------");
            } else {
                resetTrys();
                removeAllFooyView(page);
                loadError(message, throwable, page);
            }
        }

        @Override
        public void onServiceError(String message, Throwable throwable) {
            removeAllFooyView(getCurrentLoadingPage());//todo 服务器内部错误实现
            // AppUtil.showToast(BaseListActivity.this, "服务器错误");
            loadServerError();
        }

        @Override
        public void onUserLoginError(String message, Throwable throwable) { // 登录失效调去重新登录页面
        	
        }

        @Override
        public void onNoNetwork() {
            removeAllFooyView(getCurrentLoadingPage());
            loadNoNet();
        }
    }
    
    private void resetTrys() {
        mTrys = 0;
    }
    
    public void removeSpecialFootView () {
        if (mListView != null && mListView.getFooterViewsCount() >= 1) {
            mListView.removeFooterView(mSpecialFooterView);
        }

        if (mSwipeListView != null && mSwipeListView.getFooterViewsCount() >= 1) {
            mSwipeListView.removeFooterView(mSpecialFooterView);
        }


        if (mWaterGridView != null) {
            mStaggerFootView.setVisibility(View.GONE);
            mProTipLayer.setVisibility(View.VISIBLE);
            mNoDataLayer.setVisibility(View.GONE);
        }

    }

    public void setLastPage(boolean isLastPage) {
        mResponse.isLastPage = isLastPage;
    }
	
    private void removeAllFooyView(int page) {
        if (mListView != null) mListView.removeFooterView(mFooterView);
        if (mSwipeListView != null) mSwipeListView.removeFooterView(mFooterView);

        if (mWaterGridView != null) {
            mStaggerFootView.setVisibility(View.GONE);
        }
    }

    private void addSpecailFooterView() {
        ListView listView = mListView ;

        if (mSpecialFooterView != null && listView != null) {
            if (listView.getFooterViewsCount() >= 1) {
                listView.removeFooterView(mSpecialFooterView);
            }
            listView.addFooterView(mSpecialFooterView);
            //listView.setSelection(listView.getLastVisiblePosition());
        }

        listView = mSwipeListView ;
        if (mSpecialFooterView != null && listView != null) {
            if (listView.getFooterViewsCount() >= 1) {
                listView.removeFooterView(mSpecialFooterView);
            }
            listView.addFooterView(mSpecialFooterView);
            //listView.setSelection(listView.getLastVisiblePosition());
        }


        LogUtil.d("---------------finish page---------");
        if (mWaterGridView != null) {
            mStaggerFootView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mStaggerFootView.setVisibility(View.VISIBLE);
                    mProTipLayer.setVisibility(View.GONE);
                    mNoDataLayer.setVisibility(View.VISIBLE);
                }
            }, 50);
        }
    }
    
    
    public class MyOnScrollListener implements AbsListView.OnScrollListener {
    	
    	private int firstItem;
        private int visibleItem;

        private boolean isScrollEnd;

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
        	
        	isScrollEnd = firstVisibleItem + visibleItemCount == totalItemCount;
            //&& totalItemCount >= (isGridMode ? mRequest.getPageSize() / 2 : mRequest.getPageSize());

        	firstItem = firstVisibleItem;
        	visibleItem = visibleItemCount;

        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // LogUtil.d("-----------------scrollState--------=" + scrollState);
            /*if (scrollState == SCROLL_STATE_IDLE) {
                setModelStatus(true);
            } else {
                setModelStatus(false);
            }*/

            if (scrollState == SCROLL_STATE_IDLE && isScrollEnd) {
            	if (mPullListView != null && mPullListView.isFlingUp()) {
                    loadNextPageData();
                }else if (mPullSwipeListView != null && mPullSwipeListView.isFlingUp()) {
                    loadNextPageData();
                }
            }

            // Pause fetcher to ensure smoother scrolling when flinging
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    Image13Loader.getInstance().setPauseWork(true);
                    //setFastScrollAlwaysVisible(true);
                    break;

                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    
                    Image13Loader.getInstance().setPauseWork(false);
                    //setFastScrollAlwaysVisible(false);
                    break;

                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    //notifyVisibleItem(mFirstItem, mVisibleItemCount, mTotalItemCount);
                    Image13Loader.getInstance().setPauseWork(false);
                    //setFastScrollAlwaysVisible(true);
                    break;

                default:
                    break;
            }
        }
    }
    
    public class OnFlingListener implements StaggeredGridView.OnFlingListener {
        @Override
        public void onTouchFling() {
            Image13Loader.getInstance().setPauseWork(false);
        }

        @Override
        public void onFling() {
            LogUtil.d("-------------position--------" + mWaterGridView.getFirstPosition());
            Image13Loader.getInstance().setPauseWork(true);
        }

        @Override
        public void endFling() {
            Image13Loader.getInstance().setPauseWork(false);
        }
    }

    public class LoadMoreListener implements StaggeredGridView.OnLoadmoreListener {
        @Override
        public void onLoadmore() {
            if (!isLoading()) {
                loadNextPageData();
            }
        }
    }
    
    /**
     * 切换宫格或者列表模式
     *
     * @param isGrid
     */
    public void switchListMode(AbstractListAdapter adapter, boolean isGrid) {
        if (adapter == null) return;
        isGridMode = isGrid;
        int position = mListView.getFirstVisiblePosition();//position==0
        setListViewStyle();
        adapter.setViewMode(isGrid, position);
//        PreferencesUtils.putInteger(GlobeFlags.MODE_STATUS, isGrid ? GlobeFlags.MODE_BIG_PIC_MODE : GlobeFlags.MODE_LIST_MODE);
        switchModeImage(isGrid);
    }

    /**
     * 改变mPullSwipeListView的布局
     */
    private void setListViewStyle() {
        if (isGridMode) {
            mListView.setDivider(null);
        } else {
        	mListView.setDivider(getResources().getDrawable(R.drawable.list_item_line));
        	mListView.setDividerHeight(1);
        }
    }

    protected void initMode(AbstractListAdapter adapter) {
        if (PreferencesUtils.getInteger(GlobeFlags.MODE_STATUS) == GlobeFlags.MODE_LIST_MODE) {
            switchListMode(adapter, false);
            if (mSwitchModelIv != null){
            	mSwitchModelIv.setImageResource(R.drawable.bg_model_home_list);
            }
        } else {
            switchListMode(adapter, true);
            if (mSwitchModelIv != null){
            	mSwitchModelIv.setImageResource(R.drawable.bg_model_home_big);
            }
        }
    }
	
    
    protected void switchModeImage(boolean isGrid) {
    	if (mSwitchModelIv == null) return;
    	if(isGrid){
    		mSwitchModelIv.setImageResource(R.drawable.bg_model_home_big);
    	}else {
    		mSwitchModelIv.setImageResource(R.drawable.bg_model_home_list);
		}
	}
}

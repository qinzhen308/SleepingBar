package com.bolaa.sleepingbar.httputil;

import android.text.TextUtils;

import com.bolaa.sleepingbar.model.Model;
import com.bolaa.sleepingbar.model.wrapper.BeanWraper;
import com.bolaa.sleepingbar.utils.AppUtil;
import com.core.framework.app.oSinfo.SuNetEvn;
import com.core.framework.dataLoadView.DataRequest;
import com.core.framework.dataLoadView.DataService;
import com.core.framework.dataLoadView.IConsumer;
import com.core.framework.dataLoadView.IDisConsumer;
import com.core.framework.develop.LogUtil;
import com.core.framework.exception.InternalServerException;
import com.core.framework.exception.UserLoginException;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kait
 * Date: 13-1-17
 * Time: 下午4:52
 * To change this template use File | Settings | File Templates.
 */
public abstract class PageListRequest<T> implements IConsumer, IDisConsumer {
    private int isFilterBeforeSzie;
    private boolean isFirstLastPage=false;
    private boolean isRecomment=false;//
    public boolean mIsLoaddingRecomment=false;
    private boolean mGlobalIsLastPage=false;
    private int currentPage;
    private int currentLoadingPage;
    private int pageSize;
    private String baseUrl;
    private String pageIndexKey;
    private String pageCountKey;
    private long cacheTime = ParamBuilder.MINUTE * 5; // 缓存时间,以毫秒为单位
    public BeanWraper<T> beanWraper; // 带结束标签的容器类

    private List<T> pageData;
    private HttpRequester httpRequester;

    private boolean loading;
    private boolean immediateLoad;
    private boolean isPreDisply;

    private boolean isRepeateFilter; // 是否过滤重复的数据
    private HashSet<Integer> idSet; // 数据Id

    //private ICacheDecision cacheDecision;
    private PageListResponse pageResponseListener;


    public PageListRequest() {
        currentPage = currentLoadingPage = 1;
        pageIndexKey = ParamBuilder.PAGE;
        pageCountKey = ParamBuilder.PERPAGE_COUNT;

        pageSize = ParamBuilder.PAGE_SIZE_WIFI;

        initWrapter();
    }
    
    public abstract BeanWraper createBeanWraper();

    private void initWrapter() {
        if (beanWraper == null) {
            beanWraper = createBeanWraper();
        } else {
        	beanWraper.getItems().clear();
        }
    }

    public boolean isLoading() {
        return loading;
    }

    public void setRepeateFilter(boolean isNeedFilter) {
        this.isRepeateFilter = isNeedFilter;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }


    public void setPageIndexKey(String key) {
        this.pageIndexKey = key;
    }

    public void setPageCountKey(String key) {
        this.pageCountKey = key;
    }

    public int getCurrentLoadingPage() {
        return currentLoadingPage;
    }

    public void setHttpRequester(HttpRequester requester) {
        this.httpRequester = requester;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setImmediateLoad(boolean immediateLoad) {
        this.immediateLoad = immediateLoad;
    }

    public void setPreDisply(boolean preDisply) {
        this.isPreDisply = preDisply;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean cancelRequest() {
        boolean result =  DataService.getInstance().cancelGetTask(DataRequest.create().setParams(getUrl(currentPage)));
        if (result) loading = false;
        return result;
    }

    public PageListResponse getPageResponseListener() {
        return pageResponseListener;
    }

    public void setPageResponseListener(PageListResponse pageResponseListener) {
        this.pageResponseListener = pageResponseListener;
    }

    public BeanWraper getBeanWraper () {
        return beanWraper;
    }


    public List<T> getAllPageData() {
        return pageData;
    }

    public T getData(int i) {
        if (null != pageData)
            return pageData.get(i);
        return null;
    }

    public int getDataSize() {
        return null == pageData ? 0 : pageData.size();
    }

    public void prePage() {
        int pageIndex = currentPage - 1;
        if (pageIndex < 1) pageIndex = 1;

        loadPage(pageIndex);
    }

    public void nextPage() {
        if(mGlobalIsLastPage){
            mIsLoaddingRecomment=true;
            isRecomment=true;
           loadPage(1);
        }else{
            loadPage(currentPage + 1);
        }

    }

    public void reload() {
        // pageData = null;
        isRecomment=false;
        mGlobalIsLastPage=false;
        mIsLoaddingRecomment=false;
        isFirstLastPage=false;
        loadPage(1);
    }

    public void againLoad() {
        // pageData = null;

        loadPage(currentLoadingPage);
    }



    private String getUrl(int page) {
        StringBuilder add = new StringBuilder();
        if (ParamBuilder.LIMIT.equals(pageIndexKey)) {
            add.append(pageIndexKey).append("=").append(pageSize).append("&")
                    .append(pageCountKey).append("=").append(page == 1 ? 0 : pageData.size());
        } else {
            add.append(pageIndexKey).append("=").append(page).append("&")
                    .append(pageCountKey).append("=").append(pageSize);
        }

        String url;
        if (baseUrl.indexOf("?") < 0) {
            url = baseUrl + "?" + add;
        } else {
            url = baseUrl + "&" + add;
        }

        LogUtil.d("pageurl ------------ " + url);
        return url;
    }
    
    public void loadPage(int page) {
        if (loading) return;

        if (TextUtils.isEmpty(baseUrl)) {
            throw new IllegalArgumentException("PageListRequest need a base url argument.");
        }
        if (page < 1) {
            throw new IllegalArgumentException("PageListRequest: page cannot less than 1.");
        }

        loading = true;
        currentLoadingPage = page;
        //onStartRequest方法唯一使用的地方
        if (null != pageResponseListener && pageResponseListener.onStartRequest(page)) {
            DataRequest dataRequet = DataRequest.create();
            if (httpRequester != null) {
                dataRequet.setRequester(httpRequester);
            }
            if(isRecomment){
                String shttpget=getUrl(page).replace(ParamBuilder.EXCLUDE + "=0", ParamBuilder.EXCLUDE + "=1");
                dataRequet.setParams(shttpget);
            }else {
                dataRequet.setParams(getUrl(page));
            }

            dataRequet.setConsumer(this);

            if (isPreDisply && page == 1&&!mIsLoaddingRecomment) {//预先加载
                dataRequet.setDisConsumer(this);
            }

            if (immediateLoad) {
                dataRequet.renew();
            } else {
                dataRequet.submit();
            }
        } else {
            loading = false;
        }
    }
    
    @Override
    public void onDataResponse(String data) {
        loading = false;
        currentPage = currentLoadingPage;
        int dealCount= 0;

        initIdSet();

        if (null == pageData) {
            pageData = new ArrayList<T>();
        } else if (currentLoadingPage == 1&&!mIsLoaddingRecomment) {
            if (idSet.size() > 0) {
                idSet.clear();
            }
            pageData.clear();
        }

        initWrapter();
        int d=  pageData.size();
        beanWraper = parseData(data);
        List<T> curPageData = beanWraper.getItems();
        dealCount = beanWraper.getItemsCount();
        boolean isLastPage = false;
        if (beanWraper.getTotalPage() <= currentPage) {
           isLastPage = true;
        } else if (beanWraper.getTotalPage() == 0) {
            if (AppUtil.isEmpty(curPageData) || curPageData.size() < pageSize) {
                isLastPage = true;
            }
        }
        
     /*   if(mIsLoaddingRecomment){

            if(curPageData.size()==1){
                curPageData.remove(0);
               // pageData.remove(pageData.size()-1);
                isLastPage=true;
                pageResponseListener.mIsLoaddingRecomment=false;
            }
        }*/
        isFilterBeforeSzie=pageData.size();
        if (!AppUtil.isEmpty(curPageData)) {
           // !mIsLoaddingRecomment
                if (isRepeateFilter) {
                    filterData(curPageData);
                } else {
                    pageData.addAll(curPageData);
                }

        }

        if (null != pageResponseListener) {
           /* pageResponseListener.mIsLoaddingRecomment=mIsLoaddingRecomment;
           if(mIsLoaddingRecomment){

                if(pageData.size()==isFilterBeforeSzie+1){
                 pageData.remove(pageData.size()-1);
                    isLastPage=true;
                    pageResponseListener.mIsLoaddingRecomment=false;
                }
            }*/
            pageResponseListener.onPageResponse(pageData, curPageData, currentPage, isLastPage,dealCount);

            // preload next page

            if (!immediateLoad && !isLastPage) {
                DataRequest dataRequet = DataRequest.create();
                if (httpRequester != null) dataRequet.setRequester(httpRequester);
                dataRequet.setParams(getUrl(currentPage + 1));
                dataRequet.setCacheTime(cacheTime);

                /*if (null != cacheDecision) {
                    dataRequet.setCacheDecision(cacheDecision);
                }*/
                if ( SuNetEvn.getInstance().isHasNet()) {
                    dataRequet.submit();
                } else {
                    loading = false;
                    pageResponseListener.onNoNetwork();
                }
            }
        }
    }

    @Override
    public void onDataError(String message, Throwable throwable) {
        loading = false;

        LogUtil.w(throwable);
        if (null != pageResponseListener) {
            if (! SuNetEvn.getInstance().isHasNet()) {
                loading = false;
                pageResponseListener.onNoNetwork();
                return;
            }

            if (throwable instanceof ConnectTimeoutException
                    || throwable instanceof SocketTimeoutException) {
                pageResponseListener.onTimeout(throwable.getMessage(), throwable);
            } else if (throwable instanceof InternalServerException) {
                pageResponseListener.onServiceError(message, throwable);
            } else if (throwable instanceof UserLoginException) {
                pageResponseListener.onUserLoginError(message, throwable);
            } else {
                pageResponseListener.onError(message, throwable, currentPage);
            }
        }
    }

    @Override
    public void onCachedDataLoaded(String s) {
        initIdSet();

        if (null != pageResponseListener) {
            if (null == pageData) {
                pageData = new ArrayList<T>();
            } else if (currentLoadingPage == 1) {
                if (idSet.size() > 0) {
                    idSet.clear();
                }
                pageData.clear();
            }

            initWrapter();
            beanWraper = parseData(s);
            List<T> curPageData = beanWraper.getItems();
            //List<T> curPageData = parseData(s);

            if (!AppUtil.isEmpty(curPageData)) {
                pageData = curPageData;
                pageResponseListener.onCacheLoad(pageData);
            }
        }
    }

    private void initIdSet () {
        if (idSet == null) {
            idSet = new HashSet<Integer>();
        }
    }

    private void filterData (List<T> currentData) {
        if (!AppUtil.isEmpty(currentData)) {
            T objData;
            for (int i = 0; i < currentData.size(); i++) {
                objData = currentData.get(i);
                if ((objData instanceof Model) && !idSet.contains(((Model) objData).id)) {
                    pageData.add(objData);
                //int ddd= pageData.size();
                    idSet.add(((Model) objData).id);
                }

            }
        }
    }

    protected abstract BeanWraper parseData(String data);

}

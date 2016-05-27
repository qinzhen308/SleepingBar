package com.bolaa.medical.httputil;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kait
 * Date: 13-1-17
 * Time: 下午4:53
 * To change this template use File | Settings | File Templates.
 */
public abstract class PageListResponse<T> {

    public boolean mIsLoaddingRecomment=false;

    public void onPageCacheResponse(List<T> cacheData) {
        // do nothing
    }

    public abstract boolean onStartRequest(int page);

    public abstract void onPageResponse(List<T> allData, List<T> currentPageData, int page, boolean isLastPage,int dealCount);

    public abstract void onCacheLoad(List<T> allData);

    public abstract void onTimeout(String message, Throwable throwable);

    public abstract void onError(String message, Throwable throwable, int page);

    public abstract void onServiceError(String message, Throwable throwable);

    public abstract void onUserLoginError(String message, Throwable throwable);

    public abstract void onNoNetwork();
}

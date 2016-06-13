package com.bolaa.sleepingbar.view.pulltorefresh;

import android.view.View;

/**
 * Created by IntelliJ IDEA.
 * User: kait
 * Date: 12-5-19
 * Time: 下午2:08
 *
 * Interface that allows PullToRefreshBase to hijack the call to
 * AdapterView.setEmptyView()
 */
public interface EmptyViewMethodAccessor {

    /**
     * Calls upto AdapterView.setEmptyView()
     *
     * @param emptyView to set as Empty View
     */
    public void setEmptyViewInternal(View emptyView);

    /**
     * Should call PullToRefreshBase.setEmptyView() which will then
     * automatically call through to setEmptyViewInternal()
     *
     * @param emptyView to set as Empty View
     */
    public void setEmptyView(View emptyView);

}

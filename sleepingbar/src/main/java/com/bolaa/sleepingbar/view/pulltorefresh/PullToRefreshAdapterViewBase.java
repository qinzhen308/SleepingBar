package com.bolaa.sleepingbar.view.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;

/**
 * Created by IntelliJ IDEA.
 * User: kait
 * Date: 12-3-22
 * Time: 上午11:35
 * To change this template use File | SettingsActivity | File Templates.
 */
public abstract class PullToRefreshAdapterViewBase<T extends AbsListView> extends PullToRefreshBase<T>
        implements AbsListView.OnScrollListener {

    private int lastSavedFirstVisibleItem = -1;
    private AbsListView.OnScrollListener onScrollListener;
    private AbsListView.OnScrollListener scrollListener;
    private OnLastItemVisibleListener onLastItemVisibleListener;
    private View emptyView;
    private FrameLayout refreshableViewHolder;

    public PullToRefreshAdapterViewBase(Context context) {
        super(context);
        refreshableView.setOnScrollListener(this);
    }

    public PullToRefreshAdapterViewBase(Context context, int mode) {
        super(context, mode);
        refreshableView.setOnScrollListener(this);
    }

    public PullToRefreshAdapterViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        refreshableView.setOnScrollListener(this);
    }

    public final void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
                               final int totalItemCount) {

        if (null != onLastItemVisibleListener) {
            // detect if last item is visible
            int lastVisibleItemIndex = firstVisibleItem + visibleItemCount;

            if (visibleItemCount > 0 && (lastVisibleItemIndex + 1) == totalItemCount) {
                // only process first event
                if (firstVisibleItem != lastSavedFirstVisibleItem) {
                    lastSavedFirstVisibleItem = firstVisibleItem;
                    onLastItemVisibleListener.onLastItemVisible();
                }
            }
        }

        if (null != scrollListener) {
            scrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        if (null != onScrollListener) {
            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public final void onScrollStateChanged(final AbsListView view, final int scrollState) {
        if (null != onScrollListener) {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    public void setBackToTopView(){
        if (refreshableView instanceof ListView ) {
            ((ListView) refreshableView).setSelection(0);
        } else if(refreshableView instanceof GridView){
            ((GridView) refreshableView).setSelection(0);
        }
    }

    /**
     * Sets the Empty View to be used by the Adapter View.
     *
     * We need it handle it ourselves so that we can Pull-to-Refresh when the
     * Empty View is shown.
     *
     * Please note, you do <strong>not</strong> usually need to call this method
     * yourself. Calling setEmptyView on the AdapterView will automatically call
     * this method and set everything up. This includes when the Android
     * Framework automatically sets the Empty View based on it's ID.
     *
     * @param newEmptyView
     *            - Empty View to be used
     */
    public final void setEmptyView(View newEmptyView) {
        // If we already have an Empty View, removeByTime it
        if (null != emptyView) {
            refreshableViewHolder.removeView(emptyView);
        }

        if (null != newEmptyView) {
            ViewParent newEmptyViewParent = newEmptyView.getParent();
            if (null != newEmptyViewParent && newEmptyViewParent instanceof ViewGroup) {
                ((ViewGroup) newEmptyViewParent).removeView(newEmptyView);
            }

            this.refreshableViewHolder.addView(newEmptyView, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);
        }

        
        this.refreshableView.setEmptyView(newEmptyView);
        
    }

    public final void setOnLastItemVisibleListener(OnLastItemVisibleListener listener) {
        onLastItemVisibleListener = listener;
    }

    public final void setOnScrollListener(AbsListView.OnScrollListener listener) {
        onScrollListener = listener;
    }

    public final void setScrollListener(AbsListView.OnScrollListener listener) {
        scrollListener = listener;
    }

    protected void addRefreshableView(Context context, T refreshableView) {
        refreshableViewHolder = new FrameLayout(context);
        refreshableViewHolder.addView(refreshableView, ViewGroup.LayoutParams.FILL_PARENT,

                ViewGroup.LayoutParams.FILL_PARENT);
        addView(refreshableViewHolder, new LayoutParams(LayoutParams.FILL_PARENT, 0, 1.0f));
    };

    protected boolean isReadyForPullDown() {
        return isFirstItemVisible();
    }

    protected boolean isReadyForPullUp() {
        return isLastItemVisible();
    }

    public boolean isFirstItemVisible() {
        if (this.refreshableView.getCount() == 0) {
            return true;
        } else if (refreshableView.getFirstVisiblePosition() == 0) {

            final View firstVisibleChild = refreshableView.getChildAt(0);

            if (firstVisibleChild != null) {
                return firstVisibleChild.getTop() >= refreshableView.getTop();
            }
        }

        return   false;
    }

    public boolean isLastItemVisible() {
        final int count = this.refreshableView.getCount();
        final int lastVisiblePosition = refreshableView.getLastVisiblePosition();

        if (count == 0) {
            return true;
        } else if (lastVisiblePosition == count - 1) {

            final int childIndex = lastVisiblePosition - refreshableView.getFirstVisiblePosition();
            final View lastVisibleChild = refreshableView.getChildAt(childIndex);

            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= refreshableView.getBottom();
            }
        }
        return false;
    }
    
    public boolean isHeaderShown() {  
        return getHeaderLayout().isShown();  
    }  
      
    public boolean isFooterShown() {  
        return getFooterLayout().isShown();  
    }  
}


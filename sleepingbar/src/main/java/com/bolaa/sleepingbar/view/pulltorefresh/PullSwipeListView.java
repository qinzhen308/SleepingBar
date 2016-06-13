package com.bolaa.sleepingbar.view.pulltorefresh;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bolaa.sleepingbar.R;
import com.core.framework.develop.LogUtil;


/**
 * Created with IntelliJ IDEA.
 * User: mark
 * Date: 14-2-20
 * Time: 上午10:56
 * To change this template use File | Settings | File Templates.
 */
public class PullSwipeListView extends PullToRefreshAdapterViewBase<ListView> {

    private PullLoadingLayout mHeaderLoadingView;
    private PullLoadingLayout mFooterLoadingView;
    private FrameLayout mLvFooterLoadingFrame;
    private boolean mAddedLvFooter = false;
    private Context mContext;


    public PullSwipeListView(Context context) {
        super(context);
        mContext = context;
        this.setDisableScrollingWhileRefreshing(false);
    }

    public PullSwipeListView(Context context, int mode) {
        super(context, mode);
        mContext = context;
        this.setDisableScrollingWhileRefreshing(false);
    }

    public PullSwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setDisableScrollingWhileRefreshing(false);
    }

    @Override
    protected ListView createRefreshableView(Context context, AttributeSet attrs) {
        ListView lv = new SwipeListView(context, attrs);
        final int mode = getMode();

        boolean isShowHeader;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.pullshowheader);

        isShowHeader = typedArray.getBoolean(R.styleable.pullshowheader_showheader, true);

        LogUtil.d("<----------PullSwipeListView isShowHeader----------->" + isShowHeader);

        String pullLabel = context.getString(R.string.pull_to_refresh);
        String refreshingLabel = context.getString(R.string.label_loading);
        String releaseLabel = context.getString(R.string.pull_to_refresh_release);
        String upLabel = context.getString(R.string.up_to_refresh);

        if (mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) {
            FrameLayout frame = new FrameLayout(context);
            mHeaderLoadingView = new PullLoadingLayout(context, MODE_PULL_DOWN_TO_REFRESH, releaseLabel, pullLabel, refreshingLabel, isShowHeader);
            frame.addView(mHeaderLoadingView, FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

            mHeaderLoadingView.setVisibility(View.GONE);
            lv.addHeaderView(frame, null, false);//这个header是随着ListView一起的Header  这个长图是可以显示可以隐藏的那个头部
        }
        if (mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH) {
            mLvFooterLoadingFrame = new FrameLayout(context);
            mFooterLoadingView = new PullLoadingLayout(context, MODE_PULL_UP_TO_REFRESH, releaseLabel, upLabel, refreshingLabel, isShowHeader);
            mLvFooterLoadingFrame.addView(mFooterLoadingView, FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

            mFooterLoadingView.setVisibility(View.GONE);
        }

        lv.setId(android.R.id.list);
        return lv;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // setMeasuredDimension(CouponApplication.WIDTH, CouponApplication.HEIGHT);
        // int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void resetHeader() {
        // If we're not showing the Refreshing view, or the list is empty, then
        // the header/footer views won't show so we use the
        // normal method
        ListAdapter adapter = refreshableView.getAdapter();
        if (!getShowViewWhileRefreshing() || null == adapter || adapter.isEmpty()) {
            super.resetHeader();
            return;
        }

        PullLoadingLayout originalLoadingLayout;
        PullLoadingLayout listViewLoadingLayout;

        int scrollToHeight = getHeaderHeight() - 10;
        int selection;

        switch (getCurrentMode()) {
            case MODE_PULL_UP_TO_REFRESH:
                originalLoadingLayout = getFooterLayout();
                listViewLoadingLayout = mFooterLoadingView;

                selection = refreshableView.getCount() - 1;
                break;
            case MODE_PULL_DOWN_TO_REFRESH:
            default:
                originalLoadingLayout = getHeaderLayout();
                listViewLoadingLayout = mHeaderLoadingView;
                scrollToHeight *= -1;
                selection = 0;
                break;
        }

        // Set our Original View to Visible
        originalLoadingLayout.setVisibility(View.VISIBLE);

        if (isSupportMultiPull) {
            mHeaderImageView.setPadding(0, 0, 0, 0);
        }

        // Scroll so our View is at the same Y as the ListView header/footer,
        // but only scroll if we've pulled to refresh
        if (getState() != MANUAL_REFRESHING) {
            refreshableView.setSelection(selection);
            setHeaderScroll(scrollToHeight);
        }

        // Hide the ListView Header/Footer
        listViewLoadingLayout.setVisibility(View.GONE);

        super.resetHeader();
    }

    public void setHeadRefresh(boolean doScroll) {
        ListAdapter adapter = refreshableView.getAdapter();
        if (!getShowViewWhileRefreshing() || null == adapter || adapter.isEmpty()) {
            super.setRefreshingInternal(doScroll);
            return;
        }

        refreshableView.setSelection(0);
        super.setRefreshingInternal(doScroll);
    }

    @Override
    public void setRefreshingInternal(boolean doScroll) {

        // If we're not showing the Refreshing view, or the list is empty, then
        // the header/footer views won't show so we use the
        // normal method
        //qjb  注释：可将"努力加载中"视图变得随着列表点击弹回  但是会引起loading状态一直在的问题
        //qjb   不注释  努力加载将无法随列表视图弹回
        ListAdapter adapter = refreshableView.getAdapter();
        if (!getShowViewWhileRefreshing() || null == adapter || adapter.isEmpty()) {
            super.setRefreshingInternal(doScroll);
            return;
        }

        super.setRefreshingInternal(false);

        final PullLoadingLayout originalLoadingLayout, listViewLoadingLayout;
        final int selection, scrollToY;

        switch (getCurrentMode()) {
            case MODE_PULL_UP_TO_REFRESH:
                originalLoadingLayout = getFooterLayout();
                listViewLoadingLayout = mFooterLoadingView;
                selection = refreshableView.getCount() - 1;
                scrollToY = getScrollY() - getHeaderHeight();
                break;
            case MODE_PULL_DOWN_TO_REFRESH:
            default:
                originalLoadingLayout = getHeaderLayout();
                listViewLoadingLayout = mHeaderLoadingView;
                selection = 0;
                scrollToY = getScrollY() + getHeaderHeight();
                break;
        }

        if (doScroll) {
            // We scroll slightly so that the ListView's header/footer is at the
            // same Y position as our normal header/footer
            setHeaderScroll(scrollToY);
        }

        // Hide our original Loading View
        if (isSupportMultiPull) {
            mHeaderImageView.setPadding(0, getHeaderHeight(), 0, 0);
            originalLoadingLayout.setVisibility(View.GONE);
        } else {
            originalLoadingLayout.setVisibility(View.INVISIBLE);
        }

        // Show the ListView Loading View and set it to refresh
//        listViewLoadingLayout.setPullRefreshTime( mContext.getString(R.string.pull_update_time) + Preferences.getInstance().get(BundleFlag.RECOM_UPDATE_TIME));
        listViewLoadingLayout.setVisibility(View.VISIBLE);
        listViewLoadingLayout.refreshing();

        if (doScroll) {
            // Make sure the ListView is scrolled to show the loading
            // header/footer
            refreshableView.setSelection(selection);

            // Smooth scroll as normal
            smoothScrollTo(0);
        }
    }

    public void doSupportMultiPull() {
        super.doSupportMultiPull();
        mHeaderLoadingView.homeMode();
    }

}


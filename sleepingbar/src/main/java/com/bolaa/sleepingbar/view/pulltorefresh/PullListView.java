package com.bolaa.sleepingbar.view.pulltorefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bolaa.sleepingbar.R;
import com.core.framework.develop.LogUtil;

/**
 * Created by IntelliJ IDEA.
 * User: kait
 * Date: 11-11-18
 * Time: 下午3:55
 * To change this template use File | SettingsActivity | File Templates.
 */
public class PullListView extends PullToRefreshAdapterViewBase<ListView> {

    private PullLoadingLayout mHeaderLoadingView;
    private PullLoadingLayout mFooterLoadingView;
    private FrameLayout mLvFooterLoadingFrame;
    private boolean mAddedLvFooter = false;
    private int headerScrollToY; // head滚动距离
    private Context mContext;

    public PullListView(Context context) {
        super(context);
        mContext = context;
        this.setDisableScrollingWhileRefreshing(false);
    }

    public PullListView(Context context, int mode) {
        super(context, mode);
        mContext = context;
        this.setDisableScrollingWhileRefreshing(false);
    }

    public PullListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setDisableScrollingWhileRefreshing(false);
    }

    @Override
    protected ListView createRefreshableView(Context context, AttributeSet attrs) {
        ListView lv = new ListView(context, attrs);
        final int mode = getMode();

        String pullLabel = context.getString(R.string.pull_to_refresh);
        String refreshingLabel = context.getString(R.string.label_loading);
        String releaseLabel = context.getString(R.string.pull_to_refresh_release);
        String upLabel = context.getString(R.string.up_to_refresh);

        boolean isShowHeader;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.pullshowheader);
        isShowHeader = typedArray.getBoolean(R.styleable.pullshowheader_showheader, true);

        LogUtil.d("<----------PullSwipeListView isShowHeader----------->" + isShowHeader);

        if (mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) {
            FrameLayout frame = new FrameLayout(context);
            mHeaderLoadingView = new PullLoadingLayout(context, MODE_PULL_DOWN_TO_REFRESH, releaseLabel, pullLabel, refreshingLabel, isShowHeader);
            frame.addView(mHeaderLoadingView, FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

            mHeaderLoadingView.setVisibility(View.GONE);
            lv.addHeaderView(frame, null, false);
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        // setMeasuredDimension(CouponApplication.WIDTH, CouponApplication.HEIGHT);
        // int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public void resetHeader() {
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

        int scrollToHeight = getHeaderHeight();
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

    public void setHeadRefresh (boolean doScroll) {
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

            LogUtil.d("---------------scrollToY = " + scrollToY);
            setHeaderScroll(scrollToY);
        }

        // Hide our original Loading View
        originalLoadingLayout.setVisibility(View.INVISIBLE);

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

    @Override
    public void resetListHeader() {
        super.resetListHeader();
       /* if (isNeedResetHeader) {
            if (refreshableView.getAdapter().getCount() <= refreshableView.getHeaderViewsCount() + refreshableView.getFooterViewsCount()) {
                scrollTo(0, headerScrollToY);
            }
            isNeedResetHeader = false;
        }*/
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try{
            super.dispatchDraw(canvas);
        }catch (IndexOutOfBoundsException e){
            LogUtil.w(e);
        }
    }

}

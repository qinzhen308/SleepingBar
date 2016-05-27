package com.bolaa.sleepingbar.view.pulltorefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.bolaa.sleepingbar.R;
import com.bolaa.sleepingbar.common.GlobeFlags;
import com.core.framework.store.DB.beans.Preferences;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: kait
 * Date: 12-3-22
 * Time: 上午11:25
 * To change this template use File | SettingsActivity | File Templates.
 */
public abstract class PullToRefreshBase<T extends View> extends LinearLayout {

    private static final float FRICTION = 2.0f;

    private static final int PULL_TO_REFRESH = 0x0;
    private static final int RELEASE_TO_REFRESH = 0x1;
    private static final int REFRESHING = 0x2;
    static final int MANUAL_REFRESHING = 0x3;

    public static final int MODE_PULL_DOWN_TO_REFRESH = 0x1;
    public static final int MODE_PULL_UP_TO_REFRESH = 0x2;
    public static final int MODE_BOTH = 0x3;

    private static final SimpleDateFormat DISPLAY_DATE_FORMAT;

    private int state = PULL_TO_REFRESH;
    private int mode = MODE_BOTH;
    private int currentMode;

    private int touchSlop;

    private float mLastTouchY;
    private boolean mFlingUp;

    private float initialMotionY;
    private float lastMotionX;
    private float lastMotionY;
    private boolean isBeingDragged = false;
    private boolean isPullToRefreshEnabled = true;
    private boolean disableScrollingWhileRefreshing = true;
    private boolean showViewWhileRefreshing = true;

    private int headerHeight;

    private int headerImageHeight;

    T refreshableView;
    private PullLoadingLayout headerLayout;
    private PullLoadingLayout footerLayout;

    private Context mContext;
    private final Handler handler = new Handler();
    private SmoothScrollRunnable currentSmoothScrollRunnable;
    private OnRefreshListener onRefreshListener;

    private boolean isShowHeader;
    private boolean isShowFoot;

    static {
        DISPLAY_DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm");
    }

    /*@Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        super.dispatchRestoreInstanceState(null);
    }*/

    public PullToRefreshBase(Context context) {
        super(context);
        init(context, null);
    }

    public PullToRefreshBase(Context context, int mode) {
        super(context);
        this.mode = mode;
        init(context, null);
    }

    public PullToRefreshBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Deprecated. Use {@link #getRefreshableView()} from now on.
     *
     * @deprecated
     * @return The Refreshable View which is currently wrapped
     */
    public final T getAdapterView() {
        return refreshableView;
    }

    /**
     * Get the Wrapped Refreshable View. Anything returned here has already been
     * added to the content view.
     *
     * @return The View which is currently wrapped
     */
    public final T getRefreshableView() {
        return refreshableView;
    }

    public final boolean isFlingUp() {
        return mFlingUp;
    }

    /**
     * Get whether the 'Refreshing' View should be automatically shown when
     * refreshing. Returns true by default.
     *
     * @return - true if the Refreshing View will be show
     */
    public final boolean getShowViewWhileRefreshing() {
        return showViewWhileRefreshing;
    }

    /**
     * Whether Pull-to-Refresh is enabled
     *
     * @return enabled
     */
    public final boolean isPullToRefreshEnabled() {
        return isPullToRefreshEnabled;
    }

    /**
     * Returns whether the widget has disabled scrolling on the Refreshable View
     * while refreshing.
     */
    public final boolean isDisableScrollingWhileRefreshing() {
        return disableScrollingWhileRefreshing;
    }

    /**
     * Returns whether the Widget is currently in the Refreshing state
     *
     * @return true if the Widget is currently refreshing
     */
    public final boolean isRefreshing() {
        return state == REFRESHING || state == MANUAL_REFRESHING;
    }

    /**
     * By default the Widget disabled scrolling on the Refreshable View while
     * refreshing. This method can change this behaviour.
     *
     * @param disableScrollingWhileRefreshing
     *            - true if you want to disable scrolling while refreshing
     */
    public final void setDisableScrollingWhileRefreshing(boolean disableScrollingWhileRefreshing) {
        this.disableScrollingWhileRefreshing = disableScrollingWhileRefreshing;
    }

    /**
     * Mark the current Refresh as complete. Will Reset the UI and hide the
     * Refreshing View
     */
    public final void onRefreshComplete() {
//        setRefreshedTimeLabel(true);
        if (state != PULL_TO_REFRESH) {
            resetHeader();
        }
        resetListHeader();
    }

    /**
     * Set OnRefreshListener for the Widget
     *
     * @param listener
     *            - Listener to be used when the Widget is set to Refresh
     */
    public final void setOnRefreshListener(OnRefreshListener listener) {
        onRefreshListener = listener;
    }

    /**
     * A mutator to enable/disable Pull-to-Refresh for the current View
     *
     * @param enable
     *            Whether Pull-To-Refresh should be used
     */
    public final void setPullToRefreshEnabled(boolean enable) {
        this.isPullToRefreshEnabled = enable;
    }

    /**
     * Set Text to show when the Widget is being pulled, and will refresh when
     * released
     *
     * @param releaseLabel
     *            - String to display
     */
    public void setReleaseLabel(String releaseLabel) {
        if (null != headerLayout) {
            headerLayout.setReleaseLabel(releaseLabel);
        }
        if (null != footerLayout) {
            footerLayout.setReleaseLabel(releaseLabel);
        }
    }

    /**
     * Set Text to show when the Widget is being Pulled
     *
     * @param pullLabel
     *            - String to display
     */
    public void setPullLabel(String pullLabel) {
        if (null != headerLayout) {
            headerLayout.setPullLabel(pullLabel);
        }
        if (null != footerLayout) {
            footerLayout.setPullLabel(pullLabel);
        }
    }

    /**
     * Set Text to show when the Widget is refreshing
     *
     * @param refreshingLabel
     *            - String to display
     */
    public void setRefreshingLabel(String refreshingLabel) {
        if (null != headerLayout) {
            headerLayout.setRefreshingLabel(refreshingLabel);
        }
        if (null != footerLayout) {
            footerLayout.setRefreshingLabel(refreshingLabel);
        }
    }

    public final void setRefreshing() {
        this.setRefreshing(true);
    }

    public final void setHeaderRefreshing() {
        if (!isRefreshing()) {
            state = REFRESHING;
            if (null != headerLayout) {
                headerLayout.refreshing();
            }
            smoothScrollTo(-headerHeight);
            state = MANUAL_REFRESHING;
        }
    }

    public final void setFooterLoading() {
        if (!isRefreshing()) {
            state = REFRESHING;
            if (null != footerLayout) {
                footerLayout.refreshing();
            }
            setHeaderScroll(headerHeight);
            state = MANUAL_REFRESHING;
        }
    }

    /**
     * Sets the Widget to be in the refresh state. The UI will be updated to
     * show the 'Refreshing' view.
     *
     * @param doScroll
     *            - true if you want to force a scroll to the Refreshing view.
     */
    public final void setRefreshing(boolean doScroll) {
        if (!isRefreshing()) {
            setRefreshingInternal(doScroll);
            state = MANUAL_REFRESHING;
        }
    }

    public final boolean hasPullFromTop() {
        return currentMode != MODE_PULL_UP_TO_REFRESH;
    }

    /**
     * Set Time to show when the Widget is refreshed
     *
     * @param isRefreshComplete
     */
    public void setRefreshedTimeLabel(boolean isRefreshComplete) {
        StringBuilder updateAt = new StringBuilder();
        updateAt.append(mContext.getString(R.string.pull_update_time));
        if (isRefreshComplete) {
            String updateTime = DISPLAY_DATE_FORMAT.format(new Date());
            updateAt.append(updateTime);
            Preferences.getInstance().save(GlobeFlags.RECOM_UPDATE_TIME, updateTime);
        } else {
            updateAt.append(Preferences.getInstance().get(GlobeFlags.RECOM_UPDATE_TIME));
        }

    }

    protected void resetHeader() {
        state = PULL_TO_REFRESH;
        isBeingDragged = false;

        if (null != headerLayout) {
            headerLayout.reset();
        }
        if (null != footerLayout) {
            footerLayout.reset();
        }

        smoothScrollTo(0);
    }

    protected void setRefreshingInternal(boolean doScroll) {
        state = REFRESHING;

        if (null != headerLayout) {
            headerLayout.refreshing();
        }
        if (null != footerLayout) {
            footerLayout.refreshing();
        }

        if (doScroll) {
            if (showViewWhileRefreshing) {
                smoothScrollTo(currentMode == MODE_PULL_DOWN_TO_REFRESH ? -headerHeight : headerHeight);
            } else {
                smoothScrollTo(0);
            }
        }
    }

    protected View mHeaderImageView;

    protected boolean isSupportMultiPull;

    public void doSupportMultiPull() {
        if (mHeaderImageView != null) {
            mHeaderImageView.setVisibility(View.VISIBLE);
        }

        headerLayout.homeMode();

        isSupportMultiPull = true;
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        setOrientation(LinearLayout.VERTICAL);
        touchSlop = ViewConfiguration.getTouchSlop();

        setBackgroundColor(getResources().getColor(R.color.milky));
        refreshableView = this.createRefreshableView(context, attrs);
        this.addRefreshableView(context, refreshableView);

        String pullLabel = context.getString(R.string.pull_to_refresh);
        String refreshingLabel = context.getString(R.string.label_loading);
        String releaseLabel = context.getString(R.string.pull_to_refresh_release);
        String upLabel = context.getString(R.string.up_to_refresh);




        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.pullshowheader);
        isShowHeader = typedArray.getBoolean(R.styleable.pullshowheader_showheader, true);

        TypedArray typedArrayFoot = context.obtainStyledAttributes(attrs, R.styleable.pullshowfoot);
        isShowFoot = typedArrayFoot.getBoolean(R.styleable.pullshowfoot_showfoot, true);

        if (mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) {
            //正在刷新的头部
            headerLayout = new PullLoadingLayout(context, MODE_PULL_DOWN_TO_REFRESH, releaseLabel, pullLabel, refreshingLabel, isShowHeader);
//            setRefreshedTimeLabel(false);
            addView(headerLayout, 0, new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            measureView(headerLayout);
            headerHeight = headerLayout.getMeasuredHeight();

            //多点触控的头部
            mHeaderImageView = LayoutInflater.from(mContext).inflate(R.layout.header_pulltorefresh_home, null);
            addView(mHeaderImageView, 0, new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            measureView(mHeaderImageView);
            mHeaderImageView.setVisibility(View.INVISIBLE);
            headerImageHeight = mHeaderImageView.getMeasuredHeight();
        }

        if ((mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH) && isShowFoot) {
            footerLayout = new PullLoadingLayout(context, MODE_PULL_UP_TO_REFRESH, releaseLabel, upLabel, refreshingLabel, isShowHeader);
//            setRefreshedTimeLabel(false);
            addView(footerLayout, new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            measureView(footerLayout);
            headerHeight = footerLayout.getMeasuredHeight();
        }

        switch (mode) {
            case MODE_BOTH:
                setPadding(0, -headerHeight-headerImageHeight, 0, -headerHeight);
                break;

            case MODE_PULL_UP_TO_REFRESH:
                setPadding(0, 0, 0, -headerHeight);
                break;

            default:
            case MODE_PULL_DOWN_TO_REFRESH:
                setPadding(0, -headerHeight-headerImageHeight, 0, 0);//隐藏掉头部的内容
                break;
        }

        if (mode != MODE_BOTH) {
            currentMode = mode;
        }
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    private float deltY = 0;
    private float multiStartY = 0;
    private float multiLastStartY = 0;

    private float archY = 0;

    private boolean isMultiScrolling = false;

    // 判断点下的是那跟手指
    private int mode_multi = -1;
    @Override
    public final boolean onTouchEvent(MotionEvent event) {

        if (!isPullToRefreshEnabled) {
            return false;
        }

        if (isRefreshing() && disableScrollingWhileRefreshing) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {

                if (isSupportMultiPull) {
                    if (isBeingDragged) {
                        int count = event.getPointerCount();
                        event.getActionIndex();
                        if (isMultiScrolling) {//多点触控

                            if (event.getPointerCount() >=2) {
                                if (mode_multi == 1) {
                                    multiLastStartY = event.getY(0);
                                } else if (mode_multi == 2) {
                                    multiLastStartY = event.getY(1);

                                }
                            } else {
                                if (mode_multi == 1) {
                                    multiLastStartY = event.getY();
                                } else if (mode_multi == 2) {
                                    multiLastStartY = event.getY();

                                }
                            }

                            deltY = multiLastStartY - multiStartY;
                            lastMotionY = archY + deltY;
                            this.pullEvent();
                        } else {
                            lastMotionY = event.getY();
                            archY = lastMotionY;
                            this.pullEvent();
                        }

                        return true;
                    }
                } else {
                    if (isBeingDragged) {
                        lastMotionY = event.getY();
                        this.pullEvent();
                        return true;
                    }
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                if (isReadyForPull()) {
                    lastMotionY = initialMotionY = event.getY();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_1_DOWN:
                mode_multi = 1;
                archY = lastMotionY;
                if (isSupportMultiPull) {
                    multiStartY = event.getY(0);

                    if (isReadyForPull()) {
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_1_UP:
                break;

            case MotionEvent.ACTION_POINTER_2_DOWN: {
                mode_multi = 2;
                archY = lastMotionY;
                isMultiScrolling = true;

                if (isSupportMultiPull) {
                    multiStartY = event.getY(1);

                    if (isReadyForPull()) {
                        return true;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_2_UP:

                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                isMultiScrolling = false;
                mode_multi = -1;
                if (isBeingDragged) {
                    isBeingDragged = false;

                    if (state == RELEASE_TO_REFRESH && null != onRefreshListener && isShowHeader) {
                        switch (currentMode) {
                            case MODE_PULL_UP_TO_REFRESH:
                                setHeaderScroll(headerHeight);
                                break;
                            case MODE_PULL_DOWN_TO_REFRESH:
                            default:
                                setHeaderScroll(-headerHeight);
                                break;
                        }

                        setRefreshingInternal(true);
                        onRefreshListener.onRefresh();
                    } else {
                        smoothScrollTo(0);
                    }
                    return true;
                }
                break;
            }
        }

        return false;
    }

    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isPullToRefreshEnabled) {
            return false;
        }

        if (isRefreshing() && disableScrollingWhileRefreshing) {
            return true;
        }

        final int action = event.getAction();

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            isBeingDragged = false;
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN && isBeingDragged) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                mFlingUp = mLastTouchY - event.getY() > 0;

                if (isReadyForPull()) {

                    final float y = event.getY();
                    final float dy = y - lastMotionY;
                    final float yDiff = Math.abs(dy);
                    final float xDiff = Math.abs(event.getX() - lastMotionX);

                    if (yDiff > touchSlop && yDiff > xDiff) {
                        if ((mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) && dy >= 0.0001f
                                && isReadyForPullDown()) {
                            lastMotionY = y;
                            isBeingDragged = true;
                            if (mode == MODE_BOTH) {
                                currentMode = MODE_PULL_DOWN_TO_REFRESH;
                            }
                        } else if ((mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH) && dy <= 0.0001f
                                && isReadyForPullUp()) {
                            lastMotionY = y;
                            isBeingDragged = true;
                            if (mode == MODE_BOTH) {
                                currentMode = MODE_PULL_UP_TO_REFRESH;
                            }
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                mLastTouchY = event.getY();
                if (isReadyForPull()) {
                    lastMotionY = initialMotionY = event.getY();
                    lastMotionX = event.getX();
                    isBeingDragged = false;
                }
                break;
            }
        }

        return isBeingDragged;
    }

    private int mNewHeight;
    /**
     * Actions a Pull Event
     *
     * @return true if the Event has been handled, false if there has been no
     *         change
     */
    private boolean pullEvent() {

        int newHeight;
        final int oldHeight = this.getScrollY();

        switch (currentMode) {
            case MODE_PULL_UP_TO_REFRESH:
                newHeight = Math.round(Math.max(initialMotionY - lastMotionY, 0) / FRICTION);
                break;
            case MODE_PULL_DOWN_TO_REFRESH:
            default:
                if (isSupportMultiPull) {
                    newHeight = Math.round(Math.min(initialMotionY - lastMotionY, 0) / FRICTION);
                    if (newHeight <= -mHeaderImageView.getMeasuredHeight()-headerLayout.getHeight()) {
                        newHeight = -mHeaderImageView.getMeasuredHeight()-headerLayout.getHeight();
                    }
                } else {
                    newHeight = Math.round(Math.min(initialMotionY - lastMotionY, 0) / FRICTION);
                }
                break;
        }

        mNewHeight = newHeight;

        setHeaderScroll(newHeight);

        if (newHeight != 0) {
            if (state == PULL_TO_REFRESH && headerHeight < Math.abs(newHeight)) {
                state = RELEASE_TO_REFRESH;

                switch (currentMode) {
                    case MODE_PULL_UP_TO_REFRESH:
                        footerLayout.releaseToRefresh();
                        break;
                    case MODE_PULL_DOWN_TO_REFRESH:
                        headerLayout.releaseToRefresh();
                        break;
                }

                return true;

            } else if (state == RELEASE_TO_REFRESH && headerHeight >= Math.abs(newHeight)) {
                state = PULL_TO_REFRESH;

                switch (currentMode) {
                    case MODE_PULL_UP_TO_REFRESH:
                        footerLayout.pullToRefresh();
                        break;
                    case MODE_PULL_DOWN_TO_REFRESH:
                        headerLayout.pullToRefresh();
                        break;
                }

                return true;
            }
        }

        return oldHeight != newHeight;
    }

    private boolean isReadyForPull() {
        switch (mode) {
            case MODE_PULL_DOWN_TO_REFRESH:
                return isReadyForPullDown();
            case MODE_PULL_UP_TO_REFRESH:
                return isReadyForPullUp();
            case MODE_BOTH:
                return isReadyForPullUp() || isReadyForPullDown();
        }
        return false;
    }

    protected void addRefreshableView(Context context, T refreshableView) {
        addView(refreshableView, new LayoutParams(LayoutParams.FILL_PARENT, 0, 1.0f));
    }

    protected final void setHeaderScroll(int y) {
        scrollTo(0, y);
    }

    protected final void smoothScrollTo(int y) {
        if (null != currentSmoothScrollRunnable) {
            currentSmoothScrollRunnable.stop();
        }

        if (this.getScrollY() != y) {
            this.currentSmoothScrollRunnable = new SmoothScrollRunnable(handler, getScrollY(), y);
            handler.post(currentSmoothScrollRunnable);
        }
    }

    /**
     * This is implemented by derived classes to return the created View. If you
     * need to use a custom View (such as a custom ListView), override this
     * method and return an instance of your custom class.
     *
     * Be sure to set the ID of the view in this method, especially if you're
     * using a ListActivity or ListFragment.
     *
     * @param context
     * @param attrs
     *            AttributeSet from wrapped class. Means that anything you
     *            include in the XML layout declaration will be routed to the
     *            created View
     * @return New instance of the Refreshable View
     */
    protected abstract T createRefreshableView(Context context, AttributeSet attrs);

    protected final int getCurrentMode() {
        return currentMode;
    }

    protected final PullLoadingLayout getFooterLayout() {
        return footerLayout;
    }

    protected final PullLoadingLayout getHeaderLayout() {
        return headerLayout;
    }

    protected final int getHeaderHeight() {
        return headerHeight;
    }

    protected final int getMode() {
        return mode;
    }

    protected final int getState() {
        return state;
    }

    public final void setMode(int mode) {
        this.mode = mode;
        this.currentMode = mode;
    }

    /**
     * Implemented by derived class to return whether the View is in a state
     * where the user can Pull to Refresh by scrolling down.
     *
     * @return true if the View is currently the correct state (for example, top
     *         of a ListView)
     */
    protected abstract boolean isReadyForPullDown();

    /**
     * Implemented by derived class to return whether the View is in a state
     * where the user can Pull to Refresh by scrolling up.
     *
     * @return true if the View is currently in the correct state (for example,
     *         bottom of a ListView)
     */
    protected abstract boolean isReadyForPullUp();

    @Override
    public void setLongClickable(boolean longClickable) {
        getRefreshableView().setLongClickable(longClickable);
    }

    public static interface OnLastItemVisibleListener {
        public void onLastItemVisible();
    }

    public static interface
            OnRefreshListener {
        public void onRefresh();
    }

    final class SmoothScrollRunnable implements Runnable {

        static final int ANIMATION_DURATION_MS = 400;
        static final int ANIMATION_FPS = 1000 / 60;

        private final Interpolator interpolator;
        private final int scrollToY;
        private final int scrollFromY;
        private final Handler handler;

        private boolean continueRunning = true;
        private long startTime = -1;
        private int currentY = -1;

        public SmoothScrollRunnable(Handler handler, int fromY, int toY) {
            this.handler = handler;
            this.scrollFromY = fromY;
            this.scrollToY = toY;
            this.interpolator = new AccelerateDecelerateInterpolator();
        }

        @Override
        public void run() {

            /**
             * Only set startTime if this is the first time we're starting, else
             * actually calculate the Y delta
             */
            if (startTime == -1) {
                startTime = System.currentTimeMillis();
            } else {
                /**
                 * We do do all calculations in long to reduce software float
                 * calculations. We use 1000 as it gives us good accuracy and
                 * small rounding errors
                 */
                long normalizedTime = (1000 * (System.currentTimeMillis() - startTime)) / ANIMATION_DURATION_MS;
                normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

                final int deltaY = Math.round((scrollFromY - scrollToY)
                        * interpolator.getInterpolation(normalizedTime / 1000f));
                this.currentY = scrollFromY - deltaY;
                setHeaderScroll(currentY);
            }

            // If we're not at the target Y, keep going...
            if (continueRunning && scrollToY != currentY) {
                handler.postDelayed(this, ANIMATION_FPS);
            }
        }

        public void stop() {
            this.continueRunning = false;
            this.handler.removeCallbacks(this);
        }
    }


    public void resetListHeader() {};
}

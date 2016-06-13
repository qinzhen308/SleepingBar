
package com.bolaa.sleepingbar.view.pulltorefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.bolaa.sleepingbar.R;
import com.core.framework.develop.LogUtil;

public class SwipeListView extends ListView implements EmptyViewMethodAccessor {

    public final static int MOVE_DISTANCE = 30; // 上下滑动距离监听

    private Boolean mIsHorizontal;

    private View mPreItemView;

    private View mCurrentItemView;

    private float mFirstX;

    private float mFirstY;

    private int mRightViewWidth, mDefaultRightViewWidth;

    // private boolean mIsInAnimation = false;
    private final int mDuration = 25;

    private final int mDurationStep = 10;

    public boolean mIsShown;

    private float mFirstDownY; // 按下的位置

    OnMoveTouchListener mMoveTouchListener;

    private ShowItemRightLinstener mShowItemRightLinstener;//右边隐藏部分是否可滑动出来的监听

    private float mDownY;

    private boolean isPointList;//判断按下位置是不是在列表区域

    //private boolean mViewShow = true;

    public SwipeListView(Context context) {
        this(context, null);
    }

    public SwipeListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.swipelistviewstyle);

        //获取自定义属性和默认值(v4.0.0列表全部不让侧滑)
        mRightViewWidth = (int) mTypedArray.getDimension(R.styleable.swipelistviewstyle_right_width, context.getResources().getDimensionPixelOffset(R.dimen.swipe_width));
        mDefaultRightViewWidth = mRightViewWidth;
        mTypedArray.recycle();
    }

    /**
     * return true, deliver to listView. return false, deliver to child. if
     * move, return true
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float lastX = ev.getX();
        float lastY = ev.getY();


        //if (pointToPosition((int)mFirstX, (int)mFirstY) < getHeaderViewsCount()) return super.onTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFirstDownY = ev.getRawY();
                mIsHorizontal = null;
                mFirstX = lastX;
                mFirstY = lastY;
                int motionPosition = pointToPosition((int) mFirstX, (int) mFirstY);

                // Log.d("TAG", "--------motionPosition = " + motionPosition);
                // Log.d("TAG _ getFirstVisiblePosition", "--------getFirstVisiblePosition = " + getFirstVisiblePosition());
                if (motionPosition >= 0) {
                    View currentItemView = getChildAt(motionPosition - getFirstVisiblePosition());
                    mPreItemView = mCurrentItemView;
                    mCurrentItemView = currentItemView;
                    //控制item是否能侧滑，监听每次拦截事件时，触摸点所在项是否需要侧滑
                    int i=getCount();
                    if (mShowItemRightLinstener != null && (motionPosition - getHeaderViewsCount()) >= 0&&getCount()-getFooterViewsCount()>motionPosition) {
                        isPointList=true;
                        if (mShowItemRightLinstener.isRightSlideable(motionPosition - getHeaderViewsCount())) {
                            mRightViewWidth = mDefaultRightViewWidth;
                        } else {
                            mRightViewWidth = 0;
                        }
                    }else{
                        isPointList=false;
                    }
                }

                break;

            case MotionEvent.ACTION_MOVE:
                float dx = lastX - mFirstX;
                float dy = lastY - mFirstY;

                if (Math.abs(dx) >= (isPointList?5:20) && Math.abs(dy) >= (isPointList?5:20)) {
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsShown && mPreItemView!=null&&(mPreItemView != mCurrentItemView || isHitCurItemLeft(lastX))) {
                    /**
                     * 情况一：
                     * <p>
                     * 一个Item的右边布局已经显示，
                     * <p>
                     * 这时候点击任意一个item, 那么那个右边布局显示的item隐藏其右边布局
                     */
                    hiddenRight(mPreItemView);
                    return true;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    private boolean isHitCurItemLeft(float x) {
        return x < getWidth() - mRightViewWidth;
    }

    /**
     * @param dx
     * @param dy
     * @return judge if can judge scroll direction
     */
    private boolean judgeScrollDirection(float dx, float dy) {
        boolean canJudge = true;

        if (Math.abs(dx) > 30 && Math.abs(dx) > 2 * Math.abs(dy)) {
            mIsHorizontal = true;
        } else if (Math.abs(dy) > 30 && Math.abs(dy) > 2 * Math.abs(dx)) {
            mIsHorizontal = false;
        } else {
            canJudge = false;
        }

        return canJudge;
    }

    /**
     * return false, can't move any direction. return true, cant't move
     * vertical, can move horizontal. return super.onTouchEvent(ev), can move
     * both.
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float lastX = ev.getX();
        float lastY = ev.getY();

        int position = pointToPosition((int) mFirstX, (int) mFirstY);
        if (position < getHeaderViewsCount()
                || position == getAdapter().getCount() - getFooterViewsCount()) {
            mIsHorizontal = false;
            return super.onTouchEvent(ev); // headview的滑动不响应
        }


        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = lastX - mFirstX;
                float dy = lastY - mFirstY;

                float currentY = ev.getRawY();
                if (mMoveTouchListener != null) {
                    if (currentY - mFirstDownY >= MOVE_DISTANCE) {
                        mMoveTouchListener.onMoveDown();
                    } else if (mFirstDownY - currentY >= MOVE_DISTANCE) {
                        mMoveTouchListener.onMoveUp();
                    }
                }


                //LogUtil.d("-----------------RawY------------ = " + (currentY - mFirstDownY));
                mFirstDownY = currentY;

                // confirm is scroll direction
                if (mIsHorizontal == null) {
                    if (!judgeScrollDirection(dx, dy)) {
                        break;
                    }
                }

                if (mIsHorizontal) {
                    if (mIsShown && mPreItemView!=null&&mPreItemView != mCurrentItemView) {
                        /**
                         * 情况二：
                         * <p>
                         * 一个Item的右边布局已经显示，
                         * <p>
                         * 这时候左右滑动另外一个item,那个右边布局显示的item隐藏其右边布局
                         * <p>
                         * 向左滑动只触发该情况，向右滑动还会触发情况五
                         */
                        hiddenRight(mPreItemView);
                        //mViewShow = false;
                    }

                    if (mIsShown && mPreItemView == mCurrentItemView) {
                        dx = dx - mRightViewWidth;
                    }

                    // can't move beyond boundary
                    if (dx < 0 && dx > -mRightViewWidth) {
                        mCurrentItemView.scrollTo((int) (-dx), 0);
                    }

                    return true;
                } else {
                    if (mIsShown&&mPreItemView!=null) {
                        /**
                         * 情况三：
                         * <p>
                         * 一个Item的右边布局已经显示，
                         * <p>
                         * 这时候上下滚动ListView,那么那个右边布局显示的item隐藏其右边布局
                         */
                        hiddenRight(mPreItemView);
                        //mViewShow = true;
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                clearPressedState();
                if (mIsShown) {
                    /**
                     * 情况四：
                     * <p>
                     * 一个Item的右边布局已经显示，
                     * <p>
                     * 这时候左右滑动当前一个item,那个右边布局显示的item隐藏其右边布局
                     */
                if(mPreItemView!=null){
                    hiddenRight(mPreItemView);
                }
                    //mViewShow = true;
                }

                if (mIsHorizontal != null && mIsHorizontal) {
                    if (mFirstX - lastX > mRightViewWidth / 2) {
                        showRight(mCurrentItemView);
                    } else {
                        /**
                         * 情况五：
                         * <p>
                         * 向右滑动一个item,且滑动的距离超过了右边View的宽度的一半，隐藏之。
                         */
                        hiddenRight(mCurrentItemView);
                        //mViewShow = true;
                    }
                    return true;
                }

                break;
        }

        return super.onTouchEvent(ev);
    }

    private void clearPressedState() {
        // TODO current item is still has background, issue

        if (mCurrentItemView != null) {
            mCurrentItemView.setPressed(false);
            setPressed(false);
            refreshDrawableState();
        }
        // invalidate();
    }

    public void hideRightView() {
        if (mPreItemView != null) {
            hiddenRight(mPreItemView);
        }
    }

    private void showRight(View view) {
        if (mPreItemView != null) {
            hiddenRight(mPreItemView);
        }

        Message msg = new MoveHandler().obtainMessage();
        msg.obj = view;
        msg.arg1 = view.getScrollX();
        msg.arg2 = mRightViewWidth;
        msg.sendToTarget();

        mIsShown = true;
    }

    private void hiddenRight(View view) {
        if (mCurrentItemView == null) {
            return;
        }
        Message msg = new MoveHandler().obtainMessage();//
        msg.obj = view;
        msg.arg1 = view.getScrollX();
        msg.arg2 = 0;

        msg.sendToTarget();

        mIsShown = false;
    }

    @Override
    public void setEmptyView(View emptyView) {
        SwipeListView.this.setEmptyView(emptyView);
    }

    @Override
    public void setEmptyViewInternal(View emptyView) {
        super.setEmptyView(emptyView);
    }

    /**
     * show or hide right layout animation
     */
    @SuppressLint("HandlerLeak")
    class MoveHandler extends Handler {
        int stepX = 0;

        int fromX;

        int toX;

        View view;

        private boolean mIsInAnimation = false;

        private void animatioOver() {
            mIsInAnimation = false;
            stepX = 0;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (stepX == 0) {
                if (mIsInAnimation) {
                    return;
                }
                mIsInAnimation = true;
                view = (View) msg.obj;
                fromX = msg.arg1;
                toX = msg.arg2;
                stepX = (int) ((toX - fromX) * mDurationStep * 1.0 / mDuration);
                if (stepX < 0 && stepX > -1) {
                    stepX = -1;
                } else if (stepX > 0 && stepX < 1) {
                    stepX = 1;
                }
                if (Math.abs(toX - fromX) < 10) {
                    view.scrollTo(toX, 0);
                    animatioOver();
                    return;
                }
            }

            fromX += stepX;
            boolean isLastStep = (stepX > 0 && fromX > toX) || (stepX < 0 && fromX < toX);
            if (isLastStep) {
                fromX = toX;
            }

            view.scrollTo(fromX, 0);
            invalidate();

            if (!isLastStep) {
                this.sendEmptyMessageDelayed(0, mDurationStep);
            } else {
                animatioOver();
            }
        }
    }

    public int getRightViewWidth() {
        return mRightViewWidth;
    }

    public void setRightViewWidth(int mRightViewWidth) {
        this.mRightViewWidth = mRightViewWidth;
    }

    public void setOnMoveTouchListener(OnMoveTouchListener listener) {
        this.mMoveTouchListener = listener;
    }

    public interface OnMoveTouchListener {
        public void onMoveUp();

        public void onMoveDown();

    }

    public void setShowItemRightLinstener(ShowItemRightLinstener showItemRightLinstener) {
        mShowItemRightLinstener = showItemRightLinstener;
    }

    public interface ShowItemRightLinstener {
        public boolean isRightSlideable(int pointPositon);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (IndexOutOfBoundsException e) {
            LogUtil.w(e);
        }
    }


    public int getDefaultRightViewWidth() {
        return mDefaultRightViewWidth;
    }
}

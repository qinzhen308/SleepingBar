package com.bolaa.medical.view.banner;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import com.core.framework.develop.LogUtil;

/**
 * Created by qz on 2015/7/20.
 */
public class BaseViewPager extends ViewPager {
    public BaseViewPager(Context context) {
        super(context);
        init(context);
    }

    public BaseViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /** 触摸时按下的点 **/
    PointF downP = new PointF();
    /** 触摸时当前的点 **/
    PointF curP = new PointF();
    protected int mTouchSlop;
    protected int mMinimumVelocity;
    protected int mMaximumVelocity;
    protected VelocityTracker mVelocityTracker;

    OnSingleTouchListener onSingleTouchListener;

    private void init(Context context) {
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        mMinimumVelocity = config.getScaledMinimumFlingVelocity();
        mMaximumVelocity = config.getScaledMaximumFlingVelocity();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            //每次进行onTouch事件都记录当前的按下的坐标
            curP.x = event.getX();
            curP.y = event.getY();
            obtainVelocityTracker(event);
            final int action = event.getActionMasked();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    downP.x = event.getX();
                    downP.y = event.getY();
                    //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
//                    getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_MOVE:
//                    getParent().requestDisallowInterceptTouchEvent(true);
                    if(Math.abs(event.getX()-downP.x)<mTouchSlop){
                        return true;
                    }else{
                        event.setLocation((curP.x-downP.x)>0?curP.x-mTouchSlop:curP.x+mTouchSlop,curP.y);
                        return super.onTouchEvent(event);
                    }

                case MotionEvent.ACTION_UP:
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int initialVelocity = (int) velocityTracker.getXVelocity();

                    //在up时判断是否按下和松手的坐标为一个点,如果是一个点或者触摸浮动小于mTouchSlop，将执行点击事件
                    if ((downP.x==curP.x && downP.y==curP.y) || Math.abs(curP.x - downP.x) < mTouchSlop
                            && (Math.abs(initialVelocity) < mMinimumVelocity)) {
                        onSingleTouch();
                        releaseVelocityTracker();
                        // return true;
                    }
                    releaseVelocityTracker();
                    break;
            }
        } catch (Exception e) {
            LogUtil.w(e);
        }

        return super.onTouchEvent(event);
    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public void onSingleTouch() {
        if (onSingleTouchListener!= null) {
            onSingleTouchListener.onSingleTouch();
        }
    }

    public interface OnSingleTouchListener {
        public void onSingleTouch();
    }

    public void setOnSingleTouchListener(OnSingleTouchListener onSingleTouchListener) {
        this.onSingleTouchListener = onSingleTouchListener;
    }
}

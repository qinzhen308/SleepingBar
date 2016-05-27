package com.bolaa.sleepingbar.view.banner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.core.framework.develop.LogUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: qz
 * Date: 15-6-15
 * Time: 下午3:05
 * 自动轮播的viewpager
 */
public class BannerViewPager extends BaseViewPager{
    private static final int DEFAULT_SWITCH_INTERVAL = 5000;
    private int mSwitchInterval = DEFAULT_SWITCH_INTERVAL;
    private boolean isAllowScroll=true;//touch的时候banner不滚动,默认滚动

    private ScheduledExecutorService mScheduleService;
    private Handler mScheduleHandler;
    private AtomicInteger mCurrentIndex = new AtomicInteger(0);
    private CirclePageIndicator mPageIndicator;
    private boolean allowLoop=false;//banner滑动循环模式

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BannerViewPager(Context context) {
        super(context);
        init(context);
    }
    private void init(Context context) {
        mScheduleHandler = new ScheduleHandler();
    }
    private float dX;
    private float dY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dX = ev.getX();
                dY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - dX) > Math.abs(ev.getY() - dY)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            //每次进行onTouch事件都记录当前的按下的坐标
            curP.x = event.getX();
            curP.y = event.getY();
            final int action = event.getActionMasked();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    downP.x = event.getX();
                    downP.y = event.getY();
                    //此句代码是为了通知他的父ViewPager现在进行的是本控件的操作，不要对我的操作进行干扰
                    getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        } catch (Exception e) {
            LogUtil.w(e);
        }
        return super.onTouchEvent(event);
    }


    public void setPageIndicator(CirclePageIndicator pageIndicator){
        mPageIndicator=pageIndicator;
        if(mPageIndicator!=null){
            mPageIndicator.setOnPageChangeListener(new AdPagerChangeListener());
        }
    }

    public void setLoopPageIndicator(LoopCirclePageIndicator pageIndicator,int realCount){
        if(pageIndicator!=null){
            pageIndicator.setRealCount(realCount);
            pageIndicator.setOnPageChangeListener(new AdPagerChangeListener());
        }
    }

    //设置开始执行视图循环播放
    public void startCircleView() {
        stopCircleView();
        mScheduleService = Executors.newSingleThreadScheduledExecutor();
        mScheduleService.scheduleAtFixedRate(new CircleTask(),
                mSwitchInterval, mSwitchInterval, TimeUnit.MILLISECONDS);
    }

    //停止执行循环任务
    public void stopCircleView() {

        if (mScheduleService != null && !mScheduleService.isShutdown()) {
            mScheduleService.shutdownNow();
        }
    }

    //设置任务执行间隔时间
    public void setSwitchInterval(int interval) {
        this.mSwitchInterval = interval;
        this.startCircleView();
    }

    private final class ScheduleHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            setCurrentItem(mCurrentIndex.get(), false);
        }
    }

    //执行循环的任务
    private final class CircleTask implements Runnable {

        @Override
        public void run() {
            if (isAllowScroll&&mCurrentIndex.getAndIncrement() == getAdapter().getCount()) {
                mCurrentIndex.set(0);
            }
            mScheduleHandler.sendEmptyMessage(0);
        }
    }

    public void setLoopMode(boolean allowLoop,int origin){
        this.allowLoop=allowLoop;
        if(allowLoop){
            mCurrentIndex = new AtomicInteger(origin);
        }
    }

    /**
     *
     * @return 当前position，即使重新setadapter，index也不变
     */
    public int getAutoCurrentIndex(){
        return mCurrentIndex.get();
    }

    /**
     * 重置为0
     */
    public void resetAutoCurrentIndex(){
        mCurrentIndex.set(0);
    }

    /**
     * 重置为原点(可循环的banner)
     */
    public void resetAutoCurrentIndex(int origin){
        mCurrentIndex.set(origin);
    }

    public final class AdPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            mCurrentIndex.set(i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {
            if(i==0){
                isAllowScroll=true;
            }else{
                isAllowScroll=false;
            }
        }
    }
}

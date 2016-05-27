package com.bolaa.medical.view;

import java.lang.reflect.Field;

import javax.security.auth.PrivateCredentialPermission;

import com.core.framework.develop.LogUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;
import android.widget.ScrollView;

public class PageScrollView extends ScrollView{
	OnScrollListener mOnScrollListener;
	private int mLastScrollState = OnScrollListener.SCROLL_STATE_IDLE;
	OverScroller mScroller;
	boolean isTouched;
	

	public PageScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	public PageScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
	public PageScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init();
	}
	
	private void init(){
//		mScroller=new OverScroller(getContext());
		accessScoller();
	}
	
	private void accessScoller(){
		try {
			Class<?> clazz=Class.forName("com.bolaa.medical.view.PageScrollView");
			Field field=clazz.getSuperclass().getDeclaredField("mScroller");
			field.setAccessible(true);
			mScroller=(OverScroller)field.get(this);
			LogUtil.d(mScroller.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			isTouched=true;
			break;
			
		case MotionEvent.ACTION_MOVE:
			reportScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
			break;
			
		case MotionEvent.ACTION_UP:
			isTouched=false;
			break;
		case MotionEvent.ACTION_CANCEL:
			isTouched=false;
			
			break;

		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			isTouched=true;
			break;
			
		case MotionEvent.ACTION_MOVE:
			reportScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
			break;
			
		case MotionEvent.ACTION_UP:
			isTouched=false;
			break;
		case MotionEvent.ACTION_CANCEL:
			isTouched=false;
			
			break;

		default:
			break;
		}
		return super.onTouchEvent(ev);
		
	}
	
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		LogUtil.d("onScrollChanged  now("+ l+","+t+") before("+oldl+","+oldt+")");
		if(mOnScrollListener!=null){
			mOnScrollListener.onScroll(l, t, oldl, oldt);
		}
	}
	
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if(!isTouched){
			if(mScroller.isFinished()){
				reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
			}else {
				reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
			}
		}
		super.computeScroll();
		
	}
	
	void reportScrollStateChange(int newState) {
        if (newState != mLastScrollState) {
            if (mOnScrollListener != null) {
                mLastScrollState = newState;
                mOnScrollListener.onScrollStateChanged(this, newState);
            }
        }
    }
	
	public interface OnScrollListener{
		/**
         * The view is not scrolling. Note navigating the list using the trackball counts as
         * being in the idle state since these transitions are not animated.
         */
        public static int SCROLL_STATE_IDLE = 0;

        /**
         * The user is scrolling using touch, and their finger is still on the screen
         */
        public static int SCROLL_STATE_TOUCH_SCROLL = 1;

        /**
         * The user had previously been scrolling using touch and had performed a fling. The
         * animation is now coasting to a stop
         */
        public static int SCROLL_STATE_FLING = 2;
        
		public void onScroll(int l, int t, int oldl, int oldt);
		public void onScrollStateChanged(ViewGroup viewGroup,int state);
	}
	
	public void setOnScrollListener(OnScrollListener onScrollListener){
		this.mOnScrollListener=onScrollListener;
	}

}

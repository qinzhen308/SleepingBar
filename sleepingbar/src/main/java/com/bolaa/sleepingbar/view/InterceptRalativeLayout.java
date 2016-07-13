package com.bolaa.sleepingbar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;


/**
 * Created with IntelliJ IDEA.
 * User: mark
 * Date: 13-11-12
 * Time: 下午1:38
 * To change this template use File | Settings | File Templates.
 */
public class InterceptRalativeLayout extends RelativeLayout {

    public InterceptRalativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptRalativeLayout(Context context, AttributeSet attrs, int style) {
        super(context, attrs,style);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}

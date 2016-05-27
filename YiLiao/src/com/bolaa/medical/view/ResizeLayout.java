package com.bolaa.medical.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


/**
 * Created with IntelliJ IDEA.
 * User: mark
 * Date: 13-11-12
 * Time: 下午1:38
 * To change this template use File | Settings | File Templates.
 */
public class ResizeLayout extends RelativeLayout {
    private OnResizeListener mListener;

    public interface OnResizeListener {
        void OnResize(int w, int h, int oldw, int oldh);
    }

    public void setOnResizeListener(OnResizeListener l) {
        mListener = l;
    }

    public ResizeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mListener != null) {
            mListener.OnResize(w, h, oldw, oldh);
        }
    }
}

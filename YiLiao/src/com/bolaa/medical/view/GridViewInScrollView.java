package com.bolaa.medical.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.GridView;

/**
 * Created by qz.
 */
public class GridViewInScrollView extends GridView {

    public GridViewInScrollView(Context context) {
        super(context);
    }

    public GridViewInScrollView(Context context , AttributeSet attr){
        super(context , attr);
    }

    public GridViewInScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}

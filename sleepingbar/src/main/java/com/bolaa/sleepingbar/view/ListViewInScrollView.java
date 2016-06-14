package com.bolaa.sleepingbar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.ListView;

/**
 * Created by qz.
 */
public class ListViewInScrollView extends ListView {

    public ListViewInScrollView(Context context) {
        super(context);
    }

    public ListViewInScrollView(Context context , AttributeSet attr){
        super(context , attr);
    }

    public ListViewInScrollView(Context context, AttributeSet attrs, int defStyle) {
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
